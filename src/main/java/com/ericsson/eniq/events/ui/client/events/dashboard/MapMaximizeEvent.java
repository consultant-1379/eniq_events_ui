package com.ericsson.eniq.events.ui.client.events.dashboard;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This event is used to handle maximize action on map portlet.
 * Used by MapPortletWindow only.
 *
 * @author evyagrz
 * @since 11 2011
 */
public class MapMaximizeEvent extends GwtEvent<MapMaximizeEventHandler>{
    public final static Type<MapMaximizeEventHandler> TYPE = new Type<MapMaximizeEventHandler>();

    @Override
    public Type<MapMaximizeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final MapMaximizeEventHandler handler) {
        // Handle maximize
        handler.handleMaximize(this);
    }
}