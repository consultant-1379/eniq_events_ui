/**
 * Ericsson Password TextBox
 */
package com.ericsson.eniq.events.ui.login.client.component.einputbox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author egallou
 * @since May 2011
 */

public class EPasswordTextBox extends Composite {

    /*
     * Text Box for actual masked password
     */
    private ETextBox passwordTexBox;

    /*
     * TextBox for Displaying user prompt 
     */
    private ETextBox promptHolder;

    /*
     * User Prompt to be displayed in PasswordTextBox
     */
    private String prompt;

    /*
     * Panel to hold both text boxes
     */
    private AbsolutePanel passwordPanel;

    private static EInputBoxResources resources;

    static {
        resources = GWT.create(EInputBoxResources.class);
        resources.css().ensureInjected();
    }

    /**
     * Constructor: Create PasswordTextBox (no prompt)
     */
    public EPasswordTextBox() {
        initComponents(false);
    }

    /**
     * Constructor: Create PasswordTextBox with user prompt
     * @param userPrompt Text to prompt user
     */
    @UiConstructor
    public EPasswordTextBox(final String userPrompt) {
        initComponents(true);
        setUserPrompt(userPrompt); //NOPMD
        setEventHandlers();
    }

    /*
     * Add the prompt text to the prompt holding TextBox
     */
    public void setUserPrompt(final String userPrompt) {
        prompt = userPrompt;
        promptHolder.setPrompt(prompt);
    }

    /*
     * Initialise components for PaswordTextBox - the password and the prompt holder
     * must be displayed directly over each other
     */
    private void initComponents(final boolean usePrompt) {

        passwordTexBox = new ETextBox();
        passwordTexBox.getElement().setPropertyString("type", "password");

        promptHolder = new ETextBox();

        passwordPanel = new AbsolutePanel();
        passwordPanel.getElement().getStyle().setOverflow(Style.Overflow.VISIBLE);
        passwordPanel.add(passwordTexBox);
        passwordPanel.add(promptHolder);

        if (usePrompt) {
            passwordTexBox.setVisible(false);
            promptHolder.setVisible(true);
        }

        initWidget(passwordPanel);
    }

    /*
     * Add event handlers to textBoxes to deal with hiding and showing user prompts
     */
    private void setEventHandlers() {
        promptHolder.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(final FocusEvent event) {
                onPromptfocus();
            }
        });

        passwordTexBox.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(final BlurEvent event) {
                onPasswordBlur();
            }
        });

    }

    /*
     * When the PromptHolder text box gets focus - set focus to password box and hide prompt
     */
    private void onPromptfocus() {
        promptHolder.setVisible(false);
        passwordTexBox.setVisible(true);
        passwordTexBox.setFocus(true);
    }

    /*
     * When the password box loses focus - if its empty show prompt
     */
    private void onPasswordBlur() {

        if (passwordTexBox.getText().equalsIgnoreCase(null) || passwordTexBox.getText().equalsIgnoreCase("")) {
            showPrompt();
        }
    }

    private void showPrompt() {
        promptHolder.setVisible(true);//password box
        promptHolder.showPrompt();
        passwordTexBox.setVisible(false);
    }

    public void clear() {
        this.setText("");
        this.showPrompt();
    }

    /**
     * Check if Password Text box has prompt enabled, never returns the prompt
     */
    public String getText() {
        return passwordTexBox.getText();

    }

    /**
     * @param text Text to set in PasswordBox (masked value)
     */
    public void setText(final String text) {
        passwordTexBox.setText(text);
    }

    /**
     * @param tabIndex
     */
    public void setTabIndex(final int tabIndex) {
        if (promptHolder != null) {
            promptHolder.setTabIndex(tabIndex);
        } else {
            passwordTexBox.setTabIndex(tabIndex);
        }
    }

    /**
     * @param enabled
     */
    public void setEnabled(final boolean enabled) {
        passwordTexBox.setEnabled(enabled);

        if (promptHolder != null) {
            promptHolder.setEnabled(enabled);
        }
    }

    /**
     * @param keyUpHandler Add event handler for KeyUp
     */
    public HandlerRegistration addKeyUpHandler(final KeyUpHandler keyUpHandler) {
        return passwordTexBox.addKeyUpHandler(keyUpHandler);
    }

    /**
     * @param setFocus
     */
    public void setFocus(final boolean setFocus) {
        if (promptHolder != null) {
            promptHolder.setFocus(setFocus);
        } else {
            passwordTexBox.setFocus(setFocus);
        }

    }

}
