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
public interface IWorkspaceConfiguration {
    IDimensionList getDimensions();

    IWindows getWindows();

    IWorkspaceMenu getWorkspaceMenu();

    IDimensionMenu getDimensionMenu();
}
