package com.example.ideanote.hideoradio.presentation.presenter;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.domain.interactor.UseCase;
import com.example.ideanote.hideoradio.presentation.events.BusHolder;
import com.example.ideanote.hideoradio.presentation.events.ClearCacheEvent;
import com.example.ideanote.hideoradio.presentation.events.DownloadEvent;
import com.example.ideanote.hideoradio.presentation.services.EpisodeDownloadService;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeDetailActivity;
import com.example.ideanote.hideoradio.presentation.view.dialog.DownloadFailDialog;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import javax.inject.Named;

public class EpisodeDetailPresenter implements Presenter {

    private final UseCase episodeDetailUseCase;

    EpisodeDetailActivity episodeDetailActivity;

    private Episode episode;

    @Inject
    EpisodeDetailPresenter(@Named("episodeDetail") UseCase episodeDetailUseCase) {
        this.episodeDetailUseCase = episodeDetailUseCase;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onResume() {
        BusHolder.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        BusHolder.getInstance().unregister(this);

    }

    public void setView(EpisodeDetailActivity episodeDetailActivity) {
        this.episodeDetailActivity = episodeDetailActivity;
    }

    public void initialize() {
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
            EpisodeDetailPresenter.this.episode = episode; // bad smell
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) episodeDetailActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Subscribe
    public void onClearCache(ClearCacheEvent clearCacheEvent) {
        if (episode != null && episode.isDownload()) {
            episode.clearCache();
            episode.save();
        }
    }

    @Subscribe
    public void onDownload(DownloadEvent downloadEvent) {
        if (isOnline()) {
            episodeDetailActivity.startService(
                    EpisodeDownloadService.createIntent(
                            episodeDetailActivity.getApplicationContext(), episode));
        } else {
            DownloadFailDialog dialog = new DownloadFailDialog();
            dialog.show(episodeDetailActivity.getSupportFragmentManager(), "DownloadFailDialog");
        }
    }
}
