package com.example.ideanote.hideoradio;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import javax.inject.Inject;

public class PodcastPlayer
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private static String TAG = PodcastPlayer.class.getName();


    private static PodcastPlayer instance;

    @Inject
    MediaPlayer mediaPlayer;

    private CurrentTimeListener currentTimeListener;
    private PlayerState state = PlayerState.STOPPED;
    private Episode episode;
    private Service service;

    public PodcastPlayer() {}

    public boolean isPlaying() {
        return (state == PlayerState.PLAYING);
    }

    public boolean isStopped() {
        return (state == PlayerState.STOPPED);
    }

    public boolean isPaused() {
        return (state == PlayerState.PAUSED);
    }

    public static PodcastPlayer getInstance() {
        if (instance == null) {
            instance = new PodcastPlayer();
        }
        return instance;
    }

    public void start(Context context, Episode episode) {

        // ちょっと怪しいコード
        mediaPlayer.reset();

        state = PlayerState.PREPARING;
        this.episode = episode;

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(context, episode.getUri());

            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Release MediaPlayer instance and nullify instance
     */
    public void release() {
        mediaPlayer.reset();
        mediaPlayer.release();
        instance = null;
    }

    public void start() {
        restart();
    }

    public void restart() {
        mediaPlayer.start();
        state = PlayerState.PLAYING;
    }

    public void pause() {
        mediaPlayer.pause();
        state = PlayerState.PAUSED;
    }

    public void reset() {
        mediaPlayer.reset();
    }

    public void stop() {
        mediaPlayer.pause();

        this.seekTo(0);
        state = PlayerState.STOPPED;
    }

    public void seekTo(int msec) {
        mediaPlayer.seekTo(msec);
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        state = PlayerState.PREPARED;
        start();
        state = PlayerState.PLAYING;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.pause();
        state = PlayerState.STOPPED;
        release();
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Service getService() {
        return service;
    }

    public Episode getEpisode() {
        return episode;
    }

    public void setCurrentTimeListener(final CurrentTimeListener currentTimeListener) {
        this.currentTimeListener = currentTimeListener;

        Timer timer = new Timer(new Timer.TimerCallback() {
            @Override
            public void onTick(long millis) {
                if (isPlaying() || isPaused()) {
                    currentTimeListener.onTick(mediaPlayer.getCurrentPosition());
                } else {
                    currentTimeListener.onTick(0);
                }
            }
        });
        timer.start();
    }

    private enum PlayerState {
        PLAYING,
        STOPPED,
        PAUSED,
        PREPARING,
        PREPARED
    }

    public interface CurrentTimeListener {
        void onTick(int currentPosition);
    }
}
