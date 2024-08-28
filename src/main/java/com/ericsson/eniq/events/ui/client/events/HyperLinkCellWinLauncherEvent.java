/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired into the eventBus when the end user clicks
 * on a hyper link in a grid cell that launches a new window
 * (CSS altered on link to know to launch new window instead of drill)
 * 
 * @author eendmcm
 * @since Mar 2010
 */
public class HyperLinkCellWinLauncherEvent extends GwtEvent<HyperLinkCellWinLauncherEventHandler> {

    /* value of the cell containing the hyper link */
    private final String cellValue;

    /* ID of the window that is been opened when the user clicks the hyper link */
    private final String launchWinID;

    /* row index on chosen row */
    private final int rowIndex;

    /*
     * id of window been updated containing
     * multi-instance window information 
     */
    private final MultipleInstanceWinId multiWinID;

    public static final Type<HyperLinkCellWinLauncherEventHandler> TYPE = new Type<HyperLinkCellWinLauncherEventHandler>();

    /**
     * Construct event fired to event bus to indicate the user is drilling down on a cell
     * 
     * @param multiWinID  id of window been updated  - containing multi-instance window information 
     * @param val       - value contained in the linked cell.
     * @param url       - URL to retrieve the drill data from.       
     * @param rowNdx    - index of the chosen row
     */
    public HyperLinkCellWinLauncherEvent(final MultipleInstanceWinId multiWinId, final String val,
            final String launchID, final int rowNdx) {

        this.multiWinID = multiWinId;
        this.cellValue = val;
        this.launchWinID = launchID;
        this.rowIndex = rowNdx;

    }

    @Override
    protected void dispatch(final HyperLinkCellWinLauncherEventHandler handler) {
        handler.handleCellLauncherClick(multiWinID, cellValue, launchWinID, rowIndex);

    }

    @Override
    public Type<HyperLinkCellWinLauncherEventHandler> getAssociatedType() {
        return TYPE;
    }

}
