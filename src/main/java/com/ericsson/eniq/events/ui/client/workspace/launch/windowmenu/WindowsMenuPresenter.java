/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu;

import com.ericsson.eniq.events.common.client.service.DataServiceHelper;
import com.ericsson.eniq.events.ui.client.common.Constants;
import com.ericsson.eniq.events.ui.client.workspace.config.WorkspaceConfigService;
import com.ericsson.eniq.events.ui.client.workspace.events.GroupPopupChangeEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.GroupPopupChangeEventHandler;
import com.ericsson.eniq.events.ui.client.workspace.events.LaunchWindowEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.StatusBarChangeEvent;
import com.ericsson.eniq.events.ui.client.workspace.launch.AbstractWindowMenuPresenter;
import com.ericsson.eniq.events.ui.client.workspace.launch.IWindowMenuView;
import com.ericsson.eniq.events.ui.client.workspace.launch.WindowLaunchParams;
import com.ericsson.eniq.events.ui.client.workspace.launch.WorkspaceLaunchMenuResourceBundle;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.google.gwt.regexp.shared.RegExp;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class WindowsMenuPresenter extends AbstractWindowMenuPresenter<WindowsMenuView> {

    private String workspaceId;

    private static final long MAX_PTMSI = 4294967295L;

    /**
     * @param view
     * @param eventBus
     * @param resourceBundle
     * @param configService
     * @param dataServiceHelper
     */
    @Inject
    public WindowsMenuPresenter(final WindowsMenuView view, final EventBus eventBus,
            final WorkspaceLaunchMenuResourceBundle resourceBundle, final WorkspaceConfigService configService,
            final DataServiceHelper dataServiceHelper) {
        super(view, eventBus, resourceBundle, configService, dataServiceHelper);
        bind();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.common.client.mvp.BasePresenter#onBind()
     */
    @Override
    protected void onBind() {
        registerHandler(getEventBus().addHandler(GroupPopupChangeEvent.TYPE, new GroupPopupChangeEventHandler() {

            @Override
            public void onGroupPopupChange(final String selectedWorkspaceId, final Boolean isWindowsTabSelected) {
                if (workspaceId.equals(selectedWorkspaceId)) {
                    if (isWindowsTabSelected != null) {
                        getView().setWindowsTabSelected(isWindowsTabSelected);
                    }
                    getView().handleGroupElementPopupPanel();
                } else {
                    getView().hideGroupElementPopupPanel();
                }
            }
        }));
    }

    public void init(final String workspaceId) {
        this.workspaceId = workspaceId;
        getView().init(resourceBundle, configService);
    }

    /**
     * @param params
     */
    public void onLaunchWindows(final WindowLaunchParams params) {
        getEventBus().fireEvent(new LaunchWindowEvent(params));
    }

    /**
     * @param selectedWindows
     */
    public void onWindowsSelected(final int selectedWindows) {
        getEventBus().fireEvent(new StatusBarChangeEvent(selectedWindows + " Analysis Windows Selected"));
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.workspace.launch.AbstractWindowMenuPresenter#getWindowMenuView()
     */
    @Override
    protected IWindowMenuView getWindowMenuView() {
        return getView();
    }

    /**
     * Validate IMSI | MSISDN | PTMSI entries in the textEntryBox
     * (3rd text entry field in the launch menu).
     * If the value entered does not match the regular expression, 
     * an error message is shown and the method returns false.
     * @param value
     * @param selectedDimension
     * @return
     */
    protected boolean validateTextEntry(String value, final String selectedDimension) {
        boolean result = true;
        value = value.replaceAll(" ", ""); //if the value has SPACE, remove it!
        if (selectedDimension.equalsIgnoreCase(Constants.IMSI)) {
            result = validateIMSI(value);
        } else if (selectedDimension.equalsIgnoreCase(Constants.MSISDN)) {
            result = validateMSISDN(value);
        } else {
            result = validatePTMSI(value);
        }
        getView().setInvalid(!result);
        return result;
    }

    /**
     * validate an IMSI
     * @param value
     * @return
     */
    private boolean validateIMSI(final String value) {
        boolean result = true;
        final RegExp regExp = RegExp.compile(Constants.IMSI_PATTERN);
        if (!regExp.test(value)) {
            showWarning(Constants.IMSI);
            result = false;
        }
        return result;
    }

    /**
     * validate an MSISDN
     * @param value
     * @return
     */
    private boolean validateMSISDN(final String value) {
        boolean result = true;
        final RegExp regExp = RegExp.compile(Constants.MSISDN_PATTERN);
        if (!regExp.test(value)) {
            showWarning(Constants.MSISDN);
            result = false;
        }
        return result;
    }

    /**
     * validate a PTMSI
     * @param value
     * @return
     */
    private boolean validatePTMSI(final String value) {
        boolean result = true;
        final RegExp regExp = RegExp.compile(Constants.PTMSI_PATTERN);
        if (regExp.test(value)) {
            final Long ptmsi = Long.parseLong(value);
            if (ptmsi >= MAX_PTMSI) {
                result = false;
                showWarning(Constants.PTMSI);
            }
        } else {
            showWarning(Constants.PTMSI);
            result = false;
        }
        return result;
    }

    /**
     * Displays an error message for invalid data entered into the textEntryBox 
     * (3rd text entry field in the launch menu).
     * This comment will be removed soon by efreass
     * @param selectedDimension
     */
    private void showWarning(final String selectedDimension) {
        final MessageDialog error = new MessageDialog();
        if (selectedDimension.equalsIgnoreCase(Constants.IMSI)) {
            error.show(Constants.INVALID_IMSI, Constants.INVALID_IMSI_MESSAGE,
                    MessageDialog.DialogType.WARNING);
        } else if (selectedDimension.equalsIgnoreCase(Constants.MSISDN)) {
            error.show(Constants.INVALID_MSISDN, Constants.INVALID_MSISDN_MESSAGE,
                    MessageDialog.DialogType.WARNING);
        } else {
            error.show(Constants.INVALID_PTMSI, Constants.INVALID_PTMSI_MESSAGE,
                    MessageDialog.DialogType.WARNING);
        }
    }

}
