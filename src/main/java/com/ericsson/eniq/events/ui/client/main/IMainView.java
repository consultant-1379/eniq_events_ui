/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.main;

import com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay;

import com.extjs.gxt.ui.client.widget.TabPanel;

/**
 * Interface for main view
 * @author eeicmsy
 * @since Feb 2010
 *
 */
public interface IMainView extends WidgetDisplay {

    /**
     * Get main container under the banner
     * @return  main container
     */
    TabPanel getContainerTab();


}
