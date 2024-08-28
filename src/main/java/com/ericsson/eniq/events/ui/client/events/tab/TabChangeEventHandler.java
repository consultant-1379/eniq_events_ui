package com.ericsson.eniq.events.ui.client.events.tab;

import com.google.gwt.event.shared.EventHandler;

public interface TabChangeEventHandler extends EventHandler {
    void onTabChangeEvent(TabChangeEvent tabChangeEvent);
}
