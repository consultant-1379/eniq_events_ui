package com.ericsson.eniq.events.ui.client.gin; // NOPMD by eeicmsy on 12/10/11 20:13

import com.ericsson.eniq.events.common.client.CommonMain;
import com.ericsson.eniq.events.common.client.preferences.IJsonAutoBeanFactory;
import com.ericsson.eniq.events.common.client.preferences.IUserPreferencesHelper;
import com.ericsson.eniq.events.common.client.preferences.UserPreferencesHelper;
import com.ericsson.eniq.events.common.client.preferences.UserPreferencesProvider;
import com.ericsson.eniq.events.common.client.service.DataServiceImpl;
import com.ericsson.eniq.events.common.client.service.IDataService;
import com.ericsson.eniq.events.common.client.service.IServiceProperties;
import com.ericsson.eniq.events.common.client.service.MapService;
import com.ericsson.eniq.events.common.client.service.MapServiceImpl;
import com.ericsson.eniq.events.common.client.service.StorageService;
import com.ericsson.eniq.events.ui.client.EniqEventsServiceProperties;
import com.ericsson.eniq.events.ui.client.charts.ChartConfigTemplateRegistryImpl;
import com.ericsson.eniq.events.ui.client.charts.ChartPresenter;
import com.ericsson.eniq.events.ui.client.charts.IChartConfigTemplateRegistry;
import com.ericsson.eniq.events.ui.client.charts.IChartPresenter;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.IMultiMetaDataHelper;
import com.ericsson.eniq.events.ui.client.common.IUserPreferencesReader;
import com.ericsson.eniq.events.ui.client.common.MetaDataRetriever;
import com.ericsson.eniq.events.ui.client.common.MetaReader;
import com.ericsson.eniq.events.ui.client.common.MultiMetaDataHelper;
import com.ericsson.eniq.events.ui.client.common.UIComponentFactory;
import com.ericsson.eniq.events.ui.client.common.UIComponentFactoryImpl;
import com.ericsson.eniq.events.ui.client.common.UserPreferencesReader;
import com.ericsson.eniq.events.ui.client.common.service.DashboardManager;
import com.ericsson.eniq.events.ui.client.common.service.DashboardManagerImpl;
import com.ericsson.eniq.events.ui.client.common.service.TabManager;
import com.ericsson.eniq.events.ui.client.common.service.TabManagerImpl;
import com.ericsson.eniq.events.ui.client.dashboard.DashboardPresenter;
import com.ericsson.eniq.events.ui.client.dashboard.DashboardView;
import com.ericsson.eniq.events.ui.client.dashboard.PortletTemplateRegistry;
import com.ericsson.eniq.events.ui.client.dashboard.PortletTemplateRegistryImpl;
import com.ericsson.eniq.events.ui.client.dashboard.portlet.JsonAutoBeanDataFactory;
import com.ericsson.eniq.events.ui.client.groupmanagement.multiselect.MultiSelectGroupRegistry;
import com.ericsson.eniq.events.ui.client.groupmanagement.multiselect.MultiSelectGroupRegistryImpl;
import com.ericsson.eniq.events.ui.client.kpi.KPIConfigurationPresenter;
import com.ericsson.eniq.events.ui.client.kpi.KPIConfigurationView;
import com.ericsson.eniq.events.ui.client.kpi.KpiPanelPresenter;
import com.ericsson.eniq.events.ui.client.kpi.KpiPanelView;
import com.ericsson.eniq.events.ui.client.main.IMainView;
import com.ericsson.eniq.events.ui.client.main.MainPresenter;
import com.ericsson.eniq.events.ui.client.main.MainView;
import com.ericsson.eniq.events.ui.client.mvp.ApplicationPresenter;
import com.ericsson.eniq.events.ui.client.northpanel.NorthPanelPresenter;
import com.ericsson.eniq.events.ui.client.northpanel.NorthPanelView;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceConfigAutoBeanFactory;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceManager;
import com.ericsson.eniq.events.ui.client.workspace.config.WorkspaceConfigService;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Declare bindings
 * <p/>
 * Bind the various classes and providers using a Guice module. The module class
 * looks almost exactly like it would in regular Guice (We use the GinModule and
 * AbstractGinModule instead of Module and AbstractModule.)
 * <p/>
 * Ref GIN {@link http://code.google.com/p/google-gin/wiki/GinTutorial}
 *
 * @author eeicmsy
 * @since Jan 2010
 *        <p/>
 *        TODO this is all inject in one module - have seen example with several
 *        where extend AbstractGinModule directly in each and then specify more
 *        modules in the Ginjector class annotations
 */
public class EniqEventsUIClientModule extends AbstractGinModule {

    public static final String UI_METADATA_PATH = "UI_METADATA_PATH";

    @Override
    protected void configure() {
        //        PerformanceUtil.getSharedInstance().clear("EniqConfiguration");
        /*
        DO NOT ADD RESOURCE BUNDLES HERE as they are automatically bound by gin
        Pretty much anything in GWT that involves annotating an interface is automatically bound by gin as
        efficiently as possible. Adding that stuff here is pointless. See also: Messages, Constants...
        */

        bind(AutoBeanFactory.class).to(JsonAutoBeanDataFactory.class).in(Singleton.class);
        bind(IJsonAutoBeanFactory.class).to(JsonAutoBeanDataFactory.class).in(Singleton.class);
        bind(IUserPreferencesHelper.class).to(UserPreferencesHelper.class).in(Singleton.class);
        bind(IUserPreferencesReader.class).to(UserPreferencesReader.class).in(Singleton.class);

        /*
        Depending on whether you have a Server with a Table to store the user Setting you will
        have to bind the correct class to the Storage server, use:
        ServerStorage.class              for production mode (default) ie. Table is present
        BrowserLocalStorage.class        for local deployment against dummy json or with a pre 2.1.8 TP on the server
        */

        bind(ApplicationPresenter.class).in(Singleton.class);

        bind(TabManager.class).to(TabManagerImpl.class).in(Singleton.class);
        bind(UIComponentFactory.class).to(UIComponentFactoryImpl.class).in(Singleton.class);

        bind(DashboardManager.class).to(DashboardManagerImpl.class).in(Singleton.class);
        bind(DashboardPresenter.class).in(Singleton.class);
        bind(DashboardView.class).in(Singleton.class);

        bind(IChartConfigTemplateRegistry.class).to(ChartConfigTemplateRegistryImpl.class).in(Singleton.class);
        bind(IChartPresenter.class).to(ChartPresenter.class);
        bind(PortletTemplateRegistry.class).to(PortletTemplateRegistryImpl.class).in(Singleton.class);

        bind(MultiSelectGroupRegistry.class).to(MultiSelectGroupRegistryImpl.class).in(Singleton.class);

        bind(MapService.class).to(MapServiceImpl.class).in(Singleton.class);

        /*
        You can bind the UI_METADATA_PATH using the following values for the metaData String
        "METADATA/UI"                     for production mode (default)
        "UIMetaData_Dev.json"             for eniq_events_ui Development Mode
        "UIMetaData_Dev_MinForSON.json"   for SON VIS Development Mode
        */
        final String metaData = "METADATA/UI";

        bindConstant().annotatedWith(Names.named(UI_METADATA_PATH)).to(metaData);

        bind(MainPresenter.class);

        bind(IMainView.class).to(MainView.class);

        bind(NorthPanelPresenter.class).in(Singleton.class);
        bind(NorthPanelView.class).in(Singleton.class);

        bind(IMetaReader.class).to(MetaReader.class).in(Singleton.class);
        bind(IMultiMetaDataHelper.class).to(MultiMetaDataHelper.class).in(Singleton.class);

        requestStaticInjection(MetaDataRetriever.class);

        bind(KpiPanelView.class).in(Singleton.class);
        bind(KpiPanelPresenter.class).in(Singleton.class);
        bind(KPIConfigurationView.class).in(Singleton.class);
        bind(KPIConfigurationPresenter.class).in(Singleton.class);

        bind(IServiceProperties.class).to(EniqEventsServiceProperties.class);
        bind(IDataService.class).to(DataServiceImpl.class);
        bind(WorkspaceManager.class).in(Singleton.class);

        bind(WorkspaceConfigAutoBeanFactory.class).in(Singleton.class);
        bind(WorkspaceConfigService.class).in(Singleton.class);
        //        PerformanceUtil.getSharedInstance().logTimeTaken("Time taken in configuration : ", "EniqConfiguration");
    }

    @Provides
    @Singleton
    public UserPreferencesProvider getIUserPreferencesProvider() {
        return CommonMain.getCommonInjector().getUserPreferencesProvider();
    }

    @Provides
    @Singleton
    public StorageService getStorageService() {
        return CommonMain.getCommonInjector().getStorageService();
    }

    @Provides
    @Singleton
    public EventBus getEventBus() {
        return CommonMain.getCommonInjector().getEventBus();
    }
}