package com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu;

import com.ericsson.eniq.events.ui.client.common.Constants;
import com.ericsson.eniq.events.ui.client.workspace.launch.WorkspaceLaunchMenuResourceBundle;
import com.ericsson.eniq.events.widgets.client.textbox.ExtendedTextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class WindowFilterPanel extends Composite {
    private static final int TEXTBOX_HEIGHT = 36 + 1;/*including bottom border*/

    private static final int HEADER_HEIGHT = 20;

    private static final int BORDERS_SPACE = 2;

    private static WindowFilterPanelUiBinder uiBinder = GWT.create(WindowFilterPanelUiBinder.class);

    interface WindowFilterPanelUiBinder extends UiBinder<Widget, WindowFilterPanel> {
    }

    @UiField
    Label header;

    @UiField
    ExtendedTextBox filter;

    @UiField
    HTMLPanel glassPanel;

    @UiField
    HTMLPanel filterBoxHolder;

    @UiField
    ScrollPanel scrollPanel;

    @UiField
    HTMLPanel filterContent;

    private WorkspaceLaunchMenuResourceBundle resourceBundle;

    private WindowFilter windowFilter;

    public WindowFilterPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        sinkEvents(Event.ONPASTE);
    }

    public void init(final WindowFilter wf, final WorkspaceLaunchMenuResourceBundle resourceBundle) {
        this.windowFilter = wf;
        this.resourceBundle = resourceBundle;
        filter.getElement().setId(Constants.SELENIUM_TAG + "windowFilter");
        filter.setEnabled(false);
        filter.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(final KeyUpEvent event) {
                windowFilter.updateFilter(filter.getText());
            }
        });

        filter.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(final ValueChangeEvent<String> event) {
                windowFilter.updateFilter(event.getValue());
            }
        });
    }

    public void setScrollPanelHeight(final int filterPanelHeight) {
        scrollPanel.setHeight((filterPanelHeight - HEADER_HEIGHT - TEXTBOX_HEIGHT - BORDERS_SPACE) + "px");//-4
    }

    public void clear() {
        filter.setText("");
        filter.enableDefaultText();
    }

    public void setHeader(final String header) {
        this.header.setText(header);
    }

    public void setId(final String id) {
        getElement().setId(id);
    }

    /** @param enabled  */
    public void setEnabled(final boolean enabled) {
        filter.setEnabled(enabled);
        if (enabled) {
            glassPanel.removeStyleName(resourceBundle.workspaceLaunchStyle().enabled());
            this.removeStyleName(resourceBundle.workspaceLaunchStyle().windowFilterPanelDisabled());
        } else {
            glassPanel.addStyleName(resourceBundle.workspaceLaunchStyle().enabled());
            this.addStyleName(resourceBundle.workspaceLaunchStyle().windowFilterPanelDisabled());
        }
    }

    /** @return the header */
    public String getHeader() {
        return header.getText();
    }

    public HTMLPanel getFilterContent() {
        return filterContent;
    }

    /**
     * @return
     */
    public boolean isEnabled() {
        return filter.isEnabled();
    }
}
