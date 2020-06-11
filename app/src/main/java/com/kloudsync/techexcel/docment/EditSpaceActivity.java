package com.kloudsync.techexcel.docment;

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
import com.kloudsync.techexcel.adapter.SpaceMembersAdapter;
import com.kloudsync.techexcel.adapter.TeamMembersAdapterV2;
import com.kloudsync.techexcel.bean.EventSpaceData;
import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.bean.TeamMember;
import com.kloudsync.techexcel.bean.params.EventAccessOption;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.AccessOptionManager;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.PopDeleteDocument;
import com.kloudsync.techexcel.help.SpaceMemberOperationDialog;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.response.TeamMembersResponse;
import com.kloudsync.techexcel.response.TeamsResponse;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.KloudCache;
import com.kloudsync.techexcel.ui.InviteFromSpaceActivity;
import com.ub.kloudsync.activity.SpaceAddMemberPopup;
import com.ub.kloudsync.activity.SpacePropertyActivity;
import com.ub.kloudsync.activity.TeamMorePopup;
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

public class EditSpaceActivity extends AppCompatActivity implements SpaceMembersAdapter.OnItemClickListener, SpaceMembersAdapter.MoreOptionsClickListener {

    private RelativeLayout backLayout;
    private TextView createbtn;
    private EditText spaceNameEdit;
    private int spaceId;
    private int teamId;
    private TextView titleText;
    private String spaceName;
    private RelativeLayout selectaccess;
    private ImageView accessicon;
    private TextView accessname;
    private TextView isameid;
    private RecyclerView memberList;
    private LinearLayout addmember;
    private SpaceMembersAdapter madapter;
    private int isCreate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editspace);
        EventBus.getDefault().register(this);
        spaceId = getIntent().getIntExtra("space_id", 0);
        spaceName = getIntent().getStringExtra("space_name");
        teamId = getIntent().getIntExtra("teamId", 0);
        isCreate=getIntent().getIntExtra("isCreate",0);
        initView();
        getSpaceDetail();

    }

    public void getSpaceDetail() {
        TeamSpaceInterfaceTools.getinstance().getTeamItem(AppConfig.URL_PUBLIC + "TeamSpace/Item?itemID=" + spaceId, TeamSpaceInterfaceTools.GETTEAMITEM, new TeamSpaceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                TeamSpaceBean teamSpaceBean = (TeamSpaceBean) object;
                currentAccess = teamSpaceBean.getAccess();
                if(!TextUtils.isEmpty(teamSpaceBean.getName())){
                    spaceNameEdit.setText(teamSpaceBean.getName());
                    spaceNameEdit.setSelection(teamSpaceBean.getName().length());
                }
                switch (currentAccess) {
                    case AccessOptionManager.PUBLIC:
                        receiverEventEndCoach(new EventAccessOption(currentAccess, getResources().getString(R.string.access_public)));
                        break;
                    case AccessOptionManager.PROTECTED:
                        receiverEventEndCoach(new EventAccessOption(currentAccess, getResources().getString(R.string.access_protect)));
                        break;
                    case AccessOptionManager.PRIVATE:
                        receiverEventEndCoach(new EventAccessOption(currentAccess, getResources().getString(R.string.access_private)));
                        break;
                }
                getTeamList();
            }
        });
    }


    private int currentTeamAccess;

    private List<Team> mCurrentTeamData = new ArrayList<>();

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
                        Log.e("ddd", team.getItemID() + "  " + team.getAccess() + " " + teamId);
                        if (team.getItemID() == teamId) {
                            currentTeamAccess = team.getAccess();
                            if (currentAccess == currentTeamAccess) {
                                isameid.setVisibility(View.VISIBLE);
                            } else {
                                isameid.setVisibility(View.GONE);
                            }
                            getSpaceMemebers(spaceId);
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

    private void getSpaceMemebers(int spaceId) {
        ServiceInterfaceTools.getinstance().getSpaceMembers(spaceId + "").enqueue(new Callback<TeamMembersResponse>() {
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
        backLayout = findViewById(R.id.layout_back);
        spaceNameEdit = findViewById(R.id.edit_team_name);
        titleText = findViewById(R.id.tv_title);
        selectaccess = findViewById(R.id.selectaccess);
        isameid = findViewById(R.id.isameid);
        if(isCreate==1){
            titleText.setText(getResources().getString(R.string.create_new_space));
        }else{
            titleText.setText(getResources().getString(R.string.edit_space));
        }

        if (!TextUtils.isEmpty(spaceName)) {
            spaceNameEdit.setText(spaceName);
        }
        createbtn.setOnClickListener(new MyOnClick());
        backLayout.setOnClickListener(new MyOnClick());
        selectaccess.setOnClickListener(new MyOnClick());
        accessicon = findViewById(R.id.accessicon);
        accessname = findViewById(R.id.accessname);

        addmember = findViewById(R.id.addmember);
        addmember.setOnClickListener(new MyOnClick());
        memberList = (RecyclerView) findViewById(R.id.list_member);
        memberList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        madapter = new SpaceMembersAdapter(this, getRoleInSchool());
        madapter.setOnItemClickListener(this);
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
                case R.id.createbtn:
                    updateSpace();
                    break;
                case R.id.selectaccess:
                    Intent intent = new Intent(EditSpaceActivity.this, ChangeAccessOptionActivity.class);
                    intent.putExtra("Access", currentAccess);
                    startActivity(intent);
                    break;
                case R.id.addmember:  // add space member
                    showAddSpaceMember();
                    break;
                default:
                    break;
            }
        }
    }

    private void showAddSpaceMember(){
        SpaceAddMemberPopup spaceAddMemberPopup = new SpaceAddMemberPopup();
        spaceAddMemberPopup.getPopwindow(this);
        spaceAddMemberPopup.setFavoritePoPListener(new SpaceAddMemberPopup.FavoritePoPListener() {


            @Override
            public void addadmin() {
                Intent intent = new Intent(EditSpaceActivity.this, InviteFromSpaceActivity.class);
                intent.putExtra("team_id", spaceId);
                intent.putExtra("isAddAdmin", true);
                startActivityForResult(intent, REQUEST_ADD_ADMIN);

            }

            @Override
            public void addmember() {
                Intent intent = new Intent(EditSpaceActivity.this, InviteFromSpaceActivity.class);
                intent.putExtra("team_id", spaceId);
                intent.putExtra("isAddAdmin", false);
                startActivityForResult(intent, REQUEST_ADD_ADMIN);

            }
        });
        spaceAddMemberPopup.StartPop(addmember);
    }

    private int getRoleInSchool() {
        return KloudCache.getInstance(this).getTeamRole().getTeamRole();
    }


    @Override
    public void onItemClick(int position, Object data) {

    }


    private SpaceMemberOperationDialog spaceMemberOperationDialog;

    @Override
    public void moreOptionsClick(final TeamMember member) {

        spaceMemberOperationDialog = new SpaceMemberOperationDialog(this, member);
        spaceMemberOperationDialog.setOptionsLinstener(new SpaceMemberOperationDialog.InviteOptionsLinstener() {
            @Override
            public void setAdmin() {
                String url = AppConfig.URL_PUBLIC + "TeamSpace/ChangeMemberType?ItemID=" + spaceId + "&MemberID=" + member.getMemberID() + "&memberType=" + 1; //设置Admin = 1
                ServiceInterfaceTools.getinstance().changeMemberType(url, ServiceInterfaceTools.CHANGEMEMBERTYPE, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        getSpaceMemebers(spaceId);
                    }
                });
            }

            @Override
            public void clean() {
                String url = AppConfig.URL_PUBLIC + "TeamSpace/RemoveMember?ItemID=" + spaceId + "&MemberID=" + member.getMemberID();
                ServiceInterfaceTools.getinstance().removeMember(url, ServiceInterfaceTools.REMOVEMEMBER, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        getSpaceMemebers(spaceId);
                    }
                });

            }
        });
        spaceMemberOperationDialog.show();

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
        if (currentAccess == currentTeamAccess) {
            isameid.setVisibility(View.VISIBLE);
        } else {
            isameid.setVisibility(View.GONE);
        }
    }


    private void updateSpace() {
        final String editName = spaceNameEdit.getText().toString();
        if (TextUtils.isEmpty(editName)) {
            Toast.makeText(getApplication(), "please edit space name", Toast.LENGTH_SHORT).show();
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
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    private JSONObject getParams(String editName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID", spaceId);
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
                EventBus.getDefault().post(new TeamSpaceBean());
                EventBus.getDefault().post(new EventSpaceData(spaceId));
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
                getSpaceMemebers(spaceId);
            }
        }
    }
}
