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
public class GroupElementArray extends JSONArray {
    private GroupElementJson[] groupElementArray;

    /**
     * 
     */
    public GroupElementArray() {
    }

    /**
     * @param jso
     */
    public GroupElementArray(final JavaScriptObject jso) {
        super(jso);
        groupElementArray = new GroupElementJson[this.size()];
        for (int i = 0; i < this.size(); i++) {
            groupElementArray[i] = new GroupElementJson(get(i).isObject().getJavaScriptObject());
        }
    }

    public void addGroupElement(final int index, final GroupElementJson groupElement) {
        set(index, groupElement);
    }

    public GroupElementJson getGroupElement(final int index) {
        return index < groupElementArray.length ? groupElementArray[index] : null;
    }
}
