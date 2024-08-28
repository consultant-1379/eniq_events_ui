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
public class GroupElementJson extends JSONObject {
    private KeyArray keyArray;

    public GroupElementJson() {
    }

    /**
     * @param jso
     */
    public GroupElementJson(final JavaScriptObject jso) {
        super(jso);
        keyArray = new KeyArray(get("key").isArray().getJavaScriptObject());
    }

    public void setKey(final KeyArray keyArray) {
        put("key", keyArray);
    }

    public KeyArray getKeyArray() {
        return keyArray;
    }

}
