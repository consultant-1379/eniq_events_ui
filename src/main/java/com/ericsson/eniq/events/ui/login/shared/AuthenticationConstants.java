/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.shared;

/**
 * This is a contants class used by the login and change password pages and java bean
 *
 * @author estepdu
 *
 */
public final class AuthenticationConstants {

    private AuthenticationConstants() {
    }

    //This is required as the UI classes cannot be accesses as they are not deployed
    public final static String EMPTY_STRING = "";

    public final static String ONE_SPACE = " ";

    public final static String FILTER_OBJECTCLASS_ANY = "(objectclass=*)";

    public static final String ENIQ_EVENTS_LDAP_URI = "ENIQ_EVENTS_LDAP_URI";

    public static final String ENIQ_EVENTS_JNDI_NAME = "Eniq_Event_Properties";

    public static final String ENIQ_EVENTS_UI_VERSION = "ENIQ_EVENTS_UI_VERSION";

    public static final String ENIQ_EVENTS_UI_COPYRIGHT = "ENIQ_EVENTS_UI_COPYRIGHT";

    public static final String ENIQ_EVENTS_SERVICES_URI = "ENIQ_EVENTS_SERVICES_URI";

    public static final String ENIQ_EVENTS_REQUEST_TIMEOUT_TIME_IN_MILLISECONDS = "ENIQ_EVENTS_REQUEST_TIMEOUT_TIME_IN_MILLISECONDS";

    public enum VALIDATION_RESPONSE {
        SUCCESS(1000, "Success"), ERR_USER_NOT_A_UI_USER(1001,
                "User does not have a role assigned. Please contact the system administrator"), ERR_ACCOUNT_PASSWORD_REQUIRED_TO_BE_CHANGED(
                1002, "Password change required"), ERR_INVALID_USER_DETAILS_ENTERED(1003,
                "Invalid user details entered, account locked or password expired."), ERR_CONTACT_ADMINISTRATOR_ERROR_MSG(1004,
                "Please contact the system administrator"), ERR_PASSWORD_IN_PASSWORD_HISTORY_ERROR_MSG(1005,
                "Unable to change password, please choose a password other than your last five passwords"), ERR_PASSWORD_IN_PASSWORD_EXPIRED_ERROR_MSG(
                1006, "Your password has expired. Please change your password"), ERR_MAXIMUM_NUMBER_OF_CONCURRENT_USER_REACHED_MSG(
                1007, "The maximum limit for concurrent connections has been reached"), USER_LOCKOUT_MESSAGE(1008,
                "Application is currently offline"), AUTHENTICATION_ERROR_MESSAGE(1009,
                "Authentication Error. Invalid Credentials"), ERR_UNEXPECTED_ERROR_DURING_AUTH(2001,
                "Unexpected error during authentication"), ERR_LDAP_CODE_19_MSG(6019, "LDAP: error code 19"), ERR_LDAP_CODE_32_MSG(
                6032, "LDAP: error code 32 - No Such Object"), ERR_FAILED_TO_FIND_PERMISSIONS_OF_USER(6044,
                "Grace limit exceeded for expired password. Contact system administrator"), ERR_LDAP_CODE_49_MSG(6049,
                "LDAP: error code 49 - Invalid Credentials"), ERR_WHILE_TRYING_TO_CONNECT_TO_LDAP_MSG(6099,
                "Error while trying to connect to LDAP");

        private String responseMessage;

        private int responseCode;

        private VALIDATION_RESPONSE(final int responseCode, final String responseMessage) {
            this.responseMessage = responseMessage;
            this.responseCode = responseCode;
        }

        public String getResponseMessage() {
            return responseMessage;
        }

        public int getResponseCode() {
            return responseCode;
        }

    }

    public static String getResponseMessageForCode(final int responseCode) {
        for (final VALIDATION_RESPONSE response : VALIDATION_RESPONSE.values()) {
            if (response.getResponseCode() == responseCode) {
                return response.getResponseMessage();
            }
        }
        return "System Error";
    }

    public static String getXmlResponseString(final int responseCode) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<response>");
        sb.append("<responseCode>" + responseCode + "</responseCode>");
        sb.append("</response>");
        return sb.toString();
    }

    public static boolean isResponseSuccess(final int responseCode) {
        return responseCode == VALIDATION_RESPONSE.SUCCESS.getResponseCode();
    }
}