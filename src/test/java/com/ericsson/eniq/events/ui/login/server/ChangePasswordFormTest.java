/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.server;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.login.common.JMockTestBaseClass;
import com.ericsson.eniq.events.ui.server.ChangePasswordForm;
import com.ericsson.eniq.events.ui.server.ChangeUserDetails;
import com.ericsson.eniq.events.ui.server.ValidateUser;

import java.lang.reflect.Method;

/**
 * @author estepdu
 *
 */
public class ChangePasswordFormTest extends JMockTestBaseClass {

    private StubbedChangePasswordForm changePasswordFormUnderTest;

    private ValidateUser mockedValidUserClass;

    private ChangeUserDetails mockedChangeUserDetailsClass;

    final static String TEST_EXISTING_USER_USERNAME = "Test Existing User Name";

    final static String TEST_EXISTING_USER_PASSWORD = "Test Existing User Password";

    final static String TEST_NEW_USER_PASSWORD = "New U$er_Pa$$w0rd1";

    final static String TEST_NEW_USER_PASSWORD_CONFIRMED = "New User Password Confirmed";

    @Before
    public void setup() {
        changePasswordFormUnderTest = new StubbedChangePasswordForm();
        mockedValidUserClass = context.mock(ValidateUser.class);
        mockedChangeUserDetailsClass = context.mock(ChangeUserDetails.class);
    }

    @After
    public void tearDown() {
        changePasswordFormUnderTest = null;
        mockedValidUserClass = null;
        mockedChangeUserDetailsClass = null;
    }

    @Test
    public void testExistingUserNameSet() {
        changePasswordFormUnderTest.setExistingUserName(TEST_EXISTING_USER_USERNAME);
        assertThat(changePasswordFormUnderTest.getExistingUserName(), is(TEST_EXISTING_USER_USERNAME));
    }

    @Test
    public void testExistingUserPasswordSet() {
        changePasswordFormUnderTest.setExistingUserPassword(TEST_EXISTING_USER_PASSWORD);
        assertThat(changePasswordFormUnderTest.getExistingUserPassword(), is(TEST_EXISTING_USER_PASSWORD));
    }

    @Test
    public void testNewUserPasswordSet() {
        changePasswordFormUnderTest.setNewUserPassword(TEST_NEW_USER_PASSWORD);
        assertThat(changePasswordFormUnderTest.getNewUserPassword(), is(TEST_NEW_USER_PASSWORD));
    }

    @Test
    public void testClearLoginFormDetails() {
        changePasswordFormUnderTest.clearChangePasswordFormDetails();
        assertThat(changePasswordFormUnderTest.getExistingUserName(), is(""));
        assertThat(changePasswordFormUnderTest.getExistingUserPassword(), is(""));
        assertThat(changePasswordFormUnderTest.getNewUserPassword(), is(""));
    }

    @Test
    public void testPositiveProcess() throws Exception {
        changePasswordFormUnderTest.setExistingUserName(TEST_EXISTING_USER_USERNAME);
        changePasswordFormUnderTest.setExistingUserPassword(TEST_EXISTING_USER_PASSWORD);
        changePasswordFormUnderTest.setNewUserPassword(TEST_NEW_USER_PASSWORD);

        //        final ValidateUser mockedValidUserClass = context.mock(ValidateUser.class);
        context.checking(new Expectations() {
            {
                one(mockedChangeUserDetailsClass).changePassword(with(any(String.class)), with(any(String.class)),
                        with(any(String.class)));
            }
        });
        assertThat(changePasswordFormUnderTest.process(), is(true));
    }

    private class StubbedChangePasswordForm extends ChangePasswordForm {

        @Override
        protected ValidateUser getValidUserClass() {
            return mockedValidUserClass;
        }

        @Override
        protected ChangeUserDetails getChangeUserDetailsClass() {
            return mockedChangeUserDetailsClass;
        }
    }

}
