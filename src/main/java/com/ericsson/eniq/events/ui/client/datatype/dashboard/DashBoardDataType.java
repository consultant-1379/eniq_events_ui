/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype.dashboard;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.List;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.ui.client.dashboard.DashboardTimeComponent;

/**
 * Holds JsonObjectWrapper info related to dashboards.
 * 
 * @author eeicmsy
 * @since Sept 2011
 *
 */
public class DashBoardDataType {

    private final String tabOwnerId;

    private final String winId;

    private final String title;

    private final String maxDaysBack;

    private final List<PortletDataType> portals;

    /**
     * Hold dashboard window information (populated from meta data)
     * 
     * @param tabOwnerId     - unique id for tab where dashboard exists
     * @param winId          - dashboard window id  (which will use as part of portal id)
     * @param title          - title to place on dashboard window
     * @param maxDaysBack    - max days back (if empty will default to int in this class)
     * @param portals        - portal information for portals to display inside dashboard window
     */
    public DashBoardDataType(final String tabOwnerId, final String winId, final String title, final String maxDaysBack,
            final List<PortletDataType> portals) {

        this.tabOwnerId = tabOwnerId;
        this.winId = winId;
        this.title = title;
        this.maxDaysBack = maxDaysBack;
        this.portals = portals;

    }

    public String getTabOwnerId() {
        return tabOwnerId;
    }

    public String getWinId() {
        return winId;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Earliest time from today can make a selection on dashboard calendar
     * @return default or value set in meta data (which must be an int)
     */
    public long getAllowedDaysBackMS() {
        if (!maxDaysBack.isEmpty()) {
            return CommonConstants.DAY_IN_MILLISEC * Integer.parseInt(maxDaysBack.trim());
        }
        return DashboardTimeComponent.DEFAULT_ALLOWED_DAYS_BACK_MS;
    }

    public List<PortletDataType> getPortals() {
        return portals;
    }

}