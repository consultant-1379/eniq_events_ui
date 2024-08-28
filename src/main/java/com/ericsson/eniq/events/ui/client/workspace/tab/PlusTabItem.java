/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.tab;

import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.widget.TabItem;

/**
 * @author ecarsea
 * @author ealeerm - Alexey Ermykin
 * @since 06/2012
 */
public class PlusTabItem extends TabItem {
    private final String headerDisabledStyle;

    public PlusTabItem(String text, String tabStyle, String iconStyle, String disabledStyle) {
        this.headerDisabledStyle = disabledStyle;
        setText(text);
        StringBuilder sb = new StringBuilder();
        sb.append("<li class='{style} ").append(tabStyle).append("' id={id} role='tab' tabindex='0' title='New Workspace Tab'><a class=x-tab-strip-close role='presentation'></a>");
        sb.append("<a class='x-tab-right'  role='presentation'><em role='presentation' class='x-tab-left'>");
        sb.append("<span class='x-tab-strip-inner' role='presentation'><span class='x-tab-strip-text {textStyle}'>{text}</span></span>");
        sb.append("</em></a><div class='").append(iconStyle).append("'></div></li>");
        Template itemTemplate = new Template(sb.toString());
        itemTemplate.compile();
        template = itemTemplate;
    }

    @Override
    public void disable() {
        super.disable();
        header.addStyleName(headerDisabledStyle);
    }

    @Override
    public void enable() {
        super.enable();
        header.removeStyleName(headerDisabledStyle);
    }
}