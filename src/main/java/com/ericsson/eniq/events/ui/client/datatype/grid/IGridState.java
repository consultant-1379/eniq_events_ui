/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype.grid;

import java.util.List;

/**
 * Interface for saving/restoring properties of a grid
 * @author ecarsea
 * @since 2012
 *
 */
public interface IGridState {

    Integer getOffset();

    void setOffset(Integer offset);

    Integer getLimit();

    void setLimit(Integer limit);

    String getSortField();

    void setSortField(String sortField);

    String getSortDir();

    void setSortDir(String sortDir);

    List<IColumnState> getColumnsState();

    void setColumnsState(List<IColumnState> columnsState);
}
