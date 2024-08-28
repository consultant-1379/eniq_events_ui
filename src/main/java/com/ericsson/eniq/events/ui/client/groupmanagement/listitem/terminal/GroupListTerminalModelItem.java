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
public class GroupListTerminalModelItem implements GroupListItem {
    
    private final String model;
    
    private final String tac;
    
    public GroupListTerminalModelItem(final String modelTac){
        String[] result = modelTac.split(",");
        this.model = result[0];
        this.tac = result[1];
    }
    @Override
    public String getStringValue() {
        StringBuilder modelAndTac = new StringBuilder();
        modelAndTac.append(this.model);
        modelAndTac.append(",");
        modelAndTac.append(this.tac);
        return modelAndTac.toString();

    }

    @Override
    public String[] getKeyValues() {
        return new String[] { this.getModel(), this.getTac() };

    }

    public String getTac() {
        return this.tac;
    }

    public String getModel() {
        return this.model;
    }
}
