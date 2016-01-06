package com.example.ideanote.hideoradio.presentation.presenter;


import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.domain.interactor.UseCase;
import com.example.ideanote.hideoradio.presentation.view.EpisodeListView;

import java.util.List;

import javax.inject.Inject;

public class EpisodeListPresenter implements Presenter {

    private EpisodeListView episodeListView;

    private final UseCase episodeListUseCase;

    @Inject
    public EpisodeListPresenter(UseCase episodeListUseCase) {
        this.episodeListUseCase = episodeListUseCase;
    }

    @Override
    public void onCreate() {
        // do nothing
    }

    @Override
    public void onResume() {
        // do nothing
    }

    @Override
    public void onDestroy() {
        // do nothing
    }

    public void setView(EpisodeListView episodeListView) {
        this.episodeListView = episodeListView;
    }

    public void loadEpisodes() {
        hideRetryView();
        showLoadingView();
        getEpisodes();
    }

    private void getEpisodes() {
        episodeListUseCase.execute(new EpisodeListSubscriber());
    }

    private void showEpisodeList(List<Episode> episodes) {
        episodeListView.renderEpisodes(episodes);
    }

    private void showLoadingView() {
        episodeListView.showLoadingView();
    }

    private void hideLoadingView() {
        episodeListView.hideLoadingView();
    }

    private void showRetryView() {
        episodeListView.showRetryView();
    }

    private void hideRetryView() {
        episodeListView.hideRetryView();
    }

    private final class EpisodeListSubscriber extends rx.Subscriber<List<Episode>> {
        @Override
        public void onCompleted() {
            EpisodeListPresenter.this.hideLoadingView();
        }

        @Override
        public void onNext(List<Episode> episodes) {
            EpisodeListPresenter.this.showEpisodeList(episodes);
        }

        @Override
        public void onError(Throwable e) {
            EpisodeListPresenter.this.hideLoadingView();
            // TODO: show error message
            EpisodeListPresenter.this.showRetryView();
        }
    }
}
