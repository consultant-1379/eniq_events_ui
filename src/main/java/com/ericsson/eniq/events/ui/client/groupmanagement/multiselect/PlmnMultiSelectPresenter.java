/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.multiselect;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.eniq.events.common.client.json.IJSONArray;
import com.ericsson.eniq.events.common.client.json.IJSONObject;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.plmn.GroupListPlmnCountryItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.plmn.GroupListPlmnItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.plmn.GroupListPlmnOperatorItem;
import com.google.gwt.json.client.JSONValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class PlmnMultiSelectPresenter extends AbstractMultiSelectPresenter {

    /**
     * @param view
     * @param eventBus
     */
    @Inject
    public PlmnMultiSelectPresenter(MultiSelectView view, EventBus eventBus) {
        super(view, eventBus);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.multiselect.GroupItemsAggregator#getAggregateGroupListItem(com.ericsson.eniq.events.ui.client.groupmanagement.groupelement.GroupListItem, com.ericsson.eniq.events.ui.client.groupmanagement.groupelement.GroupListItem)
     */
    @Override
    public GroupListItem getAggregateGroupListItem(GroupListItem firstItem, GroupListItem secondItem) {
        return new GroupListPlmnItem((GroupListPlmnCountryItem) firstItem, (GroupListPlmnOperatorItem) secondItem);
    }

    @Override
    protected List<GroupListItem> convertToGroupItemList(final JSONValue value, final boolean initialLoading,
            String dataRoot) {
        final JsonObjectWrapper object = new JsonObjectWrapper(value.isObject());
        if (initialLoading) {
            return getCountries(object.getArray(dataRoot));
        }
        return getOperators(object.getArray(dataRoot));
    }

    List<GroupListItem> getCountries(IJSONArray countriesArray) {
        final List<GroupListItem> suggestions = new ArrayList<GroupListItem>();
        final int size = countriesArray.size();
        for (int i = 0; i < size; i++) {
            IJSONObject id = countriesArray.get(i).getObject("id");
            GroupListPlmnCountryItem country = new GroupListPlmnCountryItem(id.getString("1"), id.getString("2"));
            suggestions.add(country);
        }
        return suggestions;
    }

    List<GroupListItem> getOperators(IJSONArray countriesArray) {
        final List<GroupListItem> suggestions = new ArrayList<GroupListItem>();
        final int size = countriesArray.size();
        for (int i = 0; i < size; i++) {
            IJSONObject id = countriesArray.get(i).getObject("id");
            GroupListPlmnOperatorItem country = new GroupListPlmnOperatorItem(id.getString("1"), id.getString("2"));
            suggestions.add(country);
        }
        return suggestions;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.multiselect.MultiSelectPresenter#getUrlParamValue(com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem)
     */
    @Override
    public String getUrlParamValue(GroupListItem selectedItem) {
        return ((GroupListPlmnCountryItem) selectedItem).getMcc();
    }
}
