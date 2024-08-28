/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.datatype.group.GroupMgmtConfigDataType;
import com.ericsson.eniq.events.ui.client.groupmanagement.event.GroupEditCancelEvent;
import com.ericsson.eniq.events.ui.client.groupmanagement.event.GroupSaveAsEvent;
import com.ericsson.eniq.events.ui.client.groupmanagement.event.GroupSaveEvent;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.multiselect.AbstractMultiSelectPresenter;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.GROUP_NAME_ERROR_MESSAGE;
import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.GROUP_NAME_ERROR_TITLE;

/**
 * Group Editing is done by two widgets, one with manual entry for TAC and IMSI, and one with
 * entry via live load of group nodes. Common presenter functionality for the Group Edit widgets
 * is contained in this abstract class
 *
 * @author ecarsea
 * @since 2011
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractGroupEditPresenter<T extends BaseView> extends BasePresenter<T> {

    protected String groupTypeName;

    protected String groupTypeId;

    private GroupOperationServerComms groupOperationServerComms;

    private GroupMgmtConfigDataType groupMgmtConfigDataType;

    /** The group elements as they currently exist in the DB * */
    private Collection<GroupListItem> currentGroupSavedContents;

    private String currentGroupName;

    private boolean isNewGroup;

    /**
     * @param eventBus
     * @param view
     */
    @SuppressWarnings("unchecked")
    public AbstractGroupEditPresenter(final EventBus eventBus, final T view) {
        super(view, eventBus);
        view.setPresenter(this);
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.BasePresenter#onUnbind()
    */
    @Override
    protected void onUnbind() {
        super.onUnbind();
        getEditGroupView().close();
    }

    /**
     * @param groupTypeId
     * @param groupMgmtConfigDataType
     * @param groupName
     * @param groupContents
     * @param isNewGroup
     * @param wizardPresenter
     */
    public void init(final String groupTypeName, final String groupTypeId,
            final GroupMgmtConfigDataType groupMgmtConfigDataType, final String groupName,
            final Collection<GroupListItem> groupContents, final boolean isNewGroup,
            final AbstractMultiSelectPresenter wizardPresenter) {
        this.groupTypeName = groupTypeName;
        this.groupTypeId = groupTypeId;
        this.groupMgmtConfigDataType = groupMgmtConfigDataType;
        this.currentGroupSavedContents = groupContents;
        this.currentGroupName = groupName;
        this.isNewGroup = isNewGroup;
        getEditGroupView().configure(groupName, isNewGroup);
        groupOperationServerComms = new GroupOperationServerComms(getEventBus(),
                groupMgmtConfigDataType.getGroupConfigurationUrl());
        addGroupContents(groupContents);
    }

    /**
     * If in edit mode add the current saved group contents to the view
     *
     * @param groupContents
     */
    protected abstract void addGroupContents(Collection<GroupListItem> groupContents);

    /**
     * Get the view associated with the derived presenter
     *
     * @return
     */
    protected abstract IEditGroupView getEditGroupView();

    /** When cancel button is clicked in the edit view */
    public void cancelGroupEdit() {
        getEventBus().fireEvent(new GroupEditCancelEvent());
    }

    /**
     *
     */
    public void saveGroup() {
        saveGroup(getEditGroupView().getGroupName());
    }

    /**
     * @param groupName
     */
    public void saveGroup(final String groupName) {
        final Collection<GroupListItem> groupElements = getGroupElements();

        final Collection<GroupListItem> itemsToDelete = getItemsToDelete(groupElements, groupName);

        final Collection<GroupListItem> itemsToSave = getItemsToSave(groupElements, groupName);

        if (editGroupRequired(itemsToDelete, itemsToSave, groupName)) {
            groupOperationServerComms.editGroup(groupMgmtConfigDataType.getGroupManagementItem(groupTypeId),
                    new GroupData(groupName, itemsToDelete), new GroupData(groupName, itemsToSave));
            return;
        }

        /** Delete Only  **/
        if (!itemsToDelete.isEmpty()) {
            final List<GroupData> groupDataList = new ArrayList<GroupData>() {
                {
                    add(new GroupData(groupName, itemsToDelete));
                }
            };
            groupOperationServerComms.deleteGroups(groupMgmtConfigDataType.getGroupManagementItem(groupTypeId),
                    groupDataList, false);

        }

        /** Save Only **/
        if (!itemsToSave.isEmpty()) {
            final List<GroupData> groupDataList = new ArrayList<GroupData>() {
                {
                    add(new GroupData(groupName, itemsToSave));
                }
            };
            groupOperationServerComms.saveGroup(groupMgmtConfigDataType.getGroupManagementItem(groupTypeId),
                    groupDataList);
        }
    }

    /**
     * Check if we need to edit the group i.e. delete items, then add items. Only need to do this if we have both
     * items to delete and add, and also if we are saving to the same group i.e. Not Save As.
     *
     * @param itemsToDelete
     * @param itemsToSave
     * @param groupName
     *
     * @return
     */
    protected boolean editGroupRequired(final Collection<GroupListItem> itemsToDelete,
            final Collection<GroupListItem> itemsToSave, final String groupName) {
        return !itemsToDelete.isEmpty() && !itemsToSave.isEmpty() && !isSaveAsMode(groupName);
    }

    /**
     * Return collection of group items to be deleted remotely
     *
     * @param groupElements
     * @param groupName
     *
     * @return
     */
    private Collection<GroupListItem> getItemsToDelete(final Collection<GroupListItem> groupElements,
            final String groupName) {
        /** If Saving as different Group no need to do anything, if saving current group, do remote operation **/
        if (isSaveAsMode(groupName)) {
            return Collections.<GroupListItem> emptyList();
        }
        final List<GroupListItem> tempList = new ArrayList<GroupListItem>(currentGroupSavedContents);
        tempList.removeAll(groupElements);
        return tempList;
    }

    /**
     * Are we in Save As Mode or just saving the current group
     *
     * @param groupName
     *
     * @return
     */
    protected boolean isSaveAsMode(final String groupName) {
        return !groupName.equalsIgnoreCase(this.currentGroupName);
    }

    /**
     * Return collection of items to be saved remotely
     *
     * @param groupElements
     * @param groupName
     *
     * @return
     */
    private Collection<GroupListItem> getItemsToSave(final Collection<GroupListItem> groupElements,
            final String groupName) {
        /** If Saving as different Group, need to save all group Elements **/
        if (isSaveAsMode(groupName)) {
            return groupElements;
        }
        final List<GroupListItem> tempList = new ArrayList<GroupListItem>(groupElements);
        tempList.removeAll(currentGroupSavedContents);
        return tempList;
    }

    /**
     * Retrieve group elements from the view
     *
     * @return
     */
    protected abstract Collection<GroupListItem> getGroupElements();

    /** Handle clicking of the save button */
    public void onSaveButtonClicked() {
        /** If No group name entered yet, show save as dialog **/
        if (getEditGroupView().isGroupNameEntryBoxEmpty()) {
            getEventBus().fireEvent(new GroupSaveAsEvent());
        } else if (GroupManagementUtils.isGroupNameValid(getEditGroupView().getGroupName())) {
            //there is no need for prompt when user click on save
            getEventBus().fireEvent(new GroupSaveEvent(getEditGroupView().getGroupName(), isNewGroup));
        } else {
            displayErrorDialog(GROUP_NAME_ERROR_TITLE, GROUP_NAME_ERROR_MESSAGE);
        }
    }

    /** Handle clicking of Save As Button */
    public void onSaveAsButtonClicked() {
        getEventBus().fireEvent(new GroupSaveAsEvent());
    }

    /**
     * Delete Items from current group. Just remove from view and await save request
     *
     * @param selectedItems
     */
    protected void deleteItems(final Collection<GroupListItem> selectedItems) {
        removeItems(selectedItems);
        setSaveButtonsEnabled();
        getEditGroupView().setDeleteButtonEnabled(false);
    }

    /** Enabled save buttons if new elements have been added to the view */
    protected void setSaveButtonsEnabled() {
        final Collection<GroupListItem> groupViewContents = getGroupElements();
        final boolean saveAsActionApplicable = groupViewContents.size() > 0;
        //Save As button disable only when no group elements is there to save in group, otherwise it will always be enable in edit view
        getEditGroupView().setSaveAsButtonEnabled(saveAsActionApplicable);

        // Save button is disabled only when both selected group elements and saved group elements are identical
        final List<GroupListItem> savedCompareList = new ArrayList<GroupListItem>(currentGroupSavedContents);
        savedCompareList.removeAll(groupViewContents);
        final List<GroupListItem> selectedCompareList = new ArrayList<GroupListItem>(groupViewContents);
        selectedCompareList.removeAll(currentGroupSavedContents);

        getEditGroupView().setSaveButtonEnabled(
                ((savedCompareList.size() > 0 || selectedCompareList.size() > 0) && saveAsActionApplicable)
                        || isSaveAsMode(getEditGroupView().getGroupName()));
    }

    /**
     * Remove items from the view
     *
     * @param selectedItems
     */
    protected abstract void removeItems(Collection<GroupListItem> selectedItems);

    protected void displayErrorDialog(final String title, final String message) {
        final MessageDialog dialog = new MessageDialog();
        dialog.show(title, message, MessageDialog.DialogType.ERROR);
    }
}
