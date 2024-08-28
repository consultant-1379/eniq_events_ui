/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.events.RefreshWindowEventHandler;

/**
 * Handle refresh on main window. Send server call with existing URL parameters to 
 * refresh window with server data
 *  
 * @author eeicmsy
 * @since July 2010
 */
public class BaseWinRefreshWindowHandler<D extends IBaseWindowView> implements RefreshWindowEventHandler {

    private final BaseWindowPresenter<D> baseWinPresenter;

    /**
     * Shut down handler for main window
     * @param baseWindowPresenter   - main window presenter
     * @param display              -  view reference
     */
    public BaseWinRefreshWindowHandler(final BaseWindowPresenter<D> baseWindowPresenter) {

        this.baseWinPresenter = baseWindowPresenter;
    }

    /* for use when using event bus to broadcast to all windows */
    @Override
    public void handleWindowRefresh(final MultipleInstanceWinId multiWinId) {

        // guards
        if (baseWinPresenter.isThisWindowGuardCheck(multiWinId)) {
            //refreshing the time on the window, this is not a drill folks!
            this.baseWinPresenter.setIsDrillDown(false);
            handleWindowRefresh();
        }
    }

    /* for direct refresh call */
    @Override
    public void handleWindowRefresh() {
        // when bread crumb exists this will use 
        // its (up to date) widgetSpecificUrlParams
        baseWinPresenter.getServerComm().makeServerCallWithURLParams();
    }
}
