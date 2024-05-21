package com.example.strider;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ViewJourneysTest {

    @Rule
    public ActivityTestRule<ViewJourneys> activityRule = new ActivityTestRule<>(ViewJourneys.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testClickOnJourney_opensSingleJourneyActivity() {
        // Perform a click on the first item in the list view
        Espresso.onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(0).perform(click());

        // Verify that the SingleJourney activity is started
        Intents.intended(IntentMatchers.hasComponent(ViewSingleJourney.class.getName()));
    }

    // You can add more tests here for other functionalities of the ViewJourneys activity
}