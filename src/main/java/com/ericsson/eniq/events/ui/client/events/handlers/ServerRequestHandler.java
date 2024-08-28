/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events.handlers;

import com.ericsson.eniq.events.common.client.mvp.Display;
import com.ericsson.eniq.events.ui.client.common.widget.AbstractBaseWindowDisplay;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.events.ServerRequestEvent;
import com.ericsson.eniq.events.ui.client.events.ServerRequestEventHandler;

/**
 * Handles masking and unmasking windows when server request handling
 * @author edmibuz
 */
public class ServerRequestHandler implements ServerRequestEventHandler {

    private final Display display;

    private final MultipleInstanceWinId multiWinID;

    public ServerRequestHandler(final MultipleInstanceWinId multiWinID, final Display display) {

        this.multiWinID = multiWinID;
        this.display = display;
    }

    /**
     * (unfortunatly) Account for multiple window id changing at run time, 
     * i.e. when drill on a grid from APN to a TAC.
     * Our guard for event bus will have to account for this
     * 
     * @param data  new search data
     */
    public void resetSearchData(final SearchFieldDataType data) {
        multiWinID.setSearchInfo(data);
    }

    @Override
    public void onRequestFired(final ServerRequestEvent event) {
        if (!isApplicable(event)) {
            return;
        }

        display.startProcessing();
    }

    @Override
    public void onRequestCancelled(final ServerRequestEvent event) {
        if (!isApplicable(event)) {
            return;
        }

        display.stopProcessing();
    }

    private boolean isApplicable(final ServerRequestEvent event) {
        final MultipleInstanceWinId eventId = event.getMultiWinID();

        if (display instanceof AbstractBaseWindowDisplay) {

            final SearchFieldDataType eventSearchData = eventId.getSearchInfo();
            if (eventSearchData != null) {
                if (!multiWinID.equals(eventId))   {
                    return false;
                }
            }
        }

        return this.multiWinID.isThisWindowGuardCheck(eventId);
    }

}
