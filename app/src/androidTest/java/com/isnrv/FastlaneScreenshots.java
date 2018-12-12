package com.isnrv;

import androidx.test.rule.ActivityTestRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(JUnit4.class)
public class FastlaneScreenshots {
	@ClassRule
	public static final LocaleTestRule localeTestRule = new LocaleTestRule();

	@Rule
	public ActivityTestRule<Main> activityRule = new ActivityTestRule<>(Main.class);

	@BeforeClass
	public static void beforeAll() {
		Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());
	}

	@Test
	public void testTakeScreenshot() {
		onView(withId(R.id.prayer_layout)).check(matches(isDisplayed()));
		Screengrab.screenshot("PrayerTimes");
	}
}
