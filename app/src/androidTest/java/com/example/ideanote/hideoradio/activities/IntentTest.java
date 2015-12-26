package com.example.ideanote.hideoradio.activities;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.test.suitebuilder.annotation.LargeTest;

import com.example.ideanote.hideoradio.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;

@RunWith(JUnit4.class)
@LargeTest
public class IntentTest {

    private static final String EPISODE_DETAIL_ACTIVITY_NAME =
            "com.example.ideanote.hideoradio.activities.EpisodeDetailActivity";

    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(
            MainActivity.class);

    @Before
    public void setup() {
        // do something
    }

    @Test
    public void test1() {
        onView(withId(R.id.episode_list_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        intended(hasComponent(hasClassName(EPISODE_DETAIL_ACTIVITY_NAME)));
    }
}
