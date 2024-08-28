package com.ericsson.eniq.events.ui.client.gin;

import com.ericsson.eniq.events.ui.client.businessobjects.BusinessObjectsPresenter;
import com.ericsson.eniq.events.ui.client.charts.IChartPresenter;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.IMultiMetaDataHelper;
import com.ericsson.eniq.events.ui.client.common.IUserPreferencesReader;
import com.ericsson.eniq.events.ui.client.common.service.TabManager;
import com.ericsson.eniq.events.ui.client.dashboard.DashboardPresenter;
import com.ericsson.eniq.events.ui.client.grid.JSONGridStateManager;
import com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementPresenter;
import com.ericsson.eniq.events.ui.client.groupmanagement.fileupload.GroupFileUploadPresenter;
import com.ericsson.eniq.events.ui.client.kpi.KPIConfigurationPresenter;
import com.ericsson.eniq.events.ui.client.kpi.KpiPanelPresenter;
import com.ericsson.eniq.events.ui.client.mvp.ApplicationPresenter;
import com.ericsson.eniq.events.ui.client.resources.EniqResourceBundle;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceManager;
import com.ericsson.eniq.events.ui.client.workspace.config.WorkspaceConfigService;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * Associating the module with the injector
 * <p/>
 * <p/>
 * Note that you only need to create injector methods for classes that you would
 * directly access in your top-level initialization code, such as the UI classes
 * to install in your RootPanel. You don't need to create injector methods for
 * lower-level classes that will be automatically injected.
 * <p/>
 * So for example, if Class A uses class B which uses class C, you only need to
 * create an injector method for A, as the other classes B and C will
 * automatically be injected into A. In other words, injector methods provide a
 * bridge between the Guice and non-Guice world.
 * <p/>
 * (There is going to be no code implementing this interface - see the GinModule class.
 * The injector will be created with a GWT.create in the main entry point class defined in the gwt.xml file)
 * <p/>
 * Ref GIN {@link http://code.google.com/p/google-gin/wiki/GinTutorial}
 *
 * @author eeicmsy
 * @since December 2009
 */
@GinModules(EniqEventsUIClientModule.class)
public interface EniqEventsUIGinjector extends Ginjector {
    ApplicationPresenter getMainAppPresenter();

    /**
     * @deprecated If you depend on this then inject it directly using Gin and the Inject annotation. These methods
     * are for legacy code.
     */
    @Deprecated
    TabManager getTabManager();

    /**
     * @deprecated If you depend on this then inject it directly using Gin and the Inject annotation. These methods
     * are for legacy code.
     */
    @Deprecated
    EniqResourceBundle getEniqResourceBundle();

    /**
     * @deprecated If you depend on this then inject it directly using Gin and the Inject annotation. These methods
     * are for legacy code.
     */
    @Deprecated
    DashboardPresenter getDashboardPresenter();

    /**
     * @deprecated If you depend on this then inject it directly using Gin and the Inject annotation. These methods
     * are for legacy code.
     */
    @Deprecated
    IMetaReader getMetaReader();

    /**
     * @deprecated If you depend on this then inject it directly using Gin and the Inject annotation. These methods
     * are for legacy code.
     */
    @Deprecated
    IMultiMetaDataHelper getMultiMetaDataHelper();

    /**
     * @deprecated If you depend on this then inject it directly using Gin and the Inject annotation. These methods
     * are for legacy code.
     */
    @Deprecated
    IChartPresenter getChartPresenter();

    /**
     * @deprecated If you depend on this then inject it directly using Gin and the Inject annotation. These methods
     * are for legacy code.
     */
    @Deprecated
    GroupManagementPresenter getGroupManagementPresenter();

    /**
     * @deprecated If you depend on this then inject it directly using Gin and the Inject annotation. These methods
     * are for legacy code.
     */
    @Deprecated
    GroupFileUploadPresenter getGroupFileUploadPresenter();

    /**
     * @deprecated If you depend on this then inject it directly using Gin and the Inject annotation. These methods
     * are for legacy code.
     */
    @Deprecated
    JSONGridStateManager getJsonGridStateManager();

    /**
     * @deprecated If you depend on this then inject it directly using Gin and the Inject annotation. These methods
     * are for legacy code.
     */
    @Deprecated
    KpiPanelPresenter getKpiPanelPresenter();

    /**
     * @deprecated If you depend on this then inject it directly using Gin and the Inject annotation. These methods
     * are for legacy code.
     */
    @Deprecated
    KPIConfigurationPresenter getKpiConfigPresenter();

    /**
     * @deprecated If you depend on this then inject it directly using Gin and the Inject annotation. These methods
     * are for legacy code.
     */
    @Deprecated
    BusinessObjectsPresenter getBusinessObjectsPresenter();

    /**
     * @deprecated If you depend on this then inject it directly using Gin and the Inject annotation. These methods
     * are for legacy code.
     */
    @Deprecated
    IUserPreferencesReader getUserPreferencesReader();

    /**
     * @deprecated If you depend on this then inject it directly using Gin and the Inject annotation. These methods
     * are for legacy code.
     */
    @Deprecated
    WorkspaceManager getWorkspaceManager();

    /**
     * @deprecated If you depend on this then inject it directly using Gin and the Inject annotation. These methods
     * are for legacy code.
     */
    @Deprecated
    WorkspaceConfigService getWorkspaceConfigService();

}