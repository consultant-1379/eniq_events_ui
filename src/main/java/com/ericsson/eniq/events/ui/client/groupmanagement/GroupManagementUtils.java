/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.ericsson.eniq.events.ui.client.common.ReadLoginSessionProperties;
import com.ericsson.eniq.events.ui.client.datatype.group.GroupManagementItemDataType;
import com.ericsson.eniq.events.ui.client.groupmanagement.json.*;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog.DialogType;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.TextBox;

import java.util.List;

import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.*;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;

/**
 * Helper class for Group Management. abstract because all methods are static.
 *
 * @author ecarsea
 * @since 2011
 */
public abstract class GroupManagementUtils {

    /**
     * Generate a JSON String for a Group
     *
     * @param groupItemConfig
     * @param groupDataList
     *
     * @return
     */
    public static String generateGroupJsonData(final GroupManagementItemDataType groupItemConfig,
            final List<GroupData> groupDataList) {

        final GroupRoot root = new GroupRoot();
        final GroupArray groupArray = new GroupArray();
        int groupIndex = 0;
        for (final GroupData groupData : groupDataList) {
            addGroupData(groupItemConfig, groupArray, groupIndex, groupData);
            groupIndex++;
        }
        root.setGroups(groupArray);
        return root.toString();
    }

    /**
     * Remove key separators. e.g USA(272) = 272
     *
     * @param displayKey
     *
     * @return native key
     */

    public static String parseKey(final String displayKey) {
        final RegExp exp = RegExp.compile(GROUP_KEYS_REGEXP, "g");
        MatchResult matchResult = exp.exec(displayKey);
        String key = displayKey;
        while (matchResult != null) {//use last matched pattern
            for (int i = 0; i < matchResult.getGroupCount(); i++) {
                key = matchResult.getGroup(i);
            }
            matchResult = exp.exec(displayKey);
        }
        //return the last match
        return key.substring(1, key.length() - 1);
    }

    private static void addGroupData(final GroupManagementItemDataType groupItemConfig, final GroupArray groupArray,
            final int groupIndex, final GroupData groupData) {
        final Group group = new Group();
        group.setGroupName(groupData.getGroupName());
        group.setGroupType(groupItemConfig.getId());
        final GroupElementArray groupElementArray = new GroupElementArray();
        int groupElementIndex = 0;
        for (final GroupListItem ge : groupData.getGroupElements()) {
            addGroupElement(groupItemConfig, groupElementArray, groupElementIndex, ge);
            groupElementIndex++;
        }
        group.setGroupElementArray(groupElementArray);
        groupArray.addGroup(groupIndex, group);
    }

    private static void addGroupElement(final GroupManagementItemDataType groupItemConfig,
            final GroupElementArray groupElementArray, final int groupElementIndex, final GroupListItem groupElement) {
        final GroupElementJson groupElementJson = new GroupElementJson();
        final KeyArray keyArray = new KeyArray();
        int index = 0;
        final String[] keyValues = groupElement.getKeyValues();
        for (final String groupElementKeyName : groupItemConfig.getGroupElementKeyNameList()) {
            keyArray.addKey(index, getKey(index, keyValues, groupElementKeyName));
            index++;
        }

        groupElementJson.setKey(keyArray);
        groupElementArray.addGroupElement(groupElementIndex, groupElementJson);
    }

    protected static Key getKey(final int index, final String[] tokens, final String groupElementKeyName) {
        final Key key = new Key();
        key.setName(groupElementKeyName);
        final String value = tokens.length > index ? tokens[index] : KEY_NULL;
        key.setValue(value.isEmpty() ? KEY_NULL : value);
        return key;
    }

    /**
     * Check response from Server to Group Request and ensure that the response contains valid JSON
     *
     * @param response
     *
     * @return
     */
    public static JSONValue checkAndParse(final Response response) {
        final String json = response.getText();
        return parseJsonString(json);
    }

    /**
     * Parse JSON String
     *
     * @param json
     *
     * @return
     */
    public static JSONValue parseJsonString(final String json) {
        JSONValue responseValue = null;
        try {
            responseValue = JSONUtils.parse(json);
            if (responseValue == null) {
                showParseFailureDialog();
            }
        } catch (final Exception e) {
            showParseFailureDialog();
        }
        return responseValue;
    }

    public static void showParseFailureDialog() {
        final MessageDialog messageDialog = new MessageDialog();
        messageDialog.show(SERVER_ERROR, SERVER_CORRUPT_RESPONSE, DialogType.ERROR);
    }

    public static boolean isTextBoxEmpty(final TextBox textBox) {
        return textBox.getText() == null || textBox.getText().isEmpty();
    }

    /**
     * URL for adding/deleting groups
     *
     * @param groupConfigUrl
     * @param groupAction
     *
     * @return
     */
    public static String getGroupConfigurationUrl(final String groupConfigUrl,
            final GroupManagementConstants.GroupAction groupAction) {
        return getUrlRoot() + groupConfigUrl + "?" + ACTION_PARAMETER + "=" + groupAction.getActionString();
    }

    /**
     * URL to Retrieve list of Groups for a Group Type
     *
     * @param urlSuffix
     *
     * @return
     */
    public static String getRequestUrl(final String urlSuffix) {
        return getUrlRoot() + urlSuffix;
    }

    /**
     * @param loadUrl
     * @param nodeType
     * @param groupName
     *
     * @return
     */
    public static String getGroupElementRequestUrl(final String loadUrl, final String nodeType, final String groupName) {
        return getUrlRoot() + loadUrl + "?" + "type=" + nodeType + ":" + encodeGroupName(groupName);
    }

    public static String getCustomizedGroupElementRequestUrl(final String groupEditUrl, final String groupName) {
        if (groupEditUrl.contains("?")) {
            return getUrlRoot() + groupEditUrl + "&groupname=" + encodeGroupName(groupName);
        }
        return getUrlRoot() + groupEditUrl + "?groupname=" + encodeGroupName(groupName);
    }

    protected static String encodeGroupName(final String groupName) {
        return CommonParamUtil.encode(groupName).replaceAll("&", "%26");
    }

    protected static String getUrlRoot() {
        return ReadLoginSessionProperties.getEniqEventsServicesURI();
    }

    public static String getWizardUrl(final String loadUrl, final String param, final String value) {
        final StringBuffer finalUrl = new StringBuffer(getUrlRoot());
        finalUrl.append(loadUrl).append("?callback=" + CALLBACK_PARAM);
        if (value != null) {
            finalUrl.append("&").append(param).append("=").append(value);
        }
        return finalUrl.toString();
    }

    public static String removeCallbackParam(final String result) {
        final String callbackParam = CALLBACK_PARAM + "(";
        final int indexOf = result.indexOf(callbackParam) + callbackParam.length();
        final String jsonStr = result.substring(indexOf, result.lastIndexOf(")"));
        return jsonStr;
    }

    public static String getElementTypeFromHeader(final String header) {
        return header.substring(header.indexOf(GROUP_WINDOW_TITLE_PREFIX) + GROUP_WINDOW_TITLE_PREFIX.length());
    }

    public static String prepareHeader(final String elementType) {
        return GROUP_WINDOW_TITLE_PREFIX + elementType;
    }

    public static String createIdForFilterPanel(final String postfix) {
        return FILTER_PANEL_ID + "_" + postfix.trim().replace(" ", "_").toUpperCase();

    }

    public static boolean isGroupNameValid(final String groupName) {
        return groupName.matches(REGEX_EXP_GROUP_NAME);
    }

}