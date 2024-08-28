/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.google.gwt.event.shared.GwtEvent;

/**
 * event is fired onto the eventBus when the end user clicks
 * on a hyper link in a grid cell. 
 * 
 * @author eendmcm
 * @since Mar 2010
 */
public class HyperLinkCellClickEvent extends GwtEvent<HyperLinkCellClickEventHandler> {

    /*
     * id of window been updated containing
     * multi-instance window information 
     */
    private final MultipleInstanceWinId multiWinID;

    /* value of the cell containing the hyper link */
    private final String cellValue;

    private final String drillDownWindowTypeId;

    /* row index on chosen row */
    private final int rowIndex;

    /**
     * Event fired to event bus to indicate the user is drilling down on a cell
     * 
     * @param multiWinId    unique window id with multi instance support (search field data)
     * @param val        - value contained in the linked cell.
     * @param drillDownWindowTypeId      - id in drilldownWindows section (used referenced as drillDownWindowType in grids section) containing 
     *                                     information needed to call for new grid 
     * @param rowNdx     - index of the chosen row
     */
    public HyperLinkCellClickEvent(final MultipleInstanceWinId multiWinId, final String val,
            final String drillDownWindowTypeId, final int rowNdx) {

        this.multiWinID = multiWinId;
        this.cellValue = val;
        this.drillDownWindowTypeId = drillDownWindowTypeId;
        this.rowIndex = rowNdx;

    }

    public static final Type<HyperLinkCellClickEventHandler> TYPE = new Type<HyperLinkCellClickEventHandler>();

    @Override
    protected void dispatch(final HyperLinkCellClickEventHandler handler) {
        handler.handleCellLinkClick(multiWinID, cellValue, drillDownWindowTypeId, rowIndex);

    }

    @Override
    public Type<HyperLinkCellClickEventHandler> getAssociatedType() {
        return TYPE;
    }

}
