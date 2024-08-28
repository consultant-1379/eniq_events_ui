/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.buttonenabling;

import com.ericsson.eniq.events.ui.client.common.comp.BaseToolBar;
import com.ericsson.eniq.events.ui.client.common.widget.IExtendedWidgetDisplay;
import com.ericsson.eniq.events.ui.client.datatype.ButtonEnableParametersDataType;
import com.ericsson.eniq.events.widgets.client.button.ImageButton;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.button.Button;

import java.util.HashMap;
import java.util.Map;

import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;

/**
 * Static utility to test if buttons should be enabled based on inputed conditions.
 * Ideally want this utility to check all buttons we have defined in the Map contained in this class
 * against the current toolbar on the window
 *
 * @author eeicmsy
 * @since Nov 2010
 *
 */
public abstract class ToolBarButtonManager {

    static Map<String, IButtonEnableConditions> buttonEnableMap = getButtonEnableMap();

    /*
     * Extracted for junit
     */
    static Map<String, IButtonEnableConditions> getButtonEnableMap() {
        if (buttonEnableMap == null) {
            buttonEnableMap = new HashMap<String, IButtonEnableConditions>();

            buttonEnableMap.put(BTN_KPI, new KPIButtonEnableConditions());
            buttonEnableMap.put(BTN_KPI_CS, new KPIButtonEnableConditions());
            buttonEnableMap.put(BTN_SAC, new SACButtonEnableConditions());
            buttonEnableMap.put(BTN_PROPERTIES, new PropertiesButtonEnableConditions());
            buttonEnableMap.put(BTN_SUBSCRIBER_DETAILS, new SubscriberDetailsButtonEnableConditions());
            buttonEnableMap.put(BTN_SUBSCRIBER_DETAILS_PTMSI, new SubscriberDetailsButtonEnableConditions());
            buttonEnableMap.put(BTN_SUBSCRIBER_DETAILS_CS, new SubscriberDetailsButtonEnableConditions());
            buttonEnableMap.put(BTN_SUBSCRIBER_DETAILS_MSISDN_CS, new SubscriberDetailsButtonEnableConditions());
            buttonEnableMap.put(BTN_SUBSCRIBER_DETAILS_WCDMA_CFA, new SubscriberDetailsButtonEnableConditions());

            buttonEnableMap.put(BTN_RECUR_ERROR, new RecurErrorButtonEnableConditions());
            buttonEnableMap.put(BTN_EXPORT_BUTTON, new GenericButtonEnableConditions());
            buttonEnableMap.put(BTN_LEGEND, new GenericButtonEnableConditions());
            buttonEnableMap.put(BTN_GRAPH_TO_GRID_TOGGLE, new GenericButtonEnableConditions());
        }
        return buttonEnableMap;
    }

    /**
     * Handle button enabling for all buttons on the toolbar based on the conditions.
     * Handles enabling of any toolbar button defined in this class (Map), if the buttons are
     * found on this toolbar
     *
     *
     * @param display           - window containing the toolbar (buttons)
     * @param baseToolbar       - base toolbar containing the buttons
     * @param currentSettings   - current settings for gird and search field, etc that are needed to determine the
     *                            enabling status of a button
     */
    public static void handleToolbarButtonEnabling(final IExtendedWidgetDisplay display, final BaseToolBar baseToolbar,
            final ButtonEnableParametersDataType currentSettings) {

        for (final String buttonId : buttonEnableMap.keySet()) {
            // check if meta data define this button for this toolbar
            final Component component = baseToolbar.getItemByItemId(buttonId);
            if (component != null) {
                // TODO It is complete disaster, remove once ImageButtons are implemented throughout the application
                if (component instanceof Button) {
                    currentSettings.isCurrentlyEnabled = component.isEnabled();
                } else if (component instanceof WidgetComponent) {
                    final WidgetComponent widgetComponent = (WidgetComponent) component;

                    final ImageButton button = (ImageButton) widgetComponent.getWidget();
                    currentSettings.isCurrentlyEnabled = button.isEnabled();
                }

                display.setToolbarButtonEnabled(buttonId, shouldEnableButton(buttonId, currentSettings));
            }
        }
    }

    /**
     * Utility to check what a toolbar buttons enabling status should be assuming a set of conditions has been created
     * for the passed button id. 
     * 
     * @param buttonId         - known button id - refer to map in this class
     * @param currentSettings  - current settings for gird and search field, etc that are needed to determine the 
     *                           enabling status of a button
     * @return                 - true if the button should be set enabled based on the currentSettings passed (when checked against 
     *                           the conditions set by the concrete implementation  of IButtonEnableConditions assosicated with the passed 
     *                           button id)
     */
    public static boolean shouldEnableButton(final String buttonId, final ButtonEnableParametersDataType currentSettings) {

        final IButtonEnableConditions conditions = buttonEnableMap.get(buttonId);
        if (conditions != null) {
            return conditions.shouldEnableButton(currentSettings);
        }
        return false;
    }

}
