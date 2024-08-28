/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.

 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid.filters;

import static com.ericsson.eniq.events.common.client.CommonConstants.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ericsson.eniq.events.ui.client.grid.filters.DateTimeFilter.FilterType;
import com.ericsson.eniq.events.ui.client.grid.listeners.DatePickerListener;
import com.ericsson.eniq.events.ui.client.grid.listeners.DateTimeFieldListener;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.menu.AdapterMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.impl.ClippedImagePrototype;

/**
 * Text field implemented as a date time filter field Trigger button added to proved a sub menu item with date and time and seconds selections options.
 * 
 * @author esuslyn
 * @since February 2011
 */
public class DateTimeField extends TextField<String> {

    private El wrap;

    private El input;

    private Button pickerButton;

    private Menu datePickerMenu;

    private FilterType filterType;

    private Date date;

    private List<DateTimeFieldListener> fieldListeners;

    private static final String DATE_PICKER = "date-picker-btn";

    private static final String DATE_TIME_FIELD = "date-time-field";

    private static final String DATE_PICKER_ITEM = "date-picker-item";

    public DateTimeField() {
        setReadOnly(true);
        configurePicker();
    }

    private void configurePicker() {
        pickerButton = new Button();
        pickerButton.setToolTip("Select date and time");
        pickerButton.setStyleName(DATE_PICKER);
        /*     pickerButton.setIcon(new ClippedImagePrototype("gxt/images/default/form/date-trigger.gif", 2, 3, 13, 13));*/
        pickerButton.setIcon(new ClippedImagePrototype("resources/images/whiteTheme/form/dropdownArrowButton.png", 0,
                0, 26, 21));

        final DateTimePicker datePicker = new DateTimePicker();
        pickerButton.addSelectionListener(new SelectionListenerImpl(datePicker));
        configurePickerMenu(datePicker);
    }

    private void configurePickerMenu(final DateTimePicker datePicker) {
        datePickerMenu = new Menu();
        final DateTimePickerListener pickerListener = new DateTimePickerListener();
        datePicker.addDatePickerListener(pickerListener);
        final AdapterMenuItem item = new AdapterMenuItem(datePicker);
        item.setBorders(true);
        item.setNeedsIndent(false);
        datePickerMenu.setBorders(false);
        datePickerMenu.add(item);
        datePickerMenu.addStyleName(DATE_PICKER_ITEM);
    }

    public void addFilterUpdateListener(final DateTimeFieldListener fieldListener) {
        if (fieldListeners == null) {
            fieldListeners = new ArrayList<DateTimeFieldListener>();
        }
        fieldListeners.add(fieldListener);
    }

    @Override
    protected void doAttachChildren() {
        super.doAttachChildren();
        ComponentHelper.doAttach(pickerButton);
    }

    @Override
    protected void doDetachChildren() {
        super.doDetachChildren();
        ComponentHelper.doDetach(pickerButton);
    }

    @Override
    protected El getInputEl() {
        return input;
    }

    @Override
    protected void onRender(final Element target, final int index) {
        wrap = new El(DOM.createDiv());
        wrap.addStyleName(DATE_TIME_FIELD);
        input = new El(DOM.createInputText());
        wrap.appendChild(input.dom);
        setElement(wrap.dom, target, index);
        super.onRender(target, index);
        pickerButton.render(wrap.dom);
    }

    private void showPicker() {
        datePickerMenu.show(el().dom, "tl-bl?");
    }

    public void hidePicker() {
        datePickerMenu.hide();
    }

    /**
     * @return the date time picker component
     */
    public DateTimePicker getDateTimePicker() {
        if (datePickerMenu != null) {
            return (DateTimePicker) datePickerMenu.getWidget(0);
        }
        return null;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
        setRawValue(convertDateToString(date));
    }

    public void applyFilter() {
        if (fieldListeners != null) {
            for (final DateTimeFieldListener fieldListener : fieldListeners) {
                fieldListener.fireUpdate(filterType);
            }
        }
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public void setFilterType(final FilterType filterType) {
        this.filterType = filterType;
    }

    private String convertDateToString(final Date date) {
        return date != null ? DateTimeFormat.getFormat(DATE_MINUTE_FORMAT).format(date) : null;
    }

    private final class SelectionListenerImpl extends SelectionListener<ButtonEvent> {
        /**
        * 
        */
        private final DateTimePicker datePicker;

        /**
         * @param datePicker
         */
        private SelectionListenerImpl(final DateTimePicker datePicker) {
            this.datePicker = datePicker;
        }

        @Override
        public void componentSelected(final ButtonEvent ce) {
            datePicker.setDateTime(getDate());
            showPicker();
        }
    }

    private class DateTimePickerListener implements DatePickerListener {

        @Override
        public void onSelect(final Date date) {
            setDate(date);
            hidePicker();
            applyFilter();
        }

        @Override
        public void onCancel() {
            datePickerMenu.hide();
        }
    }
}