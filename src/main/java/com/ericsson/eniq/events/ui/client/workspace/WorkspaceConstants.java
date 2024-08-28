/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public abstract class WorkspaceConstants {

    public static final String WORKSPACE_MANAGEMENT_ID = "workspaceManagement";

    public static final int MAX_WORKSPACE_NAME_LENGTH = 64;

    public static final int MAX_VISIBLE_WORKSPACE_NAME_LENGHT = 20;

    public static final int MAX_NUMBER_OF_USER_TABS = 1000;

    public static final int MAX_SELECTED_WINDOWS = 5;

    public static final int LAUNCH_MENU_OFFSET = 275;

    public static final int LAUNCH_TAB_OFFSET = 20;
    
    public static final int KPI_PANEL_OFFSET = 33;

    private WorkspaceConstants() {
    }

    public enum WindowPositioning {
        CASCADE("cascade"), TILE("tile"), FREEFORM("freeform");

        String positioning;

        private WindowPositioning(String positioning) {
            this.positioning = positioning;
        }

        public static WindowPositioning fromString(final String positioning) {
            for (final WindowPositioning windowPositioning : WindowPositioning.values()) {
                if (windowPositioning.toString().equalsIgnoreCase(positioning)) {
                    return windowPositioning;
                }
            }
            return FREEFORM;
        }

        @Override
        public String toString() {
            return positioning;
        }
    }

    public enum DefinedWorkspaceType {

        PREDEFINED("Predefined"), USER_DEFINED("User Defined");

        private final String type;

        DefinedWorkspaceType(final String type) {
            this.type = type;
        }

        public static DefinedWorkspaceType fromString(final String type) {
            for (final DefinedWorkspaceType definedType : DefinedWorkspaceType.values()) {
                if (definedType.toString().compareToIgnoreCase(type) == 0) {
                    return definedType;
                }
            }
            return USER_DEFINED;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    public enum DimensionSelectorType {
        NO_SEARCH("NO_SEARCH"), SINGLE_SEARCH("SINGLE_SEARCH"), TEXT_ENTRY("TEXT_ENTRY"), PAIRED_SEARCH("PAIRED_SEARCH");

        private final String selectorType;

        DimensionSelectorType(String selectorType) {
            this.selectorType = selectorType;
        }

        public static DimensionSelectorType fromString(String selectorType) {
            for (DimensionSelectorType type : DimensionSelectorType.values()) {
                if (type.toString().equals(selectorType)) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return selectorType;
        }
    }

    public enum TechnologyType {
        CORE("Core"), LTE("4G"), GSM("2G"), WCDMA("3G");

        private final String technologyType;

        TechnologyType(String technologyType) {
            this.technologyType = technologyType;
        }

        public static TechnologyType fromString(String technologyType) {
            for (TechnologyType type : TechnologyType.values()) {
                if (type.toString().equals(technologyType)) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return technologyType;
        }
    }
}
