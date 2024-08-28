/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget;

import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.BaseToolBar;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.main.AbstractWindowLauncher;
import com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay;
import com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController;
import com.extjs.gxt.ui.client.core.El;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Interface extracted to window 
 * functionality between our grids and our charts, i.e. 
 * a more generic interface to pass as a view reference for 
 * toolbar type functionality instead of 
 * say IEventGridView which would be specific to grids. 
 * 
 * @author eeicmsy
 * @since April 2010
 * 
 * @see com.ericsson.eniq.events.ui.client.common.widget.IEventGridView
 *
 */
public interface IExtendedWidgetDisplay extends WidgetDisplay {

    /**
     * Enable or disable button in window toolbar.
     * Fail silently if the button does not exist on current grid
     * 
     * @param buttonID    id set in metadata for button in toolbar
     * @param isEnabled   true to enable, false to disable
     */
    void setToolbarButtonEnabled(final String buttonID, final boolean isEnabled);

    /**
     * Utility fetching the custom toolbar used for the grid
     * This is the UPPER toolbar 
     * @return  the custom toolbar used for the grid 
     */
    BaseToolBar getWindowToolbar();

    /**
     * Utility so can add the grid/chart to the display widget 
     * (grid/chart will repaint itself)
     * @param child grid being added
     */
    void addWidget(Widget child);

    /**
     * Utility fetching parent window of grid/chart
     * @return  parent window of grid window
     */
    BaseWindow getParentWindow();

    /**
     * Utility returning the menu task bar on the
     * tab that the window is sitting in
     * @return  the Menu Task Bar
     */
    IWorkspaceController getWorkspaceController();

    /**
     * Utility fetching MetaMenuItem information used to launch grid ro chart
     * @return  meta menu item data associated with the grid launch
     */
    MetaMenuItem getViewSettings();

    /**
     * Facility to append the drill down window title to the
     * original title on the window from a view reference
     */
    void upDateDrillWindowTitle(final String value);

    /**
     * Facility to update the title that is displayed
     * on this Parent Window based on the title that
     * is associated with the current widget within the window!
     * @param value title
     */
    void setWidgetTitle(final String value);

    /**
     * Set the time parameters that are
     * associated with this window
     * (store user selected time)
     * 
     * TODO (change this later to use presenter time only)
     * 
     * @param time - time dataType for the window
     */
    void updateTime(final TimeInfoDataType time);

    /**
     * Utility to return current user time selection for the window
     * @return dataType containing the time parameters provided by end user
     */
    TimeInfoDataType getTimeData();

    /**
     * Utility to return time stamp information on a window 
     * (to reproduce following navigation bread crumb change)
     * 
     * @return time stamp label, indicating last time this window data 
     *         was refreshed with the server
     */
    String getLastRefreshTimeStamp();

    /**
     * 
     * This is the "View's version" of widget params
     * (which we know has not been cleared on success call) 
     * 
     * Utility to return url parameters specific to
     * this widget that are not contained in metadata
     * e.g. values passed to services as part of a hyperlink click 
     * 
     * @return sample : "?time=1440&type=IMSI&imsi=460030001929998&display=chart&tzOffset=+0000&maxRows=5000"
     */
    String getWidgetSpecificURLParams();

    /**
     * (cheap and dirty)
     * Widget specific parameters that window was launched with which 
     * have not been cleared. Generally in design today widget paramters 
     * need to be cleared on window population to avoid them being added to 
     * the next call.
     * 
     * Widget params used for 
     * 1) chart drilldown
     * 2) chart view menu changes
     * 3) Chart with breadsrumb
     * 3) Recur error window row changes
     * 4) Breadcrumb on grids
     * 6) Grouping grids
     * 
     * all in slightly different way - so safest at this point to 
     * isolate for difference required for CSV
     *
     * For CSV case (CSV button press) want to get those parameters used 
     * to launch the window (prior to clearing )
     * 
     * @return sample : "?time=1440&type=IMSI&imsi=460030001929998&display=chart&tzOffset=+0000&maxRows=5000"
     */
    String getWidgetSpecificURLParamsForCSV();

    /**
     * This is the "View's version" of widget params
     * (which we know has not been cleared on success call).
     * The "Design" actually needs this at the moment - 
     * this is expected to be a full URL parameter  
     * 
     * Sample:
     * "?time=1440&type=IMSI&imsi=460030001929998&display=chart&tzOffset=+0000&maxRows=5000"
     * 
     * @param value  = full URL paramters for current screen (e.g. 
     *                 to be available for CSV etc).
     */
    void updateWidgetSpecificURLParams(final String value);

    /**
     * Utlity to set timestamp for last server call
     * (well reset it based on breadcrumb selected window change)
     * @param timeStamp  time to display (on lower toolbar)
     */
    void updateLastRefreshedTimeStamp(final String timeStamp);

    /**
     * Update time from presenter (hack - know presenter already updated)
     * @param time       - time dataType for the window
     */
    void updateTimeFromPresenter(final TimeInfoDataType time);

    /**
     * Give the presenter reference to the view
     * @param presenter  presenter reference
     */
    void setPresenter(final IBaseWindowPresenter presenter);

    /**
     * Method to ensure the new window launched fits within the constrain area.
     * In scenarios where, constrain area is less than that of the window being launched.
     */
    void fitIntoContainer();

    /**
     * Return presenter exposed methods from the view class
     * (e.g. presenter owns search data, etc)
     * 
     * 
     * @return  presenter reference
     */
    IBaseWindowPresenter getPresenter();

    /**
     * Display should be capable of returning its toggle 
     * window equivalenet view launcher for when user 
     * presses to toggle view from a graph to a grid and 
     * vice versa. For example:
     * <pre>
     * MultiInstanceChartWindowView --> MultiInstanceGridLauncher
     * MultiInstanceGridView  --> MultiInstanceChartLauncher
     * ChartWindowView --> GridLauncher
     * EventGridView --> ChartLauncher
     * </pre>
     * 
     * @param eventBus   MVP pattern event bus required to create presenters
     * @return           Toggle window launcher
     */
    AbstractWindowLauncher getToggleWindowLauncher(EventBus eventBus);

    /**
     * Get the regular (untoggled) window launcher
     * @param eventBus   MVP pattern event bus required to create presenters  
     * @return           Regular window launcher (chart for a chart, grid for a grid)
     */
    AbstractWindowLauncher getWindowLauncher(EventBus eventBus);

    /**
     * Get the window body
     * @return Element that represents the x-window-body div of the window
     */
    El getWindowBody();

}
