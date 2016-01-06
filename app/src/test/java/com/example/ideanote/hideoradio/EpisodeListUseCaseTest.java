package com.example.ideanote.hideoradio;

import com.example.ideanote.hideoradio.domain.repository.EpisodeRepository;
import com.example.ideanote.hideoradio.data.executor.JobExecutor;
import com.example.ideanote.hideoradio.domain.executor.PostExecutionThread;
import com.example.ideanote.hideoradio.presentation.UIThread;
import com.example.ideanote.hideoradio.domain.interactor.EpisodeListUseCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class EpisodeListUseCaseTest {

    @Mock
    private EpisodeRepository mockEpisodeRepository;

    @Mock
    private PostExecutionThread mockPostExecutionThread;

    private EpisodeListUseCase episodeListUseCase;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        episodeListUseCase = new EpisodeListUseCase(
                mockEpisodeRepository, new JobExecutor(), new UIThread());
    }

    @Test
    public void buildUseCaseObservable() {
        when(mockEpisodeRepository.episodes()).thenReturn(Observable.<List<Episode>>empty());
        episodeListUseCase.execute(new TestSubscriber());

        verify(mockEpisodeRepository).episodes();
    }

    private final class TestSubscriber extends Subscriber<List<Episode>> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(List<Episode> episodes) {

        }
    }
}
