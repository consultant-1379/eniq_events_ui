/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid;

import java.util.Date;

import com.ericsson.eniq.events.common.client.json.JsonObjectWrapper;
import com.ericsson.eniq.events.ui.client.common.JSONUtils;
import com.ericsson.eniq.events.ui.client.common.UIToolBarLayout;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONValue;

import static com.ericsson.eniq.events.ui.client.common.CSSConstants.LAST_REFRESH_LABEL_CSS;
import static com.ericsson.eniq.events.ui.client.common.Constants.DATA_TIMEZONE_PARMA_JSON_RESPONSE;
import static com.ericsson.eniq.events.ui.client.common.Constants.EMPTY_STRING;
import static com.ericsson.eniq.events.ui.client.common.Constants.NULL;
import static com.ericsson.eniq.events.ui.client.common.ToolTipConstants.LAST_REFRESH_TOOLTIP;

/**
 * FooterToolBar is one of the two types of toolbars the other being GridPagingToolbar that can
 * be applied to grids. FooterToolBar can be used on grids or charts and is used fot displaying the last time
 * the data displayed was refreshed
 *
 * @author esuslyn
 * @author eeicmsy
 * @since May 2010
 */
public class FooterToolBar extends ToolBar implements IBottomToolBar {
    private Label lastRefreshLabel;

    private String currentLastRefreshText = EMPTY_STRING;

    /**
     * Constructor for FooterToolBar
     */
    public FooterToolBar() {
        addLastRefreshLabel(); //NOPMD eemecoy 14/7/10, a necessary evil to get under test
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


    @Override
    public String getLastRefreshTimeStamp() {
        return currentLastRefreshText;
    }

    private final void addLastRefreshLabel() {

        add(new FillToolItem()); // put last refresh to far right
        add(new SeparatorToolItem());
        lastRefreshLabel = createLabel();
        lastRefreshLabel.addStyleName(LAST_REFRESH_LABEL_CSS);
        lastRefreshLabel.setToolTip(LAST_REFRESH_TOOLTIP);
        add(lastRefreshLabel);
    }

    /*  junit extraction */
    Label createLabel() {
        return new Label();
    }

    @Override
    public void updateLastRefreshedTimeStamp(final String timeStamp) {
        /* breadcrumb update */
        currentLastRefreshText = timeStamp;
        lastRefreshLabel.setText(timeStamp);

    }
}
