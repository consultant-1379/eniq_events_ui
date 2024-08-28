/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.fileupload;

import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.common.client.service.RestfulRequestBuilder.ContentType;
import com.ericsson.eniq.events.common.client.service.RestfulRequestBuilder.State;
import com.ericsson.eniq.events.ui.client.common.Constants;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.ericsson.eniq.events.ui.client.common.ServerComms;
import com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.GroupAction;
import com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementUtils;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog.DialogType;
import com.ericsson.eniq.events.widgets.client.mask.MaskHelper;
import com.extjs.gxt.ui.client.core.XDOM;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.FILE_IMPORT_DELETE_SUCCESS_DIALOG_MESSAGE;
import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.FILE_IMPORT_DELETE_SUCCESS_DIALOG_TITLE;
import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.FILE_IMPORT_SAVE_SUCCESS_DIALOG_MESSAGE;
import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.FILE_IMPORT_SAVE_SUCCESS_DIALOG_TITLE;
import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.GROUP_FILE_IMPORT_ERROR;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public class GroupFileUploadPresenter extends BasePresenter<GroupFileUploadView> {

    private final ServerComms serverComms;

    private final String fileUploadUrl;

    private final MessageDialog uploadResultDialog;

    private GroupAction action;

    private final MaskHelper maskHelper;

    private final FileUploadRequestCallback callback = new FileUploadRequestCallback();

    @Inject
    public GroupFileUploadPresenter(final EventBus eventBus, final GroupFileUploadView view,
            final IMetaReader metaReader) {
        super(view, eventBus);
        serverComms = new ServerComms(eventBus);
        maskHelper = new MaskHelper();
        fileUploadUrl = metaReader.getGroupManagementConfigData().getGroupConfigurationUrl();
        uploadResultDialog = new MessageDialog();
        uploadResultDialog.setGlassEnabled(true);
        bind();
    }

    public void upload(final String fileName, final String data, final GroupAction action) {
        final String completeUrl = GroupManagementUtils.getGroupConfigurationUrl(fileUploadUrl, action);
        final int extIndex = fileName.length() - 6;

        maskHelper.mask(getView().container.getElement(), "Uploading File");
        if (extIndex > 0 && fileName.substring(extIndex).equalsIgnoreCase(".json")) {
            serverComms.requestData(State.POST, completeUrl, data, callback);
        } else {
            serverComms.requestData(State.POST, completeUrl, data, callback, ContentType.XML);
        }
    }

    private class FileUploadRequestCallback implements RequestCallback {

        /* (non-Javadoc)
         * @see com.google.gwt.http.client.RequestCallback#onResponseReceived(com.google.gwt.http.client.Request, com.google.gwt.http.client.Response)
         */
        @Override
        public void onResponseReceived(Request request, Response response) {
            maskHelper.unmask();
            final JSONValue jsonData = GroupManagementUtils.checkAndParse(response);

            // exception message written into response
            if (jsonData != null && JSONUtils.checkData(jsonData)) {
                showConfirmationDialog();
            } else {
                handleUploadError(jsonData == null ? EMPTY_RESPONSE_FROM_SERVER : "");
            }
        }

        /* (non-Javadoc)
         * @see com.google.gwt.http.client.RequestCallback#onError(com.google.gwt.http.client.Request, java.lang.Throwable)
         */
        @Override
        public void onError(Request request, Throwable exception) {
            handleUploadError(exception.getMessage());
        }

    }

    private void handleUploadError(final String errorMessage) {
        maskHelper.unmask();
        if (!errorMessage.isEmpty()) {
            uploadResultDialog.getElement().getStyle().setZIndex(Math.max(Constants.ENIQ_EVENTS_BASE_ZINDEX + 3,
                    XDOM.getTopZIndex()));
            uploadResultDialog.show(GROUP_FILE_IMPORT_ERROR, errorMessage, DialogType.ERROR);
        }
    }

    public void launch(final GroupAction action) {
        this.action = action;
        getView().launch(action);
    }

    protected void showConfirmationDialog() {
        if (action.equals(GroupAction.ADD)) {
            uploadResultDialog.getElement().getStyle().setZIndex(Math.max(Constants.ENIQ_EVENTS_BASE_ZINDEX + 3,
                    XDOM.getTopZIndex()));
            uploadResultDialog.show(FILE_IMPORT_SAVE_SUCCESS_DIALOG_TITLE, FILE_IMPORT_SAVE_SUCCESS_DIALOG_MESSAGE,
                    DialogType.INFO);
        } else if (action.equals(GroupAction.DELETE)) {
            uploadResultDialog.getElement().getStyle().setZIndex(Math.max(Constants.ENIQ_EVENTS_BASE_ZINDEX + 3,
                    XDOM.getTopZIndex()));
            uploadResultDialog.show(FILE_IMPORT_DELETE_SUCCESS_DIALOG_TITLE, FILE_IMPORT_DELETE_SUCCESS_DIALOG_MESSAGE,
                    DialogType.INFO);
        }
    }

    /**
     * 
     */
    public void close() {
        unbind();
    }
}
