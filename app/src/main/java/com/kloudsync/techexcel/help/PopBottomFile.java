package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventShowMenuIcon;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.view.MyDialog;
import com.ub.techexcel.adapter.BottomFileAdapter;
import com.ub.techexcel.tools.Tools;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class PopBottomFile implements DialogInterface.OnDismissListener, OnClickListener, TextView.OnEditorActionListener {

    private MyDialog bottomFileWindow;
    int width;
    private Context mContext;
    //--
    private RecyclerView fileList;
    private BottomFileAdapter adapter;
    //    private LinearLayout uploadLayout;
    private MeetingConfig meetingConfig;
    private RelativeLayout mRllListFile;
    private EditText mEtFileName;
    private InputMethodManager mImm;
    private List<MeetingDocument> mDocuments = new ArrayList<>();
    private int mDocumentId;
    private PopupWindow mPpwAddDoc;
    private RelativeLayout mTvAddDoc;
    private View mView;
    private LinearLayout mLlyAddDoc;
    private RelativeLayout mRlyDocListEmpey;
    private LinearLayout filePop;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_add:
                /*if (uploadLayout.getVisibility() != View.VISIBLE) {
                    uploadLayout.setVisibility(View.VISIBLE);
                } else {
                    uploadLayout.setVisibility(View.GONE);
                }*/

                showAddDocPpw();
                break;
            case R.id.popup_bottom_file:
                /*if (uploadLayout.getVisibility() == View.VISIBLE) {
                    uploadLayout.setVisibility(View.GONE);
                } else {
                    if (bottomFileWindow != null) {
                        bottomFileWindow.dismiss();
                    }

                }*/
                break;
            case R.id.take_photo:
              /*  if(uploadLayout != null){
                    uploadLayout.setVisibility(View.GONE);
                }*/
                addDocPpwDismiss();
                if (bottomFileOperationsListener != null) {
                    bottomFileOperationsListener.addFromCamera();
                }

                break;
            case R.id.file_library:
                /*if(uploadLayout != null){
                    uploadLayout.setVisibility(View.GONE);
                }*/
                addDocPpwDismiss();
                if (bottomFileOperationsListener != null) {
                    bottomFileOperationsListener.addFromPictures();
                }
                break;
            case R.id.fileststem_library:
                /*if(uploadLayout != null){
                    uploadLayout.setVisibility(View.GONE);
                }*/
                addDocPpwDismiss();
                if (bottomFileOperationsListener != null) {
                    bottomFileOperationsListener.addFromFileSystem();
                }
                break;
            case R.id.save_file:
               /* if(uploadLayout != null){
                    uploadLayout.setVisibility(View.GONE);
                }*/
                addDocPpwDismiss();
                if (bottomFileOperationsListener != null) {
                    bottomFileOperationsListener.addFromFavorite();
                }
                break;
            case R.id.team_document:
               /* if(uploadLayout != null){
                    uploadLayout.setVisibility(View.GONE);
                }*/
                addDocPpwDismiss();
                if (bottomFileOperationsListener != null) {
                    bottomFileOperationsListener.addFromTeam();
                }
                break;

            case R.id.blank_file:
                /*if(uploadLayout != null){
                    uploadLayout.setVisibility(View.GONE);
                }*/
                addDocPpwDismiss();
                if (bottomFileOperationsListener != null) {
                    bottomFileOperationsListener.addBlankFile();
                }
                break;
        }


    }

    private void showAddDocPpw() {
        if (mPpwAddDoc == null) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.ppw_add_doc, null);
            mLlyAddDoc = mView.findViewById(R.id.lly_ppw_add_doc);
            mLlyAddDoc.getBackground().setAlpha(204);
            LinearLayout take_photo = mView.findViewById(R.id.take_photo);
            LinearLayout file_library = mView.findViewById(R.id.file_library);
            LinearLayout fileststem_library = mView.findViewById(R.id.fileststem_library);
            LinearLayout favorite_file = mView.findViewById(R.id.save_file);
            LinearLayout team_file = mView.findViewById(R.id.team_document);
            LinearLayout blank_file = mView.findViewById(R.id.blank_file);

            take_photo.setOnClickListener(this);
            file_library.setOnClickListener(this);
            fileststem_library.setOnClickListener(this);
            favorite_file.setOnClickListener(this);
            team_file.setOnClickListener(this);
            blank_file.setOnClickListener(this);
            mPpwAddDoc = new PopupWindow(mView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mPpwAddDoc.setBackgroundDrawable(new ColorDrawable());
            mPpwAddDoc.setOutsideTouchable(true);
            mPpwAddDoc.setFocusable(true);
        }
        int y = mContext.getResources().getDimensionPixelOffset(R.dimen.dp_5);
        mLlyAddDoc.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int width = mLlyAddDoc.getMeasuredWidth();
        int addIconWidth = mTvAddDoc.getWidth();
        Log.i(PopBottomFile.class.getSimpleName().toString(), "width = " + width + "addIconWidth=" + addIconWidth);
        mPpwAddDoc.showAsDropDown(mTvAddDoc, -width + addIconWidth, y);
    }

    private void addDocPpwDismiss() {
        if (mPpwAddDoc != null && mPpwAddDoc.isShowing()) {
            mPpwAddDoc.dismiss();
        }
    }


    public interface BottomFileOperationsListener {
        void addFromTeam();

        void addFromCamera();

        void addFromCameraDocImage();

        void addFromPictures();

        void addFromFileSystem();

        void addFromFavorite();

        void addBlankFile();
    }

    private BottomFileOperationsListener bottomFileOperationsListener;

    public PopBottomFile(Context context, MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
        this.mContext = context;
        getPopupWindow();
    }


    public void getPopupWindow() {
        if (null != bottomFileWindow) {
            bottomFileWindow.dismiss();
            return;
        } else {
            init();
        }
    }


    public void init() {
        mImm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater
                .inflate(R.layout.pop_bottom_file, null);
        filePop = (LinearLayout) view.findViewById(R.id.popup_bottom_file);
        mRllListFile = view.findViewById(R.id.rll_list_file);
        mRlyDocListEmpey = view.findViewById(R.id.rly_doc_list_empty);
//        uploadLayout = (LinearLayout) view.findViewById(R.id.upload_linearlayout);
        filePop.setOnClickListener(this);
        mEtFileName = view.findViewById(R.id.et_dialog_bottom_file_file_name);
        fileList = (RecyclerView) view.findViewById(R.id.list_file);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(mContext);
//        linearLayoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        fileList.setLayoutManager(linearLayoutManager3);
        mTvAddDoc = view.findViewById(R.id.layout_add);
        mEtFileName.setOnEditorActionListener(this);
        mTvAddDoc.setOnClickListener(this);
        bottomFileWindow = new MyDialog(mContext, R.style.my_dialog);
        bottomFileWindow.setContentView(view);
        bottomFileWindow.setCanceledOnTouchOutside(true);
        bottomFileWindow.setOnDismissListener(this);
    }


    public void show(View view, PopBottomFile.BottomFileOperationsListener bottomMenuOperationsListener) {
        this.bottomFileOperationsListener = bottomMenuOperationsListener;
        if (bottomFileWindow == null) {
            init();
        }
        if (mDocuments.size() > 0) {
            setDocEmptyTipsVisibility(false);
        } else {
            setDocEmptyTipsVisibility(true);
        }

        WindowManager.LayoutParams layoutParams = bottomFileWindow.getWindow().getAttributes();
        if (Tools.isOrientationPortrait((Activity) mContext)) {
            filePop.setBackgroundResource(R.drawable.shape_white_top_radius_15);
            bottomFileWindow.getWindow().setGravity(Gravity.BOTTOM);
            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = mContext.getResources().getDimensionPixelOffset(R.dimen.dp_320);
            bottomFileWindow.getWindow().setWindowAnimations(R.style.PopupAnimation5);
        } else {
            filePop.setBackgroundResource(R.drawable.shape_white_left_radius_15);
            bottomFileWindow.getWindow().setGravity(Gravity.RIGHT);
            layoutParams.width = mContext.getResources().getDimensionPixelOffset(R.dimen.dp_375);
            layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            bottomFileWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            bottomFileWindow.getWindow().setWindowAnimations(R.style.anination3);
        }
        bottomFileWindow.getWindow().setAttributes(layoutParams);

        if (!bottomFileWindow.isShowing()) {
            bottomFileWindow.show();
        }
    }


    public boolean isShowing() {
        if (bottomFileWindow != null) {
            return bottomFileWindow.isShowing();
        }
        return false;
    }

    public void hide() {
        if (bottomFileWindow != null) {
            bottomFileWindow.dismiss();
        }

    }

    private void hideKeyBoard() {
        mImm.hideSoftInputFromWindow(mEtFileName.getWindowToken(), 0);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mEtFileName.setText("");
        EventBus.getDefault().post(new EventShowMenuIcon());
    }

    List<MeetingDocument> mSearchList = new ArrayList<>();

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH && event.getAction() == KeyEvent.ACTION_DOWN)) {
            hideKeyBoard();
            String searchText = mEtFileName.getText().toString();
            if (TextUtils.isEmpty(searchText)) {
//                ToastUtils.show(mContext,R.string.the_content_can_not_be_blank);
                adapter.setDocumentId(mDocuments, mDocumentId);
                adapter.notifyDataSetChanged();
                if (mDocuments.size() > 0) {
                    setDocEmptyTipsVisibility(false);
                } else {
                    setDocEmptyTipsVisibility(true);
                }
                return true;
            }
            mSearchList.clear();
            for (MeetingDocument meetingDocument : mDocuments) {
                if (meetingDocument.getTitle().contains(searchText)) {
                    mSearchList.add(meetingDocument);
                }
            }
            adapter.setDocumentId(mSearchList, mDocumentId);
            adapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void setDocuments(List<MeetingDocument> documents, int documentId, BottomFileAdapter.OnDocumentClickListener clickListener) {
        mDocuments = documents;
        mDocumentId = documentId;
        if (adapter == null) {
            adapter = new BottomFileAdapter(mContext, documents, meetingConfig);
            adapter.setOnDocumentClickListener(clickListener);
            adapter.setDocumentId(documents, documentId);
            fileList.setAdapter(adapter);
        } else {
            adapter.setOnDocumentClickListener(clickListener);
            adapter.setDocumentId(documents, documentId);
            adapter.notifyDataSetChanged();
        }
        if (mDocuments.size() > 0) {
            setDocEmptyTipsVisibility(false);
        } else {
            setDocEmptyTipsVisibility(true);
        }
    }

    public void setDocuments(List<MeetingDocument> documents, BottomFileAdapter.OnDocumentClickListener clickListener) {
        mDocuments = documents;
        if (adapter == null) {
            adapter = new BottomFileAdapter(mContext, documents, meetingConfig);
            adapter.setOnDocumentClickListener(clickListener);
            fileList.setAdapter(adapter);
        } else {
            adapter.setOnDocumentClickListener(clickListener);
            adapter.refreshFiles(documents);
        }
    }

    public void addTempDoc(MeetingDocument tempDoc) {
        if (adapter != null) {
            adapter.addTempDocument(tempDoc);
        }
    }

    public void refreshTempDoc(String prompt, int progress) {
        if (adapter != null) {
            adapter.refreshTempDoc(progress, prompt);
        }
    }

    public void removeTempDoc() {
        if (adapter != null) {
            adapter.removeTempDoc();
        }
    }

    public void openAndShowAdd(View view, BottomFileOperationsListener bottomFileOperationsListener) {
        show(view, bottomFileOperationsListener);
//        uploadLayout.setVisibility(View.VISIBLE);
        mTvAddDoc.post(new Runnable() {
            @Override
            public void run() {
                if (!((Activity) mContext).isFinishing()) showAddDocPpw();
            }
        });
    }

    private void setDocEmptyTipsVisibility(boolean isShow) {
        if (isShow) {
            mRlyDocListEmpey.setVisibility(View.VISIBLE);
            fileList.setVisibility(View.GONE);
        } else {
            mRlyDocListEmpey.setVisibility(View.GONE);
            fileList.setVisibility(View.VISIBLE);
        }
    }

}
