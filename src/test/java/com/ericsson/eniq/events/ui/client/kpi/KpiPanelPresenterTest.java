/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.kpi;

import com.ericsson.eniq.events.common.client.preferences.IUserPreferencesHelper;
import com.ericsson.eniq.events.common.client.service.IDataService;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

/**
 * @author eemecoy
 *
 */
public class KpiPanelPresenterTest extends TestEniqEventsUI {

    private StubbedKpiPanelPresenter kpiPanelPresenter;

    KPIConfigurationPresenter mockedKpiConfigurationPresenter;
    KPIConfigurationProperties mockedProperties;

    int configuredRefreshRate;

    Timer refreshTimer;

    private final int initialRefreshRate = 3;

    @Before
    public void setupForTest() {
        mockedProperties = context.mock(KPIConfigurationProperties.class);
        mockedKpiConfigurationPresenter = context.mock(KPIConfigurationPresenter.class);
        kpiPanelPresenter = new StubbedKpiPanelPresenter(null, mockedEventBus, null,null, null, null);
        refreshTimer = context.mock(Timer.class);
        kpiPanelPresenter.refreshTimer = refreshTimer;
        configuredRefreshRate = initialRefreshRate;
    }

    @Test
    public void testRefreshTimerNotRescheduledIfNoChangeMadeToRefreshRateConfiguration() {
        expectScheduleRefreshTimer(initialRefreshRate);
        kpiPanelPresenter.getKPIRefreshRateUpdateHandler().onRefreshRateUpdate();
        kpiPanelPresenter.getKPIRefreshRateUpdateHandler().onRefreshRateUpdate();
    }

    @Test
    public void testRefreshTimerRescheduledIfChangeMadeToRefreshRateConfiguration() {
        expectScheduleRefreshTimer(initialRefreshRate);
        kpiPanelPresenter.getKPIRefreshRateUpdateHandler().onRefreshRateUpdate();
        final int newRefreshRate = 23;
        configuredRefreshRate = newRefreshRate;
        expectScheduleRefreshTimer(newRefreshRate);
        kpiPanelPresenter.getKPIRefreshRateUpdateHandler().onRefreshRateUpdate();
    }

    private void expectScheduleRefreshTimer(final int period) {
        context.checking(new Expectations() {
            {
                one(refreshTimer).scheduleRepeating(period);
            }
        });

    }

    class StubbedKpiPanelPresenter extends KpiPanelPresenter {
        /**
         * @param view
         * @param eventBus
         * @param userPreferencesHelper
         */
        public StubbedKpiPanelPresenter(final KpiPanelView view, final EventBus eventBus,
                final IUserPreferencesHelper userPreferencesHelper, final IMetaReader metaReader,
                final IDataService dataService, KPIConfigurationPresenter kpiConfigurationPresenter) {
            super(view, eventBus, userPreferencesHelper, metaReader, dataService, kpiConfigurationPresenter,
                    null, null);
        }

        /* (non-Javadoc)
         * @see com.ericsson.eniq.events.ui.client.kpi.KpiPanelPresenter#onBind()
         */
        @Override
        protected void onBind() {
        }

        /* (non-Javadoc)
         * @see com.ericsson.eniq.events.ui.client.kpi.KpiPanelPresenter#getConfiguredRefreshRateInMilliseconds()
         */
        @Override
        int getConfiguredRefreshRateInMilliseconds() {
            return configuredRefreshRate;
        }

    }

}
