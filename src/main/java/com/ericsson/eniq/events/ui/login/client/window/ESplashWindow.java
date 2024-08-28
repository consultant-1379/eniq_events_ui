/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.window;

import com.ericsson.eniq.events.ui.login.client.Login;
import com.ericsson.eniq.events.ui.login.client.component.interfaces.ICollapsible;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * An {@link EWindow} that is styled as a Splash. Can show a GlassPanel to act modal.
 * @author ecarsea
 * @since 2011
 *
 */
public class ESplashWindow extends EWindow {

    private static ESplashWindowResources resources;

    static {
        resources = GWT.create(ESplashWindowResources.class);
        resources.css().ensureInjected();
    }

    private final static String CSS_STYLENAME = "splash";

    private final boolean showGlassPanel;

    private FlexTable collapsibleTable;

    private SimplePanel shadowOverlay;

    public ESplashWindow() {
        this(false);
    }

    public ESplashWindow(final boolean showGlassPanel) {
        super();
        this.showGlassPanel = showGlassPanel;
        topWindow = this;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Widget#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();
        setVisible(true);
    }

    /**
     * Show or hide the Window.
     * Show GlassPanel if property set.
     */
    @Override
    public void setVisible(final boolean visible) {
        if (this.showGlassPanel) {
            if (visible) {
                showGlassPanel();
            } else {
                hideGlassPanel();
            }
        }
        super.setVisible(visible);
    }

    /**
     * Hide the GlassPanel
     */
    public void hideGlassPanel() {
        this.setGlassEnabled(false);
        Window.enableScrolling(true);
    }

    /**
     * Show GlassPanel.
     * @param zIndex
     */
    public void showGlassPanel() {
        Window.enableScrolling(false);
        this.setGlassEnabled(true);
    }

    /**
     * Apply splash specific styling
     */
    @Override
    protected void applyStyle() {
        super.applyStyle();

        imgTopLeft.addStyleDependentName(CSS_STYLENAME);
        imgTopRight.addStyleDependentName(CSS_STYLENAME);
        imgBottomLeft.addStyleDependentName(CSS_STYLENAME);
        imgBottomRight.addStyleDependentName(CSS_STYLENAME);

        bottomWidget.addStyleDependentName(CSS_STYLENAME);
        // simulate addStyleDependentName for FlexTable formatter
        topRow.getCellFormatter().setStyleName(0, 1, getItemTheme("Window-Border" + "-t" + "-" + CSS_STYLENAME));

        centerRow.getCellFormatter().setStyleName(0, 0, getItemTheme("Window-Border" + "-l" + "-" + CSS_STYLENAME));
        centerRow.getCellFormatter().setStyleName(0, 2, getItemTheme("Window-Border" + "-r" + "-" + CSS_STYLENAME));
        centerRow.getCellFormatter().setStyleName(2, 0, getItemTheme("Window-Border" + "-l" + "-" + CSS_STYLENAME));
        centerRow.getCellFormatter().setStyleName(2, 2, getItemTheme("Window-Border" + "-r" + "-" + CSS_STYLENAME));

        if (collapsibleTable != null) {
            collapsibleTable.getWidget(0, 0).setStyleName(
                    getItemTheme("Window-Border-Collapsible" + "-tl" + "-" + CSS_STYLENAME));

            collapsibleTable.getWidget(0, 2).setStyleName(
                    getItemTheme("Window-Border-Collapsible" + "-tr" + "-" + CSS_STYLENAME));

            collapsibleTable.getWidget(1, 0).setStyleName(
                    getItemTheme("Window-Border-Collapsible" + "-bl" + "-" + CSS_STYLENAME));
            collapsibleTable.getWidget(1, 1).setStyleName(
                    getItemTheme("Window-Border-Collapsible" + "-br" + "-" + CSS_STYLENAME));

            shadowOverlay.setStyleName("shadowOverlay-" + Login.CSS_THEME_LIGHT);
        }
    }

    protected void addBottomPanel(final Widget content) {
        final Label leftLabel = new Label();

        centerRow.setWidget(2, 0, leftLabel);
        centerRow.setWidget(2, 1, content);
        centerRow.getFlexCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
        final Label rightLabel = new Label();
        centerRow.setWidget(2, 2, rightLabel);
    }

    protected ICollapsible addCollapsiblePanel(final Widget content) {
        final FlowPanel collapsiblePanel = new FlowPanel();
        collapsiblePanel.setStyleName("collapsiblePanel");

        shadowOverlay = new SimplePanel();
        collapsiblePanel.add(shadowOverlay);
        collapsibleTable = new FlexTable();
        collapsibleTable.setCellPadding(0);
        collapsibleTable.setCellSpacing(0);
        collapsibleTable.setBorderWidth(0);
        centerRow.getFlexCellFormatter().setColSpan(1, 0, 3);
        centerRow.setWidget(1, 0, collapsiblePanel);
        final SimplePanel topLeftBorder = new SimplePanel();
        collapsibleTable.setWidget(0, 0, topLeftBorder);

        collapsibleTable.setWidget(0, 1, content);
        collapsibleTable.getFlexCellFormatter().setRowSpan(0, 1, 2);
        collapsibleTable.getFlexCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
        final SimplePanel topRightBorder = new SimplePanel();
        collapsibleTable.setWidget(0, 2, topRightBorder);

        final SimplePanel bottomLeftBorder = new SimplePanel();
        collapsibleTable.setWidget(1, 0, bottomLeftBorder);

        final SimplePanel bottomRightBorder = new SimplePanel();
        collapsibleTable.setWidget(1, 1, bottomRightBorder);
        collapsiblePanel.add(collapsibleTable);

        return new ICollapsible() {

            @Override
            public void expand() {
                collapsiblePanel.addStyleDependentName("expanded");
            }

            @Override
            public void collapse() {
                collapsiblePanel.removeStyleDependentName("expanded");
            }
        };
    }

    /**
     * @param upperSplashContainer
     * @param lowerSplashContainer
     * @param tabPanel
     * @return
     */
    public ICollapsible createCollapsibleSplashPanel(final Widget topPanel, final Widget bottomPanel,
            final Widget collapsible) {
        addBottomPanel(bottomPanel);
        final ICollapsible collapsiblePanel = addCollapsiblePanel(collapsible);
        /** Top Panel is the content panel of the center row Flex Table. Need to do this as EWindow will create an empty content
         * area otherwise. called last as this method will call initUI also. **/
        this.setContent(topPanel);
        return collapsiblePanel;
    }
}
