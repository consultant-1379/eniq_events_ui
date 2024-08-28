/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.DefinedWorkspaceType;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;
import com.ericsson.eniq.events.ui.client.workspace.launch.WorkspaceLaunchMenuResourceBundle;
import com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu.WorkspaceFilter.WorkspaceStateItem;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog.DialogType;
import com.ericsson.eniq.events.widgets.client.dialog.PromptDialog;
import com.ericsson.eniq.events.widgets.client.dialog.events.PromptOkEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Defined Workspaces Menu
 *
 * @author ecarsea
 * @since May 2012
 */
public class WorkspaceMenuView extends BaseView<WorkspaceMenuPresenter> implements IWorkspaceSelectionHandler {
    private static final int RENAME_BUTTON_HEIGHT = 20 + 40;/*margin + button bar height*/

    private static final int LAUNCH_BUTTON_HEIGHT = 20 + 40;/*margin + button bar height*/

    private static final int SEPARETOR_HEIGHT = 1;

    private static final String CONFIRM_DELETE_PROMPT_TITLE_MULTI = "Delete Workspaces";

    private static final String CONFIRM_DELETE_PROMPT_TITLE = "Delete Workspace";

    private static final String CONFIRM_DELETE_PROMPT_MULTI = "Are you sure you want to delete the selected workspaces? This can not be undone";

    private static final String CONFIRM_DELETE_PROMPT = "Are you sure you want to delete the selected workspace? This can not be undone";

    private final WorkspaceLaunchMenuResourceBundle workspaceLaunchMenuResourceBundle;

    interface WorkspaceMenuViewUiBinder extends UiBinder<Widget, WorkspaceMenuView> {
    }

    private static WorkspaceMenuViewUiBinder uiBinder = GWT.create(WorkspaceMenuViewUiBinder.class);

    //Do not remove
    /* @UiField(provided = true)
     ToggleRail<DefinedWorkspaceType> workspaceToggleRail;*/

    @UiField
    Button deleteBtn;

    @UiField
    Button renameBtn;

    @UiField
    Button launchBtn;

    @UiField
    WorkspaceFilterPanel workspaceFilterPanel;

    private WorkspaceFilter workspaceFilter;

    @SuppressWarnings("unused")
    @UiHandler("launchBtn")
    public void onLaunchClicked(final ClickEvent event) {
        final Collection<WorkspaceStateItem> selectedItems = workspaceFilterPanel.getSelectedItems();
        getPresenter().onLaunchWorkspaces(getWorkspaces(selectedItems));
    }

    @SuppressWarnings("unused")
    @UiHandler("deleteBtn")
    public void onDeleteClicked(final ClickEvent event) {
        getPresenter().deleteItems(getWorkspaces(workspaceFilterPanel.getSelectedItems()));
    }

    @SuppressWarnings("unused")
    @UiHandler("renameBtn")
    public void onRenameClicked(final ClickEvent event) {
        getPresenter().onRename(getWorkspaces(workspaceFilterPanel.getSelectedItems()));
    }

    @Inject
    public WorkspaceMenuView(final WorkspaceLaunchMenuResourceBundle workspaceLaunchMenuResourceBundle) {
        this.workspaceLaunchMenuResourceBundle = workspaceLaunchMenuResourceBundle;
        //DO NOT REMOVE - will be back in 13B!
        //workspaceToggleRail = createWorkspaceToggleRail();
        initWidget(uiBinder.createAndBindUi(this));
    }

    public List<WorkspaceState> getWorkspaces(final Collection<WorkspaceStateItem> selectedItems) {
        final List<WorkspaceState> workspaces = new ArrayList<WorkspaceState>();
        for (final WorkspaceStateItem item : selectedItems) {
            workspaces.add(item.getWorkspaceState());
        }
        return workspaces;
    }

    //todo do not delete - to be restored when we get predefined workspaces back in 13B
    /*private ToggleRail<DefinedWorkspaceType> createWorkspaceToggleRail() {

        final ToggleRail<DefinedWorkspaceType> workspaceToggle = new ToggleRail<DefinedWorkspaceType>("100%",
                new ToString<DefinedWorkspaceType>() {
                    @Override
                    public String toString(DefinedWorkspaceType value) {
                        return value.toString();
                    }
                });

        workspaceToggle.addValueChangeHandler(new ValueChangeHandler<DefinedWorkspaceType>() {
            @Override
            public void onValueChange(ValueChangeEvent<DefinedWorkspaceType> event) {
                configureWorkspaceType(event.getValue());
            }
        });

        workspaceToggle.add(DefinedWorkspaceType.PREDEFINED);
        workspaceToggle.add(DefinedWorkspaceType.USER_DEFINED);
        return workspaceToggle;
    }*/

    /**
     * @param workspaceType
     */
    private void configureWorkspaceType(final DefinedWorkspaceType workspaceType) {
        workspaceFilterPanel.clear();
        resetButtons();
        if (workspaceType.equals(DefinedWorkspaceType.PREDEFINED)) {
            workspaceFilterPanel.setHeader(DefinedWorkspaceType.PREDEFINED.toString());
            setRenameDeleteBtnsEnabled(false);
        } else {
            workspaceFilterPanel.setHeader(DefinedWorkspaceType.USER_DEFINED.toString());
            setRenameDeleteBtnsEnabled(true);
        }

        //do not remove - back in 13B
        //workspaceToggleRail.setValue(workspaceType);
        getPresenter().onToggleChange(workspaceType);
    }

    public void init() {
        final WorkspaceFilterView workspaceFilterView = new WorkspaceFilterView(workspaceLaunchMenuResourceBundle,
                workspaceFilterPanel.getFilterContent());
        workspaceFilterView.setWorkspaceSelectionHandler(this);
        this.workspaceFilter = new WorkspaceFilter(workspaceFilterView);
        workspaceFilterPanel.init(workspaceFilter);
        configureWorkspaceType(DefinedWorkspaceType.USER_DEFINED);
    }

    public void setFilterPanelHeight(final int viewHeight) {
        final int scrollBarHeight = viewHeight - (RENAME_BUTTON_HEIGHT + SEPARETOR_HEIGHT + LAUNCH_BUTTON_HEIGHT);
        workspaceFilterPanel.setScrollPanelHeight(scrollBarHeight);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu.IWorkspaceSelectionHandler#onDoubleClick()
     */
    @Override
    public void onDoubleClick(final WorkspaceState workspaceState) {
        getPresenter().onLaunchWorkspaces(new ArrayList<WorkspaceState>() {
            {
                add(workspaceState);
            }
        });

    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu.IWorkspaceSelectionHandler#onSelectionChange(int)
     */
    @Override
    public void onSelectionChange(final int selectedWorkspaces) {
        enableButtons(selectedWorkspaces);
        updateButtonText(selectedWorkspaces);
        getPresenter().onWorkspacesSelected(selectedWorkspaces);
    }

    /**
     *
     * @param selectedCount
     */
    private void enableButtons(final int selectedCount) {
        deleteBtn.setEnabled(selectedCount > 0);
        renameBtn.setEnabled(selectedCount == 1);
        launchBtn.setEnabled(selectedCount > 0);
    }

    private void updateButtonText(final int selected) {
        deleteBtn.setText("Delete" + appendSelected(selected));
        renameBtn.setText("Rename" + appendSelected(selected));
        launchBtn.setText("Launch" + appendSelected(selected));
    }

    /**
     * @param selected
     * @return
     */
    private String appendSelected(final int selected) {
        return ((selected > 0) ? " (" + selected + ")" : "");
    }

    public void updateWorkspaces(final List<WorkspaceState> workspaces, final List<String> startupItems) {
        workspaceFilterPanel.clear();
        resetButtons();

        //DO NOT REMOVE NEXT LINE WILL BE BACK IN 13B
        //workspaceFilter.updateWorkspaceList(workspaces, startupItems, workspaceToggleRail.getValue().toString());
        workspaceFilter.updateWorkspaceList(workspaces, startupItems, DefinedWorkspaceType.USER_DEFINED.toString());

    }

    /**
     * 
     */
    private void resetButtons() {
        enableButtons(0);
        updateButtonText(0);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu.IWorkspaceSelectionHandler#onStartupItemUpdate(com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState)
     */
    @Override
    public void onStartupItemUpdate(final WorkspaceState state) {
        getPresenter().onStartupItemUpdate(state);

    }

    /**
     * @param handler
     * @param multiple
     */
    public void showConfirmDialog(final PromptOkEventHandler handler, final boolean multiple) {
        final PromptDialog confirmDialog = new PromptDialog();
        confirmDialog.setGlassEnabled(true);
        confirmDialog.addOkEventHandler(handler);
        confirmDialog.show(multiple ? CONFIRM_DELETE_PROMPT_TITLE_MULTI : CONFIRM_DELETE_PROMPT_TITLE,
                multiple ? CONFIRM_DELETE_PROMPT_MULTI : CONFIRM_DELETE_PROMPT, DialogType.WARNING);
    }

    /**
     * @param enable
     */
    private void setRenameDeleteBtnsEnabled(final boolean enable) {
        renameBtn.setVisible(enable);
        deleteBtn.setVisible(enable);
    }

    /**
     * @return
     */
    /* public DefinedWorkspaceType getToggleWorkspaceType() {
         return workspaceToggleRail.getValue();
     }*/
}