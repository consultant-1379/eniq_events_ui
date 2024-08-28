/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui;

import static com.ericsson.eniq.events.ui.MockUtilities.renameAndReplaceMethod;

/**
 * There are clear problems with version of GXT we use
 * when trying to perform unit test.
 * The crux of the problem is the static #getModuleBaseURL being called
 * so this class is adding a method to disarm GXT behavior
 * (rather than forcing us to go down the path of adapting all our components to
 * have GWT interfaces in an effort to avoid hitting Component intansiation code) 
 * 
 *  
 *  GXTMockUtilities.disarm();
 *  GWTMockUtilities.disarm();
 * 
 * @author James Winters 
 * @author eeicmsy
 * @since March 2010
 *
 */
public class GXTMockUtilities {
    private static boolean alreadyDisarmed;

    //static Logger logger = Logger.getLogger(GXTMockUtilities.class);

    public static void disarm() {

        if (!alreadyDisarmed) {
            renameAndReplaceMethod("com.google.gwt.core.client.impl.Impl", "java.lang.String", "getModuleBaseURL");
            // renameAndReplaceMethod(gwtClazz, returnClazz,
            // "getHostPageBaseURL");
            renameAndReplaceMethod("com.extjs.gxt.ui.client.GXT", "init");
            alreadyDisarmed = true;
        }
    }

    // this is somewhat dangerous as you will be reloading classes
    // the JVM doesn't really like this
    public static void restore() {

    }

}
