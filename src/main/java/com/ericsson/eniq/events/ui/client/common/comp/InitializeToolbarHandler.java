/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.common.client.json.IJSONArray;
import com.ericsson.eniq.events.common.client.json.IJSONObject;
import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.common.client.service.CommonParamUtil;
import com.ericsson.eniq.events.common.client.service.RestfulRequestBuilder;
import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.listeners.ToolBarItemListener;
import com.ericsson.eniq.events.ui.client.common.listeners.ToolBarMenuItemListener;
import com.ericsson.eniq.events.ui.client.common.widget.AbstractWizardOverLay;
import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.ericsson.eniq.events.ui.client.common.widget.WizardOverLayDynamic;
import com.ericsson.eniq.events.ui.client.common.widget.WizardOverLayFixedResultSet;
import com.ericsson.eniq.events.ui.client.datatype.*;
import com.ericsson.eniq.events.ui.client.datatype.ToolbarPanelInfoDataType.EventType;
import com.ericsson.eniq.events.ui.client.events.RefreshWindowEventHandler;
import com.ericsson.eniq.events.ui.client.events.TimeParameterValueChangeEventHandler;
import com.ericsson.eniq.events.ui.client.gin.EniqEventsUIGinjector;
import com.ericsson.eniq.events.ui.client.main.AbstractWindowLauncher;
import com.ericsson.eniq.events.ui.client.main.GridLauncher;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay;
import com.ericsson.eniq.events.ui.client.resources.ToolbarIconResourceHelper;
import com.ericsson.eniq.events.ui.shared.enums.LicenceGroupType;
import com.ericsson.eniq.events.widgets.client.button.ImageButton;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessagePanel;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessageType;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.ericsson.eniq.events.widgets.client.overlay.AWizardOverlayPanel;
import com.ericsson.eniq.events.widgets.client.overlay.WizardOverlay;
import com.ericsson.eniq.events.widgets.client.overlay.WizardOverlayType;
import com.ericsson.eniq.events.widgets.client.overlay.caching.IJSONObjectCacheHandler;
import com.ericsson.eniq.events.widgets.client.overlay.events.WizardLaunchEvent;
import com.ericsson.eniq.events.widgets.client.overlay.events.WizardLaunchEventHandler;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.dom.client.Style;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestTimeoutException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.ImageResource;
import com.google.web.bindery.event.shared.EventBus;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;
import static com.ericsson.eniq.events.widgets.client.overlay.WizardOverlayType.CHART;
import static com.ericsson.eniq.events.widgets.client.overlay.WizardOverlayType.GRID;

/**
 * Class to take some of the work for BaseWindow Presenter to initialise toolbars for floating windows (charts and
 * grids)
 *
 * @author eeicmsy  (moving code from EvnetGridPresenter)
 * @author eendmcm
 * @since April 2010
 */
public class InitializeToolbarHandler<D extends IExtendedWidgetDisplay> {

    private final EniqEventsUIGinjector injector = MainEntryPoint.getInjector();

    private final IMetaReader metaReader = injector.getMetaReader();

    private final D display;

    private final BaseWindowPresenter<? extends WidgetDisplay> baseWinPresenter;

    private final EventBus eventBus;

    /* variable so won't add twice ever */
    private Component wizObj;

    private Component messageDisplayHolder = null;
    private ComponentMessagePanel componentMessage = null;
    
    private ToolbarIconResourceHelper iconResourceHelper;

    private boolean hasWizard = false;

    /**
     * Class with functionality to setup toolbars for windows (grids and charts)
     *
     * @param display          - window view (MVP) pattern for grid or chart
     * @param eventBus         - default event bus singleton
     * @param baseWinPresenter - Main base window presenter (MVP pattern)
     */
    public InitializeToolbarHandler(final D display, final EventBus eventBus,
            final BaseWindowPresenter<? extends WidgetDisplay> baseWinPresenter) {

        this.display = display;
        this.baseWinPresenter = baseWinPresenter;
        this.eventBus = eventBus;
    }

    /**
     * Because Dynamic and Fixed Wizard types have different behavior it is not enough always to check if wizard id is
     * present
     *
     * @return true if a dynamic wizard exists
     */
    public boolean containsDynamicWizard() {
        // In case wizObj is WidgetComponent, we know that it is new implementation of wizard overlay wizard, since GXT
        // Toolbar only accepts GXT Component
        return ((wizObj != null && wizObj instanceof WizardOverLayDynamic<?>))
                || ((wizObj != null && wizObj instanceof WidgetComponent));
    }

    /**
     * Initialises UPPER ToolBar for floating window based on BaseWindowPresenter information for toolbar type
     *
     * @param displayingTime true if displaying the time component on the window toolbar (which you will be in near all
     *                       cases except for example cause code tables)
     */
    public void initializeToolbar(final boolean displayingTime) {

        iconResourceHelper = new ToolbarIconResourceHelper();

        /* remove any exiting buttons */
        display.getWindowToolbar().removeAllToolBarItems();

        Component btn;
        final MetaMenuItem metaMenuItem = baseWinPresenter.getMetaMenuItem();
        final String toolBarType = (metaMenuItem != null) ? metaMenuItem.getCurrentToolBarType() : EMPTY_STRING;
        /* get meta data that defines the Toolbar of this component */
        if (toolBarType.length() != 0) {
            final ToolBarInfoDataType arrToolBars = metaReader.getToolBarItems(toolBarType);

            for (int x = 0; x < arrToolBars.toolBarPanels.size(); x++) {
                final ToolbarPanelInfoDataType tb = arrToolBars.toolBarPanels.get(x);

                btn = getButtonFromToolbarPanelInfo(tb);
                display.getWindowToolbar().addToolbarItem(btn);

                if (tb.hasSeperator) {
                    /* Add a separator after the button */
                    display.getWindowToolbar().addToolbarSeperator();
                }
            }

            initToolBarNavigation(((IBaseWindowView) display).getBaseWindowTitle());
        }

        //Has this Window got a Wizard, If so lets display
        setupWizardOverLayIfRequired(metaMenuItem);
        setupMessagePanel();

        if (displayingTime) { // not for cause code tables
            /* do not assume will always create new window (or toggle) with a combobox for
             * time, could be a label (e.g. drilling on a chart to create grid),
             * this method will initialise comboboxes but display label when required
             * (assumes equivalent of display.getWindowToolbar().initTimeRangeComboBox() 
             * will be called internally)
             */
            display.updateTime(baseWinPresenter.getTimeData()); // i.e. label or combobox
        }

    }

    /**
     * Setup the panel to show error|warning|Info messages.
     */
    private void setupMessagePanel() {
        if(messageDisplayHolder == null){
            componentMessage = new ComponentMessagePanel();
            componentMessage.setHeight("200px");
            messageDisplayHolder = new WidgetComponent(componentMessage);
            messageDisplayHolder.setVisible(false);

            display.getWindowToolbar().addOverLay(messageDisplayHolder);
        }
    }


    /*
     * Define and add wizard overlay (once) to toolbar if required 
     * @param metaMenuItem   
     */
    private void setupWizardOverLayIfRequired(final MetaMenuItem metaMenuItem) {
        if (wizObj == null && metaMenuItem != null) {

            // this is dangerous - assumes metaMenuItem state maintained (i.e. cleared, 
            // even after drilldowns
            final String wizardId = metaMenuItem.getWizardId();

            // not trusting either of these on own but "trust" both
            final boolean isInDrill = baseWinPresenter.isDrilledDownScreen() || baseWinPresenter.drillDepth != 0;

            if (!wizardId.isEmpty() && !isInDrill) {

                final WizardInfoDataType wizardInfo = createWizardInfoDataType(wizardId);
                // use the load URL being empty as criteria to know if launch 
                // fixed or one that has to load 
                final boolean needsServerCallToPopulate = !wizardInfo.getLoadURL().isEmpty();

                if (needsServerCallToPopulate) {
                    if ("WCDMA_HFA_CC_ANALYSIS_PIE".equals(wizardId)) {
                        // We need new implementation for WCDMA wizard overlay
                        LinkedHashSet<WizardOverlayType> radioButtonItems = new LinkedHashSet<WizardOverlayType>();
                        radioButtonItems.add(CHART);
                        radioButtonItems.add(GRID);
                        wizObj = new WidgetComponent(new WizardOverlay<IJSONObject>(
                                JsonObjectWizardItemTranslator.getInstance(), "Configure", radioButtonItems));
                    } else {
                        wizObj = new WizardOverLayDynamic<D>(baseWinPresenter.getMetaMenuItem(),
                                display.getWorkspaceController(), wizardInfo, baseWinPresenter, this.eventBus, display);
                    }
                } else {
                    wizObj = new WizardOverLayFixedResultSet<D>(baseWinPresenter.getMetaMenuItem(),
                            display.getWorkspaceController(), wizardInfo, baseWinPresenter, this.eventBus, display);
                }

                display.getWindowToolbar().addOverLay(wizObj);

                if (wizObj instanceof AbstractWizardOverLay) {
                    final AbstractWizardOverLay overlay = (AbstractWizardOverLay) wizObj;

                    /* Removed coupling from BaseToolbar onResize() call
                    *
                    * Required to notify the wizard that the container toolbar is being resized. The hidden GXT containers will have
                    * their height reset back to auto and this will prevent the overlay from actually overlaying the underlying chart
                    */
                    display.getWindowToolbar().addListener(Events.Resize, new Listener<BaseEvent>() {
                        @Override
                        public void handleEvent(final BaseEvent be) {
                            overlay.parentToolbarResize();
                        }
                    });

                    // Removed coupling from BaseToolbar cleanUpOnClose() for cleanUpOnClose call of overlay.
                    display.getParentWindow().getWidget().addListener(Events.Hide, new Listener<BaseEvent>() {
                        @Override
                        public void handleEvent(final BaseEvent be) {
                            overlay.cleanUpOnClose();
                        }
                    });

                    overlay.loadWizardData();
                } else if (wizObj instanceof WidgetComponent) {
                    // New approach using WizardOverlay
                    final WizardOverlay<IJSONObject> overlay = (WizardOverlay<IJSONObject>) ((WidgetComponent) wizObj)
                            .getWidget();

                    // Set Grid type by default currently
                    overlay.setType(GRID);

                    Collection<AWizardOverlayPanel<IJSONObject>> overlayPanels = overlay.getOverlayPanels();
                    if (!overlayPanels.isEmpty()) {
                        // Set the overlay groups, if there are any
                        LicenceGroupType[] licenceGroupTypes = LicenceGroupType.values();
                        for (AWizardOverlayPanel<IJSONObject> overlayPanel : overlayPanels) {
                            // As caching needs to compare objects and JSONObject comparison glitches (having the same
                            // representation, but different instances, gives you false when compared by equals)
                            overlayPanel.setSelectedItemsCacheHandler(IJSONObjectCacheHandler.getInstance());

                            if (licenceGroupTypes != null && licenceGroupTypes.length > 0) {
                                overlayPanel.setGroups(LicenceGroupTranslator.getInstance(), licenceGroupTypes);
                            }
                        }
                    }

                    final WizardOverlayDataHandler dataHandler = new WizardOverlayDataHandler(overlay, wizardInfo);
                    dataHandler.loadData();

                    baseWinPresenter.replaceTimeAndRefreshHandlers(dataHandler, dataHandler);

                    dataHandler.setWizardFixed();
                }

                setHasWizard(true);
            }
        }
    }




    WizardInfoDataType createWizardInfoDataType(final String wizardId) {
        return metaReader.getWizardMetaMenuItemDataType(wizardId);
    }

    private ImageButton createImageButton(final ToolbarPanelInfoDataType tb) {
        final ImageResource[] imageButtonResources = iconResourceHelper.getIcon(tb.id);

        final ImageButton imageButton = new ImageButton(imageButtonResources[0]);
        imageButton.setDisabledImage(imageButtonResources[1]);
        imageButton.setHoverImage(imageButtonResources[2]);

        imageButton.setTitle(tb.toolTip);
        imageButton.setEnabled(tb.isEnabled);
        imageButton.setVisible(tb.isVisible);
        imageButton.getElement().setId(tb.id);

        imageButton.getElement().getStyle().setMarginLeft(10, Style.Unit.PX);
        imageButton.getElement().getStyle().setMarginRight(5, Style.Unit.PX);
        imageButton.getElement().getStyle().setMarginTop(6, Style.Unit.PX);

        /*straight copy for now from other method*/
        // (default is buttons are visible) -  overriding visibility of button if search type not as specified when applicable
        final String searchType = (baseWinPresenter.searchData == null) ? null : baseWinPresenter.searchData.getType();
        if (tb.visibleWhen.length() > 0 && searchType != null && (!searchType.equals(tb.visibleWhen))) {
            imageButton.setVisible(false);
        }

        imageButton.addClickHandler(new ToolBarItemListener(eventBus, display, tb.eventID));

        return imageButton;
    }

    private Button createButton(final ToolbarPanelInfoDataType tb) {
        Button btn;
        /* check if this is a Toggle Button - if so btn is new instance of ToggleButton*/
        if (tb.isToggle) {
            btn = new ToggleButton(tb.name, IconHelper.createStyle(tb.style)); // NOPMD by eeicmsy on 13/04/10 20:06
        } else {
            btn = new Button(tb.name, IconHelper.createStyle(tb.style)); // NOPMD by eeicmsy on 13/04/10 20:06
        }

        btn.setToolTip(tb.toolTip);
        btn.setEnabled(tb.isEnabled);
        btn.setVisible(tb.isVisible);
        btn.setId(tb.id);

        // (default is buttons are visible) -  overriding visibility of button if search type not as specified when applicable
        final String searchType = (baseWinPresenter.searchData == null) ? null : baseWinPresenter.searchData.getType();
        if (tb.visibleWhen.length() > 0 && searchType != null && (!searchType.equals(tb.visibleWhen))) {
            btn.setVisible(false);
        }

        /* check if this a splitter button */
        if (tb.subItems.size() > 0) {
            /* Add the menu to the button */
            btn.setMenu(getToolBarMenu(tb));
        } else {

            /* add a generic action listener to the ToolBarItem */
            btn.addSelectionListener(new ToolBarItemListener(eventBus, display, tb.eventID)); // NOPMD by eeicmsy on 13/04/10 20:08
        }
        return btn;
    }

    /*
    * Set up buttons on the toolbar - which may also contain there own menu items
    * @return toolbar button
    */
    private Component getButtonFromToolbarPanelInfo(final ToolbarPanelInfoDataType tb) {
        if (!tb.isImageButton) {
            return createButton(tb);
        } else {
            final ImageButton imageButton = createImageButton(tb);
            final WidgetComponent widgetComponent = new WidgetComponent(imageButton) {
                // TODO: Remove this once ImageButtons have taken over the world

                @Override
                public void setEnabled(final boolean enabled) {
                    imageButton.setEnabled(enabled);
                }
            };

            widgetComponent.setItemId(imageButton.getElement().getId());

            return widgetComponent;
        }
    }

    private Menu getToolBarMenu(final ToolbarPanelInfoDataType tb) {
        final Menu menu = new Menu();

        /*iterate subItems build up menu */
        for (final ToolbarPanelInfoDataType item : tb.subItems) {
            final MenuItem menuItem = new MenuItem(item.name); // NOPMD by eeicmsy on 14/07/10 16:23
            menuItem.setId(item.id);
            if (item.style.length() > 0) {
                menuItem.addStyleName(item.style); // note as its a menu item ensure "no-repeat" is set in CSS
            }
            /* add a generic action listener to the Menu Item (unless want it disabled permanently
             * based on search type (which can be null) */
            final String searchType = (baseWinPresenter.searchData == null) ? null : baseWinPresenter.searchData
                    .getType();

            if (searchType != null && (!(searchType.equals(EMPTY_STRING))) && searchType.equals(item.disableWhen)) {
                menuItem.disable();
            } else {
                menuItem.addSelectionListener(new ToolBarMenuItemListener(eventBus, display, item.eventID, item.urlInfo)); // NOPMD by eeicmsy on 14/07/10 16:23
            }

            menu.add(menuItem);
        }
        return menu;
    }

    /**
     * Sets up the navigation toolbar on initial display of a window
     *
     * @param title title to add to bread crumb
     */
    public void initToolBarNavigation(final String title) {

        final BaseToolBar winToolBar = display.getWindowToolbar();

        final Component btnBack = winToolBar.getItemByItemId(BTN_BACK);
        final Component btnForward = winToolBar.getItemByItemId(BTN_FORWARD);
        final Button btnBreadCrumb = winToolBar.getButtonByItemId(BTN_NAV);

        if (btnBack != null) {
            btnBack.setEnabled(false);
        }

        if (btnForward != null) {
            btnForward.setEnabled(false);
        }

        if (btnBreadCrumb != null) {
            /* breadcrumb will always be enabled when the initial window is launched*/
            btnBreadCrumb.setEnabled(true);

            final Menu menu = btnBreadCrumb.getMenu();
            /* check for the placeholder menuItem and remove it*/
            final Component placeHolderItem = menu.getItemByItemId(BTN_NAV_PLACEHOLDER);
            if (placeHolderItem != null) {
                menu.remove(placeHolderItem);
            }

            /* CAUSE CODE - SUSAN 
             * Pass fixedWinID into BreadCrumbMenuItem() */
            /* add new item that represents a bread crumb to the Menu */

            final BreadCrumbMenuItem menuItem = new BreadCrumbMenuItem(title, menu.getItemCount(),
                    baseWinPresenter.getFixedQueryId(), null);
            //final ToolBarMenuItemListener menuItemListen = new ToolBarMenuItemListener();
            menuItem.addSelectionListener(new ToolBarMenuItemListener(eventBus, display, EventType.NAVIGATION));
            menuItem.setURL(baseWinPresenter.getWsURL());
            menuItem.setId(baseWinPresenter.getFixedQueryId() + UNDERSCORE
                    + String.valueOf(baseWinPresenter.drillDepth));

            menu.add(menuItem);

        }

    }

    /**
     * synchronise the back / forward and splitter navigation based on the user drilldown options
     *
     * @param title              - title to add to bread crumb (drilldown  Info name)
     * @param widgetSpecificInfo - Outbound server call information was was used to populate  window following a
     *                           drilldown hyperlink click. This is like information stored in widgetSpecificParams
     *                           (except widgetSpecificParams will be wiped on success server calls - which is where
     *                           would want this information for enabling toolbar buttons) e.g. can use to fetch data
     *                           such as <li>&key=SUM&type=CELL&cell=CELL146889&vendor=ERICSSON&bsc=BSC735&RAT=1</li>
     *                           <li>&key=ERR&type=BSC&groupname=Another_Group_HIER3&eventID=1</li> that was used when
     *                           window data was being first fetched
     */
    public void updateToolBarNavigation(final String title, final IHyperLinkDataType widgetSpecificInfo) {
        final BaseToolBar winToolBar = display.getWindowToolbar();

        final Component btnBack = winToolBar.getItemByItemId(BTN_BACK);
        final Button btnBreadCrumb = winToolBar.getButtonByItemId(BTN_NAV);

        if (btnBreadCrumb != null && btnBack != null) {
            btnBack.setEnabled(true);
        }

        if (btnBreadCrumb != null) {
            /* back and breadcrumb will always be enabled after a drilldown is clicked
             * (in current code mechanism back button functionality wil not work without breadcrumb
             * BTN_NAV must exist (so make invisible if don't want it displayed)*/

            btnBreadCrumb.setEnabled(true);

            /* add new item that represents a bread crumb to the Menu */
            final Menu menu = btnBreadCrumb.getMenu();
            if (menu.getItemByItemId(baseWinPresenter.getFixedQueryId() + "_"
                    + String.valueOf(baseWinPresenter.drillDepth)) == null) {
                /* CAUSE CODE - SUSAN 
                 * Pass fixedWinID into BreadCrumbMenuItem() */
                final BreadCrumbMenuItem menuItem = new BreadCrumbMenuItem(title, menu.getItemCount(),
                        baseWinPresenter.getFixedQueryId(), widgetSpecificInfo);
                menuItem.addSelectionListener(new ToolBarMenuItemListener(eventBus, display, EventType.NAVIGATION));

                menuItem.setURL(baseWinPresenter.getWsURL());
                menuItem.setId(baseWinPresenter.getFixedQueryId() + "_" + String.valueOf(baseWinPresenter.drillDepth));

                final BreadCrumbMenuItem itemCurrentlyDisplayed = (BreadCrumbMenuItem) getItemCurrentlyDisplayed(menu);
                if (itemCurrentlyDisplayed != null) {
                    if (menu.getItemCount() > itemCurrentlyDisplayed.getIndex() + 1) {
                        for (int i = itemCurrentlyDisplayed.getIndex() + 1; i < menu.getItemCount(); i++) {
                            menu.remove(menu.getItem(i));
                        }
                    }
                }
                menu.add(menuItem);
            }
        }
    }

    /*
     * @param menu - breadcrumb navigation menu
     * returns the BreadCrumbMenuItem represents the grid that is currently displayed.
     */
    private Component getItemCurrentlyDisplayed(final Menu menu) {
        Component item = null;
        for (int i = 0; i < menu.getItemCount(); i++) {
            if (((BreadCrumbMenuItem) menu.getItem(i)).isGridDisplayed()) {
                item = menu.getItem(i);
                break;
            }
        }
        return item;
    }

    public boolean hasWizard() {
        return this.hasWizard;
    }

    public void setHasWizard(boolean hasWizard) {
        this.hasWizard = hasWizard;
    }

    private class WizardOverlayDataHandler implements RefreshWindowEventHandler, TimeParameterValueChangeEventHandler {

        private final WizardOverlay<IJSONObject> overlay;

        private final WizardInfoDataType wizardInfo;

        private boolean isFirstLaunch = true;

        private WizardOverlayDataHandler(final WizardOverlay<IJSONObject> overlay, final WizardInfoDataType wizardInfo) {
            this.overlay = overlay;
            this.wizardInfo = wizardInfo;

            overlay.addLaunchEventHandler(new WizardLaunchEventHandler<IJSONObject>() {
                @Override
                public void onLaunch(final WizardLaunchEvent<IJSONObject> event) {
                    if (event.getWizardOverlayType().equals(CHART)) {
                        launchChart(event.getSelectedItems());
                    } else {
                        launchGrid();
                    }
                }
            });
        }

        /* Copied from WizardOverLayDynamic.java */
        @SuppressWarnings("deprecation")
        public void loadData() {
            // Get the url to call, make servercomms calls
            resetToStarterState();
            final String sParameters = prepareUrlParameters();
            
            //show the loading mask.
            display.startProcessing();
            if (sParameters == null) {
                return;
            }

            // LOAD URL is to fetch checkboxes
            baseWinPresenter.getServerComm().requestData(RestfulRequestBuilder.State.GET, wizardInfo.getLoadURL(),
                    sParameters, new RequestCallback() {
                        @Override
                        public void onResponseReceived(final Request request, final Response response) {
                            // Hide loading mask
                            display.stopProcessing();

                            // Clear items, if there are any on the wizard panel
                            overlay.getCurrentOverlayPanel().clear();
                            // Update overlay panel with received items

                            if (STATUS_CODE_OK == response.getStatusCode()) {

                                final JSONValue responseValue = checkAndParse(response.getText());

                                boolean hasItemsToShow = false;
                                if (responseValue != null && JSONUtils.checkData(responseValue)) {

                                    final JsonObjectWrapper metadata = new JsonObjectWrapper(responseValue.isObject());
                                    final IJSONArray arrCause = metadata.getArray(JSON_ROOTNODE);

                                    final IJSONObject[] items = new IJSONObject[arrCause.size()];
                                    for (int i = 0; i < arrCause.size(); i++) {
                                        final IJSONObject item = arrCause.get(i);
                                        items[i] = item;

                                        hasItemsToShow = true;
                                    }

                                    final AWizardOverlayPanel<IJSONObject> overlayPanel = overlay
                                            .getCurrentOverlayPanel();
                                    overlayPanel.update(items);

                                    final List<IJSONObject> selected = overlayPanel.getSelected();

                                    if (!isFirstLaunch) {
                                        if (!selected.isEmpty() && CHART.equals(overlay.getType())) {
                                            // Reload chart if visible already
                                            overlay.fireEvent(new WizardLaunchEvent<IJSONObject>(CHART, selected));
                                        } else {
                                            overlay.setCollapsed(false);
                                        }
                                    }
                                }

                                if (!hasItemsToShow) {
                                    showMessage(WARNING,
                                            NO_CAUSE_CODES_FOUND_WIZARD_MESSAGE,
                                            ComponentMessageType.WARN);
                                }
                            }
                        }

                        @Override
                        public void onError(final Request request, final Throwable t) {
                            if (t instanceof RequestTimeoutException) {
                                // handle a request timeout
                                final MessageDialog errorDialog = new MessageDialog();
                                errorDialog.show(TIMEOUT_EXCEPTION, t.getMessage(),
                                        MessageDialog.DialogType.ERROR);
                            } else {
                                MessageDialog.get().show(EMPTY_STRING, t.getMessage(), MessageDialog.DialogType.ERROR);
                            }

                            display.updateWidgetSpecificURLParams(EMPTY_STRING);
                            display.stopProcessing();
                        }
                    });
        }

        /**
         * Show the message panel.
         * @param title
         * @param message
         * @param messageType
         */
        private void showMessage(final String title, final String message, ComponentMessageType messageType){
            componentMessage.populate(messageType, title, message);
            //show the message panel
            messageDisplayHolder.setVisible(true);
            //hide the wizard overlay
            wizObj.setVisible(false);

            final El elBody = display.getWindowBody();
            if(elBody.isVisible()){
                display.getWindowBody().setHeight(0);
            }
            //set the height of the messagePanels container.
            final Style parentElementStyle = overlay.getElement().getParentElement().getStyle();
            parentElementStyle.setHeight(200, Style.Unit.PX);
            final BaseWindow b = ((BaseWindow) baseWinPresenter.getView());
            b.getWidget().setResizable(false);
        }

        /**
         * Hide the message panel.
         */
        private void hideMessage(){
            if(messageDisplayHolder != null){
                //if the error message is showing on the window.
                if(messageDisplayHolder.isVisible()){
                    //show the wizard
                    wizObj.setVisible(true);
                    //hide the message panel
                    messageDisplayHolder.setVisible(false);
                    //set the wizard expanded.
                    overlay.setCollapsed(false);
                    //make sure the window is the right height to show the expanded overlay.
                    overlay.setHeight("200px");
                    final BaseWindow b = ((BaseWindow) baseWinPresenter.getView());
                    b.getWidget().setResizable(true);
                }
            }
        }

        private void resetToStarterState() {
            hideMessage();
        }


        private String prepareUrlParameters() {
            final SearchFieldDataType searchData = baseWinPresenter.getSearchData();

            final StringBuilder sParameters = new StringBuilder();

            if (isSearchDataRequiredMissing()) {
                return null;
            }

            final boolean isAddingSearch = isSearchFieldUser(); // not added isSearchUser to wizard
            if (isAddingSearch) {
                // launch button press will have checked for search field info
                if ((searchData != null) && (!searchData.isEmpty())) {
                    sParameters.append(searchData.getSearchFieldURLParams(true));
                }
            }

            final TimeInfoDataType timeData = baseWinPresenter.getTimeData();
            // final append time parameters (harmless if not used)
            sParameters.append((timeData == null ? EMPTY_STRING : timeData.getQueryString(!isAddingSearch)));

            // append timeZone info
            sParameters.append(CommonParamUtil.getTimeZoneURLParameter());

            /** Append display parameter. At this point we have at least a TimeZone param so use the & delimiter **/
            sParameters.append(CommonParamUtil.REGULAR_URL_PARAM_DELIMITOR);
            sParameters.append(DISPLAY_TYPE_PARAM);
            sParameters.append(baseWinPresenter.getOutBoundDisplayTypeParameter());
            return sParameters.toString();
        }

        /*
         * Copied from WizardOverLayDynamic.java
         * parses a string into a JSONValue
         */
        private JSONValue checkAndParse(final String text) {
            final JSONValue responseValue = null;
            try {
                if (text != null && text.length() > 0) {
                    return JSONUtils.parse(text);
                }
            } catch (final JSONException e) {
                // server should not have passed success,
                // e.g. now trying to parse an error message (500 error)
                displayException(e, text);
            }

            return responseValue;
        }

        /* Copied from WizardOverLayDynamic.java */
        private void displayException(final Throwable exception, final String text) {

            final MessageDialog errorDialog = new MessageDialog();
            errorDialog.setGlassEnabled(true);

            if (exception instanceof RequestTimeoutException) {
                errorDialog.show(TIMEOUT_EXCEPTION, exception.getMessage(), MessageDialog.DialogType.ERROR);
            } else {
                /* text can be pure html (from glassfish response) */
                errorDialog.show(CHECK_GLASSFISH_LOG_MESSAGE + text, exception.getMessage(),
                        MessageDialog.DialogType.ERROR);
            }
        }

        /**
         * Copied from AbstractWizardOverLay.java
         * <p/>
         * Note reading from meta (not requiring adding search info into Wizard section (or WizardInfoDataType)
         *
         * @return true if new search field information to make server calls
         */
        private boolean isSearchFieldUser() {
            return baseWinPresenter.getMetaMenuItem().isSearchFieldUser();
        }

        /**
         * Copied from AbstractWizardOverLay.java
         * <p/>
         * Display warning for no search field if required
         *
         * @return true if search data is required and it is missing (i.e. indicating not to proceed with the call)
         */
        private boolean isSearchDataRequiredMissing() {

            if (!isSearchFieldUser()) {
                return false;
            }

            final SearchFieldDataType searchData = baseWinPresenter.getSearchData();

            if (searchData == null || searchData.isEmpty()) {

                MessageDialog.get().show(MISSING_INPUT_DATA, NEED_SEARCH_FIELD_MESSAGE,
                        MessageDialog.DialogType.WARNING);
                return true;
            }
            return false;
        }

        /* Copied from AbstractWizardOverLay.java */
        private void launchGrid() {
            closeLaunchedWindow();

            isFirstLaunch = false;

            baseWinPresenter.getMetaMenuItem().setWizard(EMPTY_STRING);
            baseWinPresenter.getMetaMenuItem().clearWizardInfoToGrid();

            // so make the grid the "toggleToolBarType" in meta data ALWAYS
            baseWinPresenter.getMetaMenuItem().toggleBottomToolBarType();
            baseWinPresenter.getMetaMenuItem().toggleToolBarType();

            AbstractWindowLauncher launcher = new GridLauncher(baseWinPresenter.getMetaMenuItem(), eventBus, display
                        .getWorkspaceController().getCenterPanel(), display.getWorkspaceController());
            /* pass "true" to keep meta data state (not requiring a fresh meta menu item in base window Presenter)
            * (and search data from parent - i.e. not search component as independent (could even be in ranking tab)
            */
            launcher.launchWindow(baseWinPresenter.getTimeData(), baseWinPresenter.getSearchData(), true,
                    baseWinPresenter.getView().getWindowState());
            // TODO: consider to move toggling to windowState (see class WindowState)
        }

        /* Copied from AbstractWizardOverLay.java */
        private void launchChart(final List<IJSONObject> selectedItems) {
            /* default display is chart type */
            baseWinPresenter.getMetaMenuItem().setWizard(EMPTY_STRING);
            isFirstLaunch = false;

            setWizardFixed();

            // Prepare selected items
            final String params = prepareSelectedItemsString(selectedItems);

            final String wsURL = wizardInfo.getWSURL() + prepareUrlParameters();
            baseWinPresenter.getServerComm().makeServerPost(baseWinPresenter.getMultipleInstanceWinId(), wsURL, params,
                    RestfulRequestBuilder.ContentType.JSON);
        }

        /* Copied from AbstractWizardOverLay.java */
        public void closeLaunchedWindow() {
            //Close the Original Window (without getting into Id issues)
            final BaseWindow b = ((BaseWindow) baseWinPresenter.getView());
            b.hide();

            isFirstLaunch = false;

            setWizardFixed();
        }

        private String prepareSelectedItemsString(final List<IJSONObject> selectedItems) {
            final StringBuilder sb = new StringBuilder("{\"selected\":[");
            boolean firstItem = true;
            for (final IJSONObject data : selectedItems) {

                if (!firstItem) {
                    sb.append(",");
                } else {
                    firstItem = false;
                }

                sb.append(data.getNativeObject().toString());
            }

            sb.append("]}");

            return sb.toString();
        }

        /* Copied from AbstractWizardOverLay.java */
        private void setWizardFixed() {

            final BaseWindow b = ((BaseWindow) baseWinPresenter.getView());

            if (b != null) {
                b.getWidget().setResizable(!isFirstLaunch);
                /*b.getWidget().setMaximizable(!firstLaunch); */
                final El elBody = display.getWindowBody();
                b.getWidget().setShadow(false);
                elBody.setDisplayed((isFirstLaunch ? "none" : "block"));

                if (!isFirstLaunch) {
                    /** Make the minimum height of the Chart window at least the default window height (if the wizard overlay height is very
                     * small the window height for the chart would be very small, so this ensures that it will be at least the default.
                     */
                    if (b.getWidget().getHeight() < b.getWidget().getDefaultLaunchHeight()) {
                        b.getWidget().setHeight(b.getWidget().getDefaultLaunchHeight());
                    }

                    final Style parentElementStyle = overlay.getElement().getParentElement().getStyle();

                    parentElementStyle.setHeight(19, Style.Unit.PX);
                    parentElementStyle.setOverflow(Style.Overflow.VISIBLE);

                    overlay.getElement().getParentElement().getParentElement().getStyle()
                            .setOverflow(Style.Overflow.VISIBLE);
                    display.getWindowToolbar().getElement().getParentElement().getStyle()
                            .setOverflow(Style.Overflow.VISIBLE);
                }

                b.getWidget().layout(true);
            }
        }

        @Override
        public void handleWindowRefresh(final MultipleInstanceWinId multiWinID) {
            if (!baseWinPresenter.getMultipleInstanceWinId().equals(multiWinID)) {
                return;
            }
            handleWindowRefresh();
        }

        @Override
        public void handleWindowRefresh() {
            loadData();
        }

        @Override
        public void handleTimeParamUpdate(final MultipleInstanceWinId multiWinId, final TimeInfoDataType time) {
            // Have to check search field data here when in multi-mode
            if (!baseWinPresenter.getMultipleInstanceWinId().equals(multiWinId)) {
                return;
            }
            handleTimeParamUpdate(time);
        }

        @Override
        public void handleTimeParamUpdate(final TimeInfoDataType time) {
            /*
             * Reset the wizard listening Flag
             * and invoke a request for updated wizard content
             */
            baseWinPresenter.setTimeData(time);

            loadData();
        }

    }
}
