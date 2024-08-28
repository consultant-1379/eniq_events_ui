/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import java.util.List;

import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.PresetResponseDisplayDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.events.handlers.ServerFailedResponseHandler;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;

/**
 * Pulling former abstract methods from BasewindowPresenter to shorten class lenght)
 * 
 * @author eeicmsy
 * @since July 2010
 *
 */
public interface IBaseWindowPresenter {

    // TODO does too much (and these are old abstract methods in class obviously with heavy grid leanings (not chart))

    /**
     * Set up any pre-server call initialisation, e.g.
     * for a grid define the grid column meta data
     * @param  fixedWinQueryId   - (will have the queryId in sub class to identify grid or chart,
     *                           but passing it in as a parameter here too because of multiple 
     *                           window instances scenario (a multiple instance chart being toggled to 
     *                           a table)) -- to ensure fixed one is used when reading metadata  
     *                    
     */
    void initializeWidgit(final String fixedWinQueryId);

    /**
     * Utility returning query id (win ID) on window
     * @return  queryId defined in meta data (same as id will use
     *          for windows, buttons and menu items)
     */
    String getQueryId();

    /**
     * Utlity returing fixed query id on window
     * (usually same as winid - or with "_APN" etc tacked on for 
     * multiResultSets
     * 
     * @return fixed query id
     */
    String getFixedQueryId();

    /**
     * Utlity returning Id of tab for window is sitting in, in order
     * to guarantee a unique id (when combine with query id)
     * @return  tab id
     */
    String getTabOwnerId();

    /**
     * Set up any pre-server call initialisation where the grid definition is already known, e.g.
     * for a grid define the grid column meta data that had previously been read from meta
    
     * @param gridMetaData - dataType containing all info pertaining to the display / config of the grid
     * @param resetColumns - boolean, determines which method on the grid is called - setColumns which in turn
     *                       initialises a new view if this is a grouping grid or resetColumns which updates the column info for the grid
     * @param title        - title of window
     */
    void initializeWidgitWithGridInfo(final GridInfoDataType gridMetaData, final boolean resetColumns, String title);

    /**
     * SubClasses should implement this to remove 
     * listeners etc., added to the window 
     * when the window closes
     */
    void cleanUpOnClose();

    /**
     * Removes any BreadCrumbMenuItems that are no longer valid
     */
    void cleanUpBreadCrumbMenu();

    /**
     * Utility to fetch current bread crumb item for current Grid
     * displayed 
     * @return current gread crumb for grid
     */
    BreadCrumbMenuItem getCurrentBreadCrumbMenuItem();

    /**
     * Method included to allow a direct call to handle a server 
     * response when already have response data (e.g for a toggle scenario).
     * returns the number of rows in the response object
     * 
     * 
     * @param response - data which may (or may not) have been fetched from 
     *                   a previous call on another window (e.g. if toggling to a
     *                   grid from a graph response) 
     * @return         - row count in return data (i.e. 0 if no data)                 
     */
    int handleSuccessResponse(final Response response);

    /**
     * Handler for failed server responses
     * @return  subclass to provide handler for failed response
     */
    ServerFailedResponseHandler getServerFailedResponseHandler();

    int handleSuccessResponseWithJSONValue(final Response response, final JSONValue data, final List<Filter> filters);

    /**
     * Updates time on presenter and view for window
     * @param timeData the timeData to set
     */
    void setTimeData(final TimeInfoDataType timeData);

    /**
     * Getter utlity for search data
     * @return  search data for window
     */
    SearchFieldDataType getSearchData();

    /**
     * Fetching SearchFieldDataType information but 
     * considering starting with "?" instead of "&" delimitor 
     * when no time being displayed
     * 
     * 
     * @return  e.g. &type=APN&node=whatever, or ?type=APN&node=whatever 
     */
    String getSearchURLParameters();

    /**
     * Reset presenter search data reference 
     * (without makeing a server call)
     * @param data  search data
     * @return      true if data changed
     */
    boolean resetSearchData(final SearchFieldDataType data);

    /**
     * Get the dateTime local to the current grid. This time is equal to the time displayed in the
     * bottom right corner of the window.
     * @return local timeDate (timeDate returned from services after offset.)
     */
    TimeInfoDataType getWindowTimeDate();

    /**
     * Set the dateTime of the current grid. This time is equal to the time displayed in the
     * bottom right corner of the window.
     * @param timeInfoDataType time returned from services.
     */
    void setWindowTimeDate(TimeInfoDataType timeInfoDataType);

    /**
     * Update web service URL for the window
     * @param url  new web service url
     */
    void setWsURL(final String url);

    /**
     * Want to send a call to the server to cancel the 
     * on going server call (presses the cancel button
     * we supply on the loading message, will now make a 
     * request down to the server to cancel the ongoing request)
     */
    void sendCancelRequestCall();

    /**
     * Handle request UI side to clean up window
     * following cancel request. 
     * We already ensure no breadcrumb is set up
     * until after call returns, this is too 
     * clean out any other window settings, e.g. clean 
     * out widget specific parameters.
     */
    void cleanUpWindowForCancelRequest();

    /**
     * Direct call to presenter shutdown (unbind and
     * deregistor with event bus)
     */
    void handleShutDown();

    /**
     * Utility to fetch current success response information held by the window and 
     * current display position and size. (Such that the window will appear to 
     * maintain its own size and position when we replace it with a new window for toggling 
     * from graph to grid and vice versa).
     * 
     * @return  response object known to have been set by sub class (toggle scenario) - with search 
     *          cached applicable search field data for window and current display position at time of calling
     */
    PresetResponseDisplayDataType getPresetResponseDisplayData();

    /**
     * Direct call to window - without using bus
     */
    void handleGroupingGrid(final String winTitle, final GridInfoDataType gridInfoDataType, final Response response,
            final JSONValue data, final List<Filter> filters, final Menu breadCrumbMenu,
            final SearchFieldDataType searchData, final String wsURL, final TimeInfoDataType timeInfo,
            boolean isTogglingFromGraph, boolean isDrilling);

    /**
     * @return
     */
    MultipleInstanceWinId getMultipleInstanceWinId();

}
