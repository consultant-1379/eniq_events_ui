/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.charts;

/**
 * Datatype class to hold Chart element details in order for a chart to implement show/hide of chart elements
 * @author ecarsea
 * @since 2011
 *
 */
public class ChartElementDetails {
    /** Id of chart element **/
    private String elementId;

    public ChartElementDetails() {
    }

    public ChartElementDetails(final String elementId) {
        super();
        this.elementId = elementId;
    }

    public void setElementId(final String elementId) {
        this.elementId = elementId;
    }

    public String getElementId() {
        return elementId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((elementId == null) ? 0 : elementId.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChartElementDetails other = (ChartElementDetails) obj;
        if (elementId == null) {
            if (other.elementId != null) {
                return false;
            }
        } else if (!elementId.equals(other.elementId)) {
            return false;
        }
        return true;
    }

}
