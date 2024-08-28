/*
 * -----------------------------------------------------------------------
 *      Copyright (C) ${YEAR} LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.dashboard;

import static com.ericsson.eniq.events.common.client.CommonConstants.*;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;
import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.HasRowIndex;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.MapPortletWindow;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.PlaceHolder;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.PortletWindow;
import com.ericsson.eniq.events.ui.client.dashboard.threshold.ThresholdsPresenter;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.DashBoardDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletType;
import com.ericsson.eniq.events.widgets.client.window.title.TitleWindowResourceBundle;
import com.ericsson.eniq.events.widgets.client.window.title.TitleWindowResourceBundleHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Dashboard view is a container for all portlets.
 *
 * @author evyagrz
 * @since 10 2011
 */
public class DashboardView extends BaseView<DashboardPresenter> implements ClickHandler {

    private static DashboardViewUiBinder uiBinder = GWT.create(DashboardViewUiBinder.class);

    interface DashboardViewUiBinder extends UiBinder<Widget, DashboardView> {
    }

    @UiField
    AbsolutePanel dashboardContainer;

    @UiField
    FlowPanel portletPanel;

    @UiField
    HorizontalPanel titlePanel;

    @UiField
    SimplePanel mapPanel;

    public final static int MAX_PORTAL_COLUMNS = 3;

    private final TitleWindowResourceBundle resourceBundle = TitleWindowResourceBundleHelper.getBundle();

    private final EventBus eventBus;

    private final List<VerticalPanel> columnList = new ArrayList<VerticalPanel>();

    private PickupDragController widgetDragController;

    private final ThresholdsPresenter thresholdsPresenter;

    private String baseDashBoardTitle;

    private Label titleLabel;

    @Inject
    public DashboardView(final EventBus eventBus, final ThresholdsPresenter thresholdsPresenter) {
        this.eventBus = eventBus;
        this.thresholdsPresenter = thresholdsPresenter;

        initWidget(uiBinder.createAndBindUi(this));
    }

    /**
     * Method is called by presenter when dashBoardData is loaded from MetaReader.
     * Creates dashboard layout for portlets, add all available portlets from meta data.
     *
     * @param dashBoardData
     */
    public void update(final DashBoardDataType dashBoardData) {

        this.baseDashBoardTitle = dashBoardData.getTitle();
        portletPanel.clear();
        mapPanel.clear();
        portletPanel.add(createTitleWidget());
        portletPanel.add(buildDashboardLayout());
    }

    public List<VerticalPanel> getColumnList() {
        return columnList;
    }

    public AbsolutePanel getDashboardContainer() {
        return dashboardContainer;
    }

    public FlowPanel getPortletPanel() {
        return portletPanel;
    }

    public SimplePanel getMapPanel() {
        return mapPanel;
    }

    private PickupDragController getDragController(final EventBus eventBus, final AbsolutePanel boundaryPanel) {
        final PortletDragHandler portletDragHandler = new PortletDragHandler(eventBus);
        final PickupDragController widgetDragController = new PickupDragController(boundaryPanel, false);

        widgetDragController.setBehaviorMultipleSelection(false);
        widgetDragController.addDragHandler(portletDragHandler);

        return widgetDragController;
    }

    private Panel buildDashboardLayout() {
        final AbsolutePanel boundaryPanel = new AbsolutePanel();
        boundaryPanel.setSize("100%", "100%");

        final HorizontalPanel columnPanel = new HorizontalPanel();
        columnPanel.addStyleName("portlets-column-holder");
        widgetDragController = getDragController(eventBus, boundaryPanel);

        destroyAllPortletWindows();
        columnList.clear();

        for (int col = 0; col < MAX_PORTAL_COLUMNS; col++) {
            final VerticalPanel column = new VerticalPanel(); // NOPMD (ok instansiate in loop)
            column.setStyleName("portlets-column");
            columnPanel.add(column);
            columnList.add(column);

            final VerticalPanelDropController widgetDropController = new VerticalPanelDropController(column); // NOPMD
            widgetDragController.registerDropController(widgetDropController);
        }
        final SimplePanel panel = new SimplePanel();
        panel.setStyleName("portlets-panel");
        panel.setWidget(columnPanel);

        boundaryPanel.add(panel);
        return boundaryPanel;
    }

    private Widget createTitleWidget() {
        final Image arrow = new Image(resourceBundle.arrowForActionMenu());
        arrow.getElement().getStyle().setPropertyPx("marginLeft", 10);
        arrow.getElement().getStyle().setProperty("cursor", "pointer");
        titlePanel.clear();
        titlePanel.add(arrow);

        titleLabel = new Label(baseDashBoardTitle);
        titleLabel.getElement().getStyle().setPropertyPx("marginLeft", 5);
        titlePanel.add(titleLabel);

        arrow.addClickHandler(this);
        return titlePanel;
    }

    /**
     * Update time on dashboard title, This time represents the latest day worth of data being
     * used (it is not the times used in each porlet). It requires that initial time passed to
     * DashboardPresenter is the day interval (the PorletWindows themselves will adhust for there own times)
     *
     * @param timeData current time data (prior to individual portlets changing timeFrom,
     *                 i.e assuming one day or as per DashboardTimeComponent
     */
    public void updateTitleBarText(TimeInfoDataType timeData) {
        if (titleLabel != null) {
            timeData = checkTimeData(timeData);
            // Consistent time string as rest of UI (if not liked change the #timeData.toString)
            titleLabel.setText(baseDashBoardTitle + SINGLE_SPACE + timeData);
        }
    }

    /**
     * Checks Time Data for one day difference between date to and date from
     */
    private TimeInfoDataType checkTimeData(final TimeInfoDataType timeData) {
        final long msDiff = timeData.dateTo.getTime() - timeData.dateFrom.getTime();
        if (msDiff > DAY_IN_MILLISEC) {
            timeData.dateFrom = new Date(timeData.dateTo.getTime() - DAY_IN_MILLISEC);
        }
        return timeData;
    }

    @Override
    public void onClick(final ClickEvent event) {
        getPresenter().openDashboardMenu();
    }

    public PlaceHolder createPlaceHolder(final PortletDataType portletData) {
        final String portletId = portletData.getPortletId();
        final int portletHeight = portletData.getPortletHeight();
        final int rowIndex = portletData.getRowIndex();
        return new PlaceHolder(portletId, portletHeight, rowIndex, eventBus);
    }

    public PlaceHolder createPlaceHolder(final String portletId, final int portletHeight) {
        return new PlaceHolder(portletId, portletHeight, -1, eventBus);
    }

    public void addPortlet(final PortletDataType portletInfo, final SearchFieldDataType searchData,
            final TimeInfoDataType timeData, final PortletTemplate portletTemplate) {
        String portletId = portletInfo.getPortletId();

        PortletWindow portletWindow = getPresenter().getPortletById(portletId);
        if (portletWindow == null) {
            portletWindow = new PortletWindow(portletInfo, eventBus, thresholdsPresenter);
        }

        widgetDragController.makeDraggable(portletWindow, portletWindow.getDragablePart());

        final int columnIndex = portletInfo.getColumnIndex();
        final int rowIndex = portletInfo.getRowIndex();
        final PortletType type = portletInfo.getType();

        final VerticalPanel columnPanel = columnList.get(columnIndex);
        if (!PortletType.MAP.equals(type)) {
            columnPanel.insert(portletWindow, computeRowIndex(columnPanel, rowIndex));
            portletTemplate.init(portletInfo);
            portletWindow.setBody(portletTemplate.asWidget());
            portletWindow.mask(); // refresh mask not working on add (not attached)
        } else {
            // Map should be under all portlets in a separate panel
            if (mapPanel.getWidget() != null) {
                mapPanel.clear();
            }
            final MapPortletWindow mapPortletWindow = new MapPortletWindow(portletInfo, eventBus);
            portletTemplate.init(portletInfo);
            mapPortletWindow.setBody(portletTemplate.asWidget());
            mapPanel.add(mapPortletWindow);

        }

        // none search field porlets would need to update for time
        // can not use anything cached for time or seach data as menu taskbar may have updated

        if (portletInfo.isSearchFieldUser()) {
            portletWindow.refresh(searchData, timeData);
        } else {
            portletWindow.refreshTimeData(timeData);
        }
    }

    public PortletWindow getPortletWindow(PortletDataType portletInfo) {
        PortletWindow portletWindow = null;
        final int columnIndex = portletInfo.getColumnIndex();
        final int rowIndex = portletInfo.getRowIndex();

        final VerticalPanel portletColumn = columnList.get(columnIndex);

        // We can get exception if we try to get portlet when it's not yet inside dom model...
        try {
            final Widget widget = portletColumn.getWidget(rowIndex);
            if (widget instanceof PortletWindow) {
                portletWindow = (PortletWindow) widget;
            }
        } catch (final IndexOutOfBoundsException e) {
            portletWindow = null;
        }
        if (portletWindow != null && !portletWindow.getPortletId().equals(portletInfo.getPortletId())) {
            portletWindow = null;
        }

        return portletWindow;
    }

    public void destroyAllPortletWindows() {
        for (PortletWindow window : getAllPortletWindows()) {
            window.destroy();
        }
    }

    public void markEmptyColumn() {
        for (final VerticalPanel column : columnList) {
            if (column.getWidgetCount() == 0) {
                column.addStyleName("emptyTable");
            }
        }
    }

    private ArrayList<PortletWindow> getAllPortletWindows() {
        ArrayList<PortletWindow> portletWindows = new ArrayList<PortletWindow>();

        for (VerticalPanel portletColumn : columnList) {
            for (Object widget : portletColumn) {
                if (widget instanceof PortletWindow) {
                    portletWindows.add((PortletWindow) widget);
                }
            }
        }
        return portletWindows;
    }

    private int computeRowIndex(final VerticalPanel columnPanel, final int rowIndex) {
        int index = 0;
        for (; index < columnPanel.getWidgetCount(); index++) {
            final HasRowIndex hasRowIndex = (HasRowIndex) columnPanel.getWidget(index);
            if (rowIndex < hasRowIndex.getRowIndex()) {
                return index;
            }
        }
        return index;
    }
}