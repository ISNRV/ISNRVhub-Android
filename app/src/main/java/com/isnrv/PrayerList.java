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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.isnrv.core.AthanTimes;
import com.isnrv.core.IqamaTimes;
import com.isnrv.core.Prayer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalTime;

/**
 * Fragment that shows prayer times in a table highlighting next prayer
 */
public class PrayerList extends Fragment {
	private static final String FORMAT = "h:mm a";
	private static final String DAY_INDEX = "day index";

	private int dayIndex;
	private DateTime date;

	static PrayerList newInstance(int sectionNumber) {
		final PrayerList section = new PrayerList();
		final Bundle args = new Bundle();
		args.putInt(DAY_INDEX, sectionNumber);
		section.setArguments(args);
		return section;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			dayIndex = getArguments().getInt(DAY_INDEX);
			date = DateTime.now().plusDays(dayIndex);
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.prayer_list, container, false);
		final RecyclerView listView = view.findViewById(R.id.prayers);
		listView.setLayoutManager(new LinearLayoutManager(getContext()));
		listView.setAdapter(new ListViewAdapter());

		if (date.getDayOfWeek() == DateTimeConstants.FRIDAY) showExtraFridayPrayerTime(view);
//
		return view;
	}

	private void showExtraFridayPrayerTime(View view) {
		final LocalTime extraFridayPrayer = IqamaTimes.getExtraFridayPrayerTime(date);
		final TextView textView = view.findViewById(R.id.extraFriday);
		if (extraFridayPrayer != null) {
			final String text = getString(R.string.extraFriday).replace("$time", extraFridayPrayer.toString(FORMAT));
			textView.setText(text);
			textView.setVisibility(View.VISIBLE);
		}
	}

	private class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.CardViewHolder> {
		final LocalTime[] iqamaTimes;
		final LocalTime[] athanTimes;
		final int nextPrayer;

		private ListViewAdapter() {
			athanTimes = AthanTimes.get(date);
			iqamaTimes = IqamaTimes.get(date);
			nextPrayer = findNextPrayer(iqamaTimes);
		}

		@NonNull
		@Override
		public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			final View view = LayoutInflater.from(getContext()).inflate(R.layout.prayer_card, parent, false);
			return new CardViewHolder(view);
		}

		@Override
		public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
			holder.athanTime.setText(athanTimes[position].toString(FORMAT));
			holder.iqamaTime.setText(iqamaTimes[position].toString(FORMAT));

			if (position == Prayer.DHUHR && date.getDayOfWeek() == DateTimeConstants.FRIDAY) {
				// Change Duhr prayer name to Jumaa on Fridays
				holder.prayerName.setText(getString(R.string.Friday));
			} else {
				holder.prayerName.setText(getResources().getStringArray(R.array.prayers)[position]);
			}

			if (dayIndex == 0 && nextPrayer == position) {
				// Highlight next prayer for today
				holder.athanTime.setTypeface(null, Typeface.BOLD);
				holder.iqamaTime.setTypeface(null, Typeface.BOLD);
			}
		}

		@Override
		public int getItemCount() {
			return 5;
		}

		/**
		 * Find which prayer is next by comparing iqama time with current time
		 *
		 * @return prayer index of the next prayer
		 */
		private int findNextPrayer(final LocalTime[] prayerTimes) {
			final LocalTime currentTime = LocalTime.now();
			for (int i = 0; i < prayerTimes.length; i++) {
				if (currentTime.isBefore(prayerTimes[i])) {
					return i;
				}
			}
			return -1;
		}

		private class CardViewHolder extends RecyclerView.ViewHolder {
			final TextView prayerName;
			final TextView athanTime;
			final TextView iqamaTime;

			private CardViewHolder(@NonNull View view) {
				super(view);
				prayerName = view.findViewById(R.id.prayer_name);
				athanTime = view.findViewById(R.id.athan_time);
				iqamaTime = view.findViewById(R.id.iqama_time);
			}
		}

	}
}