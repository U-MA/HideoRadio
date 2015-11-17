package com.example.ideanote.hideoradio;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(manifest = "app/test/resources/TestManifest.xml", constants = BuildConfig.class,
        application = TestApplication.class)
public class PodcastPlayerTest {

    private PodcastPlayer podcastPlayer;

    @Before
    public void setup() {
        podcastPlayer = PodcastPlayer.getInstance();
    }

    @After
    public void teardown() {
        podcastPlayer.release();
    }

    @Test
    public void preconditions() {
        assertThat(podcastPlayer).isNotNull();
    }

    @Test
    public void isStoppedAtFirst() {
        assertThat(podcastPlayer.isStopped()).isTrue();
    }
}
