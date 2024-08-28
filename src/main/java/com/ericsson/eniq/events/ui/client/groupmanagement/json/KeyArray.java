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
public class KeyArray extends JSONArray {
    private Key[] keyArray;

    public KeyArray() {
    }

    /**
     * @param javaScriptObject
     */
    public KeyArray(final JavaScriptObject jso) {
        super(jso);
        keyArray = new Key[this.size()];
        for (int i = 0; i < this.size(); i++) {
            keyArray[i] = new Key(get(i).isObject().getJavaScriptObject());
        }
    }

    public void addKey(final int index, final Key key) {
        set(index, key);
    }

    public Key getKey(final int index) {
        return index < keyArray.length ? keyArray[index] : null;
    }
}
