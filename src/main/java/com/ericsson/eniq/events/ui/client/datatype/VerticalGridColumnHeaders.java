/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.datatype;

/**
 * Data type to hold information for a vertical grid
 *
 * @author esuslyn
 * @since August 2010
 */
public class VerticalGridColumnHeaders {

   public final String[] keys;

   public final String columnHeaderPartOfTitle;

   /**
    * @param keys                    - the column headers for a vertical grid
    * @param columnHeaderPartOfTitle - if one of the column headers needs to be displayed in the title
    *                                (in the case of VIP Customer is displayed in title rather than rendered as column in grid)
    */
   public VerticalGridColumnHeaders(final String[] keys, final String columnHeaderPartOfTitle) {
      this.keys = keys;
      this.columnHeaderPartOfTitle = columnHeaderPartOfTitle;
   }

}
