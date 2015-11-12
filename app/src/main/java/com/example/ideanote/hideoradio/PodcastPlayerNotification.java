package com.example.ideanote.hideoradio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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

    private static Notification buildPlayNotification(Context context, Episode episode) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_action_playback_play)
                .setContentTitle(episode.getTitle())
                .setContentText(episode.getDescription())
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
                .setContentText(episode.getDescription());

        PendingIntent intent = PendingIntent.getActivity(context, 0,
                MainActivity.createIntent(context, episode.getEpisodeId()), PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(intent);

        return builder.build();
    }
}
