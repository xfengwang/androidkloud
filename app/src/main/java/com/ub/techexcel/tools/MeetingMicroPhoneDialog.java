package com.ub.techexcel.tools;
import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.kloudsync.techexcel.R;
public class MeetingMicroPhoneDialog implements View.OnClickListener {
    private Activity host;
    private Dialog microPhoneTurnOnDialog;
    private View view;
    private TextView tv_turn_on,tv_cancel;

    public interface OnMicroPhoneTurnOnListener {
        void onTurnOnMicroPhone();
        void onClose();
    }

    private OnMicroPhoneTurnOnListener onMicroPhoneTurnOnListener;

    public void setOnMicroPhoneTurnOnListener(OnMicroPhoneTurnOnListener onMicroPhoneTurnOnListener) {
        this.onMicroPhoneTurnOnListener = onMicroPhoneTurnOnListener;
    }

    public MeetingMicroPhoneDialog(Activity host) {
        this.host = host;
        initPopuptWindow(host);
    }
    public void initPopuptWindow(final Activity host) {
        LayoutInflater layoutInflater = LayoutInflater.from(host);
        view = layoutInflater.inflate(R.layout.dialog_meeting_microphone, null);
        view.setAlpha(0.8f);
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);
        tv_turn_on = view.findViewById(R.id.tv_turn_on);
        tv_turn_on.setOnClickListener(this);
        microPhoneTurnOnDialog = new Dialog(host, R.style.my_dialog);
        microPhoneTurnOnDialog.setContentView(view);
        microPhoneTurnOnDialog.setCancelable(false);
        Window window = microPhoneTurnOnDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = host.getResources().getDimensionPixelSize(R.dimen.microphone_dialog_width);
        microPhoneTurnOnDialog.getWindow().setAttributes(lp);
    }

    public void dismiss() {
        if (microPhoneTurnOnDialog != null) {
            microPhoneTurnOnDialog.dismiss();
            microPhoneTurnOnDialog = null;
        }
    }

    public void show(){
        if (microPhoneTurnOnDialog != null) {
            microPhoneTurnOnDialog.show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_turn_on:
                onMicroPhoneTurnOnListener.onTurnOnMicroPhone();
                dismiss();
                break;
            case R.id.tv_cancel:
                onMicroPhoneTurnOnListener.onClose();
                dismiss();
                break;
        }
    }
}
