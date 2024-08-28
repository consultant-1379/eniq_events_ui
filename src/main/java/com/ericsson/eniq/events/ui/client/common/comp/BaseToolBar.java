/**
 * -----------------------------------------------------------------------
i *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import static com.ericsson.eniq.events.ui.client.common.CSSConstants.*;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import com.ericsson.eniq.events.ui.client.common.widget.TimeRangeComboBox;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.datatype.kpi.KPIConfigurationPanelDataType;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Component Class - Custom Toolbar
 * with meta driven buttons and controls
 * 
 * THIS is the UPPER TOOLBAR
 * 
 * 
 * @author eendmcm
 * @since Feb 2010
 */

public class BaseToolBar extends ContentPanel implements IBaseToolbar {

    private static final String TIME_RANGE_COMP = "timeRangeComp";

    private final EventBus eventBus;

    private Label timeSelectedLabel = null; // left null for unit test

    private TimeRangeComboBox cmbTime = null;

    private final MultipleInstanceWinId multiWinId;

    private final ToolBar winToolBar;

    private final ContentPanel overLayContainer = new ContentPanel();

    private boolean isTimeDisabled = false;

    /**
     * Construct baseToolbar for window
     * @param multiWinId  -  id of window including support for multi-instance mode
     * @param eventBus - bus we fire events at
     */
    public BaseToolBar(final MultipleInstanceWinId multiWinId, final EventBus eventBus) {
        super();

        super.setHeaderVisible(false);
        this.multiWinId = multiWinId;
        this.eventBus = eventBus;
        this.setAutoHeight(true);

        //Component contains the toolbar and the overLay container
        winToolBar = new ToolBar();
        this.setTopComponent(winToolBar);
        overLayContainer.setHeaderVisible(false);
    }

    /* over-riding to default to preferred method in case called */
    @Override
    public Component getItemByItemId(final String itemID) {
        Component result = winToolBar.getItemByItemId(itemID);
        if (result == null) {
            result = super.getItemByItemId(itemID);

        }
        return result; // can still be null

    }

    /**
     * Preferred method when reading off the toolbar 
     * Assume when calling you know you are getting a button (or null if 
     * button does not exist) 
     * 
     * @param itemId  button id from meta data, e.g. "btnKPI"
     * @return        null if the item does not exist on the Toolbar, 
     *                else return the button
     *                
     */
    public Button getButtonByItemId(final String itemId) {
        final Component comp = winToolBar.getItemByItemId(itemId);
        if (comp instanceof Button) {
            return (Button) comp;
        }
        return null;
    }

    /**
     * Notification of search field change for window 
     */
    public void setResetMultiWinId(final MultipleInstanceWinId multiWinId) {
        if (cmbTime != null) {
            cmbTime.removeSelectionEventHandler();
            cmbTime.addSelectionEventHandler(multiWinId, this.eventBus);

        }
    }

    @Override
    public void addToolbarItem(final Component toolBarItem) {
        addComponent(toolBarItem);

    }

    /**
     * Inserts the provided container into the
     * overlay this component for display as part of the toolbar
     * @param obj
     */
    public void addOverLay(final Component obj) {
        this.insertOverLay(obj);
        this.layout(true);
    }

    /**
     * Utility returning the time comonent on the upper toolbar
     * @return  time component (combox or label), or can be null
     */
    public Component getTimeRangeComponent() {
        return winToolBar.getItemByItemId(TIME_RANGE_COMP);
    }

    /**
     * Remove listeners for clean up on window close.
     * Only to call this when know killing (hiding) the window
     * (general good practice in case memory resource issues)
     */
    public void cleanUpOnClose() {
        if (cmbTime != null) {
            cmbTime.removeSelectionEventHandler();

        }
    }

    /**
     * Since we have hidden the time information from user 
     * adding a label to right of upper toolbar to indicate current time selection
     * 
     * This is the time on the upper toolbar (indicating time made selection using the
     * time selection component), as apposed to the time stamp on the lower toolbar)
     * 
     * @param timeText  time label to display on upper toolbar
     */
    public void upDateTimeLabel(final String timeText) {
        getTimeSelectedLabel().setText(timeText);
    }

    /**
     * remove any existing items from 
     * the Toolbar
     */
    public void removeAllToolBarItems() {

        winToolBar.removeAll();
    }

    /**
     * add a separator icon to the toolBar 
     */
    @Override
    public void addToolbarSeperator() {

        winToolBar.add(new SeparatorToolItem());
    }

    @Override
    public Widget asWidget() {
        return this.asWidget();
    }

    /**
     * add an container for the wizard/overlay  that will
     * be rendered to the bottom of the toolBar. 
     * @param overLay
     */
    private void insertOverLay(final Component overLay) {
        this.add(overLay);
        overLay.render(this.getElement());
    }

    /*
     * split out like this for JUNIT
     */
    void addComponent(final Component item) {

        winToolBar.add(item);
    }

    private void replaceTimeRangeComp(final Component newTimeRangeComp) {
        newTimeRangeComp.setToolTip("Time selection for window");
        newTimeRangeComp.setId(TIME_RANGE_COMP);
        winToolBar.insert(newTimeRangeComp, getTimeRangeComponentIndex());
    }

    /*
     * removes the current time range component and 
     * returns the index of the removed time range component 
     * so it can be replaced with the updated time range component
     */
    private int getTimeRangeComponentIndex() {
        final Component timeRangeComp = getTimeRangeComponent();
        final int compIndex = winToolBar.indexOf(timeRangeComp);
        winToolBar.remove(timeRangeComp);
        return compIndex;
    }

    /**
     * Initially adds a TimeRangeComboBox as the Canned Time Control to the window menu bar
     * */
    public void initTimeRangeComboBox(final boolean isKpiWindow) {

        if (winToolBar.getItemByItemId(TIME_RANGE_COMP) == null) {
            winToolBar.add(new FillToolItem());// put this last component to far right
            winToolBar.add(new SeparatorToolItem());
            setTimeRangeComboBox(isKpiWindow);

            final boolean setComboVisible = isTodisplayTimeAsComboBox(TimeInfoDataType.DEFAULT);

            // still add it even if not showing it (i..e preserve index position)
            if (setComboVisible) {
                winToolBar.add(cmbTime);
            } else {
                // so don't see a default combobox you are about to replace with a label
                winToolBar.add(getTimeSelectedLabel());
            }
        }

    }

    /*  junit extraction */
    Label getTimeSelectedLabel() {
        if (timeSelectedLabel == null) {
            timeSelectedLabel = new Label();
            timeSelectedLabel.setId(TIME_RANGE_COMP);
        }
        return timeSelectedLabel;
    }

    void setTimeRangeComboBox(final boolean isKpiWindow) {

        if (cmbTime == null) {
            cmbTime = new TimeRangeComboBox();
            //init TimeRangeComboBox with corresponding meta data
            if(isKpiWindow) {
                final KPIConfigurationPanelDataType configPanelType = MainEntryPoint.getInjector().getMetaReader()
                                  .getKPIConfigurationPanelMetaData();
                cmbTime.initWithMetadata(configPanelType.getRefreshTime().getComboTimeData());
            }
            else {
                cmbTime.init();
            }
            cmbTime.setValue(cmbTime.getStore().getAt(DEFAULT_TIME_RANGE_SELECTED_INDEX));
            cmbTime.setToolTip("Time selection for window");
            cmbTime.setId(TIME_RANGE_COMP);
            cmbTime.addSelectionEventHandler(multiWinId, this.eventBus);

        }
    }

    /**
     * Effectually to always present as label 
     * (no canned combobox), 
     *  i.e. effectively to always disable
     *  
     */
    public void disableTimeRangeComp() {

        if ((winToolBar.getItemByItemId(TIME_RANGE_COMP) instanceof TimeRangeComboBox)) {
            getTimeSelectedLabel().addStyleName(GRID_TOOLBAR_CSS);
            this.timeSelectedLabel.setText(cmbTime.getRawValue());
            replaceTimeRangeComp(this.timeSelectedLabel);

        } // else its a label, i.e. disabled already 
        isTimeDisabled = true; // perminanly

    }

    private boolean isTodisplayTimeAsComboBox(final TimeInfoDataType time) {
        return (!isTimeDisabled && time.timeRange.length() > 0);
    }

    /**
     *  Sets the Canned Time Control to either label or combo box
     *  based on the time range parameters set in TimeInfoDataType 
     *  
     *  @param time - TimeInfoDataType dataType object to hold the Time Paramters selected by end user.
     *                Can be null when we are not displaying a time component
     *                
     */
    public void updateTimeRangeComp(final TimeInfoDataType time,final boolean isKpiWindow) {
        if (time == null) {
            return; // no time component on display
        }
        if (isTodisplayTimeAsComboBox(time)) {
            setTimeRangeComboBox(isKpiWindow);
            this.cmbTime.disableEvents(true);
            this.cmbTime.setValue(cmbTime.getStore().getAt(time.timeRangeSelectedIndex));
            this.cmbTime.enableEvents(true);
            if (!(winToolBar.getItemByItemId(TIME_RANGE_COMP) instanceof TimeRangeComboBox)) {
                replaceTimeRangeComp(this.cmbTime);
            }
        } else {
            // disabled time label needs to be updated to correct val too (so want to be in here)

            getTimeSelectedLabel().addStyleName(GRID_TOOLBAR_CSS);
            this.timeSelectedLabel.setText(time.toString());
            if (!(this.getItemByItemId(TIME_RANGE_COMP) instanceof Label)) {
                replaceTimeRangeComp(this.timeSelectedLabel);
            }
        }
    }

    public void setTimeDisabled(boolean timeDisabled) {
        isTimeDisabled = timeDisabled;
    }
}
