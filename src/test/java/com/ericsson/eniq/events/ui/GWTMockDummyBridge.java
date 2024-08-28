package com.ericsson.eniq.events.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import com.google.gwt.core.client.GWTBridge;
import com.google.gwt.dev.About;
import com.google.gwt.junit.GWTMockUtilities;

/**
 * A dummy implementation of {@link GWTBridge}, which instantiates mock versions of classes
 * Inspired by GWT's GWTDummyBridge
 * 
 * @see GWTMockUtilities
 */
class GWTMockDummyBridge extends GWTBridge {
    private static final Logger logger = Logger.getLogger(GWTMockDummyBridge.class.getName());

    protected Mockery context = new JUnit4Mockery();
    {
        context.setImposteriser(ClassImposteriser.INSTANCE);
    }

    /**
     * Overriding GWT code to return a mocked version of the requested class.
     * Additionally, we specify here that any calls are valid on this class 
     * @return mock of class
     */
    @Override
    public <T> T create(final Class<?> classLiteral) {
        final T mock = (T) context.mock(classLiteral);
        context.checking(new Expectations() {
            {
                ignoring(mock);
            }
        });
        return mock;
    }

    /**
     * @return the current version of GWT ({@link About#getGwtVersionNum()})
     */
    @Override
    public String getVersion() {
        return About.getGwtVersionNum();
    }

    /**
     * @return false
     */
    @Override
    public boolean isClient() {
        return false;
    }

    /**
     * Logs the message and throwable to the standard logger, with level {@link
     * Level#SEVERE}.
     */
    @Override
    public void log(final String message, final Throwable e) {
        logger.log(Level.SEVERE, message, e);
    }
}
