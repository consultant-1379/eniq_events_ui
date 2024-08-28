/**
 *
 */
package com.ericsson.eniq.events.ui.client.kpi.widget;

import java.util.List;

import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.service.TabManager;
import com.ericsson.eniq.events.ui.client.common.widget.TimeRangeComboBox;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.kpi.KPIConfigurationPresenter;
import com.ericsson.eniq.events.ui.client.kpi.events.StateChangeEvent;
import com.ericsson.eniq.events.ui.client.kpi.widget.IndicatorButton.IButtonHandler;
import com.ericsson.eniq.events.ui.client.kpi.widget.IndicatorButton.IconType;
import com.ericsson.eniq.events.ui.client.main.AbstractWindowLauncher;
import com.ericsson.eniq.events.ui.client.main.GridLauncher;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceManager;
import com.ericsson.eniq.events.ui.client.workspace.launch.WorkspaceLaunchView;
import com.extjs.gxt.ui.client.data.ModelData;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/** @author evidbab */
public class ButtonHandler implements IButtonHandler {

    private final EventBus eventBus;
    private KPIConfigurationPresenter kpiConfigurationPresenter;
    private TabManager tabManager;
    private IMetaReader metaReader;
    private WorkspaceManager workspaceManager;
    private WorkspaceLaunchView workspaceLaunchView;

    @Inject
    public ButtonHandler(final EventBus eventBus, KPIConfigurationPresenter kpiConfigurationPresenter,
            TabManager tabManager, IMetaReader metaReader, WorkspaceManager workspaceManager,
            WorkspaceLaunchView workspaceLaunchView) {
        this.workspaceManager = workspaceManager;
        this.tabManager = tabManager;
        this.metaReader = metaReader;
        this.kpiConfigurationPresenter = kpiConfigurationPresenter;
        this.eventBus = eventBus;
        this.workspaceManager = workspaceManager;
        this.workspaceLaunchView = workspaceLaunchView;
    }

    @Override
    public void onClick(final IconType type, final String metaMenuItemID, final String launchTime) {
        launchWindow(type.toString(), metaMenuItemID, launchTime);
    }


    private void launchWindow(final String iconType, final String metaMenuItemID, final String launchTime) {
            // Hacked to bits !
            final StateChangeEvent stateChangeEvent = new StateChangeEvent(iconType);
            eventBus.fireEvent(stateChangeEvent);
            final MetaMenuItem item = metaReader.getMetaMenuItemFromID(metaMenuItemID);

            final AbstractWindowLauncher windowLauncher = new GridLauncher(item, eventBus,
                    workspaceManager.getActiveWorkspacePresenter().getView().getWindowContainer().getWindowContainerPanel(),
                    workspaceManager.getActiveWorkspacePresenter().getView().getPresenter().getWorkspaceLaunchPresenter()
                            .getWorkspaceController());


        if(!workspaceManager.getActiveWorkspacePresenter().getWorkspaceLaunchPresenter().getView().isPinned()) {
        workspaceManager.getActiveWorkspacePresenter().getWorkspaceLaunchPresenter().getView().slideOut();
        }

        if (launchTime == null) {
                windowLauncher.launchWindow(false);
            } else {
                final TimeInfoDataType launchWinTime = getWindowLaunchTime(launchTime);
                windowLauncher.launchWindow(launchWinTime, false, "");
            }


    }

    private TimeInfoDataType getWindowLaunchTime(final String launchTime) {
        if (launchTime == null) {
            return null;
        }
        final TimeInfoDataType launchWinTime = new TimeInfoDataType();
        final KPIConfigurationPresenter configPresenter = kpiConfigurationPresenter;

        final TimeRangeComboBox combo = configPresenter.getView().getRefreshTimeCombo();
        final List<ModelData> models = combo.getStore().getModels();

        for (final ModelData model : models) {
            if (launchTime.equals(combo.getDisplay(model))) {
                launchWinTime.timeRangeDisplay = launchTime;
                launchWinTime.timeRange = combo.getValue(model);
                launchWinTime.timeRangeSelectedIndex = combo.getIndex(model);

                return launchWinTime;
            }
        }

        return TimeInfoDataType.DEFAULT;

    }
}
