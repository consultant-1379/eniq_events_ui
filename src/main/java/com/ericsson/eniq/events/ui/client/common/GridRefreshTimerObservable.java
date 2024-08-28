/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

import java.util.HashSet;
import java.util.Set;

import com.google.web.bindery.event.shared.EventBus;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.events.RefreshWindowEvent;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.google.gwt.user.client.Timer;

/**
 * The ranking grids may need to support auto-refresh 
 * based on a timer. This class will hold that timer and 
 * notify its observers when the timer interval is reached to
 * refresh themselves periodically.
 * 
 * @author eeicmsy
 * @since March 2010
 *
 */
public class GridRefreshTimerObservable {

    private final IMetaReader metaReader = MainEntryPoint.getInjector().getMetaReader();

    private static GridRefreshTimerObservable instance;

    private final EventBus eventBus;

    private final Set<MultipleInstanceWinId> registeredGrids = new HashSet<MultipleInstanceWinId>();

    /**
     * Fetch the GridRefreshTimerObservable
     * 
     * @param bus   using the EventBus to relay refresh call to observers 
     *              (since refresh toolbars already use this mechanism we hit the 
     *               same code that way -- so no new observable interface here 
     */
    public static GridRefreshTimerObservable getInstance(final EventBus bus) {
        if (instance == null) {
            instance = new GridRefreshTimerObservable(bus);
        }
        return instance;
    }

    /*
     * (singleton pattern - access adjusted for junit) creating timer  
     * @param bus  the default event bus 
     */
    GridRefreshTimerObservable(final EventBus bus) {
        this.eventBus = bus;
        if (isAutoRefreshOn()) { // NOPMD by eeicmsy on 22/03/10 22:29
            createRankingTimer(); // NOPMD by eeicmsy on 22/03/10 22:30
        }
    }

    /* extract for junit */
    boolean isAutoRefreshOn() {
        return metaReader.getIsAutoRefreshOn();
    }

    /* extract for junit */
    RankingTimer createRankingTimer() {
        return new RankingTimer();
    }

    /**
     * Register this grid (ranking grid) as being interested in 
     * receiving notifications to refresh itself.
     * Will only perform any actions if ranking feature is turned on
     *  
     * @see isAutoRefreshFeatureTurnedOn
     * @param multWinId  window identification from metadata (containing tab id etc)
     *                   (ranking grids will not care about search field)
     *                   
     * @return  true if regisitored
     */
    public boolean registorGrid(final MultipleInstanceWinId multWinId) {
        return registeredGrids.add(multWinId);

    }

    /**
     * Method which should be called in grid shutdown
     * @param multWinId  window identification from metadata (containing tab id etc)
     *                   (ranking grids will not care about search field)
     * @return  true if unregistored else fail silently
     */
    public boolean removeGrid(final MultipleInstanceWinId multWinId) {
        return registeredGrids.remove(multWinId);

    }

    /**
     * GWT Timer which will call for registered grids to 
     * refresh themselves
     */
    class RankingTimer extends Timer {

        public RankingTimer() {
            scheduleRepeating(getRankingTimerInterval()); // NOPMD by eeicmsy on 22/03/10 22:29
        }

        /* extract for junit */
        int getRankingTimerInterval() {
            return metaReader.getRankingTimerInterval();
        }

        @Override
        public void run() {
            /* update listeners called from clock interval expiration to refresh grids */

            for (final MultipleInstanceWinId key : registeredGrids) {

                eventBus.fireEvent(new RefreshWindowEvent(key)); // NOPMD by eeicmsy on 19/03/10 19:58
            }

        }
    }
}
