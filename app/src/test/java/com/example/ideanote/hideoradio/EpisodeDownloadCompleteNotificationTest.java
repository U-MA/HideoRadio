package com.example.ideanote.hideoradio;

import android.support.v4.app.NotificationCompat;

import com.example.ideanote.hideoradio.presentation.notifications.EpisodeDownloadCompleteNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class EpisodeDownloadCompleteNotificationTest {

    private final static String EPISODE_TITLE = "This is a test title";

    @Mock
    Episode mockEpisode;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void checkTitleAndText() {
        when(mockEpisode.getTitle()).thenReturn(EPISODE_TITLE);

        NotificationCompat.Builder builder =
                EpisodeDownloadCompleteNotification.createBuilder(
                        RuntimeEnvironment.application, mockEpisode);

        assertEquals("Download completed", builder.mContentTitle);
        assertEquals(EPISODE_TITLE, builder.mContentText);
    }
}
