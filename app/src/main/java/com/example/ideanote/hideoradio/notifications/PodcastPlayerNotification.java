package com.example.ideanote.hideoradio.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.PodcastPlayer;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.activities.MainActivity;
import com.example.ideanote.hideoradio.services.PodcastPlayerService;

public class PodcastPlayerNotification {
    private final static int PLAYBACK_NOTIFICATION_ID = 1000;

    public final static String PLAY = "notification_play";
    public final static String PAUSE = "notification_pause";

    private final static String TAG = PodcastPlayerNotification.class.getSimpleName();

    private PodcastPlayerNotification() {
    }

    public static void notify(Context context, Episode episode, String kind) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        switch (kind) {
            case PLAY:
                manager.notify(PLAYBACK_NOTIFICATION_ID, buildPlayNotification(context, episode));
                break;
            case PAUSE:
                manager.notify(PLAYBACK_NOTIFICATION_ID, buildPauseNotification(context, episode));
                break;
            default:
                Log.i(TAG, "Invalid kind");
                break;
        }
    }

    public static void cancel(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(PLAYBACK_NOTIFICATION_ID);
    }

    private static PendingIntent createPauseIntent(Context context, Episode episode) {
        Intent intent = PodcastPlayerService.createPlayPauseIntent(context, episode);
        return PendingIntent.getService(context, 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static PendingIntent createStopIntent(Context context, Episode episode) {
        Intent intent = PodcastPlayerService.createStopIntent(context, episode);
        return PendingIntent.getService(context, 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static Notification buildPlayNotification(Context context, Episode episode) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_action_playback_play)
                .setContentTitle(episode.getTitle())
                .setContentText(episode.getDescription())
                .addAction(R.drawable.ic_action_playback_pause, "PAUSE", createPauseIntent(context, episode))
                .addAction(R.drawable.ic_action_cancel, "STOP", createStopIntent(context, episode))
                .setOngoing(true);

        PendingIntent intent = PendingIntent.getActivity(context, 0,
                MainActivity.createIntent(context, episode.getEpisodeId()), PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(intent);

        return builder.build();
    }

    private static Notification buildPauseNotification(Context context, Episode episode) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_action_playback_pause)
                .setContentTitle(episode.getTitle())
                .setContentText(episode.getDescription())
                .addAction(R.drawable.ic_action_playback_play, "PLAY", createPauseIntent(context, episode))
                .addAction(R.drawable.ic_action_cancel, "STOP", createStopIntent(context, episode));

        PendingIntent intent = PendingIntent.getActivity(context, 0,
                MainActivity.createIntent(context, episode.getEpisodeId()), PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(intent);

        return builder.build();
    }
}
