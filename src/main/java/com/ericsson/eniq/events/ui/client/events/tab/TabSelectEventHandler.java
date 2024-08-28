package com.ericsson.eniq.events.ui.client.events.tab;

import com.google.gwt.event.shared.EventHandler;

public interface TabSelectEventHandler extends EventHandler {
	void onSelect(String tabId);
}
