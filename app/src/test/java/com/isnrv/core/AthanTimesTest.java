package com.isnrv.core;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AthanTimesTest {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.forPattern("h:mma");

    /**
     * Test if the getIndex() returns the correct index in the prayer timetable for the given day
     */
    @Test
    public void testDayTableIndex() {
        assertEquals("Day before February in a non-leap year",
                29, AthanTimes.getIndex(new DateTime(2021, 1, 30, 0, 0)));

        assertEquals("Day in February before February 28 in a non-leap year",
                40, AthanTimes.getIndex(new DateTime(2021, 2, 10, 0, 0)));

        assertEquals("Day after February in a non-leap year",
                67, AthanTimes.getIndex(new DateTime(2021, 3, 8, 0, 0)));

        assertEquals("Day before February in a leap year",
                29, AthanTimes.getIndex(new DateTime(2020, 1, 30, 0, 0)));

        assertEquals("Day in February before February 29 in a non-leap year",
                40, AthanTimes.getIndex(new DateTime(2020, 2, 10, 0, 0)));

        assertEquals("February 29 in a leap year",
                59, AthanTimes.getIndex(new DateTime(2020, 2, 29, 0, 0)));

        assertEquals("Day after February in a leap year",
                67, AthanTimes.getIndex(new DateTime(2020, 3, 8, 0, 0)));
    }

    /**
     * Compare calculated Fajr Athan time against actual Fajr Athan times for selected dates copied from IslamicFinder.com
    */
    @Test
    public void testAthanTimes() {

        assertTrue("First day of the year in a leap year",
                areEqual(AthanTimes.get(new DateTime(2020, 1, 1, 0, 0))[0], "6:19AM"));

        assertTrue("A random day before February 29 in a leap year",
                areEqual(AthanTimes.get(new DateTime(2020, 1, 31, 0, 0))[0], "6:12AM"));
        assertTrue("February 29 in a leap year",
                areEqual(AthanTimes.get(new DateTime(2020, 2, 29, 6, 0))[0], "5:42AM"));
        assertTrue("March 7 in a leap year (within table range before DST start)",
                areEqual(AthanTimes.get(new DateTime(2020, 3, 7, 0, 0))[0], "5:32AM"));
        assertTrue("The actual day DST starts in a leap year",
                areEqual(AthanTimes.get(new DateTime(2020, 3, 8, 6, 0))[0], "6:32AM"));
        assertTrue("March 13 in a leap year (within table range after DST)",
                areEqual(AthanTimes.get(new DateTime(2020, 3, 13, 6, 0))[0], "6:24AM"));
        assertTrue("A day after March 14 in a leap year",
                areEqual(AthanTimes.get(new DateTime(2020, 3, 21, 0, 0))[0], "6:12AM"));

        assertTrue("The actual day DST ends in a leap year",
                areEqual(AthanTimes.get(new DateTime(2020, 11, 1, 6, 0))[0], "5:35AM"));
        assertTrue("November 6 in a leap year (within table range after DST)",
                areEqual(AthanTimes.get(new DateTime(2020, 11, 6, 0, 0))[0], "5:39AM"));
        assertTrue("A random day after table range in a leap year",
                areEqual(AthanTimes.get(new DateTime(2020, 11, 30, 0, 0))[0], "6:01AM"));

        assertTrue("Last day of the year in a leap year",
                areEqual(AthanTimes.get(new DateTime(2020, 12, 31, 0, 0))[0], "6:19AM"));

        assertTrue("First day of the year in a non-leap year",
                areEqual(AthanTimes.get(new DateTime(2021, 1, 1, 0, 0))[0], "6:19AM"));

        assertTrue("A random day before February 29 in a non-leap year",
                areEqual(AthanTimes.get(new DateTime(2021, 1, 31, 6, 0))[0], "6:11AM"));

        assertTrue("February 28 in a non-leap year",
                areEqual(AthanTimes.get(new DateTime(2021, 2, 28, 0, 0))[0], "5:42AM"));

        assertTrue("A day in March before table range in a non-leap year",
                areEqual(AthanTimes.get(new DateTime(2021, 3, 7, 0, 0))[0], "5:33AM"));

        assertTrue("March 8 in a non-leap year (at the beginning of the table range before DST start)",
                areEqual(AthanTimes.get(new DateTime(2021, 3, 8, 0, 0))[0], "5:31AM"));

        assertTrue("March 13 in a non-leap year (within table range before DST)",
                areEqual(AthanTimes.get(new DateTime(2021, 3, 13, 6, 0))[0], "5:23AM"));

        assertTrue("The actual day DST starts in a non-leap year",
                areEqual(AthanTimes.get(new DateTime(2021, 3, 14, 6, 0))[0], "6:23AM"));

        assertTrue("A random day after March 13 in a non-leap year",
                areEqual(AthanTimes.get(new DateTime(2021, 3, 31, 0, 0))[0], "5:57AM"));

        assertTrue("November 1st in a non-leap year (within table range before DST end)",
                areEqual(AthanTimes.get(new DateTime(2021, 11, 1, 0, 0))[0], "6:34AM"));

        assertTrue("November 6 in a non-leap year (within table range after DST)",
                areEqual(AthanTimes.get(new DateTime(2021, 11, 6, 0, 0))[0], "6:38AM"));

        assertTrue("The actual day DST ends in a non-leap year",
                areEqual(AthanTimes.get(new DateTime(2021, 11, 7, 6, 0))[0], "5:40AM"));

        assertTrue("A random day after November 6 in a non-leap year",
                areEqual(AthanTimes.get(new DateTime(2021, 11, 30, 0, 0))[0], "6:01AM"));

        assertTrue("Last day of the year in a non-leap year",
                areEqual(AthanTimes.get(new DateTime(2021, 12, 31, 0, 0))[0], "6:18AM"));
    }

    private boolean areEqual(LocalTime actual, String expected) {
        return Math.abs(Minutes.minutesBetween(LocalTime.parse(expected, TIME_FORMAT), actual).getMinutes()) < 2;
    }
}