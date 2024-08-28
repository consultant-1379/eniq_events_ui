/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.config.datatype;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface IWindow {

    String getId();
    
    String getWindowTitle();

    ISupportedTechnologies getSupportedTechnologies();

    ISupportedAccessGroups getSupportedAccessGroups();

    ISupportedDimensions getSupportedDimensions();

    IExcludedDimensions getExcludedDimensions();    

    String getWindowType();
}