package com.example.ideanote.hideoradio;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MediaBarView extends FrameLayout {

    private View rootView;
    private TextView episodeTitleTextView;
    private Button playAndStopButton;
    private Button exitButton;

    public MediaBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("MediaBarView", "Constructor");

        rootView = View.inflate(getContext(), R.layout.media_bar_view, null);
        episodeTitleTextView = (TextView) rootView.findViewById(R.id.episode_title_text);
        playAndStopButton = (Button) rootView.findViewById(R.id.play_and_pause);
        exitButton = (Button) rootView.findViewById(R.id.exit_button);
        addView(rootView);
    }

    public void setEpisode(Episode episode) {
        PodcastPlayer podcastPlayer = PodcastPlayer.getInstance();
        if (episode == null ||
                (!podcastPlayer.isPlaying() && !podcastPlayer.isPaused())) {
            rootView.setVisibility(View.GONE);
            return;
        }
        Log.i("MediaBarView.setEpisode", episode.getTitle());

        show(episode);
    }

    public void show(Episode episode) {
        episodeTitleTextView.setText(episode.getTitle());
        setMediaPlayAndPauseButton();
        setExitButton();

        rootView.setVisibility(View.VISIBLE);
    }

    public void setMediaPlayAndPauseButton() {
        playAndStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PodcastPlayer podcastPlayer = PodcastPlayer.getInstance();
                if (podcastPlayer.isPlaying()) {
                    podcastPlayer.pause();
                } else {
                    podcastPlayer.start();
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
                    // TODO: PodcastPlayerをメモリから削除
                }
            }
        });
    }
}

