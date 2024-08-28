/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.common.comp;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.events.TimeParameterValueChangeEventHandler;

/**
 * BaseWindowPresenter time change handler
 * 
 * @author eeicmsy
 * @since July 2010
 *
 */
public class BaseWinTimeParameterValueHandler<D extends IBaseWindowView> implements TimeParameterValueChangeEventHandler {

    private final BaseWindowPresenter<D> baseWinPresenter;

    /**
     * Base window time update handler
     * @param baseWindowPresenter   - main window presenter
     */
    public BaseWinTimeParameterValueHandler(final BaseWindowPresenter<D> baseWindowPresenter) {
        this.baseWinPresenter = baseWindowPresenter;

    }

    /*
     * handles time parameters updates on this window from event bus broadcast
     */
    @Override
    public void handleTimeParamUpdate(final MultipleInstanceWinId multiWinId, final TimeInfoDataType time) {

        // guards
        if (baseWinPresenter.isThisWindowGuardCheck(multiWinId)) {
            //refreshing the time on the window, this is not a drill folks!
            this.baseWinPresenter.setIsDrillDown(false);
            handleTimeParamUpdate(time);

        }
    }

    @Override
    public void handleTimeParamUpdate(final TimeInfoDataType time) {

        /* update provided Time Parameters*/
        baseWinPresenter.setTimeData(time);
        baseWinPresenter.makeServerCallWithURLParams();

    }

}
