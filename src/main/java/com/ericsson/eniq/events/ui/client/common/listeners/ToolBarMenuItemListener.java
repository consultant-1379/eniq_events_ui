/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.listeners;

import com.google.web.bindery.event.shared.EventBus;

import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.ericsson.eniq.events.ui.client.datatype.ToolBarURLChangeDataType;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.ericsson.eniq.events.ui.client.events.WindowToolbarEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;

/**
 *  Generic menu item listener on floating window ToolBar drop down menu
 *  
 * @author eeicmsy
 * @since April 2010
 */
public class ToolBarMenuItemListener extends SelectionListener<MenuEvent> {

    private final IExtendedWidgetDisplay view;

    private final EventType eventID;

    private final EventBus eventBus;

    private final ToolBarURLChangeDataType urlInfo;

    /**
     * Generic menu item listener on floating window ToolBar drop down menu
     * @param eventBus    - eventBus singleton required for presenters
     * @param view        - view class in MVP pattern (generic for charts and grid windows)
     * @param eventID     - defined event type for toolbar button, as defined in ToolbarPanelInfoDataType
     * @param urlInfo     - can be null mostly unless toolbar menu item is making its own new server calls.
     */
    public ToolBarMenuItemListener(final EventBus eventBus, final IExtendedWidgetDisplay view, final EventType eventID,
            final ToolBarURLChangeDataType urlInfo) {
        this.view = view;
        this.eventID = eventID;
        this.eventBus = eventBus;
        this.urlInfo = urlInfo;
    }

    /**
     * Generic menu item listener on floating window ToolBar drop down menu
     * 
     * @param eventBus    - eventBus singleton required for presenters
     * @param view        - view class in MVP pattern (generic for charts and grid windows)
     * @param eventID     - defined event type for toolbar button, as defined in ToolbarPanelInfoDataType
     */
    public ToolBarMenuItemListener(final EventBus eventBus, final IExtendedWidgetDisplay view, final EventType eventID) {
        this(eventBus, view, eventID, null);
    }

    @Override
    public void componentSelected(final MenuEvent ce) {
        eventBus.fireEvent(new WindowToolbarEvent(view, eventID, eventBus, ce.getItem(), urlInfo));
    }
}
