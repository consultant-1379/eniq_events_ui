/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2013 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launchwindowconfig;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.workspace.launchwindowconfig.datatype.LaunchTypeMenuItem;
import com.ericsson.eniq.events.widgets.client.dropdown.DropDownMenu;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * @author egallou
 * @since 06/2013
 */
public class ConfigWidgetView extends BaseView<ConfigWidgetPresenter> {

    @UiField
    DropDownMenu<LaunchTypeMenuItem> dropDownMenu;

    @UiField
    CheckBox drops;

    @UiField
    CheckBox setup;

    public boolean isDuplicates() {
        return duplicates;
    }

    private boolean duplicates = false;

    ConfigurationDialogView parentDialog;


    interface ConfigWidgetViewUiBinder extends UiBinder<Widget, ConfigWidgetView> {
    }

    private static ConfigWidgetViewUiBinder uiBinder = GWT.create(ConfigWidgetViewUiBinder.class);

    public ConfigWidgetView(List<LaunchTypeMenuItem> configOptions, int selectedOption, ConfigurationDialogView configurationDialogView) {
        this.parentDialog = configurationDialogView;

        initWidget(uiBinder.createAndBindUi(this));
        setupEventHandlers();
        addOptions(configOptions, selectedOption);
    }

    private void setupEventHandlers() {
        dropDownMenu.addValueChangeHandler(new ValueChangeHandler<LaunchTypeMenuItem>() {
            @Override
            public void onValueChange(ValueChangeEvent<LaunchTypeMenuItem> launchTypeMenuItemValueChangeEvent) {
                duplicates = parentDialog.checkSelectionForDuplicates();
                if (!duplicates) {
                    if (!setup.getValue()) reSelectCheckBoxes(setup);
                    if (!drops.getValue()) reSelectCheckBoxes(drops);
                }
            }
        });

        drops.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                updateWindowCount(booleanValueChangeEvent.getValue());
            }
        });

        setup.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                updateWindowCount(booleanValueChangeEvent.getValue());
            }
        });
    }

    private void updateWindowCount(Boolean checkBoxValue) {
        if (!duplicates) {
            updateWindowValues(checkBoxValue);
        }
    }

    private void updateWindowValues(Boolean checkBoxValue) {
        if (checkBoxValue) {
            parentDialog.incrementWindowCount();
        } else parentDialog.decrementWindowCount();
    }

    private void addOptions(List<LaunchTypeMenuItem> configOptions, int selectedOption) {
        dropDownMenu.update(configOptions);
        dropDownMenu.setValue(configOptions.get(selectedOption));//first one in list set as default

        reSelectCheckBoxes(drops);
        reSelectCheckBoxes(setup);
    }

    private void reSelectCheckBoxes(CheckBox checkBox) {
        checkBox.setValue(true);
        updateWindowCount(true);
    }

    public CheckBox getDrops() {
        return drops;
    }

    public CheckBox getSetup() {
        return setup;
    }

    public LaunchTypeMenuItem getSelectedLaunchType() {
        return dropDownMenu.getValue();
    }

    public void resetCheckBoxes() {
        if (setup.getValue()) {
            updateWindowValues(false);//check box has been counted - remove it
        }
        if (drops.getValue()) {
            updateWindowValues(false);//check box has been counted - remove it
        }
    }

}
