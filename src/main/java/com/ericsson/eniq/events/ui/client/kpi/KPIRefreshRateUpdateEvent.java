/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.kpi;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author eemecoy
 *
 */
public class KPIRefreshRateUpdateEvent extends GwtEvent<KPIRefreshRateUpdateHandler> {

    public static final Type<KPIRefreshRateUpdateHandler> TYPE = new Type<KPIRefreshRateUpdateHandler>();

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public GwtEvent.Type<KPIRefreshRateUpdateHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(final KPIRefreshRateUpdateHandler handler) {
        handler.onRefreshRateUpdate();

    }

}
