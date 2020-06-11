package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.telecom.Call;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.bean.MeetingPauseOrResumBean;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.PrivateCoachManager;
import com.kloudsync.techexcel.tool.ToastUtils;
import com.ub.techexcel.bean.AgoraMember;
import com.ub.techexcel.tools.MeetingServiceTools;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Faction:会议暂停管理类
 */
public class MeetingPauseManager implements View.OnClickListener {
    private static MeetingPauseManager mInstance;
    private Activity mActivity;
    private RelativeLayout mMeetingPauseLayout;
    private RelativeLayout mMeetingPauseBigLayout;
    private TextView mMeetingPauseTitle;
    private ImageView mMeetingPauseMinimize;
    private TextView mMeetingPauseTips;
    private EditText mMeetingPauseEditTips;
    private TextView mMeetingPauseTime;
    private RelativeLayout mRlyMeetingResume;
    private RelativeLayout mEndCoaching;
    private TextView mMeetingResumeBtn;
    private MeetingConfig mMeetingConfig;
    private SpannableStringBuilder mStringBuilder;
    private ImageSpan mImageSpan;
    private ClickableSpan mClickableSpan;
    private RelativeLayout mMeetingPauseMinLayout;
    private TextView mMeetingPauseMinTitle;
    private TextView mMeetingPauseMinTime;
    private ImageView mMeetingPausebig;
    private ImageView img_coach_icon;
    private Timer mPauseTimer;
    private TimerTask mPauseTimerTask;
    private boolean mStopPauseTimerTask;
    private long mPauseTime = 0;
    private RelativeLayout mRlyMeetingPauseEdit;
    private TextView mTipsEditOk;
    private TextView mTipsEditCancle;
    private InputMethodManager mImm;
    //    private SimpleDraweeView coachMemberImage;
    private TextView coachMemberName;
    private RelativeLayout coachLayout;
    private RelativeLayout audiencesLayout;
    private TextView audiencesPromptText;
    private ImageView coach_sync;

    public MeetingPauseManager(Activity activity, MeetingConfig config) {
        mMeetingConfig = config;
        mActivity = activity;
        initView(activity);
    }

    public static MeetingPauseManager getInstance(Activity activity, MeetingConfig config) {
        if (mInstance == null) {
            synchronized (MeetingPauseManager.class) {
                if (mInstance == null) {
                    mInstance = new MeetingPauseManager(activity, config);
                }
            }
        }
        return mInstance;
    }

    private void initView(Activity activity) {
        mImm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mMeetingPauseLayout = activity.findViewById(R.id.rly_meeting_pause_layout);
        mMeetingPauseBigLayout = activity.findViewById(R.id.rly_meeting_pause_big);
        mMeetingPauseTitle = activity.findViewById(R.id.tv_meeting_pause_title);
        coach_sync = activity.findViewById(R.id.coach_sync);
        mMeetingPauseMinimize = activity.findViewById(R.id.iv_meeting_pause_minimize);
        mMeetingPauseTips = activity.findViewById(R.id.tv_meeting_pause_tips_text);
        mRlyMeetingPauseEdit = activity.findViewById(R.id.rly_meeting_pause_edit);
        mTipsEditOk = activity.findViewById(R.id.tv_edit_pause_tips_ok);
        mTipsEditCancle = activity.findViewById(R.id.tv_edit_pause_tips_cancle);
        mMeetingPauseEditTips = activity.findViewById(R.id.et_meeting_pause_tips_text);
        mMeetingPauseTime = activity.findViewById(R.id.tv_meeting_pause_time);
        mRlyMeetingResume = activity.findViewById(R.id.rly_meeting_resume);
        mEndCoaching = activity.findViewById(R.id.rly_end_coach);
        mMeetingResumeBtn = activity.findViewById(R.id.btn_meeting_resume);
        mMeetingPauseMinLayout = activity.findViewById(R.id.rly_meeting_pause_minimize);
        mMeetingPauseMinTitle = activity.findViewById(R.id.tv_meeting_pause_minimize_title);
        mMeetingPauseMinTime = activity.findViewById(R.id.tv_meeting_pause_min_time);
        mMeetingPausebig = activity.findViewById(R.id.iv_meeting_pause_big);
        mTipsEditOk.setOnClickListener(this);
        mTipsEditCancle.setOnClickListener(this);
        mMeetingPauseMinimize.setOnClickListener(this);
        mMeetingPausebig.setOnClickListener(this);
        mRlyMeetingResume.setOnClickListener(this);
        mEndCoaching.setOnClickListener(this);
        mStringBuilder = new SpannableStringBuilder();
        Drawable drawable = activity.getResources().getDrawable(R.drawable.meeting_pause_edit_tips_icon);
        int widthiAndHeight = activity.getResources().getDimensionPixelOffset(R.dimen.dp_14);
        drawable.setBounds(0, 0, widthiAndHeight, widthiAndHeight);
        mImageSpan = new ImageSpan(drawable);
        mClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String tips = mMeetingPauseTips.getText().toString().replaceAll("\t", "");
                mMeetingPauseEditTips.setText(tips);
                mMeetingPauseEditTips.setFocusable(true);
                mMeetingPauseEditTips.setFocusableInTouchMode(true);
                mMeetingPauseEditTips.requestFocus();
//				mMeetingPauseEditTips.requestFocusFromTouch();
                mImm.showSoftInput(mMeetingPauseEditTips, InputMethodManager.SHOW_FORCED);
                mMeetingPauseTips.setVisibility(View.GONE);
                mRlyMeetingPauseEdit.setVisibility(View.VISIBLE);
            }
        };

        if (mMeetingConfig.getSystemType() == AppConfig.COMPANY_MODEL) {//如果是会议模式
            setTipInfo(mActivity.getString(R.string.meeting_pause_tips));
        } else {//如果是课堂模式
            setTipInfo(mActivity.getString(R.string.class_pause_tips));
        }
    }

    public void initCoachLayout(RelativeLayout coachLayout, MeetingConfig meetingConfig) {
        this.mMeetingConfig = meetingConfig;
        this.coachLayout = coachLayout;
        this.coachMemberName = coachLayout.findViewById(R.id.txt_coach_name);
        this.img_coach_icon = coachLayout.findViewById(R.id.img_coach_icon);
        img_coach_icon.setOnClickListener(this);
        if (meetingConfig.getPrivateCoachMember() != null) {
            this.coachLayout.setVisibility(View.VISIBLE);
            fillPrivateCoachLayout(meetingConfig.getPrivateCoachMember());
        }
        if (isHost() && meetingConfig.getPrivateCoachMember().getUserId() > 0) {
            mEndCoaching.setVisibility(View.VISIBLE);
            mRlyMeetingResume.setVisibility(View.GONE);
        }
    }

    public void clearCoachLayout() {
        if (coachLayout != null) {
            coachLayout.setVisibility(View.GONE);
            if (audiencesLayout != null) {
                audiencesLayout.setVisibility(View.GONE);
                audiencesPromptText.setText("");
            }
        }
        if (isHost()) {
            mEndCoaching.setVisibility(View.GONE);
            mRlyMeetingResume.setVisibility(View.VISIBLE);
        }
    }

    public void removeMemberCoachLayout() {
        if (audiencesLayout != null) {
            initAudiencesLayout(audiencesLayout, mMeetingConfig, mActivity.getResources().getString(R.string.prompt_joined_private_channel));
        }
    }

    private List<AgoraMember> agoraMembers = new ArrayList<>();

    public void initAudiencesLayout(RelativeLayout audiencesLayout, MeetingConfig meetingConfig, String endPromt) {
        Log.e("initAudiencesLayout", "audience:" + mMeetingConfig.getPrivateCoachAudiences().size());
        this.audiencesLayout = audiencesLayout;
        this.mMeetingConfig = meetingConfig;
        this.audiencesPromptText = audiencesLayout.findViewById(R.id.txt_coach_audiences);
        if (meetingConfig.getPrivateCoachAudiences() != null && meetingConfig.getPrivateCoachAudiences().size() > 0) {
            List<AgoraMember> audienceMemebers = new ArrayList<>();
            for (AgoraMember agoraMember : meetingConfig.getPrivateCoachAudiences()) {
                if (agoraMember.isCoachingAudience()) {
                    audienceMemebers.add(agoraMember);
                }
            }
            if (audienceMemebers.size() > 0) {
                agoraMembers.clear();
                agoraMembers.addAll(audienceMemebers);
                if (mMeetingPauseBigLayout.getVisibility() == View.VISIBLE) {
                    this.audiencesLayout.setVisibility(View.VISIBLE);
                } else if (mMeetingPauseMinLayout.getVisibility() == View.VISIBLE) {
                    this.audiencesLayout.setVisibility(View.GONE);
                }
                String audiences = "";
                for (int i = 0; i < audienceMemebers.size(); i++) {
                    if (i > 4) {
                        break;
                    }
                    AgoraMember member = audienceMemebers.get(i);
                    audiences += member.getUserName() + ",";
                }
                if (audiences.length() > 0) {
                    audiences = audiences.substring(0, audiences.length() - 1);
                }
                audiences += "  " + audienceMemebers.size() + " " + endPromt;
                audiencesPromptText.setText(audiences);
            } else {
                agoraMembers.clear();
                audiencesLayout.setVisibility(View.GONE);
                audiencesPromptText.setText("");
            }
        } else {
            agoraMembers.clear();
            audiencesLayout.setVisibility(View.GONE);
            audiencesPromptText.setText("");
        }
    }

    public void fillPrivateCoachLayout(MeetingMember meetingMember) {
        this.coachMemberName.setText(meetingMember.getUserName());
        String url = meetingMember.getAvatarUrl();
        Uri imageUri = null;
        if (!TextUtils.isEmpty(url)) {
            imageUri = Uri.parse(url);
        }
//        this.coachMemberImage.setImageURI(imageUri);
        adjustCoachLayoutLocation();
    }

    private void resetCoachLayout() {
        if (this.coachLayout != null) {
            this.coachMemberName.setText("");
        }
        if (this.audiencesLayout != null) {
            this.audiencesLayout.setVisibility(View.GONE);
            this.audiencesPromptText.setText("");
        }


    }

    private void adjustCoachLayoutLocation() {
        if (this.coachLayout != null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) coachLayout.getLayoutParams();
            if (mMeetingPauseBigLayout.getVisibility() == View.VISIBLE) {
                params.addRule(RelativeLayout.BELOW, R.id.rly_meeting_pause_big);
            } else if (mMeetingPauseMinLayout.getVisibility() == View.VISIBLE) {
                params.addRule(RelativeLayout.BELOW, R.id.rly_meeting_pause_minimize);
            }
            this.coachLayout.setLayoutParams(params);
        }

    }


    public void setTipInfo(String tips) {
        mStringBuilder.clear();
        mStringBuilder.clearSpans();
        if (isHost()) {
            tips += "\t\t\t";
            mStringBuilder.append(tips);
            mStringBuilder.setSpan(mImageSpan, tips.length() - 2, tips.length() - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            mStringBuilder.setSpan(mClickableSpan, tips.length() - 2, tips.length() - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            mStringBuilder.append(tips);
        }
        mMeetingPauseTips.setText(mStringBuilder);
        mMeetingPauseTips.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public String getPauseTipsText() {
        String tipsText = mMeetingPauseTips.getText().toString().replaceAll("\t", "");
        return tipsText;
    }

    public long getPauseTime() {
        return mPauseTime;
    }

    /**
     * 是否是主持人或是否是老师
     *
     * @return
     */
    private boolean isHost() {
        if (mMeetingConfig.getMeetingHostId().equals(AppConfig.UserID) || mMeetingConfig.getPresenterId().equals(AppConfig.UserID)) {
            return true;
        }
        return false;
    }

    /**
     * 显示暂停布局
     */
    public void showBigLayout() {
        mMeetingPauseEditTips.setText("");
        mRlyMeetingPauseEdit.setVisibility(View.GONE);
        mMeetingPauseTips.setVisibility(View.VISIBLE);
        if (mMeetingConfig.getSystemType() == AppConfig.COMPANY_MODEL) {//如果是会议模式
            mMeetingPauseTitle.setText(R.string.the_meeting_is_suspended);
            mMeetingPauseMinTitle.setText(R.string.meeting_suspended);
            if (isHost()) {
                mMeetingResumeBtn.setText(R.string.resume_meeting);
                mRlyMeetingResume.setVisibility(View.VISIBLE);
            } else {
                mRlyMeetingResume.setVisibility(View.GONE);
            }
        } else {  //如果是课堂模式
            mMeetingPauseTitle.setText(R.string.practice_time);
            mMeetingPauseMinTitle.setText(R.string.practice_in_class);
            if (isHost()) {//如果是老师
                mMeetingResumeBtn.setText(R.string.continue_class);
                mRlyMeetingResume.setVisibility(View.VISIBLE);
            } else {
                mRlyMeetingResume.setVisibility(View.GONE);
            }
        }

        startMeetingPauseTime();
        mMeetingPauseLayout.setVisibility(View.VISIBLE);
        mMeetingPauseBigLayout.setVisibility(View.VISIBLE);
        mMeetingPauseMinLayout.setVisibility(View.GONE);
        if (audiencesLayout != null) {
            if (agoraMembers != null && agoraMembers.size() > 0) {
                audiencesLayout.setVisibility(View.VISIBLE);
            } else {
                audiencesLayout.setVisibility(View.GONE);
            }
        }
        adjustCoachLayoutLocation();
    }

    /**
     * 暂停布局最小化
     */
    private void showMinLayout() {
        mMeetingPauseBigLayout.setVisibility(View.GONE);
        mMeetingPauseMinLayout.setVisibility(View.VISIBLE);
        if (audiencesLayout != null) {
            audiencesLayout.setVisibility(View.GONE);
        }
        adjustCoachLayoutLocation();
    }

    /**
     * 隐藏暂停布局
     */
    public void hide() {
        if (mImm.isActive()) {
            mMeetingPauseEditTips.setFocusable(false);
            mMeetingPauseEditTips.setFocusableInTouchMode(false);
            mMeetingPauseEditTips.requestFocus();
//					mMeetingPauseEditTips.requestFocusFromTouch();
            mImm.hideSoftInputFromWindow(mMeetingPauseLayout.getWindowToken(), 0);
        }
        if (coachLayout != null) {
            resetCoachLayout();
            coachLayout.setVisibility(View.GONE);
        }
        mMeetingPauseLayout.setVisibility(View.GONE);
        stopMeetingPauseTime();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_meeting_pause_minimize:
                showMinLayout();
                break;
            case R.id.rly_meeting_resume:
                if (mImm.isActive()) {
                    mMeetingPauseEditTips.setFocusable(false);
                    mMeetingPauseEditTips.setFocusableInTouchMode(false);
                    mMeetingPauseEditTips.requestFocus();
//					mMeetingPauseEditTips.requestFocusFromTouch();
                    mImm.hideSoftInputFromWindow(mMeetingPauseLayout.getWindowToken(), 0);
                }
                requestMeetingResume();
                break;
            case R.id.rly_end_coach:
                if (mMeetingConfig.isCoaching()) {
                    PrivateCoachManager.getManager(mActivity).endTeacherCoach(mMeetingConfig);
                }
                break;
            case R.id.iv_meeting_pause_big:
                showBigLayout();
                break;
            case R.id.tv_edit_pause_tips_ok:
                requestMeetingPauseMessage();
                break;
            case R.id.img_coach_icon:
                PrivateCoachManager.getManager(mActivity).openStartAllStuds(img_coach_icon, mMeetingConfig);
                break;
            case R.id.tv_edit_pause_tips_cancle:
                if (mImm.isActive()) {
                    mMeetingPauseEditTips.setFocusable(false);
                    mMeetingPauseEditTips.setFocusableInTouchMode(false);
                    mMeetingPauseEditTips.requestFocus();
//					mMeetingPauseEditTips.requestFocusFromTouch();
                    mImm.hideSoftInputFromWindow(mMeetingPauseLayout.getWindowToken(), 0);
                }
                mMeetingPauseEditTips.setText("");
                mMeetingPauseTips.setVisibility(View.VISIBLE);
                mRlyMeetingPauseEdit.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 继续会议
     */
    private void requestMeetingResume() {
        Observable.just("meeting_pause").observeOn(Schedulers.io()).map(new Function<String, MeetingPauseOrResumBean>() {
            @Override
            public MeetingPauseOrResumBean apply(String s) throws Exception {
                MeetingPauseOrResumBean meetingPauseOrResumBean = MeetingServiceTools.getInstance().requestMeetingResume();
                return meetingPauseOrResumBean;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<MeetingPauseOrResumBean>() {
            @Override
            public void accept(MeetingPauseOrResumBean bean) throws Exception {
                if (bean.getMsg() != null && bean.getMsg().equals("success")) {
                    bean.setPause(false);
                    EventBus.getDefault().post(bean);
                } else {
                    ToastUtils.show(mActivity, bean.getMsg());
                    return;
                }
            }
        }).subscribe();
    }

    /**
     * 修改提示文字
     */
    private void requestMeetingPauseMessage() {
        final String tips = mMeetingPauseEditTips.getText().toString();
        if (TextUtils.isEmpty(tips)) {
            ToastUtils.show(mActivity, R.string.the_content_can_not_be_blank);
            return;
        }
        Observable.just("meeting_pause").observeOn(Schedulers.io()).map(new Function<String, MeetingPauseOrResumBean>() {
            @Override
            public MeetingPauseOrResumBean apply(String s) throws Exception {
                MeetingPauseOrResumBean meetingPauseOrResumBean = MeetingServiceTools.getInstance().requestMeetingPauseMessage(tips);
                return meetingPauseOrResumBean;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<MeetingPauseOrResumBean>() {
            @Override
            public void accept(MeetingPauseOrResumBean bean) throws Exception {
                if (bean.getMsg() != null && bean.getMsg().equals("success")) {
                    if (mImm.isActive()) {
                        mMeetingPauseEditTips.setFocusable(false);
                        mMeetingPauseEditTips.setFocusableInTouchMode(false);
                        mMeetingPauseEditTips.requestFocus();
//						mMeetingPauseEditTips.requestFocusFromTouch();
                        mImm.hideSoftInputFromWindow(mMeetingPauseLayout.getWindowToken(), 0);
                    }
                    setTipInfo(tips);
                    mMeetingPauseEditTips.setText("");
                    mMeetingPauseTips.setVisibility(View.VISIBLE);
                    mRlyMeetingPauseEdit.setVisibility(View.GONE);
                } else {
                    ToastUtils.show(mActivity, bean.getMsg());
                }
            }
        }).subscribe();
    }

    /**
     * 开始计时
     */
    public void startMeetingPauseTime() {
        if (mPauseTimer == null && mPauseTimerTask == null) {
            mPauseTimer = new Timer();
            mPauseTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mStopPauseTimerTask) return;
                            updatePauseTime(mPauseTime++);
                        }
                    });
                }
            };
            mStopPauseTimerTask = false;
            mPauseTimer.schedule(mPauseTimerTask, 0, 1000);
        }
    }

    /**
     * 设置暂停时间
     *
     * @param time
     */
    public void setPauseTime(long time) {
        mPauseTime = time;
    }

    /**
     * 更新暂停时间文本显示
     *
     * @param time
     */
    private void updatePauseTime(long time) {
        long h = time / 3600;
        long m = time % 3600 / 60;
        long s = time % 60;
        String hours;
        String minute;
        String second;
        String pauseTime;
        if (!(h > 9)) {
            hours = "0" + h;
        } else {
            hours = String.valueOf(h);
        }

        if (!(m > 9)) {
            minute = "0" + m;
        } else {
            minute = String.valueOf(m);
        }
        if (!(s > 9)) {
            second = "0" + s;
        } else {
            second = String.valueOf(s);
        }
        if (h <= 0) {
            pauseTime = minute + ":" + second;
        } else {
            pauseTime = hours + ":" + minute + ":" + second;
        }
        mMeetingPauseTime.setText(pauseTime);
        mMeetingPauseMinTime.setText(pauseTime);
    }

    /**
     * 停止计时
     */
    private void stopMeetingPauseTime() {
        mStopPauseTimerTask = true;
        mPauseTime = 0;
        if (mPauseTimer != null && mPauseTimerTask != null) {
            mPauseTimer.cancel();
            mPauseTimerTask.cancel();
            mPauseTimer = null;
            mPauseTimerTask = null;
        }
    }

    public void destory() {
        hide();
        mMeetingPauseLayout = null;
        mMeetingPauseBigLayout = null;
        mMeetingPauseTitle = null;
        mMeetingPauseMinimize = null;
        mMeetingPauseTips = null;
        mMeetingPauseEditTips = null;
        mMeetingPauseTime = null;
        mRlyMeetingResume = null;
        mEndCoaching = null;
        mMeetingResumeBtn = null;
        mMeetingConfig = null;
        mStringBuilder = null;
        mImageSpan = null;
        mClickableSpan = null;
        mMeetingPauseMinLayout = null;
        mMeetingPauseMinTitle = null;
        mMeetingPauseMinTime = null;
        mMeetingPausebig = null;
        mActivity = null;
        mInstance = null;
    }


    public void updateSyncStatus(int status) {
        if (coach_sync == null) {
            return;
        }
        if (status == 1) {
            if (coach_sync.getVisibility() != View.VISIBLE) {
                coach_sync.setVisibility(View.VISIBLE);
                coach_sync.setAnimation(shakeAnimation(6));
            }
        } else {
            if (coach_sync.getVisibility() == View.VISIBLE) {
                coach_sync.setVisibility(View.GONE);
            }
        }
    }

    public static Animation shakeAnimation(int counts) {
        Animation rotateAnimation = new RotateAnimation(0, 20, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new CycleInterpolator(counts));
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setDuration(3000);
        return rotateAnimation;
    }
}
