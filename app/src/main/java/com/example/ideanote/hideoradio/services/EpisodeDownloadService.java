package com.example.ideanote.hideoradio.services;

import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.example.ideanote.hideoradio.events.BusHolder;
import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.events.EpisodeDownloadCompleteEvent;
import com.example.ideanote.hideoradio.notifications.EpisodeDownloadCompleteNotification;
import com.example.ideanote.hideoradio.notifications.EpisodeDownloadNotification;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for downloading an episode.
 */
public class EpisodeDownloadService extends Service {

    private static final String TAG = EpisodeDownloadService.class.getName();

    private static final String EXTRA_EPISODE_ID = "extra_episode_id";
    private static final int BUFFER_SIZE = 23 * 1024;

    private static List<Episode> downloadingList = new ArrayList<>();

    private static boolean isCancel;
    private static Episode episode;
    private static Context context;

    public static Intent createIntent(Context context, Episode episode) {
        Intent intent = new Intent(context, EpisodeDownloadService.class);

        /* これでうまくいくかはよくわかっていない
         * もしRebuildのようにするのであればEpisodeをParcelableにする必要がありそう */
        intent.putExtra(EXTRA_EPISODE_ID, episode.getEpisodeId());
        return intent;
    }

    /**
     * Whether an episode with episodeId is downloading.
     * @param episodeId
     * @return whether an episode is downloading, or not.
     */
    public static boolean isDownloading(String episodeId) {
        Episode e = Episode.findById(episodeId);
        for (Episode episode : downloadingList) {
            if (e.isEquals(episode)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Download a specified episode.
     */
    @Override
    public int onStartCommand(Intent intent, int flag, int id) {
        isCancel = false;
        context = getApplicationContext();

        String episodeId = intent.getStringExtra(EXTRA_EPISODE_ID);

        // episodeIdがnull, またはそのエピソードがdownload中であれば何もしない
        if (episodeId == null || isDownloading(episodeId)) {
            Log.i(TAG, "episodeId == null");
            stopSelf();
            return START_STICKY;
        }

        episode = Episode.findById(episodeId);

        // Download済みであれば何もしない
        if (episode.isDownload()) {
            Log.i(TAG, String.valueOf(episodeId) + " is already downloaded");
            stopSelf();
            return START_STICKY;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedInputStream bufferedInputStream;
                FileOutputStream fileOutputStream;
                // startForeground(); // Create download notification
                try {
                    Uri enclosure = episode.getEnclosure();
                    if (enclosure == null) {
                        return;
                    }
                    URL url = new URL(enclosure.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    /* episodeIdが.mp3で終わっていることを仮定 */
                    final String episodeId = episode.getEpisodeId();
                    int index = episodeId.lastIndexOf('/');
                    String filename = episodeId.substring(index+1);
                    Log.i(TAG, episodeId + " " + index);
                    File destFile = new File(context.getExternalFilesDir(null), filename);

                    InputStream inputStream = urlConnection.getInputStream();
                    bufferedInputStream = new BufferedInputStream(inputStream, BUFFER_SIZE);
                    fileOutputStream = new FileOutputStream(destFile);

                    int max = urlConnection.getContentLength();
                    int actual, total = 0, progress = 0;
                    final int fivePercent = (int) (max * 0.05);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while ((actual = bufferedInputStream.read(buffer, 0, BUFFER_SIZE)) > 0) {
                        total += actual;
                        fileOutputStream.write(buffer, 0, actual);

                        if (total > progress) {
                            NotificationCompat.Builder builder = EpisodeDownloadNotification.createBuilder(context, episode);
                            builder.setProgress(max, total, false);
                            EpisodeDownloadNotification.notify(context, builder.build());
                            progress += fivePercent;
                        }

                        if (isCancel) {
                            break;
                        }
                    }

                    String externalFilePath = destFile.getPath();

                    if (TextUtils.isEmpty(externalFilePath)) {
                        Log.e(TAG, "TextUtil: Download failed: " + episodeId);
                    } else {
                        Log.i(TAG, "Download Complete");
                        episode.setMediaLocalPath(externalFilePath);
                        episode.save();
                        BusHolder.getInstance().post(new EpisodeDownloadCompleteEvent());
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // stopForeground();
                EpisodeDownloadNotification.cancel(context, episode);
                EpisodeDownloadCompleteNotification.notify(context, episode);
            }
        }).start();

        return START_STICKY;
    }

    public static void cancel(Context context, Episode episode) {
        Log.i(TAG, "cancel");
        removeEpisodeFromDownloadingList(episode);
        EpisodeDownloadNotification.cancel(context, episode);
        isCancel = true;
    }

    /**
     * downloadingListから指定されたエピソードを削除する
     *
     * @param episode
     */
    private static void removeEpisodeFromDownloadingList(Episode episode) {
        for (Episode e : downloadingList) {
            if (episode.isEquals(e)) {
                downloadingList.remove(e);
            }
        }
    }
}
