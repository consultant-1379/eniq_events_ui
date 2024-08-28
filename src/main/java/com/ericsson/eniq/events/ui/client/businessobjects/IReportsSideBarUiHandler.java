/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.businessobjects;


/**
 * @author ecarsea
 * @since 2011
 *
 */
public interface IReportsSideBarUiHandler {

    /**
     * @param header
     * @param url
     */
    void onItemSelected(String header, String url);

}
