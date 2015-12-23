package com.example.ideanote.hideoradio.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.activities.MainActivity;

/**
 * Notification for donloading an episode
 */
public class EpisodeDownloadNotification {
    public static final int DOWNLOAD_NOTIFICATION_ID = 2000;
    private static final String ACTION_CANCEL = "action_cancel";

    public static void notify(Context context, Episode episode) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(DOWNLOAD_NOTIFICATION_ID, build(context, episode));
    }

    public static void notify(Context context, Notification notification) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(DOWNLOAD_NOTIFICATION_ID, notification);
    }

    public static void cancel(Context context, Episode episode) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(DOWNLOAD_NOTIFICATION_ID);
    }

    public static NotificationCompat.Builder createBuilder(Context context, Episode episode) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(android.R.drawable.stat_sys_download);
        builder.setContentTitle("Downloading...");
        builder.setContentText(episode.getTitle());
        builder.setOngoing(true);

        PendingIntent launchIntent = PendingIntent.getActivity(
                context, 0, MainActivity.createIntent(context, null), PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(launchIntent);

        return builder;
    }

    private static Notification build(Context context, Episode episode) {
        NotificationCompat.Builder builder = createBuilder(context, episode);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;

        return notification;
    }
}
