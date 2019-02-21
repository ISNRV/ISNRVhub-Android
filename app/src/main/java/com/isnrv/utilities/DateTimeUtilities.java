package com.isnrv.utilities;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeUtilities {
	public static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.forPattern("h:mma");

	private DateTimeUtilities() {
	}

	public static int getTimeShift(DateTime date) {
		final int month = date.getMonthOfYear();
		if (month != DateTimeConstants.MARCH && month != DateTimeConstants.NOVEMBER) {
			return 0;
		}

		if (month == DateTimeConstants.MARCH) {
			return isStandardTime(date) ? -1 : 0;
		}

		return isStandardTime(date) ? 0 : 1;
	}

	public static boolean isStandardTime(DateTime dateTime) {
		return DateTimeZone.forID("America/New_York").isStandardOffset(dateTime.toInstant().getMillis());
	}

	public static DateTime getWeekStart(DateTime date) {
		return date.minusDays(date.getDayOfWeek() % 7);
	}


	public static boolean isAcademicYear(DateTime date) {
		return (date.isAfter(fallSemesterStart(date)) && date.isBefore(fallSemesterEnd(date))) ||
				(date.isAfter(springSemesterStart(date)) && date.isBefore(springSemesterEnd(date)));
	}

	private static DateTime springSemesterEnd(DateTime date) {
		return date.withMonthOfYear(DateTimeConstants.MAY).withDayOfMonth(20);
	}

	private static DateTime springSemesterStart(DateTime date) {
		return date.withMonthOfYear(DateTimeConstants.JANUARY).withDayOfMonth(15);
	}

	private static DateTime fallSemesterEnd(DateTime date) {
		return date.withMonthOfYear(DateTimeConstants.DECEMBER).withDayOfMonth(20);
	}

	private static DateTime fallSemesterStart(DateTime date) {
		return date.withMonthOfYear(DateTimeConstants.AUGUST).withDayOfMonth(20);
	}
}
