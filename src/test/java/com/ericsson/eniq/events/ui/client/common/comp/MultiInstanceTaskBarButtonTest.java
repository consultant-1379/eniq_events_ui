/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.common.comp;

import static junit.framework.Assert.*;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

public class MultiInstanceTaskBarButtonTest extends TestEniqEventsUI {

    BaseWindow mockedBaseWindow;

    private final String ID_ONE = "ID_ONE";

    Menu mockedMenu;

    El mockedDomEl;

    MultiInstanceTaskBarButton objUnderTest;

    @Before
    public void setUp() {
        // failed to mock id into base window so mocking its parts:

        mockedBaseWindow = context.mock(BaseWindow.class);
        mockedMenu = context.mock(Menu.class);
        mockedDomEl = context.mock(El.class);

        objUnderTest = new StubbedMultiInstanceTaskBarButton();

    }

    @Test
    public void addInstanceMenuItemOnlyStoresNonUniqueItems() throws Exception {

        final String ID_ONE = "ID_ONE";
        final String expectedButtonText = (" (1)");

        context.checking(new Expectations() {
            {
                one(mockedMenu).add(with(any(MenuItem.class)));
                one(mockedMenu).add(with(any(MenuItem.class)));
            }
        });

        objUnderTest.addInstance(ID_ONE, mockedBaseWindow);
        // look a second one has no affect !
        objUnderTest.addInstance(ID_ONE, mockedBaseWindow);

        assertEquals("stored menu item", true, objUnderTest.storedMenuIds.containsKey(ID_ONE));
        assertEquals("button text as expected ", expectedButtonText, objUnderTest.getText());
    }

    @Test
    public void buttonTextUpDatesForMenuItemsAdded() throws Exception {

        final String ID_ONE = "ID_ONE";
        final String ID_TWO = "ID_TWO";
        final String ID_THREE = "ID_THREE";
        final String ID_FOUR = "ID_FOUR";

        final String expectedButtonText = (" (4)");

        context.checking(new Expectations() {
            {
                exactly(4).of(mockedMenu).add(with(any(MenuItem.class)));
            }
        });
        objUnderTest.addInstance(ID_ONE, mockedBaseWindow);
        objUnderTest.addInstance(ID_TWO, mockedBaseWindow);
        objUnderTest.addInstance(ID_THREE, mockedBaseWindow);
        objUnderTest.addInstance(ID_FOUR, mockedBaseWindow);

        assertEquals("stored menu items size ok", 4, objUnderTest.storedMenuIds.size());
        assertEquals("button text as expected ", expectedButtonText, objUnderTest.getText());
    }

    @Test
    public void buttonTextUpDatesForMenuItemsRemovedAndAdded() throws Exception {

        final String ID_ONE = "ID_ONE";
        final String ID_TWO = "ID_TWO";
        final String ID_THREE = "ID_THREE";
        final String ID_FOUR = "ID_FOUR";

        final String expectedButtonText = (" (2)");

        context.checking(new Expectations() {
            {
                exactly(4).of(mockedMenu).add(with(any(MenuItem.class)));

                one(mockedMenu).getItemByItemId(ID_THREE);
                will(returnValue(new MenuItem(ID_THREE)));
                one(mockedMenu).getItemByItemId(ID_ONE);
                will(returnValue(new MenuItem(ID_ONE)));

                exactly(2).of(mockedMenu).remove(with(any(MenuItem.class)));
            }
        });
        objUnderTest.addInstance(ID_ONE, mockedBaseWindow);
        objUnderTest.addInstance(ID_TWO, mockedBaseWindow);
        objUnderTest.addInstance(ID_THREE, mockedBaseWindow);
        objUnderTest.addInstance(ID_FOUR, mockedBaseWindow);

        objUnderTest.removeInstance(ID_THREE);
        objUnderTest.removeInstance(ID_ONE);

        assertEquals("stored menu items size ok", 2, objUnderTest.storedMenuIds.size());
        assertEquals("button text as expected ", expectedButtonText, objUnderTest.getText());
    }

    private void addListenerIntoStore() {

        context.checking(new Expectations() {
            {
                one(mockedMenu).add(with(any(MenuItem.class)));

            }
        });
        objUnderTest.addInstance(ID_ONE, mockedBaseWindow);

    }

    @Test
    public void clickWhenWindowMaximisedBlinks() throws Exception {
        addListenerIntoStore();
        context.checking(new Expectations() {
            {
                one(mockedBaseWindow).bringToFront();
            }
        });

        objUnderTest.storedMenuIds.get(ID_ONE).componentSelected(null);
    }

    private class StubbedMultiInstanceTaskBarButton extends MultiInstanceTaskBarButton {

        @Override
        public Menu getMenu() {
            return mockedMenu;
        }

        @Override
        public void setMenu(final Menu menu) {
            super.menu = mockedMenu;
        }

        @Override
        public void setIconStyle(final String tpye) {

        }

    }

}
