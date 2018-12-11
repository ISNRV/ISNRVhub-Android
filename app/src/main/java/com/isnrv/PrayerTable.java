package com.isnrv;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment that shows prayer times in a table highlighting next prayer
 */
@SuppressWarnings("WeakerAccess")
public class PrayerTable extends Fragment {
	public static final String TAG = PrayerTable.class.getCanonicalName();
	private static final int NUM_OF_FIELDS = 11;
	private static final int[] prayerTableCells = {R.id.textViewFajrA, R.id.textViewFajrI,
			R.id.textViewDuhrA, R.id.textViewDuhrI,
			R.id.textViewAsrA, R.id.textViewAsrI,
			R.id.textViewMagribA, R.id.textViewMagribI,
			R.id.textViewIshaA, R.id.textViewIshaI};

	private static void populateTable(final View view, final String[] prayers) {
		if (prayers.length != NUM_OF_FIELDS) return;
		//populate
		for (int i = 0; i < NUM_OF_FIELDS - 1; i++) {
			((TextView) view.findViewById(prayerTableCells[i])).setText(prayers[i]);
		}
		//set next prayer text bold
		final int nextPrayerIndex = findNextPrayer(prayers);
		if (nextPrayerIndex != -1) {
			((TextView) view.findViewById(prayerTableCells[nextPrayerIndex])).setTypeface(null, Typeface.BOLD);
			((TextView) view.findViewById(prayerTableCells[nextPrayerIndex + 1])).setTypeface(null, Typeface.BOLD);
		}
	}

	/**
	 * Find which prayer is next by comparing iqama time with current time
	 *
	 * @return prayer index of the next prayer
	 */
	private static int findNextPrayer(final String[] prayerTimes) {
		// Set data format
		final SimpleDateFormat format = new SimpleDateFormat("hh:mm aa", Locale.US);
		Date prayerDate;
		try {
			// Get current time
			final Date currentTime = format.parse(format.format(Calendar.getInstance().getTime()));
			// Get index of next prayer (comparison is based on iqama times)
			for (int i = 1; i < prayerTimes.length; i = i + 2) {
				prayerDate = format.parse(prayerTimes[i]);
				if (currentTime.before(prayerDate)) {
					Log.d(TAG, prayerTimes[i]);
					return (i - 1);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return -1;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.prayer, container, false);
		new WebsiteParser(this, view).execute();
		return view;
	}

	private static class WebsiteParser extends AsyncTask<String, Void, String> {
		private final WeakReference<PrayerTable> tableReference;
		private final WeakReference<View> viewReference;

		private WebsiteParser(PrayerTable prayerTable, View view) {
			tableReference = new WeakReference<>(prayerTable);
			viewReference = new WeakReference<>(view);
		}

		protected void onPreExecute() {
			final View view = viewReference.get();
			if (view == null) return;
			view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
			view.findViewById(R.id.prayerTable).setVisibility(View.GONE);
		}

		protected String doInBackground(String... paramVarArgs) {
			final StringBuilder results = new StringBuilder();
			try {
				//Connect to website
				final Elements elements = Jsoup.connect("http://www.isnrv.org").get().body().getElementsByClass("prayer-time");
				if (elements.size() != NUM_OF_FIELDS) return "";

				for (int i = 0; i < elements.size(); i++) {
					results.append(elements.get(i).text());
					if (i != (NUM_OF_FIELDS - 1)) {
						results.append(",");
					}
					Log.d(TAG, "Result is: " + results.toString());
				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
			return results.toString();
		}

		protected void onPostExecute(String results) {
			final Context context = tableReference.get().getContext();
			final View view = viewReference.get();

			if (context == null || view == null) return;

			view.findViewById(R.id.progressBar).setVisibility(View.GONE);
			view.findViewById(R.id.prayerTable).setVisibility(View.VISIBLE);

			if (results.isEmpty()) {
				Toast.makeText(context, context.getString(R.string.connection_error), Toast.LENGTH_LONG).show();
			} else {
				PrayerTable.populateTable(view, results.split(","));
			}
		}
	}
}