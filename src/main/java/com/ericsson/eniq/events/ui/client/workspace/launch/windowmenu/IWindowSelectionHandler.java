/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu;

import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IWindow;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface IWindowSelectionHandler {
    void onDoubleClick(IWindow window);

    /**
     * @param selectedWindows
     */
    void onSelectionChange(int selectedWindows);
}
