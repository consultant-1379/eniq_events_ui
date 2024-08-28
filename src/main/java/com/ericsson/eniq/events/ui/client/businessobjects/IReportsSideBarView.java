/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.businessobjects;

import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public interface IReportsSideBarView {
    void slideIn();

    void slideOut();

    boolean isSlideInState();

    /**
     * @param handler
     */
    void init(IReportsSideBarUiHandler handler);

    /**
     * @return
     */
    Widget asWidget();

    /**
     * @param jsonValue
     */
    void createTree(JSONValue jsonValue);

    void unmask();

    /**
     * @param mask text to display in mask element
     */
    void mask(String maskText);

    void clear();
}
