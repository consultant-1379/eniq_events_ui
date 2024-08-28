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
public class KPIConfigurationPanelElement {

    private String defaultValue;

    private int minValue;

    private int maxValue = 10080;

    private String comboTimeData;

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;

    }

    public void setMaxValue(final int i) {
        this.maxValue = i;

    }

    public void setMinValue(final int i) {
        this.minValue = i;

    }

    /**
     * @return the minValue
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * @return the maxValue
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setComboTimeData(final String comboTimeData) {
        this.comboTimeData = comboTimeData;
    }

    /**
     * @return the comboTimeData
     */
    public String getComboTimeData() {
        return comboTimeData;
    }

}
