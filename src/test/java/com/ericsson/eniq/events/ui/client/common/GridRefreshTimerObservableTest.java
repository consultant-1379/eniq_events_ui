/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.GridRefreshTimerObservable.RankingTimer;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.events.RefreshWindowEvent;
import com.google.web.bindery.event.shared.EventBus;
import org.jmock.Expectations;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author eeicmsy
 * @since March 2010
 *
 */
public class GridRefreshTimerObservableTest extends TestEniqEventsUI {

    private GridRefreshTimerObservable objectToTest;

    private final String tabId = "tabID";

    private final String testWinID1 = "testWinID1";

    boolean isRefreshFeatureOn;

    int refreshTimerIntervalMS = 0;

    @Test
    public void canAddAndRemoveGrids() {
        isRefreshFeatureOn = false;
        objectToTest = getStubbedGridRefreshTimerObservable();
        assertEquals("Can registor a window", true, objectToTest.registorGrid(createMultiWinId(tabId, testWinID1)));
        assertEquals("Can unregistor a window", true, objectToTest.removeGrid(createMultiWinId(tabId, testWinID1)));
    }

    @Test
    public void rankingTimerSchedulesWhenTurnFeatureOn() {
        isRefreshFeatureOn = true;
        refreshTimerIntervalMS = 1;
        objectToTest = getStubbedGridRefreshTimerObservable();
        assertEquals("Can registor a window", true, objectToTest.registorGrid(createMultiWinId(tabId, testWinID1)));
        assertEquals("Attempted to schedule timer", true,
                ((StubbedGridRefreshTimerObservable) objectToTest).attemptedToSchedule);
    }

    @Test
    public void timerSendsNoNotificationsWhenNoWindowRegistored() {
        isRefreshFeatureOn = true;
        refreshTimerIntervalMS = 1;
        objectToTest = getStubbedGridRefreshTimerObservable();
        final RankingTimer timerToTest = objectToTest.createRankingTimer();

        context.checking(new Expectations() {
            {
                never(mockedEventBus).fireEvent(with(any(RefreshWindowEvent.class)));
            }
        });

        timerToTest.run();
    }

    @Test
    public void timerSendsOneNotificationWhenOneWindowRegistored() {
        isRefreshFeatureOn = true;
        refreshTimerIntervalMS = 1;

        objectToTest = getStubbedGridRefreshTimerObservable();

        objectToTest.registorGrid(createMultiWinId(tabId, testWinID1));
        final RankingTimer timerToTest = objectToTest.createRankingTimer();

        context.checking(new Expectations() {
            {
                one(mockedEventBus).fireEvent(with(any(RefreshWindowEvent.class)));
            }
        });

        timerToTest.run();
    }

    private MultipleInstanceWinId createMultiWinId(final String tabId, final String winId) {
        return new MultipleInstanceWinId(tabId, winId/*, null*/);
    }

    @Test
    public void timerSendsMultipleNotificationPerWindowRegistored() {
        isRefreshFeatureOn = true;
        refreshTimerIntervalMS = 1;

        objectToTest = getStubbedGridRefreshTimerObservable();

        objectToTest.registorGrid(createMultiWinId(tabId, testWinID1));
        objectToTest.registorGrid(createMultiWinId(tabId, "testWinID2"));
        objectToTest.registorGrid(createMultiWinId(tabId, "testWinID3"));
        objectToTest.registorGrid(createMultiWinId(tabId, "testWinID4"));

        final RankingTimer timerToTest = objectToTest.createRankingTimer();

        context.checking(new Expectations() {
            {
                exactly(4).of(mockedEventBus).fireEvent(with(any(RefreshWindowEvent.class)));
            }
        });

        timerToTest.run();
    }

    //
    // private methods and stubs
    //

    private GridRefreshTimerObservable getStubbedGridRefreshTimerObservable() {
        return new StubbedGridRefreshTimerObservable(mockedEventBus);
    }

    /* stub our inner timer as can not handle GWT javascript */
    private class StubbedGridRefreshTimerObservable extends GridRefreshTimerObservable {

        boolean attemptedToSchedule;

        /**
         * @param bus
         */
        StubbedGridRefreshTimerObservable(final EventBus bus) {
            super(bus);

        }

        @Override
        boolean isAutoRefreshOn() {
            return isRefreshFeatureOn;
        }

        @Override
        GridRefreshTimerObservable.RankingTimer createRankingTimer() {
            return new StubbedRankingTimer();
        }

        // innner class in stub
        class StubbedRankingTimer extends GridRefreshTimerObservable.RankingTimer {
            @Override
            int getRankingTimerInterval() {
                return refreshTimerIntervalMS;
            }

            @Override
            public void scheduleRepeating(final int inerval) { // real land GWT would fall over
                attemptedToSchedule = true;

            }

        }

    }

}
