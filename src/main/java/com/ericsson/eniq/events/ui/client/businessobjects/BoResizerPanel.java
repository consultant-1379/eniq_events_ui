/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.businessobjects;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public class BoResizerPanel extends FocusPanel {

    private Element resizeElement;

    private boolean mouseDown;

    private int resizeStartX;

    private ScheduledCommand layoutCommand;

    private int preResizeWidth;

    private int maxWidth;

    private boolean finishedLayoutCommand = true;

    private final static int MIN_WIDTH = 300;

    /**
     *
     */
    public BoResizerPanel() {
        sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONMOUSEMOVE);
    }

    public void setResizeElement(final Element resizeElement) {
        this.resizeElement = resizeElement;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt.user.client.Event)
     */
    @Override
    public void onBrowserEvent(final Event event) {
        final int xPos = event.getClientX();
        switch (event.getTypeInt()) {
        case Event.ONMOUSEDOWN:
            mouseDown = true;
            resizeStartX = xPos;
            maxWidth = Window.getClientWidth() / 2;
            preResizeWidth = resizeElement.getClientWidth();
            Event.setCapture(getElement());
            break;

        case Event.ONMOUSEUP:
            mouseDown = false;
            Event.releaseCapture(getElement());
            break;

        case Event.ONMOUSEMOVE:
            if (mouseDown) {
                int newWidth = preResizeWidth + xPos - resizeStartX;
                if (newWidth == resizeElement.getClientWidth()) {
                    return;
                }
                if (newWidth > maxWidth) {
                    newWidth = maxWidth;
                }

                if (newWidth < MIN_WIDTH) {
                    newWidth = MIN_WIDTH;
                }
                setNewWidth(newWidth);

            }
            break;
        }
        event.preventDefault();
    }

    private void setNewWidth(final int newWidth) {
        // Defer actually updating the finishedLayoutCommand, so that if we receive many
        // mouse events before finishedLayoutCommand/paint occurs, we'll only update once.
        if (finishedLayoutCommand) {
            layoutCommand = new Command() {
                @Override
                public void execute() {
                    resizeElement.getStyle().setWidth(newWidth, Unit.PX);
                    finishedLayoutCommand = true;
                }
            };
            finishedLayoutCommand = false;
            Scheduler.get().scheduleDeferred(layoutCommand);
        }
    }
}
