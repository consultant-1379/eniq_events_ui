/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.common.widget;

import static com.ericsson.eniq.events.highcharts.client.ChartConstants.*;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.ericsson.eniq.events.common.client.datatype.ChartDataType;
import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.client.charts.ChartElementDetails;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.datatype.LicenceGroupTypeDataType;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.WizardInfoDataType;
import com.ericsson.eniq.events.ui.client.events.HideShowChartElementEvent;
import com.ericsson.eniq.events.ui.client.events.RefreshWindowEventHandler;
import com.ericsson.eniq.events.ui.client.events.TimeParameterValueChangeEventHandler;
import com.ericsson.eniq.events.ui.client.gin.EniqEventsUIGinjector;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay;
import com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController;
import com.ericsson.eniq.events.widgets.client.checkable.AllCheckbox;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Changing line chart functionality to use a "wizard overlay" to
 * rather than a menu item to toggle on and off the chart elements.
 * <p/>
 * This wizard overlay is different than standard (cause code) wizard overlay, in that
 * <p/>
 * <li> the result set is not changing (not passing parameters based on user checkbox selection).
 * We have to draw the graph with full result set but only display what is selected in the checkboxes) </li>
 * <p/>
 * <li>the checkboxes are not changing (can be based on fixed meta data - do not want a fresh call for checkboxes
 * following a search field or time change </li>
 *
 * @author eeicmsy
 * @see com.ericsson.eniq.events.ui.client.common.widget.WizardOverLayDynamic
 * @since Aug 2011
 */
public class WizardOverLayFixedResultSet<D extends IExtendedWidgetDisplay> extends AbstractWizardOverLay<D> {

    private final EniqEventsUIGinjector injector = MainEntryPoint.getInjector();

    private final static int MAX_TEXT_LENGTH_FOR_CB = 25;

    private Map<String, ContentPanel> licencePanels;

    private Map<String, AllCheckbox> allCheckboxes;

    //TODO not looking so generic for changes - i.e. what about LTE, Data Volume, etc
    private static final List<String> GROUP_WIZ_IDS = Arrays.asList("EVENT_VOLUME", "KPI_ANALYSIS",
            "NETWORK_EVENT_VOLUME");

    /**
     * Fixed result set for wizard checkboxes (fetched via fixed meta data)
     * Checkbox action will be to show and hide chart elements from
     * an initial fixed result set action
     * <p/>
     * Checkbox selection added to cache
     */
    public WizardOverLayFixedResultSet(final MetaMenuItem metaMenu, final IWorkspaceController workspaceController,
            final WizardInfoDataType wizardInfo, final BaseWindowPresenter<? extends WidgetDisplay> basePresenter,
            final EventBus bus, final D display) {

        super(metaMenu, workspaceController, wizardInfo, basePresenter, bus, display);
    }

    @Override
    public void submitSearchFieldInfo() {
        // over-riding to do nothing because fixed checkbox options do not care about
        // search field update
    }

    @Override
    public ClickHandler getLaunchSelectionListener() {
        return new LaunchSelectionListener();
    }

    @Override
    public RefreshWindowEventHandler getWizardRefreshHandler() {
        return new FixedWizardRefreshHandler(); // only while chard not launched

    }

    @Override
    public TimeParameterValueChangeEventHandler getWizardTimeChangeHandler() {
        return new FixedWizardTimeChangeHandler();
    }

    @Override
    public void clearCachedChartData() {

    }

    @Override
    public boolean hasChartCache() {
        return false;
    }

    /* resisting temptation to define a loadURL with our own JSON,
     * instead reading checkboxes off existing meta data structure for charts and grids
     * 
     * @see com.ericsson.eniq.events.ui.client.common.widget.WizardOverlay#loadWizardData()
     */
    @Override
    public void loadWizardData() {
        display.stopProcessing();
        //Clean start
        cpResponseHolder.removeAll();
        cpResponseHolder.setAutoHeight(true);
        checkboxes.clear();

        if (allCheckboxes != null) {
            allCheckboxes.clear();
        }

        if (licencePanels != null) {
            licencePanels.clear();
        }

        // same String must be in chart and grid section, e.g. NETWORK_EVENT_VOLUME
        final String wizId = wizardInfo.getWizardID();
        final boolean hasGroups = GROUP_WIZ_IDS.contains(wizId);

        // working with existing meta data format (not loading a new JSON)
        final GridInfoDataType gridInfoType = injector.getMetaReader().getGridInfo(wizId); // need for licences
        final ChartDataType chartDataType = injector.getMetaReader().getChartConfigInfo(wizId);
        final List<LicenceGroupTypeDataType> licenceGroupTypes = injector.getMetaReader().getLicenceGroupTypeDataType();

        final String indexNotForCheckBox = chartDataType.xAxisColID;

        final List<ColumnInfoDataType> columnInfoDataTypes = Arrays.asList(gridInfoType.columnInfo);
        Collections.sort(columnInfoDataTypes);

        // the grid data (its the same response) has better licencing information
        for (final ColumnInfoDataType columnInfo : columnInfoDataTypes) {
            // don't include x axis info in checkbox (y axis only)
            if (!columnInfo.columnID.equals(indexNotForCheckBox) && !columnInfo.isSystem) { // there is no system columns in graphs but future proof

                final String header = columnInfo.columnHeader;
                final CheckBox chk = new CheckBox();

                setUpCheckBox(chk, header, columnInfo.columnID);

                String licence = columnInfo.licenceType;
                if (hasGroups) {
                    if (licence == null || licence.isEmpty()) {
                        licence = UNGROUPED; //no licence info supplied from metadata when grouping is expected
                    }

                    final ContentPanel licencePanel = getLicencePanel(licence, licenceGroupTypes);
                    licencePanel.add(chk);

                    if (allCheckboxes != null) {
                        allCheckboxes.get(licence).registerChild(chk);
                    }

                } else {
                    cpResponseHolder.add(chk);
                }

                chkAll.registerChild(chk);
                checkboxes.add(chk);
            }
        }

        addLicensePanels();

        //If no resultset for the provided Criteria disable options
        cpWizardRadioGroup.enableRadioChart(!checkboxes.isEmpty());
        chkAll.setEnabled(!checkboxes.isEmpty());
        final boolean isChartSelected = cpWizardRadioGroup.isChartSelected();
        toggleRadioStatus(isChartSelected);
        selectAllSetEnabled(isChartSelected);
        cpResponseHolder.layout(true);
        setWizardFixed();
    }

    private void addLicensePanels() {
        ContentPanel grouped = null;

        if (licencePanels != null) {
            for (final ContentPanel cp : licencePanels.values()) {
                if (cp.getHeading().equalsIgnoreCase(UNGROUPED)) {
                    grouped = cp;
                    continue;
                }
                cpResponseHolder.add(cp);
                cp.setExpanded(false);
                toggleOverLayIcon(cp);
            }
            //add the "uncategorised" panel last to the wizard
            if (grouped != null) {
                cpResponseHolder.add(grouped);
                grouped.setExpanded(false);
                toggleOverLayIcon(grouped);
            }
        }

    }

    @Override
    protected void toggleRadioStatus(final boolean isChartChecked) {
        super.toggleRadioStatus(isChartChecked);
        selectAllSetEnabled(isChartChecked);
        enableHeaders(isChartChecked);
    }

    private void enableHeaders(final boolean isChartChecked) {
        if (licencePanels != null) {
            if (isChartChecked) {
                for (final ContentPanel cp : licencePanels.values()) {
                    cp.getHeader().setEnabled(true);
                }
            } else {
                for (final ContentPanel cp : licencePanels.values()) {
                    cp.getHeader().setEnabled(false);
                }
            }
        }
    }

    private void selectAllSetEnabled(final Boolean isChartChecked) {
        if (allCheckboxes != null) {
            for (final AllCheckbox chk : allCheckboxes.values()) {
                chk.setEnabled(isChartChecked);
            }
        }
    }

    private ContentPanel getLicencePanel(final String licenceType,
            final List<LicenceGroupTypeDataType> licenceGroupTypes) {
        if (licencePanels == null) {
            licencePanels = new TreeMap<String, ContentPanel>();
        }

        if (allCheckboxes == null) {
            allCheckboxes = new HashMap<String, AllCheckbox>();
        }

        if (licencePanels.containsKey(licenceType)) {
            return licencePanels.get(licenceType); // exit
        }
        String name = licenceType;

        for (final LicenceGroupTypeDataType licenceGroupType : licenceGroupTypes) {
            if (licenceGroupType.getId().equals(licenceType)) {
                name = licenceGroupType.getName();
                break;
            }
        }

        final ContentPanel licenceContentPanel = getBasePanel(name, "wizard-top", true, name);
        licenceContentPanel.setId(name);
        licenceContentPanel.getHeader().sinkEvents(Events.OnClick.getEventCode());
        licenceContentPanel.getHeader().addListener(Events.OnClick, new OverLayHeaderListener(true));

        licencePanels.put(licenceType, licenceContentPanel);

        final AllCheckbox allCheckbox = new AllCheckbox();
        allCheckboxes.put(licenceType, allCheckbox);
        chkAll.registerChild(allCheckbox.getCheckBox());
        licenceContentPanel.add(allCheckbox);

        return licenceContentPanel;
    }

    private void setUpCheckBox(final CheckBox chk, final String header, final String columnId) {
        chk.setText(formatLength(header));
        chk.setFormValue(columnId); //"1", etc
        chk.setTitle(header); // tooltip (full header)
    }

    /*
     * Using CSS for wizard-content-body label and setting a width (and <tt>float:left</tt> or
     * <tt>display:block</tt> so not inline) is not going to help case for when not large
     * (i.e. cause codes) - so adding this method
     * @param header  e.g. MS Originating Call Completion Ratio
     * @return        hard coded truncated string, e.g "MS Originating Call Compl..."
     */
    private String formatLength(final String header) {
        if (header.length() > MAX_TEXT_LENGTH_FOR_CB) {
            return header.substring(0, MAX_TEXT_LENGTH_FOR_CB) + ELLIPSE;
        }
        return header;
    }

    /**
     * for fixed wizard case can invoke launch code direct from a time change update
     *
     * @param isServerCallRequested true if coming from a time change update or refresh(requiring a
     *                              new chart to be launched (without having to press launch button)
     */
    private void doLaunch(final boolean isServerCallRequested) {
        /* guard for lack of search field
         * we will let the checkboxes appear as don't need search field for that,
         * but when press launch we will check for URL call
         */
        if (isSearchDataRequiredMissing()) {
            return;
        }

        if (cpWizardRadioGroup.isGridSelected()) {
            launchGrid();
        } else {
            final MultipleInstanceWinId multiWinId = baseWinPresenter.getMultipleInstanceWinId();

            final Set<ChartElementDetails> elementIdsTicked = new HashSet<ChartElementDetails>();
            for (final CheckBox chk : checkboxes) {
                if (chk != null && chk.getValue()) {
                    elementIdsTicked.add(getChartElementDetails(chk));
                }
            }

            if (!elementIdsTicked.isEmpty()) { // Consistent with dynamic (launch does nothing when empty)

                if (isServerCallRequested || isFirstLaunch()) {
                    launchChart(EMPTY_STRING);
                }

                eventBus.fireEvent(new HideShowChartElementEvent(multiWinId, elementIdsTicked));
                setWizardFixed();
            }
        }
    }

    private ChartElementDetails getChartElementDetails(final CheckBox chk) {
        return new ChartElementDetails(chk.getFormValue());
    }

    private class LaunchSelectionListener implements ClickHandler {

        @Override
        public void onClick(final ClickEvent event) {
            doLaunch(false);
        }
    }

    /*
     * Fixed wizard case. This is used for time control to react to 
     * time change in chart or grid not launched yet. 
     * If time is change - will do nothing until press launch (except store time to 
     * use in call) 
     */
    private final class FixedWizardTimeChangeHandler implements TimeParameterValueChangeEventHandler {

        @Override
        public void handleTimeParamUpdate(final MultipleInstanceWinId multiWinId, final TimeInfoDataType time) {

            // Have to check search field data here when in multi-mode
            if (!baseWinPresenter.getMultipleInstanceWinId().equals(multiWinId)) {
                return;
            }
            handleTimeParamUpdate(time);
        }

        @Override
        public void handleTimeParamUpdate(final TimeInfoDataType time) {

            /* 
             * (only required on first launch - after
             * this BaseWindowPresenter (ChartPreseter) handling can take over
             */
            if (isFirstLaunch()) {
                baseWinPresenter.setTimeData(time);
                // no need to launch chart (if did launch it would have two calls) 
            }

            // in case open
            setWizardFixed();

        }
    }

    /*
     * Fixed wizard case. This is used for refresh control to react to 
     * time change in chart or grid not launched yet. 
     * Changed functionality as still making the main parent call - will
     * do nothing when press refresh (he has his checkboxes)
     */
    private final class FixedWizardRefreshHandler implements RefreshWindowEventHandler {

        @Override
        public void handleWindowRefresh(final MultipleInstanceWinId multiWinID) {

            if (!baseWinPresenter.getMultipleInstanceWinId().equals(multiWinID)) {
                return;
            }
            handleWindowRefresh();
        }

        @Override
        public void handleWindowRefresh() {
            // in case open
            setWizardFixed();
        }
    }
}