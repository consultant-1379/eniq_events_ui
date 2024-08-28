/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for SearchFieldValueResetEvent
 * 
 * @author eeicmsy
 * @since Feb 2010
 *
 */
public interface SearchFieldValueResetEventHandler extends EventHandler {

    /**
     * Windows listen to the event should update when the search field
     * parameter changes (i.e. make a server call)
     *
     * @param tabId         - unique id of tab where window is contained to avoid windows interfering with each other
     * @param queryId       - window Id (which could exist across multiple tabs)
     * @param data          - Parameter search field input holding url Strings, e.g. "type=SGSN" "node=myNode"
     *                        and search field value itself, i.e. information which may use on title bars, etc
     * @param url           - The default window URL   (which may be for example lost when drill into KPI ratio)                                          
     */
    void handleSearchFieldParamUpdate(final String tabId, final String queryId, final SearchFieldDataType data,
            final String url);
}
