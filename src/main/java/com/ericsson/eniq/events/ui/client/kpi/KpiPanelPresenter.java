package com.ericsson.eniq.events.ui.client.kpi;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

import com.ericsson.eniq.events.common.client.datatype.IPropertiesState;
import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.common.client.preferences.IUserPreferencesHelper;
import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.common.client.service.IDataService;
import com.ericsson.eniq.events.common.client.service.RestfulRequestBuilder;
import com.ericsson.eniq.events.ui.client.common.Constants;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.ReadLoginSessionProperties;
import com.ericsson.eniq.events.ui.client.datatype.KpiPanelDataType;
import com.ericsson.eniq.events.ui.client.datatype.KpiPanelSeverityType;
import com.ericsson.eniq.events.ui.client.datatype.KpiPanelType;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.events.tab.TabChangeEvent;
import com.ericsson.eniq.events.ui.client.events.tab.TabChangeEventHandler;
import com.ericsson.eniq.events.ui.client.kpi.widget.ButtonHandler;
import com.ericsson.eniq.events.ui.client.kpi.widget.IndicatorButton.IconType;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceManager;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import static com.ericsson.eniq.events.common.client.CommonConstants.FIRST_URL_PARAM_DELIMITOR;

public class KpiPanelPresenter extends BasePresenter<KpiPanelView> {

    private final static Logger LOGGER = Logger.getLogger(KpiPanelPresenter.class.getName());
    
    private static final String CONTENT_TYPE = "Content-Type";
    
    private static final String APPLICATION_JSON = "application/json";

    private final EventBus eventBus;

    private IDataService dataService;

    final MultipleInstanceWinId multiWinID;

    private final IUserPreferencesHelper userPreferencesHelper;

    Timer refreshTimer;

    private final KPIRefreshRateUpdateHandler kpiRefreshRateUpdateHandler = new KPIRefreshRateUpdateHandlerImpl();

    public int currentRefreshRate;

    private String kpiUrl;// = "NETWORK/KPI_NOTIFICATION/NOTIFICATION_COUNT";

    private final IMetaReader metaReader;

    private static final int NUMBER_MILLISECONDS_IN_SECOND = 1000;
    private static final int NUMBER_SECONDS_IN_MINUTES = 60;

    private KPIConfigurationPresenter kpiConfigurationPresenter;

    private Provider<ButtonHandler> buttonHandlerProvider;
    private TabChangeEventHandler tabChangeEventHandler;
    private WorkspaceManager workspaceManager;

    private class TabChangeEventHandlerImpl implements TabChangeEventHandler {
        @Override
        public void onTabChangeEvent(final TabChangeEvent tabChangeEvent) {

            if(workspaceManager.getActiveWorkspaces().containsKey(tabChangeEvent.getTabID())) {
                if(!getView().isAttached()) {
                    RootPanel.get().add(getView());
                }
            } else {
                if (getView().isAttached()) {
                    RootPanel.get().remove(getView());
                }
            }
        }
    }

    @Inject
    public KpiPanelPresenter(final KpiPanelView view, final EventBus eventBus,
                             final IUserPreferencesHelper userPreferencesHelper, final IMetaReader metaReader,
                             IDataService dataService, KPIConfigurationPresenter kpiConfigurationPresenter,
            Provider<ButtonHandler> buttonHandlerProvider, WorkspaceManager workspaceManager) {
        super(view, eventBus);
        this.kpiConfigurationPresenter = kpiConfigurationPresenter;
        this.workspaceManager = workspaceManager;
        this.eventBus = eventBus;
        this.dataService = dataService;
        this.tabChangeEventHandler = new TabChangeEventHandlerImpl();
        this.userPreferencesHelper = userPreferencesHelper;
        this.metaReader = metaReader;
        this.buttonHandlerProvider = buttonHandlerProvider;
        this.multiWinID = new MultipleInstanceWinId("", "kpiPanelId");

        bind();
    }

    @Override
    protected void onBind() {
        eventBus.addHandler(KPIRefreshRateUpdateEvent.TYPE, kpiRefreshRateUpdateHandler);
        eventBus.addHandler(TabChangeEvent.TYPE, tabChangeEventHandler);
        onInitialize();
    }


    private void onInitialize() {
        final KpiPanelType kpiPanelType = metaReader.getKpiPanelMetaData();

        kpiUrl = kpiPanelType.getUrl();
        refreshKpiPanel();

        //refreshKpiPanel() only if the KPI Panel is enabled
        if(kpiPanelType.isEnabled()) {
            // refreshKpiPanel();
            // Setup timer to refresh panel automatically.
            refreshTimer = new Timer() {
                @Override
                public void run() {
                    refreshKpiPanel();
                }
            };
            refreshTimer.scheduleRepeating(getConfiguredRefreshRateInMilliseconds());
        }

        configure(metaReader.getKpiPanelMetaData());

        // This assumes an initial workspace...
        RootPanel.get().add(getView());
    }

    private void configure(final KpiPanelType kpiPanelType) {
        final KpiPanelView view = getView();

        //disabling the KPI Panel is it has set to false ie.when no license is installed on the server
        if(!kpiPanelType.isEnabled()) {
            view.setVisible(false);
            return;
        }
        view.criticalPanel.setEventBus(eventBus);
        view.majorPanel.setEventBus(eventBus);
        view.minorPanel.setEventBus(eventBus);
        view.warningPanel.setEventBus(eventBus);

        for (final KpiPanelSeverityType kpiPanelSeverityType : kpiPanelType.getKpiPanelSeverityType()) {
            final String severity = kpiPanelSeverityType.getSeverity().toLowerCase();

            final String winId = kpiPanelSeverityType.getMenuItemName();

            switch (IconType.fromString(severity)) {
                case CRITICAL:
                    view.criticalPanel.setWindowId(winId);
                    view.criticalPanel.addHandler(buttonHandlerProvider.get());
                    view.criticalPanel.setPopLabel(kpiPanelSeverityType.getPopUpMessage());
                    break;
                case MAJOR:
                    view.majorPanel.setWindowId(winId);
                    view.majorPanel.addHandler(buttonHandlerProvider.get());
                    view.majorPanel.setPopLabel(kpiPanelSeverityType.getPopUpMessage());
                    break;
                case MINOR:
                    view.minorPanel.setWindowId(winId);
                    view.minorPanel.addHandler(buttonHandlerProvider.get());
                    view.minorPanel.setPopLabel(kpiPanelSeverityType.getPopUpMessage());
                    break;
                case WARNING:
                    view.warningPanel.setWindowId(winId);
                    view.warningPanel.addHandler(buttonHandlerProvider.get());
                    view.warningPanel.setPopLabel(kpiPanelSeverityType.getPopUpMessage());
                    break;
                default:
                    LOGGER.log(Level.WARNING, "Unknown KPI Panel severity type: " + severity);
            }
        }
    }

    int getConfiguredRefreshRateInMilliseconds() {
        final int refreshRateInMinutes = new KPIConfigurationProperties().getRefreshRateParameterInMinutes(getProperties(), metaReader);
        return refreshRateInMinutes * NUMBER_SECONDS_IN_MINUTES * NUMBER_MILLISECONDS_IN_SECOND;
    }

    private void refreshKpiPanel() {
        if(kpiUrl == null || kpiUrl.isEmpty()) {
            kpiUrl= Constants.KPI_URL;
        }
        final String wsURL = getCompletedURL(kpiUrl);
        dataService.performRemoteOperation(RestfulRequestBuilder.State.GET, wsURL, "", new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                 if (response.getStatusCode() == Response.SC_OK) {
                    // avoid parsing results if content is other than json type
                    String contentType = response.getHeader(CONTENT_TYPE);
                    if (contentType != null && contentType.contains(APPLICATION_JSON)) {
                        final String text = response.getText();
                        final JSONValue jsonValue = KpiPanelUtils.parseJsonString(text);
                        if (jsonValue != null) {
                            final Map<String, KpiPanelDataType> kpiPanelDataType = KpiPanelUtils.getKpiPanelDataType(jsonValue);
                            updatePanel(kpiPanelDataType, KpiPanelUtils.getTotalBreaches());
                        }
                    }
                }

            }

            @Override
            public void onError(Request request, Throwable exception) {
                LOGGER.log(Level.WARNING, "Error retrieving KPI data", exception);
            }
        });
    }

    public void launchKPIConfiguration() {
        final KPIConfigurationPresenter configPresenter = kpiConfigurationPresenter;
        configPresenter.getDialog().show();
    }

    private void updatePanel(final Map<String, KpiPanelDataType> kpiPanelDataTypes, final int totNoOfBreaches) {
        final KpiPanelView view = getView();

        final NumberFormat formatted = NumberFormat.getFormat("0.0");

        final double parseDouble = Double.parseDouble(totNoOfBreaches + ".00");

        int criticalBreaches = 0;
        int majorBreaches = 0;
        int minorBreaches = 0;
        int warningBreaches = 0;

        final KpiPanelDataType criticalBreachesDataType = kpiPanelDataTypes.get("critical");
        final KpiPanelDataType majorBreachesDataType = kpiPanelDataTypes.get("major");
        final KpiPanelDataType minorBreachesDataType = kpiPanelDataTypes.get("minor");
        final KpiPanelDataType warningBreachesDataType = kpiPanelDataTypes.get("warning");

        if (criticalBreachesDataType != null) {
            criticalBreaches = criticalBreachesDataType.getNoOfBreaches();
        }
        if (majorBreachesDataType != null) {
            majorBreaches = majorBreachesDataType.getNoOfBreaches();
        }
        if (minorBreachesDataType != null) {
            minorBreaches = minorBreachesDataType.getNoOfBreaches();
        }
        if (warningBreachesDataType != null) {
            warningBreaches = warningBreachesDataType.getNoOfBreaches();
        }

        view.criticalPanel.setLabel(formatted.format(getPercent(criticalBreaches, parseDouble)));
        view.criticalPanel.setBreaches(criticalBreaches);
        view.majorPanel.setLabel(formatted.format(getPercent(majorBreaches, parseDouble)));
        view.majorPanel.setBreaches(majorBreaches);
        view.minorPanel.setLabel(formatted.format(getPercent(minorBreaches, parseDouble)));
        view.minorPanel.setBreaches(minorBreaches);
        view.warningPanel.setLabel(formatted.format(getPercent(warningBreaches, parseDouble)));
        view.warningPanel.setBreaches(warningBreaches);

    }

    private double getPercent(final int noOfBreaches, final double totNoOfBreaches) {
        return (totNoOfBreaches == 0) ? 0 : (noOfBreaches * 100.0) / totNoOfBreaches;
    }

    private String getCompletedURL(final String urlEnd) {
        final StringBuilder completeURL = new StringBuilder();
        completeURL.append(ReadLoginSessionProperties.getEniqEventsServicesURI());
        completeURL.append(urlEnd);
        completeURL.append(getURLParameters());
        return completeURL.toString();
    }

    private String getURLParameters() {
        final StringBuilder urlParameters = new StringBuilder();
        urlParameters.append(FIRST_URL_PARAM_DELIMITOR);
        urlParameters.append("time=");
        urlParameters.append(getTimeQueryParameter());
        urlParameters.append(CommonParamUtil.getTimeZoneURLParameter());
        return urlParameters.toString();
    }

    private String getTimeQueryParameter() {
        return new KPIConfigurationProperties()
                .convertStoredPropertiesToTimeQueryParameter(getProperties(), metaReader);
    }

    private IPropertiesState getProperties() {
        return userPreferencesHelper.getStateById(KPIConfigurationConstants.KPI_NOTIFICATION_STORAGE_KEY, IPropertiesState.class);
    }

    KPIRefreshRateUpdateHandler getKPIRefreshRateUpdateHandler() {
        return kpiRefreshRateUpdateHandler;
    }

    private class KPIRefreshRateUpdateHandlerImpl implements KPIRefreshRateUpdateHandler {

        @Override
        public void onRefreshRateUpdate() {
            final int configuredRefreshRate = getConfiguredRefreshRateInMilliseconds();
            if (configuredRefreshRate != currentRefreshRate) {
                currentRefreshRate = configuredRefreshRate;
                refreshTimer.scheduleRepeating(configuredRefreshRate);
            }

        }
    }
}
