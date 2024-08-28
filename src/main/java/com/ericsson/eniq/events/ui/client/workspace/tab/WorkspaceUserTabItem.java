/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.tab;

import com.ericsson.eniq.events.ui.client.datatype.TabInfoDataType;
import com.ericsson.eniq.events.ui.client.workspace.events.CloseWorkspaceEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceStatusChangeEvent;
import com.ericsson.eniq.events.ui.client.workspace.events.WorkspaceStatusChangeEventHandler;
import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author ecarsea
 * @author ealeerm - Alexey Ermykin
 * @since 06/2012
 */
public class WorkspaceUserTabItem extends TabItem {

    private final WorkspaceOptionsMenu optionsMenu;

    boolean isDirty;

    String optionsMenuClass;

    private final TabInfoDataType tabInfo;

    private final EventBus eventBus;

    private final String hideDirtyWorkspaceStyle;

    public WorkspaceUserTabItem(TabInfoDataType tabInfo, final WorkspaceOptionsMenu optionsMenu, EventBus eventBus,
            final WorkspaceTabsResourceBundle resources) {
        this.tabInfo = tabInfo;
        this.eventBus = eventBus;
        this.hideDirtyWorkspaceStyle = resources.css().hideDirtyWorkspace();

        final String closeTabButtonStyle = resources.css().closeTabButton();
        String dirtyWorkspaceIndicatorStyle = resources.css().dirtyWorkspaceIndicator();

        optionsMenuClass = optionsMenu.getResources().css().optionsMenu();
        this.header.addListener(Events.BrowserEvent, new Listener<ComponentEvent>() {

            @Override
            public void handleEvent(ComponentEvent ce) {
                Element target = ce.getTarget();
                String styleName = fly(target).getStyleName();
                // Is it one of our tab components
                if ((styleName.equals(optionsMenuClass) || styleName.equals(closeTabButtonStyle))) {
                    if (ce.getEventTypeInt() == Event.ONMOUSEOVER) {
                        ce.setCancelled(true);
                        ce.stopEvent();
                        ce.preventDefault();
                    }
                    if (ce.getEventTypeInt() == Event.ONCLICK) {
                        ce.setCancelled(true); // cancel the event, dont want tab to be selected.
                        if (styleName.equals(optionsMenuClass)) {
                            optionsMenu.setAutohidePartner(target);
                            onDropDownClick(ce);
                        } else {
                            onCloseClick();
                        }
                    }
                }
            }
        });
        this.optionsMenu = optionsMenu;
        setText(tabInfo.getName());
        StringBuilder sb = new StringBuilder();
        sb.append("<li class='{style} id={id} role='tab' tabindex='0'><a class=x-tab-strip-close role='presentation'></a>");
        sb.append("<a class='x-tab-right' role='presentation'><em role='presentation' class='x-tab-left'>");
        sb.append("<span class='").append(optionsMenuClass).append("' title='Tab options'></span>");
        sb.append("<span class='x-tab-strip-inner' role='presentation'>");
        sb.append("<span class='x-tab-strip-text {textStyle}'>{text}</span></span>");
        sb.append("<span class='").append(dirtyWorkspaceIndicatorStyle).append("'></span><span class='")
                .append(closeTabButtonStyle).append("' title='Close Workspace'></span>");
        sb.append("</em></a></li>");

        Template itemTemplate = new Template(sb.toString());
        itemTemplate.compile();
        template = itemTemplate;

        eventBus.addHandler(WorkspaceStatusChangeEvent.TYPE, new WorkspaceStatusChangeEventHandler() {
            @Override
            public void onWindowStatusChange(WorkspaceStatusChangeEvent event) {
                if (WorkspaceUserTabItem.this.tabInfo.getId().equals(event.getWorkspaceId())) {
                    if (event.isDirty() != isDirty) {
                        isDirty = event.isDirty();
                        markDirtyWorkspace();
                    }
                }
            }
        });
        /** Start with a clean workspace, no need to save it **/
        markDirtyWorkspace();
    }

    public void onDropDownClick(ComponentEvent ce) {
        optionsMenu.processClick(ce);
    }

    public void onCloseClick() {
        optionsMenu.processClose();
        eventBus.fireEvent(new CloseWorkspaceEvent(tabInfo));
    }

    public boolean isDirty() {
        return isDirty;
    }

    void markDirtyWorkspace() {
        if (isDirty) {
            getHeader().removeStyleName(hideDirtyWorkspaceStyle);
        } else {
            getHeader().addStyleName(hideDirtyWorkspaceStyle);
        }
    }
}