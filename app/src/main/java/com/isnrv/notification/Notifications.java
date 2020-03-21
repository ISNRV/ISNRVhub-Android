package com.isnrv.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.isnrv.Main;
import com.isnrv.R;
import com.isnrv.core.IqamaTimes;
import com.isnrv.core.Prayer;

import org.joda.time.DateTime;

public class Notifications extends BroadcastReceiver {
	private static final String NAME = Notifications.class.getName();

	private static final String TITLE = "title";
	private static final String TEXT = "text";

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			toggle(context, PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notifications", false));
		}
	}

	/**
	 * Enable/Disable prayer time notifications based on the notifications setting
	 */
	public static void toggle(Context context, boolean isEnabled) {
		if (isEnabled) {
			Log.i(NAME, "Enabling boot receiver");
			context.getPackageManager().setComponentEnabledSetting(
					new ComponentName(context, Notifications.class),
					PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
					PackageManager.DONT_KILL_APP);

			scheduleNextNotification(context);

		} else {
			Log.i(NAME, "Disabling boot receiver");
			context.getPackageManager().setComponentEnabledSetting(
					new ComponentName(context, Notifications.class),
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
					PackageManager.DONT_KILL_APP);

			cancelNotifications(context);
		}
	}


	/**
	 * Schedule next prayer notification
	 */
	static void scheduleNextNotification(Context context) {
		final Prayer nextPrayer = IqamaTimes.getNextPrayer();
		if (nextPrayer != null) {
			final int index = nextPrayer.getIndex();
			final DateTime athanTime = nextPrayer.getAthanTime();
			final DateTime iqamaTime = nextPrayer.getIqamaTime();
			final String title = context.getString(R.string.prayerNotificationTitle).replace("$name", context.getResources().getStringArray(R.array.prayers)[index]);
			final String text = context.getString(R.string.prayerNotificationText).replace("$time", iqamaTime.toString("h:mm a"));

			Log.i(NAME, "Scheduling next notification at " + athanTime + "with title (" + title + ") and text (" + text + ")");
			final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			if (manager != null) {
				if (Build.VERSION.SDK_INT >= 23) {
					manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, athanTime.getMillis(), buildAlarmIntent(context, title, text));
				} else {
					manager.set(AlarmManager.RTC_WAKEUP, athanTime.getMillis(), buildAlarmIntent(context, title, text));
				}
			}
		}
	}

	/**
	 * Cancel any scheduled prayer time notification
	 */
	private static void cancelNotifications(Context context) {
		final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (alarmManager != null && alarmIntent != null) {
			Log.i(NAME, "Cancelling next scheduled notification");
			alarmManager.cancel(alarmIntent);
		}
	}

	private static PendingIntent alarmIntent;

	private static PendingIntent buildAlarmIntent(Context context, String title, String text) {
		final Intent intent = new Intent(context, Notifier.class);
		intent.putExtra(TITLE, title);
		intent.putExtra(TEXT, text);
		alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return alarmIntent;
	}

	/**
	 * Notification broadcast receiver triggered at set time to show prayer time notification
	 */
	public static class Notifier extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			final Bundle extras = intent.getExtras();
			if (extras != null) {
				final String title = extras.getString(TITLE, null);
				final String text = extras.getString(TEXT, null);
				if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(text)) {
					showNotification(context, title, text);
				}
			}
			scheduleNextNotification(context);
		}

		/**
		 * Show a notification of the prayer
		 *
		 * @param title notification title
		 * @param text  notification text
		 */
		private void showNotification(Context context, String title, String text) {
			final String channelId = "Prayer time notifications";
			final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			if (manager != null) {
				// Create notification channel for SDK version >= 28
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					final NotificationChannel channel = new NotificationChannel(channelId, "Prayer Times", NotificationManager.IMPORTANCE_DEFAULT);
					manager.createNotificationChannel(channel);
				}
				final Intent intent = new Intent(context, Main.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				final Notification notification = new NotificationCompat.Builder(context, channelId)
						.setContentTitle(title)
						.setContentText(text)
						.setSmallIcon(R.drawable.large_logo)
						.setAutoCancel(true)
						.setContentIntent(pendingIntent).build();
				Log.i(NAME, "Showing scheduled notification with title (" + title + ") and text (" + text + ")");
				manager.notify(0, notification);
			}
		}
	}
}