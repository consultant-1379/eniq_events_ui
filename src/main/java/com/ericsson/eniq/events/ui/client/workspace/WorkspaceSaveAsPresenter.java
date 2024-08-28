/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace;

import java.util.Map;

import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.ui.client.datatype.TabInfoDataType;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;
import com.ericsson.eniq.events.ui.client.workspace.events.SaveWorkspaceAsEvent;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.MAX_WORKSPACE_NAME_LENGTH;

/**
 * Presenter for the save as dialog.
 *
 * @author ealeerm - Alexey Ermykin
 * @author eromsza
 * @since 06/2012
 */
public class WorkspaceSaveAsPresenter extends BasePresenter<WorkspaceSaveAsView> implements
        WorkspaceSaveAsView.IWorkspaceSaveAsPresenter {

    private TabInfoDataType tabInfo;

    private Map<String, WorkspaceState> workspaceStateMap;

    private WorkspaceMessages messages;

    @Inject
    public WorkspaceSaveAsPresenter(final EventBus eventBus, final WorkspaceSaveAsView view, final WorkspaceMessages messages) {
        super(view, eventBus);
        this.messages =  messages;
        bind();
    }

    public void launch(Map<String, WorkspaceState> workspaceStateMap, TabInfoDataType tabInfo) {
        this.workspaceStateMap = workspaceStateMap;
        this.tabInfo = tabInfo;
        getView().launch(tabInfo.getName(), messages.provideWorkspaceName());
    }

    public void onCancelButtonClicked() {
        getView().remove();
    }

    public void onSaveButtonClicked() {
        final String workspaceName = getView().getWorkspaceName();

        final String errorMessage = validateWorkspaceName(workspaceName);
        if (errorMessage == null) {
            SaveWorkspaceAsEvent event = new SaveWorkspaceAsEvent(tabInfo);
            event.setNewWorkspaceName(workspaceName);
            getEventBus().fireEvent(event);
            getView().remove();
        } else {
            getView().showError(errorMessage);
        }
    }
    //TODO Remove Check "...equals("Predefined")" when Predefines are added again.
    private String validateWorkspaceName(String workspaceName) {
        for (WorkspaceState workspaceState : workspaceStateMap.values()) {
            if (workspaceState.getName().equals(workspaceName)&&!workspaceState.getWorkspaceType().equals("Predefined")) {
                return messages.workspaceNameExists();
            }
        }

        final String name = workspaceName.trim();

        if (name.length() >= MAX_WORKSPACE_NAME_LENGTH) {
            return messages.workspaceNameSizeExceeded();
        }

        RegExp regExp = RegExp.compile(messages.specialCharactersRegEx());
        MatchResult matcher = regExp.exec(workspaceName);
        if (matcher != null) {
            return messages.invalidWorkspaceNameSpecialCharacter(matcher.toString());
        }

        regExp = RegExp.compile(messages.newWorkspaceRegEx());
        matcher = regExp.exec(workspaceName);
        if (matcher != null) {
            return messages.invalidWorkspaceNameNewWorkspace(workspaceName);
        }

        return null;
    }
}
