package com.ericsson.eniq.events.ui.client.northpanel;

import com.ericsson.eniq.events.common.client.PerformanceUtil;
import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.ui.client.events.SetupLicensesEvent;
import com.ericsson.eniq.events.ui.client.events.SetupLicensesEventHandler;
import com.ericsson.eniq.events.ui.client.events.UserLogoutEvent;
import com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants;
import com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementPresenter;
import com.ericsson.eniq.events.ui.client.groupmanagement.fileupload.GroupFileUploadPresenter;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Created with IntelliJ IDEA.
 * User: emauoco
 * Date: 05/11/12
 */
public class NorthPanelPresenter extends BasePresenter<NorthPanelView> implements SetupLicensesEventHandler {

    private final AboutDialog aboutDialog;

    private final static String HELP_SET_FEATURES = "toolbar=0,status=0,menubar=0";

    private final static String HELP_SET_LOCATION = "help/index.html";

    private final static String HELP_SET_PAGE_NAME = ""; // e.g."<a target=name href=    >";

    @Inject
    public NorthPanelPresenter(final NorthPanelView view, final EventBus eventBus, final AboutDialog aboutDialog) {
        super(view, eventBus);
        PerformanceUtil.getSharedInstance().logCurrentTime("North panel Presenter instantiating...");
        getEventBus().addHandler(SetupLicensesEvent.TYPE, this);

        this.aboutDialog = aboutDialog;

        bind();
    }

    void openEniqEventsUserGuide() {
        Window.open(GWT.getHostPageBaseURL() + HELP_SET_LOCATION, HELP_SET_PAGE_NAME, HELP_SET_FEATURES);
    }

    void launchGroupImport() {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                final GroupFileUploadPresenter presenter = MainEntryPoint.getInjector().getGroupFileUploadPresenter();
                presenter.launch(GroupManagementConstants.GroupAction.ADD);
            }

            @Override
            public void onFailure(final Throwable reason) {
                Window.alert("code splitting failed..");
            }
        });
    }

    void launchGroupDelete() {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                final GroupFileUploadPresenter presenter = MainEntryPoint.getInjector().getGroupFileUploadPresenter();
                presenter.launch(GroupManagementConstants.GroupAction.DELETE);
            }

            @Override
            public void onFailure(final Throwable reason) {
                Window.alert("code splitting failed..");
            }
        });
    }

    void launchGroupManagement() {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                final GroupManagementPresenter presenter = MainEntryPoint.getInjector().getGroupManagementPresenter();
                presenter.launch();
            }

            @Override
            public void onFailure(final Throwable reason) {
                Window.alert("code splitting failed..");
            }
        });
    }

    void showAboutDialog() {
        aboutDialog.show();
        aboutDialog.center();
    }

    @Override
    protected void onBind() {
        displayAdminBroadCastMessage();
        displayUserName();
    }

    /* display system admin message on banner*/
    private void displayAdminBroadCastMessage() {
        final String nativeAdmnMessage = getNativeAdminBroadCast();
        if (nativeAdmnMessage != null && (!nativeAdmnMessage.equals("null"))) {
            getView().setSystemAdminBannerMessage(nativeAdmnMessage);
        }
    }

    /* returns the current admin message for banner
    /* (exposed for junit to over-ride)
    */
    native String getNativeAdminBroadCast()/*-{
		return $wnd.window.adminMsg;
    }-*/;

    private void displayUserName() {
        final String nativeReadUpUser = getLoginUserName();

        final String user = (nativeReadUpUser == null) ? "[undefined user]" : nativeReadUpUser;
        getView().setUserLoggedName(user);
    }

    private String getLoginUserName() {
        return CommonParamUtil.getLoginUserName();
    }

    void userLogout() {
        getEventBus().fireEvent(new UserLogoutEvent());
    }

    /*
    * call to native javascript method
    * onLogOut to clean up Session vars
    */
    private native void logOut() /*-{
		$wnd.onLogout();
    }-*/;

    @Override
    public void onLicensesEvent(final SetupLicensesEvent licensesEvent) {
        aboutDialog.addProductLicenses(licensesEvent.getLicenses());
        getView().configureOptionsMenu(licensesEvent.getLicenses());
    }
}
