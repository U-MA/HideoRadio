package com.example.ideanote.hideoradio;

import com.example.ideanote.hideoradio.domain.interactor.UseCase;
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

    EpisodeListPresenter episodeListPresenter;

    @Before
    public void setup() {
        mockUseCase = mock(UseCase.class);
        episodeListPresenter = new EpisodeListPresenter(mockUseCase);

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
}
