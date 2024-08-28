/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.common.service;

import java.util.Collection;

/**
 * 
 * Entry point to service model.
 * 
 * @author edmibuz
 *
 */
public interface TabManager {

	WindowManager getWindowManager(String tabId);

	Collection<WindowManager> getWindowManagers();

}
