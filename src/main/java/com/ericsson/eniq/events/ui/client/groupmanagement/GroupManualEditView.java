/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementView.GroupTypeData;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItemCell;
import com.ericsson.eniq.events.ui.client.groupmanagement.resources.GroupMgmtResourceBundle;
import com.ericsson.eniq.events.widgets.client.dropdown.DropDown;
import com.ericsson.eniq.events.widgets.client.textbox.ExtendedTextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.inject.Inject;

import static com.ericsson.eniq.events.common.client.CommonConstants.CELL_LIST_OFFSET_LIMIT;
import static com.google.gwt.event.dom.client.KeyCodes.*;

/** 
 * 
 * Manual Edit Component for TAC and IMSI
 * @author ecarsea
 * @since 2011
 *
 */
public class GroupManualEditView extends BaseView<GroupManualEditPresenter> implements IEditGroupView {

    private static final char COMMA_CHAR = ',';

    private static GroupManualEditViewUiBinder uiBinder = GWT.create(GroupManualEditViewUiBinder.class);

    interface GroupManualEditViewUiBinder extends UiBinder<Widget, GroupManualEditView> {
    }

    @UiField
    Button addButton;

    @UiField
    Button cancelButton;

    @UiField
    Button deleteButton;

    @UiField
    Button saveButton;

    @UiField
    Button saveAsButton;

    @UiField
    ExtendedTextBox groupName;

    @UiField
    ExtendedTextBox elementEntryBox;

    @UiField(provided = true)
    GroupManagementCellList<GroupListItem> elementList;

    @UiField(provided = true)
    GroupMgmtResourceBundle resourceBundle;

    @UiField
    DropDown<GroupTypeData> groupTypeDropDown;

    private final MultiSelectionModel<GroupListItem> selectionModel;

    @SuppressWarnings("unused")
    @UiHandler("addButton")
    public void addButtonClicked(final ClickEvent event) {
        if (!GroupManagementUtils.isTextBoxEmpty(elementEntryBox)) {
            getPresenter().onGroupElementsEntered(elementEntryBox.getText());
        }
    }

    @SuppressWarnings("unused")
    @UiHandler("deleteButton")
    public void deleteButtonClicked(final ClickEvent event) {
        getPresenter().deleteElements(selectionModel.getSelectedSet());
    }

    @SuppressWarnings("unused")
    @UiHandler("cancelButton")
    public void cancelButtonClicked(final ClickEvent event) {
        getPresenter().cancelGroupEdit();
    }

    @SuppressWarnings("unused")
    @UiHandler("saveButton")
    public void saveButtonClicked(final ClickEvent event) {
        getPresenter().onSaveButtonClicked();
    }

    @SuppressWarnings("unused")
    @UiHandler("saveAsButton")
    public void saveAsButtonClicked(final ClickEvent event) {
        getPresenter().onSaveAsButtonClicked();
    }

    @UiHandler("elementEntryBox")
    public void elementEntryBoxKeyDown(final KeyPressEvent event) {

        final int keyCode = event.getNativeEvent().getKeyCode();
        final char charCode = event.getCharCode();

        if (keyCode == (char) KEY_ENTER) {
            if (!GroupManagementUtils.isTextBoxEmpty(elementEntryBox)) {
                getPresenter().onGroupElementsEntered(elementEntryBox.getText());
            }
        }
        if ((!Character.isDigit(charCode)) && (charCode != COMMA_CHAR) && (keyCode != (char) KEY_BACKSPACE)
                && (keyCode != (char) KEY_DELETE) && (keyCode != (char) KEY_LEFT) && (keyCode != (char) KEY_RIGHT)) {
            ((ExtendedTextBox) event.getSource()).cancelKey();
        }
    }

    @UiHandler("elementEntryBox")
    public void elementEntryBoxValueChange(@SuppressWarnings("unused") final ValueChangeEvent<String> event) {
        addButton.setEnabled(!GroupManagementUtils.isTextBoxEmpty(elementEntryBox));
    }

    @UiHandler("elementEntryBox")
    public void onNewElementKeyUp(@SuppressWarnings("unused") final KeyUpEvent event) {
        addButton.setEnabled(!GroupManagementUtils.isTextBoxEmpty(elementEntryBox));
    }

    @Inject
    public GroupManualEditView(final GroupMgmtResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        elementList = new GroupManagementCellList<GroupListItem>(new GroupListItemCell());
        elementList.setVisibleRange(0, CELL_LIST_OFFSET_LIMIT);
        elementList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

        // Add a selection model to handle user selection.
        selectionModel = new MultiSelectionModel<GroupListItem>();
        elementList.setSelectionModel(selectionModel);
        initWidget(uiBinder.createAndBindUi(this));
        groupTypeDropDown.setEnabled(false);
        bind(); // NOPMD
    }

    private void bind() {
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange(final SelectionChangeEvent event) {
                setDeleteButtonEnabled(selectionModel.getSelectedSet() != null
                        && !selectionModel.getSelectedSet().isEmpty());
            }

        });
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.IEditGroupView#setDeleteButtonEnabled(boolean)
     */
    @Override
    public void setDeleteButtonEnabled(final boolean enabled) {
        deleteButton.setEnabled(enabled);
    }

    /**
     * @param enabled
     */
    @Override
    public void setSaveButtonEnabled(final boolean enabled) {
        saveButton.setEnabled(enabled);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.IEditGroupView#setSaveAsButtonEnabled(boolean)
     */
    @Override
    public void setSaveAsButtonEnabled(final boolean enabled) {
        saveAsButton.setEnabled(enabled);
    }

    /**
     * @param dataProvider
     */
    public void setData(final String groupDisplayName, final ListDataProvider<GroupListItem> dataProvider) {
        dataProvider.addDataDisplay(elementList);
        groupTypeDropDown.setText(groupDisplayName);
    }

    public void clearGroupEntryBox() {
        elementEntryBox.setText("");
        addButton.setEnabled(false);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.IEditGroupView#close()
     */
    @Override
    public void close() {
        // nothing to clean up
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.IEditGroupView#configure(java.lang.String, boolean)
     */
    @Override
    public void configure(final String groupNameStr, final boolean isNewGroup) {
        saveAsButton.setVisible(!isNewGroup);
        if (!groupNameStr.isEmpty()) {
            this.groupName.setText(groupNameStr);
            //group name should be disable when use press edit group button
            this.groupName.setEnabled(false);
        }
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.IEditGroupView#getGroupName()
     */
    @Override
    public String getGroupName() {
        return isGroupNameEntryBoxEmpty() ? "" : groupName.getText();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.IEditGroupView#isGroupNameEntryBoxEmpty()
     */
    @Override
    public boolean isGroupNameEntryBoxEmpty() {
        return groupName.containsDefaultText();
    }
}
