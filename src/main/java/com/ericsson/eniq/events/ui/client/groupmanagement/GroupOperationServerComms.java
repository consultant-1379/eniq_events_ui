/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

import com.ericsson.eniq.events.common.client.service.RestfulRequestBuilder.State;
import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.ericsson.eniq.events.ui.client.common.ServerComms;
import com.ericsson.eniq.events.ui.client.datatype.group.GroupManagementItemDataType;
import com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.*;
import com.ericsson.eniq.events.ui.client.groupmanagement.event.GroupOperationCompletedEvent;
import com.ericsson.eniq.events.ui.client.groupmanagement.event.GroupWindowMaskEvent;
import com.ericsson.eniq.events.ui.client.groupmanagement.event.GroupWindowUnMaskEvent;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog.DialogType;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to perform the group configuration service requests i.e. ADD/DELETE of groups and group elements
 * @author ecarsea
 * @since 2011
 */
public class GroupOperationServerComms {

    private final EventBus eventBus;

    private final ServerComms serverComms;

    private final GroupOperationRequestCallback requestCallback = new GroupOperationRequestCallback();

    private final String groupConfigUrlSuffix;

    private final MessageDialog operationResultDialog;

    /** Will only ever be performing one operation at a time even though the actions are asynchronous **/
    private GroupOperation currentOperation;

    private EditOperationCallback editOperationCallback;

    private boolean multiGroupDelete;

    public GroupOperationServerComms(final EventBus eventBus, final String groupConfigUrlSuffix) {
        this.eventBus = eventBus;
        this.groupConfigUrlSuffix = groupConfigUrlSuffix;
        serverComms = new ServerComms(eventBus);
        operationResultDialog = new MessageDialog();
    }

    private class GroupOperationRequestCallback implements RequestCallback {

        /* (non-Javadoc)
         * @see com.google.gwt.http.client.RequestCallback#onResponseReceived(com.google.gwt.http.client.Request, com.google.gwt.http.client.Response)
         */
        @Override
        public void onResponseReceived(final Request request, final Response response) {
            final JSONValue jsonData = GroupManagementUtils.checkAndParse(response);

            // exception message written into response
            if (jsonData != null && currentOperation.equals(GroupOperation.SAVE) ? JSONUtils.checkData(jsonData, true)
                    : JSONUtils.checkData(jsonData)) {
                /** Two remote operations i.e. delete and save, use this callback to seamlessly perform the two ops as one **/
                if (editOperationCallback != null) {
                    editOperationCallback.performNextOperation();
                    editOperationCallback = null;
                    return;
                }
                eventBus.fireEvent(new GroupWindowUnMaskEvent());
                displayConfirmationDialog();
                eventBus.fireEvent(new GroupOperationCompletedEvent(true, currentOperation));
            } else {
                handleFail(jsonData == null ? EMPTY_RESPONSE_FROM_SERVER : "");
            }
        }

        protected void displayConfirmationDialog() {
            if (currentOperation.equals(GroupOperation.DELETE_GROUP)) {
                if (multiGroupDelete) {
                    operationResultDialog.show(GroupManagementConstants.GROUPS_DELETE_SUCCESS_DIALOG_TITLE,
                            GroupManagementConstants.GROUPS_DELETE_SUCCESS_DIALOG_MESSAGE, DialogType.INFO);
                } else {
                    operationResultDialog.show(GroupManagementConstants.GROUP_DELETE_SUCCESS_DIALOG_TITLE, GroupManagementConstants.GROUP_DELETE_SUCCESS_DIALOG_MESSAGE,
                            DialogType.INFO);
                }
            } else if (currentOperation.equals(GroupOperation.SAVE)
                    || currentOperation.equals(GroupOperation.DELETE_GROUP_ELEMENTS)) {
                operationResultDialog.show(GroupManagementConstants.GROUP_SAVE_SUCCESS_DIALOG_TITLE, GroupManagementConstants.GROUP_SAVE_SUCCESS_DIALOG_MESSAGE,
                        DialogType.INFO);
            }
        }

        /* (non-Javadoc)
         * @see com.google.gwt.http.client.RequestCallback#onError(com.google.gwt.http.client.Request, java.lang.Throwable)
         */
        @Override
        public void onError(final Request request, final Throwable exception) {
            handleFail(exception.getMessage());
        }

        protected void handleFail(final String error) {
            if (editOperationCallback != null) {
                editOperationCallback = null;
            }
            eventBus.fireEvent(new GroupWindowUnMaskEvent());
            if (!error.isEmpty()) {
                displayFailureDialog(error);
            }
            eventBus.fireEvent(new GroupOperationCompletedEvent(false, currentOperation));
        }

    }

    /**
     * @param message
     */
    protected void displayFailureDialog(final String message) {
        if (currentOperation.equals(GroupOperation.DELETE_GROUP)) {
            operationResultDialog.show(GroupManagementConstants.DELETE_GROUP_FAILURE_DIALOG_TITLE, message, DialogType.ERROR);
        } else if (currentOperation.equals(GroupOperation.SAVE)) {
            operationResultDialog.show(GroupManagementConstants.SAVE_GROUP_FAILURE_DIALOG_TITLE, message, DialogType.ERROR);
        } else if (currentOperation.equals(GroupOperation.DELETE_GROUP_ELEMENTS)) {
            operationResultDialog.show(GroupManagementConstants.SAVE_GROUP_ELEMENT_FAILURE_DIALOG_TITLE, message, DialogType.ERROR);
        }
    }

    /**
     * Save an edited group
     * @param groupItemConfig
     * @param groupDataList
     */
    public void saveGroup(final GroupManagementItemDataType groupItemConfig, final List<GroupData> groupDataList) {
        currentOperation = GroupOperation.SAVE;
        final String json = GroupManagementUtils.generateGroupJsonData(groupItemConfig, groupDataList);
        eventBus.fireEvent(new GroupWindowMaskEvent(GroupManagementConstants.SAVING_GROUP_MASK_MESSAGE));
        serverComms.requestData(State.POST, GroupManagementUtils.getGroupConfigurationUrl(this.groupConfigUrlSuffix,
                GroupManagementConstants.GroupAction.ADD), json, requestCallback);

    }

    /**
     * Delete a group
     * @param groupItemConfig
     * @param groupDataList
     * @param deleteFullGroup - delete full group
     */
    public void deleteGroups(final GroupManagementItemDataType groupItemConfig, final List<GroupData> groupDataList,
            final boolean deleteFullGroup) {
        currentOperation = deleteFullGroup ? GroupOperation.DELETE_GROUP : GroupOperation.DELETE_GROUP_ELEMENTS;
        multiGroupDelete = groupDataList.size() > 1;
        final String json = GroupManagementUtils.generateGroupJsonData(groupItemConfig, groupDataList);
        eventBus.fireEvent(new GroupWindowMaskEvent(deleteFullGroup ? (multiGroupDelete ? GroupManagementConstants.DELETING_GROUPS_MASK_MESSAGE
                : GroupManagementConstants.DELETING_GROUP_MASK_MESSAGE) : GroupManagementConstants.DELETING_GROUP_ELEMENTS_MASK_MESSAGE));
        serverComms.requestData(State.POST, GroupManagementUtils.getGroupConfigurationUrl(this.groupConfigUrlSuffix,
                GroupManagementConstants.GroupAction.DELETE), json, requestCallback);

    }

    /**
     * @param groupManagementItem
     * @param itemsToDelete
     * @param itemsToSave
     */
    public void editGroup(final GroupManagementItemDataType groupManagementItem, final GroupData itemsToDelete,
            final GroupData itemsToSave) {
        currentOperation = GroupOperation.SAVE;
        editOperationCallback = new EditOperationCallback() {

            @Override
            public void performNextOperation() {
                deleteGroups(groupManagementItem, new ArrayList<GroupData>() {
                    {
                        add(itemsToDelete);
                    }
                }, false);
            }
        };
        saveGroup(groupManagementItem, new ArrayList<GroupData>() {
            {
                add(itemsToSave);
            }
        });

    }

    /**
     * Edit Operation requires both a remote save and a delete. This interface is used to perform the second remote operation if 
     * the first operation has been successful;
     */
    private interface EditOperationCallback {
        void performNextOperation();
    }
}
