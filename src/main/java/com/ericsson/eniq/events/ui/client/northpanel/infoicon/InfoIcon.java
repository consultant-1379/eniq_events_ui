/*
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.northpanel.infoicon;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

public class InfoIcon extends Composite {

    private static InfoIconResources resources;

    static {
        resources = GWT.create(InfoIconResources.class);
        resources.css().ensureInjected();
    }

    public InfoIcon() {
        initWidget(new SimplePanel());
        setStyleName(resources.css().infoIcon());

        setVisible(false);
    }

    public void setMessage(final String message) {
        if (message == null || "".equals(message)) {
            clear();
        } else {
            setVisible(true);
            setTitle(message);
            addStyleName(resources.css().glow());
        }
    }

    public void clear() {
        setTitle("");
        removeStyleName(resources.css().glow());
    }
}
