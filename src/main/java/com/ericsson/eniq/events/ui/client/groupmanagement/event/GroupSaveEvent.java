/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author ecarsea
 * @since 2011
 */
public class GroupSaveEvent extends GwtEvent<GroupSaveEventHandler> {

   public static final Type<GroupSaveEventHandler> TYPE = new Type<GroupSaveEventHandler>();

   private final String groupName;

   private final boolean promptAllowed;

   /** @param groupName  */
   public GroupSaveEvent(final String groupName, final boolean promptNeeded) {
      this.groupName = groupName;
      this.promptAllowed = promptNeeded;
   }

   /* (non-Javadoc)
   * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
   */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<GroupSaveEventHandler> getAssociatedType() {
      return TYPE;
   }

   /* (non-Javadoc)
   * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
   */
   @Override
   protected void dispatch(final GroupSaveEventHandler handler) {
      handler.onGroupSave(this);
   }

   /** @return the promptNeeded */
   public boolean isPromptAllowed() {
      return promptAllowed;
   }

   /** @return the groupName */
   public String getGroupName() {
      return groupName;
   }
}
