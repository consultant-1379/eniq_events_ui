/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.ericsson.eniq.events.ui.client.common.Constants;
import com.ericsson.eniq.events.ui.client.workspace.component.CheckboxCollapsePanel;
import com.ericsson.eniq.events.ui.client.workspace.launch.SelectedItemHandler;
import com.ericsson.eniq.events.ui.client.workspace.launch.SelectionCountEventTranslator;
import com.ericsson.eniq.events.ui.client.workspace.launch.WorkspaceLaunchMenuResourceBundle;
import com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu.WindowFilter.WindowItem;
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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.DefaultSelectionEventManager.EventTranslator;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class WindowFilterView implements IWindowFilterView, SelectedItemHandler<WindowItem> {

    private static final ItemTemplate ITEM_TEMPLATE = GWT.create(ItemTemplate.class);

    private final WorkspaceLaunchMenuResourceBundle resourceBundle;

    private final HTMLPanel filterContentPanel;

    private IWindowSelectionHandler selectionHandler;

    private final Set<HasData<WindowItem>> selectedDataLists = new HashSet<HasData<WindowItem>>();

    /**
     * Keep Category Panels while filtering so as expanded panels will remain if they contain windows
     */
    private final Map<String, CheckboxCollapsePanel> categoryPanels = new TreeMap<String, CheckboxCollapsePanel>();

    private final EventTranslator<WindowItem> eventTranslator = new SelectionCountEventTranslator<WindowItem>(this);

    /**
     * @param resourceBundle
     * @param filterContentPanel
     */
    public WindowFilterView(WorkspaceLaunchMenuResourceBundle resourceBundle, HTMLPanel filterContentPanel) {
        this.resourceBundle = resourceBundle;
        this.filterContentPanel = filterContentPanel;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu.IWindowFilterView#clearFilter()
     */
    @Override
    public void clearFilter() {
        selectedDataLists.clear();
    }

    public void setWindowSelectionHandler(IWindowSelectionHandler selectionHandler) {
        this.selectionHandler = selectionHandler;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu.IWindowFilterView#addItem(java.lang.String, com.google.gwt.view.client.ListDataProvider)
     */
    @Override
    public void addItem(String name, ListDataProvider<WindowItem> dataProvider) {
        final CellList<WindowItem> cellList = new CellList<WindowItem>(new WindowCell());
        // Connect the list to the data provider.
        dataProvider.addDataDisplay(cellList);
        cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
        cellList.getElement().getStyle().setOverflow(Overflow.VISIBLE);
        final MultiSelectionModel<WindowItem> selectionModel = new MultiSelectionModel<WindowItem>();
        cellList.setSelectionModel(selectionModel, DefaultSelectionEventManager.createCustomManager(eventTranslator));
        selectionModel.addSelectionChangeHandler(getSelectionChangeHandler(cellList, selectionModel));
        addToPanel(cellList, name, dataProvider.getList());
    }

    protected Handler getSelectionChangeHandler(final CellList<WindowItem> cellList,
            final MultiSelectionModel<WindowItem> selectionModel) {
        return new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(final SelectionChangeEvent event) {
                Set<WindowItem> selected = selectionModel.getSelectedSet();
                if (selected != null) {
                    selectedDataLists.add(cellList);
                } else {
                    selectedDataLists.remove(cellList);
                }
                int selectedWindows = getSelectedCount();
                selectionHandler.onSelectionChange(selectedWindows);
            }
        };
    }

    @Override
    public List<WindowItem> getSelectedItems() {
        List<WindowItem> selectedItemList = new ArrayList<WindowItem>();
        for (HasData<WindowItem> dataList : selectedDataLists) {
            for (WindowItem wi : dataList.getVisibleItems())
                if (dataList.getSelectionModel().isSelected(wi)) {
                    selectedItemList.add(wi);
                }
        }
        return selectedItemList;
    }

    private class WindowCell extends AbstractCell<WindowItem> {

        public WindowCell() {
            super("dblclick");
        }

        @Override
        public void render(final com.google.gwt.cell.client.Cell.Context context, final WindowItem value,
                final SafeHtmlBuilder sb) {
            if (value != null) {
                sb.append(ITEM_TEMPLATE.item(resourceBundle.workspaceLaunchStyle().windowListItem(),
                        value.getFormattedName(), value.getWindow().getId()));
            }
        }

        /* (non-Javadoc)
         * @see com.google.gwt.cell.client.AbstractCell#onBrowserEvent(com.google.gwt.cell.client.Cell.Context, com.google.gwt.dom.client.Element, java.lang.Object, com.google.gwt.dom.client.NativeEvent, com.google.gwt.cell.client.ValueUpdater)
         */
        @Override
        public void onBrowserEvent(final com.google.gwt.cell.client.Cell.Context context, final Element parent,
                final WindowItem value, final NativeEvent event, final ValueUpdater<WindowItem> valueUpdater) {
            if (event.getType().equals("dblclick")) {
                event.preventDefault();
                event.stopPropagation();
                if (selectionHandler != null) {
                    selectionHandler.onDoubleClick(value.getWindow());
                }
            }
            super.onBrowserEvent(context, parent, value, event, valueUpdater);
        }
    };

    public interface ItemTemplate extends SafeHtmlTemplates {
        @Template("<div id=\"{2}\" class=\"{0}\">{1}</div>")
        SafeHtml item(String itemClass, SafeHtml item, String id);
    }

    /**
     * Creating collapsible panels for every filter, might not be efficient enough. If not just use display:none and block
     * @param cellList
     * @param header
     * @param data
     */
    private void addToPanel(final CellList<WindowItem> cellList, String header, final List<WindowItem> data) {
        CheckboxCollapsePanel categoryPanel = getCategoryPanel(header);
        categoryPanel.setText(getFullHeader(header, data.size()));
        categoryPanel.getElement().setId(Constants.SELENIUM_TAG + "categoryPanel");
        categoryPanel.setContent(cellList);
        categoryPanels.put(header, categoryPanel);
    }

    private CheckboxCollapsePanel getCategoryPanel(String name) {
        CheckboxCollapsePanel collapsePanel = null;
        if (categoryPanels.containsKey(name)) {
            collapsePanel = categoryPanels.get(name);
        } else {
            collapsePanel = new CheckboxCollapsePanel();
        }
        return collapsePanel;
    }

    /* (non-Javadoc)
    * @see com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu.IWindowFilterView#addAllCategoryPanels()
    */
    @Override
    public void addAllCategoryPanels() {
        Collection<CheckboxCollapsePanel> allCategoryPanels = categoryPanels.values();
        for (CheckboxCollapsePanel categoryPanel : allCategoryPanels) {
            filterContentPanel.add(categoryPanel);
        }
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.workspace.launch.windowmenu.IWindowFilterView#removeAllCategoryPanels()
     */
    @Override
    public void removeAllCategoryPanels() {
        String [] keys = categoryPanels.keySet().toArray(new String[categoryPanels.keySet().size()]);

        for(String key : keys) {
                filterContentPanel.remove(categoryPanels.get(key));
                categoryPanels.remove(key);
        }
    }

    protected String getFullHeader(String header, int windowTotal) {
        return header + " (" + windowTotal + ")";
    }

    @Override
    public int getSelectedCount() {
        int selectedWindows = 0;
        for (HasData<WindowItem> dataList : selectedDataLists) {
            for (WindowItem wi : dataList.getVisibleItems())
                if (dataList.getSelectionModel().isSelected(wi)) {
                    selectedWindows++;
                }
        }
        return selectedWindows;
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.workspace.launch.SelectedItemHandler#clearOtherCategorySelections(com.google.gwt.view.client.HasData)
     */
    @Override
    public void clearOtherCategorySelections(HasData<WindowItem> dataList) {
        for (HasData<WindowItem> dl : selectedDataLists) {
            if (!dl.equals(dataList)) {
                for (WindowItem item : dl.getVisibleItems()) {
                    dl.getSelectionModel().setSelected(item, false);
                }
            }
        }
    }
}
