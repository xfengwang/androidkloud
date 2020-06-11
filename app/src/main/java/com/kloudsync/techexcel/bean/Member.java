package com.kloudsync.techexcel.bean;

public class Member {
    private String userId;
    private String userName;
    private int rongCloudId;
    private String avatarUrl;
    private String sessionId;
    private int role;
    private int presenter;
    private int isOnline;
    private int agoraStatus;
    private int microphoneStatus;
    private int cameraStatus;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getRongCloudId() {
        return rongCloudId;
    }

    public void setRongCloudId(int rongCloudId) {
        this.rongCloudId = rongCloudId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getPresenter() {
        return presenter;
    }

    public void setPresenter(int presenter) {
        this.presenter = presenter;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public int getAgoraStatus() {
        return agoraStatus;
    }

    public void setAgoraStatus(int agoraStatus) {
        this.agoraStatus = agoraStatus;
    }

    public int getMicrophoneStatus() {
        return microphoneStatus;
    }

    public void setMicrophoneStatus(int microphoneStatus) {
        this.microphoneStatus = microphoneStatus;
    }

    public int getCameraStatus() {
        return cameraStatus;
    }

    public void setCameraStatus(int cameraStatus) {
        this.cameraStatus = cameraStatus;
    }
}
