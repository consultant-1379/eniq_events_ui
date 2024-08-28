/*
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.HasPortletId;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletMoveEvent;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class PortletDragHandler implements DragHandler {

    private final EventBus eventBus;

    private VerticalPanel dragSourceParent;

    private int rowIndex = -1;

    // Used onDragEnd to check if widget changed position or stayed on same place
    private Widget parentBeforeDragEnd;

    public PortletDragHandler(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Log the drag end event.
     *
     * @param event the event to log
     */
    @Override
    public void onDragEnd(final DragEndEvent event) {
        final HasPortletId draggable = (HasPortletId) event.getContext().draggable;

        final Widget source = (Widget) event.getSource();
        final Widget sourceParent = source.getParent();

        if (sourceParent instanceof VerticalPanel && sourceParent.getStyleName().contains("emptyTable")) {
            sourceParent.removeStyleName("emptyTable");
        }

        int widgetIndex = -1;
        if (dragSourceParent != null) {
            if (dragSourceParent.getWidgetCount() == 0) {
                dragSourceParent.addStyleName("emptyTable");
            }
            widgetIndex = getWidgetIndex(source);
            dragSourceParent = null;
        }

        // We only fire event when widget changed it's position, so that Map is not reloading if
        // somebody clicked on a title by accident
        if (hasMoved(sourceParent, widgetIndex)) {
            final String portletId = draggable.getPortletId();
            eventBus.fireEvent(new PortletMoveEvent(portletId));
        }

    }

    private boolean hasMoved(final Widget sourceParent, final int widgetIndex) {
        return !parentBeforeDragEnd.equals(sourceParent) || rowIndex != widgetIndex;
    }

    /**
     * Log the drag start event.
     *
     * @param event the event to log
     */
    @Override
    public void onDragStart(final DragStartEvent event) {
        final Widget source = (Widget) event.getSource();
        final Widget sourceParent = source.getParent();

        if (sourceParent instanceof VerticalPanel) {
            dragSourceParent = (VerticalPanel) sourceParent;
            rowIndex = getWidgetIndex(source);
        }

        parentBeforeDragEnd = sourceParent;
    }

    private int getWidgetIndex(final Widget source) {
        for (int i = 0; i < dragSourceParent.getWidgetCount(); i++) {
            if (source.equals(dragSourceParent.getWidget(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Log the preview drag end event.
     *
     * @param event the event to log
     * @throws VetoDragException exception which may be thrown by any drag handler
     */
    @Override
    public void onPreviewDragEnd(final DragEndEvent event) throws VetoDragException {
    }

    /**
     * Log the preview drag start event.
     *
     * @param event the event to log
     * @throws VetoDragException exception which may be thrown by any drag handler
     */
    @Override
    public void onPreviewDragStart(final DragStartEvent event) throws VetoDragException {
    }
}