package com.isnrv.notification;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

public class Notifications extends BroadcastReceiver {
	private static final String TAG = Notifications.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		if (!TextUtils.isEmpty(action) && Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			context.startService(new Intent(context, NotificationService.class));
		}
	}

	public static void toggle(Context context, boolean isEnabled) {
		if (isEnabled) {
			Log.i(TAG, "Enabling boot receiver");
			context.getPackageManager().setComponentEnabledSetting(
					new ComponentName(context, Notifications.class),
					PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
					PackageManager.DONT_KILL_APP);

			NotificationService.scheduleNextNotification(context);

		} else {
			Log.i(TAG, "Disabling boot receiver");
			NotificationService.cancelNotifications(context);

			context.getPackageManager().setComponentEnabledSetting(
					new ComponentName(context, Notifications.class),
					PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
					PackageManager.DONT_KILL_APP);
		}
	}
}