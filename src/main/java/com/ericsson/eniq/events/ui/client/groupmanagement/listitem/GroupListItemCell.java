/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.listitem;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public class GroupListItemCell extends AbstractSafeHtmlCell<GroupListItem> {
    /**
     * Constructs a GroupListItemCell that uses a {@link SimpleSafeHtmlRenderer} to render
     * its text.
     */
    public GroupListItemCell() {
        super(new SafeHtmlRenderer<GroupListItem>() {
            @Override
            public SafeHtml render(GroupListItem object) {
                return SafeHtmlUtils.fromString(object.getStringValue());
            }

            @Override
            public void render(GroupListItem object, SafeHtmlBuilder appendable) {
                appendable.append(SafeHtmlUtils.fromString(object.getStringValue()));
            }
        });
    }

    /**
     * Constructs a GroupListItemCell that uses the provided {@link SafeHtmlRenderer} to
     * render its text.
     * 
     * @param renderer a {@link SafeHtmlRenderer SafeHtmlRenderer<String>} instance
     */
    public GroupListItemCell(SafeHtmlRenderer<GroupListItem> renderer) {
        super(renderer);
    }

    /* (non-Javadoc)
     * @see com.google.gwt.cell.client.AbstractSafeHtmlCell#render(com.google.gwt.cell.client.Cell.Context, com.google.gwt.safehtml.shared.SafeHtml, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
     */
    @Override
    public void render(Context context, SafeHtml value, SafeHtmlBuilder sb) {
        if (value != null) {
            sb.append(value);
        }
    }
}
