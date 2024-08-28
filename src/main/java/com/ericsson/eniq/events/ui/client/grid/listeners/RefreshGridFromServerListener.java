/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid.listeners;

import com.google.web.bindery.event.shared.EventBus;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.events.RefreshWindowEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;

/**
 * Click event of Refresh Button on a grid
 * raises an event on the EventBus to call the
 * server with the original parameters
 * 
 * @author eendmcm
 * @since Mar 2010
 */
public class RefreshGridFromServerListener extends SelectionListener<ButtonEvent> {

    private final EventBus eventBus;

    private final MultipleInstanceWinId multiWinId;

    /**
     * class constructor
     * listener for Refresh Button on Grid Paging Toolbar
     * 
     * @param bus       - EventBus
     * @param tabId     - unique id of tab where window is contained to avoid windows interfering with each other
     * @param winID     - ID associated with the BaseWindow
     */
    public RefreshGridFromServerListener(final EventBus bus, final MultipleInstanceWinId multiWinId) {
        eventBus = bus;
        this.multiWinId = multiWinId;

    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.event.SelectionListener#componentSelected(com.extjs.gxt.ui.client.event.ComponentEvent)
     */
    @Override
    public void componentSelected(final ButtonEvent ce) {
        // MVP pattern does not allow talk to window direct so using eventBus
        eventBus.fireEvent(new RefreshWindowEvent(multiWinId));
    }

}
