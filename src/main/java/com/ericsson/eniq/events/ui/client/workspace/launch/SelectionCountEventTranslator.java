/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.workspace.launch;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager.EventTranslator;
import com.google.gwt.view.client.DefaultSelectionEventManager.SelectAction;

/**
 * Event Translator for GWT Cell Data Based widgets. Prevents selection of further items above a set maximum
 * @author ecarsea
 * @since 2012
 *
 */
public class SelectionCountEventTranslator<T> implements EventTranslator<T> {

    private final SelectedItemHandler<T> handler;

    public SelectionCountEventTranslator(SelectedItemHandler<T> handler) {
        this.handler = handler;
    }

    @Override
    public boolean clearCurrentSelection(CellPreviewEvent<T> event) {
        NativeEvent nativeEvent = event.getNativeEvent();
        /*
         * Update selection on click. Selection is toggled only if the user
         * presses the ctrl key. If the user does not press the control key,
         * selection is additive.
         */
        boolean ctrlOrMeta = nativeEvent.getCtrlKey() || nativeEvent.getMetaKey();
        return !ctrlOrMeta;
    }

    @Override
    public SelectAction translateSelectionEvent(CellPreviewEvent<T> event) {
        NativeEvent nativeEvent = event.getNativeEvent();
        if ("click".equals(nativeEvent.getType())) {

            /**
             * Update selection on click. Selection is toggled only if the user
             * presses the ctrl key. If the user does not press the control key,
             * selection is additive.
             */
            boolean ctrlOrMeta = nativeEvent.getCtrlKey() || nativeEvent.getMetaKey();
            if (!ctrlOrMeta) {
                /** Item selected without the ctrl or meta being held, so toggle off selections in other categories **/
                handler.clearOtherCategorySelections(event.getDisplay());
            }
        }
        return SelectAction.DEFAULT;
    }
}
