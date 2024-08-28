package com.ericsson.eniq.events.ui.client.events.dashboard;


import com.ericsson.eniq.events.ui.client.events.component.BaseComponentEvent;

/**
 * @author evyagrz
 * @since 10 2011
 */
public class PortletUnMaskEvent extends BaseComponentEvent<PortletUnMaskEventHandler> {
	
    public final static Type<PortletUnMaskEventHandler> TYPE = new Type<PortletUnMaskEventHandler>();

    public PortletUnMaskEvent(final String portletId) {
        super(portletId);
    }

    @Override
    public Type<PortletUnMaskEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final PortletUnMaskEventHandler handler) {
        handler.onUnMask(this);
    }
}