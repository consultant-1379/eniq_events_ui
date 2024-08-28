/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.main;

import com.ericsson.eniq.events.ui.client.charts.window.ChartWindowPresenter;
import com.ericsson.eniq.events.ui.client.charts.window.ChartWindowView;
import com.ericsson.eniq.events.ui.client.charts.window.IChartWindowView;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.common.comp.WindowState;
import com.ericsson.eniq.events.ui.client.common.widget.AbstractBaseWindowDisplay;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Generic action for main menu item selection when we know 
 * the window type to launch is a chart.
 * 
 * Performs launch of empty chart window. 
 * Window itself will discover if it needs to request data to populate.
 * 
 * Also used to toggle from grid to a chart when Response passed in directly.
 * 
 * @see  {@link com.ericsson.eniq.events.ui.client.main.GenericTabPresenter} 
 * @author eeicmsy
 * @since April 2010
 *
 */
public class ChartLauncher extends AbstractWindowLauncher {

    private final EventBus eventBus;

    private ChartWindowPresenter presenterCreated;

    /**
     * Construct generic selection listener for main menu menuitem 
     * responsible for launching charts
     *  
     * @param item               Menu item selected with details for server calls required to populate grid
     * @param eventBus           The singleton event bus used in MVP pattern
     * @param containingPanel    Center panel where launched window will be constrained to.
     * @param workspaceController Controller with container which will "own" the launched window
     */
    public ChartLauncher(final MetaMenuItem item, final EventBus eventBus, final ContentPanel containingPanel,
            final IWorkspaceController workspaceController) {
        this(item, eventBus, containingPanel, workspaceController, "");
    }

    /**
     * Construct generic selection listener for main menu menuitem 
     * responsible for launching charts
     *  
     * @param item               Menu item selected with details for server calls required to populate grid
     * @param eventBus           The singleton event bus used in MVP pattern
     * @param containingPanel    Center panel where launched window will be constrained to.
     * @param workspaceController Controller with container which will "own" the launched window
     * @param windowId            Associate search data with a window id. Only used by workspace controller
     */
    public ChartLauncher(final MetaMenuItem item, final EventBus eventBus, final ContentPanel containingPanel,
            final IWorkspaceController workspaceController, String windowId) {

        super(item, containingPanel, workspaceController, windowId);
        this.eventBus = eventBus;
    }

    @Override
    public AbstractBaseWindowDisplay createView(final MultipleInstanceWinId multiWinId, final WindowState windowState) {

        return new ChartWindowView(multiWinId, item, workspaceController, containingPanel, windowState);
    }

    /**
     * Utility to update chart title to add a sub title from a launcher reference
     * 
     * @param subTitle e.g. pass "Cause Code 16"
     */
    public void updateChartTitle(final String subTitle) {
        if (presenterCreated != null) {
            presenterCreated.addChartSubTitle(subTitle);
        }
    }

    @Override
    public BaseWindowPresenter<IChartWindowView> createPresenter(final AbstractBaseWindowDisplay view,
            MultipleInstanceWinId winId) {

        presenterCreated = new ChartWindowPresenter((IChartWindowView) view, winId, eventBus);
        return presenterCreated;
    }

    @Override
    public void handleEnablingForReLaunch() {
        // nothing to do
    }
}
