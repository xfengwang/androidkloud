package com.ub.kloudsync.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventSpaceData;
import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.bean.params.EventAccessOption;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.AccessOptionManager;
import com.kloudsync.techexcel.docment.EditSpaceActivity;
import com.kloudsync.techexcel.response.TeamsResponse;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewSpaceActivityV2 extends Activity implements View.OnClickListener {

    private ImageView back;
    private EditText inputname;
    private TextView createbtn;
    private int teamid = 0;
    private TextView tv_title;
    private CheckBox checkbox;
    private TextView next;

    RelativeLayout backLayout;
    private List<Team> mCurrentTeamData = new ArrayList<>();
    private Team currentTeam=new Team();
    private int currentAccess;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_space);
        teamid = getIntent().getIntExtra("ItemID", 0);
        EventBus.getDefault().register(this);
        initView();
        getTeamList();
    }



    private void initView() {
        backLayout =  findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        inputname =  findViewById(R.id.inputname);
        createbtn =  findViewById(R.id.createbtn);
        checkbox = findViewById(R.id.checkbox);
        next = findViewById(R.id.next);
        checkbox.setChecked(true);
        createbtn.setOnClickListener(this);
        next.setOnClickListener(this);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.Create_Space));
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    AccessOptionManager.getManager(CreateNewSpaceActivityV2.this).sameAsTeam(currentTeam.getAccess());
                }else{
                    AccessOptionManager.getManager(CreateNewSpaceActivityV2.this).unSameAsTeam(currentTeam.getAccess());
                }
            }
        });

    }



    public void getTeamList() {
        ServiceInterfaceTools.getinstance().getAllTeams(AppConfig.SchoolID + "").enqueue(new Callback<TeamsResponse>() {
            @Override
            public void onResponse(Call<TeamsResponse> call, Response<TeamsResponse> response) {
                if (response != null && response.isSuccessful()) {
                    List<Team> list = response.body().getRetData();
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    mCurrentTeamData.clear();
                    mCurrentTeamData.addAll(list);
                    for (int i = 0; i < mCurrentTeamData.size(); i++) {
                        Team team = mCurrentTeamData.get(i);
                        Log.e("ddd",team.getItemID()+"  "+team.getAccess()+" "+teamid);
                        if (team.getItemID() == teamid) {
                            currentTeam=team;
                            currentAccess=currentTeam.getAccess();
                            AccessOptionManager.getManager(CreateNewSpaceActivityV2.this).sameAsTeam(currentAccess);
                            break;
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<TeamsResponse> call, Throwable t) {

            }
        });

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverEventEndCoach(EventAccessOption accessOption) {
        currentAccess=accessOption.getAccess();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.createbtn:
                CreateNew(false);
                break;
            case R.id.next:
                CreateNew(true);

                break;

        }
    }


    private void CreateNew(boolean isNext) {
        if (TextUtils.isEmpty(inputname.getText().toString().trim())) {
            Toast.makeText(this, "please input space name first", Toast.LENGTH_SHORT).show();
            return;
        }
        TeamSpaceInterfaceTools.getinstance().createTeamSpace(AppConfig.URL_PUBLIC + "TeamSpace/CreateTeamSpace", TeamSpaceInterfaceTools.CREATETEAMSPACE,currentAccess,0,
                AppConfig.SchoolID, 2, inputname.getText().toString().trim(), teamid, 0, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        int spaceId= (int) object;
                        EventBus.getDefault().post(new TeamSpaceBean());
                        EventBus.getDefault().post(new EventSpaceData(spaceId));
                        if(isNext){
                            Intent intent2 = new Intent(CreateNewSpaceActivityV2.this, EditSpaceActivity.class);
                            intent2.putExtra("space_id", spaceId);
                            intent2.putExtra("space_name", "");
                            intent2.putExtra("teamId",teamid);
                            intent2.putExtra("isCreate", 1);
                            startActivity(intent2);
                            finish();
                        }else{
                            finish();
                        }

                    }
                }
        );
    }
}
