package com.ericsson.eniq.events.ui.client.kpi.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/*
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

/**
 * Style for Portlets in ClientBundle + CssResource used by PortletWindow &
 * PlaceHolder.
 * 
 * @author eaajssa
 * @since Jan 2012
 */
public interface IButtonResourceBundle extends ClientBundle {

	@Source("css/IButton.css")
	Style style();

	@Source("images/red_normal.png")
	ImageResource criticalImage();

	@Source("images/blue_normal.png")
	ImageResource warningImage();

	@Source("images/orange_normal.png")
	ImageResource majorImage();

	@Source("images/yellow_normal.png")
	ImageResource minorImage();

	@Source("images/red_clicked.png")
	ImageResource criticalImageClicked();

	@Source("images/blue_clicked.png")
	ImageResource warningImageClicked();

	@Source("images/orange_clicked.png")
	ImageResource majorImageClicked();

	@Source("images/yellow_clicked.png")
	ImageResource minorImageClicked();

	@Source("images/active_launched_window_tab.png")
	ImageResource activeLaunchedImage();

	@Source("images/launched_window_tab.png")
	ImageResource activeImage();

	@Source("images/hover_tab.png")
	ImageResource hoverImage();

	interface Style extends CssResource {
		String container();

		String buttonHover();

		String buttonActive();

		String buttonActiveLaunched();

		String image();

		String label();

		String popup();

		String popupImage();

		String popupTitle();

		String breachesLabel();
		
		String percentLabel();
	}

}
