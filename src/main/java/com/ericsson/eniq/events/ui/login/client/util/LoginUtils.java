/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.util;

/**
 * Location of any helper code for LoginPresenter class.
 * 
 * @author eriwals
 * @since May 2011
 * 
 */
public class LoginUtils {

    /** Number of repeating characters to check for
     * between old and new password */
    private static final int NO_REPEATING_CHARS = 3;

    // non-instantiable
    private LoginUtils() {
    }

    public static native boolean isIE()/*-{
		return navigator.userAgent.indexOf('MSIE') >= 0;
    }-*/;

    /**
     * Defines the possible values for password strength
     *
     */
    public enum PasswordStrength {

        POOR(1, "password strength: 1 of 5"), FAIR(2, "password strength: 2 of 5"), GOOD(3, "password strength: 3 of 5"), VERY_GOOD(
                4, "password strength: 4 of 5"), STRONG(5, "password strength: passed");
        PasswordStrength(final int strength, final String strengthTip) {
            this.strengthValue = strength;
            this.strengthTip = strengthTip;
        }

        public PasswordStrength getIncreasedStrength() {
            if (getStrengthValue() == POOR.getStrengthValue()) {
                return FAIR;
            }
            if (getStrengthValue() == FAIR.getStrengthValue()) {
                return GOOD;
            }
            if (getStrengthValue() == GOOD.getStrengthValue()) {
                return VERY_GOOD;
            }
            return STRONG;
        }

        private int getStrengthValue() {
            return this.strengthValue;
        }

        public String getStrengthTip() {
            return this.strengthTip;
        }

        private int strengthValue;

        private String strengthTip;
    };

    /**
     * Determine strength of provided password using regular expressions.
     * Also checks for repeated sequences of characters between old and
     * new password.
     * 
     * @param oldPassword  the previous password
     * @param newPassword the new password
     * 
     * @return PasswordStrength:
     *          STRONG  matches all four of the rules
     *          VERY_GOOD match any three of the rules
     *          GOOD    matches any two of the rules
     *          FAIR    matches any one of the rules
     *          POOR    matches none of the rules
     */
    public static PasswordStrength checkPasswordStrength(final String oldPassword, final String newPassword) {

        PasswordStrength passwordStrength = PasswordStrength.POOR;

        //must contain one digit from 0-9
        if (newPassword.matches(".*\\d.*")) {
            passwordStrength = passwordStrength.getIncreasedStrength();
        }

        //must contain one upper case character
        if (newPassword.matches(".*[A-Z].*")) {
            passwordStrength = passwordStrength.getIncreasedStrength();
        }

        //length at least 8 characters and maximum of 255
        if (newPassword.matches(".{8,255}")) {
            passwordStrength = passwordStrength.getIncreasedStrength();
        }

        if (!hasRepeatSequence(oldPassword, newPassword, NO_REPEATING_CHARS))  {
            passwordStrength = passwordStrength.getIncreasedStrength();
        }
        return passwordStrength;

    }

    /**
     * Checks if there are characters repeated from the old password.
     * @param oldPwd   The old password.
     * @param newPwd   The new password.
     * @param noRepeatingChars The number of repeating characters to check for.
     * @return  true if noRepeatingChars characters are repeated.
     */
    public static boolean hasRepeatSequence(final String oldPwd, final String newPwd, final int noRepeatingChars) {
        boolean hasRepeatSequence = false;

        // Handle null arguments
        if (oldPwd == null || newPwd == null) {
            // LOGGER.log(Level.WARNING, "Error checking password for repeating sequence of chars, null arguments supplied.");
            return hasRepeatSequence;
        }

        if (newPwd.length()>= noRepeatingChars) {
            for (int i=0; i<newPwd.length()-(noRepeatingChars -1); i++) {
                final String chunk = newPwd.substring(i, i+ noRepeatingChars);
                if (oldPwd.indexOf(chunk)>=0) {
                    hasRepeatSequence = true;
                    break;
                }
            }
        }

        return hasRepeatSequence;
    }
}
