package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import com.ericsson.eniq.events.common.client.datatype.IPropertiesState;
import com.ericsson.eniq.events.common.client.datatype.IThresholdState;
import com.ericsson.eniq.events.common.client.datatype.IUserPreferences;
import com.ericsson.eniq.events.common.client.preferences.IJsonAutoBeanFactory;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.IDashboardState;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.IPortletState;
import com.ericsson.eniq.events.ui.client.datatype.grid.IColumnState;
import com.ericsson.eniq.events.ui.client.datatype.grid.IGridState;
import com.ericsson.eniq.events.ui.client.workspace.datatype.AllWorkspacesState;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WindowState;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;
import com.google.web.bindery.autobean.shared.AutoBean;

/**
 * @author evyagrz
 * @since 10 2011	
 */
public interface JsonAutoBeanDataFactory extends IJsonAutoBeanFactory {

    AutoBean<IPortletState> portletState();

    AutoBean<IDashboardState> dashboardState();

    @Override
    AutoBean<IThresholdState> thresholdState();

    AutoBean<IGridState> gridState();

    @Override
    AutoBean<IUserPreferences> userSettings();

    AutoBean<IColumnState> columnState();

    @Override
    AutoBean<IPropertiesState> propertiesState();

    AutoBean<AllWorkspacesState> workspacesState();

    AutoBean<WorkspaceState> workspaceState();

    AutoBean<WindowState> windowState();
}