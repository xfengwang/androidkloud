package com.ub.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventJoinMeeting;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.ui.DocAndMeetingActivity;
import com.ub.techexcel.bean.NotifyBean;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wang on 2017/11/17.
 */

public class AlertJoinActivity extends Activity implements View.OnClickListener {

    private TextView join;
    private ImageView cancel;
    private String  meetingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertjoindialog);
        meetingId = getIntent().getStringExtra("meetingId");
        join = findViewById(R.id.joinmeeting);
        join.setOnClickListener(this);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.joinmeeting:
                joinmeeting(meetingId);
                Tools.clearClipboard(this);
                break;
            case R.id.cancel:
                Tools.clearClipboard(this);
                finish();
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
                getSharedPreferences(AppConfig.LOGININFO,
                        MODE_PRIVATE).edit().putString("join_meeting_room", "").commit();
                Toast.makeText(this, "这是您自己的会议ID", Toast.LENGTH_LONG).show();
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
                                        getSharedPreferences(AppConfig.LOGININFO,
                                                MODE_PRIVATE).edit().putString("join_meeting_room", "").commit();
                                        Toast.makeText(AlertJoinActivity.this, "输入正确的会议ID", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    } else {
                        Observable.just("").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                getSharedPreferences(AppConfig.LOGININFO,
                                        MODE_PRIVATE).edit().putString("join_meeting_room", "").commit();
                                Toast.makeText(AlertJoinActivity.this, "输入正确的会议ID", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this, DocAndMeetingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("meeting_id", eventJoinMeeting.getMeetingId());
        intent.putExtra("meeting_type", 0);
        intent.putExtra("lession_id", eventJoinMeeting.getLessionId());
        intent.putExtra("meeting_role", eventJoinMeeting.getRole());
        intent.putExtra("from_meeting", true);
        startActivity(intent);
        finish();
    }



    private void goToWatingMeeting(EventJoinMeeting eventJoinMeeting){
        Intent intent = new Intent(this, DocAndMeetingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("meeting_id", eventJoinMeeting.getMeetingId());
        intent.putExtra("meeting_type", 0);
        intent.putExtra("lession_id", -1);
        intent.putExtra("meeting_role", eventJoinMeeting.getRole());
        intent.putExtra("from_meeting", true);
        startActivity(intent);
        finish();
    }



}
