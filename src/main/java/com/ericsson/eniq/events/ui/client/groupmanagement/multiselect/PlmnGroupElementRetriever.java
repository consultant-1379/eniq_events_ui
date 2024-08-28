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
import com.ericsson.eniq.events.ui.client.datatype.group.GroupMgmtConfigDataType;
import com.ericsson.eniq.events.ui.client.groupmanagement.GroupElementRetriever;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.plmn.GroupListPlmnCountryItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.plmn.GroupListPlmnItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.plmn.GroupListPlmnOperatorItem;
import com.google.gwt.json.client.JSONObject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class PlmnGroupElementRetriever extends GroupElementRetriever {

    /**
     * @param eventBus
     * @param configType
     */
    public PlmnGroupElementRetriever(EventBus eventBus, GroupMgmtConfigDataType configType) {
        super(eventBus, configType);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.GroupElementRetriever#getGroupName(com.google.gwt.json.client.JSONObject)
     */
    @Override
    protected String getGroupName(JSONObject obj) {
        final JsonObjectWrapper metaData = new JsonObjectWrapper(obj);
        final IJSONObject groupObj = metaData.getArray("data").get(0);
        return groupObj.getString("name");
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.groupmanagement.GroupElementRetriever#getGroupElements(com.google.gwt.json.client.JSONObject)
     */
    @Override
    public List<GroupListItem> getGroupElements(JSONObject object) {
        List<GroupListItem> groupElementList = new ArrayList<GroupListItem>();
        final JsonObjectWrapper metaData = new JsonObjectWrapper(object);
        final IJSONObject groupObj = metaData.getArray("data").get(0);
        final IJSONArray elements = groupObj.getArray("values");
        for (int i = 0; i < elements.size(); i++) {
            final IJSONObject elementObj = elements.get(i);
            final IJSONObject idObj = elementObj.getObject("id");
            groupElementList.add(getGroupElement(idObj));
        }
        return groupElementList;
    }

    /**
     * @param obj
     * @return
     */
    private GroupListItem getGroupElement(IJSONObject obj) {
        String country = obj.getString("1");
        String mcc = obj.getString("2");
        String operator = obj.getString("3");
        String mnc = obj.getString("4");
        return new GroupListPlmnItem(new GroupListPlmnCountryItem(country, mcc), new GroupListPlmnOperatorItem(
                operator, mnc));
    }
}
