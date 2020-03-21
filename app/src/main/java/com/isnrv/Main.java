package com.isnrv;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * Main Activity
 */
public class Main extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

	private static final int MAX_DAYS = 366;
	private final DateTime now = DateTime.now();
	private ViewPager pager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setTitle(R.string.prayerTimes);
		pager = findViewById(R.id.pager);
		pager.setAdapter(new Adapter(getSupportFragmentManager()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.calendar:
				final DateTime current = now.plusDays(pager.getCurrentItem());
				final DatePickerDialog dialog = new DatePickerDialog(this, this, current.getYear(), current.getMonthOfYear() - 1, current.getDayOfMonth());
				dialog.getDatePicker().setMinDate(now.getMillis());
				dialog.getDatePicker().setMaxDate(now.plusDays(MAX_DAYS).getMillis());
				dialog.show();
				return true;
			case R.id.settings:
				startActivity(new Intent(this, Settings.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		final DateTime selectedDate = DateTime.now().withDate(year, month + 1, day);
		pager.setCurrentItem(Days.daysBetween(now, selectedDate).getDays(), true);
	}

	private class Adapter extends FragmentPagerAdapter {
		Adapter(FragmentManager manager) {
			super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		}

		@NonNull
		@Override
		public Fragment getItem(int position) {
			return PrayerList.newInstance(position);
		}

		@Override
		public int getCount() {
			return MAX_DAYS;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return DateTime.now().plusDays(position).toString(getString(R.string.dateFormat));
		}
	}
}