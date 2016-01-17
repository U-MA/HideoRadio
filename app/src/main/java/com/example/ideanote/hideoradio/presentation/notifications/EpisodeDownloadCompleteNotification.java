package com.example.ideanote.hideoradio.presentation.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeListActivity;

public class EpisodeDownloadCompleteNotification {
    private final static int DOWNLOAD_COMPLETE_NOTIFICATION_ID = 3000;


    public static NotificationCompat.Builder createBuilder(Context context, Episode episode) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Download completed")
                .setContentText(episode.getTitle());

        // TODO: launch EpisodeDetailActivity
        PendingIntent launchIntent = PendingIntent.getActivity(
                context, 0, EpisodeListActivity.createIntent(context, null), PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(launchIntent);

        return builder;
    }
}
