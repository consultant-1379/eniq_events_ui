/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.common.client.service.DefaultServiceProperties;
import com.ericsson.eniq.events.common.client.time.TimePeriod;
import com.ericsson.eniq.events.ui.client.datatype.ExtraURLDataType;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceConstants.DimensionSelectorType;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceUtils;
import com.ericsson.eniq.events.ui.client.workspace.component.WorkspacePairedLiveloadOracle;
import com.ericsson.eniq.events.ui.client.workspace.config.WorkspaceConfigService;
import com.ericsson.eniq.events.ui.client.workspace.config.datatype.IDimension;
import com.ericsson.eniq.events.ui.client.workspace.datatype.WindowState;
import com.ericsson.eniq.events.ui.client.workspace.launch.AbstractWindowMenuPresenter.DimensionGroup;
import com.ericsson.eniq.events.ui.client.workspace.launch.AbstractWindowMenuPresenter.PairedSuggestion;
import com.ericsson.eniq.events.ui.client.workspace.launch.IWindowMenuView;
import com.ericsson.eniq.events.ui.client.workspace.launch.WorkspaceLaunchMenuResourceBundle;
import com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu.GroupElementPopupPanel;
import com.ericsson.eniq.events.widgets.client.dropdown.DropDown;
import com.ericsson.eniq.events.widgets.client.dropdown.DropDownMouseOverEvent;
import com.ericsson.eniq.events.widgets.client.dropdown.DropDownMouseOverHandler;
import com.ericsson.eniq.events.widgets.client.dropdown.TimePeriodDropDownItem;
import com.ericsson.eniq.events.widgets.client.dropdown.decorator.DefaultDropDownDecorator;
import com.ericsson.eniq.events.widgets.client.dropdown.time.DropDownTimeComponent;
import com.ericsson.eniq.events.widgets.client.mask.MaskHelper;
import com.ericsson.eniq.events.widgets.client.suggestbox.ExtendedSuggestBoxWithButton;
import com.ericsson.eniq.events.widgets.client.suggestbox.ExtendedSuggestDisplay;
import com.ericsson.eniq.events.widgets.client.suggestbox.LiveLoadOracle;
import com.ericsson.eniq.events.widgets.client.textbox.ExtendedTextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Workspace Launch Element
 *
 * @author ecarsea
 * @since June 2012
 */
@SuppressWarnings("deprecation")
public class WorkspaceLaunchElementView extends BaseView<WorkspaceLaunchElementPresenter> implements IWindowMenuView {

    private final WorkspaceConfigService workspaceConfigService;

    interface WorkspaceLaunchElementViewUiBinder extends UiBinder<Widget, WorkspaceLaunchElementView> {
    }

    private static WorkspaceLaunchElementViewUiBinder uiBinder = GWT.create(WorkspaceLaunchElementViewUiBinder.class);

    @UiField
    CheckBox launchCheckBox;

    @UiField
    Label dimensionLabel;

    @UiField
    Label windowTitle;

    @UiField
    DropDownTimeComponent timeSelectionDropDown;

    @UiField
    HTMLPanel maskHolder;

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

    private LiveLoadOracle primaryLiveloadOracle;

    private WorkspacePairedLiveloadOracle secondaryLiveloadOracle;

    @UiField
    WorkspaceLaunchMenuResourceBundle resourceBundle;

    private final GroupElementPopupPanel groupElementPopup = new GroupElementPopupPanel();

    private final MaskHelper maskHelper = new MaskHelper();

    private IDimension selectedDimension;

    private final DefaultServiceProperties serviceProperties;

    private WindowState windowState;

    private final String SEPERATOR = " > ";

    private ExtendedSuggestDisplay suggestionDisplay = null;

    @Inject
    public WorkspaceLaunchElementView(final WorkspaceLaunchMenuResourceBundle resourceBundle,
            final WorkspaceConfigService workspaceConfigService) {
        this.workspaceConfigService = workspaceConfigService;
        this.serviceProperties = new DefaultServiceProperties();
        this.resourceBundle = resourceBundle;

        setupPrimaryLiveload();
        setupSecondaryLiveload();
        setupGroupSuggest();
        setupPairedSuggest();
        initWidget(uiBinder.createAndBindUi(this));
        setupTimeDropDown();
        setupHandlers();
    }

    private void setupHandlers() {
        launchCheckBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (launchCheckBox.getValue()) {
                    //enable all the controls...
                    setComponentsEnabled(true);
                } else {
                    //disable all the controls....
                    setComponentsEnabled(false);
                }
            }
        });
    }

    private void setComponentsEnabled(final boolean state){
        timeSelectionDropDown.setEnabled(state);
        textEntryBox.setEnabled(state);
        primaryLiveload.setEnabled(state);
        groupSuggestBox.setEnabled(state);
        pairedSuggestBox.setEnabled(state);
        groupInfoToggleButton.setEnabled(state);
        secondaryLiveload.setEnabled(state);
        getPresenter().toggleLaunchElement(state);
    }

    @UiHandler("groupInfoToggleButton")
    void onToggleButtonValueChange(final ValueChangeEvent<Boolean> valueChangeEvent) {
        if(!groupInfoToggleButton.isDown() && groupElementPopup.isVisable()){
            groupElementPopup.hide();
        }
    }

    public void init(final WindowState windowState) {
        this.windowState = windowState;

        this.timeSelectionDropDown.setValue(new TimePeriodDropDownItem(
                TimePeriod.fromString(windowState.getTimePeriod()),
                windowState.getFrom(),
                windowState.getTo()));
        selectedDimension = workspaceConfigService.getDimension(windowState.getDimensionId());
        this.dimensionLabel.setText(selectedDimension.getName());

        setWindowTitle(windowState);

        switch (DimensionSelectorType.fromString(selectedDimension.getSelectorType())) {
        case TEXT_ENTRY:
            showSelectionItem(textEntryBox);
            textEntryBox.setText(WorkspaceUtils.getString(windowState.getPrimarySelection()));
            break;
        case SINGLE_SEARCH:
            if (WorkspaceUtils.isNonEmptyString(selectedDimension.getGroupType())) {
                groupSuggestBox.setText(WorkspaceUtils.getString(windowState.getPrimarySelection()));
                showSelectionItem(maskHolder);
                maskHelper.mask(maskHolder.getElement(), "Loading " + selectedDimension.getName() + "s",
                        maskHolder.getOffsetHeight());
                getPresenter().getGroups(selectedDimension);
            } else {
                showSelectionItem(primaryLiveload);
                primaryLiveload.setText(WorkspaceUtils.getString(windowState.getPrimarySelection()));
                primaryLiveloadOracle.init(selectedDimension.getLiveloadUrl(), selectedDimension.getId());
            }

            break;
        case PAIRED_SEARCH:
            showSelectionItem(pairedSuggestBox);
            showSelectionItem(secondaryLiveload);
            pairedSuggestBox.setValue(new PairedSuggestion(WorkspaceUtils.getString(windowState.getPrimarySelection()),
                    WorkspaceUtils.getString(windowState.getPairedSelectionUrl())));
            secondaryLiveload.setText(WorkspaceUtils.getString(windowState.getSecondarySelection()));
            showSelectionItem(maskHolder);
            maskHelper.mask(maskHolder.getElement(), "Loading Terminal Makes", maskHolder.getOffsetHeight());
            getPresenter().getInitialSuggestions(selectedDimension);
            break;
        case NO_SEARCH:
            break;
        }
    }

    private void setWindowTitle(WindowState windowState) {
        StringBuilder stringBuilder = new StringBuilder(SEPERATOR);
        stringBuilder.append(workspaceConfigService.getWindowCategory(windowState.getWindowId()));
        stringBuilder.append(SEPERATOR);
        stringBuilder.append(workspaceConfigService.getWindow(windowState.getWindowId()).getWindowTitle());

        if (!windowState.getExtraURLType().equals("")){
            ArrayList<String> titles = ExtraURLDataType.extractExtraURLParams(ExtraURLDataType.ExtraURLType.fromString(windowState.getExtraURLType()), windowState.getExtraURLParams());
            for(String title: titles){
                stringBuilder.append(SEPERATOR);
                stringBuilder.append(title);
            }
        }
        this.windowTitle.setText(stringBuilder.toString());
    }


    private void setupTimeDropDown() {
        final List<TimePeriodDropDownItem> timePeriods = new ArrayList<TimePeriodDropDownItem>();
        for (final TimePeriod timePeriod : TimePeriod.values()) {
            timePeriods.add(new TimePeriodDropDownItem(timePeriod));
        }
        timeSelectionDropDown.update(timePeriods);
        timeSelectionDropDown.setValue(new TimePeriodDropDownItem(WorkspaceUtils
                .getTimePeriodFromMinutes(TimeInfoDataType.DEFAULT.timeRange)));
    }

    /**
     * @param widget
     */
    private void showSelectionItem(final Widget widget) {
        widget.removeStyleName(resourceBundle.workspaceLaunchStyle().hideSelectionItem());
    }

    /**
     * @param groups
     */
    @Override
    public void setGroups(final List<DimensionGroup> groups) {
        unmask();
        showSelectionItem(groupPanel);
        groupSuggestBox.update(groups);
    }

    /**
     * @param suggestions
     */
    @Override
    public void setInitalSuggestions(final List<PairedSuggestion> suggestions) {
        unmask();
        pairedSuggestBox.update(suggestions);
    }

    @Override
    public void unmask() {
        hideSelectionItem(maskHolder);
        maskHelper.unmask();
    }

    /**
     * @param widget
     */
    private void hideSelectionItem(final Widget widget) {
        if (!widget.getStyleName().contains(resourceBundle.workspaceLaunchStyle().hideSelectionItem())) {
            widget.addStyleName(resourceBundle.workspaceLaunchStyle().hideSelectionItem());
        }
    }

    private void setupPairedSuggest() {
        pairedSuggestBox = new DropDown<PairedSuggestion>() {
            /* (non-Javadoc)
             * @see com.ericsson.eniq.events.widgets.client.dropdown.DropDown#getPopupParent()
             */
            @Override
            protected FlowPanel getPopupParent() {
                // force to use RootPanel for popup
                return null;
            }
        };
        pairedSuggestBox.addValueChangeHandler(new ValueChangeHandler<PairedSuggestion>() {

            @Override
            public void onValueChange(final ValueChangeEvent<PairedSuggestion> event) {
                if (event.getValue() != null) {
                    secondaryLiveload.setDefaultText(" - Enter terminal - ");
                    secondaryLiveloadOracle.init(event.getValue().getLoadUrl(), event.getValue().getId());
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

    private void setupPrimaryLiveload() {
        primaryLiveloadOracle = new LiveLoadOracle(serviceProperties);
        primaryLiveload = new ExtendedSuggestBoxWithButton(primaryLiveloadOracle);
        primaryLiveload.getTextBox().addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(final BlurEvent blurEvent) {
                primaryLiveload.setText(windowState.getPrimarySelection());
            }
        });
        primaryLiveload.getTextBox().addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(final FocusEvent focusEvent) {
                primaryLiveload.setText("");
            }
        });
    }

    /**
     *  The values of this live load depend on the selection in the primary liveload. i.e. Terminal Make/Model
     */
    private void setupSecondaryLiveload() {
        secondaryLiveloadOracle = new WorkspacePairedLiveloadOracle(serviceProperties);
        secondaryLiveload = new ExtendedSuggestBoxWithButton(secondaryLiveloadOracle, suggestionDisplay);
        secondaryLiveload.getTextBox().addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(final BlurEvent blurEvent) {
                secondaryLiveload.setText(windowState.getSecondarySelection());
            }
        });
        secondaryLiveload.getTextBox().addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(final FocusEvent focusEvent) {
                primaryLiveload.setText("");
            }
        });
    }

    private void setupGroupSuggest() {
        groupSuggestBox = new DropDown<DimensionGroup>() {
            /* (non-Javadoc)
             * @see com.ericsson.eniq.events.widgets.client.dropdown.DropDown#getPopupParent()
             */
            @Override
            protected FlowPanel getPopupParent() {
                // force to use RootPanel for popup
                return null;
            }
        };
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
                final DimensionGroup group = event.getValue();
                groupElementPopup.setGroupElements(group.getElements());

                if (groupInfoToggleButton.isDown()) {

                    final int left = WorkspaceLaunchElementView.this.getAbsoluteLeft();
                    final int top = WorkspaceLaunchElementView.this.getAbsoluteTop();
                    final int width = groupSuggestBox.getOffsetWidth();
                    final int popupLeft = left + width;

                    groupElementPopup.setPopupPosition(popupLeft + 450, top - 47);
                    groupElementPopup.setHeader(selectedDimension.getGroupType() + " Group Information");
                    groupElementPopup.show();
                }
            }
        });
    }

    public boolean isEnabled() {
        return launchCheckBox.getValue();
    }

    public WindowState getWindowState() {
        /** Fill up the window state params **/
        return windowState;
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
                primarySelection = groupSuggestBox.getText();
            } else {
                primarySelection = primaryLiveload.getText();
            }
            break;
        case TEXT_ENTRY:
            primarySelection = textEntryBox.getText();
            break;

        case NO_SEARCH:
            break;
        }
        return primarySelection;
    }

    private String getPairedSelectionUrl() {
        if (selectedDimension.getSelectorType().equals(DimensionSelectorType.PAIRED_SEARCH)) {
            return pairedSuggestBox.getValue().getLoadUrl();
        }
        return "";
    }

    /**
     * Put the parameters from the view into the Window State object
     */
    public void updateWindowState() {
        windowState.setTimePeriod(timeSelectionDropDown.getValue().toString());
        final TimePeriodDropDownItem value = timeSelectionDropDown.getValue();
        final Date curDate = new Date();
        final TimePeriod period = value.getTimePeriod();
        final Date from = value.getFrom() == null ? new Date(curDate.getTime() - period.toMiliseconds()) : value
                .getFrom();
        final Date to = value.getTo() == null ? new Date() : value.getTo();
        windowState.setFrom(from);
        windowState.setTo(to);
        windowState.setPrimarySelection(getPrimarySelection());
        windowState.setPairedSelectionUrl(getPairedSelectionUrl());
        windowState.setSecondarySelection(secondaryLiveload.getText());
        windowState.setEnabled(true);
    }
}