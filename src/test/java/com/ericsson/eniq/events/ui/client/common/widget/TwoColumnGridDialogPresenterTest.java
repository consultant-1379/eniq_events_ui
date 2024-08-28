/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

/*
 * @author eeikbe
 * @since April 2014
 *
 */

package com.ericsson.eniq.events.ui.client.common.widget;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.datatype.MetaMenuItemDataType;
import com.ericsson.eniq.events.ui.client.datatype.VerticalGridColumnHeaders;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


public class TwoColumnGridDialogPresenterTest  extends TestEniqEventsUI {

    MetaMenuItemDataType mockedMetaMenuItemDataType;
    VerticalGridColumnHeaders mockedVerticalGridColumnHeaders;

    @Before
    public void setUp() {
        mockedMetaMenuItemDataType = context.mock(MetaMenuItemDataType.class);
        mockedVerticalGridColumnHeaders = context.mock(VerticalGridColumnHeaders.class);
    }

    @Test
    public void testTwoColumnGridDialogPresenterConstructor() {
        TwoColumnGridDialogPresenter presenter = new TwoColumnGridDialogPresenter(null, mockedEventBus);
        assertNotNull(presenter);
    }

    @Test
    public void testConvertTimeRemoveMiliseconds(){

        String dateString = "\"2014-04-11 12:45:00.123\"";
        String expected = "2014-04-11 12:45:00";
        TwoColumnGridDialogPresenter presenter = new TwoColumnGridDialogPresenter(null, mockedEventBus);
        String actual = presenter.convertTime(dateString, TwoColumnGridDialogPresenter.DATE_FORMAT_HH_MM_SS_SSS, TwoColumnGridDialogPresenter.DATE_FORMAT_HH_MM_SS);
        assertEquals(expected, actual);
    }

    @Test
    public void testConvertTimeRemoveSeconds(){

        String dateString = "\"2014-04-11 12:45:00.123\"";
        String expected = "2014-04-11 12:45";
        TwoColumnGridDialogPresenter presenter = new TwoColumnGridDialogPresenter(null, mockedEventBus);
        String actual = presenter.convertTime(dateString, TwoColumnGridDialogPresenter.DATE_FORMAT_HH_MM_SS_SSS, TwoColumnGridDialogPresenter.DATE_FORMAT_HH_MM);
        assertEquals(expected, actual);
    }

    @Test
    public void testConvertTimeAmericanDateFormat(){

        String dateString = "\"2014-04-11 12:45:00.123\"";
        String expected = "04-11-2014 12:45:00";
        TwoColumnGridDialogPresenter presenter = new TwoColumnGridDialogPresenter(null, mockedEventBus);
        String actual = presenter.convertTime(dateString, TwoColumnGridDialogPresenter.DATE_FORMAT_HH_MM_SS_SSS, "MM-dd-yyyy HH:mm:ss");
        assertEquals(expected, actual);
    }

    @Test
    public void testConvertTimeNotTime(){
        String dateString = "willywonka";
        String expected = "willywonka";
        TwoColumnGridDialogPresenter presenter = new TwoColumnGridDialogPresenter(null, mockedEventBus);
        String actual = presenter.convertTime(dateString, TwoColumnGridDialogPresenter.DATE_FORMAT_HH_MM_SS_SSS, TwoColumnGridDialogPresenter.DATE_FORMAT_HH_MM_SS);
        assertEquals(expected, actual);
    }

    @Test
    public void testConvertTimeEmptyString(){
        String dateString = "";
        String expected = "";
        TwoColumnGridDialogPresenter presenter = new TwoColumnGridDialogPresenter(null, mockedEventBus);
        String actual = presenter.convertTime(dateString, TwoColumnGridDialogPresenter.DATE_FORMAT_HH_MM_SS_SSS, TwoColumnGridDialogPresenter.DATE_FORMAT_HH_MM_SS);
        assertEquals(expected, actual);
    }
}
