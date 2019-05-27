package com.isnrv.core;

import android.content.Context;

import com.isnrv.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

/**
 * Prayer info
 */
public class Prayer {
	public static final int FAJR = 0;
	public static final int DHUHR = 1;
	public static final int MAGHRIB = 3;
	public static final int ISHA = 4;

	private final int index;
	private final DateTime athanTime;
	private final DateTime iqamaTime;

	public Prayer(int index, DateTime athanTime, DateTime iqamaTime) {
		this.index = index;
		this.athanTime = athanTime;
		this.iqamaTime = iqamaTime;
	}

	public int getIndex() {
		return index;
	}

	public DateTime getAthanTime() {
		return athanTime;
	}

	public DateTime getIqamaTime() {
		return iqamaTime;
	}

	@SuppressWarnings("unused")
	public String getName(Context context) {
		if (index == DHUHR && iqamaTime.getDayOfWeek() == DateTimeConstants.FRIDAY) {
			return context.getResources().getString(R.string.Friday);
		}
		return context.getResources().getStringArray(R.array.prayers)[index];
	}
}