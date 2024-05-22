package com.example.strider;

import android.content.Intent;
import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class StatisticsActivityTest {

    @Rule
    public ActivityTestRule<StatisticsActivity> activityTestRule =
            new ActivityTestRule<>(StatisticsActivity.class);

    @Before
    public void setUp() {
        // Đảm bảo rằng activity được khởi chạy
        activityTestRule.launchActivity(new Intent());
    }

    @Test
    public void testUIElementsDisplayed() {
        // Kiểm tra xem các phần tử giao diện người dùng được hiển thị
        Espresso.onView(withId(R.id.Statistics_recordDistance)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.Statistics_distanceToday)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.Statistics_timeToday)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.Statistics_distanceAllTime)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.Statistics_selectDate)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.barchart)).check(matches(isDisplayed()));
    }

    @Test
    public void testDatePickerDialogue() {
        // Nhấn vào văn bản ngày để mở hộp thoại chọn ngày
        Espresso.onView(withId(R.id.Statistics_selectDate)).perform(ViewActions.click());
        // Chờ hộp thoại xuất hiện
        SystemClock.sleep(1000);
        Espresso.onView(withId(R.id.Statistics_selectDate)).check(matches(isDisplayed()));
        Espresso.onView(withText("OK")).perform(ViewActions.click());
    }

}
