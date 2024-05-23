package com.example.strider;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import static androidx.test.espresso.matcher.ViewMatchers.withId;


public class TaskActivityTest {

    @Rule
    public ActivityScenarioRule<TaskActivity> activityScenarioRule =
            new ActivityScenarioRule<>(TaskActivity.class);
    @Test
    public void testUIDisplay() {
        Espresso.onView(withId(R.id.editButton)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(withId(R.id.enterKm)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(withId(R.id.enterTime)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(withId(R.id.enterTime)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(withId(R.id.editButton)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(withId(R.id.kmTaskTV)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(withId(R.id.timeTaskTV)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }


}
