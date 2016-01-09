package com.example.ideanote.hideoradio.di;

import android.media.MediaPlayer;

import com.example.ideanote.hideoradio.data.executor.JobExecutor;
import com.example.ideanote.hideoradio.data.repository.EpisodeDataRepository;
import com.example.ideanote.hideoradio.domain.executor.PostExecutionThread;
import com.example.ideanote.hideoradio.domain.executor.ThreadExecutor;
import com.example.ideanote.hideoradio.domain.repository.EpisodeRepository;
import com.example.ideanote.hideoradio.presentation.UIThread;
import com.example.ideanote.hideoradio.presentation.notifications.PodcastNotificationManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.*;

@Module
public class TestApplicationModule {
    @Provides
    @Singleton
    public MediaPlayer provideMediaPlayer() {
        return new MediaPlayer();
    }

    @Provides
    @Singleton
    public PodcastNotificationManager providePodcastNotificationManager() {
        return mock(PodcastNotificationManager.class);
    }

    @Provides
    @Singleton
    public EpisodeRepository provideEpisodeRepository(EpisodeDataRepository episodeDataRepository) {
        return episodeDataRepository;
    }

    @Provides
    @Singleton
    public ThreadExecutor provideThreadExecutor(JobExecutor jobExecutor) {
        return jobExecutor;
    }

    @Provides
    @Singleton
    public PostExecutionThread providePostExecutionThread(UIThread uiThread) {
        return uiThread;
    }
}