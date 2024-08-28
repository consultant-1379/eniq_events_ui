/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.language;

/**
 * @author eemecoy
 *
 */
public class Grammar {

    private static final String LOWER_CASE_S = "s";

    public String getNounEnding(final int i) {
        if (i == 1) {
            return "";
        }
        return LOWER_CASE_S;
    }

    public String pluralizeNoun(final String noun) {
        final StringBuilder stringBuilder = new StringBuilder(noun);
        stringBuilder.append(LOWER_CASE_S);
        return stringBuilder.toString();
    }

}
