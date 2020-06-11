package com.kloudsync.techexcel.frgment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.Attendee;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.PopShareMeeting;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.ui.MeetingViewActivity;
import com.ub.service.activity.MeetingPropertyActivity;
import com.ub.service.activity.NewMeetingActivity;
import com.ub.techexcel.adapter.ServiceAdapter2;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.MeetingMoreOperationPopup;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
/**
 * Created by tonyan on 2019/11/9.
 */

public class MeetingFragment extends MyFragment {

    private PullToRefreshListView meetingList;
    int type;
    String keyWord;
    private List<ServiceBean> mList = new ArrayList<>();
    LinearLayout noMeetingLayout;
    private int REQUEST_VIEW=0X001;
    private int REQUEST_EDIT=0X002;
    private int currentPage=0;
    private List<ServiceBean> serviceBeans=new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void lazyLoad() {
        Log.e("MeetingFragment", "lazyLoad");
    }

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_meeting, container, false);
            noMeetingLayout = view.findViewById(R.id.layout_no_meeting);
            meetingList = view.findViewById(R.id.list_meeting);

            meetingList.setMode(PullToRefreshBase.Mode.BOTH);

            meetingList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

                public void onPullDownToRefresh(PullToRefreshBase refreshView) {
                    currentPage=0;
                    loadMeetings();
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase refreshView) {
                    currentPage+=1;
                    loadMeetings();
                }
            });
        }

        meetingAdapter = new ServiceAdapter2(getActivity(), serviceBeans, true, 0);
        meetingList.setAdapter(meetingAdapter);

        loadMeetings();
        return view;
    }

    private void loadMeetings() {
        Observable.just(type).observeOn(Schedulers.io()).map(new Function<Integer, List<ServiceBean>>() {
            @Override
            public List<ServiceBean> apply(Integer integer) throws Exception {
                return requestMeetings();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ServiceBean>>() {
            @Override
            public void accept(List<ServiceBean> serviceBeans) throws Exception {
                loadMeeting(serviceBeans);
            }
        });

    }

    private List<ServiceBean> requestMeetings() {

        String url = "";
        if (keyWord == null || keyWord.equals("")) {
            url = AppConfig.URL_PUBLIC
                    + "Lesson/List?roleID=3&isPublish=1&pageIndex="+currentPage+"&pageSize=20&type=" + type;
        } else {
            try {
                url = AppConfig.URL_PUBLIC
                        + "Lesson/List?roleID=3&isPublish=1&pageIndex="+currentPage+"&pageSize=20&type=" + type + "&keyword=" + URLEncoder.encode(LoginGet.getBase64Password(keyWord), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        JSONObject returnJson = ConnectService
                .getIncidentbyHttpGet(url);
        Log.e("Lesson/List", url + "  " + returnJson.toString());
        return parseData(returnJson);
    }

    private List<ServiceBean> parseData(JSONObject returnJson) {
        try {
            int retCode = returnJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONArray retdata = returnJson.getJSONArray("RetData");
                    mList.clear();
                    for (int i = 0; i < retdata.length(); i++) {
                        JSONObject service = retdata.getJSONObject(i);
                        ServiceBean bean = new ServiceBean();
                        int statusID = service.getInt("Status");
                        bean.setStatusID(statusID);
                        bean.setId(service.getInt("LessonID"));
                        bean.setRoleinlesson(service.getInt("Role"));
                        bean.setPlanedEndDate(service.getString("PlanedEndDate"));
                        bean.setPlanedStartDate(service.getString("PlanedStartDate"));
                        bean.setCourseName(service.getString("CourseName"));
                        bean.setUserName(service.getString("StudentNames"));
                        bean.setName(service.getString("Title"));
                        bean.setTeacherName(service.getString("TeacherNames"));
                        bean.setFinished(service.getInt("IsFinished") == 1 ? true : false);
                        bean.setStudentCount(service.getInt("StudentCount"));
                        JSONArray jsonArray = service.getJSONArray("MemberList");
                        bean.setMembers(jsonArray.toString());
                        mList.add(bean);
                    }
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mList;

    }

    public void loadMeeting(List<ServiceBean> meetings) {
        if ((meetings == null || meetings.size() == 0)&&currentPage==0) {
            noMeetingLayout.setVisibility(View.VISIBLE);
            meetingList.setVisibility(View.GONE);
        } else {
            noMeetingLayout.setVisibility(View.GONE);
            meetingList.setVisibility(View.VISIBLE);
            if(type==1){
                ListSort(meetings);
            }else {
                meetings = sortBydata(meetings);
            }
            if(currentPage==0) serviceBeans.clear();
            serviceBeans.addAll(meetings);
            meetingAdapter.notifyDataSetChanged();
            meetingList.onRefreshComplete();
            meetingAdapter.setOnModifyServiceListener(new ServiceAdapter2.OnModifyServiceListener() {
                @Override
                public void select(final ServiceBean bean) {
                    MeetingMoreOperationPopup meetingMoreOperationPopup = new MeetingMoreOperationPopup();
                    meetingMoreOperationPopup.getPopwindow(getActivity());
                    meetingMoreOperationPopup.setFavoritePoPListener(new MeetingMoreOperationPopup.FavoritePoPListener() {
                        @Override
                        public void delete() {
                            deleteMeeting(bean);
                        }

                        @Override
                        public void view() {
                            if (type == 3 || type == 2) {
                                Intent intent = new Intent(getActivity(), MeetingViewActivity.class);
                                intent.putExtra("userid", bean.getUserId());
                                intent.putExtra("meetingId", bean.getId() + "");
                                intent.putExtra("teacherid", bean.getTeacherId());
                                intent.putExtra("identity", bean.getRoleinlesson());
                                intent.putExtra("isInstantMeeting", 0);
                                intent.putExtra("isStartCourse", true);
                                intent.putExtra("isPrepare", true);
                                intent.putExtra("filemeetingId", bean.getId() + "");
                                intent.putExtra("isFinished", true);
                                intent.putExtra("yinxiangmode", 1);
                                // --------
                                intent.putExtra("meeting_id", bean.getId() + "");
                                intent.putExtra("meeting_type", MeetingType.FINISHMEETING);
                                intent.putExtra("meeting_role", bean.getRoleinlesson());
                                try {
                                    intent.putExtra("lession_id", Integer.parseInt(bean.getId() + ""));

                                }catch (Exception e){

                                }
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getActivity(), MeetingViewActivity.class);
                                intent.putExtra("userid", bean.getUserId());
                                intent.putExtra("meetingId", bean.getId() + "");
                                intent.putExtra("teacherid", bean.getTeacherId());
                                intent.putExtra("identity", bean.getRoleinlesson());
                                intent.putExtra("isInstantMeeting", 0);
                                intent.putExtra("isStartCourse", true);
                                intent.putExtra("isPrepare", true);
                                intent.putExtra("yinxiangmode", 1);

                                // --------
                                intent.putExtra("meeting_id", bean.getId() + "");
	                            intent.putExtra("meeting_type", MeetingType.UPCOMINGMEETING);
                                intent.putExtra("meeting_role", bean.getRoleinlesson());
                                try {
                                    intent.putExtra("lession_id", Integer.parseInt(bean.getId() + ""));

                                }catch (Exception e){

                                }
                                startActivityForResult(intent,REQUEST_VIEW);
                            }
                        }

                        @Override
                        public void edit() {
                            if (type == 3) {
//                                Intent shareintent = new Intent(getActivity(), MeetingShareActivity.class);
//                                shareintent.putExtra("lesson", bean);
//                                startActivity(shareintent);
                                shareDocumentDialog(bean);
                            } else {
                                //Intent intent = new Intent(getActivity(), MeetingPropertyActivity.class);
//                                intent.putExtra("servicebean", bean);
//                                getActivity().startActivity(intent);
                                Intent schintent = new Intent(getActivity(), NewMeetingActivity.class);
                                schintent.putExtra("servicebean", bean);
                                startActivityForResult(schintent,REQUEST_EDIT);

                                getActivity().overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
                            }
                        }

                        @Override
                        public void startMeeting() {
//                            Intent intent = new Intent(getActivity(), MeetingViewActivity.class);
//                            intent.putExtra("userid", bean.getUserId());
//                            intent.putExtra("meetingId", bean.getId() + "");
//                            intent.putExtra("filemeetingId", bean.getId() + "");
//                            intent.putExtra("teacherid", bean.getTeacherId());
//                            intent.putExtra("identity", bean.getRoleinlesson());
//                            intent.putExtra("isInstantMeeting", 0);
//                            intent.putExtra("isStartCourse", true);
//                            startActivity(intent);
	                        if (onStartMeetingCallBackListener != null) {
		                        onStartMeetingCallBackListener.startMeetingCallBack();
	                        }
                        }

                        @Override
                        public void dismiss() {
                        }

                        @Override
                        public void open() {

                        }

                        @Override
                        public void property() {
                            Intent intent = new Intent(getActivity(), NewMeetingActivity.class);
                            intent.putExtra("servicebean", bean);
                            intent.putExtra("IsProperty", true);
                            getActivity().startActivity(intent);
                        }
                    });
                    meetingMoreOperationPopup.StartPop(meetingList, bean, type);
                }
            });
        }
    }


    private void shareDocumentDialog(final ServiceBean document) {
        final PopShareMeeting psk = new PopShareMeeting();
        psk.getPopwindow(getActivity(), document);
        psk.startPop();
    }

    ServiceAdapter2 meetingAdapter;

    private List<ServiceBean> sortBydata(List<ServiceBean> serviceBeanList) {
        Collections.sort(serviceBeanList, new Comparator<ServiceBean>() {
            @Override
            public int compare(ServiceBean s1, ServiceBean s2) {
                String x1 = s1.getPlanedStartDate();
                String x2 = s2.getPlanedStartDate();
                if (TextUtils.isEmpty(x1)) {
                    x1 = "0";
                }
                if (TextUtils.isEmpty(x2)) {
                    x2 = "0";
                }
                if (Long.parseLong(x1) > Long.parseLong(x2)) {
                    return -1;
                } else if (Long.parseLong(x1) == Long.parseLong(x2)) {
                    return 0;
                }
                return 1;
            }
        });

        for (ServiceBean bean : serviceBeanList) {
            String planedsatrtdate = bean.getPlanedStartDate();
            if (TextUtils.isEmpty(planedsatrtdate)) {
                bean.setDateType(4);
            } else {
                long today = System.currentTimeMillis();
                long planed = Long.parseLong(planedsatrtdate);
                long diff = diffTime();
                long xx = planed - today;
                if (xx < 0) {
                    bean.setDateType(4);//今天之前的  已结束的
                } else if (xx >= 0 && xx < diff) {
                    bean.setDateType(1); //今天的
                    bean.setMins((int) (xx / 1000 / 60));
                } else if (xx >= diff && xx < 86400000 * 2) {
                    bean.setDateType(2); //明天的
                } else if (xx >= 86400000 * 2) {
                    bean.setDateType(3);//后天及以后
                }
            }
        }
        return serviceBeanList;

    }

    private Dialog dialog;

    private void deleteMeeting(final ServiceBean bean) {
        final LayoutInflater inflater = LayoutInflater
                .from(getActivity());
        View windov = inflater.inflate(R.layout.confirmservice, null);
        windov.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        windov.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                new ApiTask(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = com.kloudsync.techexcel.service.ConnectService.getIncidentDataattachment(AppConfig.URL_PUBLIC + "Lesson/Delete?lessonID=" + bean.getId());
                        try {
                            int retcode = jsonObject.getInt("RetCode");
                            if (retcode == 0) {
                                loadMeetings();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start(ThreadManager.getManager());

            }
        });
//        dialog = new AlertDialog.Builder(getActivity()).show();
//        Window dialogWindow = dialog.getWindow();
//        WindowManager m = getActivity().getWindowManager();
//        Display d = m.getDefaultDisplay();
//        WindowManager.LayoutParams p = dialogWindow.getAttributes();
//        p.width = (int) (d.getWidth() * 0.8);
//        dialogWindow.setAttributes(p);
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.setContentView(windov);


        dialog = new Dialog(getActivity(), R.style.bottom_dialog);

        dialog.setContentView(windov);
        dialog.getWindow().setWindowAnimations(R.style.dialogwindowAnim);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getActivity().getResources().getDisplayMetrics().widthPixels;
        dialog.getWindow().setAttributes(params);
        dialog.show();
    }


    private long diffTime() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long diff = cal.getTimeInMillis() - System.currentTimeMillis();
        return diff;
    }

	/**
	 * 开始会议接口回调
	 */
	public interface OnStartMeetingCallBackListener {
		void startMeetingCallBack();
	}

	OnStartMeetingCallBackListener onStartMeetingCallBackListener;

	public void setOnStartMeetingCallBackListener(OnStartMeetingCallBackListener onStartMeetingCallBackListener) {
		this.onStartMeetingCallBackListener = onStartMeetingCallBackListener;
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_VIEW&&resultCode==MeetingViewActivity.RESULT_SRART_MEETING){
            if(onStartMeetingCallBackListener!=null){
                onStartMeetingCallBackListener.startMeetingCallBack();
            }
        }else if(requestCode==REQUEST_EDIT){
            reLoadMeetings();
        }
    }

    public void reLoadMeetings(){
        Observable.just(1).observeOn(Schedulers.io()).map(new Function<Integer, List<ServiceBean>>() {
            @Override
            public List<ServiceBean> apply(Integer integer) throws Exception {
                return requestMeetings();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ServiceBean>>() {
            @Override
            public void accept(List<ServiceBean> serviceBeans) throws Exception {
                loadMeeting(serviceBeans);
            }
        });
    }

    private static void ListSort(List<ServiceBean> list) {
        Collections.sort(list, new Comparator<ServiceBean>() {
            @Override
            public int compare(ServiceBean o1, ServiceBean o2) {
                try {
                    String x1 = o1.getPlanedStartDate();
                    String x2 = o2.getPlanedStartDate();
                    if (Long.parseLong(x1) < Long.parseLong(x2)) {
                        return -1;
                    } else if (Long.parseLong(x1) > Long.parseLong(x2)) {
                        return 1;
                    } else {
                        return 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }


}
