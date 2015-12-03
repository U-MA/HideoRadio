package com.example.ideanote.hideoradio.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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

/**
 * Service for downloading an episode.
 */
public class EpisodeDownloadService extends IntentService {

    private static final String EXTRA_EPISODE_ID = "extra_episode_id";
    private static final int BUFFER_SIZE = 23 * 1024;

    private boolean isDown = false; // whether episode is downloading, or not.


    public static Intent createIntent(Context context, Episode episode) {
        Intent intent = new Intent(context, EpisodeDownloadService.class);

        /* これでうまくいくかはよくわかっていない
         * もしRebuildのようにするのであればEpisodeをParcelableにする必要がありそう */
        intent.putExtra(EXTRA_EPISODE_ID, episode.getEpisodeId());
        return intent;
    }

    public EpisodeDownloadService() {
        super("DownloadService");
    }

    /**
     * Whether an episode with episodeId is downloading.
     * @param episodeId
     * @return whether an episode is downloading, or not.
     */
    public boolean isDownloading(String episodeId) {
        return isDown;
    }

    /**
     * Download a specified episode.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        final Context context = getApplicationContext();

        String episodeId = intent.getStringExtra(EXTRA_EPISODE_ID);

        // episodeIdがnull, またはそのエピソードがdownload中であれば何もしない
        if (episodeId == null || isDownloading(episodeId)) {
            Log.i("DownloadService", "episodeId == null");
            return;
        }

        Episode episode = Episode.findById(episodeId);

        // Download済みであれば何もしない
        if (episode.isDownload()) {
            Log.i("EpisodeDownloadService", String.valueOf(episodeId) + " is already downloaded");
            return;
        }

        BufferedInputStream bufferedInputStream = null;
        FileOutputStream fileOutputStream = null;

        isDown = true;
        EpisodeDownloadNotification.notify(context, episode);
        try {
            Uri enclosure = episode.getEnclosure();
            if (enclosure == null) {
                return;
            }
            URL url = new URL(enclosure.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            /* episodeIdが.mp3で終わっていることを仮定 */
            int index = episodeId.lastIndexOf('/');
            String filename = episodeId.substring(index+1);
            Log.i("DownloadService", episodeId + " " + index);
            File destFile = new File(context.getExternalFilesDir(null), filename);

            InputStream inputStream = urlConnection.getInputStream();
            bufferedInputStream = new BufferedInputStream(inputStream, BUFFER_SIZE);
            fileOutputStream = new FileOutputStream(destFile);

            int max = urlConnection.getContentLength();
            int total = 0;
            int actual;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((actual = bufferedInputStream.read(buffer, 0, BUFFER_SIZE)) > 0) {
                total += actual;
                NotificationCompat.Builder builder = EpisodeDownloadNotification.createBuilder(context, episode);
                builder.setProgress(max, total, false);
                EpisodeDownloadNotification.notify(context, builder.build());
                fileOutputStream.write(buffer, 0, actual);
            }

            String externalFilePath = destFile.getPath();

            if (TextUtils.isEmpty(externalFilePath)) {
                Toast.makeText(this, "Download failed", Toast.LENGTH_LONG).show();
                Log.e("DownloadService", "TextUtil: Download failed: " + episodeId);
            } else {
                Log.i("DownloadService", "Download Complete");
                episode.setMediaLocalPath(externalFilePath);
                episode.save();
                BusHolder.getInstance().post(new EpisodeDownloadCompleteEvent());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Download failed", Toast.LENGTH_LONG).show();
            Log.e("DownloadService", "Malformed: Download failed: " + episodeId);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Download failed", Toast.LENGTH_LONG).show();
            Log.e("DownloadService", "IOException: Download failed: " + episodeId);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Download failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        isDown = false;
        EpisodeDownloadNotification.cancel(context, episode);
        EpisodeDownloadCompleteNotification.notify(context, episode);
    }
}
