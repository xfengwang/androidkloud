package com.kloudsync.techexcel.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.BottomDocThumbnailAdapter;
import com.kloudsync.techexcel.bean.DocThumbnailListBean;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.ThumbnailDeleteBean;
import com.kloudsync.techexcel.bean.UploadImageWithHashBean;
import com.kloudsync.techexcel.help.PopBottomFile;
import com.kloudsync.techexcel.tool.DocumentUploadTool;
import com.kloudsync.techexcel.tool.Md5Tool;
import com.kloudsync.techexcel.tool.ToastUtils;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 底部文档缩略图列表弹窗
 */
public class BottomDocThumbnailDialog implements View.OnClickListener, DialogInterface.OnDismissListener, BottomDocThumbnailAdapter.DocThumbnailDeleteListener {

	private final String TAG = BottomDocThumbnailDialog.class.getSimpleName();
	private Context mContext;
	private Dialog mDialog;
	private Button mBtnThumbnailAdd;
	private RecyclerView mRvThumbnail;
	private ImageView mIvThumbnailClose;
	private PopupWindow mPpwAddDoc;
	private View mAddPpwView;
	private LinearLayout mLlyAddDoc;
	private View mThumbnailView;
	private List<DocThumbnailListBean.DataBean.ImageListBean> mImageList = new ArrayList<>();
	private BottomDocThumbnailAdapter mBottomDocThumbnailAdapter;
	private PopBottomFile.BottomFileOperationsListener mBottomFileOperationsListener;
	private BottomDocThumbnailAdapter.DocThumbnailListener mDocThumbnailListener;
	private DocThumbnailListBean mDocThumbnailListBean;
	private Gson mGson;
	private MeetingConfig mMeetingConfig;
	private List<String> mImgPaths = new ArrayList<>();//图片地址临时存储集合
	private int mUploadPosition;//上传时的设置给对象的标识,方便更新进度
	private List<DocThumbnailListBean.DataBean.ImageListBean> mImgPositionBeanList = new ArrayList<>();//需上传图片对象临时存储集合

	public BottomDocThumbnailDialog(Context context, PopBottomFile.BottomFileOperationsListener listener, BottomDocThumbnailAdapter.DocThumbnailListener docThumbnailListener) {
		mContext = context;
		mBottomFileOperationsListener = listener;
		mDocThumbnailListener = docThumbnailListener;
		initView();
	}

	private void initView() {
		mGson = new Gson();
		mThumbnailView = LayoutInflater.from(mContext).inflate(R.layout.dialog_bottom_doc_thumbnail, null);
		mThumbnailView.getBackground().setAlpha(204);
		mBtnThumbnailAdd = mThumbnailView.findViewById(R.id.btn_dialog_thumbnail_add);
		mRvThumbnail = mThumbnailView.findViewById(R.id.rv_dialog_thumbnail);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
		linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		mRvThumbnail.setLayoutManager(linearLayoutManager);
		mIvThumbnailClose = mThumbnailView.findViewById(R.id.iv_dialog_thumbnail_close);
		mBtnThumbnailAdd.setOnClickListener(this);
		mIvThumbnailClose.setOnClickListener(this);
		mDialog = new Dialog(mContext, R.style.my_dialog);
		mDialog.setContentView(mThumbnailView);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.setOnDismissListener(this);
		mDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mDialog.getWindow().setGravity(Gravity.BOTTOM);

		mBottomDocThumbnailAdapter = new BottomDocThumbnailAdapter(mContext, mImageList);
		mBottomDocThumbnailAdapter.setDocThumbnailListener(mDocThumbnailListener);
		mBottomDocThumbnailAdapter.setDocThumbnailDeleteListener(this);
		mRvThumbnail.setAdapter(mBottomDocThumbnailAdapter);
	}

	public boolean isShow() {
		return mDialog == null ? false : mDialog.isShowing();
	}

	public void show(MeetingConfig meetingConfig) {
		mMeetingConfig = meetingConfig;
		if (meetingConfig.getDocument() != null) {
			int attachmentID = meetingConfig.getDocument().getAttachmentID();
			requestDocImageList(attachmentID);

			String name = meetingConfig.getDocument().getTitle();
			if (name.endsWith(".ppt") || name.endsWith(".PPT") || name.endsWith(".pptx") || name.endsWith(".PPTX") || name.endsWith(".pdf") ||
					name.endsWith(".pdf") || name.endsWith(".doc") || name.endsWith(".DOC") || name.endsWith(".docx") || name.endsWith(".DOCX")
					|| name.endsWith(".xls") || name.endsWith(".XLS")) {
				mBtnThumbnailAdd.setVisibility(View.GONE);
			} else {
				mBtnThumbnailAdd.setVisibility(View.VISIBLE);
			}
		}

		if (mDialog != null && !isShow()) {
			mDialog.show();
		}
	}

	private void dismiss() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	private void requestDocImageList(int attachmentID) {
		if (mImgPaths.size() > 0) return;
		Observable.just(attachmentID)
				.observeOn(Schedulers.io())
				.map(integer -> ServiceInterfaceTools.getinstance().getDocImageList(attachmentID))
				.observeOn(AndroidSchedulers.mainThread())
				.doOnNext(jsonObject -> {
					String json = jsonObject.toString();
					if (!TextUtils.isEmpty(json)) {
						DocThumbnailListBean docThumbnailListBean = mGson.fromJson(json, DocThumbnailListBean.class);
						mDocThumbnailListBean = docThumbnailListBean;
						if (mDocThumbnailListBean.getCode() == 0 && mDocThumbnailListBean.getData() != null && mDocThumbnailListBean.getData().getImageList() != null) {
							mBottomDocThumbnailAdapter.setListData(docThumbnailListBean.getData().getImageList());
							mBottomDocThumbnailAdapter.notifyDataSetChanged();
						}
					}
				})
				.subscribe();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_dialog_thumbnail_add:
				showAddDocPpw();
				break;
			case R.id.iv_dialog_thumbnail_close:
				dismiss();
				break;
			case R.id.take_photo:
			case R.id.file_library:
				addDocPpwDismiss();
				if (mBottomFileOperationsListener != null) {
					mBottomFileOperationsListener.addFromCameraDocImage();
				}
				break;
			case R.id.save_file:
				addDocPpwDismiss();
				if (mBottomFileOperationsListener != null) {
					mBottomFileOperationsListener.addFromFavorite();
				}
				break;
			case R.id.team_document:
				addDocPpwDismiss();
				if (mBottomFileOperationsListener != null) {
					mBottomFileOperationsListener.addFromTeam();
				}
				break;
		}
	}

	@Override
	public void onDismiss(DialogInterface dialogInterface) {
		for (DocThumbnailListBean.DataBean.ImageListBean bean : mImageList) {
			bean.setLongClick(false);
		}
		mBottomDocThumbnailAdapter.notifyDataSetChanged();
	}

	private void showAddDocPpw() {
		if (mPpwAddDoc == null) {
			mAddPpwView = LayoutInflater.from(mContext).inflate(R.layout.ppw_add_doc, null);
			mLlyAddDoc = mAddPpwView.findViewById(R.id.lly_ppw_add_doc);
			mLlyAddDoc.getBackground().setAlpha(204);
			LinearLayout take_photo = mAddPpwView.findViewById(R.id.take_photo);
			LinearLayout file_library = mAddPpwView.findViewById(R.id.file_library);
			LinearLayout fileststem_library = mAddPpwView.findViewById(R.id.fileststem_library);
			LinearLayout favorite_file = mAddPpwView.findViewById(R.id.save_file);
			LinearLayout team_file = mAddPpwView.findViewById(R.id.team_document);
			LinearLayout blank_file = mAddPpwView.findViewById(R.id.blank_file);
			mAddPpwView.findViewById(R.id.tv_ppw_add_doc_divider4).setVisibility(View.GONE);
			mAddPpwView.findViewById(R.id.tv_ppw_add_doc_divider5).setVisibility(View.GONE);
			fileststem_library.setVisibility(View.GONE);
			blank_file.setVisibility(View.GONE);
			take_photo.setOnClickListener(this);
			file_library.setOnClickListener(this);
			favorite_file.setOnClickListener(this);
			team_file.setOnClickListener(this);
			mPpwAddDoc = new PopupWindow(mAddPpwView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			mPpwAddDoc.setBackgroundDrawable(new ColorDrawable());
			mPpwAddDoc.setOutsideTouchable(true);
			mPpwAddDoc.setFocusable(true);
			mPpwAddDoc.setClippingEnabled(false);
		}
		int x = mContext.getResources().getDimensionPixelOffset(R.dimen.dp_10);
		int y = mContext.getResources().getDimensionPixelOffset(R.dimen.dp_10);
		mLlyAddDoc.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		int height = mLlyAddDoc.getMeasuredHeight();
		Log.i(PopBottomFile.class.getSimpleName().toString(), "width = " + height);
		mPpwAddDoc.showAtLocation(mThumbnailView, Gravity.NO_GRAVITY, x, -height - y);
	}

	private void addDocPpwDismiss() {
		if (mPpwAddDoc != null && mPpwAddDoc.isShowing()) {
			mPpwAddDoc.dismiss();
		}
	}


	public void uploadDocImageWithHash(List<Uri> uris, List<String> paths) {
		if (mDocThumbnailListBean == null) {
			ToastUtils.show(mContext, R.string.uploadfailure);
			return;
		}
		if (mDocThumbnailListBean.getData() == null) {
			ToastUtils.show(mContext, R.string.uploadfailure);
			return;
		}
		DocThumbnailListBean.DataBean docThumbnailListBeanData = mDocThumbnailListBean.getData();
		final int[] pageNumber = {1};//图片上传到图片组的位置
		int index = 1;//图片添加到列表中的位置
		for (int i = 0; i < mImageList.size(); i++) {
			if (mImageList.get(i).isCheck()) {
				pageNumber[0] = i + 2;
				index = i + 2;
			}
		}

		mImgPaths.addAll(paths);
		for (int i = 0; i < paths.size(); i++) {
			DocThumbnailListBean.DataBean.ImageListBean bean = new DocThumbnailListBean.DataBean.ImageListBean();
			mUploadPosition++;
			bean.setUploadPosition(mUploadPosition);
			bean.setProgress(0);
			bean.setTemp(true);
			mImageList.add(index - 1, bean);
			index++;
			mImgPositionBeanList.add(bean);
		}
		mBottomDocThumbnailAdapter.notifyDataSetChanged();
		Log.i(TAG, mImgPaths.toString());
		if (!mIsUploading) {
			uploadDocImage(mImgPaths.get(0), docThumbnailListBeanData, pageNumber, mImgPositionBeanList.get(0));
		}
	}

	private boolean mIsUploading;
	private void uploadDocImage(String path, DocThumbnailListBean.DataBean docThumbnailListBeanData, int[] pageNumber, DocThumbnailListBean.DataBean.ImageListBean imageListBean) {
		mIsUploading = true;
		Log.i(TAG, "uploadDocImage : path = " + path);
		Observable.just(path)
				.observeOn(Schedulers.io())
				.map(new Function<String, JSONObject>() {
					@Override
					public JSONObject apply(String s) throws Exception {
						File file = new File(s);
						JSONObject jsonObject;
						if (file != null && file.exists()) {
							String hashKey = Md5Tool.getMd5ByFile(file);
							String title = file.getName();
							jsonObject = ServiceInterfaceTools.getinstance().uploadImageWithHash(docThumbnailListBeanData.getChangeNumber(), hashKey, docThumbnailListBeanData.getLiveImageId(), pageNumber[0], title);
						} else {
							jsonObject = new JSONObject();
						}
						Log.i(TAG, jsonObject.toString() + "path = " + s);
						return jsonObject;
					}
				})
				.observeOn(AndroidSchedulers.mainThread())
				.doOnNext(new Consumer<JSONObject>() {
					@Override
					public void accept(JSONObject jsonObject) throws Exception {
						UploadImageWithHashBean uploadImageWithHashBean = mGson.fromJson(jsonObject.toString(), UploadImageWithHashBean.class);
						if (!TextUtil.isEmpty(jsonObject.toString())) {
							if (uploadImageWithHashBean.getData() != null) {
								File file = new File(path);
								if (uploadImageWithHashBean.getData().isSucceed()) {
//									DocThumbnailListBean.DataBean.ImageListBean bean = new DocThumbnailListBean.DataBean.ImageListBean();
									docThumbnailListBeanData.setChangeNumber(uploadImageWithHashBean.getData().getChangeNumber());
									imageListBean.setTemp(false);
									UploadImageWithHashBean.DataBean.LiveImageVOBean liveImageVO = uploadImageWithHashBean.getData().getLiveImageVO();
									imageListBean.setAttachmentUrl(liveImageVO.getAttachmentUrl());
									imageListBean.setFileName(liveImageVO.getFileName());
									imageListBean.setItemId(liveImageVO.getItemId());
									imageListBean.setLogicalFileId(liveImageVO.getLogicalFileId());
									imageListBean.setPageNumber(liveImageVO.getPageNumber());
									imageListBean.setPath(liveImageVO.getPath());
									imageListBean.setPhysicalFileId(liveImageVO.getPhysicalFileId());
									imageListBean.setTitle(liveImageVO.getTitle());
//									mImageList.add(pageNumber[0] - 1, bean);
									mBottomDocThumbnailAdapter.notifyDataSetChanged();
									for (int i = 0; i < mImgPaths.size(); i++) {
										if (mImgPaths.get(i).equals(path)) {
											mImgPaths.remove(i);
										}
									}
									mImgPositionBeanList.remove(imageListBean);
									if (mImgPaths.size() > 0) {
										uploadDocImage(mImgPaths.get(0), docThumbnailListBeanData, pageNumber, mImgPositionBeanList.get(0));
									} else {
										mIsUploading = false;
									}
									Log.i(TAG, "uploadDocImage : mImgPaths.size = " + mImgPaths.size() + "_" + uploadImageWithHashBean.getData().isSucceed());
								} else {
									int fileId = uploadImageWithHashBean.getData().getLogicalFileId();
									String servicePath = uploadImageWithHashBean.getData().getPath();
//									DocThumbnailListBean.DataBean.ImageListBean bean = new DocThumbnailListBean.DataBean.ImageListBean();
//									bean.setUploadPosition(mUploadPosition);
//									bean.setProgress(0);
//									bean.setTemp(true);
//									mImageList.add(pageNumber[0] - 1, bean);
//									mBottomDocThumbnailAdapter.addDocImage(pageNumber[0] - 1, bean);
									Log.i(TAG, "uploadDocImage : mImgPaths.size = " + mImgPaths.size() + "pageNumber = " + (pageNumber[0] - 1));
									DocumentUploadTool uploadTool = new DocumentUploadTool(mContext);
									uploadTool.setUploadDetailLinstener(new DocumentUploadTool.DocUploadDetailLinstener() {
										@Override
										public void uploadStart() {
										}

										@Override
										public void uploadFile(int progress) {
											((Activity) mContext).runOnUiThread(new Runnable() {
												@Override
												public void run() {
													mBottomDocThumbnailAdapter.refreshDocImage(imageListBean.getUploadPosition(), progress);

												}
											});
										}

										@Override
										public void convertFile(int progress) {
											((Activity) mContext).runOnUiThread(new Runnable() {
												@Override
												public void run() {
													mBottomDocThumbnailAdapter.refreshDocImage(imageListBean.getUploadPosition(), progress);

												}
											});
										}

										@Override
										public void uploadFinished(Object result) {
											UploadImageWithHashBean hashBean = mGson.fromJson(result.toString(), UploadImageWithHashBean.class);
											docThumbnailListBeanData.setChangeNumber(hashBean.getData().getChangeNumber());
											UploadImageWithHashBean.DataBean.LiveImageVOBean liveImageVO = hashBean.getData().getLiveImageVO();
											imageListBean.setTemp(false);
											imageListBean.setAttachmentUrl(liveImageVO.getAttachmentUrl());
											imageListBean.setFileName(liveImageVO.getFileName());
											imageListBean.setItemId(liveImageVO.getItemId());
											imageListBean.setLogicalFileId(liveImageVO.getLogicalFileId());
											imageListBean.setPageNumber(liveImageVO.getPageNumber());
											imageListBean.setPath(liveImageVO.getPath());
											imageListBean.setPhysicalFileId(liveImageVO.getPhysicalFileId());
											imageListBean.setTitle(liveImageVO.getTitle());
											ToastUtils.show(mContext, R.string.uploadsuccess);
											mBottomDocThumbnailAdapter.notifyDataSetChanged();
											for (int i = 0; i < mImgPaths.size(); i++) {
												if (mImgPaths.get(i).equals(path)) {
													mImgPaths.remove(i);
												}
											}
											mImgPositionBeanList.remove(imageListBean);
											if (mImgPaths.size() > 0) {
												uploadDocImage(mImgPaths.get(0), docThumbnailListBeanData, pageNumber, mImgPositionBeanList.get(0));
											} else {
												mIsUploading = false;
											}
											Log.i(TAG, "uploadDocImage : mImgPaths.size = " + mImgPaths.size() + "_" + uploadImageWithHashBean.getData().isSucceed());
										}

										@Override
										public void uploadError(String message) {
											((Activity) mContext).runOnUiThread(new Runnable() {
												@Override
												public void run() {
													ToastUtils.show(mContext, message);
//													mBottomDocThumbnailAdapter.removeDocImage(imageListBean.getUploadPosition());
													for (int i = 0; i < mImgPaths.size(); i++) {
														if (mImgPaths.get(i).equals(path)) {
															mImgPaths.remove(i);
														}
													}
													mImgPositionBeanList.remove(imageListBean);
													if (mImgPaths.size() > 0) {
														uploadDocImage(mImgPaths.get(0), docThumbnailListBeanData, pageNumber, mImgPositionBeanList.get(0));
													} else {
														mIsUploading = false;
													}
												}
											});

										}
									});
									uploadTool.setUploadDocImageRequestBody(docThumbnailListBeanData.getChangeNumber(), docThumbnailListBeanData.getLiveImageId(), pageNumber[0]);
									uploadTool.uploadFile(mContext, servicePath, fileId, file, String.valueOf(mMeetingConfig.getLessionId()), 4);
								}
								pageNumber[0]++;
							} else {
								mIsUploading = false;
								ToastUtils.show(mContext, jsonObject.toString());
								mImgPaths.clear();
							}
						} else {
							mImgPaths.clear();
							mIsUploading = false;
						}
					}
				})
				.subscribe();
	}

	@Override
	public void thumbnailDelete(DocThumbnailListBean.DataBean.ImageListBean bean) {
		Observable.just(bean).observeOn(Schedulers.io())
				.map(new Function<DocThumbnailListBean.DataBean.ImageListBean, JSONObject>() {
					@Override
					public JSONObject apply(DocThumbnailListBean.DataBean.ImageListBean imageListBean) throws Exception {

						return ServiceInterfaceTools.getinstance().deleteDocThumbnail(mDocThumbnailListBean.getData().getChangeNumber(), imageListBean.getItemId());
					}
				})
				.observeOn(AndroidSchedulers.mainThread())
				.doOnNext(new Consumer<JSONObject>() {
					@Override
					public void accept(JSONObject jsonObject) throws Exception {
						String json = jsonObject.toString();
						if (!TextUtils.isEmpty(json)) {
							ThumbnailDeleteBean thumbnailDeleteBean = mGson.fromJson(json, ThumbnailDeleteBean.class);
							if (thumbnailDeleteBean.getData() != null) {
								mDocThumbnailListBean.getData().setChangeNumber(thumbnailDeleteBean.getData().getChangeNumber());
								mImageList.remove(bean);
								if (bean.isCheck() && mImageList.size() > 0) {
									mImageList.get(0).setCheck(true);
								}
								mBottomDocThumbnailAdapter.notifyDataSetChanged();
							} else {
								ToastUtils.show(mContext, json);
							}
						}
					}
				}).subscribe();
	}
}
