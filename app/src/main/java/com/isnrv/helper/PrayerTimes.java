package com.isnrv.helper;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class to read prayer times
 */
class PrayerTimes {
	private static final String TAG = PrayerTimes.class.getCanonicalName();

	private PrayerTimes() {
	}

	/**
	 * Find which prayer is next by comparing Athan time with current time.
	 *
	 * @return time of next prayer
	 */
	static NextPrayer findNextAthan(AssetManager assets) {
		final String[] prayerTimes = getTodayPrayersArrayWithFajr(assets);
		// Set data format
		final SimpleDateFormat format = new SimpleDateFormat("M/d/y, hh:mm aa", Locale.US);
		Date prayerDate;
		final Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = Integer.parseInt(prayerTimes[0]);
		int day = Integer.parseInt(prayerTimes[1]);
		try {
			// Get current time 
			Date currentTime = format.parse(format.format(cal.getTime()));
			// Get index of next prayer (comparison is based on athan times)
			for (int i = 2; i < prayerTimes.length - 2; i = i + 2) {
				prayerDate = format.parse(month + "/" + day + "/" + year + ", " + prayerTimes[i]);
				if (currentTime.before(prayerDate)) {
					cal.setTime(prayerDate);
					return new NextPrayer(cal, (i - 2) / 2);
				}
			}
			// Next prayer is tomorrow's Fajr. Set fields for next day.
			prayerDate = format.parse(month + "/" + day + "/" + year + ", " + prayerTimes[prayerTimes.length - 2]);
			cal.setTime(prayerDate);
			// Add one day to the calendar
			cal.add(Calendar.DATE, 1);
			return new NextPrayer(cal, 0);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	/**
	 * Get today prayer times and tomorrow Fajr time
	 *
	 * @return an array of Athan and Iqama times of today's prayers and next day Fajr
	 */
	private static String[] getTodayPrayersArrayWithFajr(AssetManager assetManager) {
		// Get a string of today prayers
		final String prayers = getTodayPrayers(assetManager);
		return prayers != null ? prayers.split(",") : new String[]{};
	}

	/**
	 * @return string of today prayers and next day Fajr prayer. String also contains current day and month
	 */
	private static String getTodayPrayers(AssetManager assets) {
		// Get current day and month
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH) + 1;

		// Get today prayer times based on the day and month
		try (BufferedReader reader = openPrayerFile(assets)) {
			String line;
			while ((line = reader.readLine()) != null) {
				final String[] fields = line.split(",");
				if (fields.length == 12 && fields[0].equals(Integer.toString(month))
						&& fields[1].equals(Integer.toString(day))) {
					// Find a line with similar current month and day
					// Get Fajr athan for the next day
					String nextDayFajr = reader.readLine();
					if (nextDayFajr == null) {
						// Reached end of file (today is last day of the year).
						// Read first line in file.
						try (BufferedReader r2 = openPrayerFile(assets)) {
							nextDayFajr = r2.readLine();
						}
					}
					final String[] splits = nextDayFajr.split(",");
					return line + "," + splits[2] + "," + splits[3];
				}
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	private static BufferedReader openPrayerFile(AssetManager assets) throws IOException {
		return new BufferedReader(new InputStreamReader(assets.open("prayerTimes.txt")));
	}

	/**
	 * Info of next prayer
	 */
	static class NextPrayer {
		private final Calendar athanCal;
		private final int index;

		private NextPrayer(Calendar athanCal, int index) {
			this.athanCal = athanCal;
			this.index = index;
		}

		Calendar getAthanCal() {
			return athanCal;
		}

		int getIndex() {
			return index;
		}
	}
}