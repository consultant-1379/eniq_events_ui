/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import com.ericsson.eniq.events.ui.client.common.ToolBarStateManager;

import java.util.*;

import static com.ericsson.eniq.events.ui.client.common.Constants.COMMA;
import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;

/**
 * Similar to MetaMenuItem except will not hold Id (and is not a menu item).
 * Used when query Id (winId)  is set at run time rather than being available
 * from metaData. This will server as a utility class to build up a
 * full MetaMenuItem object when id known.
 *
 * @author eeicmsy
 * @see com.ericsson.eniq.events.ui.client.common.MetaMenuItem
 * @since April 2010
 */
public class MetaMenuItemDataType {

    private static Map<String, Type> windowTypes;

    // reading of JSON file (i.e. in truth no real advantage to
    // creating an enum as will only be converting it from a string
    public enum Type {
        GRID, CHART, RANKING, CONFIG
    }

    /*
    * populated when searhc type must be ignore, e..g BSC,CELL
    */
    public final List<String> excludedSearchTypes;

    /**
     * Name displayed on menu item
     * (which will also use on button (not
     * adding extra string because assume can use the
     * same name for menu item and button (no elipses for example)
     */
    public final String text;

    /**
     * Item id
     * This is not final because it may be
     * required to set query id at runtime (e.g. for
     * KPI windows the id changes with different node selections
     */
    public String id;

    /** Restful web service associated with item */
    public/*final*/String url;

    /**
     * CSS icon which will repeat both on the menu item and
     * on the launch window button for this window in taskbar
     */
    public final String style;

    /**
     * Is window that is opened by this command
     * going to be interested in a change to the value in the search field
     * (there are windows which must change content when search field is
     * updated)
     */
    public/*final*/SearchFieldUser isSearchFieldUser = SearchFieldUser.FALSE;

    /**
     * Interested in empty search fields and full search field (empty search field
     * implies go to full network)
     * <p/>
     * Setting this is so rare (e.g.event volume graphs)leaving isSearchFieldUser boolean alone.
     * Assume isSearchFieldUser is set true in meta data when setting this.
     * If this property also set to TRUE, we are saying that even if the search field is not populated we will make a call:
     * <pre>
     * isEmptyAndFullSearchFieldUser is true : so "if search field IS populated then disregard above and behave
     * as though a "interested in search field user".
     */
    public boolean isEmptyAndFullSearchFieldUser;

    /**
     * Type of window to launch
     * from this menu item
     */
    public/*final*/Type windowType;

    /*
    * parameters you want to add after exising URL params, e.g. &groupname=Nokia
    */
    private String widgetSpecificParams;

    /*
    * Special case, 2 calls.  e.g. drilldown from dashboard to summary grid and
    * then when finished that do a further call to drill in further
    */
    private String forLaterWidgetSpecificParams;

    private String forLaterInfoWithURL;

    public final ToolBarStateManager toolBarHandler;

    /**
     * Type of display used to render the
     * server response i.e. grid, line, pie, bar
     */
    public/*final*/String display;

    /** the type parameter used by the services layer */
    public/*final*/String queryType;

    /** the key parameter used by the services layer */
    public/*final*/String queryKey;

    /**
     * flag to determine if this menuitem has the potential to
     * display different grid formats based on the provided parameters
     */
    public boolean hasMultiResult = false;

    /**
     * A string defining the max number of rows parameter to be sent to the RESTful services.
     * Currently this will hold the name of a JNDI parameter to be read, future implementations should
     * retrieve this value from user-specific storage.
     */
    public String maxRowsParam;

    /** Allowed to clear widgetSpecificParams (default true) */
    private boolean isAllowedClearWidgetSpecificParams = true;

    /**
     * only added when want time to be displayed but disabled (i.e. time combobox always a label)
     * TODO if ever require window to launch with no time, make an enum instead to handle no time
     */
    private final boolean isDisablingTime;

    /** only added when want time to be displayed but disabled (i.e. time combobox always a label)
      * the value is a fixed value to be set to the label instead of the default setting */
    private final String timeValue;

    /**
     * determine if the menuitem been launched has an overlay/wizard
     * to capture more information from the end user before a
     * final screen is displayed to the end user.
     */
    public String wizardID = EMPTY_STRING;

    private String minimizedButtonName;

    /**
     * determine if this menuitem has to be launched with data tiering delays
     */
    private boolean isDataTieredDelay = false;

//    /**
//     * determine if the grid should highlight a time difference between events.
//     * This is used in the Event analysis grid when showing the time gap between
//     * failed events.
//     */
//    private boolean isShowTimeGap = false;

    /**
     * Builder Pattern Implementation for MetaMenuItemDataType to allow classes using this type to set only the required
     * parameters of this type, and allow the remaining parameters to hold their default settings.
     *
     * @author ecarsea
     * @since 2011
     */
    @SuppressWarnings({ "hiding", "PMD.UnusedPrivateField" })
    public static class Builder {
        private String text = EMPTY_STRING;

        private String id = EMPTY_STRING;

        private String url = EMPTY_STRING;

        private String style = EMPTY_STRING;

        private SearchFieldUser isSearchFieldUser = SearchFieldUser.FALSE;

        private Type windowType = Type.GRID;

        private String display = EMPTY_STRING;

        private String type = EMPTY_STRING;

        private String key = EMPTY_STRING;

        private boolean multiResult;

        private boolean isEmptyAndFullSearchFieldUser;

        private String widgetSpecificParams = EMPTY_STRING;

        private String forLaterWidgetSpecificParams = EMPTY_STRING;

        private ToolBarStateManager toolBarHandler = new ToolBarStateManager(EMPTY_STRING,
                ToolBarStateManager.BottomToolbarType.PAGING, EMPTY_STRING);

        private String maxRowsParam = EMPTY_STRING;

        private String wizardID = EMPTY_STRING;

        private String minimizedButtonName = EMPTY_STRING;

        private boolean isDisablingTime;

        private String timeValue;

        private List<String> excludedSearchTypes = new ArrayList<String>();
        
        private boolean isDataTieredDelay;

        public Builder text(final String text) {
            this.text = text;
            return this;
        }

        public Builder id(final String id) {
            this.id = id;
            return this;
        }

        public Builder url(final String url) {
            this.url = url;
            return this;
        }

        public Builder style(final String style) {
            this.style = style;
            return this;
        }

        public Builder isSearchFieldUser(final SearchFieldUser isSearchFieldUser) {
            this.isSearchFieldUser = isSearchFieldUser;
            return this;
        }

        public Builder windowType(final Type windowType) {
            this.windowType = windowType;
            return this;
        }

        public Builder display(final String display) {
            this.display = display;
            return this;
        }

        public Builder type(final String type) {
            this.type = type;
            return this;
        }

        public Builder key(final String key) {
            this.key = key;
            return this;
        }

        public Builder multiResult(final boolean multiResult) {
            this.multiResult = multiResult;
            return this;
        }

        public Builder isEmptyAndFullSearchFieldUser(final boolean isEmptyAndFullSearchFieldUser) {
            this.isEmptyAndFullSearchFieldUser = isEmptyAndFullSearchFieldUser;
            return this;
        }

        public Builder widgetSpecificParams(final String widgetSpecificParams) {
            this.widgetSpecificParams = widgetSpecificParams;
            return this;
        }

        public Builder forLaterWidgetSpecificParams(final String forLaterWidgetSpecificParams) {
            this.forLaterWidgetSpecificParams = forLaterWidgetSpecificParams;
            return this;
        }

        public Builder forLaterInfoWithURL(final String forLaterInfoWithURL) {
            return this;
        }

        public Builder toolBarHandler(final ToolBarStateManager toolBarHandler) {
            this.toolBarHandler = toolBarHandler;
            return this;
        }

        public Builder maxRowsParam(final String maxRowsParam) {
            this.maxRowsParam = maxRowsParam;
            return this;
        }

        public Builder wizardID(final String wizardID) {
            this.wizardID = wizardID;
            return this;
        }

        public Builder minimizedButtonName(final String minimizedButtonName) {
            this.minimizedButtonName = minimizedButtonName;
            return this;
        }

        public Builder isDisablingTime(final boolean isDisablingTime) {
            this.isDisablingTime = isDisablingTime;
            return this;
        }

        public Builder timeValue(final String timeValue) {
            this.timeValue = timeValue;
            return this;
        }

        public Builder excludedSearchTypes(final String commaSeperatedExcludedSearchTypes) {
            this.excludedSearchTypes = commaSeperatedExcludedSearchTypes.isEmpty() ? new ArrayList<String>()
                    : new ArrayList<String>(Arrays.asList(commaSeperatedExcludedSearchTypes.split(COMMA)));
            return this;
        }
        
        public Builder isDataTieredDelay(final boolean isDataTieredDeplay){
            this.isDataTieredDelay = isDataTieredDeplay;
            return this;
        }


        public MetaMenuItemDataType build() {
            return new MetaMenuItemDataType(this);
        }
    }

    /** @param builder - builder of MetaMenuItemDataType */
    private MetaMenuItemDataType(final Builder builder) {
        this.text = builder.text;
        this.id = builder.id;
        this.url = builder.url;
        this.style = builder.style; // same style will be needed on launch button
        this.isSearchFieldUser = builder.isSearchFieldUser;
        this.windowType = builder.windowType;
        this.toolBarHandler = builder.toolBarHandler;
        this.display = builder.display;
        this.queryType = builder.type;
        this.queryKey = builder.key;
        this.hasMultiResult = builder.multiResult;
        this.isEmptyAndFullSearchFieldUser = builder.isEmptyAndFullSearchFieldUser;
        this.widgetSpecificParams = builder.widgetSpecificParams;
        this.forLaterWidgetSpecificParams = builder.forLaterWidgetSpecificParams;
        this.forLaterInfoWithURL = builder.forLaterWidgetSpecificParams;
        this.maxRowsParam = builder.maxRowsParam;
        this.wizardID = builder.wizardID;
        this.minimizedButtonName = builder.minimizedButtonName;
        this.isDisablingTime = builder.isDisablingTime;
        this.timeValue = builder.timeValue;
        this.excludedSearchTypes = builder.excludedSearchTypes;
        this.isDataTieredDelay = builder.isDataTieredDelay;
    }

    public String getWidgetSpecificParams() {
        return widgetSpecificParams;
    }

    public String getForLaterWidgetSpecificParams() {
        return forLaterWidgetSpecificParams;
    }

    /**
     * Return text to appear on mimimised button on menu taskbar which will
     * also use on title bar.
     * <p/>
     * Generally the menu item name that created the window,
     * else special name set for button
     * in meta data tabMenuItems build up, ref MIN_MENU_NAME_ON_TASKBAR
     *
     * @return text to put on mimised button in taskbar and title bar
     */
    public String getTaskBarButtonAndInitialTitleBarName() {
        if (minimizedButtonName == null || minimizedButtonName.isEmpty()) {
            minimizedButtonName = this.text;
        }
        return minimizedButtonName;
    }

    /**
     * Specifically set up this MetaMenuItemDataType not to allow
     * emptying of widgetSpecificParams
     * <p/>
     * TODO isAllowedClearWidgetSpecificParams is not a constructed (via metadata) arg
     * -it could be in future if see an need
     *
     * @param isAllowedClearWidgetSpecificParams
     *         false to stop clearing widgetSpecificParams
     *
     * @see #setWidgetSpecificParams
     */
    public void setAllowedClearWidgetSpecificParams(final boolean isAllowedClearWidgetSpecificParams) {
        this.isAllowedClearWidgetSpecificParams = isAllowedClearWidgetSpecificParams;
    }

    public void setForLaterWidgetSpecificParams(final String drillDownWindowType, final String laterWidgetSpecificParams) {
        this.forLaterWidgetSpecificParams = laterWidgetSpecificParams;
        this.forLaterInfoWithURL = drillDownWindowType;
    }

    public String getForLaterInfoWithURL() {
        return forLaterInfoWithURL;
    }

    /**
     * Set widgetSpecificParams in MetaMenuItemDataType
     *
     * @param widgetSpecificParams - url parameters to update
     *
     * @return true if updating widgetSpecificParams, false if not allowed
     *         set widgetSpecificParams to empty and widgetSpecificParams is empty
     */
    public boolean setWidgetSpecificParams(final String widgetSpecificParams) {

        /*
        * By default can clear widgetSpecificParams, but
        * in some cases will want to stop this from happening
        */
        if (!isAllowedClearWidgetSpecificParams && widgetSpecificParams.length() == 0) {
            return false;
        }
        this.widgetSpecificParams = widgetSpecificParams;
        return true;
    }

    /**
     * Unusual window which wants time displayed but wants it disabled
     *
     * @return true if want time combox to be a label
     */
    public boolean isDisablingTime() {
        return isDisablingTime;
    }

    /**
     * Unusual window which wants time displayed but wants it disabled and the fixed time should be used instead of the default.
     * @return    String the time value to be set to a label when isDisablingTime is true  
     */
    public String getTimeValue() {
        return timeValue;
    }

    /**
     * Set id at run time (multi-instance windows (KPI) with dynamic id
     *
     * @param id run time generated id which is used as window id
     *           Normally shared as query id
     */
    public void setWinId(final String id) {
        this.id = id;
    }
    
    public boolean isDataTiering() {
        return isDataTieredDelay;
    }

    public static Type convertType(final String windowTypeStr) {
        if (windowTypes == null) {
            windowTypes = new HashMap<String, Type>();

            windowTypes.put("GRID", Type.GRID);
            windowTypes.put("CHART", Type.CHART);
            windowTypes.put("RANKING", Type.RANKING);
            windowTypes.put("CONFIG", Type.CONFIG);

        }
        final Type returnVal = windowTypes.get(windowTypeStr);
        return (returnVal == null) ? Type.GRID : returnVal; // default grid if not set
    }

    @Override
    public String toString() {
        final String toolBarHandlerStr = String.valueOf(toolBarHandler);
        final String urlStr = String.valueOf(url);
        final String idStr = String.valueOf(id);
        final StringBuilder sb = new StringBuilder(587 + toolBarHandlerStr.length() + urlStr.length() + idStr.length());
        sb.append("MetaMenuItemDataType");
        sb.append("{text='").append(text).append('\'');
        sb.append(", id='").append(idStr).append('\'');
        sb.append(", excludedSearchTypes=").append(excludedSearchTypes);
        sb.append(", url='").append(urlStr).append('\'');
        sb.append(", style='").append(style).append('\'');
        sb.append(", isSearchFieldUser=").append(isSearchFieldUser);
        sb.append(", isEmptyAndFullSearchFieldUser=").append(isEmptyAndFullSearchFieldUser);
        sb.append(", windowType=").append(windowType);
        sb.append(", widgetSpecificParams='").append(widgetSpecificParams).append('\'');
        sb.append(", forLaterWidgetSpecificParams='").append(forLaterWidgetSpecificParams).append('\'');
        sb.append(", forLaterInfoWithURL='").append(forLaterInfoWithURL).append('\'');
        sb.append(", toolBarHandler=").append(toolBarHandlerStr);
        sb.append(", display='").append(display).append('\'');
        sb.append(", queryType='").append(queryType).append('\'');
        sb.append(", queryKey='").append(queryKey).append('\'');
        sb.append(", hasMultiResult=").append(hasMultiResult);
        sb.append(", maxRowsParam='").append(maxRowsParam).append('\'');
        sb.append(", isAllowedClearWidgetSpecificParams=").append(isAllowedClearWidgetSpecificParams);
        sb.append(", isDataTieredDelay=").append(isDataTieredDelay);
        sb.append(", isDisablingTime=").append(isDisablingTime);
        sb.append(", timeValue=").append(timeValue);
        sb.append(", wizardID='").append(wizardID).append('\'');
        sb.append(", minimizedButtonName='").append(minimizedButtonName).append('\'');
//        sb.append(", isShowTimeGap='").append(isShowTimeGap).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
