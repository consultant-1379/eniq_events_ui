/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class LaunchWindowCompletedEvent extends GwtEvent<LaunchWindowCompletedEventHandler> {

    public final static Type<LaunchWindowCompletedEventHandler> TYPE = new Type<LaunchWindowCompletedEventHandler>();

    private final Widget source;

    /**
     * @param source
     */
    public LaunchWindowCompletedEvent(Widget source) {
        this.source = source;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<LaunchWindowCompletedEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(LaunchWindowCompletedEventHandler handler) {
        handler.onWindowLaunchComplete(source);
    }

}
