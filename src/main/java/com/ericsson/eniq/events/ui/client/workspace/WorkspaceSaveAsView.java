/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.common.widget.window.EniqWindowGwt;
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
 * @author ealeerm - Alexey Ermykin
 * @author eromsza
 * @since 06/2012
 */
public class WorkspaceSaveAsView extends BaseView<WorkspaceSaveAsPresenter> {

    private static final String SAVE_AS_WINDOW_ID = "selenium_tag_WORKSPACE_SAVE_AS";

    private static final String DEFAULT_HEIGHT = "130px";

    private static final String DEFAULT_WIDTH = "320px";

    interface IWorkspaceSaveAsPresenter {

        public void onCancelButtonClicked();

        public void onSaveButtonClicked();
    }

    interface WorkspaceSaveAsViewUiBinder extends UiBinder<Widget, WorkspaceSaveAsView> {
    }

    private static WorkspaceSaveAsViewUiBinder uiBinder = GWT.create(WorkspaceSaveAsViewUiBinder.class);

    @UiField
    ExtendedTextBox saveAsWorkspaceName;

    @UiField
    Label errorLabel;

    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;

    private final EniqWindowGwt window;

    private final WorkspaceMessages messages;

    @SuppressWarnings("unused")
    @UiHandler("saveButton")
    public void onSaveClicked(final ClickEvent event) {
        getPresenter().onSaveButtonClicked();
    }

    @SuppressWarnings("unused")
    @UiHandler("cancelButton")
    public void onCancelClicked(final ClickEvent event) {
        getPresenter().onCancelButtonClicked();
    }

    @UiHandler("saveAsWorkspaceName")
    public void onSaveAsKeyUp(@SuppressWarnings("unused") final KeyUpEvent event) {
        saveButton.setEnabled(!isWorkspaceNameEntryBoxEmpty());
        showError(EMPTY_STRING);
    }

    @Inject
    public WorkspaceSaveAsView(final WorkspaceMessages messages) {
        this.messages = messages;
        window = new EniqWindowGwt();
        window.setGlassEnabled(true);
        window.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        window.getElement().setId(SAVE_AS_WINDOW_ID);

        initWidget(uiBinder.createAndBindUi(this));

        window.setContent(this.asWidget());
    }

    public void launch(String savedWorkspaceName, String defaultText) {
        RootPanel.get().add(window);
        window.setTitle(messages.saveAsWorkspaceTitle());
        saveAsWorkspaceName.setDefaultText(defaultText);
        window.center();
    }

    public String getWorkspaceName() {
        return saveAsWorkspaceName.getText();
    }

    public void remove() {
        RootPanel.get().remove(window);
    }

    public void showError(String errorMessage) {
        errorLabel.setText(errorMessage);
        saveAsWorkspaceName.highlightInvalidField(EMPTY_STRING.equals(errorMessage) ? false : true);
    }

    private boolean isWorkspaceNameEntryBoxEmpty() {
        return EMPTY_STRING.equals(saveAsWorkspaceName.getText()) || saveAsWorkspaceName.containsDefaultText();
    }
}