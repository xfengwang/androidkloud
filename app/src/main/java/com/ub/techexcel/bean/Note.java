package com.ub.techexcel.bean;

import com.kloudsync.techexcel.bean.DocumentPage;

import java.io.Serializable;
import java.util.List;

public class Note implements Serializable {

    private static final long serialVersionUID = 0x110;

    private int documentItemID;
    private int pageNumber;
    private String localFileID;
    private int noteID;
    private String title;
    private int attachmentFileID;
    private int attachmentID;
    private int fileID;
    private String fileName;
    private String attachmentUrl;
    private String sourceFileUrl;
    private String createdDate;
    private int status;
    private int linkID;
    private String localFilePath;
    private String url;
    private int PageCount;
    private List<DocumentPage> documentPages;
    private String newPath;
    private String lastModifiedDate;
	private int noteType;

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    public List<DocumentPage> getDocumentPages() {
        return documentPages;
    }

    public void setDocumentPages(List<DocumentPage> documentPages) {
        this.documentPages = documentPages;
    }

    public int getPageCount() {
        return PageCount;
    }

    public void setPageCount(int pageCount) {
        PageCount = pageCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public int getLinkID() {
        return linkID;
    }

    public void setLinkID(int linkID) {
        this.linkID = linkID;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getDocumentItemID() {
        return documentItemID;
    }

    public void setDocumentItemID(int documentItemID) {
        this.documentItemID = documentItemID;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getLocalFileID() {
        return localFileID;
    }

    public void setLocalFileID(String localFileID) {
        this.localFileID = localFileID;
    }

    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAttachmentFileID() {
        return attachmentFileID;
    }

    public void setAttachmentFileID(int attachmentFileID) {
        this.attachmentFileID = attachmentFileID;
    }

    public int getAttachmentID() {
        return attachmentID;
    }

    public void setAttachmentID(int attachmentID) {
        this.attachmentID = attachmentID;
    }

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getSourceFileUrl() {
        return sourceFileUrl;
    }

    public void setSourceFileUrl(String sourceFileUrl) {
        this.sourceFileUrl = sourceFileUrl;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

	public int getNoteType() {
		return noteType;
	}

	public void setNoteType(int noteType) {
		this.noteType = noteType;
	}

    @Override
    public String toString() {
        return "Note{" +
                "documentItemID=" + documentItemID +
                ", pageNumber=" + pageNumber +
                ", localFileID='" + localFileID + '\'' +
                ", noteID=" + noteID +
                ", title='" + title + '\'' +
                ", attachmentFileID=" + attachmentFileID +
                ", attachmentID=" + attachmentID +
                ", fileID=" + fileID +
                ", fileName='" + fileName + '\'' +
                ", attachmentUrl='" + attachmentUrl + '\'' +
                ", sourceFileUrl='" + sourceFileUrl + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", status=" + status +
                ", linkID=" + linkID +
                ", localFilePath='" + localFilePath + '\'' +
                ", url='" + url + '\'' +
                ", PageCount=" + PageCount +
                ", documentPages=" + documentPages +
                ", newPath='" + newPath + '\'' +
                '}';
    }
}
