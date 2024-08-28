/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.events.WidgetSpecificParamsChangeEventHandler;
import com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay;

/**
 * BaseWindowPresenter helper. Execute a widgetSpecificParams change on the window 
 * and resend server call (refresh) on window
 * 
 * (Follows general pattern to take some work out of BaseWindowPresenter)
 * 
 * @author eeicmsy
 * @since Jan 2011
 *
 */
public class BaseWinWidgetSpecificParamsChangeHandler<D extends IBaseWindowView> implements
        WidgetSpecificParamsChangeEventHandler {

    private final BaseWindowPresenter<D> baseWinPresenter;

    /** 
    *  Construct handler of widgetSpecificParams change event
    *  Utility for BasewindowPresenter
    *  
     * @param baseWindowPresenter   - main window presenter
     */
    public BaseWinWidgetSpecificParamsChangeHandler(final BaseWindowPresenter<D> baseWindowPresenter) {
        this.baseWinPresenter = baseWindowPresenter;

    }

    @Override
    public void handleWidgetSpecificParamsChange(final String tabId, final String winId,
            final String widgetSpecificParams, final String url, final SearchFieldDataType searchData,
            final boolean notRelaunchingWindow) {

        // guards 
        if (!baseWinPresenter.getTabOwnerId().equals(tabId)) {
            return;
        }

        if (!baseWinPresenter.getQueryId().equals(winId)) {
            return;
        }
        // on the fly update (which also updates breadcrumb if any)

        // (If more users of setWidgetSpecificParams to presenter would 
        // move this code into the presenter 
        // The breadcrumb can only be guaranteed complete until after make call
        // in BaseWinServerComms (so this is a partial update)

        final BreadCrumbMenuItem breadCrumb = baseWinPresenter.getCurrentBreadCrumbMenuItem();
        if (breadCrumb != null) {
            breadCrumb.setPartialWidgetURLParameters(widgetSpecificParams);
        }

        baseWinPresenter.setWidgetSpecificParams(widgetSpecificParams);

        /* handle cases when launch window without using search component */

        baseWinPresenter.resetSearchData(searchData);

        // extra while call ongoing (when result back resetting the search data above
        // will put same title bar up)
        baseWinPresenter.upDateWindowTitleWithSearchData(searchData);

        //  
        // Setting URL and not using any URLs from breadcrumb, 
        // to clearing existing breadcrumb URL when drilled down 

        baseWinPresenter.setWsURL(url);

        if (baseWinPresenter.drillDepth == 0 || notRelaunchingWindow) { // can at least trust this if never drilled

            // refresh
            baseWinPresenter.makeServerCallWithURLParams();
        } else {
            // calls a reset window action to clear breadcrumb (new launcher created)

            /* can not trust that baseWinPresenter.getSearchData() is up to date, i.e.
             * if search field update is whats invoking call
             */
            // TODO rename
            baseWinPresenter.handleGraphToGridToggleReset(searchData);
        }

    }

}
