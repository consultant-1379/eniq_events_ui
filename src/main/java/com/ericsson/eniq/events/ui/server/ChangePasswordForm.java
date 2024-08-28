/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.server;

/**
 * @author estepdu
 *
 */
import static com.ericsson.eniq.events.ui.login.shared.AuthenticationConstants.*;
import static com.ericsson.eniq.events.ui.login.shared.AuthenticationConstants.VALIDATION_RESPONSE.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ericsson.eniq.events.ui.login.shared.AuthenticationConstants;
import com.ericsson.eniq.repository.AsciiCrypter;

/**
 *
 * This class is used for the logic behind the change password form page.
 *
 */
public class ChangePasswordForm {

    private static final Logger LOGGER = Logger.getLogger(ChangePasswordForm.class.getName());

    /* The properties */
    private String existingUserName = EMPTY_STRING;

    private String existingUserPassword = EMPTY_STRING;

    private String newUserPassword = EMPTY_STRING;

    // Change Password Response Code
    private int changePasswordResponseCode;

    public String getExistingUserName() {
        return existingUserName;
    }

    public void setExistingUserName(final String existingUserName) {
        this.existingUserName = existingUserName.trim();
    }

    public String getExistingUserPassword() {
        return existingUserPassword;
    }

    public void setExistingUserPassword(final String existingUserPassword) {
        this.existingUserPassword = existingUserPassword.trim();
    }

    public String getNewUserPassword() {
        return newUserPassword;
    }

    public String getEncryptedNewUserPassword() {
        String encryptedData = EMPTY_STRING;
        try {
            encryptedData = AsciiCrypter.getInstance().encrypt(this.newUserPassword);
        } catch (final Exception e) {
            encryptedData = EMPTY_STRING;
        }
        return encryptedData;
    }

    public void setNewUserPassword(final String newUserPassword) {
        this.newUserPassword = newUserPassword.trim();
    }

    /**
     * Get the response Code
     * @return changePasswordResponseCode
     */
    public int getChangePasswordResponseCode() {
        return changePasswordResponseCode;
    }

    /**
     * Clear/Set the user details
     */
    public void clearChangePasswordFormDetails() {
        // Clear the form
        existingUserName = EMPTY_STRING;
        existingUserPassword = EMPTY_STRING;
        newUserPassword = EMPTY_STRING;
        changePasswordResponseCode = SUCCESS.getResponseCode();
    }

    /**
     * Check if the Form is valid by checking the various criteria
     *
     * @return True if the form is valid False if not
     */
    public boolean process() {
        // Clear all errors
        changePasswordResponseCode = SUCCESS.getResponseCode();
        changeUserPassword();

        // If no errors, form is valid
        return AuthenticationConstants.isResponseSuccess(changePasswordResponseCode);
    }

    private void changeUserPassword() {
        final ChangeUserDetails changeUserDetail = getChangeUserDetailsClass();

        // Finally if there are no error messages change the password!
        if (AuthenticationConstants.isResponseSuccess(changePasswordResponseCode)) {
            try {
                changeUserDetail.changePassword(existingUserName, existingUserPassword, newUserPassword);
            } catch (final Exception ex) {
                parseException(ex);
            }
        }
    }

    private void parseException(final Exception ex) {
        if (ex.getMessage().contains(ERR_LDAP_CODE_49_MSG.getResponseMessage())) {
            changePasswordResponseCode = ERR_INVALID_USER_DETAILS_ENTERED.getResponseCode();
        } else if (ex.getMessage().contains(ERR_LDAP_CODE_19_MSG.getResponseMessage())) {
            changePasswordResponseCode = ERR_PASSWORD_IN_PASSWORD_HISTORY_ERROR_MSG.getResponseCode();
        } else if (ex.getMessage().contains(ERR_LDAP_CODE_32_MSG.getResponseMessage())) {
            // Do noting in this situation as this is just saying that no attribute
            // were found under this name
            // Valid for administrator as they will NOT have the password overlay
            // applied
        } else {
            changePasswordResponseCode = ERR_CONTACT_ADMINISTRATOR_ERROR_MSG.getResponseCode();
            LOGGER.log(Level.WARNING, ERR_WHILE_TRYING_TO_CONNECT_TO_LDAP_MSG.getResponseMessage(), ex);
        }
        // Your password must be at least number characters; cannot repeat your
        // previous number passwords
    }

    /**
     * Required for JUnit tests to run
     *
     * @return
     */
    protected ValidateUser getValidUserClass() {
        return new ValidateUser();
    }

    /**
     * Required for JUnit tests to run
     *
     * @return
     */
    protected ChangeUserDetails getChangeUserDetailsClass() {
        return new ChangeUserDetails();
    }

}
