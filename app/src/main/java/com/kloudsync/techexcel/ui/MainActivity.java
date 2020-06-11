package com.kloudsync.techexcel.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.CompanyAccountInfo;
import com.kloudsync.techexcel.bean.EventCameraAndStoragePermissionForJoinMeetingGranted;
import com.kloudsync.techexcel.bean.EventCameraAndStoragePermissionForStartMeetingGranted;
import com.kloudsync.techexcel.bean.EventDoc;
import com.kloudsync.techexcel.bean.EventOpenOrCloseBluethoothNote;
import com.kloudsync.techexcel.bean.EventRefreshMembers;
import com.kloudsync.techexcel.bean.EventRefreshTab;
import com.kloudsync.techexcel.bean.EventSpaceData;
import com.kloudsync.techexcel.bean.EventSpaceFragment;
import com.kloudsync.techexcel.bean.EventSyncBook;
import com.kloudsync.techexcel.bean.EventSyncRoom;
import com.kloudsync.techexcel.bean.EventViewDocInSpacePermissionGranted;
import com.kloudsync.techexcel.bean.EventWxFilePath;
import com.kloudsync.techexcel.bean.FollowInfo;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.bean.UserPath;
import com.kloudsync.techexcel.bean.params.EventCamera;
import com.kloudsync.techexcel.bean.params.EventCloseStartKloud;
import com.kloudsync.techexcel.bean.params.EventProjectFragment;
import com.kloudsync.techexcel.bean.params.EventTeamFragment;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.AddDocToSpaceDialog;
import com.kloudsync.techexcel.dialog.AddWxDocDialog;
import com.kloudsync.techexcel.dialog.ExitMeetingDialog;
import com.kloudsync.techexcel.dialog.UploadFileDialog;
import com.kloudsync.techexcel.dialog.message.CustomizeMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.FriendMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.GroupMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.HelloFriendMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.KnowledgeMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.SendFileMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.ShareMessageItemProvider;
import com.kloudsync.techexcel.dialog.message.SystemMessageItemProvider;
import com.kloudsync.techexcel.docment.WeiXinApi;
import com.kloudsync.techexcel.frgment.ContactFragment;
import com.kloudsync.techexcel.frgment.CourseFragment;
import com.kloudsync.techexcel.frgment.MyFragment;
import com.kloudsync.techexcel.frgment.PersonalCenterFragment;
import com.kloudsync.techexcel.frgment.ProjectOneFragment;
import com.kloudsync.techexcel.frgment.ServiceFragment;
import com.kloudsync.techexcel.frgment.SpaceDocumentsFragment;
import com.kloudsync.techexcel.frgment.SpaceSyncRoomFragment;
import com.kloudsync.techexcel.frgment.TeamDocumentsFragment;
import com.kloudsync.techexcel.frgment.TopicFragment;
import com.kloudsync.techexcel.frgment.TwoToOneFragment;
import com.kloudsync.techexcel.help.AddDocumentTool;
import com.kloudsync.techexcel.help.ContactHelpInterface;
import com.kloudsync.techexcel.help.EverPenManger;
import com.kloudsync.techexcel.help.MeetingKit;
import com.kloudsync.techexcel.info.School;
import com.kloudsync.techexcel.mvp.presenter.MainPresenter;
import com.kloudsync.techexcel.mvp.view.IMainActivityView;
import com.kloudsync.techexcel.personal.PersonalCollectionActivity;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.kloudsync.techexcel.service.UploadService;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.CustomSyncRoomTool;
import com.kloudsync.techexcel.tool.DensityUtil;
import com.kloudsync.techexcel.tool.DocumentUploadTool;
import com.kloudsync.techexcel.tool.ToastUtils;
import com.kloudsync.techexcel.tool.UriTool;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.pgyersdk.update.DownloadFileListener;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.pgyersdk.update.javabean.AppBean;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tqltech.tqlpencomm.Dot;
import com.tqltech.tqlpencomm.PenCommAgent;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.service.KloudWebClientManager;
import com.ub.service.activity.SocketService;
import com.ub.service.activity.SyncBookActivity;
import com.ub.service.activity.SyncRoomActivity;
import com.ub.techexcel.bean.EventViewDocPermissionGranted;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.InviteJoinMeetingDialog;
import com.ub.techexcel.tools.MeetingServiceTools;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectCallback;
import io.rong.imlib.RongIMClient.ErrorCode;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kloudsync.techexcel.help.KloudPerssionManger.REQUEST_PERMISSION_CAMERA_AND_WRITE_EXTERNSL_FOR_JOIN_MEETING;
import static com.kloudsync.techexcel.help.KloudPerssionManger.REQUEST_PERMISSION_CAMERA_AND_WRITE_EXTERNSL_FOR_START_MEETING;
import static com.kloudsync.techexcel.help.KloudPerssionManger.REQUEST_PERMISSION_CAMERA_AND_WRITE_EXTERNSL_FOR_UPLOADFILE;
import static com.kloudsync.techexcel.help.KloudPerssionManger.REQUEST_PERMISSION_FOR_INSTALL_APK;
import static com.kloudsync.techexcel.help.KloudPerssionManger.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE;
import static com.kloudsync.techexcel.help.KloudPerssionManger.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE_FOR_VIWE_DOC_IN_SPACE;


public class MainActivity extends FragmentActivity implements AddWxDocDialog.OnDocSavedListener, AddDocToSpaceDialog.OnSpaceSelectedListener, OnClickListener, IMainActivityView {

    private List<TextView> tvs = new ArrayList<TextView>();
    private TextView tv_redcontact;
    private RelativeLayout rl_update;
    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private TextView tv_community;
    private int tvIDs[] = {R.id.txt_tab_document, R.id.txt_tab_chat, R.id.txt_tab_meeting,
            R.id.txt_tab_syncroom, R.id.txt_tab_personal};
    private int drawIDs[] = {R.drawable.tab1_unselected, R.drawable.tab2_unselected,
            R.drawable.tab3_unselected, R.drawable.tab4_unselected,
            R.drawable.tab5_unselected};
    private int draw_selectIDs[] = {R.drawable.tab1_selected, R.drawable.tab2_selected,
            R.drawable.tab3_selected, R.drawable.tab4_selected, R.drawable.tab5_selected};
    float density;

    public static RongIMClient mRongIMClient;
    public static MainActivity instance = null;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private FrameLayout teamFrame;
    private FrameLayout spaceFrame;

    public static boolean RESUME = false;
    Intent service;
    App app;
    public static boolean IsInDoc = false;
    public static boolean IsInSyncRoom = false;
    public static boolean IsInSyncBook = false;

    private static ContactHelpInterface chi;

    private TextView documentTab, chatTab, meetingTab, syncroomTab, personalTab;


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.RONGCONNECT_ERROR:
                    sharedPreferences = getSharedPreferences(AppConfig.LOGININFO, MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("isLogIn", false);
                    editor.commit();
                    Toast.makeText(getApplicationContext(), (String) msg.obj,
                            Toast.LENGTH_LONG).show();
                    RongIM.getInstance().disconnect();
                    final Intent intent = getPackageManager()
                            .getLaunchIntentForPackage(getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    break;
                case AppConfig.OnlineGunDan:
//                    if (1 == vp.getCurrentItem()) {
//                        GoTOTab(0);
//                    }
                    RongIM.getInstance().disconnect();
                    AppConfig.Online = 1;
                    Toast.makeText(MainActivity.this,
                            getResources().getString(R.string.Dialog_GunDan), Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.UPLOADHEAD:
                    rl_update.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.uploadsuccess),
                            Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), PersonalCollectionActivity.class);
                    startActivity(i);
                    break;
                case AppConfig.UPLOADFAILD:
                    rl_update.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.uploadfailure),
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    long systemTime = 0;
    private EverPenManger mEverPenManger;
    private MainPresenter mPresenter;
    private PenCommAgent mBleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("MainActivity", "on create");
        systemTime = System.currentTimeMillis();
        instance = this;
        app = (App) getApplication();
        app.setMainActivityInstance(instance);
        app.CheckLanguage();
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putBoolean("isLogIn", true).commit();
        fragmentManager = getSupportFragmentManager();
        setContentView(R.layout.activity_main);
//        PgyUpdateManager.register(this);
        initView();
        mEverPenManger = EverPenManger.getInstance(this);
        mEverPenManger.init();
        mBleManager = mEverPenManger.getBleManager();
        requestRongCloudOnlineStatus();
        GetSchoolInfo();
        initUpdate();
        startWBService();
        initWxApi();
//        GetMyPermission();
//        String wechatFilePath = getIntent().getStringExtra("wechat_data_path");
//        Log.e("check_dialog===", wechatFilePath+" ");
//        if (!TextUtils.isEmpty(wechatFilePath)) {
//            if (addWxDocDialog != null) {
//                addWxDocDialog.dismiss();
//                addWxDocDialog = null;
//            }
//            addWxDocDialog = new AddWxDocDialog(this, wechatFilePath);
//            addWxDocDialog.setSavedListener(this);
//
//            addWxDocDialog.show();
//        }
        mPresenter = new MainPresenter();
        mPresenter.attachView(this);
        mEverPenManger.addListener(mPresenter);
        registerReceiver(exitMeetngPromptReceiver, new IntentFilter("show_exit_meeting_dialog"));

        EventBus.getDefault().post(new EventCloseStartKloud());


    }


    private void isUploadweixinFile() {
        if (!TextUtils.isEmpty(AppConfig.wechatFilePath)) {
            if (addWxDocDialog != null) {
                addWxDocDialog.dismiss();
                addWxDocDialog = null;
            }
            addWxDocDialog = new AddWxDocDialog(this, AppConfig.wechatFilePath);
            addWxDocDialog.setSavedListener(this);
            addWxDocDialog.show();
        }
    }

    private void requestRongCloudOnlineStatus() {
        ServiceInterfaceTools.getinstance().getRongCloudOnlineStatus().enqueue(new Callback<NetworkResponse<Integer>>() {
            @Override
            public void onResponse(Call<NetworkResponse<Integer>> call, Response<NetworkResponse<Integer>> response) {
                if (response != null && response.isSuccessful() && response.body() != null) {
                    if (response.body().getRetData() == null) {
                        return;
                    }
                    AppConfig.Online = response.body().getRetData();
                }
            }

            @Override
            public void onFailure(Call<NetworkResponse<Integer>> call, Throwable t) {

            }
        });

        Log.e("user_token", "user_token:" + AppConfig.UserToken);
    }

    private void GetMyPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.LOCATION_HARDWARE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.LOCATION_HARDWARE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO}, 0x0010);
        }
    }

    UserPath userPath;

    private void GetSchoolInfo() {
        LoginGet lg = new LoginGet();
        lg.setSchoolTeamGetListener(new LoginGet.SchoolTeamGetListener() {
            @Override
            public void getST(final School school) {
                Log.e("GetShoolInfo", "school:" + school);
                Observable.just("init_company_info").doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (school != null) {
                            TeamSpaceBean teamSpaceBean = school.getTeamSpaceBean();
                            AppConfig.SchoolID = school.getSchoolID();
                            editor.putInt("SchoolID", school.getSchoolID());
                            editor.putString("SchoolName", school.getSchoolName());
                            if (teamSpaceBean != null) {
                                editor.putString("teamname", teamSpaceBean.getName());
                                editor.putInt("teamid", teamSpaceBean.getItemID());
                            }
                            editor.commit();
                        } else {
                            editor.putString("SchoolName", "");
                            editor.putString("teamname", "");
                            editor.putInt("teamid", -1);
                            editor.commit();
                        }
                    }
                }).observeOn(Schedulers.io()).doOnNext(new Consumer<String>() {
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
                        }
                    }
                }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        initDatas();
                    }
                }).subscribe();

            }
        });
        lg.GetUserPreference(this, 10001 + "");

    }

    private IWXAPI api;

    private void initWxApi() {
        WeiXinApi.getInstance().init(this);

        api = WXAPIFactory.createWXAPI(MainActivity.this,
                AppConfig.WX_APP_ID, true);
        api.registerApp(AppConfig.WX_APP_ID);
        api.handleIntent(getIntent(), WeiXinApi.getInstance());
        WeiXinApi.getInstance().setApi(api);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, WeiXinApi.getInstance());
    }

    private void UpOrNoOutInfo() {
        if (!TextUtils.isEmpty(AppConfig.OUTSIDE_PATH)) {
            Intent service2 = new Intent(MainActivity.this, UploadService.class);
            service2.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            startService(service2);
        } else {
            return;
        }
    }

    private void startWBService() {

        service = new Intent(this, SocketService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(service);
        } else {
            startService(service);
        }
    }

    ProgressDialog progressDialog;
    Uri installUri;

    private void initUpdate() {
        // TODO Auto-generated method stub
        new PgyUpdateManager.Builder()
                .setForced(false)                //设置是否强制更新,非自定义回调更新接口此方法有用
                .setUserCanRetry(false)         //失败后是否提示重新下载，非自定义下载 apk 回调此方法有用
                .setDeleteHistroyApk(false)     // 检查更新前是否删除本地历史 Apk
                .setUpdateManagerListener(new UpdateManagerListener() {
                    @Override
                    public void onNoUpdateAvailable() {
                        //没有更新是回调此方法
                        Log.e("pgyer", "there is no new version");
                    }

                    @Override
                    public void onUpdateAvailable(final AppBean appBean) {
                        //有更新是回调此方法
                        Log.e("pgyer", "there is new version can update"
                                + "new versionCode is " + appBean.getVersionCode());

                        //调用以下方法，DownloadFileListener 才有效；如果完全使用自己的下载方法，不需要设置DownloadFileListener
//
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(getResources().getString(R.string.update))
                                .setMessage(getResources().getString(R.string.update_message))
                                .setPositiveButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        dialog.dismiss();
                                    }
                                }).setCancelable(false).setNegativeButton(
                                getResources().getString(R.string.update),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        PgyUpdateManager.downLoadApk(appBean.getDownloadURL());
                                    }
                                }).show();
                    }

                    @Override
                    public void checkUpdateFailed(Exception e) {
                        //更新检测失败回调
                        Log.e("pgyer", "check update failed ", e);

                    }
                })
                //注意 ：下载方法调用 PgyUpdateManager.downLoadApk(appBean.getDownloadURL()); 此回调才有效
                .setDownloadFileListener(new DownloadFileListener() {   // 使用蒲公英提供的下载方法，这个接口才有效。
                    @Override
                    public void downloadFailed() {
                        //下载失败
                        Log.e("pgyer", "download apk failed");
                        if (progressDialog != null) {
                            progressDialog.cancel();
                            progressDialog = null;
                        }

                    }

                    @Override
                    public void downloadSuccessful(Uri uri) {
                        Log.e("pgyer", "download apk success");
                        if (progressDialog != null) {
                            progressDialog.cancel();
                            progressDialog = null;
                        }

                        installUri = uri;
                        Observable.just("install").observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                installApkBeforeCheckPermission(installUri);
                            }
                        }).subscribe();


//                        PgyUpdateManager.installApk(uri);  // 使用蒲公英提供的安装方法提示用户 安装apk
                    }

                    @Override
                    public void onProgressUpdate(Integer... integers) {
                        Log.e("pgyer", "update download apk progress : " + integers[0]);
                        if (progressDialog == null) {
                            progressDialog = new ProgressDialog(MainActivity.this);
                            progressDialog.setProgressStyle(1);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage(getResources().getString(R.string.downloading));
                            progressDialog.show();
                        } else {
                            if (integers != null && integers.length > 0) {
                                progressDialog.setProgress(integers[0]);
                            }

                        }
                    }
                }).register();
    }

    private void installApkBeforeCheckPermission(Uri uri) {
//        if(KloudPerssionManger.isPermissionInstallApkGranted(this)){
//            Toast.makeText(this,"permisson_granted",Toast.LENGTH_SHORT).show();
//
//        }else {
//            Toast.makeText(this,"request_permission",Toast.LENGTH_SHORT).show();
//            ActivityCompat.requestPermissions(this, new String[]{
//                    Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.REQUEST_INSTALL_PACKAGES}, REQUEST_PERMISSION_FOR_INSTALL_APK);
//        }

//        PgyUpdateManager.installApk(uri);
        String filePath = UriTool.getFilePathByUri(this, uri);
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file.exists()) {
                installAPK(file);
            }
        }

    }

    private void initView() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        density = dm.density;
        rl_update = (RelativeLayout) findViewById(R.id.rl_update);
        tv_redcontact = (TextView) findViewById(R.id.tv_redcontact);
        teamFrame = findViewById(R.id.frame_tab_team);
        spaceFrame = findViewById(R.id.frame_tab_space);
        initTabs();
        RongConnect();
//        setBindViewText();
    }

    private void initTabs() {
        documentTab = findViewById(R.id.txt_tab_document);
        chatTab = findViewById(R.id.txt_tab_chat);
        meetingTab = findViewById(R.id.txt_tab_meeting);
        syncroomTab = findViewById(R.id.txt_tab_syncroom);
        personalTab = findViewById(R.id.txt_tab_personal);
        documentTab.setOnClickListener(this);
        chatTab.setOnClickListener(this);
        meetingTab.setOnClickListener(this);
        syncroomTab.setOnClickListener(this);
        personalTab.setOnClickListener(this);
        tvs.add(documentTab);
        tvs.add(chatTab);
        tvs.add(meetingTab);
        tvs.add(syncroomTab);
        tvs.add(personalTab);
        setTabName();
    }

    private void setTabName() {
        String[] tab = getResources().getStringArray(R.array.tabname);
        for (int i = 0; i < tvIDs.length; i++) {
            TextView tabText = tvs.get(i);
            if (tabText == syncroomTab) {
                tabText.setText(CustomSyncRoomTool.getInstance(this).getCustomyinxiang());
            } else if (tabText == meetingTab) {

                if (AppConfig.systemType == 0) {
                    tabText.setText(R.string.meeting);
                } else {
                    tabText.setText(R.string.course);

                }
            } else {
                tabText.setText(tab[i]);
            }


        }

    }

    private void RongConnect() {
        RongIM.connect(AppConfig.RongUserToken, new ConnectCallback() {

            @Override
            public void onError(ErrorCode arg0) {
                Toast.makeText(getApplicationContext(), "connect onError",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String arg0) {
                /*Toast.makeText(getApplicationContext(), "connect onSuccess",
                        Toast.LENGTH_SHORT).show();*/
                AppConfig.RongUserID = arg0;

                RongIM.registerMessageTemplate(new CustomizeMessageItemProvider());
                RongIM.registerMessageTemplate(new KnowledgeMessageItemProvider());
                RongIM.registerMessageTemplate(new SystemMessageItemProvider());
                RongIM.registerMessageTemplate(new FriendMessageItemProvider());
                RongIM.registerMessageTemplate(new GroupMessageItemProvider());
                RongIM.registerMessageTemplate(new SendFileMessageItemProvider());
//				RongIM.registerMessageTemplate(new CourseMessageItemProvider());
                RongIM.registerMessageTemplate(new ShareMessageItemProvider());
                RongIM.registerMessageTemplate(new HelloFriendMessageItemProvider());

//                initDatas();
                if (RongIM.getInstance() != null
                        && RongIM.getInstance().getRongIMClient() != null) {
                    /**
                     * 设置连接状态变化的监听器.
                     */
                    RongIM.getInstance()
                            .getRongIMClient()
                            .setConnectionStatusListener(
                                    new MyConnectionStatusListener());
                }
                AppConfig.Online = 0;
            }

            @Override
            public void onTokenIncorrect() {
                Log.e("Token Incorrect", "Token Incorrect");
                Message msg = new Message();
                msg.what = AppConfig.RONGCONNECT_ERROR;
                msg.obj = "连接错误";
                handler.sendMessage(msg);
            }
        });
//		getToken();
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            if (inSpace && spaceFragmentData != null) {
                if (v.getId() == R.id.txt_tab_document || v.getId() == R.id.txt_tab_syncroom) {
                    if (v.getId() == R.id.txt_tab_document) {
                        spaceFragmentData.setType(1);
                    } else {
                        spaceFragmentData.setType(2);
                    }
                    spaceFragmentData.setTeamId(sharedPreferences.getInt("teamid", 0));
                    spaceFragmentData.setTeamName(sharedPreferences.getString("teamname", ""));
                    handleChangeSpaceFragment(spaceFragmentData);
                } else {
                    changeTeamFragment((TextView) v);
                }
            } else {
                changeTeamFragment((TextView) v);
            }
//            setBindViewText();

        }
    }

    private class MyConnectionStatusListener implements
            RongIMClient.ConnectionStatusListener {

        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus) {

                case CONNECTED:// 连接成功。

                    break;
                case DISCONNECTED:// 断开连接。

                    break;
                case CONNECTING:// 连接中。

                    break;
                case NETWORK_UNAVAILABLE:// 网络不可用。

                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
                    if (!instance.isFinishing()) {
                        Message msg = new Message();
                        msg.what = AppConfig.OnlineGunDan;
//                        msg.obj = "该账号已在其他设备上登录";
                        handler.sendMessage(msg);
                    }

                    break;
                default:
                    break;
            }
        }
    }

    TeamDocumentsFragment documentsFragment;
    SpaceDocumentsFragment spaceDocumentsFragment;
    TopicFragment topicFragment;
    TwoToOneFragment twoToOneFragment;
    MyFragment serviceFragment;
    PersonalCenterFragment personalCenterFragment;
    ProjectOneFragment projectOneFragment;

    private void initDatas() {
        documentsFragment = new TeamDocumentsFragment();
        spaceDocumentsFragment = new SpaceDocumentsFragment();
        twoToOneFragment = new TwoToOneFragment();
        if (AppConfig.systemType == 0) {
            serviceFragment = new ServiceFragment();
        } else {
            serviceFragment = new CourseFragment();
        }

        topicFragment = new TopicFragment();
        personalCenterFragment = new PersonalCenterFragment();
        projectOneFragment = new ProjectOneFragment();
        if (sharedPreferences.getBoolean("enable_sync", false)) {
            syncroomTab.setVisibility(View.VISIBLE);
        } else {
            syncroomTab.setVisibility(View.GONE);
        }

        mTabs.add(documentsFragment);
        mTabs.add(twoToOneFragment);
        mTabs.add(serviceFragment);
        mTabs.add(topicFragment);
        mTabs.add(personalCenterFragment);
        changeTeamFragment(documentTab);
        Log.e("user_info", "user_token:" + AppConfig.UserToken + ",company_id:" + AppConfig.SchoolID);
        isOpenYinxiang();
        setTabName();
    }

    private void isOpenYinxiang() {
        String url = AppConfig.URL_PUBLIC + "School/GetSettingItem?schoolID=" + AppConfig.SchoolID + "&settingID=10001";
        ServiceInterfaceTools.getinstance().getSchoolSettingItem(url, ServiceInterfaceTools.GETSCHOOLSETTINGITEM, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                int settingValue = (int) object;
                if (settingValue == 1) {
                    syncroomTab.setVisibility(View.VISIBLE);
                } else if (settingValue == 0) {
                    syncroomTab.setVisibility(View.GONE);
                }
            }
        });
    }

    public void GoTOTab(int s) {
        for (int i = 0; i < tvs.size(); i++) {
            if (i == s) {
                // 必须设置图片大小，否则不显示ad
                Drawable d = getResources().getDrawable(draw_selectIDs[s]);
                d.setBounds(0, 0, DensityUtil.dp2px(getApplicationContext(), 18),
                        DensityUtil.dp2px(getApplicationContext(), 18)); // 必须设置图片大小，否则不显示
                tvs.get(s).setTextColor(getResources().getColor(R.color.tab_blue));
                tvs.get(s).setCompoundDrawables(null, d, null, null);
            } else {
                Drawable d = getResources().getDrawable(drawIDs[i]);
                d.setBounds(0, 0, DensityUtil.dp2px(getApplicationContext(), 18),
                        DensityUtil.dp2px(getApplicationContext(), 18));  // 必须设置图片大小，否则不显示
                tvs.get(i).setTextColor(getResources().getColor(R.color.tab_gray));
                tvs.get(i).setCompoundDrawables(null, d, null, null);
            }
        }
    }


    public void changeSelectedTab(int id) {
        for (int i = 0; i < tvs.size(); i++) {
            TextView tabText = tvs.get(i);

            if (tabText.getId() == R.id.txt_tab_document) {
                if (isDisplayProjectOne) {
                    tabText.setText("项目");
                } else {
                    tabText.setText(getString(R.string.service));
                }
            }


            if (tabText.getId() == id) {
                // 必须设置图片大小，否则不显示ad
                Drawable d = getResources().getDrawable(draw_selectIDs[tvs.indexOf(tabText)]);
                d.setBounds(0, 0, DensityUtil.dp2px(getApplicationContext(), 18),
                        DensityUtil.dp2px(getApplicationContext(), 18)); // 必须设置图片大小，否则不显示
                tabText.setTextColor(getResources().getColor(R.color.tab_blue));
                tabText.setCompoundDrawables(null, d, null, null);
            } else {

                Drawable d = getResources().getDrawable(drawIDs[tvs.indexOf(tabText)]);
                d.setBounds(0, 0, DensityUtil.dp2px(getApplicationContext(), 18),
                        DensityUtil.dp2px(getApplicationContext(), 18));  // 必须设置图片大小，否则不显示
                tabText.setTextColor(getResources().getColor(R.color.tab_gray));
                tabText.setCompoundDrawables(null, d, null, null);
            }
        }
    }

    private boolean inSpace = false;

    private void UpdateOutData() {
        RequestParams params = new RequestParams();
        params.setHeader("UserToken", AppConfig.UserToken);

        params.addBodyParameter("Content-Type", "multipart/form-data");// 设定传送的内容类型
        // params.setContentType("application/octet-stream");
        File file = new File(AppConfig.OUTSIDE_PATH);
        if (file.exists()) {
            int lastSlash = 0;
            lastSlash = AppConfig.OUTSIDE_PATH.lastIndexOf("/");
            String name = AppConfig.OUTSIDE_PATH.substring(lastSlash + 1, AppConfig.OUTSIDE_PATH.length());
            params.addBodyParameter(name, file);
            String url = null;
            try {
                url = AppConfig.URL_PUBLIC + "EventAttachment/AddNewFavoriteMultipart?FileName="
                        + URLEncoder.encode(LoginGet.getBase64Password(name), "UTF-8")
                        + "&Guid=" + AppConfig.DEVICE_ID + System.currentTimeMillis()
                        + "&Total=1&Index=1";
                Log.e("hahaha", url + ":" + name);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.e("url", url);
            HttpUtils http = new HttpUtils();
            http.configResponseTextCharset("UTF-8");
            http.send(HttpRequest.HttpMethod.POST, url, params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onStart() {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.upload),
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onLoading(long total, long current,
                                              boolean isUploading) {

                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            Log.e("hahaha", responseInfo + "");
                            Message message = new Message();
                            message.what = AppConfig.UPLOADHEAD;
                            handler.sendEmptyMessage(message.what);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            Log.e("error", msg.toString());
                            Message message = new Message();
                            message.what = AppConfig.UPLOADFAILD;
                            handler.sendEmptyMessage(message.what);
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }

        AppConfig.OUTSIDE_PATH = "";
    }

    public void DisplayRed(boolean flag_r) {
        tv_redcontact.setVisibility(flag_r ? View.VISIBLE : View.GONE);
        if (chi != null) {
            chi.RefreshRed(flag_r);
        }
    }


    private void KillFile() {
        FileUtils fileUtils = new FileUtils(getApplicationContext());
        fileUtils.deleteFile();
        fileUtils.deleteFile2();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment.getClass().equals(ContactFragment.class)) {
            chi = (ContactHelpInterface) fragment;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        /*if (!flag_update) {
            UpOrNoOutInfo();
            flag_update = true;
        }*/
//        flag_update = !flag_update;
//        if (!TextUtils.isEmpty(AppConfig.OUTSIDE_PATH) && flag_update) {
//            UpOrNoOutInfo();
//        }
    }


    public void onResume() {
        super.onResume();
//        WatchCourseActivity3.watch3instance = false;
        SyncRoomActivity.syncroomInstance = false;
        SyncBookActivity.syncbookInstance = false;
        if (RESUME) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            for (int i = 0; i < mTabs.size(); i++) {
                Fragment fragment = (Fragment) mTabs.get(i);
                ft.remove(fragment);
            }
//            ft.commit();
            //华为用ft.commit()，会引发异常(Can not perform this action after onSaveInstanceState)，改用这个就不会了，而且效果一样
            ft.commitAllowingStateLoss();
            ft = null;
            fm.executePendingTransactions();
//
            initDatas();
            setTabName();
            initView();
            RESUME = false;
        }

        if (AppConfig.isRefreshRed) {
            DisplayRed(false);
        }


        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }


        AppConfig.isToPersonalCenter = false;
        AppConfig.isRefreshRed = false;

        MobclickAgent.onPageStart("MainActivity");
        MobclickAgent.onResume(this);
        Tools.keepSocketServiceOn(this);
//        testNote();
        Log.e("time_interval", "interval:" + (System.currentTimeMillis() - systemTime));//统计时长
        isUploadweixinFile();

        openJoinMeeting();

    }

    InviteJoinMeetingDialog inviteJoinMeetingDialog;

    private void openJoinMeeting() {
        String content = Tools.getClipContent(this);
        if (!TextUtils.isEmpty(content)) { //
            if (content.contains("https://kloud.cn/join?id=")) {
                int start = content.lastIndexOf("id=") + 3;
                int end = content.lastIndexOf("复制") - 1;
                if (end > start) {
                    String meetingId = content.substring(start, end);
                    if (!TextUtils.isEmpty(meetingId)) {
                        inviteJoinMeetingDialog = new InviteJoinMeetingDialog();
                        inviteJoinMeetingDialog.getPopupWindowInstance(this);
                        inviteJoinMeetingDialog.show(meetingId);

                    }
                }

            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshMeetingMainMembers(EventRefreshMembers refreshMembers) {
        if (inviteJoinMeetingDialog != null && inviteJoinMeetingDialog.isShowing()) {
            inviteJoinMeetingDialog.refresh(refreshMembers);
        }
    }


    private void testNote() {
        EventOpenOrCloseBluethoothNote note = new EventOpenOrCloseBluethoothNote();
        note.setNoteId("1915234");
        handleBluetoothNote(note);
    }

    public void onPause() {
        Log.e("MainActivity", "on pause");
        super.onPause();
        MobclickAgent.onPageEnd("MainActivity");
        MobclickAgent.onPause(this);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toastRequestErrorMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            message = getString(R.string.operate_failure);
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshTabs(EventRefreshTab eventRefreshTab) {
        boolean enableSync = sharedPreferences.getBoolean("enable_sync", false);
        Log.e("MainActivity", "enableSync:" + enableSync);
        isOpenYinxiang();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        instance = null;
        mEverPenManger.startOrStopFindDevice(false);
        if (mBleManager != null) {
            mBleManager.ReqOfflineDataTransfer(false);
        }
        mEverPenManger.removeListener(mPresenter);
        mEverPenManger.unBindService();
        mBleManager = null;
        mEverPenManger = null;
        mPresenter.detachView();
        mPresenter = null;
        handler.removeCallbacksAndMessages(null);
        app.setMainActivityInstance(null);
        AppConfig.isUpdateCustomer = false;
        AppConfig.isUpdateDialogue = false;
        AppConfig.HASUPDATAINFO = false;
        stopService();
        unregisterReceiver(exitMeetngPromptReceiver);

        app.getThreadMgr().shutDown();
        KillFile();
        System.gc();
    }

    private void stopService() {
        if (isServiceRunning(this, "com.ub.service.activity.SocketService")) {
            Intent service = new Intent(this, SocketService.class);
        }
    }

    AddWxDocDialog addWxDocDialog;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fromWeChat(EventWxFilePath filePath) {
        Log.e("check_dialog", "show dialog");
//        if (addWxDocDialog != null) {
//            addWxDocDialog.dismiss();
//            addWxDocDialog = null;
//        }
//        addWxDocDialog = new AddWxDocDialog(this, filePath.getPath());
//        addWxDocDialog.setSavedListener(this);
//        addWxDocDialog.show();

    }

//    public  String getTopActivity(Activity context) {
//        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
//        ComponentName componentName;
//        if (runningTaskInfos != null) {
//            componentName = runningTaskInfos.get(0).topActivity;
//            String result = componentName.getPackageName() + "." + componentName.getClassName();
//            return result;
//        } else {
//            return null;
//        }
//    }


//    boolean isShow = false;
//    WindowManager windowManager;
//    WindowManager.LayoutParams params ;
//    View contentView;
//    private void showWindow() {
//        if (windowManager == null) {
//            windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//            params = new WindowManager.LayoutParams();
//            //窗口类型
//            if (Build.VERSION.SDK_INT > 25) {
//                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//            } else {
//                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//            }
//            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//            params.gravity = Gravity.BOTTOM;
//            // 设置图片格式，效果为背景透明
////            lp.format = PixelFormat.RGBA_8888;
//            params.width = WindowManager.LayoutParams.MATCH_PARENT;
//            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            contentView = LayoutInflater.from(this).inflate(R.layout.dialog_adddf, null);
//            //设置监听
//        }
//        if (!isShow) {
//            windowManager.addView(contentView, params);
//            isShow = true;
//        }
//    }


    private UploadFileDialog uploadFileDialog;

    private AddDocToSpaceDialog addDocToSpaceDialog;

    @Override
    public void onSpaceSelected(int spaceId) {
        AddDocumentTool.addDocumentToSpace(this, filePath, spaceId, new DocumentUploadTool.DocUploadDetailLinstener() {
            @Override
            public void uploadStart() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog == null) {
                            uploadFileDialog = new UploadFileDialog(MainActivity.this);
                            uploadFileDialog.setTile("uploading");
                            uploadFileDialog.show();

                        } else {
                            if (!uploadFileDialog.isShowing()) {
                                uploadFileDialog.show();
                            }
                        }
                    }
                });
            }

            @Override
            public void uploadFile(final int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                            uploadFileDialog.setProgress(progress);
                        }
                    }
                });
            }

            @Override
            public void convertFile(final int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                            uploadFileDialog.setTile("Converting");
                            uploadFileDialog.setProgress(progress);
                        }
                    }
                });
            }

            @Override
            public void uploadFinished(Object result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null) {
                            uploadFileDialog.cancel();
                        }
                        EventBus.getDefault().post(new TeamSpaceBean());
                    }
                });

            }

            @Override
            public void uploadError(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "add favorite success", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    String filePath;

    @Override
    public void onSaveSpace(String path) {
        AppConfig.wechatFilePath = "";
        this.filePath = path;
        if (addDocToSpaceDialog != null) {
            addDocToSpaceDialog.dismiss();
        }
        addDocToSpaceDialog = new AddDocToSpaceDialog(this);
        addDocToSpaceDialog.setOnSpaceSelectedListener(this);
        addDocToSpaceDialog.show();

    }

    @Override
    public void onCancel() {
        AppConfig.wechatFilePath = "";
    }


    @Override
    public void onSaveFavorite(String path) {
        Intent intent = new Intent(MainActivity.this, PersonalCollectionActivity.class);
        startActivity(intent);
    }


    private FragmentManager fragmentManager;
    private TextView currentTabText;
    private Fragment currentTeamFragment;
    private Fragment currentSpaceFragment;

    private Fragment getTeamFragment(final int id) {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        switch (id) {
            case R.id.txt_tab_document:
                if (isDisplayProjectOne) {
                    fragment = projectOneFragment;
                } else {
                    fragment = documentsFragment;
                }
                fragment.setArguments(bundle);
                break;
            case R.id.txt_tab_chat:
                fragment = twoToOneFragment;
                fragment.setArguments(bundle);
                break;

            case R.id.txt_tab_meeting:
                fragment = serviceFragment;
                fragment.setArguments(bundle);
                break;

            case R.id.txt_tab_syncroom:
                fragment = topicFragment;
                fragment.setArguments(bundle);
                break;

            case R.id.txt_tab_personal:
                fragment = personalCenterFragment;
                fragment.setArguments(bundle);
                break;
        }

        return fragment;
    }


    private boolean isDisplayProjectOne = false;

    @Subscribe
    public void changeProjectOne(EventProjectFragment eventProjectFragment) {
        Log.e("eventProjectFragment", eventProjectFragment.getSubSystemId() + "");
        isDisplayProjectOne = true;

        TextView v = documentTab;
        teamFrame.setVisibility(View.VISIBLE);
        spaceFrame.setVisibility(View.GONE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = getTeamFragment(v.getId());
        if (currentTeamFragment != null) {
            fragmentTransaction.hide(currentTeamFragment);
        }
        if (!fragment.isAdded()) {
            fragmentTransaction.add(R.id.frame_tab_team, fragment, String.valueOf(v.getId()));
        }
        fragmentTransaction.commitAllowingStateLoss();
        currentTeamFragment = fragment;
        changeSelectedTab(v.getId());

    }


    public void changeTeamFragment(TextView v) {
//        isDisplayProjectOne
        teamFrame.setVisibility(View.VISIBLE);
        spaceFrame.setVisibility(View.GONE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        fragment = fragmentManager.findFragmentByTag(String.valueOf(v.getId()));
        Log.e("changeFragment", "fragment:" + fragment);
        if (fragment == null) {
            if (currentTeamFragment != null) {
                fragmentTransaction.hide(currentTeamFragment);
            }
            fragment = getTeamFragment(v.getId());
            if (!fragment.isAdded() && null == fragmentManager.findFragmentByTag(String.valueOf(v.getId()))) {
                fragmentTransaction.add(R.id.frame_tab_team, fragment, String.valueOf(v.getId()));
            }
        } else if (fragment == currentTeamFragment) {

        } else {
            if (currentTeamFragment != null) {
                fragmentTransaction.hide(currentTeamFragment);
            }
            fragmentTransaction.show(fragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
        currentTeamFragment = fragment;
        changeSelectedTab(v.getId());
    }

    @Subscribe
    public void changeSpaceFragment(EventSpaceFragment spaceFragment) {
        Log.e("EventBus", "changeSpaceDocumentsFragment,space:" + spaceFragment);
        spaceFragmentData = spaceFragment;
        inSpace = true;
        handleChangeSpaceFragment(spaceFragmentData);
    }

    EventSpaceFragment spaceFragmentData;

    @Subscribe
    public void changeTeamFragment(EventTeamFragment teamFragment) {
        Log.e("EventBus", "changeTeamDocumentsFragment:" + teamFragment);
        spaceFragmentData = null;
        currentSpaceFragment = null;
        inSpace = false;
        if (teamFragment.getType() == 1) {
            changeTeamFragment(documentTab);
        } else if (teamFragment.getType() == 2) {
            changeTeamFragment(syncroomTab);
        }
    }


    public void handleChangeSpaceFragment(EventSpaceFragment spaceFragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        Log.e("currentSpaceFragment", "space:" + spaceFragment);
        if (spaceFragment.getType() == 1) {
            //go to space documents
            teamFrame.setVisibility(View.GONE);
            spaceFrame.setVisibility(View.VISIBLE);
            fragment = new SpaceDocumentsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("ItemID", spaceFragment.getItemID());
            bundle.putString("space_name", spaceFragment.getSpaceName());
            bundle.putInt("team_id", spaceFragment.getTeamId());
            bundle.putString("project_name", spaceFragment.getTeamName());
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.frame_tab_space, fragment);
            fragmentTransaction.commitAllowingStateLoss();
            changeSelectedTab(R.id.txt_tab_document);
        } else if (spaceFragment.getType() == 2) {
            // go to space syncrooms
            teamFrame.setVisibility(View.GONE);
            spaceFrame.setVisibility(View.VISIBLE);
            fragment = new SpaceSyncRoomFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("ItemID", spaceFragment.getItemID());
            bundle.putInt("spaceid", spaceFragment.getItemID());
            bundle.putString("space_name", spaceFragment.getSpaceName());
            bundle.putInt("team_id", spaceFragment.getTeamId());
            bundle.putString("project_name", spaceFragment.getTeamName());
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.frame_tab_space, fragment);
            fragmentTransaction.commitAllowingStateLoss();
            changeSelectedTab(R.id.txt_tab_syncroom);
        }
        currentSpaceFragment = fragment;
    }

    @Subscribe
    public void eventSpaceChanged(EventSpaceData spaceData) {
        if (spaceFragmentData != null) {
            spaceFragmentData.setSpaceId(spaceData.getSpaceId());
            spaceFragmentData.setItemID(spaceData.getSpaceId());
            spaceFragmentData.setSpaceName(spaceData.getSpaceName());
        }
    }

    @Subscribe
    public void followOhterDevice(FollowInfo info) {
        Log.e("followOhterDevice", "info:" + info);
        if (info.getData() == null) {
            return;
        }
        switch (info.getActionType()) {
            case "HELLO":
//                handleHeartMessage(info);
                break;
            case "ENABLE_TV_FOLLOW":
                handleEnableTvFollow(info);
                break;
            case "DISABLE_TV_FOLLOW":
                sendLeaveMeetingMessage();
                break;
            case "BIND_TV_JOIN_MEETING":
                handleBindJoinMeeting(info);
                break;
            case "BIND_TV_LEAVE_MEETING":
                sendLeaveMeetingMessage();
                break;
        }
    }


    private void handleBindJoinMeeting(FollowInfo followInfo) {
        JSONObject messageJson = followInfo.getData();
        try {
            if (messageJson.has("meetingId")) {
                followInfo.setMeetingId(messageJson.getString("meetingId"));
            }
            if (messageJson.has("type")) {
                followInfo.setType(messageJson.getInt("type"));
            }
            if (messageJson.has("itemId")) {
                followInfo.setItemId(messageJson.getInt("itemId"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("handleBindJoinMeeting", "info:" + followInfo);

        // -- 1:如果在会议里面
//        if (WatchCourseActivity3.watch3instance && WatchCourseActivity3.isInMeeting) {
//            return;
//        }

        // -- 2:如果在document,syncroom ,或者 syncbook里面
//        if((WatchCourseActivity3.watch3instance && !WatchCourseActivity3.isInMeeting) || SyncRoomActivity.syncroomInstance ||
//                SyncBookActivity.syncbookInstance){
//            String _meetingId = "";
//            if(WatchCourseActivity3.watch3instance){
//                _meetingId = WatchCourseActivity3.meetingId;
//            }
//            if(SyncBookActivity.syncbookInstance){
//                _meetingId = SyncBookActivity.meetingId;
//            }
//            if(SyncRoomActivity.syncroomInstance){
//                _meetingId = SyncRoomActivity.meetingId;
//            }
//
//        }
        sendLeaveMeetingMessage();
        doFollowUser(followInfo);


    }

    private void handleEnableTvFollow(FollowInfo followInfo) {
        JSONObject messageJson = followInfo.getData();
        try {
            if (messageJson.has("meetingId")) {
                followInfo.setMeetingId(messageJson.getString("meetingId"));
            }
            if (messageJson.has("type")) {
                followInfo.setType(messageJson.getInt("type"));
            }
            if (messageJson.has("itemId")) {
                followInfo.setItemId(messageJson.getInt("itemId"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("handleEnableTvFollow", "info:" + followInfo);

        // -- 1:如果在会议里面
//        if (WatchCourseActivity3.watch3instance && WatchCourseActivity3.isInMeeting) {
//            return;
//        }

        // -- 2:如果在document,syncroom ,或者 syncbook里面
//        if((WatchCourseActivity3.watch3instance && !WatchCourseActivity3.isInMeeting) || SyncRoomActivity.syncroomInstance ||
//                SyncBookActivity.syncbookInstance){
//            String _meetingId = "";
//            if(WatchCourseActivity3.watch3instance){
//                _meetingId = WatchCourseActivity3.meetingId;
//            }
//            if(SyncBookActivity.syncbookInstance){
//                _meetingId = SyncBookActivity.meetingId;
//            }
//            if(SyncRoomActivity.syncroomInstance){
//                _meetingId = SyncRoomActivity.meetingId;
//            }
//
//        }
        sendLeaveMeetingMessage();
        doFollowUser(followInfo);

    }


    private void handleHeartMessage(FollowInfo followInfo) {
        try {
            String meetingId = null;
            int meetingType = 0;
            JSONObject messageJson = followInfo.getData();
            if (messageJson.has("hasOwner")) {
                boolean hasOwner = messageJson.getBoolean("hasOwner");
                if (hasOwner) {
                    // 开启了同步
                    // 1: 如果正在会议里面，不用做额外处理
//                    if (WatchCourseActivity3.watch3instance && WatchCourseActivity3.isInMeeting) {
//                        return;
//                    }

                    if (messageJson.has("tvOwnerMeetingId")) {
                        followInfo.setMeetingId(messageJson.getString("tvOwnerMeetingId"));
                    } else {
                        followInfo.setMeetingId("");
                    }

                    if (messageJson.has("tvOwnerMeetingLessonId")) {
                        followInfo.setLessionId(messageJson.getString("tvOwnerMeetingLessonId"));
                    }

                    if (messageJson.has("tvOwnerMeetingType")) {
                        followInfo.setType(messageJson.getInt("tvOwnerMeetingType"));
                    }

                    // -- 2:如果在document,syncroom ,或者 syncbook里面
//                    Log.e("check_instance", "watch3instance:" + WatchCourseActivity3.watch3instance + "--syncroomInstance:" + SyncRoomActivity.syncroomInstance + "--syncbookInstance:" + SyncBookActivity.syncbookInstance);
//                    if ((WatchCourseActivity3.watch3instance && !WatchCourseActivity3.isInMeeting) || SyncRoomActivity.syncroomInstance ||
//                            SyncBookActivity.syncbookInstance) {
//                        String _meetingId = "";
//                        if (WatchCourseActivity3.watch3instance) {
//                            _meetingId = WatchCourseActivity3.meetingId;
//                            Log.e("----", "one,meeting_id:" + _meetingId);
//                        }
//                        if (SyncBookActivity.syncbookInstance) {
//                            _meetingId = SyncBookActivity.meetingId;
//                            Log.e("----", "two,meeting_id:" + _meetingId);
//                        }
//                        if (SyncRoomActivity.syncroomInstance) {
//                            _meetingId = SyncRoomActivity.meetingId;
//                            Log.e("----", "three,meeting_id:" + _meetingId);
//                        }
//
//                        if (messageJson.has("tvBindUserId")) {
//                            String bindUserId = messageJson.getString("tvBindUserId");
//                            if (!TextUtils.isEmpty(bindUserId)) {
//                                if (TextUtils.isEmpty(followInfo.getMeetingId())) {
//                                    // 心跳中没有会议了
////                                    sendLeaveMeetingMessage();
//                                    return;
//                                }
//                            }
//                        }
//
//                        if (!_meetingId.equals(messageJson.getString("tvOwnerMeetingId"))) {
//                            Log.e("----", "four,tvOwnerMeetingId:" + messageJson.getString("tvOwnerMeetingId"));
//                            sendLeaveMeetingMessage();
//                            followUser(followInfo.getMeetingId(), followInfo.getLessionId(), followInfo.getType());
//                        }
//                        return;
//                    }

                    // -- 没有在任何..
                    if (!SyncRoomActivity.syncroomInstance ||
                            !SyncBookActivity.syncbookInstance)
                        if (messageJson.has("tvOwnerMeetingId")) {
                            followUser(followInfo.getMeetingId(), followInfo.getLessionId(), followInfo.getType());

                        }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void doFollowUser(final FollowInfo info) {
        if (info.getType() == 2) {
            // Doc
            Observable.just(info).observeOn(Schedulers.io()).map(new Function<FollowInfo, FollowInfo>() {
                @Override
                public FollowInfo apply(FollowInfo followInfo) throws Exception {
                    Response<ResponseBody> response = ServiceInterfaceTools.getinstance().
                            getLessionIdByItemId(info.getItemId()).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject data = new JSONObject(new String(response.body().bytes()));
                        if (data.has("RetData")) {
                            followInfo.setLessionId(data.getInt("RetData") + "");
                        }
                    }
                    Log.e("getLessionId", "info:" + followInfo);
                    return followInfo;
                }
            }).doOnNext(new Consumer<FollowInfo>() {
                @Override
                public void accept(FollowInfo followInfo) throws Exception {
                    followUser(followInfo.getMeetingId(), followInfo.getLessionId(), followInfo.getType());
                }
            }).subscribe();
        } else {
            if (info.getMeetingId().contains(",")) {
                String[] datas = info.getMeetingId().split(",");
                followUser(info.getMeetingId(), datas[0], info.getType());
            }
        }

    }

    private void followUser(String meetingId, String lessionId, int type) {
        Log.e("MainActivity", "follow user,meeting type:" + type + ",meeting id" + meetingId + ",lession id:" + lessionId);
        if (TextUtils.isEmpty(lessionId) || lessionId.equals("0")) {
//            Toast.makeText(this, "数据获取失败", Toast.LENGTH_SHORT).show();
            return;
        }
        if (type == 1 || type == 2) {
            Intent intent = (type == 2) ? new Intent(this, DocAndMeetingActivity.class) :
                    new Intent(this, SyncRoomActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("userid", AppConfig.UserID);
            intent.putExtra("meetingId", meetingId);
            intent.putExtra("isTeamspace", true);
            intent.putExtra("identity", 1);
            intent.putExtra("is_meeting", false);
            intent.putExtra("lessionId", lessionId);
            intent.putExtra("isInstantMeeting", 1);
            intent.putExtra("meeting_type", type);
            intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
            intent.putExtra("isStartCourse", false);
            intent.putExtra("type", type);
            startActivity(intent);
        } else if (type == 0) {
            Intent intent = new Intent(this, DocAndMeetingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("userid", AppConfig.UserID);
            intent.putExtra("meetingId", meetingId);
            intent.putExtra("isTeamspace", false);
            intent.putExtra("identity", 1);
            intent.putExtra("lessionId", lessionId);
            intent.putExtra("isInstantMeeting", 1);
            intent.putExtra("meeting_type", type);
            intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
            intent.putExtra("isStartCourse", false);
            intent.putExtra("is_meeting", true);
            intent.putExtra("type", type);
            startActivity(intent);
        } else if (type == 3) {
            Intent intent = new Intent(this, SyncBookActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("userid", AppConfig.UserID);
            intent.putExtra("meetingId", meetingId);
            intent.putExtra("isTeamspace", true);
            intent.putExtra("identity", 1);
            intent.putExtra("is_meeting", false);
            intent.putExtra("lessionId", lessionId);
            intent.putExtra("isInstantMeeting", 1);
            intent.putExtra("meeting_type", type);
            intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
            intent.putExtra("isStartCourse", false);
            intent.putExtra("type", type);
            startActivity(intent);
        }

    }

    private void sendLeaveMeetingMessage() {
        Log.e("MainActivity", "sendLeaveMeetingMessage");
        Intent intent = new Intent();
        intent.setAction("com.cn.socket");
        intent.putExtra("message", "LEAVE_MEETING");
        sendBroadcast(intent);
    }

    @Subscribe
    public void doc(EventDoc doc) {
        Log.e("event_bus", "set doc:" + true);
//        WatchCourseActivity3.watch3instance = true;
    }

    @Subscribe
    public void doc(EventSyncBook book) {
        Log.e("event_bus", "set book:" + true);
        SyncBookActivity.syncbookInstance = true;
    }

    @Subscribe
    public void doc(EventSyncRoom room) {
        Log.e("event_bus", "set room:" + true);
        SyncRoomActivity.syncroomInstance = true;

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("MainActivity", "on stop");
        keepWebSocketLive();
    }

    private void keepWebSocketLive() {
        if (isServiceRunning(this, "com.ub.service.activity.SocketService")) {
            Log.e("MainActivity", "SocketService is running");
            KloudWebClientManager.getInstance().startHeartBeat();
        } else {
            Log.e("MainActivity", "SocketService is not running");
            startWBService();
        }
    }

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> seviceList = activityManager.getRunningServices(300);
        if (seviceList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < seviceList.size(); i++) {
            if (seviceList.get(i).service.getClassName().toString().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                EventViewDocPermissionGranted viewDocPermissionGranted = new EventViewDocPermissionGranted();
                EventBus.getDefault().post(viewDocPermissionGranted);

            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "查看文档需要访问sdcard的权限，请允许", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == REQUEST_PERMISSION_CAMERA_AND_WRITE_EXTERNSL_FOR_JOIN_MEETING) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                EventCameraAndStoragePermissionForJoinMeetingGranted joinMeetingGranted = new EventCameraAndStoragePermissionForJoinMeetingGranted();
                EventBus.getDefault().post(joinMeetingGranted);

            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED || grantResults[2] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "加入会议前请先同意必要的权限", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == REQUEST_PERMISSION_CAMERA_AND_WRITE_EXTERNSL_FOR_START_MEETING) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                EventCameraAndStoragePermissionForStartMeetingGranted startMeetingGranted = new EventCameraAndStoragePermissionForStartMeetingGranted();
                EventBus.getDefault().post(startMeetingGranted);

            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED || grantResults[2] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "开始会议前请先同意必要的权限", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_PERMISSION_CAMERA_AND_WRITE_EXTERNSL_FOR_UPLOADFILE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                EventCamera startMeetingGranted = new EventCamera();
                EventBus.getDefault().post(startMeetingGranted);

            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "开始会议需要访问相机，请允许", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == REQUEST_PERMISSION_FOR_INSTALL_APK) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (installUri != null) {
                    installApkBeforeCheckPermission(installUri);
                }
            }
        } else if (requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE_FOR_VIWE_DOC_IN_SPACE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                EventViewDocInSpacePermissionGranted viewDocPermissionGranted = new EventViewDocInSpacePermissionGranted();
                EventBus.getDefault().post(viewDocPermissionGranted);

            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "查看文档需要访问sdcard的权限，请允许", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void installAPK(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //第二个参数需要与<provider>标签中的android:authorities属性相同
            uri = FileProvider.getUriForFile(this, "com.kloudsync.techexcel.FileProvider", apkFile);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            uri = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }

    @Override
    public void getBleManager(PenCommAgent bleManager) {
        mBleManager = bleManager;
    }

    @Override
    public void onConnected() {
        if (personalCenterFragment != null) {
            personalCenterFragment.setCurrentPenNameAndStatus(true);
        }
    }

    @Override
    public void onDisconnected() {
        if (personalCenterFragment != null) {
            personalCenterFragment.setCurrentPenNameAndStatus(false);
        }
    }

    @Override
    public void onConnectFailed() {
        if (personalCenterFragment != null) {
            personalCenterFragment.setCurrentPenNameAndStatus(false);
        }
    }


    @Override
    public void onReceiveDot(Dot dot) {
       /* String uuid = UUID.randomUUID().toString() + System.currentTimeMillis();
        NoteDotBean noteDotBean = new NoteDotBean();
        noteDotBean.setDotId(uuid);
        noteDotBean.setDot(dot);
	    SyncWebNoteActionsCache.getInstance(this).cacheActions(noteDotBean);*/
    }

    @Override
    public void onReceiveOfflineStrokes(Dot dot) {

    }

    @Override
    public void toast(String msg) {
        ToastUtils.show(this, msg);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    private String getBindViewText(int fileId) {
        String appBindName = "";
        int language = sharedPreferences.getInt("language", 1);
        if (language == 1 && App.appENNames != null) {
            for (int i = 0; i < App.appENNames.size(); i++) {
                if (fileId == App.appENNames.get(i).getFieldId()) {
                    System.out.println("Name->" + App.appENNames.get(i).getFieldName());
                    appBindName = App.appENNames.get(i).getFieldName();
                    break;
                }
            }
        } else if (language == 2 && App.appCNNames != null) {
            for (int i = 0; i < App.appCNNames.size(); i++) {
                if (fileId == App.appCNNames.get(i).getFieldId()) {
                    System.out.println("Name->" + App.appCNNames.get(i).getFieldName());
                    appBindName = App.appCNNames.get(i).getFieldName();
                    break;
                }
            }
        }
        return appBindName;
    }

    private void setBindViewText() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String document = getBindViewText(1004);
                documentTab.setText(TextUtils.isEmpty(document) ? getString(R.string.documents) : document);
                String chat = getBindViewText(1005);
                chatTab.setText(TextUtils.isEmpty(chat) ? getString(R.string.dialogue) : chat);
                String meeting = getBindViewText(1007);
                if (AppConfig.systemType == 0) {
                    meetingTab.setText(TextUtils.isEmpty(meeting) ? getString(R.string.course) : meeting);

                } else {
                    meetingTab.setText(TextUtils.isEmpty(meeting) ? getString(R.string.Meeting) : meeting);

                }
                String syncroom = getBindViewText(1001);
                syncroomTab.setText(TextUtils.isEmpty(syncroom) ? getString(R.string.community) : syncroom);
            }
        }, 500);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleBluetoothNote(EventOpenOrCloseBluethoothNote note) {

        if (!TextUtils.isEmpty(note.getNoteId())) {
            if (note.getStatus() == 1) {
                Observable.just(note.getNoteId()).observeOn(Schedulers.io()).map(new Function<String, Note>() {
                    @Override
                    public Note apply(String noteId) throws Exception {
                        return MeetingServiceTools.getInstance().syncGetNoteByNoteId(noteId);
                    }

                }).doOnNext(new Consumer<Note>() {
                    @Override
                    public void accept(final Note note) throws Exception {
                        if (note.getAttachmentID() > 0) {
                            String title = note.getTitle();
                            if (TextUtils.isEmpty(title)) {
                                title = "";
                            }
                            String url = AppConfig.URL_PUBLIC
                                    + "Lesson/AddTempLessonWithOriginalDocument?attachmentID=" + note.getAttachmentID()
                                    + "&Title=" + URLEncoder.encode(LoginGet.getBase64Password(""), "UTF-8");
                            JSONObject jsonObject = ServiceInterfaceTools.getinstance().syncGetTempLessonWithOriginalDocument(url);
                            if (jsonObject.has("RetCode")) {
                                if (jsonObject.getInt("RetCode") == 0) {
                                    JSONObject data = jsonObject.getJSONObject("RetData");
                                    final String lessionId = data.optLong("LessonID") + "";
                                    final String itemId = data.optLong("ItemID") + "";
                                    if (!TextUtils.isEmpty(lessionId)) {
                                        Observable.just("view_note").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                                            @Override
                                            public void accept(String s) throws Exception {
                                                goToViewNote(lessionId, itemId, note);
                                            }
                                        });
                                    }
                                }
                            }
                        }

                    }
                }).subscribe();
            }

        }
    }

    private void goToViewNote(String lessonId, String itemId, Note note) {
        updateSocket();
        Intent intent = new Intent(this, NoteViewActivityV2.class);
//        Intent intent = new Intent(getActivity(), WatchCourseActivity3.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("userid", AppConfig.UserID);
        //-----
        intent.putExtra("meeting_id", Integer.parseInt(lessonId) + "," + AppConfig.UserID);
//        intent.putExtra("meeting_id", "Doc-" + AppConfig.UserID);
        intent.putExtra("document_id", itemId);
        intent.putExtra("meeting_type", 2);
        intent.putExtra("lession_id", Integer.parseInt(lessonId));
        intent.putExtra("url", note.getAttachmentUrl());
        intent.putExtra("note_id", note.getNoteID());
        intent.putExtra("local_file_id", note.getLocalFileID());
        startActivity(intent);
    }

    private void updateSocket() {
        Intent service = new Intent(this, SocketService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(service);
            startService(service);
        } else {
            startService(service);
        }
    }

    ExitMeetingDialog exitMeetingDialog;

    private void showMeetingExitDialog() {
        if (exitMeetingDialog != null) {
            if (exitMeetingDialog.isShowing()) {
                exitMeetingDialog.hide();
            }
            exitMeetingDialog = null;
        }
        exitMeetingDialog = new ExitMeetingDialog(this);
        exitMeetingDialog.show();
    }

    BroadcastReceiver exitMeetngPromptReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showMeetingExitDialog();
        }
    };
}
