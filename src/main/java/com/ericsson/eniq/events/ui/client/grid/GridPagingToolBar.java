/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 ******************************************************************************* */
package com.ericsson.eniq.events.ui.client.grid;

import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.ericsson.eniq.events.ui.client.common.UIToolBarLayout;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.messages.XMessages;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Element;
import com.ericsson.eniq.events.ui.client.common.Constants;

import java.util.Date;

import static com.ericsson.eniq.events.ui.client.common.CSSConstants.LAST_REFRESH_LABEL_CSS;
import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;
import static com.ericsson.eniq.events.ui.client.common.Constants.NULL;
import static com.ericsson.eniq.events.ui.client.common.ToolTipConstants.LAST_REFRESH_TOOLTIP;

/**
 * Wrap the GXT PagingToolBar to enable
 * custom implementation of the refresh button
 * This adds a label for the number of rows in the original result set from the query
 * And a label for the last refreshed time
 */
public class GridPagingToolBar extends PagingToolBar implements IBottomToolBar {

    private Label lastRefreshLabel;

    private String currentLastRefreshText = EMPTY_STRING;

    /* boolean to get around toobar not being rendered in time */
    private boolean wantRefreshEnabed;
    private static final String CURRENT_PAGE = "currentPage";
    private static final String NEXT_BUTTON = "NEXT_BUTTON";
    private static final String PREVIOUS_BUTTON = "PREVIOUS_BUTTON";
    private static final String FIRST_BUTTON = "FIRST_BUTTON";
    private static final String LAST_BUTTON = "LAST_BUTTON";

    private static final String REFRESH_BUTTON = "REFRESH";
    /**
     * Constructor for GridPagingToolBar
     *
     * @param pageSize - parameter for given page size
     */
    public GridPagingToolBar(final int pageSize) {
        super(pageSize);
        add(new SeparatorToolItem());
        addLastRefreshLabel();
        setLayout(new UIToolBarLayout());
    }

    @Override
    public void upDateLastRefreshedLabel(Response response) {
        final JSONValue jsonValue = JSONUtils.parse(response.getText());
        final JsonObjectWrapper metaData = new JsonObjectWrapper(jsonValue.isObject());
        final String dataTimeFrom = metaData.getString(DATA_TIME_FROM);
        final String dataTimeTo = metaData.getString(DATA_TIME_TO);

        // note. dataTimeFrom and dataTimeTo returned from service layer is UTC based milliseconds
        // so you don't need to consider timezone offset as
        // DateTimeFormat::format() will automatically return current locale based time
        if (!dataTimeFrom.equals(EMPTY_STRING) && !dataTimeTo.equals(EMPTY_STRING) && !dataTimeFrom.equals(NULL) && !dataTimeTo.equals(NULL)) {
            final Date from = new Date(Long.parseLong(dataTimeFrom));
            final Date to = new Date(Long.parseLong(dataTimeTo));
            currentLastRefreshText = LABEL_FORMAT.format(from) + " - " + LABEL_FORMAT.format(to);
        }

        lastRefreshLabel.setText(currentLastRefreshText);
    }


    /**
     * Utlity when can use to disable toolbar but leave refresh button enabled
     *
     * @param isEnable            regular flag enabling whole toolbar
     * @param refreshButtonEnable set to enable (or disable) refresh button
     */
    public void setEnabled(final boolean isEnable, final boolean refreshButtonEnable) {
        super.setEnabled(isEnable);
        super.refresh.setEnabled(refreshButtonEnable);
        /**
         * Override GXT disabled styling to have a cursor pointer. GXT button element is of course a table containing a button so
         * have to retrieve the button element to set the cursor style.
         */
        if (refreshButtonEnable) {
            if (super.refresh.getElement().getElementsByTagName("button").getLength() == 1) {
                super.refresh.getElement().getElementsByTagName("button").getItem(0).addClassName("enableCursor");
            }
        }
        wantRefreshEnabed = refreshButtonEnable; // need for breadcrumb nav

    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        supportComponentsEnabling();
    }

    /* over-ride render to take care of refresh button (when know want it enabled) */
    @Override
    public void render(final Element target, final int index) {
        super.render(target, index);
        // was not rendered when called before
        if (wantRefreshEnabed) {
            super.refresh.setEnabled(wantRefreshEnabed);
        }
        supportComponentsEnabling();
    }

    @Override
    protected void onLoad(final LoadEvent event) {
        super.onLoad(event);
        supportComponentsEnabling();

    }

    @Override
    public String getLastRefreshTimeStamp() {
        return currentLastRefreshText;
    }

    @Override
    public void updateLastRefreshedTimeStamp(final String timeStamp) {
        /* breadcrumb update */
        currentLastRefreshText = timeStamp;
        lastRefreshLabel.setText(timeStamp);

    }

    /**
     * Adds a listener for the refresh button click
     * Use to also Update Refresh listener because search field data can change
     *
     * @param refreshBtnListener - SelectionListener<ButtonEvent> contain multiWinId info with
     *                           currrent search data
     */
    public void replaceRefreshBtnListener(final SelectionListener<ButtonEvent> refreshBtnListener) {
        super.refresh.removeAllListeners();
        super.refresh.addListener(Events.Select, refreshBtnListener);
    }

    /**
     * Method to call on window close down
     */
    public void cleanUpOnClose() {
        super.refresh.removeAllListeners();
        removeAllListeners();
    }

    private final void addLastRefreshLabel() {
        /** We do not want this label to be grayed out when the Paging Toolbar is disabled. Therefore, do nothing
         * when the disable method is called on this component (This will ensure that we do not assign the GXT disabling styles to
         * this component, and while it will inherit some disabling styles from parent, we have taken care of that in CSS with !important).
         * Also reset the opacity on the toolbar container itself to 1 so it doesnt cover this component. (The individual elements such as the pager
         * will have their opacity reduced anyway.
         */
        lastRefreshLabel = new Label() {
            /* (non-Javadoc)
             * @see com.extjs.gxt.ui.client.widget.Component#disable()
             */
            @Override
            public void disable() {
                GridPagingToolBar.this.getElement().getStyle().setOpacity(1);
            }
        };
        lastRefreshLabel.addStyleName(LAST_REFRESH_LABEL_CSS);
        lastRefreshLabel.setToolTip(LAST_REFRESH_TOOLTIP);
        add(lastRefreshLabel);
    }

    private void supportComponentsEnabling() {
        final boolean isContainerEnabled = isEnabled();
        first.setEnabled(activePage != 1 && pages > 1 && isContainerEnabled);
        prev.setEnabled(activePage != 1 && pages > 1 && isContainerEnabled);
        next.setEnabled(activePage != pages && pages > 1 && isContainerEnabled);
        last.setEnabled(activePage != pages && pages > 1 && isContainerEnabled);

        afterText.setEnabled(activePage > 0 && pages > 1 && isContainerEnabled);
        beforePage.setEnabled(activePage > 0 && pages > 1 && isContainerEnabled);
        pageText.setEnabled(activePage > 0 && pages > 1 && isContainerEnabled);
    }

    public void displayFullToolbar(){
        first.setVisible(true);
        last.setVisible(true);
        afterText.setVisible(true);
        displayText.setVisible(true);

        pageText.addStyleName(CURRENT_PAGE);
        next.addStyleName(NEXT_BUTTON);
        prev.addStyleName(PREVIOUS_BUTTON);
        refresh.addStyleName(REFRESH_BUTTON);
        first.addStyleName(FIRST_BUTTON);
        last.addStyleName(LAST_BUTTON);
    }
    public com.extjs.gxt.ui.client.widget.button.Button getNext(){
        return next;
    }
    public com.extjs.gxt.ui.client.widget.button.Button getPrev(){
        return prev;
    }
}
