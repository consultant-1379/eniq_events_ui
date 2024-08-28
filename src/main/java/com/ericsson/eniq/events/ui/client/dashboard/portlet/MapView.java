/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import java.util.Collection;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.control.MousePosition;
import org.gwtopenmaps.openlayers.client.control.MousePositionOptions;
import org.gwtopenmaps.openlayers.client.control.MousePositionOutput;
import org.gwtopenmaps.openlayers.client.control.PanZoomBar;
import org.gwtopenmaps.openlayers.client.format.GeoJSON;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;
import org.gwtopenmaps.openlayers.client.util.JSObject;

import com.ericsson.eniq.events.common.client.map.MapStyleHelper.StyleMapEnum;
import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * View container for a map 
 *
 * @author ejedmar
 */
public class MapView extends BaseView<MapPresenter> {

    interface MapViewUiBinder extends UiBinder<Widget, MapView> {
    }

    private static MapViewUiBinder uiBinder = GWT.create(MapViewUiBinder.class);

    @UiField
    SimplePanel mapContainer;

    private static final String SRS = "srs";

    private static final String FORMAT = "image/png";

    private MapWidget mapWidget;

    public MapView() {
        initWidget(uiBinder.createAndBindUi(this));
        initMapWidget();
    }

    public Map getMap() {
        return mapWidget.getMap();
    }

    private void initMapWidget() {
        final MapOptions mapOptions = new MapOptions();
        mapOptions.setNumZoomLevels(16);
        mapOptions.removeDefaultControls();

        mapWidget = new MapWidget("100%", "100%", mapOptions);
        getMap().addControl(new PanZoomBar());
        final MousePositionOutput mpOut = new MousePositionOutput() {
            @Override
            public String format(final LonLat lonLat, final Map map) {
                String out = "";
                out += "<b>This is the longitude </b> ";
                out += lonLat.lon();
                out += "<b>, and this the latitude</b> ";
                out += lonLat.lat();
                return out;
            }
        };
        final MousePositionOptions mpOptions = new MousePositionOptions();
        mpOptions.setFormatOutput(mpOut);
        getMap().addControl(new MousePosition(mpOptions));
        mapContainer.setWidget(mapWidget);
    }

    /**
     * Adds base layer(an image)
     *
     * @param url       GeoServer WMS service uri
     * @param layerName requested layer
     * @param srs       projection TODO move tile flag
     */
    public Layer addBaseLayer(final String url, final String layerName, final String srs) {
        final WMSParams wmsParams = new WMSParams();
        wmsParams.setFormat(FORMAT);
        wmsParams.setLayers(layerName);
        //		wmsParams.setParameter("tiled", "true");
        wmsParams.setParameter(SRS, srs);

        final WMSOptions wmsOptions = new WMSOptions();
        wmsOptions.setSingleTile(true);
        wmsOptions.setIsBaseLayer(true);
        final WMS baseLayer = new WMS(layerName, url, wmsParams, wmsOptions);
        return baseLayer;
    }

    /**
     * Adds a vector layer with a specified name <i>layerName</i>, populates this layer with <i>data</i> and
     * applies style map <i>styleMapEnum</i> to this layer.
     * @param layerName layer name
     * @param data feature data
     * @param styleMapEnum style map
     */
    public Vector addVectorLayer(final String layerName, final JavaScriptObject data, final StyleMapEnum styleMapEnum) {// NOPMD by ejedmar on 16/11/11 12:38, it is a org.gwtopenmaps.openlayers.client.layer.Vector
        final Layer layerByName = getMap().getLayerByName(layerName);
        if (layerByName != null) {
            getMap().removeLayer(layerByName);
        }
        final Vector layer = buildVectorLayer(layerName, data, styleMapEnum);
        getMap().addLayer(layer);

        return layer;
    }

    private boolean newToBeCreated(final boolean requiredNew, final Vector layer) {//NOPMD by ejedmar on 11/11/11 17:24, it is a org.gwtopenmaps.openlayers.client.layer.Vector, no interface
        return layer == null || requiredNew;
    }

    public void hideOthersThan(final String layerName) {
        for (final Layer layer : getMap().getLayers()) {
            if (notBaseAndOtherThan(layerName, layer)) {
                layer.setIsVisible(false);
            }
        }
    }

    public void removeAllExceptBase() {
        for (final Layer layer : getMap().getLayers()) {
            if (!layer.isBaseLayer()) {
                getMap().removeLayer(layer);
            }
        }
    }

    private boolean notBaseAndOtherThan(final String layerName, final Layer layer) {
        return !layer.isBaseLayer() && !layerName.equals(layer.getName());
    }

    public Vector buildVectorLayer(final String layer, final JavaScriptObject jso, final StyleMapEnum styleMapEnum) {//NOPMD by ejedmar on 11/11/11 17:24, it is a org.gwtopenmaps.openlayers.client.layer.Vector
        final Vector vectorLayer = new Vector(layer);
        vectorLayer.setIsBaseLayer(false);
        applyStyleMap(vectorLayer.getJSObject(), styleMapEnum.styleMap());
        populateLayer(vectorLayer.getJSObject(), new GeoJSON().getJSObject(), jso);
        return vectorLayer;
    }

    /**
     * GWT Openlayers doesn't wrap this function TODO consider extending Vector
     */
    public final native void populateLayer(JSObject vectorLayer, JSObject geoJson, JavaScriptObject jso) /*-{
		return vectorLayer.addFeatures(geoJson.read(jso));
    }-*/;

    /**
     * GWT Openlayers doesn't wrap OpenLayers.StyleMap(json_data) TODO consider
     * extending StyleMap
     */
    private final native JavaScriptObject applyStyleMap(JSObject layer, JavaScriptObject styleMap) /*-{
		layer.styleMap = styleMap;
    }-*/;

    private final native JavaScriptObject applyStyle(JSObject layer, JavaScriptObject style) /*-{
		layer.style = style;
    }-*/;

    public void reset() {
        removeAllExceptBase();
        resetStyleMap(StyleMapEnum.RNCIDS_STYLE.styleMap());
        resetStyleMap(StyleMapEnum.CELLS_STYLE.styleMap());
    }

    private native void resetStyleMap(JavaScriptObject styleMap) /*-{
		styleMap.styles['default'].rules = [];
    }-*/;

    /** 
     * GWT OpenLayer doesn't wrap this function TODO consider
     * extending StyleMap
     */
    private final native JavaScriptObject applyUniqueValueRules(String key, JavaScriptObject styleMap,
            JavaScriptObject lookup) /*-{
		styleMap.styles['default'].rules = [];
		styleMap.addUniqueValueRules("default", key, lookup);
		styleMap.styles['default'].addRules([ new $wnd.OpenLayers.Rule({
			elseFilter : true
		}) ]);
		return styleMap;
    }-*/;

    private final native JavaScriptObject getHighlighted(JavaScriptObject styleMap) /*-{
		return styleMap.styles['highlight'].defaultStyle;
    }-*/;

    /**
     * Highlights(changes style) for specified layer <i>elements</i>
     * @param layerName layer name
     * @param elements features to be highlighted
     * @param lookupAttrName lookup key
     * @param styleMapEnum style map to be applied
     */
    public void highlightGroup(final String layerName, final Collection<String> elements, final String lookupAttrName,
            final StyleMapEnum styleMapEnum) {
        final Layer layer = getMap().getLayerByName(layerName);
        final JSONObject lookup = new JSONObject();
        for (final String element : elements) {
            lookup.put(element, new JSONObject(getHighlighted(styleMapEnum.styleMap())));//NOPMD by ejedmar 09:06 14/11/2011
        }
        applyStyleMap(layer.getJSObject(),
                applyUniqueValueRules(lookupAttrName, styleMapEnum.styleMap(), lookup.getJavaScriptObject()));
        layer.redraw();
    }

    public void highlight(final Layer layer, final StyleMapEnum styleMapEnum) {
        applyStyle(layer.getJSObject(), getHighlighted(styleMapEnum.styleMap()));
        layer.redraw();
    }
}
