/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.charts.window;

import com.ericsson.eniq.events.ui.client.charts.IChartPresenter;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.WindowState;
import com.ericsson.eniq.events.ui.client.common.widget.AbstractBaseWindowDisplay;
import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.ToolBarURLChangeDataType;
import com.ericsson.eniq.events.ui.client.grid.FooterToolBar;
import com.ericsson.eniq.events.ui.client.main.AbstractWindowLauncher;
import com.ericsson.eniq.events.ui.client.main.ChartLauncher;
import com.ericsson.eniq.events.ui.client.main.GridLauncher;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.http.client.Response;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Generic view for a chart view.
 * This will be the base view for all charts.
 *
 * @author eeicmsy
 * @since April 2010
 */
public class ChartWindowView extends AbstractBaseWindowDisplay implements IChartWindowView {

    private final IChartPresenter chart;

    // TODO in time take away metaMenuItem copy from here (we can access the presenter)
    protected final MetaMenuItem metaItem;

    private final FooterToolBar bottomToolbar;

    /**
     * Construct generic chart view supporting passing of instance type such
     * that instance type will be available to base class when over-riding methods
     * to create special launch buttons
     *
     * @param multiWinId          - Multi-instance support id. Contains id we put on base window (which is same as what will use for menu item,
     *                            launch button and query (contains search field data)
     * @param metaItem            - Menu Item which launched the window (chart) containing all information
     *                            needed to submit URL query etc)
     * @param workspaceController
     * @param constrainArea       - area window (chart) is to sit in.
     * @param windowState         - state of window
     *
     */
    public ChartWindowView(final MultipleInstanceWinId multiWinId,
            final MetaMenuItem metaItem, final IWorkspaceController workspaceController,
            final ContentPanel constrainArea, WindowState windowState) {
        super( multiWinId, workspaceController, constrainArea, metaItem.getTaskBarButtonAndInitialTitleBarName(), metaItem.getStyle(), windowState);

        this.metaItem = metaItem;

        chart = MainEntryPoint.getInjector().getChartPresenter();
        chart.init(this.metaItem.getDisplay());

        bottomToolbar = new FooterToolBar();
        getWidget().setBottomComponent(bottomToolbar);

    }

    @Override
    public void startProcessing() {
        /* checked IE, chrome, firefox, safari
         * getting problems using checkbox view menu items - in firefox 
         * (after refresh chart -they no longer have an affect unless
         * minimise and maximise window again) -
         * which this visibility hack 
         * seems to solve to a degree - except for some versions of IE8 which 
         * cause charts to launch empty (but refresh is ok in IE) 
         */
        if (!GXT.isIE) {
            chart.setVisible(false);
        }
        super.startProcessing();
    }

    @Override
    public void stopProcessing() {
        /* see startProcessing comment */
        if (!GXT.isIE) {
            chart.setVisible(true);
        }

        super.stopProcessing();

    }

    /**
     * Over-riding #minimize due to some bug with chart not closing
     * on minimise press - without this the window closes but chart remains.
     * See also what had to do when launch window again.
     * ecarsea - Same problem exists with HighCharts
     *
     * @see com.ericsson.eniq.events.ui.client.common.comp.MenuTaskBarButton
     */
    // (TODO check with memory profiler)
    @Override
    public void onMinimize() {
        super.onMinimize();
        chart.setVisible(false);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.common.comp.BaseWindow#onShow()
     */
    @Override
    protected void onShow() {
        super.onShow();
        chart.setVisible(true);
    }

    @Override
    protected void onRestore() {
        super.onRestore();

        if (isMinimised()) {
            /*
            * this may be hacky here (well seems like OFC error to me) - have
            * an issue with charts not minimising when minimising its parent
            * window - see ChartWindowView -- this is to recover OFC chart on launch
            * button press
            */
            chart.setVisible(true);
        }
    }

    @Override
    public MetaMenuItem getViewSettings() {
        return metaItem;
    }

    @Override
    public IChartPresenter getChartControl() {
        return chart;
    }

    @Override
    public AbstractWindowLauncher getToggleWindowLauncher(final EventBus eventBus) {
        if (toggleWindowLauncher == null) {

            toggleWindowLauncher = new GridLauncher(metaItem, eventBus, constraintArea, workspaceController);
        }
        return toggleWindowLauncher;
    }

    @Override
    public AbstractWindowLauncher getWindowLauncher(final EventBus eventBus) {
        return new ChartLauncher(metaItem, eventBus, constraintArea, workspaceController);
    }

    /**
     * Method supporting changing window view from chart to grid
     * with out using existing meta menu item data
     * (previous Response object nullified at point of call)
     * <p/>
     * Utility resetting URL when toggle from graph to grid
     * (in case where forcing a new server call on this toggle - not presenting
     * the data in the current chart)
     *
     * @param eventBus the event bus
     * @param urlInfo  containing URL of new service and any additional info required (e.g. toolbar)
     */
    public void changeToggleWindowLauncher(final EventBus eventBus, final ToolBarURLChangeDataType urlInfo) {

        /* making a copy of metamenuItem here makes no odds to internal data */
        metaItem.setWidgetSpecificParams(urlInfo.getWidgetSpecificParams()); // e.g. groupname=noki
        metaItem.setWsURL(urlInfo.url);
        metaItem.setMaxRowsParam(urlInfo.maxRowsParam);
        /* if change replacingMeta.display to "grid" at this point ChartGridChangeEventHandler will fail to go from
         * grid back to same kind of chart as used be */

        metaItem.setWindowType(MetaMenuItemDataType.convertType(urlInfo.windowType));

        /* change toggle toolbar for chart drill down but don't loose original 
         * as will want it back when user opens a graph again*/

        metaItem.setTempToggleToolBarType(urlInfo.toolbarType);

        toggleWindowLauncher = new GridLauncher(metaItem, eventBus, constraintArea, workspaceController);

    }

    /**
     * Unfortunately we have three types of toggle toolbar now,
     * i.e. chart, toggled grid and drilled grid from chart
     * don't let chart drilling mess up chart toggle toolbar.
     * Call to reset toggle toolbar back to what is was after drill
     * (Method paired with #resetToggleWindowLauncher)
     */
    public void resetUpperToggleToolBar() {
        if (toggleWindowLauncher != null) {
            toggleWindowLauncher.resetUpperToggleToolBar();
        }

    }

    @Override
    public String getLastRefreshTimeStamp() {
        return bottomToolbar.getLastRefreshTimeStamp();
    }

    @Override
    public void updateLastRefreshedTimeStamp(final String timeStamp) {
        bottomToolbar.updateLastRefreshedTimeStamp(timeStamp);
    }

    /**
     * Only do for (success) server calls - i.e. resets current TimeStamp for window
     */
    @Override
    public void upDateLastRefreshedLabel(Response response) {
        bottomToolbar.upDateLastRefreshedLabel(response);
    }

}
