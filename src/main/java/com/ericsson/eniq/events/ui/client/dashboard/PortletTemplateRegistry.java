/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard;

import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletType;

public interface PortletTemplateRegistry {

	PortletTemplate createByName(PortletType name);
	
}
