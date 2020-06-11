package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.contact.MyFriendsActivity;
import com.kloudsync.techexcel.docment.WeiXinApi;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.ub.kloudsync.activity.Document;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.tools.Tools;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MeetingMemberShare {

    public Context mContext;
    private LinearLayout lin_copy;
    private LinearLayout lin_wechat;
    private LinearLayout lin_email;
    private LinearLayout lin_contact;
    private List<LinearLayout> lin_all = new ArrayList<>();
    private TextView closeImage;
    boolean record;
    private static PopShareKloudSyncDismissListener popShareKloudSyncDismissListener;
    public Dialog mPopupWindow;
    private String url="";
    private ClipboardManager mClipboard = null;

    public interface PopShareKloudSyncDismissListener {
        void CopyLink();

        void Wechat();

        void Contact();

        void Email();
    }

    public void setPoPDismissListener(PopShareKloudSyncDismissListener popShareKloudSyncDismissListener) {
        this.popShareKloudSyncDismissListener = popShareKloudSyncDismissListener;
    }

    public void getPopwindow(Context context) {
        this.mContext = context;
        mClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
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
        View popupWindow = layoutInflater.inflate(R.layout.meeting_member_share, null);
        lin_copy = popupWindow.findViewById(R.id.lin_copy);
        lin_wechat = popupWindow.findViewById(R.id.lin_wechat);
        lin_email = popupWindow.findViewById(R.id.lin_email);
        lin_contact = popupWindow.findViewById(R.id.lin_contact);
        closeImage = popupWindow.findViewById(R.id.cancel);
        lin_all.add(lin_copy);
        lin_all.add(lin_wechat);
        lin_all.add(lin_email);
        lin_all.add(lin_contact);
        lin_copy.setOnClickListener(new MyOnClick());
        lin_wechat.setOnClickListener(new MyOnClick());
        lin_email.setOnClickListener(new MyOnClick());
        lin_contact.setOnClickListener(new MyOnClick());
        closeImage.setOnClickListener(new MyOnClick());
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(popupWindow);
        mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }



    public void startPop(MeetingConfig meetingConfig) {
        if (mPopupWindow != null) {
            WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
            if (Tools.isOrientationPortrait((Activity) mContext)) {
                View root = ((Activity) mContext).getWindow().getDecorView();
                params.width = root.getMeasuredWidth() * 9 / 10;
            } else {
                params.width = mContext.getResources().getDisplayMetrics().widthPixels * 3 / 5;
                View root = ((Activity) mContext).getWindow().getDecorView();
                params.height = root.getMeasuredHeight() * 3 / 5;
            }
            mPopupWindow.getWindow().setAttributes(params);
            mPopupWindow.show();
            url="https://kloud.cn/join?id="+meetingConfig.getMeetingId();
        }
    }


    private class MyOnClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lin_copy:
                    if (popShareKloudSyncDismissListener != null) {
                        popShareKloudSyncDismissListener.CopyLink();
                    }
                    copyLink();
                    break;
                case R.id.lin_wechat:
                    if (popShareKloudSyncDismissListener != null) {
                        popShareKloudSyncDismissListener.Wechat();
                    }
                    weiXinShare(url);
                    break;
                case R.id.lin_email:
                    sendEmail();
                    break;
                case R.id.lin_contact:
                    goToShare();
                    break;
                case R.id.cancel:
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                    break;
            }
            mPopupWindow.dismiss();
        }
    }

    private void copyLink() {
        ClipData  myClip = ClipData.newPlainText("text", url);
        mClipboard.setPrimaryClip(myClip);
        Toast.makeText(mContext, "Link Copied:"+url, Toast.LENGTH_SHORT).show();
    }

    private void sendEmail(){
        String[] targetemail = {"214176156@qq.com"};
        String[] email = {"1599528112@qq.com"};
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822"); // 设置邮件格式
        intent.putExtra(Intent.EXTRA_EMAIL, targetemail);
        intent.putExtra(Intent.EXTRA_CC, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, "这是邮件的主题部分");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        mContext.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
    }


    private void goToShare() {
        Intent i = new Intent(mContext, MyFriendsActivity.class);
        i.putExtra("isShare", true);
        mContext.startActivity(i);
    }

    private void weiXinShare(String url) {
        if (isWXAppInstalledAndSupported(WeiXinApi.getInstance().GetApi())) {
            WXTextObject textObject=new WXTextObject();
            String content=AppConfig.UserName+"邀请参加会议\n会议主题:kloud\n加入会议:\n"+url+"\n复制整段描述后打开手机端Kloud,可直接入会";
            textObject.text=content;
            WXMediaMessage msg=new WXMediaMessage();
            msg.mediaObject=textObject;
            msg.description=mContext.getString(R.string.join_meeting)+":"+url;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.message = msg;
                    req.transaction = buildTransction("text"); //创建唯一的标识
                    req.scene = SendMessageToWX.Req.WXSceneSession; // 设置场景（好友==>朋友圈）
                    WeiXinApi.getInstance().GetApi().sendReq(req);
                }
            }).start();
        } else {
            Toast.makeText(mContext, "微信客户端未安装，请确认",
                    Toast.LENGTH_LONG).show();
        }
    }




    private boolean isWXAppInstalledAndSupported(IWXAPI api) {
        return api.isWXAppInstalled() && api.isWXAppSupportAPI();
    }

    private String buildTransction(String str) {
        return (str == null) ? String.valueOf(System.currentTimeMillis()) : str + System.currentTimeMillis();
    }

    public boolean isShow() {
        return mPopupWindow.isShowing();
    }





}
