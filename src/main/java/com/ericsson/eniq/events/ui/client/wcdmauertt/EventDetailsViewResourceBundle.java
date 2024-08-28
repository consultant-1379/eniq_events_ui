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

package com.ericsson.eniq.events.ui.client.wcdmauertt;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface EventDetailsViewResourceBundle extends ClientBundle {
    @Source("EventDetailsView.css")
    EventSummaryStyle css();

    interface EventSummaryStyle extends CssResource {
        String summaryGrid();

        String oddRow();

        String gridLabel();

        String summaryGridRow();

        String tabText();

        String windowTitle();
    }
}
