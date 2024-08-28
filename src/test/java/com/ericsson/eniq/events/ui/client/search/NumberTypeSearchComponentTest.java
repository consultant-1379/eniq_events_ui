/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import org.jmock.Expectations;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author eeicmsy
 * @since March 2010
 *
 */
public class NumberTypeSearchComponentTest extends TestEniqEventsUI {

    // TODO   Even mocking a number field falls over in GWT LocaleInfo
    // TODO   numberConstants = infoImpl.getNumberConstants();
    // TODO   numberConstants = infoImpl.getNumberConstants();
    // TODO   numberConstants = infoImpl.getNumberConstants();
    // TODO   numberConstants = infoImpl.getNumberConstants();
    // TODO   numberConstants = infoImpl.getNumberConstants();

    NumberTypeSearchComponent objectToTest;

    ISubmitSearchHandler mockedSubmitSearchHandler;

    NumberField mockedNumberField;

    // TODO @Before
    public void setUp() {
        mockedSubmitSearchHandler = context.mock(ISubmitSearchHandler.class);
        mockedNumberField = context.mock(NumberField.class);
        createObjectToTest();

    }

    @Test
    public void dummy() throws Exception {
        // included for cruise control
        // TODO can not mock a GXT NumberField class
    }

    // TODO @After
    public void tearDown() {
        objectToTest = null;
    }

    // TODO @Test
    public void getSearchComponentValueReturnsExpectedValue() throws Exception {

        final String testVal = "1232";
        context.checking(new Expectations() {
            {
                allowing(mockedNumberField).getValue();
                will(returnValue(testVal));

            }
        });

        final SearchFieldDataType actualVal = objectToTest.getSearchComponentValue();
        final SearchFieldDataType expectedVal = new SearchFieldDataType(testVal,
                new String[] { "something=" + testVal }, null, null, false, "", null, false);
        assertEquals(
                "got expected searchField parameters (warning toString of SearchFieldDataType may interfer with compare message)",
                expectedVal, actualVal);
    }

    // TODO @Test
    public void getSearchComponentReturnsExpectedValue() throws Exception {
        final Component comp = objectToTest.getSearchComponent();
        assertEquals("got expected component", true, comp != null);
    }

    // TODO @Test
    public void performActionForSearchItemSelectsSubmitsCall() throws Exception {

        objectToTest.addSubmitSearchHandler(mockedSubmitSearchHandler);

    }

    private void createObjectToTest() {
        checkExpectationsForObjectCreation();
        objectToTest = new StubbedNumberTypeSearchComponent("SUBSCRIBER_TAB", "Enter something", "something", "tooltip");
    }

    private void checkExpectationsForObjectCreation() {
        context.checking(new Expectations() {
            {
                allowing(mockedNumberField).setEmptyText("Enter something");
                one(mockedNumberField).addKeyListener(
                        with(any(AbstractSingleTypeSearchComponent.SearchFieldUpDatedListener.class)));
            }
        });
    }

    private class StubbedNumberTypeSearchComponent extends NumberTypeSearchComponent {

        public StubbedNumberTypeSearchComponent(final String tabOwnerId, final String emptyText, final String param,
                final String submitButtonTip) {
            super(tabOwnerId, emptyText, param, submitButtonTip);
        }

        @Override
        public NumberField createNumberField() {
            return mockedNumberField;
        }

    }

}
