/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.mvp;

import com.ericsson.eniq.events.ui.client.common.service.NotificationService;
import com.ericsson.eniq.events.ui.client.main.MainPresenter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

/**
 * Presents a particular presenter when passed a container.
 * This class is created
 * by GIN injection - see xxxModule class in the gin package.
 * It constructs parameter in constructor (the  presenter) using GIN inject
 *
 * @author eeicmsy
 * @since Jan 2010
 */
public final class ApplicationPresenter implements UncaughtExceptionHandler {

    private HasWidgets container;

    private final MainPresenter mainPresenter;

    private final NotificationService notificationService;

    // TODO does not seem very generic - i.e. can not use for another presenter if
    // always binding to the same presenter on construction

    @Inject
    public ApplicationPresenter(final MainPresenter mainPresenter, final NotificationService notificationService) {
        this.mainPresenter = mainPresenter;
        this.notificationService = notificationService;
    }

    public void go(final HasWidgets container) {
        this.container = container;

        // Comment the next line in Dev mode to have logs
        registerErrorHandler();
        showMain();
    }

    private void registerErrorHandler() {
        GWT.setUncaughtExceptionHandler(this);
    }

    @Override
    public void onUncaughtException(final Throwable e) {
        notificationService.showErorDialog("Unexpected error!", e);
        e.printStackTrace();
    }

    private void showMain() {
        container.add(mainPresenter.getView().asWidget());
    }

}