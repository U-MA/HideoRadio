package com.example.ideanote.hideoradio.domain.interactor;

import com.example.ideanote.hideoradio.domain.executor.PostExecutionThread;
import com.example.ideanote.hideoradio.domain.executor.ThreadExecutor;
import com.example.ideanote.hideoradio.domain.repository.EpisodeRepository;

import javax.inject.Inject;

import rx.Observable;

public class EpisodeListUseCase extends UseCase {

    private final EpisodeRepository episodeRepository;

    @Inject
    public EpisodeListUseCase(EpisodeRepository episodeRepository, ThreadExecutor threadExecutor,
                       PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.episodeRepository = episodeRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return episodeRepository.episodes();
    }
}
