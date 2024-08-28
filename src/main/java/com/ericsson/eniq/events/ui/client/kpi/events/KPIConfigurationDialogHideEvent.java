/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.kpi.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author eaajssa
 * @since 2012
 *
 */
public class KPIConfigurationDialogHideEvent extends GwtEvent<KPIConfigurationDialogHideEventHandler> {

    public final static Type<KPIConfigurationDialogHideEventHandler> TYPE = new Type<KPIConfigurationDialogHideEventHandler>();

    @Override
    public Type<KPIConfigurationDialogHideEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final KPIConfigurationDialogHideEventHandler handler) {
        handler.onHide(this);
    }
}
