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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
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
        final FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(new ListViewAdapter());
        return view;
    }


    private class ListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int HEADER_VIEW = 0;
        private static final int PRAYER_VIEW = 1;
        private static final int EXTRA_FRIDAY_VIEW = 2;
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
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == PRAYER_VIEW) {
                final View view = LayoutInflater.from(getContext()).inflate(R.layout.prayer_card, parent, false);
                return new PrayerViewHolder(view);
            } else if (viewType == HEADER_VIEW) {
                final View view = LayoutInflater.from(getContext()).inflate(R.layout.header, parent, false);
                return new HeaderViewHolder(view);
            } else {
                final View view = LayoutInflater.from(getContext()).inflate(R.layout.extra_friday, parent, false);
                return new ExtraFridayViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof PrayerViewHolder) {
                final int index = position < 3 ? position - 1 : position - 2;
                final PrayerViewHolder holder = (PrayerViewHolder) viewHolder;
                holder.athanTime.setText(athanTimes[index].toString(FORMAT));
                holder.iqamaTime.setText(iqamaTimes[index].toString(FORMAT));

                if (index == Prayer.DHUHR && date.getDayOfWeek() == DateTimeConstants.FRIDAY) {
                    // Change Duhr prayer name to Jumaa on Fridays
                    holder.prayerName.setText(getString(R.string.Friday));
                } else {
                    holder.prayerName.setText(getResources().getStringArray(R.array.prayers)[index]);
                }

                if (dayIndex == 0 && nextPrayer == index) {
                    // Highlight next prayer for today
                    holder.athanTime.setTypeface(null, Typeface.BOLD);
                    holder.iqamaTime.setTypeface(null, Typeface.BOLD);
                }
            } else if (viewHolder instanceof ExtraFridayViewHolder &&
                    date.getDayOfWeek() == DateTimeConstants.FRIDAY) {
                final LocalTime extraFridayPrayer = IqamaTimes.getExtraFridayPrayerTime(date);
                if (extraFridayPrayer != null) {
                    final String text = getString(R.string.extraFriday).replace("$time", extraFridayPrayer.toString(FORMAT));
                    ((ExtraFridayViewHolder) viewHolder).extraFridayTime.setText(text);
                    ((ExtraFridayViewHolder) viewHolder).extraFridayTime.setVisibility(View.VISIBLE);
                }

            }
        }

        @Override
        public int getItemCount() {
            return 7;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return HEADER_VIEW;
            } else if (position == 3) {
                return EXTRA_FRIDAY_VIEW;
            } else {
                return PRAYER_VIEW;
            }
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

        private class PrayerViewHolder extends RecyclerView.ViewHolder {
            final TextView prayerName;
            final TextView athanTime;
            final TextView iqamaTime;


            private PrayerViewHolder(@NonNull View view) {
                super(view);
                prayerName = view.findViewById(R.id.prayer_name);
                athanTime = view.findViewById(R.id.athan_time);
                iqamaTime = view.findViewById(R.id.iqama_time);
            }
        }

        private class HeaderViewHolder extends RecyclerView.ViewHolder {
            private HeaderViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

        private class ExtraFridayViewHolder extends RecyclerView.ViewHolder {
            final TextView extraFridayTime;

            private ExtraFridayViewHolder(@NonNull View itemView) {
                super(itemView);
                extraFridayTime = itemView.findViewById(R.id.extraFriday);

            }
        }
    }
}