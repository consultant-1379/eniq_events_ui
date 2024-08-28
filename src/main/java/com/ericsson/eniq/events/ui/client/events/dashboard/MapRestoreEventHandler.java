package com.ericsson.eniq.events.ui.client.events.dashboard;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author evyagrz
 * @since 11 2011
 */
public interface MapRestoreEventHandler extends EventHandler {
    void restoreMap(MapRestoreEvent event);
}
