package com.example.ideanote.hideoradio.services;

import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.HideoRadioApplication;
import com.example.ideanote.hideoradio.Injector;
import com.example.ideanote.hideoradio.PodcastPlayer;
import com.example.ideanote.hideoradio.internal.di.ApplicationComponent;
import com.example.ideanote.hideoradio.notifications.PodcastPlayerNotification;

import javax.inject.Inject;

/**
 * Service for playing podcast media
 */
public class PodcastPlayerService extends Service {
    private final static String TAG = PodcastPlayerService.class.getSimpleName();

    private final static String EXTRA_EPISODE_ID = "extra_episode_id";

    private final static String ACTION_PLAY_AND_PAUSE = "action_play_and_pause";
    private final static String ACTION_STOP = "action_stop";

    @Inject
    PodcastPlayer podcastPlayer;

    public static Intent createPlayPauseIntent(Context context, Episode episode) {
        Intent intent = new Intent(context, PodcastPlayerService.class);
        intent.setAction(ACTION_PLAY_AND_PAUSE);
        intent.putExtra(EXTRA_EPISODE_ID, episode.getEpisodeId());

        return intent;
    }

    public static Intent createStopIntent(Context context, Episode episode) {
        Intent intent = new Intent(context, PodcastPlayerService.class);
        intent.setAction(ACTION_STOP);
        intent.putExtra(EXTRA_EPISODE_ID, episode.getEpisodeId());

        return intent;
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
        Log.i(TAG, "onStartCommand");

        if (intent != null) {

            String action = intent.getAction();
            String episodeId = intent.getStringExtra(EXTRA_EPISODE_ID);
            Episode episode = Episode.findById(episodeId);

            if (podcastPlayer.getEpisode() != null && !podcastPlayer.getEpisode().getEpisodeId().equals(episodeId)) {
                // 違うEpisodeを再生する必要がある
                podcastPlayer.stop();
                podcastPlayer.reset();
                Log.i(TAG, "different episode");
            }

            switch (action) {
                case ACTION_PLAY_AND_PAUSE:
                    if (podcastPlayer.isPlaying()) {
                        podcastPlayer.pause();
                        PodcastPlayerNotification.notify(getApplicationContext(), episode,
                                PodcastPlayerNotification.PAUSE);
                        stopForeground(false);
                    } else if (podcastPlayer.isPaused()) {
                        podcastPlayer.start();
                        PodcastPlayerNotification.notify(getApplicationContext(), episode,
                                PodcastPlayerNotification.PLAY);
                        startForeground(1000, PodcastPlayerNotification.buildPlayNotification(getApplicationContext(), episode));
                    } else if (podcastPlayer.isStopped()) {
                        podcastPlayer.start(getApplicationContext(), episode);
                        PodcastPlayerNotification.notify(getApplicationContext(), episode,
                                PodcastPlayerNotification.PLAY);
                        startForeground(1000, PodcastPlayerNotification.buildPlayNotification(getApplicationContext(), episode));
                    } else {
                        Log.d("PodcastPlayerService", "Invalid action");
                    }
                    break;
                case ACTION_STOP:
                    Log.i(TAG, "ACTION_STOP");
                    if (!podcastPlayer.isStopped()) {
                        podcastPlayer.stop();
                        podcastPlayer.release();
                        PodcastPlayerNotification.cancel(getApplicationContext());
                        stopService(createStopIntent(getApplicationContext(), episode));
                    } else {
                        Log.d("PodcastPlayerService", "Invalid action");
                    }
                    break;
                default:
                    break;
            }
        } else {
            Log.i(TAG, "podcastPlayer is playing?" + podcastPlayer.isPlaying());
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
}
