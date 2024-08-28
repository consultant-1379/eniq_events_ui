/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.login.client.mvp;

import java.util.LinkedList;
import java.util.List;

import com.ericsson.eniq.events.ui.login.client.Login;
import com.ericsson.eniq.events.ui.login.client.component.EIconButton;
import com.ericsson.eniq.events.ui.login.client.component.EIconButton.ButtonType;
import com.ericsson.eniq.events.ui.login.client.component.EProgressBar;
import com.ericsson.eniq.events.ui.login.client.component.EProgressBar.ILabelFormatter;
import com.ericsson.eniq.events.ui.login.client.component.ETabPanel;
import com.ericsson.eniq.events.ui.login.client.component.ETeardropPopup;
import com.ericsson.eniq.events.ui.login.client.component.einputbox.EPasswordTextBox;
import com.ericsson.eniq.events.ui.login.client.component.einputbox.ETextBox;
import com.ericsson.eniq.events.ui.login.client.component.interfaces.ICollapsible;
import com.ericsson.eniq.events.ui.login.client.mvp.LoginPresenter.ILoginViewActionCallback;
import com.ericsson.eniq.events.ui.login.client.window.ESplashWindow;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * @author ecarsea
 * @since 2011
 */
public class LoginView implements LoginPresenter.ILoginView {

    /**
     * 
     */
    private static final int CHANGE_PASSWORD_SUCCESS_DELAY = 3000;

    interface Binder extends UiBinder<HTMLPanel, LoginView> {
    }

    Binder uiBinder;

    @UiField
    HTMLPanel upperSplashPanel;

    @UiField
    HTMLPanel lowerSplashPanel;

    @UiField
    HTMLPanel tradeMarkPanel;

    @UiField
    HTMLPanel eniqPanel;

    /*@UiField
    HTMLPanel globesPanel;*/

    @UiField
    HTMLPanel windowContent;

    @UiField
    FlowPanel upperSplashContainer;

    @UiField
    EProgressBar progressBar;

    ICollapsible collapsiblePanel;

    @UiField
    ETabPanel tabPanel;

    @UiField
    ETextBox usernameField;

    @UiField
    EPasswordTextBox passwordField;

    @UiField
    EIconButton loginButton;

    @UiField
    Label loginMessageLabel;

    @UiField
    Label capsLockOn1;

    @UiField
    ETextBox usernameChangePassword;

    @UiField
    EPasswordTextBox oldPassword;

    @UiField
    EPasswordTextBox newPassword;

    @UiField
    EPasswordTextBox confirmPassword;

    @UiField
    EIconButton loginButtonChangePassword;

    @UiField
    EIconButton loginButtonOldPasswordEntered;

    @UiField
    HTML changePasswordMessageLabel;

    @UiField
    HTML passwordHelpIndicator;

    @UiField
    Label capsLockOn2;

    @UiField
    Label capsLockOn3;

    @UiField
    HTMLPanel passwordEntryForm;

    @UiField
    Label navigationPage1;

    @UiField
    Label navigationPage2;

    private boolean shiftOn;

    @UiField
    FlowPanel lowerSplashContainer;

    @UiField
    SimplePanel changePasswordPageSeparator;

    @UiField
    ParagraphElement versionLabel;

    @UiField
    ParagraphElement copyrightLabel;

    private final ESplashWindow window;

    private String progressBarText = "";

    private int selectedTabIndex;

    private final List<Label> capsLockIndicators;

    private final ETeardropPopup popup;

    /** Native Handlers for detecting Upper Case Keys **/
    private final KeyDownHandler shiftDownHandler = new KeyDownHandler() {
        @Override
        public void onKeyDown(final KeyDownEvent e) {
            if (e.getNativeKeyCode() == KeyCodes.KEY_SHIFT) {
                shiftOn = true;
            }
        }
    };

    private final KeyUpHandler shiftUpHandler = new KeyUpHandler() {
        @Override
        public void onKeyUp(final KeyUpEvent e) {
            if (e.getNativeKeyCode() == KeyCodes.KEY_SHIFT) {
                shiftOn = false;
            }
        }
    };

    /**
     *
     */
    private final KeyPressHandler capsLockHandler = new KeyPressHandler() {
        @Override
        public void onKeyPress(final KeyPressEvent e) {
            final char charCode = e.getCharCode();
            if ((charCode >= 65 && charCode <= 90) && !shiftOn) {
                showCapsLockOn();
            } else if ((charCode >= 97 && charCode <= 122) && shiftOn) {
                showCapsLockOn();
            } else {
                hideCapsLockOn();
            }
        }
    };

    private ILoginPresenter presenter;

    /** Other Handlers for UIBinder Elements **/
    @UiHandler(value = { "usernameField", "passwordField" })
    public void onKeyUpUserName(final KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            onLoginSubmit();
        }
    }

    @UiHandler("confirmPassword")
    public void onConfirmPasswordKeyUp(final KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            changePasswordSubmit();
        }
    }

    @UiHandler("newPassword")
    public void onNewPasswordKeyUp(@SuppressWarnings("unused")
    final KeyUpEvent event) {
        presenter.checkPasswordStrength(oldPassword.getText(), newPassword.getText());
    }

    @UiHandler("loginButton")
    public void onLoginClick(@SuppressWarnings("unused")
    final ClickEvent clickEvent) {
        //logToConsole("login button click : " + new Date().getTime());
        onLoginSubmit();
    }

    private static native void logToConsole(String message) /*-{
		if (console != null) {
			console.log(message);
		}
    }-*/;

    @UiHandler("passwordHelpIndicator")
    public void onHelpClicked(@SuppressWarnings("unused")
    final ClickEvent clickEvent) {

        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            @Override
            public void setPosition(final int offsetWidth, final int offsetHeight) {
                popup.setPopupPosition(passwordHelpIndicator.getAbsoluteLeft() - ETeardropPopup.LEFT_OFFSET,
                        passwordHelpIndicator.getAbsoluteTop() - ETeardropPopup.TOP_OFFSET);
            }
        });
    }

    @UiHandler("loginButtonChangePassword")
    public void onPasswordChangeClick(@SuppressWarnings("unused")
    final ClickEvent clickEvent) {
        changePasswordSubmit();
    }

    /**
     * 
     */
    private void changePasswordSubmit() {
        changePasswordMessageLabel.setText("");
        if (isPasswordChangeInputValid()) {
            presenter.onPasswordChange();
        }
    }

    @UiHandler("loginButtonOldPasswordEntered")
    public void onLoginChangePasswordClicked(@SuppressWarnings("unused")
    final ClickEvent clickEvent) {
        onOldUserNamePasswordSubmit();
    }

    @UiHandler(value = { "usernameChangePassword", "oldPassword" })
    public void onLoginChangePasswordKeyUp(final KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            onOldUserNamePasswordSubmit();
        }
    }

    @UiHandler("tabPanel")
    public void onTabSelection(final SelectionEvent<Integer> event) {
        selectedTabIndex = event.getSelectedItem();
        if (selectedTabIndex == 0) {
            passwordEntryForm.removeStyleDependentName("show");
            usernameChangePassword.showPrompt();
            oldPassword.clear(); //TODO make uniform
            newPassword.clear();
            confirmPassword.clear();
            setLoginOperationComplete();
            changePasswordMessageLabel.setText("");
            passwordHelpIndicator.getElement().getStyle().setDisplay(Display.NONE);
            navigationPage1.setStylePrimaryName("navigationOn");
            navigationPage2.setStylePrimaryName("navigationOff");
        }
        enableTabIndexNewPasswordEntryPage(false);
    }

    @Inject
    public LoginView(final LoginResources resources) {
        resources.css().ensureInjected();
        window = new ESplashWindow(true) {
            @Override
            public void onLoad() {
                super.onLoad();

                progressBarText = "Loading...";
                final RepeatingCommand command = new RepeatingCommand() {

                    @Override
                    public boolean execute() {
                        progressBar.incrementProgress();
                        if (progressBar.isProgressCompleted()) {
                            progressBar.setText("User Action Required");
                            enableLoginView();
                            return false;
                        }
                        return true;
                    }
                };
                Scheduler.get().scheduleFixedDelay(command, 5);
            }
        };

        window.setStylePrimaryName("loginWindow");
        /** Bind the UIBinder to this class **/
        uiBinder = GWT.create(Binder.class);
        windowContent = uiBinder.createAndBindUi(this);

        /** Group the caps lock indicators for ease of use **/
        capsLockIndicators = new LinkedList<Label>() {
            {
                add(capsLockOn1);
                add(capsLockOn2);
                add(capsLockOn3);
            }
        };

        popup = new ETeardropPopup(new HTML("<p>Password Rules<br/>" + "Must be 8 characters in length<br/>"
                + "Must have at least one uppercase letter<br/>" + "Must have at least one digit<br/>"
                + "Must not have 3 characters from previous password<br/></p>"));
        popup.setAnimationEnabled(true);

        /** Set up the Themeable sub components of this view, and set the default theme.
         * Register ourselves with the Theme manager so we will be notified of a theme change **/
        createThemableComponentList();
        applyStyle(Login.CSS_THEME_LIGHT);

        /** Add our handlers for detecting Upper Case entry of letters **/
        tabPanel.addDomHandler(shiftDownHandler, KeyDownEvent.getType());
        tabPanel.addDomHandler(shiftUpHandler, KeyUpEvent.getType());
        tabPanel.addDomHandler(capsLockHandler, KeyPressEvent.getType());

        tabPanel.selectTab(0);
        /** Dont want the tabs to be involved in tab indexing so as we can tab straight to our text boxes **/
        tabPanel.disableTabIndex();

        /** Format of our progress bar label while its updating **/
        progressBar.setLabelFormatter(new ILabelFormatter() {

            @Override
            public String formatLabel(final EProgressBar bar, final double percent) {
                return progressBarText + " " + (int) percent + "%";
            }
        });

        /** Configure the window. Adding 3 rows to cater for one of them being collapsible. Need to add Top Panel Last **/
        collapsiblePanel = window.createCollapsibleSplashPanel(upperSplashContainer, lowerSplashContainer, tabPanel);

        copyrightLabel.setInnerText(InputElement.as(RootPanel.get("appCopyright").getElement()).getValue());
        final String versionText = "Version: " + InputElement.as(RootPanel.get("appVersion").getElement()).getValue();
        versionLabel.setInnerText(versionText);
    }

    @Override
    public Widget asWidget() {
        return window;
    }

    /**
     * Set Message. Defaults to error message
     */
    @Override
    public void setMessage(final String text) {
        this.setMessage(text, true);
    }

    @Override
    public void setMessage(final String text, final boolean error) {
        if (selectedTabIndex == 0) {
            setMessageStyle(loginMessageLabel, error);
            loginMessageLabel.setText(text);
            changePasswordMessageLabel.setText("");

        } else {
            setMessageStyle(changePasswordMessageLabel, error);
            loginMessageLabel.setText("");
            changePasswordMessageLabel.setText(text);
        }
    }

    private void setMessageStyle(final Label label, final boolean error) {
        final String errorStyleDependentName = "error-" + Login.CSS_THEME_LIGHT;
        label.removeStyleDependentName(errorStyleDependentName);
        if (error) {
            label.addStyleDependentName(errorStyleDependentName);
        }
    }

    @Override
    public String getUserId() {
        if (selectedTabIndex == 0) {
            return usernameField.getText();
        }
        return usernameChangePassword.getText();
    }

    @Override
    public void setUserId(final String userId) {
        usernameField.setText(userId);
    }

    @Override
    public String getPassword() {
        if (selectedTabIndex == 0) {
            return passwordField.getText();
        }
        return oldPassword.getText();
    }

    @Override
    public void setPassword(final String password) {
        passwordField.setText(password);
    }

    @Override
    public void enableLoginView() {
        expandWindow();
        setLoginFormEnabled(true);
        setPasswordChangeFormEnabled(true);
    }

    private void setLoginFormEnabled(final boolean enabled) {
        usernameField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        loginButton.setEnabled(enabled);
        tabPanel.getTabBar().setTabEnabled(1, enabled);
    }

    @Override
    public void disableLoginView() {
        setLoginFormEnabled(false);
        setPasswordChangeFormEnabled(false);
    }

    private void setPasswordChangeFormEnabled(final boolean enabled) {
        usernameChangePassword.setEnabled(enabled);
        oldPassword.setEnabled(enabled);
        newPassword.setEnabled(enabled);
        confirmPassword.setEnabled(enabled);
        loginButtonChangePassword.setEnabled(enabled);
        tabPanel.getTabBar().setTabEnabled(0, enabled);
    }

    @Override
    public void setPasswordStrengthTip(final String strengthTip) {
        loginMessageLabel.setText("");
        setMessageStyle(changePasswordMessageLabel, false);
        passwordHelpIndicator.getElement().getStyle().setDisplay(Display.BLOCK);
        changePasswordMessageLabel.setText(strengthTip);
    }

    private void showCapsLockOn() {
        for (final Label capsLockIndicator : capsLockIndicators) {
            capsLockIndicator.setVisible(true);
        }
    }

    private void hideCapsLockOn() {
        for (final Label capsLockIndicator : capsLockIndicators) {
            capsLockIndicator.setVisible(false);
        }
    }

    @Override
    public String getNewPassword() {
        return newPassword.getText();
    }

    @Override
    public String getConfirmPassword() {
        return confirmPassword.getText();
    }

    @Override
    public void switchToChangePasswordTab() {
        tabPanel.selectTab(1);
    }

    private boolean isUserInputValid() {
        if (isNullOrEmpty(getUserId()) || isNullOrEmpty(getPassword())) {
            setMessage("Mandatory fields are empty");
            return false;
        }
        return true;
    }

    private boolean isPasswordChangeInputValid() {
        if (isNullOrEmpty(getNewPassword()) || isNullOrEmpty(getConfirmPassword())) {
            setMessage("Mandatory fields are empty");
            return false;
        }

        if (!getNewPassword().equals(getConfirmPassword())) {
            setMessage("New passwords entered do not match");
            return false;
        }

        return true;
    }

    private boolean isNullOrEmpty(final String value) {
        return value == null || "".equals(value.trim());
    }

    /**
     * UiBinder factory method to create the correct button for this view
     * @return
     */
    @UiFactory
    EIconButton makeEIconButton() {
        return new EIconButton(ButtonType.LOGIN_SUBMIT);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.login.client.mvp.LoginPresenter.ILoginView#enableNewPassword()
     */
    @Override
    public void setNewPasswordEnabled(final boolean enabled) {
        passwordHelpIndicator.getElement().getStyle().setDisplay(Display.NONE);
        confirmPassword.setEnabled(enabled);
        loginButtonChangePassword.setEnabled(enabled);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.login.client.mvp.LoginPresenter.ILoginView#setVisible()
     */
    @Override
    public void showGlassPanel() {
        /** Show Glass Panel.**/
        window.showGlassPanel();
    }

    /**
     * List of Themeable components in this view. Will iterate through this list and change the theme when required.
     */
    private void createThemableComponentList() {

    }

    private void applyStyle(final String theme) {
        final List<Widget> styleables = new LinkedList<Widget>() {
            {
                add(capsLockOn1);
                add(capsLockOn2);
                add(capsLockOn3);
                add(lowerSplashPanel);
                add(upperSplashPanel);
                add(tradeMarkPanel);
                add(eniqPanel);
                /*  add(globesPanel);*/
                add(changePasswordPageSeparator);
                add(changePasswordMessageLabel);
                add(passwordHelpIndicator);
                add(loginMessageLabel);
                add(navigationPage1);
                add(navigationPage2);
            }
        };
        //TODO when theming impl is decided replace this with the new impl
        for (final Widget w : styleables) {
            w.addStyleDependentName(theme);
        }
    }

    private void onLoginSubmit() {
        loginMessageLabel.setText("");
        if (isUserInputValid()) {
            presenter.onLoginSelected();
        }
    }

    @Override
    public void showNewPasswordEntryPage() {
        enableTabIndexNewPasswordEntryPage(true);
        confirmPassword.setEnabled(false);
        loginButtonChangePassword.setEnabled(false);
        passwordEntryForm.addStyleDependentName("show");
        navigationPage1.setStylePrimaryName("navigationOff");
        navigationPage2.setStylePrimaryName("navigationOn");
    }

    private void enableTabIndexNewPasswordEntryPage(final boolean enabled) {
        int index = 0;
        if (!enabled) {
            index = -1;
        }
        newPassword.setTabIndex(index);
        confirmPassword.setTabIndex(index);
        loginButtonChangePassword.setTabIndex(index);
    }

    private void expandWindow() {
        collapsiblePanel.expand();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.login.client.mvp.LoginPresenter.ILoginView#displayValidatingUserView(boolean)
     */
    @Override
    public void displayValidatingUserView(final boolean isChangingPassword) {
        progressBar.resetProgress();
        progressBarText = "Validating User";
        progressBar.setIncrementSpeed(isChangingPassword ? 25 : 50);
        progressBar.incrementProgress();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.login.client.mvp.LoginPresenter.ILoginView#displayChangingPassword()
     */
    @Override
    public void displayChangingPassword() {
        progressBarText = "Changing Password...";
        progressBar.incrementProgress();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.login.client.mvp.LoginPresenter.ILoginView#displayAuthenticating()
     */
    @Override
    public void displayAuthenticating() {
        collapseWindow();
        progressBarText = "Authenticating...";
        progressBar.incrementProgress();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.login.client.mvp.LoginPresenter.ILoginView#loginOperationComplete()
     */
    @Override
    public void setLoginOperationComplete() {
        progressBar.setComplete();
        progressBar.setText("User Action Required");
    }

    private void collapseWindow() {
        collapsiblePanel.collapse();
    }

    private void onOldUserNamePasswordSubmit() {
        changePasswordMessageLabel.setText("");
        passwordHelpIndicator.getElement().getStyle().setDisplay(Display.NONE);
        if (isUserInputValid()) {
            presenter.onChangePasswordLoginSelected();
        }
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.login.client.mvp.LoginPresenter.ILoginView#setPresenter(com.ericsson.eniq.events.login.client.mvp.LoginPresenter)
     */
    @Override
    public void setPresenter(final ILoginPresenter loginPresenter) {
        this.presenter = loginPresenter;
    }

    /* 
     * Fixed delay to allow the user time to see on the progress bar the success response to changing the password
     * (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.login.client.mvp.LoginPresenter.ILoginView#showPasswordChangeSuccess(com.ericsson.eniq.events.ui.login.client.mvp.LoginPresenter.ILoginViewActionCallback)
     */
    @Override
    public void showPasswordChangeSuccess(final ILoginViewActionCallback callback) {
        progressBarText = "Change Password Success";
        progressBar.incrementProgress();
        final RepeatingCommand command = new RepeatingCommand() {

            @Override
            public boolean execute() {
                callback.actionComplete();
                return false;
            }
        };
        Scheduler.get().scheduleFixedDelay(command, CHANGE_PASSWORD_SUCCESS_DELAY);

    }
}
