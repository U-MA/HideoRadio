package com.example.ideanote.hideoradio.presentation.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.HideoRadioApplication;
import com.example.ideanote.hideoradio.presentation.internal.di.ApplicationComponent;
import com.example.ideanote.hideoradio.presentation.media.PodcastPlayer;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.presentation.notifications.PodcastPlayerNotification;

public class MediaBarView extends FrameLayout {
    private static String TAG = MediaBarView.class.getSimpleName();

    private View rootView;
    private TextView episodeTitleTextView;
    private ImageButton playAndStopButton;
    private ImageButton exitButton;

    private PodcastPlayer podcastPlayer;

    public MediaBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "Constructor");

        rootView = View.inflate(getContext(), R.layout.media_bar_view, null);
        episodeTitleTextView = (TextView) rootView.findViewById(R.id.episode_title_text);
        playAndStopButton = (ImageButton) rootView.findViewById(R.id.play_and_pause);
        exitButton = (ImageButton) rootView.findViewById(R.id.exit_button);
        addView(rootView);

        ApplicationComponent applicationComponent =
                ((HideoRadioApplication) context.getApplicationContext()).getComponent();

        podcastPlayer = applicationComponent.podcastPlayer();
    }

    public void show(Episode episode) {
        episodeTitleTextView.setText(episode.getTitle());
        episodeTitleTextView.setSelected(true);
        setMediaPlayAndPauseButton();
        setExitButton();

        rootView.setVisibility(View.VISIBLE);
    }

    public void hide() {
        rootView.setVisibility(GONE);
    }

    public void setMediaPlayAndPauseButton() {
        playAndStopButton.setImageResource(
                podcastPlayer.isPlaying() ?
                R.drawable.ic_action_playback_pause :
                R.drawable.ic_action_playback_play);

        playAndStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (podcastPlayer.isPlaying()) {
                    podcastPlayer.pause();
                    PodcastPlayerNotification.notify(getContext(), podcastPlayer.getEpisode(),
                            PodcastPlayerNotification.PAUSE);
                    playAndStopButton.setImageResource(R.drawable.ic_action_playback_play);
                } else {
                    podcastPlayer.restart();
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
                if (podcastPlayer.isPlaying() || podcastPlayer.isPaused()) {
                    podcastPlayer.stop();
                    rootView.setVisibility(View.GONE);
                    podcastPlayer.getService().stopSelf();
                    // TODO: PodcastPlayerをメモリから削除
                }
            }
        });
    }
}

