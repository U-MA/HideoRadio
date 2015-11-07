package com.example.ideanote.hideoradio;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * Service for playing podcast media
 */
public class PodcastPlayerService extends IntentService {
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
        String action = intent.getAction();
        String episodeId = intent.getStringExtra(EXTRA_EPISODE_ID);
        Episode episode = Episode.findById(episodeId);

        switch (action) {
            case ACTION_PLAY_AND_PAUSE:
                if (podcastPlayer.isPlaying()) {
                    podcastPlayer.pause();
                } else if (podcastPlayer.isPaused()) {
                    podcastPlayer.start();
                } else if (podcastPlayer.isStopped()) {
                    podcastPlayer.start(getApplicationContext(), episode);
                } else {
                    Log.d("PodcastPlayerService", "Invalid action");
                }
                break;
            case ACTION_STOP:
                if (podcastPlayer.isStopped()) {
                    podcastPlayer.stop();
                    podcastPlayer.release();
                    // TODO: Notification
                } else {
                    Log.d("PodcastPlayerService", "Invalid action");
                }
                break;
            default:
                break;
        }
    }
}
