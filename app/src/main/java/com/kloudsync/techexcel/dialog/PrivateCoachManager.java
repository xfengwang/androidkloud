package com.kloudsync.techexcel.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.bean.params.EventEndCoach;
import com.kloudsync.techexcel.bean.params.EventTalkAllStu;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.MeetingKit;
import com.kloudsync.techexcel.help.MeetingPauseManager;
import com.kloudsync.techexcel.tool.SocketMessageManager;
import com.ub.techexcel.adapter.AgoraCameraAdapterV2;
import com.ub.techexcel.bean.AgoraMember;
import com.ub.techexcel.tools.PopPrivateCoachOperations;
import com.ub.techexcel.tools.PopPrivateCoachTalkToAllOperations;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class PrivateCoachManager implements View.OnClickListener, PopPrivateCoachTalkToAllOperations.OnCoachOperationsListener {

    private Context mContext;
    private static Handler recordHandler;

    private PrivateCoachManager(Context context) {
        this.mContext = context;
        recordHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (recordHandler == null) {
                    return;
                }
                handlePlayMessage(msg);
                super.handleMessage(msg);
            }
        };
    }

    private static final int MESSAGE_PLAY_TIME_REFRESHED = 1;

    private void handlePlayMessage(Message message) {
        switch (message.what) {
            case MESSAGE_PLAY_TIME_REFRESHED:

                break;
        }

    }

    static volatile PrivateCoachManager instance;

    public static PrivateCoachManager getManager(Context context) {
        if (instance == null) {
            synchronized (SocketMessageManager.class) {
                if (instance == null) {
                    instance = new PrivateCoachManager(context);
                }
            }
        }
        return instance;
    }

    @Override
    public void onClick(View view) {

    }


    /**
     * 老师开启一对一成功
     */
    public void startPrivateCoachSuccess(MeetingConfig meetingConfig, AgoraMember member, AgoraCameraAdapterV2 cameraAdapter,
                                         MeetingPauseManager mMeetingPauseManager, RelativeLayout coachLayout) {
        myMeetingConfig = meetingConfig;
        myAgoraCameraAdapterV2 = cameraAdapter;
        myMeetingPauseManager = mMeetingPauseManager;

        meetingConfig.setCoaching(true);
        meetingConfig.setPrivateCoachMember(member.getUserId());
        if (mMeetingPauseManager != null) {
            mMeetingPauseManager.initCoachLayout(coachLayout, meetingConfig);
        }
        if (cameraAdapter != null) {
            cameraAdapter.setSpeakerMember(getSelectedAgora(member.getUserId() + "", meetingConfig));
            cameraAdapter.setCoachStudent(member);
        }
        MeetingKit.getInstance().handleMemberAgoraWhenCoaching();
    }


    private MeetingConfig myMeetingConfig;
    private AgoraCameraAdapterV2 myAgoraCameraAdapterV2;
    private MeetingPauseManager myMeetingPauseManager;
    private SoundtrackPlayManager soundtrackPlayManager;


    /**
     * 私教信息更新(老师 学生)
     *
     * @param meetingConfig
     * @param mMeetingPauseManager
     * @param cameraAdapter
     * @param messageManager
     * @param inviteUserId
     */
    public void updatePrivateCoachInfo(MeetingConfig meetingConfig, MeetingPauseManager mMeetingPauseManager,
                                       AgoraCameraAdapterV2 cameraAdapter, SocketMessageManager messageManager,
                                       int inviteUserId, int hostUserId, RelativeLayout coachLayout, SoundtrackPlayManager soundtrackPlayManager) {
        myMeetingConfig = meetingConfig;
        myAgoraCameraAdapterV2 = cameraAdapter;
        myMeetingPauseManager = mMeetingPauseManager;
        this.soundtrackPlayManager = soundtrackPlayManager;


        meetingConfig.setCoaching(true);
        meetingConfig.setPrivateCoachMember(inviteUserId);
        if (mMeetingPauseManager != null) {
            mMeetingPauseManager.initCoachLayout(coachLayout, meetingConfig);
        }
        if (cameraAdapter != null) {
            cameraAdapter.setSpeakerMember(getSelectedAgora(inviteUserId + "", meetingConfig));
            cameraAdapter.setCoachStudent(getSelectedAgora(inviteUserId + "", meetingConfig));
        }
        if (inviteUserId == Integer.parseInt(AppConfig.UserID)) {
            if (messageManager != null) {
                messageManager.sendMessage_DocumentShowedPrivateCoach(meetingConfig);
            }
            updateSyncPrivateCoaching(myMeetingConfig);

            if (soundtrackPlayManager != null) {
                if (soundtrackPlayManager.isPlaying()) {
                    soundtrackPlayManager.privatecoachnotifyplay();
                }

            }
        }
        MeetingKit.getInstance().handleMemberAgoraWhenCoaching();
    }


    private AgoraMember getSelectedAgora(String userId, MeetingConfig meetingConfig) {
        if (meetingConfig.getAgoraMembers() != null && meetingConfig.getAgoraMembers().size() > 0) {
            for (AgoraMember selectedAgora : meetingConfig.getAgoraMembers()) {
                if ((selectedAgora.getUserId() + "").equals(userId)) {
                    return selectedAgora;
                }
            }
        }
        return null;
    }


    /**
     * 结束私教
     *
     * @param meetingConfig
     * @param cameraAdapter
     * @param mMeetingPauseManager
     */
    public void endCoach(MeetingConfig meetingConfig, AgoraCameraAdapterV2 cameraAdapter, MeetingPauseManager mMeetingPauseManager) {

        if (meetingConfig.getPrivateCoachMember().getUserId() == Integer.parseInt(AppConfig.UserID)) {
            if (soundtrackPlayManager != null) {
                if (soundtrackPlayManager.isPlaying()) {
                    soundtrackPlayManager.privatecoachnotifyend();
                }
            }
        }
        MeetingKit.getInstance().unmuteAllMember();
        meetingConfig.clearCoachAudiences();
        cameraAdapter.clearCoach();
        if (mMeetingPauseManager != null) {
            mMeetingPauseManager.clearCoachLayout();
        }
        meetingConfig.setCoaching(false);


    }


    /**
     * 移除成员
     */
    public void removeMemberCoach(MeetingConfig meetingConfig, AgoraCameraAdapterV2 cameraAdapter, MeetingPauseManager mMeetingPauseManager, int userId) {
        meetingConfig.clearCoachMeAudiences(userId);
        cameraAdapter.updateUser(userId);
        MeetingKit.getInstance().handleMemberAgoraWhenCoaching();
        if (mMeetingPauseManager != null) {
            mMeetingPauseManager.removeMemberCoachLayout();
        }
    }


    /**
     * 结束私教
     *
     * @param meetingConfig
     */
    public void endTeacherCoach(final MeetingConfig meetingConfig) {
        if (meetingConfig.getSettingValue() == 1 && talkallstudent != null) { //正在talk all students
            String url = AppConfig.URL_MEETING_BASE + "meeting/update_meeting_setting";
            ServiceInterfaceTools.getinstance().updateMeetingSetting(url, ServiceInterfaceTools.UPDATEMEETINGSETTING, 0, new ServiceInterfaceListener() {
                @Override
                public void getServiceReturnData(Object object) {
                    meetingConfig.setSettingValue(0);
                    talkallstudent.setVisibility(View.GONE);
                    MeetingKit.getInstance().stoptalkAllStudents();
                    endTeacherCoach2(meetingConfig);
                }
            });
        } else {
            endTeacherCoach2(meetingConfig);
        }
    }

    private void endTeacherCoach2(final MeetingConfig meetingConfig) {
        Observable.just(meetingConfig.getPrivateCoachMember()).observeOn(Schedulers.io()).map(new Function<MeetingMember, JSONObject>() {
            @Override
            public JSONObject apply(final MeetingMember member) throws Exception {
                return ServiceInterfaceTools.getinstance().syncRequstPrivateCoachingStop();

            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if (jsonObject != null && jsonObject.has("code")) {
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        EventBus.getDefault().post(new EventEndCoach());
                    }
                }
            }
        }).subscribe();
    }


    /**
     * 移除成员
     *
     * @param userId
     */
    public void removePrivateCoachMemberCoach(final MeetingConfig meetingConfig, final AgoraCameraAdapterV2 cameraAdapter, final MeetingPauseManager mMeetingPauseManager, final int userId) {
        Observable.just(userId).observeOn(Schedulers.io()).map(new Function<Integer, JSONObject>() {
            @Override
            public JSONObject apply(final Integer member) throws Exception {
                return ServiceInterfaceTools.getinstance().syncRequstPrivateCoachingRemove(userId);

            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if (jsonObject != null && jsonObject.has("code")) {
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        removeMemberCoach(meetingConfig, cameraAdapter, mMeetingPauseManager, userId);
                    }
                }
            }
        }).subscribe();
    }

    /**
     * 邀请旁听生
     *
     * @param member
     * @param cameraAdapter
     */
    public void processInviteProvateCoachAudience(final AgoraMember member, final AgoraCameraAdapterV2 cameraAdapter) {
        Observable.just(member).observeOn(Schedulers.io()).map(new Function<AgoraMember, JSONObject>() {
            @Override
            public JSONObject apply(final AgoraMember member) throws Exception {
                JSONObject params = new JSONObject();
                JSONArray array = new JSONArray();
                array.put(member.getUserId());
                params.put("userIdList", array);
                return ServiceInterfaceTools.getinstance().syncRequstPrivateCoachingInviteAudience(params);
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if (jsonObject != null && jsonObject.has("code")) {
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        // 邀请audience成功;
                        if (cameraAdapter != null) {
                            cameraAdapter.setCoachToBeAudience(member);
                        }
                    }
                }
            }
        }).subscribe();
    }


    PopPrivateCoachTalkToAllOperations popPrivateCoachTalkToAllOperations;

    public void openStartAllStuds(ImageView img_coach_icon, MeetingConfig mMeetingConfig) {
        if (mMeetingConfig.getPresenterId().equals(AppConfig.UserID)) {
            if (popPrivateCoachTalkToAllOperations != null) {
                if (popPrivateCoachTalkToAllOperations.isShowing()) {
                    popPrivateCoachTalkToAllOperations.dismiss();
                    popPrivateCoachTalkToAllOperations = null;
                }
            }
            popPrivateCoachTalkToAllOperations = new PopPrivateCoachTalkToAllOperations(mContext);
            popPrivateCoachTalkToAllOperations.setOnCoachOperationsListener(this);
            popPrivateCoachTalkToAllOperations.show(img_coach_icon, mMeetingConfig);
        }
    }


    @Override
    public void onTalkToAllStudent(final MeetingConfig meetingConfig) {
        String url = AppConfig.URL_MEETING_BASE + "meeting/update_meeting_setting";
        ServiceInterfaceTools.getinstance().updateMeetingSetting(url, ServiceInterfaceTools.UPDATEMEETINGSETTING, 1, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                meetingConfig.setSettingValue(1);
                MeetingKit.getInstance().talkAllStudents();
                EventBus.getDefault().post(new EventTalkAllStu());
            }
        });
    }

    @Override
    public void onEndPrivateCoach(MeetingConfig meetingConfig) {
        endTeacherCoach(meetingConfig);
    }


    private TextView stopTalk;
    private LinearLayout talkallstudent;

    //stop talk all students
    public void stopTalkAllStudents(final MeetingConfig meetingConfig, final LinearLayout talkallstudent) {
        this.talkallstudent = talkallstudent;
        talkallstudent.setVisibility(View.VISIBLE);
        stopTalk = talkallstudent.findViewById(R.id.stoptalk);
        stopTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = AppConfig.URL_MEETING_BASE + "meeting/update_meeting_setting";
                ServiceInterfaceTools.getinstance().updateMeetingSetting(url, ServiceInterfaceTools.UPDATEMEETINGSETTING, 0, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        meetingConfig.setSettingValue(0);
                        talkallstudent.setVisibility(View.GONE);
                        MeetingKit.getInstance().stoptalkAllStudents();
                    }
                });
            }
        });
    }

    /**
     * @param teacherId
     * @param settingValue
     */
    public void isTeacherTalkToAllStu(MeetingConfig meetingConfig, String teacherId, int settingValue) {
        Log.e("isTeacherTalkToAllStu", teacherId + "  " + settingValue);
        meetingConfig.setSettingValue(settingValue);
        MeetingKit.getInstance().openTalkToTeacher(teacherId, settingValue);
    }


    public void leavePrivateCoaching() {

        Observable.just("").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(final String member) throws Exception {
                JSONObject params = new JSONObject();
                return ServiceInterfaceTools.getinstance().syncRequstPrivateCoachingLeave(params);
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if (jsonObject != null && jsonObject.has("code")) {
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        removeMemberCoach(myMeetingConfig, myAgoraCameraAdapterV2, myMeetingPauseManager, Integer.parseInt(AppConfig.UserID));
                    }
                }
            }
        }).subscribe();
    }


    /**
     * 学生更新自己录制音想的状态
     */
    public void updateSyncPrivateCoaching(final MeetingConfig meetingConfig) {
        if (meetingConfig.isCoaching()) {
            if (meetingConfig.getPrivateCoachMember().getUserId() == Integer.parseInt(AppConfig.UserID)) {
                Observable.just("").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
                    @Override
                    public JSONObject apply(final String member) throws Exception {
                        JSONObject params = new JSONObject();
                        int status = meetingConfig.isSyncing() ? 1 : 0;
                        params.put("syncStatus", status);
                        return ServiceInterfaceTools.getinstance().syncRequstPrivateCoachingUpdateSyncStatus(params, status);
                    }
                }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        if (jsonObject != null && jsonObject.has("code")) {
                            int code = jsonObject.getInt("code");
                            if (code == 0) {
                                updateSync(myMeetingPauseManager, meetingConfig.isSyncing() ? 1 : 0);
                            }
                        }
                    }
                }).subscribe();
            }
        }

    }


    public void updateSync(MeetingPauseManager mMeetingPauseManager, int status) {
        if (mMeetingPauseManager != null) {
            mMeetingPauseManager.updateSyncStatus(status);
        }
    }
}
