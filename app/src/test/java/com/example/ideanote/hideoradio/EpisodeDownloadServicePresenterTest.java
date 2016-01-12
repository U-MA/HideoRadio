package com.example.ideanote.hideoradio;

import android.content.Intent;

import com.example.ideanote.hideoradio.domain.interactor.EpisodeDownloadUseCase;
import com.example.ideanote.hideoradio.presentation.presenter.EpisodeDownloadServicePresenter;
import com.example.ideanote.hideoradio.presentation.services.EpisodeDownloadService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.Subscriber;

import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class EpisodeDownloadServicePresenterTest {

    EpisodeDownloadUseCase mockEpisodeDownloadUseCase;

    EpisodeDownloadServicePresenter episodeDownloadServicePresenter;

    @Before
    public void setup() {
        mockEpisodeDownloadUseCase = mock(EpisodeDownloadUseCase.class);
        episodeDownloadServicePresenter = new EpisodeDownloadServicePresenter(mockEpisodeDownloadUseCase);
    }

    @Test
    public void startDownload() {
        final String EPISODE_ID = "episodeId";

        Intent intent = new Intent();
        intent.putExtra(EpisodeDownloadService.EXTRA_EPISODE_ID, EPISODE_ID);

        episodeDownloadServicePresenter.onStartCommand(intent, 0, 0);

        verify(mockEpisodeDownloadUseCase).download((String) anyObject(), (Subscriber) anyObject());
    }
}
