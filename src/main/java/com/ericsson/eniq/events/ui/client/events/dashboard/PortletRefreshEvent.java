package com.ericsson.eniq.events.ui.client.events.dashboard;


import com.ericsson.eniq.events.ui.client.events.component.BaseComponentEvent;

/**
 * Event is fired when the particular portled needs to be reloaded.
 * This could be used for scheduled refresh or request parameter change.
 * 
 * @author edmibuz
 *
 */
public class PortletRefreshEvent extends BaseComponentEvent<PortletRefreshEventHandler> {
	
    public final static Type<PortletRefreshEventHandler> TYPE = new Type<PortletRefreshEventHandler>();

    public PortletRefreshEvent(final String portletId) {
        super(portletId);
    }

    @Override
    public Type<PortletRefreshEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final PortletRefreshEventHandler handler) {
        handler.onRefresh(this);
    }
    
}