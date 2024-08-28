package com.ericsson.eniq.events.ui.client.datatype.dashboard;

import java.util.List;

/**
 * @author evyagrz
 * @since 10 2011
 */
public interface IDashboardState {

    List<IPortletState> getPortletStates();

    void setPortletStates(List<IPortletState> portletStates);
    
}