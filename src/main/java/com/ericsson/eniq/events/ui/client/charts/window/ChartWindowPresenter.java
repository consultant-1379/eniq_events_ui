/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.charts.window;

import com.ericsson.eniq.events.common.client.datatype.ChartDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.ui.client.charts.ChartElementDrillDownListener;
import com.ericsson.eniq.events.ui.client.charts.IChartElementDrillDownListener;
import com.ericsson.eniq.events.ui.client.charts.IChartPresenter;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.common.comp.BreadCrumbMenuItem;
import com.ericsson.eniq.events.ui.client.common.widget.AbstractBaseWindowDisplay;
import com.ericsson.eniq.events.ui.client.datatype.ButtonEnableParametersDataType;
import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.events.ChangeChartGridEvent;
import com.ericsson.eniq.events.ui.client.events.HideShowChartElementEvent;
import com.ericsson.eniq.events.ui.client.events.SucessResponseEvent;
import com.ericsson.eniq.events.ui.client.events.SucessResponseEventHandler;
import com.ericsson.eniq.events.ui.client.events.handlers.ChartGridChangeEventHandler;
import com.ericsson.eniq.events.ui.client.events.handlers.ChartHideShowElementHandler;
import com.ericsson.eniq.events.ui.client.events.handlers.ServerFailedResponseHandler;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessageType;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.EventBus;

import java.util.List;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

/**
 * Presents a chart with results of the given action
 * Charts can be of various types set up from meta data.
 * <p/>
 * Charts can be toggled to grids using the same response object
 * that created the chart.
 * <p/>
 * Charts elements can also be drilled down on (clicked) to
 * produce new grid views in this "same" window (supporting
 * toggling with a new Response object also)
 *
 * @author eeicmsy
 * @since April 2010
 */
public class ChartWindowPresenter extends BaseWindowPresenter<IChartWindowView> {

    private final IMetaReader metaReader = MainEntryPoint.getInjector().getMetaReader();

    private final ServerSuccessResponseHandler chartDataSuccessHandler = new ServerSuccessResponseHandler();

    private ChartHideShowElementHandler chartHideShowElementHandler = null;

    private final IChartPresenter currentChart;

    private TimeInfoDataType windowTimeDate;

    /* to only add widgets once */
    private boolean isWidgetAddedAlready;

    private final IChartElementDrillDownListener chartDrillDownListener;

    /**
     * for SELENIUM testing
     */
    /* Ideally would like to be turning this off for production - seen method cause exceptions 
    (if want to avoid hard-coding, use a config file or similar
    to ensure production code not affected) */
    static boolean exportInterface = true;

    private boolean experiencedError = false;

    /**
     * Construct generic presenter for Chart windows
     * Register for success and fail responses
     *
     * @param display  view class in MVP pattern
     * @param eventBus eventBus singleton required for presenters
     */
    public ChartWindowPresenter(final IChartWindowView display, final MultipleInstanceWinId multiWinId,
            final EventBus eventBus) {
        super(display, multiWinId, eventBus);
        /* (the failure handling part) is in the base class */
        registerHandler(eventBus.addHandler(SucessResponseEvent.TYPE, chartDataSuccessHandler));

        /* for when view menu on window activates a chart change
           e.g. roaming by country or operator, etc) */
        registerHandler(eventBus.addHandler(ChangeChartGridEvent.TYPE, new ChartGridChangeEventHandler(eventBus, this)));

        /* drill-down listener (removed on detach)*/

        currentChart = getDisplayedChart(); // NOPMD by eeicmsy on 03/04/11 21:15

        chartDrillDownListener = getChartElementDrillDownListener(); // NOPMD 
        currentChart.addChartDrillDownListener(chartDrillDownListener);

        bind();

        exportDrillDownChartMethod();
    }

    /* for junit */
    IChartElementDrillDownListener getChartElementDrillDownListener() {
        return new ChartElementDrillDownListener(getEventBus(), this);
    }

    @Override
    public void cleanUpOnClose() {

    }

    @Override
    protected String getOutBoundDisplayTypeParameter() {
        return OUT_BOUND_CHART_DISPLAY_PARAM;
    }

    @Override
    public void initializeWidgit(final String winId) {

        //If Chart contains a wizard - use the wizard ID to define the chart
        final String sMetaRetrievalID = (metaMenuItem.getWizardId().isEmpty() ? winId : metaMenuItem.getWizardId());
        /* get the meta data that defines chart layout */
        final ChartDataType meta = getChartConfigInfo(sMetaRetrievalID);

        /* reset listener info */
        chartDrillDownListener.setEventId(meta.id); // revert time from preset
        currentChart.setConfigData(meta);
        setHideShowElementHandler();
    }

    /**
     * Utility to update chart sub title (as apposed to window title)
     *
     * @param subTitle e.g. pass "Cause Code 16"  to become  "Cause Code 16 Sub Cause Code Analysis"
     */
    public void addChartSubTitle(final String subTitle) {
        currentChart.addSubTitle(subTitle);
    }

    /* force ALL charts types to be capable of removing elements on demand */
    private void setHideShowElementHandler() {

        if (chartHideShowElementHandler == null) {
            chartHideShowElementHandler = new ChartHideShowElementHandler(getMultipleInstanceWinId(),
                    this.currentChart);

            registerHandler(getEventBus().addHandler(HideShowChartElementEvent.TYPE, chartHideShowElementHandler));
        }
    }

    @Override
    public int handleSuccessResponse(final Response response) {
        setResponseObj(response); // storing  for possible toggle to grid
        JSONValue responseValue = null;
        AbstractBaseWindowDisplay display = (AbstractBaseWindowDisplay) getView();

        restoreDefaults(display); //remove error message if any, make chart visible

        try {
            responseValue = parseText(response.getText());
        } catch (final JSONException e) {
            experiencedError=true;
            return 0;
        }

        // back as a chart - should have no use for temp toolbar
        // (moving calls to here to suit drilldown to "licence" column grid from chart)
        // (and calling on presenter rather than launcher, i.e not ((ChartWindowView) display).resetUpperToggleToolBar())
        super.resetUpperToggleToolBar();

        if (responseValue != null && validate(responseValue)) {
            /* delay update of window title until data present */
            upDateWindowTitleWithSearchDataIfRequired();

            /* initialise and bind the grid data */
            currentChart.updateData(responseValue);
            if (!isWidgetAddedAlready) { // chart refresh scenario
                getView().addWidget(currentChart.asWidget());
                isWidgetAddedAlready = true;
            }
            getView().stopProcessing(); // unmask
            /* oh dear - don't  carry these to next call*/
            setWidgetSpecificParams(EMPTY_STRING);

            final int rowCount = currentChart.getChartRowCount();
            handleButtonEnabling(rowCount);

            this.windowTimeDate = new TimeInfoDataType(getTimeData());

            return rowCount;

        }
        handleButtonEnabling(0);
        return 0;
    }

    private void restoreDefaults(AbstractBaseWindowDisplay display) {
        //reset everything to default
        display.hideErrorMessage();
        currentChart.setVisible(true);
        experiencedError = false;
    }

    @Override
    public void cleanUpWindowForCancelRequest() {
        setWidgetSpecificParams(EMPTY_STRING);
    }

    @Override
    public ButtonEnableParametersDataType getButtonEnableParameters(final int rowCount) {

        final ButtonEnableParametersDataType params = new ButtonEnableParametersDataType();

        params.rowCount = rowCount;

        params.searchData = getSearchData(); // not the menu task bar one - one owned by window
        params.hasSearchFieldChanged = true;
        params.isRowSelected = false;
        params.widgetSpecificInfo = null; // not storing cell click drilldown informatino for chart 
        params.columnsMetaData = null;
        return params;

    }

    /**
     * Handle success response when event bus fired a SucessResponseEvent
     * Indicating server has returned with some success result
     * (the standard fail response is in the base class)
     */
    private final class ServerSuccessResponseHandler implements SucessResponseEventHandler {

        @Override
        public void handleResponse(final MultipleInstanceWinId multiWinId, final String requestData,
                final Response response) {

            // guards 
            if (isThisWindowGuardCheck(multiWinId)) {

                //extra guard for Charts with a wizard that
                //launch a grid using same winID - ensure the meta is for chart
                if (metaMenuItem.getDisplay().equalsIgnoreCase(MetaMenuItemDataType.Type.GRID.toString())) {
                    return;
                }

                //only at server call (not toggle or other reasons hand success called )
                getView().upDateLastRefreshedLabel(response);

                saveJSONResponseTimeRangeValues(response);

                handleSuccessResponse(response);
            }

            if(experiencedError) {
                displayErrorMessagePanel(response);
            }
        }

        private boolean saveJSONResponseTimeRangeValues(final Response response) {
            if (response.getText().length() > 0) {
                JsonObjectWrapper data;
                try {
                    data = new JsonObjectWrapper(JSONUtils.parse(response.getText()).isObject());
                } catch (final JSONException e) {
                    ((AbstractBaseWindowDisplay) getView()).showErrorMessage(ComponentMessageType.ERROR, CHECK_GLASSFISH_LOG_MESSAGE, e.getMessage());
                    return false;
                }
                final String dataTimeFrom = data.getString(DATA_TIME_FROM_PARMA_JSON_RESPONSE);
                final String dataTimeTo = data.getString(DATA_TIME_TO_PARMA_JSON_RESPONSE);
                final String timeZone = data.getString(DATA_TIMEZONE_PARMA_JSON_RESPONSE);
                // Update the time information with the data returned from services
                final TimeInfoDataType timeData = getTimeData();
                if (timeData != null) { // e.g. cause code table
                    timeData.dataTimeFrom = dataTimeFrom;
                    timeData.dataTimeTo = dataTimeTo;
                    timeData.timeZone = timeZone;
                    setTimeData(timeData);
                }
            }
            return true;
        }

    }

    //EMEACOD: changed to add in licence and install error options
    private void displayErrorMessagePanel(Response response) {
        String msg = "";
        int chartHeight = currentChart.asWidget().getOffsetHeight();
        if(response.getText().contains(INSTALL_ERROR)){
             msg = TECHPACK_NOT_INSTALLED;
        }
        else if(response.getText().contains(LICENSE_ERROR_DESCRIPTION_SUBSTRING)){
            msg = LICENSE_ERROR;
        }
        else{
            msg = CHECK_GLASSFISH_LOG_MESSAGE;
        }
        currentChart.setVisible(false);
        AbstractBaseWindowDisplay display = (AbstractBaseWindowDisplay) getView();
        display.showErrorMessage(ComponentMessageType.ERROR,  ERROR, msg);
        if (chartHeight>0) display.setErrorMessageHeight(chartHeight+"px");
    }

    //////////////////  junit

    /* extracted (to override) for junit */
    IChartPresenter getDisplayedChart() {
        return getView().getChartControl();
    }

    /* extracted (to override) for junit */
    public ChartDataType getChartConfigInfo(final String winId) {
        return metaReader.getChartConfigInfo(winId, getSearchData() == null ? false : getSearchData().isGroupMode());
    }

    /* extracted (to override) for junit */
    JSONValue parseText(final String s) {
        return JSONUtils.parse(s);
    }

    /* extracted fro junit overide*/
    boolean validate(final JSONValue responseValue) {
        return JSONUtils.checkData(responseValue, getEventBus(), getMultipleInstanceWinId());
    }

    @Override
    public void cleanUpBreadCrumbMenu() {

    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.common.comp.BaseWindowPresenter#initializeWidgitWithGridInfo(java.lang.String, com.ericsson.eniq.events.ui.client.datatype.GridInfoDataType)
     */
    @Override
    public void initializeWidgitWithGridInfo(final GridInfoDataType gridMetaData, final boolean resetColumns,
            String title) {
        // Redundant Stub method for charts - TODO revisit and perhaps remove from base win as abstract method

    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.common.comp.BaseWindowPresenter#handleSuccessResponseWithJSONValue(com.google.gwt.http.client.Response, com.google.gwt.json.client.JSONValue, java.util.List, java.util.Map, java.util.Map)
     */

    @Override
    public int handleSuccessResponseWithJSONValue(final Response response, final JSONValue data,
            final List<Filter> filters) {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowPresenter#getCurrentBreadCrumbMenuItem()
     */
    @Override
    public BreadCrumbMenuItem getCurrentBreadCrumbMenuItem() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServerFailedResponseHandler getServerFailedResponseHandler() {

        return new ServerFailedResponseHandler(getMultipleInstanceWinId(), getView(),
                ChartWindowPresenter.this.getMetaMenuItem()) {
            @Override
            protected void completeRendering() {
                cleanUpWindowForCancelRequest();
                experiencedError = true;
                currentChart.setVisible(false);
                if(getInitToolBarHandler().hasWizard()){
                  getView().getWindowToolbar().hide(); //only hide Wizard toolbars
                }
            }
        };
    }

    public TimeInfoDataType getWindowTimeDate() {
        return this.windowTimeDate;
    }

    public void setWindowTimeDate(TimeInfoDataType timeInfoDataType){
        this.windowTimeDate = new TimeInfoDataType(timeInfoDataType);
    }

    /////////////////////////////////   
    //////////////    SELENIUM  Testing Methods only
    /////////////////////////////////   

    /**
     * METHOD INTRODUCED FOR SELENIUM ONLY
     */
    private void exportDrillDownChartMethod() {
        if (exportInterface) {
            drillDownChartMethod(this);
        }
    }

    /**
     * METHOD INTRODUCED FOR SELENIUM ONLY
     * <p/>
     * Method we want to use from javascript.
     *
     * @param chartElementClicked
     * @param drillDownWindowType
     *
     * @return String to confirm this method is called
     */
    public String drillDownChart(final String chartElementClicked, final String drillDownWindowType) {
        chartDrillDownListener.performChartClickManually(chartElementClicked, drillDownWindowType);
        return "drillDownChart() called";
    }

    /**
     * SELENIUM ONLY
     * Method we want to use from javascript
     *
     * @return chart URL
     */
    public String getChartURL() {
        return getWsURL();
    }

    /**
     * SELENIUM ONLY
     * expose interface as javascript and ONLY used by selenium Testing
     *
     * @param instance
     */
    private native void drillDownChartMethod(ChartWindowPresenter instance) /*-{
		//It is only used to create a reference in the browser to the final method
		$wnd.drillDown = function(chartElementClicked, drillDownWindowType) {
			return instance.@com.ericsson.eniq.events.ui.client.charts.window.ChartWindowPresenter::drillDownChart(Ljava/lang/String;Ljava/lang/String;)(chartElementClicked, drillDownWindowType);
		};

		$wnd.getURL = function() {
			return instance.@com.ericsson.eniq.events.ui.client.charts.window.ChartWindowPresenter::getChartURL()();
		};
    }-*/;

}
