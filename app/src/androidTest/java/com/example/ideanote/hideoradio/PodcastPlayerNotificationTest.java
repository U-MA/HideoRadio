package com.example.ideanote.hideoradio;

import android.content.Intent;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.test.suitebuilder.annotation.LargeTest;

import com.activeandroid.query.Select;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeDetailActivity;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeListActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.matcher.RootMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
@LargeTest
public class PodcastPlayerNotificationTest {
    private final static String EPISODE_DETAIL_ACTIVIYT_NAME =
            EpisodeDetailActivity.class.getName();
    private final static int TIMEOUT = 5000;

    private final static Episode episode = new Select().from(Episode.class).where("Id=?", 8).executeSingle();

    UiDevice uiDevice;

    @Rule
    public ActivityTestRule<EpisodeDetailActivity> activityTestRule =
            new ActivityTestRule<EpisodeDetailActivity>(EpisodeDetailActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra(EpisodeListActivity.EXTRA_EPISODE_ID, episode.getEpisodeId());
                    return intent;
                }
            };

    @Before
    public void setup() {
        // Let app to notify PodcastPlayerNotification

        // Click media play button
        onView(withId(R.id.image_button)).perform(click());

        // Click play button on dialog
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.pressHome();
        uiDevice.openNotification();
        uiDevice.wait(Until.hasObject(By.pkg("com.android.systemui")), TIMEOUT);
    }

    @After
    public void teardown() {
        uiDevice.pressHome();
    }

    @Test
    public void existNotification() throws UiObjectNotFoundException {
        UiObject view = uiDevice.findObject(new UiSelector().text(episode.getTitle()));

        UiObject titleView = uiDevice.findObject(new UiSelector()
                .packageName("com.example.ideanote.hideoradio")
                .text(episode.getTitle()));
        assertTrue(titleView.exists());

        UiObject pauseActionButton = uiDevice.findObject(new UiSelector()
                .packageName("com.example.ideanote.hideoradio")
                .text("PAUSE"));
        assertTrue(pauseActionButton.exists());

        UiObject stopActionButton = uiDevice.findObject(new UiSelector()
                .packageName("com.example.ideanote.hideoradio")
                .text("STOP"));
        assertTrue(stopActionButton.exists());

        view.click();

        UiObject uiObject = uiDevice.findObject(new UiSelector()
                .packageName("com.example.ideanote.hideoradio")
                .text(episode.getTitle()));
        assertTrue(uiObject.exists());

        uiDevice.openNotification();
        uiDevice.wait(Until.hasObject(By.pkg("com.android.systemui")), TIMEOUT);

        pauseActionButton.click();

        UiObject playActionButton = uiDevice.findObject(new UiSelector()
                .packageName("com.example.ideanote.hideoradio")
                .text("PLAY"));
        assertTrue(playActionButton.exists());

        stopActionButton.click();

        uiDevice.openNotification();
        uiDevice.wait(Until.hasObject(By.pkg("com.android.systemui")), TIMEOUT);

        UiObject notification = uiDevice.findObject(new UiSelector()
                .packageName("com.example.ideanote.hideoradio"));
        assertFalse(notification.exists());
    }
}
