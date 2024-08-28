package com.ericsson.eniq.events.ui.client.events.component;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public abstract class BaseComponentEvent<T extends EventHandler> extends GwtEvent<T> {
	
	private final String componentId;

	public BaseComponentEvent(final String componentId) {
		this.componentId = componentId;
	}

	public String getComponentId() {
		return componentId;
	}	
	
}
