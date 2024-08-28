package com.ericsson.eniq.events.ui.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author edmibuz
 */
public interface ServerRequestEventHandler extends EventHandler {

    void onRequestFired(ServerRequestEvent event);

    void onRequestCancelled(ServerRequestEvent event);

}
