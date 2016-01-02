package com.example.ideanote.hideoradio;

import android.media.MediaPlayer;

import com.example.ideanote.hideoradio.internal.di.ApplicationComponent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "app/src/main/AndroidManifest.xml")
public class PodcastPlayerTest {

    private static final String DUMMY_FILE = "com.example.ideanote.hideoradio.test.assets.hideoradio_0322.mp3";

    private PodcastPlayer podcastPlayer;

    MediaPlayer mockMediaPlayer;

    @Before
    public void setup() {
        ApplicationComponent applicationComponent = DaggerPodcastPlayerTest_TestApplicationComponent.builder()
                .testApplicationModule(new TestApplicationModule())
                .build();
        ((HideoRadioApplication) RuntimeEnvironment.application).setComponent(applicationComponent);

        podcastPlayer = new PodcastPlayer();
        applicationComponent.inject(podcastPlayer);
    }

    @After
    public void teardown() {
        podcastPlayer.release();
    }

    @Test
    public void preconditions() {
        assertNotNull(podcastPlayer);
    }

    @Test
    public void StopStateWhenInit() {
        assertTrue(podcastPlayer.isStopped());
    }

    @Test
    public void pauseStateAfterPodcastPause() {
        Episode mockEpisode = mock(Episode.class);
        podcastPlayer.start(RuntimeEnvironment.application, mockEpisode);
        podcastPlayer.pause();

        assertTrue(podcastPlayer.isPaused());
    }

    @Test
    public void stopStateAfterPodcastStop() {
        Episode mockEpisode = mock(Episode.class);
        podcastPlayer.start(RuntimeEnvironment.application, mockEpisode);
        podcastPlayer.stop();

        assertTrue(podcastPlayer.isStopped());
    }

    @Test
    public void prepareAfterPodcastPlayerStart() {
        Episode mockEpisode = mock(Episode.class);
        podcastPlayer.start(RuntimeEnvironment.application, mockEpisode);

        verify(mockMediaPlayer).prepareAsync();
    }


    @Singleton
    @Component(modules = TestApplicationModule.class)
    interface TestApplicationComponent extends ApplicationComponent {
    }

    @Module
    class TestApplicationModule {
        @Provides
        @Singleton
        public PodcastPlayer providePodcastPlayer() {
            return PodcastPlayer.getInstance();
        }

        @Provides
        @Singleton
        public MediaPlayer provideMediaPlayer() {
            mockMediaPlayer = mock(MediaPlayer.class);
            return mockMediaPlayer;
        }
    }
}
