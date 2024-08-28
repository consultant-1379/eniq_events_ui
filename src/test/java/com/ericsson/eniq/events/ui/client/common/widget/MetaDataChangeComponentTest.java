/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget;

import static junit.framework.Assert.*;

import java.util.ArrayList;
import java.util.List;

import com.google.web.bindery.event.shared.EventBus;
import com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.datatype.MetaDataChangeDataType;
import com.ericsson.eniq.events.ui.client.events.MetaDataChangeEvent;
import com.ericsson.eniq.events.ui.client.events.MetaDataChangeEventHandler;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.event.shared.GwtEvent.Type;

/**
 * @author eeicmsy
 *
 */
public class MetaDataChangeComponentTest extends TestEniqEventsUI {

    MetaDataChangeComponent objToTest;

    WidgetDisplay mockedDisplay;

    Menu mockedMenu;

    @Before
    public void setUp() {
        mockedDisplay = context.mock(WidgetDisplay.class);
        mockedMenu = context.mock(Menu.class);

    }

    @After
    public void tearDown() {
        objToTest = null;
    }

    @Test
    public void bothLicencesShouldDisplayComponent() throws Exception {

        objToTest = getObjectToTest("METADATA/UI", true, true);
        assertEquals("The combobox should be displayed as expected", true, objToTest.shouldBeDisplayed());

    }

    @Test
    public void singleLicencesShouldNotDisplayComponent() throws Exception {

        objToTest = getObjectToTest("METADATA/UI", false, true);
        assertEquals("The combobox should not be displayed", false, objToTest.shouldBeDisplayed());

    }

    @Test
    public void expectedStartUpShouldBeLicencedOne() throws Exception {

        objToTest = getObjectToTest("METADATA/UI", false, true);

        final MetaDataChangeDataType startupItem = objToTest.getStartupItem();
        final String actaulPath = startupItem.getMetaDataPath();
        // only licenced CS
        final String expectedStartup = "METADATA/UI_MSS";

        assertEquals("The combobox should not be displayed", false, objToTest.shouldBeDisplayed());

        assertEquals("The start up display should be circuit switched as that all have licence for", expectedStartup,
                actaulPath);

    }

    @Test
    public void expectedStartUpCanBeNull() throws Exception {

        objToTest = getObjectToTest(null, true, false);

        final MetaDataChangeDataType startupItem = objToTest.getStartupItem();
        final String actaulPath = startupItem.getMetaDataPath();

        final String expectedStartup = "METADATA/UI";

        assertEquals("The combobox should not be displayed", false, objToTest.shouldBeDisplayed());

        assertEquals("The start up display should be packet switched as that all have licence for", expectedStartup,
                actaulPath);

    }

    @Test
    public void noLicencesWillNotDisplayCombo() throws Exception {

        context.checking(new Expectations() {
            {
                one(mockedEventBus).addHandler(with(any(Type.class)), with(any(MetaDataChangeEventHandler.class)));

            }
        });

        final List<MetaDataChangeDataType> menuItems = getMetaDataChangeMenuItems(false, false);
        objToTest = new MetaDataChangeComponentStub(mockedEventBus, menuItems, null);

        assertEquals("The combobox should not be displayed", false, objToTest.shouldBeDisplayed());
        assertEquals("The component should say has no licences", false, objToTest.getLicenceCount() > 0);

    }

    @Test
    public void handleMetaDataChangeEvent() throws Exception {
        objToTest = getObjectToTest("METADATA/UI_MSS", true, true);

        MetaDataChangeDataType changeItem = new MetaDataChangeDataTypeStub("Data", "Data", null,
                "Packet switched menu options", "METADATA/UI", "PS", true);

        objToTest.handleMetaDataChangeEvent(changeItem);
        String actaul = objToTest.getKey();
        String expectedName = "PS";
        assertEquals("The combobox has changed", expectedName, actaul);

        changeItem = new MetaDataChangeDataTypeStub("Circuit Switched", "Voice", null, "Circuit switched menu options",
                "METADATA/UI_MSS", "CS", true);

        objToTest.handleMetaDataChangeEvent(changeItem);
        actaul = objToTest.getKey();
        expectedName = "CS";
        assertEquals("The combobox has changed again", expectedName, actaul);

    }

    ///////////////////////////////////////////////////////////////////////////////////
    ////////////    Tests END  //////////// 
    ///////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    private MetaDataChangeComponent getObjectToTest(final String startupPath, final boolean isPacketLicence,
            final boolean isCircuitLicence) {

        context.checking(new Expectations() {
            {
                one(mockedMenu).setShadow(false);
                exactly(2).of(mockedMenu).add(with(any(MetaDataChangeDataType.class)));
                one(mockedEventBus).fireEvent(with(any(MetaDataChangeEvent.class)));
                one(mockedEventBus).addHandler(with(any(Type.class)), with(any(MetaDataChangeEventHandler.class)));

            }
        });

        final List<MetaDataChangeDataType> menuItems = getMetaDataChangeMenuItems(isPacketLicence, isCircuitLicence);
        return new MetaDataChangeComponentStub(mockedEventBus, menuItems, startupPath);

        //  assertEquals(afterCall + " is the same starts as expected", true, afterCall.startsWith(expectedStartAfter));

    }

    private List<MetaDataChangeDataType> getMetaDataChangeMenuItems(final boolean isPacketLicence,
            final boolean isCircuitLicence) {
        final MetaDataChangeDataType menu1 = new MetaDataChangeDataTypeStub("Packet Switched", "PS", null,
                "Packet switched menu options", "METADATA/UI", "PS", isPacketLicence);

        final MetaDataChangeDataType menu2 = new MetaDataChangeDataTypeStub("Circuit Switched", "Voice", null,
                "Circuit switched menu options", "METADATA/UI_MSS", "CS", isCircuitLicence);

        final List<MetaDataChangeDataType> menuItems = new ArrayList<MetaDataChangeDataType>();
        menuItems.add(menu1);
        menuItems.add(menu2);

        return menuItems;
    }

    @SuppressWarnings("unchecked")
    private class MetaDataChangeComponentStub<D extends WidgetDisplay> extends MetaDataChangeComponent {

        public MetaDataChangeComponentStub(final EventBus bus,
                final List<MetaDataChangeDataType> menuItems, final String startupPath) {
            super(bus, menuItems, startupPath);
        }

        @Override
        public Menu createMenu() {
            return mockedMenu;
        }

        @Override
        public void setMenu(final Menu menu) {
            super.menu = mockedMenu;
        }

        @Override
        public void loadMetaData(final String path) {

        }

        @Override
        public void setToolTip(final String text) {
        }

        @Override
        public void setIconStyle(final String text) {

        }

    }

    private class MetaDataChangeDataTypeStub extends MetaDataChangeDataType {

        public MetaDataChangeDataTypeStub(final String name, final String shortText, final String style,
                final String tip, final String metaDataPath, final String key, final boolean isLicenced) {
            super(name, shortText, style, tip, metaDataPath, key, isLicenced);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void setToolTip(final String text) {
        }

    }

}
