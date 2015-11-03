package com.example.ideanote.hideoradio;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;

public class PodcastPlayer extends MediaPlayer implements MediaPlayer.OnPreparedListener {

    private static PodcastPlayer instance;

    private PlayerState state = PlayerState.STOPPED;
    private Episode episode;

    @Override
    public boolean isPlaying() {
        return (state == PlayerState.PLAYING);
    }

    public boolean isStopped() {
        return (state == PlayerState.STOPPED);
    }

    public boolean isPaused() {
        return (state == PlayerState.PAUSED);
    }

    private PodcastPlayer() {
        super();
    }

    public static PodcastPlayer getInstance() {
        if (instance == null) {
            instance = new PodcastPlayer();
        }
        return instance;
    }

    public void start(Context context, Episode episode) {
        state = PlayerState.PREPARING;
        this.episode = episode;

        try {
            setAudioStreamType(AudioManager.STREAM_MUSIC);
            setDataSource(context, episode.getEnclosure());
            setOnPreparedListener(this);
            prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        restart();
    }

    public void restart() {
        super.start();
        state = PlayerState.PLAYING;
    }

    @Override
    public void pause() {
        super.pause();
        state = PlayerState.PAUSED;
    }

    @Override
    public void stop() {
        super.pause();
        super.seekTo(0);
        state = PlayerState.STOPPED;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        state = PlayerState.PREPARED;
        start();
        state = PlayerState.PLAYING;
    }

    public Episode getEpisode() {
        return episode;
    }

    private enum PlayerState {
        PLAYING,
        STOPPED,
        PAUSED,
        PREPARING,
        PREPARED
    }
}
