/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.main;

import com.ericsson.eniq.events.ui.client.common.MetaMenuItem;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindowPresenter;
import com.ericsson.eniq.events.ui.client.common.comp.WindowState;
import com.ericsson.eniq.events.ui.client.common.widget.AbstractBaseWindowDisplay;
import com.ericsson.eniq.events.ui.client.common.widget.EventGridPresenter;
import com.ericsson.eniq.events.ui.client.common.widget.EventGridView;
import com.ericsson.eniq.events.ui.client.common.widget.IEventGridView;
import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.workspace.IWorkspaceController;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Generic action for main menu item selection when we know 
 * the window type to launch is a grid.
 * 
 * Performs launch of empty grid window. 
 * Window itself will discover if it needs to request data to populate.
 * 
 * Also used to toggle from chart to a grid when Response passed in directly.
 * 
 * @see  {@link com.ericsson.eniq.events.ui.client.main.GenericTabPresenter} 
 * @author eeicmsy
 * @since March 2010
 *
 */
public class GridLauncher extends AbstractWindowLauncher {

    private final EventBus eventBus;

    /**
     * Construct generic selection listener for main menu menuitem 
     * responsible for launching grids (including ranking grids)
     * 
     * Can create multiple views or single views
     * 
     *
     * @param item               Menu item selected with details for server calls required to populate grid
     * @param eventBus           The singleton event bus used in MVP pattern
     * @param containingPanel    Center panel where launched window will be constrained to.
     * @param workspaceController Controller with container panel which will "own" the launched window i.e. Menu Task Bar
     */
    public GridLauncher(final MetaMenuItem item, final EventBus eventBus, final ContentPanel containingPanel,
            final IWorkspaceController workspaceController) {
        this(item, eventBus, containingPanel, workspaceController, "");
    }

    /**
     * Construct generic selection listener for main menu menuitem 
     * responsible for launching grids (including ranking grids)
     * 
     * Can create multiple views or single views
     * 
     *
     * @param item               Menu item selected with details for server calls required to populate grid
     * @param eventBus           The singleton event bus used in MVP pattern
     * @param containingPanel    Center panel where launched window will be constrained to.
     * @param workspaceController Controller with container panel which will "own" the launched window i.e. Menu Task Bar
     * @param windowId
     */
    public GridLauncher(MetaMenuItem item, EventBus eventBus, ContentPanel containingPanel,
            IWorkspaceController workspaceController, String windowId) {
        super(item, containingPanel, workspaceController, windowId);
        this.eventBus = eventBus;
    }

    @Override
    public AbstractBaseWindowDisplay createView(final MultipleInstanceWinId multiWinId, final WindowState windowState) {
        return new EventGridView(multiWinId, item, workspaceController, containingPanel, windowState);
    }

    @Override
    public BaseWindowPresenter<IEventGridView> createPresenter(final AbstractBaseWindowDisplay view,
            MultipleInstanceWinId winId) {
        return new EventGridPresenter((IEventGridView) view, winId, eventBus);
    }

    @Override
    public void handleEnablingForReLaunch() {
        // nothing to do
    }
}