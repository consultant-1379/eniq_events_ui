/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.google.gwt.event.shared.GwtEvent;

/**
 * event is fired onto the eventBus when the end user changes
 * the provided TimeParameters. This will cause the grid/chart within
 * the container window to make a new call for data from the server
 * 
 * @author eendmcm
 * @since Mar 2010
 */
public class TimeParameterValueChangeEvent extends GwtEvent<TimeParameterValueChangeEventHandler> {

    /*
     * id of window been updated containing
     * multi-instance window information 
     */
    private final MultipleInstanceWinId multiWinID;

    /*
     * Time Parameters provided via the TimeParameters Dialog
     */
    private final TimeInfoDataType timeInfo;

    public static final Type<TimeParameterValueChangeEventHandler> TYPE = new Type<TimeParameterValueChangeEventHandler>();

    /**
     * Event fired to eventbus to indicate the time parameters have changed
     * 
     * @param multiWinID      id of window been updated  - containing multi-instance window information 
     * @param data            Time Info dataType with details of time parameters provided 
     */
    public TimeParameterValueChangeEvent(final MultipleInstanceWinId multiWinID, final TimeInfoDataType time) {
        this.multiWinID = multiWinID;
        this.timeInfo = time;
    }

    @Override
    protected void dispatch(final TimeParameterValueChangeEventHandler handler) {
        handler.handleTimeParamUpdate(multiWinID, timeInfo);
    }

    @Override
    public Type<TimeParameterValueChangeEventHandler> getAssociatedType() {

        return TYPE;
    }

}
