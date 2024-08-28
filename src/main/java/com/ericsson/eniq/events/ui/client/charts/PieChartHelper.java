/**
 /*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.charts;
import com.ericsson.eniq.events.common.client.datatype.ChartDrillDownInfoDataType;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.ToolBarStateManager;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.common.widget.AbstractBaseWindowDisplay;
import com.ericsson.eniq.events.ui.client.datatype.*;
import com.ericsson.eniq.events.ui.client.events.GraphDrillDownLaunchEvent;
import com.ericsson.eniq.events.ui.client.events.GraphDrillDownLaunchEventHandler;
import com.ericsson.eniq.events.ui.client.main.AbstractWindowLauncher;
import com.ericsson.eniq.events.ui.client.main.ChartLauncher;
import com.ericsson.eniq.events.ui.client.main.GridLauncher;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.web.bindery.event.shared.EventBus;

import java.util.logging.Logger;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.PIE_CHART_DISPLAY;

/**
 * Handle hard-coded pie chart only with drill down to sub pie and then to grid
 * e.g. Cause Code, SubCause Code and Grid
 *
 * @since Nov 2011
 */
public class PieChartHelper implements GraphDrillDownLaunchEventHandler {

    private final Logger LOGGER = Logger.getLogger(PieChartHelper.class.getName());

    private final String tabId;

    private final EventBus eventBus;

    private final ContentPanel centerPanel;

    private final IWorkspaceController controller;

    private final IMetaReader metaReader = MainEntryPoint.getInjector().getMetaReader();

    public PieChartHelper(final IWorkspaceController controller, final EventBus eventBus) {
        this.tabId = controller.getTabOwnerId();
        this.centerPanel = controller.getCenterPanel();
        this.controller = controller;
        this.eventBus = eventBus;
    }

    private String getTabOwnerId() {
        return tabId;
    }

    private ContentPanel getCenterPanel() {
        return centerPanel;
    }

    @Override
    public void handleLaunchFromGraphDrillDown(final GraphDrillDownLaunchEvent event) {

        // guard preventing multiple window launch in tabs
        if (!getTabOwnerId().equalsIgnoreCase(event.getTabOwnerId())) {
            return;
        }

        final boolean launchChart = event.getChartDrillDownInfo().getDrillType().equalsIgnoreCase(LAUNCH_CHART);

        if (launchChart) {
            LOGGER.finer("Launching pie chart");
            handleLaunchingChart(event.getChartDrillDownInfo(), event.getSearchData(), event.getChartElementSelected(),
                    event.getParentStyle(), event.getParentTime(), event.getQueryParameters(),
                    event.getSearchFieldUser());
        } else {
            LOGGER.finer("Launching grid");
            handleLaunchingGrid(event.getChartDrillDownInfo(), event.getSearchData(), event.getParentStyle(),
                    event.getParentTime(), event.getQueryParameters(), event.getChartElementSelected(),
                    event.getSearchFieldUser()); // putting chartElementSelected on title bar
        }

    }

    private void handleLaunchingChart(final ChartDrillDownInfoDataType info, final SearchFieldDataType searchData,
            final String chartElementSelected, final String parentStyle, final TimeInfoDataType parentTime,
            final String queryParameters, final SearchFieldUser searchFieldUser) {

        // adding a URL parameter to chartDrillDownWindows in meta data
        final String sURL = info.getWsURL();
        final StringBuilder parameterStr = new StringBuilder();

        //append time parameters (if any) and parameters passed from Graph
        parameterStr.append(parentTime == null ? EMPTY_STRING : parentTime.getQueryString(false));
        parameterStr.append(queryParameters);

        final ChartLauncher launcher = (ChartLauncher) getPieChartLauncher(info, parentStyle, sURL, parameterStr,
                searchFieldUser, chartElementSelected);

        if (searchFieldUser == SearchFieldUser.FALSE) {

            try {
                launcher.launchWindowFromChart(parentTime, false, EMPTY_STRING, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            final PresetResponseDisplayDataType presetInfo = handleExistingSubWindow(info.getChartDrillWinTypeID(),parentTime);
            parameterStr.append(searchData.getSearchFieldURLParams(true));
            launcher.launchWindowWithPresetSearchData(searchData, parentTime, false, chartElementSelected);
            launcher.setPresetResponseDisplayData(presetInfo); // (can be null) for position  to appear in same spot
            launcher.updateChartTitle(chartElementSelected); // title inside chart (not title bar)
        }
    }

    /*
     * Never bring to front - remove (due to replace) existing window.
     * New window even for same node in single mode
     * and multiple mode as could have clicked on a different pie slice - requiring a new server call
     * 
     * e.g sub chart :need a fresh call when drill on another pie slice, but keep using existing window (in all modes)
     * sub drill on grid:
     */
    private PresetResponseDisplayDataType handleExistingSubWindow(final String singleModeWinId,
            final TimeInfoDataType parentTime) {

        final BaseWindow existingWin;

        existingWin = controller.getWindow(singleModeWinId);
        PresetResponseDisplayDataType presetInfo = null;

        if (existingWin != null) {
            if (existingWin instanceof AbstractBaseWindowDisplay) {
                presetInfo = ((AbstractBaseWindowDisplay) existingWin).getPresenter().getPresetResponseDisplayData();
                presetInfo.resetTimeSelectionData(parentTime); // keep position but may have to update the time
                presetInfo.responseObj = null; // just want it for position (always want new calls
            }

            existingWin.hide(); // only one of these at a time
        }
        return presetInfo;
    }

    /* this should support multiple mode too */
    private void handleLaunchingGrid(final ChartDrillDownInfoDataType info, final SearchFieldDataType searchData,
            final String parentStyle, final TimeInfoDataType parentTime, final String queryParameters,
            final String chartElementSelected, final SearchFieldUser searchFieldUser) {

        final String buildGridID;

        if (searchFieldUser == SearchFieldUser.FALSE) {
            buildGridID = info.getDrillTargetDisplayId();
        } else {
              buildGridID = info.getDrillTargetDisplayId() + UNDERSCORE + searchData.getType()
                    + (searchData.isGroupMode() ? UNDER_SCORE_GROUP : "") + IS_DRILL_IDENTIFIER;
        }

        /* for single mode making minimized buttons node independant, because won't be able to remove or
           find existing windows following search field change (*/

        final String sURL = info.getWsURL();
        final StringBuilder parameterStr = new StringBuilder();
        parameterStr.append(queryParameters);
        parameterStr.append(info.getWidgetSpecificParams()); // e.g @key=ERR if applicable (not for GSM)

        final AbstractWindowLauncher launcher = getGridLauncher(info, parentStyle, buildGridID, sURL, parameterStr,
                chartElementSelected, searchFieldUser);

        if (searchFieldUser == SearchFieldUser.FALSE) {
            // TODO: consider to pass WindowsState as the last parameter (it is especially required if it was drilled
            // down from another window)
//            launcher.launchWindow(parentTime, true, chartElementSelected);
            launcher.launchWindowFromChart(parentTime, true, chartElementSelected, null);

        } else {
            final PresetResponseDisplayDataType presetInfo = handleExistingSubWindow(buildGridID, parentTime);
            launcher.setPresetResponseDisplayData(presetInfo);
            launcher.launchWindowWithPresetSearchData(searchData, parentTime, false, chartElementSelected);
        }
    }

    private AbstractWindowLauncher getGridLauncher(final ChartDrillDownInfoDataType info, final String parentStyle,
            final String buildGridID, final String sURL, final StringBuilder parameterStr,
            final String chartElementSelected, final SearchFieldUser searchFieldUser) {

        final ToolBarStateManager toolbarHandler = new ToolBarStateManager(info.getDrillToolbarType(),
                ToolBarStateManager.BottomToolbarType.PAGING, EMPTY_STRING);

        final StringBuilder minimizedButtonName = new StringBuilder(info.getNameForTaskBar());
        minimizedButtonName.append(" (").append(chartElementSelected).append(")");

        final MetaMenuItemDataType gridMeta = new MetaMenuItemDataType.Builder().text(info.getDrillTargetDisplayId())
                .id(buildGridID).minimizedButtonName(minimizedButtonName.toString()).style(parentStyle)
                .url(metaReader.getCompletedURL(sURL)).windowType(MetaMenuItemDataType.Type.GRID)
                .widgetSpecificParams(parameterStr.toString()).toolBarHandler(toolbarHandler)
                .isSearchFieldUser(searchFieldUser).build();

        final MetaMenuItem generatedMetaMenuItem = new MetaMenuItem(gridMeta);

        if (searchFieldUser == SearchFieldUser.FALSE) {
            final BaseWindow window = controller.getWindow(generatedMetaMenuItem.getID());
            if (window != null) {
                window.hide();
            }
        }
        return new GridLauncher(generatedMetaMenuItem, eventBus, getCenterPanel(), controller);
    }

    private AbstractWindowLauncher getPieChartLauncher(final ChartDrillDownInfoDataType info, final String parentStyle,
            final String sURL, final StringBuilder parameterStr, final SearchFieldUser searchFieldUser,
            final String chartElementSelected) {

        final StringBuilder minimizedButtonName = new StringBuilder();
        if (searchFieldUser == SearchFieldUser.FALSE) {
            minimizedButtonName.append(info.getNameForTaskBar());
            minimizedButtonName.append(" (").append(chartElementSelected).append(")");
        } else {
            minimizedButtonName.append(info.getNameForTaskBar());
        }

        /* e.g. grid id is "sub cause code analysis",  search field user to have node in title bar of sub pie 
         * Disables time when launching chart  
         */
        final String drillDownWindowName = info.getDrillDownWindowName();

        /** Id of Drill down target will get either a specific grid id from the GridId param or else just use the same id
         * as the DrillDownType Id i.e. where a chart will only drill down to one target.
         */
        final String drillDownTargetId = info.getDrillTargetDisplayId().isEmpty() ? info.getChartDrillWinTypeID()
                : info.getDrillTargetDisplayId();

        final MetaMenuItemDataType chartMetaItem = new MetaMenuItemDataType.Builder()
                .text(drillDownWindowName)
                .id(drillDownTargetId)
                .url(metaReader.getCompletedURL(sURL))
                .style(parentStyle)
                .isSearchFieldUser(searchFieldUser)
                .windowType(MetaMenuItemDataType.Type.CHART)
                .display(PIE_CHART_DISPLAY)
                .widgetSpecificParams(parameterStr.toString())
                .minimizedButtonName(minimizedButtonName.toString())
                .toolBarHandler(
                        new ToolBarStateManager(info.getDrillToolbarType(),
                                ToolBarStateManager.BottomToolbarType.PLAIN, EMPTY_STRING)).isDisablingTime(true)
                .build();

        final MetaMenuItem generatedMetaMenuItem = new MetaMenuItem(chartMetaItem);

        if (searchFieldUser != SearchFieldUser.FALSE) { //  PATH or TRUE
            return new ChartLauncher(generatedMetaMenuItem, eventBus, getCenterPanel(), controller);
        }
        final BaseWindow window = controller.getWindow(generatedMetaMenuItem.getID());
        if (window != null) {
            window.hide();
        }

        return new ChartLauncher(generatedMetaMenuItem,eventBus,getCenterPanel(),controller);

    }}