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

package com.ericsson.eniq.events.ui.client.wcdmauertt;

import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

public class EventGridViewUerttTest extends TestEniqEventsUI {
    Label ue;
    Label eNb;
    Label srnc;
    Label drnc;
    Label cn;
    Label mockArrowLabel;
    Label timestamp;
    Label mockLabelWidget;
    HTML mockHtml;
    FlexTable displayTable;
    Map<String, Label> mapping;
    VerticalPanel mockVerticalPanel;
    EventGridViewUertt objUnderTest;
    UerttDetailsMenuOption objUerttDetailsMenuOption;
    private final String SELENIUM_DATA_TABLE  = "DATA TABLE";

    @Before
    public void setUp() {
        ue = context.mock(Label.class, "ue");
        eNb = context.mock(Label.class, "NodeB");
        srnc = context.mock(Label.class, "srnc");
        drnc = context.mock(Label.class, "drnc");
        cn = context.mock(Label.class, "cn");
        timestamp = context.mock(Label.class, "timestamp");
        mockArrowLabel = context.mock(Label.class, "mockArrowLabel");
        mockLabelWidget = context.mock(Label.class, " ");
        mockHtml = context.mock(HTML.class);
        displayTable = context.mock(FlexTable.class);
        mapping = new HashMap<String, Label>();
        mapping.put("UE", ue);
        mapping.put("NodeB", eNb);
        mapping.put("SRNC", srnc);
        mapping.put("DRNC", drnc);
        mapping.put("CN", cn);
        mapping.put("Timestamp", timestamp);
        mapping.put(" ", mockLabelWidget);
        mockVerticalPanel = context.mock(VerticalPanel.class);
        objUerttDetailsMenuOption = context.mock(UerttDetailsMenuOption.class);
        objUnderTest = new StubbedEventGridViewUertt(displayTable, mapping,
                mockArrowLabel, objUerttDetailsMenuOption, mockVerticalPanel, mockHtml);

    }

    @Test
    public void testremoveUnderScoresFromEventName() {
        objUnderTest = new EventGridViewUertt();
        String eventName = "RNC_EVENT_RRC_Event_Protocol";
        String newEventName = objUnderTest
                .removeUnderScoresFromEventName(eventName);
        assertEquals("The protocol name is as per requirement",
                "Event Protocol", newEventName);

    }

    @Test
    public void testremoveUnderScoresFromEventName_UnknownEvent() {
        objUnderTest = new EventGridViewUertt();
        String eventName = "INTERNAL_EVENT_RELEASE";
        String newEventName = objUnderTest
                .removeUnderScoresFromEventName(eventName);
        assertEquals("The protocol name is as per requirement",
                "INTERNAL_EVENT_RELEASE", newEventName);

    }

    @Test
    public void testremoveUnderScoresFromEventName_EventProtocolName()
    {
        objUnderTest = new EventGridViewUertt();
        String eventName = "EVENT_PROTOCOL_RRC";
        String newEventName = objUnderTest
                .removeUnderScoresFromEventName(eventName);
        assertEquals("The protocol name is as per requirement",
                "RRC", newEventName);
    }

    @Test
    public void testremoveUnderScoresFromEventName_RandomName()
    {
        objUnderTest = new EventGridViewUertt();
        String eventName = "INCORRECT_EVENTNAME";
        String newEventName = objUnderTest
                .removeUnderScoresFromEventName(eventName);
        assertEquals("The protocol name is as per requirement",
                "INCORRECT_EVENTNAME", newEventName);
    }


    @Test
    public void testInit() throws Exception {

        setUpExpectationsOnGWTComponents();
        objUnderTest.init();
    }

    @Test
    public void testaddBlankRowAfterNodes_MoreThanOneRow() {
        setUpExpectationsOnGWTComponents();
        objUnderTest.init();
        int rowCount1 = objUnderTest.addBlankRowAfterNodes(0);
        int rowCount2 = objUnderTest.addBlankRowAfterNodes(rowCount1);
        assertEquals(2, rowCount2);
    }

    @Test
    public void testaddEventWithDirectionInformaton_MoreThanOneEvent() {
        setUpExpectationsOnGWTComponents();
        objUnderTest.init();
        int rowCount1 = objUnderTest.addEventWithDirectionInformaton(0, "SENT",
                "EVENT_PROTOCOL_RRC", "RNC_EVENT_RRC_Event_Protocol",
                "2013-12-03 09:00.00");
        int rowCount2 = objUnderTest.addEventWithDirectionInformaton(rowCount1,
                "SENT", "EVENT_PROTOCOL_RANAP",
                "RNC_EVENT_RANAP_Event_Protocol", "2013-12-03 09:00.00");
        int rowCount3 = objUnderTest.addEventWithDirectionInformaton(rowCount2,
                "RECEIVED", "EVENT_PROTOCOL_RANAP",
                "RNC_EVENT_RANAP_Event_Protocol", "2013-12-03 09:00.00");
        assertEquals(3, rowCount3);
    }

    @Test
    public void testaddEventWithDirectionInformaton_SameEvents() {
        setUpExpectationsOnGWTComponents();
        objUnderTest.init();
        int rowCount1 = objUnderTest.addEventWithDirectionInformaton(0, "SENT",
                "EVENT_PROTOCOL_RRC", "RNC_EVENT_RRC_Event_Protocol",
                "2013-12-03 09:00.00");
        int rowCount2 = objUnderTest.addEventWithDirectionInformaton(rowCount1,
                "SENT", "EVENT_PROTOCOL_RRC", "RNC_EVENT_RRC_Event_Protocol",
                "2013-12-03 09:00.00");
        assertEquals(2, rowCount2);
    }

    @Test
    public void testaddEventWithDirectionInformaton_UnknownEvent() {
        setUpExpectationsOnGWTComponents();
        objUnderTest.init();
        int rows = objUnderTest.addEventWithDirectionInformaton(0, "SENT",
                "EVENT_PROTOCOL_UnknownEvent",
                "RNC_EVENT_Unknown_Event_Protocol", "2013-12-03 09:00.00");
        assertEquals(0, rows);
    }

    @Test
    public void testaddEventWithDirectionInformaton_RRCSent() {
        setUpExpectationsOnGWTComponents();
        objUnderTest.init();
        int rows = objUnderTest.addEventWithDirectionInformaton(0, "SENT",
                "EVENT_PROTOCOL_RRC", "RNC_EVENT_RRC_Event_Protocol",
                "2013-12-03 09:00.00");
        assertEquals(1, rows);
    }

    @Test
    public void testaddEventWithDirectionInformaton_RRCReceived() {
        setUpExpectationsOnGWTComponents();
        objUnderTest.init();
        int rows = objUnderTest.addEventWithDirectionInformaton(0, "RECEIVED",
                "EVENT_PROTOCOL_RRC", "RNC_EVENT_RRC_Event_Protocol",
                "2013-12-03 09:00.00");
        assertEquals(1, rows);
    }

    @Test
    public void testaddEventWithDirectionInformaton_RANAPSent() {
        setUpExpectationsOnGWTComponents();
        objUnderTest.init();
        int rows = objUnderTest.addEventWithDirectionInformaton(0, "SENT",
                "EVENT_PROTOCOL_RANAP", "RNC_EVENT_RANAP_Event_Protocol",
                "2013-12-03 09:00.00");
        assertEquals(1, rows);
    }

    @Test
    public void testaddEventWithDirectionInformaton_RANAPReceived() {
        setUpExpectationsOnGWTComponents();
        objUnderTest.init();
        int rows = objUnderTest.addEventWithDirectionInformaton(0, "RECEIVED",
                "EVENT_PROTOCOL_RANAP", "RNC_EVENT_RANAP_Event_Protocol",
                "2013-12-03 09:00.00");
        assertEquals(1, rows);
    }

    @Test
    public void testaddEventWithDirectionInformaton_RNSAPSent() {
        setUpExpectationsOnGWTComponents();
        objUnderTest.init();
        int rows = objUnderTest.addEventWithDirectionInformaton(0, "SENT",
                "EVENT_PROTOCOL_RNSAP", "RNC_EVENT_RNSAP_Event_Protocol",
                "2013-12-03 09:00.00");
        assertEquals(1, rows);
    }

    @Test
    public void testaddEventWithDirectionInformaton_RNSAPReceived() {
        setUpExpectationsOnGWTComponents();
        objUnderTest.init();
        int rows = objUnderTest.addEventWithDirectionInformaton(0, "RECEIVED",
                "EVENT_PROTOCOL_RNSAP", "RNC_EVENT_RNSAP_Event_Protocol",
                "2013-12-03 09:00.00");
        assertEquals(1, rows);
    }

    @Test
    public void testaddEventWithDirectionInformaton_NBAPSent() {
        setUpExpectationsOnGWTComponents();
        objUnderTest.init();
        int rows = objUnderTest.addEventWithDirectionInformaton(0, "SENT",
                "EVENT_PROTOCOL_NBAP", "RNC_EVENT_NBAP_Event_Protocol",
                "2013-12-03 09:00.00");
        assertEquals(1, rows);
    }

    @Test
    public void testaddEventWithDirectionInformaton_NBAPReceived() {
        setUpExpectationsOnGWTComponents();
        objUnderTest.init();
        int rows = objUnderTest.addEventWithDirectionInformaton(0, "RECEIVED",
                "EVENT_PROTOCOL_NBAP", "RNC_EVENT_NBAP_Event_Protocol",
                "2013-12-03 09:00.00");
        assertEquals(1, rows);
    }

    @SuppressWarnings("unchecked")
    private void setUpExpectationsOnGWTComponents() {
        context.checking(new Expectations() {
            {
                allowing(displayTable).setText(with(any(int.class)),
                        with(any(int.class)), with(any(String.class)));
                allowing(displayTable).addStyleName(with(any(String.class)));
                allowing(displayTable).setWidget(with(any(int.class)),
                        with(any(int.class)), with(any(Label.class)));
                allowing(displayTable).getFlexCellFormatter();
                allowing(displayTable).getFlexCellFormatter().setColSpan(
                        with(any(int.class)), with(any(int.class)),
                        with(any(int.class)));
                allowing(displayTable).getFlexCellFormatter().setWidth(
                        with(any(int.class)), with(any(int.class)),
                        with(any(String.class)));
                allowing(displayTable).getFlexCellFormatter().setStyleName(
                        with(any(int.class)), with(any(int.class)),
                        with(any(String.class)));
                allowing(displayTable).getCellCount(with(any(int.class)));
                allowing(ue).setText(with(any(String.class)));
                allowing(ue).addStyleName(with(any(String.class)));
                allowing(eNb).setText(with(any(String.class)));
                allowing(eNb).addStyleName(with(any(String.class)));
                allowing(srnc).setText(with(any(String.class)));
                allowing(srnc).addStyleName(with(any(String.class)));
                allowing(drnc).setText(with(any(String.class)));
                allowing(drnc).addStyleName(with(any(String.class)));
                allowing(cn).setText(with(any(String.class)));
                allowing(cn).addStyleName(with(any(String.class)));
                allowing(timestamp).setText(with(any(String.class)));
                allowing(timestamp).addStyleName(with(any(String.class)));
                allowing(mockArrowLabel).addStyleName(with(any(String.class)));
                allowing(mockLabelWidget).addStyleName(with(any(String.class)));
                allowing(mockLabelWidget).addClickHandler(with(any(ClickHandler.class)));
                allowing(mockVerticalPanel).add(with(any(UerttDetailsMenuOption.class)));
                allowing(mockVerticalPanel).addStyleName(with(any(String.class)));
            }
        });
    }

}

class StubbedEventGridViewUertt extends EventGridViewUertt {

    protected Mockery context = new JUnit4Mockery();
    {
        context.setImposteriser(ClassImposteriser.INSTANCE);
    }

    FlexTable displayTable;
    private final Map<String, Label> nameToLabelMapping;
    Label mockLabel;
    UerttDetailsMenuOption mockOption;
    VerticalPanel mockVerticalPanel;
    HTML mockHtml;

    public StubbedEventGridViewUertt(final FlexTable displayTable,
                                     final Map<String, Label> nameToLabelMapping, final Label mockLabel, final UerttDetailsMenuOption mockOption, final VerticalPanel mockVerticalPanel, final HTML mockHtml) {
        this.displayTable = displayTable;
        this.nameToLabelMapping = nameToLabelMapping;
        this.mockLabel = mockLabel;
        this.mockOption = mockOption;
        this.mockVerticalPanel = mockVerticalPanel;
        this.mockHtml = mockHtml;
    }

    @Override
    protected FlexTable createFlexTable() {
        return displayTable;
    }

    @Override
    protected FlexTable createFlexTableHeader() {
        return displayTable;
    }

    @Override
    protected Label createLabel(final String labelText) {
        return nameToLabelMapping.get(labelText);
    }

    @Override
    protected Label createBlankLabel() {
        return mockLabel;
    }
    @Override
    protected UerttDetailsMenuOption createUerttDetailsMenuOption(int index, String labelText)
    {
        return mockOption;
    }
    @Override
    protected VerticalPanel createVerticalPanel()
    {
        return mockVerticalPanel;
    }

    @Override
    protected HTML createCellContent(String labelText)
    {
        return mockHtml;
    }

    @Override
    protected void setSeleniumTagsOnFlextables()
    {

    }

}