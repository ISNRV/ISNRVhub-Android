package com.isnrv.notification;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.isnrv.Main;
import com.isnrv.R;
import com.isnrv.core.IqamaTimes;
import com.isnrv.core.Prayer;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

/**
 * Service to show notifications at prayer times
 */
public class NotificationService extends IntentService {
	private static final String KEY = NotificationService.class.getSimpleName();
	private static final String TITLE = "title";
	private static final String TEXT = "text";
	private static final String TIME = "time";
	private static final int TwO_MINUTES = 2 * 60 * 1000;

	public NotificationService() {
		super(KEY);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// Should be using Intent extras here, but it's not working for some reason
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String title = preferences.getString(TITLE, null);
		String text = preferences.getString(TEXT, null);

		if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(text)) {
			final LocalTime time = DateTime.parse(preferences.getString(TIME, null)).toLocalTime();
			// Solve problem of repeating notification
			if (Math.abs(LocalTime.now().getMillisOfDay() - time.getMillisOfDay()) < TwO_MINUTES) {
				showNotification(title, text);
			}
		}

		final Prayer nextPrayer = IqamaTimes.getNextPrayer();
		if (nextPrayer != null) {
			final int index = nextPrayer.getIndex();
			final DateTime athanTime = nextPrayer.getAthanTime();
			final DateTime iqamaTime = nextPrayer.getIqamaTime();
			title = getString(R.string.prayer_noti_title).replace("$name", getResources().getStringArray(R.array.prayers)[index]);
			text = getString(R.string.prayer_noti_text).replace("$time", iqamaTime.toString("h:mm a"));
			scheduleNextNotification(athanTime, title, text);
		}

	}

	/**
	 * Show a notification of the prayer
	 *
	 * @param title notification title
	 * @param text  notification text
	 */
	private void showNotification(String title, String text) {
		final Intent intent = new Intent(this, Main.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		final Notification notification = new Notification.Builder(this)
				.setContentTitle(title)
				.setContentText(text)
				.setSmallIcon(R.drawable.logo)
				.setAutoCancel(true)
				.setContentIntent(pendingIntent).build();

		final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if(manager != null) {
			manager.notify(R.string.notification_number, notification);
		}
	}

	/**
	 * Schedule next notification
	 */
	private void scheduleNextNotification(DateTime notificationTime, String title, String text) {
		final Intent intent = new Intent(getApplicationContext(), NotificationService.class);
		final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putString(TIME, notificationTime.toString()).putString(TITLE, title).putString(TEXT, text).apply();


		final PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
		final AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		if (manager != null) {
			manager.set(AlarmManager.RTC_WAKEUP, notificationTime.getMillis(), pendingIntent);
		}
	}
}