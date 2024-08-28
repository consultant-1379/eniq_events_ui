/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.dashboard.threshold;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.eniq.events.common.client.datatype.ThresholdDataType;
import com.ericsson.eniq.events.common.client.threshold.UpdateCommand;

@RunWith(MockitoJUnitRunner.class)
public class UpdateCommandTest {

    @Test
    public void execute_highest_InvokesSetHighest() {
        final ThresholdDataType threshold = new ThresholdDataType("777", ThresholdDataType.Format.NUMBER, "name", 10.0, 12.0);
        final UpdateCommand command = new UpdateCommand(threshold, true, 0.9d);

        command.execute();
        final String s = command.toString();
        System.out.println(s);
        assertTrue("0.9 must be in toString()", s.contains("0.9"));
        assertTrue("true must be in toString()", s.contains("true"));
        assertTrue("value must be in toString()", s.contains("value"));
        assertTrue("isHighest must be in toString()", s.contains("isHighest"));

    }

    @Test
    public void execute_notHighest_InvokesSetLowest() {
        final ThresholdDataType threshold = new ThresholdDataType("777", ThresholdDataType.Format.NUMBER, "name", 10.0, 12.0);
        final UpdateCommand command = new UpdateCommand(threshold, true, 0.1d);

        command.execute();

        final String s = command.toString();
        assertTrue("0.1 must be in toString()", s.contains("0.1"));
        assertTrue("true must be in toString()", s.contains("true"));
        assertTrue("value must be in toString()", s.contains("value"));
        assertTrue("isHighest must be in toString()", s.contains("isHighest"));
    }

    @Test
    public void toString_ContainsImportantValue() {
        final ThresholdDataType threshold = new ThresholdDataType("777", ThresholdDataType.Format.NUMBER, "name", 10.0, 12.0);

        final UpdateCommand command = new UpdateCommand(threshold, true, 11.9d);

        final String s = command.toString();
        assertTrue("11.9 must be in toString()", s.contains("11.9"));
        assertTrue("true must be in toString()", s.contains("true"));
        assertTrue("value must be in toString()", s.contains("value"));
        assertTrue("isHighest must be in toString()", s.contains("isHighest"));
    }

}