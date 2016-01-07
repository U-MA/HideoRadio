package com.example.ideanote.hideoradio.presentation.presenter;


import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.domain.interactor.UseCase;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeDetailActivity;

import javax.inject.Inject;
import javax.inject.Named;

public class EpisodeDetailPresenter implements Presenter {

    private String episodeId;

    private final UseCase episodeDetailUseCase;

    EpisodeDetailActivity episodeDetailActivity;

    @Inject
    EpisodeDetailPresenter(@Named("episodeDetail") UseCase episodeDetailUseCase) {
        this.episodeDetailUseCase = episodeDetailUseCase;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    public void setView(EpisodeDetailActivity episodeDetailActivity) {
        this.episodeDetailActivity = episodeDetailActivity;
    }

    public void initialize(String episodeId) {
        this.episodeId = episodeId;
        episodeDetailUseCase.execute(new EpisodeDetailSubscriber());
    }

    private final class EpisodeDetailSubscriber extends rx.Subscriber<Episode> {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(Episode episode) {
            episodeDetailActivity.renderEpisode(episode);
        }
    }
}
