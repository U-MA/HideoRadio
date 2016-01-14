package com.example.ideanote.hideoradio;

import android.content.Intent;
import android.view.View;

import com.example.ideanote.hideoradio.domain.interactor.UseCase;
import com.example.ideanote.hideoradio.presentation.media.PodcastPlayer;
import com.example.ideanote.hideoradio.presentation.presenter.EpisodeDetailPresenter;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeDetailActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class EpisodeDetailPresenterTest {

    EpisodeDetailActivity mockEpisodeDetailActivity;

    UseCase mockUseCase;

    PodcastPlayer mockPodcastPlayer;

    EpisodeDetailPresenter episodeDetailPresenter;

    @Before
    public void setup() {
        mockUseCase = mock(UseCase.class);
        mockPodcastPlayer = mock(PodcastPlayer.class);
        mockEpisodeDetailActivity = mock(EpisodeDetailActivity.class);

        episodeDetailPresenter = new EpisodeDetailPresenter(mockUseCase, mockPodcastPlayer);
        episodeDetailPresenter.setView(mockEpisodeDetailActivity);
    }

    @Test
    public void setupMediaView_playerIsPlaying() {
        when(mockPodcastPlayer.isPlaying()).thenReturn(true);
        when(mockPodcastPlayer.has(any(Episode.class))).thenReturn(true);

        episodeDetailPresenter.setupMediaView();

        verify(mockEpisodeDetailActivity).setPauseMediaButton();
        verify(mockEpisodeDetailActivity).setSeekBarEnabled(true);
        verify(mockEpisodeDetailActivity).currentTimeUpdate(anyInt());
    }

    @Test
    public void setupMediaView_playerIsPaused() {
        when(mockPodcastPlayer.isPaused()).thenReturn(true);

        episodeDetailPresenter.setupMediaView();

        verify(mockEpisodeDetailActivity).setPlayMediaButton();
        verify(mockEpisodeDetailActivity).setSeekBarEnabled(false);
        verify(mockEpisodeDetailActivity).currentTimeUpdate(anyInt());
    }

    public void setupMediaView_playerIsStopped() {
        when(mockPodcastPlayer.isStopped()).thenReturn(true);

        episodeDetailPresenter.setupMediaView();

        verify(mockEpisodeDetailActivity).setPlayMediaButton();
        verify(mockEpisodeDetailActivity).setSeekBarEnabled(false);
        verify(mockEpisodeDetailActivity).currentTimeUpdate(0);
    }

    @Test
    public void onClick_playerIsPlaying() {
        when(mockPodcastPlayer.isNowEpisode((String) anyObject())).thenReturn(true);
        when(mockPodcastPlayer.isPlaying()).thenReturn(true);

        View mockView = mock(View.class);
        when(mockView.getContext()).thenReturn(RuntimeEnvironment.application);

        episodeDetailPresenter.onClick(mockView);

        verify(mockEpisodeDetailActivity).setPlayMediaButton();
        verify(mockEpisodeDetailActivity).setSeekBarEnabled(false);
        verify(mockEpisodeDetailActivity).startService((Intent) anyObject());
    }

    @Test
    public void onClick_playerIsPaused() {
        when(mockPodcastPlayer.isNowEpisode((String) anyObject())).thenReturn(true);
        when(mockPodcastPlayer.isPaused()).thenReturn(true);

        View mockView = mock(View.class);
        when(mockView.getContext()).thenReturn(RuntimeEnvironment.application);

        episodeDetailPresenter.onClick(mockView);

        verify(mockEpisodeDetailActivity).setPauseMediaButton();
        verify(mockEpisodeDetailActivity).setSeekBarEnabled(true);
        verify(mockEpisodeDetailActivity).startService((Intent) anyObject());
    }
}
