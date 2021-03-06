package com.example.ideanote.hideoradio.presentation.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.ideanote.hideoradio.HideoRadioApplication;
import com.example.ideanote.hideoradio.presentation.events.BusHolder;
import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.presentation.events.EpisodeDownloadCompleteEvent;
import com.example.ideanote.hideoradio.presentation.internal.di.ApplicationComponent;
import com.example.ideanote.hideoradio.presentation.notifications.EpisodeDownloadCompleteNotification;
import com.example.ideanote.hideoradio.presentation.notifications.EpisodeDownloadNotification;
import com.example.ideanote.hideoradio.presentation.presenter.EpisodeDownloadServicePresenter;

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

import javax.inject.Inject;

/**
 * Service for downloading an episode.
 */
public class EpisodeDownloadService extends Service implements IService {
    private static final String TAG = EpisodeDownloadService.class.getName();

    public static final String EXTRA_EPISODE_ID = "extra_episode_id";


    @Inject
    EpisodeDownloadServicePresenter episodeDownloadServicePresenter;

    public static Intent createIntent(Context context, Episode episode) {
        Intent intent = new Intent(context, EpisodeDownloadService.class);
        intent.putExtra(EXTRA_EPISODE_ID, episode.getEpisodeId());
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ApplicationComponent applicationComponent =
                ((HideoRadioApplication) getApplication()).getComponent();
        applicationComponent.inject(this);

        episodeDownloadServicePresenter.setService(this);
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
        episodeDownloadServicePresenter.onStartCommand(intent, flag, id);

        return START_STICKY;
    }

    @Override
    public File getExternalFilesDir() {
        return getExternalFilesDir(null);
    }

    @Override
    public void startForeground(Episode episode) {
        Notification notification =
                EpisodeDownloadNotification.createBuilder(getApplicationContext(), episode).build();
        startForeground(EpisodeDownloadNotification.DOWNLOAD_NOTIFICATION_ID, notification);
    }

    @Override
    public void stopForeground(Episode episode) {
        stopForeground(true);
        Notification notification =
                EpisodeDownloadCompleteNotification.createBuilder(getApplicationContext(), episode).build();
        NotificationManagerCompat.from(getApplicationContext()).notify(
                EpisodeDownloadCompleteNotification.DOWNLOAD_COMPLETE_NOTIFICATION_ID, notification);
    }

    @Override
    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
    }
}
