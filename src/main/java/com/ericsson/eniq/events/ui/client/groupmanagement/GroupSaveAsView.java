/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.widgets.client.textbox.ExtendedTextBox;
import com.ericsson.eniq.events.ui.client.common.widget.window.EniqWindowEvent;
import com.ericsson.eniq.events.ui.client.common.widget.window.EniqWindowGwt;
import com.ericsson.eniq.events.ui.client.common.widget.window.EniqWindowListener;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;

/**
 * @author ecarsea
 * @since 2011
 * 
 */
public class GroupSaveAsView extends BaseView<GroupSaveAsPresenter> {

    /**
     * 
     */
    private static final String SAVE_AS_WINDOW_ID = "selenium_tag_GROUP_SAVE_AS_WINDOW";

    private static GroupSaveAsViewUiBinder uiBinder = GWT.create(GroupSaveAsViewUiBinder.class);

    interface GroupSaveAsViewUiBinder extends UiBinder<Widget, GroupSaveAsView> {
    }

    private static final String DEFAULT_HEIGHT = "390px";

    private static final String DEFAULT_WIDTH = "380px";

    private static final int GROUP_NAME_LIST_LIMIT = 250;

    private final EniqWindowGwt window;

    @UiField(provided = true)
    GroupManagementCellList<String> groupNameList;

    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;

    @UiField
    ExtendedTextBox saveAsGroupName;

    private final SingleSelectionModel<String> selectionModel;

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

    @SuppressWarnings("unused")
    @UiHandler("saveAsGroupName")
    public void onSaveAsValueChange(final ValueChangeEvent<String> event) {
        saveButton.setEnabled(!isGroupNameEntryBoxEmpty());
    }

    @SuppressWarnings("unused")
    @UiHandler("saveAsGroupName")
    public void onSaveAsKeyUp(final KeyUpEvent event) {
        saveButton.setEnabled(!isGroupNameEntryBoxEmpty());
    }

    @Inject
    public GroupSaveAsView() {
        window = new EniqWindowGwt();
        window.setGlassEnabled(true);
        window.setTitle("Save As");
        window.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        window.getElement().setId(SAVE_AS_WINDOW_ID);
        
        groupNameList = new GroupManagementCellList<String>(new TextCell());
        groupNameList.setVisibleRange(0, GROUP_NAME_LIST_LIMIT);
        groupNameList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

        // Add a selection model to handle user selection.
        selectionModel = new SingleSelectionModel<String>();
        groupNameList.setSelectionModel(selectionModel);
        initWidget(uiBinder.createAndBindUi(this));
        window.setContent(this.asWidget());
        bind(); // NOPMD
    }

    protected boolean isGroupNameEntryBoxEmpty() {
        return saveAsGroupName.containsDefaultText();
    }

    /**
     * @param dataProvider
     */
    public void setListDataProvider(final ListDataProvider<String> dataProvider) {
        dataProvider.addDataDisplay(groupNameList);
    }

    /**
     * 
     */
    private void bind() {
        selectionModel.addSelectionChangeHandler(new Handler() {

            @Override
            public void onSelectionChange(final SelectionChangeEvent event) {
                saveAsGroupName.setText(selectionModel.getSelectedObject());
                saveButton.setEnabled(!isGroupNameEntryBoxEmpty());
                saveAsGroupName.valueEntered();
            }
        });
        window.addWindowListener(new EniqWindowListener() {

            @Override
            public void windowClosed(final EniqWindowEvent eniqWindowEvent) {
                getPresenter().close();
            }
        });
    }

    public String getSaveGroupName() {
        return saveAsGroupName.getText();
    }

    public void launch() {
        RootPanel.get().add(window);
        window.center();
    }

    public void remove() {
        RootPanel.get().remove(window);
    }
}
