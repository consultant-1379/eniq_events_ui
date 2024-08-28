/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.common.widget.window.EniqWindowGwt;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceMessages;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceUtils;
import com.ericsson.eniq.events.widgets.client.textbox.ExtendedTextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;

/**
 * Workspace Rename Dialog
 *
 * @author ecarsea
 * @author eromsza
 * @since June 2012
 */
public class WorkspaceRenameDialogView extends BaseView<WorkspaceRenameDialogPresenter> {

    private static final String RENAME_WINDOW_ID = "selenium_tag_WORKSPACE_RENAME_DIALOG";

    private static final String DEFAULT_HEIGHT = "130px";

    private static final String DEFAULT_WIDTH = "380px";

    interface IWorkspaceRenameDialogPresenter {

        public void onCancelButtonClicked();

        public void onUpdateButtonClicked();
    }

    interface WorkspaceRenameDialogViewUiBinder extends UiBinder<Widget, WorkspaceRenameDialogView> {
    }

    private static WorkspaceRenameDialogViewUiBinder uiBinder = GWT.create(WorkspaceRenameDialogViewUiBinder.class);

    @UiField
    ExtendedTextBox renameWorkspaceName;

    @UiField
    Label errorLabel;

    @UiField
    Button updateButton;

    @UiField
    Button cancelButton;

    private EniqWindowGwt window;

    private WorkspaceMessages messages;

    @UiHandler("updateButton")
    public void onUpdateButtonClicked(@SuppressWarnings("unused") final ClickEvent event) {
        getPresenter().onUpdateButtonClicked();
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClicked(@SuppressWarnings("unused") final ClickEvent event) {
        getPresenter().onCancelButtonClicked();
    }

    @UiHandler("renameWorkspaceName")
    public void onRenameKeyUp(@SuppressWarnings("unused") final KeyUpEvent event) {
        updateButton.setEnabled(!isWorkspaceNameEntryBoxEmpty());
        showError(EMPTY_STRING);
    }

    @Inject
    public WorkspaceRenameDialogView(WorkspaceMessages messages) {
        this.messages = messages;
        window = new EniqWindowGwt();
        window.setGlassEnabled(true);
        window.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        window.getElement().setId(RENAME_WINDOW_ID);

        initWidget(uiBinder.createAndBindUi(this));

        window.setContent(this.asWidget());
    }

    public void launch(String savedWorkspaceName, String defaultText) {
        RootPanel.get().add(window);
        window.setTitle(messages.renameWorkspaceTitle(WorkspaceUtils.getVisibleWorkspaceName(savedWorkspaceName)));
        renameWorkspaceName.setDefaultText(defaultText);
        window.center();
    }

    public String getWorkspaceName() {
        return renameWorkspaceName.getText();
    }

    public void remove() {
        RootPanel.get().remove(window);
    }

    public void showError(String errorMessage) {
        errorLabel.setText(errorMessage);
        renameWorkspaceName.highlightInvalidField(EMPTY_STRING.equals(errorMessage) ? false : true);
    }

    private boolean isWorkspaceNameEntryBoxEmpty() {
        return EMPTY_STRING.equals(renameWorkspaceName.getText()) || renameWorkspaceName.containsDefaultText();
    }
}