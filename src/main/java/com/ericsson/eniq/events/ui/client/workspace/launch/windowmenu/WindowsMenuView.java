/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.common.client.service.DefaultServiceProperties;
import com.ericsson.eniq.events.common.client.time.TimePeriod;
import com.ericsson.eniq.events.ui.client.common.Constants;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.DimensionSelectorType;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.TechnologyType;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceUtils;
import com.ericsson.eniq.events.ui.client.workspace.component.WorkspacePairedLiveloadOracle;
import com.ericsson.eniq.events.ui.client.workspace.config.WorkspaceConfigService;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IDimension;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IDimensionMenuItem;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IWindow;
import com.ericsson.eniq.events.ui.client.workspace.launch.AbstractWindowMenuPresenter.DimensionGroup;
import com.ericsson.eniq.events.ui.client.workspace.launch.AbstractWindowMenuPresenter.PairedSuggestion;
import com.ericsson.eniq.events.ui.client.workspace.launch.IWindowMenuView;
import com.ericsson.eniq.events.ui.client.workspace.launch.WindowLaunchParams;
import com.ericsson.eniq.events.ui.client.workspace.launch.WorkspaceLaunchMenuResourceBundle;
import com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu.WindowFilter.WindowItem;
import com.ericsson.eniq.events.widgets.client.dropdown.*;
import com.ericsson.eniq.events.widgets.client.dropdown.decorator.DefaultDropDownDecorator;
import com.ericsson.eniq.events.widgets.client.dropdown.time.DropDownTimeComponent;
import com.ericsson.eniq.events.widgets.client.mask.MaskHelper;
import com.ericsson.eniq.events.widgets.client.suggestbox.ExtendedSuggestBoxWithButton;
import com.ericsson.eniq.events.widgets.client.suggestbox.LiveLoadOracle;
import com.ericsson.eniq.events.widgets.client.textbox.ExtendedTextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.MAX_SELECTED_WINDOWS;

/**
 * Windows Menu
 *
 * @author ecarsea
 * @since May 2012
 */
@SuppressWarnings("deprecation")
public class WindowsMenuView extends BaseView<WindowsMenuPresenter> implements IWindowMenuView, IWindowSelectionHandler {

    private static final int DROPDOWNS_HEIGHT = 178;

    private static final int LAUNCH_BUTTON_HEIGHT = 20 + 21 + 18;/*top margin+height+bottom margin*/

    private static final int GROUP_INFO_PANEL_OFFSET = 50;

    private static final int DIMENSION_POPUP_MAX_HEIGHT = 570;

    interface WindowsMenuViewUiBinder extends UiBinder<Widget, WindowsMenuView> {
    }

    private static WindowsMenuViewUiBinder uiBinder = GWT.create(WindowsMenuViewUiBinder.class);

    @UiField
    WindowFilterPanel windowFilterPanel;

    @UiField
    DropDownTimeComponent timeSelectionDropDown;

    @UiField(provided = true)
    DropDown<DimensionItem> dimensionDropDown;

    @UiField
    HTMLPanel maskHolder;

    @UiField
    HorizontalPanel maxWindowsLabel;

    @UiField
    Button launchBtn;

    @UiField
    ExtendedTextBox textEntryBox;

    @UiField(provided = true)
    ExtendedSuggestBoxWithButton primaryLiveload;

    @UiField
    HorizontalPanel groupPanel;

    @UiField(provided = true)
    DropDown<DimensionGroup> groupSuggestBox;

    @UiField(provided = true)
    DropDown<PairedSuggestion> pairedSuggestBox;

    @UiField
    ToggleButton groupInfoToggleButton;

    @UiField(provided = true)
    ExtendedSuggestBoxWithButton secondaryLiveload;

    @UiField
    WorkspaceLaunchMenuResourceBundle resourceBundle;

    private WorkspaceConfigService configService;

    private final GroupElementPopupPanel groupElementPopupPanel = new GroupElementPopupPanel();

    private LiveLoadOracle primaryLiveloadOracle;

    private WorkspacePairedLiveloadOracle secondaryLiveloadOracle;

    private IDimension selectedDimension;

    private final MaskHelper maskHelper = new MaskHelper();

    /** Only one of these items can be displayed at a time. All occupy same location **/
    private final Widget[] itemCarousel;

    private WindowFilter windowFilter;

    private final DefaultServiceProperties serviceProperties;

    private TechnologyType selectedTechnology;

    private DimensionGroup selectedGroup;

    private boolean isWindowsTabSelected = true;

    private boolean isGroupInfoButtonToggled = false;

    @UiHandler("launchBtn")
    void onLaunchClicked(@SuppressWarnings("unused")
    final ClickEvent event) {
        final List<IWindow> windows = new ArrayList<IWindow>();
        for (final WindowItem windowItem : windowFilter.getSelectedItems()) {
            windows.add(windowItem.getWindow());
        }
        doLaunch(windows);
    }

    @UiHandler("groupInfoToggleButton")
    void onToggleButtonValueChange(final ValueChangeEvent<Boolean> valueChangeEvent) {
        isGroupInfoButtonToggled = valueChangeEvent.getValue();
        handleGroupElementPopupPanel();
    }

    public WindowsMenuView() {
        this.serviceProperties = new DefaultServiceProperties();
        setupPrimaryLiveload();
        setupSecondaryLiveload();
        setupGroupSuggest();
        setupPairedSuggest();
        createDimensionDropDown();
        initWidget(uiBinder.createAndBindUi(this));
        final TimePeriod timePeriod = WorkspaceUtils
                .getTimePeriodFromMinutes(TimeInfoDataType.getDefaultTime().timeRange);
        setupTimeDropDown();
        if (timePeriod == TimePeriod.CUSTOM) {
            final Date from = TimeInfoDataType.getDefaultTime().dateFrom;
            final Date to = TimeInfoDataType.getDefaultTime().dateTo;
            timeSelectionDropDown.setValue(new TimePeriodDropDownItem(timePeriod, from, to));
        } else {
            timeSelectionDropDown.setValue(new TimePeriodDropDownItem(timePeriod));
        }
        setDefaultText();
        addHandlers(textEntryBox);
        itemCarousel = new Widget[] { primaryLiveload, groupPanel, pairedSuggestBox, textEntryBox };

        setSeleniumTags();
    }

    /**
     * Add some handlers for the ExtendedTextBox (textEntryBox).
     * @param inputBox
     */
    private void addHandlers(final ExtendedTextBox inputBox) {
        inputBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(final ChangeEvent changeEvent) {
                final boolean isEmpty = textEntryBox.isTextBoxEmpty();
                if (!isEmpty) {
                    final boolean value = getPresenter().validateTextEntry(textEntryBox.getText(),
                            selectedDimension.getId());
                    enableFields(value);
                }
                if (isEmpty) {
                    enableFields(false);
                    setInvalid(false);
                }
            }
        });
    }

    /**
     * Enable/disable multiple fields (windowFilterPanel & launchBtn).
     * This method handles the special conditions:
     * 1. Set both disabled (if imsi, ptmsi or msisdn are invalid)
     * 2. set windowFilterPanel enabled
     * 3. set launch button enabled only if there are selected windows in the windowFilterPanel.
     * @param value
     */
    private void enableFields(final boolean value) {
        windowFilterPanel.setEnabled(value);
        if (value) {
            //if there are selected windows in the filterWindow, then enable the launch button.
            final int selectedWindowCount = windowFilter.getSelectedItems().size();
            if (selectedWindowCount > 0) {
                launchBtn.setEnabled(value);
            }
        }
        if (!value) {
            launchBtn.setEnabled(value);
        }
    }

    void setupTimeDropDown() {
        final List<TimePeriodDropDownItem> timePeriods = new ArrayList<TimePeriodDropDownItem>();
        for (final TimePeriod timePeriod : TimePeriod.values()) {
            timePeriods.add(new TimePeriodDropDownItem(timePeriod));
        }
        timeSelectionDropDown.update(timePeriods);
        timeSelectionDropDown.getCalendarPopUp().setMaxNumberDaysRange(0);//0 indicates no max range
    }

    void setWindowsTabSelected(final boolean isWindowsTabSelected) {
        this.isWindowsTabSelected = isWindowsTabSelected;
    }

    void handleGroupElementPopupPanelOnMouseOver() {
        if (isWindowsTabSelected && isGroupInfoButtonToggled) {
            showGroupElementPopupPanel();
        } else {
            hideGroupElementPopupPanel();
        }
    }

    void handleGroupElementPopupPanel() {
        if (isWindowsTabSelected && isGroupInfoButtonToggled && selectedGroup != null) {
            showGroupElementPopupPanel();
        } else {
            hideGroupElementPopupPanel();
        }
    }

    void showGroupElementPopupPanel() {
        groupElementPopupPanel.setPopupPosition(WindowsMenuView.this.getOffsetWidth() + GROUP_INFO_PANEL_OFFSET,
                WindowsMenuView.this.getAbsoluteTop());
        groupElementPopupPanel.setHeader(selectedDimension.getName() + " Information");
        groupElementPopupPanel.show();
    }

    void hideGroupElementPopupPanel() {
        groupElementPopupPanel.hide();
    }

    private void createDimensionDropDown() {
        dimensionDropDown = new DropDown<DimensionItem>() {
            @Override
            protected int getPopupMaxHeight() {
                return DIMENSION_POPUP_MAX_HEIGHT;
            }
        };
    }

    private void setSeleniumTags() {
        timeSelectionDropDown.getElement().setId(Constants.SELENIUM_TAG + "timeRangeDropDown");
        dimensionDropDown.getElement().setId(Constants.SELENIUM_TAG + "dimensionSelect");
        secondaryLiveload.getElement().setId(Constants.SELENIUM_TAG + "searchFieldInput");
        primaryLiveload.getElement().setId(Constants.SELENIUM_TAG + "searchField");
        launchBtn.getElement().setId(Constants.SELENIUM_TAG + "launchWindowButton");
        pairedSuggestBox.getElement().setId(Constants.SELENIUM_TAG + "pairedSuggestBox");
        textEntryBox.getElement().setId(Constants.SELENIUM_TAG + "textEntryBox");
        groupSuggestBox.getElement().setId(Constants.SELENIUM_TAG + "groupSuggestBox");
    }

    public void init(final WorkspaceLaunchMenuResourceBundle resourceBundle, final WorkspaceConfigService configService) {
        this.resourceBundle = resourceBundle;
        this.configService = configService;
        setupDimensionDropDown(configService);
        final WindowFilterView windowFilterView = new WindowFilterView(resourceBundle,
                windowFilterPanel.getFilterContent());
        windowFilterView.setWindowSelectionHandler(this);
        this.windowFilter = new WindowFilter(configService, windowFilterView);
        windowFilterPanel.init(windowFilter, resourceBundle);
    }

    private void setupPrimaryLiveload() {
        primaryLiveloadOracle = new LiveLoadOracle(serviceProperties);
        primaryLiveload = new ExtendedSuggestBoxWithButton(primaryLiveloadOracle,null);
        primaryLiveload.getTextBox().addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(final BlurEvent blurEvent) {
                primaryLiveload.enableDefaultText();
            }
        });
        setupLiveloadEventHandlers(primaryLiveload, true);
    }

    /**
     *  The values of this live load depend on the selection in the primary liveload. i.e. Terminal Make/Model
     */
    private void setupSecondaryLiveload() {
        secondaryLiveloadOracle = new WorkspacePairedLiveloadOracle(serviceProperties);
        secondaryLiveload = new ExtendedSuggestBoxWithButton(secondaryLiveloadOracle, null);
        secondaryLiveload.getTextBox().addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(final BlurEvent blurEvent) {
                secondaryLiveload.enableDefaultText();
            }
        });
        secondaryLiveload.getTextBox().addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(final FocusEvent focusEvent) {
                secondaryLiveload.checkDefaultTextOnFocus();
            }
        });
        secondaryLiveload.setEnabled(false);
        secondaryLiveload.setDefaultText("Not applicable");
        setupLiveloadEventHandlers(secondaryLiveload, false);
    }

    private void setupPairedSuggest() {
        pairedSuggestBox = new DropDown<PairedSuggestion>();
        pairedSuggestBox.addValueChangeHandler(new ValueChangeHandler<PairedSuggestion>() {

            @Override
            public void onValueChange(final ValueChangeEvent<PairedSuggestion> event) {
                if (event.getValue() != null) {
                    resetLaunch();
                    secondaryLiveload.setVisible(true);
                    secondaryLiveload.setEnabled(true);
                    secondaryLiveload.setDefaultText(" - Enter terminal - ");
                    secondaryLiveloadOracle.init(event.getValue().getLoadUrl(), event.getValue().getId());
                    windowFilterPanel.setEnabled(false);
                    launchBtn.setEnabled(false);
                }
            }
        });
        pairedSuggestBox.setDecorator(new DefaultDropDownDecorator<PairedSuggestion>(DropDown.resourceBundle) {

            @Override
            public String toString(final PairedSuggestion value) {
                if (value == null) {
                    return null;
                }
                return value.getId();
            }
        });
    }

    private void setupGroupSuggest() {
        groupSuggestBox = new DropDown<DimensionGroup>();
        groupSuggestBox.addValueChangeHandler(new ValueChangeHandler<DimensionGroup>() {

            @Override
            public void onValueChange(final ValueChangeEvent<DimensionGroup> event) {
                selectedGroup = event.getValue();
                resetLaunch();
                final String groupTypeRegex = configService.getDimension(selectedDimension.getGroupType())
                        .getLiveloadTechnologyIndicator();
                if (selectedGroup != null) {
                    if (WorkspaceUtils.isNonEmptyString(groupTypeRegex)) {
                        selectedTechnology = null;
                        for (final String groupNameItem : selectedGroup.getElements()) {
                            try {
                                final RegExp re = RegExp.compile(groupTypeRegex);
                                if (re.test(groupNameItem)) {
                                    final MatchResult m = re.exec(groupNameItem);
                                    selectedTechnology = TechnologyType.fromString(m.getGroup(0));
                                }
                            } catch (final Exception e) {
                                // do nothing. if there are any regex exception etc just dont filter based on technology
                            }
                        }
                    }
                    groupElementPopupPanel.setGroupElements(selectedGroup.getElements());
                    if (!windowFilterPanel.isEnabled()) {
                        windowFilterPanel.setEnabled(true);
                        windowFilter.updateFilter(selectedDimension);
                    }
                    windowFilter.updateFilter(selectedDimension, selectedTechnology);
                } else {
                    groupElementPopupPanel.setGroupElements(Collections.<String> emptyList());
                }
                handleGroupElementPopupPanel();
            }
        });

        groupSuggestBox.setDecorator(new DefaultDropDownDecorator<DimensionGroup>(DropDown.resourceBundle) {

            @Override
            public String toString(final DimensionGroup value) {
                if (value == null) {
                    return null;
                }
                return value.getName();
            }
        });

        groupSuggestBox.addDropDownMouseOverHandler(new DropDownMouseOverHandler<DimensionGroup>() {

            @Override
            public void onMouseOver(final DropDownMouseOverEvent<DimensionGroup> event) {
                groupElementPopupPanel.setGroupElements(event.getValue().getElements());
                handleGroupElementPopupPanelOnMouseOver();
            }
        });
    }

    private void setupDimensionDropDown(final WorkspaceConfigService configService) {
        final List<DimensionItem> dimensionItems = new ArrayList<DimensionItem>();
        boolean isSeparatorNeeded;

        for (final IDimensionMenuItem item : configService.getDimensionMenu().getMenuItem()) {
            isSeparatorNeeded = false;
            for (final String dimensionId : item.getDimensionIds().getDimensionId()) {
                final IDimension dimension = configService.getDimension(dimensionId);
                if (dimension != null) {
                    final DimensionItem dimensionItem = new DimensionItem(dimension);
                    dimensionItems.add(dimensionItem);
                    isSeparatorNeeded = true;
                }
            }
            if (isSeparatorNeeded) {
                /** Separators **/
                dimensionItems.add(new DimensionItem() {

                    @Override
                    public boolean isSeparator() {
                        return true;
                    }
                });
            }
        }
        // remove last separator, unneeded
        if (dimensionItems.size()>0)
            dimensionItems.remove(dimensionItems.size() - 1);
        dimensionDropDown.update(dimensionItems);
        dimensionDropDown.addValueChangeHandler(new ValueChangeHandler<DimensionItem>() {

            @Override
            public void onValueChange(final ValueChangeEvent<DimensionItem> event) {
                selectedDimension = event.getValue().getDimension();
                onDimensionSelected();
            }
        });
    }

    void onDimensionSelected() {
        resetLaunchState();
        switch (DimensionSelectorType.fromString(selectedDimension.getSelectorType())) {
        case PAIRED_SEARCH:
            /** TERMINAL **/
            showSelectionItem(primaryLiveload);
            primaryLiveload.setEnabled(false);
            pairedSuggestBox.setDefaultText("Enter Terminal Make");
            showSelectionItem(maskHolder);
            maskHelper.mask(maskHolder.getElement(), "Loading Terminal Makes", maskHolder.getOffsetHeight());
            getPresenter().getInitialSuggestions(selectedDimension);
            windowFilter.updateFilter(selectedDimension);
            secondaryLiveload.setEnabled(false);
            break;
        case SINGLE_SEARCH:
            /** GROUPS **/
            if (WorkspaceUtils.isNonEmptyString(selectedDimension.getGroupType())) {
                showSelectionItem(primaryLiveload);
                showSelectionItem(secondaryLiveload);
                primaryLiveload.setEnabled(false);
                secondaryLiveload.setEnabled(false);
                showSelectionItem(maskHolder);
                maskHelper.mask(maskHolder.getElement(), "Loading " + selectedDimension.getName() + "s");
                groupSuggestBox.setDefaultText("Enter " + selectedDimension.getName());
                getPresenter().getGroups(selectedDimension);
                handleGroupElementPopupPanel();
                //                    hideSelectionItem(primaryLiveload);
                showSelectionItem(groupSuggestBox);
                showSelectionItem(groupInfoToggleButton);
                groupSuggestBox.setEnabled(true);
                groupSuggestBox.setVisible(true);
            } else {
                /** APN, CONTROLLER, ACCESS AREA, SGSN-MME, MSC, TRACKING AREA **/
                showSelectionItem(primaryLiveload);
                primaryLiveload.setDefaultText("Enter " + selectedDimension.getName());
                primaryLiveload.setEnabled(true);
                primaryLiveloadOracle.init(selectedDimension.getLiveloadUrl(), selectedDimension.getId());
                secondaryLiveload.setEnabled(false);
            }
            break;
        case TEXT_ENTRY:
            /** IMSI, PTMSI, MSISDN **/
            primaryLiveload.setEnabled(false);
            secondaryLiveload.setEnabled(false);
            selectItemToShow(textEntryBox);
            textEntryBox.setEnabled(true);
            textEntryBox.setDefaultText("Enter " + selectedDimension.getName());
            windowFilter.updateFilter(selectedDimension);
            break;
        case NO_SEARCH:
            /** 2G, 3G, 4G,  **/
            showSelectionItem(primaryLiveload);
            showSelectionItem(secondaryLiveload);
            windowFilterPanel.setEnabled(true);
            primaryLiveload.setEnabled(false);
            secondaryLiveload.setEnabled(false);
            windowFilter.updateFilter(selectedDimension);
            break;
        }
    }

    void selectItemToShow(final Widget selectionItem) {

        for (final Widget item : itemCarousel) {
            if (!selectionItem.equals(item)) {
                hideSelectionItem(item);
            }
        }
        showSelectionItem(selectionItem);
    }

    protected void hidePrimaryItems() {
        for (final Widget item : itemCarousel) {
            hideSelectionItem(item);
        }
    }

    /**
     * @param widget
     */
    private void showSelectionItem(final Widget widget) {
        widget.removeStyleName(resourceBundle.workspaceLaunchStyle().hideSelectionItem());
    }

    /**
     * @param widget
     */
    private void hideSelectionItem(final Widget widget) {
        if (!widget.getStyleName().contains(resourceBundle.workspaceLaunchStyle().hideSelectionItem())) {
            widget.addStyleName(resourceBundle.workspaceLaunchStyle().hideSelectionItem());
        }
    }

    private void resetLaunchState() {
        setDefaultText();
        showMaxWindowsWarning(false);
        selectedTechnology = null;
        selectedGroup = null;
        windowFilterPanel.clear();
        hidePrimaryItems();
        hideGroupElementPopupPanel();
        secondaryLiveload.setVisible(false);
        hideSelectionItem(groupSuggestBox);
        hideSelectionItem(pairedSuggestBox);
        hideSelectionItem(maskHolder);
        hideSelectionItem(textEntryBox);
        hideSelectionItem(groupInfoToggleButton);
        primaryLiveload.setEnabled(true);
        primaryLiveload.setDefaultText("Not applicable");
        primaryLiveload.setVisible(true);
        secondaryLiveload.setEnabled(true);
        secondaryLiveload.setDefaultText("Not applicable");
        secondaryLiveload.setVisible(true);
        windowFilterPanel.setEnabled(false);
        launchBtn.setEnabled(false);
        launchBtn.setText("Launch");
        getPresenter().onWindowsSelected(0);
        handleGroupElementPopupPanel();
    }

    private void setDefaultText() {
        primaryLiveload.setDefaultText("Not applicable");
    }

    private void setupLiveloadEventHandlers(final ExtendedSuggestBoxWithButton liveloadBox,
            final boolean isPrimaryLiveload) {
        liveloadBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
            @Override
            public void onSelection(final SelectionEvent<SuggestOracle.Suggestion> selectionEvent) {
                selectedTechnology = null;

                if (!isPrimaryLiveload) {
                    windowFilterPanel.setEnabled(true);
                    resetLaunch();
                } else {
                    windowFilterPanel.setEnabled(true);
                    resetLaunch();
                    /* Only for non-group liveloads, group liveloads are handled in setupGroupSuggest() */
                    final String regex = selectedDimension.getLiveloadTechnologyIndicator();
                    if (selectedDimension.getGroupType() == null && WorkspaceUtils.isNonEmptyString(regex)) {
                        try {
                            final String liveloadItem = selectionEvent.getSelectedItem().getDisplayString();
                            final RegExp re = RegExp.compile(regex);
                            if (re.test(liveloadItem)) {
                                final MatchResult m = re.exec(liveloadItem);
                                selectedTechnology = TechnologyType.fromString(m.getGroup(0));
                            }
                        } catch (final Exception e) {
                            // do nothing. if there are any regex exception etc just dont filter based on technology
                        }
                    }
                }
                windowFilter.updateFilter(selectedDimension, selectedTechnology);
            }
        });

        liveloadBox.getTextBox().addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(final FocusEvent focusEvent) {
                liveloadBox.checkDefaultTextOnFocus();
            }
        });
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu.IWindowSelectionHandler#onDoubleClick(com.ericsson.eniq.events.ui.client.workspace.config.datatype.IWindow)
     */
    @Override
    public void onDoubleClick(final IWindow window) {
        doLaunch(new ArrayList<IWindow>() {
            {
                add(window);
            }
        });

    }

    private void doLaunch(final List<IWindow> windows) {
        final TimePeriodDropDownItem value = timeSelectionDropDown.getValue();
        final Date curDate = new Date();
        final TimePeriod period = value.getTimePeriod();
        final Date from = value.getFrom() == null ? new Date(curDate.getTime() - period.toMiliseconds()) : value
                .getFrom();
        final Date to = value.getTo() == null ? new Date() : value.getTo();

        final WindowLaunchParams params = new WindowLaunchParams(period, from, to, selectedDimension,
                selectedTechnology, windows, getPrimarySelection(), getPairedSelectionUrl(), getSecondarySelection(),
                this);
        TimeInfoDataType.setDefaultTime(WorkspaceUtils.getTimeInfo(period, from, to));
        getPresenter().onLaunchWindows(params);
    }

    /**
     * @return
     */
    private String getSecondarySelection() {
        if (DimensionSelectorType.fromString(selectedDimension.getSelectorType()).equals(
                DimensionSelectorType.PAIRED_SEARCH)) {
            return secondaryLiveload.getText();
        }
        return "";
    }

    /**
     * @return
     */
    private String getPrimarySelection() {
        String primarySelection = "";
        switch (DimensionSelectorType.fromString(selectedDimension.getSelectorType())) {
        case PAIRED_SEARCH:
            primarySelection = pairedSuggestBox.getValue().getId();
            break;
        case SINGLE_SEARCH:
            if (WorkspaceUtils.isNonEmptyString(selectedDimension.getGroupType())) {
                primarySelection = groupSuggestBox.getValue().getName();
            } else {
                primarySelection = primaryLiveload.getText();
            }
            break;
        case TEXT_ENTRY:
            final String value = textEntryBox.getText().replaceAll(" ", "");
            primarySelection = value;
            break;

        case NO_SEARCH:
            break;
        }
        return primarySelection;
    }

    /**
     * Set the textEntryBox invalid. This highlights the field with a red stroke.
     * @param value
     */
    protected void setInvalid(final boolean value) {
        textEntryBox.highlightInvalidField(value);
    }

    private String getPairedSelectionUrl() {
        if (selectedDimension.getSelectorType().equals(DimensionSelectorType.PAIRED_SEARCH)) {
            return pairedSuggestBox.getValue().getLoadUrl();
        }
        return "";
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu.IWindowSelectionHandler#onSelectionChange(java.util.List)
     */
    @Override
    public void onSelectionChange(final int selectedWindows) {
        launchBtn.setEnabled(selectedWindows > 0);
        launchBtn.setText("Launch" + ((selectedWindows > 0) ? " (" + selectedWindows + ")" : ""));
        getPresenter().onWindowsSelected(selectedWindows);
        showMaxWindowsWarning(selectedWindows >= MAX_SELECTED_WINDOWS);
    }

    /**
     * @param groups
     */
    @Override
    public void setGroups(final List<DimensionGroup> groups) {
        unmask();
        selectItemToShow(groupPanel);
        groupSuggestBox.update(groups);
    }

    /**
     * @param suggestions
     */
    @Override
    public void setInitalSuggestions(final List<PairedSuggestion> suggestions) {
        unmask();
        selectItemToShow(pairedSuggestBox);
        pairedSuggestBox.update(suggestions);
    }

    @Override
    public void unmask() {
        hideSelectionItem(maskHolder);
        maskHelper.unmask();
    }

    public void setFilterPanelHeight(final int viewHeight) {
        final int scrollBarHeight = viewHeight - (DROPDOWNS_HEIGHT + LAUNCH_BUTTON_HEIGHT);
        windowFilterPanel.setScrollPanelHeight(scrollBarHeight);
    }

    void showMaxWindowsWarning(final boolean show) {
        maxWindowsLabel.getElement().getStyle().setDisplay(show ? Display.BLOCK : Display.NONE);
    }

    private static class DimensionItem implements IDropDownItem {
        private final IDimension dimension;

        /**
         * @param dimension
         */
        public DimensionItem(final IDimension dimension) {
            this.dimension = dimension;
        }

        /**
         * 
         */
        public DimensionItem() {
            this.dimension = null;
        }

        /**
         * @return
         */
        public IDimension getDimension() {
            return dimension;
        }

        /* (non-Javadoc)
         * @see com.ericsson.eniq.events.widgets.client.dropdown.IDropDownItem#isSeparator()
         */
        @Override
        public boolean isSeparator() {
            return false;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return dimension == null ? "" : dimension.getName();
        }
    }

    //    Resets the launch button and info message "Analysis Windows Selected" when the user changes search criteria eg Terminal
    public void resetLaunch(){
        launchBtn.setEnabled(false);
        launchBtn.setText("Launch");
        getPresenter().onWindowsSelected(0);
    }
}