/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.events.TimeParameterValueChangeEvent;
import com.ericsson.eniq.events.ui.client.gin.EniqEventsUIGinjector;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.JsonReader;
import com.extjs.gxt.ui.client.data.MemoryProxy;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.web.bindery.event.shared.EventBus;

/**
 * ComboBox for selecting a time range to change the Time Parameters for the Event Data.
 * Contains a selection listener that fires selction event to event bus.
 * 
 * @author esuslyn
 * @since July 2010
 */
public class TimeRangeComboBox extends ComboBox<ModelData> {

    private final EniqEventsUIGinjector injector = MainEntryPoint.getInjector();

    private final static String CMB_TIME_DISPLAY = "display";

    private final static String CMB_TIME_VALUE = "value";

    private final static String CMB_TIME_DATA = "data";

    private final static String CMB_TIME_STYLE = "cmbTimeRange";

    private TimeRangeComboBoxListener timeComboListener;

    /**
     * Loads the JSON Data that will be used
     * as the datasource for the Time Range Combo box
     */

    public void init() {
        setupComboBoxProperties();
        setupComboBoxStore(injector.getMetaReader().getTimeComboData());
    }

    public void initWithMetadata(final String metadata) {
        setupComboBoxProperties();
        setupComboBoxStore(metadata);
    }

    /**
     * Add Selection listener to Combo Box to request Event Data when time range selected
     * 
     * @param multiWinId  - support for multiple window instances
     *                      assuming search data can not change
     * @param eventBus  - eventbus (the same one used thoughout)
     * 
     */
    public void addSelectionEventHandler(final MultipleInstanceWinId multiWinId, final EventBus eventBus) {
        timeComboListener = new TimeRangeComboBoxListener(multiWinId, eventBus);
        this.addListener(Events.SelectionChange, timeComboListener);
    }

    /**
     * Remove Selection listener to Combo Box for clean up on window close 
     * (general good practice in case memory resource issues)
     * 
     * (could have used #removeAllListeners() to avoid the local variable but whatever.. )
     */
    public void removeSelectionEventHandler() {
        if (timeComboListener != null) {
            removeListener(Events.SelectionChange, timeComboListener);
        }
    }

    /**
     * Only need to apply this style to Time Range Combo Box 
     * when displayed in the Time Settings Window 
     * (so needed to be extracted from setupComboBoxProperties())
     */
    public void setStyleForTimeSettingWindow() {
        //    this.addStyleName(CMB_TIME_STYLE);
    }

    /////////////
    // private methods and classes
    /////////////

    private void setupComboBoxProperties() {
        this.setDisplayField(CMB_TIME_DISPLAY);
        this.setValueField(CMB_TIME_VALUE);
        this.setAllowBlank(false);
        this.setAutoValidate(true);
        this.setLazyRender(false);
    }

    private void setupComboBoxStore(final String cmbData) {
        final MemoryProxy<String> proxy = new MemoryProxy<String>(cmbData);
        final ModelType type = new ModelType();
        type.setRoot(CMB_TIME_DATA);
        type.addField(CMB_TIME_DISPLAY);
        type.addField(CMB_TIME_VALUE);

        final JsonReader<ModelType> jsonReader = new JsonReader<ModelType>(type);
        // reading up on this seems to indicate a bug in generics on this BaseListLoader to adding unchecked
        final BaseListLoader loader = new BaseListLoader(proxy, jsonReader);
        store = new ListStore<ModelData>(loader);
        this.setStore(store);
        loader.load();
    }

    public static TimeInfoDataType extractUserTimeDetailsFromEvent(final ModelData timeComboSelection) {
        final TimeInfoDataType userTimeDetails = new TimeInfoDataType();
        userTimeDetails.timeRange = (String) timeComboSelection.get(CMB_TIME_VALUE);
        userTimeDetails.timeRangeDisplay = (String) timeComboSelection.get(CMB_TIME_DISPLAY);
        return userTimeDetails;
    }

    public String getDisplay(final ModelData model) {
        return model.get(CMB_TIME_DISPLAY).toString();
    }

    public String getValue(final ModelData model) {
        return model.get(CMB_TIME_VALUE).toString();
    }

    public int getIndex(final ModelData model) {
        return getStore().indexOf(model);
    }

    /**
     * 
     * Selection Listener on time range combo box, 
     * firing event onto event bus for external handling
     *
     */
    private class TimeRangeComboBoxListener implements Listener<BaseEvent> {

        public final MultipleInstanceWinId multiWinId;

        private final EventBus eventBus;

        /**
         * @param multiWinId  -
         * @param eventBus   - the event bus
         */
        public TimeRangeComboBoxListener(final MultipleInstanceWinId multiWinId, final EventBus eventBus) {
            super();
            this.multiWinId = multiWinId;
            this.eventBus = eventBus;
        }

        /* (non-Javadoc)
         * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
         */
        @Override
        public void handleEvent(final BaseEvent be) {
            final ModelData timeComboSelection = ((TimeRangeComboBox) be.getSource()).getValue();
            final TimeInfoDataType userTimeDetails = extractUserTimeDetailsFromEvent(timeComboSelection);
            /* reset so next time window launches uses cached parameter for time */
            userTimeDetails.timeRangeSelectedIndex = TimeRangeComboBox.this.getStore().indexOf(timeComboSelection);
            eventBus.fireEvent(new TimeParameterValueChangeEvent(multiWinId, userTimeDetails));
        }

    }
}
