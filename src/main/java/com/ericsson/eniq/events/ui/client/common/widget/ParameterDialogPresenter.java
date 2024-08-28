/**
 * 
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * 
 */
package com.ericsson.eniq.events.ui.client.common.widget;

import com.ericsson.eniq.events.common.client.mvp.BasePresenter;
import com.ericsson.eniq.events.ui.client.common.comp.BaseWindow;
import com.ericsson.eniq.events.ui.client.mvp.WidgetDisplay;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author eemecoy
 *
 */
public abstract class ParameterDialogPresenter<D extends WidgetDisplay> extends BasePresenter<D> {

    protected final IExtendedWidgetDisplay gridOrChartRef;
    
    private ParameterDialog parameterDialog;

    private final ClickHandler dlgButtonListener;

    /**
     * @param view
     * @param eventBus
     */
    public ParameterDialogPresenter(final D view, final EventBus eventBus) {
        super(view, eventBus);

        /* cast to a parent view on a grid or a chart */
        gridOrChartRef = (IExtendedWidgetDisplay) view;
        dlgButtonListener = new DialogButtonListener();
    }

    void setDialog(final ParameterDialog parameterDialog) {
        this.parameterDialog = parameterDialog;
    }

    ParameterDialog getDialog() {
        return parameterDialog;
    }

    void initDisplay(final BaseWindow dialogParent) {
        /* add a component to the ParentWindow of the Grid */
        addToParentWindow(parameterDialog);
        
        final Rectangle dialogCoords = getCenterCoords(parameterDialog, dialogParent);
        parameterDialog.setScreenCoords(dialogCoords);

        /* get a handle to the OK Button on Dialog */
        final Button okBtn = parameterDialog.getUpdateButton();
        okBtn.addClickHandler(dlgButtonListener);

        /* get a handle to the Cancel Button on Dialog and add listener */
        final Button cnlBtn = parameterDialog.getCancelButton();
        cnlBtn.addClickHandler(dlgButtonListener);
    }

    /*
     * Position the dialog in the centre of the Parent Window.
     */
    private Rectangle getCenterCoords(final ParameterDialog dlgChild, final BaseWindow dlgParent) {

        final Rectangle location = new Rectangle();

        final Point topLeft = dlgParent.getWidget().getPosition(false);
        final Size parentSize = dlgParent.getWidget().getSize();

        /* ensure the child window been launched is not wider than the parent window */
        if (parentSize.width > dlgChild.getWidth()) {
            location.x = ((parentSize.width - dlgChild.getWidth()) / 2) + topLeft.x;
        } else {
            location.x = topLeft.x;
        }

        /*
        * ensure the child window been launched is not taller than the parent
        * window
        */
        if (parentSize.height > dlgChild.getHeight()) {
            location.y = ((parentSize.height - dlgChild.getHeight()) / 2) + topLeft.y;
        } else {
            location.y = topLeft.y;
        }
        return location;
    }
    
    /*
     * add a component to the ParentWindow of the Grid/Chart
     */
    void addToParentWindow(final Component item) {
        gridOrChartRef.getParentWindow().getWidget().add(item);
    }

    /*
     * remove the component from the ParentWindow of the Grid/Chart
     */
    void removeFromParentWindow(final Component item) {
        gridOrChartRef.getParentWindow().getWidget().remove(item);
    }

    /*
     * Listener Class for the OK button click event from the Time Dialog
     */
    private final class DialogButtonListener implements ClickHandler {

        @Override
        public void onClick(final ClickEvent event) {
            final Object source = event.getSource();
            if (source.equals(parameterDialog.getUpdateButton())) {
                handleOK();
            } else if (source.equals(parameterDialog.getCancelButton())) {
                handleCancel();
            }
        }

        /*
        * event handler for OK button on dialog
        */
        private void handleOK() {
            /* get the time parameters provided */
            if (parameterDialog.validate()) {
                //these should be pulled down into subclass
                handleSuccessfulEvent();
                //final TimeInfoDataType userTimeDetails = dlgTime.getUserTimeSelection();
                // MVP pattern does not allow talk to window direct so using eventBus
                //getEventBus().fireEvent(new TimeParameterValueChangeEvent(multiWinID, userTimeDetails));
                /* close method is depreciated */

                //this should be common
                closeDialog();
            }
        }

        /*
        * event handler for cancel button on dialog
        */
        private void handleCancel() {
            closeDialog();
        }

        private void closeDialog() {
            /*
            * important (for wizard overlay) to remove this when close time dialog /
            * (as this dialog is modal  assuming parent can not be null now
            */
            removeFromParentWindow(parameterDialog);
            parameterDialog.close();
        }
    }

    public abstract void handleSuccessfulEvent();

}
