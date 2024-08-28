/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype.grid;

/**
 * Interface for saving/restoring properties of a grid column
 * @author ecarsea
 * @since 2012
 *
 */
public interface IColumnState {

    String getColumnTypeId();

    void setColumnTypeId(String columnTypeId);

    int getColumnIndex();

    void setColumnIndex(int columnIndex);

    boolean isHidden();

    void setHidden(boolean hidden);

    int getWidth();

    void setWidth(int width);
}
