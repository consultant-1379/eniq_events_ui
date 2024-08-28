/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget;

import static com.ericsson.eniq.events.common.client.CommonConstants.*;

import java.util.Date;
import java.util.List;

import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.kpi.KPIConfigurationPanelDataType;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * class for dialog that will allow the end user to specify the time parameters
 * for data been retrieved from the server.
 *
 * @author eendmcm
 * @since Feb 2010
 */
public class TimeParameterDialog extends ParameterDialog {

    private final ClickHandler rdButtonToggle = new RadioButtonToggle();

    private final static String CMB_TIME_DISPLAY = "display";

    private final static String CMB_TIME_VALUE = "value";

    private final static String DATETIME_STYLE = "dateDlg";

    private final static String RD_TIME_STYLE = "rdTimeRange";

    private final static String RD_DATETIME_STYLE = "rdDateTimeRange";

    private final static String TIME_EMPTY_TEXT = "Time...";

    private final static String DATE_EMPTY_TEXT = "Date...";

    private final static String INVALID_TO_DATE_MESSAGE = "This date is before the 'From' date";

    private final static String INVALID_DATE_MESSAGE = "This date is greater than the current date";

    private final static String INVALID_FROM_DATE_MESSAGE_ONE_DAY_IS_MAX = "Selection interval is limited to one day";

    private final static String INVALID_TO_TIME_MESSAGE = "This time and date is before the 'From' time and date";

    private final static String TIME_MANDITORY_INPUT_MESSAGE = "This field is mandatory";

    private TimeRangeComboBox cmbTime;

    private DateField fromDate;

    private DateField toDate;

    private TimeField fromTime;

    private TimeField toTime;

    private RadioButton rdTime;

    private RadioButton rdDate;

    private final TimeInfoDataType timeSettings;

    private TimeInfoDataType userSelection;

    private final Listener<FieldEvent> dateListener = new Listener<FieldEvent>() {

        @Override
        public void handleEvent(final FieldEvent be) {
            validateCtrls();
        }
    };

    private HorizontalPanel rangePanel;

    private VerticalPanel dateTimeCombosPanel;

    private boolean isFromKpiPanel;

    /**
     * Construct TimeParameterDialog
     *
     * @param dialogTitle - dialog title
     * @param time   - time info
     */
    public TimeParameterDialog(final String dialogTitle, final TimeInfoDataType time,final boolean isKpiWindow) {
        super(dialogTitle);
        timeSettings = time;
        isFromKpiPanel=isKpiWindow;
        initDialog();
    }

    /*
     * initialise the controls for the dialog
     */
    private void initDialog() {

        /* setup the main panel within the timeDialog window */
        final VerticalPanel pnl = new VerticalPanel();

        // Radio buttons panel
        final HorizontalPanel radioButtonPanel = createRadioButtonPanel();
        pnl.add(radioButtonPanel);
        pnl.setCellHeight(radioButtonPanel, "30px");

        // Content for dialog
        final FlowPanel contentPanel = new FlowPanel();
        contentPanel.setHeight("80px");
        contentPanel.add(createRangePanel());
        contentPanel.add(createTimeCombosPanel());

        pnl.add(contentPanel);

        // Bottom buttons panel (update and cancel)
        final HorizontalPanel buttonsPanel = createButtonsPanel();
        pnl.add(buttonsPanel);
        pnl.setCellHorizontalAlignment(buttonsPanel, HasHorizontalAlignment.ALIGN_CENTER);

        final int margin = 15;
        pnl.setWidth(DIALOG_WIDTH - margin * 2 + "px");
        pnl.getElement().getStyle().setMargin(margin, Style.Unit.PX);

        addPanelToParameterDialog(pnl);

        /* populate the provided time parameters */
        if (timeSettings.timeRange.length() > 0) {
            rdTime.setValue(true);
            cmbTime.setValue(cmbTime.getStore().getAt(timeSettings.timeRangeSelectedIndex));
            cmbTime.setEditable(false);
            toggleDisplay(rdTime.getValue());
        } else {
            this.rdDate.setValue(true);
            this.fromDate.setValue(timeSettings.dateFrom);
            this.toDate.setValue(timeSettings.dateTo);
            this.fromTime.setValue(timeSettings.timeFrom);
            this.toTime.setValue(timeSettings.timeTo);
            cmbTime.setValue(cmbTime.getStore().getAt(timeSettings.timeRangeSelectedIndex));
            toggleDisplay(!rdDate.getValue());
        }

        showParameterDialog();
        clearInvalidation();//first run - no invalidation
    }

    private HorizontalPanel createRadioButtonPanel() {
        final HorizontalPanel radioButtonPanel = new HorizontalPanel();

        rdTime = initRadio("Time Range", RD_TIME_STYLE, "Time", true);
        rdTime.getElement().setId(SELENIUM_TAG + "timeRange");

        rdDate = initRadio("Date and Time Range", RD_DATETIME_STYLE, "Time", false);
        rdDate.getElement().setId(SELENIUM_TAG + "dateTimeRange");

        radioButtonPanel.add(rdTime);
        radioButtonPanel.add(rdDate);

        radioButtonPanel.setCellWidth(rdTime, "110px");

        return radioButtonPanel;
    }

    private VerticalPanel createTimeCombosPanel() {
        dateTimeCombosPanel = new VerticalPanel();
        dateTimeCombosPanel.add(createFrom());
        dateTimeCombosPanel.add(createTo());
        return dateTimeCombosPanel;
    }

    private HorizontalPanel createFrom() {
        fromDate = initDateCtrl();
        fromDate.setId(SELENIUM_TAG + "fromDate");
        fromDate.addListener(Events.Blur, dateListener);
        fromDate.addListener(Events.Focus, dateListener);

        fromTime = initTimeCtrl();
        fromTime.setId(SELENIUM_TAG + "fromTime");
        fromTime.addListener(Events.Blur, dateListener);
        fromTime.addListener(Events.Focus, dateListener);

        final HorizontalPanel fromCombos = new HorizontalPanel();
        fromCombos.add(initLabel("From:", "lblDtRangeDestFrom"));
        fromCombos.add(fromDate);
        fromCombos.add(fromTime);

        fromCombos.addStyleName("timeParameterCombo");

        return fromCombos;
    }

    private HorizontalPanel createTo() {
        toDate = initDateCtrl();
        toDate.setId(SELENIUM_TAG + "toDate");
        toDate.addListener(Events.Blur, dateListener);
        toDate.addListener(Events.Focus, dateListener);

        toTime = initTimeCtrl();
        toTime.setId(SELENIUM_TAG + "toTime");
        toTime.addListener(Events.Blur, dateListener);
        toTime.addListener(Events.Focus, dateListener);

        final HorizontalPanel toCombos = new HorizontalPanel();
        toCombos.add(initLabel("To:", "lblDtRangeDestTo"));
        toCombos.add(toDate);
        toCombos.add(toTime);

        toCombos.addStyleName("timeParameterCombo");

        return toCombos;
    }

    private HorizontalPanel createRangePanel() {
        rangePanel = new HorizontalPanel();

        if (cmbTime == null) {
            cmbTime = new TimeRangeComboBox();
            //init TimeRangeComboBox with corresponding meta data
            if(isFromKpiPanel) {
                final KPIConfigurationPanelDataType configPanelType = MainEntryPoint.getInjector().getMetaReader()
                        .getKPIConfigurationPanelMetaData();
                cmbTime.initWithMetadata(configPanelType.getRefreshTime().getComboTimeData());
            }
            else {
                cmbTime.init();
            }
        }
        cmbTime.setId(SELENIUM_TAG + "time");

        rangePanel.add(cmbTime);

        return rangePanel;
    }

    private Label initLabel(final String sTxt, final String id) {
        final Label lbl = new Label();

        lbl.setId(id);
        lbl.setText(sTxt);

        return lbl;
    }

    /*
     * creates and returns a TimeField Control
     */
    private TimeField initTimeCtrl() {
        final TimeField time = new TimeField();
        time.setAllowBlank(false);
        time.setAutoValidate(true);
        time.addStyleName(DATETIME_STYLE);
        time.setMaxHeight(100); // just display 4 or so
        time.setEditable(false);
        time.setEmptyText(TIME_EMPTY_TEXT);
        time.setWidth(100);
        // consistant with live load to an extent so full list again when drop down
        // not just selected
        time.setTriggerAction(TriggerAction.ALL);

        return time;
    }

    /*
     * creates and returns a DateField Control
     */
    private DateField initDateCtrl() {
        final DateField date = new DateField();
        date.setPropertyEditor(new DateTimePropertyEditor(DateField_Displayed_Date_Format));
        date.setAllowBlank(false);
        date.setAutoValidate(true);
        date.setWidth(140);
        date.setEditable(false);
        date.setMaxValue(new Date());
        date.setEmptyText(DATE_EMPTY_TEXT);
        date.addStyleName(DATETIME_STYLE);

        return date;

    }

    /*
     * creates and returns a Radio button with the assigned properties Text -
     * sTitle StyleName - style Name - sName Checked - bChecked Has a Change
     * Listener - bListener
     */
    private RadioButton initRadio(final String sTitle, final String style, final String sName, final Boolean bChecked) {

        final RadioButton rd = new RadioButton(sName, sTitle);
        rd.addStyleName(style);
        rd.setValue(bChecked);
        rd.addClickHandler(rdButtonToggle);

        return rd;
    }

    /*
     * toggle the enabled status and the validate status of the controls based on
     * the radio button selection
     */
    private void toggleDisplay(final Boolean bToggle) {

        rangePanel.setVisible(bToggle);
        dateTimeCombosPanel.setVisible(!bToggle);
    }

    /**
     * External interface for validating the time parameter dialog inputs
     *
     * @return True if the inputs are valid
     */
    @Override
    public boolean validate() {
        return validateCtrls();
    }

    /**
     * Method for validating controls, private as it is called in the constructor
     *
     * @return True if the inputs are valid
     */
    private boolean validateCtrls() {
        boolean isValid = true;
        clearInvalidation();

        if (rdTime.getValue()) {
            if (!cmbTime.validate(true)) {
                isValid = false;
            }
        } else {
            isValid = validateDateTimeFields();
        }
        return isValid;
    }

    /**
     * Method for validating and marking the date-time fields
     *
     * @return True if all fields are valid
     */
    private boolean validateDateTimeFields() {
        boolean isValid = true;
        if (!fromDate.validate(true)) {
            invalidateField(fromDate);
            isValid = false;
        }
        if (!toDate.validate(true)) {
            invalidateField(toDate);
            isValid = false;
        }
        if (!fromTime.validate(true)) {
            invalidateField(fromTime);
            isValid = false;
        }
        if (!toTime.validate(true)) {
            invalidateField(toTime);
            isValid = false;
        }
        if (!isDateValid()) {
            isValid = false;
        }
        return isValid;
    }

    /**
     * Method for marking fields invalid, marks with appropriate message depending
     * on the content of the input field
     *
     * @param field The field to mark
     */
    private void invalidateField(final Field<?> field) {
        field.markInvalid(fromDate.getRawValue().length() == 0 ? TIME_MANDITORY_INPUT_MESSAGE : INVALID_VALUE_MESSAGE);
    }

    /**
     * Method to validate the date input into the date-time fields. Checks if the
     * to date is before the from date (and marks it invalid if so) and checks if
     * the To date and From date are equal and if the To time is before or equal
     * to the From time (and again marks it invalid if it is so)
     *
     * @return True if the To date-time is after the From date-time
     */
    private boolean isDateValid() {
        boolean isValid;

        if (fromDate.getRawValue().length() > 0 && toDate.getRawValue().length() > 0) { // If
            // both
            // dates
            // are
            // populated
            final Date fromD = fromDate.getValue();
            final Date toD = toDate.getValue();
            final Date currentDate = new Date(System.currentTimeMillis());

            isValid = checkAndMarkValid(fromD, toD, currentDate);

        } else {
            isValid = false;
        }

        return isValid;
    }

    private boolean checkAndMarkValid(final Date fromD, final Date toD, final Date currentDate) {

        // extra for MSS (which may remove later) or else add week into CS meta data
        if (isMoreThanADayAgoAndBlocked(fromD)) {
            fromDate.markInvalid(INVALID_FROM_DATE_MESSAGE_ONE_DAY_IS_MAX);
            return false;
        }
        boolean isValid = true;
        if (toD.compareTo(currentDate) > 0) {
            isValid = false;
            toDate.markInvalid(INVALID_DATE_MESSAGE);
        } else if (fromD.compareTo(toD) > 0) { // Validate the dates
            isValid = false;
            toDate.markInvalid(INVALID_TO_DATE_MESSAGE);
        } else if (fromD.compareTo(toD) == 0) {
            if (toTime.getRawValue().length() > 0 && fromTime.getRawValue().length() > 0) {
                // If both dates are the same and both times are populated
                final Date fromT = fromTime.getValue().getDate();
                final Date toT = toTime.getValue().getDate();
                if (fromT.compareTo(toT) >= 0) { // Validate the times
                    isValid = false;
                    toTime.markInvalid(INVALID_TO_TIME_MESSAGE);
                }
            } else {
                isValid = false;
            }
        }
        return isValid;
    }

    /*
     * Possibly Temp method added for MSS If "1 week" is not present in time
     * control then not allowed select more than one day on date component
     * 
     * (extra (possibly temp) addition for MSS to block user selecting values
     * greater than a day
     * 
     * @param fromD "from date"
     * 
     * @return true if will be blocking user selection for from date exceeding a
     * day (if week is present in meta data for time combobox user will not be
     * blocked for mininum date)
     */
    private boolean isMoreThanADayAgoAndBlocked(final Date fromD) {

        if (!isDisplayingWeekInTimeCombo()) {
            final long now = System.currentTimeMillis();
            final long minAllowed = now - DAY_IN_MILLISEC;

            final long selectedFrom = fromD.getTime();
            return selectedFrom < minAllowed;
        }

        return false;

    }

    /*
     * @return true if time combobox is displaying "1 week"
     */
    private boolean isDisplayingWeekInTimeCombo() {
        final ListStore<ModelData> timeComboStore = cmbTime.getStore();
        final List<ModelData> timeVals = timeComboStore.getModels();

        for (final ModelData data : timeVals) {
            if (ONE_WEEK_MS_TIME_PARAMETER.equals(data.get(CMB_TIME_VALUE))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clears the invalid markers on all date-time fields
     */
    private void clearInvalidation() {
        fromDate.clearInvalid();
        toDate.clearInvalid();
        fromTime.clearInvalid();
        toTime.clearInvalid();
    }

    /**
     * populates and returns a DataType containing the time parameters provided by
     * the end user
     *
     * @return userSelection
     */
    public TimeInfoDataType getUserTimeSelection() {
        if (userSelection == null) {
            userSelection = new TimeInfoDataType();
        }
        final ModelData timeComboSelection = cmbTime.getValue();

        if (rdTime.getValue()) {

            userSelection.timeRange = (String) timeComboSelection.get(CMB_TIME_VALUE);
            userSelection.timeRangeDisplay = (String) timeComboSelection.get(CMB_TIME_DISPLAY);
        } else if (rdDate.getValue()) {
            userSelection.dateFrom = fromDate.getValue();
            userSelection.dateTo = toDate.getValue();
            userSelection.timeFrom = fromTime.getValue();
            userSelection.timeTo = toTime.getValue();

        }

        /* reset so next time window launches uses cached parameter for time */
        userSelection.timeRangeSelectedIndex = cmbTime.getStore().indexOf(timeComboSelection);
        return userSelection;
    }

    /*
     * Listener Class for the toggle of the Date | Time Radio buttons
     */
    private final class RadioButtonToggle implements ClickHandler {
        @Override
        public void onClick(final ClickEvent event) {
            final RadioButton source = (RadioButton) event.getSource();
            if (source.equals(rdTime) && source.getValue()) {
                toggleDisplay(true);
            } else if (source.equals(rdDate) && source.getValue()) {
                toggleDisplay(false);
            }
        }
    }

}
