/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.common;

/**
 * @author estepdu
 *
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestMethod;
import org.junit.runner.RunWith;

/**
 * Base class for any unit test that wants to use JMock
 * Will set up the JUnit4Mockery instance, context, and will also
 * ensure that all expectations must be satisfied for a test to pass
 * 
 *
 */
@RunWith(JMockTestBaseClass.MyMockRunner.class)
public abstract class JMockTestBaseClass {

    protected Mockery context = new JUnit4Mockery();

    {
        // we need to mock classes, not just interfaces.
        context.setImposteriser(ClassImposteriser.INSTANCE);
    }

    public static class MyMockRunner extends JMock {

        @SuppressWarnings("deprecation")
        public MyMockRunner(final Class<?> testClass) throws InitializationError {
            super(testClass);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected TestMethod wrapMethod(final Method method) {
            return new TestMethod(method, getTestClass()) {
                @Override
                public void invoke(final Object testFixture) throws IllegalAccessException, InvocationTargetException {
                    super.invoke(testFixture);
                    mockeryOf(testFixture).assertIsSatisfied();
                }
            };

        }

    }

}
