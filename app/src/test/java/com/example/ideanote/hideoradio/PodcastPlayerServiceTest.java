package com.example.ideanote.hideoradio;

import android.app.Application;
import android.content.Intent;
import android.media.MediaPlayer;

import com.example.ideanote.hideoradio.internal.di.ApplicationComponent;
import com.example.ideanote.hideoradio.services.PodcastPlayerService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PodcastPlayerServiceTest {

    // Brought PodcastPlayerService
    private final static String EXTRA_EPISODE_ID = "extra_episode_id";
    private final static String ACTION_PLAY_AND_PAUSE = "action_play_and_pause";
    private final static String ACTION_STOP = "action_stop";


    private PodcastPlayer mockPodcastPlayer;

    private PodcastPlayerService service;

    @Before
    public void setup() {
        ((HideoRadioApplication) RuntimeEnvironment.application).setComponent(
                DaggerPodcastPlayerServiceTest_TestPodcastPlayerServiceComponent.builder()
                .testPodcastPlayerServiceModule(new TestPodcastPlayerServiceModule()).build());

        service = new PodcastPlayerService();
    }

    @After
    public void teardown() {
        service.onDestroy();
    }

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

    @Test
    public void onCreate() {
        service.onCreate();

        verify(mockPodcastPlayer).setService(service);
    }

    @Test
    public void onBind() {
        assertNull(service.onBind(null));
    }

    @Singleton
    @Component(modules = TestPodcastPlayerServiceModule.class)
    interface TestPodcastPlayerServiceComponent extends ApplicationComponent {
        void inject(PodcastPlayer podcastPlayer);
        void inject(PodcastPlayerService service);

        MediaPlayer mediaPlayer();
    }

    @Module
    class TestPodcastPlayerServiceModule {
        @Singleton
        @Provides
        PodcastPlayer providePodcastPlayer() {
            mockPodcastPlayer = mock(PodcastPlayer.class);
            return mockPodcastPlayer;
        }

        @Singleton
        @Provides
        MediaPlayer provideMediaPlayer() {
            return new MediaPlayer();
        }
    }
}
