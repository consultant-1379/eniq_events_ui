/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

/**
 * @author ekurshi
 * @since 2012
 *
 */
public class LicenseInfoDataType {
    private final String featureName;

    private final String description;

    public LicenseInfoDataType(final String featurName, final String description) {
        this.featureName = featurName;
        this.description = description;
    }

    /**
     * @return the featureName
     */
    public String getFeatureName() {
        return featureName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
