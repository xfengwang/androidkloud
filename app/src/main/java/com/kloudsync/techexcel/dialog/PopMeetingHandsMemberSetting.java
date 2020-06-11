package com.kloudsync.techexcel.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventKickOffMember;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.tool.DensityUtil;
import com.kloudsync.techexcel.tool.PopupWindowUtil;
import com.ub.techexcel.tools.Tools;

import org.greenrobot.eventbus.EventBus;

import static com.kloudsync.techexcel.bean.MeetingMember.TYPE_ITEM_HANDSUP_MEMBER;
import static com.kloudsync.techexcel.bean.MeetingMember.TYPE_ITEM_MAIN_SPEAKER;
import static com.kloudsync.techexcel.bean.MeetingMember.TYPE_ITEM_SPEAKING_SPEAKER;

/**
 * Created by tonyan on 2019/12/20.
 */

public class PopMeetingHandsMemberSetting extends PopupWindow implements View.OnClickListener {

    private Context context;

    private MeetingMember meetingMember;
    private TextView mAllowSpeak, mHandDown, mSetMainMembers, kickOffMember;
    private MeetingConfig meetingConfig;
    private View mView;

    public interface OnHandsMemberSettingChanged {
        void setHandsAllowSpeak(MeetingMember meetingMember);

        void setHandsDown(MeetingMember meetingMember);

        void setHandsMember(MeetingMember meetingMember);
    }

    private OnHandsMemberSettingChanged onMemberSettingChanged;

    public void setOnMemberSettingChanged(OnHandsMemberSettingChanged onMemberSettingChanged) {
        this.onMemberSettingChanged = onMemberSettingChanged;
    }

    public PopMeetingHandsMemberSetting(Context context) {
        super(context);
        this.context = context;
        initalize();
    }

    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.inflate(R.layout.pop_meeting_hands_member_options, null);
        mAllowSpeak = mView.findViewById(R.id.ppw_tv_speak);
        mHandDown = mView.findViewById(R.id.ppw_tv_hand_down);
        mSetMainMembers = mView.findViewById(R.id.ppw_tv_main_members);
        kickOffMember = mView.findViewById(R.id.txt_kick_off);
        kickOffMember.setOnClickListener(this);
        mSetMainMembers.setOnClickListener(this);
        mAllowSpeak.setOnClickListener(this);
        mHandDown.setOnClickListener(this);
        setContentView(mView);
        initWindow();
    }

    private void initWindow() {
        this.setWidth(context.getResources().getDimensionPixelOffset(R.dimen.meeting_members_setting_width));
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.update();

    }

    public void showAtBottom(MeetingMember meetingMember, View v, MeetingConfig meetingConfig) {
        this.meetingMember = meetingMember;
        this.meetingConfig = meetingConfig;
//        if(meetingMember.getPresenter() == 1){
//            mAllowSpeak.setVisibility(View.GONE);
//        }
//        if(meetingMember.getPresenter() == 1 || meetingMember.getRole() == 2){
//            mHandDown.setVisibility(View.GONE);
//        }
//        if(meetingConfig.getMeetingHostId().equals(meetingMember.getUserId()+"")){
//            // 操作的成员是HOST
//            kickOffMember.setVisibility(View.GONE);
//        }else {
//            // 不是HOST，如果自己是HOST
//            if(AppConfig.UserID.equals(meetingConfig.getMeetingHostId())){
//                kickOffMember.setVisibility(View.VISIBLE);
//            }else {
//                kickOffMember.setVisibility(View.GONE);
//            }
//        }

        //判断自己的身份
        if (meetingConfig.getMeetingHostId().equals(AppConfig.UserID)) {  // 主持人身份
            mSetMainMembers.setVisibility(View.VISIBLE);  //设为发言人
            mAllowSpeak.setVisibility(View.VISIBLE);   // 设为临时发言人
            mHandDown.setVisibility(View.VISIBLE); // 把手放下
            kickOffMember.setVisibility(View.VISIBLE); // 请他离开会议
        } else if (meetingConfig.getPresenterId().equals(AppConfig.UserID)) {  //演示者身份
            mSetMainMembers.setVisibility(View.VISIBLE);  //设为发言人
            mAllowSpeak.setVisibility(View.VISIBLE);   // 设为临时发言人
            mHandDown.setVisibility(View.VISIBLE); // 把手放下
        } else if (meetingConfig.getViewType() == TYPE_ITEM_MAIN_SPEAKER) { //发言人身份
            mSetMainMembers.setVisibility(View.VISIBLE);  //设为发言人
            mAllowSpeak.setVisibility(View.VISIBLE);   // 设为临时发言人
            mHandDown.setVisibility(View.VISIBLE); // 把手放下
        } else if (meetingConfig.getViewType() == TYPE_ITEM_SPEAKING_SPEAKER) { //临时发言人身份

        } else if (meetingConfig.getViewType() == TYPE_ITEM_HANDSUP_MEMBER) { //允许发言身份
            if ((meetingMember.getUserId() + "").equals(AppConfig.UserID)) {
                mHandDown.setVisibility(View.VISIBLE);
            } else {
                mHandDown.setVisibility(View.INVISIBLE);
            }
        } else {

        }
//        mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        int popupHeight = mView.getMeasuredHeight();
//        int xoff = -context.getResources().getDimensionPixelOffset(R.dimen.dp_160);
//        showAsDropDown(view, xoff, -popupHeight);

//        this.mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        int popupWidth = this.mView.getMeasuredWidth();
//        int popupHeight = this.mView.getMeasuredHeight();
//        int[] location = new int[2];
//        view.getLocationOnScreen(location);
//
//        showAtLocation(view, Gravity.NO_GRAVITY, location[0] - popupWidth - DensityUtil.dp2px(context, 40), location[1] + view.getHeight() / 2 - popupHeight / 2);

        int topLength=50;
        if(Tools.isOrientationPortrait((Activity)context )){
            topLength=1230;
            int windowPos[] = PopupWindowUtil.calculatePopWindowPos2(v, mView , topLength);
            int height = context.getResources().getDisplayMetrics().heightPixels;
            Log.e("duang", height + ":" + windowPos[1]+"  "+windowPos[0]);
            int xOff = 20; // 可以自己调整偏移
            windowPos[0] -= xOff;
            showAtLocation(v, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
        }else{
            topLength=50;
            int windowPos[] = PopupWindowUtil.calculatePopWindowPos(v, mView , topLength);
            int height = context.getResources().getDisplayMetrics().heightPixels;
            Log.e("duang", height + ":" + windowPos[1]+"  "+windowPos[0]);
            int xOff = 20; // 可以自己调整偏移
            windowPos[0] -= xOff;
            showAtLocation(v, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ppw_tv_speak://允许发言  临时发言人
                if (meetingMember != null && onMemberSettingChanged != null) {
                    onMemberSettingChanged.setHandsAllowSpeak(meetingMember);
                }
                dismiss();
                break;
            case R.id.ppw_tv_hand_down://把手放下
                if (meetingMember != null && onMemberSettingChanged != null) {
                    onMemberSettingChanged.setHandsDown(meetingMember);
                }
                dismiss();
                break;
            case R.id.ppw_tv_main_members://成为主讲人
                if (meetingMember != null && onMemberSettingChanged != null) {
                    onMemberSettingChanged.setHandsMember(meetingMember);

                }
                dismiss();
                break;
            case R.id.txt_kick_off:
                if (meetingMember != null && onMemberSettingChanged != null) {
                    Log.e("check_post_kick_off", "post_2");
                    EventKickOffMember kickOffMember = new EventKickOffMember();
                    kickOffMember.setMeetingMember(meetingMember);
                    EventBus.getDefault().post(kickOffMember);
                }
                dismiss();
            default:
                break;
        }
    }


}
