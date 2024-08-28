/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.json;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public class Key extends JSONObject {

    private String name;

    private String value;

    /**
     * 
     */
    public Key() {
        setDefaults(); //NOPMD
    }

    /**
     * @param jso
     */
    public Key(final JavaScriptObject jso) {
        super(jso);
        name = get("name").isString().stringValue();
        value = get("value").isString().stringValue();
    }

    private void setDefaults() {
        setStopTime(null);
        setStartTime(null);
    }

    public void setStopTime(final String stopTime) {
        put("stopTime", stopTime != null ? new JSONString(stopTime) : JSONNull.getInstance());
    }

    public void setName(final String name) {
        put("name", (name != null ? new JSONString(name) : JSONNull.getInstance()));
    }

    public void setValue(final String value) {
        put("value", (value != null ? new JSONString(value) : JSONNull.getInstance()));
    }

    public void setStartTime(final String startTime) {
        put("startTime", (startTime != null ? new JSONString(startTime) : JSONNull.getInstance()));
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
