package com.example.strider;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testButtonClick_opensRecordJourney() {
        // Click on the button with the ID 'record_button' (replace with actual ID)
        Espresso.onView(ViewMatchers.withId(R.id.to_record_button)).perform(ViewActions.click());

        // Verify that the RecordJourney activity is started
        Intents.intended(IntentMatchers.hasComponent(RecordJourney.class.getName()));
    }

    @Test
    public void testButtonClick_opensViewJourneys() {
        // Click on the button with the ID 'view_button' (replace with actual ID)
        Espresso.onView(ViewMatchers.withId(R.id.journey_history_button)).perform(ViewActions.click());

        // Verify that the ViewJourneys activity is started
        Intents.intended(IntentMatchers.hasComponent(ViewJourneys.class.getName()));
    }

    @Test
    public void testButtonClick_opensStatisticsActivity() {
        // Click on the button with the ID 'statistics_button' (replace with actual ID)
        Espresso.onView(ViewMatchers.withId(R.id.statistics_button)).perform(ViewActions.click());

        // Verify that the StatisticsActivity is started
        Intents.intended(IntentMatchers.hasComponent(StatisticsActivity.class.getName()));
    }
}