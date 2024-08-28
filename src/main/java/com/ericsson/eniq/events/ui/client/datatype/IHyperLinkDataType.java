/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

/**
 * Interface to share some functionality for
 * hyper-links launching new windows  (LaunchWinDataType) and
 * drill-down windows (DrillDownInfoDataType)
 *  
 * Introduced particularly with a view to pass (create) new 
 * SearchDataTypes from hyperlink strings
 * 
 * @author eeicmsy
 * @since July 2010
 */
public interface IHyperLinkDataType {

    /**
     * Get the parameter info to pass to the method that will 
     * launch the new window - reuse the same dataType used for drill downs
     */
    DrillDownParameterInfoDataType[] getParams();

    /**
     * Returns integer string (id) from metadata
     * detailing the column to use for the
     * search value that will be passed to
     * the new window generated from hyper link 
     */
    String getSearchValColumn();

    /**
     * Get details for type e.g. BSC / APN etc
     * this is needed when the SearchDataType object 
     * is dynamically built
     */
    String getType();

    boolean isDisablingTime();

}
