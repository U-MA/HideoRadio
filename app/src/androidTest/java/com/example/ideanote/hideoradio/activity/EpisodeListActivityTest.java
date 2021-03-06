package com.example.ideanote.hideoradio.activity;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.activeandroid.query.Select;
import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.HideoRadioApplication;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.di.TestApplicationModule;
import com.example.ideanote.hideoradio.presentation.internal.di.ApplicationComponent;
import com.example.ideanote.hideoradio.presentation.media.PodcastPlayer;
import com.example.ideanote.hideoradio.presentation.services.PodcastPlayerService;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeDetailActivity;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeListActivity;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EpisodeListActivityTest {

    private final static String EPISODE_DETAIL_ACTIVITY_NAME =
            EpisodeDetailActivity.class.getName();

    @Inject
    PodcastPlayer mockPodcastPlayer;

    @Rule
    public IntentsTestRule<EpisodeListActivity> intentsTestRule =
            new IntentsTestRule<>(EpisodeListActivity.class, true, false);

    @Before
    public void setup() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        HideoRadioApplication application =
                (HideoRadioApplication) instrumentation.getTargetContext().getApplicationContext();
        TestApplicationComponent component =
                DaggerEpisodeListActivityTest_TestApplicationComponent.builder()
                .testApplicationModule(new TestApplicationModule())
                .build();

        // TODO この行を削除してもテストが通るようにする
        //      この行を入れることでPodcastPlayerのmockがPodcastPlayerServiceに
        //      うまく注入出来るが、このメソッドはテストのためのメソッドなので出来れば使いたくない
        application.setComponent(component);

        component.inject(this);
    }

    @After
    public void teardown() {
        reset(mockPodcastPlayer);

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Intent intent = PodcastPlayerService.createStopIntent(instrumentation.getTargetContext());
        intentsTestRule.getActivity().startService(intent);
    }

    @Test
    public void showEpisodeList() {
        when(mockPodcastPlayer.isStopped()).thenReturn(true);

        intentsTestRule.launchActivity(new Intent());

        onView(withId(R.id.episode_list_view)).check(matches(isDisplayed()));
    }

    @Test
    public void goneMediaBarWhenPlayerIsStopped() {
        when(mockPodcastPlayer.isStopped()).thenReturn(true);

        intentsTestRule.launchActivity(new Intent());

        onView(withId(R.id.media_bar)).check(matches(not(isDisplayed())));
    }

    @Test
    public void visibleMediaBarWhenPlayerIsNotStopped() {
        final String TITLE = "title";
        when(mockPodcastPlayer.isStopped()).thenReturn(false);
        when(mockPodcastPlayer.getEpisode()).thenReturn(
                new Episode(null, TITLE, null, null, null, null, null));

        intentsTestRule.launchActivity(new Intent());

        onView(withId(R.id.media_bar)).check(matches(isDisplayed()));
    }

    @Test
    public void invokeEpisodeDetailActivityWhenTouchCard() {
        when(mockPodcastPlayer.isStopped()).thenReturn(true);

        intentsTestRule.launchActivity(new Intent());

        onView(withId(R.id.episode_list_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        intended(hasComponent(hasClassName(EPISODE_DETAIL_ACTIVITY_NAME)));
    }

    @Test
    public void pauseWhenMediaBarViewPauseButtonClicked() {
        final String TITLE = "title";

        when(mockPodcastPlayer.isStopped()).thenReturn(false);
        when(mockPodcastPlayer.isPlaying()).thenReturn(true);
        when(mockPodcastPlayer.getEpisode()).thenReturn(
                new Episode(null, TITLE, null, null, null, null, null));

        intentsTestRule.launchActivity(new Intent());

        onView(withId(R.id.play_and_pause)).perform(click());

        verify(mockPodcastPlayer).pause();
    }

    @Test
    public void restartWhenMediaBarViewPlayButtonClicked() {
        final String TITLE = "title";

        when(mockPodcastPlayer.isStopped()).thenReturn(false);
        when(mockPodcastPlayer.isPaused()).thenReturn(true);
        when(mockPodcastPlayer.isPlaying()).thenReturn(false);
        when(mockPodcastPlayer.getEpisode()).thenReturn(
                new Episode(null, TITLE, null, null, null, null, null));

        intentsTestRule.launchActivity(new Intent());

        onView(withId(R.id.play_and_pause)).perform(click());

        verify(mockPodcastPlayer).restart();
    }

    @Test
    public void launchIntentWhenMediaBarViewIsTouched() {
        Episode episode = new Select().from(Episode.class).where("Id=?", 8).executeSingle();

        when(mockPodcastPlayer.isPlaying()).thenReturn(true);
        when(mockPodcastPlayer.getEpisode()).thenReturn(episode);

        intentsTestRule.launchActivity(new Intent());

        onView(withId(R.id.media_bar)).perform(click());

        intended(hasComponent(hasClassName(EPISODE_DETAIL_ACTIVITY_NAME)));
    }

    @Test
    public void mediaBarViewIsGoneWhenClickExitButton() {
        final String TITLE = "title";

        when(mockPodcastPlayer.isPlaying()).thenReturn(true);
        when(mockPodcastPlayer.getEpisode()).thenReturn(
                new Episode(null, TITLE, null, null, null, null, null));

        intentsTestRule.launchActivity(new Intent());

        onView(withId(R.id.exit_button)).perform(click());

        onView(withId(R.id.media_bar)).check(matches(not(isDisplayed())));
    }

    @Test
    public void showDownloadDialogWhenDownloadButtonIsClicked() {
        final String TITLE = "title";

        when(mockPodcastPlayer.getEpisode()).thenReturn(
                new Episode(null, TITLE, null, null, null, null, null));

        intentsTestRule.launchActivity(new Intent());

        onView(withId(R.id.episode_list_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(
                        0, clickChildViewWithId(R.id.download_toggle_button)));

        onView(withText("Download this episode")).check(matches(isDisplayed()));
    }

    @Singleton
    @Component(modules = TestApplicationModule.class)
    public interface TestApplicationComponent extends ApplicationComponent {
        void inject(EpisodeListActivityTest episodeListActivityTest);
    }

    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                if (v != null) {
                    v.performClick();
                }
            }
        };
    }
}
