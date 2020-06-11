package com.kloudsync.techexcel.tool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.kloudsync.techexcel.bean.EventSendJoinMeetingMessage;
import com.kloudsync.techexcel.bean.EventSocketMessage;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.bean.VedioData;
import com.kloudsync.techexcel.bean.params.EventSoundSync;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.techexcel.bean.AgoraMember;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.bean.SendWebScoketNoteBean;
import com.ub.techexcel.tools.Tools;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by tonyan on 2019/11/19.
 */

public class SocketMessageManager {

    private Context context;
    private volatile WebSocketClient socketClient;
    public static final String MESSAGE_LEAVE_MEETING = "LEAVE_MEETING";
    public static final String MESSAGE_JOIN_MEETING = "JOIN_MEETING";
    public static final String MESSAGE_BROADCAST_FRAME = "BROADCAST_FRAME";
    public static final String MESSAGE_SEND_MESSAGE = "SEND_MESSAGE";
    public static final String MESSAGE_END_MEETING = "END_MEETING";
    public static final String MESSAGE_MAKE_PRESENTER = "MAKE_PRESENTER";
    public static final String MESSAGE_ATTACHMENT_UPLOADED = "ATTACHMENT_UPLOADED";
    public static final String ATTACHMENT_UPLOADED_WITH_CHANGE_NUMBER = "ATTACHMENT_UPLOADED_WITH_CHANGE_NUMBER";
	public static final String ATTACHMENT_REMOVED = "ATTACHMENT_REMOVED";
    public static final String MESSAGE_AGORA_STATUS_CHANGE = "AGORA_STATUS_CHANGE";
    public static final String MESSAGE_MEMBER_LIST_CHANGE = "MEMBER_LIST_CHANGE";
    public static final String MESSAGE_NOTE_DATA = "NOTE_DATA";
    public static final String MESSAGE_OPEN_OR_CLOSE_NOTE = "OPEN_OR_CLOSE_NOTE";
    public static final String MESSAGE_NOTE_CHANGE = "NOTE_CHANGE";
    public static final String MESSAGE_NOTE_P1_CREATEAD = "NOTE_ITEM_4_P1_CREATED";
    public static final String MESSAGE_HELLO = "HELLO";
    public static final String MESSAGE_MEETING_STATUS = "MEETING_STATUS";
    public static final String MEETING_CHANGE = "MEETING_CHANGE";
    public static final String MEETING_MEMBER_SPEAKING = "MEMBER_SPEAKING";
    public static final String MEETING_USER_JOIN_MEETING_ON_OTHER_DEVICE = "USER_JOIN_MEETING_ON_OTHER_DEVICE";
    public static final String MEETING_DISPLAY_SPEAKING_CAMERA = "DISPLAY_SPEAKING_CAMERA";
    public static final String MEETING_JOIN_PRIVATE_COACHING_AS_STUDENT = "JOIN_PRIVATE_COACHING_AS_STUDENT";
    public static final String MEETING_INVITE_TO_PRIVATE_COACHING_AS_AUDIENCE = "INVITE_TO_PRIVATE_COACHING_AS_AUDIENCE";
    public static final String MEETING_REFUSE_PRIVATE_COACHING_INVITATION = "REFUSE_PRIVATE_COACHING_INVITATION";
    public static final String MEETING_REMOVED_FROM_PRIVATE_COACHING_BY_HOST = "REMOVED_FROM_PRIVATE_COACHING_BY_HOST";
    public static final String MEETING_PRIVATE_COACHING_SYNC_STATUS_UPDATED = "PRIVATE_COACHING_SYNC_STATUS_UPDATED";
    public static final String MEETING_AUDIENCE_LEAVE_PRIVATE_COACHING = "AUDIENCE_LEAVE_PRIVATE_COACHING";
    public static final int MESSAGE_VIDEO_PAUSE = 0;
    public static final int MESSAGE_VIDEO_PLAY = 1;
    public static final int MESSAGE_VIDEO_CLOSE = 2;

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (TextUtils.isEmpty(message)) {
                return;
            }
            EventBus.getDefault().post(parseMessage(message));
        }
    };

    private EventSocketMessage parseMessage(String message) {
        EventSocketMessage socketMessage = new EventSocketMessage();
        if (message.equals(MESSAGE_LEAVE_MEETING)) {
            socketMessage.setAction(MESSAGE_LEAVE_MEETING);
            return socketMessage;
        }


        String _message = Tools.getFromBase64(message);
        try {
            JSONObject jsonMessage = new JSONObject(_message);
            if (jsonMessage.has("action")) {
                socketMessage.setAction(jsonMessage.getString("action"));
            }

            socketMessage.setData(jsonMessage);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return socketMessage;
    }

    private SocketMessageManager(Context context) {
        this.context = context;
    }

    static volatile SocketMessageManager instance;

    public static SocketMessageManager getManager(Context context) {
        if (instance == null) {
            synchronized (SocketMessageManager.class) {
                if (instance == null) {
                    instance = new SocketMessageManager(context);
                }
            }
        }
        return instance;
    }

    public void sendMessage_JoinMeeting(MeetingConfig config) {

        try {
            JSONObject message = new JSONObject();
            message.put("action", "JOIN_MEETING");
            message.put("sessionId", config.getUserToken());
            message.put("meetingId", config.getMeetingId());
            message.put("meetingPassword", "");
            message.put("clientVersion", "v20140605.0");
            message.put("role", config.getRole());
//            message.put("role", 1);
            message.put("mode", 0);
            message.put("type", config.getType());
            message.put("lessonId", config.getLessionId());
            message.put("isInstantMeeting", 1);
            message.put("companyId", AppConfig.SchoolID);
            if (config.getDocument() != null) {
                message.put("itemId", config.getDocument().getItemID());
            }
            sendEventJoinMeeting("");
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_JoinMeeting(MeetingConfig config, String meetingId) {

        try {
            JSONObject message = new JSONObject();
            message.put("action", "JOIN_MEETING");
            message.put("sessionId", config.getUserToken());
            message.put("meetingId", meetingId);
            message.put("meetingPassword", "");
            message.put("clientVersion", "v20140605.0");
            message.put("role", config.getRole());
//            message.put("role", 1);
            message.put("mode", 0);
            message.put("type", config.getType());
            message.put("lessonId", config.getLessionId());
            message.put("isInstantMeeting", 1);
            if (config.getDocument() != null) {
                message.put("itemId", config.getDocument().getItemID());
            }
            sendEventJoinMeeting("");
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_startMeeting(MeetingConfig config, String newMeetingId) {

        try {
            JSONObject message = new JSONObject();
            message.put("action", "JOIN_MEETING");
            message.put("sessionId", config.getUserToken());
            message.put("meetingId", newMeetingId);
            message.put("meetingPassword", "");
            message.put("clientVersion", "v20140605.0");
            config.setRole(MeetingConfig.MeetingRole.HOST);
            message.put("role", config.getRole());
            message.put("mode", 0);
            message.put("type", 0);
            message.put("lessonId", config.getLessionId());
            message.put("isInstantMeeting", 1);
            if (config.getDocument() != null) {
                message.put("itemId", config.getDocument().getItemID());
            }
            doSendMessage(message.toString());
            sendEventJoinMeeting(newMeetingId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_MemberSpeaking() {
        try {
            JSONObject message = new JSONObject();
            message.put("action", "MEMBER_SPEAKING");
            message.put("sessionId", AppConfig.UserToken);
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 老师第一次join时，调一下这个方法，将当前文档ID发给鹏飞
     */
    public void sendMessage_UpdateAttchment(MeetingConfig meetingConfig) {
        JSONObject message = new JSONObject();
        if (meetingConfig.getDocument() == null) {
            return;
        }

        try {
            message.put("sessionId", AppConfig.UserToken);
            if (meetingConfig.getType() == MeetingType.MEETING) {
                message.put("action", "UPDATE_CURRENT_DOCUMENT_ID");
                message.put("documentId", meetingConfig.getDocument().getItemID());
            } else {
                message.put("action", "UPDATE_CURRENT_ATTACHMENT_ID");
                message.put("documentId", meetingConfig.getDocument().getAttachmentID());
            }
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage_DocumentShowed(MeetingConfig config) {

        try {
            JSONObject message = new JSONObject();
            MeetingDocument document = config.getDocument();
            if(document == null){
                return;
            }
            message.put("actionType", 8);
            message.put("attachmentUrl", document.getAttachmentUrl());
            message.put("meetingID", config.getMeetingId());
            message.put("itemId", document.getItemID());
            message.put("incidentID", config.getPageNumber());
            message.put("pageNumber", config.getPageNumber());
            message.put("docType", config.getType());
            message.put("isH5", true);
            //---------
            doSendMessage(wrapperSendMessage(AppConfig.UserToken, 0, Tools.getBase64(message.toString()).replaceAll("[\\s*\t\n\r]", "")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendMessage_UpdateAttchment(config);
    }

    public void sendMessage_DocumentShowedPrivateCoach(MeetingConfig config) {
        try {
            JSONObject message = new JSONObject();
            MeetingDocument document = config.getDocument();
            if(document == null){
                return;
            }
            message.put("roleType", "1");
            message.put("actionType", "8");
            message.put("attachmentUrl", document.getAttachmentUrl());
            message.put("meetingID", config.getMeetingId());
            message.put("itemId", document.getItemID()+"");
            message.put("incidentID", config.getMeetingId()+"");
            message.put("pageNumber", config.getPageNumber());
            message.put("isH5", false);
            message.put("blankPageNumber", "");
            Log.e("doSendMessage--",message.toString());
            doSendMessage(wrapperSendMessage(AppConfig.UserToken, 0, Tools.getBase64(message.toString()).replaceAll("[\\s*\t\n\r]", "")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject message2 = new JSONObject();
        try {
            message2.put("sessionId", AppConfig.UserToken);
            message2.put("action", "UPDATE_CURRENT_ATTACHMENT_ID");
            message2.put("documentId", config.getDocument().getAttachmentID());
            doSendMessage(message2.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }





    }
    public void sendMessage_ViewNote(MeetingConfig config, Note note) {

        try {
            JSONObject message = new JSONObject();
            MeetingDocument document = config.getDocument();
            message.put("actionType", 8);
            message.put("roleType", 1);
//            message.put("eventID", config.getDocument().getEventID());
            message.put("attachmentUrl", note.getAttachmentUrl());
            message.put("meetingID", config.getMeetingId());
            message.put("itemId", note.getNoteID());
            message.put("incidentID", config.getPageNumber());
            message.put("pageNumber", 1);
            message.put("docType", 1);
            message.put("isH5", true);
            //---------
            doSendMessage(wrapperSendMessage(AppConfig.UserToken, 0, Tools.getBase64(message.toString()).replaceAll("[\\s*\t\n\r]", "")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendMessage_UpdateAttchment(config);
    }

    public void sendMessage_LeaveMeeting(MeetingConfig config) {
        try {
            JSONObject message = new JSONObject();
            message.put("action", "LEAVE_MEETING");
            message.put("sessionId", config.getUserToken());
            message.put("meetingId", config.getMeetingId());
//                    message.put("followToLeave", 1);
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_EndMeeting(MeetingConfig config) {
        try {
            JSONObject message = new JSONObject();
            message.put("action", "END_MEETING");
            message.put("sessionId", config.getUserToken());
            message.put("meetingId", config.getMeetingId());
//                    message.put("followToLeave", 1);
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_MyActionFrame(String actions, MeetingConfig meetingConfig) {
        String _actions = Tools.getBase64(actions).replaceAll("[\\s*\t\n\r]", "");
        try {
            JSONObject message = new JSONObject();
            message.put("action", "ACT_FRAME");
            message.put("sessionId", AppConfig.UserToken);
            message.put("retCode", 1);
            message.put("data", _actions);
            message.put("itemId", meetingConfig.getDocument().getItemID());
            message.put("sequenceNumber", "3837");
            message.put("ideaType", "document");
            Log.e("check_send", "sendMessage_MyActionFrame,doSendMessage ");
            doSendMessage(message.toString());
        } catch (JSONException e) {
            Log.e("check_send", "sendMessage_MyActionFrame:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMessage_MyNoteActionFrame(String actions, MeetingConfig meetingConfig, Note note) {
        String _actions = Tools.getBase64(actions).replaceAll("[\\s*\t\n\r]", "");
        try {
            JSONObject message = new JSONObject();
            message.put("action", "ACT_FRAME");
            message.put("sessionId", AppConfig.UserToken);
            message.put("retCode", 1);
            message.put("data", _actions);
            message.put("itemId", note.getNoteID());
            message.put("sequenceNumber", "3837");
            message.put("ideaType", "document");
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_MeetingStatus(MeetingConfig config) {
        try {
            JSONObject message = new JSONObject();
            message.put("action", "MEETING_STATUS");
            message.put("sessionId", config.getUserToken());
            message.put("meetingId", config.getMeetingId());
            message.put("lessonId", config.getLessionId());
            message.put("status", 1);
            Log.e("startRecording", message.toString());
//                    message.put("followToLeave", 1);
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_MakePresenter(MeetingConfig config, MeetingMember member) {

        try {
            JSONObject message = new JSONObject();
            message.put("action", "MAKE_PRESENTER");
            message.put("sessionId", AppConfig.UserToken);
            message.put("meetingId", config.getMeetingId());
            message.put("lessonId", config.getLessionId());
            message.put("newPresenterSessionId", member.getSessionId());
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage_VedioPlayedStatus(int state, float time, String vid, String url) {
        JSONObject message = new JSONObject();
        try {
            message.put("stat", state);
            message.put("actionType", 19);
            message.put("time", time);
            message.put("vid", vid);
            message.put("url", url);
            message.put("type", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        doSendMessage(wrapperSendMessage(AppConfig.UserToken, 0, Tools.getBase64(message.toString()).replaceAll("[\\s*\t\n\r]", "")));
    }


    public void sendMessage_AgoraStatusChange(MeetingConfig config, AgoraMember member) {
        try {
            JSONObject message = new JSONObject();
            message.put("action", "AGORA_STATUS_CHANGE");
            message.put("sessionId", config.getUserToken());
            message.put("agoraStatus", 1);
            message.put("microphoneStatus", member.isMuteAudio() ? 3 : 2);
            message.put("cameraStatus", member.isMuteVideo() ? 3 : 2);
            message.put("screenStatus", 0);
            message.put("client", "android");
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage_AgoraStatusChange(MeetingConfig config, boolean isMicroOn, boolean isCameraOn) {
        try {
            JSONObject message = new JSONObject();
            message.put("action", "AGORA_STATUS_CHANGE");
            message.put("sessionId", config.getUserToken());
            message.put("agoraStatus", 1);
            message.put("microphoneStatus", isMicroOn ? 2 : 3);
            message.put("cameraStatus", isCameraOn ? 2 : 3);
            message.put("screenStatus", 0);
            message.put("client", "android");
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void sendMessage_recording_AgoraStatusChange(MeetingConfig config, boolean isMicroOn, boolean isCameraOn) {
        try {
            JSONObject message = new JSONObject();
            message.put("action", "AGORA_STATUS_CHANGE");
            message.put("sessionId", config.getUserToken());
            message.put("agoraStatus", 1);
            message.put("microphoneStatus", isMicroOn ? 2 : 3);
            message.put("cameraStatus", isCameraOn ? 2 : 3);
            message.put("screenStatus", 0);

            Log.e("startRecording", "" + message.toString());
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage_audio_sync(MeetingConfig config, EventSoundSync eventSoundSync) {
        try {
            JSONObject message = new JSONObject();
            message.put("action", "AUDIO_SYNC");
            message.put("sessionId", config.getUserToken());
            message.put("status", eventSoundSync.getStatus());
            message.put("soundtrackId", eventSoundSync.getSoundtrackID());
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_Soundtrack_Playing_Status(String soundtrackId, int status, long time) {
        JSONObject message = new JSONObject();
        try {
            message.put("actionType", 23);
            message.put("soundtrackId", soundtrackId);
            message.put("stat", status); //1 开始播放  0 停止播放  2暂停播放 3继续播放
            if (status == 2 || status == 4 || status == 5 || status == 6|| status == 1) {
                //   4每隔1秒发播放进度
                //   5 进度条拖动停止通知播放进度
                message.put("time", time);
            }
            doSendMessage(wrapperSendMessage(AppConfig.UserToken, 0, Tools.getBase64(message.toString()).replaceAll("[\\s*\t\n\r]", "")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_InviteToMeeting(MeetingConfig config, String users) {
        try {
            JSONObject message = new JSONObject();
            message.put("action", "INVITE_TO_MEETING");
            message.put("sessionId", config.getUserToken());
            message.put("meetingId", config.getMeetingId());
            message.put("userIds", users);
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_DocVedioStatus(VedioData vedioData, int stat, long time) {
        JSONObject message = new JSONObject();
        try {
            message.put("stat", stat);
            message.put("actionType", 19);
            message.put("time", time);
            message.put("vid", vedioData.getId());
            message.put("url", vedioData.getUrl());
            message.put("type", 1);
            doSendMessage(wrapperSendMessage(AppConfig.UserToken, 0, Tools.getBase64(message.toString()).replaceAll("[\\s*\t\n\r]", "")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void sendMessage_ViewModeStatus(int viewMode, String currentSessionID) {
        JSONObject message = new JSONObject();
        try {
            message.put("videoMode", viewMode);
            message.put("actionType", 9);
            if (viewMode == 2) {
                message.put("currentSessionID", currentSessionID);
            }
            doSendMessage(wrapperSendMessage(AppConfig.UserToken, 0, Tools.getBase64(message.toString()).replaceAll("[\\s*\t\n\r]", "")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_MuteStatus(int stat) {
        JSONObject message = new JSONObject();
        try {
            message.put("stat", stat);
            message.put("actionType", 14);
            message.put("userId", "");
            doSendMessage(wrapperSendMessage(AppConfig.UserToken, 0, Tools.getBase64(message.toString()).replaceAll("[\\s*\t\n\r]", "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_KickMember(MeetingConfig meetingConfig, MeetingMember member) {
        JSONObject message = new JSONObject();
        try {
            message.put("actionType", 30);
            message.put("meetingId", meetingConfig.getMeetingId());
            message.put("userId", member.getUserId());
            doSendMessage(wrapperSendMessage(AppConfig.UserToken, 1, Tools.getBase64(message.toString()).replaceAll("[\\s*\t\n\r]", ""), member.getUserId() + ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_SelectSpeaker(String userId) {
        JSONObject message = new JSONObject();
        try {
            message.put("actionType", 31);
            message.put("userId", userId);
            doSendMessage(wrapperSendMessage(AppConfig.UserToken, 0, Tools.getBase64(message.toString()).replaceAll("[\\s*\t\n\r]", ""), ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_myNoteData(String pageIdentiﬁer, int noteId, SendWebScoketNoteBean sendWebNoteData) {
        String dataStr = Tools.getBase64(new Gson().toJson(sendWebNoteData));
        Log.e("SocketMessageManage", "sendMessage_myNoteData_dataStr = " + dataStr);
        doSendMessage(wrapperSendMessage(AppConfig.UserToken, pageIdentiﬁer, noteId, dataStr));
    }

    public void sendMessage_openOrCloseNote(String address, int noteId, String uuid) {
        JSONObject message = new JSONObject();
        try {
            message.put("action", "OPEN_OR_CLOSE_NOTE");
            message.put("sessionId", AppConfig.UserToken);
            message.put("noteId", noteId);
            message.put("address", address);
            message.put("strokeId", uuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doSendMessage(message.toString());
    }

    private WebSocketClient getClient() {
        socketClient = AppConfig.webSocketClient;
        return socketClient;
    }

    private void doSendMessage(String message) {
        JSONObject finalMessage = null;
        try {
            finalMessage = new JSONObject(message);
            finalMessage.put("client", "android");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (getClient() != null) {
            try {
                getClient().send(finalMessage.toString());
                Log.e("doSendMessage", "send:" + finalMessage);
            } catch (Exception exception) {
                Log.e("doSendMessage", "send_exception:" + exception.getMessage());
            }
        } else {
            sendMessageAgain(finalMessage.toString());
            Log.e("doSendMessage", "send_exception,client_is_null");
        }
    }

    private void sendMessageAgain(final String message) {
        Observable.just("send_again").delay(1000, TimeUnit.MILLISECONDS).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (getClient() != null) {
                    try {
                        getClient().send(message);
                    } catch (Exception exception) {

                    }
                }
            }
        }).subscribe();

    }

    private String wrapperSendMessage(String sessionId, int type, String data) {
        JSONObject message = new JSONObject();
        try {
            message.put("action", "SEND_MESSAGE");
            message.put("sessionId", sessionId);
            message.put("type", type);
            message.put("userList", "");
            message.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message.toString();

    }

    private String wrapperSendMessage(String sessionId, String pageIdentiﬁer, int noteId, String data) {
        JSONObject message = new JSONObject();
        try {
            message.put("action", "NOTE_DATA");
            message.put("sessionId", sessionId);
            message.put("pageIdentiﬁer", pageIdentiﬁer);
            message.put("noteId", noteId);
            message.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message.toString();

    }

    private String wrapperSendMessage(String sessionId, int type, String data, String userList) {
        JSONObject message = new JSONObject();
        try {
            message.put("action", "SEND_MESSAGE");
            message.put("sessionId", sessionId);
            message.put("type", type);
            message.put("userList", userList);
            message.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message.toString();

    }

    public void registerMessageReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cn.socket");
        context.registerReceiver(messageReceiver, intentFilter);
    }

    public void release() {
        context.unregisterReceiver(messageReceiver);
        instance = null;
    }

    private void sendEventJoinMeeting(String newMeetingId) {
        EventSendJoinMeetingMessage joinMeetingMessage = new EventSendJoinMeetingMessage();
        joinMeetingMessage.setNewMeetingId(newMeetingId);
        EventBus.getDefault().post(joinMeetingMessage);
    }

    public void sendMessage_MySpeakerViewSizeChange(int size) {
        JSONObject message = new JSONObject();
        try {
            message.put("actionType", 32);
            message.put("size", size);
            doSendMessage(wrapperSendMessage(AppConfig.UserToken, 0, Tools.getBase64(message.toString()).replaceAll("[\\s*\t\n\r]", "")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
