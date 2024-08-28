/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace;

import com.ericsson.eniq.events.ui.client.datatype.TabInfoDataType;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface IWorkspaceContainer {

    /**
     * Get the Perspective Container in which a workspace perspective will be placed. In this case it is a GXT TabPanel
     * @return
     */
    public TabPanel getTabContainer();

    public TabItem addWorkspaceTab(final TabInfoDataType tabInfo);

}
