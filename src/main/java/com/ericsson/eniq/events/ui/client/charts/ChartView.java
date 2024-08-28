/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.charts;

import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * Charting widget, renders the various Charting Types
 * available e.g. Pie, Line, Bar via High Charts Library
 *
 * @author ecarsea
 * @since June 2011
 */
public class ChartView extends BoxComponent implements IChartView {
    private boolean parentIsGwtPanel;

    private final IChartUiHandler presenter;

    private final String containerId;

    /**
     * @param presenter   The presenter for the chart
     * @param containerId The containerID for this container
     */
    public ChartView(final IChartUiHandler presenter, final String containerId) {
        this.presenter = presenter;
        this.containerId = containerId;
    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.BoxComponent#onShow()
     */
    @Override
    protected void onShow() {
        super.onShow();
        final int clientWidth = getElement().getClientWidth();
        final int clientHeight = getElement().getClientHeight();
        presenter.onResize(clientWidth, clientHeight);
        presenter.onShow(clientHeight, clientWidth);
    }

    /* 
     * Create the div container element for the chart and set it to be the element of this BoxComponent.
     * (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.Component#onRender(com.google.gwt.user.client.Element, int)
     */
    @Override
    protected void onRender(final Element parent, final int index) {
        final Element element = DOM.createElement("div");
        element.setId(this.containerId);
        setElement(element, parent, index);
        super.onRender(parent, index);
        this.getElement().getStyle().setOverflowY(Overflow.HIDDEN);

        // Temporal fix for sizing issues in portlets - for some reason wrong width is gotten from parent at this stage
        // TODO: Remove once the sizing problem is solved without usage of deferred command.
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                /* Width is taken from element itself, as it will always have it taking the whole available space,
                 * but height is taken from parent, because this element itself is just a placeholder for chart and
                 * is empty at this state */
                presenter.onRender(parent.getClientHeight(), getElement().getClientWidth(), parentIsGwtPanel);
            }
        });
    }

    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.Component#getElement()
     */
    @Override
    public Element getElement() {
        /** If the parent element is GWT this method will be called before the widget is rendered, so am
         * using this to identify the parent container.
         */
        if (!rendered) {
            parentIsGwtPanel = true;
        }
        return super.getElement();
    }

    /* 
     * Need to re-render the chart on a resize.
     * (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.BoxComponent#onResize(int, int)
     */
    @Override
    protected final void onResize(final int width, final int height) {
        super.onResize(width, height);
        /** Dont resize if chart is not shown. The onShow method will do the resize when chart is shown **/
        if (this.isVisible()) {
            presenter.onResize(width, height);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.onDetach();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.charts.IChartView#setHoriztontalScrollEnabled(boolean)
     */
    @Override
    public void setHorizontalScrollEnabled(final boolean overflowX) {
        if (overflowX) {
            getElement().getStyle().setOverflowX(Overflow.SCROLL);
        } else {
            getElement().getStyle().setOverflowX(Overflow.HIDDEN);
        }
    }
}
