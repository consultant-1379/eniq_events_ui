/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace;

import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowView;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.JsonAutoBeanDataFactory;
import com.ericsson.eniq.events.ui.client.datatype.TabInfoDataType;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;
import com.ericsson.eniq.events.ui.client.workspace.events.CloseAllWorkspaceWindowsEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.CloseAllWorkspaceWindowsEventHandler;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceLaunchConfigCompleteEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceLaunchConfigCompleteEventHandler;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceStatusChangeEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceStatusChangeEventHandler;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceToolbarWindowCloseEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceToolbarWindowCloseEventHandler;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceToolbarWindowOpenEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceToolbarWindowOpenEventHandler;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceToolbarWindowTitleUpdateEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceToolbarWindowTitleUpdateEventHandler;
import com.ericsson.eniq.events.ui.client.workspace.launch.WorkspaceLaunchPresenter;
import com.ericsson.eniq.events.ui.client.workspace.launch.WorkspaceLaunchView;
import com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu.WorkspaceLaunchDialogPresenter;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for workspace (single tab)
 * @author ecarsea
 * @since 2012
 *
 */
public class WorkspacePresenter extends BasePresenter<WorkspaceView> {

    private final WorkspaceLaunchPresenter workspaceLaunchPresenter;

    private final JsonAutoBeanDataFactory factory;

    private final WorkspaceLaunchDialogPresenter workspaceLaunchDialogPresenter;

    private TabInfoDataType tabInfo;

    /**
     * @param view
     * @param eventBus
     * @param workspaceLaunchPresenter
     * @param factory
     * @param workspaceLaunchDialogPresenter
     */
    @Inject
    public WorkspacePresenter(final WorkspaceView view, final EventBus eventBus,
            final WorkspaceLaunchPresenter workspaceLaunchPresenter, final JsonAutoBeanDataFactory factory,
            final WorkspaceLaunchDialogPresenter workspaceLaunchDialogPresenter) {
        super(view, eventBus);
        this.workspaceLaunchPresenter = workspaceLaunchPresenter;
        this.factory = factory;
        this.workspaceLaunchDialogPresenter = workspaceLaunchDialogPresenter;
        view.addLaunchMenu(workspaceLaunchPresenter.getView());
        bind();
    }

    /** @deprecated hack for KPI, do not reuse this  purlease
     */
    @Deprecated
    public WorkspaceLaunchPresenter getWorkspaceLaunchPresenter() {
        return this.workspaceLaunchPresenter;
    }

    public void init(final TabInfoDataType dataType) {
        this.tabInfo = dataType;
        workspaceLaunchPresenter.init(getView().getWindowContainer(), tabInfo.getId());
        workspaceLaunchDialogPresenter.init(tabInfo);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.common.client.mvp.BasePresenter#onUnbind()
     */
    @Override
    protected void onUnbind() {
        super.onUnbind();
        workspaceLaunchPresenter.unbind();
    }

    @Override
    protected void onBind() {
        registerHandler(getEventBus().addHandler(WorkspaceStatusChangeEvent.TYPE,
                new WorkspaceStatusChangeEventHandler() {

                    @Override
                    public void onWindowStatusChange(final WorkspaceStatusChangeEvent event) {
                        if (event.getWorkspaceId().equals(tabInfo.getId())) {
                            if (event.getCurrentOpenWindowCount() >= 0) {
                                getView().setToolBarButtonsEnabled(event.getCurrentOpenWindowCount() > 1);
                            }
                        }
                    }
                }));

        registerHandler(getEventBus().addHandler(WorkspaceToolbarWindowOpenEvent.TYPE,
                new WorkspaceToolbarWindowOpenEventHandler() {

                    @Override
                    public void onWindowToolbarOpenEvent(final WorkspaceToolbarWindowOpenEvent event) {
                        if (event.getWorkspaceId().equals(tabInfo.getId())) {
                            /** Adding MultiInstance Buttons for Launch Menu windows.
                             */
                            addCategoryMenuButton(event);
                        }
                    }
                }));

        registerHandler(getEventBus().addHandler(WorkspaceToolbarWindowTitleUpdateEvent.TYPE,
                new WorkspaceToolbarWindowTitleUpdateEventHandler() {

                    @Override
                    public void onWindowTitleUpdateEvent(final WorkspaceToolbarWindowTitleUpdateEvent event) {
                        if (event.getWorkspaceId().equals(tabInfo.getId())) {
                            getView().updateCategoryButton(event.getCategory(), event.getBaseWindow(),
                                    event.getTitle(), event.getIcon());
                        }
                    }
                }));

        registerHandler(getEventBus().addHandler(WorkspaceToolbarWindowCloseEvent.TYPE,
                new WorkspaceToolbarWindowCloseEventHandler() {

                    @Override
                    public void onWindowToolbarCloseEvent(final WorkspaceToolbarWindowCloseEvent event) {
                        if (event.getWorkspaceId().equals(tabInfo.getId())) {
                            removeCategoryMenuButton(event.getBaseWindow());
                        }
                    }
                }));

        /** Launch has been clicked on the Restore Workspace Dialog Box so restore windows etc **/
        registerHandler(getEventBus().addHandler(WorkspaceLaunchConfigCompleteEvent.TYPE,
                new WorkspaceLaunchConfigCompleteEventHandler() {

                    @Override
                    public void onWorkspaceLaunchConfigComplete(final WorkspaceLaunchConfigCompleteEvent event) {
                        if (event.getWorkspaceId().equals(tabInfo.getId())) {
                            workspaceLaunchPresenter.restoreWorkspace(event.getWorkspaceState());
                        }

                    }
                }));

        registerHandler(getEventBus().addHandler(CloseAllWorkspaceWindowsEvent.TYPE,
                new CloseAllWorkspaceWindowsEventHandler() {

                    @Override
                    public void closeAllWorkspaceWindows(final CloseAllWorkspaceWindowsEvent event) {
                        if (event.getTabInfo().getId().equals(tabInfo.getId())) {
                            workspaceLaunchPresenter.closeAllWindows();
                        }
                    }
                }));
    }

    public void cascade() {
        workspaceLaunchPresenter.cascade();
    }

    public void tile() {
        workspaceLaunchPresenter.tile();
    }

    public void addCategoryMenuButton(final WorkspaceToolbarWindowOpenEvent event) {
        getView().addCategoryMenuButton(event.getCategory(), event.getTitle(), event.getIcon(), event.getBaseWindow());
    }

    public void removeCategoryMenuButton(final IBaseWindowView window) {
        getView().removeCategoryMenuButton(window);
    }

    public WorkspaceState getWorkspaceData() {
        return workspaceLaunchPresenter.getWorkspaceState(factory);
    }

    /**
     * @param workspaceState
     */
    public void restoreWorkspace(final WorkspaceState workspaceState) {
        /** Launch any dialog box allowing user to configure saved windows if there are any **/
        if (workspaceState.getWindows().size() > 0) {
            workspaceLaunchDialogPresenter.launch(tabInfo.getId(), workspaceState, getView());
        }
    }

    /**
     * @return the tabInfo
     */
    public TabInfoDataType getWorkspaceInfo() {
        return tabInfo;
    }

    public WorkspaceLaunchView getLaunchMenu() {
        return workspaceLaunchPresenter.getView();
    }
}
