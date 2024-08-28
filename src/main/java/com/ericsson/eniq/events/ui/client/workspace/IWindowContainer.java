/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace;

import com.extjs.gxt.ui.client.widget.ContentPanel;

/**
 * Interface wrapper for GXT ContentPanel. GXT doesnt believe in interfaces.
 * @author ecarsea
 * @since 2012
 *
 */
public interface IWindowContainer {

    ContentPanel getWindowContainerPanel();

}
