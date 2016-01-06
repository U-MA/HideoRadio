package com.example.ideanote.hideoradio.services;

import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.Injector;
import com.example.ideanote.hideoradio.PodcastPlayer;
import com.example.ideanote.hideoradio.internal.di.ApplicationComponent;
import com.example.ideanote.hideoradio.notifications.PodcastNotificationManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Service for playing podcast media
 */
@Singleton
public class PodcastPlayerService extends Service {
    private final static String TAG = PodcastPlayerService.class.getName();

    private final static String EXTRA_EPISODE_ID = "com.example.ideanote.hideoradio.extra_episode_id";

    private final static String ACTION_START   = "com.example.ideanote.hideoradio.start";
    private final static String ACTION_RESTART = "com.example.ideanote.hideoradio.restart";
    private final static String ACTION_PAUSE   = "com.example.ideanote.hideoradio.pause";
    private final static String ACTION_STOP    = "com.example.ideanote.hideoradio.stop";

    private final static String ACTION_PLAY_AND_PAUSE = "action_play_and_pause"; // deprecated

    @Inject
    PodcastPlayer podcastPlayer;

    PodcastNotificationManager podcastNotificationManager;

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

    @Deprecated
    public static Intent createPlayPauseIntent(Context context, Episode episode) {
        Intent intent = new Intent(context, PodcastPlayerService.class);
        intent.setAction(ACTION_PLAY_AND_PAUSE);
        intent.putExtra(EXTRA_EPISODE_ID, episode.getEpisodeId());

        return intent;
    }

    public void setManager(PodcastNotificationManager podcastNotificationManager) {
        this.podcastNotificationManager = podcastNotificationManager;
    }

    /**
     * Instance PodcastPlayer
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        ApplicationComponent applicationComponent = Injector.obtain(getApplication());
        applicationComponent.inject(this);

        podcastPlayer.setService(this);
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
        String episodeId = intent.getStringExtra(EXTRA_EPISODE_ID);

        switch (action) {
            case ACTION_START:
                start(episodeId);
                break;
            case ACTION_RESTART:
                restart();
                break;
            case ACTION_PAUSE:
                pause();
                break;
            case ACTION_STOP:
                stop();
                break;
        }

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
        podcastNotificationManager.startForeground();
    }

    /**
     * Restart a previous episode.
     */
    private void restart() {
        podcastPlayer.restart();
        podcastNotificationManager.startForeground();
    }

    /**
     * Pause a playing episode.
     */
    private void pause() {
        podcastPlayer.pause();
        podcastNotificationManager.stopForeground();
    }

    /**
     * Stop a episode.
     */
    private void stop() {
        podcastPlayer.stop();
        podcastPlayer.release();
        podcastNotificationManager.cancel();
        stopSelf();
    }

    public boolean isPlaying() {
        return podcastPlayer.isPlaying();
    }

    public Episode getEpisode() {
        return podcastPlayer.getEpisode();
    }
}
