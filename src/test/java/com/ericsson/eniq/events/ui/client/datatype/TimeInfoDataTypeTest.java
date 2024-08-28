/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import static junit.framework.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import com.ericsson.eniq.events.ui.client.common.Constants;
import com.google.gwt.i18n.client.DateTimeFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.extjs.gxt.ui.client.widget.form.Time;

/**
 * @author eeicmsy
 *
 */

public class TimeInfoDataTypeTest extends TestEniqEventsUI {

    TimeInfoDataType objectToTest;

    @Before
    public void setUp() {
        objectToTest = new TimeInfoDataType();
    }

    @After
    public void tearDown() {
        objectToTest = null;
    }

    @Test
    public void testToStringDateRange() throws Exception {

        final String expected = "22:00, 1970-01-01 to 23:00, 1970-01-01";

        objectToTest.dateFrom = new Date(0);
        objectToTest.dateTo = new Date(0);

        objectToTest.timeFrom = new Time(22, 0);
        objectToTest.timeTo = new Time(23, 0);

        assertEquals("toString as expected", expected, objectToTest.toString());
    }

    @Test
    public void testToStringTimeRange() throws Exception {

        final String expected = "1 week";

        objectToTest.timeRangeDisplay = "1 week";
        objectToTest.timeRange = "not empty";
        objectToTest.dateFrom = new Date(0);
        objectToTest.dateTo = new Date(0);

        objectToTest.timeFrom = new Time(22, 0);
        objectToTest.timeTo = new Time(23, 0);

        assertEquals("toString as expected", expected, objectToTest.toString());
    }

    @Test
    public void getQueryStringTimeRange() throws Exception {

        final String expected = "?time=1400";

        objectToTest.timeRangeDisplay = "whatever";
        objectToTest.timeRange = "1400";
        objectToTest.dateFrom = new Date(0);
        objectToTest.dateTo = new Date(0);

        objectToTest.timeFrom = new Time(22, 0);
        objectToTest.timeTo = new Time(23, 0);

        assertEquals("getQueryString as expected", expected, objectToTest.getQueryString(true));

    }

    @Test
    public void getQueryStringDateRange() throws Exception {

        final String expected = "?dateFrom=01011970&dateTo=01011970&timeFrom=2200&timeTo=2300";

        objectToTest.dateFrom = new Date(0);
        objectToTest.dateTo = new Date(0);
        objectToTest.timeFrom = new Time(22, 0);
        objectToTest.timeTo = new Time(23, 0);

        assertEquals("getQueryString as expected", expected, objectToTest.getQueryString(true));
    }

    @Test
    public void getQueryStringForDrill(){
        objectToTest.dataTimeFrom = "1388534400000";
        objectToTest.dataTimeTo = "1389139200000";

        Date dateTo = new Date(Long.parseLong(objectToTest.dataTimeTo));
        DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("HHmm");
        String dateToString = dateTimeFormat.format(dateTo);

        Date dateFrom = new Date(Long.parseLong(objectToTest.dataTimeFrom));
        String dateFromString = dateTimeFormat.format(dateFrom);

        final String expected = "?dateFrom=01012014&dateTo=08012014&timeFrom=" + dateFromString +"&timeTo="+dateToString;
        String actual = objectToTest.getDrillQueryString();

        assertEquals("The time for the drill query is not as expected", expected, actual);
    }


    @Test
    public void testWeekRangeFromDate(){

        objectToTest.dataTimeFrom = "1388534400000";
        objectToTest.dataTimeTo = "1389139200000";
        objectToTest.timeRange = "1400";
        objectToTest.timeZone = "";

        String expected = "1387929600000";

        TimeInfoDataType timeInfoDataType = new TimeInfoDataType(objectToTest, Constants.ONE_WEEK_TIME_RANGE_SELECTED_INDEX);
        assertEquals("The time should be set back by 1 week.", expected, timeInfoDataType.dataTimeFrom);

        assertEquals("The selected index of the time dropdown should be 7", Constants.ONE_WEEK_TIME_RANGE_SELECTED_INDEX, timeInfoDataType.timeRangeSelectedIndex);
        assertEquals("The time range display should be 1 week", Constants.ONE_WEEK_TIME_RANGE_DISPLAY, timeInfoDataType.timeRangeDisplay);
    }
}
