/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.events;

import static junit.framework.Assert.*;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;

/**
 * @author eeicmsy
 *
 */
public class MetaDataReadyEventTest extends TestEniqEventsUI {

    private MetaDataReadyEvent objectUnderTest;

    private MetaDataReadyEventHandler mockedMetaDataReadyEventHandler;

    @Before
    public void setUp() {
        mockedMetaDataReadyEventHandler = context.mock(MetaDataReadyEventHandler.class);
        objectUnderTest = new MetaDataReadyEvent();
    }

    @After
    public void tearDown() {
        objectUnderTest = null;
    }

    @Test
    public void sendsNotificationWhenMetaDataReady() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockedMetaDataReadyEventHandler).handleMetaDataReadyEvent();
            }
        });
        objectUnderTest.dispatch(mockedMetaDataReadyEventHandler);
    }

    @Test
    public void getAssociatedTypeasExpected() throws Exception {
        assertEquals("Type is as excepted", MetaDataReadyEvent.TYPE, objectUnderTest.getAssociatedType());
    }
}
