package com.kloudsync.techexcel.docment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.dialog.AccessOptionManager;

public class ChangeAccessOptionActivity extends AppCompatActivity {

    private TextView createbtn;
    private TextView titleText;
    private RelativeLayout backLayout;
    private int currentAccess = AccessOptionManager.PRIVATE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changeaccessoptionactivity);
        currentAccess = getIntent().getIntExtra("Access", 0);
        initView();
    }


    private void initView() {
        createbtn = findViewById(R.id.createbtn);
        titleText = findViewById(R.id.tv_title);
        titleText.setText(getResources().getString(R.string.app_name));
        backLayout =  findViewById(R.id.layout_back);
        createbtn.setOnClickListener(new MyOnClick());
        backLayout.setOnClickListener(new MyOnClick());
        AccessOptionManager.getManager(this).initAccess(this, currentAccess);
    }


    protected class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_back:
                    finish();
                    break;
                case R.id.createbtn:
                    finish();
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
