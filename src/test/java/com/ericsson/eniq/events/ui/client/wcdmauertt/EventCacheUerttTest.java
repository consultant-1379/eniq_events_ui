/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 ******************************************************************************* */
package com.ericsson.eniq.events.ui.client.wcdmauertt;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class EventCacheUerttTest {

    EventPojo eventPojo = new EventPojo("RRC Event Protocol", "RRC", "SENT", "2013-12-10 15:09");
    EventCacheUertt objEventCacheUertt = new EventCacheUertt();

    @Before
    public void setUp() {
        objEventCacheUertt.addToEventList(eventPojo);
    }

    @Test
    public void testaddToEventList() {

        int size = objEventCacheUertt.getEventList().size();
        assertEquals("The size of Array List is 1", 1, size);
    }

    @Test
    public void testclearEventList() {
        objEventCacheUertt.clearEventList();
        int size = objEventCacheUertt.getEventList().size();
        assertEquals("The Array List has been emptied", 0, size);
    }

    @Test
    public void getSubSetElementTestForMoreThanFiveHundred() {
        int pageIndex = 11;
        int eventListSize = 511;
        for (int i = 0; i < eventListSize; i++) {
            objEventCacheUertt.addToEventList(eventPojo);
        }
        int size = objEventCacheUertt.getSubSetElement(pageIndex).size();
        assertEquals("The Array List has been emptied", 12, size);
    }

    @Test
    public void getSubSetElementTestForLessThanFifty() {
        int pageIndex = 1;
        int eventListSize = 45;
        for (int i = 0; i < eventListSize; i++) {
            objEventCacheUertt.addToEventList(eventPojo);
        }
        int size = objEventCacheUertt.getSubSetElement(pageIndex).size();
        assertEquals("The Array List has been emptied", 46, size);
    }

    @Test
    public void getSubSetElementTestForLessThanHundred() {
        int pageIndex = 2;
        int eventListSize = 99;
        for (int i = 0; i < eventListSize; i++) {
            objEventCacheUertt.addToEventList(eventPojo);
        }
        int size = objEventCacheUertt.getSubSetElement(pageIndex).size();
        assertEquals("The Array List has been emptied", 50, size);
    }
}
