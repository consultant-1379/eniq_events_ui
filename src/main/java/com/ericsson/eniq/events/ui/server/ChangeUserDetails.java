/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.server;

import static com.ericsson.eniq.ldap.management.LDAPAttributes.*;
import static com.ericsson.eniq.ldap.util.LDAPConstants.*;

import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import com.ericsson.eniq.events.ui.login.shared.AuthenticationConstants.VALIDATION_RESPONSE;
import com.ericsson.eniq.ldap.management.LDAPConnectionFactory;

/**
 * This class is user to change attributes for a given user in LDAP.
 * 
 * @author estepdu
 * @since May 2010
 *
 */
public class ChangeUserDetails {

    /**
     * This method will change the user attributes when a user changes their password.
     * There are two attributes that need to change. One is the user password itself and
     * other is the resetPasswordOnLogin. The latter attribute is required to be set to false
     * once a user has been unlocked by an administrator.
     * 
     * @param existingUserName User name from the ChangePassword.jsp page.
     * @param existingUserPassword Password from the ChangePassword.jsp page.
     * @param newUserPassword The new user password the user wishes to change their password to.
     */
    public void changePassword(final String existingUserName, final String existingUserPassword,
            final String newUserPassword) throws NamingException {

        DirContext ctx = null;
        try {
            ctx = LDAPConnectionFactory.getConnection(existingUserName, existingUserPassword);
        } catch (final Exception ex) {
            throw new NamingException(VALIDATION_RESPONSE.ERR_WHILE_TRYING_TO_CONNECT_TO_LDAP_MSG.getResponseMessage());
        }
        try {
            if (ctx != null) {

                final String name = new StringBuilder(UID).append(OP_EQUALS).append(existingUserName).append(OP_COMMA)
                        .append(USERS_BASE_DN).toString();

                final ModificationItem[] mods = new ModificationItem[2];

                mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(USER_PASSWORD,
                        newUserPassword));
                mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(RESET_PWD_ONLOGIN,
                        FALSE));

                ctx.modifyAttributes(name, mods);

            }
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }
}
