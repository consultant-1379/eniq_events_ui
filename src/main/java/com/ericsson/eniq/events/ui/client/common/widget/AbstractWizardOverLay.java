/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget;

import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWinServerComms;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldDataType;
import com.ericsson.eniq.events.ui.client.datatype.WizardInfoDataType;
import com.ericsson.eniq.events.ui.client.events.RefreshWindowEventHandler;
import com.ericsson.eniq.events.ui.client.events.TimeParameterValueChangeEventHandler;
import com.ericsson.eniq.events.ui.client.main.*;
import com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay;
import com.ericsson.eniq.events.ui.client.search.ISubmitSearchHandler;
import com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController;
import com.ericsson.eniq.events.widgets.client.checkable.AllCheckbox;
import com.ericsson.eniq.events.widgets.client.checkable.event.ChildSelectEvent;
import com.ericsson.eniq.events.widgets.client.checkable.event.ChildSelectEventHandler;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessagePanel;
import com.ericsson.eniq.events.widgets.client.component.ComponentMessageType;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Header;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.ericsson.eniq.events.highcharts.client.ChartConstants.WIZARD_OVERLAY_COLLAPSED_HEIGHT;
import static com.ericsson.eniq.events.ui.client.common.Constants.*;
import static com.extjs.gxt.ui.client.Style.Scroll.AUTO;

/**
 * Overlay that sits at bottom WindowToolbar
 * Gets displayed if wizardURL property from metadata
 * is populated.
 * Note: This will be split into a generic wizard once
 * more overlays are required within screens
 *
 * @author eendmcm
 * @author eeicmsy
 * @since Aug 2011
 */
public abstract class AbstractWizardOverLay<D extends IExtendedWidgetDisplay> extends LayoutContainer implements
        ISubmitSearchHandler {

    private ContentPanel parentConfigurePanel;
    private ContentPanel messagePanel;
    private ComponentMessagePanel messageDisplay;

    protected ContentPanel cpResponseHolder;

    /* want chart, grid and select all to co-exist not a span within div */
    private ContentPanel chAllHolder;

    private SimplePanel cpButtons;

    /* grid and chart radio button options */
    protected final WizardRadioGroup cpWizardRadioGroup = new WizardRadioGroup();

    private final MetaMenuItem meta;

    protected final IWorkspaceController workspaceController;

    protected final BaseWindowPresenter<? extends WidgetDisplay> baseWinPresenter;

    protected final EventBus eventBus;

    /* All checkboxes to appear in the wizard */
    protected final List<CheckBox> checkboxes = new ArrayList<CheckBox>();

    protected final WizardInfoDataType wizardInfo;

    protected AllCheckbox chkAll;

    /* First launch state is when showing the wizard overlay, 
     * without the chart or grid */
    private boolean isFirstLaunch = true;

    protected final Button btnLaunch;

    protected final D display;

    private final Listener<BaseEvent> gridRadioButtonListener = getGridRadioButtonListener();

    /**
     * Constructor for the Overlay
     *
     * @param metaMenu      - metaMenu for the chosen Menu Option
     * @param workspaceController          - workspaceController for tab
     * @param wizardInfo    DataType to retain the data relating to a Wizard Overlay
     * @param basePresenter - BaseWindowPresenter for the Container Window
     * @param bus           - Instance of eventBus that is used throughout the app
     * @param display       - view reference
     */
    public AbstractWizardOverLay(final MetaMenuItem metaMenu, final IWorkspaceController workspaceController,
            final WizardInfoDataType wizardInfo, final BaseWindowPresenter<? extends WidgetDisplay> basePresenter,
            final EventBus bus, final D display) {

        super();
        if(metaMenu.getID().equalsIgnoreCase(NETWORK_CAUSE_CODE_ANALYSIS))
            chkAll= new AllCheckbox(true);
        else
            chkAll= new AllCheckbox();
        
        setLayout(new FlowLayout());
        setScrollMode(AUTO);

        this.display = display;
        this.meta = metaMenu;
        this.workspaceController = workspaceController;
        this.baseWinPresenter = basePresenter;
        this.eventBus = bus;

        this.btnLaunch = new Button("Launch");
        this.wizardInfo = wizardInfo;

        createOverlay(); // NOPMD by eeicmsy on 04/08/11 20:13

        replaceBaseWindowListeners();

        chkAll.addChildSelectEventHandler(new ChildSelectEventHandler() {
            @Override
            public void onChildSelect(final ChildSelectEvent event) {
                final AllCheckbox source = (AllCheckbox) event.getSource();
                btnLaunch.setEnabled(source.isAnyChildSelected());

                // Required to handle check/un-check for dup items
                final CheckBox selectedChild = event.getChild();
                source.checkDuplicateItems(selectedChild);
            }
        });

    }
          public String getWindowId() {
              return this.meta.getID();
          }
    
    /**
     * Load data (check-boxes into wizard)
     * Separate from constructor
     * Has to be called for wizard to work
     * <p/>
     * Either make a server call to load wizard
     * or read direct from meta data (fixed case)
     */
    public abstract void loadWizardData();

    /**
     * Return listener to apply to the launch button
     * for chart or grid and check-box selections
     * <p/>
     * Different listeners will be required depending on if
     * need making a server call or not
     *
     * @return listener to apply to launch button press
     */
    public abstract ClickHandler getLaunchSelectionListener();

    /**
     * For as long as putting toolbar overlay on existing base window presenter
     * we news to replace refresh and time handling to suit wizard over lay functionality
     * more than the base window functionaliy
     *
     * @return Time handler
     */
    public abstract TimeParameterValueChangeEventHandler getWizardTimeChangeHandler();

    /**
     * For as long as putting toolbar overlay on existing base window presenter
     * we news to replace refresh and time handling to suit wizard over lay functionality
     * more than the base window functionaliy
     *
     * @return Refresh handler
     */
    public abstract RefreshWindowEventHandler getWizardRefreshHandler();

    /**
     * Clear Cache added for last selected checkboxes for Dynamic checkboxes case
     * (e.g. cause codes). Has to be cleared or will keep launching graphs (not grids) if
     * finds checkboxes in cache (need to cache - when launching new windows following time change)
     */
    public abstract void clearCachedChartData();

    /**
     * Applicable for dynamic wixard
     *
     * @return true if has cached checkboxes
     */
    public abstract boolean hasChartCache();

    /**
     * First launch state is when showing the wizard overlay,
     * without the chart or grid
     *
     * @param isFirstLaunched true if just showing the checkboxes
     */
    public void setFirstLaunch(final boolean isFirstLaunched) {
        this.isFirstLaunch = isFirstLaunched;
    }

    /**
     * Have we launched the chart yet  (grid will create own new window
     * and loose all wizard)
     *
     * @return firstLaunch state
     * @see #setFirstLaunch
     */
    public boolean isFirstLaunch() {
        return isFirstLaunch;
    }

    /*
     * Initiates and Layout the Overlay
     */
    private void createOverlay() {

        this.addStyleName("wizard");
        //Initialise panels
        parentConfigurePanel = getBasePanel("Configure", "wizard-top", true, "mainConfigurePanel");
        chAllHolder = getBasePanel("", "wizard-content", false, "allCheckBoxPanel");
        cpResponseHolder = getBasePanel("", "wizard-content", false, "responseHolder");
        messagePanel = getBasePanel("", "wizard-top", false, "messagePanel");

        cpResponseHolder.addStyleName("mainWizardPanel");
        cpResponseHolder.addStyleName(wizardInfo.getWizardContentCSS());
        chAllHolder.addStyleName(wizardInfo.getWizardContentCSS());
        messagePanel.setVisible(false);

        configureMessagePanel();
        setupButtonsPanel();

        if (meta.isSearchFieldUser() && workspaceController != null) { // the wizard panel may have to react to search field changes
            workspaceController.addSubmitSearchHandler(this);
        }

        //Set the configurations
        configurePanels();
        addLaunchButtonListener();
        //add components to the screen
        this.add(messagePanel);
        this.add(parentConfigurePanel);
        this.add(chAllHolder);
        this.add(cpResponseHolder);
        this.add(cpButtons);

    }

    private void configureMessagePanel() {
        messageDisplay = new ComponentMessagePanel();
        messageDisplay.setHeight("200px");
        messagePanel.add(messageDisplay);
    }

    /*
     * register the handler for the response that will populate the bottom Panel
     */
    private void replaceBaseWindowListeners() {
        // Replacing to avoid two callbacks - want time updating checkboxes only
        // TODO not really working i think (not replacing)
        baseWinPresenter.replaceTimeAndRefreshHandlers(getWizardTimeChangeHandler(), getWizardRefreshHandler());

    }

    /*
     * Fix window width and height based on content of the wizard
     * i.e. If this is the initial Config View the window is not resizeable
     */
    protected void setWizardFixed() {

        final BaseWindow b = ((BaseWindow) baseWinPresenter.getView());

        final boolean firstLaunch = isFirstLaunch();
        if (b != null) {
            b.getWidget().setResizable(!firstLaunch);
            /*b.getWidget().setMaximizable(!firstLaunch); */
            final El elBody = display.getWindowBody();
            b.getWidget().setShadow(false);
            elBody.setDisplayed((firstLaunch ? "none" : "block"));
            /*
             * Because need revert to grid option for  #hasChartCache() in dynamic case (to clear the cache 
             * when user switches to grid (as not found another place to be able to clear cache)
             * Making visible always to be consistent, i.e. for fixed case  (i.e. not just firstLaunch || hasChartCache()).
             */
            cpWizardRadioGroup.setVisible(true);

            parentConfigurePanel.setExpanded(firstLaunch);

            toggleOverLayIcon(parentConfigurePanel);
            toggleOverlay();

            if (!firstLaunch) {
                /** Make the minimum height of the Chart window at least the default window height (if the wizard overlay height is very
                 * small the window height for the chart would be very small, so this ensures that it will be at least the default.
                 */
                if (b.getWidget().getHeight() < b.getWidget().getDefaultLaunchHeight()) {
                    b.getWidget().setHeight(b.getWidget().getDefaultLaunchHeight());
                }
                /**
                 * Get the Wizard Overlay to expand over the Window body containing the chart rather than expanding
                 * the toolbar and pushing down the chart. Unfortunately GXT makes it rather awkward to do this so need
                 * to access the native DOM to get the GXT generated container divs and set the heights + overflows correctly
                 * TODO move to css i.e. switch classes for the different styles
                 */
                final Style parentElementStyle = this.getElement().getParentElement().getStyle();

                parentElementStyle.setHeight(WIZARD_OVERLAY_COLLAPSED_HEIGHT, Unit.PX);
                parentElementStyle.setOverflow(Overflow.VISIBLE);
                this.getElement().getParentElement().getParentElement().getStyle().setOverflow(Overflow.VISIBLE);
                this.display.getWindowToolbar().getElement().getParentElement().getStyle()
                        .setOverflow(Overflow.VISIBLE);
            }
            b.getWidget().layout(true);
        }
    }

    void toggleOverLayIcon(final ContentPanel panel) {
        if (panel.isCollapsed()) {
            panel.setIconStyle("wizard-top-collapse");
            removeStyleName("wizard_shadow");
        } else {
            panel.setIconStyle("wizard-top-expand");
            addStyleName("wizard_shadow");
        }
    }

    private void toggleOverlay() {
        /*Change Icon for expanded or collapse on wizard*/
        final El elBody = display.getWindowBody();

        final boolean cpWizardExpanded = parentConfigurePanel.isExpanded();

        if (cpWizardExpanded) {

            elBody.addStyleName("winbody_open");

            if (!isFirstLaunch()) {
                parentConfigurePanel.addStyleName("wizard-top-body-graph");
            }

        } else {
            elBody.removeStyleName("winbody_open");
            parentConfigurePanel.removeStyleName("wizard-top-body-graph");
        }

        this.cpResponseHolder.setVisible(cpWizardExpanded);
        this.chAllHolder.setVisible(cpWizardExpanded);
        this.cpButtons.setVisible(cpWizardExpanded);
        /**
         * Ensure the Window height is at least big enough to show the entire expanded wizard.
         */
        if (cpWizardExpanded) {
            final BaseWindow b = ((BaseWindow) baseWinPresenter.getView());
            /** Wizard height + window toolbars **/
            final int minimumRequiredWindowHeight = this.getHeight(false)
                    + b.getWidget().getTopComponent().getElement().getOffsetHeight()
                    + b.getWidget().getBottomComponent().getElement().getOffsetHeight();
            // if window grows beyond the content panel due to wizard size, make it fit to container
            if (b.getWidget().getHeight() > b.getConstraintArea().getHeight()) {
                b.getWidget().fitContainer();
            } else {
                b.getWidget().setHeight(Math.max(b.getWidget().getHeight(false), minimumRequiredWindowHeight));
            }
        }
    }

    /**
     * Note reading from meta (not requiring adding search info into Wizard section
     * (or WizardInfoDataType)
     *
     * @return true if new search field informatino to make server calls
     */
    protected boolean isSearchFieldUser() {
        return meta.isSearchFieldUser();
    }

    /**
     * Display warning for no search field if required
     *
     * @return true if search data is required and it is missing
     *         (i.e. indicating not to proceed with the call)
     */
    protected boolean isSearchDataRequiredMissing() {

        if (!isSearchFieldUser()) {
            return false;
        }

        final SearchFieldDataType searchData = baseWinPresenter.getSearchData();

        if (searchData == null || searchData.isEmpty()) {

            MessageDialog.get().show(MISSING_INPUT_DATA, NEED_SEARCH_FIELD_MESSAGE, MessageDialog.DialogType.WARNING);
            return true;
        }
        return false;
    }

    /**
     * clean up amy listeners added
     */
    public void cleanUpOnClose() {
        if (meta.isSearchFieldUser() && workspaceController != null) {
            // no matter if not added
            workspaceController.removeSubmitSearchHandler(this);
        }
        /* the baseWinPresenter is about to be terminated (hide call)
         * don't replaceTimeAndRefreshHandlers(null, null) or introduce new listeners
         */
    }

    /**
     * Show the message panel. This is used to display errors on the window, such as
     * "No Cause Codes fornd for the selected time range"
     * @param title - title of the message
     * @param message - message to display
     * @param dialogType - the type of error (Info, Warning, Error)
     */
    protected void showMessagePanel(final String title, final String message, ComponentMessageType dialogType){
        parentConfigurePanel.setVisible(false);
        chAllHolder.setVisible(false);
        cpResponseHolder.setVisible(false);
        cpButtons.setVisible(false);
        messageDisplay.populate(dialogType, title, message);
        messagePanel.setVisible(true);
    }

    /**
     * Hide the message panel.
     */
    protected  void hideMessagePanel(){
        parentConfigurePanel.setVisible(true);
        chAllHolder.setVisible(true);
        cpResponseHolder.setVisible(true);
        cpButtons.setVisible(true);
        messagePanel.setVisible(false);
    }
    /**
     * If the parent toolbar is resized it will set the height of this component back to auto so need to set it
     * to a specific height and let its components overflow in order to overlay on to the chart.
     */
    public void parentToolbarResize() {
        if (!isFirstLaunch()) {
            this.getElement().getParentElement().getStyle().setHeight(WIZARD_OVERLAY_COLLAPSED_HEIGHT, Unit.PX);
            //XXXXfinal int height = this.getHeight(false);
        }
    }

    /*
     * logic to enable/disable components
     * based on the status of the Grid Radio Button
     */
    protected void toggleRadioStatus(final boolean isChartChecked) {
        chkAll.setEnabled(isChartChecked);

        if (checkboxes.size() > 0) {
            for (final CheckBox chk : checkboxes) {
                if (chk != null) {
                    chk.setEnabled(isChartChecked);
                }
            }
        } else {
            btnLaunch.setEnabled(false);
        }
    }

    /**
     * Reset wizard to no checkboxes with grid selected
     */
    protected void resetToStarterState() {
        cpResponseHolder.removeAll();
        cpResponseHolder.setAutoHeight(true);
        checkboxes.clear();
        chkAll.clear();
        chkAll.setEnabled(false);
        hideMessagePanel();
        // preserve chart data whilst swithing time or refreshing 
        handleRadioGroupForDynamicCache();
    }

    /* interim state enabling until fetch results (window should be masked anyway) 
     * (may have items cached but may turn out can not select them if not present
     * when server returns checkboxes)
     */
    private void handleRadioGroupForDynamicCache() {
        if (hasChartCache()) { // preserve chart data whilst swithing time or refreshing 
            cpWizardRadioGroup.selectChartButton(true);
            chkAll.setEnabled(false);
            btnLaunch.setEnabled(false);

        } else {
            cpWizardRadioGroup.selectGridButton(true);
            btnLaunch.setEnabled(true);
        }
    }

    /**
     * Reuse server comm in Basewindow presenter as will have display type information,
     * search info, etc
     *
     * @return server communication handler
     */
    protected BaseWinServerComms<? extends WidgetDisplay> getServerComms() {
        return baseWinPresenter.getServerComm();
    }

    protected void launchGrid() {

        clearCachedChartData(); // cache is only to preserve checkbox selections when using charts

        closeLaunchedWindow();

        meta.setWizard(EMPTY_STRING);
        meta.clearWizardInfoToGrid();

        // so make the grid the "toggleToolBarType" in meta data ALWAYS
        meta.toggleBottomToolBarType();
        meta.toggleToolBarType();

        // they all start as charts (isMultipleMode included largely to get KPI to work when choose grid option)
        AbstractWindowLauncher launcher = new GridLauncher(meta, eventBus, workspaceController.getCenterPanel(), workspaceController);

        /* pass "true" to keep meta data state (not requiring a fresh meta menu item in base window Presenter)
         * (and search data from parent - i.e. not search component as independant (could even be in ranking tab)
         */
        if (baseWinPresenter.getIsDrillDown()){
            launcher.launchWindowFromWizard(baseWinPresenter.getTimeData(), baseWinPresenter.getSearchData(), true, baseWinPresenter
                    .getView().getWindowState());
        }else {
            launcher.launchWindow(baseWinPresenter.getTimeData(), baseWinPresenter.getSearchData(), true, baseWinPresenter
                    .getView().getWindowState());
        }

    }

    /**
     * Relaunch entire wizard (with new time)
     */
    protected void reLaunchWizardChartSelected() {
        closeLaunchedWindow();

        meta.setWizard(wizardInfo.getWizardID());

        // they all start as charts (isMultipleMode included largely to get KPI to work when choose grid option)
        AbstractWindowLauncher launcher = new ChartLauncher(meta, eventBus, workspaceController.getCenterPanel(), workspaceController);

        /* relaunching fresh meta menu item in base window Presenter is fine
         * (better)
         * Reuse the time and search data
         */
        launcher.launchWindow(baseWinPresenter.getTimeData(), baseWinPresenter.getSearchData(), false, baseWinPresenter
                .getView().getWindowState());

    }

    /**
     * Close launched window.
     * Close to begin life as a grid, or
     * loose the pie chart in dynamic mode following a time or refresh)
     */
    protected void closeLaunchedWindow() {
        //Close the Original Window (without getting into Id issues)
        final BaseWindow b = ((BaseWindow) baseWinPresenter.getView());
        b.hide();

        setFirstLaunch(false);
        setWizardFixed();
        cleanUpOnClose();
    }

    /**
     * Launch chart
     *
     * @param extraParams querty parameters - note when used going to wsU RL from wizardInfo
     */
    protected void launchChart(final String extraParams) {
        /* default display is chart type */
        meta.setWizard(EMPTY_STRING);
        setFirstLaunch(false);
        setWizardFixed();

        if (extraParams.isEmpty()) {
            // slight hack not even using wsURL in wizard info (don't need it), for fixed case
            getServerComms().makeServerCallWithURLParams();
        } else {
            final String wsURL = wizardInfo.getWSURL(); // can be different than basePresener wsURL
            getServerComms().makeServerRequest(baseWinPresenter.getMultipleInstanceWinId(), wsURL, extraParams);
        }
    }

    /*
     * Configures the various controls that will be
     * displayed in the panel sections of the wizard
     * Note: these are predefined
     */
    private void configurePanels() {

        final VerticalPanel top = new VerticalPanel();

        top.add(cpWizardRadioGroup);
        cpWizardRadioGroup.enableRadioChart(false);

        /* adding to own div to 
         * stop raio buttons hidding chkAll */
        chAllHolder.add(chkAll);
        top.add(chAllHolder);

        parentConfigurePanel.add(top);

        /* handle change of radio button status - Enable/Disable Checkbox Options (and caching for dynamic) */
        cpWizardRadioGroup.init();
        handleRadioGroupForDynamicCache();

        btnLaunch.setWidth("70px");
        btnLaunch.getElement().getStyle().setMarginTop(10, Unit.PX);

        cpButtons.setWidget(btnLaunch);
        this.parentConfigurePanel.setExpanded(true);
        parentConfigurePanel.getHeader().sinkEvents(Events.OnClick.getEventCode());
        parentConfigurePanel.getHeader().addListener(Events.OnClick, new OverLayHeaderListener());
    }

    private void addLaunchButtonListener() {
        final ClickHandler launchButtonListener = getLaunchSelectionListener();
        btnLaunch.addClickHandler(launchButtonListener);
    }

    /*
     * Configures the button area displayed at bottom of the wizard
     */
    private void setupButtonsPanel() {
        cpButtons = new SimplePanel();
        final Style style = cpButtons.getElement().getStyle();
        style.setProperty("textAlign", "center");
        style.setHeight(40, Unit.PX);
    }

    /*
     * Provides a base contentPanel for use.
     */
    protected ContentPanel getBasePanel(final String title, final String style, final boolean hasHeader, final String id) {
        final ContentPanel cp = new ContentPanel();
        cp.setAnimCollapse(false);
        cp.setCollapsible(true);
        cp.setBodyStyleName(style + "-body");
        cp.setStyleName(style);
        cp.setHeading(title);
        cp.setIconStyle(style + "-expand");
        cp.setHeaderVisible(hasHeader);
        cp.setId(id);
        return cp;
    }

    Listener<BaseEvent> getGridRadioButtonListener() {
        return new GridRadioButtonListener();
    }

    /*
     * Chart and Grid Radio button Radio Group
     *
     */
    class WizardRadioGroup extends RadioGroup {

        private final Radio rdChart = new Radio();

        private final Radio rdGrid = new Radio();

        public WizardRadioGroup() {
            rdChart.setBoxLabel("Chart View");
            rdGrid.setBoxLabel("Grid View");

            add(rdChart);
            add(rdGrid);

        }

        public void init() {
            rdGrid.addListener(Events.Change, gridRadioButtonListener);
        }

        public void enableRadioChart(final boolean value) {
            rdChart.setEnabled(value);
        }

        public void enableRadioGrid(final boolean value) {
            rdGrid.setEnabled(value);
        }

        public void selectChartButton(final boolean value) {
            rdChart.setValue(value);
        }

        /** 
         * note interfer with selection when need to
         * User does not want changing back to a grid selection after 
         * change time (or search data)
         * @param value value
         */
        public void selectGridButton(final boolean value) {
            if (rdChart.getValue()) {
                // leave user selection alone even if it is disabled
                return;
            }
            rdGrid.setValue(value);
        }

        public boolean isChartSelected() {
            return rdChart.getValue();
        }

        public boolean isGridSelected() {
            return rdGrid.getValue();
        }

    }

    /*
     * Listener class for click events on the
     * header of the wizard content panels
     */
    protected class OverLayHeaderListener implements Listener<ComponentEvent> {

        private final boolean launchAlways;

        public OverLayHeaderListener(final boolean launchAlways) {
            this.launchAlways = launchAlways;
        }

        public OverLayHeaderListener() {
            this(false);
        }

        /* (non-Javadoc)
        * @see com.extjs.gxt.ui.client.event.Listener#handleEvent(com.extjs.gxt.ui.client.event.BaseEvent)
        */
        @Override
        public void handleEvent(final ComponentEvent be) {

            final Header source = (Header) be.getSource();
            final ContentPanel expandablePanel = (ContentPanel) source.getParent();

            if (!isFirstLaunch() || launchAlways) {
                /* Only collapse / expand the panel if this is not the initial
                 * wizard overlay i.e. User has displayed the Pie Chart
                 */
                final boolean isCollapsed = expandablePanel.isCollapsed();
                //Toggle the selected panels expand status
                expandablePanel.setExpanded(isCollapsed);
                toggleOverLayIcon(expandablePanel);
                toggleOverlay();
            }
        }
    }

    /*
     * Listener on "grid view" press (there is only two radio buttons
     * for  view and chart view so this is a "toggle listener")
     */
    private class GridRadioButtonListener implements Listener<BaseEvent> {

        @Override
        public void handleEvent(final BaseEvent be) {
            final Radio selectedRadio = (Radio) be.getSource();

            final boolean isChartChecked = !selectedRadio.getValue();
            btnLaunch.setEnabled(selectedRadio.getValue() || chkAll.isAnyChildSelected());

            toggleRadioStatus(isChartChecked);
        }

    }
}
