/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.grid.listeners;

/**
 * @author ekurshi
 * @since 2011
 *
 *Handles the  ok(after selecting date and time) and cancel button click of date time picker.
 */

import java.util.Date;

public interface DatePickerListener {
    /**
     * This method calls when user click the ok button of date time picker. It sets the date in DateTimeField apply the filters.
     * @param date Value that user entered in date time picker.
     */
    void onSelect(Date date);

    /**
     * This method calls when user click the cancel button of date time picker. this hide the date time picker.
     */
    void onCancel();
}
