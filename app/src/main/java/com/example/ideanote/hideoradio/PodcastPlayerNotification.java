package com.example.ideanote.hideoradio;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

public class PodcastPlayerNotification {

    private PodcastPlayerNotification() {
    }

    public static void notify(Context context, Episode episode) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("This is sample.")
                .setContentText("Description");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1000, builder.build());
    }
}
