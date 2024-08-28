/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;

/**
 * WARNING Test creates a log for this test
 * @author eeicmsy
 * @since March 2010
 *
 */
public class TraceTest extends TestEniqEventsUI {

    private final static String LOG_FILE_NAME = System.getProperty("user.dir") + "\\" + "junitUI.log";

    private static PrintStream traceStream;

    private final static PrintStream origionalTraceOut = System.out;

    private final static boolean origionalTraceOn = Trace.isTracing;

    @Before()
    public void setUp() {

        try {
            Trace.isTracing = true;
            traceStream = new PrintStream(LOG_FILE_NAME);
            Trace.setOut(traceStream);
        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @After
    public void clearLog() {
        traceStream.flush();
        traceStream.close();

    }

    @AfterClass
    public static void afterClass() {
        traceStream.flush();
        traceStream.close();
        Trace.setOut(origionalTraceOut);
        Trace.isTracing = origionalTraceOn;

    }

    @Test
    public void traceMSGPrintsOutAsExpected() throws Exception {

        final String passedString = "trace test hello message";
        Trace.trace(Trace.Level.MSG, passedString);

        final String expected = "MSG " + passedString;
        Assert.assertEquals("Trace as expected", expected, "" + readTrace());
    }

    @Test
    public void traceWARNPrintsOutAsExpected() throws Exception {

        final String passedString = "trace test hello warning ";
        Trace.trace(Trace.Level.WARN, passedString);

        final String expected = "WARN " + passedString;
        Assert.assertEquals("Trace warning as expected", expected, "" + readTrace());
    }

    @Test
    public void traceERRPrintsOutAsExpected() throws Exception {

        final String passedString = "trace test hello error ";
        Trace.trace(Trace.Level.ERROR, passedString);

        final String expected = "ERROR " + passedString;
        Assert.assertEquals("Trace error as expected ", expected, "" + readTrace());
    }

    @Test
    @Ignore
    // Trace class defunced
    public void traceExceptionPrintsOutAsExpected() throws Exception {
        final String passedString = "we have a bug ";
        final String exMsg = "bug decription";
        final Exception passedException = new Exception(exMsg);

        // not going to hard-code stack */
        final String expectedStartsWith = "ERROR we have a bug  [Exception Message] bug decription java.lang.Exception: bug decription";

        Trace.traceException(passedString, passedException);
        Assert.assertEquals("Trace with stack trace", true, readTrace().toString().startsWith(expectedStartsWith));

    }

    @Test
    public void testCanTurnOffTrace() {
        Trace.isTracing = false;

        final String passedString = "trace test hello error ";
        Trace.trace(Trace.Level.ERROR, passedString);

        final String expected = ""; // EMPTY
        Assert.assertEquals("Trace error as expected ", expected, "" + readTrace());

    }

    //////////////////////    private methods

    private String readTrace() {

        final StringBuilder returnTrace = new StringBuilder();

        final File logFile = new File(LOG_FILE_NAME);

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        try {
            fis = new FileInputStream(logFile);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);

            while (dis.available() != 0) {
                returnTrace.append(dis.readLine());
            }
            fis.close();
            bis.close();
            dis.close();

        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block

            System.out.println("NOT FOUND !!!" + LOG_FILE_NAME);
            e.printStackTrace();

        } catch (final IOException e) {
            System.out.println("IO Exception !!!" + LOG_FILE_NAME);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return returnTrace.toString();

    }

}
