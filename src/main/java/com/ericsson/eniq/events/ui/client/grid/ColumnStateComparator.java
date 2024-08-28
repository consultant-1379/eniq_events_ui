/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import java.util.Comparator;

import com.ericsson.eniq.events.ui.client.datatype.grid.IColumnState;

/**
* @author ealeerm
* @since 05/2012
*/
final class ColumnStateComparator implements Comparator<IColumnState> {

    private final static ColumnStateComparator INSTANCE = new ColumnStateComparator();

    private ColumnStateComparator() {
    }

    public static ColumnStateComparator getInstance() {
        return INSTANCE;
    }

    @Override
    public int compare(final IColumnState cs1, final IColumnState cs2) {
        int thisVal = cs1.getColumnIndex();
        int anotherVal = cs2.getColumnIndex();
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }
}
