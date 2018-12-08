package com.isnrv.helper;

import com.isnrv.ScheduledService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartOnBoot extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("TAG", "StartOnBoot");
		Intent serviceIntent = new Intent(context, ScheduledService.class);
		context.startService(serviceIntent);
	}
}