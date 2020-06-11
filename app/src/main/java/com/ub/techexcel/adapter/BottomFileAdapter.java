package com.ub.techexcel.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.DocAndMeetingFileListPopupwindow;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.tool.ToastUtils;
import com.kloudsync.techexcel.view.RoundProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class BottomFileAdapter extends RecyclerView.Adapter<BottomFileAdapter.ViewHolder> {


    private LayoutInflater inflater;
    private List<MeetingDocument> mDatas = new ArrayList<>();
    public ImageLoader imageLoader;
    private OnDocumentClickListener onDocumentClickListener;
    public Context context;
    private int documentId;
    private MeetingDocument tempDocument;
    private MeetingConfig meetingConfig;

    public void setMeetingConfig(MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
    }

    private DocAndMeetingFileListPopupwindow mDocAndMeetingFileListPopupwindow;

    public void addTempDocument(MeetingDocument tempDocument) {
        this.tempDocument = tempDocument;
        mDatas.add(tempDocument);
        notifyDataSetChanged();
    }


    public void setDocumentId(List<MeetingDocument> documents, int documentId) {
        this.documentId = documentId;
        mDatas.clear();
        mDatas.addAll(documents);
        if (mDatas.size() < 0) {
            return;
        }

        for (MeetingDocument document : mDatas) {
            if (document.getItemID() == documentId) {
                document.setSelect(true);
            } else {
                document.setSelect(false);
            }
        }
    }

    private void clearSelected() {

        for (MeetingDocument document : mDatas) {
            document.setSelect(false);
        }
    }

    public interface OnDocumentClickListener {
        void onDocumentClick(MeetingDocument document);
    }

    public void setOnDocumentClickListener(OnDocumentClickListener onDocumentClickListener) {
        this.onDocumentClickListener = onDocumentClickListener;
    }

    public BottomFileAdapter(Context context, List<MeetingDocument> datas, MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
        inflater = LayoutInflater.from(context);
        mDatas.clear();
        mDatas.addAll(datas);
        imageLoader = new ImageLoader(context);
        this.context = context;
    }

    public void refreshFiles(List<MeetingDocument> documents) {
        List<MeetingDocument> list = fillSelected(documents);
        mDatas.clear();
        mDatas.addAll(list);
        notifyDataSetChanged();
    }

    private List<MeetingDocument> fillSelected(List<MeetingDocument> documents) {

        int itemId = 0;
        if (mDatas != null && mDatas.size() > 0) {
            for (MeetingDocument document : mDatas) {
                if (document.isSelect()) {
                    itemId = document.getItemID();
                    break;
                }
            }
        }
        if (itemId > 0) {
            if (documents != null && documents.size() > 0) {
                int index = documents.indexOf(new MeetingDocument(itemId));
                if (index >= 0) {
                    documents.get(index).setSelect(true);
                }

            }
        }
        return documents;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View a) {
            super(a);
        }

        SimpleDraweeView icon;
        TextView name/*, identlyTv*/, mTvYinXiangCount, mTvCreateUserName, mDocTime;
        LinearLayout headll/*, bgisshow*/, bgisshow2, mLlyYinXiangCount;
        RoundProgressBar rpb_update;
        ImageView mIvItemDocMore;
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.pop_document_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.icon = view.findViewById(R.id.studenticon);
        viewHolder.mDocTime = view.findViewById(R.id.txt_doc_time);
        viewHolder.name = (TextView) view.findViewById(R.id.studentname);
//        viewHolder.identlyTv = (TextView) view.findViewById(R.id.identlyTv);
        viewHolder.headll = (LinearLayout) view.findViewById(R.id.headll);
//        viewHolder.bgisshow = (LinearLayout) view.findViewById(R.id.bgisshow);
        viewHolder.bgisshow2 = (LinearLayout) view.findViewById(R.id.bgisshow2);
        viewHolder.rpb_update = (RoundProgressBar) view.findViewById(R.id.rpb_update);
        viewHolder.mIvItemDocMore = view.findViewById(R.id.iv_item_doc_more);
        viewHolder.mTvCreateUserName = view.findViewById(R.id.tv_item_create_user_name);
        viewHolder.mLlyYinXiangCount = view.findViewById(R.id.lly_item_doc_yin_xiang_count);
        viewHolder.mTvYinXiangCount = view.findViewById(R.id.tv_item_yin_xiang_count);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MeetingDocument document = mDatas.get(position);
        if (document.isTemp()) {
            holder.itemView.setOnClickListener(null);
            holder.bgisshow2.setVisibility(View.VISIBLE);
            holder.rpb_update.setVisibility(View.VISIBLE);
//                holder.bgisshow.setVisibility(View.GONE);
//                holder.icon.setVisibility(View.GONE);
            if (TextUtils.isEmpty(document.getTempDocPrompt())) {
                holder.name.setText("");
            } else {
                holder.name.setText(/*document.getTempDocPrompt()*/document.getTitle());
            }
            holder.rpb_update.setProgress(document.getProgress());
        } else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (meetingConfig != null) {
                        if (meetingConfig.getType() == MeetingType.MEETING) {
                            if (!meetingConfig.isMeetingPause() && !meetingConfig.getPresenterId().equals(AppConfig.UserID)) {
                                ToastUtils.show(context, R.string.you_are_not_the_presenter_and_cannot_operate);
                                return;
                            }
                        }
                    }
                    if (onDocumentClickListener != null) {
                        clearSelected();
                        document.setSelect(true);
                        onDocumentClickListener.onDocumentClick(document);
                        notifyDataSetChanged();
                    }
                }
            });
            holder.bgisshow2.setVisibility(View.GONE);
            holder.rpb_update.setVisibility(View.GONE);
//                holder.bgisshow.setVisibility(View.VISIBLE);
//                holder.icon.setVisibility(View.VISIBLE);
            int syncCount = document.getSyncCount();
            if (syncCount > 0) {
                holder.mLlyYinXiangCount.setVisibility(View.VISIBLE);
                holder.mTvYinXiangCount.setText(String.valueOf(syncCount));
            } else {
                holder.mLlyYinXiangCount.setVisibility(View.GONE);
                holder.mTvYinXiangCount.setText("");
            }
            if (!TextUtils.isEmpty(document.getCreatedDate())) {
                String createData = new SimpleDateFormat("yyyy.MM.dd HH:mm").format(Long.parseLong(document.getCreatedDate()));

                holder.mDocTime.setText(createData);
                holder.mDocTime.setVisibility(View.VISIBLE);
            } else {
                holder.mDocTime.setVisibility(View.GONE);
            }
            holder.name.setText(/*(position + 1) + ""*/document.getTitle());
            String url = document.getAttachmentUrl();
            if (!TextUtils.isEmpty(url)) {
                url = url.substring(0, url.lastIndexOf("<")) + "1" + url.substring(url.lastIndexOf("."), url.length());
                Uri imageUri = null;
                if (!TextUtils.isEmpty(url)) {
                    imageUri = Uri.parse(url);
                }
                holder.icon.setImageURI(imageUri);
            }
            holder.mTvCreateUserName.setText(null);
            holder.headll.setSelected(document.isSelect());
            holder.mIvItemDocMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDocAndMeetingFileListPopupwindow == null) {
                        mDocAndMeetingFileListPopupwindow = new DocAndMeetingFileListPopupwindow(context);
                    }
                    mDocAndMeetingFileListPopupwindow.show(holder.mIvItemDocMore, document, meetingConfig);
                }
            });
        }

    }


    public void refreshTempDoc(int progress, String prompt) {
        if (tempDocument != null) {
            tempDocument.setProgress(progress);
            tempDocument.setTempDocPrompt(prompt);
        }
        notifyDataSetChanged();
    }

    public void removeTempDoc() {
        if (tempDocument != null) {
            mDatas.remove(tempDocument);
            tempDocument = null;
        }
        notifyDataSetChanged();
    }

}
