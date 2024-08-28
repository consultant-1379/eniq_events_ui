/**
 * Ericsson TextBox for EWCL
 * Coding and Style Setting for Prompt Text Box included as IE does not support Placement tag
 */
package com.ericsson.eniq.events.ui.login.client.component.einputbox;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;

/**
 * @author egallou
 * @since May 2011
 */
public class ETextBox extends AbstractEInputBoxBase implements FocusHandler, BlurHandler {

    public ETextBox() {
        super();
    }

    /**
     * @param element
     */
    public ETextBox(final Element element) {
        super(element);
    }

    /**
     * @param prompt
     */
    public ETextBox(final String prompt) {
        super(prompt);
    }

    /**
     * @param event
     * Check if text box has prompt enabled when it loses focus, will restore prompt
     */
    @Override
    public void onBlur(final BlurEvent event) {
        if (super.getText().equals("")) {

            showPrompt();
        }
    }

    /**
     * @param event
     * Check if text box has prompt enabled when it gets focus, clears prompt
     */
    @Override
    public void onFocus(final FocusEvent event) {
        if (promptText != null && super.getText().equalsIgnoreCase(promptText)) {

            hidePrompt();
        }
    }

    /**
     * Check if text box has prompt enabled, never return the prompt
     */
    @Override
    public String getText() {
        String text;

        if (promptText != null && promptText.equals(super.getText())) {
            text = "";
        } else {
            text = super.getText();
        }

        return text;
    }
}
