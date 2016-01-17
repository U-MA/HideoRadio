package com.example.ideanote.hideoradio.presentation.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeListActivity;

/**
 * Notification for donloading an episode
 */
public class EpisodeDownloadNotification {
    public static final int DOWNLOAD_NOTIFICATION_ID = 2000;


    public static NotificationCompat.Builder createBuilder(Context context, Episode episode) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(android.R.drawable.stat_sys_download);
        builder.setContentTitle("Downloading...");
        builder.setContentText(episode.getTitle());
        builder.setOngoing(true);

        // TODO: launch EpisodeDetailActivity
        PendingIntent launchIntent = PendingIntent.getActivity(
                context, 0, EpisodeListActivity.createIntent(context, null), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(launchIntent);

        return builder;
    }
}
