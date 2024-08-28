/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.kpi;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.common.widget.TimeRangeComboBox;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.kpi.KPIConfigurationPanelDataType;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ericsson.eniq.events.ui.client.common.Constants.DEFAULT_TIME_RANGE_SELECTED_INDEX;

/**
 * @author eemecoy
 * 
 */
public class KPIConfigurationView extends BaseView<KPIConfigurationPresenter> {

    interface Binder extends UiBinder<Widget, KPIConfigurationView> {

    }

    Binder uiBinder = GWT.create(Binder.class);

    @UiField
    VerticalPanel wrapper;

    @UiField
    Button cancelButton;

    @UiField
    Button updateButton;

    @UiField
    SimplePanel refreshTimeHolder;

    @UiField
    SimplePanel refreshRateHolder;

    TimeRangeComboBox refreshTime = new TimeRangeComboBox();

    TimeRangeComboBox refreshRate = new TimeRangeComboBox();

    private Map<String, TimeRangeComboBox> comboBoxes;

    private KPIMessages messages;

    @Inject
    public KPIConfigurationView(KPIMessages kpiMessages) {
        this.messages = kpiMessages;
        final Widget widget = uiBinder.createAndBindUi(this);
        initWidget(widget);
    }

    public void init() {
        refreshTimeHolder.add(refreshTime);
        refreshRateHolder.add(refreshRate);
        refreshTime.setEditable(false);
        refreshRate.setEditable(false);
        initCombo(refreshTime, messages.selectLastDataTitle());
        initCombo(refreshRate, messages.selectRefreshRateTitle());
        setUpListBoxes();
        setElementsId();
        initListeners();
    }

    private void initListeners() {
        for (final String key : comboBoxes.keySet()) {
            final Listener<? extends BaseEvent> listener = new TimeRangeComboBoxListener(key);
            comboBoxes.get(key).addListener(Events.Blur, listener);
            comboBoxes.get(key).addListener(Events.Focus, listener);
        }
    }

    private void setElementsId() {
        wrapper.getElement().setId("kpiConfigPanel");
        refreshTimeHolder.getElement().setId("refreshTimeCombo");
        refreshRateHolder.getElement().setId("refreshRateCombo");
        cancelButton.getElement().setId("cancelBtn");
        updateButton.getElement().setId("updateBtn");
    }

    private void initCombo(final TimeRangeComboBox comboBox, final String title) {
        //TODO emauoco - This MetaData reading stuff should be in a presenter, not in the view
        final KPIConfigurationPanelDataType configPanelType = MainEntryPoint.getInjector().getMetaReader()
                .getKPIConfigurationPanelMetaData();

        if (comboBox == refreshRate) {
            comboBox.initWithMetadata(configPanelType.getRefreshRate().getComboTimeData());
        } else {
            comboBox.initWithMetadata(configPanelType.getRefreshTime().getComboTimeData());
        }

        // Style is defined in EniqEventsUI.css
        comboBox.addStyleName("kpiConfigTimeComboBox");
        comboBox.setValue(comboBox.getStore().getAt(DEFAULT_TIME_RANGE_SELECTED_INDEX));
        comboBox.setToolTip(title);

    }

    private void setUpListBoxes() {
        initializeListBoxKeys();
        setTriggerOptionOnListBoxes();
        setPreviouslyConfiguredValues();
    }

    private void setTriggerOptionOnListBoxes() {
        for (final TimeRangeComboBox comboBox : comboBoxes.values()) {
            comboBox.setTriggerAction(TriggerAction.ALL);
        }

    }

    private void initializeListBoxKeys() {
        comboBoxes = new HashMap<String, TimeRangeComboBox>();
        comboBoxes.put(KPIConfigurationConstants.REFRESH_TIME, refreshTime);
        comboBoxes.put(KPIConfigurationConstants.REFRESH_RATE, refreshRate);

    }

    private void setPreviouslyConfiguredValues() {
        final Map<String, String> userConfiguredSettings = getPresenter().getUserConfiguredSettings();
        for (final String key : comboBoxes.keySet()) {
            final String previouslyConfiguredValue = userConfiguredSettings.get(key);
            if (previouslyConfiguredValue != null) {
                setPreviouslyConfiguredValueInListBox(comboBoxes.get(key), previouslyConfiguredValue);
            }
        }
    }

    private void setPreviouslyConfiguredValueInListBox(final TimeRangeComboBox comboBox,
            final String previouslyConfiguredValue) {
        comboBox.setValue(getCorrespondingIndex(comboBox, previouslyConfiguredValue));
    }

    private ModelData getCorrespondingIndex(final TimeRangeComboBox comboBox, final String previouslyConfiguredValue) {
        final ListStore<ModelData> timeComboStore = comboBox.getStore();
        final List<ModelData> timeValues = timeComboStore.getModels();
        for (final ModelData timeValue : timeValues) {
            final String display = timeValue.get("display");
            if (display.equals(previouslyConfiguredValue)) {
                return timeValue;
            }
        }
        return null;
    }

    @UiHandler("cancelButton")
    public void onCancel(@SuppressWarnings("unused") final ClickEvent event) {
        getPresenter().closeDialog();
    }

    public void resetCorrectValues() {
        setPreviouslyConfiguredValues();
    }

    @UiHandler("updateButton")
    public void onUpdate(@SuppressWarnings("unused") final ClickEvent event) {
        if (getPresenter().haveUserTimeDetailsBeenEntered()) {
            final Map<String, String> validationErrors = getPresenter().getInvalidEntries();
            if (validationErrors.isEmpty()) {
                getPresenter().onUpdate();
                getPresenter().closeDialog();
            } else {
                markInvalidFields(validationErrors);
            }
        } else {
            getPresenter().closeDialog();
        }
    }

    void markInvalidFields(final Map<String, String> validationErrors) {
        for (final String invalidField : validationErrors.keySet()) {
            final String validationError = validationErrors.get(invalidField);
            markInvalidField(invalidField, validationError);
        }
    }

    private void markInvalidField(final String key, final String validationError) {
        comboBoxes.get(key).markInvalid(validationError);
    }

    private class TimeRangeComboBoxListener implements Listener<BaseEvent> {

        private final String key;

        /**
         * @param key
         */
        public TimeRangeComboBoxListener(final String key) {
            this.key = key;
        }

        @Override
        public void handleEvent(final BaseEvent be) {
            final ModelData timeComboSelection = ((TimeRangeComboBox) be.getSource()).getValue();
            if (timeComboSelection != null) {
                final TimeInfoDataType userTimeDetails = TimeRangeComboBox
                        .extractUserTimeDetailsFromEvent(timeComboSelection);
                getPresenter().setUserTimeDetails(key, userTimeDetails);
                final String validationError = getPresenter().validateEntry(key, userTimeDetails);
                if (validationError != null) {
                    markInvalidField(key, validationError);
                }
            }

        }

    }

    public TimeRangeComboBox getRefreshTimeCombo() {
        return refreshTime;
    }

}
