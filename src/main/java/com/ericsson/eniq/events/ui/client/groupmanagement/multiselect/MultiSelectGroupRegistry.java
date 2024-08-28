/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.multiselect;

import com.ericsson.eniq.events.ui.client.datatype.group.GroupMgmtConfigDataType;
import com.ericsson.eniq.events.ui.client.groupmanagement.GroupElementRetriever;

/**
 * @author ecarsea
 * @since 2012
 *
 */
public interface MultiSelectGroupRegistry {

    /**
     * @param name
     * @return
     */
    AbstractMultiSelectPresenter getMultiSelectPresenter(String name);

    /**
     * @param name
     * @param configDataType
     * @return
     */
    GroupElementRetriever getGroupElementRetriever(String name, GroupMgmtConfigDataType configDataType);

}
