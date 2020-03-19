package com.isnrv.utilities;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateTimeUtilitiesTest {

    @Test
    public void testGetTimeShift() {
        /* Prayer time table range for DST start adjustment is March 8 to March 14 */
        /* Prayer time table range for DST end adjustment is November 1 to November 7 */

        assertEquals("Random day before March",
                0, DateTimeUtilities.getTimeShift(new DateTime(2019, 1, 1, 0,0)));


        assertEquals("Before the actual DST start & outside the table range for DST adjustment",
               0, DateTimeUtilities.getTimeShift(new DateTime(2019, 3, 1, 0,0)));
        assertEquals("Before the actual DST start & at the beginning of the table range for DST adjustment",
                -1, DateTimeUtilities.getTimeShift(new DateTime(2019, 3, 8, 0,0)));
        assertEquals("At the date of the actual DST start & within the table range for DST adjustment",
                0, DateTimeUtilities.getTimeShift(new DateTime(2019, 3, 10, 8,0)));
        assertEquals("After the actual DST start & at the end of the table range for DST adjustment",
                0, DateTimeUtilities.getTimeShift(new DateTime(2019, 3, 14, 0,0)));
        assertEquals("After the actual DST start & outside the table range for DST adjustment",
                0, DateTimeUtilities.getTimeShift(new DateTime(2019, 3, 30, 0,0)));

        assertEquals("Random day between March and November",
                0, DateTimeUtilities.getTimeShift(new DateTime(2019, 5, 1, 0,0)));

        assertEquals("Before the actual DST end & within the table range for DST adjustment",
                1, DateTimeUtilities.getTimeShift(new DateTime(2019, 11, 1, 0,0)));
        assertEquals("Right before the actual DST end & within the table range for DST adjustment",
                1, DateTimeUtilities.getTimeShift(new DateTime(2019, 11, 3, 0,0)));
        assertEquals("At the date of the actual DST end & within the table range for DST adjustment",
                0, DateTimeUtilities.getTimeShift(new DateTime(2019, 11, 3, 8,0)));
        assertEquals("After the date of the actual DST end & within the table range for DST adjustment",
                0, DateTimeUtilities.getTimeShift(new DateTime(2019, 11, 7, 0,0)));
        assertEquals("After the date of the actual DST end & outside the table range for DST adjustment",
                0, DateTimeUtilities.getTimeShift(new DateTime(2019, 11, 7, 0,0)));


        assertEquals("Random day after November",
                0, DateTimeUtilities.getTimeShift(new DateTime(2019, 12, 1, 0,0)));

        assertEquals("Random day before March",
                0, DateTimeUtilities.getTimeShift(new DateTime(2020, 1, 1, 0,0)));

        assertEquals("Before actual DST start & outside table range for DST adjustment",
                0, DateTimeUtilities.getTimeShift(new DateTime(2020, 3, 1, 0,0)));
        assertEquals("Right before the actual DST start at the beginning of the table range for DST adjustment",
                -1, DateTimeUtilities.getTimeShift(new DateTime(2020, 3, 8, 0,0)));
        assertEquals("At the date of the actual DST start at the beginning of the table range for DST adjustment",
                0, DateTimeUtilities.getTimeShift(new DateTime(2020, 3, 8, 8,0)));
        assertEquals("After the actual DST start & within the table range for DST adjustment",
                0, DateTimeUtilities.getTimeShift(new DateTime(2020, 3, 14, 8,0)));

        assertEquals("After the actual DST start & outside the table range for DST adjustment",
                0, DateTimeUtilities.getTimeShift(new DateTime(2020, 3, 30, 0,0)));

        assertEquals("Random day between March and November",
                0, DateTimeUtilities.getTimeShift(new DateTime(2020, 5, 1, 0,0)));

        assertEquals("Before the actual DST end & within table range for DST adjustment",
                1, DateTimeUtilities.getTimeShift(new DateTime(2020, 11, 1, 0,0)));
        assertEquals("After the actual DST end & within table range for DST adjustment",
                0, DateTimeUtilities.getTimeShift(new DateTime(2020, 11, 1, 8,0)));
        assertEquals("After the date of the actual DST end & within the table range for DST adjustment",
               0,  DateTimeUtilities.getTimeShift(new DateTime(2020, 11, 7, 0,0)));
        assertEquals("After the date of the actual DST end & outside the table range for DST adjustment",
                0, DateTimeUtilities.getTimeShift(new DateTime(2020, 11, 10, 0,0)));


        assertEquals("Random day after November",
                0, DateTimeUtilities.getTimeShift(new DateTime(2020, 12, 1, 0,0)));

        assertEquals("Random day before March",
                0, DateTimeUtilities.getTimeShift(new DateTime(2021, 1, 1, 0,0)));

        assertEquals("Before the actual DST start & outside table range for DST adjustment",
                0, DateTimeUtilities.getTimeShift(new DateTime(2021, 3, 1, 0,0)));
        assertEquals("Before the actual DST start & at the beginning of the table range for DST adjustment",
                -1, DateTimeUtilities.getTimeShift(new DateTime(2021, 3, 8, 8,0)));
        assertEquals("Right before the actual DST start at the end of the table range for DST adjustment",
                -1, DateTimeUtilities.getTimeShift(new DateTime(2021, 3, 14, 0,0)));
        assertEquals("At the date of the actual DST start at the end of the table range for DST adjustment",
                0, DateTimeUtilities.getTimeShift(new DateTime(2021, 3, 14, 8,0)));
        assertEquals("After the actual DST start & outside the table range for DST adjustment",
                0, DateTimeUtilities.getTimeShift(new DateTime(2021, 3, 30, 0,0)));

        assertEquals("Random day between March and November",
                0, DateTimeUtilities.getTimeShift(new DateTime(2021, 5, 1, 0,0)));

        assertEquals("Right before the actual DST end at the beginning of the table range for DST adjustment",
                1, DateTimeUtilities.getTimeShift(new DateTime(2021, 11, 1, 0,0)));
        assertEquals("At the date of the actual DST end at the beginning of the table range for DST adjustment",
                1, DateTimeUtilities.getTimeShift(new DateTime(2021, 11, 1, 0,0)));
        assertEquals("After the actual DST end & at the end of the table range for DST adjustment",
                0, DateTimeUtilities.getTimeShift(new DateTime(2021, 11, 7, 8,0)));
        assertEquals("After actual DST end & outside the table range for DST adjustment",
                0, DateTimeUtilities.getTimeShift(new DateTime(2021, 11, 11, 8,0)));

        assertEquals("Random day after November",
                0, DateTimeUtilities.getTimeShift(new DateTime(2021, 12, 1, 0,0)));
    }
}