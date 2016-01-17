package com.example.ideanote.hideoradio.presentation.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeDetailActivity;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeListActivity;

public class EpisodeDownloadCompleteNotification {
    public final static int DOWNLOAD_COMPLETE_NOTIFICATION_ID = 3000;


    public static NotificationCompat.Builder createBuilder(Context context, Episode episode) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_action_tick)
                .setContentTitle("Download completed")
                .setContentText(episode.getTitle())
                .setAutoCancel(true);

        Intent intent = new Intent(context, EpisodeDetailActivity.class);
        intent.putExtra(EpisodeListActivity.EXTRA_EPISODE_ID, episode.getEpisodeId());
        PendingIntent launchIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(launchIntent);

        return builder;
    }
}
