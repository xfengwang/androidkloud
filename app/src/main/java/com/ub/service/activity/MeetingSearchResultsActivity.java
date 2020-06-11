package com.ub.service.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.techexcel.adapter.ServiceAdapter2;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.MeetingMoreOperationPopup;
import com.ub.techexcel.tools.ServiceTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MeetingSearchResultsActivity extends Activity implements View.OnClickListener,TextWatcher {

    EditText et_search;
    ImageView img_clear_edit;
    TextView tv_cancel;
    PullToRefreshListView listView;
    private TextView statustxt;

    //private List<List<ServiceBean>> mlist = new ArrayList<>();  //所有课程的list集合

    private List<ServiceBean> mList1 = new ArrayList<>();
    private List<ServiceBean> serviceBeans=new ArrayList<>();

    private int type=0;

    private ServiceAdapter2 serviceAdapter;

    String keyWord;
    private int currentPage=0;


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AppConfig.LOAD_FINISH:
                    if(type==1){
                        ListSort(mList1);
                    }
                    serviceAdapter.setOnModifyServiceListener(new ServiceAdapter2.OnModifyServiceListener() {
                        @Override
                        public void select(final ServiceBean bean) {
                            MeetingMoreOperationPopup meetingMoreOperationPopup = new MeetingMoreOperationPopup();
                            meetingMoreOperationPopup.getPopwindow(MeetingSearchResultsActivity.this);
                            meetingMoreOperationPopup.setFavoritePoPListener(new MeetingMoreOperationPopup.FavoritePoPListener() {
                                @Override
                                public void delete() {

                                }

                                @Override
                                public void view() {

                                }

                                @Override
                                public void edit() {

                                }

                                @Override
                                public void startMeeting() {
//                                    Intent intent = new Intent(MeetingSearchResultsActivity.this, WatchCourseActivity2.class);
//                                    intent.putExtra("userid", bean.getUserId());
//                                    intent.putExtra("meetingId", bean.getId() + "");
//                                    intent.putExtra("filemeetingId", bean.getId() + "");
//                                    intent.putExtra("teacherid", bean.getTeacherId());
//                                    intent.putExtra("identity", bean.getRoleinlesson());
//                                    intent.putExtra("isInstantMeeting", 0);
//                                    intent.putExtra("isStartCourse", true);
//                                    startActivity(intent);
                                }

                                @Override
                                public void dismiss() {
                                }

                                @Override
                                public void open() {
                                }

                                @Override
                                public void property() {

                                }

                            });
                            meetingMoreOperationPopup.StartPop(listView, bean, type);
                        }
                    });
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetingsearchresults);

        type=getIntent().getIntExtra("type",0);
        initView();

    }

    private void initView() {

        et_search = findViewById(R.id.et_search);
        img_clear_edit = findViewById(R.id.img_clear_edit);
        tv_cancel = findViewById(R.id.tv_cancel);
        listView = findViewById(R.id.list_doc);
        tv_cancel.setOnClickListener(this);
        et_search.addTextChangedListener(this);
        statustxt=findViewById(R.id.statustxt);
        switch (type){
            case 1:
                statustxt.setText(getString(R.string.upcoming));
                break;
            case 2:
                statustxt.setText(getString(R.string.pastdue));
                break;
            case 3:
                statustxt.setText(getString(R.string.pastfinish));
                break;
        }

        listView.setMode(PullToRefreshBase.Mode.BOTH);

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

            public void onPullDownToRefresh(PullToRefreshBase refreshView) {
                currentPage=0;
                loadMeetings(keyWord);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase refreshView) {
                currentPage+=1;
                loadMeetings(keyWord);
            }
        });

        serviceAdapter = new ServiceAdapter2(MeetingSearchResultsActivity.this, serviceBeans, true, type);

        serviceAdapter.setFromSearch(true, keyword);

        listView.setAdapter(serviceAdapter);


    }

    private void loadMeetings(final String key) {
        Observable.just(type).observeOn(Schedulers.io()).map(new Function<Integer, List<ServiceBean>>() {
            @Override
            public List<ServiceBean> apply(Integer integer) throws Exception {
                return requestMeetings(key);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ServiceBean>>() {
            @Override
            public void accept(List<ServiceBean> serviceBeans) throws Exception {
                loadMeeting(serviceBeans);
            }
        });
    }

    public void loadMeeting(List<ServiceBean> meetings) {
        if(type==1){
            ListSort(meetings);
        }else {
            meetings = sortBydata(meetings);
        }
        if(currentPage==0) serviceBeans.clear();
        serviceBeans.addAll(meetings);
        serviceAdapter.notifyDataSetChanged();
        listView.onRefreshComplete();
    }

    private List<ServiceBean> requestMeetings(String key) {
        String url = "";
        if (key == null || key.equals("")) {
            url = AppConfig.URL_PUBLIC
                    + "Lesson/List?roleID=3&isPublish=1&pageIndex="+currentPage+"&pageSize=20&type=" + type;
        } else {
            try {
                url = AppConfig.URL_PUBLIC
                        + "Lesson/List?roleID=3&isPublish=1&pageIndex="+currentPage+"&pageSize=20&type=" + type + "&keyword=" + URLEncoder.encode(LoginGet.getBase64Password(key), "UTF-8") ;
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
        List<ServiceBean> serviceBeanList=new ArrayList<ServiceBean>();
        try {
            int retCode = returnJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONArray retdata = returnJson.getJSONArray("RetData");
                    serviceBeanList.clear();
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
                        serviceBeanList.add(bean);
                    }
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return serviceBeanList;
    }


    /**
     * 获得所有原始数据
     */
    private void getAllServiceData(String keyword) {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                Context.MODE_PRIVATE);
        int schoolId = sharedPreferences.getInt("SchoolID", -1);

        List<ServiceBean> list = new ArrayList<>();
        mList1.clear();

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        executorService.execute(new ServiceTool(type, list,keyword));

        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {
                List<ServiceBean> serviceBeanList = sortBydata(list);
                for (ServiceBean bean : serviceBeanList) {
                    mList1.add(bean);
                }
                Collections.reverse(mList1);
                handler.sendEmptyMessage(AppConfig.LOAD_FINISH);
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


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
                    return 1;
                }
                if (Long.parseLong(x1) == Long.parseLong(x2)) {
                    return 0;
                }
                return -1;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                hideInput();
                finish();
                break;
        }
    }

    @Override
    protected void onStop() {
        hideInput();
        super.onStop();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }


    private String keyword;
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

//        keyword=charSequence.toString();
//
//        getAllServiceData(keyword);


    }

    @Override
    public void afterTextChanged(Editable editable) {
        keyWord=et_search.getText().toString().trim();
        currentPage=0;
        loadMeetings(et_search.getText().toString().trim());
    }

    private void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
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
