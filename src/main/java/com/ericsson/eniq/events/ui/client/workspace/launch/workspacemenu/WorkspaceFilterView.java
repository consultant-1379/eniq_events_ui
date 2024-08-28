/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu;

import com.ericsson.eniq.events.ui.client.workspace.launch.SelectedItemHandler;
import com.ericsson.eniq.events.ui.client.workspace.launch.SelectionCountEventTranslator;
import com.ericsson.eniq.events.ui.client.workspace.launch.WorkspaceLaunchMenuResourceBundle;
import com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu.WorkspaceFilter.WorkspaceStateItem;
import com.ericsson.eniq.events.widgets.client.collapse.CollapsePanel;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.view.client.*;
import com.google.gwt.view.client.DefaultSelectionEventManager.EventTranslator;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class WorkspaceFilterView implements IWorkspaceFilterView, SelectedItemHandler<WorkspaceStateItem> {

    private static final String LAUNCH_ON_STARTUP = "Launch on Startup";

    private static final ItemTemplate ITEM_TEMPLATE = GWT.create(ItemTemplate.class);

    private final WorkspaceLaunchMenuResourceBundle resourceBundle;

    private final HTMLPanel filterContentPanel;

    IWorkspaceSelectionHandler selectionHandler;

    private final EventTranslator<WorkspaceStateItem> eventTranslator = new SelectionCountEventTranslator<WorkspaceStateItem>(
            this);

    private final Set<WorkspaceStateItem> selectedItems = new HashSet<WorkspaceStateItem>();

    private CellList<WorkspaceStateItem> startupCellList;

    private CollapsePanel startupPanel;

    private CollapsePanel workspacePanel;

    private CellList<WorkspaceStateItem> workspaceCellList;

    /**
     * @param resourceBundle
     * @param filterContentPanel
     */
    public WorkspaceFilterView(WorkspaceLaunchMenuResourceBundle resourceBundle, HTMLPanel filterContentPanel) {
        this.resourceBundle = resourceBundle;
        this.filterContentPanel = filterContentPanel;
        createStartupWorkspacePanel();
        createWorkspacePanel();
    }

    private void createStartupWorkspacePanel() {
        startupCellList = createCellList(new MultiSelectionModel<WorkspaceStateItem>(), true);
        startupPanel = addToPanel(startupCellList);
    }

    private void createWorkspacePanel() {
        MultiSelectionModel<WorkspaceStateItem> selectionModel = new MultiSelectionModel<WorkspaceStateItem>();
        workspaceCellList = createCellList(selectionModel, false);

        workspaceCellList.setSelectionModel(selectionModel,
                DefaultSelectionEventManager.createCustomManager(eventTranslator));
        selectionModel.addSelectionChangeHandler(getSelectionChangeHandler(selectionModel));
        workspacePanel = addToPanel(workspaceCellList);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu.IWindowFilterView#clearFilter()
     */
    @Override
    public void clearFilter() {
        selectedItems.clear();
    }

    public void setWorkspaceSelectionHandler(IWorkspaceSelectionHandler selectionHandler) {
        this.selectionHandler = selectionHandler;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu.IWorkspaceFilterView#addFavouritesItem(com.google.gwt.view.client.ListDataProvider)
     */
    @Override
    public void addStartupItems(ListDataProvider<WorkspaceStateItem> dataProvider) {
        // Connect the list to the data provider.
        dataProvider.addDataDisplay(startupCellList);
        updateHeader(startupPanel, LAUNCH_ON_STARTUP, dataProvider.getList().size());
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.workspace.launch.workspacemenu.IWorkspaceFilterView#addSavedWorkspacesItem(com.google.gwt.view.client.ListDataProvider, String header)
     */
    @Override
    public void addWorkspacesItems(ListDataProvider<WorkspaceStateItem> dataProvider, String header) {
        dataProvider.addDataDisplay(workspaceCellList);
        updateHeader(workspacePanel, header, dataProvider.getList().size());
    }

    /**
     * @param panel
     * @param text
     * @param size
     */
    private void updateHeader(CollapsePanel panel, String text, int size) {
        panel.setText(text + " (" + size + ")");

    }

    protected CellList<WorkspaceStateItem> createCellList(final MultiSelectionModel<WorkspaceStateItem> selectionModel,
            boolean isFavouritesPanel) {
        Set<String> events = new HashSet<String>();
        events.add("click");
        if (!isFavouritesPanel) {
            events.add("dblclick");
        }
        
        CellList.Resources resources = GWT.create(WorkSpaceFilterCellListResources.class);
        CellList<WorkspaceStateItem> cellList = new CellList<WorkspaceStateItem>(new WorkspaceCell(events,isFavouritesPanel), resources) {
            /* (non-Javadoc)
             * @see com.google.gwt.user.cellview.client.CellList#onBrowserEvent2(com.google.gwt.user.client.Event)
             */
            @Override
            protected void onBrowserEvent2(Event event) {
                /** Handle click of favourites button in cell. Dont let item be selected if favourites clicked and unselect any selected items **/
                if (event.getType().equals("click")) {
                    if (!Element.is(event.getEventTarget())) {
                        return;
                    }
                    Element el = event.getEventTarget().cast();
                    if (el.getAttribute("__id").equals("favourites")) {
                        event.preventDefault();
                        event.stopPropagation();
                        for (WorkspaceStateItem item : selectionModel.getSelectedSet()) {
                            selectionModel.setSelected(item, false);
                        }
                        final Element target = event.getEventTarget().cast();

                        // Forward the event to the cell.
                        String idxString = "";
                        Element cellTarget = target;
                        while ((cellTarget != null) && ((idxString = cellTarget.getAttribute("__idx")).length() == 0)) {
                            cellTarget = cellTarget.getParentElement();
                        }
                        if (idxString.length() > 0) {
                            int idx = Integer.parseInt(idxString);
                            int indexOnPage = idx - getPageStart();
                            if (!isRowWithinBounds(indexOnPage)) {
                                // If the event causes us to page, then the index will be out of bounds.
                                return;
                            }

                            WorkspaceStateItem value = getVisibleItem(indexOnPage);
                            selectionHandler.onStartupItemUpdate(value.getWorkspaceState());
                            event.preventDefault();
                            event.stopPropagation();
                            return;
                        }
                    }
                }
                super.onBrowserEvent2(event);
            }
        };
        cellList.getElement().getStyle().setOverflow(Overflow.VISIBLE);
        cellList.setKeyboardSelectionPolicy(isFavouritesPanel ? KeyboardSelectionPolicy.DISABLED
                : KeyboardSelectionPolicy.ENABLED);
        return cellList;
    }

    protected Handler getSelectionChangeHandler(final MultiSelectionModel<WorkspaceStateItem> selectionModel) {
        return new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(final SelectionChangeEvent event) {
                selectedItems.clear();
                Set<WorkspaceStateItem> selected = selectionModel.getSelectedSet();
                if (selected != null) {
                    selectedItems.addAll(selected);
                }
                selectionHandler.onSelectionChange(selectedItems.size());
            }
        };
    }

    @Override
    public Collection<WorkspaceStateItem> getSelectedItems() {
        return selectedItems;
    }

    private class WorkspaceCell extends AbstractCell<WorkspaceStateItem> {

        private final boolean isFavouritesCell;

        public WorkspaceCell(Set<String> events, boolean isFavourites) {
            super(events);
            this.isFavouritesCell = isFavourites;
        }

        @Override
        public void render(final com.google.gwt.cell.client.Cell.Context context, final WorkspaceStateItem value,
                final SafeHtmlBuilder sb) {
            if (value != null) {
                String imageClass = "";
                /** id for onBrowserEvent2 of CellList to detect that this is an event on the favourites div **/
                String id = "favourites";
                String title = "";
                if (isFavouritesCell) {
                    imageClass = resourceBundle.workspaceLaunchStyle().removeFromFavourites();
                    title = "Remove from Launch at startup";
                    id = "favourites";
                } else if (!value.isLaunchOnStartup()) {
                    imageClass = resourceBundle.workspaceLaunchStyle().addToFavourites();
                    title = "Add to Launch at startup";
                } else {
                    /** Do nothing, favourites div is blank, in case where favourites are shown in saved workspaces collapsible **/
                    imageClass = resourceBundle.workspaceLaunchStyle().noFavouritesIcon();
                    id = "";
                }
                sb.append(ITEM_TEMPLATE.item(resourceBundle.workspaceLaunchStyle().workspaceItemHolder(), id, title,
                        imageClass, resourceBundle.workspaceLaunchStyle().workspaceFavourites(), resourceBundle
                                .workspaceLaunchStyle().workspaceListItem(), value.getFormattedName()));
            }
        }

        /* (non-Javadoc)
         * @see com.google.gwt.cell.client.AbstractCell#onBrowserEvent(com.google.gwt.cell.client.Cell.Context, com.google.gwt.dom.client.Element, java.lang.Object, com.google.gwt.dom.client.NativeEvent, com.google.gwt.cell.client.ValueUpdater)
         */
        @Override
        public void onBrowserEvent(final com.google.gwt.cell.client.Cell.Context context, final Element parent,
                final WorkspaceStateItem value, final NativeEvent event,
                final ValueUpdater<WorkspaceStateItem> valueUpdater) {
            if (event.getType().equals("dblclick")) {
                event.preventDefault();
                event.stopPropagation();
                if (selectionHandler != null) {
                    selectionHandler.onDoubleClick(value.getWorkspaceState());
                }
            }
            super.onBrowserEvent(context, parent, value, event, valueUpdater);
        }
    };

    /**
     * Creating collapsible panels for every filter, might not be efficient enough. If not just use display:none and block
     * @param cellList
     * @return 
     */
    private CollapsePanel addToPanel(final CellList<WorkspaceStateItem> cellList) {
        CollapsePanel collapsePanel = new CollapsePanel();
        collapsePanel.setContent(cellList);
        collapsePanel.setCollapsed(false);
        filterContentPanel.add(collapsePanel);
        return collapsePanel;
    }

    public interface ItemTemplate extends SafeHtmlTemplates {
        @Template("<div class=\"{0}\"><div __id=\"{1}\" title=\"{2}\" class" +
                "=\"{3} {4}\"></div><div " +
                "class=\"{5}\">{6}</div></div>")
        SafeHtml item(String workspaceItemHolder, String id, String title, String imageClass, String favouritesClass,
                String itemClass, SafeHtml item);
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.workspace.launch.SelectedItemHandler#getSelectedCount()
     */
    @Override
    public int getSelectedCount() {
        return selectedItems.size();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.workspace.launch.SelectedItemHandler#clearOtherCategorySelections(com.google.gwt.view.client.HasData)
     */
    @Override
    public void clearOtherCategorySelections(HasData<WorkspaceStateItem> hasData) {
        // DO nothing here for the moment, as we have no other categorys for workspace items as yet

    }
}
