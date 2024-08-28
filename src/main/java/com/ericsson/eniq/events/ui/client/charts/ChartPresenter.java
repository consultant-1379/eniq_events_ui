/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.eniq.events.ui.client.charts;

import com.ericsson.eniq.events.common.client.datatype.ChartDataType;
import com.ericsson.eniq.events.common.client.datatype.ChartDisplayType;
import com.ericsson.eniq.events.common.client.datatype.ParametersDataType;
import com.ericsson.eniq.events.common.client.json.JsonDataParserUtils;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.highcharts.client.ChartRegistry;
import com.ericsson.eniq.events.highcharts.client.HighChartUtils;
import com.ericsson.eniq.events.highcharts.client.HighChartsJS;
import com.ericsson.eniq.events.highcharts.client.config.ChartConfigTemplate;
import com.ericsson.eniq.events.highcharts.client.events.IHighChartsEventListener;
import com.ericsson.eniq.events.highcharts.client.events.PointClickEvent;
import com.ericsson.eniq.events.highcharts.client.events.PointEventHandler;
import com.ericsson.eniq.events.highcharts.client.options.series.QueryParamOptions;
import com.ericsson.eniq.events.ui.client.common.Constants;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.ericsson.eniq.events.ui.client.drill.DrillManager;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletResizeEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletResizeEventHandler;
import com.ericsson.eniq.events.widgets.client.drill.IDrillCallback;
import com.ericsson.eniq.events.widgets.client.drill.Point;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.ericsson.eniq.events.highcharts.client.ChartConstants.*;

/**
 * @since 2011
 *
 */
public class ChartPresenter implements IChartUiHandler, IChartPresenter, IHighChartsEventListener,
        PortletResizeEventHandler {

    private static final int SUB_TITLE_LINE_WRAP_LENGTH = 100;

    private static final int ID_SIZE = 16;

    private static final String DVTP="dvtp";

    private Map<String, String> dvtpChartParameters = new HashMap<String, String>();

    /* display type of this chart e.g. pie, bar etc */
    private String displayTypeStr;

    /* data that is displayed on the chart in json format */
    private Map<String, Object[]> seriesMap;

    /* meta that defines the layout and style of the chart */
    private ChartDataType graphConfigData;

    /** Flag to indicate that the model is updated and chart needs to be re-rendered.
     * Model is updated in the bind method, but can only be re-rendered in the show method
     * as container div has display:none at time of model update
     */
    private boolean modelUpdated;

    /*
     * Reference to chart configuration created in #bind 
     */
    private ChartConfigTemplate chartConfig;

    /*
     * listener on chart (which should remove on clean up 
     */
    private IChartElementDrillDownListener chartListener;

    /**
     * Root of the HighCharts Javascript API
     */
    protected HighChartsJS highChartsJS;

    /**
     * Hold list of selected chart element ids. Only relevant for chart configurations that
     * support adding/removing chart elements i.e. series'.
     */
    private Set<String> selectedChartElementIds;

    private final IChartView view;

    private final IChartConfigTemplateRegistry templateRegistry;

    private String subTitle;

    private final DrillManager drillManager;

    @Inject
    public ChartPresenter(final IChartConfigTemplateRegistry templateRegistry, final EventBus eventBus,
                          IMetaReader metaReader) {
        this.templateRegistry = templateRegistry;
        this.drillManager = new DrillManager(metaReader.getDrillManagerData());
        eventBus.addHandler(PortletResizeEvent.TYPE, this);
        highChartsJS = getHighChartsJS();
        view = getView();
        /**
         * Register as a listener for chart point click events which are received from the native Javascript
         */
        PointEventHandler.get().registerListener(highChartsJS.getChartName(), this);
    }

    /**
     * Init HighChart component here. Subscribe for events
     */
    @Override
    public void init(final String displayType) {
        displayTypeStr = displayType;
    }

    /**
     * For Junit
     *
     * @return
     */
    protected HighChartsJS getHighChartsJS() {
        return new HighChartsJS(HighChartUtils.generateID(ID_SIZE));
    }

    /**
     * For Junit
     *
     * @return
     */
    protected ChartView getView() {
        return new ChartView(this, highChartsJS.getContainerDivId());
    }

    /**
     * Add drill down listener to chart to support creating new
     * windows when "enabled" chart elements are clicked
     *
     * @param drillDownListener general drilldown listener
     */
    @Override
    public void addChartDrillDownListener(final IChartElementDrillDownListener drillDownListener) {
        this.chartListener = drillDownListener;

    }

    /**
     * returns Graph control as a Widget
     *
     * @return
     */
    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    /**
     * Clear current model (e.g. required
     * prior to a refresh or time parameter update)
     */
    private void reset() {
        chartConfig = null; // NB. Ensure new one (could be rebind)
    }

    /**
     * generates the graph based on the provided
     * config data and display data
     */
    private void bind() {
        if (graphConfigData != null && seriesMap != null) {
            /**
             * Clear the current Javascript String and rebuild the string with the new model
             */
            highChartsJS.clearJS();
            chartConfig = getChartConfiguration();
            if (this.selectedChartElementIds != null) {
                chartConfig.showChartElements(this.selectedChartElementIds);
            }
            if (this.subTitle != null && !subTitle.isEmpty()) {
                chartConfig.setSubTitle(subTitle);
            }
            chartConfig.buildChart();
            this.modelUpdated = true;
        }
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.charts.IChartUiHandler#show(int, int)
     */
    @Override
    public void onShow(final int height, final int width) {
        /**
         * Render the chart only is it has been loaded already and the model has been updated. We dont
         * want to render the chart just because this widget has been hidden and then shown
         */
        if (ChartRegistry.get().containsChart(highChartsJS.getChartName()) && this.modelUpdated) {
            configureScrollableContainer(width, height);
            highChartsJS.doRender();
            this.modelUpdated = false;
        }
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.charts.IChartPresenter#updateData(com.google.gwt.json.client.JSONValue)
     */
    @Override
    public void updateData(final JSONValue chartData) {
        JsonObjectWrapper data = new JsonObjectWrapper(JSONUtils.parse(chartData.toString()).isObject());
        if (displayTypeStr.equalsIgnoreCase(DVTP)) {
            dvtpChartParameters.clear();
            dvtpChartParameters.put(Constants.CHART_TITLE_PARAM, data.getString(Constants.CHART_TITLE_PARAM));
            dvtpChartParameters.put(Constants.CHART_TIME_TICK_INTERVAL_PARAM, data.getString(Constants.CHART_TIME_TICK_INTERVAL_PARAM));
            dvtpChartParameters.put(Constants.CHART_START_TIME_PARAM, data.getString(Constants.CHART_START_TIME_PARAM));
            dvtpChartParameters.put(Constants.CHART_END_TIME_PARAM, data.getString(Constants.CHART_END_TIME_PARAM));
            dvtpChartParameters.put(Constants.DATA_TIMEZONE_PARMA_JSON_RESPONSE, data.getString(Constants.DATA_TIMEZONE_PARMA_JSON_RESPONSE));
            }
        updateData(JsonDataParserUtils.convertJsonRowsToColumns(chartData));
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.charts.IChartPresenter#updateData(java.util.Map)
     */
    @Override
    public void updateData(final Map<String, Object[]> data) {
        reset();
        this.seriesMap = data;
        bind();
        render();
    }

    private void render() {
        /**
         * Render the chart only if the container widget has been rendered and the chart data is updated.
         */
        if (view.isRendered() && modelUpdated && view.isVisible()) {
            final Widget viewWidget = view.asWidget();
            configureScrollableContainer(viewWidget.getOffsetWidth(), viewWidget.getOffsetHeight());
            highChartsJS.doRender();
            this.modelUpdated = false;
        }
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.charts.IChartUiHandler#render()
     */
    @Override
    public void onRender(final int parentHeight, final int parentWidth, final boolean parentIsGwtPanel) {
        if (parentIsGwtPanel) {
            if (graphConfigData != null && seriesMap != null) {
                /** We have rendered latest model so set flag to false **/
                modelUpdated = false;
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                    @Override
                    public void execute() {
                        chartConfig.setSize(parentWidth, parentHeight);
                        highChartsJS.doRender();
                    }
                });
            }
        }
        configureScrollableContainer(parentWidth, parentHeight);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.charts.IChartUiHandler#resize(int, int)
     */
    @Override
    public void onResize(final int width, final int height) {
        /** Resize on a deferred command to allow GXT to complete its event loop  */
        final ScheduledCommand command = new ScheduledCommand() {

            @Override
            public void execute() {
                /** If Chart has already been rendered i.e. loaded, we only want to resize it **/
                if (ChartRegistry.get().containsChart(highChartsJS.getChartName())) {
                    if (chartConfig.requiresScrollableChart()) {
                        resizeScrollableChart(width, height);
                    } else if (view.asWidget().isVisible()) {
                        if (displayTypeStr.equalsIgnoreCase(DVTP)) {
                            bind();
                            render();
                        } else {
                            highChartsJS.doResize(width, height);
                        }
                    }
                } else {
                    /**
                     * Rendering for the first time. We are rendering the chart here for the first time, rather than in the
                     * onRender or onShow method because chart is contained in a GXT window, and the chart takes a default
                     * 500 ms to render including animation, so if we were to render the chart in the onRender or onShow method
                     * and then resize was called immediately (which is the case in GXT windows), the animation delay causes
                     * the chart to be resize incorrectly.
                     */
                    if (graphConfigData != null && seriesMap != null) {
                        /** We have rendered latest model so set flag to false **/
                        modelUpdated = false;
                        highChartsJS.doRender();
                    }
                }
            }
        };
        Scheduler.get().scheduleDeferred(command);

    }

    /**
     * set the configuration info retrieved from
     * metadata that defines the chart display
     *
     * @param configData - DataType with config info
     */
    @Override
    public void setConfigData(final ChartDataType configData) {
        graphConfigData = configData;
    }

    @Override
    public void addSubTitle(final String subTitle) {
        this.subTitle = wrapString(subTitle, SUB_TITLE_LINE_WRAP_LENGTH);
    }

    /**
     * get the number of rows of data that
     * the Chart contains in its bound data object
     *
     * @return row count for charts direct from model
     */
    @Override
    public int getChartRowCount() {
        return (chartConfig == null ? 0 : chartConfig.getRowCount());
    }

    /**
     * Call to model to show and hide certain elements
     * Note doing the "bind" too = no need for caller to do it
     *
     * @param chartElementDetails - Chart Element Details to be shown/hidden
     * @return true  - if line element changed (added or removed)
     */
    @Override
    public boolean showChartElements(final Set<ChartElementDetails> chartElementDetails) {
        this.selectedChartElementIds = new HashSet<String>();
        for (final ChartElementDetails ced : chartElementDetails) {
            this.selectedChartElementIds.add(ced.getElementId());
        }
        if (chartConfig != null) {
            bind();
            /** If the chart is already rendered then render again with the new model. If not then let the
             * OnResize method render it, as this will prevent the resize of the window (which happens in GXT after the 
             * window is rendering) from causing the below render to flicker as it resizes halfway through rendering.
             * Also only render when this component is visible - chart will throw exception if container is disabled while
             * attempting to render.
             */
            if (ChartRegistry.get().containsChart(highChartsJS.getChartName()) && this.modelUpdated && view.isVisible()) {
                this.highChartsJS.doRender();
                modelUpdated = false;
            }
            return true;
        }
        return false;
    }

    /**
     * @return true  - if legend parameter changed
     */
    @Override
    public boolean hideShowChartLegend() {
        /**
         * Change the Javascript internal String for the chart and re-render. Have to do re-render everytime the 
         * JS internal String is updated. Move to JS overlays would remove this necessity, just need to redraw.
         */
        if (chartConfig != null) {
            this.chartConfig.toggleLegendEnabled();
            this.highChartsJS.doRender();
            return true;
        }
        return false;
    }

    /*
     * determine the chart configuration to use based on
     * the displayTypeStr of the chart
     */
    protected ChartConfigTemplate getChartConfiguration() {
        // meta data strings (no enum)
        if (chartConfig == null) {
            chartConfig = templateRegistry.createByType(ChartDisplayType.fromString(displayTypeStr));
            if (displayTypeStr.equalsIgnoreCase(DVTP))
                graphConfigData.parameters = new ParametersDataType(dvtpChartParameters);

            chartConfig.init(highChartsJS, graphConfigData, seriesMap);
        }

        return chartConfig;
    }

    /* 
     * To avoid having to change code dependent on this implementation of the IChartPresenter interface, retrieve the required data from
     * the PointClickEvent and put it into the GXT ChartListener event. This is a temporary measure used while we have both GXT
     * and HighCharts implementations at the same time, and will save us having to have two different listeners in handler classes
     * for chart events. When GXT Charts are completely remove there will be only one HighCharts listener and the handler classes will 
     * be changed for this new listener.
     * Listener
     * (non-Javadoc)
     * @see com.ericsson.eniq.events.highcharts.client.events.IHighChartsEventListener#onPointClick(com.ericsson.eniq.events.highcharts.client.events.PointClickEvent)
     */
    @Override
    public void onPointClick(final PointClickEvent pointClickEvent) {
        final Map<String, String> drillDownDataMap = new HashMap<String, String>();
        //pass the chartID that has been clicked
        drillDownDataMap.put(CHART_META_ID, (pointClickEvent.getProperty(CHART_META_ID).toString()));
        drillDownDataMap.put(CHART_ELEMENT_SELECTED_KEY, pointClickEvent.getProperty(CHART_ELEMENT_SELECTED_KEY)
                .toString());

        if (pointClickEvent.getProperty(QueryParamOptions.OBJECT_ID) != null) {
            getQueryParameters(pointClickEvent, drillDownDataMap);
        }
        drillManager.getDrillDownInfo(DrillManager.getRowMap(seriesMap,
                (int) pointClickEvent.getDouble(DATA_POINT_ROW_INDEX)),
                pointClickEvent.getProperty(CHART_ELEMENT_DRILLDOWN_KEY).toString(), new Point(pointClickEvent
                .getNativeEvent().getPageX(), pointClickEvent.getNativeEvent().getPageY()),
                new IDrillCallback() {

                    @Override
                    public void onDrillDownSelected(String drillDownTargetId) {
                        drillDownDataMap.put(CHART_ELEMENT_DRILLDOWN_KEY, drillDownTargetId);
                        if (chartListener != null) {
                            chartListener.drillDown(drillDownDataMap);
                        }
                    }
                });
    }

    private void getQueryParameters(final PointClickEvent pointClickEvent, final Map<String, String> drillDownDataMap) {
        @SuppressWarnings("unchecked")
        final JsArray<JavaScriptObject> paramArray = (JsArray<JavaScriptObject>) pointClickEvent
                .getProperty(QueryParamOptions.OBJECT_ID);
        for (int i = 0; i < paramArray.length(); i++) {
            final JavaScriptObject jso = paramArray.get(i);
            drillDownDataMap.put((String) HighChartUtils.getProperty(jso, "name"),
                    HighChartUtils.getProperty(jso, "value").toString());
        }
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.charts.IChartUiHandler#detach()
     */
    @Override
    public void onDetach() {
        /**
         * Clean up listeners and registry
         */
        PointEventHandler.get().removeListener(highChartsJS.getChartName());
        ChartRegistry.get().removeChart(highChartsJS.getChartName());
    }

    /**
     * Configure the Scrolling options of the Chart Container. If number of points beyond a threshold and the chart does not
     * support zooming, a scrollable container is required.
     *
     * @param width
     * @param height
     */
    private void configureScrollableContainer(final int width, final int height) {
        /** If scrollable chart necessary, pass in the parent height and width and let the chart calculate its width
         * based on the number of xaxis categories. Set overflow to scroll on container div **/
        if (chartConfig != null && chartConfig.requiresScrollableChart()) {
            view.setHorizontalScrollEnabled(true);
            chartConfig.renderScrollableChart(width, height);
        } else {
            view.setHorizontalScrollEnabled(false);
        }
    }

    /**
     * Resize the scrollable chart based on the parents new width and height.
     *
     * @param width
     * @param height
     */
    private void resizeScrollableChart(final int width, final int height) {
        if (width > chartConfig.getWidth()) {
            /** Normal chart window now, no scrolling **/
            view.setHorizontalScrollEnabled(false);
            highChartsJS.doResize(width, height);
        } else {
            /** Scrolling is back **/
            view.setHorizontalScrollEnabled(true);
            highChartsJS.doResize(chartConfig.getWidth(), height - CHART_WINDOW_SCROLLBAR_OFFSET);
        }
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.charts.IChartPresenter#setVisible(boolean)
     */
    @Override
    public void setVisible(final boolean visible) {
        view.asWidget().setVisible(visible);

    }

    /**
     * Use native wrap String as this is used already for HighCharts labels so lets use it here also,
     * for a consistent implementation.
     *
     * @param str    String to be wrapped
     * @param length
     * @return
     */
    private final native String wrapString(String str, int length) /*-{
        return $wnd.wrapString(str, length);
    }-*/;

    @Override
    public void onResize(final PortletResizeEvent portletAddEvent) {
        final Widget v = view.asWidget();
        if (v != null && v.isVisible()) {
            onResize(v.getOffsetWidth(), v.getOffsetHeight());
        }
    }
}
