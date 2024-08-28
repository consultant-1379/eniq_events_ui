/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.charts;

/**
 * @author ecarsea
 * @since 2011
 *
 */
public interface IChartUiHandler {

    /**
     * @param parentHeight 
     * @param parentWidth 
     * @param parentIsGwtPanel 
     * 
     */
    void onRender(int parentHeight, int parentWidth, boolean parentIsGwtPanel);

    /**
     * @param width
     * @param height
     */
    void onResize(int width, int height);

    void onDetach();

    /**
     * @param height
     * @param width
     */
    void onShow(int height, int width);

}
