/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.workspace.launchwindowconfig;

import com.ericsson.eniq.events.ui.client.common.widget.window.EniqWindowGwt;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

public class ConfigurationDialogWindow extends Window {

    private EniqWindowGwt gwtWindow;
    private boolean firstLaunch = true;

    public ConfigurationDialogWindow(EniqWindowGwt eniqWindowGwt) {
        this.gwtWindow = eniqWindowGwt;
        this.setLayout(new FitLayout());
    }

    public void setGlassEnabled() {
        gwtWindow.setGlassEnabled(false);
    }

    public void setContainer(ContentPanel container) {
        getDraggable().setContainer(container);
        setContainer(container.getElement());
    }

    public SimplePanel getWindowContentPanel() {
        return gwtWindow.getWindowContentPanel();
    }

    public Image getCloseButton() {
        return gwtWindow.getCloseButton();
    }

    public void putToFront() {
        setVisible(true);
        toFront();

    }

    @Override
    public void hide() {
        if (firstLaunch) {
            super.hide();
            firstLaunch = false;
        } else {
            this.setVisible(true);
            super.hide();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }
}
