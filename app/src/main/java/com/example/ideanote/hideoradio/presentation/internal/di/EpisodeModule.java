package com.example.ideanote.hideoradio.presentation.internal.di;

import com.example.ideanote.hideoradio.domain.interactor.EpisodeListUseCase;
import com.example.ideanote.hideoradio.domain.interactor.UseCase;

import dagger.Module;
import dagger.Provides;

@Module
public class EpisodeModule {
    @PerActivity
    @Provides
    UseCase provideEpisodeListUseCase(EpisodeListUseCase episodeListUseCase) {
        return episodeListUseCase;
    }
}
