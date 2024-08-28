/**
 * -----------------------------------------------------------------------
 o *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget;

import com.ericsson.eniq.events.ui.client.common.Constants;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.BaseToolBar;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.common.comp.IBaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.common.comp.WindowState;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.events.MaskEvent;
import com.ericsson.eniq.events.ui.client.gin.EniqEventsUIGinjector;
import com.ericsson.eniq.events.ui.client.main.AbstractWindowLauncher;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessagePanel;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessageType;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Widget;

import static com.ericsson.eniq.events.ui.client.common.CSSConstants.LOADING_STYLE_NAME;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;

/**
 * Shared view class to capture some shared functionality for window views for grids and charts
 * All views for windows should extend this
 *
 * @author eeicmsy
 * @see com.ericsson.eniq.events.ui.client.common.widget.EventGridView
 * @see com.ericsson.eniq.events.ui.client.charts.window.ChartWindowView
 */
public abstract class AbstractBaseWindowDisplay extends BaseWindow implements IExtendedWidgetDisplay {

    private final EniqEventsUIGinjector injector = MainEntryPoint.getInjector();

    /* default user selected time data (in view class)*/
    private TimeInfoDataType timeData = new TimeInfoDataType();

    /*TODO would like possibly remove from view (as in presenter,
    meta menu item , breadcrumb and presenter), but the one in the view
    is the one  known not being cleared at moment (used CSV etc) */
    private String widgetSpecificParams = EMPTY_STRING;

    /*
    * particularly for CSV - the  widgetSpecificParamsPrevious are cleared
    * on grid population (success result)
    */
    private String widgetSpecificParamsPrevious = EMPTY_STRING;

    /**
     * If have to toggle window (from graph to grid) this is the type
     * of launcher that will be required
     */
    protected AbstractWindowLauncher toggleWindowLauncher;

    private IBaseWindowPresenter presenter;

    // El to  hold which element is actually being masked.
    // For some reason successive calls to getBody().getParent() doesn't seem
    // to return the same element (that is, the call to unmask() doesn't work)
    private El maskedEl = null;

    private final CancelButtonListener cancelListener = new CancelButtonListener();

    protected final IWorkspaceController workspaceController;

    private ContentPanel errorMessageHolder = new ContentPanel();  //as we have gxt windows we need a gxt panel to hold this to allow for removal

    private ComponentMessagePanel errorMessage = new ComponentMessagePanel();

    private boolean errorMessageShowing = false;

    /**
     * Abstract class constructor passing instance type to support
     * views launched from instance button (e.g. KPI)
     *
     */
    protected AbstractBaseWindowDisplay(final MultipleInstanceWinId id,
            final IWorkspaceController workspaceController, final ContentPanel constrainArea, final String title,
            final String icon, final WindowState windowState) {
        super(id, constrainArea, title, icon, workspaceController.getEventBus(), windowState);
        this.workspaceController = workspaceController;
    }

    /**
     * Setting the presenter on the view at launch time
     * (its is actually expected in MVP that the view will have the presenter)
     *
     * @param presenter - the presenter reference (important to set on view creation
     *
     * @see com.ericsson.eniq.events.ui.client.main.AbstractWindowLauncher
     */
    @Override
    public void setPresenter(final IBaseWindowPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Return presenter reference exposed methods from the view
     *
     * @return presenter reference
     */
    @Override
    public IBaseWindowPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void fitIntoContainer() {
        getWidget().fitIntoContainer();
    }

    @Override
    //TODO possibly remove this in time - make all access via one (i.e. presenter)
    public abstract MetaMenuItem getViewSettings();

    @Override
    public BaseToolBar getWindowToolbar() {
        return winToolBar;
    }

    @Override
    public Widget asWidget() {
        return getWidget();
    }

    @Override
    public void startProcessing() {
        if (getWidget().isRendered() && maskedEl == null) {
            maskedEl = getWidget().getBody().getParent();

            addCancelMaskOption();
            workspaceController.getEventBus().fireEvent(new MaskEvent(true, workspaceController.getTabOwnerId()));
        }
    }

    @Override
    public void stopProcessing() {
        if (getWidget().isRendered()) {
            if (maskedEl != null) {
                maskedEl.unmask();
                cleanCancelMask();
                maskedEl = null;
            }

        }
        workspaceController.getEventBus().fireEvent(new MaskEvent(false, workspaceController.getTabOwnerId()));
    }

    @Override
    public IWorkspaceController getWorkspaceController() {
        return workspaceController;
    }

    @Override
    public BaseWindow getParentWindow() {
        return this;
    }

    @Override
    public void setToolbarButtonEnabled(final String buttonID, final boolean isEnabled) {
        final Component btn = winToolBar.getItemByItemId(buttonID);
        if (btn != null) { // if meta data did not define this button for grid
            btn.setEnabled(isEnabled);
        }
    }

    @Override
    public void addWidget(final Widget child) {
        getWidget().addWidget(child);
    }

    @Override
    public void upDateDrillWindowTitle(final String drillWinTitle) {
        if (drillWinTitle != null && !drillWinTitle.trim().isEmpty() && (getBaseWindowTitle() != null && !getBaseWindowTitle().contains(drillWinTitle))) {
            final StringBuilder buffer = new StringBuilder(drillWinTitle);
            buffer.append(DASH);
            buffer.append(getBaseWindowTitle());
            updateTitle(buffer.toString());
        }
    }

    @Override
    public void setWidgetTitle(final String value) {
        String newFullTitle = applyTitle(value);
        getWidget().setWindowTitle(newFullTitle);
    }

    @Override
    public void updateTimeFromPresenter(final TimeInfoDataType time) {
        // call coming from presenter - implies no need to update presenter
        timeData = time;
        if (getWidget().isRendered()) { // important or firefox will display a toolbar prior to window launch
            winToolBar.initTimeRangeComboBox(isKpiWindow());
            winToolBar.updateTimeRangeComp(time,isKpiWindow());
        }

    }
    /*
    * Checking the icon to ensure the window is from KPI Panel or from others
    * @return  true if window is from KPI Panel
    */
    private boolean isKpiWindow() {
        boolean isKpiWindow=getIcon().toUpperCase().contains(Constants.KPI_PANEL_ICON);
        return isKpiWindow;
    }

    @Override
    public void updateTime(final TimeInfoDataType time) {

        this.updateTimeFromPresenter(time);
        if (getWidget().isRendered()) {

            if (presenter != null) {
                presenter.setTimeData(time);
            }
        }
    }

    @Override
    public TimeInfoDataType getTimeData() {
        return timeData;
    }

    @Override
    public String getWidgetSpecificURLParams() {
        return widgetSpecificParams;
    }

    @Override
    public void updateWidgetSpecificURLParams(final String value) {
        if (value == null || value.isEmpty()) {
            // cache previous - clearing is happening after success but
            // still want it in case press CSV button
            widgetSpecificParamsPrevious = widgetSpecificParams;
        }
        widgetSpecificParams = value;
    }

    @Override
    public String getWidgetSpecificURLParamsForCSV() {
        return widgetSpecificParamsPrevious;
    }

    @Override
    public El getWindowBody() {
        return getWidget().getBody();
    }

    /*
    * ensure there is no orphan cancel button sitting on the DOM
    */
    private void cleanCancelMask() {
        // allow several cancel buttons
        final String cancelButtonId = getCancelButtonId();
        if (DOM.getElementById(cancelButtonId) != null) {
            DOM.getElementById(cancelButtonId).removeFromParent();
        }
    }

    /*
    * Id which will allow several cancel buttons to be
    * present on UI
    * @return  unique cancel button id for window
    */
    private String getCancelButtonId() {
        return generateCompositeId() + CANCEL_MASK_BTN;
    }

    /*
    * adds a cancel button to the mask div
    * that is over-layed on widgets while they are retrieving info
    * This will send cancel request call to server and abort our request
    */
    private void addCancelMaskOption() {
        cleanCancelMask();

        final String cancelButtonId = getCancelButtonId();

        //Add the button to the mask and setup the click listener
        maskedEl.mask(injector.getMetaReader().getLoadingMessage() + "<br>" + "<button id='" + cancelButtonId
                + "' type='button' class='cancelMask'>Cancel</button>", LOADING_STYLE_NAME);

        final Element ell = DOM.getElementById(cancelButtonId);
        // TODO Drill downs on pie charts somehow have null value
        if (ell != null) {
            DOM.sinkEvents((com.google.gwt.user.client.Element) ell, com.google.gwt.user.client.Event.ONCLICK);
            // Clear out the element's event listener and add cancel Listener
            DOM.setEventListener((com.google.gwt.user.client.Element) ell, null);
            DOM.setEventListener((com.google.gwt.user.client.Element) ell, cancelListener);
        }
    }
    
    public void hideErrorMessage(){
        if(errorMessageShowing) {
        getWindow().remove(errorMessageHolder) ;
            errorMessageShowing=false;
        }
    }

    public void showErrorMessage(ComponentMessageType messageType,String errorTitle, String errorText) {
            errorMessage.getElement().setId("errorMessage");
            errorMessage.setHeight("100%");
            errorMessage.populate(messageType, errorTitle, errorText);

            errorMessageHolder.setHeight("100%");
            errorMessageHolder.add(errorMessage);

            getWindow().add(errorMessageHolder);
            getWidget().layout(true);
            errorMessageShowing=true;
    }

    /*For chart windows - odd gxt window behaviou sometimes loses its height*/
    public void setErrorMessageHeight(String height){
        errorMessage.setHeight(height);
        errorMessageHolder.setHeight(height);
    }

    /**
     * user clicks the cancel button on the masked widget
     * (we used hide the window here - now we send a cancel request
     */
    private class CancelButtonListener implements EventListener {

        @Override
        public void onBrowserEvent(final Event event) {
            presenter.cleanUpWindowForCancelRequest();
            presenter.sendCancelRequestCall();
        }

    }
}
