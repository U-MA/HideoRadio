package com.example.ideanote.hideoradio;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ideanote.hideoradio.notifications.PodcastPlayerNotification;

public class MediaBarView extends FrameLayout {
    private static String TAG = MediaBarView.class.getSimpleName();

    private View rootView;
    private TextView episodeTitleTextView;
    private ImageButton playAndStopButton;
    private ImageButton exitButton;

    public MediaBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "Constructor");

        rootView = View.inflate(getContext(), R.layout.media_bar_view, null);
        episodeTitleTextView = (TextView) rootView.findViewById(R.id.episode_title_text);
        playAndStopButton = (ImageButton) rootView.findViewById(R.id.play_and_pause);
        exitButton = (ImageButton) rootView.findViewById(R.id.exit_button);
        addView(rootView);
    }

    public void setEpisode(Episode episode) {
        PodcastPlayer podcastPlayer = PodcastPlayer.getInstance();
        if (episode == null ||
                (!podcastPlayer.isPlaying() && !podcastPlayer.isPaused())) {
            Log.i(TAG, "View.GONE");
            rootView.setVisibility(View.GONE);
            return;
        }
        Log.i(TAG, episode.getTitle());

        show(episode);
    }

    public void show(Episode episode) {
        episodeTitleTextView.setText(episode.getTitle());
        episodeTitleTextView.setSelected(true);
        setMediaPlayAndPauseButton();
        setExitButton();

        rootView.setVisibility(View.VISIBLE);
    }

    public void setMediaPlayAndPauseButton() {
        playAndStopButton.setImageResource(
                PodcastPlayer.getInstance().isPlaying() ?
                R.drawable.ic_action_playback_pause :
                R.drawable.ic_action_playback_play);

        playAndStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PodcastPlayer podcastPlayer = PodcastPlayer.getInstance();
                if (podcastPlayer.isPlaying()) {
                    podcastPlayer.pause();
                    PodcastPlayerNotification.notify(getContext(), podcastPlayer.getEpisode(),
                            PodcastPlayerNotification.PAUSE);
                    playAndStopButton.setImageResource(R.drawable.ic_action_playback_play);
                } else {
                    podcastPlayer.start();
                    PodcastPlayerNotification.notify(getContext(), podcastPlayer.getEpisode(),
                            PodcastPlayerNotification.PLAY);
                    playAndStopButton.setImageResource(R.drawable.ic_action_playback_pause);
                }
            }
        });
    }

    public void setExitButton() {
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PodcastPlayer podcastPlayer = PodcastPlayer.getInstance();
                if (podcastPlayer.isPlaying() || podcastPlayer.isPaused()) {
                    podcastPlayer.stop();
                    rootView.setVisibility(View.GONE);
                    PodcastPlayerNotification.cancel(getContext());
                    // TODO: PodcastPlayerをメモリから削除
                }
            }
        });
    }
}

