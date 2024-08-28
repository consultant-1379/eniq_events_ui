/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.ericsson.eniq.events.common.client.time.TimePeriod;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.google.gwt.user.client.DOM;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.MAX_VISIBLE_WORKSPACE_NAME_LENGHT;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class WorkspaceUtils {

    // A sorted map based on the minutes in TimePeriod enums to help mapping to TimeInfoDataType.
    private static final Map<Integer, TimePeriod> timePeriodMap = new TreeMap<Integer, TimePeriod>() {
        {
            for (TimePeriod timePeriod : TimePeriod.values()) {
                put(timePeriod.toMinutes(), timePeriod);
            }

        }
    };

    /**
     * Generate unique id for a window/tab
     * @return
     */
    public static String generateId() {
        return DOM.createUniqueId();
    }

    public static boolean isNonEmptyString(String string) {
        return string != null && !string.isEmpty();
    }

    public static String getVisibleWorkspaceName(String name) {
        if (name == null) {
            return "";
        }

        String s = name.trim();
        if (s.length() > MAX_VISIBLE_WORKSPACE_NAME_LENGHT) {
            final String CUT_POSTFIX = "...";
            s = s.substring(0, MAX_VISIBLE_WORKSPACE_NAME_LENGHT - CUT_POSTFIX.length());
            s += CUT_POSTFIX;
        }
        return s;
    }

    public static TimeInfoDataType getTimeInfo(TimePeriod timePeriod, Date from, Date to) {
        TimeInfoDataType timeInfoDataType = new TimeInfoDataType();
        if (from != null) {
            timeInfoDataType.dateFrom = from;
            timeInfoDataType.timeFrom = new Time(from);
        }
        if (to != null) {
            timeInfoDataType.dateTo = to;
            timeInfoDataType.timeTo = new Time(to);
        }
        if (!timePeriod.equals(TimePeriod.CUSTOM)) {
            timeInfoDataType.timeRange = Integer.toString(timePeriod.toMinutes());
            timeInfoDataType.timeRangeDisplay = timePeriod.toFullText();
        }
        timeInfoDataType.timeRangeSelectedIndex = getTimeRangeIndexFromMinutes(timePeriod.toMinutes());
        return timeInfoDataType;
    }

    /**
     * Returns an index to TimePeriodMap sorted by the minutes values in TimePeriod enums according to a minutes key.
     *
     * @param minutes TimePeriod in minutes
     * @return A time range index according to a minutes key (value 2 as "1 hour" by default)
     */
    private static int getTimeRangeIndexFromMinutes(int minutes) {
        int index = 0;
        for (int timePeriod : timePeriodMap.keySet()) {
            if (minutes == timePeriod) {
                break;
            }
            index++;
        }

        // The last index is the CUSTOM time selection
        return index;
    }

    /**
     * Returns a TimePeriod instance based on the minutes key provided.
     *
     * @param minutes Minutes in String representation
     * @return TimePeriod instance to be returned ("1 hour" instance by default)
     */
    public static final TimePeriod getTimePeriodFromMinutes(String minutes) {
        try {
            if (EMPTY_STRING.equals(minutes)) {
                return timePeriodMap.get(Integer.MAX_VALUE);
            }
            return timePeriodMap.get(Integer.valueOf(minutes));
        } catch (NumberFormatException nfe) {
            // This should never happen
        }

        return timePeriodMap.get(DEFAULT_TIME_RANGE_SELECTED_INDEX);
    }

    public static String getString(String str) {
        return str == null ? "" : str;
    }

    /**
     * @param favourites
     * @param workspaceState
     * @return
     */
    public static boolean isStartupItem(Collection<String> favourites, WorkspaceState workspaceState) {
        return favourites.contains(workspaceState.getId());
    }

    /**
     * @param collection
     * @return
     */
    public static <T> Collection<T> getCollectionWithNullCheck(Collection<T> collection) {
        return collection == null ? Collections.<T> emptyList() : collection;
    }
}
