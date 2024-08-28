package com.ericsson.eniq.events.ui.client.events.tab;

import com.google.gwt.event.shared.GwtEvent;

public class TabSelectEvent extends GwtEvent<TabSelectEventHandler> {

	public final static Type<TabSelectEventHandler> TYPE = new Type<TabSelectEventHandler>();
	private final String tabId;

	public TabSelectEvent(final String tabId) {
		this.tabId = tabId;
	}

	@Override
	public Type<TabSelectEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final TabSelectEventHandler handler) {
		handler.onSelect(tabId);
	}

	/*
	 * @return the tabId
	 */
	public String getTabId() {
		return tabId;
	}
}
