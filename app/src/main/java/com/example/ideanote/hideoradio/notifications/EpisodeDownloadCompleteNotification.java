package com.example.ideanote.hideoradio.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.activities.MainActivity;

public class EpisodeDownloadCompleteNotification {
    private final static int DOWNLOAD_COMPLETE_NOTIFICATION_ID = 3000;

    /**
     * notify EpisodeDownloadNotification
     *
     * @param context
     * @param episode an episode downloaded now
     */
    public static void notify(Context context, Episode episode) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(DOWNLOAD_COMPLETE_NOTIFICATION_ID, build(context, episode));
    }

    private static Notification build(Context context, Episode episode) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_action_tick)
                .setContentTitle("Download Completed")
                .setContentText(episode.getTitle());

        PendingIntent launchIntent = PendingIntent.getActivity(
                context, 0, MainActivity.createIntent(context, null), PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(launchIntent);

        return builder.build();
    }
}
