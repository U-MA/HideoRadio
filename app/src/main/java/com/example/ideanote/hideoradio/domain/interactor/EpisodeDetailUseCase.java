package com.example.ideanote.hideoradio.domain.interactor;

import com.example.ideanote.hideoradio.domain.executor.PostExecutionThread;
import com.example.ideanote.hideoradio.domain.executor.ThreadExecutor;
import com.example.ideanote.hideoradio.domain.repository.EpisodeRepository;

import javax.inject.Inject;

import rx.Observable;

public class EpisodeDetailUseCase extends UseCase {

    private final String episodeId;
    private final EpisodeRepository episodeRepository;

    @Inject
    public EpisodeDetailUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
                                String episodeId, EpisodeRepository episodeRepository) {
        super(threadExecutor, postExecutionThread);
        this.episodeId = episodeId;
        this.episodeRepository = episodeRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return episodeRepository.episode(episodeId);
    }
}
