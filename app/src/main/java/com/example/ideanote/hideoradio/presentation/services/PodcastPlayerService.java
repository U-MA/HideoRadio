package com.example.ideanote.hideoradio.presentation.services;

import android.app.Service;
import android.app.usage.NetworkStatsManager;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.HideoRadioApplication;
import com.example.ideanote.hideoradio.data.repository.EpisodeDataRepository;
import com.example.ideanote.hideoradio.domain.repository.EpisodeRepository;
import com.example.ideanote.hideoradio.presentation.UIThread;
import com.example.ideanote.hideoradio.presentation.events.BusHolder;
import com.example.ideanote.hideoradio.presentation.events.EpisodeCompleteEvent;
import com.example.ideanote.hideoradio.presentation.events.PodcastPlayerStateChangedEvent;
import com.example.ideanote.hideoradio.presentation.media.PodcastPlayer;
import com.example.ideanote.hideoradio.presentation.internal.di.ApplicationComponent;
import com.example.ideanote.hideoradio.presentation.notifications.PodcastPlayerNotification;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Subscriber;

/**
 * Service for playing podcast media
 */
@Singleton
public class PodcastPlayerService extends Service {
    private final static String TAG = PodcastPlayerService.class.getName();

    public final static String EXTRA_EPISODE_ID = "com.example.ideanote.hideoradio.extra_episode_id";

    public final static String ACTION_START   = "com.example.ideanote.hideoradio.start";
    public final static String ACTION_RESTART = "com.example.ideanote.hideoradio.restart";
    public final static String ACTION_PAUSE   = "com.example.ideanote.hideoradio.pause";
    private final static String ACTION_STOP    = "com.example.ideanote.hideoradio.stop";

    private final static int NOTIFICATION_ID = 5555;

    @Inject
    PodcastPlayer podcastPlayer;

    @Inject
    EpisodeRepository episodeDataRepository;

    PodcastPlayerNotification podcastPlayerNotification;

    NotificationManagerCompat notificationManagerCompat;

    public static Intent createStartIntent(Context context, String episodeId) {
        Intent intent = new Intent(context, PodcastPlayerService.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_EPISODE_ID, episodeId);

        return intent;
    }

    public static Intent createRestartIntent(Context context) {
        Intent intent = new Intent(context, PodcastPlayerService.class);
        intent.setAction(ACTION_RESTART);

        return intent;
    }

    public static Intent createPauseIntent(Context context) {
        Intent intent = new Intent(context, PodcastPlayerService.class);
        intent.setAction(ACTION_PAUSE);

        return intent;
    }

    public static Intent createStopIntent(Context context) {
        Intent intent = new Intent(context, PodcastPlayerService.class);
        intent.setAction(ACTION_STOP);

        return intent;
    }

    /**
     * Instance PodcastPlayer
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        ApplicationComponent applicationComponent =
                ((HideoRadioApplication) getApplicationContext()).getComponent();
        Log.i(TAG, applicationComponent.toString());
        applicationComponent.inject(this);

        podcastPlayer.setService(this);

        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        podcastPlayerNotification = new PodcastPlayerNotification(getApplicationContext());

        BusHolder.getInstance().register(this);
    }

    /**
     * Process PodcastPlayer
     * start, pause, stop and so on.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int id) {
        if (intent == null) {
            throw new IllegalArgumentException("intent must not be null");
        }

        String action = intent.getAction();
        final String episodeId = intent.getStringExtra(EXTRA_EPISODE_ID);

        switch (action) {
            case ACTION_START:
                Log.i(TAG, ACTION_START);
                Log.i(TAG, episodeDataRepository.toString());
                episodeDataRepository.episode(episodeId)
                        .subscribeOn(new UIThread().getScheduler())
                        .observeOn(new UIThread().getScheduler())
                        .subscribe(new Subscriber<Episode>() {
                            @Override
                            public void onCompleted() {
                                // do nothing
                            }

                            @Override
                            public void onError(Throwable e) {
                                // do nothing
                            }

                            @Override
                            public void onNext(Episode episode) {
                                Log.i(PodcastPlayerService.class.getSimpleName(), "onNext");
                                Log.e(PodcastPlayerService.class.getSimpleName(), "" + (episode != null));
                                Log.i(PodcastPlayerService.class.getSimpleName(), episode.toString());
                                if (!episode.isDownloaded()) {
                                    ConnectivityManager connectivityManager =
                                            (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
                                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                                    if (networkInfo == null || !networkInfo.isConnected()) {
                                        Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                                start(episodeId);
                                playNotificationNotify(episodeId);
                            }
                        });
                break;
            case ACTION_RESTART:
                Log.i(TAG, ACTION_RESTART);
                restart();
                restartNotificationNotify(episodeId);
                break;
            case ACTION_PAUSE:
                Log.i(TAG, ACTION_PAUSE);
                pause();
                pausedNotificationNotify(episodeId);
                break;
            case ACTION_STOP:
                Log.i(TAG, ACTION_STOP);
                stop();
                cancelNotification();
                break;
        }
        BusHolder.getInstance().post(new PodcastPlayerStateChangedEvent());

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();

        BusHolder.getInstance().unregister(this);
    }

    private void playNotificationNotify(String episodeId) {
        Episode episode = Episode.findById(episodeId); // TODO: Use Repository

        podcastPlayerNotification.initialize(episode);

        Log.i(TAG, "startForeground");
        startForeground(NOTIFICATION_ID, podcastPlayerNotification.createBuilderForPlaying().build());
    }

    private void restartNotificationNotify(String episodeId) {
        Log.i(TAG, "startForeground");
        startForeground(NOTIFICATION_ID, podcastPlayerNotification.createBuilderForPlaying().build());
    }

    private void pausedNotificationNotify(String episodeId) {
        Log.i(TAG, "stopForeground");
        stopForeground(false);
        notificationManagerCompat.notify(NOTIFICATION_ID,
                podcastPlayerNotification.createBuilderForPaused().build());
    }

    private void cancelNotification() {
        notificationManagerCompat.cancel(NOTIFICATION_ID);
    }

    /**
     * Start a new episode.
     *
     * @param episodeId An episode id to newly play.
     */
    private void start(String episodeId) {
        if (podcastPlayer.getEpisode() != null && !podcastPlayer.getEpisode().getEpisodeId().equals(episodeId)) {
            // 違うEpisodeを再生する必要がある
            podcastPlayer.stop();
            podcastPlayer.reset();
            Log.i(TAG, "different episode");
        }

        Episode episode = Episode.findById(episodeId);
        podcastPlayer.start(getApplicationContext(), episode);
    }

    /**
     * Restart a previous episode.
     */
    private void restart() {
        podcastPlayer.restart();
    }

    /**
     * Pause a playing episode.
     */
    private void pause() {
        podcastPlayer.pause();
    }

    /**
     * Stop a episode.
     */
    private void stop() {
        podcastPlayer.stop();
        podcastPlayer.release();
        stopSelf();
    }

    public boolean isPlaying() {
        return podcastPlayer.isPlaying();
    }

    public Episode getEpisode() {
        return podcastPlayer.getEpisode();
    }

    @Subscribe
    public void onEpisodeComplete(final EpisodeCompleteEvent event) {
        stopForeground(true);
    }
}
