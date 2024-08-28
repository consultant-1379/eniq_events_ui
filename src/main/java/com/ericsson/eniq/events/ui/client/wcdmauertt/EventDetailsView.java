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

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.widgets.client.collapse.CollapsePanel;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessagePanel;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessageType;
import com.ericsson.eniq.events.widgets.client.scroll.ScrollPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventDetailsView extends BaseView<EventDetailsPresenter>{
    final static EventDetailsViewResourceBundle resources;
    private String scrollHeight = "330px";
    private String scrollWidth = "600px";
    private int maxStringLength = 54;
    private ScrollPanel scrollPanel;
    private final String SELENIUM_SUBSCRIBER_INFO = "Subscriber Information";
    private final String SELENIUM_EVENT_INFO = "Event Information";
    private final String SELENIUM_CELLRNC_INFO = "CELL/RNC Information";
    private final String SELENIUM_MISC_INFO = "Miscellaneous Information";


    List<CollapsibleSection> sections = new ArrayList<CollapsibleSection>();

    static {
        resources = GWT.create(EventDetailsViewResourceBundle.class);
        resources.css().ensureInjected();
    }

    // Extracted out to make this class testable
    protected ScrollPanel createScrollPanel()
    {
        return new ScrollPanel();
    }

    protected FlowPanel createFlowPanel()
    {
        return new FlowPanel();
    }

    protected CollapsePanel[] createCollapsePanels(int size)
    {
        return new CollapsePanel[size];
    }

    protected CollapsePanel createCollapsePanel()
    {
        return new CollapsePanel();
    }

    protected ComponentMessagePanel createComponentMessagePanel()
    {
        return new ComponentMessagePanel();
    }

    protected Grid createGrid(int row, int column)
    {
        return new Grid(row,column);
    }

    protected HTML createCellContent(String labelText)
    {
        return new HTML(labelText);
    }

    public Widget createContent(List<CollapsibleSection> sections){
        scrollPanel = createScrollPanel();
        FlowPanel panelsContainer = createFlowPanel();

        if (sections != null) {
            if (sections.size() > 0) {
                CollapsePanel[] collapsePanels = createCollapsePanels(sections.size());

                int i = 0;
                ClickHandler handler = new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        scrollPanel.redraw();
                    }
                };
                for (CollapsibleSection sectionsObj : sections) {

                    List<EventsSummaryDetailsDataType> detailsList = new ArrayList<EventsSummaryDetailsDataType>();
                    Map<String, String> sectionProperties = sectionsObj.getDetails();
                    for (Map.Entry<String, String> entry : sectionProperties.entrySet()) {
                        detailsList.add(new EventsSummaryDetailsDataType(String.valueOf(entry.getValue()), entry
                                .getKey()));
                    }
                    CollapsePanel collapsePanel = addSection(sectionsObj, detailsList);
                    collapsePanel.addHeaderClickHandler(handler);
                    collapsePanels[i++] = collapsePanel;
                }
                for (CollapsePanel collapsePanel : collapsePanels) {
                    panelsContainer.add(collapsePanel);
                }
                scrollPanel.set(panelsContainer);
                setSeleniumTagsOnCollapsePanel(collapsePanels);
            } else {
                noDataMessage(scrollPanel);
            }
        }
        scrollPanel.setSize(scrollWidth,scrollHeight);
        return scrollPanel;
    }
    private CollapsePanel addSection(final CollapsibleSection section,
                                     final List<EventsSummaryDetailsDataType> properties) {
        CollapsePanel newPropertiesSection = createCollapsePanel(section);
        int numRows = properties.size();

        Grid propertiesGrid = createGrid(numRows, 2);
        propertiesGrid.addStyleName(resources.css().summaryGrid());
        propertiesGrid.getColumnFormatter().setWidth(0, "25%");
        propertiesGrid.getColumnFormatter().setWidth(1, "75%");
        for (int row = 0; row < numRows; ++row) {
            if (row % 2 == 0) {
                propertiesGrid.getRowFormatter().addStyleName(row, resources.css().oddRow());
            }
            propertiesGrid.getRowFormatter().addStyleName(row, resources.css().summaryGridRow());

            propertiesGrid.setWidget(row, 0, setUpCellText(properties.get(row).getName(), true));
            propertiesGrid.setWidget(row, 1, setUpCellText(properties.get(row).getValue(), false));
        }
        newPropertiesSection.setContent(propertiesGrid);
        newPropertiesSection.addHeaderClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            }
        });
        return newPropertiesSection;
    }
    private HTML setUpCellText(final String text, final boolean isHeader) {
        String labelText;

        if (text.length() > maxStringLength)
        {
            String labelTextFormatted = text.substring(0, maxStringLength) + "<br/>" + text.substring(maxStringLength, text.length());
            HTML cellContent = createCellContent(labelTextFormatted);
            cellContent.addStyleName(resources.css().gridLabel());
            return cellContent;
        }

        else if (text.equals(" "))
        {
            HTML cellContent = createCellContent("<html><body><p> - </p></body></html>");
            return cellContent;
        }

        else
        {
            labelText = text.replace("\\n", "<br/>");
            HTML cellContent = createCellContent(labelText);
            cellContent.addStyleName(resources.css().gridLabel());
            if (isHeader && labelText.startsWith("-")) {
                cellContent.addStyleName(resources.css().tabText());
                cellContent.setText(labelText.replaceFirst("-", ""));
            }
            return cellContent;
        }
    }

    private CollapsePanel createCollapsePanel(final CollapsibleSection section) {
        CollapsePanel newPropertiesSection = createCollapsePanel();
        newPropertiesSection.setText(section.getSectionState().getId());
        newPropertiesSection.setCollapsed(section.getSectionState().isCollapsed());

        return newPropertiesSection;
    }

    private void noDataMessage(ScrollPanel scrollPanel) {
        ComponentMessagePanel noData = createComponentMessagePanel();
        noData.populate(ComponentMessageType.INFO, "No Data Available", "No Details Available for the Selected Event.");
        scrollPanel.set(noData);
    }

    protected void setSeleniumTagsOnCollapsePanel(final CollapsePanel[] collapsePanels)
    {
        collapsePanels[0].getElement().setId(CommonConstants.SELENIUM_TAG+SELENIUM_SUBSCRIBER_INFO);
        collapsePanels[1].getElement().setId(CommonConstants.SELENIUM_TAG+SELENIUM_EVENT_INFO);
        collapsePanels[2].getElement().setId(CommonConstants.SELENIUM_TAG+SELENIUM_CELLRNC_INFO);
        collapsePanels[3].getElement().setId(CommonConstants.SELENIUM_TAG+SELENIUM_MISC_INFO);
    }
}
