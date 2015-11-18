package com.example.ideanote.hideoradio.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.PodcastPlayer;
import com.example.ideanote.hideoradio.notifications.PodcastPlayerNotification;

/**
 * Service for playing podcast media
 */
public class PodcastPlayerService extends IntentService {
    private final static String TAG = PodcastPlayerService.class.getSimpleName();

    private final static String EXTRA_EPISODE_ID = "extra_episode_id";

    private final static String ACTION_PLAY_AND_PAUSE = "action_play_and_pause";
    private final static String ACTION_STOP = "action_stop";

    private PodcastPlayer podcastPlayer;

    public PodcastPlayerService() {
        super("PodcastPlayerService");
    }

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
        Log.i(TAG, "onCreate");
        super.onCreate();

        podcastPlayer = PodcastPlayer.getInstance();
        // TODO: create notification if you need
    }

    /**
     * Process PodcastPlayer
     * start, pause, stop and so on.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent");
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
                } else if (podcastPlayer.isPaused()) {
                    podcastPlayer.start();
                    PodcastPlayerNotification.notify(getApplicationContext(), episode,
                            PodcastPlayerNotification.PLAY);
                } else if (podcastPlayer.isStopped()) {
                    podcastPlayer.start(getApplicationContext(), episode);
                    PodcastPlayerNotification.notify(getApplicationContext(), episode,
                            PodcastPlayerNotification.PLAY);
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
                } else {
                    Log.d("PodcastPlayerService", "Invalid action");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }
}
