package com.example.ideanote.hideoradio;

import android.content.Intent;

import com.example.ideanote.hideoradio.services.PodcastPlayerService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PodcastPlayerServiceTest {

    // Brought PodcastPlayerService
    private final static String EXTRA_EPISODE_ID = "extra_episode_id";
    private final static String ACTION_PLAY_AND_PAUSE = "action_play_and_pause";
    private final static String ACTION_STOP = "action_stop";

    @Test
    public void createPlayPauseIntent() {
        final String EPISODE_ID = "123";
        Episode mockEpisode = mock(Episode.class);
        when(mockEpisode.getEpisodeId()).thenReturn(EPISODE_ID);
        Intent intent = PodcastPlayerService.createPlayPauseIntent(
                RuntimeEnvironment.application, mockEpisode);

        assertEquals(ACTION_PLAY_AND_PAUSE, intent.getAction());
        assertEquals(EPISODE_ID, intent.getStringExtra(EXTRA_EPISODE_ID));
    }

    @Test
    public void createStopIntent() {
        final String EPISODE_ID = "123";
        Episode mockEpisode = mock(Episode.class);
        when(mockEpisode.getEpisodeId()).thenReturn(EPISODE_ID);
        Intent intent = PodcastPlayerService.createStopIntent(
                RuntimeEnvironment.application, mockEpisode);

        assertEquals(ACTION_STOP, intent.getAction());
        assertEquals(EPISODE_ID, intent.getStringExtra(EXTRA_EPISODE_ID));
    }
}
