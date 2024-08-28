/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.common.client.service.RestfulRequestBuilder.State;
import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.ericsson.eniq.events.ui.client.common.MetaReader;
import com.ericsson.eniq.events.ui.client.common.ServerComms;
import com.ericsson.eniq.events.ui.client.datatype.group.GroupManagementItemDataType;
import com.ericsson.eniq.events.ui.client.datatype.group.GroupMgmtConfigDataType;
import com.ericsson.eniq.events.ui.client.groupmanagement.event.*;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.multiselect.AbstractMultiSelectPresenter;
import com.ericsson.eniq.events.ui.client.groupmanagement.multiselect.MultiSelectGroupRegistry;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog.DialogType;
import com.ericsson.eniq.events.widgets.client.dialog.PromptDialog;
import com.ericsson.eniq.events.widgets.client.dialog.events.PromptCancelEvent;
import com.ericsson.eniq.events.widgets.client.dialog.events.PromptCancelEventHandler;
import com.ericsson.eniq.events.widgets.client.dialog.events.PromptOkEvent;
import com.ericsson.eniq.events.widgets.client.dialog.events.PromptOkEventHandler;
import com.ericsson.eniq.events.widgets.client.mask.MaskHelper;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

import java.util.*;

import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.*;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;
/**
 * Overall Presenter for the Group Management Component.
 *
 * @author ecarsea
 * @since 2011
 */
public class GroupManagementPresenter extends BasePresenter<GroupManagementView> {

    private final GroupMgmtConfigDataType groupMgmtConfigDataType;

    private final ServerComms serverComms;

    private String selectedGroupTypeId;

    private String selectedGroupTypeName;

    private final GroupModel groupModel = new GroupModel();

    private final Provider<GroupManualEditPresenter> groupEditPresenterProvider;

    @SuppressWarnings("rawtypes")
    private AbstractGroupEditPresenter<? extends BaseView> groupEditPresenter;

    protected List<String> currentGroupNamesSelected = new ArrayList<String>();

    private final Provider<GroupLiveLoadPresenter> groupLiveloadPresenterProvider;

    private final GroupOperationServerComms groupOperationServerComms;

    private PromptDialog saveOverwritePrompt;

    private final Provider<GroupSaveAsPresenter> groupSaveAsPresenterProvider;

    private final MaskHelper maskHelper;

    private String saveGroupName;

    private final GroupManagementRequestCallback requestCallback = new GroupManagementRequestCallback();

    private final MultiSelectGroupRegistry multiSelectGroupRegistry;

    /**
     * @param eventBus
     * @param view
     */
    @Inject
    public GroupManagementPresenter(final EventBus eventBus, final GroupManagementView view,
            final MetaReader metaReader, final Provider<GroupManualEditPresenter> groupEditPresenterProvider,
            final Provider<GroupLiveLoadPresenter> groupLiveloadPresenterProvider,
            final Provider<GroupSaveAsPresenter> groupSaveAsPresenterProvider,
            final MultiSelectGroupRegistry multiSelectGroupRegistry) {
        super(view, eventBus);
        this.groupEditPresenterProvider = groupEditPresenterProvider;
        this.groupLiveloadPresenterProvider = groupLiveloadPresenterProvider;
        this.groupSaveAsPresenterProvider = groupSaveAsPresenterProvider;
        this.multiSelectGroupRegistry = multiSelectGroupRegistry;
        groupMgmtConfigDataType = metaReader.getGroupManagementConfigData();
        groupOperationServerComms = new GroupOperationServerComms(eventBus,
                groupMgmtConfigDataType.getGroupConfigurationUrl());
        view.setPresenter(this);

        serverComms = new ServerComms(eventBus);
        maskHelper = new MaskHelper();
        bind();
    }

    @Override
    public void onBind() {
        saveOverwritePrompt = new PromptDialog();
        saveOverwritePrompt.setGlassEnabled(true);

        saveOverwritePrompt.addOkEventHandler(new PromptOkEventHandler() {

            @Override
            public void onPromptOk(final PromptOkEvent e) {
                groupEditPresenter.saveGroup(saveGroupName);

            }
        });
        saveOverwritePrompt.addCancelEventHandler(new PromptCancelEventHandler() {

            @Override
            public void onPromptCancel(final PromptCancelEvent e) {
                launchSaveAsDialog();

            }
        });
        registerHandler(getEventBus().addHandler(GroupEditCancelEvent.TYPE, new GroupEditCancelEventHandler() {

            @Override
            public void onGroupEditCancelEvent() {
                returnToMainView();
            }
        }));

        registerHandler(getEventBus().addHandler(GroupSaveEvent.TYPE, new GroupSaveEventHandler() {

            @Override
            public void onGroupSave(final GroupSaveEvent event) {
                handleGroupSaveEvent(event);
            }
        }));

        registerHandler(getEventBus().addHandler(GroupOperationCompletedEvent.TYPE,
                new GroupOperationCompletedEventHandler() {

                    @Override
                    public void onGroupOperationCompleted(final GroupOperationCompletedEvent event) {
                        handleOperationCompleteEvent(event);
                    }
                }));

        registerHandler(getEventBus().addHandler(GroupSaveAsEvent.TYPE, new GroupSaveAsEventHandler() {

            @Override
            public void onGroupSaveAsEvent() {
                launchSaveAsDialog();
            }
        }));
        registerHandler(getEventBus().addHandler(GroupWindowMaskEvent.TYPE, new GroupWindowMaskEventHandler() {

            @Override
            public void onGroupWindowMaskEvent(final GroupWindowMaskEvent groupWindowMaskEvent) {
                maskHelper.mask(getView().getElement(), groupWindowMaskEvent.getMaskMessage(), getView()
                        .getOffsetHeight());
            }
        }));
        registerHandler(getEventBus().addHandler(GroupWindowUnMaskEvent.TYPE, new GroupWindowUnMaskEventHandler() {

            @Override
            public void onGroupWindowUnMaskEvent() {
                maskHelper.unmask();

            }
        }));

        registerHandler(getEventBus().addHandler(GroupElementsLoadEvent.TYPE, new GroupElementsLoadEventHandler() {

            @Override
            public void onGroupElementsLoaded(final GroupElementsLoadEvent event) {
                if (event.isDeletingGroup()) {
                    /** Create the JSON string for the group(s) **/
                    final List<GroupData> groupDataList = new ArrayList<GroupData>();
                    for (final String groupName : event.getGroupElementMap().keySet()) {
                        final List<GroupListItem> groupElements = event.getGroupElementMap().get(groupName);
                        groupDataList.add(new GroupData(groupName, groupElements));
                    }

                    groupOperationServerComms.deleteGroups(
                            groupMgmtConfigDataType.getGroupManagementItem(selectedGroupTypeId), groupDataList, true);
                } else {
                    final String groupName = currentGroupNamesSelected.get(0);
                    loadGroupEditView(groupName, event.getGroupElementMap().get(groupName), false);
                }
            }
        }));
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.common.BasePresenter#onUnbind()
    */
    @Override
    protected void onUnbind() {
        super.onUnbind();
    }

    /** Launch the Group Management Component Inital View * */
    public void launch() {
        getView().launch(groupMgmtConfigDataType);
    }

    /**
     * Group Type selected from Drop Down Box
     *
     * @param groupTypeId
     */
    public void onGroupTypeSelected(final String groupTypeName, final String groupTypeId) {
        this.selectedGroupTypeId = groupTypeId;
        this.selectedGroupTypeName = groupTypeName;
        getView().setEditEnabled(false);
        getView().setDeleteEnabled(false);
        /** If we have already loaded this group from the server, then retrieve it from our cache.
         * Saves loading the group name list every time the user selects a different group type from drop down **/
        if (groupModel.hasGroupNameList(groupTypeId)) {
            showGroups();
        } else {
            loadGroupNames();
        }

    }

    /** Load the list of group names for the selected group type from the server. This also returns the elements contained in each group */
    protected void loadGroupNames() {
        final GroupManagementItemDataType item = groupMgmtConfigDataType.getGroupManagementItem(selectedGroupTypeId);
        maskHelper.mask(getView().getElement(), LOADING_GROUPS, getView().getOffsetHeight());
        serverComms.requestData(State.GET, GroupManagementUtils.getRequestUrl(item.getLoadGroupUrl()), "",
                requestCallback);
    }

    private class GroupManagementRequestCallback implements RequestCallback {
        private static final String GROUP_NAME_LIST_FAILURE = "Group Name List Failure";

        /* (non-Javadoc)
         * @see com.google.gwt.http.client.RequestCallback#onResponseReceived(com.google.gwt.http.client.Request, com.google.gwt.http.client.Response)
         */
        @Override
        public void onResponseReceived(final Request request, final Response response) {
            maskHelper.unmask();
            final JSONValue jsonData = GroupManagementUtils.checkAndParse(response);

            // exception message written into response
            if (jsonData != null && JSONUtils.checkData(jsonData)) {
                /** Cache the group Names + elements **/
                groupModel.addGroups(selectedGroupTypeId, jsonData);
                showGroups();
            } else {
                handleFailure(jsonData == null ? SERVER_CORRUPT_RESPONSE : "");
            }
        }

        /* (non-Javadoc)
         * @see com.google.gwt.http.client.RequestCallback#onError(com.google.gwt.http.client.Request, java.lang.Throwable)
         */
        @Override
        public void onError(final Request request, final Throwable exception) {
            handleFailure(exception.getMessage());

        }

        protected void handleFailure(final String error) {
            maskHelper.unmask();
            if (!error.isEmpty()) {
                final MessageDialog messageDialog = new MessageDialog();
                messageDialog.show(GROUP_NAME_LIST_FAILURE, error, DialogType.ERROR);
            }
        }

    }

    /** Show the Group Names in the view */
    public void showGroups() {
        getView().setGroups(groupModel.getGroupNameList(selectedGroupTypeId));
    }

    /**
     * When user clicks the "New" Button, load the edit view for that group type. Pass an empty string to the view for the group name as it will not
     * be created yet and pass an empty list to the loadGroupEditView method as this is a new group and will not
     * contain any elements at this point.
     */
    public void onNewGroupSelected() {
        loadGroupEditView("", Collections.<GroupListItem> emptyList(), true);
    }

    /** When user clicks the edit button, get the group name and the group elements and load the edit view for this group */
    public void onGroupEditButtonClicked() {
        /** Edit button only enabled on selection of single group **/
        getGroupElements(currentGroupNamesSelected.get(0));
    }

    /**
     * Double clicking on a group is the same as clicking the edit button for that group
     *
     * @param groupNameListItem
     */
    public void onGroupDoubleClick(final GroupListItem groupNameListItem) {
        getGroupElements(groupNameListItem.getStringValue());
    }

    protected void getGroupElements(final String groupName) {
        final List<String> groupKeyNameList = groupMgmtConfigDataType.getGroupManagementItem(selectedGroupTypeId)
                .getGroupElementKeyNameList();
        final List<String> groupNameList = new ArrayList<String>() {
            {
                add(groupName);
            }
        };
        multiSelectGroupRegistry.getGroupElementRetriever(selectedGroupTypeId, groupMgmtConfigDataType)
                .getGroupElements(selectedGroupTypeId, groupNameList, groupKeyNameList, false);
    }

    /**
     * Load the group Edit View
     *
     * @param groupName     - Name of Group - empty if new group.
     * @param groupContents - Current Stored elements of the group, empty if new group
     * @param isNewGroup    - flag to indicate if this is a new group or editing a current group
     */
    private void loadGroupEditView(final String groupName, final Collection<GroupListItem> groupContents,
            final boolean isNewGroup) {
        final GroupManagementItemDataType item = groupMgmtConfigDataType.getGroupManagementItem(selectedGroupTypeId);
        /** Check if we need to load the LiveLoad Edit View (APNs, SGSNS etc) or the Manual Edit View(IMSI, TAC) **/
        boolean liveload = true;
        final boolean isMultiSelectEditor = item.getWizard() != null;
        if ((item.getLiveloadUrl() == null || item.getLiveloadUrl().isEmpty()) && !isMultiSelectEditor) {
            groupEditPresenter = this.groupEditPresenterProvider.get();
            liveload = false;
        } else {
            groupEditPresenter = this.groupLiveloadPresenterProvider.get();
        }
        AbstractMultiSelectPresenter multiSelectPresenter = null;
        if (isMultiSelectEditor) {
            multiSelectPresenter = this.multiSelectGroupRegistry.getMultiSelectPresenter(selectedGroupTypeId);
        }
        getView().addGroupEditWidget(groupEditPresenter.getView(), liveload, isMultiSelectEditor);
        groupEditPresenter.init(selectedGroupTypeName, selectedGroupTypeId, groupMgmtConfigDataType, groupName,
                groupContents, isNewGroup, multiSelectPresenter);
        /** Disable the main view so as to prevent user from selecting a new group type of clicking on another group element **/
        getView().disable();
    }

    /** Delete button clicked on selected group name(s) */
    public void onGroupDeleteButtonClicked() {
        /** Prompt to confirm deletion **/
        final PromptDialog confirmDialog = new PromptDialog();
        confirmDialog.setGlassEnabled(true);
        confirmDialog.addOkEventHandler(new PromptOkEventHandler() {

            @Override
            public void onPromptOk(final PromptOkEvent event) {
                /** Retrieve all group elements **/
                final List<String> groupKeyNameList = groupMgmtConfigDataType.getGroupManagementItem(
                        selectedGroupTypeId).getGroupElementKeyNameList();
                multiSelectGroupRegistry.getGroupElementRetriever(selectedGroupTypeId, groupMgmtConfigDataType)
                        .getGroupElements(selectedGroupTypeId, currentGroupNamesSelected, groupKeyNameList, true);
            }
        });

        final String suffix = currentGroupNamesSelected.size() > 1 ? GROUPS_PLURAL : GROUP_SINGULAR;
        confirmDialog.show(DELETE_QUERY + suffix, ACTION_WARNING, DialogType.WARNING);
    }

    /** @param selectedItems - the selected items, cant be null */
    public void onItemsSelected(final List<GroupListItem> selectedItems) {
        currentGroupNamesSelected.clear();
        for (final GroupListItem ge : selectedItems) {
            currentGroupNamesSelected.add(ge.getStringValue());
        }
        getView().setEditEnabled(currentGroupNamesSelected.size() == 1);
        getView().setDeleteEnabled(!currentGroupNamesSelected.isEmpty());

    }

    /** Restore the Main Group Management Component View i.e. remove the edit view. */
    protected void returnToMainView() {
        if (groupEditPresenter != null) {
            getView().removeGroupEditWidget(groupEditPresenter.getView().asWidget());
            groupEditPresenter.unbind();
        }
        getView().enable();
        getView().setEditEnabled(currentGroupNamesSelected == null ? false : currentGroupNamesSelected.size() == 1);
        getView().setDeleteEnabled(currentGroupNamesSelected == null ? false : !currentGroupNamesSelected.isEmpty());
    }

    /** Launch Save As Dialog */
    protected void launchSaveAsDialog() {
        final GroupSaveAsPresenter groupSaveAsPresenter = groupSaveAsPresenterProvider.get();
        groupSaveAsPresenter.setGroupNames(groupModel.getGroupNameList(selectedGroupTypeId));
        groupSaveAsPresenter.launch();
    }

    /**
     * Handle a Group Save Event received from the Group Edit View
     *
     * @param event
     */
    protected void handleGroupSaveEvent(final GroupSaveEvent event) {
        /** Check if this will overwrite a current group and prompt to confirm **/
        this.saveGroupName = event.getGroupName();
        if (event.isPromptAllowed() && isGroupNameExists()) {
            saveOverwritePrompt.show(GROUP_ALREADY_EXISTS, ACTION_WARNING, DialogType.WARNING);
        } else {
            groupEditPresenter.saveGroup(saveGroupName);
        }
    }

    private boolean isGroupNameExists() {
        final Iterator<String> namesIterator = groupModel.getGroupNameList(selectedGroupTypeId).iterator();
        while (namesIterator.hasNext()) {
            final String name = namesIterator.next();
            if (name.equalsIgnoreCase(saveGroupName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Group Save or Delete Operation has been completed to the server
     *
     * @param event
     */
    @SuppressWarnings("unused")
    protected void handleOperationCompleteEvent(final GroupOperationCompletedEvent event) {
        returnToMainView();
        loadGroupNames();
    }

    /** Window Closed Clean up */
    public void close() {
        unbind();
        if (groupEditPresenter != null) {
            groupEditPresenter.unbind();
        }
    }
}
