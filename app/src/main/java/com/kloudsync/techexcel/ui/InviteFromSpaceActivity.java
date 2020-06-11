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
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import java.util.ArrayList;
import java.util.List;

public class InviteFromSpaceActivity extends BaseActivity implements View.OnClickListener {

    private TextView titleText;
    private RelativeLayout backLayout;
    private RecyclerView contactList;
    private InviteFromCompanyAdapter adapter;
    private int spaceId;
    private EditText searchEdit;
    private ImageView search;
    private  RelativeLayout titleRightLayout;
    private boolean isAddAdmin = true;

    @Override
    protected int setLayout() {
        return R.layout.activity_invite_from_company;
    }

    @Override
    protected void initView() {
        spaceId = getIntent().getIntExtra("team_id", 0);
        isAddAdmin = getIntent().getBooleanExtra("isAddAdmin", true);
        titleText = findViewById(R.id.tv_title);
        titleText.setText(getString(R.string.invite_form_company));
        contactList = findViewById(R.id.list_contact);
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
                        Toast.makeText(getApplicationContext(), "please select contact first", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (isAddAdmin) {
                        requestAddAdmin(contacts);
                    } else {
                        inviteCompanyMemberToSpace(contacts);
                    }
                }

                break;
        }
    }

    private void requestAddAdmin(List<CompanyContact> contacts) {
        String hh = "";
        for (int i = 0; i < contacts.size(); i++) {
            if (i == 0) {
                hh = contacts.get(i).getUserID();
            } else {
                hh = hh + "," + contacts.get(i).getUserID();
            }
        }
        String url = AppConfig.URL_PUBLIC + "TeamSpace/AddAdminMember?CompanyID=" + AppConfig.SchoolID + "&TeamSpaceID=" + spaceId + "&MemberList="+hh;
        ServiceInterfaceTools.getinstance().addAdminMember(url, ServiceInterfaceTools.ADDADMINMEMBER, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void inviteCompanyMemberToSpace(List<CompanyContact> contacts) {
        String url = AppConfig.URL_PUBLIC + "Invite/InviteCompanyMemberToSpace";
        ServiceInterfaceTools.getinstance().inviteCompanyMemberToSpace(url, ServiceInterfaceTools.INVITECOMPANYMEMBERTOSPACE, spaceId, contacts, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                setResult(RESULT_OK);
                finish();
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



    private RelativeLayout noDataLayout;
    private ProgressBar loadingBar;
    private TextView messageText;

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
