/**
 *
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 *
 */
package com.ericsson.eniq.events.ui.client.common.widget;

import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import static com.ericsson.eniq.events.ui.client.common.Constants.*;

/** @author eemecoy */
public abstract class ParameterDialog extends LayoutContainer implements IDialog {

   private final String dialogTitle;

    private final static int MAX_TEXT_LENGTH_FOR_DIALOG = 50;

    private final static String ELLIPSE = "...";

   private static final int DIALOG_HEIGHT = 188;

   static final int DIALOG_WIDTH = 355;

   private final static String DIALOG_NAME = "Time Settings";

   private final static String DIALOG_BODY_STYLE = "dlgBody";

   private final Dialog parameterDialog = new Dialog();

   private Button updateButton;

   private Button cancelButton;

   /** @param dialogTitle  */
   public ParameterDialog(final String dialogTitle) {
      this.dialogTitle = dialogTitle;
      setupDialog();
   }

    /**
     * Add 'three-dots' text ellipsis to the end of the dialog heading when the size of the heading
     * is more than MAX_TEXT_LENGTH_FOR_DIALOG characters.
     *
     * @param header  The original heading.
     * @return        The heading with the ELLIPSE at the end.
     */
    private String formatLength(final String header) {
        if (header.length() > MAX_TEXT_LENGTH_FOR_DIALOG) {
            return header.substring(0, MAX_TEXT_LENGTH_FOR_DIALOG) + ELLIPSE;
        }
        return header;
    }

   private void setupDialog() {
      parameterDialog.setModal(true);
      parameterDialog.setHeading(formatLength(dialogTitle + DASH + DIALOG_NAME));
      parameterDialog.getElement("header").setAttribute("wrap", "on");

      // Leo Dillion requests "OK" in uppercase
      parameterDialog.okText = parameterDialog.okText.toUpperCase();
      parameterDialog.setButtons(EMPTY_STRING); // We will use our own buttons

      parameterDialog.setClosable(false);
      parameterDialog.setBodyStyleName(DIALOG_BODY_STYLE);
      parameterDialog.setResizable(false);
      parameterDialog.setConstrain(true);

      parameterDialog.setHeight(DIALOG_HEIGHT);
      parameterDialog.setWidth(DIALOG_WIDTH);
   }

   /** Positions the Dialog on the screen based on the provided coordinates */
   @Override
   public void setScreenCoords(final Rectangle coords) {
      /* override width and height with defaults */
      coords.width = parameterDialog.getWidth();
      coords.height = parameterDialog.getHeight();
      parameterDialog.setBounds(coords);
   }

   /** override as getHeight does not work when using by value reference */
   @Override
   public int getHeight() {
      return parameterDialog.getHeight();
   }

   /** override as getWidth does not work when using by value reference */
   @Override
   public int getWidth() {
      return parameterDialog.getWidth();
   }

   /** close the time dialog */
   void close() {
      /* close is depreciated */
      parameterDialog.hide();
   }

   void showParameterDialog() {
      parameterDialog.show();
   }

   void addPanelToParameterDialog(final VerticalPanel pnl) {
      parameterDialog.add(pnl);
   }

   HorizontalPanel createButtonsPanel() {
      updateButton = new Button("Update");
      cancelButton = new Button("Cancel");

      updateButton.setWidth("100px");
      cancelButton.setWidth("100px");

      cancelButton.getElement().getStyle().setMarginLeft(13, Style.Unit.PX);

      final HorizontalPanel buttonsPanel = new HorizontalPanel();
      buttonsPanel.add(updateButton);
      buttonsPanel.add(cancelButton);

      return buttonsPanel;
   }

   public Button getUpdateButton() {
      return updateButton;
   }

   public Button getCancelButton() {
      return cancelButton;
   }

   public abstract boolean validate();

}
