/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

import java.util.ArrayList;
import java.util.List;


/**
 * @author eendmcm
 * @since Feb 2010
 * DataType used to configure toolBars from metaData
 */
public class ToolBarInfoDataType {
    public final List<ToolbarPanelInfoDataType> toolBarPanels = new ArrayList<ToolbarPanelInfoDataType>();

}
