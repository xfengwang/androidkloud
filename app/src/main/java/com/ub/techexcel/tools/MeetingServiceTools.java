package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.AccountSettingBean;
import com.kloudsync.techexcel.bean.DocumentPage;
import com.kloudsync.techexcel.bean.EventNote;
import com.kloudsync.techexcel.bean.EventNotePageActions;
import com.kloudsync.techexcel.bean.EventPageActions;
import com.kloudsync.techexcel.bean.EventPageActionsForSoundtrack;
import com.kloudsync.techexcel.bean.EventPageNotes;
import com.kloudsync.techexcel.bean.EventPageNotesForSoundtrack;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.bean.MeetingPauseOrResumBean;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.bean.TvDevice;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.ToastUtils;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.service.ConnectService;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.kloudsync.techexcel.config.AppConfig.URL_PUBLIC;

public class MeetingServiceTools {

    public static final int ERRORMESSAGE = 0x1105;
    public static final int GETPDFLIST = 0x1101;
    public static final int GETGETPAGEOBJECTS = 0x1102;
    public static final int ENTERTEACHERONGOINGMEETING = 0x1103;
    public static final int UPLOADFILEWITHHASH = 0x1104;
    public static final int GETTOPICATTACHMENT = 0x1106;

    public static final int STARTRECORDING = 0x2101;
    public static final int ENDRECORDING = 0x2102;
    public static final int GETMEETINGMEMBERS = 0x2103;
    public static final int GETACCOUNTINFO = 0x2104;
    public static final int UPDATECOMPANYINFO = 0x2105;
    public static final int DELETECOMPANYLOGO = 0x2106;
    public static final int MEMBERONOTHERDEVICE = 0x2107;
    public static final int GETBLUETOOTHNOTEDETAIL = 0x2108;


    private ConcurrentHashMap<Integer, ServiceInterfaceListener> hashMap = new ConcurrentHashMap<>();

    private static MeetingServiceTools meetingServiceTools;

    public static MeetingServiceTools getInstance() {
        if (meetingServiceTools == null) {
            synchronized (MeetingServiceTools.class) {
                if (meetingServiceTools == null) {
                    meetingServiceTools = new MeetingServiceTools();
                }
            }
        }
        return meetingServiceTools;
    }

    private void putInterface(int code, ServiceInterfaceListener serviceInterfaceListener) {
        ServiceInterfaceListener serviceInterfaceListener2 = hashMap.get(code);
        if (serviceInterfaceListener2 == null) {
            hashMap.put(code, serviceInterfaceListener);
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int code = msg.what;
            if (code == ERRORMESSAGE) {
	            String errorMessage = (String) msg.obj;
	            ToastUtils.show(App.getAppContext(), errorMessage);
            } else {
                ServiceInterfaceListener serviceInterfaceListener = hashMap.get(code);
                if (serviceInterfaceListener != null) {
                    serviceInterfaceListener.getServiceReturnData(msg.obj);
                    hashMap.remove(code);
                }
            }
        }
    };


    /**
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void getPdfList(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
                Log.e("meetingservicrtools", url + returnJson.toString());
                try {
                    if (returnJson.getInt("RetCode") == 0) {
                        JSONObject service = returnJson.getJSONObject("RetData");
                        JSONArray lineitems = service.getJSONArray("AttachmentList");
                        List<LineItem> items = new ArrayList<LineItem>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            LineItem item = new LineItem();
                            item.setFileName(lineitem.getString("Title"));
                            item.setUrl(lineitem.getString("AttachmentUrl"));
                            item.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                            item.setHtml5(false);
                            item.setItemId(lineitem.getString("ItemID"));
                            item.setAttachmentID(lineitem.getString("AttachmentID"));
                            item.setNewPath(lineitem.getString("NewPath"));
                            item.setFlag(0);
                            if (lineitem.getInt("Status") == 0) {
                                items.add(item);
                            }
                        }
                        Message msg = Message.obtain();
                        msg.obj = items;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void getAccountInfo(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
                Log.e("meetingservicrtools", url + returnJson.toString());
                try {
                    if (returnJson.getInt("code") == 0 && returnJson.getString("msg").equals("success")) {
                        AccountSettingBean accountSettingBean = new AccountSettingBean();

                        JSONObject datajson = returnJson.getJSONObject("data");
                        accountSettingBean.setSchoolName(datajson.getString("companyName"));
                        accountSettingBean.setVerifyEmailAddress(datajson.getString("verifyEmailAddress"));
                        accountSettingBean.setWebAddress(datajson.getString("webAddress"));
	                    accountSettingBean.setSystemType(datajson.getInt("systemType"));
                        Message msg = Message.obtain();
                        msg.obj = accountSettingBean;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    AccountSettingBean accountSettingBean = new AccountSettingBean();
                    accountSettingBean.setSystemType(0);
                    Message msg = Message.obtain();
                    msg.obj = accountSettingBean;
                    msg.what = code;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void deleteCompanyLogo(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.kloudsync.techexcel.service.ConnectService.getIncidentDataattachment(url);
                Log.e("meetingservicrtools", url + returnJson.toString());
                Message msg = Message.obtain();
                msg.obj = returnJson;
                msg.what = code;
                handler.sendMessage(msg);
            }
        }).start();
    }


    public void updateCompanyInfo(final String url, final int code, final JSONObject jsonObject, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.submitDataByJson(url, jsonObject);
                Log.e("meetingservicrtools", url + returnJson.toString());
                try {
                    if (returnJson.getInt("code") == 0 && returnJson.getString("msg").equals("success")) {
                        Message msg = Message.obtain();
                        msg.obj = "";
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start(ThreadManager.getManager());
    }


    public void syncGetDocuments(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {

        JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
        Log.e("meetingservicrtools", url + returnJson.toString());
        try {
            if (returnJson.getInt("RetCode") == 0) {
                JSONObject service = returnJson.getJSONObject("RetData");
                JSONArray lineitems = service.getJSONArray("AttachmentList");
                List<LineItem> items = new ArrayList<LineItem>();
                for (int j = 0; j < lineitems.length(); j++) {
                    JSONObject lineitem = lineitems.getJSONObject(j);
                    LineItem item = new LineItem();
                    item.setFileName(lineitem.getString("Title"));
                    item.setUrl(lineitem.getString("AttachmentUrl"));
                    item.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                    item.setHtml5(false);
                    item.setItemId(lineitem.getString("ItemID"));
                    item.setAttachmentID(lineitem.getString("AttachmentID"));
                    item.setNewPath(lineitem.getString("NewPath"));
                    item.setFlag(0);
                    if (lineitem.getInt("Status") == 0) {
                        items.add(item);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void getTopicAttachment(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
                Log.e("meetingservicrtools", url + returnJson.toString());
                try {
                    if (returnJson.getInt("RetCode") == 0) {
                        JSONArray lineitems = returnJson.getJSONArray("RetData");
                        List<LineItem> items = new ArrayList<LineItem>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            LineItem item = new LineItem();
                            item.setTopicId(lineitem.getInt("TopicID"));
                            item.setSyncRoomCount(lineitem.getInt("SyncCount"));
                            item.setFileName(lineitem.getString("Title"));
                            item.setUrl(lineitem.getString("AttachmentUrl"));
                            item.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                            item.setHtml5(false);
                            item.setItemId(lineitem.getString("ItemID"));
                            item.setAttachmentID(lineitem.getString("AttachmentID"));
                            item.setCreatedDate(lineitem.getString("CreatedDate"));
                            String attachmentUrl = lineitem.getString("AttachmentUrl");
                            if (!TextUtils.isEmpty(attachmentUrl)) {
                                String newPath = attachmentUrl.substring(attachmentUrl.indexOf(".com") + 5, attachmentUrl.lastIndexOf("/"));
                                item.setNewPath(newPath);
                            }
                            item.setFlag(0);
                            if (lineitem.getInt("Status") == 0) {
                                items.add(item);
                            }
                        }
                        Message msg = Message.obtain();
                        msg.obj = items;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getBlueToothNoteDetail(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = ConnectService.getIncidentbyHttpGet(url);
                Log.e("floatingnote", url + returnJson.toString());
                try {
                    if (returnJson.getInt("RetCode") == 0) {
                        JSONObject notejson = returnJson.getJSONObject("RetData");
                        Note note = new Note();

                        note.setTitle(notejson.getString("Title"));
                        note.setNoteID(notejson.getInt("NoteID"));
                        note.setAttachmentUrl(notejson.getString("AttachmentUrl"));
                        note.setSourceFileUrl(notejson.getString("SourceFileUrl"));
                        note.setLastModifiedDate(notejson.getString("LastModifiedDate"));

                        Message msg = Message.obtain();
                        msg.obj = note;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void getMemberOnOtherDevice(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
                Log.e("meetingservicrtools", url + returnJson.toString());
                TvDevice tvDevice = new TvDevice();
                try {
                    if (returnJson.getInt("code") == 0 && returnJson.getString("msg").equals("success")) {
                        JSONObject jsonObject = returnJson.getJSONObject("data");
                        if (jsonObject != null) {
                            tvDevice.setUserID(jsonObject.getString("userId"));
                            tvDevice.setDeviceType(jsonObject.getInt("deviceType"));
                        }
                        Message msg = Message.obtain();
                        msg.obj = tvDevice;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = tvDevice;
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    Message msg3 = Message.obtain();
                    msg3.what = code;
                    msg3.obj = tvDevice;
                    handler.sendMessage(msg3);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getGetPageObjects(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
                Log.e("meetingservicrtools", url + "   " + returnJson.toString());
                try {

                    if (returnJson.getInt("code") == 0) {
                        JSONArray data = returnJson.getJSONArray("data");
                        String mmm = "";
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObject1 = data.getJSONObject(i);
                            String ddd = jsonObject1.getString("data");
                            if (!TextUtil.isEmpty(ddd)) {
                                String dd = "'" + Tools.getFromBase64(ddd) + "'";
                                if (i == 0) {
                                    mmm += "[" + dd;
                                } else {
                                    mmm += "," + dd;
                                }
                                if (i == data.length() - 1) {
                                    mmm += "]";
                                }
                            }
                        }
                        Message msg = Message.obtain();
                        msg.what = code;
                        msg.obj = mmm;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public EventPageActions syncGetPageActions(MeetingConfig config) {

        String url = "";
        switch (config.getType()) {
            case MeetingType.DOC:
                url = "https://wss.peertime.cn/MeetingServer/page_object/list?lessonId=0&itemId=" + 0 + "&pageNumber=" + config.getPageNumber() +
                        "&attachmentId=" + config.getDocument().getAttachmentID() + "&soundtrackId=0&displayDrawingLine=0";
                break;

            //  https://wss.peertime.cn/MeetingServer/page_object/list?lessonId=0&itemId=0&pageNumber=1&attachmentId=58352&soundtrackId=0&displayDrawingLine=0
            case MeetingType.MEETING:
                url = "https://wss.peertime.cn/MeetingServer/page_object/list?lessonId=" + config.getLessionId() + "&itemId=" +
                        config.getDocument().getItemID() + "&pageNumber=" + config.getPageNumber();
                break;
            case MeetingType.SYNCBOOK:
                break;
            case MeetingType.SYNCROOM:
                break;
            default:
        }


        JSONObject returnJson = ConnectService.getIncidentbyHttpGet(url);
        Log.e("syncGetPageActions", url + "   " + returnJson.toString());
        EventPageActions pageActions = new EventPageActions();
        pageActions.setPageNumber(config.getPageNumber());
        try {
            if (returnJson.has("code")) {
                if (returnJson.getInt("code") == 0) {
                    JSONArray data = returnJson.getJSONArray("data");
                    String dataJson = "";
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject1 = data.getJSONObject(i);
                        String _data = jsonObject1.getString("data");
                        if (!TextUtil.isEmpty(_data)) {
                            String dd = "'" + Tools.getFromBase64(_data) + "'";
                            if (i == 0) {
                                dataJson += "[" + dd;
                            } else {
                                dataJson += "," + dd;
                            }
                            if (i == data.length() - 1) {
                                dataJson += "]";
                            }
                        }
                    }

                    pageActions.setData(dataJson);
                } else {

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageActions;

    }

    public EventPageActionsForSoundtrack syncGetPageActions(MeetingConfig config, String pageNumber, String attachmentId, String itemId, String soundtrackID) {
        String url = "";
        switch (config.getType()) {
            case MeetingType.DOC:

                url = "https://wss.peertime.cn/MeetingServer/page_object/list?lessonId=0&itemId=" + 0 + "&pageNumber=" + pageNumber +
                        "&attachmentId=" + attachmentId + "&soundtrackId=" + soundtrackID + "&displayDrawingLine=0";
//				url = "https://api.peertime.cn/peertime/V1/PageObject/GetPageObjects?lessonID=0&itemID=" + 0 + "&pageNumber=" + pageNumber +
//						"&attachmentID=" + attachmentId + "&soundtrackID=" + soundtrackID + "&displayDrawingLine=0";
                break;
            case MeetingType.MEETING:
                url = "https://wss.peertime.cn/MeetingServer/page_object/list?lessonId=" + config.getLessionId() + "&itemId=" +
                        itemId + "&pageNumber=" + pageNumber;
                break;
            case MeetingType.SYNCBOOK:
                break;
            case MeetingType.SYNCROOM:
                break;
            default:
        }

        JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
        Log.e("syncGetPageActions", url + "   " + returnJson.toString());
        EventPageActionsForSoundtrack pageActions = new EventPageActionsForSoundtrack();
        pageActions.setPageNumber(config.getPageNumber());
        try {
            if (returnJson.has("code")) {
                if (returnJson.getInt("code") == 0) {
                    JSONArray data = returnJson.getJSONArray("data");
                    String dataJson = "";
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject1 = data.getJSONObject(i);
                        String _data = jsonObject1.getString("data");
                        if (!TextUtil.isEmpty(_data)) {
                            String dd = "'" + Tools.getFromBase64(_data) + "'";
                            if (i == 0) {
                                dataJson += "[" + dd;
                            } else {
                                dataJson += "," + dd;
                            }
                            if (i == data.length() - 1) {
                                dataJson += "]";
                            }
                        }
                    }

                    pageActions.setData(dataJson);
                } else {

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageActions;

    }


    //    https://api.peertime.cn/peertime/V1/Soundtrack/PageActions?soundtrackID=37402&time=30008&pageNumber=2

    public EventPageActionsForSoundtrack syncGetPageActionsInSountrackByTime(long time,int pageNumber,String soundtrackID) {
        String url = URL_PUBLIC + "Soundtrack/PageActions?soundtrackID=" + soundtrackID + "&time=" + time + "&pageNumber=" + pageNumber;

        JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
        Log.e("syncGetPageActionsInSountrackByTime", url + "   " + returnJson.toString());
        EventPageActionsForSoundtrack pageActions = new EventPageActionsForSoundtrack();
        pageActions.setPageNumber(pageNumber);
        try {
            if (returnJson.has("RetCode")) {
                if (returnJson.getInt("RetCode") == 0) {
                    JSONObject retData = returnJson.getJSONObject("RetData");
                    if(retData != null && retData.has("Actions")){
                        JSONArray data = retData.getJSONArray("Actions");
                        String dataJson = "";
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObject1 = data.getJSONObject(i);
                            String _data = jsonObject1.getString("Data");
                            if (!TextUtil.isEmpty(_data)) {
                                String dd = "'" + Tools.getFromBase64(_data) + "'";
                                if (i == 0) {
                                    dataJson += "[" + dd;
                                } else {
                                    dataJson += "," + dd;
                                }
                                if (i == data.length() - 1) {
                                    dataJson += "]";
                                }
                            }
                        }

                        pageActions.setData(dataJson);
                    }

                } else {

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageActions;

    }


    public EventNotePageActions syncGetPageActions(MeetingConfig config, Note note) {

        String url = "";
        switch (config.getType()) {
            case MeetingType.DOC:
                url = "https://api.peertime.cn/peertime/V1/PageObject/GetPageObjects?lessonID=0&itemID=" + 0 + "&pageNumber=" + config.getPageNumber() +
                        "&attachmentID=" + config.getDocument().getAttachmentID() + "&soundtrackID=0&displayDrawingLine=0";
                break;
            case MeetingType.MEETING:
                if (note == null) {
                    return new EventNotePageActions();
                }
                url = URL_PUBLIC + "PageObject/GetPageObjects?lessonID=" + config.getLessionId() + "&itemID=" +
                        note.getNoteID() + "&pageNumber=" + 1;
                break;
            case MeetingType.SYNCBOOK:
                break;
            case MeetingType.SYNCROOM:
                break;
            default:
        }


        JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
        Log.e("syncGetPageActions", url + "   " + returnJson.toString());
        EventNotePageActions pageActions = new EventNotePageActions();
        pageActions.setPageNumber(config.getPageNumber());
        try {
            if (returnJson.getInt("RetCode") == 0) {
                JSONArray data = returnJson.getJSONArray("RetData");
                String dataJson = "";
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject1 = data.getJSONObject(i);
                    String _data = jsonObject1.getString("Data");
                    if (!TextUtil.isEmpty(_data)) {
                        String dd = "'" + Tools.getFromBase64(_data) + "'";
                        if (i == 0) {
                            dataJson += "[" + dd;
                        } else {
                            dataJson += "," + dd;
                        }
                        if (i == data.length() - 1) {
                            dataJson += "]";
                        }
                    }
                }

                pageActions.setData(dataJson);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageActions;

    }


    public EventPageNotesForSoundtrack syncGetPageNotesForSoundtrack(String attachmentId, String pageNumber) {
        String url = URL_PUBLIC + "DocumentNote/List?syncRoomID=" + 0 + "&documentItemID=" + attachmentId +
                "&pageNumber=" + pageNumber + "&userID=" + AppConfig.UserID;
        JSONObject returnJson = com.kloudsync.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
        Log.e("syncGetPageNotes", url + "   " + returnJson.toString());
        EventPageNotesForSoundtrack pageNotes = new EventPageNotesForSoundtrack();
        pageNotes.setPageNumber(Integer.parseInt(pageNumber));
        try {
            if (returnJson.getInt("RetCode") == 0) {
                JSONArray _notes = returnJson.getJSONArray("RetData");
                List<NoteDetail> notes = new ArrayList<NoteDetail>();
                for (int j = 0; j < _notes.length(); j++) {
                    JSONObject note = _notes.getJSONObject(j);
                    notes.add(new Gson().fromJson(note.toString(), NoteDetail.class));
                }
                pageNotes.setNotes(notes);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return pageNotes;
    }

    public EventPageNotes syncGetPageNotes(MeetingConfig meetingConfig) {
        String url = URL_PUBLIC + "DocumentNote/List?syncRoomID=" + 0 + "&documentItemID=" + meetingConfig.getDocument().getAttachmentID() +
                "&pageNumber=" + meetingConfig.getPageNumber() + "&userID=" + AppConfig.UserID;
        JSONObject returnJson = com.kloudsync.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
        Log.e("syncGetPageNotes", url + "   " + returnJson.toString());
        EventPageNotes pageNotes = new EventPageNotes();
        pageNotes.setPageNumber(meetingConfig.getPageNumber());
        try {
            if (returnJson.getInt("RetCode") == 0) {
                JSONArray _notes = returnJson.getJSONArray("RetData");
                List<NoteDetail> notes = new ArrayList<NoteDetail>();
                for (int j = 0; j < _notes.length(); j++) {
                    JSONObject note = _notes.getJSONObject(j);
                    notes.add(new Gson().fromJson(note.toString(), NoteDetail.class));
                }
                pageNotes.setNotes(notes);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return pageNotes;
    }

    public EventNote syncGetNoteByLinkId(int linkId) {
        String url = URL_PUBLIC + "DocumentNote/NoteByLinkID?linkID=" + linkId;
        JSONObject returnjson = com.kloudsync.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
        Log.e("getNoteByLinkID", url + "  " + returnjson.toString());
        EventNote eventNote = new EventNote();
        eventNote.setLinkId(linkId);
        try {
            if (returnjson.getInt("RetCode") == 0) {
                JSONObject lineitem = returnjson.getJSONObject("RetData");
                Note note = new Note();
                String attachmentUrl = lineitem.getString("AttachmentUrl");
                note.setLocalFileID(lineitem.getString("LocalFileID"));
                note.setNoteID(lineitem.getInt("NoteID"));
                note.setLinkID(lineitem.getInt("LinkID"));
                note.setPageNumber(lineitem.getInt("PageNumber"));
                note.setDocumentItemID(lineitem.getInt("DocumentItemID"));
                note.setFileName(lineitem.getString("Title"));
                note.setAttachmentUrl(attachmentUrl);
                note.setPageCount(lineitem.getInt("PageCount"));
                note.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                note.setAttachmentID(lineitem.getInt("AttachmentID"));
                String noteUrl = attachmentUrl.substring(0, attachmentUrl.lastIndexOf("<")) + 1 + attachmentUrl.substring(attachmentUrl.lastIndexOf("."));
                note.setUrl(noteUrl);

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

                note.setNewPath(attachmentUrl.substring(attachmentUrl.indexOf(".com") + 5, attachmentUrl.lastIndexOf("/")));

                List<DocumentPage> pages = new ArrayList<>();
                for (int j = 0; j < note.getPageCount(); ++j) {
                    String pageUrl = "";
                    DocumentPage page = new DocumentPage();
                    page.setLocalFileId(note.getLocalFileID());
                    page.setPageNumber(j + 1);
                    page.setDocumentId(note.getDocumentItemID());
                    page.setLocalFileId(note.getLocalFileID());
                    if (TextUtils.isEmpty(preUrl)) {
                        page.setPageUrl(pageUrl);
                    } else {
                        page.setPageUrl(preUrl + (j + 1) + endUrl);
                    }
                    pages.add(page);
                }
                note.setDocumentPages(pages);
                eventNote.setNote(note);
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return eventNote;
    }

    public EventNote syncGetNoteByNoteId(int noteId) {
	    if (noteId <= 0) {
		    return new EventNote();
	    }
        String url = URL_PUBLIC + "DocumentNote/Item?noteID=" + noteId;
        JSONObject returnjson = com.kloudsync.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
        Log.e("syncGetNoteByNoteId", url + "  " + returnjson.toString());
        EventNote eventNote = new EventNote();
        eventNote.setNoteId(noteId);
        try {
            if (returnjson.getInt("RetCode") == 0) {
                JSONObject lineitem = returnjson.getJSONObject("RetData");
                Note note = new Note();
                String attachmentUrl = lineitem.getString("AttachmentUrl");
                note.setLocalFileID(lineitem.getString("LocalFileID"));
                Log.e("syncGetNoteByNoteId", "set_local_file_id:" + lineitem.getString("LocalFileID"));
                note.setNoteID(lineitem.getInt("NoteID"));
//                note.setLinkID(lineitem.getInt("LinkID"));

                note.setDocumentItemID(lineitem.getInt("AttachmentFileID"));
                note.setFileName(lineitem.getString("Title"));
                note.setAttachmentUrl(attachmentUrl);
                note.setPageCount(lineitem.getInt("PageCount"));
                note.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                note.setAttachmentID(lineitem.getInt("AttachmentID"));
                String noteUrl = attachmentUrl.substring(0, attachmentUrl.lastIndexOf("<")) + 1 + attachmentUrl.substring(attachmentUrl.lastIndexOf("."));
                note.setUrl(noteUrl);

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

                note.setNewPath(attachmentUrl.substring(attachmentUrl.indexOf(".com") + 5, attachmentUrl.lastIndexOf("/")));

                List<DocumentPage> pages = new ArrayList<>();
                for (int j = 0; j < note.getPageCount(); ++j) {
                    String pageUrl = "";
                    DocumentPage page = new DocumentPage();
                    page.setPageNumber(j + 1);
                    page.setDocumentId(note.getDocumentItemID());
                    page.setLocalFileId(note.getLocalFileID());
                    if (TextUtils.isEmpty(preUrl)) {
                        page.setPageUrl(pageUrl);
                    } else {
                        page.setPageUrl(preUrl + (j + 1) + endUrl);
                    }
                    pages.add(page);
                }
                note.setDocumentPages(pages);
                Log.e("check_note", "local_file_id:" + note.getLocalFileID());
                eventNote.setNote(note);
            } else {

            }
        } catch (JSONException e) {
            Log.e("syncGetNoteByNoteId", "JSONException:" + e.getMessage());

            e.printStackTrace();
        }
        return eventNote;
    }


    public void enterTeacherOnGoingMeeting(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
                Log.e("meetingservicrtools", url + returnJson.toString());
                try {
                    if (returnJson.getInt("RetCode") == 0) {
                        JSONObject service = returnJson.getJSONObject("RetData");
                        ServiceBean bean = new ServiceBean();
                        bean.setId(service.getInt("LessonID"));
                        String des = service.getString("Description");
                        bean.setDescription(des);
                        int statusID = service.getInt("StatusID");
                        bean.setStatusID(statusID);
                        bean.setRoleinlesson(service.getInt("RoleInLesson"));
                        if (bean.getRoleinlesson() == 3) {
                            bean.setRoleinlesson(1);
                        }
                        JSONArray memberlist = service.getJSONArray("MemberInfoList");
                        for (int i = 0; i < memberlist.length(); i++) {
                            JSONObject jsonObject = memberlist.getJSONObject(i);
                            int role = jsonObject.getInt("Role");
                            if (role == 2) { //teacher
                                bean.setTeacherName(jsonObject.getString("MemberName"));
                                bean.setTeacherId(jsonObject.getString("MemberID"));
                            } else if (role == 1) {
                                bean.setUserName(jsonObject.getString("MemberName"));
                                bean.setUserId(jsonObject.getString("MemberID"));
                            }
                        }
                        Message msg = Message.obtain();
                        msg.obj = bean;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }


    public void uploadFileWithHash(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.submitDataByJson(url, null);
                Log.e("meetingservicrtools", url + returnJson.toString());
                Message msg = Message.obtain();
                msg.obj = returnJson;
                msg.what = code;
                handler.sendMessage(msg);

            }
        }).start(ThreadManager.getManager());

    }

    public void getSyncroomDetail(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
                Log.e("meetingservicrtools", url + returnJson.toString());
                try {
                    if (returnJson.getInt("RetCode") == 0) {
                        JSONArray lineitems = returnJson.getJSONObject("RetData").getJSONArray("FileList");
                        List<LineItem> items = new ArrayList<LineItem>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            LineItem item = new LineItem();
                            item.setTopicId(lineitem.getInt("TopicID"));
                            item.setSyncRoomCount(lineitem.getInt("SyncCount"));
                            item.setFileName(lineitem.getString("Title"));
                            item.setUrl(lineitem.getString("AttachmentUrl"));
                            item.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                            item.setHtml5(false);
                            item.setItemId(lineitem.getString("ItemID"));
                            item.setAttachmentID(lineitem.getString("AttachmentID"));
                            item.setCreatedDate(lineitem.getString("CreatedDate"));
                            String attachmentUrl = lineitem.getString("AttachmentUrl");
                            if (!TextUtils.isEmpty(attachmentUrl)) {
                                String newPath = attachmentUrl.substring(attachmentUrl.indexOf(".com") + 5, attachmentUrl.lastIndexOf("/"));
                                item.setNewPath(newPath);
                            }
                            item.setFlag(0);
                            if (lineitem.getInt("Status") == 0) {
                                items.add(item);
                            }
                        }
                        Message msg = Message.obtain();
                        msg.obj = items;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getMeetingMembers(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = ConnectService.getIncidentbyHttpGet(url);
                Log.e("getMeetingMembers", url + returnJson.toString());
                try {
                    if (returnJson.getInt("RetCode") == 0 && returnJson.getString("msg").equals("success")) {
                        JSONArray meetingMemberJson = returnJson.getJSONArray("data");
                        List<MeetingMember> meetingMembers = new ArrayList<>();
                        for (int j = 0; j < meetingMemberJson.length(); j++) {
                            JSONObject memberjson = meetingMemberJson.getJSONObject(j);
                            MeetingMember meetingMember = new MeetingMember();
                            meetingMember.setUserId(memberjson.getInt("userId"));
                            meetingMember.setUserName(memberjson.getString("userName"));
                            meetingMember.setAvatarUrl(memberjson.getString("avatarUrl"));
                            meetingMember.setIsOnline(memberjson.getInt("isOnline"));
                            meetingMember.setHandStatus(memberjson.getInt("handStatus"));
                            meetingMember.setSessionId(memberjson.getString("sessionId"));
                            meetingMember.setRole(memberjson.getInt("role"));
                            meetingMember.setPresenter(memberjson.getInt("presenter"));
                            meetingMember.setRongCloudId(memberjson.getInt("rongCloudId"));
                            meetingMember.setAgoraStatus(memberjson.getInt("agoraStatus"));
                            meetingMember.setMicrophoneStatus(memberjson.getInt("microphoneStatus"));
                            meetingMember.setCameraStatus(memberjson.getInt("cameraStatus"));
                            meetingMembers.add(meetingMember);
                        }
                        Message msg = Message.obtain();
                        msg.obj = meetingMembers;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 是否举手
     */
    public void raiseHandOnStage(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = ConnectService.getIncidentbyHttpGet(url);
                Log.e("getMeetingMembers", url + returnJson.toString());
                try {
                    if (returnJson.getInt("RetCode") == 0 && returnJson.getString("msg").equals("success")) {
                        Message msg = Message.obtain();
//                            msg.obj = meetingMembers;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void startRecording(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.submitDataByJson(url, null);

                Log.e("startRecording", url + "   " + returnJson.toString());
                try {
                    int retCode = returnJson.getInt("code");
                    if (retCode == 0 && returnJson.getString("msg").equals("success")) {
                        Message msg = Message.obtain();
                        msg.obj = returnJson.getInt("data");
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start(ThreadManager.getManager());
    }


    public void endRecording(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = ConnectService.submitDataByJson(url, null);
                Log.e("recording_end", url + "   " + returnJson.toString());
                try {
                    int retCode = returnJson.getInt("code");
                    if (retCode == 0 && returnJson.getString("msg").equals("success")) {
                        Message msg = Message.obtain();
                        msg.obj = retCode;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start(ThreadManager.getManager());
    }

    public String syncGetNoteAttachmentUrlByNoteId(int noteId) {

	    String url = URL_PUBLIC + "DocumentNote/Item?noteID=" + noteId;
        JSONObject returnjson = com.kloudsync.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
        Log.e("syncGetNoteByNoteId", url + "  " + returnjson.toString());
        String attachmentUrl = "";
        try {
            if (returnjson.getInt("RetCode") == 0) {
                JSONObject data = returnjson.getJSONObject("RetData");
                if (data.has("AttachmentUrl")) {
                    attachmentUrl = data.getString("AttachmentUrl");
                }
            } else {

            }
        } catch (JSONException e) {
            Log.e("syncGetNoteByNoteId", "JSONException:" + e.getMessage());

            e.printStackTrace();
        }
        return attachmentUrl;
    }

    public Note syncGetNoteByNoteId(String noteId) {
        String url = URL_PUBLIC + "DocumentNote/Item?noteID=" + noteId;
        JSONObject returnjson = com.kloudsync.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
        Log.e("syncGetNoteByNoteId", url + "  " + returnjson.toString());

        Note note = new Note();
        try {

            if (returnjson.getInt("RetCode") == 0) {
                JSONObject lineitem = returnjson.getJSONObject("RetData");

                String attachmentUrl = lineitem.getString("AttachmentUrl");
                note.setLocalFileID(lineitem.getString("LocalFileID"));
                Log.e("syncGetNoteByNoteId", "set_local_file_id:" + lineitem.getString("LocalFileID"));
                note.setNoteID(lineitem.getInt("NoteID"));
//                note.setLinkID(lineitem.getInt("LinkID"));

                note.setDocumentItemID(lineitem.getInt("AttachmentFileID"));
                note.setFileName(lineitem.getString("Title"));
                note.setAttachmentUrl(attachmentUrl);
                note.setPageCount(lineitem.getInt("PageCount"));
                note.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                note.setAttachmentID(lineitem.getInt("AttachmentID"));
                String noteUrl = attachmentUrl.substring(0, attachmentUrl.lastIndexOf("<")) + 1 + attachmentUrl.substring(attachmentUrl.lastIndexOf("."));
                note.setUrl(noteUrl);

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

                note.setNewPath(attachmentUrl.substring(attachmentUrl.indexOf(".com") + 5, attachmentUrl.lastIndexOf("/")));

                List<DocumentPage> pages = new ArrayList<>();
                for (int j = 0; j < note.getPageCount(); ++j) {
                    String pageUrl = "";
                    DocumentPage page = new DocumentPage();
                    page.setPageNumber(j + 1);
                    page.setDocumentId(note.getDocumentItemID());
                    page.setLocalFileId(note.getLocalFileID());
                    if (TextUtils.isEmpty(preUrl)) {
                        page.setPageUrl(pageUrl);
                    } else {
                        page.setPageUrl(preUrl + (j + 1) + endUrl);
                    }
                    pages.add(page);
                }
                note.setDocumentPages(pages);
                Log.e("check_note", "local_file_id:" + note.getLocalFileID());
            } else {

            }
        } catch (JSONException e) {
            Log.e("syncGetNoteByNoteId", "JSONException:" + e.getMessage());

            e.printStackTrace();
        }
        return note;
    }


	public MeetingDocument _syncGetNoteByNoteId(String noteId) {
		String url = URL_PUBLIC + "DocumentNote/Item?noteID=" + noteId;
		JSONObject returnjson = com.kloudsync.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
		Log.e("syncGetNoteByNoteId", url + "  " + returnjson.toString());

		MeetingDocument noteDocument = new MeetingDocument();
		try {

			if (returnjson.getInt("RetCode") == 0) {
				JSONObject lineitem = returnjson.getJSONObject("RetData");

				String attachmentUrl = lineitem.getString("AttachmentUrl");
				noteDocument.setLocalFileID(lineitem.getString("LocalFileID"));
				Log.e("syncGetNoteByNoteId", "set_local_file_id:" + lineitem.getString("LocalFileID"));
				noteDocument.setNoteID(lineitem.getInt("NoteID"));
//                note.setLinkID(lineitem.getInt("LinkID"));

				noteDocument.setDocumentItemID(lineitem.getInt("AttachmentFileID"));
				noteDocument.setFileName(lineitem.getString("Title"));
				noteDocument.setAttachmentUrl(attachmentUrl);
				noteDocument.setPageCount(lineitem.getInt("PageCount"));
				noteDocument.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
				noteDocument.setAttachmentID(lineitem.getInt("AttachmentID"));
				String noteUrl = attachmentUrl.substring(0, attachmentUrl.lastIndexOf("<")) + 1 + attachmentUrl.substring(attachmentUrl.lastIndexOf("."));

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

				noteDocument.setNewPath(attachmentUrl.substring(attachmentUrl.indexOf(".com") + 5, attachmentUrl.lastIndexOf("/")));

				List<DocumentPage> pages = new ArrayList<>();
				for (int j = 0; j < noteDocument.getPageCount(); ++j) {
					String pageUrl = "";
					DocumentPage page = new DocumentPage();
					page.setPageNumber(j + 1);
					page.setDocumentId(noteDocument.getDocumentItemID());
					page.setLocalFileId(noteDocument.getLocalFileID());
					if (TextUtils.isEmpty(preUrl)) {
						page.setPageUrl(pageUrl);
					} else {
						page.setPageUrl(preUrl + (j + 1) + endUrl);
					}
					pages.add(page);
				}
				noteDocument.setDocumentPages(pages);
				Log.e("check_note", "local_file_id:" + noteDocument.getLocalFileID());
			} else {

			}
		} catch (JSONException e) {
			Log.e("syncGetNoteByNoteId", "JSONException:" + e.getMessage());

			e.printStackTrace();
		}
		return noteDocument;
	}

	/***
	 * 暂停会议
	 */
	public MeetingPauseOrResumBean requestMeetingPause() {
		String url = AppConfig.URL_MEETING_BASE + "meeting/pause";
		JSONObject responseJO = ConnectService.submitDataByJson(url, null);
		String responseJson = responseJO.toString();
		MeetingPauseOrResumBean meetingPauseOrResumBean = new Gson().fromJson(responseJson, MeetingPauseOrResumBean.class);
		return meetingPauseOrResumBean;
	}

	/**
	 * 继续会议
	 */
	public MeetingPauseOrResumBean requestMeetingResume() {
		String url = AppConfig.URL_MEETING_BASE + "meeting/resume";
		JSONObject responseJO = ConnectService.submitDataByJson(url, null);
		String responseJson = responseJO.toString();
		MeetingPauseOrResumBean meetingPauseOrResumBean = new Gson().fromJson(responseJson, MeetingPauseOrResumBean.class);
		return meetingPauseOrResumBean;
	}

	/**
	 * 设置会议暂停提示消息
	 *
	 * @param pauseMessage
	 */
	public MeetingPauseOrResumBean requestMeetingPauseMessage(String pauseMessage) {
		String url = AppConfig.URL_MEETING_BASE + "meeting/pause_message";
		pauseMessage = LoginGet.getBase64Password(pauseMessage.replaceAll("\t", ""));
		MeetingPauseOrResumBean meetingPauseOrResumBean = new MeetingPauseOrResumBean();
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("pauseMsg", pauseMessage);
			JSONObject responseJO = ConnectService.submitDataByJson(url, jsonObject);
			String responseJson = responseJO.toString();
			meetingPauseOrResumBean = new Gson().fromJson(responseJson, MeetingPauseOrResumBean.class);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return meetingPauseOrResumBean;
	}
}
