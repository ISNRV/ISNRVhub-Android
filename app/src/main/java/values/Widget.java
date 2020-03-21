package values;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.isnrv.Main;
import com.isnrv.R;
import com.isnrv.core.AthanTimes;
import com.isnrv.core.IqamaTimes;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

/**
 * App Widget
 */
public class Widget extends AppWidgetProvider {
	private static final int[] ATHAN_TIMES = {R.id.fajr_athan, R.id.dhuhr_athan, R.id.asr_athan, R.id.maghrib_athan, R.id.isha_athan};
	private static final int[] IQAMA_TIMES = {R.id.fajr_iqama, R.id.dhuhr_iqama, R.id.asr_iqama, R.id.maghrib_iqama, R.id.isha_iqama};
	private static final int[] LABELS = {R.id.fajr_label, R.id.dhuhr_label, R.id.asr_label, R.id.maghrib_label,
			R.id.isha_label};

	private static void update(Context context, AppWidgetManager manager, int widgetId) {
		final Intent intent = new Intent(context, Main.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
		final DateTime today = DateTime.now();
		final LocalTime[] athanTimes = AthanTimes.get(today);
		final LocalTime[] iqamaTimes = IqamaTimes.get(today);

		views.setOnClickPendingIntent(R.id.widget, pendingIntent);
		views.setTextViewText(R.id.date, DateTime.now().toString(context.getString(R.string.dateFormat)));

		for (int i = 0; i < 5; i++) {
			final String[] prayerNames = context.getResources().getStringArray(R.array.prayers);
			views.setTextViewText(LABELS[i], prayerNames[i]);
			views.setTextViewText(ATHAN_TIMES[i], athanTimes[i].toString("h:mm"));
			views.setTextViewText(IQAMA_TIMES[i], iqamaTimes[i].toString("h:mm"));
		}

		manager.updateAppWidget(widgetId, views);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager manager, int[] widgetIds) {
		// Update all active widgets
		for (int widgetId : widgetIds) {
			update(context, manager, widgetId);
		}
	}

	@Override
	public void onEnabled(Context context) {
		// Enter relevant functionality for when the first widget is created
	}

	@Override
	public void onDisabled(Context context) {
		// Enter relevant functionality for when the last widget is disabled
	}
}