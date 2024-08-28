/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu;

import com.ericsson.eniq.events.common.client.service.DataServiceHelper;
import com.ericsson.eniq.events.ui.client.workspace.config.WorkspaceConfigService;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WindowState;
import com.ericsson.eniq.events.ui.client.workspace.launch.AbstractWindowMenuPresenter;
import com.ericsson.eniq.events.ui.client.workspace.launch.IWindowMenuView;
import com.ericsson.eniq.events.ui.client.workspace.launch.WorkspaceLaunchMenuResourceBundle;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class WorkspaceLaunchElementPresenter extends AbstractWindowMenuPresenter<WorkspaceLaunchElementView> {

    private IWorkspaceLaunchHandler launchHandler;

    /**
     * @param view
     * @param eventBus
     * @param resourceBundle
     * @param configService
     * @param dataServiceHelper
     */
    @Inject
    public WorkspaceLaunchElementPresenter(WorkspaceLaunchElementView view, EventBus eventBus,
            WorkspaceLaunchMenuResourceBundle resourceBundle, WorkspaceConfigService configService,
            DataServiceHelper dataServiceHelper) {
        super(view, eventBus, resourceBundle, configService, dataServiceHelper);
    }

    protected void init(WindowState windowState) {
        getView().init(windowState);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.workspace.launch.AbstractWindowMenuPresenter#getWindowMenuView()
     */
    @Override
    protected IWindowMenuView getWindowMenuView() {
        return getView();
    }

    public boolean isEnabled() {
        return getView().isEnabled();
    }

    /**
     * @return
     */
    public WindowState getWindowState() {
        return getView().getWindowState();
    }

    public void updateWindowState() {
        getView().updateWindowState();
    }

    public void toggleLaunchElement(boolean state) {
        launchHandler.onCheckboxChange(state);
    }

    public void addWorkspaceLaunchHandler(IWorkspaceLaunchHandler launchHandler) {
        this.launchHandler = launchHandler;
    }
}
