/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.file;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public class File extends JavaScriptObject {

    protected File() {
    }

    // @formatter:off
    public final native String getName() /*-{
        return this.name;
    }-*/;
    // @formatter:on
}
