package com.example.ideanote.hideoradio.domain.interactor;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.domain.executor.PostExecutionThread;
import com.example.ideanote.hideoradio.domain.executor.ThreadExecutor;
import com.example.ideanote.hideoradio.presentation.events.BusHolder;
import com.example.ideanote.hideoradio.presentation.events.EpisodeDownloadCompleteEvent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class EpisodeDownloadUseCase {

    private final static int BUFFER_SIZE = 23 * 1024;

    private final ThreadExecutor threadExecutor;
    private final PostExecutionThread postExecutionThread;

    @Inject
    public EpisodeDownloadUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
    }

    public Observable<Episode> buildUseCaseObservable(final String episodeId, final File directory) {
        return Observable.create(new Observable.OnSubscribe<Episode>() {
            @Override
            public void call(Subscriber<? super Episode> subscriber) {
                try {
                    Episode episode = Episode.findById(episodeId);
                    URL url = new URL(episode.getEnclosure().toString());

                    final String episodeId = episode.getEpisodeId();
                    int index = episodeId.lastIndexOf('/');
                    String filename = episodeId.substring(index+1);
                    File destFile = new File(directory, filename);

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, BUFFER_SIZE);
                    FileOutputStream fileOutputStream = new FileOutputStream(destFile);

                    int actual;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while ((actual = bufferedInputStream.read(buffer, 0, BUFFER_SIZE)) > 0) {
                        fileOutputStream.write(buffer, 0, actual);
                    }

                    episode.setMediaLocalPath(destFile.toString());
                    episode.save();
                    BusHolder.getInstance().post(new EpisodeDownloadCompleteEvent());

                    subscriber.onNext(episode);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void execute(final String episodeId, final File directory, Subscriber subscriber) {
        buildUseCaseObservable(episodeId, directory)
                .subscribeOn(Schedulers.from(threadExecutor))
                .observeOn(postExecutionThread.getScheduler())
                .subscribe(subscriber);
    }
}
