package com.ericsson.eniq.events.ui.client.datatype.dashboard;

import com.ericsson.eniq.events.common.client.datatype.ParametersDataType;

/**
 * @author evyagrz
 * @since 10 2011
 */
public interface IPortletDataType {
    ////////////////    run time postion info  ////////////////
    int getColumnIndex();

    void setColumnIndex(int columnIndex);

    int getRowIndex();

    void setRowIndex(int rowIndex);

    String getTabOwnerId();

    String getPortletId();

    String getPortletTitle();

    int getPortletHeight();

    String getURL();

    boolean isSearchFieldUser();

    String getDisplayType();

    //TODO use or loose
    PortletType getType();

    void setType(PortletType type);

    ParametersDataType getParameters();
}
