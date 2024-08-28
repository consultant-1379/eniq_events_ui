/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event fired to event bus when user clicks the
 * refresh button on the paging toolbar. This indicates that
 * the window needs to make a new call to the server with the
 * same query parameters
 * 
 * @author eendmcm
 * @since Mar 2010
 */
public class RefreshWindowEvent extends GwtEvent<RefreshWindowEventHandler> {

    public static final Type<RefreshWindowEventHandler> TYPE = new Type<RefreshWindowEventHandler>();

    /*
     * id of window been updated containing
     * multi-instance window information 
     */
    private final MultipleInstanceWinId multiWinID;

    /**
     * Event fired to event bus to indicate refresh has been raised
     * @param multiWinID      id of window been updated  - containing multi-instance window information 
     */
    public RefreshWindowEvent(final MultipleInstanceWinId multiWinID) {
        this.multiWinID = multiWinID;
    }

    @Override
    protected void dispatch(final RefreshWindowEventHandler handler) {
        handler.handleWindowRefresh(multiWinID);

    }

    @Override
    public Type<RefreshWindowEventHandler> getAssociatedType() {
        return TYPE;
    }
}