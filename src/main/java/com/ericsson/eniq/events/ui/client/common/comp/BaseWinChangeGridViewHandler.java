/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import java.util.List;

import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.ui.client.common.widget.IEventGridView;
import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.events.ChangeGridViewEventHandler;
import com.ericsson.eniq.events.ui.client.main.AbstractWindowLauncher;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Allow base window to change from grid view to grouping grid view
 * (extracted from main BaseWindowPresenter for class length considerations)
 * @author eeicmsy
 * @since July 2010
 *
 */
public class BaseWinChangeGridViewHandler<D extends IBaseWindowView> implements ChangeGridViewEventHandler {

    private final BaseWindowPresenter<D> baseWinPresenter;

    private final EventBus eventBus;

    private final D display;

    public BaseWinChangeGridViewHandler(final BaseWindowPresenter<D> baseWindowPresenter, final EventBus eventBus,
            final D display) {
        this.baseWinPresenter = baseWindowPresenter;
        this.eventBus = eventBus;
        this.display = display;

    }

    @Override
    public void handleChangeGridView(final MultipleInstanceWinId multiWinId, final String winTitle,
            final GridInfoDataType gridInfoDataType, final Response response, final JSONValue data,
            final List<Filter> filters, final Menu breadCrumbMenu, final SearchFieldDataType searchData,
            final String wsURL, final TimeInfoDataType timeInfo, boolean isToggling, boolean isDrilling) {

        if (baseWinPresenter.isThisWindowGuardCheck(multiWinId)) {// guard

            WindowState prevWindowState = display.getWindowState();
            final AbstractWindowLauncher launcher = ((IEventGridView) display).getWindowLauncher(eventBus);
            launcher.setPresetResponseDisplayData(baseWinPresenter.getPresetResponseDisplayData());

            /* now we must kill ourselves quickly
             * or menu taskbar is going to have serious issues
             * keeping track of two windows with the same id
             * 
             */
            ((BaseWindow) display).hide();
            baseWinPresenter.handleShutDown();

            final String widgetSpecificParams = ((IExtendedWidgetDisplay) display).getWidgetSpecificURLParams();
            isToggling = true;
            // TODO: check toggling: why it is overriden? moreover it is considered as toggled for graph in cases
            // when it was not!!! - misleading!

            // TODO: Note: it is invoked for not drilled down windows as well - confusing and misleading!!!
            launcher.launchDrillDownWindow(winTitle, gridInfoDataType, response, data, filters, breadCrumbMenu,
                    searchData, widgetSpecificParams, wsURL, timeInfo, isToggling, prevWindowState, isDrilling);
            // TODO: example: Subscriber [tab] > select IMSI > enter IMSI e.g. 310410000004327 > Launch [menu] >
            // Event Analysis -> Core
            // TODO: consider to pass winTitle in windowState
        }
    }
}
