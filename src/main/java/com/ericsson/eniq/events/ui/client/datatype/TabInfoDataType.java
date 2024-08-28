/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import java.util.HashMap;
import java.util.Map;

import com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.DefinedWorkspaceType;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WorkspaceState;

/**
 * DataType which can use to populate tabs from JsonObjectWrapper.
 *
 * @author eeicmsy
 * @author eavidat
 * @since Jul 2010 
 */
public class TabInfoDataType {

    private String id, tip, style, tabItemCenterStyle;

    private String name;

    private final boolean isRoleEnabled;

    private final boolean isModule;

    private final Map<String, String> parametersMap = new HashMap<String, String>(2);

    private boolean isPlusTab = false;

    private boolean isUserTab = false;

    private WorkspaceState workspaceState;

    public static final TabInfoDataType ALL_USER_TABS = new AllUserTabsInfoDataType();

    public DefinedWorkspaceType workspaceType;


    /**
     * Data class to store tab information for MetaReader
     * tab meta
     * @param id      - local independent reference to tab
     * @param name    -  title on tab header
     * @param tip     - tip on tab header
     * @param style   - style
     * @param tabItemCenterStyle - tab item center style
     * @param isRoleEnabled      - is role enabled
     * @param isModule           - if tab contains a separate GWT module
     * @see com.ericsson.eniq.events.ui.client.common.MetaReader
     */
    public TabInfoDataType(final String id, final String name, final String tip, final String style,
            final String tabItemCenterStyle, boolean isRoleEnabled, boolean isModule) {
        this.id = id;
        this.name = name;
        this.tip = tip;
        this.style = style;
        this.tabItemCenterStyle = tabItemCenterStyle;
        this.isRoleEnabled = isRoleEnabled;
        this.isModule = isModule;
    }
    public void setWorkspaceType(DefinedWorkspaceType workspaceType){
        this.workspaceType = workspaceType;
    }


    public DefinedWorkspaceType getWorkspaceType(){
       return workspaceType;
    }

    public boolean isModule() {
        return isModule;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the tip
     */
    public String getTip() {
        return tip;
    }

    /**
     * @return the style
     */
    public String getStyle() {
        return style;
    }

    /**
     * @return the tabItemCenterStyle
     */
    public String getTabItemCenterStyle() {
        return tabItemCenterStyle;
    }

    /**
     * @return  true if user has licence to see tab information
     */
    public boolean isRoleEnabled() {
        return isRoleEnabled;
    }

    public boolean isPlusTab() {
        return isPlusTab;
    }

    public void setPlusTab(final boolean plusTab) {
        isPlusTab = plusTab;
    }

    public boolean isUserTab() {
        return isUserTab;
    }

    public void setUserTab(boolean isUserTab) {
        this.isUserTab = isUserTab;
    }

    /**
     * Add a parameter to the tab info
     * @param paramName name of parameter
     * @param value value
     */
    public void addParameter(final String paramName, final String value) {
        parametersMap.put(paramName, value);
    }

    /**
     * @return the parametersMap
     */
    public Map<String, String> getParametersMap() {
        return parametersMap;
    }

    public void setWorkspaceState(WorkspaceState workspaceState) {
        this.workspaceState = workspaceState;
    }

    public WorkspaceState getWorkspaceState() {
        return workspaceState;
    }

    @Override
    public String toString() {
        return "TabInfoDataType{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", tip='" + tip + '\'' +
                ", style='" + style + '\'' +
                ", tabItemCenterStyle='" + tabItemCenterStyle + '\'' +
                ", isPlusTab=" + isPlusTab +
                ", isUserTab=" + isUserTab +
                ", isModule=" + isModule +
                ", isRoleEnabled=" + isRoleEnabled +
                ", parametersMap=" + parametersMap +
                '}';
    }
}
