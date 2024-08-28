/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu;

import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.ui.client.datatype.TabInfoDataType;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceView;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WindowState;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;
import com.ericsson.eniq.events.ui.client.workspace.events.CloseWorkspaceEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceLaunchConfigCompleteEvent;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class WorkspaceLaunchDialogPresenter extends BasePresenter<WorkspaceLaunchDialogView> implements IWorkspaceLaunchHandler{

    private final Provider<WorkspaceLaunchElementPresenter> workspaceLaunchElementPresenterProvider;

    private final List<WorkspaceLaunchElementPresenter> windowConfigList = new ArrayList<WorkspaceLaunchElementPresenter>();

    private TabInfoDataType tabInfo;

    private WorkspaceState workspaceState;

    private String workspaceId;

    private int numberOfEnabledLaunchElements = 0;

    private static final String TITLE = "Update Workspace";
    /**
     * @param view
     * @param eventBus
     */
    @Inject
    public WorkspaceLaunchDialogPresenter(WorkspaceLaunchDialogView view, EventBus eventBus,
            Provider<WorkspaceLaunchElementPresenter> workspaceLaunchElementPresenterProvider) {
        super(view, eventBus);
        this.workspaceLaunchElementPresenterProvider = workspaceLaunchElementPresenterProvider;
        bind();
    }

    public void init(TabInfoDataType dataType) {
        this.tabInfo = dataType;
    }

    public void launch(String workspaceId, WorkspaceState workspaceState, WorkspaceView workspaceView) {
        getView().launch(workspaceView);
        StringBuilder sb = new StringBuilder(TITLE);
        sb.append(", ");
        sb.append(workspaceState.getName());
        getView().setWindowTitle(sb.toString());
        windowConfigList.clear();
        this.workspaceState = workspaceState;
        this.workspaceId = workspaceId;
        for (WindowState windowState : workspaceState.getWindows()) {
            WorkspaceLaunchElementPresenter elementPresenter = workspaceLaunchElementPresenterProvider.get();
            elementPresenter.init(windowState);
            elementPresenter.addWorkspaceLaunchHandler(this);
            getView().addWindowConfigPanel(elementPresenter.getView());
            windowConfigList.add(elementPresenter);
            //launchElements are initially all enabled.
            numberOfEnabledLaunchElements++;
        }
    }

    public void onLaunchWindows() {
        for (WorkspaceLaunchElementPresenter presenter : windowConfigList) {
            /** For checkboxed windows update the window state. otherwise remove it from the workspace state **/
            if (presenter.isEnabled()) {
                presenter.updateWindowState();
                //show the window on relaunch of workspace
                presenter.getWindowState().setEnabled(true);
            }else {
                //do not show the window on relaunch of workspace
                presenter.getWindowState().setEnabled(false);
            }
        }
        getEventBus().fireEvent(new WorkspaceLaunchConfigCompleteEvent(workspaceId, workspaceState));
    }

    public void onCancelWindows() {
        getEventBus().fireEvent(new CloseWorkspaceEvent(tabInfo));
    }

    @Override
    public void onCheckboxChange(boolean enabled) {
        if(enabled){
            numberOfEnabledLaunchElements++;
        }else{
            numberOfEnabledLaunchElements--;
        }

        if(numberOfEnabledLaunchElements > 0){
            getView().launchBtn.setEnabled(true);
        }else{
            getView().launchBtn.setEnabled(false);
        }
    }
}
