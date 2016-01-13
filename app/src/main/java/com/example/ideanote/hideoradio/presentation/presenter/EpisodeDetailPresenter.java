package com.example.ideanote.hideoradio.presentation.presenter;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.domain.interactor.UseCase;
import com.example.ideanote.hideoradio.presentation.events.BusHolder;
import com.example.ideanote.hideoradio.presentation.events.ClearCacheEvent;
import com.example.ideanote.hideoradio.presentation.events.DownloadEvent;
import com.example.ideanote.hideoradio.presentation.events.PlayCacheEvent;
import com.example.ideanote.hideoradio.presentation.media.PodcastPlayer;
import com.example.ideanote.hideoradio.presentation.services.EpisodeDownloadService;
import com.example.ideanote.hideoradio.presentation.services.PodcastPlayerService;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeDetailActivity;
import com.example.ideanote.hideoradio.presentation.view.dialog.DownloadFailDialog;
import com.example.ideanote.hideoradio.presentation.view.dialog.MediaPlayConfirmationDialog;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import javax.inject.Named;

public class EpisodeDetailPresenter implements Presenter {

    private final UseCase episodeDetailUseCase;
    private final PodcastPlayer podcastPlayer;

    private Episode episode;
    private String episodeId;

    EpisodeDetailActivity episodeDetailActivity;

    @Inject
    public EpisodeDetailPresenter(@Named("episodeDetail") UseCase episodeDetailUseCase, PodcastPlayer podcastPlayer) {
        this.episodeDetailUseCase = episodeDetailUseCase;
        this.podcastPlayer = podcastPlayer;
    }

    @Override
    public void onCreate() {
        if (podcastPlayer.isPlaying()) {
            currentTimeUpdate(podcastPlayer.getCurrentPosition());
            episodeDetailActivity.setPauseMediaButton();
            episodeDetailActivity.setSeekBarEnabled(true);
        } else {
            currentTimeUpdate(0);
            episodeDetailActivity.setPlayMediaButton();
            episodeDetailActivity.setSeekBarEnabled(false);
        }

        podcastPlayer.setCurrentTimeListener(currentTimeListener);
    }

    @Override
    public void onResume() {
        BusHolder.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        BusHolder.getInstance().unregister(this);

        if (!podcastPlayer.isPlaying() && podcastPlayer.getService() != null) {
            podcastPlayer.getService().stopSelf();
        }
    }

    public void onClick(View v) {
        if (podcastPlayer.isNowEpisode(episodeId)) {
            Context context = v.getContext();
            Intent intent = new Intent(context, PodcastPlayerService.class);
            if (podcastPlayer.isPlaying()) {
                // 今から一時停止する
                intent.setAction(PodcastPlayerService.ACTION_PAUSE);
                episodeDetailActivity.setPlayMediaButton();
                episodeDetailActivity.setSeekBarEnabled(false);
            } else {
                // 今から再生する
                intent.setAction(PodcastPlayerService.ACTION_RESTART);
                episodeDetailActivity.setPauseMediaButton();
                episodeDetailActivity.setSeekBarEnabled(true);
            }
            episodeDetailActivity.startService(intent);
        } else {
            MediaPlayConfirmationDialog dialog = createPlayConfirmationDialog();
            dialog.show(episodeDetailActivity.getSupportFragmentManager(), "dialog");
        }
    }

    private MediaPlayConfirmationDialog createPlayConfirmationDialog() {
        MediaPlayConfirmationDialog dialog = new MediaPlayConfirmationDialog();
        Bundle bundle = new Bundle();
        bundle.putString("episodeId", episode.getEpisodeId());
        dialog.setArguments(bundle);
        return dialog;
    }

    public void setView(EpisodeDetailActivity episodeDetailActivity) {
        this.episodeDetailActivity = episodeDetailActivity;
    }

    public void initialize() {
        episodeDetailUseCase.execute(new EpisodeDetailSubscriber());
    }

    private void currentTimeUpdate(int currentTimeMillis) {
        episodeDetailActivity.currentTimeUpdate(currentTimeMillis);
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        if (podcastPlayer.isPlaying()) {
            podcastPlayer.seekTo(seekBar.getProgress());
        }
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
            EpisodeDetailPresenter.this.episodeId = episode.getEpisodeId(); // bad smell
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

    @Subscribe
    public void onPlayEpisode(PlayCacheEvent playCacheEvent) {
        if (!podcastPlayer.isPlaying() || !podcastPlayer.getEpisode().isEquals(episode)) {
            episodeDetailActivity.setPauseMediaButton();
            episodeDetailActivity.setSeekBarEnabled(true);

            Context context = episodeDetailActivity.getApplicationContext();
            Intent intent = new Intent(context, PodcastPlayerService.class);
            intent.setAction(PodcastPlayerService.ACTION_START);
            intent.putExtra(PodcastPlayerService.EXTRA_EPISODE_ID, episode.getEpisodeId());
            episodeDetailActivity.startService(intent);
        } else {
            episodeDetailActivity.setSeekBarEnabled(false);
        }
    }

    private PodcastPlayer.CurrentTimeListener currentTimeListener =
            new PodcastPlayer.CurrentTimeListener() {
                @Override
                public void onTick(int currentPosition) {
                    if (podcastPlayer.isPlaying() && podcastPlayer.getEpisode().isEquals(episode)) {
                        currentTimeUpdate(currentPosition);
                    }
                }
            };
}
