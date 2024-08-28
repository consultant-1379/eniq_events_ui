/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.json;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public class Group extends JSONObject {
    private GroupElementArray groupElementArray;

    private String groupName;

    /**
     * @param jso
     */
    public Group(final JavaScriptObject jso) {
        super(jso);
        groupElementArray = new GroupElementArray(this.get("groupElement").isArray().getJavaScriptObject());
        groupName = this.get("name").isString().stringValue();
    }

    /**
     * 
     */
    public Group() {
    }

    public void setGroupName(final String groupName) {
        put("name", new JSONString(groupName));
    }

    public void setGroupType(final String groupType) {
        put("type", new JSONString(groupType));
    }

    public void setGroupElementArray(final GroupElementArray groupElementArray) {
        put("groupElement", groupElementArray);
    }

    public String getGroupName() {
        return groupName;
    }

    public GroupElementArray getGroupElementArray() {
        return groupElementArray;
    }
}
