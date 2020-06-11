package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.techexcel.bean.AgoraMember;

public class PopPrivateCoachTalkToAllOperations implements View.OnClickListener {
    private Context mContext;
    private PopupWindow mPopupWindow;
    private View view;
    private RelativeLayout oneLayout, twoLayout;
    private MeetingConfig meetingConfig;

    public interface OnCoachOperationsListener{
        void onTalkToAllStudent(MeetingConfig meetingConfig);
        void onEndPrivateCoach(MeetingConfig meetingConfig);

    }

    private OnCoachOperationsListener onCoachOperationsListener;


    public void setOnCoachOperationsListener(OnCoachOperationsListener onCoachOperationsListener) {
        this.onCoachOperationsListener = onCoachOperationsListener;
    }

    public PopPrivateCoachTalkToAllOperations(Context context) {
        this.mContext = context;
        getPopupWindowInstance();
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.pop_private_coach_talk_options, null);
        oneLayout =  view.findViewById(R.id.layout_one);
        twoLayout =  view.findViewById(R.id.layout_two);

        oneLayout.setOnClickListener(this);
        twoLayout.setOnClickListener(this);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dismiss();
            }

        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);
    }


    @SuppressLint("NewApi")
    public void show(View v, MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
        mPopupWindow.showAsDropDown(v);
    }

    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public boolean isShowing() {
        if (mPopupWindow == null) {
            return false;
        }
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
        mPopupWindow = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_one:
                if(onCoachOperationsListener != null){
                    onCoachOperationsListener.onTalkToAllStudent(meetingConfig);
                }
                dismiss();
                break;
            case R.id.layout_two:
                if(onCoachOperationsListener != null){
                    onCoachOperationsListener.onEndPrivateCoach(meetingConfig);
                }
                dismiss();
                break;

            default:
                break;
        }
    }


}
