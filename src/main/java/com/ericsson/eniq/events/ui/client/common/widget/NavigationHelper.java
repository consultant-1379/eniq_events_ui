/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget;

import com.ericsson.eniq.events.ui.client.common.comp.BreadCrumbMenuItem;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.events.ButtonEnablingEvent;
import com.ericsson.eniq.events.ui.client.grid.JSONGrid;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.web.bindery.event.shared.EventBus;

import java.util.List;

import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;

/**
 * business logic to handle the end user events 
 * on the navigation buttons i.e. Back | Forward | Breadcrumb
 * 
 * @author eendmcm
 * @since  Apr 2010
 */
public class NavigationHelper {

    /*
     * A grid reference or a chart reference
     * (@see com.ericsson.eniq.events.ui.client.common.widget.IWidgetDisplay)
     */
    private final IExtendedWidgetDisplay viewRef;

    private final EventBus eventBus;

    /**
     * constructor - takes the widget that
     * this navigation will operate on
     * @param display
     */
    public NavigationHelper(final IExtendedWidgetDisplay display, final EventBus eventBus) {
        this.viewRef = display;
        this.eventBus = eventBus;
    }

    /**
     * handle the selection of a breadcrumb item
     * from the GUI navigation
     */
    public void handleBreadcrumb(final Component itemToDisplay) {

        final Menu breadCrumbMenu = getBreadCrumbMenu(itemToDisplay);
        final Component itemToSave = getItemCurrentlyDisplayed(breadCrumbMenu);

        update(itemToDisplay, breadCrumbMenu, itemToSave);
    }

    /*
     * gets the Menu that holds the MenuItem
     * extracted for junit
     */
    Menu getBreadCrumbMenu(final Component menuItem) {
        return (Menu) menuItem.getParent();
    }

    /**
     * handle the selection of the back
     * option from the GUI toolbar navigation
     */
    public void handleBack() {
        handleNavigation(-1);
    }

    /**
     * handle the selection of the Forward 
     * option from the GUI toolbar navigation
     */
    public void handleForward() {
        handleNavigation(+1);
    }

    /**
     * gets the widget that is been navigated away from and saves its info
     * gets the widget that is been displayed and initiates display to the GUI
     * @param menuOffset
     */
    private void handleNavigation(final int menuOffset) {

        final Menu breadCrumbMenu = getBreadCrumbMenu();
        final BreadCrumbMenuItem itemToSave = (breadCrumbMenu != null) ? (BreadCrumbMenuItem) getItemCurrentlyDisplayed(breadCrumbMenu)
                : null;
        final int saveItemNdx = itemToSave.getIndex();
        final Component itemToDisplay = getItemToDisplay(breadCrumbMenu, saveItemNdx + menuOffset);

        update(itemToDisplay, breadCrumbMenu, itemToSave);
    }

    private Menu getBreadCrumbMenu() {
        final Button btnBreadCrumb = viewRef.getWindowToolbar().getButtonByItemId(BTN_NAV);
        return (btnBreadCrumb != null) ? btnBreadCrumb.getMenu() : null;
    }

    private void update(final Component itemToDisplay, final Menu breadCrumbMenu, final Component itemToSave) {
        /* save the settings for the currently displayed widget */
        if (itemToSave instanceof BreadCrumbMenuItem) {
            saveItemConfigurations(breadCrumbMenu, itemToSave);
        }
        /* retrieve the settings for the requested widget */
        if (itemToDisplay instanceof BreadCrumbMenuItem) {

            final BreadCrumbMenuItem breadCrumbToDisplay = ((BreadCrumbMenuItem) itemToDisplay);

            final String displayURL = breadCrumbToDisplay.getURL();
            final String itemToSaveURL = ((BreadCrumbMenuItem) itemToSave).getURL();

            ((BreadCrumbMenuItem) itemToDisplay).setGridDisplayed(true);
            setBreadCrumbWindowTitleAndTime(breadCrumbMenu);

            final String gridTypeA = ((BreadCrumbMenuItem) itemToDisplay).getGridType();
            final String gridTypeB = ((BreadCrumbMenuItem) itemToSave).getGridType();

            final String gridTypeAId = ((BreadCrumbMenuItem) itemToDisplay).getId();
            final String gridTypeBId = ((BreadCrumbMenuItem) itemToSave).getId();
            
            final String gridTypeAWidgetParams = ((BreadCrumbMenuItem) itemToDisplay).getWidgetURLParameters();
            final String gridTypeBWidgetParams = ((BreadCrumbMenuItem) itemToSave).getWidgetURLParameters();

            if (gridTypeA.equals(gridTypeB)) {
                 if((!gridTypeAId.equals(gridTypeBId)) || (!gridTypeAWidgetParams.equals(gridTypeBWidgetParams)))
                    displayItem(itemToDisplay, !displayURL.equals(itemToSaveURL));
            } else {
                callChangeGridViewEvent((BreadCrumbMenuItem) itemToDisplay);
            }
        }
        checkEnableForNavBtns(breadCrumbMenu, itemToDisplay);
        checkToolbarBtns(itemToDisplay);
    }

    /*
     * Method extracted for junit test
     */
    JSONGrid getDisplayedGrid() {
        // we know viewRef is a grid as opposed to chart when this is called
        return (JSONGrid) ((IEventGridView) viewRef).getGridControl();
    }

    /*
     * exposed for junit
     * saves the component to a BreadCrumbMenuItem to use for later use
     */
    void saveItemConfigurations(final Menu menu, final Component item) {
        final JSONGrid eventGrid = getDisplayedGrid();
        final List<Filter> filters = eventGrid.getFilters().getFilterData();

        final BreadCrumbMenuItem currentBreadCrumb = (BreadCrumbMenuItem) item;
        currentBreadCrumb.saveGridConfigurations(eventGrid.getColumns(), eventGrid.getData(), filters, viewRef
                .getParentWindow().getBaseWindowTitle(), viewRef.getTimeData(), viewRef.getLastRefreshTimeStamp(),
                viewRef.getPresenter().getSearchData(), eventGrid.getStateId());

        menu.insert(currentBreadCrumb, currentBreadCrumb.getIndex());

        /*Update the list of hidden columns for this grid */
        currentBreadCrumb.saveHiddenColumns(eventGrid.getColumnModel().getColumns());
    }

    /*
     * gets grid data and configurations form the BreadCrumbMenuItem and displays it to the grid
     * (exposed for junit)
     */
    void displayItem(final Component item, final boolean urlChange) {
        final JSONGrid eventGrid = getDisplayedGrid();
        final BreadCrumbMenuItem breadCrumbMenuItem = (BreadCrumbMenuItem) item;
        eventGrid.setColumns(breadCrumbMenuItem.getGridInfoDataType());
        eventGrid.setData(breadCrumbMenuItem.getData());
        eventGrid.setupStateful(breadCrumbMenuItem.getText(),
                breadCrumbMenuItem.getGridInfoDataType() != null ? breadCrumbMenuItem.getGridInfoDataType().categoryId
                        : "");
        /* change time and label for bread crumb grid changes */
        viewRef.updateTime(breadCrumbMenuItem.getTimeData());
        viewRef.updateLastRefreshedTimeStamp(breadCrumbMenuItem.getLastRefreshedTimeStamp());
        viewRef.getTimeData().dataTimeFrom = breadCrumbMenuItem.dataTimeFrom;
        viewRef.getTimeData().dataTimeTo = breadCrumbMenuItem.dataTimeTo;

        eventGrid.bind();
        applyFilters(breadCrumbMenuItem.getFilters(), eventGrid);

        /* hide/show columns previously hidden/chosen by the end user */
        final List<ColumnConfig> columnConfList = eventGrid.getColumnModel().getColumns();
        for (final ColumnConfig columnConf : columnConfList) {
            final boolean isHidden = breadCrumbMenuItem.hiddenColumns.contains(columnConf.getId());
            columnConf.setHidden(isHidden);
        }
        //force the grid to recognise the (not) hidden status above
        eventGrid.reconfigureGrid();

        viewRef.addWidget(eventGrid.asWidget());
        viewRef.getPresenter().resetSearchData(breadCrumbMenuItem.getSearchData());

        if (urlChange) {

            /* e.g. pressed a kpi ratio hyperlink (KPI_RATIO link) and then naved back to event summarty grid, 
             * now if changed time later on we want to make sure we have the event summary grid url
             * for next call made on window
             * 
             */
            viewRef.getPresenter().setWsURL(breadCrumbMenuItem.getURL());

        }

    }

    /*
     * reapply existing filters to a grid that is being rebuilt 
     * */
    private void applyFilters(final List<Filter> filters, final JSONGrid eventGrid) {
        if (filters == null) {
            return;
        }

        for (final Filter exitingFilter : filters) {
            final Object objFilterVal = exitingFilter.getValue();
            eventGrid.getFilters().getFilter(exitingFilter.getDataIndex()).setValue(objFilterVal);
        }
    }

    /*
     * Returns the BreadCrumbMenuItem instance that stores the configuration details
     * of the grid that is currently displayed
     * extracted for junit
     */
    Component getItemCurrentlyDisplayed(final Menu menu) {
        Component item = null;
        for (int i = 0; i < menu.getItemCount(); i++) {
            if (((BreadCrumbMenuItem) menu.getItem(i)).isGridDisplayed()) {
                item = menu.getItem(i);
                break;
            }
        }
        return item;
    }

    /*
     * Searches the Navigation menu for the BreadCrumbMenuItem that was selected.
     */
    private Component getItemToDisplay(final Menu menu, final int menuItemSelected) {
        Component item = null;
        for (int i = 0; i < menu.getItemCount(); i++) {
            if (menuItemSelected == ((BreadCrumbMenuItem) menu.getItem(i)).getIndex()) {
                item = menu.getItem(i);
                break;
            }
        }
        return item;
    }

    /*
     * used on the forward and back navigation buttons to check
     * if they should be disabled. Assumed buttons exist when this method is called (no null checks)
     */
    private void checkEnableForNavBtns(final Menu menu, final Component item) {
        final BreadCrumbMenuItem breadCrumbMenuItem = (BreadCrumbMenuItem) item;
        final Component btnBack = getWindowToolbarButton(BTN_BACK);
        final Component btnForward = getWindowToolbarButton(BTN_FORWARD);

        if (btnBack == null || btnForward == null) {
            // btnBack And btnForward can be removed from metadata, but breadcrumb navigation can be there,
            // so check for nulls for that particular case
            return;
        }

        final int index = breadCrumbMenuItem.getIndex();
        btnBack.setEnabled(index != 0);
        if (menu != null) {
            final int i = menu.getItemCount() - 1;
            btnForward.setEnabled(index != i);
        }

    }

    /*
     * Currently used to check buttons on the window toolbar
     * to see if they should be enabled/disabled depending on what view is display
     * 
     *  Currently only have navigation on grids (@see EventGridPresenter)
    */
    private void checkToolbarBtns(final Component item) {
        final BreadCrumbMenuItem breadCrumbMenuItem = (BreadCrumbMenuItem) item;
        final JSONGrid eventGrid = getDisplayedGrid();

        eventBus.fireEvent(new ButtonEnablingEvent(breadCrumbMenuItem.getWinID(), eventGrid.getGridRowCount()));
    }

    /*
     * extracted for junit
     */
    Component getWindowToolbarButton(final String buttonID) {
        return viewRef.getWindowToolbar().getItemByItemId(buttonID);
    }

    /*
     * /*retrieve and set the title from the menuItem 
     * for the widget that is about to be be displayed */
    void setBreadCrumbWindowTitleAndTime(final Menu menu) {

        for (int i = 0; i < menu.getItemCount(); i++) {
            final BreadCrumbMenuItem item = (BreadCrumbMenuItem) menu.getItem(i);
            if (item.isGridDisplayed()) {
                final String sTitle = item.getWindowTitle();
                final TimeInfoDataType timeData = item.getTimeData();
                final String timeStamp = item.getLastRefreshedTimeStamp();
                final AbstractBaseWindowDisplay win = (AbstractBaseWindowDisplay) (/*(IEventGridView)*/viewRef)
                        .getParentWindow();
                /**
                 * Set the window category (Used for docking on taskbar). If we are going to a child (drill) window, use the grid id. if we are going to a primary or parent
                 * window, lets use the meta data id.  
                 */
                win.setWindowCategoryId(item.getWidgetSpecificInfo() == null ? viewRef.getViewSettings().getID() : item
                        .getGridInfoDataType().gridId);
                win.updateTitle(sTitle);
                win.getWindowToolbar().setTimeDisabled(item.getWidgetSpecificInfo() == null ? false : item
                        .getWidgetSpecificInfo().isDisablingTime());
                win.updateTime(timeData);
                win.updateLastRefreshedTimeStamp(timeStamp);
                break;
            }
        }
    }

    void callChangeGridViewEvent(final BreadCrumbMenuItem item) {
        item.setGridDisplayed(true);

        /* (removing eventBus ise of here) - see pick up by BaseWinChangeGridViewHandler for BaseWindowPresenter*/
        viewRef.getPresenter().handleGroupingGrid(item.getWindowTitle(), item.getGridInfoDataType(), null,
                item.getData(), item.getFilters(), getBreadCrumbMenu(item), item.getSearchData(), item.getURL(),
                item.getTimeData(), false, item.getWidgetSpecificInfo() != null);
    }
}
