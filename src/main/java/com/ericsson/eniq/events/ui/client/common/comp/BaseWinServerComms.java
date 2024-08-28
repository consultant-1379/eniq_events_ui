/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.ui.client.common.ServerComms;
import com.ericsson.eniq.events.ui.client.common.URLParamUtils;
import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.common.client.CommonConstants.FIRST_URL_PARAM_DELIMITOR;
import static com.ericsson.eniq.events.ui.client.common.Constants.DISPLAY_TYPE_PARAM;
import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;
import static com.ericsson.eniq.events.ui.client.common.Constants.GROUP_VALUE_PARAM;
import static com.ericsson.eniq.events.ui.client.common.Constants.KEY_PARAM;
import static com.ericsson.eniq.events.ui.client.common.Constants.MISSING_INPUT_DATA;
import static com.ericsson.eniq.events.ui.client.common.Constants.NEED_SEARCH_FIELD_MESSAGE;
import static com.ericsson.eniq.events.ui.client.common.Constants.TYPE_PARAM;

/**
 * 
 * Utility class performing server communication for windows (BaseWindowPresenter)
 * (Extracted code directly from BaseWindowPresenter to reduce it length)
 *  
 * @author eeicmsy
 * @since Oct 2010
 */
public class BaseWinServerComms<D extends IBaseWindowView> extends ServerComms {

    private final BaseWindowPresenter<D> baseWinPresenter;

    private final D display;

    private final BaseWinSearchFieldValueResetHandler<D> searchFieldResetHandler;

    /*
     * Found refresh and changing time was cleaning the
     * bread crumb (removing all entries after current position)
     * Don't want this when goes back and refreshes or changes time 
     * on an old window in the breadcrumb 
     */
    private boolean shouldCleanBreadCrumb;

    /**
     * Main class handling server communication for a window
     * 
     * @param baseWindowPresenter       -   main concrete presenter for generic windows 
     * @param eventBus                  -   the one and only event bus
     * @param display                   -   view reference for the presenter
     * @param searchFieldResetHandler   -   search field reset handler for the window
     */
    public BaseWinServerComms(final BaseWindowPresenter<D> baseWindowPresenter, final EventBus eventBus,
            final D display, final BaseWinSearchFieldValueResetHandler<D> searchFieldResetHandler) {

        super(eventBus);

        this.baseWinPresenter = baseWindowPresenter;
        this.display = display;
        this.searchFieldResetHandler = searchFieldResetHandler;

    }

    /* **************************************** */
    /* Server communication methods only allowed for this class*/
    /* **************************************** */

    /**
     * Make server call with widget specific parameters if they exist, 
     * else (recalculate) build up internal request data
     */
    public void makeServerCallWithURLParams() {
        makeServerRequestForData(getURLParams());
    }

    /**
     * Single point root for all requests for menu items windows to server. 
     * Call to the given Web Service for this window with parameters
     * (We will follow conversion with the parameters 
     * http://www.eric.com/subscriberservoce?id=IMSI_EVENT_QUERY&1212121212121
     * 
     * Result handled by SucessResponseEventHandler  (the chart presenter and grid presenter)
     * 
     * This method will update breadcrumbs and view with a view to resusing should
     * a bread crumb be revisted and refreshed (or time changed).
     * This method will be only place where encode the request 
     * 
     * Request data has to be complete at input parameter stage (do not add more to request in within 
     * this method - or will not have uniform behaviour)
     * 
     * @param requestData  fully built up parameters to pass with URI (can be build up or 
     *                     previously stored widget URLParameter). 
     *                     e.g.  ?time=30&display=grid&type=IMSI&tzOffset=+0000&maxRows=50
     */
    protected void makeServerRequestForData(final String requestData) {

        // do not make any call to server if need search field data and not there
        if (!isSearchFieldValidAndWarn()) {
            return;
        }

        // no reason why refresh or set time should clean bread crumb
        if (shouldCleanBreadCrumb) {
            baseWinPresenter.cleanUpBreadCrumbMenu();
        }

        /* important this is here to resuse full paramters for breadcrumb 
        (not encoding to avoid double encode) */
        storeFullWidgetURLParameters(requestData);

        makeServerRequest(baseWinPresenter.getMultipleInstanceWinId(), baseWinPresenter.getWsURL(), requestData);

    }

    /**
     * Get search parameter in URL format if applicable 
     * @return  e.g.  &imsi=val
     */
    public String getSearchURLParameters() {
        /* don't pass the searchparams to the drilled down calls */
        final boolean isShowingTimeSelection = (baseWinPresenter.getTimeData() != null);
        if (baseWinPresenter.isSearchFieldDataRequired()) {
            return (getSearchFieldURLParams(!isShowingTimeSelection));
        }
        return EMPTY_STRING;

    }

    /**
     * Build up the parameters to pass on the url.
     * This is only place should be building URL parameters
     * adding maxRows etc..
     * 
     * (resist temptation to do it in #makeServerRequestForData)
     * 
     * @return sample : ?time=30&display=grid&type=IMSI&tzOffset=+0000&maxRows=50
     */
    public String getInternalRequestData() {

        StringBuilder buff = new StringBuilder();

        /* (nearly) every query has time so append it as first parameter */
        /*append the time parameters */
        String sTimeParams = null;

        final TimeInfoDataType timeData = baseWinPresenter.getTimeData();
        final boolean isDrill = baseWinPresenter.getIsDrillDown();

        final boolean isShowingTimeSelection = (timeData != null);
        final boolean dataTimeExists = (timeData.dataTimeFrom != null);

        if (isShowingTimeSelection) {
            if(isDrill && dataTimeExists){
                        sTimeParams = timeData.getDrillQueryString();
            }else{
                        sTimeParams = timeData.getQueryString(true);
            }
            buff.append(sTimeParams);
        }

        /* don't pass the searchparams to the drilled down calls */
        buff.append(getSearchURLParameters());

        /*append the display parameters*/
        if (!baseWinPresenter.isSearchFieldDataRequired() && !isShowingTimeSelection) {
            buff.append(FIRST_URL_PARAM_DELIMITOR);
        } else {
            buff.append(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR);
        }

        buff.append(DISPLAY_TYPE_PARAM);
        buff.append(baseWinPresenter.getOutBoundDisplayTypeParameter());

        if (!baseWinPresenter.isDrilledDownScreen()) {
            // extra as seeing duplicate type= (one from search field one from metadata)
            if (!buff.toString().contains(TYPE_PARAM)) {
                appendTypeParameter(buff);
            }
            appendKeyParameter(buff);
        }

        /* menu items could have more data onchart drilldown, e.g. 
         * widgetSpecificParams&apn=myapn&imsi=12345&display=grid
         */
        buff = replaceURLParamsFromWidgetSpecificParams(buff, baseWinPresenter.getWidgetSpecificURLParams(), isDrill);

        buff.append(getConstantURLParamters()); // do here instead of at sending request stage

        return hackGroupkey(buff.toString());

    }

    /**
     * Gather URL search field parameters (group or single as applicable)
     * Also used by drilldown call to pick up groupname when applicable
     * 
     * @param isFirstParameter  - true if search if first paramter in URL outbuond (when no time)
     * @return empty string or URL parameters
     */
    protected String getSearchFieldURLParams(final boolean isFirstParameter) {

        final SearchFieldDataType searchData = baseWinPresenter.getSearchData();

        if (searchData == null) {
            return EMPTY_STRING;
        }
        return searchData.getSearchFieldURLParams(isFirstParameter);
    }

    /**
     *  Potentially make a call to populate the window on initialisation, 
     *  else tell user to input data into the search field
     * @param searchfieldData  search field data window was launched with
     */
    protected void potentiallyMakeCallOnWindowLaunch(final SearchFieldDataType searchfieldData) {

        if (baseWinPresenter.isSearchFieldDataRequired()) {

            if (isSearchFieldValidAndWarn()) {
                /* go through the same method a search field update would go though
                 * i.e. resisting temptation to go straight to makeServerRequestForData  */
                searchFieldResetHandler.handleSearchFieldParamUpdate(baseWinPresenter.getTabOwnerId(),
                        baseWinPresenter.getQueryId(), searchfieldData, baseWinPresenter.getWsURL());
            }
        } else {
            /* launch window that can launch without task bar search field parameters
             * (can be including widgetSpecificParams when creating grid from drilled chart */
            makeServerRequestForData(getURLParams());
        }
    }

    /////////////////////////////// 
    ///////////////////////////////   private methods
    /////////////////////////////// 

    /*
     * Storing whole URL parameters at time when call is made (After the call if you like), 
     * into breadcrumb, etc, such that they will be available when revisit window 
     * 
     * (requestData should not be encoded to avoid double encode) 
     * 
     * Store in views metaMenu Item to be available for CSV export
     * 
     * @param requestData  fully built up parameters to pass with URI (can be build up or    
     *                     previously stored widget URLParameter). 
     *                     e.g.  ?time=30&display=grid&type=IMSI&tzOffset=+0000&maxRows=50
     */
    private void storeFullWidgetURLParameters(final String requestData) {

        final BreadCrumbMenuItem currentBreadCrumbMenuItem = baseWinPresenter.getCurrentBreadCrumbMenuItem();
        if (currentBreadCrumbMenuItem != null) {
            currentBreadCrumbMenuItem.setWidgetURLParameters(requestData, baseWinPresenter.getSearchData());
        }

        /* TODO bad enough with presenter, breadcrumb and 
        * metaMenuItem without a fourth player involved in widgetSpecificURLParams state, 
         (but view one is one not clearing at moment so best one to have up to date for 
         future potential CSV export etc ) */

        ((IExtendedWidgetDisplay) display).updateWidgetSpecificURLParams(requestData);
        //XX baseWinPresenter.setWidgetSpecificParams(requestData);

    }

    /* this method can be called when launching new grid from a chart drilldown
     * when metamenu item is kept thoughout for view menu changes.
     * 
     * Necessary add on to carry though any data parameters from search field and add extrs 
     * drilldown parameters with out breaking existing, e.g
     * */

     private StringBuilder replaceURLParamsFromWidgetSpecificParams(StringBuilder currentBuff,
            final String widgetSpecificParams, final boolean isDrill) {

        if (widgetSpecificParams != null && widgetSpecificParams.length() > 0) {

            final URLParamUtils paramUtil = new URLParamUtils();
            paramUtil.replaceParams(currentBuff.toString(), widgetSpecificParams, isDrill);

            if(!isDrill){
                paramUtil.replaceTimeParams(baseWinPresenter.getTimeData());
            }

            final String params = paramUtil.getWidgetSpecificParams();

            currentBuff = new StringBuilder(params);
        }
        return currentBuff;
    }

    /*
     * This method is used when have already set some WidgetSpecificURLParams for window,
     * e.g. used when drill on a chart. Still need to append extra parameters such as 
     * time. 
     *  
     * @return   url parameters based on existing (pre-server call) parameters built up, 
     *           for window with com 
     */
    protected String getResusableURLParams() {

        final StringBuilder parmBuffer = new StringBuilder();

        // presenters version of widgetSpecificParams can be empty at this point
        parmBuffer.append(((IExtendedWidgetDisplay) display).getWidgetSpecificURLParams());
        parmBuffer.append(getConstantURLParamters());

        return replaceURLParamsFromWidgetSpecificParams(new StringBuilder(), parmBuffer.toString(), true).toString();

    }

    private void appendTypeParameter(final StringBuilder buff) {
        final String queryType = baseWinPresenter.getQueryType();
        /*append the type parameter */
        if (queryType.length() > 0) {
            buff.append(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR);
            buff.append(TYPE_PARAM);
            buff.append(queryType);
        }
    }

    private void appendKeyParameter(final StringBuilder buff) {
        final String queryKey = baseWinPresenter.getViewType();
        /*append the type parameter */
        /*append the key parameter */
        if (queryKey.length() > 0) {
            buff.append(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR);
            buff.append(KEY_PARAM);
            buff.append(queryKey);
        }
    }

    /*
     * Do not make any call, e.g. if change the time parameters
     * when search field is still not populated
     * Checks search field data present when should be present
     * Shows an info message. 
     
     * @return  true if a server call can be made
     */
    private boolean isSearchFieldValidAndWarn() {

        if (isSearchFieldValid()) {
            return true;
        }

        // TODO why was InfoPositioned.displayInfoNearSearchComponeny removed - i.e used position it next to search field
        MessageDialog.get().show(MISSING_INPUT_DATA, NEED_SEARCH_FIELD_MESSAGE, MessageDialog.DialogType.WARNING);
        return false;

    }

    public boolean isSearchFieldValid() {
        if (baseWinPresenter.isSearchFieldDataRequired()) {

            final SearchFieldDataType searchData = baseWinPresenter.getSearchData();

            if (searchData == null || searchData.isEmpty()) {
                //Commented since the error window opens twice
                //                final MessageDialog messageDialog = new MessageDialog();
                //                messageDialog.show(MISSING_INPUT_DATA, NEED_SEARCH_FIELD_MESSAGE, MessageDialog.DialogType.WARNING);
                //                messageDialog.center();

                return false;
            }
        }
        return true;
    }

    /* 
     * Constants in URL parameters - include as part of internal data
     * (concider for inclusion in header instead)
     */
    private String getConstantURLParamters() {
        final StringBuilder widgetParamsBuilder = new StringBuilder();

        widgetParamsBuilder.append(CommonParamUtil.getTimeZoneURLParameter());
        widgetParamsBuilder.append(baseWinPresenter.getMaxRowsURLParameter());
        widgetParamsBuilder.append(baseWinPresenter.getDataTieredDelayURLParameter());

        return widgetParamsBuilder.toString();

    }

    /*
     * We have one TOTAL in the meta data for IMSI. Unfortunately
     * services now wants to use key =SUM instead for IMSI Group, but
     * we can not quickly or easily support having both keys on the 
     * menu item in meta data. This is a hack to patch up the URL for 
     * this before sending it out.
     * @param requestData  url being sent in outbound call
     * @return             Same url or else a hacked version if required
     */
    private String hackGroupkey(final String requestData) {
        if (requestData.contains(GROUP_VALUE_PARAM) && requestData.contains("key=TOTAL")) {
            return requestData.replaceAll("key=TOTAL", "key=SUM");
        }
        return requestData;
    }

    /*
     * Specifically check if URL parameters exist in the breadcrumb.
     * This will be a full widget URL parameter 
     * 
     * @return  null if no widget params aleady in breadcrumb (or no bread crumb)
     */
    private String getWidgetSpecificParamsFromBreadCrumb() {

        final BreadCrumbMenuItem breadCrumb = baseWinPresenter.getCurrentBreadCrumbMenuItem();
        String widgetSpecificParams = null;
        if (breadCrumb != null) {
            /* bread crumb specifics take precedence when bread crumb exists */
            widgetSpecificParams = breadCrumb.getWidgetURLParameters();

        }
        if (widgetSpecificParams == null || widgetSpecificParams.isEmpty()) {
            return null;
        }
        return widgetSpecificParams;

    }

    /*
     * Fetch url parameters to add onto URI. If previous data exists from
     * breadcrumb use it.
     * Otherwise take widgetSpecificURLParams from view class because 
     * that one will not have been cleared
     * 
     * @return  previous parameter if already in breadcrumb, else fetch new parameters
     */
    private String getURLParams() {

        final String breadCrumbWidgetParams = getWidgetSpecificParamsFromBreadCrumb();

        shouldCleanBreadCrumb = false;

        if (breadCrumbWidgetParams == null) {
            shouldCleanBreadCrumb = true;
        } else {
            return breadCrumbWidgetParams;
        }

        String urlParameters;
        final String widgetSpecificParams = ((IExtendedWidgetDisplay) display).getWidgetSpecificURLParams();
        if (widgetSpecificParams == null || widgetSpecificParams.length() == 0) {
            urlParameters = getInternalRequestData();
        } else {
            urlParameters = getResusableURLParams();
        }
        return urlParameters;

    }

}