/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

import com.ericsson.eniq.events.common.client.json.IJSONArray;
import com.ericsson.eniq.events.common.client.json.IJSONObject;
import com.ericsson.eniq.events.ui.client.datatype.kpi.KPIConfigurationPanelDataType;
import com.ericsson.eniq.events.ui.client.datatype.kpi.KPIConfigurationPanelElement;

/**
 * @author eemecoy
 *
 */
public class KPIConfigurationMetaReader {

    public KPIConfigurationPanelDataType getKPIConfigurationPanelMetaData(final IJSONArray array) {
        final IJSONObject kpiConfigurationPanelMetaData = array.get(0);
        final KPIConfigurationPanelDataType dataType = new KPIConfigurationPanelDataType();
        final IJSONObject refreshTimeJSON = kpiConfigurationPanelMetaData.getArray("refreshTime").get(0);
        final KPIConfigurationPanelElement refreshTime = getConfigurationSettings(refreshTimeJSON);
        dataType.setRefreshTime(refreshTime);
        final IJSONObject refreshRateJSON = kpiConfigurationPanelMetaData.getArray("refreshRate").get(0);
        final KPIConfigurationPanelElement refreshRateDetails = getConfigurationSettings(refreshRateJSON);
        dataType.setRefreshRate(refreshRateDetails);
        return dataType;
    }

    private KPIConfigurationPanelElement getConfigurationSettings(final IJSONObject json) {
        final KPIConfigurationPanelElement metaData = new KPIConfigurationPanelElement();
        metaData.setDefaultValue(json.getString("defaultValueInMinutes"));
        final IJSONArray comboTimeData = json.getArray("comboTimeData");
        metaData.setComboTimeData(comboTimeData.get(0).getNativeObject().toString());
        return metaData;
    }

}
