/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.google.web.bindery.event.shared.EventBus;

import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.ericsson.eniq.events.ui.client.datatype.ToolBarURLChangeDataType;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.event.shared.EventHandler;

/**
 * Interface for class to handle event on the WindowToolBar dispatched using EventBus
 * @author eendmcm
 * @since Feb 2010
 */
public interface WindowToolbarEventHandler extends EventHandler {

    /**
     * Handle toolbar button press or toolbar menu item select
     * 
     * @param viewRef    - GridView or Chart View owning the toolbar 
     * @param eventID    - Event ID associated with the event so know what button has been selected
     * @param eventBus   - The MVP default event bus 
     * @param menuItem   - button or toggle button on the tool bar
     * @param urlInfo    - null or additional information that would be required if toolbar menu item must make new server calls
     */
    void handleToolBarEvent(IExtendedWidgetDisplay viewRef, EventType eventID, EventBus eventBus, Component menuItem,
            final ToolBarURLChangeDataType urlInfo);

}
