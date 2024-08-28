/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.common.widget.window.EniqWindowEvent;
import com.ericsson.eniq.events.ui.client.common.widget.window.EniqWindowGwt;
import com.ericsson.eniq.events.ui.client.common.widget.window.EniqWindowListener;
import com.ericsson.eniq.events.ui.client.datatype.group.GroupManagementItemDataType;
import com.ericsson.eniq.events.ui.client.datatype.group.GroupMgmtConfigDataType;
import com.ericsson.eniq.events.ui.client.groupmanagement.component.FilterPanel;
import com.ericsson.eniq.events.ui.client.groupmanagement.component.FilterPanel.RefreshSuggessionHandler;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListTextItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.resources.GroupMgmtResourceBundle;
import com.ericsson.eniq.events.widgets.client.dropdown.DropDown;
import com.ericsson.eniq.events.widgets.client.dropdown.IDropDownItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.GROUP_NAME_DISPLAY_LIMIT;

/**
 * Main View for the Group Management Component
 *
 * @author ecarsea
 * @since 2011
 */
public class GroupManagementView extends BaseView<GroupManagementPresenter> {

    private static final String GROUP_MANAGEMENT_WINDOW_ID = "selenium_tag_GROUP_MANAGEMENT_WINDOW";

    private static final String GROUP_MANAGEMENT_WINDOW_TITLE = "Group Management";

    private static final int GROUP_EDIT_VIEW_WIDTH = 485;

    private static final int GROUP_LIVELOAD_VIEW_WIDTH = 270 + 30 + 485;

    private static final int GROUP_LIVELOAD_WIZARD_VIEW_WIDTH = 270 + 30 + 270 + 30 + 485;

    /** Padding + Margins * */
    private static final int GROUP_EDIT_VIEW_OFFSET = 10 + 10;

    private static final String DEFAULT_HEIGHT = "450px";

    private static final String DEFAULT_WIDTH = "380px";

    private static GroupManagementViewUiBinder uiBinder = GWT.create(GroupManagementViewUiBinder.class);

    interface GroupManagementViewUiBinder extends UiBinder<Widget, GroupManagementView> {
    }

    final EniqWindowGwt window;

    @UiField
    HTMLPanel groupManagementContainer;

    @UiField
    HTMLPanel viewContainer;

    @UiField
    FilterPanel groupNameFilterPanel;

    @UiField
    Button newButton;

    @UiField
    Button editButton;

    @UiField
    Button deleteButton;

    @UiField(provided = true)
    GroupMgmtResourceBundle resourceBundle;

    @UiField
    DropDown<GroupTypeData> groupTypeDropDown;

    @SuppressWarnings("unused")
    @UiHandler("newButton")
    public void newButtonClicked(final ClickEvent clickEvent) {
        getPresenter().onNewGroupSelected();
    }

    @SuppressWarnings("unused")
    @UiHandler("editButton")
    public void editButtonClicked(final ClickEvent clickEvent) {
        getPresenter().onGroupEditButtonClicked();
    }

    @SuppressWarnings("unused")
    @UiHandler("deleteButton")
    public void deleteButtonClicked(final ClickEvent clickEvent) {
        getPresenter().onGroupDeleteButtonClicked();
    }

    @Inject
    public GroupManagementView(final GroupMgmtResourceBundle resourceBundle) {
        resourceBundle.style().ensureInjected();
        this.resourceBundle = resourceBundle;
        initWidget(uiBinder.createAndBindUi(this));
        window = new EniqWindowGwt();
        window.setContent(this.asWidget());
        window.setGlassEnabled(true);
        window.setTitle(GROUP_MANAGEMENT_WINDOW_TITLE);
        window.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        window.getElement().setId(GROUP_MANAGEMENT_WINDOW_ID);
        groupNameFilterPanel.init(resourceBundle, true);
        groupNameFilterPanel.setId(GroupManagementUtils.createIdForFilterPanel("GROUP_MGT_VIEW"));
        groupNameFilterPanel.setSuggestionListSize(GROUP_NAME_DISPLAY_LIMIT);
        bind();
    }

    /**
     *
     */
    private void bind() {
        groupNameFilterPanel.addRefreshSuggessionHandler(new RefreshSuggessionHandler() {

            @Override
            public void onSuggestionRefresh() {
                setEditEnabled(false);

            }
        });
        groupNameFilterPanel.setElementDoubleClickHandler(new IElementDoubleClickHandler() {

            @Override
            public void onElementDoubleClicked(final GroupListItem value) {
                getPresenter().onGroupDoubleClick(value);

            }
        });

        groupNameFilterPanel.setItemSelectionHandler(new ISelectionHandler() {

            @Override
            public void onItemsSelected(final List<GroupListItem> selectedItems) {
                getPresenter().onItemsSelected(selectedItems);

            }
        });
        window.addWindowListener(new EniqWindowListener() {

            @Override
            public void windowClosed(final EniqWindowEvent eniqWindowEvent) {
                getPresenter().close();
            }
        });
    }

    /** @param groupMgmtConfigDataType  */
    public void launch(final GroupMgmtConfigDataType groupMgmtConfigDataType) {
        RootPanel.get().add(window);
        window.center();
        setupGroupNameDropDown(groupMgmtConfigDataType);
    }

    /** @param groupNames  */
    public void setGroups(final Collection<String> groupNames) {
        List<GroupListItem> groupListItems = new ArrayList<GroupListItem>();
        for (String s : groupNames) {
            groupListItems.add(new GroupListTextItem(s));
        }
        groupNameFilterPanel.setSuggestions(groupListItems);
    }

    public void addGroupEditWidget(final Widget groupEditWidget, final boolean liveload, final boolean hasWizard) {
        groupManagementContainer.add(groupEditWidget);
        viewContainer.setVisible(false);
        window.setWidth((liveload ? (hasWizard ? GROUP_LIVELOAD_WIZARD_VIEW_WIDTH : GROUP_LIVELOAD_VIEW_WIDTH)
                : GROUP_EDIT_VIEW_WIDTH) + GROUP_EDIT_VIEW_OFFSET + "px");
        window.center();
    }

    /* (non-Javadoc)
    * @see com.google.gwt.user.client.ui.Composite#onAttach()
    */
    @Override
    protected void onAttach() {
        super.onAttach();
        getPresenter().bind();
    }

    /** @param enabled  */
    public void setDeleteEnabled(final boolean enabled) {
        deleteButton.setEnabled(enabled);
    }

    /** @param enabled  */
    public void setEditEnabled(final boolean enabled) {
        editButton.setEnabled(enabled);
    }

    /**
     * Remove the group edit view from main window
     *
     * @param groupEditWidget
     */
    public void removeGroupEditWidget(final Widget groupEditWidget) {
        if (groupEditWidget != null) {
            viewContainer.setVisible(true);
            groupManagementContainer.remove(groupEditWidget);
            window.setWidth(DEFAULT_WIDTH);
            window.center();
        }
    }

    /** Disable the components of this view */
    public void disable() {
        setEditEnabled(false);
        setDeleteEnabled(false);
        newButton.setEnabled(false);
        groupTypeDropDown.setEnabled(false);
        groupNameFilterPanel.setEnabled(false);
    }

    /** Enable the components of this view */
    public void enable() {
        newButton.setEnabled(true);
        groupTypeDropDown.setEnabled(true);
        groupNameFilterPanel.setEnabled(true);
    }

    private void setupGroupNameDropDown(final GroupMgmtConfigDataType groupMgmtConfigDataType) {
        final List<GroupTypeData> groupTypeDataList = new ArrayList<GroupTypeData>();
        for (GroupManagementItemDataType item : groupMgmtConfigDataType.getGroupManagementItems()) {
            groupTypeDataList.add(getGroupTypeData(item));
        }

        Collections.sort(groupTypeDataList);

        groupTypeDropDown.update(groupTypeDataList);
        groupTypeDropDown.addValueChangeHandler(new ValueChangeHandler<GroupTypeData>() {

            @Override
            public void onValueChange(final ValueChangeEvent<GroupTypeData> event) {
                final GroupTypeData value = event.getValue();
                newButton.setEnabled(true);
                groupNameFilterPanel.setHeader(value.getName() + " Group Name");
                groupNameFilterPanel.removeStyleName(resourceBundle.style().groupNameFilter());
                groupNameFilterPanel.clear();
                getPresenter().onGroupTypeSelected(value.getName(), value.getValue());
            }
        });
    }

    protected GroupTypeData getGroupTypeData(final GroupManagementItemDataType item) {
        return new GroupTypeData(item.getName(), item.getId());
    }

    class GroupTypeData implements IDropDownItem, Comparable<GroupTypeData> {

        protected final String name;

        protected final String value;

        /**
         * @param name
         * @param value
         */
        public GroupTypeData(final String name, final String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean isSeparator() {
            return false;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(final Object other) {
            boolean result = false;
            if (other instanceof GroupTypeData) {
                GroupTypeData that = (GroupTypeData) other;
                result = (this.name == that.value && this.value == that.value);
            }
            return result;
        }

        @Override
        public int hashCode() {
            return (41 * (41 + name.hashCode()) + value.hashCode());
        }

        @Override
        public int compareTo(final GroupTypeData other) {
            return this.getName().compareToIgnoreCase(other.getName());
        }
    }
}
