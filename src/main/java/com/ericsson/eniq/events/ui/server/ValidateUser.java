/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.server;

import static com.ericsson.eniq.events.ui.login.shared.AuthenticationConstants.*;
import static com.ericsson.eniq.events.ui.login.shared.AuthenticationConstants.VALIDATION_RESPONSE.*;
import static com.ericsson.eniq.ldap.management.LDAPAttributes.*;
import static com.ericsson.eniq.ldap.util.LDAPConstants.*;

import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.ericsson.eniq.ldap.management.LDAPConnectionFactory;
import com.ericsson.eniq.ldap.management.LDAPException;
import com.ericsson.eniq.ldap.util.ENIQEventsAccessControlUtil;
import com.ericsson.eniq.ldap.vo.LoginVO;

/**
 * This class is used to check if the user is associated with the correct role.
 * 
 * @author estepdu
 * 
 */
public class ValidateUser {

    /**
     * Check if the user is a valid UI user. An error message will be displayed on the page if the user is not associated with a role.
     *
     * @param userName User name entered on the page
     * @param userPassword User password name entered on the page
     */
    public boolean isNotValidEniqEventUIUser(final String userName, final String userPassword) throws NamingException {

        boolean isNotValidUser = true;
        DirContext ctx = null;
        try {
            ctx = LDAPConnectionFactory.getConnection(userName, userPassword);
            isNotValidUser = false;
            if (ctx != null) {
                ctx.close();
            }
        } catch (final Exception e) {
            throw new NamingException(ERR_LDAP_CODE_49_MSG.getResponseMessage());
        }
        return isNotValidUser;
    }

    public boolean hasPermissions(final String userName, final String userPassword) throws NamingException {
        boolean hasPermissions = false;
        final LoginVO loginVO = new LoginVO();
        loginVO.setLoginId(userName);
        loginVO.setPassword(userPassword);
        try {
            final Set<String> permissions = ENIQEventsAccessControlUtil.findPermissionsByUserId(loginVO);
            if (null != permissions && 0 != permissions.size()) {
                hasPermissions = true;
            }
        } catch (final LDAPException e) {
            throw new NamingException(ERR_FAILED_TO_FIND_PERMISSIONS_OF_USER.getResponseMessage() + userName);
        }
        return hasPermissions;
    }

    /**
     * Check if the user password is required to be changed this occurs if the user account has been unlocked after such the user is required to change their password.
     * @param userName
     * @param userPassword
     */
    public boolean passwordResetRequired(final String userName, final String userPassword) throws NamingException {

        DirContext ctx = null;
        boolean passwordResetRequired = false;
        try {
            ctx = LDAPConnectionFactory.getConnection(userName, userPassword);
        } catch (final Exception e) {
            throw new NamingException(ERR_WHILE_TRYING_TO_CONNECT_TO_LDAP_MSG.getResponseMessage());
        }

        if (ctx != null) {

            final String param = new StringBuilder(UID).append(OP_EQUALS).append(userName).append(OP_COMMA)
                    .append(USERS_BASE_DN).toString();

            final Attributes attrs = ctx.getAttributes(param);

            final NamingEnumeration<?> memberAtts = getAttributeForUser(attrs, RESET_PWD_ONLOGIN);
            if (memberAtts != null) {

                for (final NamingEnumeration<?> vals = memberAtts; vals.hasMoreElements();) {

                    if (vals.nextElement().toString().trim().compareToIgnoreCase(TRUE) == 0) {
                        passwordResetRequired = true;
                        break;
                    }
                }
            }
            ctx.close();
        }
        return passwordResetRequired;
    }

    private NamingEnumeration<?> getAttributeForUser(final Attributes attrs, final String attName)
            throws NamingException {
        NamingEnumeration<?> nm = null;
        final Attribute memberAtts = attrs.get(attName);
        if (memberAtts != null) {
            nm = memberAtts.getAll();
        }
        return nm;
    }

    public boolean isUserPasswordExpired(final String userName, final String userPassword) throws NamingException {

        DirContext ctx = null;
        boolean userPasswordExpired = false;
        try {
            ctx = LDAPConnectionFactory.getConnection(userName, userPassword);
        } catch (final Exception e) {
            throw new NamingException(ERR_WHILE_TRYING_TO_CONNECT_TO_LDAP_MSG.getResponseMessage());
        }
        if (ctx != null) {

            //lets get the domain lockout duration policy
            Attributes attrs = ctx.getAttributes(PASSWORD_POLICY_DN);

            //Create the search controls
            final SearchControls searchCtls = new SearchControls();

            //Specify the attributes to return
            final String returnedAtts[] = { PWD_GRACE_USE_TIME };
            //String returnedAtts[]={"pwdAccountLockedTime"};
            searchCtls.setReturningAttributes(returnedAtts);

            //Specify the search scope
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            //Specify the Base for the search
            final String searchBase = new StringBuilder(UID).append(OP_EQUALS).append(userName).append(OP_COMMA)
                    .append(USERS_BASE_DN).toString();

            //Filter that is applied
            final String filter = FILTER_OBJECTCLASS_ANY;

            //Search for objects using the filter
            final NamingEnumeration<?> answer = ctx.search(searchBase, filter, searchCtls);

            //Loop through the search results
            while (answer.hasMoreElements()) {
                final SearchResult sr = (SearchResult) answer.next();

                attrs = sr.getAttributes();

                if (attrs.getAll().hasMoreElements()) {
                    userPasswordExpired = true;
                }
            }
            ctx.close();
        }
        return userPasswordExpired;
    }
}