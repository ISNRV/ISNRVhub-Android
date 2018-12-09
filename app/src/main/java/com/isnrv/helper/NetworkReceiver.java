package com.isnrv.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * This BroadcastReceiver intercepts the android.net.ConnectivityManager.CONNECTIVITY_ACTION,
 * which indicates a connection change. It checks whether the type is TYPE_WIFI.
 * If it is, it checks whether Wi-Fi is connected and sets the wifiConnected flag in the
 * main activity accordingly.
 */
public class NetworkReceiver extends BroadcastReceiver {
	private static final String TAG = NetworkReceiver.class.getCanonicalName();
	private boolean isConnected = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive");
		final NetworkInfo activeInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		isConnected = activeInfo != null && activeInfo.isConnected();
	}

	@SuppressWarnings("unused")
	public boolean isConnected() {
		return isConnected;
	}
}