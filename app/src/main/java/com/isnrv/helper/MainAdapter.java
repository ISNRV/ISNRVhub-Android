package com.isnrv.helper;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.isnrv.Admin;
import com.isnrv.Prayer;
import com.isnrv.R;
import com.isnrv.Status;
import com.viewpagerindicator.IconPagerAdapter;

/**
 * This adapter initilize the view for different tabs that appear in main.xml
 * @author Yasir
 *
 */
public class MainAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter {
	
	private final static String TAG = "MainAdapter";
	
    private int mCount;
    private static Status status;
    private static Prayer prayer;
    private static String[] tabTitles;

    public MainAdapter(FragmentManager fm, Context context) {
        super(fm);
        Log.i(TAG,"MainAdapter");
        status = new Status(context);
        prayer = new Prayer(context);
        tabTitles = context.getResources().getStringArray(R.array.tab_titles);
        mCount = tabTitles.length;
    }
    
    
    public void onPause(){
    	status.isAppVisible(false);
    }
    
    public void onResume(){
    	status.isAppVisible(true);
    }

    @Override
    public Fragment getItem(int position) {
    	Log.i(TAG,"getItem");
        return TabView.newInstance(position);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return MainAdapter.tabTitles[position % tabTitles.length];
    }

    @Override
    public int getIconResId(int index) {
      return 0;
    }

    public void setCount(int count) {
        if (count > 0 && count <= mCount) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
    
    public final static class TabView extends Fragment {
        private int page;

        @Override
        public void onSaveInstanceState(Bundle outState) {
            // TODO Auto-generated method stub
        	Log.i(TAG,"onSaveInstanceState");
            outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
            outState.putInt("page", page);
            Log.i(TAG,"putInt:page=" + page);
            super.onSaveInstanceState(outState);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if ((savedInstanceState != null) && savedInstanceState.containsKey("page")) {
                page = savedInstanceState.getInt("page", 0);
                Log.i(TAG,"getInt:page=" + page);
            }
        }

        public static TabView newInstance(int page) {
        	Log.i(TAG,"newInstance");
            TabView fragment = new TabView();
            fragment.page = page;
            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        	//LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	Log.i(TAG,"onCreateView:" + page);
            View view = null;
        	if(page == 0){
        		view = inflater.inflate(R.layout.prayer, null);
            	prayer.setGUI(view);
            }else {
            	view = inflater.inflate(R.layout.status, null);
            	status.setGUI(view);
            }
            return view;
        }

    }
}