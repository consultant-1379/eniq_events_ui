package com.ericsson.eniq.events.ui.login.client.gin;

import com.ericsson.eniq.events.ui.login.client.mvp.ILoginPresenter;
import com.ericsson.eniq.events.ui.login.client.mvp.LoginPresenter;
import com.ericsson.eniq.events.ui.login.client.mvp.LoginView;
import com.google.gwt.inject.client.AbstractGinModule;

/**
 * Defines a module to be injected via GIN.
 * 
 * @author ecarsea
 */
public class LoginModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(LoginPresenter.ILoginView.class).to(LoginView.class);
        bind(ILoginPresenter.class).to(LoginPresenter.class);
    }
}
