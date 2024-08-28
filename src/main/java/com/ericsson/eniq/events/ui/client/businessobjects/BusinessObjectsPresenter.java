/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.businessobjects;

import com.ericsson.eniq.events.common.client.PerformanceUtil;
import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.ui.client.businessobjects.resources.ReportsSideBarResourceBundle;
import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.ericsson.eniq.events.ui.client.common.ReadLoginSessionProperties;
import com.ericsson.eniq.events.ui.client.common.ServerComms;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.events.FailedEvent;
import com.ericsson.eniq.events.ui.client.events.FailedEventHandler;
import com.ericsson.eniq.events.ui.client.events.SucessResponseEvent;
import com.ericsson.eniq.events.ui.client.events.SucessResponseEventHandler;
import com.ericsson.eniq.events.ui.client.events.tab.ReportWindowCloseEvent;
import com.ericsson.eniq.events.ui.client.events.tab.ReportWindowCloseEventHandler;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.ericsson.eniq.events.widgets.client.mask.MaskHelper;
import com.ericsson.eniq.events.widgets.client.tree.resources.TreeResourceBundle;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

/**
 * @author ecarsea
 * @since 2011
 */
public class BusinessObjectsPresenter extends BasePresenter<BusinessObjectsView> implements IReportsSideBarUiHandler,
        ReportWindowCloseEventHandler {

    private static final Logger LOGGER = Logger.getLogger(BusinessObjectsPresenter.class.getName());

    private MultipleInstanceWinId businessObjectsId;

    private String wsUrl;

    private ServerComms serverComms;

    private final IReportsSideBarView sideBarView;

    private static final String DATA = "data";

    /**
     */
    @Inject
    public BusinessObjectsPresenter(final EventBus eventBus, final BusinessObjectsView view,
            final ReportsSideBarResourceBundle resourceBundle, final TreeResourceBundle treeResourceBundle) {
        super(view, eventBus);
        PerformanceUtil.getSharedInstance().logCurrentTime("report presenter instantiating...");
        view.setPresenter(this);
        this.sideBarView = getSideBarView(resourceBundle, new MaskHelper(), treeResourceBundle); // NOPMD by eeicmsy on 09/11/11 17:20
    }

    /**
     * Allow to override for Junit
     *
     * @param resourceBundle
     * @param maskHelper
     * @return
     */
    protected IReportsSideBarView getSideBarView(final ReportsSideBarResourceBundle resourceBundle,
            final MaskHelper maskHelper, final TreeResourceBundle treeResourceBundle) {
        return new ReportsSideBarView(resourceBundle, maskHelper, treeResourceBundle);
    }

    public void init(final String tabId) {
        getView().init(tabId);
        sideBarView.init(this);
        businessObjectsId = new MultipleInstanceWinId(tabId, "");
        /** Every response coming to this tab comes here not to windows so only need tab id 
         * 
         * TODO, dont need tab id, use new server comms, remove hardcoded url maybe too?**/
        wsUrl = getCompleteURL("DASHBOARD/REPORTS");
        serverComms = new ServerComms(getEventBus());
        bind();

        getView().addSideBar(sideBarView);
        sideBarView.slideIn();
        getReportInfo();

    }

    @Override
    public void onBind() {
        registerHandler(getEventBus().addHandler(SucessResponseEvent.TYPE, new SuccessResponseEventImpl()));
        registerHandler(getEventBus().addHandler(FailedEvent.TYPE, new FailedResponseEventImpl()));
        registerHandler(getEventBus().addHandler(ReportWindowCloseEvent.TYPE, this));
    }

    /**
     * Retrieve the BO reports
     */
    public void getReportInfo() {
        sideBarView.mask("Loading Reports");
        serverComms.makeServerRequest(businessObjectsId, wsUrl, "");
    }

    public void closeAllOpenWindows() {
        getView().closeAllOpenWindows();
    }

    // TODO move to static, same method is in PortletWindow
    private static String getCompleteURL(final String url) {
        return ReadLoginSessionProperties.getEniqEventsServicesURI() + url;
    }

    @Override
    public void onReportClose(final String tabId, final String winId) {
        if (businessObjectsId.getTabId().equalsIgnoreCase(tabId)) {
            getView().closeWindow(winId);
        }
    }

    /*
     * Success response from server call
     */
    private class SuccessResponseEventImpl implements SucessResponseEventHandler {
        @Override
        public void handleResponse(final MultipleInstanceWinId multiWinId, final String requestData,
                final Response response) {
            if (businessObjectsId.equals(multiWinId)) {
                sideBarView.unmask();
                JSONValue root;
                final String text = response.getText();
                try {
                    // like server down - not a set response
                    root = JSONUtils.parse(text);
                } catch (final JSONException e) {
                    LOGGER.log(Level.WARNING, "Failing to parse response to save time information", e);

                    MessageDialog.get().show(CHECK_GLASSFISH_LOG_MESSAGE, e.getMessage(),
                            MessageDialog.DialogType.ERROR);
                    return;
                }
                if (root != null && JSONUtils.checkData(root, getEventBus(), multiWinId)) {
                    sideBarView.clear();
                    final JSONValue data = root.isObject().get(DATA);
                    if (data != null) {
                        final JSONArray array = data.isArray();
                        if (array != null && array.size() > 0) {
                            sideBarView.createTree(array.get(0));
                        }
                    }
                }
            }
        }
    }

    /*
     * Failed handling
     */
    private class FailedResponseEventImpl implements FailedEventHandler {

        @Override
        public void handleFail(final MultipleInstanceWinId multiWinId, final String requestData,
                final Throwable exception) {
            if (businessObjectsId.equals(multiWinId)) {
                sideBarView.unmask();
                MessageDialog.get().show(LOAD_REPORTS_MESSAGE, exception.getMessage(),
                        MessageDialog.DialogType.ERROR);
            }
        }
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.businessobjects.IReportsSideBarUiHandler#onItemSelected(com.google.gwt.event.logical.shared.SelectionEvent)
     */
    @Override
    public void onItemSelected(final String header, final String url) {
        sideBarView.slideOut();
        getView().addWindow(header, url);
    }
}
