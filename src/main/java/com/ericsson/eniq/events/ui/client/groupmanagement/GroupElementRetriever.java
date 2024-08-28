/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement;

import com.ericsson.eniq.events.common.client.service.RestfulRequestBuilder.State;
import com.ericsson.eniq.events.ui.client.common.ServerComms;
import com.ericsson.eniq.events.ui.client.datatype.group.GroupManagementItemDataType;
import com.ericsson.eniq.events.ui.client.datatype.group.GroupMgmtConfigDataType;
import com.ericsson.eniq.events.ui.client.groupmanagement.event.GroupElementsLoadEvent;
import com.ericsson.eniq.events.ui.client.groupmanagement.event.GroupWindowMaskEvent;
import com.ericsson.eniq.events.ui.client.groupmanagement.event.GroupWindowUnMaskEvent;
import com.ericsson.eniq.events.ui.client.groupmanagement.json.*;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListTextItem;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog.DialogType;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import java.util.*;

import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.*;

/**
 * Helper class to perform retrieval of group(s) elements
 *
 * @author ecarsea
 * @since 2011
 */
public class GroupElementRetriever {

    private final EventBus eventBus;

    protected final ServerComms serverComms;

    private final List<HandlerRegistration> eventHandlers = new ArrayList<HandlerRegistration>();

    private final MessageDialog groupElementResultDialog;

    protected boolean bound;

    private List<String> groupElementKeyNames;

    protected boolean deletingGroup;

    private Iterator<String> groupNamesIterator;

    private String groupType;

    private final GroupMgmtConfigDataType groupMgmtConfigType;

    private final Map<String, List<GroupListItem>> groupElementMap = new HashMap<String, List<GroupListItem>>();

    public GroupElementRetriever(final EventBus eventBus, final GroupMgmtConfigDataType configType) {
        this.eventBus = eventBus;
        this.groupMgmtConfigType = configType;
        serverComms = new ServerComms(eventBus);
        groupElementResultDialog = new MessageDialog();

        bind(); // NOPMD
    }

    /** Register the success and fail event handlers */
    public void bind() {
        if (!bound) {
            bound = true;
        }
    }

    /**
     * @param handler
     */
    protected void registerHandler(final HandlerRegistration handler) {
        eventHandlers.add(handler);

    }

    public void unBind() {
        if (bound) {
            for (final HandlerRegistration handler : eventHandlers) {
                handler.removeHandler();
            }
            bound = false;
        }
    }

    protected String getGroupElementsLoadUrl(final String groupName) {
        String groupElementLoadUrl = "";
        final GroupManagementItemDataType groupManagementItem = groupMgmtConfigType.getGroupManagementItem(groupType);
        final String groupEditUrl = groupManagementItem.getGroupEditUrl();
        if (groupEditUrl != null && groupEditUrl.trim().length() > 0) {
            groupElementLoadUrl = GroupManagementUtils.getCustomizedGroupElementRequestUrl(groupEditUrl, groupName);
        } else {
            groupElementLoadUrl = GroupManagementUtils.getGroupElementRequestUrl(
                    groupMgmtConfigType.getGroupElementLoadUrl(), groupType, groupName);
        }
        return groupElementLoadUrl;
    }

    protected void displayFailureDialog(final String message) {
        groupElementResultDialog.show(FAILED_TO_LOAD_GROUP_ELEMENTS, message, DialogType.ERROR);
    }

    protected void displayInfoMessage(final String message) {
        groupElementResultDialog.show(NO_GROUP_DATA, message, DialogType.INFO);
    }

    /**
     * @param groupType
     * @param groupNames
     * @param groupElementKeyNamesList
     * @param deletingGrp
     */
    public void getGroupElements(final String groupType, final List<String> groupNames,
            final List<String> groupElementKeyNamesList, final boolean deletingGrp) {
        this.groupElementKeyNames = groupElementKeyNamesList;
        this.deletingGroup = deletingGrp;
        this.groupType = groupType;
        clearGroupElementMap();
        groupNamesIterator = groupNames.iterator();
        eventBus.fireEvent(new GroupWindowMaskEvent(deletingGroup ? RETRIEVING_GROUP_ELEMENTS_FOR_DELETION
                : LOADING_GROUP_ELEMENTS));
        final String loadUrl = getGroupElementsLoadUrl(getGroupNamesIterator().next());
        serverComms.requestData(State.GET, loadUrl, "", getCallback());
    }

    protected void clearGroupElementMap() {
        groupElementMap.clear();
    }

    /**
     * @return
     */
    protected RequestCallback getCallback() {
        return new GroupElementRetrieverRequestCallback();
    }

    /**
     * @param groupElementArray
     * @param i
     *
     * @return
     */
    protected GroupListItem getGroupElement(final GroupElementArray groupElementArray, final int i) {
        final Map<String, String> keyMap = new HashMap<String, String>();
        final GroupElementJson groupElement = groupElementArray.getGroupElement(i);
        final KeyArray keyArray = groupElement.getKeyArray();
        for (int j = 0; j < keyArray.size(); j++) {
            final Key key = keyArray.getKey(j);
            keyMap.put(key.getName(), key.getValue());
        }
        final StringBuilder groupElementSb = new StringBuilder();
        /** Create the group element name from the keys in the JSON response, group element name consists of
         * the comma delimited keys.
         */
        for (final String key : groupElementKeyNames) {
            String value = keyMap.get(key);
            if (value == null || value.equalsIgnoreCase(KEY_NULL)) {
                value = "";
            }
            groupElementSb.append(value).append(",");
        }
        if (groupElementSb.length() > 0) {
            groupElementSb.deleteCharAt(groupElementSb.length() - 1);
        }
        return new GroupListTextItem(groupElementSb.toString());
    }

    /**
     * @return
     */
    protected EventBus getEventBus() {
        return eventBus;
    }

    protected class GroupElementRetrieverRequestCallback implements RequestCallback {

        @Override
        public void onResponseReceived(final Request request, final Response response) {
            final JSONValue jsonValue = GroupManagementUtils.checkAndParse(response);
            if (jsonValue == null) {
                eventBus.fireEvent(new GroupWindowUnMaskEvent());
                return;
            }
            try {
                processGroupElementLoadResponse(jsonValue);
            } catch (final Exception e) {
                eventBus.fireEvent(new GroupWindowUnMaskEvent());
                //will not show any error message when group data is not available
                displayInfoMessage("No data or access available for selected group(s).");
            }

        }

        protected void processGroupElementLoadResponse(final JSONValue jsonValue) {
            final JSONObject object = jsonValue.isObject();
            final String groupName = getGroupName(object);
            final List<GroupListItem> groupElementList = getGroupElements(object);
            groupElementMap.put(groupName, groupElementList);
            /** If we are getting the elements for more than one group i.e. in the case of multiple group deletion **/
            if (getGroupNamesIterator().hasNext()) {
                final String loadUrl = getGroupElementsLoadUrl(getGroupNamesIterator().next());
                serverComms.requestData(State.GET, loadUrl, "", getCallback());
            } else {
                /** no more group elements to retrieve finished **/
                eventBus.fireEvent(new GroupWindowUnMaskEvent());
                eventBus.fireEvent(new GroupElementsLoadEvent(groupName, groupElementMap, deletingGroup));
            }
        }

        @Override
        public void onError(final Request request, final Throwable exception) {
            eventBus.fireEvent(new GroupWindowUnMaskEvent());
            displayFailureDialog(exception.getMessage());
        }
    }

    /**
     * @param obj
     * @return
     */
    protected String getGroupName(final JSONObject obj) {
        final GroupRoot groupRoot = new GroupRoot(obj.getJavaScriptObject());
        final Group group = groupRoot.getGroupArray().getGroup(0);
        return group.getGroupName();
    }

    /**
     * @param object
     * @return
     */
    public List<GroupListItem> getGroupElements(final JSONObject object) {
        final List<GroupListItem> groupElementList = new ArrayList<GroupListItem>();
        final GroupRoot groupRoot = new GroupRoot(object.getJavaScriptObject());
        final Group group = groupRoot.getGroupArray().getGroup(0);
        final GroupElementArray groupElementArray = group.getGroupElementArray();
        for (int i = 0; i < groupElementArray.size(); i++) {
            groupElementList.add(getGroupElement(groupElementArray, i));
        }
        ;
        return groupElementList;
    }

    /**
     * @return the groupNamesIterator
     */
    protected Iterator<String> getGroupNamesIterator() {
        return groupNamesIterator;
    }
}
