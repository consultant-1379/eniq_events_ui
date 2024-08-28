package com.ericsson.eniq.events.ui.client.events.dashboard;


import com.ericsson.eniq.events.ui.client.events.component.BaseComponentEvent;

/**
 * @author evyagrz
 * @since 10 2011
 */
public class PortletMaskEvent extends BaseComponentEvent<PortletMaskEventHandler> {
	
    public final static Type<PortletMaskEventHandler> TYPE = new Type<PortletMaskEventHandler>();

    public PortletMaskEvent(final String portletId) {
        super(portletId);
    }

    @Override
    public Type<PortletMaskEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final PortletMaskEventHandler handler) {
        handler.onMask(this);
    }

}