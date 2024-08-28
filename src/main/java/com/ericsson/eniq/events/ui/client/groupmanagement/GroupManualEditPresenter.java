/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.ericsson.eniq.events.ui.client.common.language.Grammar;
import com.ericsson.eniq.events.ui.client.datatype.group.GroupMgmtConfigDataType;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListTextItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.multiselect.AbstractMultiSelectPresenter;
import com.google.gwt.view.client.ListDataProvider;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for the Manual Edit Component of Groups i.e. Manual Entry of Group Elements for TAC and IMSI
 * @author ecarsea
 * @since 2011
 *
 */
public class GroupManualEditPresenter extends AbstractGroupEditPresenter<GroupManualEditView> {

    private static final String VALIDATION_ERROR_DIALOG_TITLE = "Invalid Entry";

    private static final String GROUP_ELEMENT_SAPERATOR = ",";

    private final ListDataProvider<GroupListItem> elementListDataProvider = new ListDataProvider<GroupListItem>();

    // can also send regular expression for IMSI and TAC in metadata instead of putting this hardcode check
    private static final String REGEX_EXP_IMSI = "^[0-9]{1,18}$";//same as used in service to validate IMSI length

    private static final String REGEX_EXP_TAC = "^[0-9]{1,9}$";

    private static final int MAX_TAC_LENGTH = 9;

    private static final int MAX_IMSI_LENGTH = 18;

    private static final String TAC_GROUP = "TAC";

    /**
     * @param eventBus
     * @param view
     */
    @Inject
    public GroupManualEditPresenter(final EventBus eventBus, final GroupManualEditView view) {
        super(eventBus, view);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.AbstractGroupEditPresenter#init(java.lang.String, com.ericsson.eniq.events.ui.client.datatype.group.GroupMgmtConfigDataType, java.lang.String, java.util.Collection, boolean)
     */
    @Override
    public void init(final String groupTypeName, final String groupTypeId, final GroupMgmtConfigDataType
            groupMgmtConfigDataType,
            final String groupName, final Collection<GroupListItem> groupContents, final boolean isNewGroup,
            final AbstractMultiSelectPresenter multiSelectPresenter) {
        super.init(groupTypeName, groupTypeId, groupMgmtConfigDataType, groupName, groupContents, isNewGroup, multiSelectPresenter);
        getView().setData(groupTypeName, elementListDataProvider);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.AbstractGroupEditPresenter#addGroupContents(java.util.Collection)
     */
    @Override
    protected void addGroupContents(final Collection<GroupListItem> groupContents) {
        elementListDataProvider.getList().addAll(groupContents);
    }

    protected void deleteElements(final Set<GroupListItem> selectedSet) {
        this.deleteItems(selectedSet);
    }

    /**
     * @param text
     */
    public void onGroupElementsEntered(final String text) {
        final String[] elements = text.split(GROUP_ELEMENT_SAPERATOR);
        final List<GroupListItem> elementList = elementListDataProvider.getList();
        final List<String> invalidEntries = new ArrayList<String>();

        for (final String element : elements) {
            //Validate entered elements should be comma separated if more than one element entered,
            //Element can only contain digits and number of digits must be less than or equal to 18, as maximum IMSI length is 18
            //if there will be any space that will be trimmed
            String e = element.trim();
            GroupListItem groupListItem = new GroupListTextItem(e);
            if (!elementList.contains(groupListItem) && e.length() > 0) {
                if (!valid(e, groupTypeId.equals(TAC_GROUP) ? REGEX_EXP_TAC : REGEX_EXP_IMSI)) {
                    invalidEntries.add(e);
                    continue;
                }
                elementList.add(groupListItem);
            }
        }
        final int size = invalidEntries.size();
        if (size > 0) {
            displayErrorDialog(VALIDATION_ERROR_DIALOG_TITLE,
                    "Invalid " + groupTypeId + new Grammar().getNounEnding(size) + ": " + invalidEntries + ". "
                            + groupTypeId + " can only be digits with maximum length "
                            + (groupTypeId.equals(TAC_GROUP) ? MAX_TAC_LENGTH : MAX_IMSI_LENGTH) + ".");
        }
        getView().clearGroupEntryBox();
        setSaveButtonsEnabled();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.AbstractGroupEditPresenter#getNewGroupView()
     */
    @Override
    protected IEditGroupView getEditGroupView() {
        return getView();
    }

    /**
     * @return
     */
    @Override
    protected List<GroupListItem> getGroupElements() {
        return elementListDataProvider.getList();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.AbstractGroupEditPresenter#removeItems(java.util.Collection)
     */
    @Override
    protected void removeItems(final Collection<GroupListItem> selectedItems) {
        elementListDataProvider.getList().removeAll(selectedItems);

    }

    /**
     * Validate the given element against regular expression.
     * Element can only contain digits with maximum length 18.
     * @param element
     * @return
     */
    private static boolean valid(final String element, final String regExp) {
        return element.matches(regExp);
    }
}
