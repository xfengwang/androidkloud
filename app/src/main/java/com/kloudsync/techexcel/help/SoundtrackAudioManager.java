package com.kloudsync.techexcel.help;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.kloudsync.techexcel.bean.EventCloseSoundtrack;
import com.kloudsync.techexcel.bean.SoundtrackMediaInfo;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Uploadao;
import com.kloudsync.techexcel.tool.DocumentModel;
import com.kloudsync.techexcel.tool.SoundtrackAudioCache;
import com.ub.techexcel.tools.DownloadUtil;
import com.ub.techexcel.tools.FileUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class SoundtrackAudioManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static SoundtrackAudioManager instance;
    private MediaPlayer audioPlayer;
    private volatile long playTime;
    private Context context;
    private SoundtrackMediaInfo mediaInfo;

    private SoundtrackAudioManager(Context context) {
        this.context = context;

    }

    public static SoundtrackAudioManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SoundtrackAudioManager.class) {
                if (instance == null) {
                    instance = new SoundtrackAudioManager(context);
                }
            }
        }
        return instance;
    }

    public void setSoundtrackAudio(SoundtrackMediaInfo mediaInfo) {
        Log.e("check_play", "mediaInfo:" + mediaInfo);
        this.mediaInfo = mediaInfo;
        if (mediaInfo == null || this.mediaInfo.isPreparing()) {
            return;
        }
        this.mediaInfo.setPreparing(true);
        predownSoundtrackAudio(context, mediaInfo.getAttachmentUrl());
        prepareAudioAndPlay(mediaInfo);
    }

    public SoundtrackMediaInfo getMediaInfo() {
        return mediaInfo;
    }

    public void prepareAudioAndPlay(final SoundtrackMediaInfo audioData) {

        Observable.just("play").observeOn(Schedulers.io()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                audioPlayer = new MediaPlayer();
                try {
                    try {
                        if (audioPlayer.isPlaying()) {
                            return;
                        }
                    } catch (IllegalStateException exception) {

                    }

                    audioPlayer.setOnPreparedListener(SoundtrackAudioManager.this);
                    audioPlayer.setOnCompletionListener(SoundtrackAudioManager.this);
                    audioPlayer.setOnErrorListener(SoundtrackAudioManager.this);
                    Uri uri = null;
                    if (audioCache.containFile(audioData.getAttachmentUrl())) {
                        File file = new File(audioCache.getAudioPath(audioData.getAttachmentUrl()));
                        if (file.exists()) {
//                            audioPlayer.setDataSource(file.getAbsolutePath());
                            audioPlayer.setDataSource(context, Uri.parse(audioData.getAttachmentUrl()));
                        } else {
                            audioPlayer.setDataSource(context, Uri.parse(audioData.getAttachmentUrl()));
                        }

                    } else {
                        audioPlayer.setDataSource(context, Uri.parse(audioData.getAttachmentUrl()));
                    }

//                    audioPlayer.setDataSource(context, Uri.parse(URLDecoder.decode(audioData.getAttachmentUrl(),"UTF-8")));
                    audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                    try {
                        Log.e("check_prepare_soundtrack", "url:" + audioData.getAttachmentUrl());
                        audioPlayer.prepare();
                    } catch (IllegalStateException e) {
                        Log.e("check_prepare_soundtrack", "IllegalStateException:" + e.getMessage());
                        reinit(audioData);
                    }
                    Log.e("check_prepare_soundtrack", "player start");
                    audioPlayer.start();
                    audioData.setPreparing(false);
                    audioData.setPrepared(true);

                } catch (IOException e) {
                    e.printStackTrace();
                    audioData.setPreparing(false);
                }
            }
        });

    }

    private void reinit(SoundtrackMediaInfo mediaInfo) {

        audioPlayer = null;
        audioPlayer = new MediaPlayer();
        try {
            audioPlayer.reset();
            audioPlayer.setDataSource(context, Uri.parse(mediaInfo.getAttachmentUrl()));
            audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            audioPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mediaInfo == null) {
            return;
        }
        Log.e("check_play", "onPrepared");
        if (mediaInfo != null) {
            Log.e("check_play", "on prepared,id:" + mediaInfo.getAttachmentUrl());
            mp.start();

        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.reset();
        EventBus.getDefault().post(new EventCloseSoundtrack());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public boolean isPlaying() {
        if (mediaInfo == null) {
            return false;
        }
        if (audioPlayer != null) {
            return audioPlayer.isPlaying();
        }

        return false;
    }

    public long getPlayTime() {
        if (mediaInfo == null) {
            return 0;
        }
        if (audioPlayer == null) {
            return 0;
        }

        return audioPlayer.getCurrentPosition();
    }

    public long getTotalTime() {
        if (mediaInfo == null) {
            return 0;
        }
        return audioPlayer.getDuration();
    }


    public void release() {
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer.reset();
            audioPlayer.release();
            audioPlayer = null;
        }

        mediaInfo = null;
        instance = null;
    }

    public long getDuration() {
        if (mediaInfo == null) {
            return 0;
        }
        return audioPlayer.getDuration();
    }

    public void pause() {
        if (mediaInfo == null) {
            return;
        }
        if (audioPlayer != null) {
            audioPlayer.pause();
        }
    }

    public void restart() {
        if (mediaInfo == null) {
            return;
        }
        if (audioPlayer != null) {
            audioPlayer.start();
        }
    }

    public void seekTo(int time) {
        if (mediaInfo == null) {
            return;
        }
        if (audioPlayer != null) {
            audioPlayer.seekTo(time);
            Log.e("vedio_check", "seek_to,time:" + time);
        }
    }

    private SoundtrackAudioCache audioCache;

    private void queryDocumentAndDownLoad(final String url, final String savePath) throws MalformedURLException {
        Observable.just(url).observeOn(Schedulers.io()).map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                String newUrl = url;
                URL _url = new URL(url);
                Log.e("check_url_path", _url.getPath());
                String path = _url.getPath();
                if (!TextUtils.isEmpty(path)) {

                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }

                    int index = path.lastIndexOf("/");
                    if (index >= 0 && index < path.length()) {
                        String centerPart = path.substring(0, index);
                        String fileName = path.substring(index + 1, path.length());
                        Log.e("check_transform_url", "centerPart:" + centerPart + ",fileName:" + fileName);
                        if (!TextUtils.isEmpty(centerPart)) {
                            JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
                                    centerPart);
                            if (queryDocumentResult != null) {
                                Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
                                String part = "";
                                if (uploadao != null) {
                                    if (1 == uploadao.getServiceProviderId()) {
                                        part = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + centerPart
                                                + "/" + fileName;
                                    } else if (2 == uploadao.getServiceProviderId()) {
                                        part = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + centerPart + "/" + fileName;
                                    }
                                    newUrl = part;
                                    Log.e("check_transform_url", "url:" + url);
                                }

                            }
                        }
                    }
                }

                return newUrl;
            }
        }).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String _newUrl) throws Exception {
                safeDownloadFile(_newUrl, savePath, true);
            }
        }).subscribe();


    }

    private synchronized void safeDownloadFile(final String url, final String savePath, final boolean needRedownload) {
        audioCache = SoundtrackAudioCache.getInstance(context);
        final ThreadLocal<String> localPage = new ThreadLocal<>();
        localPage.set(url);
        Log.e("safeDownloadFile", "start_download");
//      DownloadUtil.get().cancelAll();
        DownloadUtil.get().syncDownload(localPage.get(), savePath, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(int code) {

                Log.e("safeDownloadFile", "onDownloadSuccess:" + localPage.get());
                audioCache.cacheAudio(localPage.get(), savePath);

            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                Log.e("safeDownloadFile", "onDownloadFailed:" + localPage.get());
                if (needRedownload) {
                    safeDownloadFile(url, savePath, false);
                }
            }
        });
    }

    public void predownSoundtrackAudio(Context context, String url) {

        audioCache = SoundtrackAudioCache.getInstance(context);
        FileUtils.createAudioFilesDir(context);

        if (!TextUtils.isEmpty(url)) {
            Observable.just(url).observeOn(Schedulers.io()).doOnNext(new Consumer<String>() {
                @Override
                public void accept(String _url) throws Exception {
                    if (_url instanceof String) {
                        String url = (String) _url;
                        if (!TextUtils.isEmpty(url)) {
                            if (audioCache.containFile(url)) {
                                String path = audioCache.getAudioPath(url);
                                File localFile = new File(path);
                                if (localFile.exists()) {
                                    return;
                                } else {
                                    audioCache.removeFile(url);
                                }
                            }

                            String _path = FileUtils.getBaseAudiosDir();
                            File dir = new File(_path);
                            String name = mediaInfo.getItemID() + "_" + url.substring(url.lastIndexOf("/"), url.length());
                            File audioFile = new File(dir, name);
                            queryDocumentAndDownLoad(url, audioFile.getAbsolutePath());

                        }
                    }
                }
            }).subscribe();


        }
    }

    private Uploadao parseQueryResponse(final String jsonstring) {
        try {
            JSONObject returnjson = new JSONObject(jsonstring);
            if (returnjson.getBoolean("Success")) {
                JSONObject data = returnjson.getJSONObject("Data");

                JSONObject bucket = data.getJSONObject("Bucket");
                Uploadao uploadao = new Uploadao();
                uploadao.setServiceProviderId(bucket.getInt("ServiceProviderId"));
                uploadao.setRegionName(bucket.getString("RegionName"));
                uploadao.setBucketName(bucket.getString("BucketName"));
                return uploadao;
            }
        } catch (JSONException e) {
            return null;
        }
        return null;
    }


}
