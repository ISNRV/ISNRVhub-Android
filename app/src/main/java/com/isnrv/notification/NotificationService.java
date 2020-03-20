package com.isnrv.notification;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

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
	private static final String NAME = NotificationService.class.getSimpleName();

	private static final String TITLE = "title";
	private static final String TEXT = "text";
	private static final String TIME = "time";

	private static final int TwO_MINUTES = 2 * 60 * 1000;

	public NotificationService() {
		super(NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (preferences.getBoolean("notifications", true)) {
			final String title = preferences.getString(TITLE, null);
			final String text = preferences.getString(TEXT, null);

			if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(text)) {
				final LocalTime time = DateTime.parse(preferences.getString(TIME, null)).toLocalTime();
				// Solve problem of repeating notification
				if (Math.abs(LocalTime.now().getMillisOfDay() - time.getMillisOfDay()) < TwO_MINUTES) {
					showNotification(title, text);
				}
			}
			scheduleNextNotification(this);
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
		final Notification notification = new NotificationCompat.Builder(this, NAME)
				.setContentTitle(title)
				.setContentText(text)
				.setSmallIcon(R.drawable.logo)
				.setAutoCancel(true)
				.setContentIntent(pendingIntent).build();

		final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if (manager != null) {
			manager.notify(R.string.notification_number, notification);
		}
	}

	/**
	 * Create Notification channel for SDK version > 28
	 */
	private static void createNotificationChannel(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			final NotificationChannel channel = new NotificationChannel(NAME, "Prayer Times", NotificationManager.IMPORTANCE_DEFAULT);
			channel.setDescription("Prayer Times Notification Channel");
			final NotificationManager manager = context.getSystemService(NotificationManager.class);
			if (manager != null) {
				manager.createNotificationChannel(channel);
			}
		}
	}


	/**
	 * Schedule next notification
	 */
	static void scheduleNextNotification(Context context) {
		createNotificationChannel(context);

		final Prayer nextPrayer = IqamaTimes.getNextPrayer();
		if (nextPrayer != null) {
			final int index = nextPrayer.getIndex();
			final DateTime athanTime = nextPrayer.getAthanTime();
			final DateTime iqamaTime = nextPrayer.getIqamaTime();
			String title = context.getString(R.string.prayer_noti_title).replace("$name", context.getResources().getStringArray(R.array.prayers)[index]);
			String text = context.getString(R.string.prayer_noti_text).replace("$time", iqamaTime.toString("h:mm a"));

			final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
			editor.putString(TIME, athanTime.toString()).putString(TITLE, title).putString(TEXT, text).apply();

			final PendingIntent pendingIntent = getPendingIntent(context);
			final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			if (manager != null) {
				manager.set(AlarmManager.RTC_WAKEUP, athanTime.getMillis(), pendingIntent);
			}
		}
	}

	public static void cancelNotifications(Context context) {
		final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (manager != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				manager.deleteNotificationChannel(NAME);
			}
			manager.cancel(R.string.notification_number);

		}
		final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (alarmManager != null) {
			alarmManager.cancel(getPendingIntent(context));
		}
	}

	private static PendingIntent pendingIntent;

	private static PendingIntent getPendingIntent(Context context) {
		if (pendingIntent != null) {
			return pendingIntent;
		}

		final Intent intent = new Intent(context.getApplicationContext(), NotificationService.class);
		pendingIntent = PendingIntent.getService(context.getApplicationContext(), 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}
}