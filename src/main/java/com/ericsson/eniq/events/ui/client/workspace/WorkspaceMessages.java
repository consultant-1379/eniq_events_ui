package com.ericsson.eniq.events.ui.client.workspace;

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.Messages;

/**
 * Created with IntelliJ IDEA.
 * User: emauoco
 * Date: 07/11/12
 */
@DefaultLocale("en")
public interface WorkspaceMessages extends Messages {

    @DefaultMessage("New Workspace {0}")
    String newWorkspace(int workspaceCount);

    @DefaultMessage("Close All Workspaces")
    String closeAllWorkspacesTitle();

    @DefaultMessage("Are you sure you want to close all Workspaces?\n\nYou have {0} unsaved Workspaces")
    @PluralText({"one", "Are you sure you want to close all Workspaces?\n\nYou have {0} unsaved Workspace"})
    String closeAllWorkspacesMessage(@PluralCount int unsavedWorkspacesCount);

    @DefaultMessage("Unsaved Workspaces")
    String logoutTitle();

    @DefaultMessage("You have {0} unsaved Workspaces.\nAre you sure you want to log out?")
    @PluralText({"one", "You have {0} unsaved Workspace.\nAre you sure you want to log out?"})
    String logout(@PluralCount int unsavedWorkspacesCount);

    @DefaultMessage("You have {0} unsaved Workspaces.\nAre you sure you want to log out?")
    @PluralText({"one", "You have {0} unsaved Workspace.\nAre you sure you want to log out?"})
    String browserLogout(@PluralCount int unsavedWorkspacesCount);

    @DefaultMessage("Close Workspace")
    String closeWorkspaceTitle();

    @DefaultMessage("Are you sure you want to close this Workspace without saving?")
    String closeWorkspaceMessage();

    @DefaultMessage("Active Workspaces Error")
    @PluralText({"one", "Active Workspace Error"})
    String activeWorkspacesTitle(@Optional @PluralCount int activeWorkspacesCount);

    @DefaultMessage("You selected {0} Workspaces: {1} are already active.")
    @PluralText({"one", "You selected 1 Workspace: {1} is already active."})
    String activeWorkspacesMessage(@Optional @PluralCount int activeWorkspacesCount, String workspaceNames);

    @DefaultMessage("Number of Workspaces Error")
    String maxWorkspacesTitle();

    @DefaultMessage("Maximum number of user Workspaces is: {0}")
    String maxWorkspacesMessage(int activeWorkspacesCount);

    @DefaultMessage("Enter Workspace name...")
    String provideWorkspaceName();

    @DefaultMessage("Workspace name size exceeded")
    String workspaceNameSizeExceeded();
    
    @DefaultMessage("Workspace name already exists")
    String workspaceNameExists();

    // Backslash is escaped as \\\\
    // Apostrophe is escaped as ''
    // Quote is escaped as \"
    @DefaultMessage("[''\"<>/;:|`?!$@£~#%&*\\\\]")
    String specialCharactersRegEx();

    @DefaultMessage("^(New Workspace [\\d]+)$")
    String newWorkspaceRegEx();

    @DefaultMessage("Invalid Workspace Name (''{0}'' special character)")
    String invalidWorkspaceNameSpecialCharacter(String invalidCharacters);

    @DefaultMessage("Invalid Workspace Name (''{0}'' not allowed)")
    String invalidWorkspaceNameNewWorkspace(String workspaceName);

    @DefaultMessage("Rename Workspace \"{0}\"")
    String renameWorkspaceTitle(String wokspaceName);

    @DefaultMessage("Save Workspace As...")
    String saveAsWorkspaceTitle();

}

