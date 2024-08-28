package com.ericsson.eniq.events.ui.client.grid;

import static org.junit.Assert.assertEquals;

import org.junit.*;

import com.ericsson.eniq.events.ui.*;

/**
 *  @author eriwals
 *
 */
public class CaselessComparatorTest extends TestEniqEventsUI {

    private CaselessComparator<Object> objToTest;

    @Before
    public void setUp() {

        objToTest = new CaselessComparator<Object>();

    }

    @After
    public void tearDown() {
        objToTest = null;
    }

    @Test
    public void testFirstArgNull() {
        assertEquals(objToTest.compare(null, "test"), -1);
    }

    @Test
    public void testSecondArgNull() {
        assertEquals(objToTest.compare("test", null), 1);
    }

    @Test
    public void testBothArgsNull() {
        assertEquals(objToTest.compare(null, null), 0);
    }

    @Test
    public void testSameLetterDifferentCase() {
        assertEquals(objToTest.compare("atest", "Atest"), 0);
    }

    @Test
    public void testNumber() {
        assertEquals(objToTest.compare(2, 13), -1);
    }

}