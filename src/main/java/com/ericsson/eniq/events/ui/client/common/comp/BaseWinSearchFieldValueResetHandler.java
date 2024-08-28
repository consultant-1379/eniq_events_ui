/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldUser;
import com.ericsson.eniq.events.ui.client.events.SearchFieldValueResetEventHandler;

/**
 * Handle search field update for Base window presenter.This call out is only 
 * sent to windows interested in the search field (i.e. windows set up 
 * first day in MenuTaskBar as being interested in receiveing search field updates.
 *  
 * @author eeicmsy
 * @since July 2010
 *
 */
public class BaseWinSearchFieldValueResetHandler<D extends IBaseWindowView> implements SearchFieldValueResetEventHandler {

    private final BaseWindowPresenter<D> baseWinPresenter;

    /**
     * Shut down handler for main window
     * @param baseWindowPresenter   - main window presenter
     */
    public BaseWinSearchFieldValueResetHandler(final BaseWindowPresenter<D> baseWindowPresenter) {
        this.baseWinPresenter = baseWindowPresenter;

    }

    /*
     * Handle search field updates if it turns out we are an intested window
     * @see com.ericsson.eniq.events.ui.client.events.SearchFieldValueResetEventHandler#handleSearchFieldParamUpdate(java.lang.String, java.lang.String)
     */
    @Override
    public void handleSearchFieldParamUpdate(final String tabId, final String queryId, final SearchFieldDataType data,
            final String url) {

        // guards
        if (!baseWinPresenter.getTabOwnerId().equals(tabId)) {
            return;
        }

        if (!baseWinPresenter.getQueryId().equals(queryId)) {
            return;
        }

        if (data == null || data.isEmpty()) {
            return;
        }

        /* window not to react to search field value update for this node type */
        if (baseWinPresenter.isExcludedSearchType(data.getType())) {
            return;
        }

        /*determine if this is a multi window and the enduser has potentially drilled to a screen
         * that reset the needSerach param flag and is now attempting to launch another call that requires the 
         * needSearchFieldParam to be overlooked*/

        final boolean needSearchFieldParamToPopulate = baseWinPresenter.isSearchFieldDataRequired();
        final boolean hasMultiResultSet = baseWinPresenter.hasMultiResultSet();

        if (!needSearchFieldParamToPopulate && !hasMultiResultSet) {
            return;
        }

        final boolean isSearchDataChanged = baseWinPresenter.resetSearchData(data);

        /* grids to display the potential responses.  - if search data not changed then we are just going to refresh*/

        if ((data.isGroupMode() || hasMultiResultSet) && isSearchDataChanged) {

            /* clear current drilldown levels and start again at  summary (
             *  perhaps could just let this happen in all cases)
             */

            resetURL(url);
            baseWinPresenter.handleSearchFieldUpdateWithGridClear(data);
        }
        /* retain search data for later calls */
        handleSearchSubmit(data);

    }

    /*
     * reset to default window url may be neccessary
     * if user changes node when in a drilldowned state
     */
    private void resetURL(final String url) {
        if (url != null && url.length() > 0) {
            baseWinPresenter.setWsURL(url);
        }
    }

    /*
     * May require a window toggle on play press
     * @param data search data
     */
    private void handleSearchSubmit(final SearchFieldDataType data) {

        /* WHATEVER setting this "isSearchField" user is at moment 
        // it should be set to "true" as we are now initiating a 
        // change via main search field update and calling only 
        // window interested in the search field (previously cached at window set up) 
        // so have called setting search field user to true here 
        // too) - this ensures "type" is added to outbound parameter which might not be 
        // if some other action like drilling into failures has currupted state of "isSearchField user" 
        // metamenItam data */

        baseWinPresenter.setSearchFieldUser(data.isPathMode() ? SearchFieldUser.PATH : SearchFieldUser.TRUE);

        /* retain search data for later calls */

        /* 
         * The "best" we can offer here is the refresh. This does mean our behaviour will be
         * as follows with respect to drilldown windows breadcrumb(now that using BreadCrumb to
         * store widget parameters). This means if 
         * 1) User has drilldowned but screens are still applicable to current node - then keep breadcrumb
         * 2) when screens are not applicable to current node - clear breadcrumb  
         * 
         * This works fine for grids but for charts in a toggled state (i.e. now displayed as grid) we 
         * have problems . Cleaner to "toggle" to a whole new fresh window.
         * 
         */
        if (baseWinPresenter.isWindowInToggledState()) {
            baseWinPresenter.handleGraphToGridToggleReset(data);
        } else {
            // even if search data not changed he may which to "hit play" (time moved on so new data)
            baseWinPresenter.handleWindowRefresh();
        }
    }

}