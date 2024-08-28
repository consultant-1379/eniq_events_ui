package com.ericsson.eniq.events.ui.client.common;

import java.util.logging.Logger;

import com.ericsson.eniq.events.common.client.datatype.IUserPreferences;
import com.ericsson.eniq.events.common.client.preferences.UserPreferencesProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

public class UserPreferencesRetriever implements AsyncCallback<IUserPreferences> {

    private final UserPreferencesProvider userPreferencesProvider;

    private final EventBus eventBus;

    private static final Logger LOGGER = Logger.getLogger(UserPreferencesRetriever.class.getName());

    private static boolean settingsLoaded;

    public UserPreferencesRetriever(final EventBus eventBus, final UserPreferencesProvider userPreferencesProvider) {
        this.eventBus = eventBus;
        this.userPreferencesProvider = userPreferencesProvider;
    }

    @Override
    public void onFailure(final Throwable caught) {
        String errMsg = "Failed to load user preferences!";
        if (caught != null) {
            errMsg = errMsg + caught.getMessage();
        }
        LOGGER.warning(errMsg);
        settingsLoaded = true;
        //eventBus.fireEvent(new MetaDataReadyEvent());
    }

    @Override
    public void onSuccess(final IUserPreferences userPreferences) {
        userPreferencesProvider.set(userPreferences);
        settingsLoaded = true;
        //eventBus.fireEvent(new MetaDataReadyEvent());
    }

    public static boolean isUserPreferenceReady() {
        return settingsLoaded;
    }

    public static void resetUserPreferenceReadyState() {
        settingsLoaded = false;
    }
}