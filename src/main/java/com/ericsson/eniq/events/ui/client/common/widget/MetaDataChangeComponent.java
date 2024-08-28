/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.widget;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.ericsson.eniq.events.ui.client.common.IMetaReader;
import com.ericsson.eniq.events.ui.client.common.IMultiMetaDataHelper;
import com.ericsson.eniq.events.ui.client.datatype.MetaDataChangeDataType;
import com.ericsson.eniq.events.ui.client.events.MetaDataChangeEvent;
import com.ericsson.eniq.events.ui.client.events.MetaDataChangeEventHandler;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.user.client.Element;
import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * Configured via meta data
 * 
 * A component which is independent of tab (even if do put one on each tab)
 * Introduced to allow switching view menu for the packet switched options
 * (original) and CS (MSS) options.
 * 
 * Need to be possible to hide this component completely (via meta data) Ideally
 * this component will drive what meta data is presented on launch, i.e via meta
 * data.
 * 
 * License driven. Warning : If want say Circuit Switched Menus on its own (with
 * out PS - which is what "master" meta data has), then there will be a "jump"
 * at launch when go from displaying PS to a permanent CS (prior to removing the
 * toggle option in this class in this case) - THIS WOULD BE BECAUSE USING the
 * master meta data to bring in this component.
 * 
 * To avoid this "jump" you completely swap the default start up meta data to
 * say the CS one rather than the master at installation delivery - i.e.
 * glassfish setting. So if MSS on it own that is required should ideally change
 * glassfish config for meta data location. Assuming MSS meta data is an
 * independent meta data in own right with time control etc - otherwise all
 * above is mute.
 * 
 * We put Controller node as default in CS and PS (its a share node) so this
 * jump is not really that noticable at all.
 * 
 * 
 * Using the "master" meta data (which contains all licence info and where to go
 * to fetch "minor" metadata) is more suited to dual (or future multi options)
 * for meta datas. (if you start in PS - i.e. the master, then no jump too of
 * course)
 * 
 * @author eeicmsy
 * @since April 2011
 * 
 */
public class MetaDataChangeComponent extends Button implements MetaDataChangeEventHandler {

    private static final Logger LOGGER = Logger.getLogger(MetaDataChangeComponent.class.getName());

    private final IMetaReader metaReader = MainEntryPoint.getInjector().getMetaReader();

    private final IMultiMetaDataHelper multiMetaDataHelper = MainEntryPoint.getInjector().getMultiMetaDataHelper();

    private final List<MetaDataChangeDataType> menuItems;

    private final EventBus eventBus;

    private final String startupPath;

    private final SelectionListener<MenuEvent> metaDataChangeListener = new MetaDataChangeListener();

    private int licenceCount = 0;

    private String key;

    /**
     * Button component containing drop down menu to allow meta data change
     * 
     * @param bus
     *          - standard event bus used though out presenter code
     * @param menuItems
     *          - items to display as choices in the meta data change comopnent
     * @param startupPath
     *          - default launch ui meta data path (which may change if not
     *          licenced)
     */
    public MetaDataChangeComponent(final EventBus bus, final List<MetaDataChangeDataType> menuItems,
            final String startupPath) {

        super();
        this.menuItems = menuItems;
        this.eventBus = bus;
        this.startupPath = startupPath;

        setupSelectComponent(); // NOPMD by eeicmsy on 19/04/11 11:14

        bus.addHandler(MetaDataChangeEvent.TYPE, this);
    }

    /*
     * all version of this component must all display the same thing at same time
     * in all tabs
     * 
     * @see
     * com.ericsson.eniq.events.ui.client.events.MetaDataMenuChangeEventHandler
     * #handleMetaDataChangeEvent
     * (com.ericsson.eniq.events.ui.client.datatype.MetaDataChangeDataType)
     */
    @Override
    public void handleMetaDataChangeEvent(final MetaDataChangeDataType menuSelected) {
        resetMenuSelectedDisplay(menuSelected);

    }

    /**
     * (Even if component never shown - 
     * it will know what keys are supported (licenced)
     * 
     * @return .e.g CS, PS or just PS or CS alone
     */
    public List<String> getLicencedKeys() {
        final List<String> allLicencedKeys = new ArrayList<String>();
        for (final MetaDataChangeDataType menuItem : menuItems) {
            if (menuItem.isLicenced()) {
                allLicencedKeys.add(menuItem.getKey());
            }
        }
        return allLicencedKeys;

    }

    private void setupSelectComponent() {

        licenceCount = 0;
        setId(SELENIUM_TAG + "MetaDataChangeComponent");
        setWidth(70); //LG EDIT

        final MetaDataChangeDataType startupItem = getStartupItem();

        if (startupItem == null) {
            // some meta data change required to have at least licence for CS or PS
            // not neither
            // default to default master meta data on launch (UI_METADATA_PATH) with
            // no option to change meta data
            // This suits case if not using the 'master' at all (master is meta data
            // containing all licence info
            // pertaining to toggling (ps to CS) options - which will be used to
            // locate all other meta data - if more than one

            LOGGER.warning("MetaDataChangeComponent startup item is null. This could be ok if user is "
                    + "not using the 'master metadata' but completly replacing to make another meta data the default start up");

            return;
        }

        final Menu menu = createMenu();
        menu.setShadow(false);

        setMenu(menu);

        for (final MetaDataChangeDataType menuItem : menuItems) {
            menu.add(menuItem);
            menuItem.addSelectionListener(metaDataChangeListener);

            if (menuItem.isLicenced()) {

                licenceCount++;
            }
        }

        resetMenuSelectedDisplay(startupItem);
        performActionForSelectionChanged(startupItem);
    }

    /**
     * When only one option available don't show this component at all But need to
     * to use its functionality to get start up launch if different than default
     * 
     * @return true if should add this component to taskbar
     */
    public boolean shouldBeDisplayed() {
        return licenceCount > 1;
    }

    /**
     * Direct return of number of licences When no licence UI will not display
     * search field and menu items
     * 
     * @return number of licences (CS and PS, or just 1 or none)
     */
    public int getLicenceCount() {
        return licenceCount;
    }

    /**
     * Return key for current selection in component Instead of calling #getText
     * to find key (because "CS" or "PS" may not be the shortKey (display text in
     * selected combobox), we instead have a #getKey
     * 
     * @return "CS" or "PS" used in Maps (as from meta data)
     */
    public String getKey() {
        return key;
    }

    /**
     * Supports using our default Meta Data to contain this components set up
     * only. But when only licences for one option (e.g. CS) then let
     * functionality work to change to CS mode straift away. Then hide the
     * combobox entirely.
     * 
     * @return start up state which must be licences or next licenced one is
     *         returned instead
     */
    MetaDataChangeDataType getStartupItem() { // expose junit
        MetaDataChangeDataType startupItem = null;

        MetaDataChangeDataType randomLicencedStartup = null;
        for (final MetaDataChangeDataType menuItem : menuItems) {

            if (menuItem.isLicenced()) {

                if (menuItem.getMetaDataPath().equals(startupPath)) {
                    startupItem = menuItem;
                } else {
                    randomLicencedStartup = menuItem;
                }
            }
        }

        if (startupItem == null || (!startupItem.isLicenced())) {
            startupItem = randomLicencedStartup;
        }

        return startupItem;
    }

    /*
     * Just resets what is displayed on component button without invoking a call.
     * Because this component can be present across several tabs, we want to make
     * it appear that it is the same component when user changes selection on one
     * (such when change selection on one change selection on all)
     */
    private void resetMenuSelectedDisplay(final MetaDataChangeDataType menuSelected) {

        setText(menuSelected.getShortText());
        setIconStyle(menuSelected.getStyle());
        setToolTip(menuSelected.getTip());
        this.key = menuSelected.getKey();
    }

    private void performActionForSelectionChanged(final MetaDataChangeDataType menuSelected) {

        // all copies on all tabs must look same
        eventBus.fireEvent(new MetaDataChangeEvent(menuSelected));

        /* this copy does the extra work - i.e other copies just look changed */
        final String path = menuSelected.getMetaDataPath();

        if (!multiMetaDataHelper.resetMetaDataPath(path) && !multiMetaDataHelper.hasLoaded(path)) {

            // MainPresenter has a handler for this
            loadMetaData(path);
        }

    }

    private class MetaDataChangeListener extends SelectionListener<MenuEvent> {

        @Override
        public void componentSelected(final MenuEvent ce) {
            performActionForSelectionChanged((MetaDataChangeDataType) ce.getItem());
        }
    }

    /* extracted for junit */
    Menu createMenu() {
        return new Menu() {

            @Override
            public void show(final Element elem, final String pos, final int[] offsets) {
                offsets[0] = 7;
                offsets[1] = -3;
                super.show(elem, pos, offsets);
            }

        };
    }

    /* extracted for junit */
    void loadMetaData(final String path) {
        metaReader.loadMetaData(path);
        MainEntryPoint.getInjector().getUserPreferencesReader().loadUserPreferences();
    }

}
