package com.kloudsync.techexcel.bean;

public class AccountSettingBean {

    private  String SchoolName;

    private String webAddress;

    private String verifyEmailAddress;
	private int systemType;

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public String getVerifyEmailAddress() {
        return verifyEmailAddress;
    }

    public void setVerifyEmailAddress(String verifyEmailAddress) {
        this.verifyEmailAddress = verifyEmailAddress;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

	public int getSystemType() {
		return systemType;
	}

	public void setSystemType(int systemType) {
		this.systemType = systemType;
	}
}
