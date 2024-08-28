/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

import java.util.List;

import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldUser;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;

/**
 * Generic menu items fed from JsonObjectWrapper readup. 
 * Links windows and taskbar docking buttons (and query id) via Id parameter.
 * <p/>
 * This is a menu item specifically linked to information for windows launched from
 * the main menu ("Start menu") on the main taskbar in the tab
 * (as opposed to windows launched from grid windows - though obviously the
 * same internal MetaMenuItemDataType is used for these windows).
 *
 * @author eeicmsy
 * @since Feb 2010
 */
public class MetaMenuItem extends MenuItem {

    /** flag to determine if this menu item refers to a window that has been launched from a cell hyperlink */
    private boolean launchedFromCellHyperlink = false;

    // TODO: variabled -> variable
    /*
    * Whole data content
    * hold all data variables inside MetaMenuItemDataType (not this class),
    * i.e. one copy of variabled (not two)
    */
    private final MetaMenuItemDataType data;

    // TODO: exising -> existing
    /*
    * Extracting toolbar handling to support applying exising
    * state to a copy of MetaMenuItem (to preserve temporary settings of toolbar)
    */
    private final ToolBarStateManager toolBarHandler;

    /**
     * Constructor constructing MetaMenuItem from a MetaMenuItemDataType
     *
     * @param data MetaMenuItemDataType containing all data (but is not a menuItem)
     */
    public MetaMenuItem(final MetaMenuItemDataType data) {
        this(data, false);
    }

    /**
     * For copy construct which sets if launched from hyperlink too
     *
     * @param item copy
     */
    public MetaMenuItem(final MetaMenuItem item) {
        this(item.data, item.launchedFromCellHyperlink);
    }

    /*
    * private construct (to chain constructors)
    * @param data
    * @param launchedFromCellHyperlink
    */
    private MetaMenuItem(final MetaMenuItemDataType data, final boolean launchedFromCellHyperlink) {

        super(data.text);
        this.data = data;

        this.toolBarHandler = data.toolBarHandler;

        if (data.style.length() > 0) {
            setIconStyle(data.style);
        }
        setItemId(data.id);
        setId(data.id);

        reset(); // ensure construct (and copy) always sets initial toolbar states

        this.launchedFromCellHyperlink = launchedFromCellHyperlink;
    }

    /**
     * This returns the list of Excluded Search Types.
     *
     * @return
     */
    public List<String> getExcludedSearchTypes() {
        return data.excludedSearchTypes;
    }

    /**
     * (should be same return as {@link #getId()}
     *
     * @return meta menu id, identifying query, etc
     *
     * @see com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType
     */
    public String getID() {
        return data.id;
    }

    // TODO: elipses -> ellipses

    /**
     * @return Name displayed on menu item
     *         (which will also use on button (not
     *         adding extra string because assume can use the
     *         same name for menu item and button (no elipses for example)
     *
     * @see com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType
     */
    @Override
    public String getText() {
        return data.text;
    }

    //TODO: mimimsed -> minimised

    /**
     * Usually returns same as #getText
     * unless a special tag  ref metaData constant MIN_MENU_NAME_ON_TASKBAR
     *
     * @return text to appear on mimimsed button (and temp title bar on window)
     */
    public String getTaskBarButtonAndInitialTitleBarName() {
        return data.getTaskBarButtonAndInitialTitleBarName();
    }

    /**
     * Unusual window which wants time displayed but wants it disabled and the fixed time should be used instead of the default.
     * @return    String the time value to be set to a label when isDisablingTime is true  
     */
    public String getTimeValue() {
        return data.getTimeValue();
    }

    // TODO: combox -> combo box

    /**
     * Unusual window which wants time displayed but wants it disabled
     *
     * @return true if want time combox to be a label
     */
    public boolean isDisablingTime() {
        return data.isDisablingTime();
    }

    /**
     * @return Restful web service associated with item
     *
     * @see com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType
     */
    public String getWsURL() {
        return data.url;
    }

    // TODO: assocuated -> associated

    /**
     * Set Restful web service associated with item
     *
     * @param url - web service url assocuated with item
     *
     * @see com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType
     */
    public void setWsURL(final String url) {
        data.url = url;
    }

    /**
     * @return Type of display used to render the
     *         server response i.e. grid, line, pie, bar
     *
     * @see com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType
     */
    public String getDisplay() {
        return data.display;
    }

    public void setDisplay(final String display) {
        data.display = display;
    }

    /**
     * @return - CSS icon which will repeat both on the menu item and
     *         on the launch window button for this window in taskbar
     *
     * @see com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType
     */
    public String getStyle() {
        return data.style;
    }

    // TODO: purposly -> purposely

    /**
     * NOTE: You can not trust this (at moment) when drilldown on grids is involved,
     * as it is purposly (bad), changed at run time for that functionality to work.
     * <p/>
     * Use InstanceWindowType.isInstanceWindowType with base window id when
     * want a "stable" return instead.
     *
     * @return needsParam from metadata json. Does window need search field input.
     *
     * @see com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType
     */
    public boolean isSearchFieldUser() {
        return (data.isSearchFieldUser != SearchFieldUser.FALSE);
    }

    public SearchFieldUser getSearchFieldUser() {
        return data.isSearchFieldUser;
    }

    public void setSearchFieldUser(final SearchFieldUser needsParam) {
        data.isSearchFieldUser = needsParam;
    }

    /**
     * @return flag that determines if this menu item has the potential to
     *         display a different result set based on the provided parameter
     *         i.e. the columns of the result set can vary and therefore the metadata
     *         for the grid that is displayed needs to differ
     *
     * @see com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType
     */
    public boolean hasMultiResult() {
        return data.hasMultiResult;
    }

    /**
     * @return true if both read search field and not reading search field
     *         (e.g. used when Event Volumne and Network Event Volumne opening
     *         depended on wheather search field populated)
     *
     * @see com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType
     */
    public boolean isEmptyAndFullSearchFieldUser() {
        return data.isEmptyAndFullSearchFieldUser;
    }

    /**
     * @return String value need by services layer
     *         value is read from metadata and passed to services layer as
     *         param on the url
     */
    public String getQueryType() {
        return data.queryType;
    }

    public void setQueryType(final String queryType) {
        data.queryType = queryType;
    }

    /**
     * String value need by services layer
     * value is read from metadata and passed to services layer as
     * param on the url. determines if the query is a summary, failure, successes etc
     */
    public String getQueryKey() {
        return data.queryKey;
    }

    public void setQueryKey(final String queryKey) {
        data.queryKey = queryKey;
    }

    /**
     * @return Type of window to launch
     *         from this menu item
     *
     * @see com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType
     */
    public MetaMenuItemDataType.Type getWindowType() {
        return data.windowType;
    }

    public void setWindowType(final MetaMenuItemDataType.Type windowType) {
        data.windowType = windowType;
    }

    // todo: exising -> existing

    /** Return parameters you want to add after exising URL params, e.g. &groupname=Nokia */
    public String getWidgetSpecificParams() {
        return data.getWidgetSpecificParams();
    }

    /**
     * Pecular case of drilling from chart in dashbaord to launch a new window in different tab.
     * New window (existing menu item) launching in drilldowned state - preserving history with two calls.
     * Assumes one level of drillonly
     *
     * @param drillDownWindowType  An id that exists in drilldown windows sections (containing URL to call,
     *                             and also some grid params but these are useless as not launching from grid
     * @param widgetSpecificParams parameters to make up what we are missing by not doing drilldown from grid (e.g. &eventId=2)
     */
    public void setForLaterWidgetSpecificParams(final String drillDownWindowType, final String widgetSpecificParams) {
        data.setForLaterWidgetSpecificParams(drillDownWindowType, widgetSpecificParams);
    }

    public String getForLaterWidgetSpecificParams() {
        return data.getForLaterWidgetSpecificParams();
    }

    public String getForLaterInfoWithURL() {
        return data.getForLaterInfoWithURL();
    }

    /**
     * Setter (via MetaMenuItemDataType so not out of synch)
     *
     * @param widgetSpecificParams parameters you want to add after exising URL params, e.g. &groupname=Nokia
     */
    public boolean setWidgetSpecificParams(final String widgetSpecificParams) {
        return data.setWidgetSpecificParams(widgetSpecificParams);
    }

    public void setLaunchedFromCellHyperlink(final boolean launchedFromCellHyperlink) {
        this.launchedFromCellHyperlink = launchedFromCellHyperlink;
    }

    public boolean isLaunchedFromCellHyperlink() {
        return launchedFromCellHyperlink;
    }

    /**
     * Reset toggle toolbar type temporarily to support
     * toggling for chart drill-down. Set to null to switch back
     * to regular toolbar-toggle toolbar
     *
     * @param tempToggleToolBar reset to null when finished
     */
    public void setTempToggleToolBarType(final String tempToggleToolBar) {
        toolBarHandler.setTempToggleToolBarType(tempToggleToolBar);

    }

    /**
     * Use when preserving state with MetaMenuItem on window
     * toggle only. Toggle window type between a chart and a grid
     * (assume not calling toggle for ranking grids
     */
    public void toggleWindowType() {
        if (data.windowType == MetaMenuItemDataType.Type.GRID) {
            setWindowType(MetaMenuItemDataType.Type.CHART);
        } else {
            setWindowType(MetaMenuItemDataType.Type.GRID);
        }
    }

    /**
     * Utility to toggle upper toolbar type associated with the meta item
     * when switching from a graph to a grid (only for use when have
     * established the window is being toggled - i.e. has a preset response
     * from a previous call)
     */
    public void toggleToolBarType() {
        toolBarHandler.toggleToolBarType();
    }

    /**
     * BOTTOM (footer) toolbar utility for toggling between graph and grid
     * Because we have introduces a different type of non-paging bottom
     * toolbar and we use this for charts, it will be necessary to be able to
     * toggle bottom toolbars when converting from graph to grid (though
     * in near all cases can assume toggling from PAGING toolbar to PLAIN toolbar
     */
    public void toggleBottomToolBarType() {
        toolBarHandler.toggleBottomToolBarType();
    }

    /**
     * Do not access bottomToolbarType directly to support toggling
     * between the two types (assumes never going to be more for bottom tool bar,
     * else would need similar support to that provided for top toolbar)
     */
    public ToolBarStateManager.BottomToolbarType getCurrentBottomToolBarType() {
        return toolBarHandler.getCurrentBottomToolBarType();
    }

    // todo: initiailised -> initialised

    /**
     * Do not access toolBarType variable directly.
     * Use this method instead to ensure that
     * toggle toolbar functionality can be maintained
     * (when change from a chart to a grid and vice version
     * the window should be initiailised with a different toolbar
     *
     * @return toolbar type to apply
     */
    public String getCurrentToolBarType() {
        return toolBarHandler.getCurrentToolBarType();

    }

    public void setCurrentToolBarType(final String currentToolBar) {
        toolBarHandler.setCurrentToolBarType(currentToolBar);
    }

    /**
     * returns the value held in the MetaMenuItemdataType
     * for the wizardID
     */
    public String getWizardId() {
        return data.wizardID;
    }

    /**
     * Sets the value held in the MetaMenuItemdataType
     * for the wizardID
     */
    public void setWizard(final String value) {
        data.wizardID = value;
    }

    /**
     * Clears the dataType of the wizard information that
     * was provided to ensure that the original grid
     * can be launched
     */
    public void clearWizardInfoToGrid() {
        data.wizardID = EMPTY_STRING;
        data.display = MetaMenuItemDataType.Type.GRID.toString();
        data.windowType = MetaMenuItemDataType.Type.GRID;
    }

    // todo: intial -> initial
    /*
    * Ensure new windows launch with intial toolbars
    */
    public final void reset() {
        if (toolBarHandler != null) {
            toolBarHandler.reset();
        }
    }

    /** @param maxRowsParam The string representing the JNDI key for the max number of rows for this item */
    public void setMaxRowsParam(final String maxRowsParam) {
        data.maxRowsParam = maxRowsParam;
    }

    /** @return The string representing the JNDI key for the max number of rows for this item */
    public String getMaxRowsParam() {
        return data.maxRowsParam;
    }

    public boolean getDataTieredDelayParam() {
        return data.isDataTiering();
    }

    /**
     * This method checks if the searchFieldType is excluded.
     *
     * @param searchFieldType
     *
     * @return true if the searchFieldType is excluded.
     */
    public boolean isExcludedSearchType(final String searchFieldType) {
        return this.getExcludedSearchTypes().contains(searchFieldType);
    }

    /**
     * If there is a sub menu for a menu item - no need to process click.
     *
     * @param be event
     */
    @Override
    protected void onClick(final ComponentEvent be) {
        if (getSubMenu() == null) {
            super.onClick(be);
        }
    }

    @Override
    public String toString() {
        final String handlerStr = String.valueOf(toolBarHandler);
        final String dataStr = String.valueOf(data);
        final StringBuilder sb = new StringBuilder(72 + dataStr.length() + handlerStr.length());
        sb.append("MetaMenuItem");
        sb.append("{launchedFromCellHyperlink=").append(launchedFromCellHyperlink);
        sb.append(", data=").append(dataStr);
        sb.append(", toolBarHandler=").append(handlerStr);
        sb.append('}');
        return sb.toString();
    }
}
