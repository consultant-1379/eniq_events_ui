package com.ericsson.eniq.events.ui.login.client.component.einputbox;

import com.ericsson.eniq.events.ui.login.client.Login;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextBox;

public abstract class AbstractEInputBoxBase extends TextBox implements FocusHandler, BlurHandler {

    private static final String CLASSNAME_CSS = "EInputBox";

    //instantiate styling resources 
    private static EInputBoxResources resources;

    static {
        resources = GWT.create(EInputBoxResources.class);
        resources.css().ensureInjected();
    }

    protected transient String promptText;

    private transient HandlerRegistration blurEventHandler;

    private transient HandlerRegistration focusEventHandler;

    public AbstractEInputBoxBase() {
        super();
        init();

    }

    /**
     * @param element
     */
    public AbstractEInputBoxBase(final Element element) {
        super(element);
        init();
    }

    /**
     * @param prompt
     */
    public AbstractEInputBoxBase(final String prompt) {
        super();

        setPrompt(prompt);//NOPMD the over-rideable method is public but is also final
        init();

    }

    private void init() {
        disableBrowserSpellCheck();
        setStylePrimaryName(CLASSNAME_CSS);//set the primary styles not theme dependent  
        addStyleDependentName(Login.CSS_THEME_LIGHT);
    }

    /**Disable the spell check on text boxes in Chrome**/
    private void disableBrowserSpellCheck() {
        getElement().setAttribute("spellCheck", "false");
    }

    /**
     * Set Style for prompt text
     */
    protected void setPromptStyle() {
        addStyleDependentName("prompt");
    }

    /**
     * Remove Prompt Style from textBox
     */
    protected void removePromptStyle() {
        removeStyleDependentName("prompt");
    }

    /**
     * @param prompt: Prompt string to be displayed
     * Adds the prompt text to a text box
     */
    public final void setPrompt(final String prompt) {
        promptText = prompt;

        if ((promptText == null) || (promptText.equals(""))) {
            blurEventHandler.removeHandler();
            focusEventHandler.removeHandler();
        } else {
            blurEventHandler = addBlurHandler(this);
            focusEventHandler = addFocusHandler(this);
            addStyledPrompt(promptText);
        }
    }

    /*
     * @param prompt
     * Adds the prompt text with prompt style to text box
     */
    private void addStyledPrompt(final String prompt) {
        final String content = getText();

        if ((content.length() == 0) || (!content.equalsIgnoreCase(prompt))) {
            setText(prompt);
            setPromptStyle();
        }
    }

    /**
     * Display Prompt to User
     */
    public void showPrompt() {
        setPromptStyle();
        this.setText(promptText);
    }

    /**
     * Remove Prompt from TextBox
     */
    public void hidePrompt() {
        removePromptStyle();
        this.setText("");
    }
}
