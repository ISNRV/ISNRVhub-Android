package com.isnrv.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import android.content.res.AssetManager;
import android.util.Log;


public class PrayerAlarm {
	
	public class NextPrayer{
		public Calendar athanCal;
		public String iqamaTime;
		public int index;
		public NextPrayer(Calendar athanCal, String iqamaTime, int index){
			this.athanCal = athanCal;
			this.iqamaTime = iqamaTime;
			this.index = index;
		}
	}
	
	// Read prayer table stored in assets. 
	private BufferedReader openPrayerFile(AssetManager assets){
		// Open prayer table from Assets
		InputStream inputStream;
		InputStreamReader reader = null;
		try {
			inputStream = assets.open("prayerTimes.txt");
			reader = new InputStreamReader(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return new BufferedReader(reader);
	}


	// Returns a string of today prayers and next day Fajr prayer. String also contains current day and month
	private String getTodayPrayers(AssetManager assets) {
		// Get current day and month
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH) + 1;
		
		// Get today prayer times based on the day and month
		BufferedReader br = openPrayerFile(assets);
		String line;
		String[] fields;
		try {
			do {
				line = br.readLine();
				if (line != null) {
					fields = line.split(",");
					if (fields.length == 12) {
						// Find a line with similar current month and day 
						if (fields[0].equals(Integer.toString(month))
								&& fields[1].equals(Integer.toString(day))) {
							// Get Fajr athan for the next day
							String nextDayFajr = br.readLine();
							if(nextDayFajr == null){
								// Reached end of file (today is last day of the year). Read first line in file.
								br = openPrayerFile(assets);
								nextDayFajr = br.readLine();
							}
							return line + "," + nextDayFajr.split(",")[2] + "," + nextDayFajr.split(",")[3];
						}
					}
				}
			} while (line != null);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return "";
	}
	// Returns an array consists athan and iqama of every prayer of current day with next day Fajr.
	public String[] getTodayPrayersArrayWithFajr(AssetManager assets){
		// Get a string of today prayers
		String prayers = getTodayPrayers(assets);
		return prayers.split(",");
	}
	
	// Returns an array consists athan and iqama of every prayer of current day.
	public String[] getTodayPrayersArray(AssetManager assets){
		String tempList[] = getTodayPrayersArrayWithFajr(assets);
		if(tempList != null)
			// Copy the prayerlist while ignoring first two elements (month and day) 
			// and last element in the array (next day fajr)
			return Arrays.copyOfRange(tempList, 2, tempList.length-2);
		return null;
	}
	
	// Find which prayer is next by comparing athan time with current time.
	// Return time of next prayer
	public NextPrayer findNextAthan(AssetManager assets) {
		String[] prayerTimes = getTodayPrayersArrayWithFajr(assets);
		// Set data format
		SimpleDateFormat format = new SimpleDateFormat("M/d/y, hh:mm aa");
		Date prayerDate = null;
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = Integer.parseInt(prayerTimes[0]);
		int day = Integer.parseInt(prayerTimes[1]);
		try {
			// Get current time 
			Date currentTime = format.parse(format.format(cal.getTime()));
			// Get index of next prayer (comparison is based on athan times)
			for (int i = 2; i < prayerTimes.length-2; i = i + 2) {
				prayerDate = format.parse(month + "/" + day + "/" + year + ", " + prayerTimes[i]);
				if (currentTime.before(prayerDate)) {
					cal.setTime(prayerDate);
					return new NextPrayer(cal, prayerTimes[i+1], (i-2)/2);
				}
			}
			// Next prayer is tomorrow's Fajr. Set fields for next day.
			prayerDate = format.parse(month + "/" + day + "/" + year + ", " + prayerTimes[prayerTimes.length-2]);
			cal.setTime(prayerDate);
			// Add one day to the calendar
			cal.add(Calendar.DATE, 1); 
			return new NextPrayer(cal, prayerTimes[prayerTimes.length-1], 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Find which prayer is next by comparing iqama time with current time.
	// Return prayer index of the next prayer
	public int findNextPrayer(String[] prayerTimes) {
		// Set data format
		SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
		Date prayerDate = null;
		try {
			// Get current time
			Calendar cal = Calendar.getInstance();
			Date currentTime = format.parse(format.format(cal.getTime()));
			// Get index of next prayer (comparison is based on iqama times)
			for (int i = 1; i < prayerTimes.length; i = i + 2) {
				prayerDate = format.parse(prayerTimes[i]);
				if (currentTime.before(prayerDate)) {
					System.out.println(prayerTimes[i]);
					return (i - 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	
}


