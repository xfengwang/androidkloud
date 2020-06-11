package com.kloudsync.techexcel.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tonyan on 2019/10/10.
 */

public class MeetingDocument implements Serializable {
    private String LessonID;
    private int IsAttendeeUpload;
    private int CourseID;
    private int LectureID;
    private int SyncCount;
    private int NewStatus;
    //    private String NewPath;
    //    private int ItemID;
//    private String Title;
    private String Description;
    private int AttachmentFileID;//逻辑文件ID
    //    private int AttachmentID;
    private int FileID;//物理文件ID
    //    private String FileName;
    private String SourceFileName;
    //    private String AttachmentUrl;
    private String SourceFileUrl;
    //    private String CreatedDate;
//    private int Status;
//    private int PageCount;
    private String BlankPageNumber;
    //    private int FileType;
    private String VideoSize;
    private String VideoDuration;
    private boolean IsTemporary;
    private String QueryToken;
    private List<DocumentPage> documentPages;
    private boolean isSelect;
    private boolean isTemp;
    private int progress;
    private String tempDocPrompt;
    private String LocalFileID;
    private long NoteID;
    private int documentItemID;


    /**
     * itemId : 1959931
     * attachmentId : 80505
     * logicalFileId : 90113
     * physicalFileId : 89867
     * title : 暂停会议V2.docx
     * fileName : 65e90699-6f1c-40f3-8004-10fe9c84a614.docx
     * status : 0
     * path : P49/Attachment/D89867
     * attachmentUrl : https://peertime.oss-cn-shanghai.aliyuncs.com/P49/Attachment/D89867/65e90699-6f1c-40f3-8004-10fe9c84a614_<2>.jpg
     * createdDate : 1590402024960
     * pageCount : 2
     * fileType : 3
     * fileSize : 65 B
     */

    private int itemId;
    private int attachmentId;
    private int logicalFileId;//逻辑文件ID
    private int physicalFileId;//物理文件ID
    private String title;
    private String fileName;
    private int status;
    private String path;
    private String attachmentUrl;
    private String createdDate;
    private int pageCount;
    private int fileType;
    private String fileSize;


    public int getDocumentItemID() {
        return documentItemID;
    }

    public void setDocumentItemID(int documentItemID) {
        this.documentItemID = documentItemID;
    }

    public String getLocalFileID() {
        return LocalFileID;
    }

    public void setLocalFileID(String localFileID) {
        LocalFileID = localFileID;
    }

    public long getNoteID() {
        return NoteID;
    }

    public void setNoteID(long noteID) {
        NoteID = noteID;
    }

    public String getTempDocPrompt() {
        return tempDocPrompt;
    }

    public void setTempDocPrompt(String tempDocPrompt) {
        this.tempDocPrompt = tempDocPrompt;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean temp) {
        isTemp = temp;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public MeetingDocument() {

    }

    public MeetingDocument(int itemId) {
        this.itemId = itemId;
    }

    public String getLessonID() {
        return LessonID;
    }

    public void setLessonID(String lessonID) {
        LessonID = lessonID;
    }

    public int getIsAttendeeUpload() {
        return IsAttendeeUpload;
    }

    public void setIsAttendeeUpload(int isAttendeeUpload) {
        IsAttendeeUpload = isAttendeeUpload;
    }

    public int getCourseID() {
        return CourseID;
    }

    public void setCourseID(int courseID) {
        CourseID = courseID;
    }

    public int getLectureID() {
        return LectureID;
    }

    public void setLectureID(int lectureID) {
        LectureID = lectureID;
    }

    public int getSyncCount() {
        return SyncCount;
    }

    public void setSyncCount(int syncCount) {
        SyncCount = syncCount;
    }

    public int getNewStatus() {
        return NewStatus;
    }

    public void setNewStatus(int newStatus) {
        NewStatus = newStatus;
    }

      /* public String getNewPath() {
        return NewPath;
    }

    public void setNewPath(String newPath) {
        NewPath = newPath;
    }

    public int getItemID() {
        return ItemID;
    }

    public void setItemID(int itemID) {
        ItemID = itemID;
    }*/

    /*public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }*/

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getAttachmentFileID() {
        return AttachmentFileID;
    }

    public void setAttachmentFileID(int attachmentFileID) {
        AttachmentFileID = attachmentFileID;
    }

    /*public int getAttachmentID() {
        return AttachmentID;
    }

    public void setAttachmentID(int attachmentID) {
        AttachmentID = attachmentID;
    }*/

    public int getFileID() {
        return FileID;
    }

    public void setFileID(int fileID) {
        FileID = fileID;
    }

   /* public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }*/

    public String getSourceFileName() {
        return SourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        SourceFileName = sourceFileName;
    }

    /*public String getAttachmentUrl() {
        return AttachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        AttachmentUrl = attachmentUrl;
    }*/

    public String getSourceFileUrl() {
        return SourceFileUrl;
    }

    public void setSourceFileUrl(String sourceFileUrl) {
        SourceFileUrl = sourceFileUrl;
    }

    /*public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getPageCount() {
        return PageCount;
    }

    public void setPageCount(int pageCount) {
        PageCount = pageCount;
    }*/

    public String getBlankPageNumber() {
        return BlankPageNumber;
    }

    public void setBlankPageNumber(String blankPageNumber) {
        BlankPageNumber = blankPageNumber;
    }

    /*public int getFileType() {
        return FileType;
    }

    public void setFileType(int fileType) {
        FileType = fileType;
    }*/

    public String getVideoSize() {
        return VideoSize;
    }

    public void setVideoSize(String videoSize) {
        VideoSize = videoSize;
    }

    public String getVideoDuration() {
        return VideoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        VideoDuration = videoDuration;
    }

    public boolean isTemporary() {
        return IsTemporary;
    }

    public void setTemporary(boolean temporary) {
        IsTemporary = temporary;
    }

    public String getQueryToken() {
        return QueryToken;
    }

    public void setQueryToken(String queryToken) {
        QueryToken = queryToken;
    }

    public List<DocumentPage> getDocumentPages() {
        return documentPages;
    }

    public void setDocumentPages(List<DocumentPage> documentPages) {
        this.documentPages = documentPages;
    }

    @Override
    public String toString() {
        return "MeetingDocument{" +
                "LessonID='" + LessonID + '\'' +
                ", IsAttendeeUpload=" + IsAttendeeUpload +
                ", CourseID=" + CourseID +
                ", LectureID=" + LectureID +
                ", SyncCount=" + SyncCount +
                ", NewStatus=" + NewStatus +
//                ", NewPath='" + NewPath + '\'' +
                ", Description='" + Description + '\'' +
                ", AttachmentFileID=" + AttachmentFileID +
//                ", AttachmentID=" + AttachmentID +
                ", FileID=" + FileID +
                ", SourceFileName='" + SourceFileName + '\'' +
                ", SourceFileUrl='" + SourceFileUrl + '\'' +
                ", BlankPageNumber='" + BlankPageNumber + '\'' +
                ", VideoSize='" + VideoSize + '\'' +
                ", VideoDuration='" + VideoDuration + '\'' +
                ", IsTemporary=" + IsTemporary +
                ", QueryToken='" + QueryToken + '\'' +
                ", documentPages=" + documentPages +
                ", isSelect=" + isSelect +
                ", isTemp=" + isTemp +
                ", progress=" + progress +
                ", tempDocPrompt='" + tempDocPrompt + '\'' +
                ", LocalFileID='" + LocalFileID + '\'' +
                ", NoteID=" + NoteID +
                ", documentItemID=" + documentItemID +
                ", itemId=" + itemId +
                ", attachmentId=" + attachmentId +
                ", logicalFileId=" + logicalFileId +
                ", physicalFileId=" + physicalFileId +
                ", title='" + title + '\'' +
                ", fileName='" + fileName + '\'' +
                ", status=" + status +
                ", path='" + path + '\'' +
                ", attachmentUrl='" + attachmentUrl + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", pageCount=" + pageCount +
                ", fileType=" + fileType +
                ", fileSize='" + fileSize + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeetingDocument document = (MeetingDocument) o;

        return itemId == document.itemId;
    }

    @Override
    public int hashCode() {
        return itemId;
    }

    public int getItemID() {
        return itemId;
    }

    public void setItemID(int itemId) {
        this.itemId = itemId;
    }

    public int getAttachmentID() {
        return attachmentId;
    }

    public void setAttachmentID(int attachmentId) {
        this.attachmentId = attachmentId;
    }

    public int getLogicalFileId() {
        return logicalFileId;
    }

    public void setLogicalFileId(int logicalFileId) {
        this.logicalFileId = logicalFileId;
    }

    public int getPhysicalFileId() {
        return physicalFileId;
    }

    public void setPhysicalFileId(int physicalFileId) {
        this.physicalFileId = physicalFileId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getNewPath() {
        return path;
    }

    public void setNewPath(String path) {
        this.path = path;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
}
