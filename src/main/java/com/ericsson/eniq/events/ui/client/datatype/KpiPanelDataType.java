/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

/**
 * Represent KPI Panel type and the number of breaches for each type.
 *   
 * @author evidbab
 * @since February 2012
 *
 */
public final class KpiPanelDataType {

    private final String severity;

    private final int noOfBreaches;

    public KpiPanelDataType(final String severity, final int noOfBreaches) {
        this.severity = severity;
        this.noOfBreaches = noOfBreaches;
    }

    /**
     * @return the severity
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * @return the noOfBreaches
     */
    public int getNoOfBreaches() {
        return noOfBreaches;
    }

}
