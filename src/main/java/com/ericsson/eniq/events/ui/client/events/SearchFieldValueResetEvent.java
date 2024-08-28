/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event fired to event bus when user has made
 * a selection in the search field, indicating that all windows 
 * interested in the search field will need to update.
 * 
 * @author eeicmsy
 * @since Feb 2010
 */
public class SearchFieldValueResetEvent extends GwtEvent<SearchFieldValueResetEventHandler> {

    public static final Type<SearchFieldValueResetEventHandler> TYPE = new Type<SearchFieldValueResetEventHandler>();

    /*
     * id of window to update 
     * (same as query id that will be sent to URL) 
     */
    private final String queryId;

    /*
     * unique id of tab where window is contained
     * to avoid windows interfering with each other
     */
    private final String tabId;

    /*
     * Value entered into the search field converted to a String
     * along with URL parameter(s) (e.g. myNode and "nodeType="SGSN", node=mynode")
     */
    private final SearchFieldDataType searchFieldData;

    /* the default window url to use when the search submit button is clicked. 
     * required when search field is changed and an already launched window's url is changed due to drill down 
     */
    private final String defaultUrl;

    /**
     * Event fired to eventbus to indicate the search field as updated
     * @param tabId           - unique id of tab where window is contained to avoid windows interfering with each other
     * @param queryId         - id of window (same as query id that will be sent to URL) 
     *                          to update for the search field update
     * @param data              the search field value (converted to String), e.g. "imsi=1212121", or 
     *                        - for multiple values "&type=APN&node=myNode"
     * @param url             - the default window url to use when the search submit button is clicked. 
     *                          required when search field is changed and an already launched window's url is changed due to drill down                      
     */
    public SearchFieldValueResetEvent(final String tabId, final String queryId, final SearchFieldDataType data,
            final String url) {
        this.tabId = tabId;
        this.queryId = queryId;
        this.searchFieldData = data;
        this.defaultUrl = url;
    }

    @Override
    protected void dispatch(final SearchFieldValueResetEventHandler handler) {
        handler.handleSearchFieldParamUpdate(tabId, queryId, searchFieldData, defaultUrl);
    }

    @Override
    public Type<SearchFieldValueResetEventHandler> getAssociatedType() {
        return TYPE;
    }
}
