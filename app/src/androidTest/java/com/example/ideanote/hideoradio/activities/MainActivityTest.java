package com.example.ideanote.hideoradio.activities;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.view.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void MediaViewBar_isGoneAtFirst() {
        mediaBarView().check(matches(not(isDisplayed())));
    }

    @Test
    public void MediaViewBar_isVisibleWhenPlayerPlay() {
        // This test will move EpisodeDetailActivityTest
        // mediaBarView().check(matches(isDisplayed()));
    }

    private ViewInteraction mediaBarView() {
        return onView(withId(R.id.media_bar));
    }

}
