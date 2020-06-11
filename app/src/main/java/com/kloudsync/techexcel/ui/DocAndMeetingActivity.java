package com.kloudsync.techexcel.ui;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.BottomDocThumbnailAdapter;
import com.kloudsync.techexcel.bean.BookNote;
import com.kloudsync.techexcel.bean.DocThumbnailListBean;
import com.kloudsync.techexcel.bean.DocumentPage;
import com.kloudsync.techexcel.bean.EventChangeCameraSize;
import com.kloudsync.techexcel.bean.EventClose;
import com.kloudsync.techexcel.bean.EventCloseNoteView;
import com.kloudsync.techexcel.bean.EventCloseShare;
import com.kloudsync.techexcel.bean.EventCreateSync;
import com.kloudsync.techexcel.bean.EventDeleteDocs;
import com.kloudsync.techexcel.bean.EventExit;
import com.kloudsync.techexcel.bean.EventExpanedUserList;
import com.kloudsync.techexcel.bean.EventFloatingNote;
import com.kloudsync.techexcel.bean.EventHideMeetingMenu;
import com.kloudsync.techexcel.bean.EventHighlightNote;
import com.kloudsync.techexcel.bean.EventInviteUsers;
import com.kloudsync.techexcel.bean.EventKickOffMember;
import com.kloudsync.techexcel.bean.EventMeetingDocuments;
import com.kloudsync.techexcel.bean.EventMicroPhone;
import com.kloudsync.techexcel.bean.EventMute;
import com.kloudsync.techexcel.bean.EventNote;
import com.kloudsync.techexcel.bean.EventNoteErrorShowDocument;
import com.kloudsync.techexcel.bean.EventNotePageActions;
import com.kloudsync.techexcel.bean.EventOpenNote;
import com.kloudsync.techexcel.bean.EventOpenOrCloseBluethoothNote;
import com.kloudsync.techexcel.bean.EventPageActions;
import com.kloudsync.techexcel.bean.EventPageNotes;
import com.kloudsync.techexcel.bean.EventPlaySoundtrack;
import com.kloudsync.techexcel.bean.EventPresnterChanged;
import com.kloudsync.techexcel.bean.EventRefreshDocs;
import com.kloudsync.techexcel.bean.EventRefreshMembers;
import com.kloudsync.techexcel.bean.EventRequestFailed;
import com.kloudsync.techexcel.bean.EventSelectNote;
import com.kloudsync.techexcel.bean.EventSelectSpeakerMode;
import com.kloudsync.techexcel.bean.EventSelfJoinCoachAudience;
import com.kloudsync.techexcel.bean.EventSendJoinMeetingMessage;
import com.kloudsync.techexcel.bean.EventSetPresenter;
import com.kloudsync.techexcel.bean.EventShareDocInMeeting;
import com.kloudsync.techexcel.bean.EventShareScreen;
import com.kloudsync.techexcel.bean.EventShowFullAgora;
import com.kloudsync.techexcel.bean.EventShowMenuIcon;
import com.kloudsync.techexcel.bean.EventShowNotePage;
import com.kloudsync.techexcel.bean.EventSocketMessage;
import com.kloudsync.techexcel.bean.EventTypeClicked;
import com.kloudsync.techexcel.bean.HelloMessage;
import com.kloudsync.techexcel.bean.JoinMeetingMessage;
import com.kloudsync.techexcel.bean.MeetingChangeBean;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.bean.MeetingPauseOrResumBean;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.bean.Member;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.bean.NoteId;
import com.kloudsync.techexcel.bean.SoundTrack;
import com.kloudsync.techexcel.bean.SoundtrackDetail;
import com.kloudsync.techexcel.bean.SoundtrackDetailData;
import com.kloudsync.techexcel.bean.SoundtrackMediaInfo;
import com.kloudsync.techexcel.bean.SupportDevice;
import com.kloudsync.techexcel.bean.TvDevice;
import com.kloudsync.techexcel.bean.VedioData;
import com.kloudsync.techexcel.bean.params.EventEndCoach;
import com.kloudsync.techexcel.bean.params.EventSoundSync;
import com.kloudsync.techexcel.bean.params.EventTalkAllStu;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.config.RealMeetingSetting;
import com.kloudsync.techexcel.dialog.AddFileFromDocumentDialog;
import com.kloudsync.techexcel.dialog.AddFileFromFavoriteDialog;
import com.kloudsync.techexcel.dialog.AddWxDocDialog;
import com.kloudsync.techexcel.dialog.AddWxDocDialog.OnDocSavedListener;
import com.kloudsync.techexcel.dialog.BeInvitedCoachAudienceDialog;
import com.kloudsync.techexcel.dialog.BottomDocThumbnailDialog;
import com.kloudsync.techexcel.dialog.CenterToast;
import com.kloudsync.techexcel.dialog.KickOffMemberDialog;
import com.kloudsync.techexcel.dialog.MeetingMembersDialog;
import com.kloudsync.techexcel.dialog.MeetingRecordManager;
import com.kloudsync.techexcel.dialog.MemberDisplayModeSettingDialog;
import com.kloudsync.techexcel.dialog.PopSpeakerWindowMore;
import com.kloudsync.techexcel.dialog.PrivateCoachManager;
import com.kloudsync.techexcel.dialog.RecordNoteActionManager;
import com.kloudsync.techexcel.dialog.ShareDocInMeetingDialog;
import com.kloudsync.techexcel.dialog.SoundtrackPlayDialog;
import com.kloudsync.techexcel.dialog.SoundtrackPlayManager;
import com.kloudsync.techexcel.dialog.SoundtrackRecordManager;
import com.kloudsync.techexcel.dialog.UploadFileDialog;
import com.kloudsync.techexcel.dialog.plugin.UserNotesDialog;
import com.kloudsync.techexcel.filepicker.FileEntity;
import com.kloudsync.techexcel.filepicker.FilePickerActivity;
import com.kloudsync.techexcel.filepicker.PickerManager;
import com.kloudsync.techexcel.help.AddDocumentTool;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.AudiencePromptDialog;
import com.kloudsync.techexcel.help.BottomMenuManager;
import com.kloudsync.techexcel.help.ChatManager;
import com.kloudsync.techexcel.help.DeviceManager;
import com.kloudsync.techexcel.help.DocVedioManager;
import com.kloudsync.techexcel.help.FollowSpearkerModeManager;
import com.kloudsync.techexcel.help.MeetingKit;
import com.kloudsync.techexcel.help.MeetingPauseManager;
import com.kloudsync.techexcel.help.NoteViewManager;
import com.kloudsync.techexcel.help.PageActionsAndNotesMgr;
import com.kloudsync.techexcel.help.PopBottomChat;
import com.kloudsync.techexcel.help.PopBottomFile;
import com.kloudsync.techexcel.help.PopBottomMenu;
import com.kloudsync.techexcel.help.SetPresenterDialog;
import com.kloudsync.techexcel.help.ShareDocumentDialog;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.help.UserData;
import com.kloudsync.techexcel.info.Uploadao;
import com.kloudsync.techexcel.response.DevicesResponse;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.tool.CameraRecyclerViewTouchListener;
import com.kloudsync.techexcel.tool.DocumentModel;
import com.kloudsync.techexcel.tool.DocumentPageCache;
import com.kloudsync.techexcel.tool.DocumentUploadTool;
import com.kloudsync.techexcel.tool.LocalNoteManager;
import com.kloudsync.techexcel.tool.MeetingSettingCache;
import com.kloudsync.techexcel.tool.QueryLocalNoteTool;
import com.kloudsync.techexcel.tool.SocketMessageManager;
import com.kloudsync.techexcel.tool.ToastUtils;
import com.mining.app.zxing.MipcaActivityCapture;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.service.activity.AddMeetingMemberActivity;
import com.ub.service.activity.FloatingWindowNoteManager;
import com.ub.service.activity.SocketService;
import com.ub.techexcel.adapter.AgoraCameraAdapterV2;
import com.ub.techexcel.adapter.BottomFileAdapter;
import com.ub.techexcel.adapter.FullAgoraCameraAdapter;
import com.ub.techexcel.adapter.MeetingMembersAdapter;
import com.ub.techexcel.bean.AgoraMember;
import com.ub.techexcel.bean.EventMuteAll;
import com.ub.techexcel.bean.EventNetworkFineChanged;
import com.ub.techexcel.bean.EventRoleChanged;
import com.ub.techexcel.bean.EventUnmuteAll;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.bean.TelePhoneCall;
import com.ub.techexcel.tools.AccompanyCreatePopup;
import com.ub.techexcel.tools.AccompanySelectVideoPopup;
import com.ub.techexcel.tools.DetchedPopup;
import com.ub.techexcel.tools.DevicesListDialog;
import com.ub.techexcel.tools.DownloadUtil;
import com.ub.techexcel.tools.ExitDialog;
import com.ub.techexcel.tools.FavoriteVideoPopup;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.MeetingMicroPhoneDialog;
import com.ub.techexcel.tools.MeetingServiceTools;
import com.ub.techexcel.tools.MeetingWarningDialog;
import com.ub.techexcel.tools.PopMeetingWebcamOptions;
import com.ub.techexcel.tools.PopPrivateCoachOperations;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;
import com.ub.techexcel.tools.UserSoundtrackDialog;
import com.ub.techexcel.tools.YinxiangCreatePopup;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import Decoder.BASE64Encoder;
import butterknife.Bind;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kloudsync.techexcel.bean.MeetingMember.TYPE_ITEM_MAIN_SPEAKER;

/**
 * Created by tonyan on 2019/11/19.
 */

public class DocAndMeetingActivity extends BaseWebActivity implements PopBottomMenu.BottomMenuOperationsListener,
        PopBottomFile.BottomFileOperationsListener, AddFileFromFavoriteDialog.OnFavoriteDocSelectedListener,
        BottomFileAdapter.OnDocumentClickListener, View.OnClickListener, AddFileFromDocumentDialog.OnDocSelectedListener,
        MeetingMembersAdapter.OnMemberClickedListener, AgoraCameraAdapterV2.OnCameraOptionsListener,
        FollowSpearkerModeManager.OnSpeakerAgoraStatusChanged, PopMeetingWebcamOptions.OnDisplayModeChanged,
        FollowSpearkerModeManager.OnSpeakerViewClickedListener, PopPrivateCoachOperations.OnCoachOperationsListener, BottomDocThumbnailAdapter.DocThumbnailListener {
    private final String TAG = DocAndMeetingActivity.class.getSimpleName().toString();

    private static final int REQUEST_CODE_CHOOSE = 23;
    public static final String SUNDTRACKBEAN = "sundtrackbean";
    public static MeetingConfig meetingConfig;
    public static MeetingPauseManager mMeetingPauseManager;
    private SocketMessageManager messageManager;
    //---
    private BottomMenuManager menuManager;
    private PopBottomFile bottomFilePop;
    private MeetingKit meetingKit;
    //---
    @Bind(R.id.layout_real_meeting)
    RelativeLayout meetingLayout;
    @Bind(R.id.layout_toggle_camera)
    LinearLayout toggleCameraLayout;
    @Bind(R.id.image_toggle_camera)
    ImageView toggleCameraImage;
    @Bind(R.id.image_select_speaker)
    ImageView selectSpeakerImage;

    @Bind(R.id.member_camera_list)
    RecyclerView cameraList;
    @Bind(R.id.full_camera_list)
    RecyclerView fullCameraList;
    @Bind(R.id.meeting_menu)
    ImageView meetingMenu;
    @Bind(R.id.layout_note)
    RelativeLayout noteLayout;
    @Bind(R.id.layout_full_camera)
    RelativeLayout fullCamereLayout;
    @Bind(R.id.icon_back_full_screen)
    ImageView backFullCameraImage;
    @Bind(R.id.icon_back_return_screen)
    ImageView returnCameraImage;
    @Bind(R.id.layout_vedio)
    RelativeLayout vedioLayout;
    @Bind(R.id.image_vedio_close)
    ImageView closeVedioImage;

    @Bind(R.id.layout_camera)
    RelativeLayout cameraLayout;

    @Bind(R.id.layout_meeting_default_document)
    RelativeLayout meetingDefaultDocument;

    @Bind(R.id.tv_doc_organiser_name)
    TextView organiserNameTxt;
    @Bind(R.id.txt_meeting_id)
    TextView meetingIdText;
    @Bind(R.id.tv_doc_and_meeting_progress)
    TextView mTvMeetingInProgress;
    @Bind(R.id.layout_remote_share)
    RelativeLayout remoteShareLayout;
    @Bind(R.id.frame_remote_share)
    FrameLayout remoteShareFrame;

    @Bind(R.id.layout_create_blank_page)
    LinearLayout createBlankPageLayout;

    @Bind(R.id.audiosyncll)
    LinearLayout audiosyncll;

    @Bind(R.id.timeshow)
    TextView timeshow;

    @Bind(R.id.recordstatus)
    ImageView recordstatus;

    @Bind(R.id.layout_role_host)
    LinearLayout roleHostLayout;

    @Bind(R.id.layout_role_member)
    LinearLayout roleMemberLayout;

    @Bind(R.id.layout_invite)
    LinearLayout inviteLayout;

    @Bind(R.id.layout_share)
    LinearLayout shareLayout;

    @Bind(R.id._layout_invite)
    LinearLayout _inviteLayout;

    @Bind(R.id._layout_share)
    LinearLayout _shareLayout;

    @Bind(R.id.txt_role)
    TextView roleText;

    @Bind(R.id.meeting_menu_member)
    ImageView meetingMenuMemberImage;

    @Bind(R.id.layout_note_container)
    LinearLayout noteContainer;

    @Bind(R.id.layout_soundtrack_play)
    RelativeLayout soundtrackPlayLayout;

    @Bind(R.id.sync_web)
    WebView syncWeb;

    @Bind(R.id.sondtrack_load_bar)
    ProgressBar soundtrackLoadingBar;

    @Bind(R.id.test_click)
    RelativeLayout clickLayout;
    private SoundtrackBean mSoundtrackBean;

    @Bind(R.id.layout_waiting_meeting)
    RelativeLayout waitingMeetingLayout;
    @Bind(R.id.image_meeting_hands_up)
    TextView handsUpImage;

    @Bind(R.id.view_root)
    RelativeLayout rootView;

    @Bind(R.id.layout_coach_with)
    RelativeLayout coachLayout;

    @Bind(R.id.layout_coach_audiences)
    RelativeLayout coachAudienceLayout;

    @Bind(R.id.talkallstudent)
    LinearLayout talkallstudent;

    AgoraCameraAdapterV2 cameraAdapter;
    FullAgoraCameraAdapter fullCameraAdapter;
    Gson gson;
    private SharedPreferences sharedPreferences;
    private SurfaceView surfaceView;
    private MeetingSettingCache meetingSettingCache;
    private boolean mIsMeetingPause;
    private Handler rejoinHandler;
    private static final int MESSAGE_JOIN_TIME = 1;
    private static final int MESSAGE_SHOW_HOST = 2;
    private static final int MESSAGE_CHECK_SELECTED_SPEAKER = 3;
    private static final int MESSAGE_INIT_CAMERA_ADAPTER = 4;
    private int joinTime = 0;
    private EventSendJoinMeetingMessage joinMeetingMessage;
    private String mTipsText;
    private long mPauseDuration;
    private volatile boolean mSwitchShowDocument;

    public static DocAndMeetingActivity instance = null;
    private Map<Integer, Integer> mShowDocPageMap = new HashMap<>();//保存切换文档前,该文档的当前文档页数

    private AgoraMember member;
    boolean mIsRequestFileList;//心跳返回消息中用来判断是否需要请求文件列表
    private BottomDocThumbnailDialog mBottomDocThumbnailDialog;

    /**
     * 是否处于视频会议
     */
    private boolean mIsVideoMeeting = false;

    /**
     * 2s后是否弹框麦克风
     */
    private boolean mIsMicroPhone = false;
    /**
     * 参会者弹框是否showing
     */
    private boolean mIsMemberDialogShow = false;

    /**
     * popmeetingmenu点击
     * 默认-1
     * 0 麦克风
     * 1 扬声器
     * 2 摄像头
     * 3 camera switch
     */
    private int mTypeClicked = -1;

    @Override
    public void showErrorPage() {

    }

    @Override
    public void initData() {
        boolean createSuccess = FileUtils.createFileSaveDir(this);
        if (!createSuccess) {
            Toast.makeText(getApplicationContext(), "文件系统异常，打开失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        instance = this;

        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        Log.e("DocAndMeetingActivity", "on_create");
        meetingSettingCache = MeetingSettingCache.getInstance(this);
        writeNoteBlankPageImage();
        initViews();
        //----
        RealMeetingSetting realMeetingSetting = MeetingSettingCache.getInstance(this).getMeetingSetting();
        meetingConfig = getConfig();
        mMeetingPauseManager = MeetingPauseManager.getInstance(this, meetingConfig);
        gson = new Gson();
        pageCache = DocumentPageCache.getInstance(this);
        //--
        menuManager = BottomMenuManager.getInstance(this, meetingConfig);
        menuManager.setBottomMenuOperationsListener(this);
        menuManager.setMenuIcon(menuIcon);
        initWeb();
        soundtrackPlayManager = new SoundtrackPlayManager(this, meetingConfig, soundtrackPlayLayout);
        bottomFilePop = new PopBottomFile(this, meetingConfig);
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        Log.e("check_system_type", "system_type:" + meetingConfig.getSystemType());
        messageManager = SocketMessageManager.getManager(DocAndMeetingActivity.this);
        messageManager.registerMessageReceiver();
        handleRejoinMeetingBecauseFailed();
        if (meetingConfig.getType() != MeetingType.MEETING) {
            messageManager.sendMessage_JoinMeeting(meetingConfig);
        } else {
            if (Tools.isOrientationPortrait(DocAndMeetingActivity.this)) {
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            //是meeting

            if (meetingConfig.getLessionId() == -1) {
                messageManager.sendMessage_JoinMeeting(meetingConfig);
                // 会议还没有开始
                return;
            }

            MeetingKit.getInstance().init(DocAndMeetingActivity.this, meetingConfig);
            if (meetingConfig.getRole() == MeetingConfig.MeetingRole.AUDIENCE) {
                showAudiencePromptDialog();
                messageManager.sendMessage_JoinMeeting(meetingConfig);
            } else {
                MeetingKit.getInstance().prepareJoin(DocAndMeetingActivity.this, meetingConfig);
            }
        }

        mSoundtrackBean = (SoundtrackBean) getIntent().getSerializableExtra(SUNDTRACKBEAN);

        MeetingKit.getInstance().setOnRefreshCompanyListener(new MeetingKit.OnRefreshCompanyListener() {
            @Override
            public void onRefreshCompany(String name) {
                organiserNameTxt.setText(name);
            }
        });
    }

    private void handleRejoinMeetingBecauseFailed() {
        rejoinHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int what = msg.what;
                if (what == MESSAGE_JOIN_TIME) {
                    Log.e("check_join_message", "time:" + joinTime);
                    joinTime++;
                    if (joinTime >= 8) {
                        if (joinMeetingMessage != null) {
                            joinTime = 0;
                            if (messageManager != null) {
                                if (TextUtils.isEmpty(joinMeetingMessage.getNewMeetingId())) {
                                    messageManager.sendMessage_JoinMeeting(meetingConfig);
                                } else {
                                    messageManager.sendMessage_startMeeting(meetingConfig, joinMeetingMessage.getNewMeetingId());
                                }
                                joinMeetingMessage = null;
                            }
                        }
                        rejoinHandler.removeMessages(MESSAGE_JOIN_TIME);

                    } else {
                        rejoinHandler.sendEmptyMessageDelayed(MESSAGE_JOIN_TIME, 1000);
                        Log.e("check_join_message", "send_delay");
                    }


                } else if (what == MESSAGE_SHOW_HOST) {
                    if (meetingConfig == null) {
                        return;
                    }

                    noSpeakingCount++;
                    Log.e("check_show_host", "MESSAGE_SHOW_HOST：" + noSpeakingCount);
                    if (noSpeakingCount >= 3) {
                        noSpeakingCount = 0;
                        rejoinHandler.removeMessages(MESSAGE_SHOW_HOST);
                        int _displayMode = meetingConfig.getCameraDiplayMode();
                        Log.e("check_show_host", "MESSAGE_SHOW_HOST：" + _displayMode);
                        if (_displayMode == 0) {
                            if (cameraAdapter != null) {
                                cameraAdapter.setSpeakerMember(getPresenterAgora());
                            }
                        }

                    } else {
                        rejoinHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_HOST, 1000);

                    }
                } else if (what == MESSAGE_CHECK_SELECTED_SPEAKER) {
                    if (meetingConfig == null) {
                        selectSpeakerCount = 0;
                        rejoinHandler.removeMessages(MESSAGE_CHECK_SELECTED_SPEAKER);
                    }
                    selectSpeakerCount++;
                    if (selectSpeakerCount > 5) {
                        selectSpeakerCount = 0;
                        rejoinHandler.removeMessages(MESSAGE_CHECK_SELECTED_SPEAKER);
                        int _displayMode = meetingConfig.getCameraDiplayMode();
                        if (_displayMode == 0) {
                            backToSpeaker();
                        }
                    } else {
                        rejoinHandler.sendEmptyMessageDelayed(MESSAGE_CHECK_SELECTED_SPEAKER, 1000);

                    }
                } else if (what == MESSAGE_INIT_CAMERA_ADAPTER) {
                    if (meetingConfig.getCameraDiplayMode() == -1) {
                        // 没有进入join_meeting 初始化
                        rejoinHandler.sendEmptyMessageDelayed(MESSAGE_INIT_CAMERA_ADAPTER, 1000);
                    } else {
                        rejoinHandler.removeMessages(MESSAGE_INIT_CAMERA_ADAPTER);
                        reloadAgoraMember();
                        justHandleDisplayMode(meetingConfig.getCameraDiplayMode());
                    }
                }
            }


        };
    }

    private void safeJoinMeetingIfAlreadyInMeeting() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /*创建音想弹窗*/
        if (yinxiangCreatePopup != null && meetingConfig != null && meetingConfig.getDocument() != null && yinxiangCreatePopup.isShowing()) {
            yinxiangCreatePopup.StartPop(web, meetingConfig.getDocument().getAttachmentID() + "");
        }
        /*选择音想背景音乐弹窗*/
        if (favoritePopup != null && favoritePopup.isShowing()) {
            favoritePopup.StartPop(web);
        }
        /*文件列表弹窗*/
        if (bottomFilePop != null && bottomFilePop.isShowing()) {
            bottomFilePop.show(web, this);
        }
        /*我的笔记弹窗*/
        if (notesDialog != null && notesDialog.isShowing()) {
            notesDialog.show(AppConfig.UserID, meetingConfig);
        }
        /*音想弹窗*/
        if (soundtrackDialog != null && soundtrackDialog.isShowing()) {
            soundtrackDialog.show(meetingConfig);
        }
        /*同步TV弹窗*/
        if (devicesListDialog != null && devicesListDialog.isShowing()) {
            devicesListDialog.show(null, false);
        }
        /*文件上传选择团队,空间,文件弹窗*/
        if (addFileFromDocumentDialog != null && addFileFromDocumentDialog.isShowing()) {
            addFileFromDocumentDialog.show();
        }
        /*文件上传收藏文档弹窗*/
        if (addFileFromFavoriteDialog != null && addFileFromFavoriteDialog.isShowing()) {
            addFileFromFavoriteDialog.show();
        }
        /*参会者无文档时点击屏幕中上传文档时出现的弹窗*/
        if (shareDocInMeetingDialog != null && shareDocInMeetingDialog.isShowing()) {
            shareDocInMeetingDialog.show();
        }
        /*分享弹窗*/
        if (shareDocumentDialog != null && shareDocumentDialog.isShow()) {
            shareDocumentDialog.show();
        }
        /*会议暂停界面*/
        RelativeLayout.LayoutParams layoutParams = null;
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Toast.makeText(DocAndMeetingActivity.this,"现在是竖屏", Toast.LENGTH_SHORT).show();
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = getResources().getDimensionPixelOffset(R.dimen.dp_70);
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(DocAndMeetingActivity.this,"现在是横屏", Toast.LENGTH_SHORT).show();
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            layoutParams.topMargin = getResources().getDimensionPixelOffset(R.dimen.dp_9);

        }
        mRlyMeetingPauseLayout.setLayoutParams(layoutParams);
    }

    AudiencePromptDialog audiencePromptDialog;

    private void showAudiencePromptDialog() {
        if (audiencePromptDialog != null) {
            if (audiencePromptDialog.isShowing()) {
                audiencePromptDialog.cancel();
                audiencePromptDialog = null;
            }
        }
        audiencePromptDialog = new AudiencePromptDialog(this);
        audiencePromptDialog.show();
    }

    private MeetingWarningDialog meetingWarningDialog;

    //开始会议警告信息
    private void openWarningInfo(final int type) {
        if (meetingWarningDialog != null) {
            if (meetingWarningDialog.isShowing()) {
                meetingWarningDialog.dismiss();
            }
            meetingWarningDialog = null;
        }
        meetingWarningDialog = new MeetingWarningDialog(this);
        meetingWarningDialog.setOnUserOptionsListener(new MeetingWarningDialog.OnUserOptionsListener() {
            @Override
            public void onUserStart() {
                if (type == 1) {
                    meetingKit = MeetingKit.getInstance();
                    meetingKit.prepareStart(DocAndMeetingActivity.this, meetingConfig, meetingConfig.getLessionId() + "");
                } else {
                    MeetingKit.getInstance().prepareJoin(DocAndMeetingActivity.this, meetingConfig);
                }
            }
        });

        meetingWarningDialog.show(this, meetingConfig);
    }

    private void writeNoteBlankPageImage() {
        File localNoteFile = new File(FileUtils.getBaseDir() + "note" + File.separator + "blank_note_1.jpg");
        if (localNoteFile.exists()) {
            return;
        }
        new File(FileUtils.getBaseDir() + "note").mkdirs();
        Observable.just(localNoteFile).observeOn(Schedulers.io()).doOnNext(new Consumer<File>() {
            @Override
            public void accept(File file) throws Exception {
                copyAssetsToDst("blank_note_1.jpg", file);
            }
        }).subscribe();

    }

    private void copyAssetsToDst(String srcPath, File dstPath) {
        try {
            InputStream is = getAssets().open(srcPath);
            Log.e("copy_file", "is:" + is);
            FileOutputStream fos = new FileOutputStream(dstPath);
            Log.e("copy_file", "fos:" + fos);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("copy_file", "Exception:" + e.getMessage());

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (bottomFilePop != null && bottomFilePop.isShowing()) {
//            bottomFilePop.hide();
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private ConnectionChangedListener connectionChangedListener = new ConnectionChangedListener();

    @Override
    public void displayModeChanged(int mode) {
        Log.e("handle_display_mode", "self_change_mode");
        handleDisplayMode(mode);
    }

    @Override
    public void onGoToVideoClicked() {
        handleFullScreenCamera(cameraAdapter, true);
    }


    @Override
    public void onSpeakerViewCliced(View view) {
        showWebcamOtions(view);
    }


    private class ConnectionChangedListener implements ConnectionClassManager.ConnectionClassStateChangeListener {
        @Override
        public void onBandwidthStateChange(ConnectionQuality connectionQuality) {
            if (connectionQuality == ConnectionQuality.POOR || connectionQuality == ConnectionQuality.UNKNOWN) {
                MeetingKit.getInstance().retSetConfigurationBaseonNetwork(true);
            } else {
                MeetingKit.getInstance().retSetConfigurationBaseonNetwork(false);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConnectionClassManager.getInstance().remove(connectionChangedListener);
    }

    @Override
    protected void onResume() {
        ConnectionClassManager.getInstance().register(connectionChangedListener);
        if (menuManager != null) {
            menuManager.setMenuIcon(menuIcon);
        }
        if (bottomFilePop != null && !bottomFilePop.isShowing()) {
            menuIcon.setImageResource(R.drawable.icon_menu);
            menuIcon.setEnabled(true);
        }
        Tools.keepSocketServiceOn(this);
        isUploadWeixinFile();
        super.onResume();
    }

    AddWxDocDialog addWxDocDialog;

    private void isUploadWeixinFile() {
        if (!TextUtils.isEmpty(AppConfig.wechatFilePath)) {
            if (addWxDocDialog != null) {
                addWxDocDialog.dismiss();
                addWxDocDialog = null;
            }
            addWxDocDialog = new AddWxDocDialog(this, AppConfig.wechatFilePath);
            addWxDocDialog.setInVisible();
            addWxDocDialog.setSavedListener(new OnDocSavedListener() {
                @Override
                public void onSaveSpace(String path) {

                }

                @Override
                public void onSaveFavorite(String path) {
                    AppConfig.wechatFilePath = "";
                    AddDocumentTool.addDocumentToFavorite(DocAndMeetingActivity.this, path, new DocumentUploadTool.DocUploadDetailLinstener() {
                        @Override
                        public void uploadStart() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (uploadFileDialog != null) {
                                        uploadFileDialog.cancel();
                                    }
                                    uploadFileDialog = new UploadFileDialog(DocAndMeetingActivity.this);
                                    uploadFileDialog.setTile("uploading");
                                    uploadFileDialog.show();
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
                        public void uploadFinished(final Object result) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                                        uploadFileDialog.cancel();
                                        if (result != null) {
                                            Document document = (Document) result;
                                            onFavoriteDocSelected(document.getItemID());
                                        }
                                    }
                                }
                            });
                        }

                        @Override
                        public void uploadError(final String message) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                                        uploadFileDialog.cancel();
                                    }
                                    String errorMessage = message;
                                    if (TextUtils.isEmpty(errorMessage)) {
                                        errorMessage = getString(R.string.operate_failure);
                                    }
                                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }

                @Override
                public void onCancel() {
                    AppConfig.wechatFilePath = "";
                }
            });
            addWxDocDialog.show();

        }
    }

    @SuppressLint("JavascriptInterface")
    private void initWeb() {
//        web.setZOrderOnTop(false);
        web.getSettings().setJavaScriptEnabled(true);
//        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        web.addJavascriptInterface(this, "AnalyticsWebInterface");

//        noteWeb.setZOrderOnTop(false);
        noteWeb.getSettings().setJavaScriptEnabled(true);
//        noteWeb.getSettings().setDomStorageEnabled(true);
        noteWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        noteWeb.addJavascriptInterface(new NoteJavascriptInterface(), "AnalyticsWebInterface");

//        XWalkPreferences.setValue("enable-javascript", true);
//        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
//        XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
//        XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);
        loadWebIndex();

    }

    private void loadWebIndex() {
        int deviceType = DeviceManager.getDeviceType(this);
        String indexUrl = "file:///android_asset/index.html";
        if (deviceType == SupportDevice.BOOK) {
            indexUrl += "?devicetype=4";
        }
        final String url = indexUrl;
        web.loadUrl(url, null);
        web.loadUrl("javascript:ShowToolbar(" + false + ")", null);
        web.loadUrl("javascript:Record()", null);

        noteWeb.loadUrl(url, null);
        noteWeb.loadUrl("javascript:ShowToolbar(" + false + ")", null);
        noteWeb.loadUrl("javascript:Record()", null);
    }

    private MeetingConfig getConfig() {
        Intent data = getIntent();
        if (meetingConfig == null) {
            meetingConfig = new MeetingConfig();
        }
        meetingConfig.setType(data.getIntExtra("meeting_type", MeetingType.DOC));
        meetingConfig.setMeetingId(data.getStringExtra("meeting_id"));
        meetingConfig.setLessionId(data.getIntExtra("lession_id", 0));
        int document_id = data.getIntExtra("document_id", 0);
        meetingConfig.setDocumentId(String.valueOf(document_id));
        meetingConfig.setRole(data.getIntExtra("meeting_role", MeetingConfig.MeetingRole.HOST));
        meetingConfig.setUserToken(UserData.getUserToken(this));
        meetingConfig.setFromMeeting(data.getBooleanExtra("from_meeting", false));
        meetingConfig.setSpaceId(getIntent().getIntExtra("space_id", 0));
        meetingConfig.setHost(getIntent().getBooleanExtra("is_host", false));
        return meetingConfig;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageManager != null) {
            messageManager.sendMessage_LeaveMeeting(meetingConfig);
            messageManager.release();
        }

        if (menuManager != null) {
            menuManager.release();
        }

        if (soundtrackRecordManager != null) {
            soundtrackRecordManager.release();
            soundtrackRecordManager = null;
            SoundtrackRecordManager.instance = null;
        }
        if (wakeLock != null) {
            wakeLock.release();
        }

        if (mFloatingWindowNoteManager != null) {
            mFloatingWindowNoteManager.closeFloating();
            mFloatingWindowNoteManager = null;
            FloatingWindowNoteManager.instance = null;
        }

        MeetingKit.getInstance().release();
        if (web != null) {
            web.removeAllViews();
            web.clearCache(true);
            web.destroy();
            web = null;
        }

        if (soundtrackPlayManager != null) {
            soundtrackPlayManager.followClose();
        }

        MeetingPauseManager.getInstance(this, meetingConfig).destory();
        if (meetingConfig != null) {
            meetingConfig.reset();
        }
        meetingConfig = null;
        instance = null;
    }

    private synchronized void getMeetingMembers(JSONArray users) {
        List<MeetingMember> allMembers = (List<MeetingMember>) new Gson().fromJson(users.toString(), new TypeToken<List<MeetingMember>>() {
        }.getType());
        List<MeetingMember> auditors = new ArrayList<>();
        List<MeetingMember> members = new ArrayList<>();
        for (MeetingMember member : allMembers) {
            if (member.getRole() == 3) {
                //旁听生
                auditors.add(member);
            } else {
                members.add(member);
            }

            if (member.getRole() == 2) {
                meetingConfig.setMeetingHostId(member.getUserId() + "");
            }

            if (member.getPresenter() == 1) {
                meetingConfig.setPresenterId(member.getUserId() + "");
            }
        }

        Log.e("getMeetingMembers", "members_size:" + members.size());
        meetingConfig.setMeetingAuditor(auditors);
        meetingConfig.setMeetingMembers(members);
//        EventRefreshMembers refreshMembers = new EventRefreshMembers();
//        refreshMembers.setMeetingConfig(meetingConfig);
//        EventBus.getDefault().post(refreshMembers);
    }

    private void requestDocumentsAndShowPage() {
        DocumentModel.asyncGetDocumentsInDocAndShowPage(meetingConfig, true);
    }

    private void requestDocuments() {
        DocumentModel.asyncGetDocumentsInDocAndShowPage(meetingConfig, false);
    }

    // ------- @Subscribe
    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void receiveDocuments(EventMeetingDocuments documents) {
        // 所有文档的data
        Log.e("receiverDocuemnts", "documents:" + documents);
        this.documents.clear();
        this.documents.addAll(documents.getDocuments());
        if (this.documents != null && this.documents.size() > 0) {
            int index = this.documents.indexOf(new MeetingDocument(meetingConfig.getFileId()));
            meetingConfig.setAllDocuments(this.documents);
            hideEnterLoading();
            Log.e("check_document_index", "index:" + index);
            if (index < 0) {
                if (meetingConfig.getType() == MeetingType.MEETING) {
                    meetingDefaultDocument.setVisibility(View.VISIBLE);
                    if (AppConfig.systemType == 0) {
                        meetingIdText.setText(getString(R.string._meeting_id) + meetingConfig.getMeetingId());
                        mTvMeetingInProgress.setText(R.string.miProgress);
                    } else {
                        meetingIdText.setText(getString(R.string._course_id) + meetingConfig.getMeetingId());
                        mTvMeetingInProgress.setText(R.string.course_in_progress);
                    }
                    handleMeetingDefaultDocument();
                } else {
                    index = 0;
                    meetingConfig.setDocument(this.documents.get(index));
                    downLoadDocumentPageAndShow();
                }

            } else {
                meetingConfig.setDocument(this.documents.get(index));
                downLoadDocumentPageAndShow();
            }

        } else {
            hideEnterLoading();
            menuIcon.setVisibility(View.VISIBLE);
            if (meetingConfig.getType() == MeetingType.MEETING) {
                meetingDefaultDocument.setVisibility(View.VISIBLE);
                if (AppConfig.systemType == 0) {
                    meetingIdText.setText(getString(R.string._meeting_id) + meetingConfig.getMeetingId());
                    mTvMeetingInProgress.setText(R.string.miProgress);
                } else {
                    meetingIdText.setText(getString(R.string._course_id) + meetingConfig.getMeetingId());
                    mTvMeetingInProgress.setText(R.string.course_in_progress);
                }
                handleMeetingDefaultDocument();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteDocuments(EventDeleteDocs deleteDocs) {
        int pageNumber = 1;
        int currentItemId = 0;
        if (deleteDocs.getItemId() == meetingConfig.getDocument().getItemID()) {//如果是当前正在演示的文档
            if (deleteDocs.getItemId() == documents.get(0).getItemID()) {//是文档列表的第一个
                if (documents.size() == 1) {//文档列表只有这一个文档
                    documents.clear();
                    if (bottomFilePop != null && bottomFilePop.isShowing()) {
                        bottomFilePop.setDocuments(this.documents, meetingConfig.getDocument().getItemID(), this);
                        bottomFilePop.removeTempDoc();
                    }
                    meetingConfig.setDocument(null);
                    meetingConfig.setCurrentDocumentPage(null);
                    meetingConfig.setPageNumber(1);
                    EventMeetingDocuments meetingDocuments = new EventMeetingDocuments();
                    meetingDocuments.setDocuments(documents);
                    receiveDocuments(meetingDocuments);
                    return;
                } else {
                    currentItemId = documents.get(1).getItemID();
                }
            } else {
                currentItemId = documents.get(0).getItemID();
            }
            DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, currentItemId, pageNumber);
        } else {//不是当前正在演示的文档
            MeetingDocument document = null;
            for (int i = 0; i < documents.size(); i++) {
                if (documents.get(i).getItemID() == deleteDocs.getItemId()) {
                    document = documents.remove(i);
                }
            }
            if (bottomFilePop != null && bottomFilePop.isShowing()) {
                bottomFilePop.setDocuments(this.documents, meetingConfig.getDocument().getItemID(), this);
                bottomFilePop.removeTempDoc();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshDocuments(EventRefreshDocs refreshDocs) {
        // 所有文档的data
        Log.e("refreshDocuments", "documents:" + documents);
        mIsRequestFileList = false;
        this.documents = refreshDocs.getDocuments();
        if (mIsMeetingPause && !mSwitchShowDocument) {//如果是会议暂停并且不是自己添加文档则不自动切换文档
            if (bottomFilePop != null && bottomFilePop.isShowing()) {
                bottomFilePop.setDocuments(this.documents, meetingConfig.getDocument().getItemID(), this);
                bottomFilePop.removeTempDoc();
            }
            return;
        }
        mSwitchShowDocument = false;
        if (meetingConfig.getType() == MeetingType.MEETING) {
            if (isPresenter()) {
                changeDocument(documents.get(documents.indexOf(new MeetingDocument(refreshDocs.getItemId()))), refreshDocs.getPageNumber());
            }
        } else {
            changeDocument(documents.get(documents.indexOf(new MeetingDocument(refreshDocs.getItemId()))), refreshDocs.getPageNumber());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDocumentPage(DocumentPage page) {
        Log.e("showDocumentPage", "page:" + page);
        hideEnterLoading();
        MeetingDocument document = getDocument(page);
        Log.e("showDocumentPage", "current_document:" + document);
        if (document != null) {
            meetingConfig.setDocument(document);
            meetingConfig.setCurrentDocumentPage(page);
            meetingConfig.setPageNumber(meetingConfig.getDocument().getDocumentPages().indexOf(page) + 1);
        }

        //notify change file
        notifyDocumentChanged();
        meetingDefaultDocument.setVisibility(View.GONE);
        web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        web.loadUrl("javascript:ShowPDF('" + page.getShowingPath() + "'," + (page.getPageNumber()) + ",''," + meetingConfig.getDocument().getAttachmentID() + "," + false + ")", null);
        web.loadUrl("javascript:Record()", null);
        if (bottomFilePop != null && bottomFilePop.isShowing()) {
            bottomFilePop.setDocuments(this.documents, meetingConfig.getDocument().getItemID(), this);
            bottomFilePop.removeTempDoc();
        } else {
            menuIcon.setVisibility(View.VISIBLE);
        }
    }

    private boolean isAudience() {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            return false;
        }
        return meetingConfig.getRole() == MeetingConfig.MeetingRole.AUDIENCE;
    }

    private boolean isPresenter() {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            return false;
        }
        return meetingConfig.getPresenterId().equals(AppConfig.UserID);
    }


    private AgoraMember getPresenterAgora() {
        if (!TextUtils.isEmpty(meetingConfig.getPresenterId())) {
            if (meetingConfig.getAgoraMembers() != null && meetingConfig.getAgoraMembers().size() > 0) {
                for (AgoraMember presenterAgora : meetingConfig.getAgoraMembers()) {
                    if ((presenterAgora.getUserId() + "").equals(meetingConfig.getPresenterId())) {
                        return presenterAgora;
                    }
                }
            }
        }

        return null;
    }

    private AgoraMember getSelectedAgora(String userId) {

        if (meetingConfig.getAgoraMembers() != null && meetingConfig.getAgoraMembers().size() > 0) {
            for (AgoraMember selectedAgora : meetingConfig.getAgoraMembers()) {
                if ((selectedAgora.getUserId() + "").equals(userId)) {
                    return selectedAgora;
                }
            }
        }

        return null;
    }

    private long currentNoteId;

    class TempNoteData {
        private String data;
        private long noteId;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public long getNoteId() {
            return noteId;
        }

        public void setNoteId(long noteId) {
            this.noteId = noteId;
        }
    }

    private CopyOnWriteArrayList<TempNoteData> newNoteDatas = new CopyOnWriteArrayList<>();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showNotePage(final EventShowNotePage page) {
        Log.e("showNotePage", "page:" + page);
//        totalHideCameraAdapter();
        if (!TextUtils.isEmpty(page.getNotePage().getLocalFileId())) {
            if (page.getNotePage().getLocalFileId().contains(".")) {
                currentNoteId = page.getNoteId();
                noteWeb.setVisibility(View.VISIBLE);
                String localNoteBlankPage = FileUtils.getBaseDir() + "note" + File.separator + "blank_note_1.jpg";
                Log.e("show_PDF", "javascript:ShowPDF('" + localNoteBlankPage + "'," + (page.getNotePage().getPageNumber()) + ",''," + page.getAttachmendId() + "," + true + ")");
                noteWeb.loadUrl("javascript:ShowPDF('" + localNoteBlankPage + "'," + (page.getNotePage().getPageNumber()) + ",''," + page.getAttachmendId() + "," + true + ")", null);
                noteWeb.loadUrl("javascript:Record()", null);
                handleBluetoothNote(page.getNotePage().getPageUrl());
                return;
            }
        }

        noteWeb.setVisibility(View.VISIBLE);
        noteWeb.loadUrl("javascript:ShowPDF('" + page.getNotePage().getShowingPath() + "'," + (page.getNotePage().getPageNumber()) + ",''," + page.getAttachmendId() + "," + true + ")", null);
        noteWeb.loadUrl("javascript:Record()", null);

    }


    private void totalHideCameraAdapter() {
        if (cameraList.getVisibility() == View.VISIBLE) {
            toggleMembersCamera(true);
        }
    }

    JSONObject lastjsonObject = new JSONObject();

    private void handleBluetoothNote(final String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Observable.just(url).observeOn(Schedulers.io()).map(new Function<String, String>() {
            @Override
            public String apply(String url) throws Exception {
                String newUrl = "";
                URL _url = new URL(url);
                Log.e("check_url_path", _url.getPath());
                String path = _url.getPath();
                if (!TextUtils.isEmpty(path)) {
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }

                    int index = path.lastIndexOf("/");
                    if (index >= 0 && index < path.length()) {
                        String centerPart = path.substring(0, index);
                        String fileName = path.substring(index + 1, path.length());
                        Log.e("check_transform_url", "centerPart:" + centerPart + ",fileName:" + fileName);
                        if (!TextUtils.isEmpty(centerPart)) {
                            JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
                                    centerPart);
                            if (queryDocumentResult != null) {
                                Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
                                String part = "";
                                if (uploadao != null) {
                                    if (1 == uploadao.getServiceProviderId()) {
                                        part = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + centerPart
                                                + "/" + fileName;
                                    } else if (2 == uploadao.getServiceProviderId()) {
                                        part = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + centerPart + "/" + fileName;
                                    }
                                    url = part;
                                    Log.e("check_transform_url", "url:" + url);
                                }

                            }
                        }
                    }
                }

                int checkIndex = url.lastIndexOf("/");
                if (checkIndex > 0 && checkIndex < url.length() - 2) {
                    newUrl = url.substring(0, checkIndex + 1) + "book_page_data.json";
                }
                return newUrl;
            }
        }).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(String url) throws Exception {
                JSONObject jsonObject = new JSONObject();
                if (!TextUtils.isEmpty(url)) {
                    Log.e("check_url", "url:" + url);
                    jsonObject = ServiceInterfaceTools.getinstance().syncGetNotePageJson(url);
                    try {
                        lastjsonObject = jsonObject.getJSONObject("PaintData");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return jsonObject;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                String key = "ShowDotPanData";
                JSONObject _data = new JSONObject();
                _data.put("LinesData", jsonObject);
                _data.put("ShowInCenter", false);
                _data.put("TriggerEvent", false);
                Log.e("ShowDotPanData", "ShowDotPanData");
                noteWeb.loadUrl("javascript:FromApp('" + key + "'," + _data + ")", null);
                RecordNoteActionManager.getManager(DocAndMeetingActivity.this).sendDisplayHomePageActions(currentNoteId, lastjsonObject);
            }
        }).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if (!newNoteDatas.isEmpty()) {
                    Observable.fromIterable(newNoteDatas).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<TempNoteData>() {
                        @Override
                        public void accept(TempNoteData tempNoteData) throws Exception {
                            Log.e("draw_new_note", "temp_note_note");
                            if (tempNoteData.getNoteId() == currentNoteId) {
                                String key = "ShowDotPanData";
                                JSONObject _data = new JSONObject();
                                _data.put("LinesData", tempNoteData.getData());
                                _data.put("ShowInCenter", true);
                                _data.put("TriggerEvent", true);
                                noteWeb.loadUrl("javascript:FromApp('" + key + "'," + _data + ")", null);
                            }
                            newNoteDatas.remove(tempNoteData);
                        }
                    }).subscribe();
                }
            }
        }).observeOn(Schedulers.io()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetNoteP1Item(currentNoteId);
                if (result.has("code")) {
                    if (result.getInt("code") == 0) {
                        JSONArray dataArray = result.getJSONArray("data");
                        Observable.just(dataArray).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONArray>() {
                            @Override
                            public void accept(JSONArray _jsonArray) throws Exception {
                                for (int i = 0; i < _jsonArray.length(); ++i) {
                                    JSONObject data = _jsonArray.getJSONObject(i);
                                    addLinkBorderForDTNew(data);
                                }

                            }
                        }).subscribe();


                    }
                }
            }
        }).subscribe();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void showNote(EventNote note) {
        Note _note = note.getNote();
        Log.e("show_note", "note:" + _note);
        if (meetingConfig.getType() == MeetingType.MEETING) {
            noteUsersLayout.setVisibility(View.GONE);
        } else {
            noteUsersLayout.setVisibility(View.VISIBLE);
        }
        //TODO
        NoteViewManager.getInstance().setContent(this, noteLayout, _note, noteWeb, meetingConfig, noteContainer);
        notifyViewNote(note.getNote());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void showSelectedNote(EventSelectNote selectNote) {
        Observable.just(selectNote).observeOn(Schedulers.io()).doOnNext(new Consumer<EventSelectNote>() {
            @Override
            public void accept(EventSelectNote selectNote) throws Exception {
                JSONObject response = ServiceInterfaceTools.getinstance().syncImportNote(meetingConfig, selectNote);
                if (response != null && response.has("RetCode")) {
                    if (response.getInt("RetCode") == 0) {
                        selectNote.setNewLinkId(response.getInt("RetData"));
                    }
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<EventSelectNote>() {
            @Override
            public void accept(EventSelectNote selectNote) throws Exception {
                if (selectNote.getLinkId() > 0) {
                    deleteNote(selectNote.getLinkId());
                }
                if (selectNote.getNewLinkId() > 0) {
                    drawNote(selectNote.getNewLinkId(), selectNote.getLinkProperty(), 0);
                }
            }
        }).subscribe();
    }


    public synchronized void followShowNote(int noteId) {
        if (meetingConfig.getType() == MeetingType.MEETING) {
            noteUsersLayout.setVisibility(View.GONE);
        } else {
            noteUsersLayout.setVisibility(View.GONE);
        }
        Log.e("followShowNote", "noteid:" + noteId);
        hideEnterLoading();
        //TODO
        NoteViewManager.getInstance().followShowNote(this, noteLayout, noteWeb, noteId, meetingConfig, menuIcon, noteContainer);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDocumentIfRequestNoteError(EventNoteErrorShowDocument showDocument) {
        requestDocumentsAndShowPage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showMenuIcon(EventShowMenuIcon showMenuIcon) {
        if (menuIcon != null) {
            Log.e("showMenuIcon", "show");
            menuIcon.setVisibility(View.VISIBLE);
            menuIcon.setImageResource(R.drawable.icon_menu);
            menuIcon.setEnabled(true);
            Log.e("showMenuIcon", "menu visible:  " + (menuIcon.getVisibility() == View.VISIBLE));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exit(EventExit exit) {
        Log.e("event_bus", "exit");
        handleExit(exit.isEnd());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void receiveSocketMessage(EventSocketMessage socketMessage) {
        Log.e("DocAndMeetingActivity", "socket_message:" + socketMessage);
        String action = socketMessage.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }

        switch (action) {
            case SocketMessageManager.MESSAGE_LEAVE_MEETING:
                handleMessageLeaveMeeting(socketMessage.getData());
                break;

            case SocketMessageManager.MESSAGE_JOIN_MEETING:
                handleMessageJoinMeeting(socketMessage.getData());
                break;

            case SocketMessageManager.MESSAGE_BROADCAST_FRAME:

                if (socketMessage.getData() == null) {
                    return;
                }

                if (socketMessage.getData().has("data")) {
                    try {
                        String _frame = Tools.getFromBase64(socketMessage.getData().getString("data"));
                        if (noteLayout.getVisibility() == View.VISIBLE) {
                            if (noteWeb != null) {
                                noteWeb.loadUrl("javascript:PlayActionByTxt('" + _frame + "','" + 1 + "')", null);
                            }
                        } else {
                            if (web != null) {
                                web.loadUrl("javascript:PlayActionByTxt('" + _frame + "','" + 1 + "')", null);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case SocketMessageManager.MESSAGE_SEND_MESSAGE:
                if (socketMessage.getData() == null) {
                    return;
                }

                if (socketMessage.getData().has("data")) {
                    try {
                        handleMessageSendMessage(new JSONObject(Tools.getFromBase64(socketMessage.getData().getString("data"))));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case SocketMessageManager.MESSAGE_MAKE_PRESENTER:
                if (socketMessage.getData() == null) {
                    return;
                }

                try {
                    if (socketMessage.getData().has("presenterId")) {
                        meetingConfig.setPresenterId(socketMessage.getData().getString("presenterId"));
                    }
                    if (socketMessage.getData().has("presenterSessionId")) {
                        meetingConfig.setPresenterSessionId(socketMessage.getData().getString("presenterSessionId"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (MeetingMember member : meetingConfig.getMeetingMembers()) {
                    if ((member.getUserId() + "").equals(meetingConfig.getPresenterId())) {
                        member.setPresenter(1);
                    } else {
                        member.setPresenter(0);
                    }
                }

                EventRefreshMembers refreshMembers = new EventRefreshMembers();
                refreshMembers.setMeetingConfig(meetingConfig);
                EventBus.getDefault().post(refreshMembers);
                break;
            case SocketMessageManager.ATTACHMENT_UPLOADED_WITH_CHANGE_NUMBER://有人上传了文档
                if (socketMessage.getData() == null) return;
                handleMessageAttchmentUploaded(socketMessage.getData());
                break;
            case SocketMessageManager.ATTACHMENT_REMOVED://有人删除了文档
                if (socketMessage.getData() == null) return;
                handleMessageAttchmentDelete(socketMessage.getData());
                break;
            case SocketMessageManager.MESSAGE_END_MEETING:
                finish();
                break;
            case SocketMessageManager.MESSAGE_MEMBER_LIST_CHANGE:
                if (socketMessage.getData() != null) {
                    JSONObject data = socketMessage.getData();
                    Log.e("check_list_changed", "data:" + data);
                    if (data.has("retData")) {
                        JSONObject retData = data.optJSONObject("retData");
                        if (retData != null) {
                            try {
                                int type = retData.getInt("type");
                                int value = retData.getInt("value");
                                switch (type) {
                                    case 7:
                                        if (value == 1) {//可讲话参会者
                                            try {
                                                JSONArray jsonArray = retData.getJSONArray("affectedUserList");
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    String str = jsonArray.get(i).toString();
                                                    if (AppConfig.UserID.equals(str)) {
                                                        showMicroPhoneDialog();
                                                        break;
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    case 17:
                                        // 麦克风状态变化
                                        if (value == 5) {
                                            // 主持人关闭麦克风
                                            Log.e("check_list_changed", "mute_audio");
                                            MeetingKit.getInstance().menuMicroClicked(false);
                                            MeetingKit.getInstance().refreshMeetingMenu();
                                        } else if (value == 6) {
                                            MeetingKit.getInstance().menuMicroClicked(true);
                                            MeetingKit.getInstance().refreshMeetingMenu();
                                        }
                                        break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                MeetingKit.getInstance().requestMeetingMembers(meetingConfig, false);
                break;
            case SocketMessageManager.MESSAGE_AGORA_STATUS_CHANGE:
                handleMessageAgoraStatusChange(socketMessage.getData());
                break;
            case SocketMessageManager.MESSAGE_OPEN_OR_CLOSE_NOTE:
                if (socketMessage.getData().has("retData")) {
                    openOrCloseNote(socketMessage);
                }
                break;
            case SocketMessageManager.MESSAGE_NOTE_DATA:  // 浮窗或主界面正在展示的场景下
                if (socketMessage.getData().has("retData")) {
                    try {
                        JSONObject retData = socketMessage.getData().getJSONObject("retData");
                        String noteData = retData.getString("data");
                        int noteId = retData.getInt("noteId");
                        if (currentNoteId != noteId) {
                            TempNoteData _noteData = new TempNoteData();
                            _noteData.setData(Tools.getFromBase64(noteData));
                            _noteData.setNoteId(noteId);
                            newNoteDatas.add(_noteData);
                            if (noteLayout.getVisibility() == View.VISIBLE) {
                                if (noteWeb != null) {
                                    followShowNote(noteId);
                                }
                            } else {
                                if (mFloatingWindowNoteManager != null) {
                                    if (mFloatingWindowNoteManager.isShowing()) {
                                        mFloatingWindowNoteManager.setOldNoteId((int) currentNoteId);
                                        showNoteFloatingDialog(noteId);  //换个笔记了
                                    }
                                }
                            }
                        } else {  // 同一个笔记
                            if (noteLayout.getVisibility() == View.VISIBLE) {
                                if (noteWeb != null) {
                                    lastjsonObject = new JSONObject(Tools.getFromBase64(noteData));
                                    NoteViewManager.getInstance().followPaintLine(noteData);
                                    RecordNoteActionManager.getManager(this).sendDrawActions(noteId, noteData);
                                }
                            } else {
                                if (mFloatingWindowNoteManager != null) {
                                    if (mFloatingWindowNoteManager.isShowing()) {
                                        mFloatingWindowNoteManager.followDrawNewLine(noteId, noteData);
                                        RecordNoteActionManager.getManager(this).sendDrawActions(noteId, noteData);
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case SocketMessageManager.MESSAGE_NOTE_CHANGE:
                if (socketMessage.getData().has("retData")) {
                    try {
                        int noteId = socketMessage.getData().getJSONObject("retData").getInt("noteId");
                        if (noteLayout.getVisibility() == View.VISIBLE) {
                            if (noteWeb != null) {
                                followShowNote(noteId);
                            }
                        } else {
                            if (mFloatingWindowNoteManager != null) {
                                if (mFloatingWindowNoteManager.isShowing()) {
                                    mFloatingWindowNoteManager.setOldNoteId((int) currentNoteId);
                                    showNoteFloatingDialog(noteId);  //换个笔记了
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case SocketMessageManager.MESSAGE_NOTE_P1_CREATEAD:
                if (socketMessage.getData().has("retData")) {
                    try {
                        JSONObject p1Created = socketMessage.getData().getJSONObject("retData");
                        addLinkBorderForDTNew(p1Created);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case SocketMessageManager.MESSAGE_HELLO:
                if (socketMessage.getData().has("data")) {
                    try {
                        String helloJson = Tools.getFromBase64(socketMessage.getData().getString("data"));
//                        handleAgoraStatusInHelloMessage(helloJson);
                        HelloMessage helloMessage = gson.fromJson(helloJson,
                                HelloMessage.class);
                        handleMessageHelloMessage(helloMessage, helloJson);
                        Log.e("check_hello_message", "hello_message:" + helloMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case SocketMessageManager.MESSAGE_MEETING_STATUS:
                JSONObject meetingStatusObj = socketMessage.getData();

                if (!meetingConfig.isHost()) {
                    handleMessageMeetingStatus(meetingStatusObj);
                }

                if (meetingStatusObj.has("status")) {
                    try {
                        int status = meetingStatusObj.getInt("status");
                        meetingConfig.setMeetingStatus(status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //{"action":"MEETING_STATUS","retCode":"1","meetingId":"M11","status":"1","changeNumber":"3"}
                break;

            case SocketMessageManager.MEETING_CHANGE:
                handleMessageMeetingChange(socketMessage.getData());
                break;

            case SocketMessageManager.MEETING_MEMBER_SPEAKING:
                noSpeakingCount = 0;
                handleMemberSpeakingMessage(socketMessage.getData());
                break;
            case SocketMessageManager.MEETING_USER_JOIN_MEETING_ON_OTHER_DEVICE:
//                Intent intent = new Intent();
//                noSpeakingCount = 0;
//                intent.setAction("show_exit_meeting_dialog");
//                sendBroadcast(intent);
//                finish();
                break;
            case SocketMessageManager.MEETING_DISPLAY_SPEAKING_CAMERA:
                noSpeakingCount = 0;
                rejoinHandler.removeMessages(MESSAGE_SHOW_HOST);
                rejoinHandler.obtainMessage(MESSAGE_SHOW_HOST).sendToTarget();
                if (!isSelectSpeaker) {
                    handleMemberSpeakingMessage(socketMessage.getData());
                }
                break;

            case SocketMessageManager.MEETING_JOIN_PRIVATE_COACHING_AS_STUDENT:  //所有人收到  (成为1v1对象)
                if (meetingConfig.isMeetingPause()) {
                    JSONObject coachingAsStudetData = socketMessage.getData();
                    if (coachingAsStudetData.has("retData")) {
                        try {
                            JSONObject retData = coachingAsStudetData.getJSONObject("retData");
                            int hostUserId = retData.getInt("hostUserId");
                            int inviteUserId = retData.getInt("inviteUserId");

                            PrivateCoachManager.getManager(DocAndMeetingActivity.this).updatePrivateCoachInfo(meetingConfig, mMeetingPauseManager, cameraAdapter,
                                    messageManager, inviteUserId, hostUserId, coachLayout, soundtrackPlayManager);
                            justHandleDisplayMode(0);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                break;
            case SocketMessageManager.MEETING_INVITE_TO_PRIVATE_COACHING_AS_AUDIENCE:  //所有人收到  邀请成为参会者
                if (meetingConfig.isCoaching()) {
                    if (socketMessage.getData() == null) {
                        return;
                    }
                    JSONObject coachingAsStudetData = socketMessage.getData();
                    if (coachingAsStudetData.has("retData")) {
                        try {
                            JSONObject retData = coachingAsStudetData.getJSONObject("retData");
                            if (retData.has("inviteUserId")) {
                                int inviteUserId = retData.getInt("inviteUserId");
                                if (cameraAdapter != null) {
                                    int index = meetingConfig.getAgoraMembers().indexOf(new AgoraMember(inviteUserId));
                                    if (index >= 0) {
                                        cameraAdapter.setCoachToBeAudience(meetingConfig.getAgoraMembers().get(index));
                                    }
                                }
                                if (inviteUserId == Integer.parseInt(AppConfig.UserID)) {
                                    showInVitedCoachAudienceDialog();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case SocketMessageManager.MEETING_REFUSE_PRIVATE_COACHING_INVITATION:  // 所有人收到 旁听生拒绝成为参会者
                //{"changeNumber":38,"retCode":0,"action":"REFUSE_PRIVATE_COACHING_INVITATION","retData":"225017025"}
                if (meetingConfig.isCoaching()) {
                    if (socketMessage.getData() == null) {
                        return;
                    }
                    if (cameraAdapter != null) {
                        JSONObject coachingAsStudetData = socketMessage.getData();
                        String retData = null;
                        try {
                            retData = coachingAsStudetData.getString("retData");
                            cameraAdapter.updateUser(Integer.parseInt(retData));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case SocketMessageManager.MEETING_REMOVED_FROM_PRIVATE_COACHING_BY_HOST:  // 被老师移除私教（被移除人收到）
                if (meetingConfig.isCoaching()) {
                    if (socketMessage.getData() == null) {
                        return;
                    }
                    PrivateCoachManager.getManager(this).removeMemberCoach(meetingConfig, cameraAdapter, mMeetingPauseManager, Integer.parseInt(AppConfig.UserID));
                }
                break;
            case SocketMessageManager.MEETING_AUDIENCE_LEAVE_PRIVATE_COACHING:  // 旁听主动离开private coaching
                if (meetingConfig.isCoaching()) {
                    if (socketMessage.getData() == null) {
                        return;
                    }
                    JSONObject coachingAsStudetData = socketMessage.getData();
                    int leaveUserId = 0;
                    try {
                        leaveUserId = coachingAsStudetData.getInt("retData");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    PrivateCoachManager.getManager(this).removeMemberCoach(meetingConfig, cameraAdapter, mMeetingPauseManager, leaveUserId);
                }
                break;
            case SocketMessageManager.MEETING_PRIVATE_COACHING_SYNC_STATUS_UPDATED:  // 旁听主动离开private coaching
                if (meetingConfig.isCoaching()) {
                    if (socketMessage.getData() == null) {
                        return;
                    }
                    JSONObject coachingAsStudetData = socketMessage.getData();
                    int status = 0;
                    try {
                        status = coachingAsStudetData.getInt("retData");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    PrivateCoachManager.getManager(this).updateSync(mMeetingPauseManager, status);
                }
                break;
            default:
                break;
        }
    }


    BeInvitedCoachAudienceDialog beInvitedCoachAudienceDialog;

    private void showInVitedCoachAudienceDialog() {
        if (beInvitedCoachAudienceDialog != null) {
            beInvitedCoachAudienceDialog.cancel();
        }
        beInvitedCoachAudienceDialog = new BeInvitedCoachAudienceDialog(this);
        beInvitedCoachAudienceDialog.show(meetingConfig);
    }

    private int noSpeakingCount;
    private int selectSpeakerCount;


    private void handleMemberSpeakingMessage(JSONObject data) {
        if (meetingConfig.getCameraDiplayMode() != 0) {
            return;
        }
        if (data != null) {
            if (data.has("retData")) {
                try {
                    String userid = data.getString("retData");
                    if (meetingConfig.getType() == MeetingType.MEETING) {
                        for (AgoraMember agoraMember : meetingConfig.getAgoraMembers()) {
                            if ((agoraMember.getUserId() + "").equals(userid)) {
                                if (cameraAdapter != null) {
                                    selectSpeakerImage.setImageResource(R.drawable.user_info_expanded);
                                    cameraAdapter.changeDisplayType(0, agoraMember);
                                    cameraAdapter.setSpeakerMember(agoraMember);
                                    if (mIsVideoMeeting) {
                                        EventShowFullAgora showFullAgora = new EventShowFullAgora();
                                        showFullAgora.setAgoraMember(agoraMember);
                                        EventBus.getDefault().post(showFullAgora);
                                    }

                                }

                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private AgoraMember findAgoraMember(int userId, List<AgoraMember> agoraMembers) {
        AgoraMember agoraMember = null;
        for (int i = 0; i < agoraMembers.size(); i++) {
            AgoraMember agoraMember1 = agoraMembers.get(i);
            if (agoraMember1.getUserId() == userId) {
                agoraMember = agoraMember1;
                break;
            }
        }
        return agoraMember;
    }

    private void handleMemberVideoMessage(JSONObject data) {
        if (data != null) {
            if (data.has("retData")) {
                try {
                    String userid = data.getString("retData");
                    List<AgoraMember> agoraMembers = meetingConfig.getAgoraMembers();
                    AgoraMember agoraMember = findAgoraMember(Integer.parseInt(userid), agoraMembers);
                    EventShowFullAgora showFullAgora = new EventShowFullAgora();
                    showFullAgora.setAgoraMember(agoraMember);
                    EventBus.getDefault().post(showFullAgora);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleAgoraStatusInHelloMessage(String json) {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            return;
        }

        Log.e("check_hello_json", "hello_json:" + json);
        try {
            JSONObject helloJson = new JSONObject(json);
            if (helloJson.has("role")) {
                int role = helloJson.getInt("role");

                int currentRole = meetingConfig.getRole();

                meetingConfig.setRole(role);

                if (meetingSettingCache == null) {
                    meetingSettingCache = MeetingSettingCache.getInstance(this);
                }

                if (role == MeetingConfig.MeetingRole.MEMBER || role == MeetingConfig.MeetingRole.HOST) {
                    Log.e("check_hello_json", "is:1");
                    // 是主讲人
                    if (helloJson.has("microphoneStatus")) {
                        int microphoneStatus = helloJson.getInt("microphoneStatus");
                        if (microphoneStatus != 2) {
                            if (meetingSettingCache.getMeetingSetting().isMicroOn()) {
                                MeetingKit.getInstance().menuMicroClicked(false);
                            }

                        }
                    }

                    if (helloJson.has("cameraStatus")) {
                        int cameraStatus = helloJson.getInt("cameraStatus");
                        if (cameraStatus != 2) {
                            MeetingKit.getInstance().menuCameraClicked(false);
                        }
                    }

                } else {
                    Log.e("check_hello_json", "is:1");
                    if (meetingSettingCache.getMeetingSetting().isMicroOn()) {
                        MeetingKit.getInstance().disableAudioAndVideoStream();
                    }
//                    MeetingKit.getInstance().enableAudioAndVideo();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static final int TAG_HAVE_HANDS_UP = 1;
    private static final int TAG_NO_HANDS_UP = 2;

    private void handleMessageHelloMessage(HelloMessage helloMessage, String data) {
        if (helloMessage == null) {
            return;
        }

        if (meetingConfig.getType() == MeetingType.MEETING) {

            meetingConfig.setMeetingStatus(helloMessage.getCurrentStatus());

            if (meetingConfig.getMeetingStatus() == 0) {
                // 等待会议开启的状态
                return;
            }


            JSONObject jsonData = null;
            try {
                jsonData = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            meetingMenu.setVisibility(View.VISIBLE);
            //---处理笔记
            if (meetingConfig.getMode() == 0) {
                shareScreen = null;
                if (!TextUtils.isEmpty(helloMessage.getPrevDocInfo()) && helloMessage.getNoteId() > 0) {
                    // 心跳显示处于查看笔记
                    if (noteLayout.getVisibility() != View.VISIBLE) {
                        followShowNote((int) helloMessage.getNoteId());
                    }
                } else {
                    if (noteLayout.getVisibility() == View.VISIBLE) {
                        noteWeb.loadUrl("javascript:ClearPath()", null);
                        noteWeb.setVisibility(View.GONE);
                        noteLayout.setVisibility(View.GONE);
                    }
                }
            }

            /*处理文档*/
            if (!helloMessage.isIfPause() && meetingConfig.getDocument() != null && helloMessage.getCurrentItemId() != meetingConfig.getDocument().getItemID()) {
                changeDocument((int) helloMessage.getCurrentItemId(), helloMessage.getCurrentPageNumber());
            }

            /*比较文档列表版本号是否与服务器一致*/
            if (!helloMessage.isIfPause()) {
                int difference = helloMessage.getDocumentListChangeNumber() - meetingConfig.getChangeNumber();
                Log.e(TAG, "helloChangeNumber: " + helloMessage.getDocumentListChangeNumber() + "_currentChangeNumber: " + meetingConfig.getChangeNumber() + "_difference: " + difference);
                if (Math.abs(difference) > 0 && !mIsRequestFileList) {
                    mIsRequestFileList = true;
                } else if (mIsRequestFileList && Math.abs(difference) > 0) {
                    DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, (int) helloMessage.getCurrentItemId(), helloMessage.getCurrentPageNumber());
                }
                if (Math.abs(difference) > 1) {
                    DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, (int) helloMessage.getCurrentItemId(), helloMessage.getCurrentPageNumber());
                }
            }

            // ---处理presenter
            if (!TextUtils.isEmpty(helloMessage.getCurrentPresenter()) && !TextUtils.isEmpty(AppConfig.UserID)) {
                meetingConfig.justSetPresenterId(helloMessage.getCurrentPresenter());
                if (helloMessage.getCurrentPresenter().equals(AppConfig.UserID) || mIsMeetingPause) {
                    web.loadUrl("javascript:ShowToolbar(" + true + ")", null);
                    web.loadUrl("javascript:Record()", null);
                    noteWeb.loadUrl("javascript:ShowToolbar(" + true + ")", null);
                    noteWeb.loadUrl("javascript:Record()", null);
                    Log.e("check_presenter", "ShowToolbar_in_hello");

                    //自己是presenter
                } else {
                    web.loadUrl("javascript:ShowToolbar(" + false + ")", null);
                    web.loadUrl("javascript:StopRecord()", null);
                    noteWeb.loadUrl("javascript:ShowToolbar(" + false + ")", null);
                    noteWeb.loadUrl("javascript:StopRecord()", null);
                    Log.e("check_presenter", "HideToolbar_in_hello");
                }
            }

            if (meetingConfig.getMode() != 3) {
                // 不是屏幕共享
                if (remoteShareLayout.getVisibility() == View.VISIBLE || remoteShareFrame.getVisibility() == View.VISIBLE) {
                    if (shareScreen == null) {
                        closeShareScreen(new EventCloseShare());
                    }

                }
            }

            // 处理role

            if (jsonData != null && jsonData.has("role")) {
                meetingConfig.setRole(jsonData.optInt("role"));
                if (jsonData.has("handStatus")) {
                    meetingConfig.setHandStatus(jsonData.optInt("handStatus"));
                }
            }

            if (meetingConfig.getRole() != MeetingConfig.MeetingRole.HOST && meetingConfig.getRole() != MeetingConfig.MeetingRole.MEMBER) {
                // 自己不是host也不是主讲人
                if (menuIcon.getVisibility() == View.VISIBLE && meetingConfig.getMeetingStatus() != 0) {
                    MeetingKit.getInstance().disableAudioAndVideoStream();
//                    menuIcon.setVisibility(View.GONE);
//                    meetingMenuMemberImage.setVisibility(View.VISIBLE);
                }

            } else {

                if (meetingConfig.getMeetingStatus() != 0) {
//                    if (meetingMenuMemberImage.getVisibility() == View.VISIBLE) {
//                        menuIcon.setVisibility(View.VISIBLE);
//                        meetingMenuMemberImage.setVisibility(View.GONE);
//                        handsUpImage.setVisibility(View.GONE);
//                    }
                    if (menuIcon.getVisibility() == View.GONE) {
                       /* menuIcon.setVisibility(View.VISIBLE);
                        meetingMenuMemberImage.setVisibility(View.GONE);*/
                        handsUpImage.setVisibility(View.GONE);
                    }
                }
            }

            if (meetingConfig.getRole() == MeetingConfig.MeetingRole.AUDIENCE) {

                handsUpImage.setVisibility(View.VISIBLE);
                if (meetingConfig.getHandStatus() == 1) {
                    // 已举手
                    handsUpImage.setVisibility(View.VISIBLE);
                    handsUpImage.setText(getString(R.string.wxf_hand_up));
                    handsUpImage.setTag(TAG_HAVE_HANDS_UP);
                } else {
                    handsUpImage.setVisibility(View.VISIBLE);
                    handsUpImage.setText(getString(R.string.wxf_request_speaker));
                    handsUpImage.setTag(TAG_NO_HANDS_UP);
                }
            } else {
                handsUpImage.setText(getString(R.string.wxf_request_speaker));
                handsUpImage.setVisibility(View.GONE);
            }

            // handle soundtrack

            String audioData = helloMessage.getPlayAudioData();
            if (!meetingConfig.getPresenterId().equals(AppConfig.UserID) && !mIsMeetingPause) {
                if (TextUtils.isEmpty(audioData)) {
                    // 没有soundtrack的信息
                    if (soundtrackPlayLayout.getVisibility() == View.VISIBLE) {
                        soundtrackPlayManager.followClose();
                    }
                }

                try {
                    if (!TextUtils.isEmpty(audioData)) {
                        JSONObject audioJson = new JSONObject(Tools.getFromBase64(audioData));
                        handleSountrackMessages(audioJson);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            // handle 自己声网的状态

            if (meetingSettingCache.getMeetingSetting().isMicroOn()) {
                if (helloMessage.getMicrophoneStatus() != 2) {
                    messageManager.sendMessage_AgoraStatusChange(meetingConfig, true, meetingSettingCache.getMeetingSetting().isCameraOn());
                }
            } else {
                if (helloMessage.getMicrophoneStatus() == 2) {
                    messageManager.sendMessage_AgoraStatusChange(meetingConfig, false, meetingSettingCache.getMeetingSetting().isCameraOn());
                }
            }

            if (meetingSettingCache.getMeetingSetting().isCameraOn()) {
                if (helloMessage.getCameraStatus() != 2) {
                    messageManager.sendMessage_AgoraStatusChange(meetingConfig, meetingSettingCache.getMeetingSetting().isMicroOn(), true);
                }
            } else {
                if (helloMessage.getCameraStatus() == 2) {
                    messageManager.sendMessage_AgoraStatusChange(meetingConfig, meetingSettingCache.getMeetingSetting().isMicroOn(), false);
                }
            }

            /*会议暂停与恢复*/
            if (helloMessage.isIfPause() && !mIsMeetingPause) {
                mIsMeetingPause = helloMessage.isIfPause();
                meetingConfig.setMeetingPause(mIsMeetingPause);
                String tipsText = Tools.getFromBase64(helloMessage.getPauseMsg());
                long pauseDuration = helloMessage.getPauseDuration();
                MeetingPauseManager.getInstance(this, meetingConfig).setTipInfo(tipsText);
                MeetingPauseManager.getInstance(DocAndMeetingActivity.this, meetingConfig).setPauseTime(pauseDuration);
                MeetingPauseManager.getInstance(this, meetingConfig).showBigLayout();
                handleWebUISetting();
                if (soundtrackPlayManager != null) {
                    soundtrackPlayManager.changeSeekbarStatusByRole();
                }
            } else if (!helloMessage.isIfPause() && mIsMeetingPause) {
                mIsMeetingPause = helloMessage.isIfPause();
                meetingConfig.setMeetingPause(mIsMeetingPause);
                MeetingPauseManager.getInstance(this, meetingConfig).hide();
                handleWebUISetting();
                if (bottomFilePop != null && bottomFilePop.isShowing()) {
                    bottomFilePop.hide();
                }
                if (soundtrackPlayManager != null) {
                    soundtrackPlayManager.changeSeekbarStatusByRole();
                }
            }

            // ------- display mode
            if (jsonData != null && jsonData.has("cameraDiplayMode")) {
                try {
                    int displayMode = jsonData.getInt("cameraDiplayMode");
                    int _displayMode = sharedPreferences.getInt("display_mode", 0);
                    Log.e("check_hello", "hello_mode:" + displayMode + ",save_mode:" + _displayMode);
                    if (displayMode != _displayMode) {
                        sharedPreferences.edit().putInt("display_mode", displayMode).commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // ----处理private coach

            if (jsonData.has("privateCoachingMeeting")) {
                final JSONObject coachingMeeting;
                try {
                    coachingMeeting = jsonData.getJSONObject("privateCoachingMeeting");
                    if (coachingMeeting.has("status")) {
                        if (coachingMeeting.getInt("status") == 2) { //进行中
                            if (!meetingConfig.isCoaching()) {
                                handleJoinPrivateCoachingMeeting(coachingMeeting.getJSONArray("privateCoachingMembers"));
                            } else {
                                adjustPrivateCoachingInfoInHelloMessage(coachingMeeting.getJSONArray("privateCoachingMembers"));
                            }
                        } else if (coachingMeeting.getInt("status") == 0) {
                            if (meetingConfig.isCoaching()) {
                                meetingConfig.setCoaching(false);
                                if (cameraAdapter != null) {
                                    cameraAdapter.notifyDataSetChanged();
                                }
                                MeetingPauseManager.getInstance(this, meetingConfig).hide();
                                handleWebUISetting();
                                if (bottomFilePop != null && bottomFilePop.isShowing()) {
                                    bottomFilePop.hide();
                                }

                                if (soundtrackPlayManager != null) {
                                    soundtrackPlayManager.changeSeekbarStatusByRole();
                                }
                            }
                        }
                    }
                    if (coachingMeeting.has("currentItemId")) {
                        int itemId = coachingMeeting.getInt("currentItemId");
                        if (meetingConfig.meIsCoachingMember()) {
                            followChangeDocument(itemId, coachingMeeting.getInt("currentPageNumber"));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    private void adjustPrivateCoachingInfoInHelloMessage(JSONArray privateCoachingMembers) {
        boolean hasAudience = false;
        if (privateCoachingMembers != null && privateCoachingMembers.length() > 0) {

            for (int i = 0; i < privateCoachingMembers.length(); ++i) {
                try {
                    JSONObject _member = privateCoachingMembers.getJSONObject(i);
                    int role = _member.getInt("role");
                    if (role == 1) {
                        // 学生
                        if (meetingConfig.getPrivateCoachMember().getUserId() != Integer.parseInt(_member.getString("userId"))) {
                            meetingConfig.setPrivateCoachMember(Integer.parseInt(_member.getString("userId")));
                            if (mMeetingPauseManager != null) {
                                mMeetingPauseManager.initCoachLayout(coachLayout, meetingConfig);
                            }
                            if (cameraAdapter != null) {
                                cameraAdapter.setCoachStudent(new AgoraMember(Integer.parseInt(_member.getString("userId"))));
                            }
                        }
                        int syncStatus = _member.getInt("syncStatus");
                        PrivateCoachManager.getManager(this).updateSync(mMeetingPauseManager, syncStatus);
                    } else if (role == 3) {
                        // 旁听加入
                        hasAudience = true;
                        meetingConfig.coachAudiences(_member.getString("userId"), _member.getBoolean("active"));
                        if (_member.getBoolean("active")) {
                            if (cameraAdapter != null) {
                                cameraAdapter.setCoachAudience(_member.getString("userId"));
                            }
                        }
                    } else if (role == 2) {
                        //  老师
                    }

                    MeetingKit.getInstance().handleMemberAgoraWhenCoaching();
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
            Log.e(TAG, "hasAudience:" + hasAudience);
            if (!hasAudience) {
                meetingConfig.getPrivateCoachAudiences().clear();
            }
            if (mMeetingPauseManager != null) {
                mMeetingPauseManager.initAudiencesLayout(coachAudienceLayout, meetingConfig, getResources().getString(R.string.prompt_joined_private_channel));
            }
        }
    }

    private void handleJoinPrivateCoachingMeeting(JSONArray privateCoachingMembers) {
        Log.e(TAG, "privateCoachingMembers:" + privateCoachingMembers);
        meetingConfig.setCoaching(true);
        if (privateCoachingMembers != null && privateCoachingMembers.length() > 0) {
            for (int i = 0; i < privateCoachingMembers.length(); ++i) {
                try {
                    JSONObject _member = privateCoachingMembers.getJSONObject(i);
                    if (_member.getInt("role") == 1) {
                        // 学生
                        meetingConfig.setPrivateCoachMember(Integer.parseInt(_member.getString("userId")));
                        if (cameraAdapter != null) {
                            cameraAdapter.setCoachStudent(new AgoraMember(Integer.parseInt(_member.getString("userId"))));
                        }
                        int syncStatus = _member.getInt("syncStatus");
                        PrivateCoachManager.getManager(this).updateSync(mMeetingPauseManager, syncStatus);
                    } else if (_member.getInt("role") == 3) {
                        if (cameraAdapter != null) {
                            cameraAdapter.setCoachAudience(_member.getString("userId"));
                        }
                        meetingConfig.coachAudiences(_member.getString("userId"), _member.getBoolean("isActive"), true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (mMeetingPauseManager != null) {
                mMeetingPauseManager.initAudiencesLayout(coachAudienceLayout, meetingConfig, getResources().getString(R.string.prompt_joined_private_channel));
            }
        }
        if (mMeetingPauseManager != null) {
            mMeetingPauseManager.initCoachLayout(coachLayout, meetingConfig);
        }
        MeetingKit.getInstance().handleMemberAgoraWhenCoaching();

    }

    private DetchedPopup detchedPopup;

    private void detectPopwindow() {

        Log.e("ddd", meetingConfig.getMeetingHostId() + "  " + meetingConfig.getMeetingMembers().size());
        if (meetingConfig.getMeetingHostId().equals(AppConfig.UserID)) {
            if (meetingConfig.getMeetingMembers().size() == 1) {
                if (detchedPopup == null) {
                    detchedPopup = new DetchedPopup();
                    detchedPopup.getPopwindow(DocAndMeetingActivity.this, rootView, 0);
                    detchedPopup.setDetchedPopupListener(new DetchedPopup.DetchedPopupListener() {
                        @Override
                        public void closeNow() {
                            detchedPopup.dismiss();
                            handleExit(true);

                        }
                    });
                    detchedPopup.StartPop(web);
                }
            }
        }

    }


    private void handleSountrackMessages(JSONObject audioJson) {

        if (audioJson == null) {
            return;
        }

        if (soundtrackPlayManager == null) {
            return;
        }

        if (soundtrackPlayManager != null) {
            if (soundtrackPlayManager.isLoading()) {
                return;
            }
        }

        if (meetingConfig.isMeetingPause()) {  //会议暂停中
            if (meetingConfig.isCoaching()) {
                if (!meetingConfig.meIsCoachingMember()) {
                    return;
                }
            }
        }

        try {
            if (audioJson.has("stat")) {
                int status = audioJson.getInt("stat");
                if (status == 0) {
                    if (soundtrackPlayLayout.getVisibility() == View.VISIBLE) {
                        soundtrackPlayManager.followClose();
                    }
                    return;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            Log.e("check_hello_soundtrack", "audioJson:" + audioJson);
            if (audioJson.has("stat")) {
                int status = audioJson.getInt("stat");
                if (status == 2 || status == 6) {
                    long time = 0;
                    if (audioJson.has("time")) {
                        time = audioJson.getLong("time");
                    }
                    // 心跳里面是暂停
                    if (soundtrackPlayLayout.getVisibility() != View.VISIBLE) {
                        soundtrackPlayManager.followClose();
                        soundtrackPlayManager.initLoading(menuIcon);
                        SoundTrack soundTrack = new SoundTrack();
                        soundTrack.setSoundtrackID(audioJson.getInt("soundtrackId"));
                        requestDetailAndPause(soundTrack, time);
                    } else {
                        Log.e("check_hello_soundtrack", "audioJson:" + audioJson + ",is playing:" + soundtrackPlayManager.isPlaying());
                        if (soundtrackPlayManager.isPlaying()) {
                            soundtrackPlayManager.followPause(time);
                        } else {
                            if (Math.abs(soundtrackPlayManager.getPlayTime() - time) > 2000) {
                                soundtrackPlayManager.followPause(time);
                            }
                        }
                    }


                } else if (status == 4 || status == 5 || status == 1) {
                    //跟随播放
                    long time = 0;
                    if (audioJson.has("time")) {
                        time = audioJson.getLong("time");
                    }
                    if (soundtrackPlayLayout.getVisibility() != View.VISIBLE) {
                        // TODO
                        soundtrackPlayManager.followClose();
                        soundtrackPlayManager.initLoading(menuIcon);
                        SoundTrack soundTrack = new SoundTrack();
                        soundTrack.setSoundtrackID(audioJson.getInt("soundtrackId"));
                        requestDetailAndPlay(soundTrack, time);
                    } else {
                        if (!soundtrackPlayManager.isPlaying()) {
                            soundtrackPlayManager.followRestart(time);
                        } else {
                            soundtrackPlayManager.followTheProgress(time);
                        }
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addLinkBorderForDTNew(JSONObject p1Created) throws JSONException {
        if (p1Created.has("noteId")) {
            if (currentNoteId == p1Created.getInt("noteId")) {
//                noteWeb.load("javascript:whiteboard");
                JSONArray positionArray = new JSONArray(p1Created.getString("position"));
                Log.e("addLinkBorderForDTNew", "positionArray:" + positionArray);
                JSONObject info = new JSONObject();
                info.put("ProjectID", p1Created.getInt("projectId"));
                info.put("TaskID", p1Created.getInt("itemId"));
                for (int i = 0; i < positionArray.length(); ++i) {
                    JSONObject position = positionArray.getJSONObject(i);
                    doDrawDTNewBorder(position.getInt("left"), position.getInt("top"), position.getInt("width"), position.getInt("height"), info);
                }
            }
        }
    }

    private void doDrawDTNewBorder(int x, int y, int width, int height, JSONObject info) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("type", 40);
        message.put("CW", 678);
        message.put("x", x);
        message.put("y", y);
        message.put("width", width);
        message.put("height", height);
        message.put("info", info);

        JSONObject clearLastMessage = new JSONObject();
        clearLastMessage.put("type", 40);

        Log.e("doDrawDTNewBorder", "border_PlayActionByTxt:" + message);
        noteWeb.loadUrl("javascript:PlayActionByTxt('" + message + "')", null);
    }

    @Subscribe
    public void receiveEventClose(EventClose close) {
        Log.e("receiveEventClose", "close");
        finish();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receivePageActions(EventPageActions pageActions) {
        String data = pageActions.getData();
        if (!TextUtils.isEmpty(data)) {
            if (pageActions.getPageNumber() == meetingConfig.getPageNumber()) {
                Log.e("check_play_txt", "PlayActionByArray:" + data);
                web.loadUrl("javascript:PlayActionByArray(" + data + "," + 0 + ")", null);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveNotePageActions(EventNotePageActions pageActions) {
        String data = pageActions.getData();
        if (!TextUtils.isEmpty(data)) {
            if (noteLayout.getVisibility() == View.VISIBLE) {
                noteWeb.loadUrl("javascript:PlayActionByArray(" + data + "," + 0 + ")", null);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receivePageNotes(EventPageNotes pageNotes) {
        Log.e("receivePageNotes", "page_notes:" + pageNotes);
        List<NoteDetail> notes = pageNotes.getNotes();
        if (notes != null && notes.size() > 0) {
            if (pageNotes.getPageNumber() == meetingConfig.getPageNumber()) {

                for (NoteDetail note : notes) {
                    try {
                        JSONObject message = new JSONObject();
                        message.put("type", 38);
                        message.put("LinkID", note.getLinkID());
                        message.put("IsOther", false);
                        if (!TextUtils.isEmpty(note.getLinkProperty())) {
                            message.put("LinkProperty", new JSONObject(note.getLinkProperty()));
                        }
                        Log.e("check_play_txt", "notes_PlayActionByTxt:" + message);
                        web.loadUrl("javascript:PlayActionByTxt('" + message + "')", null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }
    }


    private boolean hasInitDisplayMode = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showMemberCamera(AgoraMember member) {

        if (!hasInitDisplayMode) {
            hasInitDisplayMode = true;
            rejoinHandler.obtainMessage(MESSAGE_INIT_CAMERA_ADAPTER).sendToTarget();
        }

        if ((member.getUserId() + "").equals(AppConfig.UserID) && member.isAdd()) {
            //自己开启会议成功
            if (documents != null && documents.size() > 0) {
                notifyDocumentChanged();
            }
        }

//        if (member.isAdd()) {
//            meetingConfig.addAgoraMember(member);
//        } else {
//            //delete user
//            meetingConfig.deleteAgoraMember(member);
//        }

        checkAgoraMemberNameAndAgoraStatus();
        Log.e("check_init", "init_adatper1");
        initCameraAdatper();

        checkAgoraMemberNameAndAgoraStatus();
        Log.e("showMemeberCamera", "member:" + member);

        if (cameraAdapter != null) {
            cameraAdapter.setOnCameraOptionsListener(this);
        }

        Log.e("check_send_agora_status", "user_id:" + AppConfig.UserID + ",agora_id:" + member.getUserId());
        if ((member.getUserId() + "").equals(AppConfig.UserID)) {
            if (meetingConfig.getType() != MeetingType.MEETING) {
                return;
            }

            boolean isMicroOn = MeetingSettingCache.getInstance(this).getMeetingSetting().isMicroOn();
            boolean isCameraOn = MeetingSettingCache.getInstance(this).getMeetingSetting().isCameraOn();
            if (meetingConfig.getRole() == MeetingConfig.MeetingRole.HOST || meetingConfig.getRole() == MeetingConfig.MeetingRole.MEMBER) {
                messageManager.sendMessage_AgoraStatusChange(meetingConfig, isMicroOn, isCameraOn);
                delayLoadMembersAndRefresh(member);

                return;
            }
        }

        refreshAgoraMember(member);


    }

    private void delayLoadMembersAndRefresh(final AgoraMember agoraMember) {


        Observable.just("delay_load").delay(500, TimeUnit.MILLISECONDS).observeOn(Schedulers.io()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {

                if (meetingConfig == null) {
                    return;
                }

                JSONObject result = ServiceInterfaceTools.getinstance().syncGetMeetingMembers(meetingConfig.getMeetingId(), MeetingConfig.MeetingRole.MEMBER);
                if (result.has("code")) {
                    if (result.getInt("code") == 0) {
                        Log.e("delay_load", "one:" + agoraMember);
                        List<MeetingMember> members = new Gson().fromJson(result.getJSONArray("data").toString(), new TypeToken<List<MeetingMember>>() {
                        }.getType());
                        if (members != null) {
                            for (MeetingMember member : members) {

                                if (member.getRole() == 2) {
                                    meetingConfig.setMeetingHostId(member.getUserId() + "");
                                }

                                if (member.getPresenter() == 1) {
                                    meetingConfig.setPresenterId(member.getUserId() + "");
                                    meetingConfig.setPresenterSessionId(member.getSessionId() + "");
                                }
                            }
                            Collections.sort(members);
                            meetingConfig.setMeetingMembers(members);
                        }

                        for (MeetingMember member : meetingConfig.getMeetingMembers()) {
                            if ((member.getUserId() + "").equals(AppConfig.UserID)) {
                                agoraMember.setUserName(member.getUserName());
                                agoraMember.setIconUrl(member.getAvatarUrl());
                                Log.e("check_audio", "audio_status1:" + member.getMicrophoneStatus());
                                agoraMember.setMuteVideo(member.getCameraStatus() != 2);
                                agoraMember.setMuteAudio(member.getMicrophoneStatus() != 2);
                                agoraMember.setIsMember(1);
                            }
                        }
                        Log.e("delay_load", "two:" + agoraMember);
                        if (cameraAdapter != null) {
                            Log.e("delay_load", "three");
                            Observable.just("refresh_main").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    cameraAdapter.refreshMyAgoraStatus(agoraMember);
                                }
                            });

                        }
                    }
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (meetingConfig == null) {
                    return;
                }

                if (mIsMeetingPause) {
                    MeetingPauseManager pauseManager = MeetingPauseManager.getInstance(DocAndMeetingActivity.this, meetingConfig);
                    pauseManager.setTipInfo(mTipsText);
//                    pauseManager.setPauseTime(mPauseDuration);
                    pauseManager.showBigLayout();
                    handleWebUISetting();
                }
            }
        }).subscribe();
    }

    private EventShareScreen shareScreen;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showShareScreen(EventShareScreen shareScreen) {
        Log.e("showShareScreen", "show_screen");
        this.shareScreen = shareScreen;
        remoteShareLayout.setVisibility(View.VISIBLE);
        if (remoteShareFrame.getChildCount() == 0) {
            ViewParent parent = shareScreen.getShareView().getParent();
            if (parent != null) {
                ((FrameLayout) parent).removeView(shareScreen.getShareView());
            }
        }
        MeetingKit.getInstance().setShareScreenStream(shareScreen);
        remoteShareFrame.removeAllViews();
        remoteShareFrame.addView(shareScreen.getShareView(), new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        reloadAgoraMember();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeShareScreen(EventCloseShare closeShare) {
        SurfaceView surfaceView = (SurfaceView) remoteShareFrame.getChildAt(0);
        if (surfaceView != null) {
            surfaceView.setVisibility(View.GONE);
        }
        remoteShareFrame.removeAllViews();
        remoteShareLayout.setVisibility(View.GONE);
        shareScreen = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void inviteUsers(EventInviteUsers inviteUsers) {
        messageManager.sendMessage_InviteToMeeting(meetingConfig, inviteUsers.getUsers());
        MeetingKit.getInstance().requestMeetingMembers(meetingConfig, false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadNodeSuccess(NoteId noteId) {
        Log.e("event_bus", "draw_note_by_id:" + noteId);
        if (noteId.getLinkID() == 0) {
            return;
        }
        deleteTempNote();
        drawNote(noteId.getLinkID(), meetingConfig.getCurrentLinkProperty(), 0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventMuteAllMembersAudio(EventMuteAll muteAll) {
        MeetingKit.getInstance().menuMicroClicked(false);
        SocketMessageManager.getManager(this).sendMessage_MuteStatus(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventUnmuteAllMembersAudio(EventUnmuteAll unmuteAll) {
        MeetingKit.getInstance().menuMicroClicked(true);
        SocketMessageManager.getManager(this).sendMessage_MuteStatus(1);
    }

    private void drawNote(int linkId, JSONObject linkProperty, int isOther) {
        JSONObject noteData = new JSONObject();
        try {
            noteData.put("type", 38);
            noteData.put("LinkID", linkId);
            noteData.put("IsOther", isOther);
            noteData.put("LinkProperty", linkProperty);
            Log.e("drawNote", "note:" + noteData.toString());
            if (web != null) {
                web.loadUrl("javascript:PlayActionByTxt('" + noteData + "')", null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void drawTempNote() {
        drawNote(-1, meetingConfig.getCurrentLinkProperty(), 0);
    }

    private void deleteNote(int linkId) {
        String url = AppConfig.URL_PUBLIC + "DocumentNote/RemoveNote?linkIDs=" + linkId;
        JSONObject noteData = new JSONObject();
        try {
            noteData.put("type", 102);
            noteData.put("id", "BooXNote_" + linkId);
            Log.e("deleteTempNote", "note:" + noteData.toString());
            if (web != null) {
                web.loadUrl("javascript:PlayActionByTxt('" + noteData + "')", null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServiceInterfaceTools.getinstance().removeNote(url, ServiceInterfaceTools.REMOVENOTE, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {

            }
        });
    }

    private void deleteTempNote() {
        deleteNote(-1);
    }

    public void refreshAgoraMember(AgoraMember member) {

        Log.e("refreshAgoraMember", "member:" + member + ",camera_adapter:" + cameraAdapter);
        if (cameraAdapter != null) {
            if (member.isAdd()) {
                if (member.getIsMember() == 1) {
                    cameraAdapter.addUser(member);
                } else {
                    cameraAdapter.removeUser(member);
                }

            } else {
                cameraAdapter.removeUser(member);
            }
        }

        if (fullCameraList.getVisibility() == View.VISIBLE) {
            if (fullCameraAdapter != null) {
                if (member.isAdd()) {
                    if (member.getIsMember() == 1) {
                        fullCameraAdapter.addUser(member);
                    } else {
                        fullCameraAdapter.removeUser(member);
                    }
                } else {
                    fullCameraAdapter.removeUser(member);
                }
            }
        }
    }

    private void initCameraAdatper() {
        if (cameraAdapter == null) {
            List<AgoraMember> copyMembers = new ArrayList<>();
            for (AgoraMember _member : meetingConfig.getAgoraMembers()) {
                if (_member.getIsMember() == 1) {
                    if (!copyMembers.contains(_member)) {
                        copyMembers.add(_member);
                    }
                }
            }

            cameraAdapter = new AgoraCameraAdapterV2(this);
            cameraAdapter.setMeetingConfig(meetingConfig);
            String sizeStr = sharedPreferences.getString("speaker_size_mode", "small");
            int size = 1;
            if (sizeStr.equals("small")) {
                size = 1;
            } else if (sizeStr.equals("big")) {
                size = 2;
            } else if (sizeStr.equals("large")) {
                size = 3;
            }

            cameraAdapter.setViewType(size);
            cameraAdapter.setMembers(copyMembers);
            cameraAdapter.setOnCameraOptionsListener(this);
            cameraAdapter.initSpeakerMember(getPresenterAgora());
//            fitCameraList();
            cameraList.setAdapter(cameraAdapter);
            MeetingKit.getInstance().setCameraAdapter(cameraAdapter);


        }

        if (fullCameraAdapter == null) {
            List<AgoraMember> copyMembers = new ArrayList<>();
            for (AgoraMember _member : meetingConfig.getAgoraMembers()) {
                if (_member.getIsMember() == 1) {
                    if (!copyMembers.contains(_member)) {
                        copyMembers.add(_member);
                    }
                }
            }

            fullCameraAdapter = new FullAgoraCameraAdapter(this, meetingConfig);
            fullCameraAdapter.setMembers(copyMembers);
            fitFullCameraList(copyMembers);
            fullCameraList.setAdapter(fullCameraAdapter);
            MeetingKit.getInstance().setFullCameraAdapter(fullCameraAdapter);
        }

    }

    private void reloadAgoraMember() {
        List<AgoraMember> copyMembers = new ArrayList<>();
        for (AgoraMember member : meetingConfig.getAgoraMembers()) {
            member.setSelect(false);
            if (member.getIsMember() == 1) {
                if (!copyMembers.contains(member)) {
                    copyMembers.add(member);
                }
            }
        }

        if (cameraList.getVisibility() == View.VISIBLE) {
            if (cameraAdapter != null) {
                cameraAdapter.reset();
                cameraAdapter = null;
            }

            cameraAdapter = new AgoraCameraAdapterV2(this);
            cameraAdapter.setMeetingConfig(meetingConfig);
            String sizeStr = sharedPreferences.getString("speaker_size_mode", "small");
            int size = 1;
            if (sizeStr.equals("small")) {
                size = 1;
            } else if (sizeStr.equals("big")) {
                size = 2;
            } else if (sizeStr.equals("large")) {
                size = 3;
            }
            cameraAdapter.setViewType(size);
            cameraAdapter.setMembers(copyMembers);
            cameraAdapter.setOnCameraOptionsListener(this);
//            fitCameraList();
            cameraList.setAdapter(cameraAdapter);
            MeetingKit.getInstance().setCameraAdapter(cameraAdapter);
        }

        if (fullCameraList.getVisibility() == View.VISIBLE) {
            if (fullCameraAdapter != null) {
                fullCameraAdapter.reset();
                fullCameraAdapter = null;
            }
            fullCameraAdapter = new FullAgoraCameraAdapter(this, meetingConfig);
            fullCameraAdapter.setMembers(copyMembers);
            fitFullCameraList(copyMembers);
            fullCameraList.setAdapter(fullCameraAdapter);
            MeetingKit.getInstance().setFullCameraAdapter(fullCameraAdapter);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void highLightNote(final EventHighlightNote note) {

        if (note.getPageNumber() == meetingConfig.getPageNumber()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("LinkID", note.getNote().getLinkID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String key = "TwinkleBookNote";
            web.loadUrl("javascript:FromApp('" + key + "'," + jsonObject + ")", null);

        } else {

//            if (note.getNote().getAttachmentID() != meetingConfig.getDocument().getAttachmentID()) {
//                Log.e("highLightNote", "in_different_document");
//                final int index = documents.indexOf(new MeetingDocument(note.getNote().getDocumentItemID()));
//                changeDocument(note.getNote().getDocumentItemID(), note.getPageNumber());
//
//            } else

            {
                Log.e("highLightNote", "in_same_document");
                final String notifyUrl = meetingConfig.getNotifyUrl();
                if (TextUtils.isEmpty(notifyUrl)) {
                    return;
                }
                if (note.getPageNumber() - 1 < 0 || note.getPageNumber() > meetingConfig.getDocument().getPageCount()) {
                    return;
                }
                final DocumentPage page = meetingConfig.getDocument().getDocumentPages().get(note.getPageNumber() - 1);
                if (page == null) {
                    return;
                }
                final String pathLocalPath = notifyUrl.substring(0, notifyUrl.lastIndexOf("<")) +
                        note.getPageNumber() + notifyUrl.substring(notifyUrl.lastIndexOf("."));
                if (!TextUtils.isEmpty(page.getPageUrl())) {
                    DocumentPage _page = pageCache.getPageCache(page.getPageUrl());
                    Log.e("check_cache_page", "_page:" + _page + "，page:" + page);

                    if (_page != null && page.getPageUrl().equals(_page.getPageUrl())) {
                        if (!TextUtils.isEmpty(_page.getSavedLocalPath())) {
                            File localFile = new File(_page.getSavedLocalPath());
                            if (localFile.exists()) {
                                if (!pathLocalPath.equals(localFile.getAbsolutePath())) {
                                    if (localFile.renameTo(new File(pathLocalPath))) {
                                        Log.e("highLightNote", "uncorrect_file_name,rename");
                                        notifyWebFilePrepared(notifyUrl, note.getPageNumber());
                                        page.setSavedLocalPath(pathLocalPath);
                                        page.setShowingPath(notifyUrl);
                                        pageCache.cacheFile(page);
                                        highLightNoteInDifferentPage(page, note);
                                        return;
                                    } else {
                                        Log.e("highLightNote", "uncorrect_file_name,delete");
                                        localFile.delete();
                                    }
                                } else {
                                    Log.e("highLightNote", "correct_file_name,notify");
                                    page.setSavedLocalPath(pathLocalPath);
                                    page.setShowingPath(notifyUrl);
                                    pageCache.cacheFile(page);
                                    notifyWebFilePrepared(notifyUrl, note.getPageNumber());
                                    highLightNoteInDifferentPage(page, note);
                                    return;
                                }

                            } else {
                                //清楚缓存
                                pageCache.removeFile(_page.getPageUrl());
                            }

                        }
                    }
                }

                Observable.just(meetingConfig.getDocument()).observeOn(Schedulers.io()).map(new Function<MeetingDocument, Object>() {
                    @Override
                    public Object apply(MeetingDocument document) throws Exception {
                        safeDownloadFile(page, pathLocalPath, note.getPageNumber(), true);
                        return document;
                    }
                }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        highLightNoteInDifferentPage(page, note);

                    }
                }).subscribe();
            }


        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshMeetingMembers(EventRefreshMembers refreshMembers) {

        isShowMemberNumber();
        if (meetingConfig.getMeetingMembers() == null || meetingConfig.getType() != MeetingType.MEETING) {
            return;
        }
        if (meetingMembersDialog != null && meetingMembersDialog.isShowing()) {
//            Log.e("refreshMeetingMembers", "dialog_is_show,need_refresh:" + refreshMembers.isNeedRefresh());
//            if (refreshMembers.isNeedRefresh()) {
//            } else {
//                if (meetingConfig.getMeetingMembers().size() + meetingConfig.getMeetingAuditor().size() <= 10) {
//                    meetingMembersDialog.refresh(refreshMembers);
//                }
//            }
            meetingMembersDialog.refresh(refreshMembers);

        }

        checkAgoraMemberNameAndAgoraStatus();

        refreshMemberStatus();

        justRefreshVedioListSizeChanged();

        if (meetingConfig.isInRealMeeting()) {
            MeetingKit.getInstance().setEncoderConfigurationBaseMode();
        }

        if (cameraAdapter != null) {
            cameraAdapter.updateFirst();
        }

    }

    private void isShowMemberNumber() {
        handupnumberrl.setVisibility(View.GONE);
        handupnumberrl.setOnClickListener(this);
        //判断自己是否是发言人
        if (meetingConfig.getMeetingMembers() != null && meetingConfig.getMeetingMembers().size() > 0) {
            for (MeetingMember member : meetingConfig.getMeetingMembers()) {
                if (member.getTempOnStage() == 0 && (member.getUserId() + "").equals(AppConfig.UserID)) {
                    meetingConfig.setViewType(MeetingMember.TYPE_ITEM_MAIN_SPEAKER);
                    break;
                }
            }
        }
        if (meetingConfig.getMeetingHostId().equals(AppConfig.UserID) || meetingConfig.getPresenterId().equals(AppConfig.UserID)
                || meetingConfig.getViewType() == TYPE_ITEM_MAIN_SPEAKER) {
            if (meetingConfig.getMeetingAuditor() != null && meetingConfig.getMeetingAuditor().size() > 0) {
                List<MeetingMember> handsUpMembers = new ArrayList<>();
                for (MeetingMember auditor : meetingConfig.getMeetingAuditor()) {
                    if (auditor.getHandStatus() == 1) {
                        handsUpMembers.add(auditor);
                    }
                }
                if (handsUpMembers.size() > 0) {
                    handupnumberrl.setVisibility(View.VISIBLE);
                    handupsumber.setText(handsUpMembers.size() + "");
                }
            }
        }
    }

    private void justRefreshVedioListSizeChanged() {

        if (meetingConfig.getMeetingMembers().size() < 0 || meetingConfig.getAgoraMembers().size() < 0) {
            return;
        }

        Log.e("check_size", "meeting_member_size:" + meetingConfig.getMeetingMembers().size() + ",agora_size:" + meetingConfig.getAgoraMembers().size());

        initCameraAdatper();

        // 1:删掉不包含在memberlist里面的
        for (AgoraMember agoraMember : meetingConfig.getAgoraMembers()) {
            boolean isContain = meetingConfig.getMeetingMembers().contains(new MeetingMember(agoraMember.getUserId()));
            if (!isContain) {
//                meetingConfig.getAgoraMembers().remove(agoraMember);
                agoraMember.setIsMember(0);
                agoraMember.setAdd(false);
                refreshAgoraMember(agoraMember);
            }

        }

        for (MeetingMember meetingMember : meetingConfig.getMeetingMembers()) {

            // 挨个比较赋值
            for (AgoraMember agoraMember : meetingConfig.getAgoraMembers()) {
                boolean isContain = meetingConfig.getMeetingMembers().contains(new MeetingMember(agoraMember.getUserId()));
                Log.e("check_contain", "is_contain:" + isContain);
                //
                if (meetingMember.getUserId() == agoraMember.getUserId()) {
                    agoraMember.setIsMember(1);
                    Log.e("check_set_mute_vedio", "username:" + meetingMember.getUserName() + ",muteVideo:" + (meetingMember.getCameraStatus() != 2) + "surface_view:" + agoraMember.getSurfaceView());
                    agoraMember.setMuteVideo(meetingMember.getCameraStatus() != 2);
                    agoraMember.setMuteAudio(meetingMember.getMicrophoneStatus() != 2);
                    refreshVedioAndAudioStatusIfNeed(agoraMember);
                    break;
                }
            }

            // 不包含的要新增
            AgoraMember _agoraMember = new AgoraMember(meetingMember.getUserId());
            if (!meetingConfig.getAgoraMembers().contains(_agoraMember)) {
                _agoraMember.setIsMember(1);
                _agoraMember.setUserName(meetingMember.getUserName());
                _agoraMember.setIconUrl(meetingMember.getAvatarUrl());
                _agoraMember.setMuteVideo(meetingMember.getCameraStatus() != 2);
                _agoraMember.setMuteAudio(meetingMember.getMicrophoneStatus() != 2);
                _agoraMember.setAdd(true);
                meetingConfig.addAgoraMember(_agoraMember);
                refreshAgoraMember(_agoraMember);
            }
        }
    }

    private void refreshVedioAndAudioStatusIfNeed(AgoraMember agoraMember) {
        if (cameraAdapter != null) {
            Log.e(TAG, "agoraMember：" + agoraMember);
            cameraAdapter.refreshAudioStatus(agoraMember);
            cameraAdapter.refreshVideoStatus(agoraMember);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void muteAgoraMember(EventMute eventMute) {
        Log.e("muteAgoraMember", "eventMute:" + eventMute);
        if (cameraList.getVisibility() == View.VISIBLE) {
            if (cameraAdapter != null) {
                if (eventMute.getType() == EventMute.TYPE_MUTE_VEDIO) {
                    cameraAdapter.muteVideo(eventMute.getAgoraMember(), eventMute.isMuteVedio());
                } else if (eventMute.getType() == EventMute.TYPE_MUTE_AUDIO) {
                    cameraAdapter.muteAudio(eventMute.getAgoraMember(), eventMute.isMuteAudio());
                }

            }
        }

        if (fullCameraList.getVisibility() == View.VISIBLE) {
            if (fullCameraAdapter != null) {
                if (eventMute.getType() == EventMute.TYPE_MUTE_VEDIO) {
                    fullCameraAdapter.muteVideo(eventMute.getAgoraMember(), eventMute.isMuteVedio());
                } else if (eventMute.getType() == EventMute.TYPE_MUTE_AUDIO) {
                    fullCameraAdapter.muteAudio(eventMute.getAgoraMember(), eventMute.isMuteAudio());

                }
            }
        }

        if (eventMute.getAgoraMember() != null) {
            if ((eventMute.getAgoraMember().getUserId() + "").equals(AppConfig.UserID)) {
                int index = meetingConfig.getAgoraMembers().indexOf(eventMute.getAgoraMember());
                if (index >= 0) {
                    if (meetingConfig.getType() != MeetingType.MEETING) {
                        return;
                    }
                    if (meetingConfig.getRole() == MeetingConfig.MeetingRole.HOST || meetingConfig.getRole() == MeetingConfig.MeetingRole.MEMBER) {
                        messageManager.sendMessage_AgoraStatusChange(meetingConfig, meetingConfig.getAgoraMembers().get(index));
                    }

                }

            }
        }
    }

    private void refreshMemberStatus() {

        if (meetingConfig.getType() != MeetingType.MEETING) {
            return;
        }

        String userId = AppConfig.UserID;
        if (TextUtils.isEmpty(userId)) {
            userId = sharedPreferences.getString("UserID", "");
            AppConfig.UserID = userId;
        }

        boolean isMember = false;
        if (!TextUtils.isEmpty(userId)) {
            int _index = meetingConfig.getMeetingMembers().indexOf(new MeetingMember(Integer.parseInt(userId)));
            if (_index >= 0) {
                //自己是member
                Log.e("refreshMemberStatus", "refresh,ismember,true");
                MeetingMember _me = meetingConfig.getMeetingMembers().get(_index);
                MeetingKit.getInstance().enableAudioAndVideo();
//                if(_me.getMicrophoneStatus() != 2){
//                    MeetingKit.getInstance().disableAudioStream();
//                }
//                if(_me.getCameraStatus() != 2){
//                    MeetingKit.getInstance().disableVedioStream();
//                }
                isMember = true;
                menuIcon.setVisibility(View.VISIBLE);
//                meetingMenuMemberImage.setVisibility(View.GONE);
                handsUpImage.setTag(TAG_NO_HANDS_UP);
                handsUpImage.setText(getString(R.string.wxf_request_speaker));
                handsUpImage.setVisibility(View.GONE);
                if (cameraAdapter != null) {
                    int index = meetingConfig.getAgoraMembers().indexOf(new AgoraMember(Integer.parseInt(userId)));
                    Log.e("check_me", "index:" + index);
                    if (index >= 0) {
                        AgoraMember me = meetingConfig.getAgoraMembers().get(index);
                        Log.e("check_audio", "audio_status2:" + me.isMuteAudio());
                        if (me.getSurfaceView() != null) {
                            cameraAdapter.setMySelfVedioSurface(me.getSurfaceView(), Integer.parseInt(userId));
                        }
                    }
                }

            } else {
                isMember = false;
                Log.e("refreshMemberStatus", "refresh,ismember,false");
                Log.e("check_disable", "disable,2");
                MeetingKit.getInstance().disableAudioAndVideoStream();
//                menuIcon.setVisibility(View.GONE);
//                meetingMenuMemberImage.setVisibility(View.VISIBLE);
                handsUpImage.setVisibility(View.VISIBLE);
            }
        }

//        handleDisplayMode(isMember);

        handleWebUISetting();
    }

//    private void handleDisplayMode(boolean isMember) {
//        if (meetingConfig.getType() != MeetingType.MEETING) {
//            return;
//        }
//
//        int displayMode = sharedPreferences.getInt("display_mode", 0);
//
//        if (isMember && AppConfig.UserID.equals(meetingConfig.getMeetingHostId()) || AppConfig.UserID.equals(meetingConfig.getPresenterId())) {
//            if (followSpearkerModeManager != null) {
//
//            }
//        } else {
//
//            if (followSpearkerModeManager != null) {
//
//            }
//
//            if (displayMode == 0) {
//
//
//            }
//        }
//
//    }

    /**
     * 显示或隐藏web的按钮
     */
    private void handleWebUISetting() {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            return;
        }
        if (isPresenter() || mIsMeetingPause) {
            // show left right arrow
            web.loadUrl("javascript:ShowToolbar(" + true + ")", null);
            web.loadUrl("javascript:Record()", null);
            noteWeb.loadUrl("javascript:ShowToolbar(" + true + ")", null);
            noteWeb.loadUrl("javascript:Record()", null);
            String key = "ChangeMovePageButton";
            JSONObject _data = new JSONObject();
            JSONObject _left = new JSONObject();
            JSONObject _right = new JSONObject();
            try {
                _left.put("Show", true);
                _right.put("Show", true);
                _data.put("Left", _left);
                _data.put("Right", _right);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            web.loadUrl("javascript:ShowToolbar(" + true + ")", null);
            web.loadUrl("javascript:Record()", null);
            web.loadUrl("javascript:FromApp('" + key + "'," + _data + ")", null);
            noteWeb.loadUrl("javascript:ShowToolbar(" + true + ")", null);
            noteWeb.loadUrl("javascript:Record()", null);
            noteWeb.loadUrl("javascript:FromApp('" + key + "'," + _data + ")", null);

        } else {

            // show left right arrow
            web.loadUrl("javascript:ShowToolbar(" + false + ")", null);
            web.loadUrl("javascript:StopRecord()", null);
            noteWeb.loadUrl("javascript:ShowToolbar(" + false + ")", null);
            noteWeb.loadUrl("javascript:StopRecord()", null);
            String key = "ChangeMovePageButton";
            JSONObject _data = new JSONObject();
            JSONObject _left = new JSONObject();
            JSONObject _right = new JSONObject();
            try {
                _left.put("Show", false);
                _right.put("Show", false);
                _data.put("Left", _left);
                _data.put("Right", _right);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            web.loadUrl("javascript:ShowToolbar(" + false + ")", null);
            web.loadUrl("javascript:StopRecord()", null);
            web.loadUrl("javascript:FromApp('" + key + "'," + _data + ")", null);

            noteWeb.loadUrl("javascript:ShowToolbar(" + false + ")", null);
            noteWeb.loadUrl("javascript:StopRecord()", null);
            noteWeb.loadUrl("javascript:FromApp('" + key + "'," + _data + ")", null);
        }

    }

    private void checkAgoraMemberNameAndAgoraStatus() {

        for (MeetingMember member : meetingConfig.getMeetingMembers()) {
            if ((member.getUserId() + "").equals(AppConfig.UserID)) {
                meetingConfig.setRole(member.getRole());
                meetingConfig.setMe(member);
            }

            for (AgoraMember agoraMember : meetingConfig.getAgoraMembers()) {
                if ((member.getUserId() + "").equals(agoraMember.getUserId() + "")) {
                    agoraMember.setUserName(member.getUserName());
                    agoraMember.setIconUrl(member.getAvatarUrl());
//                    if (!(member.getUserId() + "").equals(AppConfig.UserID)) {
//                        // 该主讲人不是自己
//                        MeetingKit.getInstance().setMemberAgoraStutas(member);
//                    } else {
//                        // 该主讲人是自己
//                        MeetingKit.getInstance().setMyAgoraStutas(member);
//                    }

                    Log.e("check_audio", "audio_status1:" + member.getMicrophoneStatus());
                    agoraMember.setMuteVideo(member.getCameraStatus() != 2);
                    agoraMember.setMuteAudio(member.getMicrophoneStatus() != 2);
                    agoraMember.setIsMember(1);
                    break;
                }
            }
        }

        for (MeetingMember member : meetingConfig.getMeetingAuditor()) {
            if ((member.getUserId() + "").equals(AppConfig.UserID)) {
                meetingConfig.setRole(member.getRole());
                meetingConfig.setMe(member);
            }

            for (AgoraMember agoraMember : meetingConfig.getAgoraMembers()) {
                if ((member.getUserId() + "").equals(agoraMember.getUserId() + "")) {
                    agoraMember.setUserName(member.getUserName());
                    agoraMember.setIconUrl(member.getAvatarUrl());
                    agoraMember.setIsMember(0);
//                    if (!(member.getUserId() + "").equals(AppConfig.UserID)) {
//                        // 该参会者不是自己
//                        MeetingKit.getInstance().unsubscribeAudiorsAudioAndVedio(agoraMember.getUserId());
//                    } else {
//                        // 该参会者是自己
//                        MeetingKit.getInstance().unsubscribeMineAudioAndVedio();
//                    }
                    break;
                }
            }
        }
    }

    private void highLightNoteInDifferentPage(DocumentPage page, EventHighlightNote note) {
        showDocumentPage(page);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("LinkID", note.getNote().getLinkID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Observable.just(page).delay(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<DocumentPage>() {
            @Override
            public void accept(DocumentPage page) throws Exception {
                String key = "TwinkleBookNote";
                Log.e("TwinkleBookNote", "delay");
                web.loadUrl("javascript:FromApp('" + key + "'," + jsonObject + ")", null);
            }
        }).subscribe();

    }

    private MeetingDocument getDocument(DocumentPage page) {
        Log.e("check_page", "current_page:" + page);
        for (MeetingDocument document : documents) {
//            if(document.getDocumentPages().contains(page)){
//                return document;
//            }
            for (DocumentPage _page : document.getDocumentPages()) {
                Log.e("check_page", "page:" + _page);
                if (_page.equals(page)) {
                    return document;
                }
            }
        }
        return null;
    }

    private void downLoadDocumentPageAndShow() {
        if (meetingConfig.getDocument() == null || meetingConfig.getDocument().getDocumentPages() == null || meetingConfig.getDocument().getDocumentPages().size() <= 0) {
            return;
        }
        Observable.just(meetingConfig.getDocument()).observeOn(Schedulers.io()).map(new Function<MeetingDocument, Object>() {
            @Override
            public Object apply(MeetingDocument document) throws Exception {
                int pageNumber = 1;
                if (meetingConfig.getPageNumber() == 0) {
                    pageNumber = 1;
                } else if (meetingConfig.getPageNumber() > 0) {
                    pageNumber = meetingConfig.getPageNumber();
                }

                if (pageNumber > document.getDocumentPages().size()) {
                    pageNumber = document.getDocumentPages().size();
                }

                if (pageNumber <= 0) {
                    return null;
                }
                DocumentPage page = document.getDocumentPages().get(pageNumber - 1);
                queryAndDownLoadPageToShow(page, true);
                return page;
            }
        }).subscribe();
    }

    private synchronized void downLoadDocumentPageAndShow(MeetingDocument document, final int pageNumber) {
        if (pageNumber - 1 >= document.getDocumentPages().size()) {
            ToastUtils.show(this, R.string.error_syncing_documents);
            return;
        }
        Observable.just(document).observeOn(Schedulers.io()).map(new Function<MeetingDocument, Object>() {
            @Override
            public Object apply(MeetingDocument document) throws Exception {
                queryAndDownLoadPageToShow(document, pageNumber, true);
                return document;
            }
        }).subscribe();
    }

    private List<MeetingDocument> documents = new ArrayList<>();
    private DocumentPageCache pageCache;

    private Uploadao parseQueryResponse(final String jsonstring) {
        try {
            JSONObject returnjson = new JSONObject(jsonstring);
            if (returnjson.getBoolean("Success")) {
                JSONObject data = returnjson.getJSONObject("Data");

                JSONObject bucket = data.getJSONObject("Bucket");
                Uploadao uploadao = new Uploadao();
                uploadao.setServiceProviderId(bucket.getInt("ServiceProviderId"));
                uploadao.setRegionName(bucket.getString("RegionName"));
                uploadao.setBucketName(bucket.getString("BucketName"));
                return uploadao;
            }
        } catch (JSONException e) {
            return null;
        }
        return null;
    }

    private void queryAndDownLoadPageToShow(final DocumentPage documentPage, final boolean needRedownload) {
        String pageUrl = documentPage.getPageUrl();
        String downloadUrl = pageUrl;
        DocumentPage page = pageCache.getPageCache(pageUrl);
        Log.e("check_download_page", "get_cach_page:" + page + "--> url:" + documentPage.getPageUrl());
        if (page != null && !TextUtils.isEmpty(page.getPageUrl())
                && !TextUtils.isEmpty(page.getSavedLocalPath()) && !TextUtils.isEmpty(page.getShowingPath())) {
            if (new File(page.getSavedLocalPath()).exists()) {
                page.setDocumentId(documentPage.getDocumentId());
                page.setPageNumber(documentPage.getPageNumber());
                pageCache.cacheFile(page);
                EventBus.getDefault().post(page);
                return;
            } else {
                pageCache.removeFile(pageUrl);
            }
        }

        MeetingDocument document = meetingConfig.getDocument();
        String meetingId = meetingConfig.getMeetingId();

        JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
                document.getNewPath());
        if (queryDocumentResult != null) {
            Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
            String fileName = pageUrl.substring(pageUrl.lastIndexOf("/") + 1);

            if (uploadao != null) {
                if (1 == uploadao.getServiceProviderId()) {
                    downloadUrl = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + document.getNewPath()
                            + "/" + fileName;
                } else if (2 == uploadao.getServiceProviderId()) {
                    downloadUrl = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + document.getNewPath() + "/" + fileName;
                }
            }

            String pathLocalPath = FileUtils.getBaseDir() +
                    meetingId + "_" + encoderByMd5(downloadUrl).replaceAll("/", "_") +
                    "_" + (documentPage.getPageNumber()) +
                    pageUrl.substring(pageUrl.lastIndexOf("."));
            final String showUrl = FileUtils.getBaseDir() +
                    meetingId + "_" + encoderByMd5(downloadUrl).replaceAll("/", "_") +
                    "_<" + document.getPageCount() + ">" +
                    pageUrl.substring(pageUrl.lastIndexOf("."));
            int pageIndex = 1;
            if (meetingConfig.getPageNumber() == 0) {
                pageIndex = 1;
            } else if (meetingConfig.getPageNumber() > 0) {
                pageIndex = meetingConfig.getPageNumber();
            }

            documentPage.setSavedLocalPath(pathLocalPath);

            Log.e("check_download_page", "download_page:downloadUrl" + downloadUrl);
            //保存在本地的地址
            Log.e("======", pageUrl + "  \n " + downloadUrl);
            DownloadUtil.get().download(downloadUrl, pathLocalPath, new DownloadUtil.OnDownloadListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onDownloadSuccess(int arg0) {
                    documentPage.setShowingPath(showUrl);
                    Log.e("queryAndDownLoadCurrentPageToShow", "onDownloadSuccess:" + documentPage);
                    pageCache.cacheFile(documentPage);
                    EventBus.getDefault().post(documentPage);
                }

                @Override
                public void onDownloading(final int progress) {

                }

                @Override
                public void onDownloadFailed() {

                    Log.e("-", "onDownloadFailed:" + documentPage);
                    if (needRedownload) {
                        queryAndDownLoadPageToShow(documentPage, false);
                    }
                }
            });
        }
    }

    private synchronized void queryAndDownLoadPageToShow(final MeetingDocument document, final int pageNumber, final boolean needRedownload) {
        if ((pageNumber - 1) < 0) {
            return;
        }
        final DocumentPage _page = document.getDocumentPages().get(pageNumber - 1);
        String pageUrl = _page.getPageUrl();
        String downloadUrl = pageUrl;
        final DocumentPage page = pageCache.getPageCache(pageUrl);
        Log.e("queryAndDownLoadPageToShow", "get cach page:" + page + "--> url:" + pageUrl + "   " + pageNumber);
        if (page != null && !TextUtils.isEmpty(page.getPageUrl())
                && !TextUtils.isEmpty(page.getSavedLocalPath()) && !TextUtils.isEmpty(page.getShowingPath())) {
            if (new File(page.getSavedLocalPath()).exists()) {
                page.setDocumentId(_page.getDocumentId());
                page.setPageNumber(_page.getPageNumber());
                pageCache.cacheFile(page);
                EventBus.getDefault().post(page);
                return;
            } else {
                pageCache.removeFile(pageUrl);
            }
        }

        String meetingId = meetingConfig.getMeetingId();
        JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
                document.getNewPath());

        if (queryDocumentResult != null) {
            Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
            String fileName = pageUrl.substring(pageUrl.lastIndexOf("/") + 1);

            if (uploadao != null) {
                if (1 == uploadao.getServiceProviderId()) {
                    downloadUrl = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + document.getNewPath()
                            + "/" + fileName;
                } else if (2 == uploadao.getServiceProviderId()) {
                    downloadUrl = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + document.getNewPath() + "/" + fileName;
                }

            }

            String pathLocalPath = FileUtils.getBaseDir() +
                    meetingId + "_" + encoderByMd5(downloadUrl).replaceAll("/", "_") +
                    "_" + (_page.getPageNumber()) +
                    pageUrl.substring(pageUrl.lastIndexOf("."));
            final String showUrl = FileUtils.getBaseDir() +
                    meetingId + "_" + encoderByMd5(downloadUrl).replaceAll("/", "_") +
                    "_<" + document.getPageCount() + ">" +
                    pageUrl.substring(pageUrl.lastIndexOf("."));

            Log.e("-", "showUrl:" + showUrl);

            _page.setSavedLocalPath(pathLocalPath);

            Log.e("check_download_page", "download_page:downloadUrl" + downloadUrl);
            //保存在本地的地址

            DownloadUtil.get().download(downloadUrl, pathLocalPath, new DownloadUtil.OnDownloadListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onDownloadSuccess(int arg0) {
                    _page.setShowingPath(showUrl);
                    Log.e("queryAndDownLoadCurrentPageToShow", "onDownloadSuccess:" + page);
                    pageCache.cacheFile(_page);
                    EventBus.getDefault().post(_page);
                }

                @Override
                public void onDownloading(final int progress) {

                }

                @Override
                public void onDownloadFailed() {

                    Log.e("-", "onDownloadFailed:" + _page);
                    if (needRedownload) {
                        queryAndDownLoadPageToShow(document, pageNumber, false);
                    }
                }
            });
        }
    }

    private synchronized void changeDocument(MeetingDocument document, int pageNumber) {

        Log.e("changeDocument", "document:" + document.getTitle() + "  " + pageNumber);
        if (meetingConfig.isSyncing()) {
            return;
        }

        downLoadDocumentPageAndShow(document, pageNumber);
    }

    private synchronized void changeDocument(int itemId, int pageNumber) {

        if (hasLoadedFile) {
            int index = documents.indexOf(new MeetingDocument(itemId));
            if (index < 0) {
                return;
            }
            MeetingDocument _document = documents.get(index);
            if (meetingConfig.getDocument().equals(_document)) {
                return;
            }

            changeDocument(_document, pageNumber);

        } else {
            DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, itemId, pageNumber);
        }

    }

    private synchronized void followChangeDocument(int itemId, int pageNumber) {
        Log.e("changeDocument--", "document:" + itemId + "  " + pageNumber);
        if (documents == null || documents.size() == 0) {
            DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, itemId, pageNumber);
            return;
        }

        int index = documents.indexOf(new MeetingDocument(itemId));
        if (index < 0) {
            DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, itemId, pageNumber);
            return;
        }
        MeetingDocument _document = documents.get(index);
        if (meetingConfig.getDocument() != null && meetingConfig.getDocument().equals(_document) && (pageNumber == meetingConfig.getPageNumber())) {
            return;
        }
        changeDocument(_document, pageNumber);


    }


    private synchronized void safeDownloadFile(final DocumentPage page, final String localPath, final int pageNumber, final boolean needRedownload) {

        Log.e("safeDownloadFile", "start down load:" + page);
        final String url = meetingConfig.getNotifyUrl();
        page.setSavedLocalPath(localPath);
        final ThreadLocal<DocumentPage> localPage = new ThreadLocal<>();
        localPage.set(page);
//      DownloadUtil.get().cancelAll();
        DownloadUtil.get().syncDownload(localPage.get(), new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(int code) {
                localPage.get().setShowingPath(url);
                Log.e("safeDownloadFile", "onDownloadSuccess:" + localPage.get());
                pageCache.cacheFile(localPage.get());
                notifyWebFilePrepared(url, pageNumber);
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                Log.e("safeDownloadFile", "onDownloadFailed:" + localPage.get());
                if (needRedownload) {
                    safeDownloadFile(page, localPath, pageNumber, false);
                }
            }
        });
    }


    private synchronized void safeDownloadFile(final String pathLocalPath, final DocumentPage page, final String notifyUrl, final int index, final boolean needRedownload) {

        Log.e("safeDownloadFile", "start down load:" + page);
        page.setSavedLocalPath(pathLocalPath);
        final ThreadLocal<DocumentPage> localPage = new ThreadLocal<>();
        localPage.set(page);
//      DownloadUtil.get().cancelAll();
        DownloadUtil.get().syncDownload(localPage.get(), new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(int code) {
                localPage.get().setShowingPath(notifyUrl);
                Log.e("safeDownloadFile", "onDownloadSuccess:" + localPage.get());
                pageCache.cacheFile(localPage.get());
                notifyWebFilePrepared(notifyUrl, index);
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                Log.e("safeDownloadFile", "onDownloadFailed:" + localPage.get());
                if (needRedownload) {
                    safeDownloadFile(pathLocalPath, page, notifyUrl, index, false);
                }
            }
        });
    }

    private void notifyWebFilePrepared(final String url, final int index) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("WebView_Load", "javascript:AfterDownloadFile('" + url + "', " + index + ")");
                web.loadUrl("javascript:AfterDownloadFile('" + url + "', " + index + ")", null);

            }
        });
    }

    public String encoderByMd5(String str) {
        try {
            //确定计算方法
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            BASE64Encoder base64en = new BASE64Encoder();
            //加密后的字符串
            String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
            return newstr;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }


    //-----  JavascriptInterface ----
    @JavascriptInterface
    public void afterLoadPageFunction() {
        Log.e("JavascriptInterface", "afterLoadPageFunction");
    }

    @JavascriptInterface
    public void userSettingChangeFunction(final String option) {
        Log.e("JavascriptInterface", "userSettingChangeFunction,option:  " + option);

    }

    @JavascriptInterface
    public synchronized void preLoadFileFunction(final String url, final int currentpageNum, final boolean showLoading) {
        Log.e("JavascriptInterface", "preLoadFileFunction,url:  " + url + ", currentpageNum:" + currentpageNum + ",showLoading:" + showLoading);
        meetingConfig.setNotifyUrl(url);
        if (currentpageNum - 1 < 0) {
            return;
        }

        final DocumentPage page = meetingConfig.getDocument().getDocumentPages().get(currentpageNum - 1);

        final String pathLocalPath = url.substring(0, url.lastIndexOf("<")) +
                currentpageNum + url.substring(url.lastIndexOf("."));

        if (page != null && !TextUtils.isEmpty(page.getPageUrl())) {
            DocumentPage _page = pageCache.getPageCache(page.getPageUrl());
            Log.e("check_cache_page", "_page:" + _page + "，page:" + page);
            if (_page != null && page.getPageUrl().equals(_page.getPageUrl())) {
                if (!TextUtils.isEmpty(_page.getSavedLocalPath())) {
                    File localFile = new File(_page.getSavedLocalPath());
                    if (localFile.exists()) {
                        if (!pathLocalPath.equals(localFile.getAbsolutePath())) {
                            if (localFile.renameTo(new File(pathLocalPath))) {
                                Log.e("preLoadFileFunction", "uncorrect_file_name,rename");
                                notifyWebFilePrepared(url, currentpageNum);
                                return;
                            } else {
                                Log.e("preLoadFileFunction", "uncorrect_file_name,delete");
                                localFile.delete();
                            }
                        } else {
                            Log.e("preLoadFileFunction", "correct_file_name,notify");
                            notifyWebFilePrepared(url, currentpageNum);
                            return;
                        }

                    } else {
                        //清楚缓存
                        pageCache.removeFile(_page.getPageUrl());
                    }

                }
            }
        }

        Log.e("JavascriptInterface", "preLoadFileFunction,page:  " + page);

        new ApiTask(new Runnable() {
            @Override
            public void run() {
                safeDownloadFile(pathLocalPath, page, url, currentpageNum, true);
            }
        }).start(ThreadManager.getManager());

    }


    private boolean hasLoadedFile = false;

    @JavascriptInterface
    public void afterLoadFileFunction() {
        Log.e("JavascriptInterface", "afterLoadFileFunction");
        hasLoadedFile = true;

    }

    @JavascriptInterface
    public void showErrorFunction(final String error) {
        Log.e("JavascriptInterface", "showErrorFunction,error:  " + error);
    }

    @JavascriptInterface
    public void afterChangePageFunction(final int pageNum, int type) {
        Log.e("JavascriptInterface", "afterChangePageFunction,pageNum:  " + pageNum + ", type:" + type);
        meetingConfig.setPageNumber(pageNum);
        if (meetingConfig.getDocument() != null) {
            PageActionsAndNotesMgr.requestActionsAndNote(meetingConfig);
            mShowDocPageMap.put(meetingConfig.getDocument().getItemID(), pageNum);
        }

        if (meetingConfig.getCurrentDocumentPage() != null) {
            meetingConfig.getCurrentDocumentPage().setPageNumber(pageNum);
        }
        /*是否直接播放音想*/
        if (mSoundtrackBean != null) {
            SoundTrack soundTrack = new SoundTrack();
            soundTrack.setSoundtrackID(mSoundtrackBean.getSoundtrackID());
            EventPlaySoundtrack playSoundtrack = new EventPlaySoundtrack();
            playSoundtrack.setSoundTrack(soundTrack);
            EventBus.getDefault().post(playSoundtrack);
            mSoundtrackBean = null;
        }

    }

    @JavascriptInterface
    public void reflect(String result) {
        Log.e("JavascriptInterface", "reflect,result:  " + result);
        meetingConfig.setDocModifide(checkIfModifyDoc(result));
        notifyMyWebActions(result);
        DocVedioManager.getInstance(this).prepareVedio(result);
        //处理private coach
        //meetingConfig.getSystemType() != 0 &&
        if (meetingConfig.isMeetingPause()) {
            if (meetingConfig.isCoaching()) {
                if (isPresenter() || (meetingConfig.getPrivateCoachMember() != null && (meetingConfig.getPrivateCoachMember().getUserId() + "").equals(AppConfig.UserID))) {
                    messageManager.sendMessage_MyActionFrame(result, meetingConfig);
                }
            }
        }
    }

    private boolean checkIfModifyDoc(String result) {
        if (meetingConfig.isDocModifide()) {
            return true;
        }
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject _result = new JSONObject(result);
                if (_result.has("type")) {

                    int type = _result.getInt("type");
                    if (type == 22 || type == 24 || type == 25 || type == 103) {
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 发送翻页,产生的笔记数据消息
     *
     * @param actions
     */
    private void notifyMyWebActions(String actions) {
        if (messageManager == null) {
            messageManager = SocketMessageManager.getManager(this);
            messageManager.registerMessageReceiver();
        }

        if (meetingConfig.getType() != MeetingType.MEETING) {
            if (meetingConfig.isSyncing()) {
                if (!TextUtils.isEmpty(actions)) {
                    SoundtrackRecordManager.getManager(this).recordDocumentAction(actions);
                }
            } else {
                messageManager.sendMessage_MyActionFrame(actions, meetingConfig);
            }

        } else {

            if (meetingConfig.isSyncing()) {
                if (!TextUtils.isEmpty(actions)) {
                    SoundtrackRecordManager.getManager(this).recordDocumentAction(actions);
                }
            }


            Log.e("notifyMyWebActions", "role:" + meetingConfig.getRole());
            if (!AppConfig.UserID.equals(meetingConfig.getPresenterId()) || mIsMeetingPause) {
                return;
            }
            messageManager.sendMessage_MyActionFrame(actions, meetingConfig);
        }
    }

    private void notifyMyNoteWebActions(String actions, Note note) {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            if (messageManager == null) {
                messageManager = SocketMessageManager.getManager(this);
                messageManager.registerMessageReceiver();
            }
            messageManager.sendMessage_MyNoteActionFrame(actions, meetingConfig, note);

        } else {
            Log.e("notifyMyWebActions", "role:" + meetingConfig.getRole());
            if (!AppConfig.UserID.equals(meetingConfig.getPresenterId())) {
                return;
            }
            if (messageManager == null) {
                messageManager = SocketMessageManager.getManager(this);
                messageManager.registerMessageReceiver();
            }
            messageManager.sendMessage_MyNoteActionFrame(actions, meetingConfig, note);
        }
    }

    /**
     * 发送切换文档消息
     */
    private void notifyDocumentChanged() {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            if (messageManager != null) {
                messageManager.sendMessage_DocumentShowed(meetingConfig);
            }
        } else {
            if (!TextUtils.isEmpty(meetingConfig.getPresenterSessionId())) {
                if (AppConfig.UserID.equals(meetingConfig.getPresenterId()) && !mIsMeetingPause) {
                    if (meetingConfig.isInRealMeeting()) {
                        if (messageManager != null) {
                            messageManager.sendMessage_DocumentShowed(meetingConfig);
                        }
                    }
                }
                if (meetingConfig.isMeetingPause()) {  //暂停会议中
                    if (meetingConfig.isCoaching()) { //私教中
                        //自己是私教中的学生和老师  通知私教内学生切换文档
                        if (meetingConfig.getPrivateCoachMember().getUserId() == Integer.parseInt(AppConfig.UserID) ||
                                AppConfig.UserID.equals(meetingConfig.getPresenterId())) {
                            if (messageManager != null) {
                                messageManager.sendMessage_DocumentShowedPrivateCoach(meetingConfig);
                            }
                        }
                    }
                }


            }
        }

    }

    private void notifyViewNote(Note note) {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            if (messageManager != null) {
                messageManager.sendMessage_ViewNote(meetingConfig, note);
            }
        } else {
            if (!TextUtils.isEmpty(meetingConfig.getPresenterSessionId())) {
                if (AppConfig.UserToken.equals(meetingConfig.getPresenterSessionId())) {
                    if (meetingConfig.isInRealMeeting()) {
                        if (messageManager != null) {
                            messageManager.sendMessage_ViewNote(meetingConfig, note);
                        }

                    }
                }
            }
        }

    }

    @JavascriptInterface
    public synchronized void autoChangeFileFunction(int diff) {
        Log.e("JavascriptInterface", "autoChangeFileFunction,diff:  " + diff);
        if (documents.size() <= 1) {
            return;
        }
        if (diff == 1) {
            _changeToNextDocument();
        } else if (diff == -1) {
            _changeToPreDocument();
        }
    }

    private void _changeToNextDocument() {
        MeetingDocument document = meetingConfig.getDocument();
        int index = documents.indexOf(document);
        Log.e("check_file_index", "index:" + index + ",documents size:" + documents.size());
        if (index + 1 < documents.size()) {
            document = documents.get(index + 1);
            changeDocument(document, 1);
        }
    }

    private void _changeToPreDocument() {
        MeetingDocument document = meetingConfig.getDocument();
        int index = documents.indexOf(document);
        if (index - 1 < documents.size() && (index - 1 >= 0)) {
            document = documents.get(index - 1);
            changeDocument(document, document.getPageCount());
        }
    }

    // 播放视频
    @JavascriptInterface
    public void videoPlayFunction(final int vid) {
        Log.e("JavascriptInterface", "videoPlayFunction,vid:  " + vid);
        if (meetingConfig.getType() == MeetingType.MEETING) {
            if (!meetingConfig.getPresenterId().equals(AppConfig.UserID)) {
                Toast.makeText(this, "不是presenter，不能操作", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        DocVedioManager.getInstance(this).play(this, vedioLayout, meetingConfig, vid);

    }

    private FavoriteVideoPopup selectVideoDialog;

    //打开
    @JavascriptInterface
    public void videoSelectFunction(String video) {
        Log.e("JavascriptInterface", "videoSelectFunction,id:  " + video);
        if (selectVideoDialog == null) {
            selectVideoDialog = new FavoriteVideoPopup(this);
            selectVideoDialog.setFavoritePoPListener(new FavoriteVideoPopup.FavoriteVideoPoPListener() {
                @Override
                public void selectFavorite(int position) {

                }

                @Override
                public void cancel() {

                }

                @Override
                public void save(int type, boolean isYinxiang) {

                }

                @Override
                public void uploadFile(int type) {

                }

                @Override
                public void dismiss() {

                }

                @Override
                public void open() {

                }
            });
        }
        selectVideoDialog.show();
    }

    // 录制
    @JavascriptInterface
    public void audioSyncFunction(final int id, final int isRecording) {
        Log.e("JavascriptInterface", "audioSyncFunction,id:  " + id + ",isRecording:" + isRecording);

    }

    @JavascriptInterface
    public synchronized void callAppFunction(final String action, final String data) {
        Log.e("JavascriptInterface", "callAppFunction,action:  " + action + ",data:" + data);
        if (TextUtils.isEmpty(action) || TextUtils.isEmpty(data)) {
            return;
        }

        Observable.just(data).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                PageActionsAndNotesMgr.handleNoteActions(DocAndMeetingActivity.this, action, new JSONObject(data), meetingConfig);
            }
        }).subscribe();

    }

    // ---- Bottom Menu
    ExitDialog exitDialog;

    @Override
    public void menuClosedClicked() {
        handleExit(false);
    }

    private void handleExit(boolean isEnd) {
        if (meetingConfig == null) {
            finish();
        }
        if (exitDialog != null) {
            if (exitDialog.isShowing()) {
                exitDialog.dismiss();
            }
            exitDialog = null;
        }

        exitDialog = new ExitDialog(this, meetingConfig);
        exitDialog.setEndMeeting(isEnd);
        exitDialog.setDialogClickListener(new ExitDialog.ExitDialogClickListener() {
            @Override
            public void onSaveAndLeaveClick() {

                if (exitDialog.isEndMeeting() && meetingConfig.isInRealMeeting()) {
                    messageManager.sendMessage_EndMeeting(meetingConfig);
                }
                if (messageManager != null) {
                    messageManager.sendMessage_UpdateAttchment(meetingConfig);
                    messageManager.sendMessage_LeaveMeeting(meetingConfig);
                }
                PageActionsAndNotesMgr.requestActionsSaved(meetingConfig);
                soundtrackPlayManager.followClose();
                finish();
            }

            @Override
            public void onLeaveClick() {
                if (exitDialog.isEndMeeting() && meetingConfig.isInRealMeeting()) {
                    messageManager.sendMessage_EndMeeting(meetingConfig);
                }
                soundtrackPlayManager.followClose();
                finish();
            }
        });
        exitDialog.show();
    }

    @Override
    public void menuFileClicked() {
        if (bottomFilePop == null) {
            bottomFilePop = new PopBottomFile(this, meetingConfig);
        }
        if (documents != null && documents.size() > 0) {
            int itemId = 0;
            if (meetingConfig.getDocument() != null) {
                itemId = meetingConfig.getDocument().getItemID();
            }
            bottomFilePop.setDocuments(documents, itemId, this);

        }
        if (menuManager != null) {
            menuManager.setMenuIcon(menuIcon);
            menuManager.totalHideMenu();
        }
        menuIcon.setImageResource(R.drawable.shape_transparent);
        menuIcon.setEnabled(false);
        bottomFilePop.show(web, this);
    }

    @Override
    public void menuDocThumbnailList() {
        if (mBottomDocThumbnailDialog == null) {
            mBottomDocThumbnailDialog = new BottomDocThumbnailDialog(this, this, this);
        }
        mBottomDocThumbnailDialog.show(meetingConfig);
    }

    @Override
    public void menuStartMeetingClicked() {

        String url = AppConfig.URL_MEETING_BASE + "member/member_on_other_device?meetingId=" + meetingConfig.getLessionId();
        MeetingServiceTools.getInstance().getMemberOnOtherDevice(url, MeetingServiceTools.MEMBERONOTHERDEVICE, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                TvDevice tvDevice = (TvDevice) object;
                meetingConfig.setHost(true);
                if (tvDevice != null) {
                    if (!TextUtils.isEmpty(tvDevice.getUserID())) { //已经有其他地方开启了会议
                        openWarningInfo(1);
                    } else {
                        meetingKit = MeetingKit.getInstance();
                        meetingKit.prepareStart(DocAndMeetingActivity.this, meetingConfig, meetingConfig.getLessionId() + "");
                    }
                }
            }
        });

    }

    @Override
    public void menuShareDocClicked() {
        shareDocument();
    }

    @Override
    public void menuNoteClicked() {
        showNotesDialog();
//        showNoteFloatingDialog(1915234);
    }

    @Override
    public void menuScanTvClicked() {
        handleScanTv();
    }

    @Override
    public void menuMeetingMembersClicked() {
        if (meetingConfig.getMeetingMembers() == null || meetingConfig.getMeetingMembers().size() < 0) {
            return;
        }
        mIsMemberDialogShow = true;
        showMembersDialog();
    }

    @Override
    public void menuSyncClicked() {
        showSoundtrackDialog();
    }

    @Override
    public void menuPlayMeetingRecordClicked() {

    }

    @Override
    public void menuSettingClicked() {
        showDisplayModeSettngDialog();
    }

    private MemberDisplayModeSettingDialog displayModeSettingDialog;

    public void showDisplayModeSettngDialog() {
        if (displayModeSettingDialog != null) {
            if (displayModeSettingDialog.isShowing()) {
                displayModeSettingDialog.dismiss();
            }
            displayModeSettingDialog = null;
        }
        displayModeSettingDialog = new MemberDisplayModeSettingDialog(this);
        displayModeSettingDialog.show();
    }

    @Override
    public void menuChatClicked() {
        showChatPop();
    }

    private UserSoundtrackDialog soundtrackDialog;

    private void showSoundtrackDialog() {
        if (soundtrackDialog != null) {
            if (soundtrackDialog.isShowing()) {
                soundtrackDialog.dismiss();
                soundtrackDialog = null;
            }
        }
        soundtrackDialog = new UserSoundtrackDialog(DocAndMeetingActivity.this);
        soundtrackDialog.show(meetingConfig);
    }

    //-----
    @Override
    public void addFromTeam() {

        openTeamDocument();

    }

    @Override
    public void addFromCamera() {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA};
        startRequestPermission(permissions, 322);
    }

    @Override
    public void addFromCameraDocImage() {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA};
        startRequestPermission(permissions, 324);
    }

    @Override
    public void addFromPictures() {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA};
        startRequestPermission(permissions, 323);
    }

    @Override
    public void addFromFileSystem() {
        Intent intent = new Intent(this, FilePickerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_REQUEST_SELECTED_FILE);
    }

    @Override
    public void addBlankFile() {
        reqeustNewBlankPage();
    }

    AddFileFromDocumentDialog addFileFromDocumentDialog;

    private void openTeamDocument() {

        if (addFileFromDocumentDialog != null) {
            addFileFromDocumentDialog.dismiss();
        }
        addFileFromDocumentDialog = new AddFileFromDocumentDialog(this);
        addFileFromDocumentDialog.setOnSpaceSelectedListener(this);
        addFileFromDocumentDialog.show();

    }

    @Override
    public void onDocSelected(String docId) {
        TeamSpaceInterfaceTools.getinstance().uploadFromSpace(AppConfig.URL_PUBLIC + "EventAttachment/UploadFromSpace?lessonID=" + meetingConfig.getLessionId() + "&itemIDs=" + docId, TeamSpaceInterfaceTools.UPLOADFROMSPACE, new TeamSpaceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                Log.e("add_success", "response:" + object);
                try {
                    JSONObject data = new JSONObject(object.toString());
                    if (data.getInt("RetCode") == 0) {
                        JSONObject document = data.getJSONArray("RetData").getJSONObject(0);
                        if (document != null && document.has("ItemID")) {
                            addDocSucc(document.getInt("ItemID"));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    //------

    private AddFileFromFavoriteDialog addFileFromFavoriteDialog;

    @Override
    public void addFromFavorite() {
        if (addFileFromFavoriteDialog != null) {
            if (addFileFromFavoriteDialog.isShowing()) {
                addFileFromFavoriteDialog.dismiss();
            }
            addFileFromFavoriteDialog = null;
        }
        addFileFromFavoriteDialog = new AddFileFromFavoriteDialog(this);
        addFileFromFavoriteDialog.setOnFavoriteDocSelectedListener(this);
        addFileFromFavoriteDialog.show();
    }


    @Override
    public void onFavoriteDocSelected(String docId) {
        TeamSpaceInterfaceTools.getinstance().uploadFromSpace(AppConfig.URL_PUBLIC + "EventAttachment/UploadFromFavorite?lessonID=" +
                        meetingConfig.getLessionId() + "&itemIDs=" + docId, TeamSpaceInterfaceTools.UPLOADFROMSPACE,
                new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        Log.e("add_success", "response:" + object);
                        try {
                            JSONObject data = new JSONObject(object.toString());
                            if (data.getInt("RetCode") == 0) {
                                JSONObject document = data.getJSONArray("RetData").getJSONObject(0);
                                if (document != null && document.has("ItemID")) {
                                    addDocSucc(document.getInt("ItemID"));
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    //-----
    @Override
    public void onDocumentClick(MeetingDocument document) {
        changeDocument(document, getDocPageNumber(document.getItemID()));
    }

    private int getDocPageNumber(int selectDocItemId) {
        int pageNumber = 1;
        Iterator<Integer> iterator = mShowDocPageMap.keySet().iterator();
        while (iterator.hasNext()) {
            Integer itemId = iterator.next();
            if (itemId == selectDocItemId) {
                pageNumber = mShowDocPageMap.get(itemId);
                break;
            }
        }
        return pageNumber;
    }

    private void initRealMeeting() {
        Log.e("DocAndMeetigActivity", "initRealMeeting");
        if (meetingConfig.getType() != MeetingType.MEETING) {
            return;
        }
        if (meetingConfig.isThirdAudio()) {  //打开微信
            Intent lan = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(lan.getComponent());
            startActivity(intent);
        } else {
            MeetingKit.getInstance().setOnSpeakerAgoraStatusChanged(this);
            MeetingKit.getInstance().startMeeting();
            meetingLayout.setVisibility(View.VISIBLE);
        }
        keepScreenWake();
        MeetingRecordManager.getManager(this).initRecording(recordstatus, messageManager, meetingConfig);

        meetingMenu.setVisibility(View.VISIBLE);
        waitingMeetingLayout.setVisibility(View.GONE);
        menuIcon.setImageResource(R.drawable.icon_menu);
        menuIcon.setEnabled(true);
        if (messageManager != null && meetingConfig.isHost()) {
            messageManager.sendMessage_MeetingStatus(meetingConfig);
        }

        String meetingIndetifier = meetingConfig.getMeetingId() + "-" + meetingConfig.getLessionId();
        ChatManager.getManager(this, meetingIndetifier).joinChatRoom(getResources().getString(R.string.Classroom) + meetingConfig.getLessionId());
    }

    private void initViews() {
        toggleCameraLayout.setOnClickListener(this);
        cameraList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        cameraList.setDrawingCacheEnabled(true);
        cameraList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        fullCameraList.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        fullCameraList.setDrawingCacheEnabled(true);
        fullCameraList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        meetingMenu.setOnClickListener(this);
        backFullCameraImage.setOnClickListener(this);
        returnCameraImage.setOnClickListener(this);
        singleFullScreenImage.setOnClickListener(this);
        icon_videofull_single_full_screen.setOnClickListener(this);

        closeVedioImage.setOnClickListener(this);
        createBlankPageLayout.setOnClickListener(this);
        inviteLayout.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
        _inviteLayout.setOnClickListener(this);
        _shareLayout.setOnClickListener(this);
//        meetingMenuMemberImage.setOnClickListener(this);
        handsUpImage.setOnClickListener(this);
        selectSpeakerImage.setOnClickListener(this);
//        CameraTouchListener cameraTouchListener = new CameraTouchListener();
//        cameraTouchListener.setCameraLayout(cameraLayout,this);
        CameraRecyclerViewTouchListener cameraRecyclerViewTouchListener = new CameraRecyclerViewTouchListener();
        cameraRecyclerViewTouchListener.setCameraLayout(cameraLayout, this);
        cameraList.addOnItemTouchListener(cameraRecyclerViewTouchListener);
//        toggleCameraLayout.setOnTouchListener(cameraTouchListener);
//        followSpearkerModeManager = new FollowSpearkerModeManager(cameraTouchListener, cameraRecyclerViewTouchListener, meetingConfig);
//        followSpearkerModeManager.setSpeakerContainer(speakerContainer);
//        followSpearkerModeManager.setOnSpeakerViewClickedListener(this);
//        followSpearkerModeManager.initViews(this, true);
        organiserNameTxt.setText(sharedPreferences.getString("SchoolName", ""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_toggle_camera:
                meetingConfig.setMembersCameraToggle(!meetingConfig.isMembersCameraToggle());
                toggleMembersCamera(meetingConfig.isMembersCameraToggle());
                break;
            case R.id.meeting_menu:
                if (meetingKit == null) {
                    meetingKit = MeetingKit.getInstance();
                }
                meetingKit.showMeetingMenu(meetingMenu, this, meetingConfig, mIsMeetingPause);
                break;
            case R.id.icon_back_return_screen:
                EventShowFullAgora showFullAgora = new EventShowFullAgora();
                showFullAgora.setAgoraMember(fullCameraAdapter.getMembers().get(0));
                EventBus.getDefault().post(showFullAgora);
                break;
            case R.id.icon_back_full_screen:
//                fullCamereLayout.setVisibility(View.GONE);
//                fullCameraList.setVisibility(View.GONE);
//                cameraList.setVisibility(View.VISIBLE);
//                reloadAgoraMember();
                mIsVideoMeeting = false;
                hideFullCameraScreen();
                if (shareScreen != null) {
                    showShareScreen(shareScreen);
                }
                break;
            case R.id.icon_back_single_full_screen:
                hideAgoraFull();
                if (shareScreen != null) {
                    showShareScreen(shareScreen);
                }

                break;
            case R.id.icon_videofull_single_full_screen:
                hideAgoraFull2();
                handleFullScreenCamera(cameraAdapter, true);
                break;
            case R.id.image_vedio_close:
                DocVedioManager.getInstance(this).close();
                break;
            case R.id._layout_invite:
            case R.id.layout_invite:
                Intent intent = new Intent(this, AddMeetingMemberActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.layout_share:
            case R.id._layout_share:
                if (meetingConfig.getType() != MeetingType.MEETING) {
                    return;
                }
                if (isPresenter()) {
                    if (bottomFilePop != null) {
                        menuIcon.setEnabled(false);
                        menuIcon.setImageResource(R.drawable.shape_transparent);
                        bottomFilePop.openAndShowAdd(web, this);
                    }

                } else {
                    showShareDocInMeetingDialog();
                }

                break;
            case R.id.layout_create_blank_page:
                reqeustNewBlankPage();
                break;

            case R.id.meeting_menu_member:

                if (meetingConfig.getMeetingMembers() == null || meetingConfig.getMeetingMembers().size() < 0) {
                    return;
                }
                showMembersDialog();
                break;

            case R.id.image_meeting_hands_up:
                if (handsUpImage.getTag() != null) {
                    final int handsUp = (int) handsUpImage.getTag();
                    if (handsUp == TAG_NO_HANDS_UP) {
                        Observable.just("hands_up").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
                            @Override
                            public JSONObject apply(String s) throws Exception {
                                return ServiceInterfaceTools.getinstance().
                                        syncHandUpOrDown(1, null);
                            }
                        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
                            @Override
                            public void accept(JSONObject response) throws Exception {
                                if (response.has("code")) {
                                    if (response.getInt("code") == 0) {
                                        handsUpImage.setText(getString(R.string.wxf_hand_up));
                                        handsUpImage.setTag(TAG_HAVE_HANDS_UP);
                                    }
                                }
                            }
                        }).subscribe();
                    }
                }

                break;
            case R.id.image_select_speaker:
                toggleSelected();
//                expandUserList();
//                backToSpeaker();
                break;
            case R.id.handupnumberrl:
                showMembersDialog();
                break;
        }
    }

    private boolean isSelectSpeaker = false;

    private void toggleSelected() {
//        handleDisplayMode(0);
        if (!isSelectSpeaker) {
            if (cameraAdapter != null) {
                isSelectSpeaker = true;
                selectSpeakerCount = 0;
                rejoinHandler.removeMessages(MESSAGE_CHECK_SELECTED_SPEAKER);
                if (!meetingConfig.isMeetingPause()) {
                    rejoinHandler.obtainMessage(MESSAGE_CHECK_SELECTED_SPEAKER).sendToTarget();
                }
                selectSpeakerImage.setImageResource(R.drawable.icon_arrow_select_speaker);
                cameraAdapter.expandedListForSelected();
            }
        } else {
            isSelectSpeaker = false;
            selectSpeakerImage.setImageResource(R.drawable.user_info_expanded);
            justHandleDisplayMode(0);
        }

    }

    private void backToSpeaker() {
        justHandleDisplayMode(0);
//        toggleCameraLayout.setVisibility(View.GONE);
//        cameraList.setVisibility(View.GONE);
//        cameraLayout.setVisibility(View.GONE);
//        if (followSpearkerModeManager != null) {
//            followSpearkerModeManager.showHostView(getPresenterAgora());
//        }
//        meetingConfig.setCameraDiplayMode(0);
    }

    private void handsUpAndRefresh() {

    }

    ShareDocInMeetingDialog shareDocInMeetingDialog;

    private void showShareDocInMeetingDialog() {
        if (shareDocInMeetingDialog != null) {
            if (shareDocInMeetingDialog.isShowing()) {
                shareDocInMeetingDialog.cancel();
            }
        }
        shareDocInMeetingDialog = new ShareDocInMeetingDialog(this);
        shareDocInMeetingDialog.show();
    }

    private void toggleMembersCamera(boolean isToggle) {

        selectSpeakerImage.setVisibility(View.GONE);
        toggleCameraImage.setVisibility(View.VISIBLE);
        fullCameraList.setVisibility(View.GONE);
        if (cameraList.getVisibility() != View.VISIBLE) {
            cameraList.setVisibility(View.VISIBLE);
            reloadAgoraMember();
            toggleCameraImage.setImageResource(R.drawable.user_info_collopse);
            Log.e("handle_display_mode", "toggle_all");
            handleDisplayMode(2);

        } else {
            if (cameraAdapter != null) {
                cameraAdapter.reset();
            }
            cameraList.setVisibility(View.GONE);
            toggleCameraImage.setImageResource(R.drawable.user_info_expanded);
            Log.e("handle_display_mode", "toggle_hide");
            handleDisplayMode(1);

        }


    }

    private void requestChangeDisplayMode(final int mode) {
        Observable.just("change_mode").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(String s) throws Exception {
                return ServiceInterfaceTools.getinstance().syncChangeCameraDisplayMode(mode);
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if (jsonObject.has("code")) {
                    if (jsonObject.getInt("code") == 0) {
                    }
                }
            }
        }).subscribe();
    }

    ShareDocumentDialog shareDocumentDialog;

    private void shareDocument() {
        if (shareDocumentDialog != null) {
            shareDocumentDialog.dismiss();
            shareDocumentDialog = null;
        }
        shareDocumentDialog = new ShareDocumentDialog();
        shareDocumentDialog.getPopwindow(this, meetingConfig.getDocument());
        shareDocumentDialog.show();
    }

    UserNotesDialog notesDialog;

    private void showNotesDialog() {
        Log.e("showNotesDialog", "meeting_config:" + meetingConfig);
        if (notesDialog != null) {
            if (notesDialog.isShowing()) {
                notesDialog.dismiss();
                notesDialog = null;
            }
        }
        notesDialog = new UserNotesDialog(this);
        notesDialog.show(AppConfig.UserID, meetingConfig);
    }


    private FloatingWindowNoteManager mFloatingWindowNoteManager;

    @SuppressLint("WrongViewCast")
    private void showNoteFloatingDialog(int noteId) {
        currentNoteId = noteId;
        if (mFloatingWindowNoteManager == null) {
            mFloatingWindowNoteManager = FloatingWindowNoteManager.getManager(this, findViewById(R.id.floatnoteview));
        }
        mFloatingWindowNoteManager.show(noteId, meetingConfig);
        mFloatingWindowNoteManager.setFloatingChangeListener(new FloatingWindowNoteManager.FloatingChangeListener() {
            @Override
            public void changeHomePage(int noteId) {
                followShowNote(noteId);
            }
        });
    }

    private void openOrCloseNote(EventSocketMessage socketMessage) {
        try {
            JSONObject retData = socketMessage.getData().getJSONObject("retData");
            int noteId = retData.getInt("noteId");
            int status = retData.getInt("status");
            if (status == 1) { //打开浮窗
                if (noteLayout.getVisibility() == View.VISIBLE) {
                    if (noteWeb != null) {
                        followShowNote(noteId);
                    }
                } else {
                    showNoteFloatingDialog(noteId);
                }
            } else if (status == 0) {  //关闭浮窗 或者 主界面
                if (noteLayout.getVisibility() == View.VISIBLE) {
                    if (noteWeb != null) {
                        NoteViewManager.getInstance().closeNoteWeb();
                    }
                }
                if (mFloatingWindowNoteManager != null) {
                    if (mFloatingWindowNoteManager.isShowing()) {
                        mFloatingWindowNoteManager.closeFloating();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverOpenFloating(EventFloatingNote eventFloatingNote) {
        //从主界面切回浮窗
        RecordNoteActionManager.getManager(this).sendDisplayHomepagePopupActions(currentNoteId, lastjsonObject);
        showNoteFloatingDialog(eventFloatingNote.getNoteId());
    }


    MeetingMembersDialog meetingMembersDialog;

    private void showMembersDialog() {
        MeetingKit.getInstance().requestMeetingMembers(meetingConfig, true);
        if (meetingMembersDialog != null) {
            if (meetingMembersDialog.isShowing()) {
                meetingMembersDialog.dismiss();
                meetingMembersDialog = null;
            }
        }
        meetingMembersDialog = new MeetingMembersDialog();
        meetingMembersDialog.init(this, meetingConfig);
        meetingMembersDialog.show(getSupportFragmentManager());
        meetingMembersDialog.setOnMemberDialogDismissListener(new MeetingMembersDialog.OnMemberDialogDismissListener() {
            @Override
            public void OnMemberDialogDismiss() {
                mIsMemberDialogShow = false;
            }
        });
    }


    PopBottomChat chatBottomPop;

    private void showChatPop() {
        String chatRoomId = getResources().getString(R.string.Classroom) + meetingConfig.getLessionId();
        if (chatBottomPop != null) {
            if (chatBottomPop.isShowing()) {
                chatBottomPop.hide();
                chatBottomPop = null;
            }
        }
        String meetingIndetifier = meetingConfig.getMeetingId() + "-" + meetingConfig.getLessionId();
        chatBottomPop = new PopBottomChat(this, meetingIndetifier, chatRoomId);
        chatBottomPop.show(web, chatRoomId);
        ChatManager.getManager(this, meetingIndetifier).setPopBottomChat(chatBottomPop, chatRoomId);
    }

    //--------
    private boolean isCameraCanUse() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)
                && !getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            return false;
        } else {
            return true;
        }
    }

    private File cameraFile;

    private void openCameraForAddDoc() {
        if (!isCameraCanUse()) {
            Toast.makeText(getApplicationContext(), "相机不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String mFilePath = FileUtils.getBaseDir();
        // 文件名
        String fileName = "Kloud_" + DateFormat.format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA))
                + ".jpg";
        cameraFile = new File(mFilePath, fileName);
        //Android7.0文件保存方式改变了
        if (Build.VERSION.SDK_INT < 24) {
            Uri uri = Uri.fromFile(cameraFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, cameraFile.getAbsolutePath());
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        startActivityForResult(intent, REQUEST_CAMEIA_ADD_DOC);
    }

    private void openPickForAddImage() {
        Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.BMP, MimeType.WEBP), false)
                .countable(true)
                .capture(true)
                .captureStrategy(
                        new CaptureStrategy(true, "com.kloudsync.techexcel.FileProvider", "kloud"))
                .maxSelectable(Integer.MAX_VALUE)
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.dp_120))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .setOnSelectedListener((uriList, pathList) -> {
                    Log.e("onSelected", "onSelected: pathList=" + pathList);
                })
                .showSingleMediaType(true)
                .originalEnable(true)
                .maxOriginalSize(10)
                .autoHideToolbarOnSingleTap(true)
                .setOnCheckedListener(isChecked -> {
                    Log.e("isChecked", "onCheck: isChecked=" + isChecked);
                })
                .forResult(REQUEST_CODE_CHOOSE);

    }

    private static final int REQUEST_CAMEIA_ADD_DOC = 1;
    private static final int REQUEST_PICTURE_ADD_DOC = 2;
    private static final int REQUEST_SCAN = 3;
    private static final int REQUEST_CODE_ADD_NOTE = 100;
    private static final int REQUEST_CODE_REQUEST_SELECTED_FILE = 200;
    private static final int REQUEST_SELECTED_MP3_FILE = 201;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMEIA_ADD_DOC:
                    if (cameraFile != null && cameraFile.exists()) {
                        Log.e("onActivityResult", "camera_file:" + cameraFile);
                        uploadFileWhenAddDoc(cameraFile);
                    }
                    break;
                case REQUEST_PICTURE_ADD_DOC:
                    if (data.getData() != null) {
                        File picture = new File(FileUtils.getPath(this, data.getData()));
                        if (picture != null && picture.exists()) {
                            uploadFileWhenAddDoc(picture);
                        }
                    }
                    break;
                case REQUEST_SCAN:
                    if (meetingConfig.getType() == MeetingType.MEETING) {
                        MeetingKit.getInstance().restoreLocalVedeo();
                    }
                    String url = "https://wss.peertime.cn/MeetingServer/tv/change_bind_tv_status?status=1";
                    ServiceInterfaceTools.getinstance().changeBindTvStatus(url, ServiceInterfaceTools.CHANGEBINDTVSTATUS,
                            true, new ServiceInterfaceListener() {
                                @Override
                                public void getServiceReturnData(Object object) {

                                }
                            });
                    break;
                case REQUEST_CODE_ADD_NOTE:
                    String json = data.getStringExtra("OPEN_NOTE_BEAN_JSON");
                    BookNote note = new Gson().fromJson(json, BookNote.class);
                    if (web != null) {
                        uploadNote(note);
                    }
                    drawTempNote();
                    break;
                case REQUEST_CODE_REQUEST_SELECTED_FILE:
                    List<FileEntity> fff = PickerManager.getInstance().files;
                    for (int i = 0; i < fff.size(); i++) {
                        FileEntity fileEntity = fff.get(0);
                        if (!TextUtils.isEmpty(fileEntity.getPath())) {
                            File picture = new File(fileEntity.getPath());
                            if (picture != null && picture.exists()) {
                                uploadFileWhenAddDoc(picture);
                            }
                        }
                    }
                    break;
                case REQUEST_SELECTED_MP3_FILE://
                    List<FileEntity> audioList = PickerManager.getInstance().files;
                    for (int i = 0; i < audioList.size(); i++) {
                        FileEntity fileEntity = audioList.get(0);
                        String path = fileEntity.getPath();
                        Log.e("buildversion", path + "");
                        if (!TextUtils.isEmpty(path)) {
                            String suff = getSuffix(path);
                            if (suff.equals("mp3")) {
                                uploadMp3file(path);
                            } else {
                                Toast.makeText(DocAndMeetingActivity.this, "请选择音频文件上传", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    break;
                case REQUEST_CODE_CHOOSE:
                    List<Uri> uris = Matisse.obtainResult(data);
                    List<String> paths = Matisse.obtainPathResult(data);
                    uploadDocImage(uris, paths);
                    break;
            }
        }
    }


    LocalNoteManager noteManager;

    private void uploadNote(BookNote note) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("ID", note.documentId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("AfterEditBookNote", "jsonObject:" + jsonObject);
        web.loadUrl("javascript:AfterEditBookNote(" + jsonObject + ")", null);
        noteManager = LocalNoteManager.getMgr(this);
        String exportPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "Kloudsyn" + File.separator + "Kloud_" + note.documentId + ".pdf";
        Log.e("upload_note", "link_property:" + meetingConfig.getCurrentLinkProperty());
        noteManager.exportPdfAndUpload(this, note, exportPath, meetingConfig.getDocument().getAttachmentID() + "", meetingConfig.getPageNumber() + "", meetingConfig.getSpaceId(), "0", meetingConfig.getCurrentLinkProperty().toString());
    }

    private void uploadFileWhenAddDoc(File file) {

        AddDocumentTool.addDocumentInDoc(this, file, meetingConfig.getLessionId() + "", new DocumentUploadTool.DocUploadDetailLinstener() {
            @Override
            public void uploadStart() {
//                                Log.e("addDocumentInDoc","uploadStart");
                if (bottomFilePop != null && bottomFilePop.isShowing()) {
                    final MeetingDocument tempDoc = new MeetingDocument();
                    tempDoc.setProgress(0);
                    tempDoc.setTemp(true);
                    tempDoc.setTempDocPrompt("loading");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bottomFilePop.addTempDoc(tempDoc);
                        }
                    });

                }
            }

            @Override
            public void uploadFile(final int progress) {
                Log.e("addDocumentInDoc", "uploadFile:" + progress);
                if (bottomFilePop != null && bottomFilePop.isShowing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bottomFilePop.refreshTempDoc("uploading", progress);

                        }
                    });
                }

            }

            @Override
            public void convertFile(final int progress) {
                Log.e("addDocumentInDoc", "convertFile:" + progress);
                if (bottomFilePop != null && bottomFilePop.isShowing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bottomFilePop.refreshTempDoc("converting", progress);
                        }
                    });
                }

            }

            @Override
            public void uploadFinished(Object result) {
                Log.e("addDocumentInDoc", "uploadFinished:" + result);
                try {
                    JSONObject data = new JSONObject(result.toString());
                    if (data.getInt("RetCode") == 0) {
                        JSONObject document = data.getJSONObject("RetData");
                        if (document != null && document.has("ItemID")) {
                            addDocSucc(document.getInt("ItemID"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void uploadError(String message) {
                Log.e("addDocumentInDoc", "uploadError");
                if (bottomFilePop != null && bottomFilePop.isShowing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new CenterToast.Builder(DocAndMeetingActivity.this).
                                    setSuccess(false).setMessage("operate failed").create().show();
                            bottomFilePop.removeTempDoc();
                        }
                    });

                }

            }
        });
    }

    private UploadFileDialog uploadFileDialog;

    private void uploadMp3file(String path) {
        AddDocumentTool.addDocumentToFavoriteMp3(this, path, meetingConfig.getDocument().getAttachmentID(), new DocumentUploadTool.DocUploadDetailLinstener() {
            @Override
            public void uploadStart() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uploadFileDialog = new UploadFileDialog(DocAndMeetingActivity.this);
                        uploadFileDialog.setTile("uploading");
                        uploadFileDialog.show();

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
                        if (accompanySelectVideoPopup != null) {
                            accompanySelectVideoPopup.notifyUploadSuccess();
                        }
                    }
                });
            }

            @Override
            public void uploadError(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uploadFileDialog != null) {
                            uploadFileDialog.cancel();
                        }
                        Toast.makeText(getApplicationContext(), "add failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void uploadDocImage(List<Uri> uris, List<String> paths) {
        Log.i(TAG, "uirs = " + uris.toString() + "paths = " + paths.toString());
        if (mBottomDocThumbnailDialog != null) {
            mBottomDocThumbnailDialog.uploadDocImageWithHash(uris, paths);
        }
    }

    private String getSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }

    private void reqeustNewBlankPage() {
        Observable.just(meetingConfig).observeOn(Schedulers.io()).doOnNext(new Consumer<MeetingConfig>() {
            @Override
            public void accept(MeetingConfig meetingConfig) throws Exception {
                final JSONObject data = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "EventAttachment/AddBlankPage?lessonID=" +
                        meetingConfig.getLessionId(), null);
//                Log.e("blank_page","result:" + jsonObject);
                if (data.getInt("RetCode") == 0) {
                    JSONObject document = data.getJSONObject("RetData");
                    if (document != null && document.has("ItemID")) {
                        addDocSucc(document.getInt("ItemID"));
                    }
                }
            }
        }).subscribe();
    }

    private void addDocSucc(int newItemid) {
        mSwitchShowDocument = true;
        if (meetingConfig.getType() == MeetingType.MEETING) {
//            if (isPresenter()) {
            DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, newItemid, 1);
//            }
        } else {
            DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, newItemid, 1);
        }
        if (bottomFilePop != null && bottomFilePop.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new CenterToast.Builder(DocAndMeetingActivity.this).
                            setSuccess(true).setMessage("operate success").create().show();
                }
            });

        }
    }


    private DevicesListDialog devicesListDialog;

    private void handleScanTv() {
        ServiceInterfaceTools.getinstance().getBindTvs().enqueue(new Callback<DevicesResponse>() {
            @Override
            public void onResponse(Call<DevicesResponse> call, Response<DevicesResponse> response) {
                if (response != null && response.isSuccessful() && response.body() != null) {
                    if (response.body().getData() != null) {
                        List<TvDevice> devices = response.body().getData().getDeviceList();
                        boolean enable = response.body().getData().isEnableBind();
                        if (devices != null && devices.size() > 0) {
                            devicesListDialog = new DevicesListDialog();
                            devicesListDialog.getPopwindow(DocAndMeetingActivity.this);
                            devicesListDialog.setWebCamPopupListener(new DevicesListDialog.WebCamPopupListener() {
                                @Override
                                public void scanTv() {
                                    gotoScanTv();
                                }

                                @Override
                                public void changeBindStatus(boolean isCheck) {
                                    String url = "https://wss.peertime.cn/MeetingServer/tv/change_bind_tv_status?status=" + (isCheck ? 1 : 0);
                                    ServiceInterfaceTools.getinstance().changeBindTvStatus(url, ServiceInterfaceTools.CHANGEBINDTVSTATUS,
                                            isCheck, new ServiceInterfaceListener() {
                                                @Override
                                                public void getServiceReturnData(Object object) {

                                                }
                                            });
                                }

                                @Override
                                public void openTransfer(TvDevice tvDevice) {
//                                    tvVoice(1, tvDevice);
                                }

                                @Override
                                public void closeTransfer(TvDevice tvDevice) {
//                                    tvVoice(0, tvDevice);
                                }

                                @Override
                                public void logout(TvDevice tvDevice) {
//                                    tvlogout(0, tvDevice);
                                }
                            });
                            devicesListDialog.show(devices, enable);
                        } else {
                            gotoScanTv();
                        }
                    } else {
                        gotoScanTv();
                    }
                } else {
                    gotoScanTv();
                }
            }

            @Override
            public void onFailure(Call<DevicesResponse> call, Throwable t) {
                gotoScanTv();
            }
        });


    }

    private void gotoScanTv() {

        if (!isCameraCanUse()) {
            Toast.makeText(getApplicationContext(), "相机不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("gotoScanTv", "meeting_type:" + meetingConfig.getType());
        if (meetingConfig.getType() == MeetingType.MEETING) {
            MeetingKit.getInstance().templeDisableLocalVideo();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(DocAndMeetingActivity.this, MipcaActivityCapture.class);
                    intent.putExtra("isHorization", true);
                    intent.putExtra("type", 0);
                    startActivityForResult(intent, REQUEST_SCAN);
                }
            }, 500);

        } else {
            Intent intent = new Intent(DocAndMeetingActivity.this, MipcaActivityCapture.class);
            intent.putExtra("isHorization", true);
            intent.putExtra("type", 0);
            startActivityForResult(intent, REQUEST_SCAN);
        }
//        String url = AppConfig.wssServer + "/tv/change_bind_tv_status?status=1";
//        ServiceInterfaceTools.getinstance().changeBindTvStatus(url, ServiceInterfaceTools.CHANGEBINDTVSTATUS,
//                true, new ServiceInterfaceListener() {
//                    @Override
//                    public void getServiceReturnData(Object object) {
//
//                    }
//                });

    }

    SetPresenterDialog setPresenterDialog;

    // --set presenter
    @Override
    public void onMemberClicked(MeetingMember meetingMember) {
        if (setPresenterDialog != null) {
            setPresenterDialog.dismiss();
        }
        setPresenterDialog = new SetPresenterDialog(this);
        setPresenterDialog.show(meetingMember, this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setPresenter(EventSetPresenter setPresenter) {
//        messageManager.sendMessage_MakePresenter(meetingConfig, setPresenter.getMeetingMember());
        Observable.just(setPresenter).observeOn(Schedulers.io()).doOnNext(new Consumer<EventSetPresenter>() {
            @Override
            public void accept(EventSetPresenter eventSetPresenter) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncMakePresenter(eventSetPresenter.getMeetingMember().getUserId() + "");
                if (result.has("code")) {
                    if (result.getInt("code") == 0) {
                        MeetingKit.getInstance().requestMeetingMembers(meetingConfig, true);
                    }
                }
            }
        }).subscribe();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void playSoundtrack(EventPlaySoundtrack soundtrack) {
        Log.e("check_soundtrack_play", "playSoundtrack");
        soundtrackPlayManager.followClose();
        soundtrackPlayManager.initLoading(menuIcon);
        if (soundtrack.getSoundTrack() != null) {
            requestDetailAndPlay(soundtrack.getSoundTrack(), 0);
        }
    }

    private void requestDetailAndPlay(final SoundTrack soundTrack, final long time) {

        Observable.just(soundTrack).observeOn(Schedulers.io()).map(new Function<SoundTrack, SoundtrackDetailData>() {
            @Override
            public SoundtrackDetailData apply(SoundTrack soundtrack) throws Exception {
                SoundtrackDetailData soundtrackDetailData = new SoundtrackDetailData();
                JSONObject response = ServiceInterfaceTools.getinstance().syncGetSoundtrackDetail(soundTrack);
                if (response.has("RetCode")) {
                    if (response.getInt("RetCode") == 0) {
                        SoundtrackDetail soundtrackDetail = new Gson().fromJson(response.getJSONObject("RetData").toString(), SoundtrackDetail.class);
                        soundtrackDetailData.setSoundtrackDetail(soundtrackDetail);
                    }
                }
                return soundtrackDetailData;
            }
        }).doOnNext(new Consumer<SoundtrackDetailData>() {
            @Override
            public void accept(SoundtrackDetailData soundtrackDetailData) throws Exception {
                SoundtrackDetail soundtrackDetail = soundtrackDetailData.getSoundtrackDetail();
                if (soundtrackDetail != null && soundtrackDetail.getNewAudioInfo() != null) {
                    SoundtrackMediaInfo mediaInfo = soundtrackDetail.getNewAudioInfo();
                    if (!TextUtils.isEmpty(mediaInfo.getAttachmentUrl())) {
                        String newUrl = checkUrlForDifferentRegion(mediaInfo.getAttachmentUrl());
                        mediaInfo.setAttachmentUrl(newUrl);

                    }
                }
            }
        }).doOnNext(new Consumer<SoundtrackDetailData>() {
            @Override
            public void accept(SoundtrackDetailData soundtrackDetailData) throws Exception {
                SoundtrackDetail soundtrackDetail = soundtrackDetailData.getSoundtrackDetail();
                if (soundtrackDetail != null && soundtrackDetail.getBackgroudMusicInfo() != null) {
                    SoundtrackMediaInfo mediaInfo = soundtrackDetail.getBackgroudMusicInfo();
                    if (!TextUtils.isEmpty(mediaInfo.getAttachmentUrl())) {
                        String newUrl = checkUrlForDifferentRegion(mediaInfo.getAttachmentUrl());
                        mediaInfo.setAttachmentUrl(newUrl);

                    }
                }
            }
        }).doOnNext(new Consumer<SoundtrackDetailData>() {
            @Override
            public void accept(SoundtrackDetailData soundtrackDetailData) throws Exception {
                SoundtrackDetail soundtrackDetail = soundtrackDetailData.getSoundtrackDetail();
                if (soundtrackDetail != null && soundtrackDetail.getSelectedAudioInfo() != null) {
                    SoundtrackMediaInfo mediaInfo = soundtrackDetail.getSelectedAudioInfo();
                    if (!TextUtils.isEmpty(mediaInfo.getAttachmentUrl())) {
                        String newUrl = checkUrlForDifferentRegion(mediaInfo.getAttachmentUrl());
                        mediaInfo.setAttachmentUrl(newUrl);
                    }
                }

            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<SoundtrackDetailData>() {
            @Override
            public void accept(SoundtrackDetailData soundtrackDetailData) throws Exception {

                if (soundtrackDetailData.getSoundtrackDetail() != null) {
                    SoundtrackDetail soundtrackDetail = soundtrackDetailData.getSoundtrackDetail();
                    SoundtrackMediaInfo newAudioInfo = soundtrackDetail.getNewAudioInfo();
                    SoundtrackMediaInfo backgroundAudioInfo = soundtrackDetail.getBackgroudMusicInfo();
                    if (newAudioInfo == null && backgroundAudioInfo == null) {
                        new AlertDialog.Builder(DocAndMeetingActivity.this, R.style.ThemeAlertDialog)
                                .setMessage(getResources().getString(R.string.sound_is_still_preparing_and_cannot_do_this))
                                .setNegativeButton(getResources().getText(R.string.know_the), null)
                                .show();
                        soundtrackPlayManager.followClose();
                        return;
                    }

                    if (soundtrackPlayManager.isClosed()) {
                        return;
                    }

                    soundtrackPlayManager.setSoundtrackDetail(soundtrackDetailData.getSoundtrackDetail());

                    SoundtrackMediaInfo soundtrackMediaInfo = soundtrackDetailData.getSoundtrackDetail().getNewAudioInfo();

                    if (soundtrackMediaInfo == null) {
                        soundtrackMediaInfo = soundtrackDetailData.getSoundtrackDetail().getBackgroudMusicInfo();
                        soundtrackMediaInfo.setMediaType(SoundtrackMediaInfo.MEDIA_TYPE_BACKGROUND);

                    } else {
                        soundtrackMediaInfo.setMediaType(SoundtrackMediaInfo.MEDIA_TYPE_NEW_AUDIO);
                    }

                    if (soundtrackMediaInfo == null) {
                        soundtrackMediaInfo = soundtrackDetailData.getSoundtrackDetail().getSelectedAudioInfo();
                        soundtrackMediaInfo.setMediaType(SoundtrackMediaInfo.MEDIA_TYPE_BACKGROUND);
                    }

                    if (soundtrackMediaInfo == null) {
                        ToastUtils.showInCenter(DocAndMeetingActivity.this, "音想数据异常", "播放失败");
                        soundtrackPlayManager.followClose();
                    } else {
                        soundtrackPlayManager.doPlay(soundtrackMediaInfo, time);
                    }


                } else {
                    ToastUtils.showInCenter(DocAndMeetingActivity.this, "音想数据异常", "播放失败");
                    soundtrackPlayManager.followClose();

                }
            }
        }).subscribe();
    }

    private void requestDetailAndPause(final SoundTrack soundTrack, final long time) {

        Observable.just(soundTrack).observeOn(Schedulers.io()).map(new Function<SoundTrack, SoundtrackDetailData>() {
            @Override
            public SoundtrackDetailData apply(SoundTrack soundtrack) throws Exception {
                SoundtrackDetailData soundtrackDetailData = new SoundtrackDetailData();
                JSONObject response = ServiceInterfaceTools.getinstance().syncGetSoundtrackDetail(soundTrack);
                if (response.has("RetCode")) {
                    if (response.getInt("RetCode") == 0) {
                        SoundtrackDetail soundtrackDetail = new Gson().fromJson(response.getJSONObject("RetData").toString(), SoundtrackDetail.class);
                        soundtrackDetailData.setSoundtrackDetail(soundtrackDetail);
                    }
                }
                return soundtrackDetailData;
            }
        }).doOnNext(new Consumer<SoundtrackDetailData>() {
            @Override
            public void accept(SoundtrackDetailData soundtrackDetailData) throws Exception {
                SoundtrackDetail soundtrackDetail = soundtrackDetailData.getSoundtrackDetail();
                if (soundtrackDetail != null && soundtrackDetail.getNewAudioInfo() != null) {
                    SoundtrackMediaInfo mediaInfo = soundtrackDetail.getNewAudioInfo();
                    if (!TextUtils.isEmpty(mediaInfo.getAttachmentUrl())) {
                        Log.e(TAG, "attachmenturl:" + mediaInfo.getAttachmentUrl());
                        String newUrl = checkUrlForDifferentRegion(mediaInfo.getAttachmentUrl());
                        mediaInfo.setAttachmentUrl(newUrl);

                    }
                }
            }
        }).doOnNext(new Consumer<SoundtrackDetailData>() {
            @Override
            public void accept(SoundtrackDetailData soundtrackDetailData) throws Exception {
                SoundtrackDetail soundtrackDetail = soundtrackDetailData.getSoundtrackDetail();
                if (soundtrackDetail != null && soundtrackDetail.getBackgroudMusicInfo() != null) {
                    SoundtrackMediaInfo mediaInfo = soundtrackDetail.getBackgroudMusicInfo();
                    if (!TextUtils.isEmpty(mediaInfo.getAttachmentUrl())) {
                        Log.e(TAG, "attachmenturl:" + mediaInfo.getAttachmentUrl());
                        String newUrl = checkUrlForDifferentRegion(mediaInfo.getAttachmentUrl());
                        mediaInfo.setAttachmentUrl(newUrl);

                    }
                }
            }
        }).doOnNext(new Consumer<SoundtrackDetailData>() {
            @Override
            public void accept(SoundtrackDetailData soundtrackDetailData) throws Exception {
                SoundtrackDetail soundtrackDetail = soundtrackDetailData.getSoundtrackDetail();
                if (soundtrackDetail != null && soundtrackDetail.getSelectedAudioInfo() != null) {
                    SoundtrackMediaInfo mediaInfo = soundtrackDetail.getSelectedAudioInfo();
                    if (!TextUtils.isEmpty(mediaInfo.getAttachmentUrl())) {
                        Log.e(TAG, "attachmenturl:" + mediaInfo.getAttachmentUrl());
                        String newUrl = checkUrlForDifferentRegion(mediaInfo.getAttachmentUrl());
                        mediaInfo.setAttachmentUrl(newUrl);
                    }
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<SoundtrackDetailData>() {
            @Override
            public void accept(SoundtrackDetailData soundtrackDetailData) throws Exception {

                if (soundtrackDetailData.getSoundtrackDetail() != null) {
                    SoundtrackDetail soundtrackDetail = soundtrackDetailData.getSoundtrackDetail();
                    SoundtrackMediaInfo newAudioInfo = soundtrackDetail.getNewAudioInfo();
                    SoundtrackMediaInfo backgroundAudioInfo = soundtrackDetail.getBackgroudMusicInfo();
                    if (newAudioInfo == null && backgroundAudioInfo == null) {
                        new AlertDialog.Builder(DocAndMeetingActivity.this, R.style.ThemeAlertDialog)
                                .setMessage(getResources().getString(R.string.sound_is_still_preparing_and_cannot_do_this))
                                .setNegativeButton(getResources().getText(R.string.know_the), null)
                                .show();
                        soundtrackPlayManager.followClose();
                        return;
                    }

                    if (soundtrackPlayManager.isClosed()) {
                        return;
                    }

                    soundtrackPlayManager.setSoundtrackDetail(soundtrackDetailData.getSoundtrackDetail());

                    SoundtrackMediaInfo soundtrackMediaInfo = soundtrackDetailData.getSoundtrackDetail().getNewAudioInfo();

                    if (soundtrackMediaInfo == null) {
                        soundtrackMediaInfo = soundtrackDetailData.getSoundtrackDetail().getBackgroudMusicInfo();
                        soundtrackMediaInfo.setMediaType(SoundtrackMediaInfo.MEDIA_TYPE_BACKGROUND);

                    } else {
                        soundtrackMediaInfo.setMediaType(SoundtrackMediaInfo.MEDIA_TYPE_NEW_AUDIO);
                    }

                    if (soundtrackMediaInfo == null) {
                        soundtrackMediaInfo = soundtrackDetailData.getSoundtrackDetail().getSelectedAudioInfo();
                        soundtrackMediaInfo.setMediaType(SoundtrackMediaInfo.MEDIA_TYPE_BACKGROUND);
                    }

                    if (soundtrackMediaInfo == null) {
                        ToastUtils.showInCenter(DocAndMeetingActivity.this, "音想数据异常", "播放失败");
                        soundtrackPlayManager.followClose();
                    } else {
                        soundtrackPlayManager.doPause(soundtrackMediaInfo, time);
                    }


                } else {
                    ToastUtils.showInCenter(DocAndMeetingActivity.this, "音想数据异常", "播放失败");
                    soundtrackPlayManager.followClose();

                }
            }
        }).subscribe();
    }


    SoundtrackPlayManager soundtrackPlayManager;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeViewNote(EventCloseNoteView closeNoteView) {
        currentNoteId = 0;
        noteWeb.setVisibility(View.GONE);
        noteWeb.loadUrl("javascript:ClearPath()", null);
        newNoteDatas.clear();
        menuIcon.setVisibility(View.VISIBLE);
        notifyDocumentChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openNoteByEvent(EventOpenNote openNote) {
        openNote(openNote.getNoteId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void presenterChanged(EventPresnterChanged presnterChanged) {
        handleWebUISetting();
        if (soundtrackPlayManager != null) {
            soundtrackPlayManager.changeSeekbarStatusByRole();
        }
        MeetingKit.getInstance().refreshMeetingMenu();
        if (mIsMeetingPause) {
            if (meetingConfig.getSystemType() == AppConfig.COMPANY_MODEL) {//如果是会议模式
                MeetingPauseManager.getInstance(this, meetingConfig).setTipInfo(getString(R.string.meeting_pause_tips));
            } else {//如果是课堂模式
                MeetingPauseManager.getInstance(this, meetingConfig).setTipInfo(getString(R.string.class_pause_tips));
            }
            MeetingPauseManager.getInstance(this, meetingConfig).showBigLayout();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void createSync(EventCreateSync createSync) {
        if (meetingConfig.getSystemType() == 1) {  //教育
        } else {
            openSaveVideoPopup();
        }
        String[] permissions = new String[]{
                Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS};
        startRequestPermission(permissions, 321);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showFullAgoraItem(EventShowFullAgora fullAgora) {
        notifyAgoraVedioScreenStatus(2, fullAgora.getAgoraMember().getUserId() + "");
        fullCamereLayout.setVisibility(View.GONE);
        fullCameraList.setVisibility(View.GONE);
        showAgoraFull(fullAgora.getAgoraMember(), meetingConfig);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleMemberRoleChanged(EventRoleChanged roleChanged) {
        Log.e("check_event_role_change", "role_changed");
        if ((roleChanged.getAgoraMember().getUserId() + "").equals(AppConfig.UserID)) {
            handleAgoraMemberRoleChanged(roleChanged.getAgoraMember());
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleShareDocInMeeting(EventShareDocInMeeting shareDocInMeeting) {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            return;
        }
        requestShareDocInMeeting(shareDocInMeeting.getDocument());

    }

    private void requestShareDocInMeeting(final MeetingDocument document) {
        Observable.just("Requst").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {

            @Override
            public JSONObject apply(String s) throws Exception {
                JSONObject params = new JSONObject();
                params.put("lessonID", meetingConfig.getLessionId());
                params.put("itemIDs", document.getItemID());
                JSONObject result = ConnectService.submitDataByJson
                        (AppConfig.URL_PUBLIC + "EventAttachment/UploadFromFavorite?lessonID=" + meetingConfig.getLessionId() + "&itemIDs=" + document.getItemID(), params);
                Log.e("check_request_share_doc", "result:" + result);
                return result;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if (jsonObject.has("RetCode")) {
                    if (jsonObject.getInt("RetCode") == 0) {
                        new CenterToast.Builder(DocAndMeetingActivity.this).setSuccess(true).setMessage(getString(R.string.operate_success)).create().show();
                    } else {
                        new CenterToast.Builder(DocAndMeetingActivity.this).setSuccess(false).setMessage(getString(R.string.operate_failure)).create().show();

                    }
                } else {
                    new CenterToast.Builder(DocAndMeetingActivity.this).setSuccess(false).setMessage(getString(R.string.operate_failure)).create().show();

                }
            }
        }).subscribe();

    }

    private void handleAgoraMemberRoleChanged(AgoraMember agoraMember) {
        if (cameraList.getVisibility() == View.VISIBLE) {
            if (cameraAdapter != null) {
                if (agoraMember.getIsMember() == 1) {
                    cameraAdapter.addUser(agoraMember);
                } else {
                    cameraAdapter.removeUser(agoraMember);
                }
            }
        }

        if (fullCameraList.getVisibility() == View.VISIBLE) {
            if (fullCameraAdapter != null) {
                if (agoraMember.getIsMember() == 1) {
                    fullCameraAdapter.addUser(agoraMember);
                } else {
                    fullCameraAdapter.removeUser(agoraMember);
                }
            }
        }

        boolean isMicroOn = MeetingSettingCache.getInstance(this).getMeetingSetting().isMicroOn();
        boolean isCameraOn = MeetingSettingCache.getInstance(this).getMeetingSetting().isCameraOn();
        if (meetingConfig.getRole() == MeetingConfig.MeetingRole.HOST || meetingConfig.getRole() == MeetingConfig.MeetingRole.MEMBER) {
            messageManager.sendMessage_AgoraStatusChange(meetingConfig, isMicroOn, isCameraOn);
            return;
        }
    }

//    private CreateSyncDialog createSyncDialog;
//
//    private void showCreateSyncDialog() {
//        if (createSyncDialog != null) {
//            if (createSyncDialog.isShowing()) {
//                createSyncDialog.dismiss();
//                createSyncDialog = null;
//            }
//        }
//        createSyncDialog = new CreateSyncDialog(this);
//        createSyncDialog.show(meetingConfig.getDocument().getAttachmentID() + "");
//    }

    private YinxiangCreatePopup yinxiangCreatePopup;
    private int isrecord = 0; //判斷是背景音頻還是新的聲音
    private SoundtrackRecordManager soundtrackRecordManager;

    private void showCreateSyncDialog() {
        yinxiangCreatePopup = new YinxiangCreatePopup();
        yinxiangCreatePopup.getPopwindow(DocAndMeetingActivity.this);
        yinxiangCreatePopup.setFavoritePoPListener(new YinxiangCreatePopup.FavoritePoPListener() {
            @Override
            public void addrecord(int ii) {
                if (favoritePopup != null) {
                    isrecord = ii;
                    favoritePopup.StartPop(web);
                    favoritePopup.setData(3, true);
                }
            }

            @Override
            public void addaudio(int ii) {
                if (favoritePopup != null) {
                    isrecord = ii;
                    favoritePopup.StartPop(web);
                    favoritePopup.setData(3, true);
                }
            }

            @Override
            public void syncorrecord(boolean checked, SoundtrackBean soundtrackBean2) {  //录制音响
                soundtrackRecordManager = SoundtrackRecordManager.getManager(DocAndMeetingActivity.this);
                soundtrackRecordManager.setInitParams(checked, soundtrackBean2, audiosyncll, web, timeshow, meetingConfig);
            }
        });
        if (meetingConfig.getDocument() == null) {
            ToastUtils.show(this, R.string.no_document);
            return;
        }
        yinxiangCreatePopup.StartPop(web, meetingConfig.getDocument().getAttachmentID() + "");
    }


    private AccompanyCreatePopup accompanyCreatePopup;

    private void showCreateAccompanyDialog() {
        accompanyCreatePopup = new AccompanyCreatePopup();
        accompanyCreatePopup.getPopwindow(DocAndMeetingActivity.this);
        accompanyCreatePopup.setFavoritePoPListener(new AccompanyCreatePopup.FavoritePoPListener() {
            @Override
            public void addrecord(int ii) {
                isrecord = ii;
                openAccompanyMusicPop(meetingConfig.getRole());
            }

            @Override
            public void addaudio(int ii) {
                isrecord = ii;
                openAccompanyMusicPop(meetingConfig.getRole());
            }

            @Override
            public void syncorrecord(boolean checked, SoundtrackBean soundtrackBean) {  //录制音响
                soundtrackRecordManager = SoundtrackRecordManager.getManager(DocAndMeetingActivity.this);
                soundtrackRecordManager.setInitParams(checked, soundtrackBean, audiosyncll, web, timeshow, meetingConfig);
            }
        });
        if (meetingConfig.getDocument() == null) {
            ToastUtils.show(this, R.string.no_document);
            return;
        }
        accompanyCreatePopup.StartPop(web, meetingConfig.getDocument().getAttachmentID() + "");
    }

    private AccompanySelectVideoPopup accompanySelectVideoPopup;

    private void openAccompanyMusicPop(int role) {
        if (accompanySelectVideoPopup == null) {
            accompanySelectVideoPopup = new AccompanySelectVideoPopup(DocAndMeetingActivity.this);
            accompanySelectVideoPopup.setFavoritePoPListener(new AccompanySelectVideoPopup.FavoriteVideoPoPListener() {
                @Override
                public void save(Document document) {  //选择伴奏带
                    if (accompanyCreatePopup != null) {
                        if (isrecord == 0) {
                            accompanyCreatePopup.setAudioBean(document);
                        } else if (isrecord == 1) {
                            accompanyCreatePopup.setRecordBean(document);
                        }
                    }
                }

                @Override
                public void save1(SoundTrack soundTrack) {  // 选择伴奏音乐
                    if (accompanyCreatePopup != null) {
                        if (isrecord == 0) {
                            accompanyCreatePopup.setAudioBean1(soundTrack);
                        } else if (isrecord == 1) {
                            accompanyCreatePopup.setRecordBean1(soundTrack);

                        }
                    }
                }

                @Override
                public void uploadFile() {  //上传音频文件
                    Intent intent = new Intent(DocAndMeetingActivity.this, FilePickerActivity.class);
                    intent.putExtra("fileType", 1);
                    startActivityForResult(intent, REQUEST_SELECTED_MP3_FILE);
                }
            });
        }
        accompanySelectVideoPopup.StartPop(meetingConfig, role);
    }


    private void startRequestPermission(String[] permissions, int requestcode) {
        ActivityCompat.requestPermissions(this, permissions, requestcode);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean i = shouldShowRequestPermissionRationale(permissions[0]);
                    boolean j = shouldShowRequestPermissionRationale(permissions[1]);
                    if (!i || !j) {
                        // 提示用户去应用设置界面手动开启权限
//                       showDialogTipUserGoToAppSettting();
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.necessary_permissions_not_enabled), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (meetingConfig.getSystemType() == 1) {
                        showCreateAccompanyDialog();
                    } else {
                        showCreateSyncDialog();
                    }
                }
            }
        } else if (requestCode == 322) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getResources().getString(R.string.necessary_permissions_not_enabled), Toast.LENGTH_SHORT).show();
                } else {
                    openCameraForAddDoc();
                }
            }
        } else if (requestCode == 323) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getResources().getString(R.string.necessary_permissions_not_enabled), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_PICTURE_ADD_DOC);
                }
            }
        } else if (requestCode == 324) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getResources().getString(R.string.necessary_permissions_not_enabled), Toast.LENGTH_SHORT).show();
            } else {
                openPickForAddImage();
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveEventSoundSync(EventSoundSync eventSoundSync) {
        Log.e("syncing---", eventSoundSync.getSoundtrackID() + "  " + eventSoundSync.getStatus() + "  " + eventSoundSync.getTime());
        int soundtrackID = eventSoundSync.getSoundtrackID();
        if (eventSoundSync.getStatus() == 1) { //开始录制
            meetingConfig.setSyncing(true);
            getJspPagenumber();
//            messageManager.sendMessage_audio_sync(meetingConfig, eventSoundSync);
            recordstatus.setVisibility(View.VISIBLE);
            //判断笔记是否打开
            if (noteLayout.getVisibility() == View.VISIBLE) {
                if (noteWeb != null) {
                    // 笔记先于音想打开
                    RecordNoteActionManager.getManager(DocAndMeetingActivity.this).sendDisplayHomePageActions(currentNoteId, lastjsonObject);
                }
            } else {
                if (mFloatingWindowNoteManager != null) {
                    if (mFloatingWindowNoteManager.isShowing()) {
                        mFloatingWindowNoteManager.displayPopupActions();
                    }
                }
            }
        } else if (eventSoundSync.getStatus() == 0) {   //录音结束
            recordstatus.setVisibility(View.GONE);
            meetingConfig.setSyncing(false);
            //清除最后一页上的数据
            web.loadUrl("javascript:ClearPageAndAction()", null);
            PageActionsAndNotesMgr.requestActionsAndNote(meetingConfig);
//            JSONObject jsonObject=new JSONObject();
//            try {
//                jsonObject.put("id",12);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            soundtrackRecordManager.recordNoteAction(NoteRecordType.CLOSE_POPUP_NOTE,jsonObject);
        }
        PrivateCoachManager.getManager(this).updateSyncPrivateCoaching(meetingConfig);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventTelephone(TelePhoneCall telePhoneCall) {
        boolean isTelephoneComing = telePhoneCall.isCall();
        if (meetingConfig.isSyncing()) { //录制音想模式
            if (isTelephoneComing) { //电话进来了  停止录音
                if (soundtrackRecordManager != null) {
                    soundtrackRecordManager.pause();
                }
            } else {  //挂断电话  开始录音
                if (soundtrackRecordManager != null) {
                    soundtrackRecordManager.resume();
                }
            }
        }
    }

    private void getJspPagenumber() {

        web.evaluateJavascript("javascript:GetCurrentPageNumber()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                int id = 1;
                if (!TextUtils.isEmpty(s)) {
                    Log.e("syncing---", s + "");
                    id = Integer.parseInt(s);
                }
                JSONObject js = new JSONObject();
                try {
                    js.put("type", 2);
                    js.put("page", id);
                    js.put("time", 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                messageManager.sendMessage_MyActionFrame(js.toString(), meetingConfig);
            }
        });
    }


    private int currentPosition = -1;
    private FavoriteVideoPopup favoritePopup;

    private void openSaveVideoPopup() {
        favoritePopup = new FavoriteVideoPopup(this);
        favoritePopup.setFavoritePoPListener(new FavoriteVideoPopup.FavoriteVideoPoPListener() {
            @Override
            public void dismiss() {

            }

            @Override
            public void open() {

            }

            @Override
            public void selectFavorite(int position) {
                currentPosition = position;
            }

            @Override
            public void cancel() {

            }

            @Override
            public void save(int type, boolean isYinxiang) {
                if (isYinxiang) {
                    if (yinxiangCreatePopup != null && currentPosition >= 0) {
                        if (isrecord == 0) {
                            yinxiangCreatePopup.setAudioBean(favoritePopup.getData().get(currentPosition));
                        } else if (isrecord == 1) {
                            yinxiangCreatePopup.setRecordBean(favoritePopup.getData().get(currentPosition));
                        }
                    }
                }
            }

            @Override
            public void uploadFile(int type) {
                Intent intent = new Intent(DocAndMeetingActivity.this, FilePickerActivity.class);
                intent.putExtra("fileType", 1);
                startActivityForResult(intent, REQUEST_SELECTED_MP3_FILE);
            }

        });
    }


    private void openNote(String noteId) {
        BookNote bookNote = null;
        if (TextUtils.isEmpty(noteId)) {
            bookNote = new BookNote().setTitle("new note").setJumpBackToNote(false);
        } else {
            bookNote = new BookNote().setDocumentId(noteId).setJumpBackToNote(false);
            if (!QueryLocalNoteTool.noteIsExist(this, noteId)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DocAndMeetingActivity.this, "笔记在本地设备不存在", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
        }

        Intent intent = new Intent();
        intent.putExtra("OPEN_NOTE_BEAN", new Gson().toJson(bookNote));
        ComponentName comp = new ComponentName("com.onyx.android.note", "com.onyx.android.note.note.ui.ScribbleActivity");
        intent.setComponent(comp);
        startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
    }

    private SoundtrackPlayDialog soundtrackPlayDialog;

    private void showSoundtrackPlayDialog(SoundtrackDetail soundtrackDetail) {

        if (soundtrackPlayDialog != null) {
            if (soundtrackPlayDialog.isShowing()) {
                soundtrackPlayDialog.dismiss();
                soundtrackPlayDialog = null;
            }
        }

        soundtrackPlayDialog = new SoundtrackPlayDialog(this, soundtrackDetail, meetingConfig);
        soundtrackPlayDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handleExit(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            handleExit(false);
//            detectPopwindow();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    //------Camera vedio options

    /**
     * 所有成员头像模式
     *
     * @param member
     */
    @Override
    public void onCameraFrameClick(View itemView, AgoraMember member, int position) {
        Log.e("check_selecr_coach", "position:" + position + ",system_type:" + meetingConfig.getSystemType() + ",isMeetingPause:" + meetingConfig.isMeetingPause());
        if (position == 0) {
            showWebcamOtions(itemView);
        } else {
            if (meetingConfig.isMeetingPause()) {
                if (meetingConfig.isCoaching()) {
                    // 当前private coach的人
                    showPopPrivateCoachOperations(itemView, member);
                } else {
                    doSelectPrivateCoach(member, position);
                }
            } else {
                doSelecteSpeaker(member, position);
            }
        }

    }

    private void doSelecteSpeaker(AgoraMember member, int position) {
        if (isPresenter()) {
            if (member.isSelect()) {
                // 点名
                Log.e("doSelecteSpeaker", "dianming");
                selectSpeakerCount = 0;
                rejoinHandler.removeMessages(MESSAGE_CHECK_SELECTED_SPEAKER);
                Log.e("onCameraFrameClick", "select_member");
                SocketMessageManager.getManager(this).sendMessage_SelectSpeaker(member.getUserId() + "");
//                sharedPreferences.edit().putInt("display_mode", 0).commit();
                member.setSelect(false);
                cameraAdapter.notifyItemChanged(position);
                showSelectSpeaker(member.getUserId() + "");
            } else {
                Log.e("doSelecteSpeaker", "set_select_true");
                cameraAdapter.clearSelectedMember();
                member.setSelect(true);
                cameraAdapter.notifyItemChanged(position);
            }
        }
    }


    private void doSelectPrivateCoach(AgoraMember member, int position) {
        if (isPresenter()) {
            if (meetingConfig.isSyncing()) {
                Toast.makeText(this, "当前正在录制音想，请先停止录制音想", Toast.LENGTH_LONG).show();
                return;
            }
            if (soundtrackPlayManager != null) {
                if (soundtrackPlayManager.isPlaying()) {
                    Toast.makeText(this, "当前正在播放音想，请先停止播放音想", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            if (member.isSelectedCoach()) {
                processSelectProvateCoach(member, position);
            } else {
                Log.e("check_selecr_coach", "select_member_for_coach:" + member);
                cameraAdapter.clearSelectedCoachMember();
                member.setSelectedCoach(true);
                cameraAdapter.notifyItemChanged(position);
            }
        }
    }

    private void processSelectProvateCoach(final AgoraMember member, final int position) {
        Observable.just(member).observeOn(Schedulers.io()).map(new Function<AgoraMember, JSONObject>() {
            @Override
            public JSONObject apply(final AgoraMember member) throws Exception {
                JSONObject params = new JSONObject();
                params.put("inviteUserId", member.getUserId());
                return ServiceInterfaceTools.getinstance().syncRequstPrivateCoachingStart(params);
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if (jsonObject != null && jsonObject.has("code")) {
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        //开启一对一成功
                        PrivateCoachManager.getManager(DocAndMeetingActivity.this).startPrivateCoachSuccess(meetingConfig, member, cameraAdapter, mMeetingPauseManager, coachLayout);
                        justHandleDisplayMode(0);
                    } else {
                        member.setSelectedCoach(false);
                        cameraAdapter.notifyItemChanged(position);
                    }
                }
            }
        }).subscribe();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverEventEndCoach(EventEndCoach eventEndCoach) {
        PrivateCoachManager.getManager(this).endCoach(meetingConfig, cameraAdapter, mMeetingPauseManager);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverEventTalkAllStu(EventTalkAllStu eventTalkAllStu) {  //显示Talk all Students
        PrivateCoachManager.getManager(this).stopTalkAllStudents(meetingConfig, talkallstudent);
    }


    PopMeetingWebcamOptions popWebcamOptions;

    private void showWebcamOtions(View view) {
        if (popWebcamOptions != null) {
            if (popWebcamOptions.isShowing()) {
                popWebcamOptions.dismiss();
                popWebcamOptions = null;
            }
        }

        popWebcamOptions = new PopMeetingWebcamOptions(this);
        popWebcamOptions.setOnDisplayModeChanged(this);
        popWebcamOptions.show(view, meetingConfig);
    }


    PopPrivateCoachOperations popPrivateCoachOperations;

    private void showPopPrivateCoachOperations(View view, AgoraMember agoraMember) {
        if (popPrivateCoachOperations != null) {
            if (popPrivateCoachOperations.isShowing()) {
                popPrivateCoachOperations.dismiss();
                popWebcamOptions = null;
            }
        }

        popPrivateCoachOperations = new PopPrivateCoachOperations(this);
        popPrivateCoachOperations.setOnCoachOperationsListener(this);
        popPrivateCoachOperations.show(view, meetingConfig, agoraMember);
    }

    @Override
    public void onInviteAudienceClicked(AgoraMember agoraMember) {
        PrivateCoachManager.getManager(this).processInviteProvateCoachAudience(agoraMember, cameraAdapter);
    }

    @Override
    public void onRemoveMemberClicked(AgoraMember agoraMember) {
        PrivateCoachManager.getManager(this).removePrivateCoachMemberCoach(meetingConfig, cameraAdapter, mMeetingPauseManager, agoraMember.getUserId());
    }


    private void handleFullScreenCamera(AgoraCameraAdapterV2 cameraAdapter, boolean notify) {
        if (cameraAdapter == null || cameraAdapter.getUsers().size() == 0) {
            return;
        }
        if (notify) {
            notifyAgoraVedioScreenStatus(1, "");
        }
        fullCamereLayout.setVisibility(View.VISIBLE);
        fullCameraList.setVisibility(View.VISIBLE);
        mIsVideoMeeting = true;
        cameraList.setVisibility(View.GONE);

        List<AgoraMember> copyMembers = new ArrayList<>();
        for (AgoraMember member : meetingConfig.getAgoraMembers()) {
            if (member.getIsMember() == 1) {
                if (!copyMembers.contains(member)) {
                    copyMembers.add(member);
                }
            }
        }

        fullCameraAdapter = new FullAgoraCameraAdapter(this, meetingConfig);
        fullCameraAdapter.setMembers(copyMembers);
        fitFullCameraList(copyMembers);
        fullCameraList.setAdapter(fullCameraAdapter);
        MeetingKit.getInstance().setFullCameraAdapter(fullCameraAdapter);
//        showFullCameraScreen();
    }


    private void showFullCameraScreen() {
        notifyAgoraVedioScreenStatus(1, "");
        fullCamereLayout.setVisibility(View.VISIBLE);
        fullCameraList.setVisibility(View.VISIBLE);
        cameraList.setVisibility(View.GONE);
        reloadAgoraMember();
    }

    private void hideFullCameraScreen() {
        fullCamereLayout.setVisibility(View.GONE);
        fullCameraList.setVisibility(View.GONE);
        cameraList.setVisibility(View.VISIBLE);
        reloadAgoraMember();
        justHandleDisplayMode(meetingConfig.getCameraDiplayMode());
        notifyAgoraVedioScreenStatus(0, "");
    }

    private void fitFullCameraList(List<AgoraMember> members) {

        final int size = members.size();
        if (size == 1) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
            fullCameraList.setLayoutManager(gridLayoutManager);
        } else if (size > 1 && size <= 4) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
//            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                @Override
//                public int getSpanSize(int position) {  // 这个方法返回的是当前位置的 item 跨度大小
//                    if(size==3){
//                        if(position==size-1){
//                            return 2;
//                        }
//                    }
//                    return 1;
//                }
//            });
            fullCameraList.setLayoutManager(gridLayoutManager);
        } else if (size > 4 && size <= 9) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
            fullCameraList.setLayoutManager(gridLayoutManager);
        } else if (size > 9) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
            fullCameraList.setLayoutManager(gridLayoutManager);
        }
        GridLayoutManager s = (GridLayoutManager) fullCameraList.getLayoutManager();
        int currentSpanCount = s.getSpanCount();
        Log.e("fitFullCameraList", "span:" + currentSpanCount);
    }

    // ----handle_message

    private void handleMessageSendMessage(JSONObject data) throws JSONException {
        if (!data.has("actionType")) {
            return;
        }

        switch (data.getInt("actionType")) {
            case 8:
                if (data.has("docType")) {
                    int docType = data.getInt("docType");
                    switch (docType) {
                        case 1:
                            // 切换笔记
                            followShowNote(data.getInt("itemId"));
                            break;
                        case 2:
                            // 切换文档
                        default:
                            if (noteLayout.getVisibility() == View.VISIBLE) {
                                noteWeb.setVisibility(View.GONE);
                                noteLayout.setVisibility(View.GONE);
                            }
                            followChangeDocument(data.getInt("itemId"), Integer.parseInt(data.getString("pageNumber")));
                            break;
                    }

                } else {
                    if (noteLayout.getVisibility() == View.VISIBLE) {
                        noteWeb.loadUrl("javascript:ClearPath()", null);
                        noteWeb.setVisibility(View.GONE);
                        noteLayout.setVisibility(View.GONE);
                    }
                    followChangeDocument(data.getInt("itemId"), Integer.parseInt(data.getString("pageNumber")));
                }
                break;
            case 9:
                if (data.has("videoMode")) {
                    String mode = data.getString("videoMode");
                    meetingConfig.setMode(Integer.parseInt(mode));
                    int _mode = meetingConfig.getMode();
                    if (_mode == 3) {
                        // web分享屏幕模式
                        Log.e("check_share_screen", "data:" + data + "，uid:" + meetingConfig.getShareScreenUid() + ",mode:" + meetingConfig.getMode() + ",post_share_screen");
                        MeetingKit.getInstance().postShareScreen(meetingConfig.getShareScreenUid());
                        hideFullCameraScreen();
                        hideAgoraFull();
                    } else if (_mode == 2) {
                        // 一个人视频全屏模式
                        String userID = data.getString("currentSessionID");
                        meetingConfig.setCurrentMaxVideoUserId(userID);
                        for (int i = 0; i < meetingConfig.getAgoraMembers().size(); i++) {
                            if ((meetingConfig.getAgoraMembers().get(i).getUserId() + "").equals(userID)) {
                                fullCamereLayout.setVisibility(View.GONE);
                                fullCameraList.setVisibility(View.GONE);
                                cameraList.setVisibility(View.GONE);
                                showAgoraFull(meetingConfig.getAgoraMembers().get(i), meetingConfig);
                                break;
                            }
                        }
                    } else if (_mode == 1) {
                        // 全屏显示所有人视频模式
                        hideAgoraFull2();
                        handleFullScreenCamera(cameraAdapter, false);
                    } else if (meetingConfig.getMode() == 0) {
                        hideFullCameraScreen();
                        hideAgoraFull();
                    }
                    MeetingKit.getInstance().setEncoderConfigurationBaseMode();
                } else {
                    hideFullCameraScreen();
                    meetingConfig.setShareScreenUid(0);
                }

                break;
            case 14:
                //Log.e("check_mute_audio", "data:" + data);
                if (data.has("stat")) {
                    int stat = data.getInt("stat");
                    if (stat == 0) {
                        MeetingKit.getInstance().menuMicroClicked(false);
                    } else if (stat == 1) {
//                        MeetingKit.getInstance().menuMicroClicked(true);
                    }
                }

                break;
            case 19:
                Log.e("check_vedio_play", "data:" + data);
                DocVedioManager vedioManager = DocVedioManager.getInstance(this);
                if (data.has("stat")) {
                    int stat = data.getInt("stat");
                    VedioData vedioData = new VedioData();
                    vedioData.setId(data.getInt("vid"));
                    vedioData.setUrl(data.getString("url"));
//                    vedioData.setUrl(data.getString("url"));
                    if (stat == 0) {
                        vedioManager.doPause();
                    } else if (stat == 1) {
                        vedioManager.doPlay(this, vedioLayout, meetingConfig, vedioData);
                    } else if (stat == 2) {
                        // close
                        vedioManager.close();
                    }
                }

                break;

            case 21:
//                Log.e("check_is_hide", "isHide:" + data);
//                if (data.has("isHide")) {
//                    int isHide = data.getInt("isHide");
//                    int displayMode = sharedPreferences.getInt("display_mode", 0);
//                    if (displayMode == 2) {
//                        if (isHide == 1) {
//                            if (cameraAdapter != null) {
//                                cameraAdapter.reset();
//                            }
//                            cameraList.setVisibility(View.GONE);
//                            toggleCameraImage.setImageResource(R.drawable.user_info_expanded);
//
//                        } else {
//                            cameraList.setVisibility(View.VISIBLE);
//                            reloadAgoraMember();
//                            toggleCameraImage.setImageResource(R.drawable.user_info_collopse);
//                        }
//                    }
//
//                }

                break;

            case 23:
                handleSountrackMessages(data);
                break;
            case 30:
                // 被踢出会议
                if (meetingConfig.getType() != MeetingType.MEETING) {
                    return;
                }

                if (!meetingConfig.getMeetingHostId().equals(AppConfig.UserID)) {
                    String title = getString(R.string.tips);
                    ToastUtils.showInCenter(getApplicationContext(), title, "你被管理员踢出了会议", false);
                    finish();
                }

                break;
            case 31:
                if (data.has("userId")) {
//                    handleDisplayMode(0);
//                    meetingConfig.setCameraDiplayMode(0);
                    meetingConfig.setCameraDiplayMode(0);
                    if (cameraAdapter != null) {
                        AgoraMember speakerMember = getSelectedAgora(data.getInt("userId") + "");
                        cameraAdapter.changeDisplayType(0, speakerMember);
                        cameraAdapter.setSpeakerMember(speakerMember);
//                        cameraAdapter.setSpeakerMember();
                    }
//                    showSelectSpeaker(data.getInt("userId") + "");
                }
                break;
            case 32:

                if (data.has("size")) {
                    int size = data.getInt("size");
                    String _sizeMode = "small";
                    if (size == 1) {
                        _sizeMode = "small";
                    } else if (size == 2) {
                        _sizeMode = "big";
                    } else if (size == 3) {
                        _sizeMode = "large";
                    }
                    sharedPreferences.edit().putString("speaker_size_mode", _sizeMode).commit();

                    if (cameraAdapter != null) {
                        cameraAdapter.clearSelectedMember();
                        cameraAdapter.refreshSize(size);
                    }
//                    if (followSpearkerModeManager != null) {
//                        followSpearkerModeManager.followChangeSize(size);
//                    }
                }
                break;
            default:
                break;
        }
    }

    private void justHandleDisplayMode(int mode) {
        Log.e("handleDisplayMode", "mode:" + mode);
        meetingConfig.setCameraDiplayMode(mode);

        if (mode == 0) {
            // 显示一个
            selectSpeakerImage.setVisibility(View.VISIBLE);
            isSelectSpeaker = false;
            selectSpeakerImage.setImageResource(R.drawable.user_info_expanded);
            toggleCameraImage.setVisibility(View.GONE);
//            if(cameraAdapter == null){
//                initCameraAdatper();
//            }
            cameraAdapter.changeDisplayType(AgoraCameraAdapterV2.TYPE_ACTIVIE_SPEAKER, getPresenterAgora());
        } else if (mode == 1) {
            //  隐藏
            selectSpeakerImage.setVisibility(View.GONE);
            cameraLayout.setVisibility(View.VISIBLE);
            toggleCameraImage.setImageResource(R.drawable.user_info_expanded);
            toggleCameraImage.setVisibility(View.VISIBLE);
            cameraList.setVisibility(View.GONE);
//            speakerContainer.setVisibility(View.GONE);
        } else if (mode == 2) {
            //  显示全部
            cameraList.setVisibility(View.VISIBLE);
            cameraLayout.setVisibility(View.VISIBLE);
            toggleCameraImage.setImageResource(R.drawable.user_info_collopse);
            toggleCameraImage.setVisibility(View.VISIBLE);
            selectSpeakerImage.setVisibility(View.GONE);
            cameraAdapter.changeDisplayType(AgoraCameraAdapterV2.TYPE_ALL, getPresenterAgora());
        }
//        meetingConfig.setCameraDiplayMode(mode);
//        selectSpeakerImage.setVisibility(View.GONE);
//        String sizeStr = sharedPreferences.getString("speaker_size_mode","small");
//
//
//
//        if (mode == 0) {
//            // 显示一个
//            toggleCameraLayout.setVisibility(View.GONE);
//            cameraList.setVisibility(View.GONE);
//            cameraLayout.setVisibility(View.GONE);
//            speakerContainer.setVisibility(View.VISIBLE);
//            if (followSpearkerModeManager != null) {
//                followSpearkerModeManager.showHostView(getPresenterAgora());
//            }
//
//        } else if (mode == 1) {
//            // 显示0个
//            cameraLayout.setVisibility(View.VISIBLE);
//            toggleCameraLayout.setVisibility(View.VISIBLE);
//            toggleCameraImage.setImageResource(R.drawable.user_info_expanded);
//            toggleCameraImage.setVisibility(View.VISIBLE);
//            cameraList.setVisibility(View.GONE);
//            speakerContainer.setVisibility(View.GONE);
//
//        } else if (mode == 2) {
//            // 显示全部
//            int size = 0;
//            if(sizeStr.equals("small")){
//                size = 0;
//            }else if(sizeStr.equals("big")){
//                size = 1;
//            }else if(sizeStr.equals("large")){
//                size = 2;
//            }
//            if(cameraAdapter != null){
//                cameraAdapter.refreshSize(size);
//            }
//            toggleCameraImage.setVisibility(View.VISIBLE);
//            cameraLayout.setVisibility(View.VISIBLE);
//            toggleCameraLayout.setVisibility(View.VISIBLE);
//            cameraList.setVisibility(View.VISIBLE);
//            cameraAdapter.clearSelectedMember();
//            toggleCameraImage.setImageResource(R.drawable.user_info_collopse);
//            speakerContainer.setVisibility(View.GONE);
//        }

    }


    private void handleDisplayMode(int mode) {
        Log.e("handleDisplayMode", "mode:" + mode);
        meetingConfig.setCameraDiplayMode(mode);
        if (mode == 0) {
            // 显示一个
            isSelectSpeaker = false;
            selectSpeakerImage.setImageResource(R.drawable.user_info_expanded);
            selectSpeakerImage.setVisibility(View.VISIBLE);
            toggleCameraImage.setVisibility(View.GONE);
            cameraAdapter.changeDisplayType(AgoraCameraAdapterV2.TYPE_ACTIVIE_SPEAKER, meetingConfig.getHostAgora());
        } else if (mode == 1) {
            //  隐藏
            selectSpeakerImage.setVisibility(View.GONE);
            cameraLayout.setVisibility(View.VISIBLE);
            toggleCameraImage.setImageResource(R.drawable.user_info_expanded);
            toggleCameraImage.setVisibility(View.VISIBLE);
            cameraList.setVisibility(View.GONE);
//            speakerContainer.setVisibility(View.GONE);
        } else if (mode == 2) {
            //  显示全部
            cameraList.setVisibility(View.VISIBLE);
            cameraLayout.setVisibility(View.VISIBLE);
            toggleCameraImage.setImageResource(R.drawable.user_info_collopse);
            toggleCameraImage.setVisibility(View.VISIBLE);
            selectSpeakerImage.setVisibility(View.GONE);
            cameraAdapter.changeDisplayType(AgoraCameraAdapterV2.TYPE_ALL, meetingConfig.getHostAgora());
        }
//        String sizeStr = sharedPreferences.getString("speaker_size_mode","small");
//        int size = 0;
//        if(sizeStr.equals("small")){
//            size = 0;
//        }else if(sizeStr.equals("big")){
//            size = 1;
//        }else if(sizeStr.equals("large")){
//            size = 2;
//        }
////        cameraAdapter.refreshSize(size);
//        if (mode == 0) {
//            // 显示一个
//            toggleCameraLayout.setVisibility(View.GONE);
//            cameraList.setVisibility(View.GONE);
//            cameraLayout.setVisibility(View.GONE);
//            speakerContainer.setVisibility(View.VISIBLE);
//            if (followSpearkerModeManager != null) {
//                followSpearkerModeManager.showHostView(getPresenterAgora());
//            }
//
//        } else if (mode == 1) {
//            // 显示0个
//            cameraLayout.setVisibility(View.VISIBLE);
//            toggleCameraLayout.setVisibility(View.VISIBLE);
//            toggleCameraImage.setImageResource(R.drawable.user_info_expanded);
//            toggleCameraImage.setVisibility(View.VISIBLE);
//            cameraList.setVisibility(View.GONE);
//            speakerContainer.setVisibility(View.GONE);
//
//        } else if (mode == 2) {
//            // 显示全部
//            toggleCameraImage.setVisibility(View.VISIBLE);
//            cameraLayout.setVisibility(View.VISIBLE);
//            toggleCameraLayout.setVisibility(View.VISIBLE);
//            cameraList.setVisibility(View.VISIBLE);
//            cameraAdapter.clearSelectedMember();
//            toggleCameraImage.setImageResource(R.drawable.user_info_collopse);
//            speakerContainer.setVisibility(View.GONE);
//        }

        if (isPresenter()) {
            requestChangeDisplayMode(mode);
        }
    }

    private void showSelectSpeaker(String userId) {
        handleDisplayMode(0);
        if (cameraAdapter != null) {
            cameraAdapter.setSpeakerMember(getSelectedAgora(userId));
        }
//        meetingConfig.setCameraDiplayMode(0);
//        toggleCameraLayout.setVisibility(View.GONE);
//        cameraList.setVisibility(View.GONE);
//        cameraLayout.setVisibility(View.GONE);
////        speakerContainer.setVisibility(View.VISIBLE);
//        if (followSpearkerModeManager != null) {
//            followSpearkerModeManager.showHostView(getSelectedAgora(userId));
//        }
    }


    private void handleMessageMeetingStatus(JSONObject data) {
        Log.e(TAG, "data:" + data);
        if (data != null) {
            if (data.has("status")) {
                try {
                    int status = data.getInt("status");
                    if (status == 1) {
                        if (meetingConfig.getMeetingStatus() == 0) {
                            //Leave meeting，从新加入会议
                            String meetingId = data.getString("meetingId");
                            followJoinMeetingWhenMeetingStart(meetingId);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void followJoinMeetingWhenMeetingStart(final String meetingId) {
        Observable.just("follow_join_meeting").observeOn(Schedulers.io()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                messageManager.sendMessage_LeaveMeeting(meetingConfig);
            }
        }).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String str) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetJoinMeetingDefaultStatus(meetingId);
                if (result.has("code")) {
                    int code = result.getInt("code");
                    if (code == 0) {
                        JSONObject data = result.getJSONObject("data");
                        meetingConfig.setRole(data.getInt("role"));
                    }
                }

            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                meetingConfig.setMeetingId(meetingId);
//                messageManager.sendMessage_JoinMeeting(meetingConfig);
                MeetingKit.getInstance().prepareJoin(DocAndMeetingActivity.this, meetingConfig);

            }
        }).subscribe();
    }

    private void requestSyncDetailAndPlay(final SoundTrack soundTrack) {
        Observable.just(soundTrack).observeOn(Schedulers.io()).map(new Function<SoundTrack, SoundtrackDetailData>() {
            @Override
            public SoundtrackDetailData apply(SoundTrack soundtrack) throws Exception {
                SoundtrackDetailData soundtrackDetailData = new SoundtrackDetailData();
                JSONObject response = ServiceInterfaceTools.getinstance().syncGetSoundtrackDetail(soundTrack);
                if (response.has("RetCode")) {
                    if (response.getInt("RetCode") == 0) {
                        SoundtrackDetail soundtrackDetail = new Gson().fromJson(response.getJSONObject("RetData").toString(), SoundtrackDetail.class);
                        soundtrackDetailData.setSoundtrackDetail(soundtrackDetail);
                    }
                }
                return soundtrackDetailData;
            }

        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<SoundtrackDetailData>() {
            @Override
            public void accept(SoundtrackDetailData soundtrackDetailData) throws Exception {
                if (soundtrackDetailData.getSoundtrackDetail() != null) {
                    EventPlaySoundtrack soundtrack = new EventPlaySoundtrack();
                    soundtrack.setSoundtrackDetail(soundtrackDetailData.getSoundtrackDetail());
                    playSoundtrack(soundtrack);
                }
            }
        }).subscribe();
    }

    private void followShowFullScreenSingleAgoraMember(String memberId) {
        if (cameraList.getVisibility() == View.VISIBLE) {
            for (AgoraMember member : cameraAdapter.getUsers()) {
                if ((member.getUserId() + "").equals(memberId)) {
                    cameraAdapter.showFull(cameraAdapter.getUsers().indexOf(member));
                    break;
                }
            }
        } else if (fullCameraList.getVisibility() == View.VISIBLE) {
            for (AgoraMember member : fullCameraAdapter.getUsers()) {
                if ((member.getUserId() + "").equals(memberId)) {
                    fullCameraAdapter.showFull(cameraAdapter.getUsers().indexOf(member));
                    break;
                }
            }
        }
    }

    private void handleMessageLeaveMeeting(JSONObject data) {
        Log.e("handle_leave_meeting", "data:" + data);
        if (data == null) {
            return;
        }

        if (meetingConfig.getMeetingStatus() == 0) {
            return;
        }

        if (data.has("retCode")) {
            try {
                if (data.getInt("retCode") == 0) {
                    JSONArray usersList = data.getJSONObject("retData").getJSONArray("usersList");
                    if (usersList != null) {
                        getMeetingMembers(usersList);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        MeetingKit.getInstance().requestMeetingMembers(meetingConfig, true);

    }

    private void handleMessageAttchmentUploaded(JSONObject data) {
        Log.e("handle_attchment_upload", "data;" + data);
        int changeNumber = 0;
        if (data.has("retData")) {
            try {
                JSONObject retData = data.getJSONObject("retData");
                changeNumber = retData.getInt("changeNumber");
                int difference = changeNumber - meetingConfig.getChangeNumber();
                if (!(Math.abs(difference) > 1)) {
                    meetingConfig.setChangeNumber(changeNumber);
                    JSONObject documentInfo = retData.getJSONObject("meetingDocument");
                    MeetingDocument meetingDocument = gson.fromJson(documentInfo.toString(), MeetingDocument.class);
                    String attachmentUrl = meetingDocument.getAttachmentUrl();
                    String preUrl = "";
                    String endUrl = "";
                    if (!TextUtils.isEmpty(attachmentUrl)) {
                        int index = attachmentUrl.lastIndexOf("<");
                        int index2 = attachmentUrl.lastIndexOf(">");
                        if (index > 0) {
                            preUrl = attachmentUrl.substring(0, index);
                        }
                        if (index2 > 0) {
                            endUrl = attachmentUrl.substring(index2 + 1, attachmentUrl.length());
                        }
                    }

                    List<DocumentPage> pages = new ArrayList<>();
                    for (int j = 0; j < meetingDocument.getPageCount(); ++j) {
                        String pageUrl = "";
                        DocumentPage page = new DocumentPage();
                        page.setPageNumber(j + 1);
                        page.setDocumentId(meetingDocument.getItemID());
                        if (TextUtils.isEmpty(preUrl)) {
                            page.setPageUrl(pageUrl);
                        } else {
                            page.setPageUrl(preUrl + (j + 1) + "_4K" + endUrl);
                        }
                        pages.add(page);
                    }
                    meetingDocument.setDocumentPages(pages);
                    documents.add(meetingDocument);
                    if (bottomFilePop != null && bottomFilePop.isShowing()) {
                        bottomFilePop.setDocuments(this.documents, meetingDocument.getItemID(), this);
                        bottomFilePop.removeTempDoc();
                    }
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        /*如果changeNumber相差大于1,说明有遗漏数据需要重新获取文件列表*/
        meetingConfig.setChangeNumber(changeNumber);
        Observable.just(meetingConfig).observeOn(Schedulers.io()).doOnNext(new Consumer<MeetingConfig>() {
            @Override
            public void accept(MeetingConfig meetingConfig) throws Exception {
                EventRefreshDocs docs = DocumentModel.asyncGetDocuments(meetingConfig);
                if (docs.getDocuments() != null) {
                    DocAndMeetingActivity.this.documents = docs.getDocuments();
                    Observable.just("refresh").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            Log.e("check_refersh_file", "refresh_file_one");
                            if (bottomFilePop != null && bottomFilePop.isShowing()) {
                                Log.e("check_refersh_file", "refresh_file_two");
                                bottomFilePop.setDocuments(DocAndMeetingActivity.this.documents, DocAndMeetingActivity.this);
                            }
                        }
                    });

                }
            }
        }).subscribe();
    }

    private void handleMessageAttchmentDelete(JSONObject data) {
        Log.e("handle_attchment_delete", "data;" + data);
        int currentItemId = 0;
        try {
            JSONObject jsonObject = data.getJSONObject("retData");
            int itemId = jsonObject.getInt("itemId");
            int changeNumber = data.getInt("changeNumber");
            int difference = changeNumber - meetingConfig.getChangeNumber();
            meetingConfig.setChangeNumber(changeNumber);
            if (itemId == meetingConfig.getDocument().getItemID()) {
                if (itemId == documents.get(0).getItemID()) {
                    if (documents.size() == 1) {
                        documents.clear();
                        if (bottomFilePop != null && bottomFilePop.isShowing()) {
                            bottomFilePop.setDocuments(this.documents, meetingConfig.getDocument().getItemID(), this);
                            bottomFilePop.removeTempDoc();
                        }
                        meetingConfig.setDocument(null);
                        meetingConfig.setCurrentDocumentPage(null);
                        meetingConfig.setPageNumber(1);
                        EventMeetingDocuments meetingDocuments = new EventMeetingDocuments();
                        meetingDocuments.setDocuments(documents);
                        receiveDocuments(meetingDocuments);
                        return;
                    } else {
                        currentItemId = documents.get(1).getItemID();
                    }
                } else {
                    currentItemId = documents.get(0).getItemID();
                }
            }
            for (int i = 0; i < documents.size(); i++) {
                if (documents.get(i).getItemID() == itemId) {
                    documents.remove(i);
                    break;
                }
            }
            if (currentItemId == 0) {
                currentItemId = meetingConfig.getDocument().getItemID();
            }
            if (bottomFilePop != null && bottomFilePop.isShowing()) {
                bottomFilePop.setDocuments(this.documents, currentItemId, this);
                bottomFilePop.removeTempDoc();
            }

            /*如果changeNumber相差大于1,说明有遗漏数据需要重新获取文件列表*/
            if (Math.abs(difference) > 1) {
                DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, currentItemId, getDocPageNumber(currentItemId));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void handleMessageJoinMeeting(JSONObject data) {
        Log.e("check_join_message", "removeMessages");
        rejoinHandler.removeMessages(MESSAGE_JOIN_TIME);
        joinTime = 0;
        if (data == null) {
            return;
        }
        if (data.has("retCode")) {
            try {
                if (data.getInt("retCode") == 0) {
                    // 成功收到JOIN_MEETING的返回
                    JSONObject dataJson = data.getJSONObject("retData");
                    JoinMeetingMessage joinMeetingMessage = gson.fromJson(dataJson.toString(), JoinMeetingMessage.class);
                    meetingConfig.setType(joinMeetingMessage.getType());

                    if (meetingConfig.getType() == MeetingType.MEETING) {
                        if (dataJson.has("role")) {
                            meetingConfig.setRole(dataJson.getInt("role"));
                        }
                        if (dataJson.has("cameraDiplayMode")) {
                            meetingConfig.setCameraDiplayMode(dataJson.getInt("cameraDiplayMode"));
                        } else {
                            meetingConfig.setCameraDiplayMode(0);
                        }
                        if (dataJson.has("sizeMode")) {
                            int sizeMode = dataJson.getInt("sizeMode");
                            String _sizeMode = "small";
                            if (sizeMode == 1) {
                                _sizeMode = "small";
                            } else if (sizeMode == 2) {
                                _sizeMode = "big";
                            } else if (sizeMode == 3) {
                                _sizeMode = "large";
                            }
                            sharedPreferences.edit().putString("speaker_size_mode", _sizeMode).commit();
                        }

                        if (dataJson.has("status")) {
                            int status = dataJson.getInt("status");
                            meetingConfig.setMeetingStatus(status);
                            if (status == 0 && !meetingConfig.isHost()) {
                                // 会议还没有开始
                                showWatingMeeting();
                                return;
                            }
                        }
                    }

                    String[] datas = joinMeetingMessage.getCurrentDocumentPage().split("-");
                    meetingConfig.setFileId(Integer.parseInt(datas[0]));
                    float page = Float.parseFloat(datas[1]);
                    meetingConfig.setPageNumber((int) page);

                    if (dataJson.has("currentMode")) {
                        meetingConfig.setMode(joinMeetingMessage.getCurrentMode());
                    }

                    if (dataJson.has("systemType")) {
                        meetingConfig.setSystemType(joinMeetingMessage.getSystemType());
                    }


                    if (dataJson.has("currentMaxVideoUserId")) {
                        meetingConfig.setCurrentMaxVideoUserId(joinMeetingMessage.getCurrentMaxVideoUserId());
                    }

                    if (dataJson.has("lessonId")) {
                        if (Integer.parseInt(dataJson.getString("lessonId")) > 0) {
                            meetingConfig.setLessionId(Integer.parseInt(dataJson.getString("lessonId")));
                        }
                    }

                    Log.e("JOIN_MEETING", "join_meeting_message:" + documents);

                    if (joinMeetingMessage.getNoteId() > 0 && !TextUtils.isEmpty(joinMeetingMessage.getNotePageId())) {
                        followShowNote((int) joinMeetingMessage.getNoteId());
                    }

                    if (meetingConfig.getType() == MeetingType.DOC) {
                        meetingLayout.setVisibility(View.GONE);
                        meetingMenu.setVisibility(View.GONE);
                        requestDocumentsAndShowPage();
                    } else if (meetingConfig.getType() == MeetingType.MEETING) {

                        waitingMeetingLayout.setVisibility(View.GONE);
                        if (dataJson.has("presenterSessionId")) {
                            meetingConfig.setPresenterSessionId(joinMeetingMessage.getPresenterSessionId());
                        }
                        if (AppConfig.UserID.equals(joinMeetingMessage.getUserId())) {
                            // 说明是自己加入了会议返回的JOIN_MEETING的消息
                            Log.e("check_JOIN_MEETING", "my_self_join_in");
                            requestDocumentsAndShowPage();

//                            int displayMod\e = sharedPreferences.getInt("display_mode", 0);
                            Log.e("handle_display_mode", "in_self_join_meeting");
                            if (meetingConfig.getRole() != MeetingConfig.MeetingRole.HOST && meetingConfig.getRole() != MeetingConfig.MeetingRole.MEMBER) {
                                // 进来自己不是host也不是主讲人
                                MeetingKit.getInstance().disableAudioAndVideoStream();
//                                menuIcon.setVisibility(View.GONE);
//                                meetingMenuMemberImage.setVisibility(View.VISIBLE);
                            }

                            //                            delayRefreshAgoraList();
                            if (joinMeetingMessage.isIfPause()) {//会议是暂停的
                                mIsMeetingPause = true;
                                meetingConfig.setMeetingPause(mIsMeetingPause);
                                mTipsText = Tools.getFromBase64(joinMeetingMessage.getPauseMsg());
                                mPauseDuration = joinMeetingMessage.getPauseDuration();
                                MeetingPauseManager.getInstance(DocAndMeetingActivity.this, meetingConfig).setPauseTime(mPauseDuration);
                                MeetingPauseManager.getInstance(DocAndMeetingActivity.this, meetingConfig).startMeetingPauseTime();
                                if (soundtrackPlayManager != null) {
                                    soundtrackPlayManager.changeSeekbarStatusByRole();
                                }
                            }
                        }

                        MeetingKit.getInstance().requestMeetingMembers(meetingConfig, false);
                        String userId = dataJson.getString("userId");
                        if (AppConfig.UserID.equals(userId)) {
                            if (meetingConfig.getRole() == 2 || meetingConfig.getRole() == 1) {
                                EventMicroPhone eventMicroPhone = new EventMicroPhone();
                                EventBus.getDefault().post(eventMicroPhone);
                            }
                        }
                        if (dataJson.has("privateCoachingMeeting")) {
                            final JSONObject coachingMeeting = dataJson.getJSONObject("privateCoachingMeeting");
                            if (coachingMeeting.has("status")) {
                                if (coachingMeeting.getInt("status") == 2) {
                                    if (coachingMeeting.has("privateCoachingMembers")) {
                                        Observable.just("delay_load").delay(2000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                                            @Override
                                            public void accept(String s) throws Exception {
                                                handleJoinPrivateCoachingMeeting(coachingMeeting.getJSONArray("privateCoachingMembers"));
                                            }
                                        });
                                    }
                                }

                            }
                        }

                        if (dataJson.has("privateCoachingMeeting")) {
                            final JSONObject coachingMeeting = dataJson.getJSONObject("privateCoachingMeeting");
                            if (coachingMeeting.has("status")) {
                                if (coachingMeeting.getInt("status") == 0) {  //私教未开始

                                    if (dataJson.has("playAudioData")) {
                                        final String audioData = dataJson.getString("playAudioData");
                                        if (!TextUtils.isEmpty(audioData)) {
                                            Log.e("audioData", "audioData:" + audioData);
                                            Observable.just("delay_load").delay(2000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                                                @Override
                                                public void accept(String s) throws Exception {
                                                    handleSoundtrackWhenJoinMeeting(audioData);
                                                }
                                            });

                                        }
                                    }


                                }
                            }
                        }


                        if (meetingConfig.isInRealMeeting()) {
                            return;
                        }
                        //
                        initRealMeeting();
                    }
//                    Log.e("MeetingConfig","MeetingConfig:" + meetingConfig);
                }
            } catch (JSONException e) {
                Log.e("JOIN_MEETING", "JOIN_MEETING,JSONException:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    /**
     * 暂停,恢复会议,更新暂停会议信息
     *
     * @param data
     */
    private void handleMessageMeetingChange(JSONObject data) {
        if (data == null) return;
        MeetingChangeBean meetingChangeBean = gson.fromJson(data.toString(), MeetingChangeBean.class);
        if (meetingChangeBean.getRetCode() == null) return;
        if (meetingChangeBean.getRetCode().equals("0")) {
            MeetingChangeBean.RetDataBean meetingChangeRetData = meetingChangeBean.getRetData();
            int type = meetingChangeRetData.getType();

            switch (type) {
                case 1://暂停会议
                    mIsMeetingPause = true;
                    meetingConfig.setMeetingPause(mIsMeetingPause);
//                    MeetingPauseManager.getInstance(this, meetingConfig).setTipInfo("");
                    MeetingPauseManager.getInstance(this, meetingConfig).showBigLayout();
                    handleWebUISetting();
                    if (soundtrackPlayManager != null) {
                        soundtrackPlayManager.changeSeekbarStatusByRole();
                    }
                    break;
                case 2://恢复会议
                    mIsMeetingPause = false;
                    meetingConfig.setMeetingPause(mIsMeetingPause);

                    if (meetingConfig.isCoaching()) {
                        meetingConfig.setCoaching(false);
                        if (cameraAdapter != null) {
                            cameraAdapter.notifyDataSetChanged();
                        }
                    }
                    MeetingPauseManager.getInstance(this, meetingConfig).hide();
                    handleWebUISetting();
                    if (bottomFilePop != null && bottomFilePop.isShowing()) {
                        bottomFilePop.hide();
                    }
                    if (soundtrackPlayManager != null) {
                        soundtrackPlayManager.changeSeekbarStatusByRole();
                    }
                    break;

                case 3://更新暂停信息
                    String tipsText = (String) meetingChangeRetData.getValue();
                    if (tipsText == null && TextUtils.isEmpty(tipsText)) return;
                    tipsText = Tools.getFromBase64(tipsText);
                    MeetingPauseManager.getInstance(this, meetingConfig).setTipInfo(tipsText);
                    break;

                case 4:
                    int value = Integer.parseInt((String) meetingChangeRetData.getValue());
                    Log.e("handle_display_mode", "in_meeting_changed");
                    justHandleDisplayMode(value);
                    break;
                case 5:

                    break;
                case 8: //开启Private Coaching
                    if (!meetingConfig.isCoaching()) {
                    }
                    break;
                case 9: //结束Private Coaching
                    if (meetingConfig.isCoaching()) {
                        PrivateCoachManager.getManager(this).endCoach(meetingConfig, cameraAdapter, mMeetingPauseManager);
                    }
                    break;
                case 11:
                    //旁观者接受了Private Coaching邀请
                    if (meetingConfig.isCoaching()) {
                        if (!TextUtils.isEmpty(meetingChangeBean.getRetData().getPromoter())) {
                            meetingConfig.coachAudiences(meetingChangeBean.getRetData().getPromoter(), true, true);
                            if (mMeetingPauseManager != null) {
                                mMeetingPauseManager.initAudiencesLayout(coachAudienceLayout, meetingConfig, getResources().getString(R.string.prompt_joined_private_channel));
                            }
                            if (cameraAdapter != null) {
                                cameraAdapter.setCoachAudience(meetingChangeBean.getRetData().getPromoter());
                            }

                        }
                    }
                    break;
                case 200:  //老师是否talk to all student
                    if (meetingConfig.isCoaching()) {
                        if (!TextUtils.isEmpty(meetingChangeRetData.getPromoter())) {
                            String promoter = meetingChangeRetData.getPromoter();
                            try {
                                JSONObject retdata = data.getJSONObject("retData");
                                JSONObject value2 = retdata.getJSONObject("value");
                                int settingValue = value2.getInt("settingValue");
                                PrivateCoachManager.getManager(this).isTeacherTalkToAllStu(meetingConfig, promoter, settingValue);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventMeetingPauseOrResume(MeetingPauseOrResumBean meetingPauseOrResumBean) {
        mIsMeetingPause = meetingPauseOrResumBean.isPause();
        meetingConfig.setMeetingPause(mIsMeetingPause);
        if (mIsMeetingPause) {
            hideAgoraFull();
            if (meetingConfig.getSystemType() == AppConfig.COMPANY_MODEL) {//如果是会议模式
                MeetingPauseManager.getInstance(this, meetingConfig).setTipInfo(getString(R.string.meeting_pause_tips));
            } else {//如果是课堂模式
                MeetingPauseManager.getInstance(this, meetingConfig).setTipInfo(getString(R.string.class_pause_tips));
            }
            MeetingPauseManager.getInstance(this, meetingConfig).showBigLayout();
        } else {
            MeetingPauseManager.getInstance(this, meetingConfig).hide();
            if (soundtrackPlayManager != null) {
                if (soundtrackPlayManager.isPlaying()) {
                    soundtrackPlayManager.followClose();
                }
            }
            if (soundtrackRecordManager != null) {
                if (meetingConfig.isSyncing()) {
                    soundtrackRecordManager.release();
                }
            }
        }
        handleWebUISetting();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selfJoinCoachAsAudience(EventSelfJoinCoachAudience selfJoinCoachAudience) {
//        meetingConfig.getSystemType() != 0 &&
        if (meetingConfig.isMeetingPause()) {
            if (cameraAdapter != null) {
                if (selfJoinCoachAudience.isAccept()) {  //成为私教参会者
                    cameraAdapter.setCoachAudience(AppConfig.UserID);
                } else {
                    cameraAdapter.updateUser(Integer.parseInt(AppConfig.UserID));
                }

            }
        }
    }


    private void showWatingMeeting() {
        hideEnterLoading();
        waitingMeetingLayout.setVisibility(View.VISIBLE);
        menuIcon.setImageResource(R.drawable.shape_transparent);
        meetingMenu.setVisibility(View.VISIBLE);
    }

    private void handleSoundtrackWhenJoinMeeting(String playAudioData) {
//        {"actionType":23,"soundtrackId":38065,"time":131086,"stat":2}
        Log.e(TAG, "play_audio_data:" + playAudioData);
        if (soundtrackPlayManager != null) {
            if (soundtrackPlayManager.isLoading()) {
                return;
            }

            try {
                JSONObject data = new JSONObject(Tools.getFromBase64(playAudioData));
                Log.e("check_do_pause", "data:" + data);
                if (data.has("stat")) {


                    final int stat = data.getInt("stat");
                    final String soundtrackID = data.getString("soundtrackId");
                    int audioTime = 0;
                    if (stat == 4) {
                        audioTime = data.getInt("time");
                    }

                    Log.e("mediaplayer-----", stat + ":   " + soundtrackID);
                    if (stat == 1) {  //开始播放
                        int vid2 = 0;
                        if (!TextUtils.isEmpty(soundtrackID)) {
                            vid2 = Integer.parseInt(soundtrackID);
                        }
                        SoundTrack soundTrack = new SoundTrack();
                        soundTrack.setSoundtrackID(vid2);
                        EventPlaySoundtrack playSoundtrack = new EventPlaySoundtrack();
                        playSoundtrack.setSoundTrack(soundTrack);
                        playSoundtrack(playSoundtrack);
//                    requestSyncDetailAndPlay(soundTrack);
                    } else if (stat == 0) { //停止播放
                        if (soundtrackPlayManager != null) {
                            soundtrackPlayManager.followClose();
                        }
                    } else if (stat == 2) {  //暂停播放
                        if (soundtrackPlayManager != null) {
                            soundtrackPlayManager.followClose();
                            soundtrackPlayManager.initLoading(menuIcon);
//                        soundtrackPlayManager.followPause();
                            int vid2 = 0;
                            if (!TextUtils.isEmpty(soundtrackID)) {
                                vid2 = Integer.parseInt(soundtrackID);
                            }
                            long time = data.getLong("time");
                            SoundTrack soundTrack = new SoundTrack();
                            soundTrack.setSoundtrackID(vid2);
                            requestDetailAndPause(soundTrack, time);
                        }
                    } else if (stat == 3) { // 继续播放
                        if (soundtrackPlayManager != null) {
                            soundtrackPlayManager.followRestart(0);
                        }

                    } else if (stat == 4) {  // 追上进度
                        int vid2 = 0;
                        if (!TextUtils.isEmpty(soundtrackID)) {
                            vid2 = Integer.parseInt(soundtrackID);
                        }
                        SoundTrack soundTrack = new SoundTrack();
                        soundTrack.setSoundtrackID(vid2);
                        long time = data.getLong("time");
                        if (soundtrackPlayManager != null) {
                            soundtrackPlayManager.followClose();
                            soundtrackPlayManager.initLoading(menuIcon);
                            requestDetailAndPlay(soundTrack, time);
                        }
//                    requestSyncDetailAndPause(soundTrack);
//
                    } else if (stat == 5) {  // 拖动进度条
//                    seekToTime(audioTime);
                        if (soundtrackPlayManager != null) {
//                        soundtrackPlayManager.followSeekTo(audioTime);
//                           soundtrackPlayManager.followSeek(audioTime/1000);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private void delayRefreshAgoraList() {
        if (meetingConfig.getType() == MeetingType.MEETING) {
            Observable.just("deday_refresh").delay(3000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    if (cameraAdapter != null) {
                        cameraAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

    }


    private void handleMessageAgoraStatusChange(JSONObject data) {
        Log.e("agora_status_change", "data:" + data);
        if (data.has("retCode")) {
            try {
                if (data.getInt("retCode") == 0) {
                    JSONObject retData = data.getJSONObject("retData");
                    String usersList = retData.getString("usersList");
                    Gson gson = new Gson();
                    List<Member> list = gson.fromJson(usersList, new TypeToken<List<Member>>() {
                    }.getType());
                    Member member = null;
                    for (int i = 0; i < list.size(); i++) {
                        Member m = list.get(i);
                        if (m.getUserId().equals(AppConfig.UserID)) {
                            member = m;
                            break;
                        }
                    }
                    if (mTypeClicked == 0 && member.getUserId().equals(meetingConfig.getPresenterId()) && member.getMicrophoneStatus() == 3) {
                        showMicroPhoneDialog();
                        mTypeClicked = -1;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMeetingDefaultDocument() {
        if (meetingConfig.getRole() != MeetingConfig.MeetingRole.HOST) {
            roleMemberLayout.setVisibility(View.VISIBLE);
            roleHostLayout.setVisibility(View.GONE);
            roleText.setText(R.string.shAcan);
        } else {
            roleMemberLayout.setVisibility(View.GONE);
            roleHostLayout.setVisibility(View.VISIBLE);
            roleText.setText(R.string.shYcan);
        }
    }

    public class NoteJavascriptInterface {
        @JavascriptInterface
        public void afterChangePageFunction(final int pageNum, int type) {
//            Log.e("JavascriptInterface", "note_afterChangePageFunction,pageNum:  " + pageNum + ", type:" + type);
            NoteViewManager.getInstance().getNotePageActionsToShow(meetingConfig);
        }

        @JavascriptInterface
        public void reflect(String result) {
            Log.e("JavascriptInterface", "reflect,result:  " + result);
            Note note = NoteViewManager.getInstance().getNote();
            if (note != null) {
                notifyMyNoteWebActions(result, note);
            }
        }

        @JavascriptInterface
        public synchronized void callAppFunction(final String action, final String data) {
            Log.e("JavascriptInterface", "callAppFunction,action:  " + action + ",data:" + data);
            if (TextUtils.isEmpty(action) || TextUtils.isEmpty(data)) {
                return;
            }
        }
    }

    private PowerManager.WakeLock wakeLock;

    private void keepScreenWake() {
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "TEST");
        wakeLock.acquire();
    }

    @Override
    protected void hideAgoraFull() {
        super.hideAgoraFull();
        if (currentAgoraMember != null) {
            if (fullCameraAdapter != null) {
                fullCameraAdapter.refreshAgoraMember(currentAgoraMember);
            }

            if (cameraAdapter != null) {
                cameraAdapter.refreshAgoraMember(currentAgoraMember);
            }
        }

        hideFullCameraScreen();
    }

    private void notifyAgoraVedioScreenStatus(int viewMode, String userId) {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            return;
        }

        if (!AppConfig.UserID.equals(meetingConfig.getPresenterId())) {
            return;
        }

        SocketMessageManager.getManager(this).sendMessage_ViewModeStatus(viewMode, userId);

        meetingConfig.setMode(viewMode);

        if (viewMode == 2) {
            meetingConfig.setCurrentMaxVideoUserId(userId);
        }

        MeetingKit.getInstance().setEncoderConfigurationBaseMode();
//        SocketMessageManager.getManager(this).sendMessage_MyNoteActionFrame(actions, meetingConfig, note);
    }

    // -----

    private KickOffMemberDialog kickOffMemberDialog;

    private void showKickOffDialog(MeetingMember meetingMember) {
        if (kickOffMemberDialog != null) {
            if (kickOffMemberDialog.isShowing()) {
                kickOffMemberDialog.dismiss();
            }
            kickOffMemberDialog = null;
        }

        kickOffMemberDialog = new KickOffMemberDialog(this);
        kickOffMemberDialog.show(meetingConfig, meetingMember);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void eventKickOffMember(EventKickOffMember kickOffMember) {
        Log.e("eventKickOffMember", "show_dialog");
        showKickOffDialog(kickOffMember.getMeetingMember());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleBluetoothNote(EventOpenOrCloseBluethoothNote note) {
        if (note != null) {
            if (note.getStatus() == 1) {
                showNoteFloatingDialog(Integer.parseInt(note.getNoteId()));
            } else if (note.getStatus() == 0) {
                if (mFloatingWindowNoteManager != null && mFloatingWindowNoteManager.isShowing()) {
                    mFloatingWindowNoteManager.closeFloating();
                    mFloatingWindowNoteManager = null;
                    FloatingWindowNoteManager.instance = null;
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void networkBecameFine(EventNetworkFineChanged networkFineChanged) {
        Log.e("networkBecameFine", "refresh_web_socket");
        updateSocket();
    }

    private void updateSocket() {
        Intent service = new Intent(getApplicationContext(), SocketService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(service);
            startService(service);
        } else {
            startService(service);
        }
    }

    private String checkUrlForDifferentRegion(String url) throws MalformedURLException {
        String newUrl = url;
        URL _url = new URL(url);

        String path = _url.getPath();
        if (!TextUtils.isEmpty(path)) {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            int index = path.lastIndexOf("/");
            if (index >= 0 && index < path.length()) {
                String centerPart = path.substring(0, index);
                String fileName = path.substring(index + 1, path.length());
                Log.e("check_transform_url", "centerPart:" + centerPart + ",fileName:" + fileName);
                if (!TextUtils.isEmpty(centerPart)) {
                    JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
                            centerPart);
                    if (queryDocumentResult != null) {
                        Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
                        String part = "";
                        if (uploadao != null) {
                            if (1 == uploadao.getServiceProviderId()) {
                                part = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + centerPart
                                        + "/" + fileName;
                            } else if (2 == uploadao.getServiceProviderId()) {
                                part = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + centerPart + "/" + fileName;
                            }
                            newUrl = part;
                            Log.e("check_transform_url", "url:" + url);
                        }
                    }
                }
            }
        }

        Log.e(TAG, "new_url:" + newUrl);
        return newUrl;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventStartJoinMeeting(EventSendJoinMeetingMessage joinMeetingMessage) {
        this.joinMeetingMessage = joinMeetingMessage;
        rejoinHandler.obtainMessage(MESSAGE_JOIN_TIME).sendToTarget();
    }

    @Override
    public void onSpeakerAudioStatusChanged(int uid, boolean isMuted) {

    }

    @Override
    public void onSpeakerVideoStatusChanged(int uid, boolean isMuted) {
        Log.e(TAG, "uid:" + uid + ",ismuted:" + isMuted);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void expandedAllUserinfoList(EventExpanedUserList expanedUserList) {
        Log.e("check_post", "receive_post");
//        speakerContainer.setVisibility(View.GONE);
        //----
        cameraLayout.setVisibility(View.VISIBLE);
        cameraList.setVisibility(View.VISIBLE);
        reloadAgoraMember();
//        toggleCameraImage.setImageResource(R.drawable.eyeclose);
    }

    @Override
    public void onSelectSpeakerClicked(ImageView selectedImage) {
        if (isPresenter()) {
            rejoinHandler.removeMessages(MESSAGE_CHECK_SELECTED_SPEAKER);
            rejoinHandler.obtainMessage(MESSAGE_CHECK_SELECTED_SPEAKER).sendToTarget();
            cameraLayout.setVisibility(View.VISIBLE);
            toggleCameraLayout.setVisibility(View.VISIBLE);
            cameraList.setVisibility(View.VISIBLE);
            if (cameraAdapter != null) {
                cameraAdapter.clearSelectedMember();
            }
            toggleCameraImage.setVisibility(View.GONE);
            selectSpeakerImage.setVisibility(View.VISIBLE);
//            speakerContainer.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventShowChangeSpeakerMode(EventSelectSpeakerMode selectSpeakerMode) {
        Log.e("EventBus", "check_show_speaker_mode");
        showMorePopwindow(selectSpeakerMode.getContainer());
    }

    PopSpeakerWindowMore windowMorePop;

    private void showMorePopwindow(View view) {
        if (windowMorePop != null) {
            if (windowMorePop.isShowing()) {
                windowMorePop.dismiss();
            }
            windowMorePop = null;
        }
        windowMorePop = new PopSpeakerWindowMore(this);
        windowMorePop.showAtBottom(view);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventHideMeetingMenu(EventHideMeetingMenu hideMeetingMenu) {
        if (popWebcamOptions != null) {
            if (popWebcamOptions.isShowing()) {
                popWebcamOptions.dismiss();
                popWebcamOptions = null;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventChangeCameraSize(EventChangeCameraSize cameraSize) {
        if (cameraAdapter != null) {
            cameraAdapter.clearSelectedMember();
            cameraAdapter.refreshSize(cameraSize.getSize());
            notifySizeChanged(cameraSize.getSize());
        }

        if (popWebcamOptions != null) {
            if (popWebcamOptions.isShowing()) {
                popWebcamOptions.dismiss();
                popWebcamOptions = null;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventAlertMicroPhoneDialog(EventMicroPhone eventMicroPhone) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!meetingSettingCache.getMeetingSetting().isMicroOn() && !mIsMemberDialogShow) {
                    showMicroPhoneDialog();
                }
            }
        }, 2000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventClickMenuType(EventTypeClicked eventTypeClicked) {
        mTypeClicked = eventTypeClicked.getType();
    }


    private boolean mIsShowMicroPhoneDialog = false;

    private void showMicroPhoneDialog() {
        if (!mIsShowMicroPhoneDialog) {
            MeetingMicroPhoneDialog meetingMicroPhoneDialog = new MeetingMicroPhoneDialog(DocAndMeetingActivity.this);
            meetingMicroPhoneDialog.show();
            mIsShowMicroPhoneDialog = true;
            meetingMicroPhoneDialog.setOnMicroPhoneTurnOnListener(new MeetingMicroPhoneDialog.OnMicroPhoneTurnOnListener() {
                @Override
                public void onTurnOnMicroPhone() {
                    mIsShowMicroPhoneDialog = false;
                    MeetingKit.getInstance().menuMicroClicked(true);
                    MeetingKit.getInstance().refreshMeetingMenu();
                }

                @Override
                public void onClose() {
                    mIsShowMicroPhoneDialog = false;
                }
            });
        }
    }

    private void notifySizeChanged(int mode) {
        Log.e("notifySizeChanged", "meeting_config:" + meetingConfig);
        if (meetingConfig == null) {
            return;
        }
        if (isPresenter() && !meetingConfig.isMeetingPause()) {
            SocketMessageManager.getManager(this).sendMessage_MySpeakerViewSizeChange(mode);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EventRequestFailed(EventRequestFailed bean) {
        ToastUtils.show(this, bean.getCode() + bean.getMessage());
    }

    @Override
    public void thumbnailClick(DocThumbnailListBean.DataBean.ImageListBean imageListBean) {
        String changpage = "{\"type\":2,\"page\":" + imageListBean.getPageNumber() + "}";
        web.loadUrl("javascript:PlayActionByTxt('" + changpage + "','" + 1 + "')", null);
        Log.i(TAG, "javascript:PlayActionByTxt('" + changpage + "','" + 1 + "')");
    }

    @Override
    public void thumbnailLongClick(DocThumbnailListBean.DataBean.ImageListBean bean) {

    }

}
