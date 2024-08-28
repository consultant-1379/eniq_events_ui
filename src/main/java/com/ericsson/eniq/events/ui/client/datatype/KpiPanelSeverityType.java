/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import com.ericsson.eniq.events.common.client.json.IJSONArray;
import com.ericsson.eniq.events.common.client.json.IJSONObject;

/**
 * 
 * @author evidbab
 * @since February 2012
 *
 */
public final class KpiPanelSeverityType {

    private final String severity;

    private final String menuItemName;

    private final String popUpMessage;

    public KpiPanelSeverityType(final String severity, final String menuItemName, final String popUpMessage) {
        this.severity = severity;
        this.menuItemName = menuItemName;
        this.popUpMessage = popUpMessage;
    }

    /**
     * @return the severity
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * @return the menuItemName
     */
    public String getMenuItemName() {
        return menuItemName;
    }

    /**
     * @return the popUpMessage
     */
    public String getPopUpMessage() {
        return popUpMessage;
    }

    public static KpiPanelSeverityType[] getNotificationSeverities(final IJSONArray notificationSeverities) {
        final KpiPanelSeverityType[] kpiPanelSeverityTypes = new KpiPanelSeverityType[notificationSeverities.size()];
        for (int i = 0; i < notificationSeverities.size(); i++) {
            final IJSONObject groupElementKeyNameJson = notificationSeverities.get(i);
            final String severity = groupElementKeyNameJson.getString("severity");
            final String menuItem = groupElementKeyNameJson.getString("menuItem");
            final String popUpMessage = groupElementKeyNameJson.getString("popUpMessage");
            kpiPanelSeverityTypes[i] = new KpiPanelSeverityType(severity, menuItem, popUpMessage);
        }
        return kpiPanelSeverityTypes;
    }

}
