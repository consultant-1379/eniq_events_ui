/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2013 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.workspace.launchwindowconfig;

import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author egallou
 * @since 06/2013
 */
public class ConfigWidgetPresenter extends BasePresenter<ConfigWidgetView>{
    /**
     * @param view
     * @param eventBus
     */
    @Inject
    public ConfigWidgetPresenter(ConfigWidgetView view, EventBus eventBus) {
        super(view, eventBus);
        bind();
    }
}
