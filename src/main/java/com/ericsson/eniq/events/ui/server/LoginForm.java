/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.server;

import static com.ericsson.eniq.events.ui.login.shared.AuthenticationConstants.*;
import static com.ericsson.eniq.events.ui.login.shared.AuthenticationConstants.VALIDATION_RESPONSE.*;

import javax.naming.NamingException;

import com.ericsson.eniq.repository.AsciiCrypter;
import com.ericsson.eniq.events.ui.login.shared.AuthenticationConstants;
import com.ericsson.eniq.events.ui.server.config.ApplicationConfigManager;
import com.ericsson.eniq.events.ui.server.config.ApplicationConfigManagerFactory;

/**
 * 
 * This class is used to for the logic behind the login form.
 * 
 * @author estepdu
 * 
 */

public class LoginForm {

    /* The properties */
    private String userName = EMPTY_STRING;

    private String userPassword = EMPTY_STRING;

    // Response code of operation to send to client
    private int responseCode;

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName.trim();
    }

    public String getUserPassword() {
        return userPassword;
    }
    
    public String getEncryptedUserPassword() {
        String encryptedData = EMPTY_STRING;
        try {
            encryptedData = AsciiCrypter.getInstance().encrypt(this.userPassword);
        } catch (final Exception e) {
            encryptedData = EMPTY_STRING;
        }
        return encryptedData;
    }

    public void setUserPassword(final String userPassword) {
        this.userPassword = userPassword.trim();
    }

    /**
     * Clear/Set the user details
     */
    public void clearLoginFormDetails() {
        // Clear the form
        userName = EMPTY_STRING;
        userPassword = EMPTY_STRING;
        responseCode = SUCCESS.getResponseCode();
    }

    /**
     * Get the response code.
     * @return response code
     */
    public int getLoginResponseCode() {
        return responseCode;
    }

    /**
     * Check if the Form is valid by checking the various criteria
     * 
     * @return True if the form is valid False if not
     */
    public boolean process() {
        // Clear all errors
        responseCode = SUCCESS.getResponseCode();
        validUserDetails();

        // If no errors, form is valid
        return AuthenticationConstants.isResponseSuccess(responseCode);
    }

    /**
     * Making sure that user detail checks are sequential and the process throws a
     * single, correct exception if any fails.
     */
    private void validUserDetails() {
        final ValidateUser vu = getValidUserClass();
        try {
            if (vu.isNotValidEniqEventUIUser(userName, userPassword)) {
                responseCode = ERR_USER_NOT_A_UI_USER.getResponseCode();
            } else if (!vu.hasPermissions(userName, userPassword)) {
                responseCode = ERR_USER_NOT_A_UI_USER.getResponseCode();
            } else if (vu.passwordResetRequired(userName, userPassword)) {
                responseCode = ERR_ACCOUNT_PASSWORD_REQUIRED_TO_BE_CHANGED.getResponseCode();
            } else if (vu.isUserPasswordExpired(userName, userPassword)) {
                responseCode = ERR_PASSWORD_IN_PASSWORD_EXPIRED_ERROR_MSG.getResponseCode();
            }
        } catch (final NamingException ex) {
            parseException(ex);
        }
    }

    /**
     * This method checks to ensure that the maximum number of concurrent users
     * logged in at any one time does not exceed the limit set by the
     * administrator user.
     * 
     * @param numberOfUserSessions
     *          Number of acceptable concurrent user set in the repdb
     * @return true if the maximum number of concurrent users logged in at any one
     *         time is reached
     */
    public boolean checkMaximumConcurrentUserSessionReached(final int numberOfUserSessions) {
        boolean maximiumConcurrentUserSessionReached = false;
        final ApplicationConfigManager acm = ApplicationConfigManagerFactory.getApplicationConfigManager();
        final boolean maxUserSessionsReached = (numberOfUserSessions >= acm.getMaxUserSessions());
        if (maxUserSessionsReached) {
            maximiumConcurrentUserSessionReached = true;
            responseCode = ERR_MAXIMUM_NUMBER_OF_CONCURRENT_USER_REACHED_MSG.getResponseCode();
        }
        return maximiumConcurrentUserSessionReached;
    }

    /**
     *  Check if users locked out.
     * @return true if user lock out.
     */
    public boolean checkUserLockout() {
        if (ApplicationConfigManagerFactory.getApplicationConfigManager().lockoutUsers()) {
            responseCode = USER_LOCKOUT_MESSAGE.getResponseCode();
            return true;
        }
        return false;
    }

    /**
     * Required for JUnit tests to run
     * 
     * @return
     */
    protected ValidateUser getValidUserClass() {
        return new ValidateUser();
    }

    private void parseException(final Exception ex) {
        if (ex.getMessage().contains(ERR_LDAP_CODE_49_MSG.getResponseMessage())) {
            responseCode = ERR_INVALID_USER_DETAILS_ENTERED.getResponseCode();
        } else if (ex.getMessage().contains(ERR_LDAP_CODE_32_MSG.getResponseMessage())) {
            // Do noting in this situation as this is just saying that no attribute
            // were found under this name
            // Valid for administrator as they will NOT have the password overlay
            // applied
        } else if (ex.getMessage().contains(ERR_FAILED_TO_FIND_PERMISSIONS_OF_USER.getResponseMessage())) {
            responseCode = ERR_FAILED_TO_FIND_PERMISSIONS_OF_USER.getResponseCode();
        } else {
            responseCode = ERR_CONTACT_ADMINISTRATOR_ERROR_MSG.getResponseCode();
        }
    }
}
