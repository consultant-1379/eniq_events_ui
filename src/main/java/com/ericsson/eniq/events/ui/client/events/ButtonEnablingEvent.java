/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to fire to tell windows to check if toolbar 
 * buttons should be disabled/enabled 
 * Reminder to checks all buttons in the toolbar when this is fired
 * 
 * @author esuslyn
 * @author eeicmsy
 * @since October 2010
 */
public class ButtonEnablingEvent extends GwtEvent<ButtonEnablingEventHandler> {

    public static final Type<ButtonEnablingEventHandler> TYPE = new Type<ButtonEnablingEventHandler>();

    private final String winId;

    private final int rowCount;

    /**
     * Event to fire to tell window to check its button enable status (e.g. 
     * on BreadCrumb Navigation change)
     * @param winId -  window initiating the request
     * @param rowCount - number of rows in the grid
     */
    public ButtonEnablingEvent(final String winId, final int rowCount) {
        super();
        this.winId = winId;
        this.rowCount = rowCount;

    }

    @Override
    protected void dispatch(final ButtonEnablingEventHandler handler) {
        handler.handleButtonEnabling(winId, rowCount);

    }

    @Override
    public Type<ButtonEnablingEventHandler> getAssociatedType() {
        return TYPE;
    }

}
