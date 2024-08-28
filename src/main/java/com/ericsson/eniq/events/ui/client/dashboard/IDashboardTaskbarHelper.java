/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.dashboard;

import java.util.List;

import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.DashBoardDataType;

/**
 * Utility to read taskbar information for dashboard,
 * i.e. in situations where re-open porlet need to fetch latest data
 * which have missed notifications whilst closed
 *
 * @author eeicmsy
 * @since October 2011
 */
public interface IDashboardTaskbarHelper {

   /**
    * Utility for the menu task bar to be able to return the identification
    * of the tab it is sitting in.
    * <p/>
    * This can be included in guards for the purpose for example of ensuring a window
    * only opens in the tab intended (launching window that could exist in other tabs
    * via "launch window hyper-link" function)
    *
    * @return tab owner id  (from meta data, e.g. NETWORK_TAB, TERMINAL_TAB, SUBSCRIBER_TAB, RANKINGS_TAB)
    */
   String getTabOwnerId();

   /**
    * Return current value for search data in menu taskbar's search field value
    * This is the "last resort" to call as it we do not know if the user
    * has committed (pressed play).
    * <p/>
    * Generally ask other porlets for their search data first  rather than
    * ask the taskbar directly, but if no other porlets open, then use this.
    *
    * @return current search field parameter data from
    *         group component or search component
    */
   SearchFieldDataType getSearchComponentValue();

   /**
    * Utility returning current time data
    * Used if opening a window and need to be refreshed with latest time
    * data  (if window was already open will have recieved time updata event
    *
    * @return current time selection in dashboard time component
    */
   TimeInfoDataType getCurrentDashBoardTimeData();

   void launchDashBoard(DashBoardDataType dashBoardData, List<String> allCurrentLicences);

}
