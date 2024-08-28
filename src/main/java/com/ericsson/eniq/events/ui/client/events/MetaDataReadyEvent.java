/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event fired to event bus when UI meta data has been loaded
 * 
 * @author eeicmsy
 * @since Jan2010
 *
 */
public class MetaDataReadyEvent extends GwtEvent<MetaDataReadyEventHandler> {

    public static final Type<MetaDataReadyEventHandler> TYPE = new Type<MetaDataReadyEventHandler>();

    @Override
    protected void dispatch(final MetaDataReadyEventHandler handler) {
        handler.handleMetaDataReadyEvent();

    }

    @Override
    public Type<MetaDataReadyEventHandler> getAssociatedType() {
        return TYPE;
    }
}
