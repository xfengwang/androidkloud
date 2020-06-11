package com.kloudsync.techexcel.bean;

public class ThumbnailDeleteBean {


	/**
	 * code : 0
	 * data : {"changeNumber":0}
	 * msg : string
	 */

	private int code;
	private DataBean data;
	private String msg;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public static class DataBean {
		/**
		 * changeNumber : 0
		 */

		private int changeNumber;

		public int getChangeNumber() {
			return changeNumber;
		}

		public void setChangeNumber(int changeNumber) {
			this.changeNumber = changeNumber;
		}

		@Override
		public String toString() {
			return "DataBean{" +
					"changeNumber=" + changeNumber +
					'}';
		}
	}

	@Override
	public String toString() {
		return "ThumbnailDeleteBean{" +
				"code=" + code +
				", data=" + data +
				", msg='" + msg + '\'' +
				'}';
	}
}
