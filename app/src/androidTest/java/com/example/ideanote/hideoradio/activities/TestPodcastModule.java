package com.example.ideanote.hideoradio.activities;

import com.example.ideanote.hideoradio.presentation.media.PodcastPlayer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class TestPodcastModule {
    @Provides
    @Singleton
    public PodcastPlayer providePodcastPlayer() {
        return mock(PodcastPlayer.class);
    }
}
