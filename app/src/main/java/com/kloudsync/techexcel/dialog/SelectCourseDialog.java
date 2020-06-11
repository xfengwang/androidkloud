package com.kloudsync.techexcel.dialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import com.kloudsync.techexcel.R;
public class SelectCourseDialog implements OnClickListener {
    private Context mContext;
    private View popupWindow;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                mPopupWindow.dismiss();
                break;
            case R.id.tv_start:

                break;
            default:
                break;
        }
    }

    public boolean isShowing(){
        if(mPopupWindow != null){
            return mPopupWindow.isShowing();
        }
        return false;
    }

    public void hide(){
        if(mPopupWindow != null){
            mPopupWindow.hide();
        }
    }

    public Dialog mPopupWindow;
    public SelectCourseDialog(Context context) {
        this.mContext = context;
        initPopuptWindow();
    }
    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        popupWindow = layoutInflater.inflate(R.layout.dialog_select_course, null);
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(popupWindow);
        WindowManager.LayoutParams lp = mPopupWindow.getWindow().getAttributes();
        lp.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.8f);
        mPopupWindow.getWindow().setAttributes(lp);
    }

    public void show() {
        if (mPopupWindow != null) {
            mPopupWindow.show();
        }
    }

}
