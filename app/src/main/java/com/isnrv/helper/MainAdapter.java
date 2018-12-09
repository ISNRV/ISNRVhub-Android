package com.isnrv.helper;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.isnrv.ParkingStatus;
import com.isnrv.Prayer;
import com.isnrv.R;
import com.viewpagerindicator.IconPagerAdapter;

/**
 * This adapter initialize the view for different tabs that appear in main.xml
 */
public class MainAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter {

	private static final String TAG = MainAdapter.class.getCanonicalName();
	private final String[] tabTitles;

	public MainAdapter(FragmentManager fragmentManager, Context context) {
		super(fragmentManager);
		tabTitles = context.getResources().getStringArray(R.array.tab_titles);
	}

	@Override
	public Fragment getItem(int position) {
		return TabView.newInstance(position);
	}

	@Override
	public int getCount() {
		return tabTitles.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return tabTitles[position % tabTitles.length];
	}

	@Override
	public int getIconResId(int index) {
		return 0;
	}

	public static class TabView extends Fragment {
		private int page;

		static TabView newInstance(int page) {
			Log.i(TAG, "newInstance");
			TabView fragment = new TabView();
			fragment.page = page;
			return fragment;
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
			outState.putInt("page", page);
			Log.d(TAG, "putInt:page=" + page);
			super.onSaveInstanceState(outState);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			if ((savedInstanceState != null) && savedInstanceState.containsKey("page")) {
				page = savedInstanceState.getInt("page", 0);
				Log.d(TAG, "getInt:page=" + page);
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			Log.d(TAG, "onCreateView:" + page);
			View view;
			if (page == 0) {
				view = inflater.inflate(R.layout.prayer, container, false);
				new Prayer(getContext()).setGUI(view);
			} else {
				view = inflater.inflate(R.layout.status, container, false);
				new ParkingStatus(getContext()).setGUI(view);
			}
			return view;
		}
	}
}