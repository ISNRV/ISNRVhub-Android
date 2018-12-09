package com.isnrv.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.isnrv.ScheduledService;

public class StartOnBoot extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (!TextUtils.isEmpty(action) && Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			context.startService(new Intent(context, ScheduledService.class));
		}
	}
}