package com.kloudsync.techexcel.dialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.OrganizationAdapterV2;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.AppName;
import com.kloudsync.techexcel.bean.CompanyAccountInfo;
import com.kloudsync.techexcel.bean.CompanySubsystem;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.School;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.CustomSyncRoomTool;
import com.kloudsync.techexcel.ui.MainActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
public class SelectCompanyDialog implements OnClickListener {
    private Context mContext;
    private ExpandableListView lv_organization;
    private TextView tv_start,tv_cancel;
    private View popupWindow;
    private ArrayList<School> mlist = new ArrayList<>();
    private OrganizationAdapterV2 sAdapter;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private School school;

    private TeamSpaceBean teamSpaceBean = new TeamSpaceBean();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                mPopupWindow.dismiss();
                break;
            case R.id.tv_start:
                if (sAdapter.getSelectCompany() != null) {
                    school = sAdapter.getSelectCompany();
                    editor = sharedPreferences.edit();
                    editor.putInt("SchoolID", school.getSchoolID());
                    editor.commit();
                    getAppNames(school.getSchoolID());
                    getMyTeamList();
                }
                mPopupWindow.dismiss();
                onConfirmListener.onConfirm(school.getSchoolName());
                break;
            default:
                break;
        }
    }

    public boolean isShowing(){
        if(mPopupWindow != null){
            return mPopupWindow.isShowing();
        }
        return false;
    }

    public void hide(){
        if(mPopupWindow != null){
            mPopupWindow.hide();
        }
    }

    public Dialog mPopupWindow;
    public SelectCompanyDialog(Context context) {
        this.mContext = context;
        initPopuptWindow();
        getAllSchool();
    }
    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        sAdapter = new OrganizationAdapterV2(mContext);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        popupWindow = layoutInflater.inflate(R.layout.dialog_select_company, null);
        tv_cancel = (TextView) popupWindow.findViewById(R.id.tv_cancel);
        tv_start = (TextView) popupWindow.findViewById(R.id.tv_start);
        lv_organization = (ExpandableListView) popupWindow.findViewById(R.id.lv_organization);
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(popupWindow);
        WindowManager.LayoutParams lp = mPopupWindow.getWindow().getAttributes();
        lp.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.8f);
        lp.height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.8f);
        mPopupWindow.getWindow().setAttributes(lp);
        tv_start.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        lv_organization.setAdapter(sAdapter);

        lv_organization.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (!sAdapter.getSelectCompanyId().equals(id + "")) {
                    if (!TextUtils.isEmpty(sAdapter.getSelectCompanyId())) {
                        sAdapter.clearSelectedSubsystem(Integer.parseInt(sAdapter.getSelectCompanyId()));
                    }
                    sAdapter.setSelectCompany(Integer.parseInt(id + ""), "");
                    List<CompanySubsystem> subsystems = sAdapter.getSelectCompany().getSubsystems();
                    if (subsystems != null && subsystems.size() > 0) {
                        for (CompanySubsystem subsystem : subsystems) {
                            subsystem.setSelected(false);
                        }
                    }
                    sAdapter.notifyDataSetChanged();
                } else {
                    List<CompanySubsystem> subsystems = sAdapter.getSelectCompany().getSubsystems();
                    if (subsystems != null && subsystems.size() > 0) {
                        for (CompanySubsystem subsystem : subsystems) {
                            subsystem.setSelected(false);
                        }
                    }
                    sAdapter.getSelectCompany().setSubSystemSelected(false);
                    sAdapter.notifyDataSetChanged();
                }
                if (sAdapter.getGroup(groupPosition).getSubsystems() == null) {
                    getCompanySubsystems(id + "");
                }
                return false;
            }
        });
        lv_organization.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < sAdapter.getGroupCount(); ++i) {
                    if (i == groupPosition) {
                        continue;
                    }
                    lv_organization.collapseGroup(i);
                }
            }
        });
        lv_organization.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.e("onChildClick", "clicked");
                CompanySubsystem subsystem = sAdapter.getGroup(groupPosition).getSubsystems().get(childPosition);
                for (CompanySubsystem subsystem1 : sAdapter.getGroup(groupPosition).getSubsystems()) {
                    subsystem1.setSelected(false);
                }
                subsystem.setSelected(!subsystem.isSelected());
                sAdapter.getGroup(groupPosition).setSubSystemSelected(true);
                sAdapter.notifyDataSetChanged();

                return false;
            }
        });
    }

    public void show() {
        if (mPopupWindow != null) {
            mPopupWindow.show();
        }
    }

    private void getAllSchool() {
        LoginGet loginGet = new LoginGet();
        loginGet.setMySchoolGetListener(new LoginGet.MySchoolGetListener() {
            @Override
            public void getSchool(ArrayList<School> list) {
                mlist = list;
                sAdapter.setCompanies(mlist);
                int index = sAdapter.setSelectCompany(GetSaveInfo(), AppConfig.selectedSubSystemId);
                if (index > 0) {
                    getCompanySubsystemsAndExpand(sAdapter.getSelectCompanyId(), index);
                }
                SetMySchool();
            }
        });
        loginGet.GetSchoolInfo(mContext);
    }

    private int GetSaveInfo() {
        sharedPreferences = mContext.getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        return sharedPreferences.getInt("realSchoolID", -1);
    }

    private void getCompanySubsystemsAndExpand(final String companyId, final int index) {
        LoginGet loginGet = new LoginGet();
        loginGet.setMyCompanySubsystemsGetListener(new LoginGet.MyCompanySubsystemsGetListener() {
            @Override
            public void getCompanySubsystems(List<CompanySubsystem> list) {
                if (list != null && list.size() > 0) {
                    sAdapter.setSubsystems(list, Integer.parseInt(companyId), AppConfig.selectedSubSystemId);
                    lv_organization.expandGroup(index);
                }
            }
        });
        loginGet.getCompanySubsystems(mContext, companyId);
    }
    private void SetMySchool() {
        int id = GetSaveInfo();
        for (int i = 0; i < mlist.size(); i++) {
            if (mlist.get(i).getSchoolID() == id) {
                school = mlist.get(i);
                break;
            }
        }
    }

    private void getCompanySubsystems(final String companyId) {
        LoginGet loginGet = new LoginGet();
        loginGet.setMyCompanySubsystemsGetListener(new LoginGet.MyCompanySubsystemsGetListener() {
            @Override
            public void getCompanySubsystems(List<CompanySubsystem> list) {
                if (list != null && list.size() > 0) {
                    sAdapter.setSubsystems(list, Integer.parseInt(companyId), AppConfig.selectedSubSystemId);
                }
            }
        });
        loginGet.getCompanySubsystems(mContext, companyId);
    }

    private void getAppNames(int id) {
        ServiceInterfaceTools.getinstance().getAppNames(id).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    if (jsonObject.getString("msg").equals("success")) {
                        Gson gson = new Gson();
                        List<AppName> appCNNameList = new ArrayList<AppName>();
                        List<AppName> appENNameList = new ArrayList<AppName>();
                        List<AppName> appNameList = gson.fromJson(jsonObject.getString("data"), new TypeToken<List<AppName>>() {
                        }.getType());
                        App.appNames = appNameList;
                        for (AppName appName : appNameList) {
                            if (appName.getLanguageId() == 0) {
                                appCNNameList.add(appName);
                            } else {
                                appENNameList.add(appName);
                            }
                        }
                        App.appCNNames = appCNNameList;
                        App.appENNames = appENNameList;
                        System.out.println("App.appNames->" + App.appENNames.size());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println(t.getLocalizedMessage());
            }
        });
    }

    public void getMyTeamList() {
        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + school.getSchoolID() + "&type=1&parentID=0",
                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                        teamSpaceBean = new TeamSpaceBean();
                        if (list.size() > 0) {
                            teamSpaceBean = list.get(0);
                        }
                        AUUserInfo(sAdapter.getSelectSubsystem());
                    }
                });

    }

    private void AUUserInfo(CompanySubsystem subsystem) {
        final JSONObject jsonObject = format(subsystem);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "User/AddOrUpdateUserPreference", jsonObject);
                    Log.e("返回的jsonObject", jsonObject.toString() + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.AddOrUpdateUserPreference;
                        msg.obj = responsedata.toString();
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    private JSONObject format(CompanySubsystem subsystem) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("FieldID", 10001);
            jsonObject.put("PreferenceText", format2(subsystem) + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject format2(CompanySubsystem companySubsystem) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("SchoolID", school.getSchoolID());
            jsonObject.put("SchoolName", school.getSchoolName());
            if (companySubsystem != null) {
                jsonObject.put("TeamID", -1);
                jsonObject.put("TeamName", "");
                jsonObject.put("SubSystemData", format3(companySubsystem));
            } else {
                if (teamSpaceBean != null) {
                    jsonObject.put("TeamId", teamSpaceBean.getItemID());
                    jsonObject.put("TeamName", TextUtils.isEmpty(teamSpaceBean.getName()) ? "" : teamSpaceBean.getName());
                } else {
                    jsonObject.put("TeamID", -1);
                    jsonObject.put("TeamName", "");
                }

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject format3(CompanySubsystem subsystem) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("selectedSubSystemId", subsystem.getSubSystemId());
            jsonObject.put("selectedSubSystemType", subsystem.getType());
            jsonObject.put("subSystemName", subsystem.getSubSystemName());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.AddOrUpdateUserPreference:
                    String result = (String) msg.obj;
                    GetSchoolInfo();
                    break;
                case AppConfig.FAILED:
                    result = (String) msg.obj;
                    Toast.makeText(mContext, result,Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        }

    };

    private void GetSchoolInfo() {
        editor = sharedPreferences.edit();
        LoginGet lg = new LoginGet();
        lg.setSchoolTeamGetListener(new LoginGet.SchoolTeamGetListener() {
            @Override
            public void getST(School school) {
                Log.e("GetShoolInfo", "school:" + school);
                if (school != null) {
                    TeamSpaceBean teamSpaceBean = school.getTeamSpaceBean();
                    List<CompanySubsystem> companySubsystems = school.getSubsystems();
                    if (companySubsystems == null || companySubsystems.size() == 0) { //没有选子项
                        AppConfig.selectedSubSystemId = "";
                        AppConfig.SchoolID = school.getSchoolID();
                        editor.putInt("SchoolID", AppConfig.SchoolID);
                        editor.putString("SchoolName", school.getSchoolName());
                    } else {
                        AppConfig.selectedSubSystemId = school.getSubsystems().get(0).getSubSystemId();
                        AppConfig.SchoolID = Integer.parseInt(AppConfig.selectedSubSystemId);
                        editor.putInt("SchoolID", AppConfig.SchoolID);
                        editor.putString("SchoolName", school.getSubsystems().get(0).getSubSystemName());
                    }
                    editor.putInt("realSchoolID", school.getSchoolID());
                    editor.putString("teamname", teamSpaceBean.getName());
                    editor.putInt("teamid", teamSpaceBean.getItemID());
                    editor.commit();
                } else {
                    editor.putString("SchoolName", "");
                    editor.putString("teamname", "");
                    editor.putInt("teamid", -1);
                    editor.commit();
                }
                getCompanyNameList();
            }
        });
        lg.GetUserPreference(mContext, 10001 + "");
    }

    public void getCompanyNameList() {
        String url = AppConfig.URL_MEETING_BASE + "company_custom_display_name/name_list?companyId=" + AppConfig.SchoolID;
        ServiceInterfaceTools.getinstance().getCompanyDisplayNameList(url, ServiceInterfaceTools.GETCOMPANYDISPLAYNAMELIST, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                final JSONObject jsonObject = (JSONObject) object;
                Observable.just("request_company_count_info").observeOn(Schedulers.io()).doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        JSONObject response = ServiceInterfaceTools.getinstance().syncGetCompanyAccountInfo();
                        if (response.has("code")) {
                            int code = response.getInt("code");
                            if (code == 0) {
                                if (response.has("data")) {
                                    CompanyAccountInfo accountInfo = new Gson().fromJson(response.getJSONObject("data").toString(), CompanyAccountInfo.class);
                                    if (accountInfo != null) {
                                        Log.e("processLogin", "system_type:" + accountInfo.getSystemType() + ",-" + accountInfo.getCompanyName());
                                        AppConfig.systemType = accountInfo.getSystemType();
                                        sharedPreferences.edit().putInt("system_type", accountInfo.getSystemType()).commit();
                                    }
                                }
                            }

                            CustomSyncRoomTool.getInstance(mContext).setCustomSyncRoomContent(jsonObject);
                            MainActivity.RESUME = true;
                            EventBus.getDefault().post(new TeamSpaceBean());
                        }
                    }
                }).subscribe();

            }
        });
    }

    public interface OnConfirmListener{
        void onConfirm(String name);
    }
    public OnConfirmListener onConfirmListener;

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }
}
