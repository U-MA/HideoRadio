package com.example.ideanote.hideoradio;

import android.media.MediaPlayer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PodcastPlayerTest {

    private static final String DUMMY_FILE = "com.example.ideanote.hideoradio.test.assets.hideoradio_0322.mp3";

    @InjectMocks
    private PodcastPlayer podcastPlayer;

    @Mock
    MediaPlayer mockMediaPlayer;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
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
}
