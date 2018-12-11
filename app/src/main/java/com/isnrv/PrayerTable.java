package com.isnrv;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.isnrv.helper.PrayerTimes;

import java.util.Objects;

/**
 * Fragment that shows prayer times in a table highlighting next prayer
 */
@SuppressWarnings("WeakerAccess")
public class PrayerTable extends Fragment {
	private static final int NUM_OF_FIELDS = 10;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.prayer, container, false);
		populateTable(view);
		return view;
	}

	private void populateTable(View view) {
		final PrayerTimes prayerTimes = new PrayerTimes();
		final String[] prayers = prayerTimes.getTodayPrayersArray(Objects.requireNonNull(getActivity()).getAssets());
		if (prayers.length == NUM_OF_FIELDS) {
			final int[] prayerTableCells = {R.id.textViewFajrA, R.id.textViewFajrI,
					R.id.textViewDuhrA, R.id.textViewDuhrI,
					R.id.textViewAsrA, R.id.textViewAsrI,
					R.id.textViewMagribA, R.id.textViewMagribI,
					R.id.textViewIshaA, R.id.textViewIshaI};

			//populate
			for (int i = 0; i < NUM_OF_FIELDS; i++) {
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
}