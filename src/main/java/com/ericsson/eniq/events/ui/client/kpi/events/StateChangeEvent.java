/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.kpi.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author eaajssa
 * @since 2012
 *
 */
public class StateChangeEvent extends GwtEvent<StateChangeHandler> {

    public static final Type<StateChangeHandler> TYPE = new Type<StateChangeHandler>();
    
    private final String iconType;

    /**
     * @param iconType
     */
    public StateChangeEvent(final String iconType) {
        this.iconType = iconType;
    }

    @Override
    protected void dispatch(final StateChangeHandler handler) {
        handler.onStateChange(iconType);

    }

    @Override
    public GwtEvent.Type<StateChangeHandler> getAssociatedType() {
        return TYPE;
    }
    
    public String getIconType(){
        return iconType;
    }

}
