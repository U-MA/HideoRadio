package com.example.ideanote.hideoradio;

import android.support.v4.app.NotificationCompat;

import com.example.ideanote.hideoradio.presentation.notifications.EpisodeDownloadNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class EpisodeDownloadNotificationTest {

    private final static String EPISODE_TITLE = "This is a test title";

    @Mock
    Episode mockEpisode;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void checkTitleAndDescription() {
        when(mockEpisode.getTitle()).thenReturn(EPISODE_TITLE);

        NotificationCompat.Builder builder =
                EpisodeDownloadNotification.createBuilder(
                        RuntimeEnvironment.application, mockEpisode);

        assertEquals("Downloading...", builder.mContentTitle);
        assertEquals(EPISODE_TITLE, builder.mContentText);
    }
}
