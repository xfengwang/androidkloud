package com.ub.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.ui.DocAndMeetingActivity;
import com.ub.techexcel.bean.NotifyBean;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wang on 2017/11/17.
 */

public class AlertDialogActivity extends Activity implements View.OnClickListener {

    private TextView join, cancel;
    private JSONObject jsonObject;
    private int isNewCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alertdialog);

        isNewCourse = getIntent().getIntExtra("isNewCourse", 0);
        join = (TextView) findViewById(R.id.join);
        join.setOnClickListener(this);
        cancel = (TextView) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        try {
            jsonObject = new JSONObject(getIntent().getStringExtra("jsonObject"));
            Log.e("--------kkk", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.join:

                final String meetingId;
                final int meetingType;
                final int meetingRole;
                final int lessonId;
                try {
                    meetingId = jsonObject.getString("meetingID");
                    meetingType = MeetingType.MEETING;
                    meetingRole = jsonObject.getInt("roleType");
                    lessonId = jsonObject.getInt("incidentID");
                    if (lessonId <= 0) {
                        Toast.makeText(AlertDialogActivity.this, "加入的meeting不存在或没有开始", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(AlertDialogActivity.this, DocAndMeetingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("meeting_id", meetingId);
                    intent.putExtra("meeting_type", meetingType);
                    intent.putExtra("lession_id", lessonId);
                    intent.putExtra("meeting_role", meetingRole);
                    intent.putExtra("from_meeting", true);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.cancel:
                try {
                    notifyleave(jsonObject.getString("meetingID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                finish();
                break;
            default:
                break;
        }
    }

    private void notifyleave(String meetingId) {
        boolean isExist = false;
        for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
            if (AppConfig.progressCourse.get(i).getMeetingId().equals(meetingId)) {
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            NotifyBean notifyBean = new NotifyBean();
            notifyBean.setMeetingId(meetingId);
            notifyBean.setStatus(false);
            AppConfig.progressCourse.add(notifyBean);
        }
        Intent intent = new Intent();
        intent.setAction(getResources().getString(R.string.Receive_Course));
        sendBroadcast(intent);
    }
}
