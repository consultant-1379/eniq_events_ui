/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2013 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.listitem.terminal;

import com.ericsson.eniq.events.ui.client.groupmanagement.listitem.GroupListItem;

/**
 * @author eeikbe
 * @since 05/2013
 */
public class GroupListTerminalTACItem implements GroupListItem{
    
    private final GroupListTerminalModelItem modelItem;
    
    public GroupListTerminalTACItem(final GroupListTerminalModelItem modelItem){
        this.modelItem = modelItem;
    }
    
    public String getTac(){
        return modelItem.getTac();    
    } 
    
    public String getModel(){
        return modelItem.getModel();
    }

    @Override
    public String getStringValue() {
        return getTac();

    }

    @Override
    public String[] getKeyValues() {
        return new String[] {this.getTac()};

    }
}
