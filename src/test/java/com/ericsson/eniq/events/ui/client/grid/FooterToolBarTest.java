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
package com.ericsson.eniq.events.ui.client.grid;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.common.CSSConstants;
import com.ericsson.eniq.events.ui.client.common.ToolTipConstants;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;

public class FooterToolBarTest extends TestEniqEventsUI {

    public JsonObjectWrapper mockedMetaData;

    public Label mockedLabel;

    private StubbedFooterToolBar objToTest;

    public int numSeparatorToolItemsCreated;

    @Before
    public void setupTest() {
        mockedLabel = context.mock(Label.class);
        setUpStyleNameAndToolTipExpectationsOnLabel();
        objToTest = new StubbedFooterToolBar();
    }

    @Test
    public void testOnly1SeparatorToolItemIsCreated() {
        assertThat(numSeparatorToolItemsCreated, is(1));
    }

    private void setUpStyleNameAndToolTipExpectationsOnLabel() {
        context.checking(new Expectations() {
            {
                one(mockedLabel).addStyleName(CSSConstants.LAST_REFRESH_LABEL_CSS);
                one(mockedLabel).setToolTip(ToolTipConstants.LAST_REFRESH_TOOLTIP);
            }
        });

    }

    class StubbedFooterToolBar extends FooterToolBar {

        @Override
        Label createLabel() {
            return mockedLabel;
        }

        @Override
        public boolean add(final Component item) {
            if (item.getClass().equals(SeparatorToolItem.class)) {
                numSeparatorToolItemsCreated++;
            }
            return true;
        }

        @Override
        protected void setLayout(final Layout layout) {
        }

    }
}
