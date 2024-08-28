package com.ericsson.eniq.events.ui.client.events.dashboard;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Mask event for Map only portlet
 * @author ejedmar
 *
 */
public class MapMaskEvent extends GwtEvent<MapMaskEventHandler>{
	public final static Type<MapMaskEventHandler> TYPE = new Type<MapMaskEventHandler>();
	
    @Override
    public Type<MapMaskEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final MapMaskEventHandler handler) {
        handler.onMask(this);
    }
}
