/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.workspace.config.datatype;


/**
 * @since 2012
 */
public interface IDimension {
    String getUrlParam();

    String getGroupType();

    String getLiveloadUrl();

    String getName();

    String getStyle();

    String getSelectorType();

    ISupportedTechnologies getSupportedTechnologies();

    ISupportedAccessGroups getSupportedAccessGroups();

    String getLiveloadTechnologyIndicator();

    String getId();

    ISupportedLicenses getSupportedLicenses();
}