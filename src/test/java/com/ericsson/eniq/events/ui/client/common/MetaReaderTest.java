package com.ericsson.eniq.events.ui.client.common;

import static com.ericsson.eniq.events.ui.client.common.MetaReaderConstants.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.common.client.CommonConstants;
import com.ericsson.eniq.events.common.client.datatype.ChartDataType;
import com.ericsson.eniq.events.common.client.json.IJSONArray;
import com.ericsson.eniq.events.common.client.json.IJSONObject;
import com.ericsson.eniq.events.ui.client.datatype.TabInfoDataType;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Example of UI code that is very difficult/impossible to unit test
 * The GWT JSON parser used contains calls to native methods that don't exist in a unit test environment
 * @author eemecoy
 *
 */
public class MetaReaderTest {

    private EventBus eventBus;

    private IMultiMetaDataHelper metaData;

    private MetaReader metaReader;

    @Before
    public void setUp() {
        eventBus = mock(EventBus.class);
        metaData = mock(IMultiMetaDataHelper.class);

        metaReader = new MetaReader(eventBus, metaData, null, null);
    }

    @Test
    public void shouldGetChartConfig() {
        final IJSONArray nodeArray = nodeArray();
        final IJSONObject chart = node();

        when(nodeArray.size()).thenReturn(1);
        when(nodeArray.get(0)).thenReturn(chart);
        when(metaData.getArray(CHARTS_SECTION)).thenReturn(nodeArray);

        when(chart.getString(ID)).thenReturn("id");

        final ChartDataType chartConfigInfo = metaReader.getChartConfigInfo("id");

        assertThat(chartConfigInfo, notNullValue());
    }

    @Test
    public void shouldGetTabDataMetaInfo_Empty() {
        final IJSONArray tabs = nodeArray();
        when(metaData.getArray(TABS)).thenReturn(tabs);

        final List<TabInfoDataType> info = metaReader.getTabDataMetaInfo();

        assertThat(info.isEmpty(), is(Boolean.TRUE));
    }

    @Test
    public void shouldGetTabDataMetaInfo_Full() {
        final IJSONArray tabs = nodeArray();
        final IJSONObject tabItem = node();
        when(tabs.size()).thenReturn(1);
        when(tabs.get(0)).thenReturn(tabItem);

        when(metaData.getArray(TABS)).thenReturn(tabs);

        when(tabItem.getString(ID)).thenReturn("id");
        when(tabItem.getString(NAME)).thenReturn("name");
        when(tabItem.getString(TIP)).thenReturn("tip");
        when(tabItem.getString(STYLE)).thenReturn("style1");
        when(tabItem.getString(TAB_ITEM_CENTER_STYLE)).thenReturn("style2");
        when(tabItem.getString(IS_ROLE_ENABLED)).thenReturn(CommonConstants.TRUE);
        when(tabItem.getString(IS_MODULE)).thenReturn(CommonConstants.TRUE);

        final List<TabInfoDataType> info = metaReader.getTabDataMetaInfo();

        assertThat(info.size(), equalTo(1));
        final TabInfoDataType result = info.get(0);
        assertThat(result.getId(), equalTo("id"));
        assertThat(result.getName(), equalTo("name"));
        assertThat(result.getTip(), equalTo("tip"));
        assertThat(result.getStyle(), equalTo("style1"));
        assertThat(result.getTabItemCenterStyle(), equalTo("style2"));
    }

    IJSONArray nodeArray() {
        return mock(IJSONArray.class);
    }

    IJSONObject node() {
        return mock(IJSONObject.class);
    }

}
