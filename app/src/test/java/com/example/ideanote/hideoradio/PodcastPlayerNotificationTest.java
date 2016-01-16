package com.example.ideanote.hideoradio;

import android.support.v4.app.NotificationCompat;

import com.example.ideanote.hideoradio.presentation.notifications.PodcastPlayerNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PodcastPlayerNotificationTest {

    private final static String EPISODE_TITLE = "This is a test title";
    private final static String EPISODE_DESCRIPTION = "This is a test description";

    PodcastPlayerNotification podcastPlayerNotification;

    Episode episode;

    @Before
    public void setup() {
        podcastPlayerNotification =
                new PodcastPlayerNotification(RuntimeEnvironment.application);

        episode = new Episode();
        episode.setTitle(EPISODE_TITLE);
        episode.setDescription(EPISODE_DESCRIPTION);

        podcastPlayerNotification.initialize(episode);
    }


    @Test
    public void createBuilderForPlaying_setEpisodeTitleAndDescription() {
        NotificationCompat.Builder builder =
                podcastPlayerNotification.createBuilderForPlaying();

        assertEquals(EPISODE_TITLE, builder.mContentTitle);
        assertEquals(EPISODE_DESCRIPTION, builder.mContentText);
    }

    @Test
    public void createBuilderForPlaying_setActionPauseAndStop() {
        NotificationCompat.Builder builder =
                podcastPlayerNotification.createBuilderForPlaying();

        ArrayList<NotificationCompat.Action> actions = builder.mActions;

        // check pause action
        NotificationCompat.Action pauseAction = actions.get(0);
        assertEquals(R.drawable.ic_action_playback_pause, pauseAction.getIcon());
        assertEquals("PAUSE", pauseAction.getTitle());

        // check stop action
        NotificationCompat.Action stopAction = actions.get(1);
        assertEquals(R.drawable.ic_action_cancel, stopAction.getIcon());
        assertEquals("STOP", stopAction.getTitle());
    }

    @Test
    public void createBuilderForPaused_setEpisodeTitleAndDescription() {
        NotificationCompat.Builder builder =
                podcastPlayerNotification.createBuilderForPaused();

        assertEquals(EPISODE_TITLE, builder.mContentTitle);
        assertEquals(EPISODE_DESCRIPTION, builder.mContentText);
    }

    @Test
    public void createBuilderForPaused_setActionPlayAndStop() {
        NotificationCompat.Builder builder =
                podcastPlayerNotification.createBuilderForPaused();

        ArrayList<NotificationCompat.Action> actions = builder.mActions;

        // check pause action
        NotificationCompat.Action playAction = actions.get(0);
        assertEquals(R.drawable.ic_action_playback_play, playAction.getIcon());
        assertEquals("PLAY", playAction.getTitle());

        // check stop action
        NotificationCompat.Action stopAction = actions.get(1);
        assertEquals(R.drawable.ic_action_cancel, stopAction.getIcon());
        assertEquals("STOP", stopAction.getTitle());
    }
}
