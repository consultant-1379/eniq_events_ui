/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.datatype;

import java.util.List;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface WorkspaceState {
    String getId();

    void setId(String id);

    String getName();

    void setName(String name);

    List<WindowState> getWindows();

    void setWindows(List<WindowState> windows);

    @PropertyName("windowPositioning")
    String getWindowPositioning();

    void setWorkspaceType(String workspaceType);

    String getWorkspaceType();
}
