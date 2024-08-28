package com.ericsson.eniq.events.ui.client.mvp;

import com.ericsson.eniq.events.common.client.PerformanceUtil;
import com.ericsson.eniq.events.common.client.map.MapResourceLoader;
import com.ericsson.eniq.events.highcharts.client.HighChartsJSMethodExporter;
import com.ericsson.eniq.events.highcharts.client.HighchartsResources;
import com.ericsson.eniq.events.sessionbrowser.client.charts.ClientExceptionHandler;
import com.ericsson.eniq.events.ui.client.gin.EniqEventsUIGinjector;
import com.ericsson.eniq.events.widgets.client.monitor.EventsMonitor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point (as defined in gwt.xml file)
 *
 * @author eeicmsy
 * @since Jan 2010
 */
public class MainEntryPoint implements EntryPoint {

    private static final EniqEventsUIGinjector injector = GWT.create(EniqEventsUIGinjector.class);

    /**
     * @deprecated This method is a hack for legacy code to get the injector and should not be reused. Use Gin and
     * the Inject annotation unless this is absolutely needed. Do not use it in new code. Every time it is reused a
     * small cuddly animal dies.
     */
    @Deprecated
    public static EniqEventsUIGinjector getInjector() {
        return injector;
    }

    @Override
    public void onModuleLoad() {
        PerformanceUtil.getSharedInstance().logCurrentTime("JSecurityCheck end : ");
        PerformanceUtil.getSharedInstance().clear();
        /** Export High Charts JS Methods to the Document Window **/
        HighChartsJSMethodExporter.get().exportJSMethods();
        initialiseApplication();

        EventsMonitor.initMonitor();
        MapResourceLoader.injectResources();
        HighchartsResources.injectResources();
        PerformanceUtil.getSharedInstance().logTimeTaken("Total time taken in initiallization : ");
        PerformanceUtil.getSharedInstance().logCurrentTime("Eniq onModuleLoad finish : ");
        GWT.setUncaughtExceptionHandler(new ClientExceptionHandler());      //Used to Override IE TypeError caused by isOrHasChildImpl in DOMImplTrident
    }

    /*
     * initialise the start up screen of the GUI
     */
    private void initialiseApplication() {
        final ApplicationPresenter appPresenter = injector.getMainAppPresenter();
        appPresenter.go(RootPanel.get());
    }
}