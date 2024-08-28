/*
 * -----------------------------------------------------------------------
 *      Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.dashboard;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletType;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Provider;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class PortletTemplateRegistryImplTest extends TestEniqEventsUI {

   PortletTemplateRegistryImpl registry;

   Provider mockProvider;

   PortletTemplate mockTemplate;

   @Before
   public void setUp() {
      mockProvider = context.mock(Provider.class);
      mockTemplate = context.mock(PortletTemplate.class);

      registry = new PortletTemplateRegistryImpl(mockProvider, mockProvider, mockProvider, mockProvider,
              mockProvider, mockProvider, mockProvider, mockProvider, mockProvider, mockProvider, mockProvider,
              mockProvider, mockProvider);
   }

   @Test
   public void shouldFindByName() throws Exception {
      context.checking(new Expectations() {
         {
            one(mockProvider).get();
            will(returnValue(mockTemplate));
         }
      });

      final PortletTemplate result = registry.createByName(PortletType.CHART);

      assertThat(result, sameInstance(mockTemplate));
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldFailToFind() throws Exception {
      registry.createByName(null);
   }

}