/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace;

import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IWorkspaceConfigurationWrapper;
import com.ericsson.eniq.events.ui.client.workspace.datatype.AllWorkspacesState;
import com.ericsson.eniq.events.ui.client.workspace.datatype.PredefinedWorkspaceState;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface WorkspaceConfigAutoBeanFactory extends AutoBeanFactory {
    AutoBean<IWorkspaceConfigurationWrapper> workspaceConfigurationWrapper();

    AutoBean<AllWorkspacesState> workspacesState();

    AutoBean<PredefinedWorkspaceState> predefinedWorkspaces();
}
