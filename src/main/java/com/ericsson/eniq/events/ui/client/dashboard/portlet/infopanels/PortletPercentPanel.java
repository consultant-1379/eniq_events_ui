/*
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.dashboard.portlet.infopanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PortletPercentPanel extends Composite implements HasClickHandlers {

    @UiField
    Label total;

    @UiField
    Image indicator;

    @UiField
    SpanElement percent;

    @UiField
    PortletPercentStyle style;

    private static final PortletPercentResourceBundle resourceBundle = GWT.create(PortletPercentResourceBundle.class);

    private static final PortletPercentUiBinder uiBinder = GWT.create(PortletPercentUiBinder.class);

    @Override
    public HandlerRegistration addClickHandler(final ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    interface PortletPercentUiBinder extends UiBinder<Widget, PortletPercentPanel> {
    }

    public PortletPercentPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        setVisible(false);
    }

    /**
     * @param totals Text including Total = X;
     */
    public void setDescription(final String totals) {
        if (!isVisible()) {
            // To show only when data is set.
            setVisible(true);
        }

        total.setText(totals);
    }

    public void setIndicators(final int previous, final int current) {
        if (!isVisible()) {
            // To show only when data is set.
            setVisible(true);
        }

        if (previous == 0) {
            addStyleName(style.showOnlyCurrentTotal());
            return; // Since indicator & percent label won't be seen
        }
        removeStyleName(style.showOnlyCurrentTotal());

        final double percent = calculatePercent(previous, current);
        setPercent(percent);
    }

    public void setPercent(final double perc) {
        if (!isVisible()) {
            // To show only when data is set.
            setVisible(true);
        }

        if (Double.isNaN(perc)) {
            addStyleName(style.showOnlyCurrentTotal());
            return; // Since indicator & percent label won't be seen
        }
        removeStyleName(style.showOnlyCurrentTotal());

        setIndicatorStyle(perc);

        String sign = "";
        if (perc > 0) {
            sign = "+";
        }

        final String percValue = NumberFormat.getFormat(Math.abs(perc) > 100 ? "#0" : "#0.##").format(perc);

        percent.setInnerText(sign + percValue);
    }

    public void removePaddingForIndicator() {
        addStyleName(style.noPaddingForIndicator());
    }

    private double calculatePercent(final int previousTotal, final int currentTotal) {
        double result = Double.NaN;

        if (currentTotal != 0 && previousTotal != 0) {
            final double delta = currentTotal - previousTotal;
            result = ((delta / previousTotal) * 100);
        }

        return result;
    }

    private void setIndicatorStyle(final double percent) {
        if (percent > 0) {
            AbstractImagePrototype.create(resourceBundle.triangleUp()).applyTo(indicator);
        } else if (percent < 0) {
            AbstractImagePrototype.create(resourceBundle.triangleDown()).applyTo(indicator);
        } else {
            AbstractImagePrototype.create(resourceBundle.triangleEquals()).applyTo(indicator);
        }
    }

    interface PortletPercentStyle extends CssResource {
        String showOnlyCurrentTotal();

        String container();

        String noPaddingForIndicator();
    }
}
