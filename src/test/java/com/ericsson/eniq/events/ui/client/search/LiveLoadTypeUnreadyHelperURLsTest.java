/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import static junit.framework.Assert.*;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.ServerComms;

/**
 * Test class specifically for logic around looking up URLs
 * @author eemecoy
 *
 */
public class LiveLoadTypeUnreadyHelperURLsTest extends TestEniqEventsUI {

    private LiveLoadTypeUnreadyHelper liveLoadTypeUnreadyHelper;

    ServerComms mockedServerComms;

    @Before
    public void setUpObjects() {

        setUpGeneralExpectationsOnBusWhenDontCareAboutTypes();

        mockedServerComms = context.mock(ServerComms.class);

        liveLoadTypeUnreadyHelper = new StubbedLiveLoadTypeUnreadyHelper("TERMINAL_TAB", "node",
                "submit data for handset", "enter handset", "", "nokiaURL", "TAC", "PS,CS");
    }

    @Test
    @Ignore
    public void testSendingRequest() {
        LiveLoadTypeUnreadyHelper.typeMenuItems.clear();
        final PairedTypeSearchComponent comp = liveLoadTypeUnreadyHelper.createSearchFieldPairedType(mockedEventBus,
                true, "");
        assertEquals("enter handset", comp.emptyText);
        assertEquals("node=", comp.valParam);
        assertEquals("TAC", comp.permanentType);
    }

    class StubbedLiveLoadTypeUnreadyHelper extends LiveLoadTypeUnreadyHelper {

        public StubbedLiveLoadTypeUnreadyHelper(final String tabOwnerId, final String valParam, final String submitTip,
                final String emptyText, final String style, final String typeURL, final String permanentType,
                final String winMetaSupport) {
            super(tabOwnerId, valParam, submitTip, emptyText, style, typeURL, permanentType, winMetaSupport);
            // TODO Auto-generated constructor stub
        }

        @Override
        public ServerComms getServerCommHandler(final EventBus eventBus) {
            return mockedServerComms;

        }

    }

}
