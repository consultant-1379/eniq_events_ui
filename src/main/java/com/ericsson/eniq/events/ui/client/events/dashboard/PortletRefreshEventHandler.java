package com.ericsson.eniq.events.ui.client.events.dashboard;

import com.google.gwt.event.shared.EventHandler;

public interface PortletRefreshEventHandler extends EventHandler {

    void onRefresh(PortletRefreshEvent event);
    
}