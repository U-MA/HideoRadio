package com.example.ideanote.hideoradio.presentation.internal.di;

import com.example.ideanote.hideoradio.presentation.media.PodcastPlayer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PodcastModule {
    @Provides
    @Singleton
    public PodcastPlayer providePodcastPlayer() {
        return PodcastPlayer.getInstance();
    }
}
