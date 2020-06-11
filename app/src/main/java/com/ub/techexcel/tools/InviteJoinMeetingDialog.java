package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventClose;
import com.kloudsync.techexcel.bean.EventJoinMeeting;
import com.kloudsync.techexcel.bean.EventRefreshMembers;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.MeetingKit;
import com.kloudsync.techexcel.help.PopMeetingMenu;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.tool.MeetingSettingCache;
import com.kloudsync.techexcel.ui.DocAndMeetingActivity;
import com.ub.service.activity.AlertJoinActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by wang on 2017/9/18.
 */

public class InviteJoinMeetingDialog implements View.OnClickListener {

    private Context context;
    private Dialog settingDialog;
    private View view;
    private String meetingId;
    private TextView join;
    private ImageView cancel;
    private TextView name,count;

    public void getPopupWindowInstance(Activity host) {
        if (null != settingDialog) {
            settingDialog.dismiss();
            return;
        } else {
            initPopuptWindow(host);
        }
    }

    public void initPopuptWindow(final Context host) {
        this.context = host;
        LayoutInflater layoutInflater = LayoutInflater.from(host);
        view = layoutInflater.inflate(R.layout.invitejoinactivity, null);
        join = view.findViewById(R.id.joinmeeting);
        join.setOnClickListener(this);
        cancel = view.findViewById(R.id.cancel);
        name = view.findViewById(R.id.name);
        count = view.findViewById(R.id.count);
        cancel.setOnClickListener(this);

        settingDialog = new Dialog(host, R.style.my_dialog);
        settingDialog.setContentView(view);
        settingDialog.setCancelable(false);
        Window window = settingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = host.getResources().getDimensionPixelSize(R.dimen.meeting_setting_dialog_width);
        settingDialog.getWindow().setAttributes(lp);
        settingDialog.setCanceledOnTouchOutside(false);

    }



    @SuppressLint("NewApi")
    public void show(String meetingId) {
        this.meetingId = meetingId;
        if (settingDialog != null && !settingDialog.isShowing()) {
            settingDialog.show();
        }
//        MeetingConfig meetingConfig=new MeetingConfig();
//        meetingConfig.setMeetingId(meetingId);
//        MeetingKit.getInstance().requestMeetingMembers(meetingConfig, true);

    }

    public void refresh(EventRefreshMembers refreshMembers) {
        MeetingConfig meetingConfig=refreshMembers.getMeetingConfig();
        List<MeetingMember> meetingMembers=meetingConfig.getMeetingMembers();
        if(meetingMembers!=null&&meetingMembers.size()>0){
            for (MeetingMember meetingMember : meetingMembers) {
                if(meetingConfig.getMeetingHostId().equals(meetingMember.getUserId()+"")){
                    name.setText(meetingMember.getUserName());
                    count.setText(meetingMembers.size()+"人");
                    break;
                }
            }
        }
    }



    public boolean isShowing() {
        if (settingDialog != null) {
            return settingDialog.isShowing();
        }
        return false;
    }

    public void dismiss() {
        if (settingDialog != null) {
            settingDialog.dismiss();
            settingDialog = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.joinmeeting:
                joinmeeting(meetingId);
                Tools.clearClipboard(context);
                break;
            case R.id.cancel:
                Tools.clearClipboard(context);
                dismiss();
                break;
            default:
                break;
        }
    }


    private void joinmeeting(String meetingId) {
        if (!TextUtils.isEmpty(meetingId)) {
            meetingId = meetingId.trim().toUpperCase();
            Log.e("alertjoinactivity",meetingId+"  "+AppConfig.ClassRoomID);
            if (meetingId.equals(AppConfig.ClassRoomID.toUpperCase())) {
                context.getSharedPreferences(AppConfig.LOGININFO,
                        MODE_PRIVATE).edit().putString("join_meeting_room", "").commit();
                Toast.makeText(context, "这是您自己的会议ID", Toast.LENGTH_LONG).show();
            }else {
                doJoin(meetingId);
            }
        }

    }


    private void doJoin(final String meetingRoom) {
        Observable.just(meetingRoom).observeOn(Schedulers.io()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String meetingId) throws Exception {
                JSONObject result = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/CheckClassRoomExist?classroomID=" + meetingId);
                Log.e("do_join", AppConfig.URL_PUBLIC + "Lesson/CheckClassRoomExist?classRoomID=" + meetingId + ",result:" + result);
                if (result.has("RetCode")) {
                    int retCode = result.getInt("RetCode");
                    if (retCode == 0) {
                        if (result.has("RetData")) {
                            if (result.getInt("RetData") == 1) {
                                final EventJoinMeeting joinMeeting = new EventJoinMeeting();
                                joinMeeting.setMeetingId(meetingRoom);
                                Observable.just(joinMeeting).observeOn(Schedulers.newThread()).doOnNext(new Consumer<EventJoinMeeting>() {
                                    @Override
                                    public void accept(EventJoinMeeting eventJoinMeeting) throws Exception {
                                        JSONObject result = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/GetClassRoomLessonID?classRoomID=" + meetingRoom);
                                        Log.e("do_join", AppConfig.URL_PUBLIC + "Lesson/GetClassRoomLessonID?classRoomID=" + meetingRoom + ",result:" + result);
                                        if (result.has("RetCode")) {
                                            int retCode = result.getInt("RetCode");
                                            if (retCode == 0) {
                                                joinMeeting.setLessionId(result.getInt("RetData"));
                                                joinMeeting.setMeetingId(meetingRoom);
                                                joinMeeting.setOrginalMeetingId(meetingRoom);
                                            }
                                        }
                                    }
                                }).doOnNext(new Consumer<EventJoinMeeting>() {
                                    @Override
                                    public void accept(EventJoinMeeting eventJoinMeeting) throws Exception {
                                        if (joinMeeting.getLessionId() <= 0) {
                                            JSONObject result = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/GetClassRoomTeacherID?classroomID=" + meetingRoom);
                                            Log.e("do_join", AppConfig.URL_PUBLIC + "Lesson/GetClassRoomTeacherID?classroomID=" + meetingRoom + ",result:" + result);
                                            if (result.has("RetCode")) {
                                                int retCode = result.getInt("RetCode");
                                                if (retCode == 0) {
                                                    int hostId = result.getInt("RetData");
                                                    eventJoinMeeting.setHostId(hostId);
                                                }
                                            }
                                        }
                                    }
                                }).doOnNext(new Consumer<EventJoinMeeting>() {
                                    @Override
                                    public void accept(EventJoinMeeting eventJoinMeeting) throws Exception {
                                        if (eventJoinMeeting.getHostId() > 0 && eventJoinMeeting.getLessionId() == -1) {
                                            JSONObject result = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/UpcomingLessonList?teacherID=" + eventJoinMeeting.getHostId());
                                            Log.e("do_join", AppConfig.URL_PUBLIC + "Lesson/UpcomingLessonList?teacherID=" + eventJoinMeeting.getHostId() + ",result:" + result);
                                            if (result.has("RetCode")) {
                                                int retCode = result.getInt("RetCode");
                                                if (retCode == 0) {
                                                    JSONArray jsonArray = result.getJSONArray("RetData");
                                                    if (jsonArray != null && jsonArray.length() > 0) {
                                                        for (int i = 0; i < jsonArray.length(); ++i) {
                                                            JSONObject data = jsonArray.getJSONObject(i);
                                                            if (data.has("IsOnGoing")) {
                                                                int isOnGoing = data.getInt("IsOnGoing");
                                                                if (isOnGoing == 1) {
                                                                    if (data.has("LessonID")) {
                                                                        eventJoinMeeting.setLessionId(data.getInt("LessonID"));
                                                                        eventJoinMeeting.setMeetingId(data.getInt("LessonID") + "");
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                    }

                                }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<EventJoinMeeting>() {
                                    @Override
                                    public void accept(EventJoinMeeting eventJoinMeeting) throws Exception {
                                        joinMeeting(eventJoinMeeting);
                                    }
                                }).subscribe();
                            } else {
                                Observable.just("").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Exception {
                                        context.getSharedPreferences(AppConfig.LOGININFO,
                                                MODE_PRIVATE).edit().putString("join_meeting_room", "").commit();
                                        Toast.makeText(context, "输入正确的会议ID", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    } else {
                        Observable.just("").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                context.getSharedPreferences(AppConfig.LOGININFO,
                                        MODE_PRIVATE).edit().putString("join_meeting_room", "").commit();
                                Toast.makeText(context, "输入正确的会议ID", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }
        }).subscribe();
    }



    public void joinMeeting(EventJoinMeeting eventJoinMeeting) {
        if (eventJoinMeeting.getLessionId() == -1) {
            goToWatingMeeting(eventJoinMeeting);
            return;
        }
        Intent intent = new Intent(context, DocAndMeetingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("meeting_id", eventJoinMeeting.getMeetingId());
        intent.putExtra("meeting_type", 0);
        intent.putExtra("lession_id", eventJoinMeeting.getLessionId());
        intent.putExtra("meeting_role", eventJoinMeeting.getRole());
        intent.putExtra("from_meeting", true);
        context.startActivity(intent);
        dismiss();
    }



    private void goToWatingMeeting(EventJoinMeeting eventJoinMeeting){
        Intent intent = new Intent(context, DocAndMeetingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("meeting_id", eventJoinMeeting.getMeetingId());
        intent.putExtra("meeting_type", 0);
        intent.putExtra("lession_id", -1);
        intent.putExtra("meeting_role", eventJoinMeeting.getRole());
        intent.putExtra("from_meeting", true);
        context.startActivity(intent);
        dismiss();
    }



}
