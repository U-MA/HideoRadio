package com.example.ideanote.hideoradio;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class PodcastPlayerNotification {

    private PodcastPlayerNotification() {
    }

    public static void notify(Context context, Episode episode) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_action_playback_play)
                .setContentTitle(episode.getTitle())
                .setContentText(episode.getDescription());

        PendingIntent intent = PendingIntent.getActivity(context, 0,
                MainActivity.createIntent(context, episode.getEpisodeId()), PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(intent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1000, builder.build());
    }
}
