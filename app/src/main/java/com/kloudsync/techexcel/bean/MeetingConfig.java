package com.kloudsync.techexcel.bean;

import android.text.TextUtils;
import android.util.Log;

import com.kloudsync.techexcel.config.AppConfig;
import com.ub.techexcel.bean.AgoraMember;
import com.ub.techexcel.bean.EventNetworkFineChanged;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by tonyan on 2019/11/19.
 */

public class MeetingConfig {
    private int type;
    private String meetingId;
    private int fileId;
    private int pageNumber;
    private String userToken;
    private int lessionId;
    private int role = MeetingRole.MEMBER;
    private String documentId;
    private MeetingDocument document;
    private boolean isMicroOn;
    private boolean isRecordOn;
    private boolean isCameraOn;
    private boolean isInRealMeeting;
    private boolean isMembersCameraToggle;
    private String presenterSessionId;
    private boolean docModifide;
    private String notifyUrl;
    private CopyOnWriteArrayList<MeetingMember> meetingMembers = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<MeetingMember> meetingAuditor = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<MeetingMember> meetingInvitors = new CopyOnWriteArrayList<>();
    private String meetingHostId = "";
    private String agoraChannelId;
    private String presenterId = "";
    private CopyOnWriteArrayList<AgoraMember> agoraMembers = new CopyOnWriteArrayList<>();
    private boolean fromMeeting;
    private int mode;
    private String currentMaxVideoUserId;
    private int shareScreenUid;
    private JSONObject currentLinkProperty;
    private int spaceId;
    private DocumentPage currentDocumentPage;
    private List<MeetingDocument> allDocuments;
    private MeetingMember me;
    private boolean netWorkFine = true;
    private String noteAttachmentUrl;
    private String localFileId;
    private long currentNoteId;
    private boolean isInViewDigitalNote = true;
    private DocumentPage restoreDocument;
    private boolean isPlayingSoundtrack;
    private int meetingStatus;
    private boolean isHost;
    private int systemType = 0;// 1 是教育
    private boolean isSyncing = false;
    private boolean IsMeetingPause;
    private int roleInLession;
    private boolean isThirdAudio = false;
    private AgoraMember hostAgora;
    private int handStatus;
    private int cameraDiplayMode = -1;
    private int viewType;
    private MeetingMember privateCoachMember;
    private boolean isCoaching;
    private boolean meIsCoachingMember;
	private int changeNumber; //文件列表版本号
    private int  settingValue=0;

    public int getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(int settingValue) {
        this.settingValue = settingValue;
    }

    public boolean meIsCoachingMember() {
        meIsCoachingMember = false;
        if(!isCoaching()){
            meIsCoachingMember = false;
        }else {
            if((privateCoachMember.getUserId() +"").equals(AppConfig.UserID)){
                meIsCoachingMember = true;
            }

            if(!meIsCoachingMember){
                if(presenterId.equals(AppConfig.UserID)){
                    meIsCoachingMember = true;
                }
            }

            if(!meIsCoachingMember){
                int index = privateCoachAudiences.indexOf(new AgoraMember(Integer.parseInt(AppConfig.UserID)));
                if(index >= 0){
                    meIsCoachingMember = true;
                }
            }
        }
        return meIsCoachingMember;
    }

    private CopyOnWriteArrayList<AgoraMember> privateCoachAudiences = new CopyOnWriteArrayList<>();


    public CopyOnWriteArrayList<AgoraMember> getPrivateCoachAudiences() {
        return privateCoachAudiences;
    }


    public boolean isCoaching() {
        return isCoaching;
    }

    public void setCoaching(boolean coaching) {
        isCoaching = coaching;
    }

    public void setPrivateCoachMember(int userId) {
        if (privateCoachMember == null) {
            int index = meetingMembers.indexOf(new MeetingMember(userId));
            if (index >= 0) {
                privateCoachMember = meetingMembers.get(index);
            }
        } else {
            if (privateCoachMember.getUserId() != userId) {
                int index = meetingMembers.indexOf(new MeetingMember(userId));
                if (index >= 0) {
                    privateCoachMember = meetingMembers.get(index);
                }
            }
        }
    }

    public void coachAudiences(String userId, boolean active, boolean isAdd) {
        Log.e("coachAudiences","userId:" + userId + ",isAdd:" + isAdd);
        if (isAdd) {
            int index = agoraMembers.indexOf(new AgoraMember(Integer.parseInt(userId)));
            Log.e("coachAudiences","index:" + index);
            if (index >= 0) {
                AgoraMember agoraMember = agoraMembers.get(index);
                agoraMember.setCoachingTobeAudience(false);
                agoraMember.setCoachingAudience(false);
                if (active) {
                    agoraMember.setCoachingAudience(true);
                } else {
                    agoraMember.setCoachingTobeAudience(true);
                }
                Log.e("check_audience_add","do_add:");
                int _index = privateCoachAudiences.indexOf(new AgoraMember(Integer.parseInt(userId)));
                if(_index >= 0){
                    privateCoachAudiences.set(_index,agoraMember);
                }else {
                    privateCoachAudiences.add(agoraMember);
                }

            }
        } else {
            privateCoachAudiences.remove(agoraMembers);
        }
    }

    public void coachAudiences(String userId, boolean active) {
        if(privateCoachAudiences.size() == 0){
            coachAudiences(userId,active,true);
            return;
        }
        int index = privateCoachAudiences.indexOf(new AgoraMember(Integer.parseInt(userId)));
        if (index >= 0) {
            AgoraMember agoraMember = privateCoachAudiences.get(index);
            agoraMember.setCoachingTobeAudience(false);
            agoraMember.setCoachingAudience(false);
            if (active) {
                agoraMember.setCoachingAudience(true);
            } else {
                agoraMember.setCoachingTobeAudience(true);
            }
        }else {
            coachAudiences(userId,active,true);
        }
    }


    public void clearCoachAudiences() {
        privateCoachAudiences.clear();
        privateCoachMember=new MeetingMember();
    }

    public void clearCoachMeAudiences(int userId) {
        if(privateCoachMember.getUserId()==userId){
            privateCoachMember=new MeetingMember();
        }
       if(privateCoachAudiences.size()>0){
           for (AgoraMember privateCoachAudience : privateCoachAudiences) {
               if(privateCoachAudience.getUserId()==userId){
                   privateCoachAudiences.remove(privateCoachAudience);
                   break;
               }
           }
       }
    }




    public MeetingMember getPrivateCoachMember() {
        return privateCoachMember;
    }


    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public int getCameraDiplayMode() {
        return cameraDiplayMode;
    }

    public void setCameraDiplayMode(int cameraDiplayMode) {
        this.cameraDiplayMode = cameraDiplayMode;
    }

    public int getHandStatus() {
        return handStatus;
    }

    public void setHandStatus(int handStatus) {
        this.handStatus = handStatus;
    }

    public AgoraMember getHostAgora() {
        return hostAgora;
    }

    public void setHostAgora(AgoraMember hostAgora) {
        this.hostAgora = hostAgora;
    }

    public boolean isThirdAudio() {
        return isThirdAudio;
    }

    public void setThirdAudio(boolean thirdAudio) {
        isThirdAudio = thirdAudio;
    }

    public int getRoleInLession() {
        return roleInLession;
    }

    public void setRoleInLession(int roleInLession) {
        this.roleInLession = roleInLession;
    }

    public boolean isMeetingPause() {
        return IsMeetingPause;
    }

    public void setMeetingPause(boolean meetingPause) {
        IsMeetingPause = meetingPause;
    }

    public int getSystemType() {
        return systemType;
    }

    public void setSystemType(int systemType) {
        this.systemType = systemType;
    }

    public boolean isSyncing() {
        return isSyncing;
    }

    public void setSyncing(boolean syncing) {
        isSyncing = syncing;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public int getMeetingStatus() {
        return meetingStatus;
    }

    public void setMeetingStatus(int meetingStatus) {
        this.meetingStatus = meetingStatus;
    }

    public boolean isPlayingSoundtrack() {
        return isPlayingSoundtrack;
    }

    public void setPlayingSoundtrack(boolean playingSoundtrack) {
        isPlayingSoundtrack = playingSoundtrack;
    }

    public boolean isInViewDigitalNote() {
        return isInViewDigitalNote;
    }

    public void setInViewDigitalNote(boolean inViewDigitalNote) {
        isInViewDigitalNote = inViewDigitalNote;
    }

    public String getNoteAttachmentUrl() {
        return noteAttachmentUrl;
    }

    public void setNoteAttachmentUrl(String noteAttachmentUrl) {
        this.noteAttachmentUrl = noteAttachmentUrl;
    }

    public String getLocalFileId() {
        return localFileId;
    }

    public void setLocalFileId(String localFileId) {
        this.localFileId = localFileId;
    }

    public long getCurrentNoteId() {
        return currentNoteId;
    }

    public void setCurrentNoteId(long currentNoteId) {
        this.currentNoteId = currentNoteId;
    }

    public void setNetWorkFine(boolean _netWorkFine) {
        if (this.netWorkFine != _netWorkFine) {
            this.netWorkFine = _netWorkFine;
            if (this.netWorkFine == true) {
                EventBus.getDefault().post(new EventNetworkFineChanged());
            }
        }

    }

    public MeetingMember getMe() {
        return me;
    }

    public void setMe(MeetingMember me) {
        this.me = me;
    }

    public List<MeetingDocument> getAllDocuments() {
        return allDocuments;
    }

    public void setAllDocuments(List<MeetingDocument> allDocuments) {
        this.allDocuments = allDocuments;
    }

    public String getCurrentMaxVideoUserId() {
        return currentMaxVideoUserId;
    }

    public void setCurrentMaxVideoUserId(String currentMaxVideoUserId) {
        this.currentMaxVideoUserId = currentMaxVideoUserId;
    }

    public DocumentPage getCurrentDocumentPage() {
        return currentDocumentPage;
    }

    public void setCurrentDocumentPage(DocumentPage currentDocumentPage) {
        this.currentDocumentPage = currentDocumentPage;
    }

    public int getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
    }

    public JSONObject getCurrentLinkProperty() {
        return currentLinkProperty;
    }

    public void setCurrentLinkProperty(JSONObject currentLinkProperty) {
        this.currentLinkProperty = currentLinkProperty;
    }

    public int getShareScreenUid() {
        return shareScreenUid;
    }

    public void setShareScreenUid(int shareScreenUid) {
        this.shareScreenUid = shareScreenUid;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isFromMeeting() {
        return fromMeeting;
    }

    public void setFromMeeting(boolean fromMeeting) {
        this.fromMeeting = fromMeeting;
    }

    public List<AgoraMember> getAgoraMembers() {
        return agoraMembers;
    }

    public void addAgoraMember(AgoraMember member) {
        int index = agoraMembers.indexOf(member);
        if (index >= 0) {
            Log.e("MeetingConfig", "refresh_agora_member_data:" + member);
            AgoraMember _member = agoraMembers.get(index);
            if (member.getSurfaceView() != null) {
                _member.setSurfaceView(member.getSurfaceView());
            }

            if (!TextUtils.isEmpty(member.getUserName())) {
                _member.setUserName(member.getUserName());
            }

            if (!TextUtils.isEmpty(member.getIconUrl())) {
                _member.setIconUrl(member.getIconUrl());
            }
            _member.setMuteVideo(member.isMuteVideo());
            _member.setMuteAudio(member.isMuteAudio());
        } else {
            Log.e("MeetingConfig", "add_agora_member:" + member);
            agoraMembers.add(member);
        }
    }

    public void deleteAgoraMember(AgoraMember member) {
        agoraMembers.remove(member);
    }

    public String getPresenterId() {
        return presenterId;
    }

    public void setPresenterId(String presenterId) {
        if (!this.presenterId.equals(presenterId)) {
            EventPresnterChanged presnterChanged = new EventPresnterChanged();
            presnterChanged.setPresenterId(presenterId);
            EventBus.getDefault().post(presnterChanged);
        }
        this.presenterId = presenterId;
    }

    public void justSetPresenterId(String presenterId) {
        this.presenterId = presenterId;
    }

    public String getAgoraChannelId() {
        return agoraChannelId;
    }

    public void setAgoraChannelId(String agoraChannelId) {
        this.agoraChannelId = agoraChannelId;
    }

    public String getMeetingHostId() {
        return meetingHostId;
    }

    public void setMeetingHostId(String meetingHostId) {
        this.meetingHostId = meetingHostId;
    }

    public List<MeetingMember> getMeetingAuditor() {
        return meetingAuditor;
    }

    public void setMeetingAuditor(List<MeetingMember> meetingAuditor) {
        this.meetingAuditor.clear();
        this.meetingAuditor.addAll(meetingAuditor);
    }

    public List<MeetingMember> getMeetingInvitors() {
        return meetingInvitors;
    }

    public void setMeetingInvitors(List<MeetingMember> meetingInvitors) {
        this.meetingInvitors.clear();
        this.meetingInvitors.addAll(meetingInvitors);
    }

    public List<MeetingMember> getMeetingMembers() {
        return meetingMembers;
    }

    public synchronized void setMeetingMembers(List<MeetingMember> meetingMembers) {

        try {
            this.meetingMembers.clear();
            this.meetingMembers.addAll(meetingMembers);
        } catch (Exception e) {
        }

    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public boolean isDocModifide() {
        return docModifide;
    }

    public void setDocModifide(boolean docModifide) {
        this.docModifide = docModifide;
    }

    public String getPresenterSessionId() {
        return presenterSessionId;
    }

    public void setPresenterSessionId(String presenterSessionId) {
        this.presenterSessionId = presenterSessionId;
    }

    public boolean isMembersCameraToggle() {
        return isMembersCameraToggle;
    }

    public void setMembersCameraToggle(boolean membersCameraToggle) {
        isMembersCameraToggle = membersCameraToggle;
    }

    public boolean isInRealMeeting() {
        return isInRealMeeting;
    }

    public void setInRealMeeting(boolean inRealMeeting) {
        isInRealMeeting = inRealMeeting;
    }

    public boolean isMicroOn() {
        return isMicroOn;
    }

    public void setMicroOn(boolean microOn) {
        isMicroOn = microOn;
    }

    public boolean isRecordOn() {
        return isRecordOn;
    }

    public void setRecordOn(boolean recordOn) {
        isRecordOn = recordOn;
    }

    public boolean isCameraOn() {
        return isCameraOn;
    }

    public void setCameraOn(boolean cameraOn) {
        isCameraOn = cameraOn;
    }

    public MeetingDocument getDocument() {
        return document;
    }

    public void setDocument(MeetingDocument document) {
        this.document = document;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public int getLessionId() {
        return lessionId;
    }

    public void setLessionId(int lessionId) {
        this.lessionId = lessionId;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

	public void setChangeNumber(int changeNumber) {
		this.changeNumber = changeNumber;
	}

	public int getChangeNumber() {
		return changeNumber;
	}

    public static class MeetingRole {
        public static final int DEFULT = -1;
        public static final int MEMBER = 1;
        public static final int HOST = 2;
        public static final int AUDIENCE = 3;
        public static final int BE_INVITED = 4;
    }

    public void reset() {
        this.meetingMembers.clear();
        this.meetingAuditor.clear();
    }

    @Override
    public String toString() {
        return "MeetingConfig{" +
                "type=" + type +
                ", meetingId='" + meetingId + '\'' +
                ", fileId=" + fileId +
                ", pageNumber=" + pageNumber +
                ", userToken='" + userToken + '\'' +
                ", lessionId=" + lessionId +
                ", role=" + role +
                ", documentId='" + documentId + '\'' +
                ", document=" + document +
                ", isMicroOn=" + isMicroOn +
                ", isRecordOn=" + isRecordOn +
                ", isCameraOn=" + isCameraOn +
                ", isInRealMeeting=" + isInRealMeeting +
                ", isMembersCameraToggle=" + isMembersCameraToggle +
                ", presenterSessionId='" + presenterSessionId + '\'' +
                ", docModifide=" + docModifide +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", meetingMembers=" + meetingMembers +
                ", meetingAuditor=" + meetingAuditor +
                ", meetingInvitors=" + meetingInvitors +
                ", meetingHostId='" + meetingHostId + '\'' +
                ", agoraChannelId='" + agoraChannelId + '\'' +
                ", presenterId='" + presenterId + '\'' +
                ", agoraMembers=" + agoraMembers +
                ", fromMeeting=" + fromMeeting +
                ", mode=" + mode +
                ", currentMaxVideoUserId='" + currentMaxVideoUserId + '\'' +
                ", shareScreenUid=" + shareScreenUid +
                ", currentLinkProperty=" + currentLinkProperty +
                ", spaceId=" + spaceId +
                ", currentDocumentPage=" + currentDocumentPage +
                ", allDocuments=" + allDocuments +
                ", me=" + me +
                ", netWorkFine=" + netWorkFine +
                ", noteAttachmentUrl='" + noteAttachmentUrl + '\'' +
                ", localFileId='" + localFileId + '\'' +
                ", currentNoteId=" + currentNoteId +
                ", isInViewDigitalNote=" + isInViewDigitalNote +
		        ", restoreDocument=" + restoreDocument +
		        ", isPlayingSoundtrack=" + isPlayingSoundtrack +
		        ", meetingStatus=" + meetingStatus +
		        ", isHost=" + isHost +
		        ", systemType=" + systemType +
		        ", isSyncing=" + isSyncing +
		        ", IsMeetingPause=" + IsMeetingPause +
		        ", roleInLession=" + roleInLession +
		        ", isThirdAudio=" + isThirdAudio +
		        ", hostAgora=" + hostAgora +
		        ", handStatus=" + handStatus +
		        ", cameraDiplayMode=" + cameraDiplayMode +
		        ", viewType=" + viewType +
		        ", privateCoachMember=" + privateCoachMember +
		        ", isCoaching=" + isCoaching +
		        ", meIsCoachingMember=" + meIsCoachingMember +
		        ", changeNumber=" + changeNumber +
		        ", privateCoachAudiences=" + privateCoachAudiences +
                '}';
    }
}
