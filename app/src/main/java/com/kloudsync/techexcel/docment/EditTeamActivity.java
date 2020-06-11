package com.kloudsync.techexcel.docment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.TeamMembersAdapterV2;
import com.kloudsync.techexcel.bean.TeamMember;
import com.kloudsync.techexcel.bean.params.EventAccessOption;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.AccessOptionManager;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.SpaceMemberOperationDialog;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.response.TeamMembersResponse;
import com.kloudsync.techexcel.tool.KloudCache;
import com.kloudsync.techexcel.ui.InviteFromCompanyActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditTeamActivity extends AppCompatActivity implements TeamMembersAdapterV2.MoreOptionsClickListener {

    private RelativeLayout backLayout;
    private TextView createbtn, cancel;
    private EditText teamNameEdit;
    private int itemID, iscreate;
    private TextView titleText;
    private String teamName;
    private TeamMembersAdapterV2 madapter;
    private RelativeLayout selectaccess;
    private ImageView accessicon;
    private TextView accessname;
    private RecyclerView memberList;
    private LinearLayout addmember;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editteam);
        itemID = getIntent().getIntExtra("team_id", 0);
        teamName = getIntent().getStringExtra("team_name");
        iscreate = getIntent().getIntExtra("isCreate", 0);
        EventBus.getDefault().register(this);
        initView();
        getTeamItem();
    }

    private TeamSpaceBean currentTeam = new TeamSpaceBean();

    public void getTeamItem() {
        TeamSpaceInterfaceTools.getinstance().getTeamItem(AppConfig.URL_PUBLIC + "TeamSpace/Item?itemID=" + itemID,
                TeamSpaceInterfaceTools.GETTEAMITEM, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        TeamSpaceBean teamSpaceBean = (TeamSpaceBean) object;
                        if (teamSpaceBean.getItemID() == itemID) {
                            currentTeam = teamSpaceBean;
                            if (!TextUtils.isEmpty(currentTeam.getName())) {
                                teamNameEdit.setText(currentTeam.getName());
                                teamNameEdit.setSelection(currentTeam.getName().length());
                            }
                            switch (currentTeam.getAccess()) {
                                case AccessOptionManager.PUBLIC:
                                    receiverEventEndCoach(new EventAccessOption(currentTeam.getAccess(), getResources().getString(R.string.access_public)));
                                    break;
                                case AccessOptionManager.PROTECTED:
                                    receiverEventEndCoach(new EventAccessOption(currentTeam.getAccess(), getResources().getString(R.string.access_protect)));
                                    break;
                                case AccessOptionManager.PRIVATE:
                                    receiverEventEndCoach(new EventAccessOption(currentTeam.getAccess(), getResources().getString(R.string.access_private)));
                                    break;
                            }
                            getTeamMembers();
                        }
                    }
                });
    }


    public void getTeamMembers() {
        ServiceInterfaceTools.getinstance().getTeamMembers(itemID + "").enqueue(new Callback<TeamMembersResponse>() {
            @Override
            public void onResponse(Call<TeamMembersResponse> call, Response<TeamMembersResponse> response) {
                if (response != null && response.isSuccessful()) {
                    List<TeamMember> members = response.body().getRetData();
                    if (members == null) {
                        members = new ArrayList<>();
                    }
                    madapter.setDatas(members);
                }
            }

            @Override
            public void onFailure(Call<TeamMembersResponse> call, Throwable t) {

            }
        });
    }


    private void initView() {
        createbtn = findViewById(R.id.createbtn);
        cancel = findViewById(R.id.cancel);
        backLayout = findViewById(R.id.layout_back);
        teamNameEdit = findViewById(R.id.edit_team_name);
        titleText = findViewById(R.id.tv_title);
        if (iscreate == 1) {
            titleText.setText(getResources().getString(R.string.Create_team));
        } else {
            titleText.setText(getResources().getString(R.string.edit_team));
        }
        createbtn.setOnClickListener(new MyOnClick());
        cancel.setOnClickListener(new MyOnClick());
        backLayout.setOnClickListener(new MyOnClick());
        selectaccess = findViewById(R.id.selectaccess);
        selectaccess.setOnClickListener(new MyOnClick());
        accessicon = findViewById(R.id.accessicon);
        accessname = findViewById(R.id.accessname);
        addmember = findViewById(R.id.addmember);
        addmember.setOnClickListener(new MyOnClick());
        memberList = findViewById(R.id.list_member);
        memberList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        int teamRole = KloudCache.getInstance(this).getTeamRole().getTeamRole();
        madapter = new TeamMembersAdapterV2(this, teamRole);
        madapter.setMoreOptionsClickListener(this);
        memberList.setAdapter(madapter);
    }


    protected class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_back:
                    finish();
                    break;
                case R.id.cancel:
                    finish();
                    break;
                case R.id.createbtn:
                    updateTeam();
                    break;
                case R.id.selectaccess:
                    Intent intent = new Intent(EditTeamActivity.this, ChangeAccessOptionActivity.class);
                    intent.putExtra("Access", currentAccess);
                    startActivity(intent);
                    break;
                case R.id.addmember:
                    Intent intentmember = new Intent(EditTeamActivity.this, InviteFromCompanyActivity.class);
                    intentmember.putExtra("team_id", itemID);
                    startActivityForResult(intentmember, REQUEST_ADD_ADMIN);
                    break;
                default:
                    break;
            }
        }
    }


    private int currentAccess = AccessOptionManager.PRIVATE;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverEventEndCoach(EventAccessOption accessOption) {
        currentAccess = accessOption.getAccess();
        accessname.setText(accessOption.getName());
        if (currentAccess == AccessOptionManager.PUBLIC) {
            accessicon.setImageResource(R.drawable.teampublic1);
        } else if (currentAccess == AccessOptionManager.PROTECTED) {
            accessicon.setImageResource(R.drawable.teamprotect1);
        } else if (currentAccess == AccessOptionManager.PRIVATE) {
            accessicon.setImageResource(R.drawable.teamprivate1);
        }
    }


    private SpaceMemberOperationDialog spaceMemberOperationDialog;

    @Override
    public void moreOptionsClick(final TeamMember member) {
        spaceMemberOperationDialog = new SpaceMemberOperationDialog(this, member);
        spaceMemberOperationDialog.setOptionsLinstener(new SpaceMemberOperationDialog.InviteOptionsLinstener() {
            @Override
            public void setAdmin() {
                String url = AppConfig.URL_PUBLIC + "TeamSpace/ChangeMemberType?ItemID=" + itemID + "&MemberID=" + member.getMemberID() + "&memberType=" + 1; //设置Admin = 1
                ServiceInterfaceTools.getinstance().changeMemberType(url, ServiceInterfaceTools.CHANGEMEMBERTYPE, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        getTeamMembers();
                    }
                });
            }

            @Override
            public void clean() {

            }
        });
        spaceMemberOperationDialog.show();
        spaceMemberOperationDialog.setDeleteHidden();
    }


    private void updateTeam() {
        final String editName = teamNameEdit.getText().toString();
        if (TextUtils.isEmpty(editName)) {
            Toast.makeText(getApplication(), "please edit team name", Toast.LENGTH_SHORT).show();
            return;
        }
        final JSONObject params = getParams(editName);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "TeamSpace/UpdateTeamSpace", params);
                    Log.e("jsonObject", params.toString() + "  " + responsedata.toString());
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        updateSuccess();
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    private JSONObject getParams(String editName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID", itemID);
            jsonObject.put("Name", editName);
            jsonObject.put("Note", editName);
            jsonObject.put("Access", currentAccess);
            jsonObject.put("AccessSame", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    private void updateSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSharedPreferences(AppConfig.LOGININFO,
                        Context.MODE_PRIVATE).edit().putString("teamname", teamNameEdit.getText().toString()).commit();
                Toast.makeText(getApplicationContext(), R.string.operate_success, Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new TeamSpaceBean());
                finish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    private static final int REQUEST_ADD_ADMIN = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_ADMIN) {
                getTeamMembers();
            }
        }
    }

}
