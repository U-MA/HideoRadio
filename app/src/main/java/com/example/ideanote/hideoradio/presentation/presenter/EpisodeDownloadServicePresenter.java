package com.example.ideanote.hideoradio.presentation.presenter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.domain.interactor.EpisodeDownloadUseCase;
import com.example.ideanote.hideoradio.presentation.services.EpisodeDownloadService;
import com.example.ideanote.hideoradio.presentation.services.IService;

import java.io.File;

import javax.inject.Inject;

public class EpisodeDownloadServicePresenter implements ServicePresenter {

    private final EpisodeDownloadUseCase episodeDownloadUseCase;

    private IService episodeDownloadService;

    @Inject
    public EpisodeDownloadServicePresenter(EpisodeDownloadUseCase episodeDownloadUseCase) {
        this.episodeDownloadUseCase = episodeDownloadUseCase;
    }

    @Override
    public void onCreate() {
        // do nothing
    }

    @Override
    public void onDestroy() {
        // do nothing
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int id) {
        String episodeId = intent.getStringExtra(EpisodeDownloadService.EXTRA_EPISODE_ID);

        File directory = episodeDownloadService.getExternalFilesDir();
        Episode episode = Episode.findById(episodeId); // TODO: Use UseCase class!!

        episodeDownloadService.startForeground(episode);
        episodeDownloadUseCase.execute(episodeId, directory, new EpisodeDownloadServiceSubscriber());

        return Service.START_STICKY;
    }

    public void setService(IService episodeDownloadService) {
        this.episodeDownloadService = episodeDownloadService;
    }


    private class EpisodeDownloadServiceSubscriber extends rx.Subscriber<Episode> {
        @Override
        public void onCompleted() {
            // do nothing
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(Episode episode) {
            episodeDownloadService.stopForeground(episode);
        }
    }
}
