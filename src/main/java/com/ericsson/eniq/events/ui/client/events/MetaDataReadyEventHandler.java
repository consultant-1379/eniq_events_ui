/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for MetaDataReadyEvent
 * 
 * @author eeicmsy
 * @since Jan 2010
 *
 */
public interface MetaDataReadyEventHandler extends EventHandler {

    /**
     * Implemented by handlers to the MetaDataReadyEvent.TYPE 
     * events added to the event bus. This is the event fired
     * when meta data has been successfully read.
     * 
     * The handler classes need to can know that 
     * metadata has been loaded from 
     * server. Specifically of interest to classes 
     * initialised at client launch time - who won't
     * want to call to read any meta data until they know
     * the data has been loaded 
     * 
     * 
     * @see MetaDataReadyEvent
     * @see com.ericsson.eniq.events.ui.client.main.MainPresenter
     */
    void handleMetaDataReadyEvent();

}
