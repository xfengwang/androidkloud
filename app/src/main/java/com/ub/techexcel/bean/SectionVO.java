package com.ub.techexcel.bean;

public class SectionVO {

    private int id;
    private int type;
    private int sectionId;
    private int userId;
    private int startTime;
    private int endTime;
    private String fileName;
    private int status;
    private long createDate;
    private String fileUrl;
    private String sid;
    private boolean isClose;
    private boolean isPrepared;
    private boolean isPlaying;
    private boolean isPreparing;

    public boolean isPreparing() {
        return isPreparing;
    }

    public void setPreparing(boolean preparing) {
        isPreparing = preparing;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public void setPrepared(boolean prepared) {
        isPrepared = prepared;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isClose() {
        return isClose;
    }

    public void setClose(boolean close) {
        isClose = close;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    @Override
    public String toString() {
        return "SectionVO{" +
                "id=" + id +
                ", type=" + type +
                ", sectionId=" + sectionId +
                ", userId=" + userId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", fileName='" + fileName + '\'' +
                ", status=" + status +
                ", createDate=" + createDate +
                ", fileUrl='" + fileUrl + '\'' +
                ", sid='" + sid + '\'' +
                ", isClose=" + isClose +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SectionVO sectionVO = (SectionVO) o;

        return id == sectionVO.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
