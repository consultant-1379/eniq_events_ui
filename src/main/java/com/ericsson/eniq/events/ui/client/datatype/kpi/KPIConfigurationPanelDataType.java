/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype.kpi;

/**
 * @author eemecoy
 *
 */
public class KPIConfigurationPanelDataType {

    private KPIConfigurationPanelElement refreshTime;

    private KPIConfigurationPanelElement refreshRate;

    public void setRefreshTime(final KPIConfigurationPanelElement refreshRate) {
        this.refreshTime = refreshRate;
    }

    public void setRefreshRate(final KPIConfigurationPanelElement refreshRateDetails) {
        this.refreshRate = refreshRateDetails;
    }

    public KPIConfigurationPanelElement getRefreshTime() {
        return refreshTime;
    }

    public KPIConfigurationPanelElement getRefreshRate() {
        return refreshRate;
    }

}
