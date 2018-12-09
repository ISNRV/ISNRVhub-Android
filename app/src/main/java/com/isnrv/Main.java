package com.isnrv;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.isnrv.helper.MainAdapter;
import com.viewpagerindicator.TabPageIndicator;

/**
 * This class contains a ViewPager that shows either prayer.xml or status.xml in two separate tabs
 */
public class Main extends FragmentActivity {
	private static final String TAG = "Main";

	private Firebase configRef;
	private Firebase announcementRef;
	private TextView announcementTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.d(TAG, "onCreate()");
		announcementRef = new Firebase(
				"https://isnrvhub.firebaseio.com/announcement");
		configRef = new Firebase("https://isnrvhub.firebaseio.com/config");
		announcementTextView = findViewById(R.id.announcementTextView);

		// setup actionbar
		final ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setCustomView(R.layout.custom_actionbar);
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayUseLogoEnabled(false);
			actionBar.setDisplayShowHomeEnabled(false);
		}

		// Set ViewPager adapter
		Log.i(TAG, "mAdapter");
		MainAdapter mAdapter = new MainAdapter(getSupportFragmentManager(),
				getApplicationContext());
		ViewPager mPager = findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		// Set ViewPager indicator
		TabPageIndicator indicator = findViewById(R.id.indicator);
		indicator.setViewPager(mPager);

		setCallbacks();
	}

	private void setCallbacks() {
		// callback to check announcement
		configRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snap) {
				SharedPreferences.Editor editor = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext()).edit();
				String vibrateConfig = snap.child("vibrate").getValue().toString();
				String soundConfig = snap.child("sound").getValue().toString();
				if (soundConfig.contains("on") || soundConfig.contains("true")) {
					editor.putBoolean("soundConfig", true);
				} else {
					editor.putBoolean("soundConfig", false);
				}

				if (vibrateConfig.contains("on") || vibrateConfig.contains("true")) {
					editor.putBoolean("vibrateConfig", true);
				} else {
					editor.putBoolean("vibrateConfig", false);
				}
				editor.apply();
			}

			@Override
			public void onCancelled(FirebaseError error) {
				// do nothing
			}
		});

		// callback to check announcement
		announcementRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snap) {
				String announcement = snap.getValue().toString();
				announcementTextView.setText(announcement);
				if (announcement.isEmpty()) {
					announcementTextView.setVisibility(View.GONE);
				} else {
					announcementTextView.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onCancelled(FirebaseError error) {
				// do nothing
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_admin) {
			startActivity(new Intent(getApplicationContext(), Admin.class));
		}
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy()");
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext()).edit();
		editor.putBoolean("loggedIn", false);
		editor.apply();
	}
}