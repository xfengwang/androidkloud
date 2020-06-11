package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.TeamMembersAdapterV2;
import com.kloudsync.techexcel.bean.RoleInTeam;
import com.kloudsync.techexcel.bean.TeamMember;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.TeamMemberOperationsDialog;
import com.kloudsync.techexcel.docment.EditTeamActivity;
import com.kloudsync.techexcel.docment.InviteNewActivity;
import com.kloudsync.techexcel.docment.RenameActivity;
import com.kloudsync.techexcel.help.AddSpaceMemberDialog;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.InviteNewDialog;
import com.kloudsync.techexcel.help.SpaceMemberOperationDialog;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.response.TeamMembersResponse;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.KloudCache;
import com.kloudsync.techexcel.tool.NetWorkHelp;
import com.kloudsync.techexcel.ui.InviteFromCompanyActivity;
import com.kloudsync.techexcel.ui.InviteFromPhoneActivity;
import com.kloudsync.techexcel.ui.InviteFromSpaceActivity;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamPropertyActivity extends Activity implements View.OnClickListener, InviteNewDialog.InviteOptionsLinstener, TeamMembersAdapterV2.MoreOptionsClickListener {

    private RecyclerView memberList;
    private List<TeamUser> mTeamUserData = new ArrayList<>();
    private int itemID;
    private TextView teamspacename;
    private TextView tv_invite;
    private ImageView moreOpation;
    private RelativeLayout teamrl;
    private TextView titleText;
    RelativeLayout backLayout;
    private TeamMembersAdapterV2 madapter;
    private InviteNewDialog inviteNewDialog;
    private SharedPreferences sharedPreferences;
    int myTeamRole;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case AppConfig.DELETESUCCESS:
                    EventBus.getDefault().post(new TeamSpaceBean());
                    finish();
                    break;
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(),
                            result,
                            Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.NO_NETWORK:
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.No_networking),
                            Toast.LENGTH_SHORT).show();

                    break;
                case AppConfig.NETERROR:
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.NETWORK_ERROR),
                            Toast.LENGTH_SHORT).show();

                    break;

                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_members);
        itemID = getIntent().getIntExtra("ItemID", 0);
        myTeamRole = KloudCache.getInstance(this).getTeamRole().getTeamRole();
        sharedPreferences =getSharedPreferences(AppConfig.LOGININFO, Context.MODE_PRIVATE);
        initView();
        getTeamMembers();

    }

    private void initView() {
        titleText = findViewById(R.id.tv_title);
        titleText.setText(R.string.team_memers);
        backLayout = findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        memberList = (RecyclerView) findViewById(R.id.list_member);
        int role = KloudCache.getInstance(this).getUserRole();
        int teamRole = KloudCache.getInstance(this).getTeamRole().getTeamRole();

        memberList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        madapter = new TeamMembersAdapterV2(this, teamRole);
        madapter.setMoreOptionsClickListener(this);
        if (role == 7 || role == 8 || teamRole == RoleInTeam.ROLE_OWENER || teamRole == RoleInTeam.ROLE_ADMIN) {
            View view = getLayoutInflater().inflate(R.layout.add_header, memberList, false);
            LinearLayout headerLayout = view.findViewById(R.id.layout_header);
            headerLayout.setOnClickListener(this);
            madapter.setHeaderView(view);
        }
        memberList.setAdapter(madapter);
        teamspacename = (TextView) findViewById(R.id.teamspacename);
        tv_invite = (TextView) findViewById(R.id.tv_invite);
        moreOpation = (ImageView) findViewById(R.id.moreOpation);
        teamrl = (RelativeLayout) findViewById(R.id.teamrl);
//        moreOpation.setOnClickListener(this);
        teamrl.setOnClickListener(this);
        tv_invite.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.tv_invite:
                GoToInvite();
                break;
            case R.id.teamrl:
//                GoToRename();
                GoTOET();
                break;
            case R.id.moreOpation:
//                MoreForTeam();
                break;
            case R.id.layout_header:
//                if (inviteNewDialog == null) {
//                    inviteNewDialog = new InviteNewDialog(this);
//                    inviteNewDialog.setTitle("Add team admin");
//                }
//                inviteNewDialog.setOptionsLinstener(this);
//                inviteNewDialog.show();


                addSpaceMemberDialog = new AddSpaceMemberDialog(this);
                addSpaceMemberDialog.setOptionsLinstener(new AddSpaceMemberDialog.InviteOptionsLinstener() {
                    @Override
                    public void fromCompany() {
                        Intent intent = new Intent(TeamPropertyActivity.this, InviteFromCompanyActivity.class);
                        intent.putExtra("team_id", itemID);
                        startActivityForResult(intent, REQUEST_ADD_ADMIN);
                    }

                    @Override
                    public void formInvite() {
                        Intent intent = new Intent(TeamPropertyActivity.this, InviteFromPhoneActivity.class);
                        intent.putExtra("invite_type", 3);
                        intent.putExtra("team_id", itemID);
                        startActivity(intent);
                    }
                });
                addSpaceMemberDialog.show(0);


                break;
        }
    }

    private AddSpaceMemberDialog addSpaceMemberDialog;

    private void GoTOET() {
        Intent intent = new Intent(TeamPropertyActivity.this, EditTeamActivity.class);
        intent.putExtra("itemID", itemID);
        startActivity(intent);
    }

    private void MoreForTeam() {
        TeamMorePopup teamMorePopup = new TeamMorePopup();
        teamMorePopup.setIsTeam(true);
        teamMorePopup.setTSid(itemID);
        teamMorePopup.getPopwindow(this,sharedPreferences.getInt("Role",0));
        teamMorePopup.setFavoritePoPListener(new TeamMorePopup.FavoritePoPListener() {
            @Override
            public void dismiss() {
                getWindow().getDecorView().setAlpha(1.0f);
            }

            @Override
            public void open() {
                getWindow().getDecorView().setAlpha(0.5f);
            }

            @Override
            public void delete() {

                LoginGet lg = new LoginGet();
                lg.setBeforeDeleteTeamListener(new LoginGet.BeforeDeleteTeamListener() {
                    @Override
                    public void getBDT(int retdata) {
                        if (retdata > 0) {
                            Toast.makeText(getApplicationContext(), "Please delete space first", Toast.LENGTH_LONG).show();
                        } else {
                            DeleteTeam();
                        }
                    }
                });
                lg.GetBeforeDeleteTeam(TeamPropertyActivity.this, itemID + "");
            }

            @Override
            public void rename() {
                GoToRename();
            }

            @Override
            public void quit() {
                finish();
            }

            @Override
            public void edit() {

            }
            @Override
            public void createNewSpace() {

            }

            @Override
            public void switchSpace() {

            }
        });

        teamMorePopup.StartPop(moreOpation);
    }

    private void GoToRename() {
        Intent intent = new Intent(TeamPropertyActivity.this, RenameActivity.class);
        intent.putExtra("itemID", itemID);
        intent.putExtra("isteam", true);
        startActivity(intent);
    }

    private void GoToInvite() {
        Intent intent = new Intent(this, InviteNewActivity.class);
        intent.putExtra("itemID", itemID);
        intent.putExtra("flag_c2", true);
        startActivity(intent);
    }

    private void DeleteTeam() {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    JSONObject responsedata = ConnectService.getIncidentDataattachment(
                            AppConfig.URL_PUBLIC +
                                    "TeamSpace/DeleteTeam?teamID=" +
                                    itemID);
                    Log.e("DeleteTeam", responsedata.toString());
                    int retcode = (Integer) responsedata.get("RetCode");
                    msg = new Message();
                    if (0 == retcode) {
                        msg.what = AppConfig.DELETESUCCESS;
                        String result = responsedata.toString();
                        msg.obj = result;
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("errorMessage");
                        msg.obj = ErrorMessage;
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    msg.what = AppConfig.NETERROR;
                } finally {
                    if (!NetWorkHelp.checkNetWorkStatus(getApplicationContext())) {
                        msg.what = AppConfig.NO_NETWORK;
                    }
                    handler.sendMessage(msg);
                }
            }
        }).start(ThreadManager.getManager());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private static final int REQUEST_ADD_ADMIN = 1;

    @Override
    public void inviteFromContactOption() {
        Intent intent = new Intent(this, InviteFromCompanyActivity.class);
        intent.putExtra("team_id", itemID);
        startActivityForResult(intent, REQUEST_ADD_ADMIN);

    }


    @Override
    public void inviteNewOption() {
        Intent intent = new Intent(this, InviteFromPhoneActivity.class);
        intent.putExtra("invite_type", 3);
        intent.putExtra("team_id", itemID);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_ADMIN) {
                getTeamMembers();
            }
        }
    }

    private TeamMemberOperationsDialog operationsDialog;

//    @Override
//    public void moreOptionsClick(TeamMember member) {
//        operationsDialog = new TeamMemberOperationsDialog(this, member, myTeamRole);
//        operationsDialog.show();
//    }

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
}
