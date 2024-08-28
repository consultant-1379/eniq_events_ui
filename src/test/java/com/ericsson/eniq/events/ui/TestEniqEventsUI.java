/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui;

import java.lang.reflect.Method;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.messages.XMessages;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWTBridge;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.web.bindery.event.shared.EventBus;

/**
 * JUnit FrameWork for GXT, GWT mix
 *
 * (Avoiding GWT, GXT leading us down a junit 3 root)
 * 
 * @author eeicmsy
 * @since March 2010
 *
 */
@RunWith(JMock.class)
public abstract class TestEniqEventsUI {

    private static boolean isDisarmed;

    protected Mockery context = new JUnit4Mockery();

    {
        // we need to mock classes, not just interfaces.
        context.setImposteriser(ClassImposteriser.INSTANCE);
    }

    public EventBus mockedEventBus = context.mock(EventBus.class);

    @Before
    public void mockXMessages() {
        final XMessages mockedXMessages = context.mock(XMessages.class);
        context.checking(new Expectations() {
            {
                ignoring(mockedXMessages);
            }
        });

        GXT.MESSAGES = mockedXMessages;
    }

    @BeforeClass
    public static void setup() throws Exception {

        if (!isDisarmed) {
            GXTMockUtilities.disarm(); // our own class
            GWTMockUtilities.disarm();
            isDisarmed = true;
        }
        //the default GWTDummyBridge that GWT provides returns null for classes created, which isn't too good
        //See java doc on GWTMockDummyBridge for what it does differently
        setGwtBridge(new GWTMockDummyBridge());

    }

    /**
     * Install the given instance of {@link GWTBridge}, allowing it to override
     * the behavior of calls to {@link GWT#create(Class)}.
     * @throws Exception 
     */
    private static void setGwtBridge(final GWTBridge bridge) throws Exception {
        final Class<GWT> gwtClass = GWT.class;
        final Class<?>[] paramTypes = new Class[] { GWTBridge.class };
        final Method setBridgeMethod = gwtClass.getDeclaredMethod("setBridge", paramTypes);
        setBridgeMethod.setAccessible(true);
        setBridgeMethod.invoke(gwtClass, new Object[] { bridge });
    }

    protected void setUpGeneralExpectationsOnBusWhenDontCareAboutTypes() {
        context.checking(new Expectations() {
            {
                allowing(mockedEventBus).addHandler(with(any(Type.class)), with(any(EventHandler.class)));
            }
        });

    }

    public <T> T createAndIgnore(final Class<T> typeToMock) {
        final T mock = context.mock(typeToMock);
        context.checking(new Expectations() {
            {
                ignoring(mock);
            }
        });
        return mock;
    }
}
