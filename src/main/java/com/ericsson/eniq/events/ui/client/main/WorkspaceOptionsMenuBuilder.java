/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.main;

import com.ericsson.eniq.events.ui.client.datatype.TabInfoDataType;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.DefinedWorkspaceType;
import com.ericsson.eniq.events.ui.client.workspace.config.WorkspaceConfigService;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;
import com.ericsson.eniq.events.ui.client.workspace.events.CloseAllWorkspaceWindowsEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.CloseWorkspaceEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.SaveWorkspaceAsEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.SaveWorkspaceEvent;
import com.ericsson.eniq.events.ui.client.workspace.tab.WorkspaceOptionMenuItemType;
import com.ericsson.eniq.events.ui.client.workspace.tab.WorkspaceOptionsMenu;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

import java.util.Collection;

import static com.ericsson.eniq.events.ui.client.workspace.tab.WorkspaceOptionMenuItemType.CLOSE_ALL_WORKSPACES;
import static com.ericsson.eniq.events.ui.client.workspace.tab.WorkspaceOptionMenuItemType.CLOSE_WINDOWS;
import static com.ericsson.eniq.events.ui.client.workspace.tab.WorkspaceOptionMenuItemType.CLOSE_WORKSPACE;
import static com.ericsson.eniq.events.ui.client.workspace.tab.WorkspaceOptionMenuItemType.SAVE_WORKSPACE;
import static com.ericsson.eniq.events.ui.client.workspace.tab.WorkspaceOptionMenuItemType.SAVE_WORKSPACE_AS;

/**
 * @author ealeerm - Alexey Ermykin
 * @since 06/2012
 */
class WorkspaceOptionsMenuBuilder {
    private final TabInfoDataType tabInfo;
    private EventBus eventBus;

    public WorkspaceOptionsMenuBuilder(TabInfoDataType tabInfo, EventBus eventBus) {
        this.tabInfo = tabInfo;
        this.eventBus = eventBus;
    }

    public WorkspaceOptionsMenu invoke() {
        WorkspaceOptionsMenu optionsMenu = new WorkspaceOptionsMenu();
        optionsMenu.addItem(SAVE_WORKSPACE);
        optionsMenu.addItem(SAVE_WORKSPACE_AS);
        optionsMenu.addSeparator(SAVE_WORKSPACE, SAVE_WORKSPACE_AS);

        optionsMenu.addItem(CLOSE_WINDOWS);
        optionsMenu.addSeparator(CLOSE_WINDOWS);

        optionsMenu.addItem(CLOSE_WORKSPACE);
        optionsMenu.addItem(CLOSE_ALL_WORKSPACES);

        optionsMenu.addSelectionHandler(new SelectionHandler<WorkspaceOptionMenuItemType>() {
            @Override
            public void onSelection(final SelectionEvent<WorkspaceOptionMenuItemType> event) {
                final WorkspaceOptionMenuItemType item = event.getSelectedItem();

                switch (item) {
                case SAVE_WORKSPACE:

                    String name =tabInfo.getName();
                    WorkspaceConfigService workspaceConfigService = MainEntryPoint.getInjector().getWorkspaceConfigService();
                    Collection<WorkspaceState> states =  workspaceConfigService.getPredefinedWorkspaces();
                    boolean isPredefined = false;

                    for(WorkspaceState state:states ){
                        if(state.getName().equals(name)){
                            tabInfo.setWorkspaceType(DefinedWorkspaceType.PREDEFINED);
                            eventBus.fireEvent(new SaveWorkspaceAsEvent(tabInfo));
                            isPredefined = true;
                        }
                    }
                    if(isPredefined)
                        break;

                    eventBus.fireEvent(new SaveWorkspaceEvent(tabInfo));
                    break;
                case SAVE_WORKSPACE_AS:
                    eventBus.fireEvent(new SaveWorkspaceAsEvent(tabInfo));
                    break;
                case CLOSE_WINDOWS:
                    eventBus.fireEvent(new CloseAllWorkspaceWindowsEvent(tabInfo));
                    break;
                case CLOSE_WORKSPACE:
                    eventBus.fireEvent(new CloseWorkspaceEvent(tabInfo));
                    break;
                case CLOSE_ALL_WORKSPACES:
                    eventBus.fireEvent(new CloseWorkspaceEvent(TabInfoDataType.ALL_USER_TABS));
                    break;
                default:
                    Window.alert("Option \"" + item + "\" is not supported yet.");
                    break;
                }
            }
        });
        return optionsMenu;
    }
}
