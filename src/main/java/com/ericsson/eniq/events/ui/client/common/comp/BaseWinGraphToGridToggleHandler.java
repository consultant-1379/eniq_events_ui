/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.ui.client.charts.window.ChartWindowView;
import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.PresetResponseDisplayDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.events.GraphToGridEventHandler;
import com.ericsson.eniq.events.ui.client.main.AbstractWindowLauncher;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Handle toggling the display in the base window from displaying 
 * a chart to displaying a grid and vice versa.
 * 
 * Also contains a reset to set window back to state it would be on initial launch
 * (e.g. when replacing search field data)
 * 
 * @author eeicmsy
 * @since July 2010
 *
 */
public class BaseWinGraphToGridToggleHandler<D extends IBaseWindowView> implements GraphToGridEventHandler {

    private final BaseWindowPresenter<D> baseWinPresenter;

    private final EventBus eventBus;

    private final D display;

    /**
     * Graph to grid toggle handler for floating windows (BaseWindowPresenter)
     * @param baseWindowPresenter   - main window presenter
     * @param eventBus              - default event bus
     * @param display               - main view reference
     * 
     */
    public BaseWinGraphToGridToggleHandler(final BaseWindowPresenter<D> baseWindowPresenter, final EventBus eventBus,
            final D display) {
        this.baseWinPresenter = baseWindowPresenter;
        this.eventBus = eventBus;
        this.display = display;

    }

    @Override
    public void handleGraphToGridToggle(final MultipleInstanceWinId multiWinId, boolean shouldResetMeta,
            final String toolbarType, final String elementClickedForTitleBar) {

        if (baseWinPresenter.isThisWindowGuardCheck(multiWinId)) { // guard

            /* fetch "toggle" launcher and  take the Response from this window before kill it to 
             * pass it the the new window on launch - multi-instance windows must preserve fixed query id 
             * across to new window*/


            WindowState oldWindowState = display.getWindowState();

            final AbstractWindowLauncher launcher = ((IExtendedWidgetDisplay) display)
                    .getToggleWindowLauncher(eventBus);
            launcher.setFixedQueryId(baseWinPresenter.getFixedQueryId());
            launcher.setWsURL(baseWinPresenter.getWsURL());

            if (!(((IExtendedWidgetDisplay) display) instanceof ChartWindowView)) {
                shouldResetMeta = false;
            }
            launcher.handleGraphToGridToggleInfo(baseWinPresenter.getPresetResponseDisplayData(), shouldResetMeta,
                    toolbarType);

            /* now we must kill ourselves quickly
             * or menu taskbar is going to have serious issues
             * keeping track of two windows with the same id
             *
             */
            killWindowBeforeReplace();

            launcher.launchWindowFromChart(baseWinPresenter.getWindowTimeDate(), true, elementClickedForTitleBar, oldWindowState);
        }
    }

    @Override
    public void handleGraphToGridToggleReset(final MultipleInstanceWinId multiWinId, final SearchFieldDataType data) {

        if (baseWinPresenter.isThisWindowGuardCheck(multiWinId)) { // guard

            /* RESET new window - get normal launcher for chart or grid
             * if its toggled now - toggle it back to what it should be */
            WindowState oldWindowState = display.getWindowState();

            final PresetResponseDisplayDataType position = baseWinPresenter.getPresetResponseDisplayData();
            position.nullifyResponse(); // force new call always

            final AbstractWindowLauncher launcher;
            if (baseWinPresenter.isWindowInToggledState()) {
                launcher = ((IExtendedWidgetDisplay) display).getToggleWindowLauncher(eventBus);
                launcher.handleGraphToGridToggleInfo(position, false, null);
            } else {
                launcher = ((IExtendedWidgetDisplay) display).getWindowLauncher(eventBus);
                launcher.setPresetResponseDisplayData(position);
            }

            /* now we must kill ourselves quickly
             * or menu taskbar is going to have serious issues
             * keeping track of two windows with the same id
             * 
             */
            killWindowBeforeReplace();

            launcher.launchWindow(baseWinPresenter.getTimeData(), data, false,
                    oldWindowState);
        }
    }

    /* 
     * Kill old window before replacing
     * (access for junit)
     */
    void killWindowBeforeReplace() {
        ((BaseWindow) display).hide();
        baseWinPresenter.handleShutDown(); // unbind - clear out all event listeners in this presenter too

    }

}
