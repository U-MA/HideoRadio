package com.example.ideanote.hideoradio;

import com.example.ideanote.hideoradio.domain.interactor.UseCase;
import com.example.ideanote.hideoradio.presentation.media.PodcastPlayer;
import com.example.ideanote.hideoradio.presentation.presenter.EpisodeListPresenter;
import com.example.ideanote.hideoradio.presentation.view.EpisodeListView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.Subscriber;

import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class EpisodeListPresenterTest {

    EpisodeListView mockEpisodeListView;
    UseCase mockUseCase;
    PodcastPlayer mockPodcastPlayer;

    EpisodeListPresenter episodeListPresenter;

    @Before
    public void setup() {
        mockUseCase = mock(UseCase.class);
        mockPodcastPlayer = mock(PodcastPlayer.class);
        episodeListPresenter = new EpisodeListPresenter(mockUseCase, mockPodcastPlayer);

        mockEpisodeListView = mock(EpisodeListView.class);
        episodeListPresenter.setView(mockEpisodeListView);
    }

    @Test
    public void loadEpisodes() {
        episodeListPresenter.loadEpisodes();

        verify(mockEpisodeListView).hideRetryView();
        verify(mockEpisodeListView).showLoadingView();
        verify(mockUseCase).execute((Subscriber) anyObject());
    }

    @Test
    public void setupMediaBarView_playerIsStopped() {
        when(mockPodcastPlayer.isStopped()).thenReturn(true);

        episodeListPresenter.setupMediaBarView();

        verify(mockEpisodeListView).hideMediaBarView();
    }

    @Test
    public void setupMediaBarView_playerIsNotStopped() {
        when(mockPodcastPlayer.isStopped()).thenReturn(false);

        episodeListPresenter.setupMediaBarView();

        verify(mockEpisodeListView).showMediaBarView();
    }

    @Test
    public void onDownloadButtonClicked_episodeIsNotDownloaded() {
        Episode mockEpisode = mock(Episode.class);
        when(mockEpisode.isDownloaded()).thenReturn(false);

        episodeListPresenter.onDownloadButtonClicked(mockEpisode);

        verify(mockEpisodeListView).downloadEpisode(mockEpisode);
    }

    @Test
    public void onDownloadButtonClicked_episodeIsDownloaded() {
        Episode mockEpisode = mock(Episode.class);
        when(mockEpisode.isDownloaded()).thenReturn(true);

        episodeListPresenter.onDownloadButtonClicked(mockEpisode);

        verify(mockEpisodeListView).showClearCacheDialog(mockEpisode);
    }
}
