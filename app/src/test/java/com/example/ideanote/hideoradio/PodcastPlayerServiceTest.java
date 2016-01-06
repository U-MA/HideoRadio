package com.example.ideanote.hideoradio;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import com.example.ideanote.hideoradio.data.executor.JobExecutor;
import com.example.ideanote.hideoradio.domain.executor.PostExecutionThread;
import com.example.ideanote.hideoradio.domain.executor.ThreadExecutor;
import com.example.ideanote.hideoradio.presentation.UIThread;
import com.example.ideanote.hideoradio.presentation.internal.di.ApplicationComponent;
import com.example.ideanote.hideoradio.presentation.notifications.PodcastNotificationManager;
import com.example.ideanote.hideoradio.data.repository.EpisodeDataRepository;
import com.example.ideanote.hideoradio.domain.repository.EpisodeRepository;
import com.example.ideanote.hideoradio.presentation.services.PodcastPlayerService;

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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PodcastPlayerServiceTest {

    private PodcastPlayer mockPodcastPlayer;

    private PodcastPlayerService service;

    @Before
    public void setup() {
        ((HideoRadioApplication) RuntimeEnvironment.application).setComponent(
                DaggerPodcastPlayerServiceTest_TestPodcastPlayerServiceComponent.builder()
                        .testPodcastPlayerServiceModule(new TestPodcastPlayerServiceModule()).build());

        ApplicationComponent applicationComponent =
                ((HideoRadioApplication) RuntimeEnvironment.application).getComponent();

        service = new PodcastPlayerService();
        applicationComponent.inject(service);
    }

    @After
    public void teardown() {
        service.onDestroy();
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

    // TODO もう少しうまいテスト方法があるはず
    @Test
    public void onStartCommand_actionStart() {
        final String EPISODE_ID = "123";

        PodcastNotificationManager mockManager = mock(PodcastNotificationManager.class);
        service.setManager(mockManager);

        Intent intent = PodcastPlayerService.createStartIntent(
                RuntimeEnvironment.application,
                EPISODE_ID);
        service.onStartCommand(intent, 0, 0);

        verify(mockPodcastPlayer).start((Context) anyObject(), (Episode) anyObject());
        verify(mockManager).startForeground();
    }

    @Test
    public void onStartCommand_actionPause() {
        PodcastNotificationManager mockManager = mock(PodcastNotificationManager.class);
        service.setManager(mockManager);

        Intent intent = PodcastPlayerService.createPauseIntent(
                RuntimeEnvironment.application);
        service.onStartCommand(intent, 0, 0);

        verify(mockPodcastPlayer).pause();
        verify(mockManager).stopForeground();
    }

    @Test
    public void onStartCommand_actionStop() {
        PodcastNotificationManager mockManager = mock(PodcastNotificationManager.class);
        service.setManager(mockManager);

        Intent intent = PodcastPlayerService.createStopIntent(
                RuntimeEnvironment.application);
        service.onStartCommand(intent, 0, 0);

        verify(mockPodcastPlayer).stop();
        verify(mockManager).cancel();
    }

    @Test
    public void onStartCommand_actionRestart() {
        PodcastNotificationManager mockManager = mock(PodcastNotificationManager.class);
        service.setManager(mockManager);

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
    }
}
