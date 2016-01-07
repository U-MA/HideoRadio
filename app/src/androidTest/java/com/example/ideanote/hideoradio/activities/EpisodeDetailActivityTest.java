package com.example.ideanote.hideoradio.activities;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.activeandroid.query.Select;
import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeDetailActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.RootMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EpisodeDetailActivityTest {

    private final static String EXTRA_EPISODE_ID = "extra_episode_id";

    Episode episode = new Select().from(Episode.class).where("Id=?", 1).executeSingle();

    @Rule
    public ActivityTestRule<EpisodeDetailActivity> activityTestRule =
            new ActivityTestRule<EpisodeDetailActivity>(EpisodeDetailActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_EPISODE_ID, episode.getEpisodeId());

                    return intent;
                }
            };

    @Test
    public void titleAndDescriptionCheck() {
        onView(withText(episode.getTitle())).check(matches(isDisplayed()));
        onView(withText(episode.getDescription())).check(matches(isDisplayed()));
    }

    @Test
    public void launchDialogWhenPushPlayButton() {
        onView(withId(R.id.image_button)).perform(click());

        onView(withText("DOWNLOAD")).inRoot(isDialog()).check(matches(isDisplayed()));
    }

    @Test
    public void startEpisodeWhenLaunchingDialogClick() {
        onView(withId(R.id.image_button)).perform(click());
        onView(withText("PLAY STREAMING")).inRoot(isDialog()).perform(click());

        // TODO: verify(mockPodcastPlayer).start(Context, episodeId);
    }
}
