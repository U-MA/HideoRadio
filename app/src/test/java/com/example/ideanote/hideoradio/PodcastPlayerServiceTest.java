package com.example.ideanote.hideoradio;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.ideanote.hideoradio.data.executor.JobExecutor;
import com.example.ideanote.hideoradio.domain.executor.PostExecutionThread;
import com.example.ideanote.hideoradio.domain.executor.ThreadExecutor;
import com.example.ideanote.hideoradio.presentation.UIThread;
import com.example.ideanote.hideoradio.presentation.internal.di.ApplicationComponent;
import com.example.ideanote.hideoradio.presentation.media.PodcastPlayer;
import com.example.ideanote.hideoradio.data.repository.EpisodeDataRepository;
import com.example.ideanote.hideoradio.domain.repository.EpisodeRepository;
import com.example.ideanote.hideoradio.presentation.notifications.PodcastPlayerNotification;
import com.example.ideanote.hideoradio.presentation.services.PodcastPlayerService;

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

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PodcastPlayerServiceTest {

    private final static String EPISODE_ID = "episode_id";

    private PodcastPlayer mockPodcastPlayer;

    @Mock
    private NotificationManagerCompat notificationManagerCompat;

    @Mock
    private PodcastPlayerNotification podcastPlayerNotification;

    @InjectMocks
    private PodcastPlayerService service;

    @Before
    public void setup() {
        ((HideoRadioApplication) RuntimeEnvironment.application).setComponent(
                DaggerPodcastPlayerServiceTest_TestPodcastPlayerServiceComponent.builder()
                        .testPodcastPlayerServiceModule(new TestPodcastPlayerServiceModule()).build());

        service = new PodcastPlayerService();
        service.onCreate();

        MockitoAnnotations.initMocks(this);

        when(podcastPlayerNotification.createBuilderForPlaying()).thenReturn(
                new NotificationCompat.Builder(RuntimeEnvironment.application));
        when(podcastPlayerNotification.createBuilderForPaused()).thenReturn(
                new NotificationCompat.Builder(RuntimeEnvironment.application));
    }

    @After
    public void teardown() {
        service.onDestroy();
    }

    @Test
    public void onCreate() {
        verify(mockPodcastPlayer).setService(service);
    }

    @Test
    public void onBind() {
        assertNull(service.onBind(null));
    }

    @Test
    public void onStartCommand_actionStart_startToPlayAndNotifyPlayNotification() {
        Intent intent = PodcastPlayerService.createStartIntent(
                RuntimeEnvironment.application, EPISODE_ID);

        service.onStartCommand(intent, 0, 0);

        verify(mockPodcastPlayer).start(any(Context.class), any(Episode.class));
        verify(podcastPlayerNotification).createBuilderForPlaying();
        verify(notificationManagerCompat).notify(anyInt(), any(Notification.class));
    }

    @Test
    public void onStartCommand_actionPause() {
        Intent intent = PodcastPlayerService.createPauseIntent(RuntimeEnvironment.application);

        service.onStartCommand(intent, 0, 0);

        verify(mockPodcastPlayer).pause();
        verify(podcastPlayerNotification).createBuilderForPaused();
        verify(notificationManagerCompat).notify(anyInt(), any(Notification.class));
    }

    @Test
    public void onStartCommand_actionStop() {
        PodcastNotificationManager mockManager = mock(PodcastNotificationManager.class);

        Intent intent = PodcastPlayerService.createStopIntent(
                RuntimeEnvironment.application);
        service.onStartCommand(intent, 0, 0);

        verify(mockPodcastPlayer).stop();
        verify(mockManager).cancel();
    }

    @Test
    public void onStartCommand_actionRestart() {
        PodcastNotificationManager mockManager = mock(PodcastNotificationManager.class);

        Intent intent = PodcastPlayerService.createRestartIntent(
                RuntimeEnvironment.application);
        service.onStartCommand(intent, 0, 0);

        verify(mockPodcastPlayer).restart();
        verify(mockManager).startForeground();
    }

    @Singleton
    @Component(modules = TestPodcastPlayerServiceModule.class)
    interface TestPodcastPlayerServiceComponent extends ApplicationComponent {
        void inject(PodcastPlayer podcastPlayer);
        void inject(PodcastPlayerService service);

        ThreadExecutor threadExecutor();
        PostExecutionThread postExecutionThread();
        EpisodeRepository episodeRepository();
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

        @Singleton
        @Provides
        ThreadExecutor provideThreadExecutor(JobExecutor jobExecutor) {
            return jobExecutor;
        }

        @Singleton
        @Provides
        PostExecutionThread providePostExectionThread(UIThread postExecutionThread) {
            return postExecutionThread;
        }

        @Singleton
        @Provides
        EpisodeRepository provideEpisodeRepository(EpisodeDataRepository episodeDataRepository) {
            return episodeDataRepository;
        }

        @Provides
        @Singleton
        public PodcastNotificationManager providePodcastNotificationManager() {
            return new PodcastNotificationManager();
        }
    }
}
