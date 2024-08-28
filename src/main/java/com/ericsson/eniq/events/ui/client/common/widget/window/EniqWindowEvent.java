/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget.window;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class EniqWindowEvent {
    private final EniqWindowGwt source;

    /**
     * Build an event from a {@link EniqWindowGwt} as the source.
     * 
     * @param source
     */
    public EniqWindowEvent(final EniqWindowGwt source) {
        this.source = source;
    }

    /**
     * Returns the source of the event.
     * 
     * @return the {@link EniqWindowGwt} source
     */
    public EniqWindowGwt getWindow() {
        return this.source;
    }
}
