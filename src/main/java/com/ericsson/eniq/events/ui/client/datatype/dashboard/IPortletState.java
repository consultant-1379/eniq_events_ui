package com.ericsson.eniq.events.ui.client.datatype.dashboard;

import java.util.List;

import com.ericsson.eniq.events.common.client.datatype.IThresholdState;

/**
 * @author evyagrz
 * @since 10 2011
 */
public interface IPortletState {
	
    String getPortletId();

    void setPortletId(String portletId);

    int getColumnIndex();

    void setColumnIndex(int columnIndex);

    int getRowIndex();

    void setRowIndex(int rowIndex);

    boolean isEmpty();

    void setEmpty(boolean empty);
    
    List<IThresholdState> getThresholds();
    
    void setThresholds(List<IThresholdState> thresholds);
    
}
