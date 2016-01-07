package com.example.ideanote.hideoradio;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.example.ideanote.hideoradio.presentation.media.PodcastPlayer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PodcastPlayerTest {

    private static final String DUMMY_FILE = "com.example.ideanote.hideoradio.test.assets.hideoradio_0322.mp3";

    @InjectMocks
    private PodcastPlayer podcastPlayer;

    @Spy
    MediaPlayer mockMediaPlayer;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        doNothing().when(mockMediaPlayer).setDataSource((Context) anyObject(), (Uri) anyObject());
        doNothing().when(mockMediaPlayer).seekTo(anyInt());
        doNothing().when(mockMediaPlayer).prepareAsync();
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

        verify(mockMediaPlayer).seekTo(0);
        assertTrue(podcastPlayer.isStopped());
    }

    @Test
    public void playingStateWhenPodcastPlay() {
        doNothing().when(mockMediaPlayer).start();
        podcastPlayer.start();

        assertTrue(podcastPlayer.isPlaying());
    }

    @Test
    public void prepareAfterPodcastPlayerStart() {
        doNothing().when(mockMediaPlayer).start();

        Episode mockEpisode = mock(Episode.class);
        podcastPlayer.start(RuntimeEnvironment.application, mockEpisode);

        verify(mockMediaPlayer).prepareAsync();
    }

    @Test
    public void currentPosition() {
        final int CURRENT_MILLIS = 123;
        when(mockMediaPlayer.getCurrentPosition()).thenReturn(CURRENT_MILLIS);

        assertEquals(CURRENT_MILLIS, podcastPlayer.getCurrentPosition());
    }

    @Test
    public void getEpisode() {
        final String DUMMY_TITLE = "episode_title";
        Episode mockEpisode = mock(Episode.class);
        when(mockEpisode.getTitle()).thenReturn(DUMMY_TITLE);

        podcastPlayer.start(RuntimeEnvironment.application, mockEpisode);

        assertEquals(DUMMY_TITLE, podcastPlayer.getEpisode().getTitle());
    }

    @Test
    public void onCompletion() {
        podcastPlayer.onCompletion(mockMediaPlayer);

        verify(mockMediaPlayer).release();
        assertTrue(podcastPlayer.isStopped());
    }

    @Test
    public void onPrepared() {
        doNothing().when(mockMediaPlayer).start();
        podcastPlayer.onPrepared(mockMediaPlayer);

        verify(mockMediaPlayer).start();
        assertTrue(podcastPlayer.isPlaying());
    }
}
