/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to fire when want to change the widgetSpecificParams for a window (without creating a new launcher). 
 * 
 * 
 * @author eeicmsy
 * @since Jan 2011
 *
 */
public class WidgetSpecificParamsChangeEvent extends GwtEvent<WidgetSpecificParamsChangeEventHandler> {

    public final static Type<WidgetSpecificParamsChangeEventHandler> TYPE = new Type<WidgetSpecificParamsChangeEventHandler>();

    /*
     * id of window to update 
     * (same as query id that will be sent to URL) 
     */
    private final String winId;

    /*
     * Tab owner of the window
     */
    private final String tabId;

    /*
     * Specific URI required - i.e. to ensure won't use a drilldowned breadcrumb URI
     */
    private final String url;

    /*
     * Most recent search data available
     */
    final SearchFieldDataType searchData;

    /*
     * widget specific parameter string suitable for outbound URL call, e.g. "&eventID=5&type=IMSI"
     */
    private final String widgetSpecificParams;

    /*
     * When search field event already relaunched window
     * won;t want to do it again
     */
    private final boolean neverRelaunchWindow;

    /**
     * Event to fire when want to change the widgetSpecificParams for a window (withuot creating a new launcher). 
     * Handler of this action will  be expected to change the (MetaMenuItem) widgetSpecificParams and force a window refresh with these parameters
     * 
     * @param tabId                 - tab owner if of tab containing window (two ids shared across tabs)
     * @param winId                 - id of window 
     * @param widgetSpecificParams  - widget specific parameter string suitable for outbound URL call, e.g. "&eventID=5&type=IMSI"
     * @param url                   - specific URI required - i.e. to ensure won't use a drilldowned breadcrumb URI
     * @param searchData            - latest search data
     * 
     */
    public WidgetSpecificParamsChangeEvent(final String tabId, final String winId, final String widgetSpecificParams,
            final String url, final SearchFieldDataType searchData, final boolean neverRelaunchWindow) {
        this.tabId = tabId;
        this.winId = winId;
        this.widgetSpecificParams = widgetSpecificParams;
        this.url = url;
        this.searchData = searchData;
        this.neverRelaunchWindow = neverRelaunchWindow;
    }

    @Override
    protected void dispatch(final WidgetSpecificParamsChangeEventHandler handler) {
        handler.handleWidgetSpecificParamsChange(tabId, winId, widgetSpecificParams, url, searchData,
                neverRelaunchWindow);

    }

    @Override
    public Type<WidgetSpecificParamsChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

}