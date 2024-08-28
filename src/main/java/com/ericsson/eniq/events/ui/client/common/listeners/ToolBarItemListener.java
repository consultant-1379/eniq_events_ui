/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.listeners;

import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.ericsson.eniq.events.ui.client.events.WindowToolbarEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Common listener class to pass click events on ToolBar buttons
 * to the eventBus for the appropriate handler
 * (for grids and charts)
 * 
 * @author eeicmsy
 * @since April 2010
 */
public class ToolBarItemListener extends SelectionListener<ButtonEvent> implements ClickHandler{

    private final IExtendedWidgetDisplay view;

    private final EventType eventID;

    private final EventBus eventBus;

    /**
     * Generic listener for toolbar buttons 
     * @param eventBus    - eventBus singleton required for presenters
     * @param view        - view class in MVP pattern
     * @param eventID     - defined event type for toolbar button, as defined in ToolbarPanelInfoDataType
     */
    public ToolBarItemListener(final EventBus eventBus, final IExtendedWidgetDisplay view, final EventType eventID) {
        this.view = view;
        this.eventBus = eventBus;
        this.eventID = eventID;

    }

    @Override
    public void componentSelected(final ButtonEvent ce) {
        eventBus.fireEvent(new WindowToolbarEvent(view, eventID, eventBus, ce.getButton(), null));
    }

    @Override
    public void onClick(final ClickEvent event) {
        eventBus.fireEvent(new WindowToolbarEvent(view, eventID, eventBus, null, null));
    }
}
