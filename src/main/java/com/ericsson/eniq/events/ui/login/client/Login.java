package com.ericsson.eniq.events.ui.login.client;

import com.ericsson.eniq.events.ui.login.client.gin.LoginGinjector;
import com.ericsson.eniq.events.ui.login.client.mvp.ILoginPresenter;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The Login main class. Kick off the Login Module
 * 
 * @author ecarsea
 * 
 */
public class Login implements EntryPoint {

    public static final String CSS_THEME_LIGHT = "light";

    public static final String CSS_THEME_DARK = "dark";

    private final LoginGinjector injector = GWT.create(LoginGinjector.class);

    /* (non-Javadoc)
     * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
     */
    @Override
    public void onModuleLoad() {
        final ILoginPresenter presenter = injector.getLoginPresenter();
        RootPanel.get().add(presenter.getView().asWidget());
    }
}
