/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementView.GroupTypeData;
import com.ericsson.eniq.events.ui.client.groupmanagement.component.FilterPanel;
import com.ericsson.eniq.events.ui.client.groupmanagement.component.RemoteSuggestOracle;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.multiselect.MultiSelectView;
import com.ericsson.eniq.events.ui.client.groupmanagement.resources.GroupMgmtResourceBundle;
import com.ericsson.eniq.events.widgets.client.dropdown.DropDown;
import com.ericsson.eniq.events.widgets.client.mask.MaskHelper;
import com.ericsson.eniq.events.widgets.client.textbox.ExtendedTextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.LOADING;
import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.LOADING_MSG_ENDING_DOTS;

/**
 * Widget for Liveload of Group Nodes and editing of current Group Elements
 *
 * @author ecarsea
 * @since 2011
 */
@SuppressWarnings("unused")
public class GroupLiveloadView extends BaseView<GroupLiveLoadPresenter> implements IEditGroupView {

    private static GroupLiveloadViewUiBinder uiBinder = GWT.create(GroupLiveloadViewUiBinder.class);

    interface GroupLiveloadViewUiBinder extends UiBinder<Widget, GroupLiveloadView> {
    }

    @UiField
    HTMLPanel wizardHolder;

    @UiField
    FilterPanel liveloadGroupFilterPanel;

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
    FilterPanel currentGroupFilterPanel;

    @UiField(provided = true)
    GroupMgmtResourceBundle resourceBundle;

    @UiField
    DropDown<GroupTypeData> groupTypeDropDown;

    @UiHandler("cancelButton")
    public void cancelButtonClicked(final ClickEvent event) {
        getPresenter().cancelGroupEdit();
    }

    @UiHandler("addButton")
    public void addButtonClicked(final ClickEvent event) {
        getPresenter().addItem();
    }

    @UiHandler("deleteButton")
    public void deleteButtonClicked(final ClickEvent event) {
        getPresenter().deleteButtonClicked();
    }

    @UiHandler("saveButton")
    public void saveButtonClicked(final ClickEvent event) {
        getPresenter().onSaveButtonClicked();
    }

    @UiHandler("saveAsButton")
    public void saveAsButtonClicked(final ClickEvent event) {
        getPresenter().onSaveAsButtonClicked();
    }

    private final EventBus eventBus;

    protected List<String> selectedItems;

    private RemoteSuggestOracle oracle;

    private MultiSelectView wizardView;

    private MaskHelper maskHelper;

    @Inject
    public GroupLiveloadView(final EventBus eventBus, final GroupMgmtResourceBundle resourceBundle) {
        this.eventBus = eventBus;
        this.resourceBundle = resourceBundle;
        initWidget(uiBinder.createAndBindUi(this));
        currentGroupFilterPanel.init(resourceBundle, true);
        currentGroupFilterPanel.setId(GroupManagementUtils.createIdForFilterPanel("CURRENT_GROUP"));

        groupTypeDropDown.setEnabled(false);
    }

    public void init(final String url, final String title, final String liveLoadRoot, final boolean isPlmn,
            final String groupTypeDisplayName) {
        groupTypeDropDown.setText(groupTypeDisplayName);
        if (isPlmn) {
            liveloadGroupFilterPanel.init(resourceBundle);
            this.maskHelper = new MaskHelper();
        } else {
            oracle = new RemoteSuggestOracle(eventBus, GroupManagementUtils.getRequestUrl(url), liveLoadRoot, title,
                    liveloadGroupFilterPanel.getElement());
            liveloadGroupFilterPanel.init(resourceBundle, oracle);
            /** Show the Group Nodes on first load i.e. before user enters any keystroke **/
            liveloadGroupFilterPanel.showSuggestionList();
        }
        liveloadGroupFilterPanel.setHeader(GroupManagementUtils.prepareHeader(title));
        liveloadGroupFilterPanel.setId(GroupManagementUtils.createIdForFilterPanel(title));
        liveloadGroupFilterPanel.setItemSelectionHandler(new ISelectionHandler() {

            @Override
            public void onItemsSelected(final List<GroupListItem> selectedItems) {
                getPresenter().setLiveLoadItemsSelected(selectedItems);

            }
        });

        liveloadGroupFilterPanel.setElementDoubleClickHandler(new IElementDoubleClickHandler() {

            @Override
            public void onElementDoubleClicked(final GroupListItem value) {
                getPresenter().addItem();
            }
        });
    }

    public void setLiveLoadMask(final boolean mask) {
        if (mask) {
            final Element element = liveloadGroupFilterPanel.getElement();
            maskHelper.mask(
                    element,
                    LOADING
                            + GroupManagementUtils.getElementTypeFromHeader(liveloadGroupFilterPanel.getHeader()
                            + LOADING_MSG_ENDING_DOTS), element.getOffsetHeight());
        } else {
            maskHelper.unmask();
        }
    }

    public void showSugessions(final Collection<GroupListItem> sugessions) {
        liveloadGroupFilterPanel.setEnabled(true);
        liveloadGroupFilterPanel.setSuggestions(sugessions);
    }

    public void clear() {
        liveloadGroupFilterPanel.setSuggestions(new ArrayList<GroupListItem>());
        liveloadGroupFilterPanel.setEnabled(false);
    }

    public void addWizardView(final MultiSelectView view) {
        this.wizardView = view;
        wizardHolder.add(view);
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.groupmanagement.IEditGroupView#setButtonsEnabled(boolean)
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

    /** @return  */
    public Collection<GroupListItem> getCurrentGroupElements() {
        //TODO here should be logic for saving only key
        return currentGroupFilterPanel.getAllElements();
    }

    /** @param groupContents  */
    public void addGroupContents(final Collection<GroupListItem> groupContents) {
        currentGroupFilterPanel.addSuggestions(groupContents);

    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.groupmanagement.IEditGroupView#setDeleteButtonEnabled(boolean)
    */
    @Override
    public void setDeleteButtonEnabled(final boolean enabled) {
        deleteButton.setEnabled(enabled);
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.groupmanagement.IEditGroupView#configure(java.lang.String, boolean)
    */
    @Override
    public void configure(final String groupNameStr, final boolean isNewGroup) {
        saveAsButton.setVisible(!isNewGroup);
        if (groupNameStr != null && !groupNameStr.isEmpty()) {
            groupName.setText(groupNameStr);
            //group name should be disable when use press edit group button
            groupName.setEnabled(false);
        }
    }

    /** @param enabled  */
    public void setAddButtonEnabled(final boolean enabled) {
        addButton.setEnabled(enabled);
    }

    public void setCurrentGroupSelectionHandler(final ISelectionHandler iSelectionHandler) {
        currentGroupFilterPanel.setItemSelectionHandler(iSelectionHandler);
    }

    /** @param enabled  */
    public void setDeleteEnabled(final boolean enabled) {
        deleteButton.setEnabled(enabled);
    }

    @Override
    public void close() {
        if (wizardView != null) {
            wizardView.close();
        }
    }

    /** @param items  */
    public void removeItems(final Collection<GroupListItem> items) {
        currentGroupFilterPanel.getAllElements().removeAll(items);
        currentGroupFilterPanel.refreshSuggestBox();
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
