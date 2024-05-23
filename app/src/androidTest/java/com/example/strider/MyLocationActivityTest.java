package com.example.strider;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

public class MyLocationActivityTest {

    @Rule
    public ActivityScenarioRule<MyLocationActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MyLocationActivity.class);


    @Test
    public void testViewsVisible() {
        // Kiểm tra xem Spinner có được hiển thị không
        Espresso.onView(withId(R.id.spinner)).check(matches(isDisplayed()));


        Espresso.onView(withId(R.id.map_fragment)).check(matches(isDisplayed()));

        Espresso.onView(withId(R.id.nav_view))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

}
