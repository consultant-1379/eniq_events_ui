/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.workspace.launchwindowconfig;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.common.widget.window.EniqWindowGwt;
import com.ericsson.eniq.events.ui.client.workspace.IWindowContainer;
import com.ericsson.eniq.events.ui.client.workspace.WorkspaceUtils;
import com.ericsson.eniq.events.ui.client.workspace.launchwindowconfig.datatype.ConfigLaunchType;
import com.ericsson.eniq.events.ui.client.workspace.launchwindowconfig.datatype.LaunchTypeMenuItem;
import com.ericsson.eniq.events.widgets.client.button.ImageButton;
import com.ericsson.eniq.events.widgets.client.mask.MaskHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationDialogView extends BaseView<ConfigurationDialogPresenter> {

    @UiField(provided = true)
    static final ConfigurationDialogResourceBundle resourceBundle;

    static {
        resourceBundle = GWT.create(ConfigurationDialogResourceBundle.class);
        resourceBundle.style().ensureInjected();
    }

    private static final String DEFAULT_HEIGHT = "140px";

    private static final String DEFAULT_WIDTH = "327px";

    private static int NUM_OPTIONS;

    public static final int CONFIG_PANEL_HEIGHT = 107;

    public static final int EMPTY_WINDOW_HEIGHT = 81;

    private int numWindowsSelected = 0;

    private final MaskHelper maskHelper;

    private int left = 0;

    private int top = 0;

    interface WorkspaceLaunchDialogViewUiBinder extends UiBinder<Widget, ConfigurationDialogView> {
    }

    private static WorkspaceLaunchDialogViewUiBinder uiBinder = GWT.create(WorkspaceLaunchDialogViewUiBinder.class);

    @UiField
    Button launchBtn;

    @UiField
    Button cancelBtn;

    @UiField
    FlowPanel bodyPanel;

    @UiField
    Label statusLabel;

    @UiField
    Image infoIcon;

    private FlexTable contentGrid;

    private final ConfigurationDialogWindow window;

    private List<LaunchTypeMenuItem> options;

    @UiHandler("launchBtn")
    public void onLaunchClicked(@SuppressWarnings("unused") final ClickEvent event) {
        maskWindow();
        enableButtons(false);

        /* to get loading mask rendered .... */
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                getPresenter().onLaunchWindows();
                window.hide();
                unmaskWindow();
            }
        });
    }

    private void maskWindow() {
        maskHelper.mask(bodyPanel.getElement(), "Loading Windows", bodyPanel.getParent().getOffsetHeight());
    }

    private void unmaskWindow() {
        maskHelper.unmask();
    }

    private void enableButtons(boolean enable) {
        launchBtn.setEnabled(enable);
        cancelBtn.setEnabled(enable);
    }

    @UiHandler("cancelBtn")
    public void onCancelClicked(@SuppressWarnings("unused") final ClickEvent event) {
        window.hide();
        getPresenter().unbind();
    }

    public ConfigurationDialogView() {

        maskHelper = new MaskHelper();
        initWidget(uiBinder.createAndBindUi(this));
        setUpOptions();

        window = new ConfigurationDialogWindow(new EniqWindowGwt());
        window.add(this.asWidget());
        window.setGlassEnabled();
        window.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        window.getWindowContentPanel().getElement().getStyle().setBottom(1, Style.Unit.PX);
        window.setShadow(false);
        window.setResizable(false);
        window.setPosition(this.left, this.top);
        window.getCloseButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                window.hide();
            }
        });
    }

    private void setUpOptions() {
        NUM_OPTIONS = ConfigLaunchType.values().length;
        options = new ArrayList<LaunchTypeMenuItem>(ConfigLaunchType.values().length);
        for (final ConfigLaunchType configLaunchType : ConfigLaunchType.values()) {
            options.add(new LaunchTypeMenuItem(configLaunchType));
        }
    }

    public void launch(final IWindowContainer windowContainer) {
        showConfigWindow(windowContainer);
        addCleanGrid();
        resetButtons();
        addConfigurationPanel();
        window.putToFront();
    }

    private void resetButtons() {
        numWindowsSelected = 0;
        enableButtons(true);
        updateLaunchButtonText();
    }

    private void addCleanGrid() {
        contentGrid = new FlexTable();//max 4 rows & 2 cols right now
        contentGrid.setBorderWidth(0);
        contentGrid.setCellPadding(0);
        contentGrid.setCellSpacing(0);
        contentGrid.setStyleName(resourceBundle.style().gridStyle());
        bodyPanel.add(contentGrid);
    }

    private void showConfigWindow(IWindowContainer windowContainer) {
        bodyPanel.clear();
        window.setContainer(windowContainer.getWindowContainerPanel());
        windowContainer.getWindowContainerPanel().add(window.asWidget());
        windowContainer.getWindowContainerPanel().layout();
        window.getElement().getStyle().setTop(getPresenter().getTop(), Style.Unit.PX);
        window.getElement().getStyle().setLeft(getPresenter().getLeft(), Style.Unit.PX);
    }

    private void addConfigurationPanel() {
        if (getPresenter().getConfigWidgets().size() != NUM_OPTIONS) { //limit to 4 config windows in view
            createConfigPanel(options);
            launchBtn.setFocus(true);
        }
    }

    private void createConfigPanel(List<LaunchTypeMenuItem> options) {
        //first column
        ConfigWidgetView configWidget = new ConfigWidgetView(options, getPresenter().getConfigWidgets().size(), this);
        contentGrid.setWidget(getPresenter().getConfigWidgets().size(), 0, configWidget);

        //second column  plus/minus buttons
        FlowPanel buttonsPanel = new FlowPanel();
        ImageButton minus = createMinusButton();
        ImageButton plus = createPlusButton();
        buttonsPanel.add(plus);
        buttonsPanel.add(minus);
        contentGrid.setWidget(getPresenter().getConfigWidgets().size(), 1, buttonsPanel);

        getPresenter().getConfigWidgets().put(minus, configWidget);//add to list to track them
        updateButtons();
        updateWindowSize();
        changeStatusIcon(getPresenter().checkDuplicateValues());
    }

    private void updateWindowSize() {
        int windowHeight = EMPTY_WINDOW_HEIGHT + (getPresenter().getConfigWidgets().size() * CONFIG_PANEL_HEIGHT);
        window.setHeight(windowHeight + "px");
    }

    private ImageButton createPlusButton() {
        ImageButton plusButton = new ImageButton(resourceBundle.plusButton());
        plusButton.setDisabledImage(resourceBundle.plusButtonDisable());
        plusButton.setHoverImage(resourceBundle.plusButtonHover());
        plusButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                addConfigurationPanel();
            }
        });

        if (getPresenter().getConfigWidgets().size() == NUM_OPTIONS - 1) { //not added ours just yet, max is 4!
            plusButton.setEnabled(false);
        }
        plusButton.getElement().getStyle().setMargin(20, Style.Unit.PX);
        return plusButton;
    }

    private ImageButton createMinusButton() {
        ImageButton minusButton = new ImageButton(resourceBundle.minusButton());
        minusButton.setDisabledImage(resourceBundle.minusButtonDisable());
        minusButton.setHoverImage(resourceBundle.minusButtonHover());
        minusButton.getElement().setId(WorkspaceUtils.generateId());
        minusButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                removeConfigurationPanel((ImageButton) clickEvent.getSource());
            }
        });
        minusButton.getElement().getStyle().setMargin(20, Style.Unit.PX);
        return minusButton;
    }

    private void removeConfigurationPanel(ImageButton source) {
        for (int rowNum = 0; rowNum < contentGrid.getRowCount(); rowNum++) {
            if (source == ((FlowPanel) contentGrid.getWidget(rowNum, 1)).getWidget(1)) {
                getPresenter().getConfigWidgets().get(source).resetCheckBoxes();
                getPresenter().getConfigWidgets().remove(source);//remove from list
                contentGrid.removeRow(rowNum); //remove from grid!!
                break;
            }
        }
        updateWindowSize();
        updateButtons();
        changeStatusIcon(getPresenter().checkDuplicateValues());
    }

    /* todo move some of this logic to presenter */
    /* Can only add from last row-> only last row should have enabled plus button */
    /* Can remove row from anywhere - unless we have only 1, minus should be disabled. */
    private void updateButtons() {
        for (int row = 0; row < getPresenter().getConfigWidgets().size(); row++) {
            ImageButton plusButton = (ImageButton) ((FlowPanel) contentGrid.getWidget(row, 1)).getWidget(0);
            if (row == getPresenter().getConfigWidgets().size() - 1 && row != NUM_OPTIONS - 1) {//if it is  the last or only plus button enable
                plusButton.setEnabled(true);
            } else {
                plusButton.setEnabled(false);
            }
        }

        //if we only have one config panel - don not allow user to remove it, disable "-" button!
        if (getPresenter().getConfigWidgets().size() == 1) {
            ImageButton b = (ImageButton) ((FlowPanel) contentGrid.getWidget(0, 1)).getWidget(1);
            b.setEnabled(false);
        } else {
            ImageButton b = (ImageButton) ((FlowPanel) contentGrid.getWidget(0, 1)).getWidget(1);
            b.setEnabled(true);
        }
    }

    public void setWindowTitle(String title) {
        window.setHeading(title);
    }

    public void incrementWindowCount() {
        numWindowsSelected++;
        updateLaunchButtonText();
    }

    public void decrementWindowCount() {
        numWindowsSelected--;
        updateLaunchButtonText();
    }

    public boolean checkSelectionForDuplicates() {
        return getPresenter().checkDuplicateValues();
    }

    void updateLaunchButtonText() {
        launchBtn.setText("Launch (" + numWindowsSelected + ")");
        statusLabel.setText(numWindowsSelected + " Analysis Window(s) selected");
        if (numWindowsSelected < 1) {
            launchBtn.setEnabled(false);
        } else {
            launchBtn.setEnabled(true);
        }
        changeStatusIcon(false);
    }

    public void changeStatusIcon(boolean warningRequired) {
        if (warningRequired) {
            launchBtn.setEnabled(false);
            launchBtn.addStyleName(resourceBundle.style().warningButton());
            infoIcon.setResource(resourceBundle.warningIcon());
            infoIcon.getElement().getStyle().setMarginLeft(45, Style.Unit.PX);
            statusLabel.setText("Selection Error - duplicate windows selected");
        } else {
            launchBtn.removeStyleName(resourceBundle.style().warningButton());
            infoIcon.setResource(resourceBundle.infoIcon());
            infoIcon.getElement().getStyle().setMarginLeft(70, Style.Unit.PX);
        }
    }
}
