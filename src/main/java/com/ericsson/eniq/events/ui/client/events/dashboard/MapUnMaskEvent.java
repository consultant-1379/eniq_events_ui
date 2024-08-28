package com.ericsson.eniq.events.ui.client.events.dashboard;

import com.google.gwt.event.shared.GwtEvent;

/**
 * UnMask event for Map only portlet
 * @author ejedmar
 *
 */
public class MapUnMaskEvent extends GwtEvent<MapUnMaskEventHandler>{
	public final static Type<MapUnMaskEventHandler> TYPE = new Type<MapUnMaskEventHandler>();
	
    @Override
    public Type<MapUnMaskEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final MapUnMaskEventHandler handler) {
        handler.onUnMask(this);
    }
}
