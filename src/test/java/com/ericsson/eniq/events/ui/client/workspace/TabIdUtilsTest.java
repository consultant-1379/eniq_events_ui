package com.ericsson.eniq.events.ui.client.workspace;

import org.junit.Test;

import static com.ericsson.eniq.events.ui.client.workspace.TabIdUtils.generateTabId;
import static com.ericsson.eniq.events.ui.client.workspace.TabIdUtils.generateTabIdPrefix;
import static junit.framework.Assert.assertEquals;

/**
 * Test for {@link com.ericsson.eniq.events.ui.client.workspace.TabIdUtils}.
 *
 * @author ealeerm
 * @since Jun 15, 2012
 */
public class TabIdUtilsTest {

    /**
     * Tested method: {@link TabIdUtils#generateTabId(String tabIdPrefix, int index)}
     */
    @Test
    public void generateTabId_NullPrefix_ReturnsTabOnly() {
        assertEquals("3_TAB", generateTabId(null, 3));
        assertEquals("TAB", generateTabId(null, 0));
    }

    /**
     * Tested method: {@link TabIdUtils#generateTabId(String tabIdPrefix, int index)}
     */
    @Test
    public void generateTabId_EmptyPrefix_ReturnsTabOnly() {
        assertEquals("TAB", generateTabId("", 0));
        assertEquals("1_TAB", generateTabId("", 1));
    }

    /**
     * Tested method: {@link TabIdUtils#generateTabId(String tabIdPrefix, int index)}
     */
    @Test
    public void generateTabId_Usual_Returns() {
        assertEquals("Dear Sir or Madam_TAB", generateTabId(" Dear Sir or Madam \t", 0));
        assertEquals("Slaunche and be happy_7777777_TAB", generateTabId("Slaunche and be happy", 7777777));
    }

    /**
     * Tested method: {@link TabIdUtils#generateTabIdPrefix(String tabName)}
     */
    @Test
    public void generateTabIdPrefix_UsualTabName() {
        assertEquals("TABNAME", generateTabIdPrefix("tabName"));
    }

    /**
     * Tested method: {@link TabIdUtils#generateTabIdPrefix(String tabName)}
     */
    @Test
    public void generateTabIdPrefix_EmptyTabName() {
        assertEquals("", generateTabIdPrefix(""));
    }

    /**
     * Tested method: {@link TabIdUtils#generateTabIdPrefix(String tabName)}
     */
    @Test
    public void generateTabIdPrefix_NullTabName() {
        assertEquals("", generateTabIdPrefix(null));
    }

    /**
     * Tested method: {@link TabIdUtils#generateTabIdPrefix(String tabName)}
     */
    @Test
    public void generateTabIdPrefix_TabNameContainsUnderlines() {
        assertEquals("SLAUNCHE_AND_BE_HAPPY", generateTabIdPrefix("_Slaunche and_ be happy_"));
    }

    /**
     * Tested method: {@link TabIdUtils#generateTabIdPrefix(String tabName)}
     */
    @Test
    public void generateTabIdPrefix_TabNameWithSpaces() {
        assertEquals("WHERE_I_AM", generateTabIdPrefix(" Where I am? "));
    }

    /**
     * Tested method: {@link TabIdUtils#generateTabIdPrefix(String tabName)}
     */
    @Test
    public void generateTabIdPrefix_TabNameWithSpecialCharacters() {
        assertEquals("WHERE_ARE_CHARACTERS", generateTabIdPrefix(" Where are characters ' !@#\";:<>$%^&*(\r\n\t)_+|\"/\\'?"));
    }

    /**
     * Tested method: {@link TabIdUtils#generateTabIdPrefix(String tabName)}
     */
    @Test
    public void generateTabIdPrefix_TabNameWithMultiSpaces() {
        assertEquals("WHERE_ARE_SPACES", generateTabIdPrefix(" Where     are     spaces???  \r  \n  \t  )_+|\"/\\  ' ?  "));
    }
}
