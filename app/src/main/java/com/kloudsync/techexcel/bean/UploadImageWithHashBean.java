package com.kloudsync.techexcel.bean;

public class UploadImageWithHashBean {


	/**
	 * code : 0
	 * msg : success
	 * data : {"succeed":true,"path":"","logicalFileId":0,"changeNumber":2,"liveImageVO":{"itemId":1963835,"logicalFileId":96974,"physicalFileId":96558,"title":"微信截图_20200602140215.png","pageNumber":5,"fileName":"73c4c434-2b8a-4558-8cde-0d5b6272b6c1.png","path":"P49/Attachment/D96558","attachmentUrl":"https://peertime.oss-cn-shanghai.aliyuncs.com/P49/Attachment/D96558/73c4c434-2b8a-4558-8cde-0d5b6272b6c1_<1>.jpg"}}
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
		 * succeed : true
		 * path :
		 * logicalFileId : 0
		 * changeNumber : 2
		 * liveImageVO : {"itemId":1963835,"logicalFileId":96974,"physicalFileId":96558,"title":"微信截图_20200602140215.png","pageNumber":5,"fileName":"73c4c434-2b8a-4558-8cde-0d5b6272b6c1.png","path":"P49/Attachment/D96558","attachmentUrl":"https://peertime.oss-cn-shanghai.aliyuncs.com/P49/Attachment/D96558/73c4c434-2b8a-4558-8cde-0d5b6272b6c1_<1>.jpg"}
		 */

		private boolean succeed;
		private String path;
		private int logicalFileId;
		private int changeNumber;
		private LiveImageVOBean liveImageVO;

		public boolean isSucceed() {
			return succeed;
		}

		public void setSucceed(boolean succeed) {
			this.succeed = succeed;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public int getLogicalFileId() {
			return logicalFileId;
		}

		public void setLogicalFileId(int logicalFileId) {
			this.logicalFileId = logicalFileId;
		}

		public int getChangeNumber() {
			return changeNumber;
		}

		public void setChangeNumber(int changeNumber) {
			this.changeNumber = changeNumber;
		}

		public LiveImageVOBean getLiveImageVO() {
			return liveImageVO;
		}

		public void setLiveImageVO(LiveImageVOBean liveImageVO) {
			this.liveImageVO = liveImageVO;
		}

		public static class LiveImageVOBean {
			/**
			 * itemId : 1963835
			 * logicalFileId : 96974
			 * physicalFileId : 96558
			 * title : 微信截图_20200602140215.png
			 * pageNumber : 5
			 * fileName : 73c4c434-2b8a-4558-8cde-0d5b6272b6c1.png
			 * path : P49/Attachment/D96558
			 * attachmentUrl : https://peertime.oss-cn-shanghai.aliyuncs.com/P49/Attachment/D96558/73c4c434-2b8a-4558-8cde-0d5b6272b6c1_<1>.jpg
			 */

			private int itemId;
			private int logicalFileId;
			private int physicalFileId;
			private String title;
			private int pageNumber;
			private String fileName;
			private String path;
			private String attachmentUrl;

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
				return attachmentUrl;
			}

			public void setAttachmentUrl(String attachmentUrl) {
				this.attachmentUrl = attachmentUrl;
			}

			@Override
			public String toString() {
				return "LiveImageVOBean{" +
						"itemId=" + itemId +
						", logicalFileId=" + logicalFileId +
						", physicalFileId=" + physicalFileId +
						", title='" + title + '\'' +
						", pageNumber=" + pageNumber +
						", fileName='" + fileName + '\'' +
						", path='" + path + '\'' +
						", attachmentUrl='" + attachmentUrl + '\'' +
						'}';
			}
		}

		@Override
		public String toString() {
			return "DataBean{" +
					"succeed=" + succeed +
					", path='" + path + '\'' +
					", logicalFileId=" + logicalFileId +
					", changeNumber=" + changeNumber +
					", liveImageVO=" + liveImageVO +
					'}';
		}
	}

	@Override
	public String toString() {
		return "UploadImageWithHashBean{" +
				"code=" + code +
				", msg='" + msg + '\'' +
				", data=" + data +
				'}';
	}
}
