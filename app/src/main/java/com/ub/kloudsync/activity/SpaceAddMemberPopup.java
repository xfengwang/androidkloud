package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.tool.NetWorkHelp;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

/**
 * Created by wang on 2017/9/18.
 */

public class SpaceAddMemberPopup implements View.OnClickListener {
    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    private LinearLayout lin_admin;
    private LinearLayout lin_member;
    private TextView img_close;
    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {
        void addadmin();

        void addmember();
    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }



    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setGravity(Gravity.BOTTOM);
        mPopupWindow.getWindow().setWindowAnimations(R.style.PopupAnimation5);
        WindowManager.LayoutParams lp = mPopupWindow.getWindow().getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels;
        mPopupWindow.getWindow().setAttributes(lp);
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
        view = layoutInflater.inflate(R.layout.space_add_member_popup, null);
        lin_admin = (LinearLayout) view.findViewById(R.id.lin_admin);
        lin_member = (LinearLayout) view.findViewById(R.id.add_member);
        img_close = (TextView) view.findViewById(R.id.cancel);
        lin_admin.setOnClickListener(this);
        lin_member.setOnClickListener(this);
        img_close.setOnClickListener(this);
        mPopupWindow = new Dialog(mContext, R.style.bottom_dialog);

    }

    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {
            mPopupWindow.show();
        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lin_admin:
                dismiss();
                mFavoritePoPListener.addadmin();
                break;
            case R.id.add_member:
                dismiss();
                mFavoritePoPListener.addmember();
                break;
            case R.id.cancel:
                dismiss();
                break;
            default:
                break;
        }
    }


}
