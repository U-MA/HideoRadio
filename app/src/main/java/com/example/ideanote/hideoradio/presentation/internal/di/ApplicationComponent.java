package com.example.ideanote.hideoradio.presentation.internal.di;

import com.example.ideanote.hideoradio.presentation.services.PodcastPlayerService;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeDetailActivity;
import com.example.ideanote.hideoradio.presentation.view.fragment.EpisodeListFragment;

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
    void inject(EpisodeListFragment episodeListFragment);
    void inject(EpisodeDetailActivity episodeDetailActivity);

    void inject(PodcastPlayerService podcastPlayerService);
}
