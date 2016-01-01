package com.example.ideanote.hideoradio;

import android.app.Service;
import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

public class PodcastPlayer extends MediaPlayer
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private static String TAG = PodcastPlayer.class.getName();

    private static PodcastPlayer instance;

    private CurrentTimeListener currentTimeListener;
    private PlayerState state = PlayerState.STOPPED;
    private Episode episode;
    private Service service;

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

        // ちょっと怪しいコード
        super.reset();

        state = PlayerState.PREPARING;
        this.episode = episode;

        setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            Uri uri = episode.isDownloaded() ?
                    Uri.fromFile(new File(episode.getMediaLocalPath())) :
                    episode.getEnclosure();
            setDataSource(context, uri);

            setOnPreparedListener(this);
            setOnCompletionListener(this);
            prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Release MediaPlayer instance and nullify instance
     */
    public void release() {
        super.reset();
        super.release();
        instance = null;
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

    @Override
    public void onCompletion(MediaPlayer mp) {
        super.pause();
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
                    currentTimeListener.onTick(getCurrentPosition());
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
