package com.isnrv;

import android.app.ActionBar;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

/**
 * Main Activity
 */
public class Main extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Show custom action bar
		final ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setCustomView(R.layout.custom_actionbar);
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayUseLogoEnabled(false);
			actionBar.setDisplayShowHomeEnabled(false);
		}

		// Show prayer times
		getSupportFragmentManager().beginTransaction().add(R.id.prayers, new PrayerTable()).commit();
	}
}