package com.example.ideanote.hideoradio;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PodcastPlayerTest {

    private PodcastPlayer podcastPlayer;

    @Before
    public void setup() {
        podcastPlayer = PodcastPlayer.getInstance();
    }

    @Test
    public void StopStateWhenInit() {
        assertTrue(podcastPlayer.isStopped());
    }
}
