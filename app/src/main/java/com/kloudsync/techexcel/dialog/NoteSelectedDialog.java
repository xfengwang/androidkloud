package com.kloudsync.techexcel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventSelectNote;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.tools.NoteOthersPopup;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class NoteSelectedDialog implements View.OnClickListener {
    public Context mContext;
    public int width;
    public int heigth;
    public Dialog dialog;
    private View view;
    private ImageView closeImage;
    private TextView cancelText;
    private TextView saveText;
    private TextView selectuser;
    private TextView txt_doc_space_name;
    private EventSelectNote eventSelectNote;

    public NoteSelectedDialog(Context context) {
        mContext = context;
        initDialog();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_add_file_from_note, null);
        dialog = new Dialog(mContext, R.style.my_dialog);
        noteList = view.findViewById(R.id.list_document);
        noteList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        closeImage = view.findViewById(R.id.image_close);
        txt_doc_space_name = view.findViewById(R.id.txt_doc_space_name);
        txt_doc_space_name.setText("My Notes");
        closeImage.setOnClickListener(this);
        cancelText = view.findViewById(R.id.cancel);
        selectuser = view.findViewById(R.id.selectuser);
        saveText = view.findViewById(R.id.save);
        saveText.setOnClickListener(this);
        cancelText.setOnClickListener(this);
        selectuser.setOnClickListener(this);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * (0.85f));
        heigth = (int) (mContext.getResources().getDisplayMetrics().heightPixels * (0.80f));
        dialog.setContentView(view);
        dialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = width;
        lp.height = heigth;
        dialog.getWindow().setAttributes(lp);
    }


    public boolean isShowing() {
        if(dialog != null){
            dialog.isShowing();
        }
        return false;
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_close:
                dismiss();
                break;
            case R.id.save:
                    if (noteAdapter != null) {
                        Note note = noteAdapter.getSelectFile();
                        if (note != null) {
                            eventSelectNote.setNote(note);
                            EventBus.getDefault().post(eventSelectNote);
                            dismiss();
                        } else {
                            Toast.makeText(mContext, "no note selected", Toast.LENGTH_SHORT).show();
                        }

                }
                break;
            case R.id.cancel:
                dismiss();
                break;

        }
    }

    public void show(EventSelectNote eventSelectNote) {
        this.eventSelectNote = eventSelectNote;
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
            getNoteList();
        }
    }

    NoteAdapter noteAdapter;
    RecyclerView noteList;

    private void getNoteList() {
        selectuser.setText(AppConfig.UserName);
        ServiceInterfaceTools.getinstance().getUserNoteList(AppConfig.URL_PUBLIC + "DocumentNote/UserNoteList?userID=" + AppConfig.UserID, ServiceInterfaceTools.GETSYNCROOMUSERLIST, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                List<Note> list = new ArrayList<>();
                list.addAll((List<Note>) object);
                noteAdapter = new NoteAdapter(mContext, list);
                noteList.setAdapter(noteAdapter);
            }
        });
    }

    class DocHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView size;
        ImageView imageview;
        public DocHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            size = (TextView) itemView
                    .findViewById(R.id.filesize);
            time = (TextView) itemView
                    .findViewById(R.id.totalTime);
            imageview = (ImageView) itemView
                    .findViewById(R.id.imageview);
        }
    }


    public class NoteAdapter extends RecyclerView.Adapter<DocHolder> {
        private Context mContext;
        private List<Note> mDatas;
        int selectPosition = -1;
        public NoteAdapter(Context context, List<Note> mDatas) {
            this.mContext = context;
            this.mDatas = mDatas;

        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        public Note getSelectFile() {
            if (selectPosition == -1) {
                return null;
            }
            return mDatas.get(selectPosition);
        }

        @Override
        public DocHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DocHolder(LayoutInflater.from(mContext).inflate(R.layout.popup_video_item, parent, false));
        }

        @Override
        public void onBindViewHolder(DocHolder holder, final int position) {
            holder.name.setText(mDatas.get(position).getFileName());
            holder.time.setText("123kb");
            String create = mDatas.get(position).getCreatedDate();
            String createdate = "";
            if (!TextUtils.isEmpty(create)) {
                long dd = Long.parseLong(create);
                createdate = new SimpleDateFormat("yyyy.MM.dd").format(dd);
            }
            holder.size.setText(createdate);
            if (selectPosition == position) {
                holder.imageview.setImageResource(R.drawable.finish_a);
            } else {
                holder.imageview.setImageResource(R.drawable.finish_d);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPosition = position;
                    notifyDataSetChanged();
                }
            });
        }
    }
}
