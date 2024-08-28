/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import static junit.framework.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import com.google.web.bindery.event.shared.EventBus;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.datatype.LiveLoadTypeDataType;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.http.client.Response;

/**
 * @author eeicmsy
 * @since March
 */
public class LiveLoadTypeUnreadyHelperTest extends TestEniqEventsUI {

    LiveLoadTypeUnreadyHelper objectToTest;

    private final static String TAB_ID = "tabId";

    @Before
    public void setUp() {
        createObjectToTest();
    }

    @Test
    @Ignore
    public void createSearchComponentMakesServerCallToFetchTypes() throws Exception {

        checkExpectationsForCreatingSearchField();
        // true because not mocking makes combobox (can't without interface)
        final PairedTypeSearchComponent comp = objectToTest.createSearchFieldPairedType(mockedEventBus, true,
                "Enter type..");
        assertEquals("enter handset", comp.emptyText);
        assertEquals("node=", comp.valParam);
        assertEquals("TAC", comp.permanentType);
    }

    @Test
    @Ignore
    public void handleServerResponseByPopulatingTypes() throws Exception {
        checkExpectationsForCreatingSearchField();
        objectToTest.createSearchFieldPairedType(mockedEventBus, true, "");
        objectToTest.handleServerResponseByPopulatingTypes(null);

        assertEquals("Expected to make change on types menu in search field", true,
                ((StubbedLiveLoadTypeUnreadyHelper) objectToTest).madeMethodCallToUpDateSearchComp);
    }

    @Test
    @Ignore
    public void noAttemptMadeToChangeMenuTypesWithWrongQueryId() throws Exception {
        checkExpectationsForCreatingSearchField();
        objectToTest.createSearchFieldPairedType(mockedEventBus, true, "Enter type");
        objectToTest.handleServerResponseByPopulatingTypes(null);

        assertEquals("Expected not make change on types menu in search field", false,
                ((StubbedLiveLoadTypeUnreadyHelper) objectToTest).madeMethodCallToUpDateSearchComp);
    }

    private void checkExpectationsForCreatingSearchField() {
        context.checking(new Expectations() {
            {
                allowing(mockedEventBus).addHandler(with(any(Type.class)), with(any(EventHandler.class)));
            }
        });
    }

    @After
    public void tearDown() {
        objectToTest = null;
    }

    private void createObjectToTest() {

        objectToTest = new StubbedLiveLoadTypeUnreadyHelper(TAB_ID, "node", "submit data for handset", "enter handset",
                "", "nokiaURL", "TAC", "CS,PS");
    }

    private class StubbedLiveLoadTypeUnreadyHelper extends LiveLoadTypeUnreadyHelper {

        public StubbedLiveLoadTypeUnreadyHelper(final String tabOwnerId, final String valParam, final String submitTip,
                final String emptyText, final String style, final String typeURL, final String permanentType,
                final String winMetaSupport) {
            super(tabOwnerId, valParam, submitTip, emptyText, style, typeURL, permanentType, winMetaSupport);

        }

        public boolean madeAnAttemptAtServerCall;

        public boolean madeMethodCallToUpDateSearchComp;

        private final Collection<LiveLoadTypeDataType> dummyItems = new ArrayList<LiveLoadTypeDataType>();

        @Override
        public void makeServerRequestForTypesData(final EventBus eventBus) {
            madeAnAttemptAtServerCall = true;
        }

        @Override
        public void setUpTypeMenuOnSearchComponent() {
            // not this class job to test PairedSearchComponent innards
            madeMethodCallToUpDateSearchComp = true;
        }

        @Override
        public Collection<LiveLoadTypeDataType> readLiveLoadTypesFromMetaData(final Response response) {

            dummyItems.add(new LiveLoadTypeDataType("nokia", "liveLoadURL1"));
            dummyItems.add(new LiveLoadTypeDataType("sony", "liveLoadURL2"));
            dummyItems.add(new LiveLoadTypeDataType("ericsson", "liveLoadURL3"));

            return dummyItems;
        }

    }

}
