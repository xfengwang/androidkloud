package com.kloudsync.techexcel.frgment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.EventSearchChat;
import com.kloudsync.techexcel.bean.EventSearchContact;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.InviteContactDialog;
import com.kloudsync.techexcel.help.PopContactHAHA;
import com.kloudsync.techexcel.school.SwitchOrganizationActivity;
import com.kloudsync.techexcel.search.ui.ContactSearchAndAddActivity;
import com.kloudsync.techexcel.ui.InviteFromPhoneActivity;
import com.kloudsync.techexcel.view.CustomViewPager;
import com.ub.service.activity.NotifyActivity;
import com.ub.service.activity.SelectUserActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class TwoToOneFragment extends Fragment implements ViewPager.OnPageChangeListener, InviteContactDialog.InviteOptionsLinstener, View.OnClickListener {

    private View view;

    private ImageView img_notice;
    private RelativeLayout addLayout;
    private TextView tv_ns;
    private ViewGroup chatSelected, contactSelected;
    private TextView chatUnselected, contactUnseleted;
    private TextView tv_chat, tv_contact;
    private TextView tv_sc;
    private CustomViewPager vp_contact;
    BroadcastReceiver broadcastReceiver;
    private boolean isFragmentVisible = false;
    private boolean isFirst = true;
    private boolean isContact;
    private int width;
    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private FragmentPagerAdapter mAdapter;
    private RelativeLayout searchLayout;
    private ImageView switchCompanyImage;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.twotoone_fragment, container, false);
            initView();
        }
        initFunction();
        return view;

    }

    private void initView() {
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.LOGININFO,MODE_PRIVATE);
        switchCompanyImage = (ImageView) view.findViewById(R.id.image_switch_company);
        addLayout = (RelativeLayout) view.findViewById(R.id.layout_add);
        img_notice = (ImageView) view.findViewById(R.id.img_notice);
        tv_ns = (TextView) view.findViewById(R.id.tv_ns);
        chatSelected = (ViewGroup) view.findViewById(R.id.tv_chat_selected);
        contactSelected = (ViewGroup) view.findViewById(R.id.tv_contact_selected);
        chatUnselected = view.findViewById(R.id.txt_chat_unselected);
        contactUnseleted = view.findViewById(R.id.txt_contact_unselected);
        tv_chat = view.findViewById(R.id.tv_chat);
        tv_contact = view.findViewById(R.id.tv_contact);
        vp_contact = (CustomViewPager) view.findViewById(R.id.vp_contact);
        vp_contact.addOnPageChangeListener(this);
        vp_contact.setPagingEnabled(true);
        searchLayout = (RelativeLayout) view.findViewById(R.id.search_layout);
        searchLayout.setOnClickListener(this);
        switchCompanyImage.setOnClickListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        isFragmentVisible = isVisibleToUser;
        if (isFirst && isVisibleToUser) {
            isFirst = false;

        }
    }

    private void initFunction() {
        GetCourseBroad();
        initVP();
        addLayout.setOnClickListener(new myOnClick());
        img_notice.setOnClickListener(new myOnClick());
        chatSelected.setOnClickListener(new myOnClick());
        chatUnselected.setOnClickListener(new myOnClick());
        contactUnseleted.setOnClickListener(new myOnClick());
        contactSelected.setOnClickListener(new myOnClick());


    }

    private void initVP() {
        DialogueFragment dialogueFragment = new DialogueFragment();
        ContactFragment contactFragment = new ContactFragment();
        mTabs = new ArrayList<Fragment>();
        mTabs.add(dialogueFragment);
        mTabs.add(contactFragment);
        mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }
        };
        vp_contact.setAdapter(mAdapter);
//        vp_contact.setOffscreenPageLimit(2);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        ChangeList(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void inviteFromContactOption() {
        Intent searchAddIntent = new Intent(getActivity(), ContactSearchAndAddActivity.class);
        searchAddIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(searchAddIntent);
    }

    @Override
    public void inviteNewOption() {
        inviteNew();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_layout) {
            if (!isContact) {
                EventBus.getDefault().post(new EventSearchChat());
            } else {
                EventBus.getDefault().post(new EventSearchContact());
            }
        } else if (v.getId() == R.id.image_switch_company) {
            goToSwitchCompany();
        }
    }

    protected class myOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_add:
//                    ShowPop();
                    chatOrInvite();
                    break;
                case R.id.img_notice:
                    GoToNotice();
                    break;
                case R.id.txt_chat_unselected:
                    ChangeList(0);
                    setBindViewText();
                    break;
                case R.id.txt_contact_unselected:
                    ChangeList(1);
                    setBindViewText();
                    break;
                default:
                    break;
            }
        }
    }

    InviteContactDialog inviteContactDialog;

    private void chatOrInvite() {
        if (isContact) {
            if (inviteContactDialog != null) {
                if (inviteContactDialog.isShowing()) {
                    inviteContactDialog.dismiss();
                }
                inviteContactDialog = null;
            }
            inviteContactDialog = new InviteContactDialog(getActivity());
            inviteContactDialog.setOptionsLinstener(this);
            inviteContactDialog.setInviteFromContactLayoutGone();
            inviteContactDialog.show();

        } else {
            Intent i = new Intent(getActivity(), SelectUserActivity.class);
            i.putExtra("isDialogue", true);
            startActivity(i);

        }
    }

    private void inviteNew() {
        Intent intent = new Intent(getActivity(), InviteFromPhoneActivity.class);
        intent.putExtra("invite_type", 0);
        startActivity(intent);
    }

    private void ShowPop() {
        PopContactHAHA haha = new PopContactHAHA();
        haha.getPopwindow(getActivity());
        haha.StartPop(addLayout);
    }

    @SuppressLint("NewApi")
    public void ChangeList(int i) {
        isContact = (1 == i);
        if (i == 0) {
            contactSelected.setVisibility(View.INVISIBLE);
            contactUnseleted.setVisibility(View.VISIBLE);
            chatSelected.setVisibility(View.VISIBLE);
            chatUnselected.setVisibility(View.INVISIBLE);
        } else if (i == 1) {
            contactSelected.setVisibility(View.VISIBLE);
            contactUnseleted.setVisibility(View.INVISIBLE);
            chatSelected.setVisibility(View.INVISIBLE);
            chatUnselected.setVisibility(View.VISIBLE);
        }
//        view_lin1.setVisibility(0 == i ? View.VISIBLE: View.INVISIBLE);
//        view_lin2.setVisibility(0 == i ? View.INVISIBLE: View.VISIBLE);
//        tv_myc.setBackground(getActivity().getDrawable(0 == i ? R.drawable.blue_left_bg : R.drawable.white_left_bg));
//        tv_sc.setBackground(getActivity().getDrawable(0 == i ? R.drawable.white_right_bg : R.drawable.blue_right_bg));
        vp_contact.setCurrentItem(i, false);
    }

    private void GoToNotice() {
        startActivity(new Intent(getActivity(), NotifyActivity.class));
    }

    LocalBroadcastManager localBroadcastManager;

    private void GetCourseBroad() {
        RefreshNotify();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                RefreshNotify();
            }
        };
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());

        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.Receive_Course));
//        getActivity().registerReceiver(broadcastReceiver, filter);
        //LocalBroadcastManager 是基于Handler实现的，拥有更高的效率与安全性。安全性主要体现在数据仅限于应用内部传输，避免广播被拦截、伪造、篡改的风险
        localBroadcastManager.registerReceiver(broadcastReceiver, filter);

    }

    private void RefreshNotify() {
        int sum = 0;
        for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
            if (!AppConfig.progressCourse.get(i).isStatus()) {
                sum++;
            }
        }
        tv_ns.setText(sum + "");
        tv_ns.setVisibility(sum == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (broadcastReceiver != null && getActivity() != null) {
//            getActivity().unregisterReceiver(broadcastReceiver);
            localBroadcastManager.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    private void goToSwitchCompany() {
        Intent intent = new Intent(getActivity(), SwitchOrganizationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private String getBindViewText(int fileId){
        String appBindName="";
        int language = sharedPreferences.getInt("language",1);
        if(language==1&&App.appENNames!=null){
            for(int i=0;i<App.appENNames.size();i++){
                if(fileId==App.appENNames.get(i).getFieldId()){
                    System.out.println("Name->"+App.appENNames.get(i).getFieldName());
                    appBindName=App.appENNames.get(i).getFieldName();
                    break;
                }
            }
        }else if(language==2&&App.appCNNames!=null){
            for(int i=0;i<App.appCNNames.size();i++){
                if(fileId==App.appCNNames.get(i).getFieldId()){
                    System.out.println("Name->"+App.appCNNames.get(i).getFieldName());
                    appBindName=App.appCNNames.get(i).getFieldName();
                    break;
                }
            }
        }
        return appBindName;
    }
    private void setBindViewText(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String chat=getBindViewText(1005);
                chatUnselected.setText(TextUtils.isEmpty(chat)? getString(R.string.dialogue):chat);
                tv_chat.setText(TextUtils.isEmpty(chat)? getString(R.string.dialogue):chat);
                String contact=getBindViewText(1006);
                contactUnseleted.setText(TextUtils.isEmpty(contact)? getString(R.string.contact):contact);
                tv_contact.setText(TextUtils.isEmpty(contact)? getString(R.string.contact):contact);
            }
        },100);
    }

    @Override
    public void onResume() {
        super.onResume();
        setBindViewText();
    }
}
