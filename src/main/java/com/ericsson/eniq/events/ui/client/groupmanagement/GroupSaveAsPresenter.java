/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.ui.client.groupmanagement.event.GroupSaveEvent;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.google.gwt.view.client.ListDataProvider;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import java.util.Collection;

import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.GROUP_NAME_ERROR_MESSAGE;
import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.GROUP_NAME_ERROR_TITLE;

/**
 * Presenter for the save as dialog.
 * @author ecarsea
 * @since 2011
 *
 */
public class GroupSaveAsPresenter extends BasePresenter<GroupSaveAsView> {

    ListDataProvider<String> groupNameListDataProvider = new ListDataProvider<String>();

    @Inject
    public GroupSaveAsPresenter(final EventBus eventBus, final GroupSaveAsView view) {
        super(view, eventBus);
        getView().setPresenter(this);
        getView().setListDataProvider(groupNameListDataProvider);
    }

    /**
     * Set the list of group names in the view
     * @param groupNames
     */
    public void setGroupNames(final Collection<String> groupNames) {
        groupNameListDataProvider.getList().addAll(groupNames);
    }

    /**
     * 
     */
    public void onSaveButtonClicked() {
        final String groupName = getView().getSaveGroupName();
        if (GroupManagementUtils.isGroupNameValid(groupName)) {
            //Warning dialog should display when user save group with any existing group name
            getEventBus().fireEvent(new GroupSaveEvent(groupName, true));
            getView().remove();
        } else {
            displayErrorDialog(GROUP_NAME_ERROR_TITLE, GROUP_NAME_ERROR_MESSAGE);
        }
    }

    private void displayErrorDialog(final String title, final String message) {//this method is duplicated in AbstractGroupEditPresenter
        final MessageDialog dialog = MessageDialog.get();
        dialog.show(title, message, MessageDialog.DialogType.ERROR);
    }

    public void onCancelButtonClicked() {
        getView().remove();

    }

    public void launch() {
        getView().launch();
    }

    public void close() {
        unbind();
    }
}
