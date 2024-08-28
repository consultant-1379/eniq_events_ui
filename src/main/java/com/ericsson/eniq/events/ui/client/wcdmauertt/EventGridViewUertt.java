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
import com.ericsson.eniq.events.common.client.export.CSVBuilder;
import com.ericsson.eniq.events.common.client.export.CSVExportHelper;
import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.common.widget.EniqWindow;
import com.ericsson.eniq.events.ui.client.common.widget.IEventGridView;
import com.ericsson.eniq.events.ui.client.grid.JSONGrid;
import com.ericsson.eniq.events.ui.client.resources.EniqResourceBundle;
import com.ericsson.eniq.events.widgets.client.WidgetsResourceBundle;
import com.ericsson.eniq.events.widgets.client.WidgetsResourceBundleHelper;
import com.ericsson.eniq.events.widgets.client.window.toolbar.ToolbarItem;
import com.ericsson.eniq.events.widgets.client.window.toolbar.WindowToolbar;
import com.extjs.gxt.ui.client.util.Point;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.event.shared.EventBus;
import java.util.HashMap;
import java.util.Map;
import static com.ericsson.eniq.events.ui.client.wcdmauertt.WcdmaUerttProtocolUtilities.*;
import static com.ericsson.eniq.events.ui.client.wcdmauertt.WcdmaUerttProtocolUtilities.isRnsapReceived;

public class EventGridViewUertt extends BaseView<EventGridPresenterUertt>{
    private FlexTable displayTable;
    private FlexTable displayTableHeader;
    private Label ue;
    private Label eNb;
    private Label srnc;
    private Label drnc;
    private Label cn;
    private Label timestamp;
    private static final String USER_EQUIPMENT = "UE";
    private static final String eNodeb = "NodeB";
    private static final String SERVING_RNC = "SRNC";
    private static final String DRIFT_RNC = "DRNC";
    private static final String CORE_NETWORK = "CN";
    private static final String BLANK_CELL = " ";
    private static final String TIMESTAMP = "Timestamp";
    private static final String WIDTH_THIRTEEN_PERCENT = "13%";
    private static final String WIDTH_SIX_PERCENT = "6%";
    private static final String WIDTH_THREE_PERCENT = "3%";
    private static final String WIDTH_FIVE_PERCENT = "5%";
    private static final String WIDTH_TEN_PERCENT = "10%";
    private static final String WIDTH_FOURTEEN_PERCENT = "14%";
    private static final String WIDTH_ONE_PERCENT = "1%";
    private static final String WIDTH_TWO_PERCENT = "2%";
    private static final String RRC_PROTOCOL = "RRC: ";
    private static final String RANAP_PROTOCOL = "RANAP: ";
    private static final String RNSAP_PROTOCOL = "RNSAP: ";
    private static final String NBAP_PROTOCOL = "NBAP: ";
    private static final String windowTitle = "Event Details View";
    private UerttDetailsMenuOption option;
    private EventBus eventBus;
    private EventCacheUertt eventCacheUertt;
    private int activePage;
    private VerticalPanel optionPanel;
    private JSONGrid jsonGrid;
    private IEventGridView gridView;
    EniqWindow eniqWindow;
    private final String SELENIUM_DATA_TABLE  = "DATA TABLE";
    private final String SELENIUM_HEADER_TABLE = "HAEDER TABLE";
    private final String SELENIUM_DETAIL_WINDOW_HEADER = "DETAIL_WINDOW_HEADER" ;
    private final String SELENIUM_VIEW_DETAILS = "VIEW DETAILS";

    private static Point currentWindowPosition = new Point(50, 50);

    public void setEniqWindow(EniqWindow eniqWindow) {
        this.eniqWindow = eniqWindow;
    }

    private EventDetailsView view;
    private JSONValue responseValue;
    EventDetailsPresenter presenter;
    final static EventGridViewUerttResourceBundle resources;

    static {
        resources = GWT.create(EventGridViewUerttResourceBundle.class);
        resources.css().ensureInjected();
    }

    public EventGridViewUertt(IEventGridView gridView,JSONGrid jsonGrid) {
        this.jsonGrid = jsonGrid;
        this.gridView = gridView;
    }


    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventCacheUertt getEventCacheUertt() {
        return eventCacheUertt;
    }

    public EventGridViewUertt() {
    }

    // Extracted out to get this class testable

    protected UerttDetailsMenuOption createUerttDetailsMenuOption(int index, String labelName)
    {
        UerttDetailsMenuOption options = new UerttDetailsMenuOption(index, labelName);
        options.initMenuOption();
        options.addItem(uerttMenuItem.ViewDetails);
        options.getElement().setId(CommonConstants.SELENIUM_TAG+SELENIUM_VIEW_DETAILS);
        options.addSelectionHandler(new SelectionHandler<uerttMenuItem>() {
            private int index;
            @Override
            public void onSelection(SelectionEvent<uerttMenuItem> event) {
                final uerttMenuItem item = event.getSelectedItem();
                switch (item) {
                    case ViewDetails:
                        view = new EventDetailsView();
                        responseValue = eventCacheUertt.getResponseEventRawData();
                        presenter = new EventDetailsPresenter(view,eventBus,responseValue);
                        createUerttEventDetailsWindow(index);
                        break;
                }
            }
            public SelectionHandler<uerttMenuItem> setIndex(int index) {
                this.index = index;
                return this;
            }
        }.setIndex(options.getIndex()));
        return options;
    }

    private void createUerttEventDetailsWindow(int index)
    {
        EniqWindowUertt window = new EniqWindowUertt(eniqWindow.getContentPanel());
        eniqWindow.getContentPanel().add(window);
        eniqWindow.getContentPanel().layout();
        window.updateTitle(windowTitle);
        window.getHeader().getElement().setId(CommonConstants.SELENIUM_TAG+SELENIUM_DETAIL_WINDOW_HEADER);
        FlowPanel contentPanel = new FlowPanel();
        WindowToolbar toolbar = new WindowToolbar();
        toolbar.setStyleName(resources.css().toolbar());
        EniqResourceBundle bundle = GWT.create(EniqResourceBundle.class);
        toolbar.addItem(createExportButton(presenter,bundle), WindowToolbar.Direction.LEFT);
        contentPanel.add(toolbar.asWidget());
        VerticalPanel scrollPanelContainer = new VerticalPanel();
        VerticalPanel footerPanel = new VerticalPanel();
        footerPanel.setStyleName(resources.css().footer());
        scrollPanelContainer.add(view.createContent(presenter.go(getSelectedRowCount(index, activePage), eventCacheUertt)));
        contentPanel.add(scrollPanelContainer);
        contentPanel.add(footerPanel);
        window.add(contentPanel.asWidget());
        window.setSize(600, 410);
        window.show();
        window.fitIntoContainer();
        window.setEventDetailWindowPosition(window, eniqWindow.getContentPanel());
        window.setResizable(false);
    }

    protected VerticalPanel createVerticalPanel(){
        return new VerticalPanel();
    }


    protected Label createLabel(final String labelText) {
        return new Label(labelText);
    }

    protected Label createBlankLabel() {
        return new Label();
    }

    // Extracted out to get this class testable

    protected HTML createCellContent(String labelText)
    {
        return new HTML(labelText);
    }

    protected FlexTable createFlexTable() {
        return new FlexTable();
    }

    public FlexTable getDisplayTable() {
        return displayTable;
    }

    protected FlexTable createFlexTableHeader() {
        return new FlexTable();
    }

    public FlexTable getDisplayTableHeader() {
        return displayTableHeader;
    }

    private void createLabels() {
        ue = createLabel(USER_EQUIPMENT);
        eNb = createLabel(eNodeb);
        srnc = createLabel(SERVING_RNC);
        drnc = createLabel(DRIFT_RNC);
        cn = createLabel(CORE_NETWORK);
        timestamp = createLabel(TIMESTAMP);
    }

    private void setStyleOnLabels() {
        ue.addStyleName(resources.css().uerttLabelStyleName());
        srnc.addStyleName(resources.css().uerttLabelStyleName());
        eNb.addStyleName(resources.css().uerttLabelStyleName());
        drnc.addStyleName(resources.css().uerttLabelStyleName());
        cn.addStyleName(resources.css().uerttLabelStyleName());
        timestamp.addStyleName(resources.css().uerttLabelStyleName());
    }


    private void addLabelsToHeaderTable() {
        displayTableHeader.setWidget(0, 0, timestamp);
        displayTableHeader.setText(0, 1, BLANK_CELL);
        displayTableHeader.setWidget(0, 2, ue);
        displayTableHeader.setText(0, 3, BLANK_CELL);
        displayTableHeader.setWidget(0, 4, eNb);
        displayTableHeader.setText(0, 5, BLANK_CELL);
        displayTableHeader.setWidget(0, 6, srnc);
        displayTableHeader.setText(0, 7, BLANK_CELL);
        displayTableHeader.setWidget(0, 8, drnc);
        displayTableHeader.setText(0, 9, BLANK_CELL);
        displayTableHeader.setWidget(0, 10, cn);
        displayTableHeader.setText(0, 11, BLANK_CELL);
    }

    private void addLabelsToDisplayTable() {

        displayTable.addStyleName(resources.css().uerttGridStyle());
        displayTable.setText(0, 0, BLANK_CELL);
        displayTable.setText(0, 1, BLANK_CELL);
        displayTable.getFlexCellFormatter().setColSpan(0, 2, 2);
        displayTable.setText(0, 2, BLANK_CELL);
        displayTable.setText(0, 3, BLANK_CELL);
        displayTable.getFlexCellFormatter().setColSpan(0, 4, 2);
        displayTable.setText(0, 4, BLANK_CELL);
        displayTable.setText(0, 5, BLANK_CELL);
        displayTable.getFlexCellFormatter().setColSpan(0, 6, 2);
        displayTable.setText(0, 6, BLANK_CELL);
        displayTable.setText(0, 7, BLANK_CELL);
        displayTable.getFlexCellFormatter().setColSpan(0, 8, 2);
        displayTable.setText(0, 8, BLANK_CELL);
        displayTable.setText(0, 9, BLANK_CELL);
        displayTable.getFlexCellFormatter().setColSpan(0, 10, 2);
    }

    private void setWidthForHeaderRow() {
        displayTableHeader.getFlexCellFormatter().setWidth(0, 0, WIDTH_TEN_PERCENT);
        displayTableHeader.getFlexCellFormatter().setWidth(0, 1, WIDTH_TWO_PERCENT);
        displayTableHeader.getFlexCellFormatter().setWidth(0, 2, WIDTH_SIX_PERCENT);
        displayTableHeader.getFlexCellFormatter().setWidth(0, 3, WIDTH_THIRTEEN_PERCENT);
        displayTableHeader.getFlexCellFormatter().setWidth(0, 4, WIDTH_SIX_PERCENT);
        displayTableHeader.getFlexCellFormatter().setWidth(0, 5, WIDTH_FOURTEEN_PERCENT);
        displayTableHeader.getFlexCellFormatter().setWidth(0, 6, WIDTH_SIX_PERCENT);
        displayTableHeader.getFlexCellFormatter().setWidth(0, 7, WIDTH_FOURTEEN_PERCENT);
        displayTableHeader.getFlexCellFormatter().setWidth(0, 8, WIDTH_SIX_PERCENT);
        displayTableHeader.getFlexCellFormatter().setWidth(0, 9, WIDTH_THIRTEEN_PERCENT);
        displayTableHeader.getFlexCellFormatter().setWidth(0, 10, WIDTH_SIX_PERCENT);
        displayTableHeader.getFlexCellFormatter().setWidth(0, 11, WIDTH_ONE_PERCENT);
    }

    private void setWidthForFirstRowOfDisplayTable(){
        displayTable.getFlexCellFormatter().setWidth(0, 0, WIDTH_TEN_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(0, 1, WIDTH_TWO_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(0, 2, WIDTH_SIX_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(0, 3, WIDTH_THIRTEEN_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(0, 4, WIDTH_SIX_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(0, 5, WIDTH_FOURTEEN_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(0, 6, WIDTH_SIX_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(0, 7, WIDTH_FOURTEEN_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(0, 8, WIDTH_SIX_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(0, 9, WIDTH_THIRTEEN_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(0, 10, WIDTH_SIX_PERCENT);
    }

    public void init() {
        displayTable = createFlexTable();
        displayTableHeader = createFlexTableHeader();
        displayTableHeader.addStyleName(resources.css().uerttGridStyleHeader());
        createLabels();
        setStyleOnLabels();
        addLabelsToHeaderTable();
        setWidthForHeaderRow();
        addLabelsToDisplayTable();
        setWidthForFirstRowOfDisplayTable();
        setSeleniumTagsOnFlextables();
    }

    protected void setSeleniumTagsOnFlextables() {

        displayTable.getElement().setId(CommonConstants.SELENIUM_TAG+SELENIUM_DATA_TABLE);
        displayTableHeader.getElement().setId(CommonConstants.SELENIUM_TAG+SELENIUM_HEADER_TABLE);
    }

    private HTML formatTimestampText(String timestamp)
    {
        final String[] arrTimestamp = timestamp.split(" ", 2);
        String strTimestamp = "<html><body><p><b>" + arrTimestamp[1] + "</b></p><p>" + arrTimestamp[0] + "</p></body></html>";
        HTML cellContent = createCellContent(strTimestamp);
        return cellContent;
    }

    public String removeUnderScoresFromEventName(String eventName) {
        String eventNameModified = eventName;
        int countUnderScores = eventName.length() - eventNameModified.replaceAll("_", "").length();
        if (countUnderScores > 3) {
            eventNameModified = "";
            final String[] arrEventName = eventName.split("_", 4);
            eventNameModified += Character.toUpperCase(arrEventName[3].charAt(0));
            for (int i = 1; i < arrEventName[3].length(); i++) {
                if (arrEventName[3].charAt(i) == '_') {
                    eventNameModified += " " + Character.toUpperCase(arrEventName[3].charAt(i + 1));
                    i++;
                } else {
                    eventNameModified += Character.toLowerCase(arrEventName[3].charAt(i));
                }
            }
            return eventNameModified;
        }
        else if (countUnderScores == 2)
        {
            eventNameModified = "";
            final String[] arrEventName = eventName.split("_", 3);
            if(arrEventName[0].equals("INTERNAL"))
            {
                return eventName;
            }
            else
            {
                eventNameModified = arrEventName[2];
                return eventNameModified;
            }
        }
        else {
            return eventName;
        }
    }

    private void setWidthForEachColumn(final int rowCount) {

        displayTable.getFlexCellFormatter().setWidth(rowCount, 0, WIDTH_TEN_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(rowCount, 1, WIDTH_TWO_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(rowCount, 2, WIDTH_THREE_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(rowCount, 3, WIDTH_THREE_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(rowCount, 4, WIDTH_THIRTEEN_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(rowCount, 5, WIDTH_THREE_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(rowCount, 6, WIDTH_THREE_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(rowCount, 7, WIDTH_FOURTEEN_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(rowCount, 8, WIDTH_THREE_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(rowCount, 9, WIDTH_THREE_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(rowCount, 10, WIDTH_FOURTEEN_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(rowCount, 11, WIDTH_THREE_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(rowCount, 12, WIDTH_THREE_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(rowCount, 13, WIDTH_THIRTEEN_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(rowCount, 14, WIDTH_THREE_PERCENT);
        displayTable.getFlexCellFormatter().setWidth(rowCount, 15, WIDTH_THREE_PERCENT);
    }

    private void setTextForAllCellsToBlank(final int rowCount) {
        final int numberOfColumns = displayTable.getCellCount(rowCount);
        for (int x = 0; x < numberOfColumns; x++) {
            displayTable.setText(rowCount, x, BLANK_CELL);
        }
    }

    private Map<Integer, String> setStylesForBlankRow() {
        final Map<Integer, String> columnStyles = new HashMap<Integer, String>();

        columnStyles.put(0, resources.css().uerttCellStyle());
        columnStyles.put(1, resources.css().uerttCellStyle());
        columnStyles.put(2, resources.css().uerttCellStyleAndVline());
        columnStyles.put(3, resources.css().uerttCellStyle());
        columnStyles.put(4, resources.css().uerttCellStyle());
        columnStyles.put(5, resources.css().uerttCellStyleAndVline());
        columnStyles.put(6, resources.css().uerttCellStyle());
        columnStyles.put(7, resources.css().uerttCellStyle());
        columnStyles.put(8, resources.css().uerttCellStyleAndVline());
        columnStyles.put(9, resources.css().uerttCellStyle());
        columnStyles.put(10, resources.css().uerttCellStyle());
        columnStyles.put(11, resources.css().uerttCellStyleAndVline());
        columnStyles.put(12, resources.css().uerttCellStyle());
        columnStyles.put(13, resources.css().uerttCellStyle());
        columnStyles.put(14, resources.css().uerttCellStyleAndVline());
        columnStyles.put(15, resources.css().uerttCellStyle());
        return columnStyles;
    }

    public int addBlankRowAfterNodes(int rowCount) {
        setWidthForEachColumn(rowCount);
        setTextForAllCellsToBlank(rowCount);
        final Map<Integer, String> columnStyles = setStylesForBlankRow();
        for (final int key : columnStyles.keySet()) {
            displayTable.getFlexCellFormatter().addStyleName(rowCount, key, columnStyles.get(key));
        }
        return ++rowCount;
    }

    public int addEventWithDirectionInformaton(int rowCount, final String direction, final String protocol, final String eventName,
                                               final String timestamp) {
        if (isRRCSent(direction, protocol)) {
            rrcSentEventCreation(rowCount, timestamp, eventName);
        } else if (isRRCReceived(direction, protocol)) {
            rrcReceivedEventCreation(rowCount, timestamp, eventName);
        } else if (isRanapSent(direction, protocol)) {
            ranapSentEventCreation(rowCount, timestamp, eventName);
        } else if (isRanapReceived(direction, protocol)) {
            ranapReceivedEventCreation(rowCount, timestamp, eventName);
        } else if (isNBapSent(direction, protocol)) {
            nbapSentEventCreation(rowCount, timestamp, eventName);
        } else if (isNbapReceived(direction, protocol)) {
            nbapReceivedEventCreation(rowCount, timestamp, eventName);
        } else if (isRnsapSent(direction, protocol)) {
            rnsapSentEventCreation(rowCount, timestamp, eventName);
        } else if (isRnsapReceived(direction, protocol)) {
            rnsapReceivedEventCreation(rowCount, timestamp, eventName);
        } else {
            return rowCount;
        }
        return ++rowCount;
    }

    private void setDisplayText(final int row, final String timestamp, final String eventName, final int eventColumn) {

        String displayText = BLANK_CELL;
        for (int x = 0; x < 16; x++) {
            if (x == 0)
            {
                displayTable.setWidget(row, x, formatTimestampText(timestamp));
            }
            else if (x == eventColumn) {
                displayText = eventName;
                option = createUerttDetailsMenuOption(row, displayText);
                optionPanel = createVerticalPanel();
                optionPanel.add(option);
                optionPanel.addStyleName(resources.css().arrowSelectionCss());
                displayTable.setWidget(row,x,optionPanel);
            }
            else
            {
                displayText = BLANK_CELL;
                displayTable.setText(row, x, displayText);
            }
        }
    }
    public int getSelectedRowCount(int index, int activePage){
        if(index<3){
            index = 0;
        }
        else{
            index = ((index-2)/3);
        }
        return ((activePage-1)*50)+(index);
    }
    public void rrcSentEventCreation(final int rowCount, final String timestamp, final String eventName) {
        setWidthForEachColumn(rowCount);

        final String event_Name = RRC_PROTOCOL + removeUnderScoresFromEventName(eventName);
        final int eventColumn = 4;

        setDisplayText(rowCount, timestamp, event_Name, eventColumn);

        final Label arrowl_rrc = createBlankLabel();
        arrowl_rrc.addStyleName(resources.css().arrcAndArrowLeft());
        displayTable.setWidget(rowCount, 3, arrowl_rrc);

        final Map<Integer, String> columnStyles = getColumnStyleForRRCSent();
        for (final int key : columnStyles.keySet()) {
            displayTable.getFlexCellFormatter().addStyleName(rowCount, key, columnStyles.get(key));
        }
    }

    private Map<Integer, String> getColumnStyleForRRCSent() {
        final Map<Integer, String> columnStyles = new HashMap<Integer, String>();

        columnStyles.put(0, resources.css().timeStampTextStyle());
        columnStyles.put(4, resources.css().messageRRCStyle());
        columnStyles.put(5, resources.css().messageRRCStyle());
        columnStyles.put(6, resources.css().messageRRCStyle());
        columnStyles.put(7, resources.css().messageRRCStyle());
        columnStyles.put(8, resources.css().messageRRCStyleAndVline());
        columnStyles.put(2, resources.css().vline());
        columnStyles.put(11, resources.css().vline());
        columnStyles.put(14, resources.css().vline());

        return columnStyles;
    }

    public void rrcReceivedEventCreation(final int rowCount, final String timestamp, final String eventName) {
        setWidthForEachColumn(rowCount);

        final String event_Name = RRC_PROTOCOL + removeUnderScoresFromEventName(eventName);
        final int eventColumn = 4;
        setDisplayText(rowCount, timestamp, event_Name, eventColumn);

        final Label arrowr_rrc = createBlankLabel();
        arrowr_rrc.addStyleName(resources.css().arrcAndArrowRight());
        displayTable.setWidget(rowCount, 8, arrowr_rrc);

        final Map<Integer, String> columnStyles = getColumnStylesForRRCReceived();
        for (final int key : columnStyles.keySet()) {
            displayTable.getFlexCellFormatter().addStyleName(rowCount, key, columnStyles.get(key));
        }
    }

    private Map<Integer, String> getColumnStylesForRRCReceived() {
        final Map<Integer, String> columnStyles = new HashMap<Integer, String>();

        columnStyles.put(0, resources.css().timeStampTextStyle());
        columnStyles.put(3, resources.css().messageRRCStyle());
        columnStyles.put(4, resources.css().messageRRCStyle());
        columnStyles.put(5, resources.css().messageRRCStyle());
        columnStyles.put(6, resources.css().messageRRCStyle());
        columnStyles.put(7, resources.css().messageRRCStyle());
        columnStyles.put(2, resources.css().vline());
        columnStyles.put(8, resources.css().vline());
        columnStyles.put(11, resources.css().vline());
        columnStyles.put(14, resources.css().vline());

        return columnStyles;
    }

    public void ranapSentEventCreation(final int rowCount, final String timestamp, final String eventName) {
        setWidthForEachColumn(rowCount);

        final String event_Name = RANAP_PROTOCOL + removeUnderScoresFromEventName(eventName);
        final int eventColumn = 10;
        setDisplayText(rowCount, timestamp, event_Name, eventColumn);

        final Label arrowl_ranap = createBlankLabel();
        arrowl_ranap.addStyleName(resources.css().aranapAndArrowLeft());
        displayTable.setWidget(rowCount, 9, arrowl_ranap);

        final Map<Integer, String> columnStyles = getColumnStylesForRANAPSent();
        for (final int key : columnStyles.keySet()) {
            displayTable.getFlexCellFormatter().addStyleName(rowCount, key, columnStyles.get(key));
        }
    }

    private Map<Integer, String> getColumnStylesForRANAPSent() {
        final Map<Integer, String> columnStyles = new HashMap<Integer, String>();

        columnStyles.put(0, resources.css().timeStampTextStyle());
        columnStyles.put(10, resources.css().messageRANAPStyle());
        columnStyles.put(11, resources.css().messageRANAPStyle());
        columnStyles.put(12, resources.css().messageRANAPStyle());
        columnStyles.put(13, resources.css().messageRANAPStyle());
        columnStyles.put(14, resources.css().messageRANAPStyleAndVline());
        columnStyles.put(2, resources.css().vline());
        columnStyles.put(5, resources.css().vline());
        columnStyles.put(8, resources.css().vline());

        return columnStyles;
    }

    public void ranapReceivedEventCreation(final int rowCount, final String timestamp, final String eventName) {
        setWidthForEachColumn(rowCount);

        final String event_Name = RANAP_PROTOCOL + removeUnderScoresFromEventName(eventName);
        final int eventColumn = 10;
        setDisplayText(rowCount, timestamp, event_Name, eventColumn);

        final Label arrowr_ranap = createBlankLabel();
        arrowr_ranap.addStyleName(resources.css().aranapAndArrowRight());
        displayTable.setWidget(rowCount, 14, arrowr_ranap);

        final Map<Integer, String> columnStyles = getColumnStylesForRANAPReceived();
        for (final int key : columnStyles.keySet()) {
            displayTable.getFlexCellFormatter().addStyleName(rowCount, key, columnStyles.get(key));
        }
    }

    private Map<Integer, String> getColumnStylesForRANAPReceived() {
        final Map<Integer, String> columnStyles = new HashMap<Integer, String>();

        columnStyles.put(0, resources.css().timeStampTextStyle());
        columnStyles.put(9, resources.css().messageRANAPStyle());
        columnStyles.put(10, resources.css().messageRANAPStyle());
        columnStyles.put(11, resources.css().messageRANAPStyle());
        columnStyles.put(12, resources.css().messageRANAPStyle());
        columnStyles.put(13, resources.css().messageRANAPStyle());
        columnStyles.put(2, resources.css().vline());
        columnStyles.put(5, resources.css().vline());
        columnStyles.put(8, resources.css().vline());
        columnStyles.put(14, resources.css().vline());

        return columnStyles;
    }

    public void rnsapSentEventCreation(final int rowCount, final String timestamp, final String eventName) {
        setWidthForEachColumn(rowCount);

        final String event_Name = RNSAP_PROTOCOL + removeUnderScoresFromEventName(eventName);
        final int eventColumn = 10;
        setDisplayText(rowCount, timestamp, event_Name, eventColumn);

        final Label arrowl_rnsap = createBlankLabel();
        arrowl_rnsap.addStyleName(resources.css().arnsapAndArrowLeft());
        displayTable.setWidget(rowCount, 9, arrowl_rnsap);

        final Map<Integer, String> columnStyles = getColumnStylesForRNSAPSent();
        for (final int key : columnStyles.keySet()) {
            displayTable.getFlexCellFormatter().addStyleName(rowCount, key, columnStyles.get(key));
        }
    }

    private Map<Integer, String> getColumnStylesForRNSAPSent() {
        final Map<Integer, String> columnStyles = new HashMap<Integer, String>();

        columnStyles.put(0, resources.css().timeStampTextStyle());
        columnStyles.put(10, resources.css().messageRNSAPStyle());
        columnStyles.put(11, resources.css().messageRNSAPStyleAndVline());
        columnStyles.put(2, resources.css().vline());
        columnStyles.put(5, resources.css().vline());
        columnStyles.put(8, resources.css().vline());
        columnStyles.put(14, resources.css().vline());

        return columnStyles;
    }

    public void rnsapReceivedEventCreation(final int rowCount, final String timestamp, final String eventName) {
        setWidthForEachColumn(rowCount);

        final String event_Name = RNSAP_PROTOCOL + removeUnderScoresFromEventName(eventName);
        final int eventColumn = 10;
        setDisplayText(rowCount, timestamp, event_Name, eventColumn);

        final Label arrowr_rnsap = createBlankLabel();
        arrowr_rnsap.addStyleName(resources.css().arnsapAndArrowRight());
        displayTable.setWidget(rowCount, 11, arrowr_rnsap);

        final Map<Integer, String> columnStyles = getColumnStylesForRNSAPReceived();
        for (final int key : columnStyles.keySet()) {
            displayTable.getFlexCellFormatter().addStyleName(rowCount, key, columnStyles.get(key));
        }
    }

    private Map<Integer, String> getColumnStylesForRNSAPReceived() {
        final Map<Integer, String> columnStyles = new HashMap<Integer, String>();

        columnStyles.put(0, resources.css().timeStampTextStyle());
        columnStyles.put(9, resources.css().messageRNSAPStyle());
        columnStyles.put(10, resources.css().messageRNSAPStyle());
        columnStyles.put(2, resources.css().vline());
        columnStyles.put(5, resources.css().vline());
        columnStyles.put(8, resources.css().vline());
        columnStyles.put(11, resources.css().vline());
        columnStyles.put(14, resources.css().vline());

        return columnStyles;
    }

    public void nbapSentEventCreation(final int rowCount, final String timestamp, final String eventName) {
        setWidthForEachColumn(rowCount);

        final String event_Name = NBAP_PROTOCOL + removeUnderScoresFromEventName(eventName);
        final int eventColumn = 7;
        setDisplayText(rowCount, timestamp, event_Name, eventColumn);

        final Label arrowl_nbap = createBlankLabel();
        arrowl_nbap.addStyleName(resources.css().anbapAndArrowLeft());
        displayTable.setWidget(rowCount, 6, arrowl_nbap);

        final Map<Integer, String> columnStyles = getColumnStylesForNBAPSent();
        for (final int key : columnStyles.keySet()) {
            displayTable.getFlexCellFormatter().addStyleName(rowCount, key, columnStyles.get(key));
        }
    }

    private Map<Integer, String> getColumnStylesForNBAPSent() {
        final Map<Integer, String> columnStyles = new HashMap<Integer, String>();

        columnStyles.put(0, resources.css().timeStampTextStyle());
        columnStyles.put(7, resources.css().messageNBAPStyle());
        columnStyles.put(8, resources.css().messageNBAPStyleAndVline());
        columnStyles.put(2, resources.css().vline());
        columnStyles.put(5, resources.css().vline());
        columnStyles.put(11, resources.css().vline());
        columnStyles.put(14, resources.css().vline());

        return columnStyles;
    }

    public void nbapReceivedEventCreation(final int rowCount, final String timestamp, final String eventName) {
        setWidthForEachColumn(rowCount);

        final String event_Name = NBAP_PROTOCOL + removeUnderScoresFromEventName(eventName);
        final int eventColumn = 7;
        setDisplayText(rowCount, timestamp, event_Name, eventColumn);

        final Label arrowr_nbap = createBlankLabel();
        arrowr_nbap.addStyleName(resources.css().anbapAndArrowRight());
        displayTable.setWidget(rowCount, 8, arrowr_nbap);

        final Map<Integer, String> columnStyles = getColumnStylesForNBAPReceived();
        for (final int key : columnStyles.keySet()) {
            displayTable.getFlexCellFormatter().addStyleName(rowCount, key, columnStyles.get(key));
        }
    }

    private Map<Integer, String> getColumnStylesForNBAPReceived() {
        final Map<Integer, String> columnStyles = new HashMap<Integer, String>();

        columnStyles.put(0, resources.css().timeStampTextStyle());
        columnStyles.put(6, resources.css().messageNBAPStyle());
        columnStyles.put(7, resources.css().messageNBAPStyle());
        columnStyles.put(2, resources.css().vline());
        columnStyles.put(5, resources.css().vline());
        columnStyles.put(8, resources.css().vline());
        columnStyles.put(11, resources.css().vline());
        columnStyles.put(14, resources.css().vline());

        return columnStyles;
    }


    public void setActivePage(int activePage) {
        this.activePage = activePage;
    }

    public JSONGrid getJsonGrid() {
        return jsonGrid;
    }

    public IEventGridView getGridView() {
        return gridView;
    }

    public void setEventCacheUertt(EventCacheUertt eventCacheUertt) {
        this.eventCacheUertt = eventCacheUertt;
    }
    private ToolbarItem createExportButton(final EventDetailsPresenter presenter,EniqResourceBundle bundle) {
        final ToolbarItem item = new ToolbarItem("exportButton", new ClickHandler() {

            @Override
            public void onClick(final ClickEvent event) {
                CSVBuilder csvBuilder = presenter.getDataAsCSV();
                CSVExportHelper.exportAsCSV(csvBuilder);
            }
        }, bundle.exportToIcon());
        item.setHoverImage(bundle.exportToIconHover());
        item.setDisabledImage(bundle.exportToIconDisabled());
        item.setToolTip("Export");
        return item;
    }
}
