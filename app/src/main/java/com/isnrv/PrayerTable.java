package com.isnrv;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.isnrv.helper.PrayerTimes;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * Fragment that shows prayer times in a table highlighting next prayer
 */
@SuppressWarnings("WeakerAccess")
public class PrayerTable extends Fragment {
	public final static String TAG = "PrayerTable";
	private static final int NUM_OF_FIELDS = 11;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.prayer, container, false);
		//populateTable(view);

		parseWebsite isnrvParser = new parseWebsite(this);
		isnrvParser.setView(view);
		isnrvParser.execute();

		return view;
	}


	private static class parseWebsite extends AsyncTask<String, Void, String>
	{
		private WeakReference<PrayerTable> mPrayerTableRef;
		private WeakReference<View> mViewRef;

		private parseWebsite(PrayerTable prayerTable) {
			super();
			mPrayerTableRef = new WeakReference<>(prayerTable);
		}

		void setView(View view)
		{
			mViewRef = new WeakReference<>(view);
		}

		protected void onPreExecute()
		{
			View view = mViewRef.get();
			if(view == null){
				return;
			}

			ProgressBar progressBar = view.findViewById(R.id.progressBar);
			progressBar.setVisibility(View.VISIBLE);

			TableLayout tableLayout = view.findViewById(R.id.prayerTable);
			tableLayout.setVisibility(View.GONE);
		}


		protected String doInBackground(String... paramVarArgs)
		{
			String isnrvURL ="http://www.isnrv.org";
			PrayerTable prayerTable = mPrayerTableRef.get();
			StringBuilder results = new StringBuilder();
			if(prayerTable.getActivity() == null){
				return "";
			}

			//Connect to website
			try {
				Elements elements = Jsoup.connect(isnrvURL).get().body().getElementsByClass("prayer-time");
				if (elements.size() != NUM_OF_FIELDS) {
					return "";
				}

				for(int i=0; i<elements.size(); i++){
					results.append(elements.get(i).text());
					if (i != (NUM_OF_FIELDS - 1)) {
						results.append(",");
					}
					Log.d(TAG, "Result is: " + results.toString());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			return results.toString();
		}

		protected void onPostExecute(String results)
		{
			PrayerTable prayerTable = mPrayerTableRef.get();
			Context context = prayerTable.getContext();
			View view = mViewRef.get();
			if(context == null || view == null){
				return;
			}

			ProgressBar progressBar = mViewRef.get().findViewById(R.id.progressBar);
			progressBar.setVisibility(View.GONE);

			TableLayout tableLayout = mViewRef.get().findViewById(R.id.prayerTable);
			tableLayout.setVisibility(View.VISIBLE);

			if (results.isEmpty())
			{
				prayerTable.showErrorMessage();
				//paramString = PreferenceManager.getDefaultSharedPreferences(context).getString("prayer", "");
			}

			String[] resultsArray = results.split(",");
			if (resultsArray.length == 11) {
				prayerTable.populateTable(view, resultsArray);
			}

			/*SharedPreferences.Editor localEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
			localEditor.putString("prayer", paramString);
			localEditor.commit();*/

		}



	}

	private void populateTable(View view, String[] prayers) {
		final PrayerTimes prayerTimes = new PrayerTimes();
		//final String[] prayers = prayerTimes.getTodayPrayersArray(Objects.requireNonNull(getActivity()).getAssets());
		if (prayers.length == NUM_OF_FIELDS) {
			final int[] prayerTableCells = {R.id.textViewFajrA, R.id.textViewFajrI,
					R.id.textViewDuhrA, R.id.textViewDuhrI,
					R.id.textViewAsrA, R.id.textViewAsrI,
					R.id.textViewMagribA, R.id.textViewMagribI,
					R.id.textViewIshaA, R.id.textViewIshaI};

			//populate
			for (int i = 0; i < NUM_OF_FIELDS - 1; i++) {
				((TextView) view.findViewById(prayerTableCells[i])).setText(prayers[i]);
			}
			//set next prayer text bold
			final int nextPrayerIndex = prayerTimes.findNextPrayer(prayers);
			if (nextPrayerIndex != -1) {
				((TextView) view.findViewById(prayerTableCells[nextPrayerIndex])).setTypeface(null, Typeface.BOLD);
				((TextView) view.findViewById(prayerTableCells[nextPrayerIndex + 1])).setTypeface(null, Typeface.BOLD);
			}
		}
	}

	private void showErrorMessage()
	{
		Toast.makeText(getContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
	}
}