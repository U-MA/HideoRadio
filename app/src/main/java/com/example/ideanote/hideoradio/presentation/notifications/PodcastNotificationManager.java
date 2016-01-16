package com.example.ideanote.hideoradio.presentation.notifications;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.presentation.services.PodcastPlayerService;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeListActivity;

public class PodcastNotificationManager {

    private final static int NOTIFICATION_ID = 1000;
    private final static int REQUEST_CODE = 100;

    private PodcastPlayerService podcastPlayerService;

    private Episode episode;
    private NotificationManager managerCompat;

    public PodcastNotificationManager() {
    }

    public void startForeground() {
        Notification notification = createNotification();
        managerCompat =
                (NotificationManager) podcastPlayerService.getSystemService(
                        Context.NOTIFICATION_SERVICE);
        managerCompat.notify(NOTIFICATION_ID, notification);
        podcastPlayerService.startForeground(NOTIFICATION_ID, notification);
    }

    public void stopForeground() {
        // podcastPlayerService.stopForeground(false);
    }

    public void cancel() {
    }

    public void setService(PodcastPlayerService podcastPlayerService) {
        this.podcastPlayerService = podcastPlayerService;
    }

    private Notification createNotification() {
        episode = podcastPlayerService.getEpisode();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(podcastPlayerService)
                .setSmallIcon(R.drawable.ic_action_playback_play)
                .setContentTitle(episode.getTitle())
                .setContentText(episode.getDescription());
        setActions(builder);
        setContentIntent(builder);

        return  builder.build();
    }

    private void setActions(NotificationCompat.Builder builder) {
        if (podcastPlayerService.isPlaying()) {
            builder.addAction(R.drawable.ic_action_playback_pause, "PAUSE", createPauseIntent());
        } else {
            builder.addAction(R.drawable.ic_action_playback_play, "RESTART", createRestartIntent());
        }
        builder.addAction(R.drawable.ic_action_cancel, "STOP", createStopIntent());
    }

    private void setContentIntent(NotificationCompat.Builder builder) {
        PendingIntent intent = PendingIntent.getActivity(
                podcastPlayerService, 0,
                EpisodeListActivity.createIntent(podcastPlayerService, episode.getEpisodeId()),
                PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(intent);
    }

    private PendingIntent createPauseIntent() {
        Intent intent = PodcastPlayerService.createPauseIntent(podcastPlayerService);
        return PendingIntent.getService(podcastPlayerService, REQUEST_CODE,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private PendingIntent createRestartIntent() {
        Intent intent = PodcastPlayerService.createRestartIntent(podcastPlayerService);
        return PendingIntent.getService(podcastPlayerService, REQUEST_CODE,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private PendingIntent createStopIntent() {
        Intent intent = PodcastPlayerService.createStopIntent(podcastPlayerService);
        return PendingIntent.getService(podcastPlayerService, REQUEST_CODE,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
