/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.mvp;

import static com.ericsson.eniq.events.ui.login.shared.AuthenticationConstants.VALIDATION_RESPONSE.*;

import com.ericsson.eniq.events.ui.login.client.util.LoginUtils;
import com.ericsson.eniq.events.ui.login.client.util.LoginUtils.PasswordStrength;
import com.ericsson.eniq.events.ui.login.shared.AuthenticationConstants;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.google.inject.Inject;

/**
 * @author Peter Mucsi
 * @author Pedro Tavares - epedtav Added Login Automation.
 * @author michaeladams - added pre-fetech of product catalogue
 * @author ecarsea
 */
public class LoginPresenter implements ILoginPresenter {

    /** Interface to allow the view to notify the handler asynchronously that a time based action has completed
     *  i.e. a progress bar.
     */
    public interface ILoginViewActionCallback {
        void actionComplete();
    }

    private final ILoginView view;

    private final LoginService loginService = new LoginService();

    /**
     * {@link LoginPresenter}'s view.
     */
    public interface ILoginView {
        /** 
         * Get User Id from view. Depends on selected tab i.e. will return user name from
         * login tab or change password tab, depending on which tab is active 
         **/
        String getUserId();

        void setUserId(String userId);

        /**
         * Get password from the view. Operates in same manner as getUserId method/
         **/
        String getPassword();

        void setPassword(String password);

        void enableLoginView();

        void disableLoginView();

        void setPasswordStrengthTip(String strength);

        String getNewPassword();

        String getConfirmPassword();

        void switchToChangePasswordTab();

        void setMessage(String text, boolean error);

        void setMessage(String text);

        void setNewPasswordEnabled(boolean enabled);

        void showGlassPanel();

        void showNewPasswordEntryPage();

        void setPresenter(ILoginPresenter loginPresenter);

        Widget asWidget();

        void displayAuthenticating();

        void setLoginOperationComplete();

        void displayValidatingUserView(boolean isChangingPassword);

        void displayChangingPassword();

        void showPasswordChangeSuccess(ILoginViewActionCallback iLoginViewActionCallback);
    }

    /**
     * TODO remove RPC service. Remove OpenWorkspace stuff too. That should be part of the personal desktop
     * @param eventBus
     * @param view
     * @param proxy
     * @param ewfRpcService
     * @param personalDesktop
     * @param openWorkspace
     * @param userManagementService
     */
    @Inject
    public LoginPresenter(final ILoginView view) {
        this.view = view;
        view.setPresenter(this);
        view.showGlassPanel();
        view.setMessage(InputElement.as(RootPanel.get("errorMessage").getElement()).getValue(), true);
    }

    @Override
    public void onLoginSelected() {
        performLoginAction(false);
    }

    @Override
    public void onChangePasswordLoginSelected() {
        view.disableLoginView();
        performLoginAction(true);
    }

    /**
     * Sets the password strength indicator in the View, based on strength of the provided new password.
     */
    @Override
    public void checkPasswordStrength(final String oldPassword, final String newPassword) {
        view.setNewPasswordEnabled(false);
        switch (LoginUtils.checkPasswordStrength(oldPassword, newPassword)) {
        case POOR:
            view.setPasswordStrengthTip(PasswordStrength.POOR.getStrengthTip());
            break;
        case FAIR:
            view.setPasswordStrengthTip(PasswordStrength.FAIR.getStrengthTip());
            break;
        case GOOD:
            view.setPasswordStrengthTip(PasswordStrength.GOOD.getStrengthTip());
            break;
        case VERY_GOOD:
            view.setPasswordStrengthTip(PasswordStrength.VERY_GOOD.getStrengthTip());
            break;
        case STRONG:
            view.setPasswordStrengthTip(PasswordStrength.STRONG.getStrengthTip());
            view.setNewPasswordEnabled(true);
            break;
        }
    }

    private void performLoginAction(final boolean isChangePasswordAttempt) {
        view.disableLoginView();
        view.displayValidatingUserView(isChangePasswordAttempt);
        validateUser(isChangePasswordAttempt);

    }

    private void validateUser(final boolean isChangePasswordAttempt) {

        try {
            loginService.validateUser(view.getUserId(), view.getPassword(), new RequestCallback() {

                @Override
                public void onResponseReceived(final Request request, final Response response) {
                    /** Parse XML from response from the JSP **/
                    try {
                        final Document responseXml = XMLParser.parse(response.getText());
                        if (responseSuccess(responseXml)) {
                            if (isChangePasswordAttempt) {
                                getView().enableLoginView();
                                getView().showNewPasswordEntryPage();
                            } else {
                                authenticate(view.getUserId(), view.getPassword());
                            }
                        } else {
                            handleValidateError(isChangePasswordAttempt, responseXml);
                        }
                    } catch (final Exception e) {
                        handleSystemError(e);
                    }
                }

                @Override
                public void onError(final Request request, final Throwable t) {
                    handleSystemError(t);
                }

            });
        } catch (final Exception e) {
            handleSystemError(e);
            view.enableLoginView();
        }
    }

    /**
     * @param message
     */
    private void handleSystemError(final Throwable t) {
        logToConsole(t.getCause() + " - " + t.getMessage());
        view.setMessage("System Error");
        view.setLoginOperationComplete();
        view.enableLoginView();
    }

    /**
     * @param string
     */
    private native void logToConsole(String msg)
    /*-{
		console.log(msg);
    }-*/;

    /**
     * Server call to change the user's password
     */
    @Override
    public void onPasswordChange() {
        view.displayChangingPassword();
        try {
            loginService.changePassword(view.getUserId(), view.getPassword(), view.getNewPassword(),
                    new RequestCallback() {

                        @Override
                        public void onResponseReceived(final Request request, final Response response) {
                            /** Parse XML from response from the JSP **/
                            try {
                                final Document responseXml = XMLParser.parse(response.getText());
                                if (responseSuccess(responseXml) || changePasswordRequired(responseXml)) {
                                    view.showPasswordChangeSuccess(new ILoginViewActionCallback() {

                                        @Override
                                        public void actionComplete() {
                                            authenticate(view.getUserId(), view.getNewPassword());
                                        }
                                    });
                                } else {
                                    showError(responseXml);
                                }
                            } catch (final Exception e) {
                                handleSystemError(e);
                            }
                        }

                        @Override
                        public void onError(final Request request, final Throwable t) {
                            handleSystemError(t);
                        }

                    });
        } catch (final Exception e) {
            handleSystemError(e);
        }
    }

    private boolean responseSuccess(final Document responseXml) {
        return AuthenticationConstants.isResponseSuccess(getResponseCode(responseXml));
    }

    private boolean changePasswordRequired(final Document responseXml) {
        return (getResponseCode(responseXml) == ERR_ACCOUNT_PASSWORD_REQUIRED_TO_BE_CHANGED.getResponseCode())
        || (getResponseCode(responseXml) == ERR_PASSWORD_IN_PASSWORD_EXPIRED_ERROR_MSG.getResponseCode());
    }

    private void showError(final Document responseXml) {
        view.setLoginOperationComplete();
        view.setMessage(AuthenticationConstants.getResponseMessageForCode(getResponseCode(responseXml)), true);
        view.enableLoginView();
    }

    private int getResponseCode(final Document responseXml) {
        final String responseCodeStr = responseXml.getElementsByTagName("responseCode").item(0).getFirstChild()
                .getNodeValue();
        int responseCode;
        try {
            responseCode = Integer.parseInt(responseCodeStr);
        } catch (final NumberFormatException nfe) {
            responseCode = -1;
        }

        return responseCode;
    }

    /* (non-Javadoc)
    com.ericsson.eniq.events.ui.loginPresenter#getView()
     */
    @Override
    public ILoginView getView() {
        return view;
    }

    /**
     * @param userId
     * @param password
     */
    private void authenticate(final String userId, final String password) {
        view.displayAuthenticating();
        loginService.authenticate(userId, password);
    }

    /** 
     * Handle a ValidateUser Error
     * @param isChangePasswordAttempt
     * @param responseXml
     */
    private void handleValidateError(final boolean isChangePasswordAttempt, final Document responseXml) {
        /** If error indicates that a password change is required **/
        if (changePasswordRequired(responseXml)) {
            /** If this validate user attempt was prior to changing the password then move on to the new password entry
             * page.
             */
            if (isChangePasswordAttempt) {
                getView().enableLoginView();
                getView().showNewPasswordEntryPage();
            } else {
                /** Validate user from login attempt, switch to change password tab **/
                getView().switchToChangePasswordTab();
                showError(responseXml);
            }
        } else {
            showError(responseXml);
        }
    }
}
