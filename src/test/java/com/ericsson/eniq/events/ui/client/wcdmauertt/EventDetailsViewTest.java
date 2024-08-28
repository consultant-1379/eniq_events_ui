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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.widgets.client.collapse.CollapsePanel;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessagePanel;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessageType;
import com.ericsson.eniq.events.widgets.client.scroll.ScrollPanel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class EventDetailsViewTest extends TestEniqEventsUI {

    private StubbedEventDetailsView objToTest;
    ScrollPanel mockScrollPanel;
    FlowPanel mockFlowPanel;
    CollapsePanel mockCollapsePanel;
    ComponentMessagePanel mockComponentMessagePanel;
    List<CollapsibleSection> sections = new ArrayList<CollapsibleSection>();
    Grid mockGrid;
    HTML mockCellContent;

    @Before
    public void setUp() throws Exception {
        mockScrollPanel = context.mock(ScrollPanel.class);
        mockFlowPanel = context.mock(FlowPanel.class);
        mockCollapsePanel = context.mock(CollapsePanel.class);
        mockComponentMessagePanel = context.mock(ComponentMessagePanel.class);
        mockGrid = context.mock(Grid.class);
        mockCellContent = context.mock(HTML.class);

        CollapsibleSectionState subscriberInfo = new CollapsibleSectionState();
        subscriberInfo.setId("Subscriber Information");
        subscriberInfo.setCollapsed(false);
        Map<String, String> data1 = new HashMap<String, String>();
        data1.put("IMSI", "45000000006123");
        data1.put("IMEISV", "45000000066527");
        CollapsibleSection section1 = new CollapsibleSection(subscriberInfo, data1);
        sections.add(section1);

        CollapsibleSectionState eventInfo = new CollapsibleSectionState();
        subscriberInfo.setId("Event Information");
        subscriberInfo.setCollapsed(false);
        Map<String, String> data2 = new HashMap<String, String>();
        data1.put("IMSI", "76500000012356");
        data1.put("IMEISV", "723000000066527");
        CollapsibleSection section2 = new CollapsibleSection(eventInfo, data2);
        sections.add(section2);

        objToTest = new StubbedEventDetailsView(mockScrollPanel, mockFlowPanel, mockComponentMessagePanel, mockCollapsePanel, mockGrid,
                mockCellContent);
    }

    @Test
    public void testCreateContent_EmptySections() {
        setUpExpectationsOnGWTComponents();
        sections.clear();
        objToTest.createContent(sections);
    }

    @Test
    public void testCreateContent_NonEmptySections() {
        setUpExpectationsOnGWTComponents();
        objToTest.createContent(sections);
    }

    @SuppressWarnings("unchecked")
    private void setUpExpectationsOnGWTComponents() {
        context.checking(new Expectations() {
            {
                allowing(mockScrollPanel).set(with(any(FlowPanel.class)));
                allowing(mockScrollPanel).setSize(with(any(String.class)), with(any(String.class)));
                allowing(mockFlowPanel).add(with(any(CollapsePanel.class)));
                allowing(mockCollapsePanel).addHeaderClickHandler(with(any(ClickHandler.class)));
                allowing(mockCollapsePanel).setText(with(any(String.class)));
                allowing(mockCollapsePanel).setCollapsed(with(any(Boolean.class)));
                allowing(mockCollapsePanel).setContent(with(any(Widget.class)));
                allowing(mockComponentMessagePanel).populate(with(any(ComponentMessageType.class)), with(any(String.class)), with(any(String.class)));
                allowing(mockGrid).addStyleName(with(any(String.class)));
                allowing(mockGrid).getRowFormatter();
                allowing(mockGrid).getColumnFormatter().setWidth(with(any(Integer.class)), with(any(String.class)));
                allowing(mockGrid).setWidget(with(any(Integer.class)), with(any(Integer.class)), with(any(Widget.class)));
                allowing(mockCellContent).addStyleName(with(any(String.class)));
                allowing(mockCellContent).setText(with(any(String.class)));
            }
        });
    }

    class StubbedEventDetailsView extends EventDetailsView {
        private ScrollPanel scrollPanel;
        private FlowPanel flowPanel;
        private ComponentMessagePanel componentMessagePanel;
        private CollapsePanel collapsePanel;
        private Grid gridValue;
        private HTML cellContent;

        public StubbedEventDetailsView() {
            super();
        }

        public StubbedEventDetailsView(final ScrollPanel scrollPanel, final FlowPanel flowPanel, final ComponentMessagePanel componentMessagePanel,
                                       final CollapsePanel collapsePanel, final Grid gridValue, final HTML cellContent) {
            this.scrollPanel = scrollPanel;
            this.flowPanel = flowPanel;
            this.componentMessagePanel = componentMessagePanel;
            this.collapsePanel = collapsePanel;
            this.gridValue = gridValue;
            this.cellContent = cellContent;
        }

        @Override
        protected ScrollPanel createScrollPanel() {
            return scrollPanel;
        }

        @Override
        protected FlowPanel createFlowPanel() {
            return flowPanel;
        }

        @Override
        protected CollapsePanel[] createCollapsePanels(int size) {
            return new CollapsePanel[size]; 
        }

        @Override
        protected ComponentMessagePanel createComponentMessagePanel() {
            return componentMessagePanel;
        }

        @Override
        protected CollapsePanel createCollapsePanel() {
            return collapsePanel;
        }

        @Override
        protected Grid createGrid(int row, int column) {
            return gridValue;
        }

        @Override
        protected HTML createCellContent(String labelText) {
            return cellContent;
        }

        @Override
        protected void setSeleniumTagsOnCollapsePanel(final CollapsePanel[] collapsePanels)
        {}
    }
}
