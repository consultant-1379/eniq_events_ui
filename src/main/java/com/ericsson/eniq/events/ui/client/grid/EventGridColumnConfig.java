/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

/**
 * Extend ColumnConfig to add a columnType field
 * @author ecarsea
 * @since 2012
 *
 */
public class EventGridColumnConfig extends ColumnConfig {

    final String columnType;

    /**
     * @param columnID
     * @param columnName
     * @param width
     * @param columnType
     */
    public EventGridColumnConfig(final String columnID, final String columnName, final int width,
            final String columnType) {
        super(columnID, columnName, width);
        this.columnType = columnType;
    }

    /**
     * @return the columnType
     */
    public String getColumnType() {
        return columnType;
    }
}
