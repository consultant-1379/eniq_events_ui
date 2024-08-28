/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.component;

import com.ericsson.eniq.events.common.client.service.IServiceProperties;
import com.ericsson.eniq.events.common.client.url.Url;
import com.ericsson.eniq.events.widgets.client.suggestbox.LiveLoadOracle;

/**
 * Paired liveload for Terminals i.e. this is activated when a terminal make is selected, and that make is used to liveload the models with his oracle.
 * @author ecarsea
 * @since 2012
 */
public class WorkspacePairedLiveloadOracle extends LiveLoadOracle {

    /**
     * Basically load all makes
     */
    private static final int TERMINAL_LIVELOAD_LIMIT = 10000;

    /**
     * @param serviceProperties
     */
    public WorkspacePairedLiveloadOracle(IServiceProperties serviceProperties) {
        super(serviceProperties);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.kpianalysis.client.launchmenu.LiveLoadOracle#init(java.lang.String, java.lang.String)
     */
    @Override
    public void init(String url, String liveloadRoot) {
        /** Remove the params that are added by the primary load json object and add them properly with the Url **/
        super.init(url.substring(0, url.indexOf("?")), liveloadRoot);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.kpianalysis.client.launchmenu.LiveLoadOracle#getRequestData(java.lang.String)
     */
    @Override
    protected Url getRequestData(String query) {
        Url url = super.getRequestData(query);
        /** Add the id parameter here **/
        url.setParameter("id", liveLoadRoot);
        return url;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.widgets.client.launchmenu.LiveLoadOracle#getMaxLiveLoadLimit()
     */
    @Override
    protected Integer getMaxLiveLoadLimit() {
        return TERMINAL_LIVELOAD_LIMIT;
    }
}
