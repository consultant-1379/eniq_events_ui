package com.ericsson.eniq.events.ui.client.grid;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.ui.TestEniqEventsUI;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.event.LoadListener;

/**
 *  @author eriwals
 *
 */
public class CaselessListStoreTest extends TestEniqEventsUI {

    private CaselessListStore<ModelData> objToTest;

    private ListLoader<PagingLoadResult<ModelData>> mockedListLoader;

    private List<DummyData> initialStringData;

    private List<DummyData> initialNumberData;

    private List<DummyData> expectedStringDataAsc;

    private List<DummyData> expectedStringDataDesc;

    private List<DummyData> expectedNumberDataAsc;

    private List<DummyData> expectedNumberDataDesc;

    @Before
    public void setUp() {

        setupData();

        mockedListLoader = context.mock(ListLoader.class);

        context.checking(new Expectations() {
            {
                allowing(mockedListLoader).isRemoteSort();
                exactly(1).of(mockedListLoader).addLoadListener(with(any((LoadListener.class))));
                exactly(1).of(mockedListLoader).setRemoteSort(true);
            }
        });

        objToTest = new CaselessListStore<ModelData>(mockedListLoader);
        mockedListLoader.setRemoteSort(true);

        objToTest.setSortField("testcol1");

    }

    @After
    public void tearDown() {
        objToTest = null;
    }

    @Test
    public void testStringSortDesc() {
        objToTest.add(initialStringData);
        objToTest.sort("testcol1", SortDir.DESC);

        final List<ModelData> resultData = objToTest.getModels();

        assertTrue(resultData.size() == expectedStringDataDesc.size());

        int i = 0;

        for (final ModelData data : resultData) {
            //System.out.println(data.get("testcol1"));
            //System.out.println(expectedStringDataDesc.get(i).get("testcol1"));
            assertTrue(
                    "value " + data.get("testcol1").toString() + " at position " + i
                            + " in sorted list is not equal to expected value of "
                            + expectedStringDataDesc.get(i).get("testcol1").toString(), data.get("testcol1").toString()
                            .equals(expectedStringDataDesc.get(i).get("testcol1").toString()));
            i++;
        }
    }

    @Test
    public void testStringSortAsc() {
        objToTest.add(initialStringData);
        objToTest.sort("testcol1", SortDir.ASC);

        final List<ModelData> resultData = objToTest.getModels();

        assertTrue(resultData.size() == expectedStringDataAsc.size());

        int i = 0;

        for (final ModelData data : resultData) {
            //System.out.println(data.get("testcol1"));
            //System.out.println(expectedStringDataAsc.get(i).get("testcol1"));
            assertTrue(
                    "value " + data.get("testcol1").toString() + " at position " + i
                            + " in sorted list is not equal to expected value of "
                            + expectedStringDataAsc.get(i).get("testcol1").toString(), data.get("testcol1").toString()
                            .equals(expectedStringDataAsc.get(i).get("testcol1").toString()));
            i++;
        }

    }

    @Test
    public void testNumberSortDesc() {
        objToTest.add(initialNumberData);
        objToTest.sort("testcol1", SortDir.DESC);

        final List<ModelData> resultData = objToTest.getModels();

        assertTrue(resultData.size() == expectedNumberDataDesc.size());

        int i = 0;

        for (final ModelData data : resultData) {
            //System.out.println(data.get("testcol1"));
            //System.out.println(expectedNumberDataDesc.get(i).get("testcol1"));
            assertTrue(
                    "value " + data.get("testcol1").toString() + " at position " + i
                            + " in sorted list is not equal to expected value of "
                            + expectedNumberDataDesc.get(i).get("testcol1").toString(), data.get("testcol1").toString()
                            .equals(expectedNumberDataDesc.get(i).get("testcol1").toString()));
            i++;
        }
    }

    @Test
    public void testNumberSortAsc() {
        objToTest.add(initialNumberData);
        objToTest.sort("testcol1", SortDir.ASC);

        final List<ModelData> resultData = objToTest.getModels();

        assertTrue(resultData.size() == expectedNumberDataAsc.size());

        int i = 0;

        for (final ModelData data : resultData) {
            //System.out.println(data.get("testcol1"));
            //System.out.println(expectedNumberDataAsc.get(i).get("testcol1"));
            assertTrue(
                    "value " + data.get("testcol1").toString() + " at position " + i
                            + " in sorted list is not equal to expected value of "
                            + expectedNumberDataAsc.get(i).get("testcol1").toString(), data.get("testcol1").toString()
                            .equals(expectedNumberDataAsc.get(i).get("testcol1").toString()));
            i++;
        }

    }

    @Test
    public void testNoArgConstructor() {

        objToTest = new CaselessListStore<ModelData>();
        objToTest.setSortField("testcol1");

        objToTest.add(initialStringData);
        objToTest.sort("testcol1", SortDir.ASC);

        final List<ModelData> resultData = objToTest.getModels();

        assertTrue(resultData.size() == expectedStringDataAsc.size());

        int i = 0;

        for (final ModelData data : resultData) {
            //System.out.println(data.get("testcol1"));
            //System.out.println(expectedStringDataAsc.get(i).get("testcol1"));
            assertTrue(
                    "value " + data.get("testcol1").toString() + " at position " + i
                            + " in sorted list is not equal to expected value of "
                            + expectedStringDataAsc.get(i).get("testcol1").toString(), data.get("testcol1").toString()
                            .equals(expectedStringDataAsc.get(i).get("testcol1").toString()));
            i++;
        }

    }

    private void setupData() {

        initialStringData = new ArrayList<DummyData>();
        initialStringData.add(new DummyData("huawei Technologies Co Ltd", "HUAWEI G7600"));
        initialStringData.add(new DummyData("Flying Technology Development Co Ltd", "ORA MOBILE M8"));
        initialStringData.add(new DummyData("Huawei Technologies Co Ltd", "E510"));
        initialStringData.add(new DummyData("samsung Korea", "SGH-L200"));
        initialStringData.add(new DummyData("Nokia Corporation", "Nokia 3410"));
        initialStringData.add(new DummyData("motorola Inc.", "MQ3-4411C41"));
        initialStringData.add(new DummyData("BandRich Inc", "C152"));
        initialStringData.add(new DummyData("nokia Corporation", "Nokia N93"));
        initialStringData
                .add(new DummyData("Shenzhen Top Gouwei Electronics Co Ltd (Liantang Ind Zone)", "COSUN Q808"));

        expectedStringDataAsc = new ArrayList<DummyData>();
        expectedStringDataAsc.add(new DummyData("BandRich Inc", "C152"));
        expectedStringDataAsc.add(new DummyData("Flying Technology Development Co Ltd", "ORA MOBILE M8"));
        expectedStringDataAsc.add(new DummyData("huawei Technologies Co Ltd", "HUAWEI G7600"));
        expectedStringDataAsc.add(new DummyData("Huawei Technologies Co Ltd", "E510"));
        expectedStringDataAsc.add(new DummyData("motorola Inc.", "MQ3-4411C41"));
        expectedStringDataAsc.add(new DummyData("Nokia Corporation", "Nokia 3410"));
        expectedStringDataAsc.add(new DummyData("nokia Corporation", "Nokia N93"));
        expectedStringDataAsc.add(new DummyData("samsung Korea", "SGH-L200"));
        expectedStringDataAsc.add(new DummyData("Shenzhen Top Gouwei Electronics Co Ltd (Liantang Ind Zone)",
                "COSUN Q808"));

        expectedStringDataDesc = new ArrayList<DummyData>();
        expectedStringDataDesc.add(new DummyData("Shenzhen Top Gouwei Electronics Co Ltd (Liantang Ind Zone)",
                "COSUN Q808"));
        expectedStringDataDesc.add(new DummyData("samsung Korea", "SGH-L200"));
        expectedStringDataDesc.add(new DummyData("Nokia Corporation", "Nokia 3410"));
        expectedStringDataDesc.add(new DummyData("nokia Corporation", "Nokia N93"));
        expectedStringDataDesc.add(new DummyData("motorola Inc.", "MQ3-4411C41"));
        expectedStringDataDesc.add(new DummyData("huawei Technologies Co Ltd", "HUAWEI G7600"));
        expectedStringDataDesc.add(new DummyData("Huawei Technologies Co Ltd", "E510"));
        expectedStringDataDesc.add(new DummyData("Flying Technology Development Co Ltd", "ORA MOBILE M8"));
        expectedStringDataDesc.add(new DummyData("BandRich Inc", "C152"));

        initialNumberData = new ArrayList<DummyData>();
        initialNumberData.add(new DummyData(11, ""));
        initialNumberData.add(new DummyData(45, ""));
        initialNumberData.add(new DummyData(1, ""));
        initialNumberData.add(new DummyData(4, ""));
        initialNumberData.add(new DummyData(3, ""));
        initialNumberData.add(new DummyData(100, ""));
        initialNumberData.add(new DummyData(0, ""));
        initialNumberData.add(new DummyData(2, ""));
        initialNumberData.add(new DummyData(19, ""));

        expectedNumberDataAsc = new ArrayList<DummyData>();
        expectedNumberDataAsc.add(new DummyData(0, ""));
        expectedNumberDataAsc.add(new DummyData(1, ""));
        expectedNumberDataAsc.add(new DummyData(2, ""));
        expectedNumberDataAsc.add(new DummyData(3, ""));
        expectedNumberDataAsc.add(new DummyData(4, ""));
        expectedNumberDataAsc.add(new DummyData(11, ""));
        expectedNumberDataAsc.add(new DummyData(19, ""));
        expectedNumberDataAsc.add(new DummyData(45, ""));
        expectedNumberDataAsc.add(new DummyData(100, ""));

        expectedNumberDataDesc = new ArrayList<DummyData>();
        expectedNumberDataDesc.add(new DummyData(100, ""));
        expectedNumberDataDesc.add(new DummyData(45, ""));
        expectedNumberDataDesc.add(new DummyData(19, ""));
        expectedNumberDataDesc.add(new DummyData(11, ""));
        expectedNumberDataDesc.add(new DummyData(4, ""));
        expectedNumberDataDesc.add(new DummyData(3, ""));
        expectedNumberDataDesc.add(new DummyData(2, ""));
        expectedNumberDataDesc.add(new DummyData(1, ""));
        expectedNumberDataDesc.add(new DummyData(0, ""));

    }

    private class DummyData extends BaseModelData {

        // second col is superfluous but leaving it in for possible future use

        public DummyData(final Object testCol1, final Object testCol2) {
            set("testcol1", testCol1);
            set("testcol2", testCol2);
        }
    }
}