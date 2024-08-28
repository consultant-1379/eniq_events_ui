/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget;

import com.ericsson.eniq.events.ui.client.common.Constants;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.events.TimeParameterValueChangeEvent;
import com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Displays Dialog to the enduser allowing for the Time Parameters to be
 * provided
 *
 * @author eendmcm
 * @since Feb 2010
 */
public class TimeParameterDialogPresenter<D extends WidgetDisplay> extends ParameterDialogPresenter<D> {

    private final MultipleInstanceWinId multiWinID;

    /**
     * Constructor
     *
     * @param display  - view for this presenter (MVP pattern)
     * @param eventBus - name says it all
     */
    public TimeParameterDialogPresenter(final D display, final EventBus eventBus) {
        super(display, eventBus);

        multiWinID = createMultipleInstanceWinId();

        initDisplay();
    }

    /*
    * (Assumes view has presenter set)
    *
    * @return id which can be used im multiple window mode
    */
    MultipleInstanceWinId createMultipleInstanceWinId() {

        final IBaseWindowPresenter presenter = gridOrChartRef.getPresenter();

        /*
        * or use: gridOrChartRef.getParentWindow().getBaseWindowID()
        * gridOrChartRef.getMenuTaskBar().getTabOwnerId();
        */

        return presenter.getMultipleInstanceWinId();
    }

    /*
    * Initialise and Display the Time Parameters Dialog (split like this to allow
    * for JUNIT)
    */

    void initDisplay() {
        // get handle to Parent
        final BaseWindow dlgParent = gridOrChartRef.getParentWindow();
        setDialog(new TimeParameterDialog(getGridText(), gridOrChartRef.getTimeData(),isKpiWindow(dlgParent)));
        super.initDisplay(dlgParent);
    }

    /*
    * To check whether the parent window is from the kpi panel
    */
    private boolean isKpiWindow(final BaseWindow dlgParent) {
        boolean isKpiWindow=dlgParent.getIcon().toUpperCase().contains(Constants.KPI_PANEL_ICON);
        return isKpiWindow;
    }

    /*
    * split like this to allow for JUNIT
    */
    String getGridText() {
        // title bar on time dialog (not to say "Core")
        return gridOrChartRef.getViewSettings().getTaskBarButtonAndInitialTitleBarName();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.common.widget.ParameterDialogPresenter#handleSuccessfulEvent()
     */
    @Override
    public void handleSuccessfulEvent() {
        final TimeInfoDataType userTimeDetails = ((TimeParameterDialog) getDialog()).getUserTimeSelection();
        // MVP pattern does not allow talk to window direct so using eventBus
        getEventBus().fireEvent(new TimeParameterValueChangeEvent(multiWinID, userTimeDetails));
    }

}
