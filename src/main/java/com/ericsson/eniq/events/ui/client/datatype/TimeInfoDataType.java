/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import java.util.Date;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONValue;

import static com.ericsson.eniq.events.common.client.CommonConstants.DATE_FROM_QUERY_PARAM;
import static com.ericsson.eniq.events.common.client.CommonConstants.DATE_TO_QUERY_PARAM;
import static com.ericsson.eniq.events.common.client.CommonConstants.FIRST_URL_PARAM_DELIMITOR;
import static com.ericsson.eniq.events.common.client.CommonConstants.LABEL_DATE_FORMAT;
import static com.ericsson.eniq.events.common.client.CommonConstants.TIME_FROM_QUERY_PARAM;
import static com.ericsson.eniq.events.common.client.CommonConstants.TIME_QUERY_PARAM;
import static com.ericsson.eniq.events.common.client.CommonConstants.TIME_TO_QUERY_PARAM;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;

/**
 * DataType Object to hold the Time Paramters collected
 * from the end user via the TimeparameterDialog
 * 
 * @author eendmcm
 * @since  Mar 2010
 */
public class TimeInfoDataType {

    final static String DATA_TIME_FROM = "dataTimeFrom";

    final static String DATA_TIME_TO = "dataTimeTo";

    final static long WEEK_RANGE_IN_MS = 604800000;

    public final static TimeInfoDataType DEFAULT_ONE_WEEK_TIME_DATA = new TimeInfoDataType(CommonConstants.ONE_WEEK_MS_TIME_PARAMETER) {
    };

    public final static TimeInfoDataType DEFAULT_ONE_DAY_TIME_DATA = new TimeInfoDataType(ONE_DAY_MS_TIME_PARAMETER) {
    };

    public final static TimeInfoDataType DEFAULT_TWELVE_HOURS_TIME_DATA = new TimeInfoDataType(
            TWELVE_HOURS_MS_TIME_PARAMETER) {
    };

    public final static TimeInfoDataType DEFAULT_SIX_HOURS_TIME_DATA = new TimeInfoDataType(SIX_HOURS_MS_TIME_PARAMETER) {
    };

    public final static TimeInfoDataType DEFAULT_TWO_HOURS_TIME_DATA = new TimeInfoDataType(TWO_HOURS_MS_TIME_PARAMETER) {
    };

    public final static TimeInfoDataType DEFAULT_ONE_HOUR_TIME_DATA = new TimeInfoDataType(ONE_HOUR_MS_TIME_PARAMETER) {
    };

    public final static TimeInfoDataType DEFAULT_THIRTY_MINS_TIME_DATA = new TimeInfoDataType(
            THIRTY_MINS_MS_TIME_PARAMETER) {
    };

    public final static TimeInfoDataType DEFAULT_FIFTEEN_MINS_TIME_DATA = new TimeInfoDataType(
            FIFTEEN_MINS_MS_TIME_PARAMETER) {
    };

    public final static TimeInfoDataType DEFAULT_SUB_BI_BUSY_DAY_TIME_DATA = new TimeInfoDataType(
            DEFAULT_BUSY_DAY_TIME_PARAMETER) {
    };

    public final static TimeInfoDataType DEFAULT_SUB_BI_BUSY_HOUR_TIME_DATA = new TimeInfoDataType(
            DEFAULT_BUSY_HOUR_TIME_PARAMETER);

    public final static TimeInfoDataType DEFAULT = new TimeInfoDataType(DEFAULT_TIME_PARAMETER);

    static {
        DEFAULT_SUB_BI_BUSY_DAY_TIME_DATA.timeRangeSelectedIndex = DEFAULT_BUSY_DAY_TIME_RANGE_SELECTED_INDEX;
        DEFAULT_SUB_BI_BUSY_HOUR_TIME_DATA.timeRangeSelectedIndex = DEFAULT_BUSY_HOUR_TIME_RANGE_SELECTED_INDEX;
    }

    /*
     * Format for server calls
     */
    private final static DateTimeFormat jsonDateFormat = DateTimeFormat.getFormat(DATE_IN_JSON_FORMAT);

    /*
     * Format when present date on a label
     */
    private final static DateTimeFormat labelDateFormat = DateTimeFormat.getFormat(LABEL_DATE_FORMAT);

    /* local constants for #toString */

    private static final String TO_SPACE = " to ";

    /* initialise to empty string */
    public String timeRange = EMPTY_STRING;

    /* for display on labels - e.g. 1 week,  
     * (slight hack default until such time as combobox selection made */
    public String timeRangeDisplay = DEFAULT_TIME_PARAMETER + "  minutes";

    public int timeRangeSelectedIndex = DEFAULT_TIME_RANGE_SELECTED_INDEX;

    public Date dateFrom;

    public Date dateTo;

    public Time timeFrom;

    public Time timeTo;

    public String dataTimeFrom;

    public String dataTimeTo;

    public String timeZone;

    private static TimeInfoDataType selectedDefaultTime = TimeInfoDataType.DEFAULT;

    /**
     * Default construct 
     */
    public TimeInfoDataType() {
        this(EMPTY_STRING);
    }

    public TimeInfoDataType(Response response){
        final JSONValue jsonValue = JSONUtils.parse(response.getText());
        final JsonObjectWrapper metaData = new JsonObjectWrapper(jsonValue.isObject());
        dataTimeFrom = metaData.getString(DATA_TIME_FROM);
        dataTimeTo = metaData.getString(DATA_TIME_TO);
    }

    /**
     * Constructor. Takes in TimeInfoDataType and performs a deep copy of it's attributes.
     * @param timeData
     */
    public TimeInfoDataType(TimeInfoDataType timeData) {
        dateFrom = null;
        dateTo = null;
        timeFrom = null;
        timeTo = null;
        dataTimeFrom = "";
        dataTimeTo = "";
        timeZone = "";

        if (timeData.dataTimeFrom != null){
            dataTimeFrom = new String(timeData.dataTimeFrom);
        }
        if (timeData.dataTimeTo != null){
            dataTimeTo = new String(timeData.dataTimeTo);
        }
        if (timeData.timeZone != null){
            timeZone = new String(timeData.timeZone);
        }
        if(timeData.dateFrom != null){
            dateFrom = new Date(timeData.dateFrom.getTime());
        }
        if(timeData.dateTo != null){
            dateTo = new Date(timeData.dateTo.getTime());
        }
        if(timeData.timeTo != null){
            timeTo = new Time(timeData.timeTo.getHour(), timeData.timeTo.getMinutes());
        }
        if (timeData.timeFrom != null){
            timeFrom = new Time(timeData.timeFrom.getHour(), timeData.timeFrom.getMinutes());
        }

        timeRange = new String(timeData.timeRange);
        timeRangeDisplay = new String(timeData.timeRangeDisplay);
        timeRangeSelectedIndex = timeData.timeRangeSelectedIndex;
    }

    /**
     * This creates a TimeInfoDataType with a modified dataTimeFrom. dataTimeFrom is altered by the parameter range.
     * range determines the number of milliseconds to reduce the dataFromTime by.
     * @param timeInfoDataType
     * @param selectedIndex
     */
    public TimeInfoDataType(TimeInfoDataType timeInfoDataType, int selectedIndex) {
        dateFrom = null;
        dateTo = null;
        timeFrom = null;
        timeTo = null;

        long fromDate = Long.parseLong(timeInfoDataType.dataTimeFrom);

        if (selectedIndex == ONE_WEEK_TIME_RANGE_SELECTED_INDEX){
            fromDate = fromDate - WEEK_RANGE_IN_MS;
            dataTimeFrom = "" + fromDate;
            dataTimeTo = new String(timeInfoDataType.dataTimeTo);
            timeRangeDisplay = ONE_WEEK_TIME_RANGE_DISPLAY;

        }else{
            dataTimeFrom = new String(timeInfoDataType.dataTimeFrom);
            dataTimeTo = new String(timeInfoDataType.dataTimeTo);
            timeRangeDisplay = new String(timeInfoDataType.timeRangeDisplay);
        }


        if(timeInfoDataType.dateFrom != null){
            dateFrom = new Date(timeInfoDataType.dateFrom.getTime());
        }
        if(timeInfoDataType.dateTo != null){
            dateTo = new Date(timeInfoDataType.dateTo.getTime());
        }
        if(timeInfoDataType.timeTo != null){
            timeTo = new Time(timeInfoDataType.timeTo.getHour(), timeInfoDataType.timeTo.getMinutes());
        }
        if (timeInfoDataType.timeFrom != null){
            timeFrom = new Time(timeInfoDataType.timeFrom.getHour(), timeInfoDataType.timeFrom.getMinutes());
        }

        timeRange = new String(timeInfoDataType.timeRange);
        timeZone = new String(timeInfoDataType.timeZone);
        timeRangeSelectedIndex = selectedIndex;
    }


    public static TimeInfoDataType getDefaultTime() {
        return selectedDefaultTime;
    }

    public static void setDefaultTime(final TimeInfoDataType defaultTime) {
        selectedDefaultTime = defaultTime;
    }

    /* private for setting preset times*/
    private TimeInfoDataType(final String defaultTime) {
        timeRange = defaultTime;

        if (defaultTime.equals(CommonConstants.ONE_WEEK_MS_TIME_PARAMETER)) {
            timeRangeDisplay = ONE_WEEK_TIME_RANGE_DISPLAY;
            timeRangeSelectedIndex = ONE_WEEK_TIME_RANGE_SELECTED_INDEX;
        }
        if (defaultTime.equals(ONE_DAY_MS_TIME_PARAMETER)) {
            timeRangeDisplay = ONE_DAY_TIME_RANGE_DISPLAY;
            timeRangeSelectedIndex = ONE_DAY_TIME_RANGE_SELECTED_INDEX;
        }

    }

    /**
     * Deep Defensive copy of an instance of this class
     * @param from - type to copy from
     * @return
     */
    public static TimeInfoDataType copyInstance(final TimeInfoDataType from) {
        final TimeInfoDataType to = new TimeInfoDataType();
        to.dataTimeFrom = from.dataTimeFrom;
        to.dataTimeTo = from.dataTimeTo;
        to.dateFrom = from.dateFrom;
        to.dateTo = from.dateTo;
        to.timeFrom = from.timeFrom;
        to.timeTo = from.timeTo;
        to.timeRange = from.timeRange;
        to.timeRangeDisplay = from.timeRangeDisplay;
        to.timeZone = from.timeZone;
        to.timeRangeSelectedIndex = from.timeRangeSelectedIndex;
        return to;
    }

    /**
     * convert the provided To Date to a string 
     * format that can be utilised by JSON
     * @return - To Date in format for use by JSON
     */
    private String getDateToJSONFormat() {
        return jsonDateFormat.format(dateTo);
    }

    /**
     * convert the provided From Date to a string 
     * format that can be utilised by JSON
     * @return - From Date in format for use by JSON
     */
    private String getDateFromJSONFormat() {
        return jsonDateFormat.format(dateFrom);
    }

    /**
     * convert the provided From Time to a string 
     * format that can be utilised by JSON
     * @return - From Time in format for use by JSON
     */
    private String getTimeFromJSONFormat() {
        return getTimeFormat(timeFrom.getHour(), timeFrom.getMinutes());
    }

    /**
     * convert the provided To Time to a string 
     * format that can be utilised by JSON
     * @return - To Time in format for use by JSON
     */
    private String getTimeToJSONFormat() {
        return getTimeFormat(timeTo.getHour(), timeTo.getMinutes());
    }

    /**
     * Convert dataTimeFrom into ddMMyyyy
     * @return date formatted as ddMMyyyy
     */
    private String getDataDateFrom() {
        Date date = new Date(Long.parseLong(dataTimeFrom));
        DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("ddMMyyyy");
        return dateTimeFormat.format(date);
    }

    /**
     * Convert dataTimeTo into ddMMyyyy
     * @return date formatted as ddMMyyyy
     */
    private String getDataDateTo() {
        Date date = new Date(Long.parseLong(dataTimeTo));
        DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("ddMMyyyy");
        return dateTimeFormat.format(date);
    }

    /**
     * Convert dataTimeFrom into HHmm
     * @return date formatted as HHmm
     */
    private String getDataTimeFrom() {
        Date date = new Date(Long.parseLong(dataTimeFrom));
        DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("HHmm");
        return dateTimeFormat.format(date);
    }

    /**
     * Convert dataTimeTo into HHmm
     * @return date formatted as HHmm
     */
    private String getDataTimeTo() {
        Date date = new Date(Long.parseLong(dataTimeTo));
        DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("HHmm");
        return dateTimeFormat.format(date);
    }


    /**
     * Get the query string for the time info, when used in a drilldown.
     * Sample return values
     * ?dateFrom=01011970&dateTo=01011970&timeFrom=2200&timeTo=2300
     * @return
     */
    public String getDrillQueryString() {
        final StringBuilder query = new StringBuilder();
        query.append(FIRST_URL_PARAM_DELIMITOR);
        query.append(DATE_FROM_QUERY_PARAM);
        query.append(getDataDateFrom());
        query.append(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR);
        query.append(DATE_TO_QUERY_PARAM);
        query.append(getDataDateTo());
        query.append(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR);
        query.append(TIME_FROM_QUERY_PARAM);
        query.append(getDataTimeFrom());
        query.append(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR);
        query.append(TIME_TO_QUERY_PARAM);
        query.append(getDataTimeTo());
        return query.toString();
    }

    /**
     * Get the query string for the time info
     * Sample return values (all for when isFirstParameter is set to true)    
     * ?dateFrom=01011970&dateTo=01011970&timeFrom=2200&timeTo=2300
     * ?time=1400
     * If isFirstParameter set to false, then it will return something like:
     * &time=30
     *
     * @param isFirstParameter           if true, then result will be prepended with FIRST_URL_PARAM_DELIMITOR, if false then REGULAR_URL_PARAM_DELIMITOR
     * @return
     */
    public String getQueryString(final boolean isFirstParameter) {

        final StringBuilder query = new StringBuilder();

        final char cInitialDelimiter = (isFirstParameter) ? FIRST_URL_PARAM_DELIMITOR
                : CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR;
        query.append(cInitialDelimiter);
        if (timeRange.length() > 0) {
            query.append(TIME_QUERY_PARAM);
            query.append(timeRange);
        } else {
            query.append(DATE_FROM_QUERY_PARAM);
            query.append(getDateFromJSONFormat());
            query.append(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR);
            query.append(DATE_TO_QUERY_PARAM);
            query.append(getDateToJSONFormat());
            query.append(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR);
            query.append(TIME_FROM_QUERY_PARAM);
            query.append(getTimeFromJSONFormat());
            query.append(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR);
            query.append(TIME_TO_QUERY_PARAM);
            query.append(getTimeToJSONFormat());
        }
        return query.toString();
    }

    /* String will use on a label to show time on window */
    @Override
    public String toString() {

        final StringBuilder timeInfo = new StringBuilder();
        if (timeRange.length() > 0) {
            timeInfo.append(timeRangeDisplay);

        } else {
            timeInfo.append(getToStringTimeFormat(timeFrom.getHour(), timeFrom.getMinutes()));
            timeInfo.append(COMMA_SPACE);
            timeInfo.append(labelDateFormat.format(dateFrom));
            timeInfo.append(TO_SPACE);
            timeInfo.append(getToStringTimeFormat(timeTo.getHour(), timeTo.getMinutes()));
            timeInfo.append(COMMA_SPACE);
            timeInfo.append(labelDateFormat.format(dateTo));

        }
        return timeInfo.toString();
    }

    /*
     * takes the hr and min as Integer parameters and converts to 
     * a 4 digit string that represents the values 
     * @return string suitable for URL params, e.g. 2200 for 10 pm
     */
    private String getTimeFormat(final Integer hr, final Integer min) {
        final String sHR = getTimeString(hr);
        final String sMin = getTimeString(min);
        return sHR + sMin;
    }

    /*
     * Format time display for labels (not url parameter return
     * @param hr    e.g. 22
     * @param min   e.g. 0
     * @return      e.g. 22:00
     */
    private String getToStringTimeFormat(final int hr, final int min) {
        final String sHR = getTimeString(hr);
        final String sMin = getTimeString(min);
        return sHR + SEMI_COLON + sMin;
    }

    private String getTimeString(final int timeUnit) {
        final String timeStr = String.valueOf(timeUnit);
        return (timeStr.length() == 1) ? ("0" + timeStr) : timeStr;
    }

}
