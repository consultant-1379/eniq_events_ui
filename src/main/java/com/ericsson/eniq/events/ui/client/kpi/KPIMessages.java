package com.ericsson.eniq.events.ui.client.kpi;

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.Messages;

/**
 * -----------------------------------------------------------------------
 * Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
@DefaultLocale("en")
public interface KPIMessages extends Messages {

    @DefaultMessage("Select Last Data From")
    String selectLastDataTitle();

    @DefaultMessage("Select Refresh Rate")
    String selectRefreshRateTitle();

    @DefaultMessage("KPI Alarm Configuration")
    String kpiConfigurationDialogHeading();

}
