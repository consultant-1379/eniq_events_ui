package com.ericsson.eniq.events.ui.client.events.dashboard;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author evyagrz
 * @since 11 2011
 */
public class MapResizeEvent extends GwtEvent<MapResizeEventHandler>{
    public final static Type<MapResizeEventHandler> TYPE = new Type<MapResizeEventHandler>();

    @Override
    public Type<MapResizeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final MapResizeEventHandler handler) {
        handler.mapResize(this);
    }
}