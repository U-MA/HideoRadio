package com.example.ideanote.hideoradio.activities;

import com.example.ideanote.hideoradio.HideoRadioApplication;
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
