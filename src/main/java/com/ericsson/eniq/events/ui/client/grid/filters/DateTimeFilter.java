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

import com.ericsson.eniq.events.ui.client.grid.listeners.DateTimeFieldListener;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.BaseDateFilterConfig;
import com.extjs.gxt.ui.client.data.FilterConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Filter for date time comparison. It support greater than, less than and equal to comparison.
 * @author ekurshi
 * @since 2011
 *
 */
public class DateTimeFilter extends Filter {
    public enum FilterType {
        BEFORE, ON, AFTER
    };

    private final MenuItem beforeItem, afterItem, onItem;

    private static final String CONFIGTYPE_DATE = "date";

    private static final String COMPARISON_ON = "on";

    private static final String COMPARISON_BEFORE = "before";

    private static final String COMPARISON_AFTER = "after";

    /**
     * Creates a new date filter.
     * 
     * @param dataIndex
     *            the date index the filter is mapped to
     */
    public DateTimeFilter(final String dataIndex) {
        super(dataIndex);
        afterItem = createAfterMenuItem(); // NOPMD by ekurshi on 19/09/11 10:10
        beforeItem = createBeforeMenuItem(); // NOPMD by ekurshi on 19/09/11 10:10
        onItem = createOnMenuItem(); // NOPMD by ekurshi on 19/09/11 10:10
        configureMenu(); // NOPMD by ekurshi on 19/09/11 10:11
    }

    /**
     * extracted for junit
     */
    MenuItem createOnMenuItem() {
        final MenuItem item = new MenuItem();
        final AbstractImagePrototype onIcon = GXT.IMAGES.grid_filter_equal();
        item.setHideOnClick(false);
        item.setIcon(onIcon);
        return item;
    }

    /**
     * extracted for junit
     */
    MenuItem createAfterMenuItem() {
        final MenuItem item = new MenuItem();
        final AbstractImagePrototype afterIcon = GXT.IMAGES.grid_filter_greaterThan();
        item.setHideOnClick(false);
        item.setIcon(afterIcon);
        return item;
    }

    /**
     * extracted for junit
     */
    MenuItem createBeforeMenuItem() {
        final MenuItem item = new MenuItem();
        final AbstractImagePrototype beforeIcon = GXT.IMAGES.grid_filter_lessThan();
        item.setHideOnClick(false);
        item.setIcon(beforeIcon);
        return item;
    }

    /**
     *Populate filter menu with on, before and after fields 
     */
    void configureMenu() {
        final DateTimeFieldListener fieldListener = new FieldListener();

        final DateTimeField onField = createDateTimeField();
        onField.addFilterUpdateListener(fieldListener);
        onField.setFilterType(FilterType.ON);
        onItem.setWidget(onField);

        final DateTimeField beforeField = createDateTimeField();
        beforeField.addFilterUpdateListener(fieldListener);
        beforeField.setFilterType(FilterType.BEFORE);
        beforeItem.setWidget(beforeField);

        final DateTimeField afterField = createDateTimeField();
        afterField.addFilterUpdateListener(fieldListener);
        afterField.setFilterType(FilterType.AFTER);
        afterItem.setWidget(afterField);

        menu.add(beforeItem);
        menu.add(afterItem);
        menu.add(new SeparatorMenuItem());
        menu.add(onItem);
    }

    /*
     * extracted for junit
     */
    DateTimeField createDateTimeField() {
        return new DateTimeField();
    }

    @Override
    public List<FilterConfig> getSerialArgs() {
        final List<FilterConfig> configs = new ArrayList<FilterConfig>();
        final Date beforeDate = getBeforeDateField().getDate();
        if (beforeDate != null) {
            final FilterConfig beforeConfig = new BaseDateFilterConfig(CONFIGTYPE_DATE, COMPARISON_BEFORE, beforeDate);
            configs.add(beforeConfig);
        }
        final Date afterDate = getAfterDateField().getDate();
        if (afterDate != null) {
            final FilterConfig afterConfig = new BaseDateFilterConfig(CONFIGTYPE_DATE, COMPARISON_AFTER, afterDate);
            configs.add(afterConfig);
        }
        final Date onDate = getOnDateField().getDate();
        if (onDate != null) {
            final FilterConfig onConfig = new BaseDateFilterConfig(CONFIGTYPE_DATE, COMPARISON_ON, onDate);
            configs.add(onConfig);
        }
        return configs;
    }

    @Override
    public Object getValue() {
        return getSerialArgs();
    }

    @Override
    public boolean isActivatable() {
        if (getBeforeDateField() != null) {
            return true;
        }
        if (getAfterDateField() != null) {
            return true;
        }
        if (getOnDateField() != null) {
            return true;
        }
        return false;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setValue(final Object value) {
        final List<FilterConfig> values = (List) value;
        for (final FilterConfig config : values) {
            final String comp = config.getComparison();
            if (COMPARISON_BEFORE.equals(comp)) {
                final DateTimeField beforeDateField = getBeforeDateField();
                beforeDateField.setDate((Date) config.getValue());
                beforeDateField.setFilterType(FilterType.BEFORE);
            } else if (COMPARISON_AFTER.equals(comp)) {
                final DateTimeField afterDateField = getAfterDateField();
                afterDateField.setDate((Date) config.getValue());
                afterDateField.setFilterType(FilterType.AFTER);
            } else if (COMPARISON_ON.equals(comp)) {
                final DateTimeField onDateField = getOnDateField();
                onDateField.setDate((Date) config.getValue());
                onDateField.setFilterType(FilterType.ON);
            }
        }
    }

    /**
     * return true if date is valid and will not be filtered, false otherwise,
     */
    @Override
    public boolean validateModel(final ModelData model) {
        final String modelValue = getModelValue(model);
        if (modelValue == null) {
            return false;
        }
        final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat(Displayed_Date_Format);
        final Date date = dateTimeFormat.parse(modelValue);
        final long time = date.getTime();

        if (isBeforeDateValid(time) && isAfterDateValid(time) && isOnDateValid(time)) {
            return true;
        }
        return false;
    }

    private boolean isOnDateValid(final long time) {
        final Date onDate = getOnDateField().getDate();
        if (onDate != null) {
            return onDate.getTime() == time;
        }
        return true;
    }

    private boolean isAfterDateValid(final long time) {
        final Date afterDate = getAfterDateField().getDate();
        if (afterDate != null) {
            return time > afterDate.getTime();
        }
        return true;
    }

    private boolean isBeforeDateValid(final long time) {
        final Date beforeDate = getBeforeDateField().getDate();
        if (beforeDate != null) {
            return time < beforeDate.getTime();
        }
        return true;

    }

    DateTimeField getBeforeDateField() {
        return beforeItem != null ? (DateTimeField) beforeItem.getWidget() : null;
    }

    DateTimeField getAfterDateField() {
        return afterItem != null ? (DateTimeField) afterItem.getWidget() : null;
    }

    DateTimeField getOnDateField() {
        return onItem != null ? (DateTimeField) onItem.getWidget() : null;
    }

    private class FieldListener implements DateTimeFieldListener {

        @Override
        public void fireUpdate(final FilterType filterType) {
            switch (filterType) {
            case BEFORE:
                getOnDateField().setDate(null);
                break;
            case AFTER:
                getOnDateField().setDate(null);
                break;
            case ON:
                getBeforeDateField().setDate(null);
                getAfterDateField().setDate(null);
                break;
            }
            DateTimeFilter.this.fireUpdate();
        }
    };

}
