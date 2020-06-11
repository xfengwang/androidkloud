package com.kloudsync.techexcel.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.InviteFromCompanyAdapter;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.bean.CompanyContact;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.InviteNewContactDialog;
import com.kloudsync.techexcel.response.InviteResponse;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteFromCompanyActivity extends BaseActivity implements View.OnClickListener {

    private TextView titleText;
    private RelativeLayout backLayout;
    private RecyclerView contactList;
    private TextView invatenewcontact;
    private  InviteFromCompanyAdapter adapter;
    private int teamId;
    private EditText searchEdit;
    private ImageView search;
    private  RelativeLayout titleRightLayout;

    @Override
    protected int setLayout() {
        return R.layout.activity_invite_from_company;
    }

    @Override
    protected void initView() {
        teamId = getIntent().getIntExtra("team_id", 0);
        titleText = findViewById(R.id.tv_title);
        titleText.setText(getString(R.string.invite_form_company));
        contactList = findViewById(R.id.list_contact);
        invatenewcontact = findViewById(R.id.invatenewcontact);
        invatenewcontact.setOnClickListener(this);
        noDataLayout = findViewById(R.id.no_data_lay);
        messageText = findViewById(R.id.txt_msg);
        titleRightLayout = findViewById(R.id.layout_title_right);
        titleRightLayout.setVisibility(View.VISIBLE);
        titleRightLayout.setOnClickListener(this);
        contactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new InviteFromCompanyAdapter(this);
        View headerView = getLayoutInflater().inflate(R.layout.company_contact_search_header, contactList, false);
        loadingBar = headerView.findViewById(R.id.loading_progress);
        searchEdit = headerView.findViewById(R.id.edit_input);
        search = headerView.findViewById(R.id.search);
        searchEdit.setHint(getString(R.string.inputphoneorusername));
        adapter.setHeaderView(headerView);
        backLayout = findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        contactList.setAdapter(adapter);
        search.setOnClickListener(this);
        getCompanyContacts();
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s)){
                    getCompanyContacts();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private InviteNewContactDialog inviteNewContactDialog;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.search:
                search(searchEdit.getText().toString());
                break;
            case R.id.layout_title_right:
                if (adapter != null) {
                    List<CompanyContact> contacts = adapter.getSelectedContacts();
                    if (contacts.size() == 0) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.select_contact), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    requestAddAdmin(contacts);
                }
                break;
            case R.id.invatenewcontact:
                if (inviteNewContactDialog != null) {
                    if (inviteNewContactDialog.isShowing()) {
                        inviteNewContactDialog.cancel();
                        inviteNewContactDialog = null;
                    }
                }
                inviteNewContactDialog = new InviteNewContactDialog(this);
                inviteNewContactDialog.setOptionsLinstener(new InviteNewContactDialog.InviteOptionsLinstener() {
                    @Override
                    public void inviteFromPhone(String phone) {
                        inviteNewContact(phone);
                    }
                });
                inviteNewContactDialog.show();
                break;
        }
    }




    private void inviteNewContact(String mobile){

        ServiceInterfaceTools.getinstance().inviteNewToCompany(mobile, 3, teamId,  0).enqueue(new Callback<InviteResponse>() {
            @Override
            public void onResponse(Call<InviteResponse> call, Response<InviteResponse> response) {
                if (response != null && response.isSuccessful()) {
                    if (response.body().getRetCode() == AppConfig.RETCODE_SUCCESS) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.invitate_succeeded), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String msg = getString(R.string.operate_failure);
                        if (!TextUtils.isEmpty(response.body().getErrorMessage())) {
                            msg = response.body().getErrorMessage();
                        }
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<InviteResponse> call, Throwable t) {
            }
        });
    }

    private void requestAddAdmin(List<CompanyContact> contacts) {

        ServiceInterfaceTools.getinstance().inviteCompanyMemberAsTeamAdmin(teamId + "", contacts).enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {
                if (response != null && response.isSuccessful()) {
                    if (response.body().getRetCode() == 0) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "add failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCompanyContacts() {
        String url = AppConfig.URL_MEETING_BASE + "company_contact/list?companyId="+AppConfig.SchoolID+"&pageIndex=1&pageSize=100";
        ServiceInterfaceTools.getinstance().getCompanyContact(url, ServiceInterfaceTools.GETCOMPANYCONTACT, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                List<CompanyContact> contacts = new ArrayList<>();
                contacts.addAll((List<CompanyContact>) object);
                if (contacts.size() == 0) {
                    contacts = new ArrayList<>();
                }
                adapter.setKeyword("");
                adapter.setDatas(contacts);
            }
        });
    }

    private void search(final String searchStr) {
        if(TextUtils.isEmpty(searchStr)){
            getCompanyContacts();
        }else{
            String ff=LoginGet.getBase64Password(searchStr);
            String url = AppConfig.URL_MEETING_BASE + "company_member/search_member?companyId="+AppConfig.SchoolID+"&pageIndex=1&pageSize=100&searchText="+ff;
            ServiceInterfaceTools.getinstance().getSearchCompanyContact(url, ServiceInterfaceTools.GETCOMPANYCONTACT, new ServiceInterfaceListener() {
                @Override
                public void getServiceReturnData(Object object) {
                    List<CompanyContact> contacts = new ArrayList<>();
                    contacts.addAll((List<CompanyContact>) object);
                    if (contacts.size() == 0) {
                        contacts = new ArrayList<>();
                    }
                    showContacts(contacts,searchStr);
                }
            });
        }

    }



    private RelativeLayout noDataLayout;
    private ProgressBar loadingBar;
    private TextView messageText;


    public void showEmpty(String message) {
        noDataLayout.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.INVISIBLE);
        adapter.setDatas(new ArrayList<CompanyContact>());
        messageText.setText(message);
    }

    public void showContacts(List<CompanyContact> contacts, String keyword) {
        loadingBar.setVisibility(View.INVISIBLE);
        noDataLayout.setVisibility(View.INVISIBLE);
        contactList.setVisibility(View.VISIBLE);
        adapter.setKeyword(keyword);
        adapter.setDatas(contacts);
    }


}
