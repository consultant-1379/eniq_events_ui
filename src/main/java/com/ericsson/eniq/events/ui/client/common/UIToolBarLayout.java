package com.ericsson.eniq.events.ui.client.common;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.layout.ToolBarLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.Label;


/**
 * Created by IntelliJ IDEA.
 * User: xprevis
 * Date: 11/1/13
 * Time: 8:28 AM
 * To change this template use File | Settings | File Templates.
 *
 * Extended to include Label and LabelToolItem used in FooterToolBa, to appe
 */
public class UIToolBarLayout extends ToolBarLayout{

    @Override
    protected void addComponentToMenu(Menu menu, Component c) {

        if (c instanceof Label || c instanceof LabelToolItem) {
            addLabelToMenu(menu,c);
        } else {
            super.addComponentToMenu(menu,c);
        }

        if (menu.getItemCount() > 0) {
            if (menu.getItem(0) instanceof SeparatorMenuItem) {
                menu.remove(menu.getItem(0));
            }
            if (menu.getItemCount() > 0) {
                if (menu.getItem(menu.getItemCount() - 1) instanceof SeparatorMenuItem) {
                    menu.remove(menu.getItem(menu.getItemCount() - 1));
                }
            }
        }
    }

    private void addLabelToMenu(Menu menu, Component c){
        Label item;
        if(c instanceof  Label) {
            final Label label = (Label) c;
            item = new Label(label.getText());
        } else {
            final LabelToolItem label = (LabelToolItem) c;
            item = new Label(label.getLabel());
        }
        item.setItemId(c.getItemId());
        item.setStyleAttribute("font-weight","bold");
        menu.add(item);
    }
}

