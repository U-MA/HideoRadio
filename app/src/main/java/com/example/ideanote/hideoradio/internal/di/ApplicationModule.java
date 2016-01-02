package com.example.ideanote.hideoradio.internal.di;

import android.media.MediaPlayer;

import com.example.ideanote.hideoradio.PodcastPlayer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    @Provides
    @Singleton
    public PodcastPlayer providePodcastPlayer() {
        return PodcastPlayer.getInstance();
    }

    @Provides
    @Singleton
    public MediaPlayer provideMediaPlayer() {
        return new MediaPlayer();
    }
}
