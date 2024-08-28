/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.kpi;

import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.common.client.preferences.IUserPreferencesHelper;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.common.client.datatype.IPropertiesState;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.JsonAutoBeanDataFactory;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.kpi.events.KPIConfigurationDialogHideEvent;
import com.ericsson.eniq.events.ui.client.kpi.events.KPIConfigurationDialogHideEventHandler;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author eemecoy
 *
 */
public class KPIConfigurationPresenter extends BasePresenter<KPIConfigurationView> implements
        KPIConfigurationDialogHideEventHandler {

    private final JsonAutoBeanDataFactory jsonAutoBeanDataFactory;

    private final IUserPreferencesHelper userPreferencesHelper;

    private final IMetaReader metaReader;

    private static final String KPI_NOTIFICATION_STORAGE_KEY = "KPI_NOTIFICATION_CONFIGURATION";

    private final EventBus eventBus;

    private KPIConfigurationDialog dialog;

    private final Map<String, TimeInfoDataType> userTimeDetails = new HashMap<String, TimeInfoDataType>();

    @Inject
    public KPIConfigurationPresenter(final KPIConfigurationView view, final EventBus eventBus,
            final JsonAutoBeanDataFactory jsonAutoBeanDataFactory, final IUserPreferencesHelper userPreferencesHelper,
            final IMetaReader metaReader) {
        super(view, eventBus);
        this.eventBus = eventBus;
        this.metaReader = metaReader;
        this.jsonAutoBeanDataFactory = jsonAutoBeanDataFactory;
        this.userPreferencesHelper = userPreferencesHelper;
        bind();
    }

    @Override
    protected void onBind() {
        initializePersistedPropertiesIfRequired();
        getView().init();
    }

    public KPIConfigurationDialog getDialog() {
        if (dialog == null) {
            dialog = new KPIConfigurationDialog();
            dialog.setContent(getView());
            dialog.addHideEventHandler(this);
        }
        return dialog;
    }

    private void initializePersistedPropertiesIfRequired() {
        final Map<String, String> userConfiguredProperties = getUserConfiguredSettings();
        if (userConfiguredProperties == null || userConfiguredProperties.size() == 0) {
            final KPIConfigurationProperties kpiConfigurationProperties = new KPIConfigurationProperties();
            final Map<String, String> properties = new HashMap<String, String>();

            properties.put(KPIConfigurationConstants.REFRESH_TIME,
                    kpiConfigurationProperties.getDefaultValueForRefreshTimeInDisplayFormat(metaReader));

            properties.put(KPIConfigurationConstants.REFRESH_RATE,
                    kpiConfigurationProperties.getDefaultValueForRefreshRateInDisplayFormat(metaReader));

            setProperties(properties, getPropertiesState());
        }

    }

    public void onUpdate() {
        final IPropertiesState propertiesState = getPropertiesState();
        final Map<String, String> existingSettings = getUserConfiguredSettings();
        final Map<String, String> newSettings = createPersistableProperties();
        existingSettings.putAll(newSettings);
        setProperties(existingSettings, propertiesState);
        eventBus.fireEvent(new KPIRefreshRateUpdateEvent());
    }

    private Map<String, String> createPersistableProperties() {
        final Map<String, String> persistableProperties = new HashMap<String, String>();
        for (final String key : userTimeDetails.keySet()) {
            persistableProperties.put(key, userTimeDetails.get(key).toString());
        }
        return persistableProperties;
    }

    private void setProperties(final Map<String, String> kpiNotificationProperties,
            final IPropertiesState propertiesState) {
        propertiesState.setProperties(kpiNotificationProperties);
        //update user preferences in JNDI
        userPreferencesHelper.setState(KPI_NOTIFICATION_STORAGE_KEY, IPropertiesState.class, propertiesState);
    }

    IPropertiesState getPropertiesState() {
        return jsonAutoBeanDataFactory.propertiesState().as();
    }

    public Map<String, String> getUserConfiguredSettings() {
        final IPropertiesState properties = userPreferencesHelper.getStateById(KPI_NOTIFICATION_STORAGE_KEY,
                IPropertiesState.class);
        if (properties == null) {
            return new HashMap<String, String>();
        }
        return properties.getProperties();
    }

    public Map<String, String> getInvalidEntries() {
        return new KPIConfigurationProperties().validateSelection(metaReader, userTimeDetails);
    }

    public void closeDialog() {
        dialog.hide();
    }

    public void setUserTimeDetails(final String key, final TimeInfoDataType userTimeDetailsForOneOption) {
        userTimeDetails.put(key, userTimeDetailsForOneOption);
    }

    public String validateEntry(final String key, final TimeInfoDataType userSelection) {
        return new KPIConfigurationProperties().validateEntry(metaReader, key, userSelection);
    }

    public boolean haveUserTimeDetailsBeenEntered() {
        return userTimeDetails.size() > 0;
    }

    @Override
    public void onHide(final KPIConfigurationDialogHideEvent hideEvent) {
        if (hideEvent.getSource().equals(getDialog())) {
            getView().resetCorrectValues();
        }
    }

}
