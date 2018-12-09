package com.isnrv;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.isnrv.helper.PrayerAlarm;
import com.isnrv.helper.PrayerAlarm.NextPrayer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ScheduledService extends IntentService {
	private static final String TAG = ScheduledService.class.getCanonicalName();
	private static final String KEY = "nextNotification";

	public ScheduledService() {
		super("My service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent()");
		// Check if it is time to show prayer notification
		Calendar currentCal = Calendar.getInstance();
		String nextNotification = PreferenceManager.getDefaultSharedPreferences(this).getString(KEY, "");
		if (nextNotification != null && !nextNotification.isEmpty()) {
			final String[] contents = nextNotification.split(",");
			String time = contents[0];
			String title = contents[1];
			String text = contents[2];
			if (isEqual(time, calendarToString(currentCal))) {
				showNotification(title, text);
			}
		}

		// Get next prayer
		NextPrayer nextPrayer = new PrayerAlarm().findNextAthan(getAssets());
		Calendar nextPrayerCal = nextPrayer.getAthanCal();

		// Store next prayer data to show next time
		nextNotification = formatNextNotification(nextPrayerCal, nextPrayer.getIndex());
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
		editor.putString(KEY, nextNotification);
		editor.apply();

		// Set new alarm
		setNewAlarm(nextPrayerCal);

	}

	// Show a notification of the prayer
	private void showNotification(String title, String text) {
		Log.d(TAG, "showNotification()");
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);

		// Set notification fields
		NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
				.setContentTitle(title)
				.setContentText(text)
				.setSmallIcon(android.R.drawable.arrow_down_float)
				.setContentIntent(pendingIntent);

		// Send the notification.
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		int notificationNumber = R.string.notification_number;
		notificationManager.notify(notificationNumber, notification.build());
	}

	// Set a new alarm for next notification
	private void setNewAlarm(Calendar notificationTime) {
		//start ScheduledService for next prayer
		AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), ScheduledService.class);
		PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);

		mgr.set(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), pendingIntent);
	}

	// Format data for the next notification (separated by comma):
	// - Expected notification start time
	// - Notification title
	// - Notification text
	private String formatNextNotification(Calendar nextPrayerCal, int index) {
		String[] prayersTitle = getResources().getStringArray(R.array.prayers);
		String title = getResources().getString(R.string.prayer_noti_title);
		String text = getResources().getString(R.string.prayer_noti_text);
		if (index >= 0 && index <= 4) {
			title = title.replace("$name", prayersTitle[index]);
		}
		return calendarToString(nextPrayerCal) + "," + title + "," + text;
	}

	// Convert a calendar to string format
	private String calendarToString(Calendar cal) {
		return cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
	}

	// Compare two strings of calendar using the format (hh:mm:ss), 2 minutes or less is assumed to be equal
	private boolean isEqual(String time1, String time2) {
		SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss", Locale.US);
		try {
			Date time1Date = format.parse(time1);
			Date time2Date = format.parse(time2);
			// compare the two times
			long diff = time1Date.getTime() - time2Date.getTime();
			// If difference is less than 2 minutes, they are roughly equal
			if (diff < 1000 * 60 * 2) {
				return true;
			}
		} catch (Exception e) {
			// do nothing
		}
		return false;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "Stopped");
	}
}