/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for Change of TimeParameters
 * @author eendmcm
 * @since Feb 2010
 */
public interface TimeParameterValueChangeEventHandler extends EventHandler {

    /**
     * Handle time change update via event bus broadcase 
     * @param multiWinId    unique window id with multi instance support (search field data)
     * @param time          time to update
     */
    void handleTimeParamUpdate(final MultipleInstanceWinId multiWinId, final TimeInfoDataType time);

    /**
     * Handle direct call to update time (no event bus)
     * @param time       time to update 
     */
    void handleTimeParamUpdate(final TimeInfoDataType time);
}
