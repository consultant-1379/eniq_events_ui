package com.ericsson.eniq.events.ui.client.events.dashboard;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This event is used to restore initial size of map portlet.
 * Used by MapPortletWindow only.
 *
 * @author evyagrz
 * @since 11 2011
 */
public class MapRestoreEvent extends GwtEvent<MapRestoreEventHandler> {
    public final static Type<MapRestoreEventHandler> TYPE = new Type<MapRestoreEventHandler>();

    @Override
    public Type<MapRestoreEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final MapRestoreEventHandler handler) {
        handler.restoreMap(this);
    }
}
