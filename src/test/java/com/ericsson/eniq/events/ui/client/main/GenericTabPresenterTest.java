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
package com.ericsson.eniq.events.ui.client.main;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.junit.*;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.ToolBarStateManager;
import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType;
import com.ericsson.eniq.events.ui.client.events.SearchFieldTypeChangeEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.google.web.bindery.event.shared.EventBus;

public class GenericTabPresenterTest extends TestEniqEventsUI {

    GenericTabView mockedGenericTabView;

    private final List<MetaMenuItem> testMetaData = new ArrayList<MetaMenuItem>();

    @Before
    public void setUp() {
        mockedGenericTabView = context.mock(GenericTabView.class);
    }

    @After
    public void tearDown() {
        testMetaData.clear();
    }

    @Test
    @Ignore
    public void addGenericSelectionListenerToMetaMenuItem() {
        // can not add icon as runs into java script
        final MetaMenuItem genericItem = new MetaMenuItem(new MetaMenuItemDataType.Builder().text("Cause Code Analysis")
                .id("NETWORK_CAUSE_CODE_ANALYSIS").url("RestTest/WHATEVER").windowType(MetaMenuItemDataType.Type.GRID).display("grid").type("IMSI")
                .key("SUM").toolBarHandler(new ToolBarStateManager("TOOLBAR2", ToolBarStateManager.BottomToolbarType.PAGING, "TOOLBAR1"))
                .maxRowsParam("MAXROWSPARAM").minimizedButtonName("Miminised name").build());

        testMetaData.add(genericItem);
        createStubObjectToTest();
        assertEquals("Listener added to menu item", true, testMetaData.get(0).hasListeners());
        assertEquals("One select listener added ", true, testMetaData.get(0).getListeners(Events.Select).size() == 1);
        assertEquals("One select listener added correct type ", true, testMetaData.get(0).getListeners(Events.Select).get(0) instanceof GridLauncher);

    }

    private StubbedGenericTabPresenter createStubObjectToTest() {
        context.checking(new Expectations() {
            {
                one(mockedGenericTabView).getMenuTaskBar();
                one(mockedGenericTabView).getCenterPanel();
                one(mockedGenericTabView).getTabItem();
                one(mockedEventBus).addHandler(with(SearchFieldTypeChangeEvent.TYPE),
                        with(any(GenericTabPresenter.GTPSearchFieldTypeChangeImpl.class)));
            }
        });
        return new StubbedGenericTabPresenter(mockedGenericTabView, mockedEventBus);

    }

    private class StubbedGenericTabPresenter extends GenericTabPresenter {

        public StubbedGenericTabPresenter(final IGenericTabView display, final EventBus eventBus) {
            super(display, eventBus);

        }

        @Override
        List<MetaMenuItem> getMetaMenuItems() {
            return testMetaData;

        }

    }

}
