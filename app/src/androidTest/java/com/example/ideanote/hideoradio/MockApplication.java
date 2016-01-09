package com.example.ideanote.hideoradio;

import com.example.ideanote.hideoradio.activity.DaggerEpisodeDetailActivityTest_TestApplicationComponent;
import com.example.ideanote.hideoradio.di.TestApplicationModule;
import com.example.ideanote.hideoradio.di.TestPodcastModule;
import com.example.ideanote.hideoradio.presentation.internal.di.ApplicationComponent;

public class MockApplication extends HideoRadioApplication {
    @Override
    public ApplicationComponent getComponent() {
        return DaggerEpisodeDetailActivityTest_TestApplicationComponent.builder()
                .testApplicationModule(new TestApplicationModule())
                .testPodcastModule(new TestPodcastModule())
                .build();
    }
}
