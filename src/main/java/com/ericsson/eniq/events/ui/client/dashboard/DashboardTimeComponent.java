/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.dashboard;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.DashBoardDataType;
import com.ericsson.eniq.events.ui.client.events.TimeParameterValueChangeEvent;
import com.extjs.gxt.ui.client.event.DatePickerEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ericsson.eniq.events.common.client.CommonConstants.DateField_Displayed_Date_Format;

/**
 * Date Component to appear on Menu Task bar of a dashboard
 * AG - Aggregation time after midnite (typically 2 am)
 * AGF -Estimate Aggregation time finish 
 * Refresh - ideal refresh time - to stagger over a period for all clients 
 * 
 * <pre>
 *               20th Nov               ||      21st Nov                 ||       22nd Nov [TODAY]
 *      |-------------------------------||------------------------------||------------------------------||
 *      0 |      |   |                  0   |   |    |                   0    |  |     |                 0
 *        AG   AGF   Refresh                AG  AGF  Refresh                  AG  AGF  Refresh 
 *      
 *  (current)Requirement : If it is now 22nd Nov:
 *  
 *  
 *  x) On 22nd After REFRESH time  :  Enable  21st Nov  as max - send time 0:00 to 0:00 for porlet metadata dateFrom (TODAY still disabled)
 *  x) SEND TIME is dateTo 22nd Nov Midnite
 *  x) On 22nd Before REFRESH time :  Enable  20th Nov - send time 0:00 to 0:00  (21st and 22nd Nov still disabled)
 *  x) SEND TIME is dateTo 21nd Nov Midnite
 *  x) Put default start up time to 21th 00 or 22st 000 Nov for  above 
 
 *  x) On refresh time, update date displayed (ONLY IF) user was previously looking at Latest Enabled date
 *  x) On date select behave as refresh
 *  x) This Time to refresh all - must be staggered as many clients - stagger over say 15 mins (Random)
 *  x) Time to display on dashboard title bars : one day (the last day - from the time selected e.g. Nov 1st 00 to Nov 2nd 00:00
 *  x) Read glassfish setting to fetch  "Refresh" would  be better (ENIQ_EVENTS_MINS_FROM_MIDNITE_UI_START_DASHBOARD_REFRESH)
 *        
 * </pre>
 * 
 * @author eeicmsy
 * @since Nov 2011
 *
 */
public class DashboardTimeComponent extends DateField {

    private static final Logger LOGGER = Logger.getLogger(DashboardTimeComponent.class.getName());

    /* If not finding time for some reason do not want UI to fall over 
     * (tested this when different than default we actaully want in ApplicationConfigManagerImpl)*/
    private final static int DEFAULT_UI_START_DASHBOARD_REFRESH = 60 * 6;

    /* mins from midnite(from glassfish) after which we kick off refresh
     * and enabling of "yesterday" on calendar */
    private final static int ENIQ_EVENTS_DASHBOARD_REFRESH_TIME_MINS = getStartRefreshMins();

    private final static long REFRESH_MS_FROM_MIDNITE = ENIQ_EVENTS_DASHBOARD_REFRESH_TIME_MINS * CommonConstants.MS_IN_MIN;

    /* default day back to allow selection on calendar (if somehow not present in meta data already) */
    private final static int DEFAULT_ALLOWED_DAYS_BACK = 1095; // 3 years

    public final static long DEFAULT_ALLOWED_DAYS_BACK_MS = DEFAULT_ALLOWED_DAYS_BACK * CommonConstants.DAY_IN_MILLISEC;

    private final static int ONE_DAY = 1;

    private final static int TWO_DAYS = 2;

    /*
     * timeTo and timeFrom settings to pass to server
     */
    private final static Time DEFAULT_TIME_FOR_REQUESTS = new Time(0, 0);

    /* staggering refresh range so all UIs do not kick off at same time, 
     * e.g. if refresh time is 6 am, we will kick off the refresh at some 
     * minute interval between 6:00 and 6:15   (depending on this constant value)
     */
    private final static int TIME_RANGE_TO_STAGGER_REFRESH_OVER_MINS = 15;

    private final TimeInfoDataType userSelection;

    private final MultipleInstanceWinId multiWinID;

    private final EventBus eventBus;

    private final DashBoardDataType dashboardData;

    private Date maxDateToEnable;

    /**
     * Time component for dashboard tabs which fires event to 
     * dashboard window
     *
     * @param tabId         - tab id
     * @param winId         - window id for window owning dashboard porlets
     * @param dashboardData - extra information for ths dashboard  (e.g. time to go back)
     * @param eventBus      - event bus
     */
    public DashboardTimeComponent(final String tabId, final String winId, final DashBoardDataType dashboardData,
            final EventBus eventBus) {

        this.multiWinID = createMultipleInstanceWinId(tabId, winId);
        this.userSelection = new TimeInfoDataType();
        this.dashboardData = dashboardData;
        this.eventBus = eventBus;

        init();

        maxDateToEnable = getMaxDateToEnable();

        updateUserSelectionDates(maxDateToEnable);

        userSelection.timeTo = DEFAULT_TIME_FOR_REQUESTS;
        userSelection.timeFrom = DEFAULT_TIME_FOR_REQUESTS;

        updateForMaxDateToEnable(maxDateToEnable);

        createRefreshTimeHandlingTimer(); // NOPMD 

        setValue(maxDateToEnable); // default start up

        getDatePicker().addListener(Events.Select, new DateSelectionListener()); // NOPMD
    }

    /**
     * Utility returning current time data
     * Used if opening a window and need to be refreshed with latest time
     * data  (if window was already open will have received time updata event)
     * 
     * @return  current time selection in dashboard time component
     */
    public TimeInfoDataType getCurrentDashBoardTimeData() {
        return userSelection;
    }

    @Override
    public DatePicker getDatePicker() {
        final DatePicker datePicker = super.getDatePicker();
        datePicker.addStyleName("dashboardDatePicker");
        return datePicker;
    }

    /////////////////////////////////////////////////////////////////////////
    ////////////   private methods
    /////////////////////////////////////////////////////////////////////////

    /*
     * fire update to all porlets with current selection
     */
    private void fireCurrentSelection() {
        eventBus.fireEvent(new TimeParameterValueChangeEvent(multiWinID, userSelection));
    }

    /*
     * creates and returns a DateField Control
     */
    private void init() {
        setPropertyEditor(new DateTimePropertyEditor(DateField_Displayed_Date_Format));
        setAllowBlank(false);
        setAutoValidate(true);
    }

    private MultipleInstanceWinId createMultipleInstanceWinId(final String tabId, final String winId) {
        return new MultipleInstanceWinId(tabId, winId);
    }

    RefreshTimeHandlingDashBoardTimer createRefreshTimeHandlingTimer() {
        return new RefreshTimeHandlingDashBoardTimer();
    }

    /*
     * Set corresponding dateFrom when set dateTo. So can call a #toString on time for
     * dashboard title.
     * Note porlet window itself can change this again depending on meta data
     * (when portlet DATE_FROM being set in meta data)
     * But initial setting (and whole dashboard default) is one day range)
     * 
     * NOTE  BUMPING ALL BY A DAY because times are midnite 
     * The calendar has say 22nd disabled (but we are giving a dateTo as to 00:00 on 22nd  (midnite is last night)
     * 
     * @param dateTo  (later than dateFrom)
     */
    private void updateUserSelectionDates(final Date dateTo) {

        // BUMPING ALL BY A DAY FOR SERVER CALLS AND DASHBOARD TITLE (not for enabling)
        // Because we are at MidNite - the dateTo selection of the 21st 
        // (is really same as 22nd at night)
        // 24 hours:

        userSelection.dateFrom = dateTo;
        userSelection.dateTo = getDateBack(dateTo, -ONE_DAY); // minus

        //XXX userSelection.dateTo = dateTo;
        //XXX userSelection.dateFrom = this.getDateBack(userSelection.dateTo, ONE_DAY);
    }

    /* 
     * Call with in timer to stagger refresh over a time range
     * Implements refresh to update calendar enabled dates and
     * make call tell all porlets to update themselves (scheduled) 
     */
    private void handleRefreshTimeUpdate() {

        final Date maxAtRefreshTime = getDateBack(new Date(), ONE_DAY);
        updateForMaxDateToEnable(maxAtRefreshTime);

        /* currently looking at the previous max means must update and refresh all porlets, 
         * other wise this will happen when user manually makes selection
         */
        if (isCurrentlyLookingAtOrigionalLatest()) {
            setValue(maxAtRefreshTime);
            updateUserSelectionDates(maxAtRefreshTime);
            fireCurrentSelection();
        }
        // set after use #isCurrentlyLookingAtOrigionalLatest
        maxDateToEnable = maxAtRefreshTime;
    }

    private boolean isCurrentlyLookingAtOrigionalLatest() {
        final Date lookingAt = roundToLastMidNite(getValue());
        lookingAt.setHours(1); // (so no millisec issues)
        return (maxDateToEnable.getTime() - lookingAt.getTime()) <= CommonConstants.DAY_IN_MILLISEC;

    }

    /*
     * <pre>
     *               20th Nov               ||     21st Nov                 ||       22nd  Nov
     *      |-------------------------------||------------------------------||------------------------------||
     *      0 |      |   |                  0   |   |    |                   0    |  |     |                 0
     *        AG   AGF   Refresh                AG  AGF  Refresh                  AG  AGF  Refresh 
     *      
     *  If it is now 22nd Nov:
     *
     *  x) On 22nd After REFRESH time  :  Enable  21st Nov as latest
     *  x) On 22nd Before REFRESH time :  Enable  20th Nov as latest 
     * </pre>
    */
    private Date getMaxDateToEnable() {
        final Date today = new Date();
        return isTodaysRefreshTimePassed(today) ? getDateBack(today, ONE_DAY) : getDateBack(today, TWO_DAYS);
    }

    /*
     * @param from       start date (day)
     * @param daysBack   number of days to deduct
     * @return           new date will date rolled back by daysBack          
     */
    private Date getDateBack(final Date from, final int daysBack) {
        final long daysBackMS = daysBack * CommonConstants.DAY_IN_MILLISEC;
        final long newTime = from.getTime() - daysBackMS;
        return new Date(newTime);

    }

    /*
     * @param today   - full current time 
     * @return        - true if refresh time has passed on today
     */
    private boolean isTodaysRefreshTimePassed(final Date today) {

        final long fullTodayTime = today.getTime();

        final Date lastNite = roundToLastMidNite(today); // (currupts today)
        final long lastMidNiteTime = lastNite.getTime();
        final long todaysRefreshTime = lastMidNiteTime + REFRESH_MS_FROM_MIDNITE;

        return fullTodayTime > todaysRefreshTime;

    }

    /* when have dashboard can let max back configured via meta data */
    private Date getAllowedMaxBack(final Date dateTo) {
        return new Date(dateTo.getTime() - dashboardData.getAllowedDaysBackMS());
    }

    private void updateForMaxDateToEnable(final Date maxDateToEnable) {
        // depends on time of day launched UI
        setMaxValue(maxDateToEnable);
        final Date minValue = getAllowedMaxBack(maxDateToEnable);
        setMinValue(minValue);

    }

    /*
     * "currupts" the passed date
     * @param date  : date to change (rounf to midnite)
     */
    private final Date roundToLastMidNite(final Date date) {
        /* not importing java.util.Calendar project */
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        return date;
    }

    /*
     * Get time after midnite (when aggregation finished) when 
     * want to start scheuduling (and update enabled dates on calendar)
     */
    private static int getStartRefreshMins() {
        try {
            /* should really have found this - 
             * or at very least get the ApplicationConfigManagerImpl default gets in */
            return getNativeStartRefreshMins();

        } catch (final Exception e) {
            // This is a fault if get here meaning glassfish codereading  is failing - see ApplicationConfigManagerImpl*/
            LOGGER.log(Level.WARNING,
                    "failing to read dashboard refresh mins from midnite, using DashboardTimeComponent default "
                            + DEFAULT_UI_START_DASHBOARD_REFRESH, e);

            return DEFAULT_UI_START_DASHBOARD_REFRESH;
        }
    }

    // read mins from midnite in via glassfish (usaully 180)
    private static native int getNativeStartRefreshMins() /*-{
		return $wnd.dashboardRefreshFromMidniteMin;
    }-*/;

    /////////////////////////////////////////////////////////////////////////////
    /////////  private classes 
    /////////////////////////////////////////////////////////////////////////////

    /*
     * Select data fires event for portlets to update 
     */
    private class DateSelectionListener implements Listener<DatePickerEvent> {

        @Override
        public void handleEvent(final DatePickerEvent be) {

            updateUserSelectionDates(DashboardTimeComponent.this.getValue());
            fireCurrentSelection();
        }
    }

    /*
     * Timer initialised at the refresh time, i.e. 
     * to stagger refresh when reach refresh time.
     * 
     * For example if say Refresh time is 6 a.m, then 
     * kick off the actual refresh any time (on the minute) between 6.00 and 6:15 range (range as
     * per TIME_RANGE_TO_STAGGER_REFRESH_OVER_MINS) 
     */
    private class RandomStaggerRefreshTimer extends Timer {

        public RandomStaggerRefreshTimer() {
            schedule(getRandomDelayMS());
        }

        @Override
        public void run() {
            handleRefreshTimeUpdate();
        }

        /*
         * generate random min between 0 and 15 inclusive (or as constant)
         * @return  random milliseconds (on the minute) 
         */
        private int getRandomDelayMS() {
            final int minsToWait = (int) Math.floor(Math.random() * (TIME_RANGE_TO_STAGGER_REFRESH_OVER_MINS + 1));

            Info.display("Info", "Dashboard scheduled to update in " + minsToWait + " minutes..");

            LOGGER.log(Level.INFO, "Waiting " + minsToWait
                    + " minutes before starting any refresh work in order that clients refresh at different times");

            return ((int) (minsToWait * CommonConstants.MS_IN_MIN));
        }

    }

    /*
     * Timer to kick off at refresh time. At this point we must start staggering
     * (for all UIs) porlet refreshs
     *
     */
    private class RefreshTimeHandlingDashBoardTimer extends Timer {

        public RefreshTimeHandlingDashBoardTimer() {
            /* initial synch to next refresh time before move to 24 hour updates */
            scheduleRepeating((int) getTimeToNextRefreshMilliSecs()); // NOPMD by eeicmsy on 22/03/10 22:29
        }

        @Override
        public void run() {
            /* synched so can now wait 24 hours */
            scheduleRepeating((int) CommonConstants.DAY_IN_MILLISEC);

            new RandomStaggerRefreshTimer();

        }

        /*
         * Initial symching to next refresh time, 
         * after which we know we can wait 24 hours for next one
         * @return  time to next refresh time from time launched UI
         */
        private long getTimeToNextRefreshMilliSecs() {

            final Date nowDate = new Date();
            final long nowTime = nowDate.getTime();

            final long timeSinceLastRefreshTime = nowTime - getLastRefreshTime();
            return CommonConstants.DAY_IN_MILLISEC - timeSinceLastRefreshTime;
        }

        private long getLastRefreshTime() {

            final Date nowDate = new Date();
            roundToLastMidNite(nowDate); // changes nowDate
            final long lastMidNiteTime = nowDate.getTime();

            if (isTodaysRefreshTimePassed(new Date())) {
                return (lastMidNiteTime + REFRESH_MS_FROM_MIDNITE);
            }
            /* refresh that would have occurred on previous day */
            return (lastMidNiteTime - CommonConstants.DAY_IN_MILLISEC + REFRESH_MS_FROM_MIDNITE);
        }
    }
}
