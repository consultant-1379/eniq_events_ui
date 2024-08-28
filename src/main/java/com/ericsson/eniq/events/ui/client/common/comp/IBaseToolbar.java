/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.ui.Widget;

/**
 * interface for BaseToolBar View
 * @author eendmcm
 * @since Feb 2010
 */
public interface IBaseToolbar {

    void addToolbarItem(final Component toolBarItem);

    void addToolbarSeperator();

    Widget asWidget();
}
