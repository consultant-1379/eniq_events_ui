package com.ericsson.eniq.events.ui.client.groupmanagement.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ericsson.eniq.events.ui.client.groupmanagement.IElementDoubleClickHandler;
import com.ericsson.eniq.events.ui.client.groupmanagement.ISelectionHandler;
import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;
import com.ericsson.eniq.events.ui.client.groupmanagement.resources.GroupMgmtResourceBundle;
import com.ericsson.eniq.events.widgets.client.textbox.ExtendedTextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

public class FilterPanel extends Composite {

    private static FilterPanelUiBinder uiBinder = GWT.create(FilterPanelUiBinder.class);

    interface FilterPanelUiBinder extends UiBinder<Widget, FilterPanel> {
    }

    @UiField
    Label header;

    @UiField
    ExtendedTextBox filter;

    @UiField
    SimplePanel elementList;

    @UiField
    HTMLPanel glassPanel;

    GroupOracle oracle;

    @UiField
    HTMLPanel filterBoxHolder;

    private SuggestBox suggestBox;

    @UiField
    GroupMgmtResourceBundle resourceBundle;

    private GroupSuggestionDisplay groupSuggestionDisplay;

    private Collection<GroupListItem> suggestions = new ArrayList<GroupListItem>();

    private List<RefreshSuggessionHandler> handlers;

    public FilterPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void init(final GroupMgmtResourceBundle resourceBundle) {
        init(resourceBundle, true);
    }

    /**
     * @param resourceBundle
     * @param multiSelect
     */
    public void init(final GroupMgmtResourceBundle resourceBundle, final boolean multiSelect) {
        init(resourceBundle, new LocalSuggestOracle(), multiSelect, 300);
    }

    public void init(final GroupMgmtResourceBundle resourceBundle, final GroupOracle oracle) {
        init(resourceBundle, oracle, true, -1);
    }

    public void init(final GroupMgmtResourceBundle resourceBundle, final GroupOracle oracle, final boolean multiSelect,
            final int visibleLimit) {
        this.resourceBundle = resourceBundle;
        this.oracle = oracle;
        groupSuggestionDisplay = new GroupSuggestionDisplay(elementList, resourceBundle, multiSelect, visibleLimit);
        suggestBox = new SuggestBox(oracle, filter, groupSuggestionDisplay) {
            /* (non-Javadoc)
            * @see com.google.gwt.user.client.ui.SuggestBox#getText()
            */
            @Override
            public String getText() {
                if (((ExtendedTextBox) getTextBox()).containsDefaultText()) {
                    return "";
                }
                return super.getText();
            }
        };
        filter.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(final KeyUpEvent event) {
                if (handlers != null) {
                    for (final RefreshSuggessionHandler handler : handlers) {
                        handler.onSuggestionRefresh();
                    }
                }
            }
        });
        /** Need to explicitly enable default text for the text box here as SuggestBox will override all styles **/
        filter.enableDefaultText();
        filterBoxHolder.add(suggestBox);
    }

    public void setHeader(final String header) {
        this.header.setText(header);
    }

    public void setId(final String id) {
        getElement().setId(id);
    }

    public void setElementDoubleClickHandler(final IElementDoubleClickHandler handler) {
        groupSuggestionDisplay.setDoubleClickHandler(handler);
    }

    /** @param suggestions  */
    public void setSuggestions(final Collection<GroupListItem> suggestions) {
        this.suggestions = suggestions;
        refreshSuggestBox();
    }

    public void refreshSuggestBox() {
        oracle.clear();
        oracle.addAll(suggestions);
        oracle.setDefaultSuggestionsFromText(suggestions);
        suggestBox.showSuggestionList();
    }

    /** @param enabled  */
    public void setEnabled(final boolean enabled) {
        suggestBox.getTextBox().setEnabled(enabled);
        if (enabled) {
            glassPanel.removeStyleName(resourceBundle.style().enabled());
        } else {
            glassPanel.addStyleName(resourceBundle.style().enabled());
        }
    }

    /** @param iSelectionHandler  */
    public void setItemSelectionHandler(final ISelectionHandler iSelectionHandler) {
        groupSuggestionDisplay.setSelectionHandler(iSelectionHandler);

    }

    /** @return  */
    public Collection<GroupListItem> getAllElements() {
        return this.suggestions;
    }

    /** @param groupContents  */
    public void addSuggestions(final Collection<GroupListItem> groupContents) {
        // No duplicates
        for (GroupListItem groupElement : groupContents) {
            if (!this.suggestions.contains(groupElement)) {
                this.suggestions.add(groupElement);
            }
        }
        refreshSuggestBox();
    }

    public void showSuggestionList() {
        suggestBox.showSuggestionList();
    }

    public void clear() {
        filter.setText("");
        filter.enableDefaultText();
    }

    /** @param suggestionListSize  */
    public void setSuggestionListSize(final int suggestionListSize) {
        groupSuggestionDisplay.setListSize(suggestionListSize);
    }

    /** @return the header */
    public String getHeader() {
        return header.getText();
    }

    public void addRefreshSuggessionHandler(final RefreshSuggessionHandler handler) {
        if (handlers == null) {
            handlers = new ArrayList<FilterPanel.RefreshSuggessionHandler>();
        }
        handlers.add(handler);
    }

    public interface RefreshSuggessionHandler {
        void onSuggestionRefresh();
    }
}
