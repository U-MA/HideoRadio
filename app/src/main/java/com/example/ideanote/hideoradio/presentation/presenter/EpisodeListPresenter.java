package com.example.ideanote.hideoradio.presentation.presenter;


import android.content.Intent;
import android.view.View;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.domain.interactor.UseCase;
import com.example.ideanote.hideoradio.presentation.media.PodcastPlayer;
import com.example.ideanote.hideoradio.presentation.view.EpisodeListView;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeDetailActivity;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeListActivity;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

public class EpisodeListPresenter implements Presenter {

    private EpisodeListView episodeListView;

    private final UseCase episodeListUseCase;
    private final PodcastPlayer podcastPlayer;

    @Inject
    public EpisodeListPresenter(@Named("episodeList") UseCase episodeListUseCase, PodcastPlayer podcastPlayer) {
        this.episodeListUseCase = episodeListUseCase;
        this.podcastPlayer = podcastPlayer;
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
        if (podcastPlayer.isPaused()) {
            podcastPlayer.getService().stopSelf();
        }
    }

    public void setView(EpisodeListView episodeListView) {
        this.episodeListView = episodeListView;
    }

    public void loadEpisodes() {
        hideRetryView();
        showLoadingView();
        getEpisodes();
    }

    public void setupMediaBarView() {
        if (podcastPlayer.isStopped()) {
            hideMediaBarView();
        } else {
            showMediaBarView();
        }
    }

    public void onDownloadButtonClicked(Episode episode) {
        if (episode.isDownloaded()) {
            episodeListView.showClearCacheDialog(episode);
        } else {
            if (episodeListView.isNetworkConnected()) {
                episodeListView.showDownloadDialog(episode);
            } else {
                episodeListView.showNetworkError();
            }
        }
    }

    public void onMediaBarViewClicked(View v) {
        Intent intent = new Intent(v.getContext(), EpisodeDetailActivity.class);
        intent.putExtra(EpisodeListActivity.EXTRA_EPISODE_ID, podcastPlayer.getEpisode().getEpisodeId());
        episodeListView.launchIntent(intent);
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

    private void showMediaBarView() {
        Episode episode = podcastPlayer.getEpisode();
        episodeListView.showMediaBarView(episode);
    }

    private void hideMediaBarView() {
        episodeListView.hideMediaBarView();
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
