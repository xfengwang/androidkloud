package com.kloudsync.techexcel.bean;

public class TvDevice {
    private String deviceName;
    private String deviceSessionId;
    private String loginTime;
    private boolean enableBind;
    private boolean enableSync;
    private boolean isSelect;
    private String UserID;
    private int deviceType;
    private boolean isOpenVoice;

    public boolean isEnableSync() {
        return enableSync;
    }

    public void setEnableSync(boolean enableSync) {
        this.enableSync = enableSync;
    }

    public boolean isOpenVoice() {
        return isOpenVoice;
    }

    public void setOpenVoice(boolean openVoice) {
        isOpenVoice = openVoice;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceSessionId() {
        return deviceSessionId;
    }

    public void setDeviceSessionId(String deviceSessionId) {
        this.deviceSessionId = deviceSessionId;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public boolean isEnableBind() {
        return enableBind;
    }

    public void setEnableBind(boolean enableBind) {
        this.enableBind = enableBind;
    }


    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }
}
