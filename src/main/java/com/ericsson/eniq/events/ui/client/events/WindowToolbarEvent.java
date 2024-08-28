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
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event fired to event bus when user clicked a Window Toolbar button/menuItem
 * @author eendmcm
 * @since Feb 2010
 */
public class WindowToolbarEvent extends GwtEvent<WindowToolbarEventHandler> {

    private final IExtendedWidgetDisplay refView;

    private final EventType eventID;

    private final EventBus eventBus;

    private final Component toolbarButton;

    private final ToolBarURLChangeDataType urlInfo;

    public static final Type<WindowToolbarEventHandler> TYPE = new Type<WindowToolbarEventHandler>();

    /**
     * Event fired to event bus when user clicked a Window Toolbar button/menuItem (View menu)
     * 
     * @param refView       - view ref (grid view or a chart view)
     * @param eventID       - defined event type for toolbar button, as defined in ToolbarPanelInfoDataType
     * @param eventBus      - the MVP event bus singleton
     * @param toolbarButton - the toolbar buttton which may have its own menu too (e.g. View menu on toolbar)
     * @param urlInfo       - null mostly unless toolbar menu item is making its own new server calls different 
     *                        than its original base window (e.g. switching from roaming by country to roaming by operator or 
     *                        for business intel menu items on toolbar)
     *                   
     */
    public WindowToolbarEvent(final IExtendedWidgetDisplay refView, final EventType eventID, final EventBus eventBus,
            final Component toolbarButton, final ToolBarURLChangeDataType urlInfo) {

        this.refView = refView;
        this.eventID = eventID;
        this.eventBus = eventBus;
        this.toolbarButton = toolbarButton;
        this.urlInfo = urlInfo;
    }

    @Override
    protected void dispatch(final WindowToolbarEventHandler handler) {

        handler.handleToolBarEvent(refView, eventID, eventBus, toolbarButton, urlInfo);
    }

    @Override
    public Type<WindowToolbarEventHandler> getAssociatedType() {
        return TYPE;
    }

}
