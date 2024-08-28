package com.ericsson.eniq.events.ui.client.grid;

import com.ericsson.eniq.events.ui.client.datatype.grid.IColumnState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link com.ericsson.eniq.events.ui.client.grid.ColumnStateComparator}.
 *
 * @author ealeerm
 * @since Jun 8, 2012
 */
@RunWith(MockitoJUnitRunner.class)
public class ColumnStateComparatorTest {

    @Mock IColumnState state1;

    @Mock IColumnState state2;

    /**
     * Tests method: {@link com.ericsson.eniq.events.ui.client.grid.ColumnStateComparator#compare(IColumnState cs1, IColumnState cs2)}
     */
    @Test
    public void compare_Less_ReturnsMinusOne() {
        // Recording and stubbing mock object(s) behaviour
        when(state1.getColumnIndex()).thenReturn(1);
        when(state2.getColumnIndex()).thenReturn(2);

        assertEquals(-1, ColumnStateComparator.getInstance().compare(state1, state2));

        // Selective and explicit verification
        verify(state1).getColumnIndex();
        verify(state2).getColumnIndex();
    }

    /**
     * Tests method: {@link com.ericsson.eniq.events.ui.client.grid.ColumnStateComparator#compare(IColumnState cs1, IColumnState cs2)}
     */
    @Test
    public void compare_More_ReturnsOne() {
        // Recording and stubbing mock object(s) behaviour
        when(state1.getColumnIndex()).thenReturn(-1212);
        when(state2.getColumnIndex()).thenReturn(-7777);

        assertEquals(1, ColumnStateComparator.getInstance().compare(state1, state2));

        // Selective and explicit verification
        verify(state1).getColumnIndex();
        verify(state2).getColumnIndex();
    }

    /**
     * Tests method: {@link com.ericsson.eniq.events.ui.client.grid.ColumnStateComparator#compare(IColumnState cs1, IColumnState cs2)}
     */
    @Test
    public void compare_Equal_Returns() {
        // Recording and stubbing mock object(s) behaviour
        when(state1.getColumnIndex()).thenReturn(7);
        when(state2.getColumnIndex()).thenReturn(7);

        assertEquals(0, ColumnStateComparator.getInstance().compare(state1, state2));

        // Selective and explicit verification
        verify(state1).getColumnIndex();
        verify(state2).getColumnIndex();
    }

    /**
     * Tests method: {@link com.ericsson.eniq.events.ui.client.grid.ColumnStateComparator#compare(IColumnState cs1, IColumnState cs2)}
     */
    @Test(expected = NullPointerException.class)
    public void compare_FirstParameterIsNull_Returns() {
        state1 = null;
        ColumnStateComparator.getInstance().compare(state1, state2);
    }

    /**
     * Tests method: {@link com.ericsson.eniq.events.ui.client.grid.ColumnStateComparator#compare(IColumnState cs1, IColumnState cs2)}
     */
    @Test(expected = NullPointerException.class)
    public void compare_SecondParameterIsNull_Returns() {
        state2 = null;
        ColumnStateComparator.getInstance().compare(state1, state2);
    }

}
