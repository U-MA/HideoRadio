package com.example.ideanote.hideoradio;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class PodcastPlayer extends MediaPlayer
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

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
            if (episode.isDownload()) {
                Uri uri = Uri.parse(episode.getMediaLocalPath());
                setDataSource(context, uri);
                Log.i("PodcastPlayer", "MediaLocal play");
            } else {
                setDataSource(context, episode.getEnclosure());
            }
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
        release();
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
