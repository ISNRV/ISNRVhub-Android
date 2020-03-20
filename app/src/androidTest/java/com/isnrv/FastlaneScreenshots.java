package com.isnrv;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;
import tools.fastlane.screengrab.cleanstatusbar.CleanStatusBar;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class FastlaneScreenshots {
	@ClassRule
	public static final LocaleTestRule localeTestRule = new LocaleTestRule();

	@Rule
	public ActivityTestRule<Main> mainActivityRule = new ActivityTestRule<>(Main.class);

	@Rule
	public ActivityTestRule<Settings> settingsActivityRule = new ActivityTestRule<>(Settings.class);

	@BeforeClass
	public static void beforeAll() {
		Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());
		new CleanStatusBar().setShowNotifications(false).enable();
	}


	@Test
	public void takeMainScreenshots() {
		mainActivityRule.launchActivity(new Intent(Intent.ACTION_PICK));
		Screengrab.screenshot("TodayPrayerTimes");
		onView(withId(R.id.pager)).perform(swipeLeft()).check(matches(isDisplayed()));
		Screengrab.screenshot("TomorrowPrayerTimes");
		mainActivityRule.finishActivity();
	}

	@Test
	public void takeSettingsScreenshot() {
		settingsActivityRule.launchActivity(new Intent(Intent.ACTION_PICK));
		Screengrab.screenshot("Settings");
		settingsActivityRule.finishActivity();
	}

	@AfterClass
	public static void done() {
		CleanStatusBar.disable();
	}
}
