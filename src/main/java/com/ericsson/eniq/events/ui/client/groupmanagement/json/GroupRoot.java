/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.json;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public class GroupRoot extends JSONObject {
    private GroupArray groupArray;

    /**
     * @param jso
     */
    public GroupRoot(final JavaScriptObject jso) {
        super(jso);
        groupArray = new GroupArray(get("group").isArray().getJavaScriptObject());
    }

    /**
     * 
     */
    public GroupRoot() {
    }

    public void setGroups(final GroupArray group) {
        put("group", group);
    }

    public GroupArray getGroupArray() {
        return groupArray;
    }
}
