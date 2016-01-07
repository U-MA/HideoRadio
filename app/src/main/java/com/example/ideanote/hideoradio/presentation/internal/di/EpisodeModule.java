package com.example.ideanote.hideoradio.presentation.internal.di;

import com.example.ideanote.hideoradio.domain.executor.PostExecutionThread;
import com.example.ideanote.hideoradio.domain.executor.ThreadExecutor;
import com.example.ideanote.hideoradio.domain.interactor.EpisodeDetailUseCase;
import com.example.ideanote.hideoradio.domain.interactor.EpisodeListUseCase;
import com.example.ideanote.hideoradio.domain.interactor.UseCase;
import com.example.ideanote.hideoradio.domain.repository.EpisodeRepository;
import com.example.ideanote.hideoradio.presentation.notifications.PodcastNotificationManager;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class EpisodeModule {

    private String episodeId = "";

    public EpisodeModule() {
    }

    public EpisodeModule(String episodeId) {
        this.episodeId = episodeId;
    }

    @PerActivity
    @Provides
    @Named("episodeList")
    UseCase provideEpisodeListUseCase(EpisodeListUseCase episodeListUseCase) {
        return episodeListUseCase;
    }

    @PerActivity
    @Provides
    @Named("episodeDetail")
    UseCase provideEpisodeDetailUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                        EpisodeRepository episodeRepository) {
        return new EpisodeDetailUseCase(threadExecutor, postExecutionThread, episodeId, episodeRepository);
    }
}

