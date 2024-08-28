/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.language;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.client.common.language.Grammar;

/**
 * @author eemecoy
 *
 */
public class GrammarTest {

    private Grammar grammar;

    @Before
    public void setup() {
        grammar = new Grammar();
    }

    @Test
    public void testPluralizeNoun() {
        final String noun = "cat";
        assertThat(grammar.pluralizeNoun(noun), is(noun + "s"));
    }

    @Test
    public void testGetNounEnding() {
        assertThat(grammar.getNounEnding(3), is("s"));
        assertThat(grammar.getNounEnding(0), is("s"));
        assertThat(grammar.getNounEnding(1), is(""));
    }

}
