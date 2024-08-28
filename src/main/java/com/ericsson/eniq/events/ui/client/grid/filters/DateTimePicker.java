/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid.filters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ericsson.eniq.events.ui.client.grid.listeners.DatePickerListener;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.menu.DateMenu;

/**
 * Form with a calendar for date selection, drop down menu with hour, 
 * minute values and a input filed for seconds Allows user to submit various combinations 
 * of a date with or without hour, minute and second values.
 * 
 * @author ekurshi
 * @since September 2011
 */
public class DateTimePicker extends ContentPanel {

    private List<DatePickerListener> datePickerListeners;

    private static final String DATE_PICKER_FBAR = "date-picker-fbar";

    private static final String DATE_PICKER_CTRL = "date-picker-ctl";

    public DateTimePicker() {
        setHeading("Select Date and Time");
        setSize(340, 140);
        setFrame(true);
        final FormLayout formLayout = new FormLayout(LabelAlign.RIGHT);
        setLayout(formLayout);
        addDateField();
        addTimeField();
        addButton(new Button("OK", new OkButtonListener()));
        addButton(new Button("Cancel", new CancelButtonListener()));
        getButtonBar().addStyleName(DATE_PICKER_FBAR);
    }

    private void addDateField() {
        final DateField date = new DateField();
        date.addStyleName(DATE_PICKER_CTRL);
        date.setFieldLabel("Date");
        date.addListener(Events.OnClick, new InvalidListener());
        add(date);
        addListener(Events.OnClick, new ClickListener(date));//for hiding date menu when user click outside the date picker
    }

    private void addTimeField() {
        final TimeField time = new TimeField();
        time.addStyleName(DATE_PICKER_CTRL);
        time.setFieldLabel("Time");
        time.setTypeAhead(true);
        time.setTriggerAction(TriggerAction.ALL);
        time.addListener(Events.OnClick, new InvalidListener());
        add(time);
    }

    public void setDateTime(final Date date) {
        final List<Component> fields = getItems();
        if (fields != null && fields.size() != 0) {
            final DateField dateField = (DateField) fields.get(0);
            dateField.clear();
            dateField.setValue(date);
            final TimeField timeField = (TimeField) fields.get(1);
            if (date == null) {
                timeField.clearSelections();
            } else {
                timeField.setDateValue(date);
            }
        }
    }

    public void addDatePickerListener(final DatePickerListener pickerListener) {
        if (datePickerListeners == null) {
            datePickerListeners = new ArrayList<DatePickerListener>();
        }
        datePickerListeners.add(pickerListener);
    }

    @SuppressWarnings({ "deprecation" })
    private Date validateFields(final List<Component> formFields) {
        final DateField dateField = (DateField) formFields.get(0);
        final Date date = dateField.getValue();
        if (date != null) {
            final TimeField timeField = (TimeField) formFields.get(1);
            final Time time = timeField.getValue();
            if (time == null) {
                timeField.forceInvalid("Please select time from drop down list");
            } else {
                date.setHours(time.getHour());
                date.setMinutes(time.getMinutes());
            }
        }
        return date;
    }

    private boolean isValidationFailed(final List<Component> formFields, final Date date) {
        final TimeField timeField = (TimeField) formFields.get(1);
        if (date != null && timeField.getValue() == null) {
            return true;
        }
        return false;
    }

    private final class CancelButtonListener extends SelectionListener<ButtonEvent> {
        @Override
        public void componentSelected(final ButtonEvent ce) {
            if (datePickerListeners != null) {
                for (final DatePickerListener listener : datePickerListeners) {
                    listener.onCancel();
                }
            }
        }
    }

    private final class OkButtonListener extends SelectionListener<ButtonEvent> {
        @Override
        public void componentSelected(final ButtonEvent ce) {
            if (datePickerListeners != null) {
                final List<Component> formFields = getItems();
                final Date date = validateFields(formFields);
                if (!isValidationFailed(formFields, date)) {
                    for (final DatePickerListener listener : datePickerListeners) {
                        listener.onSelect(date);
                    }
                }
            }
        }
    }

    private class InvalidListener implements Listener<FieldEvent> {
        @Override
        public void handleEvent(final FieldEvent event) {
            event.getField().clearInvalid();
        }
    }

    /**
     * Listener for auto hiding the date menu when user click outside of the date picker
     * @author ekurshi
     *
     */
    private class ClickListener implements Listener<BaseEvent> {
        private final DateField dateField;

        public ClickListener(final DateField dateField) {
            this.dateField = dateField;
        }

        @Override
        public void handleEvent(final BaseEvent be) {
            final DatePicker datePicker = dateField.getDatePicker();
            if (datePicker.isAttached()) {
                ((DateMenu) datePicker.getParent()).hide();
            }
        }
    }
}
