package com.kloudsync.techexcel.bean;

import android.text.TextUtils;

import java.util.List;

public class DocThumbnailListBean {


	/**
	 * code : 0
	 * msg : success
	 * data : {"liveImageId":86150,"changeNumber":4,"imageList":[{"itemId":1962829,"logicalFileId":96666,"physicalFileId":96666,"title":"","pageNumber":1,"fileName":"fb18b049-6e46-4003-8a30-403713562d71.png","path":"P49/Attachment/D96666","attachmentUrl":"https://peertime.oss-cn-shanghai.aliyuncs.com/P49/Attachment/D96666/fb18b049-6e46-4003-8a30-403713562d71_<1>.jpg"}]}
	 */

	private int code;
	private String msg;
	private DataBean data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public static class DataBean {
		/**
		 * liveImageId : 86150
		 * changeNumber : 4
		 * imageList : [{"itemId":1962829,"logicalFileId":96666,"physicalFileId":96666,"title":"","pageNumber":1,"fileName":"fb18b049-6e46-4003-8a30-403713562d71.png","path":"P49/Attachment/D96666","attachmentUrl":"https://peertime.oss-cn-shanghai.aliyuncs.com/P49/Attachment/D96666/fb18b049-6e46-4003-8a30-403713562d71_<1>.jpg"}]
		 */

		private int liveImageId;
		private int changeNumber;
		private List<ImageListBean> imageList;

		public int getLiveImageId() {
			return liveImageId;
		}

		public void setLiveImageId(int liveImageId) {
			this.liveImageId = liveImageId;
		}

		public int getChangeNumber() {
			return changeNumber;
		}

		public void setChangeNumber(int changeNumber) {
			this.changeNumber = changeNumber;
		}

		public List<ImageListBean> getImageList() {
			return imageList;
		}

		public void setImageList(List<ImageListBean> imageList) {
			this.imageList = imageList;
		}

		public static class ImageListBean {
			/**
			 * itemId : 1962829
			 * logicalFileId : 96666
			 * physicalFileId : 96666
			 * title :
			 * pageNumber : 1
			 * fileName : fb18b049-6e46-4003-8a30-403713562d71.png
			 * path : P49/Attachment/D96666
			 * attachmentUrl : https://peertime.oss-cn-shanghai.aliyuncs.com/P49/Attachment/D96666/fb18b049-6e46-4003-8a30-403713562d71_<1>.jpg
			 */

			private int itemId;
			private int logicalFileId;
			private int physicalFileId;
			private String title;
			private int pageNumber;
			private String fileName;
			private String path;
			private String attachmentUrl;
			private boolean isCheck;
			private boolean isTemp;
			private int progress;
			private int uploadPosition;
			private boolean isLongClick;

			public int getItemId() {
				return itemId;
			}

			public void setItemId(int itemId) {
				this.itemId = itemId;
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

			public int getPageNumber() {
				return pageNumber;
			}

			public void setPageNumber(int pageNumber) {
				this.pageNumber = pageNumber;
			}

			public String getFileName() {
				return fileName;
			}

			public void setFileName(String fileName) {
				this.fileName = fileName;
			}

			public String getPath() {
				return path;
			}

			public void setPath(String path) {
				this.path = path;
			}

			public String getAttachmentUrl() {
				String preUrl = "";
				String endUrl = "";
				if (!TextUtils.isEmpty(attachmentUrl)) {
					int index = attachmentUrl.lastIndexOf("<");
					int index2 = attachmentUrl.lastIndexOf(">");
					if (index > 0) {
						preUrl = attachmentUrl.substring(0, index);
					}
					if (index2 > 0) {
						endUrl = attachmentUrl.substring(index2 + 1);
					}
				}
				String pageUrl = "";
				if (!TextUtils.isEmpty(preUrl)) {
					pageUrl = preUrl + 1 + "_4K" + endUrl;
				}
				return pageUrl;
			}

			public void setAttachmentUrl(String attachmentUrl) {
				this.attachmentUrl = attachmentUrl;
			}

			public boolean isCheck() {
				return isCheck;
			}

			public void setCheck(boolean check) {
				isCheck = check;
			}

			public boolean isTemp() {
				return isTemp;
			}

			public void setTemp(boolean temp) {
				isTemp = temp;
			}

			public int getProgress() {
				return progress;
			}

			public void setProgress(int progress) {
				this.progress = progress;
			}

			public int getUploadPosition() {
				return uploadPosition;
			}

			public void setUploadPosition(int uploadPosition) {
				this.uploadPosition = uploadPosition;
			}

			public boolean isLongClick() {
				return isLongClick;
			}

			public void setLongClick(boolean longClick) {
				isLongClick = longClick;
			}

			@Override
			public boolean equals(Object o) {
				if (this == o) return true;
				if (!(o instanceof ImageListBean)) return false;

				ImageListBean that = (ImageListBean) o;

				return this.itemId == that.itemId;
			}

			@Override
			public String toString() {
				return "ImageListBean{" +
						"itemId=" + itemId +
						", logicalFileId=" + logicalFileId +
						", physicalFileId=" + physicalFileId +
						", title='" + title + '\'' +
						", pageNumber=" + pageNumber +
						", fileName='" + fileName + '\'' +
						", path='" + path + '\'' +
						", attachmentUrl='" + attachmentUrl + '\'' +
						", isCheck=" + isCheck +
						", isTemp=" + isTemp +
						", progress=" + progress +
						", uploadPosition=" + uploadPosition +
						", isLongClick=" + isLongClick +
						'}';
			}
		}

		@Override
		public String toString() {
			return "DataBean{" +
					"liveImageId=" + liveImageId +
					", changeNumber=" + changeNumber +
					", imageList=" + imageList +
					'}';
		}
	}

	@Override
	public String toString() {
		return "DocThumbnailListBean{" +
				"code=" + code +
				", msg='" + msg + '\'' +
				", data=" + data +
				'}';
	}
}
