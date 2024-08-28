package com.ericsson.eniq.events.ui.client.dashboard;

import com.ericsson.eniq.events.ui.client.datatype.dashboard.DashBoardDataType;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * 
 * @author ekurshi
 * @since 2013
 *
 */
public class DashboardModule {
    private final AbsolutePanel viewContainer;

    public DashboardModule() {
        viewContainer = new AbsolutePanel();
        viewContainer.setHeight("100%");
        viewContainer.getElement().getStyle().setOverflowY(Overflow.AUTO);
    }

    public void init(final IDashboardTaskbarHelper menuTaskBar, final DashBoardDataType dashBoardData) {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                final DashboardPresenter dashboardPresenter = MainEntryPoint.getInjector().getDashboardPresenter();
                viewContainer.add(dashboardPresenter.getView());
                dashboardPresenter.loadDashboardData(menuTaskBar, dashBoardData);
            }

            @Override
            public void onFailure(final Throwable reason) {
                Window.alert("code splitting failed..");
            }
        });

    }

    public AbsolutePanel getView() {
        return viewContainer;
    }
}
