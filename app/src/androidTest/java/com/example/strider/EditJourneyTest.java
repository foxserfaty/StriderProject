package com.example.strider;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class EditJourneyTest {

    @Before
    public void setUp() {
    }

    @Test
    public void testEditJourney() {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), EditJourney.class);
        intent.putExtra("journeyID", 123);
        ActivityScenario<EditJourney> activityScenario = ActivityScenario.launch(intent);

        onView(withId(R.id.titleEditText)).check(matches(withText("")));
        onView(withId(R.id.commentEditText)).check(matches(withText("")));
        onView(withId(R.id.ratingEditText)).check(matches(withText("")));

        onView(withId(R.id.titleEditText)).perform(replaceText("Test Title"));
        onView(withId(R.id.commentEditText)).perform(replaceText("Test Comment"));
        onView(withId(R.id.ratingEditText)).perform(replaceText("4"));

        onView(withId(R.id.titleEditText)).check(matches(withText("Test Title")));
        onView(withId(R.id.commentEditText)).check(matches(withText("Test Comment")));
        onView(withId(R.id.ratingEditText)).check(matches(withText("4")));

        onView(withId(R.id.button4)).perform(click());
    }
}
