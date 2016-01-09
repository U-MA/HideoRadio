package com.example.ideanote.hideoradio.presentation.internal.di;

import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeDetailActivity;
import com.example.ideanote.hideoradio.presentation.view.fragment.EpisodeListFragment;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = EpisodeModule.class)
public interface EpisodeComponent {
    void inject(EpisodeDetailActivity episodeDetailActivity);
    void inject(EpisodeListFragment episodeListFragment);
}
