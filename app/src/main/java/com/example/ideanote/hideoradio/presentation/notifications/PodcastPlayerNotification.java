package com.example.ideanote.hideoradio.presentation.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeDetailActivity;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeListActivity;
import com.example.ideanote.hideoradio.presentation.services.PodcastPlayerService;

public class PodcastPlayerNotification {
    private final static int PLAYBACK_NOTIFICATION_ID = 1000;
    private final static int REQUEST_CODE = 0;

    private final static String PLAY_TITLE  = "PLAY";
    private final static String PAUSE_TITLE = "PAUSE";
    private final static String STOP_TITLE  = "STOP";

    private Context context;
    private String episodeId;
    private String title;
    private String text;

    public PodcastPlayerNotification(Context context) {
        this.context = context;
    }

    /**
     * Note: You must call this method before any createBuilder method.
     */
    public void initialize(Episode episode) {
        this.episodeId = episode.getEpisodeId();
        this.title = episode.getTitle();
        this.text = episode.getDescription();
    }

    public NotificationCompat.Builder createBuilderForPlaying() {
        Intent intentToLaunch = new Intent(context, EpisodeDetailActivity.class);
        intentToLaunch.putExtra(EpisodeListActivity.EXTRA_EPISODE_ID, episodeId);

        PendingIntent intent = PendingIntent.getActivity(
                context, REQUEST_CODE, intentToLaunch, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_action_playback_play)
                .setContentTitle(title)
                .setContentText(text)
                .addAction(createActionPause())
                .addAction(createActionStop())
                .setContentIntent(intent);
    }

    public NotificationCompat.Builder createBuilderForPaused() {
        Intent intentToLaunch = new Intent(context, EpisodeDetailActivity.class);
        intentToLaunch.putExtra(EpisodeListActivity.EXTRA_EPISODE_ID, episodeId);

        PendingIntent intent = PendingIntent.getActivity(
                context, REQUEST_CODE, intentToLaunch, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_action_playback_pause)
                .setContentTitle(title)
                .setContentText(text)
                .addAction(createActionRestart())
                .addAction(createActionStop())
                .setContentIntent(intent);
    }

    private NotificationCompat.Action createActionRestart() {
        Intent intent = PodcastPlayerService.createRestartIntent(context);
        PendingIntent pendingIntent =
                PendingIntent.getService(context, PLAYBACK_NOTIFICATION_ID,
                        intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return new NotificationCompat.Action(
                R.drawable.ic_action_playback_play, PLAY_TITLE, pendingIntent);
    }

    private NotificationCompat.Action createActionPause() {
        Intent intent = PodcastPlayerService.createPauseIntent(context);
        PendingIntent pendingIntent =
                PendingIntent.getService(context, PLAYBACK_NOTIFICATION_ID,
                        intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return new NotificationCompat.Action(
                R.drawable.ic_action_playback_pause, PAUSE_TITLE, pendingIntent);
    }

    private NotificationCompat.Action createActionStop() {
        Intent intent = PodcastPlayerService.createStopIntent(context);
        PendingIntent pendingIntent =
                PendingIntent.getService(context, PLAYBACK_NOTIFICATION_ID,
                        intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return new NotificationCompat.Action(
                R.drawable.ic_action_cancel, STOP_TITLE, pendingIntent);
    }
}
