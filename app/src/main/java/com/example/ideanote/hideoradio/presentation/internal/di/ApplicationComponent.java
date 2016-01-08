package com.example.ideanote.hideoradio.presentation.internal.di;

import android.media.MediaPlayer;

import com.example.ideanote.hideoradio.presentation.media.PodcastPlayer;
import com.example.ideanote.hideoradio.domain.executor.PostExecutionThread;
import com.example.ideanote.hideoradio.domain.executor.ThreadExecutor;
import com.example.ideanote.hideoradio.domain.repository.EpisodeRepository;
import com.example.ideanote.hideoradio.presentation.services.PodcastPlayerService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * A component whose lifetime is the life of the application.
 *
 * アプリケーションというよりServiceかもしれん
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(PodcastPlayer podcastPlayer);
    void inject(PodcastPlayerService podcastPlayerService);

    ThreadExecutor threadExecutor();
    PostExecutionThread postExecutionThread();
    EpisodeRepository episodeRepository();
    MediaPlayer mediaPlayer();
}
