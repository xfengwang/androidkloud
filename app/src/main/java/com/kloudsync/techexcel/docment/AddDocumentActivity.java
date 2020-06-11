package com.kloudsync.techexcel.docment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.CenterToast;
import com.kloudsync.techexcel.dialog.UploadFileDialog;
import com.kloudsync.techexcel.filepicker.FileEntity;
import com.kloudsync.techexcel.filepicker.FilePickerActivity;
import com.kloudsync.techexcel.filepicker.PickerManager;
import com.kloudsync.techexcel.help.AddDocumentTool;
import com.kloudsync.techexcel.help.DocChooseDialog;
import com.kloudsync.techexcel.tool.DocumentUploadTool;
import com.ub.kloudsync.activity.SelectTeamActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.adapter.SpaceAdapterV2;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.tools.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddDocumentActivity extends BaseActivity implements View.OnClickListener, SpaceAdapterV2.OnItemClickListener, DocChooseDialog.SelectedOptionListener {
    private RelativeLayout backLayout;
    private RelativeLayout teamLayout;
    private TextView teamNameText;
    private RecyclerView spaceList;
    String teamName;
    private SpaceAdapterV2 spaceAdapter;
    int teamId;
    private static final int REQUEST_SELECTED_IMAGE = 1;
    private static final int REQUEST_SELECT_TEAM = 2;
    private static final int REQUEST_SELECT_DOC = 3;
    private static final int REQUEST_SELECTED_FILE = 4;
    private static final int REQUEST_SELECTED_CAMERA= 5;
    UploadFileDialog uploadFileDialog;
    int spaceId;
    TextView titleText;

    // 要申请的权限
    private String[] permissions = new String[]{  Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};

    @Override
    protected int setLayout() {
        return R.layout.activity_add_document;
    }

    @Override
    protected void initView() {


        teamName = getIntent().getStringExtra("team_name");
        teamId = getIntent().getIntExtra("team_id", -1);
        titleText = findViewById(R.id.tv_title);
        titleText.setText(R.string.select_a_space);
        backLayout = findViewById(R.id.layout_back);
        teamLayout = findViewById(R.id.layout_team);
        teamNameText = findViewById(R.id.txt_team_name);
        if (!TextUtils.isEmpty(teamName)) {
            teamNameText.setText(teamName);
        }

        backLayout.setOnClickListener(this);
        spaceList = findViewById(R.id.list_space);
        spaceList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        spaceAdapter = new SpaceAdapterV2(this, new ArrayList<TeamSpaceBean>(),false);
        spaceList.setAdapter(spaceAdapter);
        spaceAdapter.setOnItemClickListener(this);
        teamLayout.setOnClickListener(this);
        getSpaceList(teamId);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.layout_team:
                Intent intent = new Intent(this, SelectTeamActivity.class);
                intent.putExtra("team_id", teamId);
                startActivityForResult(intent, REQUEST_SELECT_TEAM);
                break;
            default:
                break;
        }
    }

    private void getSpaceList(final int teamID) {
        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=2&parentID=" + teamID,
                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        spaceAdapter.setSpaces((List<TeamSpaceBean>) object);
                    }
                });
    }

    DocChooseDialog dialog;

    @Override
    public void onItemClick(TeamSpaceBean teamSpaceBean) {
        spaceId = teamSpaceBean.getItemID();
        dialog = new DocChooseDialog(this);
        dialog.setSelectedOptionListener(this);
        startRequestPermission();
    }

    @Override
    public void selectFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECTED_IMAGE);
    }

    @Override
    public void selectFromDocs() {
        Intent intent = new Intent(this, FavoriteDocumentsActivity.class);
        intent.putExtra("space_id", spaceId);
        startActivityForResult(intent, REQUEST_SELECT_DOC);
    }



    @Override
    public void selectFromFiles() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("application/msword|application/vnd.openxmlformats-officedocument.wordprocessingml.document" +
//                "|application/pdf|application/vnd.ms-powerpoint|application/vnd.openxmlformats-officedocument.presentationml.presentation");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//        }
//        startActivityForResult(intent, REQUEST_SELECTED_FILE);


        Intent intent = new Intent(this, FilePickerActivity.class);
        startActivityForResult(intent,REQUEST_SELECTED_FILE);

    }

    /**
     * 拍照上传
     */
    @Override
    public void selectFromCamera() {
        boolean createSuccess = FileUtils.createFileSaveDir(this);
        if (!createSuccess) {
            Toast.makeText(getApplicationContext(), "文件系统异常，打开失败", Toast.LENGTH_SHORT).show();
        }else{
            openCameraForAddDoc();
        }
    }


    private void startRequestPermission(){
        ActivityCompat.requestPermissions(this, permissions, 321);
    }



    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean i = shouldShowRequestPermissionRationale(permissions[0]);
                    boolean j = shouldShowRequestPermissionRationale(permissions[1]);
                    boolean k = shouldShowRequestPermissionRationale(permissions[2]);
                    if (!i||!j||!k) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
//                        showDialogTipUserGoToAppSettting();
                    } else {
                        Toast.makeText(this, "必要权限未开启", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    dialog.show();
                }
            }
        }
    }

    private File cameraFile;

    private void openCameraForAddDoc() {
        if (!isCameraCanUse()) {
            Toast.makeText(getApplicationContext(), "相机不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String mFilePath = FileUtils.getBaseDir();
        // 文件名
        String fileName = "Kloud_" + DateFormat.format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA))
                + ".jpg";
        cameraFile = new File(mFilePath, fileName);
        //Android7.0文件保存方式改变了
        if (Build.VERSION.SDK_INT < 24) {
            Uri uri = Uri.fromFile(cameraFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, cameraFile.getAbsolutePath());
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        startActivityForResult(intent, REQUEST_SELECTED_CAMERA);
    }

    private boolean isCameraCanUse() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)
                && !getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SELECTED_IMAGE:
                    if (data != null) {
                        String path = FileUtils.getPath(this, data.getData());
                        uploadFile(path);
                    }
                    break;
                case REQUEST_SELECT_TEAM:
                    if (data != null) {
                        refresh(data.getIntExtra("team_id", -1), data.getStringExtra("team_name"));
                    }
                    break;
                case REQUEST_SELECT_DOC:
                    addDocSucc();
                    break;
                case  REQUEST_SELECTED_FILE:
                    Log.e("buildversion",Build.VERSION.SDK_INT+"");
//                    Uri uri = data.getData();
//                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
//                       String path = getPath(this, uri);
//                        uploadFile(path);
//                    } else{
//                        String path = getRealPathFromURI(uri);
//                        uploadFile(path);
//                    }


//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        //高于API19版本
//                        String[] split = data.getData().getPath().split("\\:");
//                        String p = "";
//                        if (split.length >= 2) {
//                            p = Environment.getExternalStorageDirectory() + "/" + split[1];
//                            Log.e("buildversion",p+"");
//                        }
//                    } else {
//                        //低于API19版本
//                        Uri uri = data.getData();
//                        Log.e("buildversion","  gg"+ uri.toString());
//                    }


                    List<FileEntity>  fff =PickerManager.getInstance().files;
                    for (int i = 0; i < fff.size(); i++) {
                        FileEntity fileEntity=fff.get(0);
                        Log.e("buildversion",fileEntity.getPath()+"");
                        uploadFile(fileEntity.getPath());
                    }



                    break;
                case REQUEST_SELECTED_CAMERA:
                    if (cameraFile != null && cameraFile.exists()) {
                        Log.e("onActivityResult", "camera_file:" + cameraFile);
                        uploadFile(cameraFile.getAbsolutePath());
                    }
                    break;
            }
        }
    }


    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        Log.e("buildversion",uri.toString());
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }



    private void  uploadFile(String path){
        if(TextUtils.isEmpty(path)){
            Toast.makeText(AddDocumentActivity.this,"path null",Toast.LENGTH_LONG).show();
            return;
        }

        Log.e("onActivityResult",path);
        String mineType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(path));
//        Log.e("onActivityResult","mine_type:" + mineType + "," + path);

        String pathname = path.substring(path.lastIndexOf("/") + 1);
        LineItem file = new LineItem();
        file.setUrl(path);
        file.setFileName(pathname);
        if(!TextUtils.isEmpty(mineType) && mineType.contains("video")){
            AddDocumentTool.addVideoDocumentToSpace(this, path, spaceId, new DocumentUploadTool.DocUploadDetailLinstener() {
                @Override
                public void uploadStart(){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (uploadFileDialog == null) {
                                uploadFileDialog = new UploadFileDialog(AddDocumentActivity.this);
                                uploadFileDialog.setTile("uploading");
                                uploadFileDialog.show();

                            } else {
                                if (!uploadFileDialog.isShowing()) {
                                    uploadFileDialog.show();
                                }
                            }
                        }
                    });
                }

                @Override
                public void uploadFile(final int progress) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                                uploadFileDialog.setProgress(progress);
                            }
                        }
                    });
                }

                @Override
                public void convertFile(final int progress) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                                uploadFileDialog.setTile("Converting");
                                uploadFileDialog.setProgress(progress);
                            }
                        }
                    });
                }

                @Override
                public void uploadFinished(Object result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addDocSucc();
                        }
                    });

                }

                @Override
                public void uploadError(String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "add failed", Toast.LENGTH_SHORT).show();
                            if (uploadFileDialog != null) {
                                uploadFileDialog.cancel();
                            }
                        }
                    });
                }
            });

        }else {

            AddDocumentTool.addDocumentToSpace(this, path, spaceId, new DocumentUploadTool.DocUploadDetailLinstener() {
                @Override
                public void uploadStart(){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (uploadFileDialog == null) {
                                uploadFileDialog = new UploadFileDialog(AddDocumentActivity.this);
                                uploadFileDialog.setTile("uploading");
                                uploadFileDialog.show();

                            } else {
                                if (!uploadFileDialog.isShowing()) {
                                    uploadFileDialog.show();
                                }
                            }
                        }
                    });
                }

                @Override
                public void uploadFile(final int progress) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                                uploadFileDialog.setProgress(progress);
                            }
                        }
                    });
                }

                @Override
                public void convertFile(final int progress) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                                uploadFileDialog.setTile("Converting");
                                uploadFileDialog.setProgress(progress);
                            }
                        }
                    });
                }

                @Override
                public void uploadFinished(Object result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addDocSucc();
                        }
                    });

                }

                @Override
                public void uploadError(String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "add failed", Toast.LENGTH_SHORT).show();
                            if (uploadFileDialog != null) {
                                uploadFileDialog.cancel();
                            }
                        }
                    });
                }
            });

        }


//                        uploadFile(file, spaceId);
    }



    private void refresh(int teamId, String teamName) {
        this.teamId = teamId;
        getSpaceList(teamId);
        if (!TextUtils.isEmpty(teamName)) {
            teamNameText.setText(teamName);
        }
    }


    private void addDocSucc() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (uploadFileDialog != null) {
                    uploadFileDialog.cancel();
                }
                new CenterToast.Builder(getApplicationContext()).setSuccess(true).setMessage(getResources().getString(R.string.create_success)).create().show();
                EventBus.getDefault().post(new TeamSpaceBean());
                finish();
            }
        });
    }



}
