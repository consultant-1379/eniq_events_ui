/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.Date;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.ui.client.common.ReadLoginSessionProperties;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.JsonPagingLoadResultReader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.ScriptTagProxy;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ListView;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Live Load combobox (presenter from MVP pattern) which will be used 
 * in some cases for the search field component.
 * 
 * Alot of source content for live load taken directly from 
 * http://extjs.com/examples/#advancedcombobox
 * 
 * @author eeicmsy
 * @since March 2010
 *
 */
public class LiveLoadComboPresenter extends BasePresenter<ILiveLoadComboView> {

    private ScriptTagProxy<PagingLoadResult<ModelData>> proxy;

    private JsonPagingLoadResultReader<PagingLoadResult<ModelData>> reader;

    private PagingLoader<PagingLoadResult<ModelData>> loader;

    private final Listener<LoadEvent> liveLoadListener = new LiveLoadListener(
            getMaxRowsProperty(CommonConstants.ENIQ_EVENTS_LIVE_LOAD_COUNT));

    String getMaxRowsProperty(final String propertyKey) {
        return ReadLoginSessionProperties.getMaxRowsValue(propertyKey);
    }

    /**
     * Presenter for live load combobox (presenter from MVP pattern) which will be used 
     * in some cases for the search field component.
     * @param display    view part of MVP
     * @param eventBus   EventBus (singleton) required for MVP presenters
     */
    public LiveLoadComboPresenter(final ILiveLoadComboView display, final EventBus eventBus) {
        super(display, eventBus);

    }

    /**
     * Method to call when wish to set up a live loading combobox for the search field
     * 
     * The format of return from server is key:
     * 
     * The server return must pass in the callback transId 
     * in the return and pass the "query" in a parameter to SQL call
     * 
     * Response if using php would be:
     * NOTE :  The server return MUST pass back the callback transId and the rowcount 
     * in the Response formatted as in the echo below (root with ids)!
     * 
     * <?php
     * if (isset($_GET['callback'])) {
     *      $transId = $_GET['callback'];
     *  } else {
     *       echo 'No transId found';
     *       return;
     *  }
     *  echo $transId .'({"totalCount":"2",
     *  "SGSN" : [
     *  {
     *  "id" : "Athlone_SGSN"
     *  },
     *  {
     *  "id" : "Athlone_rocks_SGSN"
     *  }
     *  ]
     *  })';
     *  ?>
     * 
     * 
     * @param url    Restful service url to fetch search field information 
     *               (e.g. ideally one per node type or handsets)
     * @param root   JSON should contain root for node type being searched, 
     *               for example CELL or APN, etc.
     */
    public void setupLiveLoad(final String url, final String root) {

        final ModelType type = getModelType(root);

        proxy = createScriptTagProxy(url);

        reader = createJsonPagingLoadResultReader(type);

        loader = createPagingLoader();

        setupLoader();

        ListStore<ModelData> store;

        final boolean dontLoad = SearchFieldDataType.isSummaryType(root);

        if (dontLoad) {
            store = new ListStore<ModelData>(); // avoid null points and failure to load
        } else {
            store = new ListStore<ModelData>(loader);
        }

        getView().setDisplayField(LIVE_LOAD_ID);

        /* efforts to force a new store when a new store is being set */
        final boolean isReload = !dontLoad && (getView().getStore() != null);
        getView().setStore(store);

        if (isReload) {
            refreshLoader();
            //XXX resetListView(store);  - seems not needed in GXT2.2.4
        }
    }

    /*
     * paging bar must get updated for new store 
     * (i.e. so doesn't keep settings from a previous store)
     */
    private void refreshLoader() {
        getView().getPagingToolBar().bind(loader);
        final PagingLoadConfig config = new BasePagingLoadConfig();
        config.setOffset(0);
        config.setLimit(LIVE_LOAD_PAGING_SIZE);
        loader.load(config);

    }

    /* extract for junit (ListView unmockable) */
    void resetListView(final ListStore<ModelData> store) {
        final ListView<ModelData> listView = getView().getListView();
        listView.clearState();
        listView.setStore(store);
    }

    private ModelType getModelType(final String root) {
        /* this affects what is displayed for "Displaying 1-5 of 5" */
        final ModelType type = new ModelType();
        type.setTotalName(TOTAL_COUNT_NAME);
        type.setRoot(root);
        type.addField(LIVE_LOAD_ID, LIVE_LOAD_ID);
        return type;
    }

    private void setupLoader() {
        loader.setRemoteSort(true);
        loader.setSortField(LIVE_LOAD_ID);

        loader.addListener(Loader.BeforeLoad, liveLoadListener);

    }

    /* extracted for junit */
    ScriptTagProxy<PagingLoadResult<ModelData>> createScriptTagProxy(final String url) {
        return new ScriptTagProxy<PagingLoadResult<ModelData>>(url);
    }

    /* extracted for junit */
    PagingLoader<PagingLoadResult<ModelData>> createPagingLoader() {
        return new BasePagingLoader<PagingLoadResult<ModelData>>(proxy, reader);
    }

    /* extracted for junit */
    JsonPagingLoadResultReader<PagingLoadResult<ModelData>> createJsonPagingLoadResultReader(final ModelType type) {
        return new JsonPagingLoadResultReader<PagingLoadResult<ModelData>>(type);
    }

    ///////////////////////
    //  Methods direct to view 
    //  (set up really to avoid direct Combobox creation to suit 
    //  GXT junit and mocking
    ///////////////////////

    /**
     * Returns the raw data value in live load combobox 
     * which may or may not be a valid, defined value.
     * @return the raw value
     */
    public String getRawValue() {
        return getView().getRawValue();
    }

    /**
     * Sets the default text to display in 
     * live load combobox when it is empty
     * @param emptyText the empty text, e.g. "Enter APN"
     */
    public void setEmptyText(final String emptyText) {
        getView().setEmptyText(emptyText);
    }

    /**
     * Clears any text/value currently set in the
     * live load combobox field
     */
    public void clearSelections() {
        getView().clearSelections();
    }

    /**
     * Check if group or single search field in use
     * (if liveload combobox visible then single mode is in use)
     * @return <code>true</code> if the component is visible.
     */
    public boolean isVisible() {
        return getView().isVisible();
    }

    /**
     * Convenience function to hide or show 
     * the live load combo box 
     * @param isVisible the visible state
     */
    public void setVisible(final boolean isVisible) {
        getView().setVisible(isVisible);
    }

    /**
     * Sets the live load comboes's tool tip.
     * 
     * @param text the text
     */
    public void setToolTip(final String text) {
        getView().setToolTip(text);
    }

    /**
     * Utility to avoid repeat getRawValue checks
     * @return true if display is empty
     */
    public boolean isEmpty() {
        return getView().isEmpty();
    }

    /**
     * @param enable
     */
    public void setEnable(final boolean enable) {
        getView().setEnable(enable);
    }

    class LiveLoadListener implements Listener<LoadEvent> {
        private final String maxRows;

        /**
         * @param maxRows The number of rows to return for live load
         */
        public LiveLoadListener(final String maxRows) {
            this.maxRows = maxRows;
        }

        @Override
        public void handleEvent(final LoadEvent baseEvent) {
            final String tzOffset = DateTimeFormat.getFormat(CommonParamUtil.TIME_ZONE_DATE_FORMAT).format(new Date());
            baseEvent.<ModelData> getConfig().set("start", baseEvent.<ModelData> getConfig().get("offset"));
            baseEvent.<ModelData> getConfig().set("maxRows", maxRows);
            baseEvent.<ModelData> getConfig().set(TIME_ZONE_PARAM_WITHOUT_EQUALS, tzOffset);
        }
    }
}
