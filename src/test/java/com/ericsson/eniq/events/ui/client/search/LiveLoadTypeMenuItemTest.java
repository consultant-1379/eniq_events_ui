/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import static junit.framework.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;

/**
 * @author eeicmsy
 *
 */
public class LiveLoadTypeMenuItemTest extends TestEniqEventsUI {

    LiveLoadTypeMenuItem objectToTest;

    @Before
    public void setUp() {
        objectToTest = new LiveLoadTypeMenuItem("id", "", "name", "liveLoadURL", "style", "emptyText", "CS,PS");

    }

    @After
    public void tearDown() {
        objectToTest = null;
    }

    @Test
    public void getStyleNameTest() throws Exception {
        assertEquals("style ok", "style", objectToTest.style);
    }

    @Test
    public void getIDTest() throws Exception {
        assertEquals("id ok", "id", objectToTest.getId());
    }

    @Test
    public void isGroupTest() throws Exception {
        assertEquals("not a group", false, objectToTest.isGroupType());
    }

    @Test
    public void getNameTest() throws Exception {
        assertEquals("name ok", "name", objectToTest.name);
    }

    @Test
    public void getEmptyTextTest() throws Exception {
        assertEquals("emptyText ok", "emptyText", objectToTest.emptyText);
    }

    @Test
    public void getLiveLoadURLTest() throws Exception {
        assertEquals("liveLoadURL ok", "liveLoadURL", objectToTest.liveLoadURL);
    }

}
