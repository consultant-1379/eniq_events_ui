/*
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.groupmanagement.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Style for Group Management Widget.
 *
 * @author ecarsea
 * @since Dec 2011
 */
public interface GroupMgmtResourceBundle extends ClientBundle {

    @Source("images/x.png")
    ImageResource closeButton();

    @Source("css/GroupManagement.css")
    GroupManagementStyle style();

    interface GroupManagementStyle extends CssResource {
        String suggestionItem();

        String groupMenuBar();

        String textBox();

        String separator();

        String container();

        String expandableListContainer();

        String expandableListHeader();

        String buttonHolder();

        String defaultText();

        String glassPanel();

        String enabled();

        String elementListContainer();

        String filterBoxHolder();

        String elementScrollPanel();

        String groupNameFilter();

        String wizardContainer();

        String wizard();

        String comboHolder();

        String header();
    }
}
