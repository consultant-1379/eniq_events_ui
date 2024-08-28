/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Interface for shared functionality in bottom toolbar
 * (refresh)
 *
 * @author eeicmsy
 * @since May 2010
 */
public interface IBottomToolBar {

    final static DateTimeFormat LABEL_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm"); // NOPMD by eeicmsy on 12/05/10 20:34

    final static String DATA_TIME_FROM = "dataTimeFrom";

    final static String DATA_TIME_TO = "dataTimeTo";

    /**
     * Method to call when refresh to place the time of last
     * refresh next to the refresh button (currently no text to
     * be somewhat localised for foreigners)
     * <p/>
     * Set to current data and time
     */
    void upDateLastRefreshedLabel(Response response);

    /**
     * Utility to fetch last refreshed label for
     * window (when nav to window from  breadcrumb)
     *
     * @return last refreshed time stamp
     */
    String getLastRefreshTimeStamp();

    /**
     * Reset current time display to a previous time
     * (due to bread crumb change from a previous window)
     *
     * @param timeStamp time to replace on current window
     * @param timeStamp time to replace on current window
     */
    void updateLastRefreshedTimeStamp(final String timeStamp);

}
