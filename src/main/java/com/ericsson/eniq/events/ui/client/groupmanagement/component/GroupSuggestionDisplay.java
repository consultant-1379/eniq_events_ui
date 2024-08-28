/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.component;

import static com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementCellList;
import com.ericsson.eniq.events.ui.client.groupmanagement.IElementDoubleClickHandler;
import com.ericsson.eniq.events.ui.client.groupmanagement.ISelectionHandler;
import com.ericsson.eniq.events.ui.client.groupmanagement.component.GroupOracle.GroupSuggestion;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.resources.GroupMgmtResourceBundle;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionCallback;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Filter for a Suggest Box. GWT Suggest Box uses a Popup to display suggestions. We want a permanent widget to hold the suggestions.
 * This class does that
 * @author ecarsea
 * @since 2011
 *
 */
public class GroupSuggestionDisplay extends SuggestionDisplay {

    private final List<GroupSuggestion> suggestionList;

    private final GroupManagementCellList<GroupSuggestion> cellList;

    private IElementDoubleClickHandler doubleClickHandler;

    private ISelectionHandler iSelectionHandler;

    private static final SuggestMenuItemTemplate TEMPLATE = GWT.create(SuggestMenuItemTemplate.class);

    public GroupSuggestionDisplay(final HasWidgets itemContainer, final GroupMgmtResourceBundle resourceBundle,
            final boolean multiSelect, final int visibleLimit) {

        // Create a cell to render each value in the list.
        final AbstractCell<GroupSuggestion> cell = getGroupListItemCell(resourceBundle);

        // Create a CellList that uses the cell.
        cellList = new GroupManagementCellList<GroupSuggestion>(cell);
        cellList.getElement().getStyle().setOverflow(Overflow.VISIBLE);
        if (visibleLimit > 0) {
            cellList.setVisibleRange(0, visibleLimit);
        } else {
            cellList.setVisibleRange(0, CommonConstants.CELL_LIST_OFFSET_LIMIT);
        }
        // Create a data provider.
        final ListDataProvider<GroupSuggestion> dataProvider = new ListDataProvider<GroupSuggestion>();

        // Connect the list to the data provider.
        dataProvider.addDataDisplay(cellList);
        cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

        // Add a selection model to handle user selection.
        SelectionModel<GroupSuggestion> selectionModel = null;
        if (multiSelect) {
            selectionModel = new MultiSelectionModel<GroupSuggestion>();
        } else {
            selectionModel = new SingleSelectionModel<GroupSuggestion>();
        }
        cellList.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(getSelectionModel(multiSelect, selectionModel));
        itemContainer.add(cellList);
        suggestionList = dataProvider.getList();
    }

    protected Handler getSelectionModel(final boolean multiSelect, final SelectionModel<GroupSuggestion> selectionModel) {
        return new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(final SelectionChangeEvent event) {
                Set<GroupSuggestion> selected = null;
                if (multiSelect) {
                    selected = ((MultiSelectionModel<GroupSuggestion>) selectionModel).getSelectedSet();
                } else {
                    selected = new HashSet<GroupSuggestion>();
                    final GroupSuggestion selectedObject = ((SingleSelectionModel<GroupSuggestion>) selectionModel)
                            .getSelectedObject();
                    if (selectedObject != null) {
                        selected.add(selectedObject);
                    }
                }
                if (selected != null) {
                    onItemsSelected(new ArrayList<GroupSuggestion>(selected));
                }
            }
        };
    }

    protected AbstractCell<GroupSuggestion> getGroupListItemCell(final GroupMgmtResourceBundle resourceBundle) {
        return new AbstractCell<GroupSuggestion>("dblclick") {

            @Override
            public void render(final com.google.gwt.cell.client.Cell.Context context, final GroupSuggestion value,
                    final SafeHtmlBuilder sb) {
                if (value != null) {
                    final SafeHtml safeHtml = TEMPLATE.groupSuggestMenuItem(resourceBundle.style().suggestionItem(),
                            toSafeHtml(value.getDisplayString()));
                    sb.append(safeHtml);
                }
            }

            /* (non-Javadoc)
             * @see com.google.gwt.cell.client.AbstractCell#onBrowserEvent(com.google.gwt.cell.client.Cell.Context, com.google.gwt.dom.client.Element, java.lang.Object, com.google.gwt.dom.client.NativeEvent, com.google.gwt.cell.client.ValueUpdater)
             */
            @Override
            public void onBrowserEvent(final com.google.gwt.cell.client.Cell.Context context, final Element parent,
                    final GroupSuggestion value, final NativeEvent event,
                    final ValueUpdater<GroupSuggestion> valueUpdater) {
                if (event.getType().equals("dblclick")) {
                    event.preventDefault();
                    event.stopPropagation();
                    onDoubleClickSelection(value.getGroupElement());
                }
                super.onBrowserEvent(context, parent, value, event, valueUpdater);
            }

        };
    }

    protected void onDoubleClickSelection(final GroupListItem value) {
        if (doubleClickHandler != null) {
            doubleClickHandler.onElementDoubleClicked(value);
        }
    }

    protected void onItemsSelected(final List<GroupSuggestion> selectedItems) {
        if (iSelectionHandler != null) {
            List<GroupListItem> itemList = new ArrayList<GroupListItem>();
            for (GroupSuggestion gs : selectedItems) {
                itemList.add(gs.getGroupElement());
            }
            iSelectionHandler.onItemsSelected(itemList);
        }
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.SuggestBox.SuggestionDisplay#getCurrentSelection()
     */
    @Override
    protected Suggestion getCurrentSelection() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.SuggestBox.SuggestionDisplay#moveSelectionDown()
     */
    @Override
    protected void moveSelectionDown() {
        // do nothing, handled by cell list
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.SuggestBox.SuggestionDisplay#moveSelectionUp()
     */
    @Override
    protected void moveSelectionUp() {
        // do nothing, handled by cell list
    }

    @Override
    public void hideSuggestions() {
        suggestionList.clear();
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.SuggestBox.SuggestionDisplay#showSuggestions(com.google.gwt.user.client.ui.SuggestBox, java.util.Collection, boolean, boolean, com.google.gwt.user.client.ui.SuggestBox.SuggestionCallback)
     */
    @Override
    protected void showSuggestions(final SuggestBox suggestBox, final Collection<? extends Suggestion> suggestions,
            final boolean isDisplayStringHTML, final boolean isAutoSelectEnabled, final SuggestionCallback callback) {
        clearSelections();
        suggestionList.clear();
        // Hide the suggestions if none.
        if (suggestions == null) {
            return;
        }

        for (final Suggestion curSuggestion : suggestions) {
            suggestionList.add(((GroupSuggestion) curSuggestion));
        }
    }

    /**
     * 
     */
    private void clearSelections() {
        final SelectionModel<? super GroupSuggestion> selectionModel = cellList.getSelectionModel();
        if (selectionModel instanceof SingleSelectionModel) {

            @SuppressWarnings("unchecked")
            final SingleSelectionModel<GroupSuggestion> sm = (SingleSelectionModel<GroupSuggestion>) selectionModel;
            final GroupSuggestion selectedObject = sm.getSelectedObject();
            if (selectedObject != null) {
                selectionModel.setSelected(selectedObject, false);
            }
        } else {
            @SuppressWarnings("unchecked")
            final MultiSelectionModel<GroupSuggestion> sm = (MultiSelectionModel<GroupSuggestion>) selectionModel;
            final Set<GroupSuggestion> selectedSet = sm.getSelectedSet();
            for (final GroupSuggestion obj : selectedSet) {
                sm.setSelected(obj, false);
            }
        }
    }

    /**
     * @param str
     * @return
     */
    private static SafeHtml toSafeHtml(final String str) {
        /** No user input here really, just retrieving from our group list oracle which comes from our services which we trust.**/
        return SafeHtmlUtils.fromTrustedString(str);
    }

    public interface SuggestMenuItemTemplate extends SafeHtmlTemplates {
        @Template("<div class=\"{0}\">{1}</div>")
        SafeHtml groupSuggestMenuItem(String suggestionItemClass, SafeHtml suggestion);
    }

    /**
     * @param handler
     */
    public void setDoubleClickHandler(final IElementDoubleClickHandler handler) {
        this.doubleClickHandler = handler;
    }

    /**
     * @param handler
     */
    public void setSelectionHandler(final ISelectionHandler handler) {
        this.iSelectionHandler = handler;
    }

    /**
     * @param listSize
     */
    public void setListSize(final int listSize) {
        cellList.setVisibleRange(0, listSize);
    }
}
