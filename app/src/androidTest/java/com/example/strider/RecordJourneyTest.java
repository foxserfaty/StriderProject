package com.example.strider;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RecordJourneyTest {

    @Rule
    public ActivityTestRule<RecordJourney> activityTestRule =
            new ActivityTestRule<>(RecordJourney.class);


    @Test
    public void testUIElementsDisplayed() {
        // Check if UI elements are displayed

        Espresso.onView(withId(R.id.startButton)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.statButton)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.spotify_button)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.map_fragment)).check(matches(isDisplayed()));

    }


    @Test
    public void testButtonClicks() {
        // Test button clicks
        Espresso.onView(withId(R.id.startButton)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.startButton)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        Espresso.onView(withId(R.id.stopButton)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.onView(withId(R.id.statLayout)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.distanceText)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.durationText)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.avgSpeedText)).check(matches(isDisplayed()));

    }
}
