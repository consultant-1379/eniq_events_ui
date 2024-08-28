/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.multiselect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.common.Constants;
import com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementUtils;
import com.ericsson.eniq.events.ui.client.groupmanagement.ISelectionHandler;
import com.ericsson.eniq.events.ui.client.groupmanagement.component.FilterPanel;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.resources.GroupMgmtResourceBundle;
import com.ericsson.eniq.events.widgets.client.dialog.MessageDialog;
import com.ericsson.eniq.events.widgets.client.mask.MaskHelper;
import com.extjs.gxt.ui.client.core.XDOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;

import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.*;

/**
 * @author ekurshi
 * @since 2012
 */
public class MultiSelectView extends BaseView<AbstractMultiSelectPresenter> {

    private final FlowPanel panel;

    private List<FilterPanel> wizards;

    private final MessageDialog dialog;

    private final MaskHelper maskHelper;

    private final GroupMgmtResourceBundle resourceBundle;

    @Inject
    public MultiSelectView(final GroupMgmtResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        panel = new FlowPanel();
        panel.setStyleName(resourceBundle.style().wizardContainer());
        dialog = new MessageDialog();
        dialog.setGlassEnabled(true);
        this.maskHelper = new MaskHelper();
        initWidget(panel);
    }

    public void close() {
        getPresenter().close();
    }

    public void addWizard(final String elementType, final ISelectionHandler handler, final boolean multiSelect) {
        if (wizards == null) {
            wizards = new ArrayList<FilterPanel>();
        }
        final FilterPanel wizardPanel = new FilterPanel();
        //TODO if require liveload, need to create RemoteMultiWordSuggestOracle here
        wizardPanel.init(resourceBundle, multiSelect);
        wizardPanel.setEnabled(false);
        wizardPanel.setHeader(GroupManagementUtils.prepareHeader(elementType));
        wizardPanel.setId(GroupManagementUtils.createIdForFilterPanel(elementType));
        wizardPanel.setItemSelectionHandler(handler);
        wizardPanel.addStyleName(resourceBundle.style().wizard());
        wizards.add(wizardPanel);
        panel.add(wizardPanel);

    }

    public void displayFailureDialog(final String message) {
        dialog.setMessage(message);
        dialog.getElement().getStyle().setZIndex(Math.max(Constants.ENIQ_EVENTS_BASE_ZINDEX + 3, XDOM.getTopZIndex()));
        dialog.show();
        dialog.center();
    }

    public void displaySuggestions(final int index, final Collection<GroupListItem> suggestions) {
        final FilterPanel filterPanel = wizards.get(index);
        filterPanel.setEnabled(true);
        filterPanel.clear();
        filterPanel.setSuggestions(suggestions);
    }

    public int calculateWidth() {
        return wizards.size() * 300;
    }

    public void mask(final int index) {
        if (index < wizards.size()) {
            final FilterPanel filterPanel = wizards.get(index);
            final Element element = filterPanel.getElement();
            final String header = filterPanel.getHeader();
            maskHelper.mask(element, LOADING + GroupManagementUtils.getElementTypeFromHeader(header)
                    + LOADING_MSG_ENDING_DOTS, element.getOffsetHeight());
        } else {
            getPresenter().maskLiveLoadPanel(true);
        }
    }

    /**
     * ecarsea - Only set up to handle 3 panel editor at moment
     * @param firstItem
     */
    public void unmask(final boolean firstItem) {
        if (firstItem) {
            maskHelper.unmask();
        } else {
            getPresenter().maskLiveLoadPanel(false);
        }
    }
}
