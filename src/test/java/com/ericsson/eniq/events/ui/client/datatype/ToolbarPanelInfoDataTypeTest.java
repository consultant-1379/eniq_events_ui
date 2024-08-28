/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.datatype;

import static junit.framework.Assert.*;

import org.junit.*;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;

public class ToolbarPanelInfoDataTypeTest extends TestEniqEventsUI {

    ToolbarPanelInfoDataType objectToTest;

    @Before
    public void createObjectToTest() {
        objectToTest = new ToolbarPanelInfoDataType();
    }

    @After
    public void tearDown() {
        objectToTest = null;
    }

    @Test
    public void containsKPI() throws Exception {
        assertEquals("KPI supported", true, objectToTest.supportedEventTypes.containsKey("KPI"));

    }

    @Test
    public void containsDisconnection() throws Exception {
        assertEquals("Disconnection supported", true, objectToTest.supportedEventTypes.containsKey("DISCONNECTION_CODE_TABLE_DC_WCDMA"));

    }

}
