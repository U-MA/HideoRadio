package com.example.ideanote.hideoradio.presentation.internal.di;

import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeDetailActivity;
import com.example.ideanote.hideoradio.presentation.view.fragment.EpisodeListFragment;

import dagger.Component;

/**
 * Inject episode into a specific fragments.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = EpisodeModule.class)
public interface EpisodeComponent {
    void inject(EpisodeListFragment episodeListFragment);
    void inject(EpisodeDetailActivity episodeDetailActivity);
}
