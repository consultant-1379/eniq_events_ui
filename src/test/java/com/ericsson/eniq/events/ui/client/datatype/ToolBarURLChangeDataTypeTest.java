/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import static junit.framework.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author eeicmsy
 *
 */
public class ToolBarURLChangeDataTypeTest {

    ToolBarURLChangeDataType objectToTest;

    @Before
    public void createObjectToTest() {
        objectToTest = new ToolBarURLChangeDataType();
    }

    @After
    public void tearDown() {
        objectToTest = null;
    }

    @Test
    public void keepExistingParams() throws Exception {

        final String urlParams = "?time=late&food=good&latework=stupid";

        objectToTest.keepExistingRegularParams(urlParams);

        assertEquals("kept & params", 2, objectToTest.getParametersMap().size());

        assertEquals("correct key 1 ", true, objectToTest.getParametersMap().containsKey("&food="));
        assertEquals("correct key 2 ", true, objectToTest.getParametersMap().containsKey("&latework="));

        assertEquals("correct value ", "stupid", objectToTest.getParametersMap().get("&latework="));
        assertEquals("correct value ", "good", objectToTest.getParametersMap().get("&food="));

        assertEquals("wigit paramas kept", "&latework=stupid&food=good", objectToTest.getWidgetSpecificParams());
    }

    @Test
    public void addOutBoundParamReplaces() throws Exception {

        final String urlParams = "?time=late&food=good&latework=stupid";

        objectToTest.keepExistingRegularParams(urlParams);

        objectToTest.addOutBoundParameter("latework=", "really stupid");

        assertEquals("kept & params", 2, objectToTest.getParametersMap().size());

        assertEquals("wigit paramas kept", "&latework=really stupid&food=good", objectToTest.getWidgetSpecificParams());
    }

    @Test
    public void addOutBoundParamAdds() throws Exception {

        final String urlParams = "?time=late&food=good&latework=stupid";

        objectToTest.keepExistingRegularParams(urlParams);

        objectToTest.addOutBoundParameter("sleep=", "great");

        assertEquals("kept & params", 3, objectToTest.getParametersMap().size());

        assertEquals("wigit paramas kept", "&latework=stupid&food=good&sleep=great", objectToTest
                .getWidgetSpecificParams());
    }

}
