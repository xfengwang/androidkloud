package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.params.EventAccessOption;
import com.kloudsync.techexcel.bean.params.EventEndCoach;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.AccessOptionManager;
import com.kloudsync.techexcel.dialog.PrivateCoachManager;
import com.kloudsync.techexcel.docment.EditTeamActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

public class CreateNewTeamActivityV2 extends Activity implements View.OnClickListener {

    private RelativeLayout backLayout;
    private EditText inputname;
    private TextView createbtn;
    private TextView tv_title;
    private LinearLayout accessall;
    private RelativeLayout selectaccess;
    private ImageView accessicon;
    private TextView accessname;
    private TextView next;
    private int currentAccess=AccessOptionManager.PRIVATE;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.CreateTeamTopic:
                    String result = (String) msg.obj;
                    finish();
                    break;
                case AppConfig.FAILED:
                    result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), result,
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_team);
//        isSync = getIntent().getBooleanExtra("isSync", false);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        backLayout =findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        inputname =  findViewById(R.id.inputname);
        createbtn = findViewById(R.id.createbtn);
        createbtn.setOnClickListener(this);
        tv_title =  findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.Create_team));
        accessall = findViewById(R.id.accessall);
        selectaccess = findViewById(R.id.selectaccess);
        next = findViewById(R.id.next);
        selectaccess.setOnClickListener(this);
        next.setOnClickListener(this);
        accessicon=findViewById(R.id.accessicon);
        accessname=findViewById(R.id.accessname);
        AccessOptionManager.getManager(this).initAccess(this,currentAccess);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.createbtn:
                createNew(false);
                break;
            case R.id.next:
                createNew(true);
                break;
            case R.id.selectaccess:
                AccessOptionManager.getManager(this).initAccess(this,currentAccess);
                break;
        }
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverEventEndCoach(EventAccessOption accessOption) {
        currentAccess=accessOption.getAccess();
        accessname.setText(accessOption.getName());
        if(currentAccess==AccessOptionManager.PUBLIC){
            accessicon.setImageResource(R.drawable.teampublic1);
        }else if(currentAccess==AccessOptionManager.PROTECTED){
            accessicon.setImageResource(R.drawable.teamprotect1);
        }else if(currentAccess==AccessOptionManager.PRIVATE){
            accessicon.setImageResource(R.drawable.teamprivate1);
        }
    }



    private void createNew(boolean isNext) {
        if (TextUtils.isEmpty(inputname.getText().toString().trim())) {
            Toast.makeText(this, "please input team name first", Toast.LENGTH_SHORT).show();
            return;
        }
        TeamSpaceInterfaceTools.getinstance().createTeamSpace(AppConfig.URL_PUBLIC + "TeamSpace/CreateTeamSpace", TeamSpaceInterfaceTools.CREATETEAMSPACE,
                currentAccess,0,
                AppConfig.SchoolID, 1, inputname.getText().toString().trim(),
                0, 0, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        int teamId= (int) object;


                        getSharedPreferences(AppConfig.LOGININFO,
                                Context.MODE_PRIVATE).edit().putString("teamname", inputname.getText().toString()).commit();
                        getSharedPreferences(AppConfig.LOGININFO,
                                Context.MODE_PRIVATE).edit().putInt("teamid", teamId).commit();

                        EventBus.getDefault().post(new TeamSpaceBean());

                        if(isNext){
                            Intent intent=new Intent(CreateNewTeamActivityV2.this, EditTeamActivity.class);
                            intent.putExtra("team_id", teamId);
                            intent.putExtra("team_name", "");
                            intent.putExtra("isCreate", 1);
                            startActivity(intent);
                            finish();
                        }else{
                            finish();
                        }
                    }
                }
        );
    }



    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
