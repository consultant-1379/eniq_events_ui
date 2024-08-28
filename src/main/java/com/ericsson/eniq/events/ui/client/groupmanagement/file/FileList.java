/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.file;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.NativeEvent;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public abstract class FileList extends JsArray<File> {

    protected FileList() {

    }

    // @formatter:off
    public static final native FileList fromEvent(NativeEvent event) /*-{
            return event.target.files;
    }-*/;
    // @formatter:on
}