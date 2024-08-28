/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.window;

import com.ericsson.eniq.events.ui.login.client.Login;
import com.ericsson.eniq.events.ui.login.client.util.LoginUtils;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The default base implementation of {@link IWindow}.
 * ecarsea - Changed from extending SimplePanel to extend PopupPanel in order to use the GWT GlassPanel
 * 
 * @author Pedro Tavares - epedtav
 * @author ecarsea
 * @since 2011
 * 
 */
public class EWindow extends PopupPanel implements IWindow {

    public static int getLayerOfTheTopWindow() {
        return layerOfTheTopWindow;
    }

    public static final String CSS_PREFIX = "ewf-";

    private static final int DEFAULT_WIDTH = 200;

    private static final int DEFAULT_HEIGHT = 40;

    protected static int layerOfTheTopWindow;

    protected static IWindow topWindow;

    protected int width = -1;

    protected int height = -1;

    private Widget myContent;

    private int minWidth;

    private int minHeight;

    private boolean visible;

    protected Label imgTopLeft;

    protected Label imgTopRight;

    protected Label imgBottomLeft;

    protected Label imgBottomRight;

    protected Widget bottomWidget;

    private FlexTable ui;

    protected FlexTable topRow;

    protected FlexTable centerRow;

    private FlexTable bottomRow;

    protected Label centerLeftLabel;

    protected Label centerRightLabel;

    public EWindow() {
        myContent = new HTML("");
        init();
        initUI(); // NOPMD by epedtav on 15/02/11 14:11
        RootPanel.get().add(this);
    }

    /**
     * Initialise the Window
     */
    private void init() {

        ui = new FlexTable();
        topRow = new FlexTable();
        centerRow = new FlexTable();
        bottomRow = new FlexTable();
        //    topBar = new TopBar(this);
        imgTopLeft = new Label();
        imgTopRight = new Label();
        imgBottomLeft = new Label();
        imgBottomRight = new Label();
        bottomWidget = new Label();
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;

        // TODO default scrolling seems to affect the windows in IE, needs
        // testing
        DOM.setStyleAttribute(getElement(), "overflow", "hidden");
        DOM.setStyleAttribute(getElement(), "position", Position.FIXED.getCssName());
    }

    /**
     * Initialise the internal UI
     */
    protected void initUI() {
        // ensure minimum size
        if (width < minWidth) {
            width = minWidth;
        }
        if (height < minHeight) {
            height = minHeight;
        }
        setSize(width, height);

        topRow.setWidget(0, 0, imgTopLeft);
        topRow.setWidget(0, 2, imgTopRight);

        bottomRow.setWidget(0, 0, imgBottomLeft);
        bottomRow.setWidget(0, 1, bottomWidget);
        bottomRow.setWidget(0, 2, imgBottomRight);

        centerLeftLabel = new Label();
        centerRow.setWidget(0, 0, centerLeftLabel);
        centerRow.setWidget(0, 1, myContent);
        centerRow.getFlexCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
        centerRightLabel = new Label();
        centerRow.setWidget(0, 2, centerRightLabel);

        ui.getCellFormatter().setHeight(1, 0, "100%");
        ui.getCellFormatter().setWidth(1, 0, "100%");
        ui.getCellFormatter()
                .setAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
        ui.setCellPadding(0);
        ui.setCellSpacing(0);
        ui.setWidget(0, 0, topRow);
        ui.setWidget(1, 0, centerRow);
        ui.setWidget(2, 0, bottomRow);
        super.setWidget(ui);
        applyStyle();

        topRow.setCellPadding(0);
        topRow.setCellSpacing(0);
        topRow.setHeight("100%");
        // ensure
        topRow.getCellFormatter().setWidth(0, 1, "100%");

        centerRow.setCellPadding(0);
        centerRow.setCellSpacing(0);
        centerRow.setWidth("100%");
        centerRow.setHeight("100%");
        centerRow.setBorderWidth(0);

        bottomRow.setCellPadding(0);
        bottomRow.setCellSpacing(0);
        bottomRow.setHeight("100%");
        // ensure
        bottomRow.getCellFormatter().setWidth(0, 1, "100%");

        if (visible) {
            setSize(getOffsetWidth(), getOffsetHeight());
        }
    }

    protected void applyStyle() {
        imgTopLeft.setStyleName(getItemTheme("Window-Border" + "-tl"));
        imgTopRight.setStyleName(getItemTheme("Window-Border" + "-tr"));
        imgBottomLeft.setStyleName(getItemTheme("Window-Border" + "-bl"));
        imgBottomRight.setStyleName(getItemTheme("Window-Border" + "-br"));

        bottomWidget.setStyleName(getItemTheme("Window-Border" + "-b"));
        topRow.getCellFormatter().setStyleName(0, 1, getItemTheme("Window-Border" + "-t"));
        centerRow.getCellFormatter().setStyleName(0, 0, getItemTheme("Window-Border" + "-l"));
        centerRow.getCellFormatter().setStyleName(0, 1, getItemTheme("Window-Content"));
        myContent.setStyleName(getItemTheme("Window-Content"));
        centerRow.getCellFormatter().setStyleName(0, 2, getItemTheme("Window-Border" + "-r"));
        centerLeftLabel.setStyleName(getItemTheme("Window-Border" + "-l"));
        centerRightLabel.setStyleName(getItemTheme("Window-Border" + "-r"));

        topRow.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_BOTTOM);
        topRow.getCellFormatter().setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_BOTTOM);
        bottomRow.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        bottomRow.getCellFormatter().setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);

        bottomRow.setWidget(0, 2, imgBottomRight);
    }

    @Override
    public void setContent(final Widget widget) {
        myContent = widget;
        initUI();
    }

    /**
     * Set the size on the window. Adjust cells for particular content.
     */
    private void setSize(final int width, final int height) {
        final int OFFSET_HEIGHT = 76; // the sum of the top and bottom rows in
                                      // new Branding
        final int theWidth = Math.max(width, this.minWidth);
        final int theHeight = Math.max(height, this.minHeight);
        // Always nice funnies reSizing in IE, let's do some magic
        final int offsetHeight = LoginUtils.isIE() ? OFFSET_HEIGHT : 0;
        ui.setSize(theWidth + "px", Math.max(0, theHeight - offsetHeight) + "px");
        this.width = theWidth;
        this.height = theHeight;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    /**
     * Show or hide the Window.
     * On hide, don't detach
     */
    @Override
    public void setVisible(final boolean visible) {
        if (visible) {
            super.show();
            setSize(getOffsetWidth(), getOffsetHeight());
            doShow();
        } else {
            super.hide();
        }
        this.visible = visible;
    }

    protected void doShow() {
        DOM.setIntStyleAttribute(getElement(), "zIndex", ++layerOfTheTopWindow);
    }

    protected String getItemTheme(final String item) {
        return CSS_PREFIX + Login.CSS_THEME_LIGHT + "-" + item;
    }

}
