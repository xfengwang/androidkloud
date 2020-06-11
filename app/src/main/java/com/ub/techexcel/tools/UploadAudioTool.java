package com.ub.techexcel.tools;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ResumableUploadRequest;
import com.alibaba.sdk.android.oss.model.ResumableUploadResult;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManagerConfiguration;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventSyncSucc;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.Popupdate;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.help.UploadAudioPopupdate;
import com.kloudsync.techexcel.info.Uploadao;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.service.audiorecord.RecordEndListener;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.SoundtrackBean;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class UploadAudioTool {

    static volatile UploadAudioTool instance;
    private Context mContext;

    public UploadAudioTool(Context context){
        mContext=context;
    }

    public static UploadAudioTool getManager(Context context) {
        if (instance == null) {
            synchronized (UploadAudioTool.class) {
                if (instance == null) {
                    instance = new UploadAudioTool(context);
                }
            }
        }
        return instance;
    }

    private  UploadAudioPopupdate uploadAudioPopupdate;
    private int soundtrackID;
    private int fieldId;
    private String fieldNewPath;
    private MeetingConfig meetingConfig;
    private UploadAudioListener uploadAudioListener;

    public void uploadAudio(File file, SoundtrackBean soundtrackBean, UploadAudioPopupdate uploadAudioPopupdate, MeetingConfig meetingConfig, UploadAudioListener uploadAudioListener){

        upload=null;
        ossAsyncTask=null;
        this.soundtrackID=soundtrackBean.getSoundtrackID();
        this.fieldId=soundtrackBean.getFileId();
        this.fieldNewPath=soundtrackBean.getPath();
        this.meetingConfig=meetingConfig;
        this.uploadAudioPopupdate=uploadAudioPopupdate;
        this.uploadAudioListener=uploadAudioListener;
        final LineItem attachmentBean = new LineItem();
        attachmentBean.setUrl(file.getAbsolutePath()); // 文件的路径
        attachmentBean.setFileName(file.getName()); // 文件名
        LoginGet lg = new LoginGet();
        lg.setprepareUploadingGetListener(new LoginGet.prepareUploadingGetListener() {
            @Override
            public void getUD(Uploadao ud) {
                if (1 == ud.getServiceProviderId()) {
                    uploadWithTransferUtility(attachmentBean, ud);
                } else if (2 == ud.getServiceProviderId()) {
                    initOSS(attachmentBean, ud);
                }
            }
        });
        lg.GetprepareUploading(mContext);
    }


    private Upload upload;
    public void uploadWithTransferUtility(final LineItem attachmentBean, final Uploadao ud) {
        mfile = new File(attachmentBean.getUrl());
        fileName = mfile.getName();
        MD5Hash = fieldNewPath + "/" + fileName;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                        ud.getAccessKeyId(),
                        ud.getAccessKeySecret(),
                        ud.getSecurityToken());
                AmazonS3Client s3 = new AmazonS3Client(sessionCredentials);
                s3.setRegion(com.amazonaws.regions.Region.getRegion(ud.getRegionName()));
                com.amazonaws.services.s3.model.PutObjectRequest request = new com.amazonaws.services.s3.model.PutObjectRequest(ud.getBucketName(), MD5Hash, mfile);
                TransferManager tm = new TransferManager(s3);
                TransferManagerConfiguration transferManagerConfiguration=new TransferManagerConfiguration();
                transferManagerConfiguration.setMultipartUploadThreshold(1*100*1024);
                tm.setConfiguration(transferManagerConfiguration);
                request.setCannedAcl(CannedAccessControlList.PublicRead);
                request.setGeneralProgressListener(new ProgressListener() {
                    @Override
                    public void progressChanged(final ProgressEvent progressEvent) {
                        Log.e("syncing---amazon", mfile.length()+"   "+progressEvent.getBytesTransferred());
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uploadAudioPopupdate.setProgress2(mfile.length(), progressEvent.getBytesTransferred());
                            }
                        });
                    }
                });
                upload = tm.upload(request);
                try {
                    upload.waitForCompletion();
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            yinxiangUploadNewFile(ud);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    private OSS oss;

    private void initOSS(final LineItem attachmentBean, final Uploadao ud) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(ud.getAccessKeyId(),
                        ud.getAccessKeySecret(), ud.getSecurityToken());
                ClientConfiguration conf = new ClientConfiguration();
                conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
                conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
                conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
                conf.setMaxErrorRetry(2);  // 失败后最大重试次数，默认2次
                OSSLog.enableLog();
                oss = new OSSClient(mContext, ud.getRegionName() + ".aliyuncs.com", credentialProvider, conf);
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uploadFile3(attachmentBean, ud);
                    }
                });
            }
        }).start(ThreadManager.getManager());
    }

    private File mfile;
    private String MD5Hash;
    private String fileName;
    private OSSAsyncTask ossAsyncTask=null;
    private void uploadFile3(final LineItem attachmentBean, final Uploadao ud) {
        Log.e("syncing---", attachmentBean.getUrl() + "   " + attachmentBean.getFileName());
        String path = attachmentBean.getUrl();
        mfile = new File(path);
        fileName = mfile.getName();
        MD5Hash = fieldNewPath + "/" + fileName;
        Log.e("syncing---", fileName + "   " + MD5Hash);
        String recordDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/oss_record/";
        File recordDir = new File(recordDirectory);
        // 要保证目录存在，如果不存在则主动创建
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }
        // 创建断点上传请求，参数中给出断点记录文件的保存位置，需是一个文件夹的绝对路径
        ResumableUploadRequest request = new ResumableUploadRequest(ud.getBucketName(),
                MD5Hash, path, recordDirectory);
        //设置false,取消时，不删除断点记录文件，如果不进行设置，默认true，是会删除断点记录文件，下次再进行上传时会重新上传。
        request.setDeleteUploadOnCancelling(false);
        request.setPartSize(1 * 100 * 1024);

        // 设置上传过程回调
        request.setProgressCallback(new OSSProgressCallback<ResumableUploadRequest>() {
            @Override
            public void onProgress(ResumableUploadRequest request, final long currentSize, final long totalSize) {
                Log.e("syncing---", currentSize + "   " + totalSize);
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uploadAudioPopupdate.setProgress2(totalSize, currentSize);
                    }
                });
            }
        });
        ossAsyncTask= oss.asyncResumableUpload(request, new OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult>() {
            @Override
            public void onSuccess(ResumableUploadRequest request, ResumableUploadResult result) {
                Log.e("syncing---", "  successsss");
                ((Activity)mContext). runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        yinxiangUploadNewFile(ud);
                    }
                });
            }
            @Override
            public void onFailure(ResumableUploadRequest request, ClientException clientExcepion, ServiceException serviceException) {
                Log.e("syncing---", "  failure"+clientExcepion.getMessage());
            }
        });
    }


    public void  cancelUploadAudio(){
        if(ossAsyncTask!=null){
            ossAsyncTask.cancel();
        }
        if(upload!=null){
            upload.abort();
        }
    }



    private void yinxiangUploadNewFile(final Uploadao ud) {

        ServiceInterfaceTools.getinstance().yinxiangUploadNewFile(AppConfig.URL_PUBLIC + "Soundtrack/UploadNewFile", ServiceInterfaceTools.YINXIANGUPLOADNEWFILE,
                meetingConfig.getLessionId()+"", meetingConfig.getDocument().getAttachmentID()+"", fileName, fieldId, soundtrackID, ud, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        uploadAudioListener.uploadAudioSuccess();
                        Log.e("henshupng","sensor");
//                        Tools.setSensor((Activity) mContext);
                        // 音想音频文件上传完了,如果没有调用这个方法,调用一下
                        ServiceInterfaceTools.getinstance().notifyUploaded(AppConfig.URL_LIVEDOC + "notifyUploaded", ServiceInterfaceTools.NOTIFYUPLOADED, ud, MD5Hash, new ServiceInterfaceListener() {
                            @Override
                            public void getServiceReturnData(Object object) {
                                EventBus.getDefault().post(new EventSyncSucc());
                            }
                        });
                    }
                }
        );
    }


}
