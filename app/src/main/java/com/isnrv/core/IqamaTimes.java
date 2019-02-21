package com.isnrv.core;

import android.util.Log;
import com.isnrv.utilities.DateTimeUtilities;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalTime;

import java.util.Arrays;

public class IqamaTimes {
	private static final String TAG = IqamaTimes.class.getCanonicalName();
	private static final int[] GAPS = {15, 10, 10, 5, 10};

	private IqamaTimes() {
	}

	public static LocalTime[] get(DateTime date) {
		final LocalTime[][] athanTimes = AthanTimes.getFullWeek(date);
		LocalTime[] iqamaTimes = new LocalTime[5];
		for (int i = 0; i < 5; i++) {
			// Find min and max of each prayer over days of week
			Arrays.sort(athanTimes[i]);
			final LocalTime min = athanTimes[i][0].plusMinutes(GAPS[i]);
			final LocalTime max = athanTimes[i][6].plusMinutes(5);
			LocalTime prayerTime;
			if (i == Prayer.MAGHRIB) {
				// If Maghrib prayer
				prayerTime = max;
			} else {
				prayerTime = max.compareTo(min) > 0 ? max : min;
			}

			// Round up to multiple of 5 minutes
			iqamaTimes[i] = roundUp(prayerTime);
			handleSpecialCases(iqamaTimes, date.getDayOfWeek(), i);
		}


		return iqamaTimes;
	}

	public static Prayer getNextPrayer() {
		final DateTime now = DateTime.now();
		final LocalTime[] athanTimes = AthanTimes.get(now);
		final LocalTime[] iqamaTimes = get(now);
		LocalTime athanTime;
		LocalTime iqamaTime;
		try {
			LocalTime currentTime = now.toLocalTime();
			for (int i = 0; i < 5; i++) {
				athanTime = athanTimes[i];
				if (currentTime.isBefore(athanTime)) {
					return new Prayer(i, now.withTime(athanTime), now.withTime(iqamaTimes[i]));
				}
			}
			// Next prayer is tomorrow's Fajr
			final DateTime tomorrow = now.plusDays(1);
			athanTime = AthanTimes.get(tomorrow)[0];
			iqamaTime = get(tomorrow)[0];
			return new Prayer(0, tomorrow.withTime(athanTime), tomorrow.withTime(iqamaTime));
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	public static LocalTime getExtraFridayPrayerTime(DateTime date) {
		if (!DateTimeUtilities.isAcademicYear(date)) return null;
		return LocalTime.parse(DateTimeUtilities.isStandardTime(date) ? "12:30PM" : "2:45PM", DateTimeUtilities.TIME_FORMAT);
	}

	private static void handleSpecialCases(LocalTime[] iqamaTimes, int dayOfWeek, int prayer) {
		if (prayer == Prayer.ISHA) {
			// Isha prayer is at or after 7:30PM all year
			final LocalTime ishaThreshold = LocalTime.parse("7:30PM", DateTimeUtilities.TIME_FORMAT);
			if (iqamaTimes[prayer].compareTo(ishaThreshold) < 0) {
				iqamaTimes[prayer] = ishaThreshold;
			}
		} else if (prayer == Prayer.DHUHR) {
			if (dayOfWeek == DateTimeConstants.FRIDAY) {
				// Jumaa prayer is at 1:30PM all year
				iqamaTimes[prayer] = LocalTime.parse("1:30PM", DateTimeUtilities.TIME_FORMAT);
			} else if (dayOfWeek == DateTimeConstants.SUNDAY) {
				// Duhr prayer is at or after 1:35PM on Sundays
				final LocalTime sunDayDhuhr = LocalTime.parse("1:35PM", DateTimeUtilities.TIME_FORMAT);
				if (iqamaTimes[prayer].compareTo(sunDayDhuhr) < 0) {
					iqamaTimes[prayer] = sunDayDhuhr;
				}
			}
		}
	}

	/**
	 * Round Iqama time up to multiple of 5 minutes
	 *
	 * @param time Original Iqama time
	 * @return Iqama time rounded up to the nearest multiple of 5 minutes
	 */
	private static LocalTime roundUp(final LocalTime time) {
		final int minutes = time.getMinuteOfHour();
		final LocalTime hour = time.hourOfDay().roundFloorCopy();
		final int roundedMinutes = (minutes % 5 == 0) ? minutes : Math.round((minutes + 2.5f) / 5) * 5;
		return hour.plusMinutes(roundedMinutes);
	}
}