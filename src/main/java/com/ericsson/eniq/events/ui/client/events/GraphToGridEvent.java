/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event fired to event bus when user clicks the
 * toggle to chart button on the window toolbar. 
 * This indicates that the window needs convert the display from
 * a graph to a grid or vice versa
 * 
 * @author eendmcm
 * @since Apr 2010
 */
public class GraphToGridEvent extends GwtEvent<GraphToGridEventHandler> {

    public static final Type<GraphToGridEventHandler> TYPE = new Type<GraphToGridEventHandler>();

    /*
     * id of window been updated containing
     * multi-instance window information 
     */
    private final MultipleInstanceWinId multiWinID;

    private final String elementClickedForTitleBar;

    public GraphToGridEvent(final MultipleInstanceWinId multiWinId) {
        this(multiWinId, EMPTY_STRING);

    }

    /**
     *  Event fired to event bus when user clicks the
     *  toggle to chart button on the window toolbar
     *  
     * @param multiWinID - id of window been updated  - containing multi-instance window information 
     * @param elementClickedForTitleBar - the drilldown from an element in a chart used the toggle code (as well as the real
     *                                    toggle graph to grid code). When drill on an element in the chart (as apposed to the real toggle), 
     *                                    will want to pass through element string for the title bar into the new grid being created
     */

    public GraphToGridEvent(final MultipleInstanceWinId multiWinId, final String elementClickedForTitleBar) {
        this.multiWinID = multiWinId;
        this.elementClickedForTitleBar = elementClickedForTitleBar;

    }

    @Override
    protected void dispatch(final GraphToGridEventHandler handler) {
        handler.handleGraphToGridToggle(multiWinID, true, null, elementClickedForTitleBar);
    }

    @Override
    public Type<GraphToGridEventHandler> getAssociatedType() {

        return TYPE;
    }

}