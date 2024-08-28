/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.component;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * The Ericsson ProgressBar component
 * 
 * @author michaeladams
 * @author eendmcm     
 * @since 2011
 * 
 */
public class EProgressBar extends Composite {

    public interface ILabelFormatter {
        String formatLabel(EProgressBar progressBar, double percent);
    }

    /**
     * Increments used to increase the progress bar
     * defaults to increments of 1 
     */
    private double incrementSpeed = 1;

    /**
     * Flag to determine if Processing has completed
     */
    private boolean isComplete; // NOPMD eendmcm 

    /**
     * The bar element that displays the progress.
     */
    private Element barElement; // NOPMD eendmcm

    /**
     * The shadow element that decreases as the the progress increases.
     */
    private Element shadowElement; // NOPMD eendmcm

    /**
     * The current progress.
     */
    private double curProgress; // NOPMD eendmcm

    /**
     * The maximum progress.
     */
    private double max;

    /**
     * The minimum progress.
     */
    private double min;

    /**
     * The element that displays text on the page.
     */
    private Element textElement; // NOPMD eendmcm

    /**
     * The panel that holds all elements (shell)
     */
    private final FlowPanel pnlShell = new FlowPanel(); // NOPMD eendmcm

    /**
     * The panel that is used to centre progressbar within page
     */
    private final SimplePanel simplePanel = new SimplePanel(); // NOPMD eendmcm

    private ILabelFormatter labelFormatter;

    /**
     * Create a progress bar with default range of 0 to 100.
     */
    public EProgressBar() {
        this(0.0, 100.0, 0.0);
    }

    /**
     * Create a progress bar with an initial progress and a default range of 0 to
     * 100.
     * 
     * @param curProgress the current progress
     */
    public EProgressBar(final double curProgress) {
        this(0.0, 100.0, curProgress);
    }

    /**
     * Create a progress bar within the given range.
     * 
     * @param min the minimum progress
     * @param max the maximum progress
     */
    public EProgressBar(final double min, final double max) {
        this(min, max, 0.0);
    }

    /**
     * Create a progress bar within the given range starting at the specified
     * progress amount.
     * 
     * @param min the minimum progress
     * @param max the maximum progress
     * @param curProgress the current progress
     */
    public EProgressBar(final double min, final double max, final double curProgress) {
        super();

        this.min = min;
        this.max = max;
        this.curProgress = curProgress;

        //Create the outer shell - Hardcoding width and height
        //of shell to provide structure. These can be overridden via CSS if needed 
        pnlShell.setHeight("11px");
        pnlShell.setWidth("321px");

        simplePanel.add(pnlShell);
        initWidget(simplePanel);
        createProgressElements();
        applyStyle(); // NOPMD

    }

    public void setLabelFormatter(final ILabelFormatter labelFormatter) {
        this.labelFormatter = labelFormatter;
    }

    public ILabelFormatter getLabelFormatter() {
        return this.labelFormatter;
    }

    /**
     * Get the maximum progress.
     * 
     * @return the maximum progress
     */
    public double getMaxProgress() {
        return max;
    }

    /**
     * Get the minimum progress.
     * 
     * @return the minimum progress
     */
    public double getMinProgress() {
        return min;
    }

    /**
     * Get the current percent complete, relative to the minimum and maximum
     * values. The percent will always be between 0.0 - 1.0.
     * 
     * @return the current percent complete
     */
    public double getPercent() {
        // If we have no range
        if (max <= min) {
            return 0.0; // NOPMD MA
        }
        // Calculate the relative progress
        final double percent = (curProgress - min) / (max - min);
        return Math.max(0.0, Math.min(1.0, percent));
    }

    /**
     * Get the current increment speed utilised
     * 
     * @return the current increments used to represent progress
     */
    public double getIncrementSpeed() {
        return incrementSpeed;
    }

    /**
     * Set the increment speed
     */
    public void setIncrementSpeed(final double speed) {
        incrementSpeed = speed;
    }

    /**
     * Set the message text underneath the progress bar
     */
    public void setText(final String text) {
        DOM.setElementProperty(textElement, "innerHTML", text);
    }

    /**
     * Get the current progress.
     * 
     * @return the current progress
     */
    public double getProgress() {
        return curProgress;
    }

    /**
     * Set the maximum progress. If the minimum progress is more than the current
     * progress, the current progress is adjusted to be within the new range.
     * 
     * @param maxProgress the maximum progress
     */
    public void setMaxProgress(final double maxProgress) {
        this.max = maxProgress;
        curProgress = Math.min(curProgress, maxProgress);
        this.incrementProgress();
    }

    /**
     * Set the minimum progress. If the minimum progress is more than the current
     * progress, the current progress is adjusted to be within the new range.
     * 
     * @param minProgress the minimum progress
     */
    public void setMinProgress(final double minProgress) {
        this.min = minProgress;
        curProgress = Math.max(curProgress, minProgress);
        this.incrementProgress();
    }

    /**
     * Increments the Progress Bar by the allocated increment
     * and Updated the Text Bar with the relevant % completed
     */
    public void incrementProgress() {
        this.curProgress = Math.max(min, Math.min(max, curProgress + incrementSpeed));
        final int percent = (int) (100 * getPercent());
        updateBar(percent);
        //Job Done
        isComplete = (percent == 100) ? true : false;
    }

    public void setComplete() {
        updateBar(100);
    }

    private void updateBar(final int percent) {
        // Calculate percent complete

        DOM.setStyleAttribute(barElement, "width", percent + "%");
        //Update the Text to feedback percent complete
        if (labelFormatter != null) {
            this.setText(labelFormatter.formatLabel(this, percent));
        } else {
            this.setText((100 * percent) + "%");
        }
    }

    /**
     * Overrides the default height and width of the outer shell
     * of the component
     * Sets the height and width in to the provided values e.g 100px
     */
    @Override
    public void setSize(final String height, final String width) {
        this.setHeight(height);
        this.setWidth(width);
    }

    /**
     * Determines if this progress has completed.
     * @return
     */
    public boolean isProgressCompleted() {
        return isComplete;
    }

    /**
     * Get the bar element.
     * 
     * @return the bar element
     */
    protected Element getBarElement() {
        return barElement;
    }

    /**
     * Reset the progress text to the min.
     */
    public void resetProgress() {
        curProgress = min;
        this.updateBar(0);
    }

    /**
     * @param style
     */
    public void applyStyle() {
        pnlShell.setStyleName("EProgressBar-shell");
        DOM.setElementProperty(barElement, "className", "EProgressBar-bar");
        DOM.setElementProperty(shadowElement, "className", "EProgressBar-shadow");
        DOM.setElementProperty(textElement, "className", "EProgressBar-text");
    }

    /*
     * Creates the bar, shadow and text elements
     * that are utilised as part of the component.
     */
    private void createProgressElements() {
        barElement = DOM.createDiv();
        DOM.appendChild(pnlShell.getElement(), barElement);
        shadowElement = DOM.createDiv();
        DOM.appendChild(pnlShell.getElement(), shadowElement);
        textElement = DOM.createDiv();
        DOM.appendChild(pnlShell.getElement(), textElement);
    }

    /**
     * @param max the max to set
     */
    public void setMax(final double max) {
        this.max = max;
    }

    /**
     * @param min the min to set
     */
    public void setMin(final double min) {
        this.min = min;
    }

}
