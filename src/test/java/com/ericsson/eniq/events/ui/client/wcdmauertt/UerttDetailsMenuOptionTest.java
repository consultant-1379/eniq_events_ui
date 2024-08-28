/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 ******************************************************************************* */

package com.ericsson.eniq.events.ui.client.wcdmauertt;
import static org.junit.Assert.*;
import com.google.gwt.user.client.Element;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Label;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.widgets.client.menu.options.OptionsMenuPanel;

public class UerttDetailsMenuOptionTest extends TestEniqEventsUI {

    Label mockLabel;
    StubbedUerttDetailsMenuOption objToTest;
    OptionsMenuPanel<uerttMenuItem> mockOptionsMenuPanel;

    @Before
    public void setUp() throws Exception {
        mockLabel = context.mock(Label.class, " ");
        mockOptionsMenuPanel = context.mock(OptionsMenuPanel.class);
        objToTest = new StubbedUerttDetailsMenuOption(mockLabel, mockOptionsMenuPanel);
    }

    @Test
    public void testInitMenuOption() {
        setUpExpectationsOnMenuOptions();
        objToTest.initMenuOption();
    }

    @SuppressWarnings("unchecked")
    private void setUpExpectationsOnMenuOptions() {
        context.checking(new Expectations() {
            {
                allowing(mockLabel).setStyleName(with(any(String.class)));
                allowing(mockLabel).addClickHandler(with(any(ClickHandler.class)));
                allowing(mockLabel).removeFromParent();
                allowing(mockLabel).getElement();
                allowing(mockOptionsMenuPanel).setAutohidePartner(with(any(Element.class)));
                allowing(mockOptionsMenuPanel).addCloseHandler(with(any(CloseHandler.class)));
                allowing(mockOptionsMenuPanel).addSelectionHandler(with(any(SelectionHandler.class)));

            }
        });
    }

    class StubbedUerttDetailsMenuOption extends UerttDetailsMenuOption {
        Label mockLabel;
        OptionsMenuPanel<uerttMenuItem> optionsMenuPanel;

        public StubbedUerttDetailsMenuOption(final Label mockLabel, final OptionsMenuPanel<uerttMenuItem> optionsMenuPanel) {
            this.mockLabel = mockLabel;
            this.optionsMenuPanel = optionsMenuPanel;
        }

        @Override
        protected Label createLabel(String labelText) {
            return mockLabel;
        }

        @Override
       protected void initialise(Label label){

       }

        @Override
        protected OptionsMenuPanel<uerttMenuItem> createOptionsMenuPanel() {
            return optionsMenuPanel;
        }
        
        @Override
        protected void setSeleniumTagOnLabel(Label label)
        {
            
        }
    }

}
