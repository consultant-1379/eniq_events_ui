package com.ericsson.eniq.events.ui.client.gxt;

import javax.inject.Inject;

import com.ericsson.eniq.events.common.client.preferences.IUserPreferencesHelper;
import com.extjs.gxt.ui.client.state.Provider;

/**
 * This class provides a key value store bridge to browser's HTML5 LocalStorage mechanism.
 * It is an alternative to default GXT CookieProvider since there are too many grids in the application
 * and growth of Cookie payload causes errors.
 *
 * @author edmibuz
 */
public class GxtLocalStorageProvider extends Provider {

    private final IUserPreferencesHelper userPreferencesHelper;

    @Inject
    public GxtLocalStorageProvider(final IUserPreferencesHelper userPreferencesHelper) {
        this.userPreferencesHelper = userPreferencesHelper;
    }

    @Override
    protected void clearKey(final String name) {
        userPreferencesHelper.removeState(name);
    }

    @Override
    protected String getValue(final String name) {
        return userPreferencesHelper.getStateById(name);
    }

    @Override
    protected void setValue(final String name, final String value) {
        userPreferencesHelper.setState(name, value);
    }
}
