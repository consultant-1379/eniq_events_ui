package com.ericsson.eniq.events.ui.client.events.tab;

import com.google.gwt.event.shared.GwtEvent;

public class TabChangeEvent extends GwtEvent<TabChangeEventHandler> {

    public final static Type<TabChangeEventHandler> TYPE = new Type<TabChangeEventHandler>();

    private String tabID;

    public TabChangeEvent(String tabID) {
        this.tabID = tabID;
    }

    @Override
    public Type<TabChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final TabChangeEventHandler handlerShow) {
        handlerShow.onTabChangeEvent(this);
    }

    /*
      * @return the tabId
      */
    public String getTabID() {
        return tabID;
    }
}
