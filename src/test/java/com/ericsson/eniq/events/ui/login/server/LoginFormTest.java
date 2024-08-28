/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.server;

import static com.ericsson.eniq.events.ui.login.shared.AuthenticationConstants.VALIDATION_RESPONSE.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import javax.naming.NamingException;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.login.common.JMockTestBaseClass;
import com.ericsson.eniq.events.ui.server.LoginForm;
import com.ericsson.eniq.events.ui.server.ValidateUser;

/**
 * @author estepdu
 *
 */
public class LoginFormTest extends JMockTestBaseClass {

    private StubbedLoginForm loginFormUnderTest;

    private ValidateUser mockedValidUserClass;

    final static String TEST_USER_USERNAME = "Test User Name";

    final static String TEST_USER_PASSWORD = "Test User Password";

    @Before
    public void setup() {
        loginFormUnderTest = new StubbedLoginForm();
        mockedValidUserClass = context.mock(ValidateUser.class);
    }

    @After
    public void tearDown() {
        loginFormUnderTest = null;
        mockedValidUserClass = null;
    }

    /**
     * Test method for {@link com.ericsson.eniq.events.ui.server.LoginForm#getUserName()}.
     */
    @Test
    public void testUserNameSet() {
        loginFormUnderTest.setUserName(TEST_USER_USERNAME);
        assertThat(loginFormUnderTest.getUserName(), is(TEST_USER_USERNAME));
    }

    /**
     * Test method for {@link com.ericsson.eniq.events.ui.server.LoginForm#setUserName(java.lang.String)}.
     */
    @Test
    public void testUserPasswordSet() {
        loginFormUnderTest.setUserPassword(TEST_USER_PASSWORD);
        assertThat(loginFormUnderTest.getUserPassword(), is(TEST_USER_PASSWORD));
    }

    /**
     * Test method for {@link com.ericsson.eniq.events.ui.server.LoginForm#clearLoginFormDetails()}.
     */
    @Test
    public void testClearLoginFormDetails() {
        loginFormUnderTest.clearLoginFormDetails();
        assertThat(loginFormUnderTest.getUserName(), is(""));
        assertThat(loginFormUnderTest.getUserPassword(), is(""));
    }

    /**
     * Test method for {@link com.ericsson.eniq.events.ui.server.LoginForm#process()}.
     */
    @Test
    public void testPositiveProcess() throws NamingException {
        loginFormUnderTest.setUserName(TEST_USER_USERNAME);
        loginFormUnderTest.setUserPassword(TEST_USER_PASSWORD);

        //        final ValidateUser mockedValidUserClass = context.mock(ValidateUser.class);
        context.checking(new Expectations() {
            {

                one(mockedValidUserClass).isNotValidEniqEventUIUser(with(any(String.class)), with(any(String.class)));
                will(returnValue(false));
                one(mockedValidUserClass).hasPermissions(with(any(String.class)), with(any(String.class)));
                will(returnValue(true));
                one(mockedValidUserClass).passwordResetRequired(with(any(String.class)), with(any(String.class)));
                will(returnValue(false));
                one(mockedValidUserClass).isUserPasswordExpired(with(any(String.class)), with(any(String.class)));
                will(returnValue(false));
            }
        });
        assertThat(loginFormUnderTest.process(), is(true));
    }

    @Test
    public void testNotAValidUIUser() throws NamingException {
        loginFormUnderTest.setUserName(TEST_USER_USERNAME);
        loginFormUnderTest.setUserPassword(TEST_USER_PASSWORD);

        context.checking(new Expectations() {
            {
                one(mockedValidUserClass).isNotValidEniqEventUIUser(with(any(String.class)), with(any(String.class)));
                will(returnValue(true));
            }
        });
        assertThat(loginFormUnderTest.process(), is(false));
        //        assertThat(loginFormUnderTest.getLoginErrorMessageByErrorType(LDAP.toString()), is(ERR_USER_NOT_A_UI_USER.getErrorDescription()));
    }

    @Test
    public void testPasswordResetRequired() throws NamingException {
        loginFormUnderTest.setUserName(TEST_USER_USERNAME);
        loginFormUnderTest.setUserPassword(TEST_USER_PASSWORD);

        //        final ValidateUser mockedValidUserClass = context.mock(ValidateUser.class);
        context.checking(new Expectations() {
            {
                one(mockedValidUserClass).isNotValidEniqEventUIUser(with(any(String.class)), with(any(String.class)));
                will(returnValue(false));
                one(mockedValidUserClass).hasPermissions(with(any(String.class)), with(any(String.class)));
                will(returnValue(true));
                one(mockedValidUserClass).passwordResetRequired(with(any(String.class)), with(any(String.class)));
                will(returnValue(true));
            }
        });
        assertThat(loginFormUnderTest.process(), is(false));
        assertThat(loginFormUnderTest.getLoginResponseCode(),
                is(ERR_ACCOUNT_PASSWORD_REQUIRED_TO_BE_CHANGED.getResponseCode()));
    }

    @Test
    public void testUserPasswordExpired() throws NamingException {
        loginFormUnderTest.setUserName(TEST_USER_USERNAME);
        loginFormUnderTest.setUserPassword(TEST_USER_PASSWORD);

        //        final ValidateUser mockedValidUserClass = context.mock(ValidateUser.class);
        context.checking(new Expectations() {
            {
                one(mockedValidUserClass).isNotValidEniqEventUIUser(with(any(String.class)), with(any(String.class)));
                will(returnValue(false));
                one(mockedValidUserClass).hasPermissions(with(any(String.class)), with(any(String.class)));
                will(returnValue(true));
                one(mockedValidUserClass).passwordResetRequired(with(any(String.class)), with(any(String.class)));
                will(returnValue(false));
                one(mockedValidUserClass).isUserPasswordExpired(with(any(String.class)), with(any(String.class)));
                will(returnValue(true));
            }
        });
        assertThat(loginFormUnderTest.process(), is(false));
        assertThat(loginFormUnderTest.getLoginResponseCode(),
                is(ERR_PASSWORD_IN_PASSWORD_EXPIRED_ERROR_MSG.getResponseCode()));
    }

    private class StubbedLoginForm extends LoginForm {

        @Override
        protected ValidateUser getValidUserClass() {
            return mockedValidUserClass;
        }
    }

}
