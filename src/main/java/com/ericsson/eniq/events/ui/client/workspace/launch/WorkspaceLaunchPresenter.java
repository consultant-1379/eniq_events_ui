/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch;

import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.JsonAutoBeanDataFactory;
import com.ericsson.eniq.events.ui.client.workspace.IWindowContainer;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceWindowController;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;
import com.ericsson.eniq.events.ui.client.workspace.events.GroupPopupChangeEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.LaunchWindowEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.LaunchWindowEventHandler;
import com.ericsson.eniq.events.ui.client.workspace.events.StatusBarChangeEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.StatusBarChangeEventHandler;
import com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu.WindowsMenuPresenter;
import com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu.WindowsMenuView;
import com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu.WorkspaceMenuPresenter;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class WorkspaceLaunchPresenter extends BasePresenter<WorkspaceLaunchView> {

    private final WorkspaceWindowController workspaceController;

    private final WindowsMenuView windowsMenuView;

    private final WorkspaceMenuPresenter workspaceMenuPresenter;

    private final WindowsMenuPresenter windowsMenuPresenter;

    private String workspaceId;

    /**
     * @param view
     * @param eventBus
     */
    @Inject
    public WorkspaceLaunchPresenter(WorkspaceLaunchView view, EventBus eventBus,
            WorkspaceWindowController workspaceController, WindowsMenuPresenter windowsMenuPresenter,
            WorkspaceMenuPresenter workspaceMenuPresenter) {
        super(view, eventBus);
        this.workspaceController = workspaceController;
        this.workspaceMenuPresenter = workspaceMenuPresenter;
        this.windowsMenuPresenter = windowsMenuPresenter;
        bind();
        this.windowsMenuView = windowsMenuPresenter.getView();
        view.init(windowsMenuPresenter.getView(), workspaceMenuPresenter.getView());
    }

    /** @deprecated hack for KPI, do not reuse this  purlease
     */
    @Deprecated
    public WorkspaceWindowController getWorkspaceController() {
        return this.workspaceController;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.common.client.mvp.BasePresenter#onBind()
     */
    @Override
    protected void onBind() {
        super.onBind();
        registerHandler(getEventBus().addHandler(LaunchWindowEvent.TYPE, new LaunchWindowEventHandler() {

            @Override
            public void onWindowLaunch(final WindowLaunchParams launchParams) {
                /** Only Launch windows in this tab that are source from the launch menu of this tab **/
                if (launchParams.getSource().equals(windowsMenuView)) {
                    getView().windowLaunchCompleted();
                    /**Launch bar is slow to disappear, so trying to launch the windows in a deferred command **/
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                        @Override
                        public void execute() {
                            getEventBus().fireEvent(new GroupPopupChangeEvent(workspaceId, getView().isPinned()));
                            workspaceController.launchWindow(launchParams, getView().isPinned());
                        }
                    });

                }
            }
        }));

        registerHandler(getEventBus().addHandler(StatusBarChangeEvent.TYPE, new StatusBarChangeEventHandler() {
            @Override
            public void onStatusBarTextChange(String statusBarText) {
                getView().updateStatusLabel(statusBarText);
            }
        }));
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.common.client.mvp.BasePresenter#onUnbind()
     */
    @Override
    protected void onUnbind() {
        super.onUnbind();
        windowsMenuPresenter.unbind();
        workspaceMenuPresenter.unbind();
        workspaceController.unbind();
    }

    /**
     * @param windowContainer
     * @param workspaceId
     */
    public void init(IWindowContainer windowContainer, String workspaceId) {
        this.workspaceId = workspaceId;
        this.workspaceController.init(workspaceId, windowContainer);
        this.windowsMenuPresenter.init(workspaceId);
    }

    public void cascade() {
        slideOutIfNotPinned();
        workspaceController.cascade(getView().isPinned());
    }

    public void tile() {
        slideOutIfNotPinned();
        workspaceController.tile(getView().isPinned());
    }

    private void slideOutIfNotPinned() {
        if (!getView().isPinned()) {
            getView().slideOut();
        }
    }

    public WorkspaceState getWorkspaceState(JsonAutoBeanDataFactory factory) {
        return workspaceController.getWorkspaceState(factory);
    }

    public void restoreWorkspace(WorkspaceState workspaceState) {
        getView().slideOut();
        workspaceController.restoreWorkspace(workspaceState);
    }

    public void closeAllWindows() {
        workspaceController.closeAllWindows();

    }

    String getWorkspaceId() {
        return workspaceId;
    }

    public void savePinnedState(boolean pinDown) {
        workspaceController.setIsPinned(pinDown);
    }
}
