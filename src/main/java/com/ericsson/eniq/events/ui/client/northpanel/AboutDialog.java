/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.northpanel;

import com.ericsson.eniq.events.ui.client.common.ReadLoginSessionProperties;
import com.ericsson.eniq.events.ui.client.datatype.LicenseInfoDataType;
import com.ericsson.eniq.events.widgets.client.utilities.ZIndexHelper;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.*;

import java.util.List;

import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;

/**
 * About dialog box. Reads product revision from app settings
 *
 * @author eeicmsy
 * @since Feb 201O
 */
public class AboutDialog extends Dialog {

    private static final int DIALOG_WIDTH = 400;

    private static final String LICENCE_SCROLL_HEIGHT = "140px";

    private final FlowPanel products = new FlowPanel();

    final ScrollPanel licenseContainer = new ScrollPanel();

    private static final String VERSION = "Version : ";

    public AboutDialog() {

        setUpDialog();

        final VerticalPanel bodyPanel = new VerticalPanel();
        bodyPanel.setWidth("100%");
        bodyPanel.add(getEniqImage());

        final VerticalPanel vp = getTextContent();
        bodyPanel.add(vp);

        bodyPanel.setCellHorizontalAlignment(vp, HasHorizontalAlignment.ALIGN_LEFT);
        add(bodyPanel);
    }

    public void addProductLicenses(final List<LicenseInfoDataType> licenses) {
        if (licenses.size() > 3) {
            licenseContainer.setHeight(LICENCE_SCROLL_HEIGHT);
        }
        for (final LicenseInfoDataType license : licenses) {
            final String name = license.getDescription();
            final String version = license.getFeatureName();
            final FlowPanel productContainer = new FlowPanel();
            productContainer.add(new Label(name));
            productContainer.add(new Label(VERSION + version));

            productContainer.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
            products.add(productContainer);
        }
    }

    private void setUpDialog() {
        setWidth(DIALOG_WIDTH);
        addStyleName("about");
        setShadow(false);//GXT default shadow

        setButtons(EMPTY_STRING); // we'll use our own button
        setModal(false);
        setResizable(false);
    }

    private HorizontalPanel getEniqImage() {
        final HorizontalPanel imagePanel = new HorizontalPanel();
        final Image ericLogo = new Image("resources/images/EricssonAboutOSSImage.png");
        imagePanel.add(ericLogo);

        return imagePanel;
    }

    private VerticalPanel getTextContent() {
        final VerticalPanel contentPanel = new VerticalPanel();
        contentPanel.setWidth("100%");
        contentPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        contentPanel.getElement().getStyle().setPaddingLeft(56, Style.Unit.PX);
        contentPanel.getElement().getStyle().setPaddingTop(10, Style.Unit.PX);
        contentPanel.getElement().getStyle().setPaddingBottom(50, Style.Unit.PX);
        contentPanel.getElement().getStyle().setPaddingRight(56, Style.Unit.PX);
        contentPanel.add(createLabel(VERSION + ReadLoginSessionProperties.getEniqEventsUIVersion()));
        licenseContainer.setWidth("100%");
        licenseContainer.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        licenseContainer.getElement().getStyle().setMarginTop(10, Style.Unit.PX);
        products.setSize("100%", "100%");
        licenseContainer.add(products);
        contentPanel.add(licenseContainer);
        contentPanel.add(createLabel(ReadLoginSessionProperties.getEniqEventsUICoypright()));
        return contentPanel;
    }

    private Label createLabel(final String label) {
        return new Label(label);
    }

    @Override
    public void show(){
      super.show();
      setZIndex(ZIndexHelper.getHighestZIndex());
    }

    @Override
    public void setZIndex(int zIndex) {
        getElement().getStyle().setZIndex(zIndex);
    }

}