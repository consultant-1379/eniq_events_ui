package com.ericsson.eniq.events.ui.login.client.gin;

import com.ericsson.eniq.events.ui.login.client.mvp.ILoginPresenter;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * The GIN-jector interface. Defines the DI modules for the application.
 * 
 * @author ecarsea
 */
@GinModules({ LoginModule.class })
public interface LoginGinjector extends Ginjector {
    ILoginPresenter getLoginPresenter();
}
