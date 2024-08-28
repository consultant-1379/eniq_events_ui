/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.mvp;

import com.ericsson.eniq.events.ui.login.client.mvp.LoginPresenter.ILoginView;

/**
 * Defines the interface required by LoginView and implemented by LoginPresenter
 * 
 * @author eriwals
 * @since May 2011
 * 
 */
public interface ILoginPresenter {

    /**
     * Invoked when View requests the Presenter to process the Login form
     * 
     */
    void onLoginSelected();

    /**
     * Invoked when View requests the Presenter to process the Change Password form
     * 
     */
    void onPasswordChange();

    /**
     * Set the password strength indicator in the View, based on strength of the provided new password
     *
     * @param oldPassword the old password
     * @param newPassword  the new password which the user has entered
     */
    void checkPasswordStrength(String oldPassword, String newPassword);

    /**
     * Login Button on Change Password Screen is selected
     */
    void onChangePasswordLoginSelected();

    /**
     * @return
     */
    ILoginView getView();
}
