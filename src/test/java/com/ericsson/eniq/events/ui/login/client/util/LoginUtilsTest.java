package com.ericsson.eniq.events.ui.login.client.util;

import org.junit.Test;

import static com.ericsson.eniq.events.ui.login.client.util.LoginUtils.PasswordStrength.FAIR;
import static com.ericsson.eniq.events.ui.login.client.util.LoginUtils.PasswordStrength.GOOD;
import static com.ericsson.eniq.events.ui.login.client.util.LoginUtils.PasswordStrength.POOR;
import static com.ericsson.eniq.events.ui.login.client.util.LoginUtils.PasswordStrength.STRONG;
import static com.ericsson.eniq.events.ui.login.client.util.LoginUtils.PasswordStrength.VERY_GOOD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Test for {@link com.ericsson.eniq.events.ui.login.client.util.LoginUtils}.
 *
 * @author ealeerm
 * @since Jun 7, 2012
 */
public class LoginUtilsTest {

    /**
     * Tests method: {@link com.ericsson.eniq.events.ui.login.client.util.LoginUtils#checkPasswordStrength(String oldPassword, String newPassword)}
     */
    @Test
    public void checkPasswordStrength_ReturnsPoorPassword() {
        assertEquals(POOR, LoginUtils.checkPasswordStrength("   ", "   "));
        assertEquals(POOR, LoginUtils.checkPasswordStrength("pass", "pass"));
        assertEquals(POOR, LoginUtils.checkPasswordStrength("portlet", "portlet"));
    }

    /**
     * Tests method: {@link com.ericsson.eniq.events.ui.login.client.util.LoginUtils#checkPasswordStrength(String oldPassword, String newPassword)}
     */
    @Test
    public void checkPasswordStrength_ReturnsFairPassword() {
        assertEquals("Password with length 8 should be FAIR strength", FAIR, LoginUtils.checkPasswordStrength("password", "password")); // 8 characters
        assertEquals("Password with one uppercase character should be FAIR strength", FAIR, LoginUtils.checkPasswordStrength("ppA", "ppA"));           // One uppercase
        assertEquals("Password with one uppercase character should be FAIR strength", FAIR, LoginUtils.checkPasswordStrength("cdA", "cdA"));         // One uppercase
        assertEquals("Password with one digit should be FAIR strength", FAIR, LoginUtils.checkPasswordStrength("p1a", "p1a"));
        assertEquals("Password with one digit should be FAIR strength", FAIR, LoginUtils.checkPasswordStrength("pp1", "pp1"));
        assertEquals("Password with one digit should be FAIR strength", FAIR, LoginUtils.checkPasswordStrength("b1b", "b1b"));
        assertEquals("Password with one digit should be FAIR strength", FAIR, LoginUtils.checkPasswordStrength("b1b2d", "b1b2d"));
        assertEquals("Password with no repeating characters should be FAIR strength", FAIR, LoginUtils.checkPasswordStrength("abc", "def"));
    }

    /**
     * Tests method: {@link com.ericsson.eniq.events.ui.login.client.util.LoginUtils#checkPasswordStrength(String oldPassword, String newPassword)}
     */
    @Test
    public void checkPasswordStrength_ReturnsGoodPassword() {
        assertEquals("Password with length 8 and digit should be GOOD strength", GOOD, LoginUtils.checkPasswordStrength("password1", "password1"));
        assertEquals("Password with length 8 and one uppercase should be GOOD strength", GOOD, LoginUtils.checkPasswordStrength("passwordD", "passwordD"));
        assertEquals("Password with one uppercase and one digit should be GOOD strength", GOOD, LoginUtils.checkPasswordStrength("aD1", "aD1"));
        assertEquals("Password with one uppercase and one digit should be GOOD strength", GOOD, LoginUtils.checkPasswordStrength("11V", "11V"));
        // With no repeating characters but one uppercase and one digit should be GOOD:
        assertEquals("Password with one uppercase and one digit should be GOOD strength", GOOD, LoginUtils.checkPasswordStrength("Def1", "Def2"));
    }

    /**
     * Tests method: {@link com.ericsson.eniq.events.ui.login.client.util.LoginUtils#checkPasswordStrength(String oldPassword, String newPassword)}
     */
    @Test
    public void checkPasswordStrength_ReturnsVeryGoodPassword() {
        assertEquals("Password with length 8, digit and no repeating chars should be VERY_GOOD strength",
                VERY_GOOD, LoginUtils.checkPasswordStrength("oldone123", "password1"));
        assertEquals("Password with length 8, upper case char and no repeating chars should be VERY_GOOD strength",
                VERY_GOOD, LoginUtils.checkPasswordStrength("oldone123", "passwordD"));
        assertEquals("Password with upper case char, digit and no repeating chars should be VERY_GOOD strength",
                VERY_GOOD, LoginUtils.checkPasswordStrength("", "D1"));
        assertEquals("Password with upper case char, digit and no repeating chars should be VERY_GOOD strength",
                VERY_GOOD, LoginUtils.checkPasswordStrength("", "11V"));
        assertEquals("Password with length 8, upper case char, digit but repeating chars should be VERY_GOOD strength",
                VERY_GOOD, LoginUtils.checkPasswordStrength("12345678", "12345678V"));
    }

    /**
     * Tests method: {@link com.ericsson.eniq.events.ui.login.client.util.LoginUtils#checkPasswordStrength(String oldPassword, String newPassword)}
     */
    @Test
    public void checkPasswordStrength_ReturnsStrongPassword() {
        assertEquals("Password with length 8, upper case char, digit and no repeating chars should be STRONG",
                STRONG, LoginUtils.checkPasswordStrength("anolderone", "passworD1"));
        assertEquals("Password with length 8, upper case char, digit and no repeating chars should be STRONG",
                STRONG, LoginUtils.checkPasswordStrength("anolderone", "1Dashboard"));
    }

    /**
     * Test method: {@link com.ericsson.eniq.events.ui.login.client.util.LoginUtils#hasRepeatSequence(String, String, int)}
     */
    @Test
    public void testHasRepeatSequence() throws Exception {
        final int MAX_REPEAT_SEQUENCE = 3;
        assertTrue("Identical passwords should return true for repeated sequence check",
                LoginUtils.hasRepeatSequence("test1", "test2", MAX_REPEAT_SEQUENCE));
        assertTrue("Identical passwords should return true for repeated sequence check",
                LoginUtils.hasRepeatSequence("abc", "abc", MAX_REPEAT_SEQUENCE));
        assertTrue("Different passwords with 3 characters repeated should return true for repeated sequence check",
                LoginUtils.hasRepeatSequence("123abc789", "456abcdef", MAX_REPEAT_SEQUENCE));

        assertFalse("Blank new password should return false for repeated sequence check",
                LoginUtils.hasRepeatSequence("abc", "", MAX_REPEAT_SEQUENCE));
        assertFalse("Blank old password should return false for repeated sequence check",
                LoginUtils.hasRepeatSequence("", "abc", MAX_REPEAT_SEQUENCE));
        assertFalse("Blank old and new password should return false for repeated sequence check",
                LoginUtils.hasRepeatSequence("", "", MAX_REPEAT_SEQUENCE));

        assertFalse("Null arguments for old and new password should return false for repeated sequence check",
                LoginUtils.hasRepeatSequence(null, "abc", MAX_REPEAT_SEQUENCE));
        assertFalse("Null arguments for old and new password should return false for repeated sequence check",
                LoginUtils.hasRepeatSequence("", null, MAX_REPEAT_SEQUENCE));
    }

    /**
     * Tests method: {@link com.ericsson.eniq.events.ui.login.client.util.LoginUtils.PasswordStrength#getStrengthTip()}
     */
    @Test
    public void getStrengthTip_ContainsPasswordAndStrength() {
        assertTrue(POOR.getStrengthTip().contains("password"));
        assertTrue(POOR.getStrengthTip().contains("strength"));
        assertTrue(FAIR.getStrengthTip().contains("password"));
        assertTrue(FAIR.getStrengthTip().contains("strength"));
        assertTrue(GOOD.getStrengthTip().contains("password"));
        assertTrue(GOOD.getStrengthTip().contains("strength"));
        assertTrue(STRONG.getStrengthTip().contains("password"));
        assertTrue(STRONG.getStrengthTip().contains("strength"));
    }
} 
