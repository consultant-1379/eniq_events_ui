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
public class FileReader extends JavaScriptObject {

    protected FileReader() {

    }

    public static final FileReader create() {
        return createNative();
    }

    public final void readAsText(final JavaScriptObject file, final ProgressCallback callback) {
        this.createEventHandlers(callback);
        this.readAsText(this, file);
    }

    // @formatter:off
    /**
     * HTML5 FileReader Wrapper
     * @return
     */
    protected final native static FileReader createNative() /*-{
        return new FileReader();
    }-*/;
    
    private final native String createEventHandlers(ProgressCallback callback) /*-{
           this.onload = function(event) {
               @com.ericsson.eniq.events.ui.client.groupmanagement.file.FileReader::handleLoad(Lcom/ericsson/eniq/events/ui/client/groupmanagement/file/ProgressCallback;Lcom/ericsson/eniq/events/ui/client/groupmanagement/file/ProgressEvent;) (callback, event);
           };
    
           this.onerror = function(event) {
           @com.ericsson.eniq.events.ui.client.groupmanagement.file.FileReader::handleError(Lcom/ericsson/eniq/events/ui/client/groupmanagement/file/ProgressCallback;Lcom/ericsson/eniq/events/ui/client/groupmanagement/file/ProgressEvent;) (callback, event);
           };
       }-*/;

    protected final native String readAsText(FileReader reader, JavaScriptObject file) /*-{
        reader.readAsText(file);
    }-*/;

 // @formatter:on
    protected static final void handleLoad(final ProgressCallback callback, final ProgressEvent e) {
        callback.onLoad(e);
    }

    protected static final void handleError(final ProgressCallback callback, final ProgressEvent e) {
        callback.onError(e);
    }

}
