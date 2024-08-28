/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.search;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.widgets.client.button.ImageButton;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.TextField;
import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.google.gwt.event.dom.client.KeyCodes.KEY_BACKSPACE;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_ENTER;
import static junit.framework.Assert.assertEquals;

/** @author eeicmsy */
public class StringTypeSearchComponentTest extends TestEniqEventsUI {

   AbstractSingleTypeSearchComponent objectToTest;

   ISubmitSearchHandler mockedSubmitSearchHandler;

   TextField mockedTextField;

   ComponentEvent mockedComponentEvent;

   /* Button mockedSubmitButton;*/
   ImageButton mockedSubmitButton;

   @Before
   public void setUp() {
      mockedSubmitSearchHandler = context.mock(ISubmitSearchHandler.class);
      mockedTextField = context.mock(TextField.class);
      mockedComponentEvent = context.mock(ComponentEvent.class);
      /*mockedSubmitButton = context.mock(Button.class);*/
      mockedSubmitButton = context.mock(ImageButton.class);
      createObjectToTest();

   }

   @After
   public void tearDown() {
      objectToTest = null;
   }

   @Test
   public void getSearchComponentValueReturnsExpectedValue() throws Exception {

      final String testVal = "hello momma";
      context.checking(new Expectations() {
         {
            allowing(mockedTextField).getValue();
            will(returnValue(testVal));

         }
      });

      final SearchFieldDataType actualVal = objectToTest.getSearchComponentValue();
      final SearchFieldDataType expectedVal = new SearchFieldDataType(testVal, new String[]{"something=" + testVal}, null, null,
              false, "", null, false);
      assertEquals(
              "got expected searchField parameters (warning toString of SearchFieldDataType may interfer with compare message)",
              expectedVal, actualVal);
   }

   @Test
   public void getSearchComponentReturnsExpectedValue() throws Exception {
      final Component comp = objectToTest.getSearchComponent();
      assertEquals("got expected component", true, comp != null);
   }

   @Test
   public void initiateWithEventBusOkNotToUse() throws Exception {
      objectToTest.registerWithEventBus(null);
   }

   @Test
   public void handleEnterPressSubmitsDataToServer() throws Exception {
      context.checking(new Expectations() {
         {
            one(mockedSubmitSearchHandler).submitSearchFieldInfo();
         }
      });
      objectToTest.addSubmitSearchHandler(mockedSubmitSearchHandler);
      objectToTest.isMasked = false;
      objectToTest.searchFieldSelectionMadeListener.handleEnterPress(KEY_ENTER);
   }

   @Test
   public void handleEnterPressDoesNotSubmitsDataToServerWhenAlreadySubmitted() throws Exception {
      context.checking(new Expectations() {
         {
            never(mockedSubmitSearchHandler).submitSearchFieldInfo();
         }
      });
      objectToTest.addSubmitSearchHandler(mockedSubmitSearchHandler);
      objectToTest.isMasked = true;
      objectToTest.searchFieldSelectionMadeListener.handleEnterPress(KEY_ENTER);
   }

   @Test
   public void handleAnyThingNotEnterDoesNotSubmitsDataToServer() throws Exception {
      context.checking(new Expectations() {
         {
            never(mockedSubmitSearchHandler).submitSearchFieldInfo();
         }
      });
      objectToTest.addSubmitSearchHandler(mockedSubmitSearchHandler);
      objectToTest.isMasked = false;
      objectToTest.searchFieldSelectionMadeListener.handleEnterPress(KEY_BACKSPACE);
   }

   @Test
   public void handleSubmitButtonEnabledWhenStringChanges() throws Exception {
      context.checking(new Expectations() {
         {
            one(mockedTextField).getValue();
            will(returnValue("Text"));
            one(mockedComponentEvent).getKeyCode();
            will(returnValue(33));
            never(mockedSubmitSearchHandler).submitSearchFieldInfo();
            /* one(mockedSubmitButton).enable();*/
            one(mockedSubmitButton).setEnabled(true);
         }
      });

      objectToTest.searchFieldSelectionMadeListener.componentKeyUp(mockedComponentEvent);
   }

   @Test
   public void handleSubmitButtonDisabledWhenStringLengthZero() throws Exception {
      context.checking(new Expectations() {
         {
            one(mockedTextField).getValue();
            will(returnValue(""));
            one(mockedComponentEvent).getKeyCode();
            will(returnValue(33));
            never(mockedSubmitSearchHandler).submitSearchFieldInfo();
            /* one(mockedSubmitButton).disable();*/
            one(mockedSubmitButton).setEnabled(false);
         }
      });

      objectToTest.searchFieldSelectionMadeListener.componentKeyUp(mockedComponentEvent);
   }

   @Test
   public void handleSubmitButtonDisabledWhenFieldSubmitted() throws Exception {
      context.checking(new Expectations() {
         {
            one(mockedTextField).getValue();
            will(returnValue(""));
            one(mockedComponentEvent).getKeyCode();
            will(returnValue(KEY_ENTER));
            one(mockedSubmitSearchHandler).submitSearchFieldInfo();
            /* one(mockedSubmitButton).disable();*/
            one(mockedSubmitButton).setEnabled(false);
         }
      });
      objectToTest.addSubmitSearchHandler(mockedSubmitSearchHandler);
      objectToTest.isMasked = false;
      objectToTest.searchFieldSelectionMadeListener.componentKeyUp(mockedComponentEvent);
   }

   private void createObjectToTest() {
      checkExpectationsForObjectCreation();
      objectToTest = new StubbedStringTypeSearchComponent("tabId", "Enter something", "something");
   }

   private void checkExpectationsForObjectCreation() {
      context.checking(new Expectations() {
         {
            allowing(mockedTextField).setEmptyText("Enter something");
            one(mockedTextField).addKeyListener(
                    with(any(AbstractSingleTypeSearchComponent.SearchFieldUpDatedListener.class)));
            one(mockedTextField).sinkEvents(0x80000);
            one(mockedTextField).addListener(with(any(EventType.class)),
                    with(any(AbstractSingleTypeSearchComponent.MouseListener.class)));
         }
      });
   }

   private class StubbedStringTypeSearchComponent extends StringTypeSearchComponent {

      public StubbedStringTypeSearchComponent(final String tabOwnerId, final String emptyText, final String param) {
         super(tabOwnerId, emptyText, param);
         submitButton = mockedSubmitButton;
         // TODO Auto-generated constructor stub
      }

      @Override
      public TextField<String> createTextField() {
         return mockedTextField;
      }

   }

}
