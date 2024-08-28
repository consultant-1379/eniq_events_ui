/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ericsson.eniq.events.ui.client.datatype.group.GroupManagementItemDataType;
import com.ericsson.eniq.events.ui.client.datatype.group.GroupMgmtConfigDataType;
import com.ericsson.eniq.events.ui.client.datatype.group.WizardDataType;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.multiselect.GroupItemsAggregator;
import com.ericsson.eniq.events.ui.client.groupmanagement.multiselect.IMultiSelectHandler;
import com.ericsson.eniq.events.ui.client.groupmanagement.multiselect.AbstractMultiSelectPresenter;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for the Edit Widget of the Liveload Group components i.e. APN, BSC etc
 * @author ecarsea
 * @since 2011
 *
 */
public class GroupLiveLoadPresenter extends AbstractGroupEditPresenter<GroupLiveloadView> implements IMultiSelectHandler {

    private List<GroupListItem> liveLoadSelectedItems;

    private List<GroupListItem> currentGroupSelectedItems;

    private GroupManagementItemDataType groupManagementItem;

    private GroupListItem selectedItem;

    private GroupItemsAggregator groupItemsAggregator;

    /**
     * @param view
     * @param eventBus
     */
    @Inject
    public GroupLiveLoadPresenter(final EventBus eventBus, final GroupLiveloadView view) {
        super(eventBus, view);
        getView().setPresenter(this);
        bind();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.common.BasePresenter#onBind()
     */
    @Override
    protected void onBind() {
        super.onBind();
        /** Handle Items in the Current/New Group being selected **/
        getView().setCurrentGroupSelectionHandler(new ISelectionHandler() {

            @Override
            public void onItemsSelected(final List<GroupListItem> selectedItems) {
                currentGroupSelectedItems = selectedItems;
                /** Enabled delete button if any item(s) selected **/
                getView().setDeleteEnabled(currentGroupSelectedItems.size() > 0);

            }
        });
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.AbstractGroupEditPresenter#init(java.lang.String, com.ericsson.eniq.events.ui.client.datatype.group.GroupMgmtConfigDataType, java.lang.String, java.util.Collection, boolean)
     */
    @Override
    public void init(final String groupTypeName, final String groupTypeId, final GroupMgmtConfigDataType
            groupMgmtConfigDataType, final String groupName, final Collection<GroupListItem> groupContents,
            final boolean isNewGroup, final AbstractMultiSelectPresenter multiSelectPresenter) {
        super.init(groupTypeName, groupTypeId, groupMgmtConfigDataType, groupName, groupContents, isNewGroup, multiSelectPresenter);
        this.groupManagementItem = groupMgmtConfigDataType.getGroupManagementItem(groupTypeId);
        final String liveLoadRoot = groupManagementItem.getLiveloadRoot();
        String groupTypeDisplayName;
        String title = groupTypeDisplayName = groupManagementItem.getName();
        final String liveloadUrl = groupManagementItem.getLiveloadUrl();
        final WizardDataType wizardDataType = groupManagementItem.getWizard();
        final boolean isPlmn = wizardDataType != null;
        if (isPlmn) {
            getView().addWizardView(multiSelectPresenter.getView());
            multiSelectPresenter.init(wizardDataType, this, liveloadUrl, liveLoadRoot);
            title = groupManagementItem.getHeader();
        }
        getView().init(liveloadUrl, title, liveLoadRoot, isPlmn, groupTypeDisplayName);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.AbstractGroupEditPresenter#getNewGroupView()
     */
    @Override
    protected IEditGroupView getEditGroupView() {
        return getView();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.AbstractGroupEditPresenter#getGroupElementList()
     */
    @Override
    protected Collection<GroupListItem> getGroupElements() {
        return getView().getCurrentGroupElements();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.AbstractGroupEditPresenter#addGroupContents(java.util.Collection)
     */
    @Override
    protected void addGroupContents(final Collection<GroupListItem> groupContents) {
        getView().addGroupContents(groupContents);

    }

    /**
     * Group Nodes currently selected in Liveload Panel
     * @param selectedItems
     */
    public void setLiveLoadItemsSelected(final List<GroupListItem> selectedItems) {
        this.liveLoadSelectedItems = selectedItems;
        getView().setAddButtonEnabled(selectedItems.size() > 0);
    }

    /**
     * Add Items selected from Liveload panel  to the edited group view
     */
    public void addItem() {
        getView().addGroupContents(appendSelectedWizardItems(liveLoadSelectedItems));
        setSaveButtonsEnabled();
    }

    /**
     * Append the items which are selected from wizard view before liveload view is configured, if wizard view is active
     * 
     * @param selectedItems
     * @return
     */
    private List<GroupListItem> appendSelectedWizardItems(final List<GroupListItem> selectedItems) {
        if (selectedItem != null && groupItemsAggregator != null) {
            final ArrayList<GroupListItem> newItems = new ArrayList<GroupListItem>();
            for (final GroupListItem item : selectedItems) {
                newItems.add(groupItemsAggregator.getAggregateGroupListItem(selectedItem, item));
            }
            return newItems;
        }
        return selectedItems;
    }

    /**
     * Delete Items from the current Group
     */
    public void deleteButtonClicked() {
        deleteItems(currentGroupSelectedItems);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.AbstractGroupEditPresenter#removeItems(java.util.Collection)
     */
    @Override
    protected void removeItems(final Collection<GroupListItem> selectedItems) {
        getView().removeItems(selectedItems);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.IWizardClick#onClear()
     */
    @Override
    public void onClear() {
        getView().clear();

    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.IWizardClick#onWizardItemSelect(com.ericsson.eniq.events.ui.client.groupmanagement.groupelement.GroupListItem, java.util.List)
     */
    @Override
    public void onMultiItemSelect(final GroupListItem selectedItem, final List<GroupListItem> items) {
        getView().clear();
        if (liveLoadSelectedItems != null) {
            liveLoadSelectedItems.clear();
        }
        this.selectedItem = selectedItem;
        getView().showSugessions(items);

    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.IWizardClick#unMask()
     */
    @Override
    public void unMask() {
        getView().setLiveLoadMask(false);

    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.IWizardClick#mask(java.lang.String)
     */
    @Override
    public void mask() {
        getView().setLiveLoadMask(true);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.IWizardClick#setGroupItemsAggregator(com.ericsson.eniq.events.ui.client.groupmanagement.multiselect.GroupItemsAggregator)
     */
    @Override
    public void setGroupItemsAggregator(GroupItemsAggregator groupItemsAggregator) {
        this.groupItemsAggregator = groupItemsAggregator;
    }

}
