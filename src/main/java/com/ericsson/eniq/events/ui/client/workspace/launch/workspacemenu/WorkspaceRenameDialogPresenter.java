/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu;

import java.util.List;
import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceMessages;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceDataSaveRequestEvent;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.MAX_WORKSPACE_NAME_LENGTH;

/**
 * Presenter for the rename dialog.
 *
 * @author ealeerm - Alexey Ermykin
 * @author eromsza
 * @since 06/2012
 */
public class WorkspaceRenameDialogPresenter extends BasePresenter<WorkspaceRenameDialogView> implements
        WorkspaceRenameDialogView.IWorkspaceRenameDialogPresenter {

    private List<WorkspaceState> workspaceStates;

    private List<String> startupItems;

    private WorkspaceState workspaceState;

    private WorkspaceMessages messages;

    @Inject
    public WorkspaceRenameDialogPresenter(final EventBus eventBus, final WorkspaceRenameDialogView view, final WorkspaceMessages messages) {
        super(view, eventBus);
        this.messages = messages;
        bind();
    }

    public void launch(List<WorkspaceState> workspaceStates, List<String> startupItems, WorkspaceState selectedWorkspace) {
        this.workspaceStates = workspaceStates;
        this.startupItems = startupItems;
        this.workspaceState = selectedWorkspace;
        getView().launch(workspaceState.getName(), messages.provideWorkspaceName());
    }

    public void onCancelButtonClicked() {
        getView().remove();
    }

    public void onUpdateButtonClicked() {
        final String workspaceName = getView().getWorkspaceName();

        final String errorMessage = validateWorkspaceName(workspaceName);
        if (errorMessage == null) {
            workspaceState.setName(getView().getWorkspaceName());
            WorkspaceDataSaveRequestEvent event = new WorkspaceDataSaveRequestEvent(workspaceStates, startupItems, true);
            getEventBus().fireEvent(event);
            getView().remove();
        } else {
            getView().showError(errorMessage);
        }
    }

    private String validateWorkspaceName(String workspaceName) {
        for (WorkspaceState workspaceState : workspaceStates) {
            if (workspaceState.getName().equals(workspaceName)) {
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
