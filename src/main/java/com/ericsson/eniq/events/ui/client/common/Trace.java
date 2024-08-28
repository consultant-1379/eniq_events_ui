/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common;

import java.io.PrintStream;

/**
 * Class to proxy whatever we use for logging
 * 
 * 
 * NOTE: There is duplicate of this class in "com.ericsson.eniq.events.ui.login.utils"
 *       The UI cannot access classes outside its client directory and the login functionality 
 *       cannot access the com.ericsson.eniq.events.ui.client.common directory as this is not 
 *       deployed in the WAR. 
 *       Please update "com.ericsson.eniq.events.ui.login.utils.Trace" class when updating 
 *       this class
 * 
 * 
 * @author eeicmsy
 * @since Feb 2010
 */
public abstract class Trace {

    /*-----*************-----*************------************
     *           PLEASE READ JAVADOC NOTE ABOVE!
     * -----*************-----*************-----************
     */

    private final static String EXCEPTION_MSG = " [Exception Message] ";

    /* not final for junit change */
    //TODO: Change to a config setting to allow toggle on/off
    public static boolean isTracing = true;

    private final static String SPACER = " ";

    public static enum Level {
        MSG, WARN, ERROR;
    }

    /**
     * Set Trace (System.out) to a log file  
     * Change trace output from System console (pass to log)
     * Largely for JUNIT purposes to test this file.
     * 
     * @param out  PrintStream with log file location string
     */
    public static void setOut(final PrintStream out) {
        System.setOut(out);
    }

    /**
     * Trace method when have no Exception (Throwable) to trace
     * @param level   Level of severity of message being traces
     * @param s       String to trace
     */
    public static void trace(final Level level, final String s) {
        final StringBuilder buff = new StringBuilder();
        buff.append(level);
        buff.append(SPACER);
        buff.append(s);
        writeToLog(buff.toString());
    }

    /**
     * Trace a Throwable
     * @param s   String to trace
     * @param e   The exception (raw type to suit metareader overide)
     */
    public static void traceException(final String s, final Throwable e) {
        final StringBuilder buff = new StringBuilder();
        buff.append(Level.ERROR);
        buff.append(SPACER);
        buff.append(s);
        if (e != null) {
            buff.append(EXCEPTION_MSG);
            buff.append(e.getMessage());
            buff.append(SPACER);
            writeToLog(buff.toString());
            if (isTracing) {
                e.printStackTrace(System.out); // we can set the out
            }
        } else {
            buff.append("exception part is [null]");
        }

    }

    private static void writeToLog(final String s) {
        if (isTracing) {
            // see #setOut
            System.out.println(s);
        }
    }
}
