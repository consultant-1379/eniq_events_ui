package com.ericsson.eniq.events.ui.client.common;

import javax.inject.Inject;

import com.ericsson.eniq.events.common.client.datatype.IUserPreferences;
import com.ericsson.eniq.events.common.client.preferences.UserPreferencesProvider;
import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.common.client.service.StorageService;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Basic user preferences reader implementation
 * @author ejedmar
 * @since 2011
 *
 */
public class UserPreferencesReader implements IUserPreferencesReader {

    private final StorageService storageService;

    private final EventBus eventBus;

    private final UserPreferencesProvider userPreferencesProvider;

    @Inject
    public UserPreferencesReader(final StorageService storageService, final EventBus eventBus,
            final UserPreferencesProvider userPreferencesProvider) {
        this.storageService = storageService;
        this.eventBus = eventBus;
        this.userPreferencesProvider = userPreferencesProvider;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.common.IUserPreferencesReader#loadUserPreferences()
     */
    @Override
    public void loadUserPreferences() {
        UserPreferencesRetriever.resetUserPreferenceReadyState();
        storageService.load(CommonParamUtil.getLoginUserName(), IUserPreferences.class, new UserPreferencesRetriever(
                eventBus, userPreferencesProvider));

    }

}
