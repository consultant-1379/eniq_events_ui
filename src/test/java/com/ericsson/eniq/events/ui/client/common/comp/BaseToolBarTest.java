/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.google.web.bindery.event.shared.EventBus;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author eendmcm
 * @since Mar 2010
 */
@Ignore("eendmcm, Need to Ignore as Constructor of ContentPanel calls into Native Method setParent... ")
public class BaseToolBarTest extends TestEniqEventsUI {

    private BaseToolBar objToTest;

    @Before
    public void setUp() {
        final String tabId = "tabID";
        final String windId = "winID";

        objToTest = new StubbedBaseToolBar(createMultipleInstanceWinId(tabId, windId, null), mockedEventBus);
    }

    private MultipleInstanceWinId createMultipleInstanceWinId(final String tabId, final String winId,
            final SearchFieldDataType searchFieldData) {
        return new MultipleInstanceWinId(tabId, winId/*, searchFieldData*/);
    }

    @Test
    @Ignore("eemecoy, tried to get setTimeRangeComboBox() under test but blocked by the GXT in TimeRangeComboBox")
    public void testSetTimeRangeComboBox() {
        objToTest.setTimeRangeComboBox(true);
        objToTest.setTimeRangeComboBox(false);
    }

    @Test
    public void addButtonToToolBar() {

        final Button btn = new Button("Test");
        objToTest.addToolbarItem(btn);
    }

    @Test
    public void addSeperatorToToolBar() {
        objToTest.addToolbarSeperator();
    }

    private class StubbedBaseToolBar extends BaseToolBar {

        public StubbedBaseToolBar(final MultipleInstanceWinId multiWinId, final EventBus eventBus) {
            super(multiWinId, eventBus);
            //super();
            // TODO Auto-generated constructor stub
        }

        @Override
        void addComponent(final Component item) {

        }

        @Override
        protected native void setParent(Widget parent);

    }
}
