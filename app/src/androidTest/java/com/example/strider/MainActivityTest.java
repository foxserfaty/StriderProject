package com.example.strider;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;


import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;

public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testViewsVisible() {
        // Kiểm tra xem TextView "Select Date" hiển thị đúng không
        Espresso.onView(withId(R.id.selectDateText))
                .check(ViewAssertions.matches(ViewMatchers.withText("Select Date")));

        // Kiểm tra xem ListView hiển thị đúng không
        Espresso.onView(withId(R.id.listView))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Kiểm tra xem BottomNavigationView hiển thị đúng không
        Espresso.onView(withId(R.id.nav_view))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testDateTextClick() {
        // Kiểm tra xem khi nhấn vào TextView "Select Date" có mở được dialog Date Picker không
        Espresso.onView(withId(R.id.selectDateText))
                .perform(click());

        // Kiểm tra xem dialog Date Picker có hiển thị đúng không
        Espresso.onView(withText(android.R.string.ok))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testListViewItemClick() {
        Espresso.onData(anything())
                .inAdapterView(withId(R.id.listView))
                .atPosition(0) // Thay 0 bằng vị trí mục bạn muốn kiểm tra
                .perform(click());


        Espresso.onView(withId(R.id.ViewSingleJourney_titleText))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
