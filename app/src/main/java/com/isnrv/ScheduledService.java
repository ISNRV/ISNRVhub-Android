package com.isnrv;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.isnrv.helper.PrayerAlarm;
import com.isnrv.helper.PrayerAlarm.NextPrayer;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class ScheduledService extends IntentService {
	private NotificationManager mNM;
	private int NOTIFICATION = R.string.notification_number;
	private final String TAG = "ScheduledService";
	
	public ScheduledService() {
	    super("My service");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
	    Log.d(TAG, "onHandleIntent()");
	    // Check if it is time to show prayer notification
	    Calendar currentCal = Calendar.getInstance();
	    String nextNotification = PreferenceManager.getDefaultSharedPreferences(this).getString("nextNotification", "");
	    if(!nextNotification.isEmpty()){
	    	String notiTime = nextNotification.split(",")[0],
	    		   notiTitle = nextNotification.split(",")[1],
	    		   notiText = nextNotification.split(",")[2];
	    	if(isEqual(notiTime, calendarToString(currentCal))){
	    	    showNotification(notiTitle, notiText);
	    	}
	    }
	    
	    // Get next prayer
	    NextPrayer nextPrayer = new PrayerAlarm().findNextAthan(getAssets());
	    Calendar nextPrayerCal = nextPrayer.athanCal;
	    
	    // Store next prayer data to show next time
	    nextNotification = formatNextNotification(nextPrayerCal, nextPrayer.index, nextPrayer.iqamaTime);
	    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString("nextNotification", nextNotification);
        editor.commit();
        
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
		mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNM.notify(NOTIFICATION, notification.build());
	}
	
	// Set a new alarm for next notification
	private void setNewAlarm(Calendar notificationTime){
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
	private String formatNextNotification(Calendar nextPrayerCal, int index, String iqamaTime){
		String prayersTitle[] = getResources().getStringArray(R.array.prayers);
		String title = getResources().getString(R.string.prayer_noti_title);
		String text = getResources().getString(R.string.prayer_noti_text);
		if(index >= 0 && index <= 4){
			title.replace("$name", prayersTitle[index]);
		}
		return calendarToString(nextPrayerCal) + "," + title + "," + text;
	}
	
	// Convert a calendar to string format
	private String calendarToString(Calendar cal){
		return cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
	}
	
	// Compare two strings of calendar using the format (hh:mm:ss), 2 minutes or less is assumed to be equal
	private boolean isEqual(String time1, String time2){
		SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
		try {
			Date time1Date = format.parse(time1);
			Date time2Date = format.parse(time2);
			// compare the two times
			long diff = time1Date.getTime() - time2Date.getTime();
			// If difference is less than 2 minutes, they are roughly equal
			if(diff < 1000*60*2){
				return true;
			}
		}
		catch (Exception e) {} 
		return false;	
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "Stopped");
	}
}