/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class StatusBarChangeEvent extends GwtEvent<StatusBarChangeEventHandler> {

    public final static Type<StatusBarChangeEventHandler> TYPE = new Type<StatusBarChangeEventHandler>();

    private final String statusBarText;

    /**
     * @param statusBarText
     */
    public StatusBarChangeEvent(String statusBarText) {
        this.statusBarText = statusBarText;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<StatusBarChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(StatusBarChangeEventHandler handler) {
        handler.onStatusBarTextChange(statusBarText);
    }

}
