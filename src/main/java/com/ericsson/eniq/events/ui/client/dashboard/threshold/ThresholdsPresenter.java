package com.ericsson.eniq.events.ui.client.dashboard.threshold;

import com.ericsson.eniq.events.common.client.datatype.ThresholdDataType;
import com.ericsson.eniq.events.common.client.threshold.AbstractThresholdsPresenter;
import com.ericsson.eniq.events.common.client.threshold.ThresholdsView;
import com.ericsson.eniq.events.common.client.threshold.UpdateCommand;
import com.ericsson.eniq.events.common.client.threshold.events.ThresholdDialogHideEvent;
import com.ericsson.eniq.events.ui.client.common.service.DashboardManager;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletRefreshEvent;
import com.ericsson.eniq.events.widgets.client.threshold.ThresholdsDialog;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThresholdsPresenter extends AbstractThresholdsPresenter {

    private final List<PortletDataType> portletsToConfigure = new ArrayList<PortletDataType>();

    private final Set<String> portletsToReload = new HashSet<String>();

    private final List<UpdateCommand> updates = new ArrayList<UpdateCommand>();

    private ThresholdsDialog dialog;

    private final DashboardManager dashboardManager;

    @Inject
    public ThresholdsPresenter(final EventBus eventBus, final ThresholdsView view,
            final DashboardManager dashboardManager) {
        super(view, eventBus);

        this.dashboardManager = dashboardManager;
    }

    /**
     * Adds a section to threshold view. Designed to be called multiple times.
     */
    public void addThresholdsSection(final PortletDataType portletData) {
        this.portletsToConfigure.add(portletData);
    }

    @Override
    protected void onBind() {
        getView().getCancelButton().addClickHandler(this);
        getView().getUpdateButton().addClickHandler(this);
    }

    private void reset() {
        getDialog().clear();

        portletsToConfigure.clear();
        updates.clear();
        portletsToReload.clear();
    }

    @Override
    public void changeValue(final String portletId, final UpdateCommand command) {
        updates.add(command);
        portletsToReload.add(portletId);
    }

    public void update() {
        if (updates.isEmpty()) {
            return;
        }

        for (final UpdateCommand command : updates) {
            command.execute();
        }

        dashboardManager.saveDashboardLayout();

        for (final String portletId : portletsToReload) {
            getEventBus().fireEvent(new PortletRefreshEvent(portletId));
        }
    }

    public void hide() {
        unbind();
    }

    private ThresholdsDialog getDialog() {
        if (dialog == null) {
            dialog = new ThresholdsDialog();
            dialog.addHideEventHandler(this);
        }
        return dialog;
    }

    @Override
    public void onHide(final ThresholdDialogHideEvent hideEvent) {
        if (hideEvent.getSource().equals(getDialog())) {
            reset();
        }
    }

    @Override
    public void onClick(final ClickEvent event) {
        if (event.getSource() == getView().getCancelButton()) {
            dialog.hide();
        } else if (event.getSource() == getView().getUpdateButton()) {
            update();
            dialog.hide();
        }
    }

    public void showDialog() {
        final ThresholdsView view = getView();
        view.clear();

        for (final PortletDataType portletData : portletsToConfigure) {
            final List<ThresholdDataType> thresholds = portletData.getThresholds();
            final String portletId = portletData.getPortletId();
            final String portletTitle = portletData.getPortletTitle();
            view.addSection(portletId, portletTitle, thresholds);
        }

        getDialog().setContent(view);
        getDialog().show();
    }

}
