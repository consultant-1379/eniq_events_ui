/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.kpi;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.common.client.preferences.IUserPreferencesHelper;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.common.client.datatype.IPropertiesState;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.JsonAutoBeanDataFactory;
import com.ericsson.eniq.events.ui.client.datatype.kpi.KPIConfigurationPanelDataType;
import com.ericsson.eniq.events.ui.client.datatype.kpi.KPIConfigurationPanelElement;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author eemecoy
 *
 */
public class KPIConfigurationPresenterTest extends TestEniqEventsUI {

    IPropertiesState mockedPropertiesState;

    IMetaReader metaReader;

    @Before
    public void setupTest() {
        final KPIConfigurationView mockedView = createAndIgnore(KPIConfigurationView.class);
        mockedPropertiesState = context.mock(IPropertiesState.class);
        setUpMocksForPersistence();
        metaReader = context.mock(IMetaReader.class);
        setUpMetaData();
        final Map<String, String> expectedValues = getExpectedValues();
        expectCallToPersistDefaultValues(expectedValues);
        final IUserPreferencesHelper userPreferencesHelper = context.mock(IUserPreferencesHelper.class);
        setExpectationsOnUserPreferencesHelper(userPreferencesHelper);
        new StubbedKPIConfigurationPresenter(mockedView, null, null, userPreferencesHelper, metaReader);
    }

    @Test
    public void emptyTest() {

    }

    private Map<String, String> getExpectedValues() {
        final Map<String, String> expectedValues = new HashMap<String, String>();
        expectedValues.put(KPIConfigurationConstants.REFRESH_TIME, "30 minutes");
        expectedValues.put(KPIConfigurationConstants.REFRESH_RATE, "7 minutes");
        return expectedValues;
    }

    private void setUpMetaData() {
        final KPIConfigurationPanelDataType metaData = new KPIConfigurationPanelDataType();
        final KPIConfigurationPanelElement windowTimePeriodMetaData = new KPIConfigurationPanelElement();
        windowTimePeriodMetaData.setDefaultValue("30");
        windowTimePeriodMetaData.setMinValue(5);
        windowTimePeriodMetaData.setMaxValue(354);
        metaData.setRefreshTime(windowTimePeriodMetaData);
        final KPIConfigurationPanelElement refreshRateMetaData = new KPIConfigurationPanelElement();
        refreshRateMetaData.setDefaultValue("7");
        refreshRateMetaData.setMinValue(7);
        refreshRateMetaData.setMaxValue(1439);
        metaData.setRefreshRate(refreshRateMetaData);
        expectMetaDataCall(metaData);
    }

    private void setExpectationsOnUserPreferencesHelper(final IUserPreferencesHelper userPreferencesHelper) {
        context.checking(new Expectations() {
            {
                one(userPreferencesHelper).setState(KPIConfigurationConstants.KPI_NOTIFICATION_STORAGE_KEY,
                        IPropertiesState.class, mockedPropertiesState);
                one(userPreferencesHelper).getStateById(KPIConfigurationConstants.KPI_NOTIFICATION_STORAGE_KEY,
                        IPropertiesState.class);
                will(returnValue(mockedPropertiesState));
            }

        });

    }

    private void expectCallToPersistDefaultValues(final Map<String, String> properties) {
        context.checking(new Expectations() {
            {
                one(mockedPropertiesState).setProperties(properties);

            }

        });

    }

    private void expectMetaDataCall(final KPIConfigurationPanelDataType metaData) {
        context.checking(new Expectations() {
            {
                allowing(metaReader).getKPIConfigurationPanelMetaData();
                will(returnValue(metaData));

            }

        });

    }

    private void setUpMocksForPersistence() {
        context.checking(new Expectations() {
            {
                one(mockedPropertiesState);
                will(returnValue(null));

            }

        });
    }

    class StubbedKPIConfigurationPresenter extends KPIConfigurationPresenter {

        /**
         * @param view
         * @param eventBus
         * @param jsonAutoBeanDataFactory
         * @param userPreferencesHelper
         * @param metaReader
         */
        public StubbedKPIConfigurationPresenter(final KPIConfigurationView view, final EventBus eventBus,
                final JsonAutoBeanDataFactory jsonAutoBeanDataFactory,
                final IUserPreferencesHelper userPreferencesHelper, final IMetaReader metaReader) {
            super(view, eventBus, jsonAutoBeanDataFactory, userPreferencesHelper, metaReader);
        }

        /* (non-Javadoc)
         * @see com.ericsson.eniq.events.ui.client.kpi.KPIConfigurationPresenter#getPropertiesState()
         */
        @Override
        IPropertiesState getPropertiesState() {
            return mockedPropertiesState;
        }

    }

}
