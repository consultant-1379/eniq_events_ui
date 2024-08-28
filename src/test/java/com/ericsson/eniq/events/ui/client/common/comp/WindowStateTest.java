package com.ericsson.eniq.events.ui.client.common.comp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test for {@link com.ericsson.eniq.events.ui.client.common.comp.WindowState}.
 *
 * @author ealeerm
 * @since Jun 8, 2012
 */
public class WindowStateTest {

    WindowState state;

    @Before
    public void setUp() {
        state = new WindowState();
    }

    @After
    public void tearDown() {
    }

    /**
     * Tested method: {@link com.ericsson.eniq.events.ui.client.common.comp.WindowState#getDrillDepth()}
     */
    @Test
    public void getDrillDepth_AfterInit() {
        assertEquals(0, state.getDrillDepth());
    }

    /**
     * Tested method: {@link com.ericsson.eniq.events.ui.client.common.comp.WindowState#isDrillDown()}
     */
    @Test
    public void isDrillDown_InLifeCycle() {
        assertFalse(state.isDrillDown());

        state.incrementDrillDepth();
        assertTrue(state.isDrillDown());

        state.incrementDrillDepth();
        assertTrue(state.isDrillDown());

        state.resetDrillDepth();
        assertFalse(state.isDrillDown());

        state.incrementDrillDepth();
        assertTrue(state.isDrillDown());

        state.decrementDrillDepth();
        assertFalse(state.isDrillDown());

        state.decrementDrillDepth();
        assertFalse(state.isDrillDown());
    }

    /**
     * Tested method: {@link com.ericsson.eniq.events.ui.client.common.comp.WindowState#incrementDrillDepth()}
     */
    @Test
    public void incrementDrillDepth_Base() {
        state.incrementDrillDepth();
        assertEquals(1, state.getDrillDepth());

        state.incrementDrillDepth();
        assertEquals(2, state.getDrillDepth());
    }

    /**
     * Tested method: {@link com.ericsson.eniq.events.ui.client.common.comp.WindowState#decrementDrillDepth()}
     */
    @Test
    public void decrementDrillDepth_Base() {
        state.decrementDrillDepth();
        assertEquals(0, state.getDrillDepth());

        state.decrementDrillDepth();
        assertEquals(0, state.getDrillDepth());
    }

    /**
     * Tested method: {@link com.ericsson.eniq.events.ui.client.common.comp.WindowState#resetDrillDepth()}
     */
    @Test
    public void resetDrillDepth_Base() {
        assertEquals(0, state.getDrillDepth());

        state.resetDrillDepth();

        assertEquals(0, state.getDrillDepth());

        state.incrementDrillDepth();
        state.resetDrillDepth();
        assertEquals(0, state.getDrillDepth());

        state.incrementDrillDepth();
        state.incrementDrillDepth();
        state.resetDrillDepth();
        assertEquals(0, state.getDrillDepth());

        state.decrementDrillDepth();
        assertEquals(0, state.getDrillDepth());
    }

    /**
     * Tested method: {@link com.ericsson.eniq.events.ui.client.common.comp.WindowState#getDrillDepth()}
     */
    @Test
    public void getDrillDepth_InLifeCycle() {
        assertEquals(0, state.getDrillDepth());

        state.incrementDrillDepth();
        assertEquals(1, state.getDrillDepth());

        state.incrementDrillDepth();
        assertEquals(2, state.getDrillDepth());

        state.resetDrillDepth();
        assertEquals(0, state.getDrillDepth());

        state.incrementDrillDepth();
        assertEquals(1, state.getDrillDepth());

        for (int i = 0; i < 101; i++) {
            state.decrementDrillDepth();
            assertEquals(0, state.getDrillDepth());
        }

        assertEquals(0, state.getDrillDepth());

        for (int i = 0; i < 101; i++) {
            assertEquals(i, state.getDrillDepth());
            state.incrementDrillDepth();
            assertEquals(i + 1, state.getDrillDepth());
        }

        assertEquals(101, state.getDrillDepth());
    }

    /**
     * Tested method: {@link com.ericsson.eniq.events.ui.client.common.comp.WindowState#toString()}
     */
    @Test
    public void toString_ContainsImportantValues() {
        assertTrue(state.toString().contains("0"));

        for (int i = 0; i < 77; i++) {
            state.incrementDrillDepth();
            assertTrue(state.toString().contains("i"));
        }
    }
}
