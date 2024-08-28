/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.json;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public class GroupArray extends JSONArray {

    Group[] groupArray;

    /**
     * @param jso
     */
    public GroupArray(final JavaScriptObject jso) {
        super(jso);
        groupArray = new Group[this.size()];
        for (int i = 0; i < this.size(); i++) {
            groupArray[i] = new Group(get(i).isObject().getJavaScriptObject());
        }
    }

    public GroupArray() {
    }

    public void addGroup(final int index, final Group group) {
        set(index, group);
    }

    public Group getGroup(final int index) {
        return index < groupArray.length ? groupArray[index] : null;
    }
}
