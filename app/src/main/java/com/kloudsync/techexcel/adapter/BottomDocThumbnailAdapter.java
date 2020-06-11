package com.kloudsync.techexcel.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.DocThumbnailListBean;
import com.kloudsync.techexcel.view.RoundProgressBar;

import java.util.List;

public class BottomDocThumbnailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
	private final String TAG = BottomDocThumbnailAdapter.class.getSimpleName();
	private List<DocThumbnailListBean.DataBean.ImageListBean> mList;
	private Context mContext;

	public BottomDocThumbnailAdapter(Context context, List<DocThumbnailListBean.DataBean.ImageListBean> list) {
		mContext = context;
		mList = list;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.item_doc_thumbnail, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		ViewHolder viewHolder = (ViewHolder) holder;
		DocThumbnailListBean.DataBean.ImageListBean imageBean = mList.get(position);
		RequestOptions options = new RequestOptions();
		options.placeholder(R.drawable.app_icon)
				.error(R.drawable.app_icon)
				.dontAnimate()
				.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
		viewHolder.mRllItemDocThumbnail.setSelected(imageBean.isCheck());
		Glide.with(mContext).load(imageBean.getAttachmentUrl()).apply(options).into(viewHolder.mIvThumbnail);
		viewHolder.mTvThumbnailPosition.setText(String.valueOf(position + 1));
		viewHolder.mRllItemDocThumbnail.setTag(position);
		viewHolder.mTvThumbnailDelete.setTag(position);
		if (imageBean.isTemp()) {
			viewHolder.mUploadProgess.setVisibility(View.VISIBLE);
			viewHolder.mUploadProgess.setProgress(imageBean.getProgress());
		} else {
			viewHolder.mUploadProgess.setVisibility(View.GONE);
			viewHolder.mRllItemDocThumbnail.setOnClickListener(this);
			viewHolder.mTvThumbnailDelete.setOnClickListener(this);
		}

		if (imageBean.isLongClick()) {
			viewHolder.mTvThumbnailDelete.setVisibility(View.VISIBLE);
		} else {
			viewHolder.mTvThumbnailDelete.setVisibility(View.GONE);
		}
		viewHolder.mRllItemDocThumbnail.setOnLongClickListener(view -> {
			for (DocThumbnailListBean.DataBean.ImageListBean bean : mList) {
				if (!bean.isTemp()) {
					bean.setLongClick(true);
				}
			}
			notifyDataSetChanged();
			return false;

		});
	}

	@Override
	public int getItemCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public void onClick(View view) {
		int index = (int) view.getTag();
		DocThumbnailListBean.DataBean.ImageListBean bean;
		switch (view.getId()) {
			case R.id.rll_item_doc_thumbnail:
				bean = mList.get(index);
				if (bean.isTemp()) return;
				Log.i(TAG, "position : " + index + "temp:" + bean.isTemp());
				if (!bean.isCheck()) {
					for (int i = 0; i < mList.size(); i++) {
						if (i == index) {
							mList.get(i).setCheck(true);
						} else {
							mList.get(i).setCheck(false);
						}
					}
					mDocThumbnailListener.thumbnailClick(bean);
					notifyDataSetChanged();
				}
				break;
			case R.id.tv_item_thumbnail_delete:
				bean = mList.get(index);
				mDocThumbnailDeleteListener.thumbnailDelete(bean);
				break;
		}
	}

	public void setListData(List<DocThumbnailListBean.DataBean.ImageListBean> list) {
		getCheckItem(list);
		mList.clear();
		mList.addAll(list);
	}

	private void getCheckItem(List<DocThumbnailListBean.DataBean.ImageListBean> list) {
		if (list.size() != 0) {
			DocThumbnailListBean.DataBean.ImageListBean bean = null;
			for (int i = 0; i < mList.size(); i++) {
				if (mList.get(i).isCheck()) {
					bean = list.get(i);
					break;
				}
			}
			if (bean != null) {
				int indexOf = list.indexOf(bean);
				list.get(indexOf).setCheck(true);
			} else {
				list.get(0).setCheck(true);
			}
		}
	}

	public void addDocImage(int index, DocThumbnailListBean.DataBean.ImageListBean tempBean) {
		mList.add(index, tempBean);
		notifyDataSetChanged();
	}

	public void refreshDocImage(int uploadPosition, int progress) {
		for (DocThumbnailListBean.DataBean.ImageListBean bean : mList) {
			if (bean.getUploadPosition() == uploadPosition) {
				bean.setProgress(progress);
			}
		}
		notifyDataSetChanged();
	}

	public void removeDocImage(int uploadPosition) {
		for (DocThumbnailListBean.DataBean.ImageListBean bean : mList) {
			if (bean.getUploadPosition() == uploadPosition) {
				mList.remove(bean);
			}
		}
		notifyDataSetChanged();
	}

	private static class ViewHolder extends RecyclerView.ViewHolder {

		private final ImageView mIvThumbnail;
		private final RoundProgressBar mUploadProgess;
		private final TextView mTvThumbnailDelete;
		private final RelativeLayout mRllItemDocThumbnail;
		private final TextView mTvThumbnailPosition;

		public ViewHolder(View itemView) {
			super(itemView);
			mRllItemDocThumbnail = itemView.findViewById(R.id.rll_item_doc_thumbnail);
			mIvThumbnail = itemView.findViewById(R.id.iv_item_doc_thumbnail);
			mUploadProgess = itemView.findViewById(R.id.rpb_update);
			mTvThumbnailDelete = itemView.findViewById(R.id.tv_item_thumbnail_delete);
			mTvThumbnailPosition = itemView.findViewById(R.id.tv_item_doc_thumbnail_position);
		}
	}

	private DocThumbnailListener mDocThumbnailListener;
	private DocThumbnailDeleteListener mDocThumbnailDeleteListener;

	public void setDocThumbnailListener(DocThumbnailListener listener) {
		mDocThumbnailListener = listener;
	}

	public void setDocThumbnailDeleteListener(DocThumbnailDeleteListener listener) {
		mDocThumbnailDeleteListener = listener;
	}

	public interface DocThumbnailListener {
		void thumbnailClick(DocThumbnailListBean.DataBean.ImageListBean imageListBean);

		void thumbnailLongClick(DocThumbnailListBean.DataBean.ImageListBean bean);

	}

	public interface DocThumbnailDeleteListener {
		void thumbnailDelete(DocThumbnailListBean.DataBean.ImageListBean bean);
	}
}
