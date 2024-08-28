/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.northpanel;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.datatype.LicenseInfoDataType;
import com.ericsson.eniq.events.ui.client.northpanel.button.EniqOptionsMenu;
import com.ericsson.eniq.events.ui.client.northpanel.button.OptionMenuItemType;
import com.ericsson.eniq.events.ui.client.northpanel.infoicon.InfoIcon;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

import static com.ericsson.eniq.events.ui.client.northpanel.button.OptionMenuItemType.*;

/**
 * The North panel, which will contains the 'welcome' message and various links.
 * Implementing using UIBinding
 *
 * @author eeicmsy
 * @since Feb 2010
 */
public class NorthPanelView extends BaseView<NorthPanelPresenter> {

    private final InfoIcon infoIcon = new InfoIcon();

    private final EniqOptionsMenu optionsMenu;

    public NorthPanelView() {
        final HorizontalPanel panel = new HorizontalPanel();
        panel.add(infoIcon);
        panel.add(createSeperator());
        optionsMenu = createOptionsMenu();
        panel.add(optionsMenu);

        initWidget(panel);
        RootPanel.get("headerPnl").add(this);

        /* Hide the Options menu until the MetaData has come back from the server to display the tabs */
        this.setVisible(false);
    }

    /**
     * Configure the options menu.
     * Some options are license controlled and should only be shown if there is a valid license
     * installed.
     *
     * @param licenses - List of licenses installed on the server.
     */
    public void configureOptionsMenu(final List<LicenseInfoDataType> licenses) {
        if (licenses != null) {
            for (final LicenseInfoDataType lic : licenses) {
                optionsMenu.setVisibleOnLicense(lic);
            }
        }
    }

    private Widget createSeperator() {
        final SimplePanel panel = new SimplePanel();
        panel.setWidth("1px");
        panel.setHeight("26px");
        panel.getElement().getStyle().setBackgroundColor("#cdcdcd");

        return panel;
    }

    private EniqOptionsMenu createOptionsMenu() {
        final EniqOptionsMenu eniqOptionsMenu = new EniqOptionsMenu("Options");
        eniqOptionsMenu.getElement().getStyle().setMarginTop(1, Style.Unit.PX);

        eniqOptionsMenu.addItem(EE_USER_GUIDE);
        eniqOptionsMenu.addItem(ABOUT);
        eniqOptionsMenu.addSeparator(EE_USER_GUIDE, ABOUT);
        eniqOptionsMenu.addItem(GROUP_MANAGEMENT);
        if(isInternetExplorer()){
            eniqOptionsMenu.addSeparator(GROUP_MANAGEMENT);
        }else{
            eniqOptionsMenu.addItem(IMPORT_GROUPS);
            eniqOptionsMenu.addItem(DELETE_GROUPS);
            eniqOptionsMenu.addSeparator(GROUP_MANAGEMENT, IMPORT_GROUPS, DELETE_GROUPS);
        }
        eniqOptionsMenu.addItem(LOG_OUT);

        eniqOptionsMenu.addSelectionHandler(new OptionsSelectionHandler());

        return eniqOptionsMenu;
    }

    private boolean isInternetExplorer(){
        boolean answer = false;
        String userAgent = Window.Navigator.getUserAgent().toLowerCase();
        if(userAgent.contains("msie")){
            answer = true;
        }
        return answer;
    }
    
//    public static native String getApp
    
    public void setSystemAdminBannerMessage(final String s) {
        infoIcon.setMessage(s);
    }

    public void setUserLoggedName(final String userId) {
        optionsMenu.updateItemLabel(OptionMenuItemType.LOG_OUT, "Log Out - " + userId);
    }

    private class OptionsSelectionHandler implements SelectionHandler<OptionMenuItemType> {

        @Override
        public void onSelection(final SelectionEvent<OptionMenuItemType> event) {
            final OptionMenuItemType item = event.getSelectedItem();

            switch (item) {
            case EE_USER_GUIDE:
                getPresenter().openEniqEventsUserGuide();
                break;
            case ABOUT:
                getPresenter().showAboutDialog();
                break;
            case GROUP_MANAGEMENT:
                getPresenter().launchGroupManagement();
                break;
            case IMPORT_GROUPS:
                getPresenter().launchGroupImport();
                break;
            case DELETE_GROUPS:
                getPresenter().launchGroupDelete();
                break;
            case LOG_OUT:
                getPresenter().userLogout();
                break;
            }
        }
    }

}