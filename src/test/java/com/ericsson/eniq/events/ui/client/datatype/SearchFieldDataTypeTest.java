/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

/**
 * @author eeicmsy
 * @since March 2010
 *
 */
public class SearchFieldDataTypeTest extends TestEniqEventsUI {

    SearchFieldDataType objectToTest;

    @After
    public void tearDown() {
        objectToTest = null;
    }

    @Test
    public void testToString() throws Exception {
        final String expected = "Some node name";
        final String[] urlParams = new String[] { ("node=" + expected), "type=SGSN" };
        objectToTest = new SearchFieldDataType(expected, urlParams, null, null, false, "", null, false);

        assertEquals("toString just returned the node part", expected, objectToTest.toString());

    }

    @Test
    public void cleansKey() throws Exception {

        final String[] beforeURLParams = new String[] { ("node=MYNode"), "key=SUM", "type=SGSN" };
        objectToTest = new SearchFieldDataType("MYNode", beforeURLParams, null, null, false, "", null, false);

        final String expectedAfter = "&node=MYNode&type=SGSN";

        objectToTest.clean();
        assertEquals("url created as expected ", expectedAfter, objectToTest.getSearchFieldURLParams(false));

    }

    @Test
    public void metaDataKeysLoadsOk() throws Exception {
        final String[] urlParams = new String[] { ("node=whatever"), "type=SGSN" };
        objectToTest = new SearchFieldDataType("whatever", urlParams, null, null, false, "", null, false);

        objectToTest.setMetaDataKeys("CS,PS ");

        final List<String> expectedCollection = new ArrayList<String>();
        expectedCollection.add("CS");
        expectedCollection.add("PS");

        final List<String> actual = objectToTest.getMetaDataKeys();
        assertEquals("Converted JsonObjectWrapper keys to collection ok ", expectedCollection, actual);

    }

    @Test
    public void getSearchFieldURLParamsEnd() throws Exception {
        final String expected = "&node=MYNode&type=SGSN";
        final String[] urlParams = new String[] { ("node=MYNode"), "type=SGSN" };
        objectToTest = new SearchFieldDataType(expected, urlParams, null, null, false, "", null, false);

        assertEquals("url created as expected ", expected, objectToTest.getSearchFieldURLParams(false));

    }

    @Test
    public void getSearchFieldURLParamsStart() throws Exception {
        final String expected = "?node=MYNode&type=SGSN";
        final String[] urlParams = new String[] { ("node=MYNode"), "type=SGSN" };
        objectToTest = new SearchFieldDataType(expected, urlParams, null, null, false, "", null, false);

        assertEquals("url created as expected ", expected, objectToTest.getSearchFieldURLParams(true));

    }

    @Test
    public void getFixedPathSearchFieldURLParams() throws Exception {

        final String expected = "?searchParam=MYNode"; // exlude and ignore anything else

        final String[] urlParams = new String[] { ("node=MYNode"), "type=SGSN" };
        objectToTest = new SearchFieldDataType("MYNode", urlParams, null, null, false, "", null, false);

        objectToTest.setPathMode(true);

        assertEquals("fixed path url params created as expected ", expected, objectToTest.getSearchFieldURLParams(true));
    }

    @Test
    public void testEquals() throws Exception {
        final String nodeName = "some string";
        final String[] urlParams = new String[] { ("node=" + nodeName), "type=SGSN" };
        objectToTest = new SearchFieldDataType(nodeName, urlParams, null, null, false, "", null, false);

        final SearchFieldDataType theOtherObject = new SearchFieldDataType(nodeName, urlParams, null, null, false, "", null, false);

        assertEquals("These two are the same", true, objectToTest.equals(theOtherObject));
    }

    @Test
    public void testNotEquals() throws Exception {
        final String nodeName = "some string";
        final String[] urlParams = new String[] { ("node=" + nodeName), "type=SGSN" };
        objectToTest = new SearchFieldDataType(nodeName, urlParams, null, null, false, "", null, false);

        final String[] differentUrlParams = new String[] { ("node=" + nodeName), "type=BSC" };
        final SearchFieldDataType theOtherObject = new SearchFieldDataType(nodeName, differentUrlParams, null, null,
                false, "", null, false);

        assertEquals("These two are not the same", false, objectToTest.equals(theOtherObject));
    }

    @Test
    public void testHashCode() throws Exception {
        final String nodeName = "some string";
        final String[] urlParams = new String[] { ("node=" + nodeName), "type=SGSN" };
        objectToTest = new SearchFieldDataType(nodeName, urlParams, null, null, false, "", null, false);

        final String[] differentUrlParams = new String[] { ("node=" + nodeName), "type=BSC" };
        final SearchFieldDataType theOtherObject = new SearchFieldDataType(nodeName, differentUrlParams, null, null,
                false, "", null, false);

        final SearchFieldDataType similarOtherObject = new SearchFieldDataType(nodeName, differentUrlParams, null, null,
                false, "", null, false);

        final Set<SearchFieldDataType> noDuplicates = new HashSet<SearchFieldDataType>();
        noDuplicates.add(objectToTest);
        noDuplicates.add(theOtherObject);
        noDuplicates.add(similarOtherObject);

        assertEquals("There should only be two", 2, noDuplicates.size());

    }

}
