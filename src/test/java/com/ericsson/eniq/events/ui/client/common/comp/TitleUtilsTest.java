/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author ealeerm
 * @since 06/2012
 */
public class TitleUtilsTest {

    @Test
    public void removeDuplicateWords_NoDuplicates_ReturnsTheSameContent() {
        String result = TitleUtils.removeDuplicateWords("Access Area Group", "Event Analysis Summary");
        assertEquals("Event Analysis Summary", result);
    }

    @Test(expected = NullPointerException.class)
    public void removeDuplicateWords_NullContent_ThrowsException() {
        TitleUtils.removeDuplicateWords("Access Area Group", null);
    }

    @Test
    public void removeDuplicateWords_NullDuplicatePhrase_ReturnsTheSameContent() {
        String result = TitleUtils.removeDuplicateWords(null, "Abc");
        assertEquals("Abc", result);
    }

    @Test
    public void removeDuplicateWords_EmptyDuplicatePhrase_ReturnsTheSameContent() {
        String result = TitleUtils.removeDuplicateWords("", "Abc1");
        assertEquals("Abc1", result);
    }

    @Test
    public void removeDuplicateWords_EmptyTrimmedDuplicatePhrase_ReturnsTheSameContent() {
        String result = TitleUtils.removeDuplicateWords(" \t\n ", "Abc1");
        assertEquals("Abc1", result);
    }

    @Test
    public void removeDuplicateWords_NullDuplicatePhraseAndNotTrimmedContent_ReturnsTrimmedContent() {
        String result = TitleUtils.removeDuplicateWords(null, " \nAbc\t ");
        assertEquals("Abc", result);
    }

    @Test
    public void removeDuplicateWords_PartialDuplicatesInPhrase_ReturnsUpdatedContent() {
        String result = TitleUtils.removeDuplicateWords("Access Area Group", "Access Area : Event Analysis Summary");
        assertEquals("Event Analysis Summary", result);
    }

    @Test
    public void removeDuplicateWords_PartialDuplicatesInContent_ReturnsUpdatedContent() {
        String result = TitleUtils.removeDuplicateWords("Access Area", "Access Area Group : Event Analysis Summary");
        assertEquals("Group : Event Analysis Summary", result);
    }

    @Test
    public void removeDuplicateWords_CompleteDuplicatesInContent_ReturnsUpdatedContent() {
        String result = TitleUtils.removeDuplicateWords("Access Area Group", "Access Area Group: Event Analysis Summary");
        assertEquals("Event Analysis Summary", result);
    }

    @Test
    public void removeDuplicateWords_PartialDuplicatesInPhraseWithDash_ReturnsUpdatedContent() {
        String result = TitleUtils.removeDuplicateWords("Access Area Group", "Access Area - Event Analysis Summary");
        assertEquals("Event Analysis Summary", result);
    }

    @Test
    public void removeDuplicateWords_PartialDuplicatesInContentWithDash_ReturnsUpdatedContent() {
        String result = TitleUtils.removeDuplicateWords("Access Area", "Access Area Group - Event Analysis Summary");
        assertEquals("Group - Event Analysis Summary", result);
    }

    @Test
    public void removeDuplicateWords_CompleteDuplicatesInContentWithDash_ReturnsUpdatedContent() {
        String result = TitleUtils.removeDuplicateWords("Access Area Group",
                "Access Area Group - Event Analysis Summary");
        assertEquals("Event Analysis Summary", result);
    }

    @Test
    public void removeDuplicateWords_CompleteDuplicatesAtTheEndOfContentWithDash_ReturnsUpdatedContent() {
        String result = TitleUtils.removeDuplicateWords("Access Area Group",
                "Access Area Group - ");
        assertEquals("", result);
    }

    @Test
    public void removeDuplicateWords_ColonAtTheStart_ReturnsUpdatedContent() {
        String result = TitleUtils.removeDuplicateWords("", " : Access Area Group");
        assertEquals("Access Area Group", result);
    }

    @Test
    public void removeDuplicateWords_DashesAtTheStart_ReturnsUpdatedContent() {
        String result = TitleUtils.removeDuplicateWords("", " - Access Area Group");
        assertEquals("Access Area Group", result);
    }

    @Test
    public void removeDuplicateWords_DashesAtTheEnd_ReturnsUpdatedContent() {
        String result = TitleUtils.removeDuplicateWords("", "Access Area Group -  - ");
        assertEquals("Access Area Group", result);
    }

    @Test
    public void removeDuplicateWords_ColonAtTheStartAndDuplicatesInContent_ReturnsUpdatedContent() {
        String result = TitleUtils.removeDuplicateWords("Access Area Group",
                "Access Area Group  :   Event Analysis Summary");
        assertEquals("Event Analysis Summary", result);
    }

    @Test
    public void removeDuplicateWords_OnlyColonsAndSpacesInContent_ReturnsUpdatedContent() {
        String result = TitleUtils.removeDuplicateWords("Access Area", "  ::  : : ");
        assertEquals("", result);
    }

    @Test
    public void removeDuplicateWords_ManyColonsAtTheStartAndDuplicatesInContent_ReturnsUpdatedContent() {
        String result = TitleUtils.removeDuplicateWords("Access Area Group",
                "Access Area    :  ::::::::::::::: Unknown :: ");
        assertEquals("Unknown ::", result);
    }

    @Test
    public void adjustTitle_ColonsAndSpaceSets_ReturnsUpdatedTitle() {
        String result = TitleUtils.adjustTitle("Access Area    :  ::::::::::Group:::::  :: ");
        assertEquals("Access Area Group", result);
    }

    @Test
    public void adjustTitle_EmptyTitle_ReturnsTheSameTitle() {
        String result = TitleUtils.adjustTitle("");
        assertEquals("", result);
    }

    @Test
    public void makeContentInBracketsFirst_SwapNeeded_ReturnsUpdatedTitle() {
        String result = TitleUtils.makeContentInBracketsFirst("Event Analysis (GSM Call Failure)");
        assertEquals("GSM Call Failure Event Analysis", result);
    }

    @Test
    public void makeContentInBracketsFirst_WrongFormatWithNoClosingBracket_ReturnsOriginalTitle() {
        String result = TitleUtils.makeContentInBracketsFirst("Event Analysis (GSM Call Failure");
        assertEquals("Event Analysis (GSM Call Failure", result);
    }

    @Test
    public void makeContentInBracketsFirst_WrongFormatWithWrongBracketsOrder_ReturnsOriginalTitle() {
        String result = TitleUtils.makeContentInBracketsFirst("Event Analysis )GSM Call Failure(");
        assertEquals("Event Analysis )GSM Call Failure(", result);
    }

    @Test
    public void makeContentInBracketsFirst_NullTitle_ReturnsEmptyTitle() {
        String result = TitleUtils.makeContentInBracketsFirst(null);
        assertEquals("", result);
    }

    @Test
    public void makeContentInBracketsFirst_EmptyTitle_ReturnsEmptyTitle() {
        String result = TitleUtils.makeContentInBracketsFirst("   \t\n  \t ");
        assertEquals("", result);
    }

    @Test
    public void adjustTitleByPrefixes_TwoPrefixesWithOneDuplicate_ReturnsUpdatedTitle() {
        String result = TitleUtils.adjustTitleByPrefixes("Event Analysis Summary (GSM Call Failure)",
                "DG_GroupXXX", "GSM Call Failure");
        assertEquals("Event Analysis Summary", result);
    }
}
