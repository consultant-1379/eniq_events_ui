/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;
import static com.ericsson.eniq.events.ui.client.dashboard.portlet.MapPresenter.MapAttrNamesEnum.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.inject.Inject;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.Pixel;
import org.gwtopenmaps.openlayers.client.RenderIntent;
import org.gwtopenmaps.openlayers.client.control.SelectFeature;
import org.gwtopenmaps.openlayers.client.control.SelectFeature.ClickFeatureListener;
import org.gwtopenmaps.openlayers.client.control.SelectFeature.SelectFeatureListener;
import org.gwtopenmaps.openlayers.client.control.SelectFeature.UnselectFeatureListener;
import org.gwtopenmaps.openlayers.client.control.SelectFeatureOptions;
import org.gwtopenmaps.openlayers.client.event.LayerLoadEndListener;
import org.gwtopenmaps.openlayers.client.event.LayerLoadStartListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.Vector;

import com.ericsson.eniq.events.common.client.map.MapStyleHelper.StyleMapEnum;
import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.common.client.service.MapService;
import com.ericsson.eniq.events.common.client.service.MapServiceImpl;
import com.ericsson.eniq.events.ui.client.dashboard.DashboardPresenter;
import com.ericsson.eniq.events.ui.client.dashboard.PortletTemplate;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.events.SearchFieldTypeChangeEvent;
import com.ericsson.eniq.events.ui.client.events.SearchFieldTypeChangeEventHandler;
import com.ericsson.eniq.events.ui.client.events.SearchFieldValueResetEvent;
import com.ericsson.eniq.events.ui.client.events.SearchFieldValueResetEventHandler;
import com.ericsson.eniq.events.ui.client.events.dashboard.MapMaskEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.MapResizeEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.MapResizeEventHandler;
import com.ericsson.eniq.events.ui.client.events.dashboard.MapUnMaskEvent;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Popup;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for MapView
 *
 * @author ejedmar
 */
public class MapPresenter extends BasePresenter<MapView> implements PortletTemplate {

    /**
     * Map configuration parameters
     *
     * @author ejedmar
     * @since 2011
     */
    enum MapAttrNamesEnum {
        MAP_LAYER("mapLayer"), RNCIDS_LAYER("rncidsLayer"), CELLS_LAYER("cellsLayer"), RNCID("rncid"), CELLID("cellid"), SRS(
                "srs");

        private final String attrName;

        private MapAttrNamesEnum(final String attrName) {
            this.attrName = attrName;
        }

        public String attrName() {
            return attrName;
        }
    }

    /* assumes will only have one map per tab ever */
    private final static String MAP_PORTLET_ID = "MAP_PORTLET_ID";

    private final MapService mapService;

    private PortletDataType descriptor;

    private final HandlerFactory handlerFactory;

    private final Stack<Boolean> layersLoadingStack = new Stack<Boolean>();

    /*
     * tab id Required for event bus guard
     */
    private String tabOwnerId;

    /*
     * for event bus guard when want to know if PlaceHolder in place (no map)
     */
    private DashboardPresenter dashboardPresenter;

    @Inject
    public MapPresenter(final EventBus eventBus, final MapView view, final MapService mapService) {
        super(view, eventBus);

        this.mapService = mapService;

        this.handlerFactory = new HandlerFactory();
        //this.dashboardPresenter = dashboardPresenter;
    }

    @Override
    public Widget asWidget() {
        return getView().asWidget();
    }

    @Override
    public void init(final PortletDataType portletData) {
        this.descriptor = portletData;

        this.tabOwnerId = descriptor.getTabOwnerId();

        bind();
        if (mapService.isAvailable()) {
            addBaseLayer();
            addRncLayer();

            final EventBus eventBus = getEventBus();

            eventBus.addHandler(SearchFieldValueResetEvent.TYPE, new SearchFieldValueChangeEventHandlerImpl());
            eventBus.addHandler(SearchFieldTypeChangeEvent.TYPE, new SearchFieldTypeChangeEventHandlerImpl());

            // TODO watch lack of guards if more maps
            eventBus.addHandler(MapResizeEvent.TYPE, new MapResizeEventHandler() {
                @Override
                public void mapResize(final MapResizeEvent event) {
                    refreshMap();
                }
            });
        }
    }

    private void refreshMap() {
        addBaseLayer();
    }

    private void maskIfNecessary() {

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                if (layersLoadingStack.empty()) {
                    getEventBus().fireEvent(new MapMaskEvent());
                }
                layersLoadingStack.push(true);
            }
        });

    }

    private void unmaskIfNecessary() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                if (!layersLoadingStack.empty()) {
                    layersLoadingStack.pop();
                }

                if (layersLoadingStack.empty()) {
                    getEventBus().fireEvent(new MapUnMaskEvent());
                }
            }
        });
    }

    private String getParam(final MapAttrNamesEnum attrNameEnum) {
        return descriptor.getParameters().getParameter(attrNameEnum.attrName());
    }

    private void addBaseLayer() {
        // Remove layer if any before adding new base layer
        final Map map = getView().getMap();
        final Layer bLayer = map.getLayerByName(getParam(MAP_LAYER));
        if (bLayer != null) {
            map.removeLayer(bLayer);
        }

        final Layer baseLayer = getView().addBaseLayer(MapServiceImpl.WMS_URL, getParam(MAP_LAYER), getParam(SRS));
        baseLayer.addLayerLoadStartListener(new LayerLoadStartListener() {
            @Override
            public void onLoadStart(final LoadStartEvent eventObject) {
                maskIfNecessary();
            }
        });
        baseLayer.addLayerLoadEndListener(new LayerLoadEndListener() {
            @Override
            public void onLoadEnd(final LoadEndEvent eventObject) {
                unmaskIfNecessary();
            }
        });
        map.addLayer(baseLayer);
    }

    private void addRncLayer() {
        maskIfNecessary();
        mapService.getMarkers(getParam(RNCIDS_LAYER), new DefaultRncCallback());
    }

    /**
     * @param vectorLayer
     */
    private void addEventControls(final Vector vectorLayer, final ClickFeatureListener clickFeatureListener,
            final String attrName) {// NOPMD
        // by
        // ejedmar
        // on
        // 14/11/11
        // 12:38,
        // it
        // is
        // a
        // org.gwtopenmaps.openlayers.client.layer.Vector
        final SelectFeature selectFeatureHover = new SelectFeature(vectorLayer, createSelectFeatureOptions(true,
                clickFeatureListener, attrName));
        final SelectFeature selectFeatureClick = new SelectFeature(vectorLayer, createSelectFeatureOptions(false,
                clickFeatureListener, attrName));
        getView().getMap().addControl(selectFeatureClick);
        getView().getMap().addControl(selectFeatureHover);

        selectFeatureHover.activate();
        selectFeatureClick.activate();
    }

    //TODO popup for testing
    final Popup popup = new Popup();

    private SelectFeatureOptions createSelectFeatureOptions(final boolean isHover,
            final ClickFeatureListener clickFeatureListener, final String attrName) {
        final SelectFeatureOptions selectFeatureOptions = new SelectFeatureOptions();

        if (isHover) {
            selectFeatureOptions.setHover();
            selectFeatureOptions.setHighlightOnly(true);
            selectFeatureOptions.setRenderIntent(RenderIntent.TEMPORARY);
            //TODO popup for testing - it's probably not the best way to implement it
            selectFeatureOptions.onSelect(new SelectFeatureListener() {

                @Override
                public void onFeatureSelected(final VectorFeature vectorFeature) {
                    final String attribute = vectorFeature.getAttributes().getAttributeAsString(attrName);
                    if (popup.getItemCount() == 0) {
                        popup.addText(attribute);
                    } else {
                        final Html html = (Html) popup.getItem(0);
                        html.setHtml(attribute);
                    }
                    final int absoluteTop = getView().getAbsoluteTop();
                    final Pixel pixelFromLonLat = getView().getMap()
                            .getPixelFromLonLat(vectorFeature.getCenterLonLat());
                    popup.showAt(pixelFromLonLat.x(), absoluteTop + pixelFromLonLat.y() - 32);
                }
            });
            selectFeatureOptions.onUnSelect(new UnselectFeatureListener() {

                @Override
                public void onFeatureUnselected(final VectorFeature vectorFeature) {
                    popup.hide();
                }
            });
        }

        selectFeatureOptions.clickFeature(clickFeatureListener);
        return selectFeatureOptions;
    }

    private String buildFilterByCorrelationId(final String attrName, final String correlationId) {
        return new StringBuilder().append("(").append(attrName).append("=").append("'").append(correlationId)
                .append("'").append(")").toString();
    }

    /**
     * Feature selected event - rncid selected, so query for associated cells
     * and zoom in.
     *
     * @param vectorFeature
     */
    public void onRncSelected(final VectorFeature vectorFeature) {
        final String correlationId = vectorFeature.getAttributes().getAttributeAsString(getParam(RNCID));
        //TODO refactor + move attribute names to UIMetadata
        final String rat = vectorFeature.getAttributes().getAttributeAsString("rat");
        final String vendor = vectorFeature.getAttributes().getAttributeAsString("vendor");
        fireEvent(new StringBuilder().append(correlationId).append(",").append(vendor).append(",").append(rat)
                .toString(), CONTROLLER_TYPE);

        maskIfNecessary();
        try {
            mapService.getMarkers(getParam(CELLS_LAYER), new AsyncCallback<JavaScriptObject>() {

                @Override
                public void onFailure(final Throwable caught) {
                    unmaskIfNecessary();
                }

                @Override
                public void onSuccess(final JavaScriptObject result) {
                    try {
                        final Vector addCellLayer = addCellLayer(result);
                        getView().hideOthersThan(addCellLayer.getName());
                        getView().getMap().zoomToExtent(addCellLayer.getDataExtent());
                        addEventControls(addCellLayer, createClickCellListener(), getParam(CELLID));
                    } finally {
                        unmaskIfNecessary();
                    }
                }
            }, buildFilterByCorrelationId(getParam(RNCID), correlationId));
        } catch (final Exception e) {
            e.printStackTrace();
            unmaskIfNecessary();
        }
    }

    /**
     * Feature selected event - rncid selected, so query for associated cells
     * and zoom in.
     *
     * @param vectorFeature
     */
    public void onCellSelected(final VectorFeature vectorFeature) {
        try {
            final String correlationId = vectorFeature.getAttributes().getAttributeAsString(getParam(CELLID));
            fireEvent(correlationId, SEARCH_FIELD_CELL_TYPE);
        } catch (final Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }
    }

    /**
     * @param result
     */
    private Vector addCellLayer(final JavaScriptObject result) {// NOPMD by
        // ejedmar on
        // 14/11/11
        // 12:38, it is
        // a
        // org.gwtopenmaps.openlayers.client.layer.Vector
        final Vector vectorLayer = getView().addVectorLayer(getParam(CELLS_LAYER), result, StyleMapEnum.CELLS_STYLE);
        return vectorLayer;

    }

    private void fireEvent(final String correlationId, final String type) {
        final String[] urlParams = { "node=" + correlationId, "type=" + type };
        final String searchFieldVal = String.valueOf(correlationId);

        final SearchFieldDataType data = new SearchFieldDataType(searchFieldVal, urlParams, type, null, false, "",
                null, false);
        /* handled in this class */
        getEventBus().fireEvent(new SearchFieldValueResetEvent(descriptor.getTabOwnerId(), MAP_PORTLET_ID, data, ""));
    }

    /**
     * Event handler factory
     *
     * @author ejedmar
     * @since 2011
     */
    private class HandlerFactory {

        /**
         * Gets handler basing on data information
         *
         * @param data
         * @return handler
         */
        public SearchHandler getHandler(final SearchFieldDataType data) {
            if (isRncGroup(data)) {
                return new BscHandler(extractGroupIds(data.getGroupValues(), 1, 3, ":"));
            } else if (isCellGroup(data)) {
                return new CellHandler(extractIds(data.getGroupValues(), 3, ":"));
            } else if (isRnc(data)) {
                return new BscHandler(extractIds(Arrays.asList(data.searchFieldVal), 0, ","));
            } else if (isCell(data)) {
                return new CellHandler(extractIds(Arrays.asList(data.searchFieldVal), 0, ","));
            } else {
                return new EmptyHandler();
            }
        }

        private boolean isCell(final SearchFieldDataType data) {
            return !data.isGroupMode() && SEARCH_FIELD_CELL_TYPE.equals(data.getType());
        }

        private boolean isRnc(final SearchFieldDataType data) {
            return !data.isGroupMode() && CONTROLLER_TYPE.equals(data.getType());
        }

        private boolean isCellGroup(final SearchFieldDataType data) {
            return data.isGroupMode() && SEARCH_FIELD_CELL_TYPE.equals(data.getType());
        }

        private boolean isRncGroup(final SearchFieldDataType data) {
            return data.isGroupMode() && CONTROLLER_TYPE.equals(data.getType());
        }

        //TODO refactor fast solution, but it shouldn't be done here
        private Collection<String> extractIds(final Collection<String> elements, final int index, final String sep) {
            final List<String> ids = new ArrayList<String>();
            final Iterator<String> iterator = elements.iterator();
            while (iterator.hasNext()) {
                final String elem = iterator.next();
                ids.add(elem.split(sep)[index]);
            }
            return ids;
        }

        //TODO refactor fast solution, but it shouldn't be done here
        private Collection<String> extractGroupIds(final Collection<String> elements, final int startIndex,
                final int endIndex, final String sep) {
            final List<String> ids = new ArrayList<String>();
            final Iterator<String> iterator = elements.iterator();
            while (iterator.hasNext()) {
                final String elem = iterator.next();
                final String[] split = elem.split(sep);
                final StringBuilder sb = new StringBuilder();//NOPMD ejedmar - temporary solution
                for (int i = startIndex; i <= endIndex; i++) {
                    sb.append(split[i]);
                    if (i + 1 <= endIndex) {
                        sb.append(sep);
                    }
                }
                ids.add(sb.toString());
            }
            return ids;
        }
    }

    /**
     * Handler interface
     *
     * @author ejedmar
     * @since 2011
     */
    interface SearchHandler {
        void handle();
    }

    /**
     * Handler for Rncid Group event
     *
     * @author ejedmar
     * @since 2011
     */
    private class BscHandler implements SearchHandler {

        private final Collection<String> elements;

        public BscHandler(final Collection<String> elements) {
            this.elements = elements;
        }

        @Override
        public void handle() {
            maskIfNecessary();
            mapService.getMarkers(getParam(RNCIDS_LAYER), new HighlightRncCallback(elements));
        }
    }

    /**
     * Convenience empty handler
     *
     * @author ejedmar
     * @since 2011
     */
    private class EmptyHandler implements SearchHandler {

        @Override
        public void handle() {
        }
    }

    /**
     * Handler for Cell group
     *
     * @author ejedmar
     * @since 2011
     */
    private class CellHandler implements SearchHandler {

        private final Collection<String> elements;

        public CellHandler(final Collection<String> elements) {
            this.elements = elements;
        }

        @Override
        public void handle() {
            maskIfNecessary();
            try {
                mapService.getMarkers(getParam(CELLS_LAYER), new AsyncCallback<JavaScriptObject>() {
                    @Override
                    public void onFailure(final Throwable caught) {
                        unmaskIfNecessary();
                    }

                    @Override
                    public void onSuccess(final JavaScriptObject result) {
                        try {
                            getView().removeAllExceptBase();
                            final Vector groupCells = getView().buildVectorLayer(getParam(CELLS_LAYER), result,
                                    StyleMapEnum.CELLS_STYLE);
                            getView().getMap().zoomToExtent(groupCells.getDataExtent());

                            mapService.getMarkersByBBox(getParam(CELLS_LAYER), new AsyncCallback<JavaScriptObject>() {
                                @Override
                                public void onFailure(final Throwable caught) {
                                    unmaskIfNecessary();
                                }

                                @Override
                                public void onSuccess(final JavaScriptObject result1) {
                                    try {
                                        final Vector otherCells = addCellLayer(result1);
                                        if (elements != null) {
                                            getView().highlightGroup(getParam(CELLS_LAYER), elements, getParam(CELLID),
                                                    StyleMapEnum.CELLS_STYLE);
                                        }
                                        addEventControls(otherCells, createClickCellListener(), getParam(CELLID));
                                    } finally {
                                        unmaskIfNecessary();
                                    }
                                }
                            }, bbox(groupCells));
                        } catch (final Exception e) {
                            unmaskIfNecessary();
                        }
                    }

                    /**
                     * @param addCellLayer
                     * @return
                     */
                    private String bbox(final Vector layer) {// NOPMD by
                        // ejedmar
                        // on
                        // 16/11/11
                        // 12:38, it
                        // is a
                        // org.gwtopenmaps.openlayers.client.layer.Vector
                        return swapLongitudeWithLatitude(layer.getDataExtent());
                    }

                    /**
                     * @param dataExtent
                     * @return
                     */
                    private String swapLongitudeWithLatitude(final Bounds dataExtent) {
                        return new StringBuilder().append(dataExtent.getLowerLeftY()).append(",")
                                .append(dataExtent.getLowerLeftX()).append(",").append(dataExtent.getUpperRightY())
                                .append(",").append(dataExtent.getUpperRightX()).toString();
                    }
                }, buildFilter(elements));
            } catch (final Exception e) {
                unmaskIfNecessary();
            }

        }

        String buildFilter(final Collection<String> ids) {
            final Iterator<String> iterator = ids.iterator();
            final StringBuilder sb = new StringBuilder();
            sb.append(getParam(CELLID)).append(" IN ").append("(");
            while (iterator.hasNext()) {
                sb.append("'").append(iterator.next()).append("'");
                if (iterator.hasNext()) {
                    sb.append(",");
                }
            }
            sb.append(")");
            return sb.toString();
        }
    }

    private SelectFeature.ClickFeatureListener createClickRncListener() {
        return new SelectFeature.ClickFeatureListener() {
            @Override
            public void onFeatureClicked(final VectorFeature vectorFeature) {
                onRncSelected(vectorFeature);
            }
        };
    }

    private SelectFeature.ClickFeatureListener createClickCellListener() {
        return new SelectFeature.ClickFeatureListener() {
            @Override
            public void onFeatureClicked(final VectorFeature vectorFeature) {
                onCellSelected(vectorFeature);
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ericsson.eniq.events.ui.client.dashboard.PortletTemplate#update(com
     * .google.gwt.json.client.JSONValue,
     * com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType,
     * com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType)
     */
    @Override
    public void update(final JSONValue data, final SearchFieldDataType windowSearchData,
            final TimeInfoDataType windowTimeData) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ericsson.eniq.events.ui.client.dashboard.PortletTemplate#
     * getSearchFieldData()
     */
    @Override
    public SearchFieldDataType getSearchFieldData() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ericsson.eniq.events.ui.client.dashboard.PortletTemplate#getTimeFieldData
     * ()
     */
    @Override
    public TimeInfoDataType getTimeFieldData() {
        return null;
    }

    private class DefaultRncCallback implements AsyncCallback<JavaScriptObject> {
        @Override
        public void onFailure(final Throwable caught) {
            unmaskIfNecessary();
        }

        @Override
        public void onSuccess(final JavaScriptObject result) {
            try {
                addRncIds(result);
            } finally {
                unmaskIfNecessary();
            }
        }

        /**
         * @param result
         */
        public void addRncIds(final JavaScriptObject result) {
            getView().reset();
            final Vector vectorLayer = getView().addVectorLayer(getParam(RNCIDS_LAYER), result,
                    StyleMapEnum.RNCIDS_STYLE);
            final Bounds bounds = vectorLayer.getDataExtent();
            if (bounds != null) { // seeing null pointer
                getView().getMap().zoomToExtent(bounds);
            }
            addEventControls(vectorLayer, createClickRncListener(), getParam(RNCID));

        }
    }

    private class HighlightRncCallback extends DefaultRncCallback {

        private final Collection<String> elements;

        public HighlightRncCallback(final Collection<String> elements) {
            this.elements = elements;
        }

        @Override
        public void onSuccess(final JavaScriptObject result) {
            try {
                super.addRncIds(result);
                if (elements != null) {
                    getView().highlightGroup(getParam(RNCIDS_LAYER), elements, getParam(RNCID),
                            StyleMapEnum.RNCIDS_STYLE);
                }
            } finally {
                unmaskIfNecessary();
            }
        }
    }

    /*
     * Paired Search component update value update handling
     */
    private class SearchFieldValueChangeEventHandlerImpl implements SearchFieldValueResetEventHandler {
        @Override
        public void handleSearchFieldParamUpdate(final String tabId, final String queryId,
                final SearchFieldDataType data, final String url) {

            // guard against other tab's search field value changes
            if (!tabOwnerId.equals(tabId)) {
                return;
            }

            // guard in case a PlaceHolder 
            if (dashboardPresenter.getMapWindow() == null) {
                return;
            }

            // guard against all other windows listening to search 
            // value change in this tab 
            if (!MAP_PORTLET_ID.equals(queryId)) {
                return;
            }

            handlerFactory.getHandler(data).handle();
        }
    }

    /*
     * Paired search component search TYPE change handling 
     */
    private class SearchFieldTypeChangeEventHandlerImpl implements SearchFieldTypeChangeEventHandler {

        @Override
        public void handleTypeChanged(final String tabId, final String typeSelected, final boolean isGroup,
                final String typeText) {

            // guard against other tab's search field type changes
            // (they may not have summary type yet but no point in being lax)
            if (!tabOwnerId.equals(tabId)) {
                return;
            }

            // guard in case a PlaceHolder 
            if (dashboardPresenter.getMapWindow() == null) {
                return;
            }

            if (SearchFieldDataType.isSummaryType(typeSelected)) {

                addRncLayer();
            }

        }

    }

    public void setDashboardPresenter(final DashboardPresenter dashboardPresenter2) {
        this.dashboardPresenter = dashboardPresenter;
    }
}