/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.common.widget;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.ericsson.eniq.events.common.client.datatype.ColumnInfoDataType;
import com.ericsson.eniq.events.common.client.datatype.GridInfoDataType;
import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.MetaReaderConstants;
import com.ericsson.eniq.events.ui.client.common.comp.BaseToolBar;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.ericsson.eniq.events.ui.client.events.WidgetSpecificParamsChangeEvent;
import com.ericsson.eniq.events.ui.client.main.AbstractWindowLauncher;
import com.ericsson.eniq.events.ui.client.main.GridLauncher;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.ui.client.search.ISubmitSearchHandler;
import com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Aid launching Recurring Errors window. Launched from WindowToolbarItemEvent
 * button press. URL Widget parameters for the new window taken from column
 * record (row) of parent grid
 *
 * - Each time user changes row selection on parent grid and presses recur
 * button, recur window must update using the parent row selected parameters
 * (widget parameters) - When total search field changes widget parameters must
 * be kept for the new call required
 *
 * Avoiding being a SearchFieldValueResetEventHandler directly to avoid two
 * update calls when search field updates (when search field type changes need
 * to change the window - but don't want other search handling from event bus
 * making us crash when doing that)
 */
public class RecurErrorWindowHelper implements ISubmitSearchHandler {

    private final static Logger LOGGER = Logger.getLogger(RecurErrorWindowHelper.class.getName());

    private final IMetaReader metaReader = MainEntryPoint.getInjector().getMetaReader();

    private final static class ColumnsNotFoundException extends Exception {
    };

    /*
     * Meta Data for button (so to support button with different URLs will need
     * one RecurErrorWindowHelper per Recur Error button)
     */
    private final MetaMenuItemDataType recurErrWinData;

    /*
     * Column header and associated query parameter information from JsonObjectWrapper,
     * e.g. header and query parameter mapping: { "id" : "Cause Protocol Type",
     * "queryParamName": "causeProtTypeHeader" }
     */
    private Map<String, String> recurErrHeadersQueryParamsMap;

    /*
     * parent window reference ( event presenter grid) where the recur button was
     * pressed
     */
    private IEventGridView viewRef;

    private final EventBus eventBus;

    /*
     * ID of recur button (which will be different for each type of URL the button
     * must use), i.e. support using the same button with dufferent URLS on
     * difffent tabs
     */
    private final EventType eventID;

    /*
     * Reference to launched Recur Error window owned by the menu task bar in this
     * tab (one of these per this class).
     */
    private BaseWindow recurErrWindow;

    /*
     * Reference to menu task bar (for tab) which "owns" window
     */
    final IWorkspaceController workspaceController;

    /*
     * Store reference to current data because 1) Can never trust
     * menuTaskBar.getSearchComponentValue() (as user might not have hit the play
     * button 2) viewRef.getPresenter().getSearchData() can be old data from time
     * class was constructer
     */
    /* exposed junit */
    SearchFieldDataType currentSearchData;

    /*
     * Widget specific parameters from row Won't change after button press. Note
     * NOT to include search data
     */
    private String widgetSpecificParamsFromRow;

    /*
     * optional columns grid may have to make query (but not always there)
     */
    int numberOfOptionalCols;

    /**
     * Construct helper for Recurring Error window launch. There should be one of
     * these per eventID (i.e. one evenrID per button use (Network Tab or IMSI
     * tab, etc)
     *
     * @param viewRef
     *          parent grid view reference
     * @param eventID
     *          button event id (e.g. network tab button, subscriber tab button)
     * @param eBus
     *          the event bus
     */
    public RecurErrorWindowHelper(final IEventGridView viewRef, final EventType eventID, final EventBus eBus) {
        this.viewRef = viewRef;
        this.eventBus = eBus;
        this.eventID = eventID;

        // once per class to support different URLs for button
        recurErrWinData = this.createRecurErrMetaMenuItemDataType();
        recurErrWinData.setAllowedClearWidgetSpecificParams(false);
        workspaceController = viewRef.getWorkspaceController();

        /*
         * take direct feed from search component - avoid adding ourselves as an
         * owner window of MenuTaskabr being updated with as a rest of
         * SearchFieldValueResetEventHandlers as need our own distint handling
         */
        workspaceController.addSubmitSearchHandler(this);

    }

    /**
     * Display recurring failed events picking up parameters from the selected
     * row. User changes row selection in parent grid and presses "Recur Error"
     * button should result in new server call to change or launch new Recur error
     * window
     *
     * @param record
     *          - row selected information. Assumed record is not null.
     * @param launchingGridView
     *          - latest parent view information (extra safety to have up to date
     *          view reference, though listening to search field updates anyway -
     *          the window may have been launched without using the searhc field -
     *          i.e a window launched from grid)
     */
    public void displayRecurringFailedEventsWindow(final ModelData record, final IEventGridView launchingGridView) {

        this.viewRef = launchingGridView;

        /*
         * Search data can come from window itself (launched windows from an IMSI
         * hyperlink for example - search field component can be empty completely)
         */
        this.currentSearchData = viewRef.getPresenter().getSearchData();
        currentSearchData.clean();

        if (!replaceWidgetSpecificParamsFromRow(record, viewRef.getColumns())) {
            return;
        }

        recurErrWindow = getWindow(recurErrWinData.id);

        if (recurErrWindow == null) {
            launchNewRecurErrorWindow();
        } else {
            handleChangeExistingWindow(false);
        }
    }

    /*
     * Replace widget specific parameters for out-bound call Small validation
     * check here in case button enabled in error
     *
     * @param record select row record
     *
     * @param columnsMetaData column header information no row
     *
     * @return true if replaced widgetSpecificParamsFromRow
     */
    private boolean replaceWidgetSpecificParamsFromRow(final ModelData record, final GridInfoDataType columnsMetaData) {

        widgetSpecificParamsFromRow = null;

        try {
            widgetSpecificParamsFromRow = getWidgetSpecificParams(record, columnsMetaData,
                    getRecurErrHeadersQueryParamsMap());

        } catch (final ColumnsNotFoundException e) {
            // button should have been disabled so this should not occur
            LOGGER.warning("No action for Recurring Button press : insuffient column information");
            return false;
        }

        return true;
    }

    @Override
    public void submitSearchFieldInfo() {

        /*
         * Actually in no win here because event analysis windows can be launched
         * without using the search field at all - so consistent behaviour is
         * difficult
         */

        recurErrWindow = getWindow(recurErrWinData.id); // redefine (for launch
                                                        // button component parent
                                                        // issues)
        if (recurErrWindow != null) {
            // always called from search component update
            final SearchFieldDataType data = workspaceController.getSearchComponentValue(recurErrWindow
                    .getBaseWindowID());

            if (this.isSearchFieldDataTypeChanged(data)) {

                /*
                 * can not handle search type change as will not have extra headers that
                 * could be in new grid - we can either ignore the window or kill it
                 */
                killRecurErrWindow();

            } else {
                /* force current widget data to get updated with the new search data */
                // picking up fresh search data into widget for row if that is the case

                this.currentSearchData = data;
                handleChangeExistingWindow(true);

            }
            /* purposely set twice in this method */
            this.currentSearchData = data;
        }

    }

    /*
     * Launch new Recur Error Window for this tab (one eventID and URL per this
     * class)
     */
    private void launchNewRecurErrorWindow() {

        recurErrWinData.setWidgetSpecificParams(widgetSpecificParamsFromRow);
        final MetaMenuItem recurrErrMenuItem = new MetaMenuItem(recurErrWinData);

        // always be prepared URL to change (for button in different tabs - and
        // undoing breadcrumb)
        final String wsURL = getRecurringErrSummaryWebServiceURL();

        recurrErrMenuItem.setWsURL(wsURL);

        final ContentPanel constrainArea = viewRef.getParentWindow().getConstraintArea();

        final AbstractWindowLauncher launcher = createGridLauncher(recurrErrMenuItem, constrainArea);

        launcher.launchWindowWithPresetSearchData(currentSearchData, viewRef.getTimeData(), true);
    }

    /*
     * Existing Recur Window is launched. Pickup 1) Row selection change (widget
     * params) from parent grid and button press 2) Entire search field change
     *
     * @param neverRelaunchWindow true to avoid a window toggle iissue (@see
     * BaseWinWidgetSpecificParamsChangeHandler) when we know search field update
     * will be handling all for us
     */
    private void handleChangeExistingWindow(final boolean neverRelaunchWindow) {

        if (widgetSpecificParamsFromRow != null && currentSearchData != null) {

            final StringBuilder widgetParamsBuff = new StringBuilder();

            // picking up fresh search data into widget for row if that is the case
            widgetParamsBuff.append(currentSearchData.getSearchFieldURLParams(false));

            widgetParamsBuff.append(widgetSpecificParamsFromRow);

            final String wsURL = getRecurringErrSummaryWebServiceURL();

            if (recurErrWindow instanceof IEventGridView) {
                final BaseToolBar windowToolbar = ((IEventGridView) recurErrWindow).getWindowToolbar();
                cleanUpBreadCrumbMenu(windowToolbar);
            }

            eventBus.fireEvent(new WidgetSpecificParamsChangeEvent(workspaceController.getTabOwnerId(),
                    recurErrWinData.id, widgetParamsBuff.toString(), wsURL, currentSearchData, neverRelaunchWindow));

            recurErrWindow.toFront();
        }
    }

    private void cleanUpBreadCrumbMenu(final BaseToolBar winToolBar) {

        final Button btnBreadCrumb = (Button) winToolBar.getItemByItemId(MetaReaderConstants.BTN_NAV);
        if (btnBreadCrumb != null) {
            final Component item = btnBreadCrumb.getMenu().getItem(0);
            if (item != null) {
                selectBreadCrumbItem(item);
                removeAllButFirstFromBreadCrumbMenu((Menu) item.getParent());
            }
        }
    }

    private void removeAllButFirstFromBreadCrumbMenu(final Menu menu) {
        if (menu != null) {
            for (int i = 1; i < menu.getItemCount(); i++) {
                menu.remove(menu.getItem(i));
            }
        }
    }

    private void selectBreadCrumbItem(final Component item) {
        new NavigationHelper((IEventGridView) recurErrWindow, eventBus).handleBreadcrumb(item);
    }

    /*
     * Kill the recur error window entirely
     */
    private void killRecurErrWindow() {
        if (recurErrWindow != null) {
            ((AbstractBaseWindowDisplay) recurErrWindow).hide();
        }

    }

    /*
     * Lazy instantiate Map (once) Return column header and associated query
     * parameter information from JsonObjectWrapper, e.g. header and query parameter
     * mapping: { "id" : "Cause Protocol Type", "queryParamName":
     * "causeProtTypeHeader" }
     *
     * @return Mapping of column header to query parameter
     */
    Map<String, String> getRecurErrHeadersQueryParamsMap() {
        if (recurErrHeadersQueryParamsMap == null) {
            recurErrHeadersQueryParamsMap = getRecurErrHeadersParameters(true);

            /* late change */
            numberOfOptionalCols = recurErrHeadersQueryParamsMap.size() - getRecurErrHeadersParameters(false).size();
        }
        return recurErrHeadersQueryParamsMap;
    }

    /*
     * Column checks are largely redundant if button enabling does its job
     *
     * Fetch widget specific parameters from selected row Trust that the enabling
     * code for recur error button is working Not checking column counts here as
     * we have option columns (IMSI) that may not be present (not present for IMSI
     * grid itself - present for every thing else (PTMSI) group
     *
     *
     * @param record - current row selected on parent grid
     *
     * @param columnsMetaData
     *
     * @param headersQueryParams
     *
     * @return empty string if no columns found in grid to match desired headers
     */
    private String getWidgetSpecificParams(final ModelData record, final GridInfoDataType columnsMetaData,
            final Map<String, String> headersQueryParams) throws ColumnsNotFoundException {

        final StringBuilder wigetParamsBuff = new StringBuilder();
        final Set<String> headers = headersQueryParams.keySet();

        int foundCount = 0;
        for (final ColumnInfoDataType element : columnsMetaData.columnInfo) {

            final String colName = element.columnHeader;

            if (headers.contains(colName)) {
                foundCount++;
                wigetParamsBuff.append(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR);
                wigetParamsBuff.append(headersQueryParams.get(colName)); // queryParam
                wigetParamsBuff.append(EQUAL_STRING);
                wigetParamsBuff.append(record.get(element.columnID));
            }

        }
        final boolean allFound = (headers.size() == foundCount);
        final boolean enoughFound = ((headers.size() - numberOfOptionalCols) == foundCount);

        if (!(allFound || enoughFound)) {
            throw new ColumnsNotFoundException();
        }

        return wigetParamsBuff.toString();
    }

    /*
     * Unfortunately suddenly decided that PTMSI will want to pass IMSI, which we
     * will only have from a row from a PTMSI Event Analysis grid.
     *
     * Meaning that if we wish to populate an recur error window following a
     * search field data change (and we have switched search field type), we will
     * not be able to automatically populate currently open recurring window User
     * has to click on a row in the (new) parent window again
     *
     * @param data new search field data entered (call method before resetting
     * cached currentSearchData
     */
    private boolean isSearchFieldDataTypeChanged(final SearchFieldDataType data) {
        if (currentSearchData == null) {
            return false;
        }

        final boolean currentGroup = currentSearchData.isGroupMode();
        final boolean newDataIsGroup = data.isGroupMode();

        if (currentGroup == newDataIsGroup) {

            // e.g. IMSI or PTMSI or Group
            final String curentType = currentSearchData.getType();
            final String newType = data.getType();

            if (curentType == null && newType == null) {
                return false;
            }
            if (curentType == null || newType == null) {
                return true;
            }
            return (!curentType.equals(newType));
        }
        return true;

    }

    // //////////////// junit extractions

    BaseWindow getWindow(final String recurErrWinDataId) {
        return workspaceController.getWindow(recurErrWinDataId);
    }

    /* extract junit */
    Map<String, String> getRecurErrHeadersParameters(final boolean includeOptional) {
        return metaReader.getRecurErrHeadersParameters(includeOptional);
    }

    /*
     * GEt URL for server call
     *
     * @param eventID button event id (different if want different URLs when use
     * button across different tabs
     *
     * @return URI for recur error window call
     */
    String getRecurringErrSummaryWebServiceURL() {
        return metaReader.getRecurringErrSummaryWebServiceURL(eventID);

    }

    /* extracted for junit */
    MetaMenuItemDataType createRecurErrMetaMenuItemDataType() {
        return metaReader.getRecurErrMetaMenuItemDataType();

    }

    /* create a new instance of a gridLauncher class */
    AbstractWindowLauncher createGridLauncher(final MetaMenuItem item, final ContentPanel containingPanel) {
        return new GridLauncher(item, eventBus, containingPanel, workspaceController);
    }
}
