/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import static com.ericsson.eniq.events.ui.client.common.CSSConstants.*;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.client.common.URLParamUtils;
import com.ericsson.eniq.events.ui.client.datatype.IHyperLinkDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.json.client.JSONValue;

/**
 * Object extends the menuItem of a splitter button,
 * holds properties that allow for a grid to 
 * be redisplayed when the menuItem is selected
 * 
 * When a breadcrumb has already been "visited" we want to be able
 * to use this class to recover URL parameters required for the grid
 * should user choose refresh or change the time on a previously visit window.
 * So URL parameter for breadcrumb case should be taken from here (as 
 * apposed to window's view, presenter or MetameniItem) when making server call.
 * 
 * 
 * 
 * @author eendmcm
 * @since Apr 2010
 */
public class BreadCrumbMenuItem extends MenuItem {

    private final int index;

    private final String winID;

    private boolean gridDisplayed = true;

    private final IHyperLinkDataType drillInfo;

    private GridInfoDataType gridInfoDataType;

    private JSONValue data;

    private List<Filter> filters;

    private Map<Integer, StoreFilter<ModelData>> dateFilters;

    private String winTitleWithParms = EMPTY_STRING;

    private String url;

    private TimeInfoDataType timeData;

    private SearchFieldDataType searchData;

    private String lastRefreshedTimeStamp;

    public String dataTimeFrom;

    public String dataTimeTo;

    public String timeZone;

    private String gridStateId = EMPTY_STRING;

    /*
     * retain the exact URL that was used by the end user to populate
     * the display - this is needed if the end user browses to a display 
     * via the navigation control and decides to refresh the screen.
     */
    private String widgetSpecificUrlParams = EMPTY_STRING;

    /* record as columns that the end user may have hide */
    public final List<String> hiddenColumns = new ArrayList<String>();

    private final URLParamUtils paramUtils = new URLParamUtils();

    /**
     *  BreadCrumbMenuItem extends the menuItem of a splitter button,
     *  holds properties that allow for a grid to
     *  be redisplayed when the menuItem is selected
     * 
     * @param title  - Breadcrumb Title
     * @param index  - index of breadcrumb menu item in navigation menu
     * @param winID  - baseWinPresenter.fixedQueryId 
     * @param drillInfo           - Outbound server call information was used to populate this window
     *                              following a drilldown hyperlink click. This is like information stored in 
     *                              widgetSpecificParams (except widgetSpecificParams will be wiped on
     *                              success server calls - which is where would want this information 
     *                              for enabling toolbar buttons)
     *                              e.g. can use to fetch data such as 
     *                              <li>&key=SUM&type=CELL&cell=CELL146889&vendor=ERICSSON&bsc=BSC735&RAT=1</li>
     *                              <li>&key=ERR&type=BSC&groupname=Another_Group_HIER3&eventID=1</li>
     *
     */
    public BreadCrumbMenuItem(final String title, final int index, final String winID,
            final IHyperLinkDataType drillInfo) {
        super(title);
        this.index = index;
        this.winID = winID;
        this.drillInfo = drillInfo;

        /* new bread crumb menuitem is selected by default */
        setIconStyle(BREADCRUMB_SELECTED);
    }

    /**
      * Save current configurations of a grid (to be able to reproduce again)
      * 
      * @param gridInfoDataTypeRef
      * @param dataRef
      * @param gridFilters
      * @param fullWinTitle 
      * @param timeData                 -  preserving user time selection for grid (to display as label)
      *                                      on upper toolbar 
      * @param lastRefreshedTimeStamp   -  preserving timestamp on bottom toolbar, indicating the last time 
      *                                    a server call was made for this window
      * @param searchData               -  if user clicks on a link and navs back - want PREVIOUS presenters search data
     * 
     */
    public void saveGridConfigurations(final GridInfoDataType gridInfoDataTypeRef, final JSONValue dataRef,
            final List<Filter> gridFilters, final String fullWinTitle, final TimeInfoDataType timeData,
            final String lastRefreshedTimeStamp, final SearchFieldDataType searchData, final String gridStateId) {
        this.gridInfoDataType = gridInfoDataTypeRef;
        this.data = dataRef;
        this.filters = gridFilters;

        setGridDisplayed(false);
        this.winTitleWithParms = fullWinTitle;

        setTimeData(timeData);
        this.lastRefreshedTimeStamp = lastRefreshedTimeStamp;
        this.searchData = searchData;
        this.gridStateId = gridStateId;

    }

    /**
     * @return the winID (fixedId)
     */
    public String getWinID() {
        return winID;
    }

    /**
     * Set or unset selected icon
     * @param isGridDisplayed  true if grid displayed
     */
    public void setGridDisplayed(final boolean isGridDisplayed) {
        this.gridDisplayed = isGridDisplayed;
        setIconStyle((isGridDisplayed) ? BREADCRUMB_SELECTED : BREADCRUMB_NOT_SELECTED);
    }

    /**
     * stores the identity of columns that are 
     * hidden by the end user
     * @param cm
     */
    public void saveHiddenColumns(final List<ColumnConfig> cm) {
        hiddenColumns.clear();
        //store the identity of hidden columns
        for (final ColumnConfig c : cm) {
            if (c.isHidden()) {
                hiddenColumns.add(c.getId());
            }
        }
    }

    /**
     * Utility returning gridType string set in meta data
     * (groupingView, gridView)
     * 
     * @return  grid type (groupingView, gridView) from meta data or empty string
     */
    public String getGridType() {
        return (gridInfoDataType == null) ? EMPTY_STRING : gridInfoDataType.gridType;
    }

    /**
     * Utility to fetch stored search data from previous window in the breadcrumb
     * (e.g. User presses TAC link on event analysis and then navigates back to 
     * Event summary for an APN - we want the search field data used to be the APN 
     * 
     * @return  search data from previous presenter
     */
    public SearchFieldDataType getSearchData() {
        return searchData;
    }

    /**
     * Get Time Dialog selected time for window for display 
     * on upper toolbar.
     * 
     * Used to update the time label for previous time selections 
     * on window - but should also update time when time dialog next 
     * opened on this window.
     *  
     * @return  time data for window (from time dialog selection)
     */
    public TimeInfoDataType getTimeData() {
        return correctForCSVParamsIfRequired(timeData);

    }

    private TimeInfoDataType correctForCSVParamsIfRequired(final TimeInfoDataType timeData2) {
        if (timeData2 != null) {
            if (timeData2.dataTimeFrom == null || timeData2.dataTimeFrom.isEmpty()) {

                if (dataTimeFrom != null && !dataTimeFrom.isEmpty()) {
                    timeData2.dataTimeFrom = dataTimeFrom;
                }
                if (dataTimeTo != null && !dataTimeTo.isEmpty()) {
                    timeData2.dataTimeTo = dataTimeTo;
                }
            }
        }
        return timeData2;
    }

    /**
     * Get time stamp label for bottom toolbar
     * Time stamp can be different for all windows navigated to,
     * depending on what time they made their last call to the server
     * @return  time for time stamp indicating last time this 
     *          grid was refreshed with the server
     */
    public String getLastRefreshedTimeStamp() {
        return lastRefreshedTimeStamp;
    }

    /**
     * @return the index - menuItem in Navigation Menu
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return the gridDisplayed
     */
    public boolean isGridDisplayed() {
        return gridDisplayed;
    }

    /**
     * @return the gridInfoDataType
     */
    public GridInfoDataType getGridInfoDataType() {
        return gridInfoDataType;
    }

    /**
     * @return the data
     */
    public JSONValue getData() {
        return data;
    }

    /**
     * @return the filters
     */
    public List<Filter> getFilters() {
        return filters;
    }

    /**
     * @return the dateFilters
     */
    public Map<Integer, StoreFilter<ModelData>> getDateFilters() {
        return dateFilters;
    }

    /**
     * @return - title for the window that
     * is displaying the current cached widget
     */
    public String getWindowTitle() {
        return winTitleWithParms;
    }

    /**
     * gets the url that is assocaited with this 
     * breadcrumb menu option
     * @return
     */
    public String getURL() {
        return url;
    }

    /**
     * sets the url that is assocaited with this 
     * breadcrumb menu option
     * @param value
     * @return
     */
    public void setURL(final String value) {
        url = value;
    }

    /**
     * gets the url parameters that are specific to 
     * the display currently on screen
     * @return
     */
    public String getWidgetURLParameters() {
        return widgetSpecificUrlParams;
    }

    /**
     * Utility to preserve the "widgetSpecificUrlParams" in the breadcrumb itself
     * such that they are available should user toggle back to a grid previously entered and 
     * presses refresh  (best saved here at time of making initial server call for the grid)
     * 
     * e.g. ?time=30&display=grid&key=ERR&type=APN&apn=blackberry.net&eventID=4&tzOffset=+0000&maxRows=5000&tzOffset=+0000&maxRows=5000
     * 
     * @param paramString Whole and full parameter string to pass after a URL  
     * @param searchMetaData  - adding search data so paramUtils can learn of more search field type meta data
     */
    public void setWidgetURLParameters(final String paramString, final SearchFieldDataType searchMetaData) {

        this.widgetSpecificUrlParams = paramString;
        this.paramUtils.upDateSearchMetaData(searchMetaData);

    }

    /**
     * Update part of "widgetSpecificUrlParams" in the breadcrumb itself
     * Use when know do not have full URL params (e.g. don't have time or search data) 
     * @param paramString  partial URL params
     */
    public void setPartialWidgetURLParameters(final String paramString) {

        if (paramString == null || paramString.isEmpty()) {
            return;
        }

        if (widgetSpecificUrlParams != null && !widgetSpecificUrlParams.isEmpty()) {

            paramUtils.replaceParams(widgetSpecificUrlParams, paramString, false);
            this.widgetSpecificUrlParams = paramUtils.getWidgetSpecificParams();
        }
    }

    /**
     * Method to call when time is updated
     * Update widgetSpecificUrlParams directly here in breadcrumb for new time change.
     * 
     * (If breadcrumb widgetSpecificUrlParams could always be updated for time data we could use 
     * widgetSpecificUrlParams directly always for time changes and refresh)
     * 
     * @param timeData - new time data for current breadcrumb
     */
    public void setTimeData(final TimeInfoDataType timeData) {
        // CSV export params
        this.timeData = correctForCSVParamsIfRequired(timeData);

        if (widgetSpecificUrlParams != null && !widgetSpecificUrlParams.isEmpty()) {

            paramUtils.replaceParams(widgetSpecificUrlParams, timeData.getQueryString(true), false);
            this.widgetSpecificUrlParams = paramUtils.getWidgetSpecificParams();
        }
    }

    /**
     * Method to call when search field information is updated
     * Update widgetSpecificUrlParams directly here in breadcrumb for new time change.
     * 
     * @param sdata - new search data for current breadcrumb
     */
    public void resetSearchData(final SearchFieldDataType sdata) {

        if (widgetSpecificUrlParams != null && !widgetSpecificUrlParams.isEmpty() && (sdata != null)) {

            //TODO unbelievable... quick fix but hack - to avoid copying search params for Terminal analysis view menu windows on back from failure drill
            // need to improve this fix - avoid these specific checks in generic methods
            if (!winID.startsWith(TERMINAL_ANALYSIS_WIN_ID_PREFIX) && !winID.startsWith(LTE_HFA_QOS_QCI_SUMM_WIN_ID_PREFIX) && !winID.startsWith(LTE_CFA_QOS_QCI_SUMM_WIN_ID_PREFIX)) {

                // don't go replacing key=SUM (EventGridPresenter people added extra things to search data via metta data)
                final SearchFieldDataType sdata2 = SearchFieldDataType.newInstance(sdata);
                sdata2.clean();
                paramUtils.replaceSearchDataParams(widgetSpecificUrlParams, sdata2);
                this.widgetSpecificUrlParams = paramUtils.getWidgetSpecificParams();
            }
        }
    }

    /**
     * Return information which was available from outbound parameters at time of window luanch
     * 
     * @return e.g. Widget specific information about window creation can use to fetch data such as 
     *              <li>&key=SUM&type=CELL&cell=CELL146889&vendor=ERICSSON&bsc=BSC735&RAT=1</li>
     *                              <li>&key=ERR&type=BSC&groupname=Another_Group_HIER3&eventID=1</li>
     */
    public final IHyperLinkDataType getWidgetSpecificInfo() {
        return drillInfo;
    }

    /**
     * Returns map having parameters specific to the displayed window
     */
    public Map<String, String> getParametersMap() {
        return paramUtils.getParametersMap();
    }

    /**
     * @return the gridStateId
     */
    public String getGridStateId() {
        return gridStateId;
    }
}