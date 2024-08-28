package com.ericsson.eniq.events.ui.login.client.component;

import com.ericsson.eniq.events.ui.login.client.Login;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Ericsson Tab Panel Component
 * 
 * @author ecarsea
 *
 */
public class ETabPanel extends TabPanel {

    private static final String TAB_PANEL_NAME = "eTabPanel";

    private static final String TAB_BAR_NAME = "eTabBar";

    private static ETabPanelResources resources;

    static {
        resources = GWT.create(ETabPanelResources.class);
        resources.css().ensureInjected();
    }

    /**
     * 
     */
    public ETabPanel() {
        super();
        this.setStyleName(TAB_PANEL_NAME);
        this.getTabBar().setStyleName(TAB_BAR_NAME);
        /** Retrieve Theme from central location as it will be personalisable **/
        applyStyle(Login.CSS_THEME_LIGHT);
    }

    /**
     * @param style
     */
    private final void applyStyle(final String style) {
        this.addStyleDependentName(style);
        this.getTabBar().addStyleDependentName(style);
    }

    public void disableTabIndex() {
        for (int i = 0; i < this.getTabBar().getTabCount(); i++) {
            DOM.setElementAttribute(((Widget) this.getTabBar().getTab(i)).getElement(), "tabIndex", "");
        }
    }
}
