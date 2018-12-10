package com.isnrv.helper;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PrayerTimes {
	private static final String TAG = PrayerTimes.class.getCanonicalName();

	// Returns an array consists athan and iqama of every prayer of current day with next day Fajr.
	private String[] getTodayPrayersArrayWithFajr(AssetManager assets) {
		// Get a string of today prayers
		final String prayers = getTodayPrayers(assets);
		return prayers != null ? prayers.split(",") : new String[]{};
	}

	// Returns an array consists athan and iqama of every prayer of current day.
	public String[] getTodayPrayersArray(AssetManager assets) {
		String[] tempList = getTodayPrayersArrayWithFajr(assets);
		// Copy the prayer list while ignoring first two elements (month and day)
		// and last element in the array (next day fajr)
		return Arrays.copyOfRange(tempList, 2, tempList.length - 2);
	}

	// Find which prayer is next by comparing athan time with current time.
	// Return time of next prayer
	NextPrayer findNextAthan(AssetManager assets) {
		String[] prayerTimes = getTodayPrayersArrayWithFajr(assets);
		// Set data format
		SimpleDateFormat format = new SimpleDateFormat("M/d/y, hh:mm aa", Locale.US);
		Date prayerDate;
		Calendar cal = Calendar.getInstance();
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

	// Find which prayer is next by comparing iqama time with current time.
	// Return prayer index of the next prayer
	public int findNextPrayer(String[] prayerTimes) {
		// Set data format
		SimpleDateFormat format = new SimpleDateFormat("hh:mm aa", Locale.US);
		Date prayerDate;
		try {
			// Get current time
			Calendar cal = Calendar.getInstance();
			Date currentTime = format.parse(format.format(cal.getTime()));
			// Get index of next prayer (comparison is based on iqama times)
			for (int i = 1; i < prayerTimes.length; i = i + 2) {
				prayerDate = format.parse(prayerTimes[i]);
				if (currentTime.before(prayerDate)) {
					Log.d(TAG, prayerTimes[i]);
					return (i - 1);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return -1;
	}

	// Returns a string of today prayers and next day Fajr prayer. String also contains current day and month
	private String getTodayPrayers(AssetManager assets) {
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

	private BufferedReader openPrayerFile(AssetManager assets) throws IOException {
		return new BufferedReader(new InputStreamReader(assets.open("prayerTimes.txt")));
	}

	class NextPrayer {
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