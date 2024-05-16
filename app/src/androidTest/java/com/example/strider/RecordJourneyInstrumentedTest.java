package com.example.strider;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

@RunWith(AndroidJUnit4.class)
public class RecordJourneyInstrumentedTest {

    private ActivityScenario<RecordJourney> activityScenario;
    private LocationService.LocationServiceBinder locationService;
    private CountDownLatch latch;

    @Before
    public void setUp() {
        Intents.init();
        latch = new CountDownLatch(1);

        // Launch the activity using ActivityScenario
        activityScenario = ActivityScenario.launch(RecordJourney.class);

        // Use reflection to access the private locationService field
        try {
            activityScenario.onActivity(activity -> {
                try {
                    Field field = RecordJourney.class.getDeclaredField("locationService");
                    field.setAccessible(true);
                    locationService = (LocationService.LocationServiceBinder) field.get(activity);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Wait for service connection
        try {
            latch.await(); // Wait until latch count reaches zero
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        Intents.release();
        // Close the activity scenario
        activityScenario.close();
    }

    @Test
    public void testUIElementsDisplayed() {
        // Check if UI elements are displayed
        Espresso.onView(ViewMatchers.withId(R.id.distanceText)).check(matches(isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.durationText)).check(matches(isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.avgSpeedText)).check(matches(isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.startButton)).check(matches(isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.stopButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testPlayButton() {
        // Perform actions after the activity has been fully initialized and resumed
        Espresso.onView(ViewMatchers.withId(R.id.startButton)).perform(ViewActions.click());

        // Check if stop button is enabled and play button is disabled
        Espresso.onView(ViewMatchers.withId(R.id.stopButton)).check(matches(isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.startButton)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void testStopButton() {
        // Perform actions after the activity has been fully initialized and resumed
        Espresso.onView(ViewMatchers.withId(R.id.startButton)).perform(ViewActions.click());

        // Adding a delay to simulate tracking duration
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click stop button
        Espresso.onView(ViewMatchers.withId(R.id.stopButton)).perform(ViewActions.click());

        // Check if play button is enabled and stop button is disabled
        Espresso.onView(ViewMatchers.withId(R.id.startButton)).check(matches(isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.stopButton)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }
}
