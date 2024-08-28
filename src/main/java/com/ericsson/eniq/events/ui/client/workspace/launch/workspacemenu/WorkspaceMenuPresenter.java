/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.common.client.preferences.UserPreferencesHelper;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.DefinedWorkspaceType;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceUtils;
import com.ericsson.eniq.events.ui.client.workspace.config.WorkspaceConfigService;
import com.ericsson.eniq.events.ui.client.workspace.datatype.AllWorkspacesState;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;
import com.ericsson.eniq.events.ui.client.workspace.events.StatusBarChangeEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceDataSaveCompleteEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceDataSaveCompleteEventHandler;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceDataSaveRequestEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceLaunchConfigEvent;
import com.ericsson.eniq.events.widgets.client.dialog.events.PromptOkEvent;
import com.ericsson.eniq.events.widgets.client.dialog.events.PromptOkEventHandler;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.WORKSPACE_MANAGEMENT_ID;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class WorkspaceMenuPresenter extends BasePresenter<WorkspaceMenuView> {

    private final List<WorkspaceState> savedWorkspaceStates = new ArrayList<WorkspaceState>();

    private final List<String> startupItems = new ArrayList<String>();

    private final List<WorkspaceState> predefinedWorkspaces = new ArrayList<WorkspaceState>();

    private final UserPreferencesHelper userPreferencesHelper;

    private final Provider<WorkspaceRenameDialogPresenter> workspaceRenameDialogPresenterProvider;

    private DefinedWorkspaceType workspaceType = DefinedWorkspaceType.PREDEFINED;

    /**
     * @param view
     * @param eventBus
     * @param userPreferencesHelper
     * @param workspaceRenameDialogPresenterProvider
     * @param configService
     */
    @Inject
    public WorkspaceMenuPresenter(WorkspaceMenuView view, EventBus eventBus,
            UserPreferencesHelper userPreferencesHelper,
            Provider<WorkspaceRenameDialogPresenter> workspaceRenameDialogPresenterProvider, WorkspaceConfigService configService) {
        super(view, eventBus);
        this.userPreferencesHelper = userPreferencesHelper;
        this.workspaceRenameDialogPresenterProvider = workspaceRenameDialogPresenterProvider;
        loadSavedWorkspacesFromPreferences();
        predefinedWorkspaces.addAll(configService.getPredefinedWorkspaces());
        bind();
        view.init();
    }

    private void loadSavedWorkspacesFromPreferences() {
        savedWorkspaceStates.clear();
        startupItems.clear();
        AllWorkspacesState allWorkspacesState = userPreferencesHelper.getStateById(WORKSPACE_MANAGEMENT_ID,
                AllWorkspacesState.class);
        if (allWorkspacesState != null) {
            savedWorkspaceStates.addAll(WorkspaceUtils.getCollectionWithNullCheck(allWorkspacesState.getWorkspaces()));
            startupItems.addAll(allWorkspacesState.getStartUpItems());
        }
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.common.client.mvp.BasePresenter#onBind()
     */
    @Override
    protected void onBind() {
        super.onBind();
        getEventBus().addHandler(WorkspaceDataSaveCompleteEvent.TYPE, new WorkspaceDataSaveCompleteEventHandler() {

            /* (non-Javadoc)
             * @see com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceDataUpdateCompleteEventHandler#onWorkspaceUpdateComplete(com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceDataUpdateCompleteEvent)
             */
            @Override
            public void onWorkspaceSaveComplete(WorkspaceDataSaveCompleteEvent event) {
                loadSavedWorkspacesFromPreferences();
                getView().updateWorkspaces(getWorkspaceStates(), startupItems);
            }
        });
    }

    /**
     * @return
     */
    protected List<WorkspaceState> getWorkspaceStates() {
        return workspaceType.equals(DefinedWorkspaceType.PREDEFINED) ? predefinedWorkspaces : savedWorkspaceStates;
    }

    /**
     * @param workspaceStates
     */
    public void onLaunchWorkspaces(List<WorkspaceState> workspaceStates) {
        getEventBus().fireEvent(new WorkspaceLaunchConfigEvent(workspaceStates));
    }

    /**
     * @param selectedWorkspaces
     */
    public void onWorkspacesSelected(int selectedWorkspaces) {
        getEventBus().fireEvent(new StatusBarChangeEvent(selectedWorkspaces + " Analysis Workspaces Selected"));
    }

    /**
     * @param selectedItems
     */
    public void deleteItems(final Collection<WorkspaceState> selectedItems) {
        getView().showConfirmDialog(new PromptOkEventHandler() {

            @Override
            public void onPromptOk(final PromptOkEvent event) {
                savedWorkspaceStates.removeAll(selectedItems);
                getEventBus().fireEvent(new WorkspaceDataSaveRequestEvent(savedWorkspaceStates, startupItems));

            }
        }, selectedItems.size() > 1);
    }

    /**
     * @param workspaceState
     */
    public void onStartupItemUpdate(WorkspaceState workspaceState) {
        String id = workspaceState.getId();
        if (startupItems.contains(id)) {
            startupItems.remove(id);
        } else {
            startupItems.add(id);
        }
        getEventBus().fireEvent(new WorkspaceDataSaveRequestEvent(savedWorkspaceStates, startupItems));
    }

    /**
     *
     * @param selectedWorkspaceStates
     */
    public void onRename(List<WorkspaceState> selectedWorkspaceStates) {
        WorkspaceRenameDialogPresenter workspaceRenameDialogPresenter = workspaceRenameDialogPresenterProvider.get();
        workspaceRenameDialogPresenter.launch(savedWorkspaceStates, startupItems, selectedWorkspaceStates.get(0));
    }

    /**
     * @param value
     */
    public void onToggleChange(DefinedWorkspaceType value) {
        workspaceType = value;
        getView().updateWorkspaces(getWorkspaceStates(), startupItems);
    }
}
