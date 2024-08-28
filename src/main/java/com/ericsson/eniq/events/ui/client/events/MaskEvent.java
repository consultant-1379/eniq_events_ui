/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event fired to event bus when UI is masked / demasked
 * 
 * @author eriwals
 * @since Oct 2010
 *
 */
public class MaskEvent extends GwtEvent<MaskEventHandler> {

    public static final Type<MaskEventHandler> TYPE = new Type<MaskEventHandler>();
    
    /*
     * Boolean of whether this is a masking or demasking event
     * TRUE = masked
     * FALSE = unmasked
     */
    private final boolean isMasked;
    
    /*
     * String storing the Tab to which this event is addressed
     */
    private final String tabOwner;

    public MaskEvent(final boolean isMasked, final String tabOwner) {
        this.isMasked = isMasked;
        this.tabOwner = tabOwner;
    }    
    
    @Override
    protected void dispatch(final MaskEventHandler handler) {
        handler.handleMaskEvent(isMasked, tabOwner);
    }

    @Override
    public Type<MaskEventHandler> getAssociatedType() {
        return TYPE;
    }
}
