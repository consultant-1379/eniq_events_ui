/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.events;

import com.ericsson.eniq.events.ui.client.workspace.launch.WindowLaunchParams;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class LaunchWindowEvent extends GwtEvent<LaunchWindowEventHandler> {

    public final static Type<LaunchWindowEventHandler> TYPE = new Type<LaunchWindowEventHandler>();

    private final WindowLaunchParams launchParams;

    /**
     * @param launchParams
     */
    public LaunchWindowEvent(WindowLaunchParams launchParams) {
        this.launchParams = launchParams;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<LaunchWindowEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(LaunchWindowEventHandler handler) {
        handler.onWindowLaunch(launchParams);
    }

}
