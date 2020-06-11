package com.kloudsync.techexcel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventSelfJoinCoachAudience;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class BeInvitedCoachAudienceSyncDialog implements OnClickListener {

    public Context mContext;


    public BeInvitedCoachAudienceSyncDialog(Context context) {
        this.mContext = context;
        getPopupWindowInstance();
        mPopupWindow.getWindow().setWindowAnimations(R.style.PopupAnimation3);
    }

    public Dialog mPopupWindow;

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    private TextView cancel, ok;
    private View popupWindow;

    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        popupWindow = layoutInflater.inflate(R.layout.dialog_be_invited_coach_sync_audience, null);
        cancel = (TextView) popupWindow.findViewById(R.id.cancel);
        ok = (TextView) popupWindow.findViewById(R.id.ok);
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(popupWindow);
        WindowManager.LayoutParams lp = mPopupWindow.getWindow().getAttributes();
        lp.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.8f);
        mPopupWindow.getWindow().setAttributes(lp);
        mPopupWindow.setCancelable(false);
        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);

    }


    public  interface  CoachInviteSyncing{
        void  accept();
        void  cancel();
    }

    public  CoachInviteSyncing coachInviteSyncing;

    public void setCoachInviteSyncing(CoachInviteSyncing coachInviteSyncing){
        this.coachInviteSyncing=coachInviteSyncing;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                coachInviteSyncing.cancel();
                mPopupWindow.dismiss();
                break;
            case R.id.ok:
                coachInviteSyncing.accept();
                mPopupWindow.dismiss();
                break;
            default:
                break;
        }
    }


    public void show() {
        if (mPopupWindow != null) {
            mPopupWindow.show();
        }
    }

    public void cancel() {
        if (mPopupWindow != null) {
            mPopupWindow.cancel();
            mPopupWindow = null;
        }
    }


}
