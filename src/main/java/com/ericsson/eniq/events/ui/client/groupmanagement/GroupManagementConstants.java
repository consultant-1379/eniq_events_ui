/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

/**
 * Holder for constants for the Group Management
 *
 * @author ecarsea
 * @since 2011
 */
public abstract class GroupManagementConstants {

    public static final String ACTION_PARAMETER = "action";

    public static final String FAILED_TO_LOAD_GROUP_ELEMENTS = "Failed to load group Elements";

    public static final String NO_GROUP_DATA = "No Group Data";

    public static final String LOADING_GROUP_ELEMENTS = "Loading Group Elements...";

    public static final String RETRIEVING_GROUP_ELEMENTS_FOR_DELETION = "Retrieving Group Elements for Deletion...";

    public static final String KEY_NULL = "null";

    public static final String GROUP_SINGULAR = "this group";

    public static final String GROUPS_PLURAL = "these groups";

    public static final String GROUP_ALREADY_EXISTS = "This group name already exists, do you want to modify it?";

    public static final String DELETE_QUERY = "Are you sure you want to delete ";

    public static final String ACTION_WARNING = "This action cannot be undone";

    public static final String LOADING_GROUPS = "Loading Groups...";

    public static final String DELETING_GROUP_ELEMENTS_MASK_MESSAGE = "Deleting Group Element(s)...";

    public static final String DELETING_GROUPS_MASK_MESSAGE = "Deleting Groups...";

    public static final String DELETING_GROUP_MASK_MESSAGE = "Deleting Group...";

    public static final String LOADING_MSG_ENDING_DOTS = "...";

    public static final String SAVING_GROUP_MASK_MESSAGE = "Saving Group...";

    public static final String SAVE_GROUP_ELEMENT_FAILURE_DIALOG_TITLE = "Save Group Element Failure";

    public static final String SAVE_GROUP_FAILURE_DIALOG_TITLE = "Save Group Failure";

    public static final String DELETE_GROUP_FAILURE_DIALOG_TITLE = "Delete Group Failure";

    public static final String GROUP_SAVE_SUCCESS_DIALOG_MESSAGE = "Group Saved to Database";

    public static final String GROUP_SAVE_SUCCESS_DIALOG_TITLE = "Save Group Success";

    public static final String GROUP_DELETE_SUCCESS_DIALOG_MESSAGE = "Group removed from Database";

    public static final String GROUP_DELETE_SUCCESS_DIALOG_TITLE = "Delete Group Success";

    public static final String GROUPS_DELETE_SUCCESS_DIALOG_MESSAGE = "Groups removed from Database";

    public static final String GROUPS_DELETE_SUCCESS_DIALOG_TITLE = "Delete Groups Success";

    public static final String FILE_IMPORT_SAVE_SUCCESS_DIALOG_MESSAGE = "Group(s) Saved to Database";

    public static final String FILE_IMPORT_SAVE_SUCCESS_DIALOG_TITLE = "Save Group(s) Success";

    public static final String GROUP_FILE_IMPORT_ERROR = "Group Import Error";

    public static final String FILE_IMPORT_DELETE_SUCCESS_DIALOG_MESSAGE = "Group(s) removed from Database";

    public static final String FILE_IMPORT_DELETE_SUCCESS_DIALOG_TITLE = "Delete Group(s) Success";

    /** Show 100 rows for any cell list component i.e. in liveload, current group etc * */
    public static final int GROUP_NAME_DISPLAY_LIMIT = 250;

    public static final String GROUP_KEYS_DELIMITER = ",";

    public static final String GROUP_KEYS_REGEXP = "[(][0-9]+[)]";

    public static final String LOADING = "Loading ";

    public static final String CALLBACK_PARAM = "transId0";

    public static final String GROUP_WINDOW_TITLE_PREFIX = "Select ";

    public static final String FILTER_PANEL_ID = "selenium_tag_F_PANEL";

    public static final String REGEX_EXP_GROUP_NAME = "^[A-Za-z][0-9a-zA-Z-_]{0,34}$";

    public static final String GROUP_NAME_ERROR_TITLE = "Invalid Group Name";

    public static final String GROUP_NAME_ERROR_MESSAGE = "Group names must start with an alphabetic character and can contain any combination of alphabetic, numeric, underscore, and hyphen characters. The maximum allowable length is 35 characters";

    public enum GroupAction {
        ADD("add"), DELETE("delete");

        private final String action;

        GroupAction(final String action) {
            this.action = action;
        }

        public String getActionString() {
            return action;
        }
    }

    public enum GroupOperation {
        SAVE, DELETE_GROUP, DELETE_GROUP_ELEMENTS
    }
}
