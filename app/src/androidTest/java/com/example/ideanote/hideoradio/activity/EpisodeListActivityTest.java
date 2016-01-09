package com.example.ideanote.hideoradio.activity;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeDetailActivity;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EpisodeListActivityTest {

    private final static String EPISODE_DETAIL_ACTIVITY_NAME =
            EpisodeDetailActivity.class.getName();

    @Rule
    public IntentsTestRule<EpisodeListActivity> intentsTestRule =
            new IntentsTestRule<>(EpisodeListActivity.class);

    @Test
    public void showEpisodeList() {
        onView(withId(R.id.episode_list_view)).check(matches(isDisplayed()));
    }

    @Test
    public void invokeEpisodeDetailActivityWhenTouchCard() {
        onView(withId(R.id.episode_list_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        intended(hasComponent(hasClassName(EPISODE_DETAIL_ACTIVITY_NAME)));
    }
}
