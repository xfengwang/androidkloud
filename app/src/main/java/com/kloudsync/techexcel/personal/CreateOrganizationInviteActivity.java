package com.kloudsync.techexcel.personal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.PhoneItem;
import com.kloudsync.techexcel.bean.RoleInTeam;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.CenterToast;
import com.kloudsync.techexcel.dialog.MemberRoleDialog;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.kloudsync.techexcel.start.LoginActivity;
import com.kloudsync.techexcel.tool.ContactsTool;
import com.kloudsync.techexcel.ui.MainActivity;
import com.kloudsync.techexcel.ui.WelcomeAndCreateActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrganizationInviteActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout backLayout;
    private EditText et_name;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView titleText;
    private LinearLayout phoneItemsParentLayout;
    List<PhoneItem> phoneItems = new ArrayList<>();
    LinearLayout inviteMoreLayout;
    private TextView inviteText;
    private TextView txt_skip;
    private int firstSpaceId;

    private int currentLayoutIndex=0;
    /**
     * 是否有邀请人 true 没有  false 有
     * */
    private boolean noInvite=true;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                default:
                    break;
            }
        }

    };

    private void COjson(String result) {
        JSONObject obj = null;
        Toast.makeText(this, R.string.create_success, Toast.LENGTH_SHORT).show();
        try {

            obj = new JSONObject(result);
            int RetData = obj.getInt("RetData");
            editor = sharedPreferences.edit();
            AppConfig.SchoolID = RetData;
            editor.putString("SchoolID", RetData + "");
            editor.putInt("teamid", -1);
            editor.putString("SchoolName", et_name.getText().toString());
            editor.putString("teamname", "");
            editor.commit();
            EventBus.getDefault().post(new TeamSpaceBean());
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ContactsTool contactsTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_organization_invite);
        contactsTool = new ContactsTool();
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        firstSpaceId = getIntent().getIntExtra("space_id", 0);
        initView();
        new CenterToast.Builder(getApplicationContext()).setSuccess(true).setMessage(getResources().getString(R.string.created_success)).create().show();
        addPhoneItem(0);
        addPhoneItem(1);
        addPhoneItem(2);
        currentLayoutIndex=2;
    }

    MemberRoleDialog roleDialog;




    private void addPhoneItem(final int i) {
        PhoneItem item = new PhoneItem();
        AppConfig.COUNTRY_CODE = sharedPreferences.getInt("countrycode", 86);
        item.setRegion("+" + AppConfig.COUNTRY_CODE);
        LinearLayout itemLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_invite_phone, null);
        TextView regionText = itemLayout.findViewById(R.id.txt_region);
        final EditText phoneEdit = itemLayout.findViewById(R.id.edit_telephone);
        phoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String tel=phoneEdit.getText().toString().trim();
                phoneItems.get(i).setPhoneNumber(tel);
                for(int i=0;i<phoneItems.size();i++){
                    if(!phoneItems.get(i).getPhoneNumber().equals("")){
                        noInvite=false;
                        break;
                    }else {
                        noInvite=true;
                    }
                }
                if(!noInvite){
                    inviteText.setEnabled(true);
                    inviteText.setBackgroundResource(R.drawable.bg_login_enable);
                }else {
                    inviteText.setEnabled(false);
                    inviteText.setBackgroundResource(R.drawable.bg_login_unable);
                }
            }
        });
        regionText.setText(item.getRegion());
        LinearLayout selectedRoleLayout = itemLayout.findViewById(R.id.layout_select_role);
        final TextView roleText = itemLayout.findViewById(R.id.txt_role);
        selectedRoleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoleInTeam role = new RoleInTeam();
                String currentRole = roleText.getText().toString().trim();
                if (currentRole.equals("member")) {
                    role.setTeamRole(RoleInTeam.ROLE_MEMBER);
                } else if (currentRole.equals("admin")) {
                    role.setTeamRole(RoleInTeam.ROLE_ADMIN);
                }
                roleDialog = new MemberRoleDialog(CreateOrganizationInviteActivity.this);
                roleDialog.setRole(role, roleText);
                roleDialog.show();
                PhoneItem phoneItem = (PhoneItem) v.getTag();
                if (phoneItem != null) {
                    phoneItem.setRole(role.getTeamRole());
                }
            }
        });
        selectedRoleLayout.setTag(item);
        phoneItems.add(item);
        phoneItemsParentLayout.addView(itemLayout);
    }

    private void initView() {
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        et_name = (EditText) findViewById(R.id.et_name);
        inviteText = (TextView) findViewById(R.id.txt_invite);
        inviteText.setOnClickListener(this);
        txt_skip = (TextView) findViewById(R.id.txt_skip);
        txt_skip.setOnClickListener(this);
        titleText = (TextView) findViewById(R.id.tv_title);
        titleText.setText(R.string.invite_new_new);
        phoneItemsParentLayout = findViewById(R.id.layout_phone_item_parent);
        backLayout.setOnClickListener(this);
        inviteMoreLayout = findViewById(R.id.layout_invite_more);
        inviteMoreLayout.setOnClickListener(this);
        openContactLayout = findViewById(R.id.layout_open_contact);
        openContactLayout.setOnClickListener(this);

        inviteText.setEnabled(false);
//	    initInviteGuidePop();
//	    new Handler().postDelayed(new Runnable() {
//		    @Override
//		    public void run() {
//			    showInviteGuidePop(openContactLayout);
//		    }
//	    }, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.layout_invite_more:
                currentLayoutIndex+=1;
                addPhoneItem(currentLayoutIndex);
                break;
            case R.id.layout_open_contact:
                contactsTool.getContact(this);
                break;
            case R.id.txt_skip:
                goToMainActivity();
                break;
            case R.id.txt_invite:
                List<String> phoneNumbers = fetchInivtePhoneNumbers();
                if (phoneNumbers.size() == 0) {
                    Toast.makeText(getApplicationContext(), "please enter or select least one phone number", Toast.LENGTH_SHORT).show();
                } else {
                    if (inviteItems.size() > 0) {
                        ServiceInterfaceTools.getinstance().inviteMultipleToCompany(AppConfig.SchoolID + "", inviteItems, firstSpaceId).enqueue(new Callback<NetworkResponse>() {
                            @Override
                            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {
                                if (response != null && response.isSuccessful() && response.body() != null) {
                                    if (response.body().getRetCode() == AppConfig.RETCODE_SUCCESS) {
                                        Toast.makeText(getApplicationContext(), "invite success", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "invite failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                goToMainActivity();
                            }

                            @Override
                            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "invite failed", Toast.LENGTH_SHORT).show();
                                goToMainActivity();
                            }
                        });
                    }
                }
                break;
            default:
                break;
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("FromOrganization",true);
        startActivity(intent);
        LoginActivity.instance.finish();
        WelcomeAndCreateActivity.instance.finish();
//        CreateOrganizationActivityV2.instance.finish();
        finish();
    }

    private JSONObject format() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("SchoolName", et_name.getText().toString().trim());
            jsonObject.put("Category1", 2);
            jsonObject.put("Category2", 0);
            jsonObject.put("OwnerID", AppConfig.UserID);
            jsonObject.put("AdminID", AppConfig.UserID);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }


    private RelativeLayout openContactLayout;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ContactsTool.REQUEST_CONTACTS) {
                String phoneNume = contactsTool.contactResponse(this, data);
                fillPhoneNumber(phoneNume);
            }
        }
    }

    private void fillPhoneNumber(String phoneNumber) {
        int index = -1;
        for (PhoneItem phoneItem : phoneItems) {
            if (phoneItem.getPhoneNumber().equals(phoneNumber)) {
                Toast.makeText(getApplicationContext(), "该联系人已经添加", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        for (PhoneItem phoneItem : phoneItems) {
            if (TextUtils.isEmpty(phoneItem.getPhoneNumber())) {
                index = phoneItems.indexOf(phoneItem);
                phoneItem.setPhoneNumber(phoneNumber);
                break;

            }
        }
        if (index == -1 || index >= phoneItemsParentLayout.getChildCount()) {
            return;
        }
        View view = phoneItemsParentLayout.getChildAt(index);
        EditText phoneEdit = view.findViewById(R.id.edit_telephone);
        phoneEdit.setText(phoneNumber);
    }

    List<PhoneItem> inviteItems = new ArrayList<>();

    private List<String> fetchInivtePhoneNumbers() {
        List<String> numbers = new ArrayList<>();
        for (PhoneItem item : phoneItems) {
            if (!TextUtils.isEmpty(item.getPhoneNumber())) {
                numbers.add(item.getPhoneNumber());
                inviteItems.add(item);
            }
        }
        return numbers;
    }

	private PopupWindow guidePopupWindow;

	private void initInviteGuidePop() {
		View guideView = LayoutInflater.from(this).inflate(R.layout.pop_invite_guide, null);
		guideView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		guidePopupWindow = new PopupWindow(guideView,
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		guidePopupWindow.setBackgroundDrawable(new BitmapDrawable());
		guideView.findViewById(R.id.tv_guide_next).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				guidePopupWindow.dismiss();
			}
		});
	}

	private void showInviteGuidePop(View v) {
		final WindowManager.LayoutParams lp = CreateOrganizationInviteActivity.this.getWindow().getAttributes();
		lp.alpha = 0.5f;//代表透明程度，范围为0 - 1.0f
		CreateOrganizationInviteActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		CreateOrganizationInviteActivity.this.getWindow().setAttributes(lp);
		//点击在按钮的中上方弹出popupWindow
		int btnWidth = v.getMeasuredWidth();
		int btnHeight = v.getMeasuredHeight();

		int popWidth = guidePopupWindow.getContentView().getMeasuredWidth();
		int popHeight = guidePopupWindow.getContentView().getMeasuredHeight();

		final int xoff = (int) ((float) (btnWidth - popWidth) / 2);//PopupWindow的x偏移值
		final int yoff = popHeight + btnHeight - 40; //因为相对于按钮的上方，所以该值为负值
		guidePopupWindow.showAsDropDown(v, xoff, -yoff);

		guidePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				lp.alpha = 1.0f;
				CreateOrganizationInviteActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				CreateOrganizationInviteActivity.this.getWindow().setAttributes(lp);
			}
		});
	}
}
