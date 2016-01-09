package com.example.ideanote.hideoradio.di;

import android.media.MediaPlayer;

import com.example.ideanote.hideoradio.data.executor.JobExecutor;
import com.example.ideanote.hideoradio.data.repository.EpisodeDataRepository;
import com.example.ideanote.hideoradio.domain.executor.PostExecutionThread;
import com.example.ideanote.hideoradio.domain.executor.ThreadExecutor;
import com.example.ideanote.hideoradio.domain.interactor.EpisodeDetailUseCase;
import com.example.ideanote.hideoradio.domain.interactor.EpisodeListUseCase;
import com.example.ideanote.hideoradio.domain.interactor.UseCase;
import com.example.ideanote.hideoradio.domain.repository.EpisodeRepository;
import com.example.ideanote.hideoradio.presentation.UIThread;
import com.example.ideanote.hideoradio.presentation.media.PodcastPlayer;
import com.example.ideanote.hideoradio.presentation.notifications.PodcastNotificationManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.*;

@Module
public class TestApplicationModule {

    private String episodeId = "";

    public TestApplicationModule() {
    }

    public TestApplicationModule(String episodeId) {
        this.episodeId = episodeId;
    }

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

    @Provides
    @Singleton
    public PodcastPlayer providePodcastPlayer() {
        return mock(PodcastPlayer.class);
    }

    @Provides
    @Named("episodeList")
    UseCase provideEpisodeListUseCase(EpisodeListUseCase episodeListUseCase) {
        return episodeListUseCase;
    }

    @Provides
    @Named("episodeDetail")
    UseCase provideEpisodeDetailUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                        EpisodeRepository episodeRepository) {
        return new EpisodeDetailUseCase(threadExecutor, postExecutionThread, episodeId, episodeRepository);
    }
}
