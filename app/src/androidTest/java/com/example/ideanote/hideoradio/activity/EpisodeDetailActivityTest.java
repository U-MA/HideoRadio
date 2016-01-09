package com.example.ideanote.hideoradio.activity;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.activeandroid.query.Select;
import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.HideoRadioApplication;
import com.example.ideanote.hideoradio.R;
import com.example.ideanote.hideoradio.di.TestApplicationModule;
import com.example.ideanote.hideoradio.di.TestPodcastModule;
import com.example.ideanote.hideoradio.presentation.internal.di.ApplicationComponent;
import com.example.ideanote.hideoradio.presentation.media.PodcastPlayer;
import com.example.ideanote.hideoradio.presentation.view.activity.EpisodeDetailActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.RootMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EpisodeDetailActivityTest {

    private final static String EXTRA_EPISODE_ID = "extra_episode_id";

    Episode episode = new Select().from(Episode.class).where("Id=?", 1).executeSingle();

    @Inject
    PodcastPlayer mockPodcastPlayer;

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

    @Before
    public void setup() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        HideoRadioApplication application =
                (HideoRadioApplication) instrumentation.getTargetContext().getApplicationContext();
        TestApplicationComponent component =
                (TestApplicationComponent) application.getComponent();

        // TODO この行を削除してもテストが通るようにする
        //      この行を入れることでPodcastPlayerのmockがPodcastPlayerServiceに
        //      うまく注入出来るが、このメソッドはテストのためのメソッドなので出来れば使いたくない
        application.setComponent(component);

        component.inject(this);
    }

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

        verify(mockPodcastPlayer).start((Context) anyObject(), (Episode) anyObject());
    }

    @Singleton
    @Component(modules = { TestPodcastModule.class, TestApplicationModule.class })
    public interface TestApplicationComponent extends ApplicationComponent {
        void inject(EpisodeDetailActivityTest episodeDetailActivityTest);
    }
}
