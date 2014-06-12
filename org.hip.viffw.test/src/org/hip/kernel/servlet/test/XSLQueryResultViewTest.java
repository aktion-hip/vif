package org.hip.kernel.servlet.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.DefaultQueryResult;
import org.hip.kernel.bom.impl.test.DataHouseKeeper;
import org.hip.kernel.servlet.Context;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author: Benno Luthiger
 */
public class XSLQueryResultViewTest {
	private static DataHouseKeeper data;
//	private static final String EXPECTED1 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
//		"<transformed><Root>\n" +
//		"<TestDomainObject>\n" +
//		"    <propertySet>\n" +
//		"        <PLZ/>\n" +
//		"        <Language/>\n" +
//		"        <Fax/>\n" +
//		"        <Tel/>\n" +
//		"        <Firstname>Adam</Firstname>\n" +
//		"        <Name>test 1</Name>\n" +
//		"        <Street/>\n" +
//		"        <Amount>12.45</Amount>\n" +
//		"        <Password/>\n" +
//		"        <Sex>1</Sex>\n" +
//		"        <City/>\n" +
//		"        <Mail>dummy1@aktion-hip.ch</Mail>\n" +
//		"        <Double>13.11</Double>\n" +
//		"        <TestID>341</TestID>\n" +
//		"        <Mutation>2001-11-02 12:35:40.0</Mutation>\n" +
//		"    </propertySet>\n" +
//		"</TestDomainObject>\n" +
//		"<TestDomainObject>\n" +
//		"    <propertySet>\n" +
//		"        <PLZ/>\n" +
//		"        <Language/>\n" +
//		"        <Fax/>\n" +
//		"        <Tel/>\n" +
//		"        <Firstname>Adam</Firstname>\n" +
//		"        <Name>test 10</Name>\n" +
//		"        <Street/>\n" +
//		"        <Amount>12.45</Amount>\n" +
//		"        <Password/>\n" +
//		"        <Sex>1</Sex>\n" +
//		"        <City/>\n" +
//		"        <Mail>dummy1@aktion-hip.ch</Mail>\n" +
//		"        <Double>13.11</Double>\n" +
//		"        <TestID>350</TestID>\n" +
//		"        <Mutation>2001-11-02 12:35:40.0</Mutation>\n" +
//		"    </propertySet>\n" +
//		"</TestDomainObject>\n" +
//		"<TestDomainObject>\n" +
//		"    <propertySet>\n" +
//		"        <PLZ/>\n" +
//		"        <Language/>\n" +
//		"        <Fax/>\n" +
//		"        <Tel/>\n" +
//		"        <Firstname>Adam</Firstname>\n" +
//		"        <Name>test 11</Name>\n" +
//		"        <Street/>\n" +
//		"        <Amount>12.45</Amount>\n" +
//		"        <Password/>\n" +
//		"        <Sex>1</Sex>\n" +
//		"        <City/>\n" +
//		"        <Mail>dummy1@aktion-hip.ch</Mail>\n" +
//		"        <Double>13.11</Double>\n" +
//		"        <TestID>351</TestID>\n" +
//		"        <Mutation>2001-11-02 12:35:40.0</Mutation>\n" +
//		"    </propertySet>\n" +
//		"</TestDomainObject>\n" +
//		"<TestDomainObject>\n" +
//		"    <propertySet>\n" +
//		"        <PLZ/>\n" +
//		"        <Language/>\n" +
//		"        <Fax/>\n" +
//		"        <Tel/>\n" +
//		"        <Firstname>Adam</Firstname>\n" +
//		"        <Name>test 12</Name>\n" +
//		"        <Street/>\n" +
//		"        <Amount>12.45</Amount>\n" +
//		"        <Password/>\n" +
//		"        <Sex>1</Sex>\n" +
//		"        <City/>\n" +
//		"        <Mail>dummy1@aktion-hip.ch</Mail>\n" +
//		"        <Double>13.11</Double>\n" +
//		"        <TestID>352</TestID>\n" +
//		"        <Mutation>2001-11-02 12:35:40.0</Mutation>\n" +
//		"    </propertySet>\n" +
//		"</TestDomainObject></Root></transformed>";
//	private static final String EXPECTED2 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
//		"<transformed><Root>\n" +
//		"<TestDomainObject>\n" +
//		"    <propertySet>\n" +
//		"        <PLZ/>\n" +
//		"        <Language/>\n" +
//		"        <Fax/>\n" +
//		"        <Tel/>\n" +
//		"        <Firstname>Adam</Firstname>\n" +
//		"        <Name>test 13</Name>\n" +
//		"        <Street/>\n" +
//		"        <Amount>12.45</Amount>\n" +
//		"        <Password/>\n" +
//		"        <Sex>1</Sex>\n" +
//		"        <City/>\n" +
//		"        <Mail>dummy1@aktion-hip.ch</Mail>\n" +
//		"        <Double>13.11</Double>\n" +
//		"        <TestID>353</TestID>\n" +
//		"        <Mutation>2001-11-02 12:35:40.0</Mutation>\n" +
//		"    </propertySet>\n" +
//		"</TestDomainObject>\n" +
//		"<TestDomainObject>\n" +
//		"    <propertySet>\n" +
//		"        <PLZ/>\n" +
//		"        <Language/>\n" +
//		"        <Fax/>\n" +
//		"        <Tel/>\n" +
//		"        <Firstname>Adam</Firstname>\n" +
//		"        <Name>test 14</Name>\n" +
//		"        <Street/>\n" +
//		"        <Amount>12.45</Amount>\n" +
//		"        <Password/>\n" +
//		"        <Sex>1</Sex>\n" +
//		"        <City/>\n" +
//		"        <Mail>dummy1@aktion-hip.ch</Mail>\n" +
//		"        <Double>13.11</Double>\n" +
//		"        <TestID>354</TestID>\n" +
//		"        <Mutation>2001-11-02 12:35:40.0</Mutation>\n" +
//		"    </propertySet>\n" +
//		"</TestDomainObject>\n" +
//		"<TestDomainObject>\n" +
//		"    <propertySet>\n" +
//		"        <PLZ/>\n" +
//		"        <Language/>\n" +
//		"        <Fax/>\n" +
//		"        <Tel/>\n" +
//		"        <Firstname>Adam</Firstname>\n" +
//		"        <Name>test 15</Name>\n" +
//		"        <Street/>\n" +
//		"        <Amount>12.45</Amount>\n" +
//		"        <Password/>\n" +
//		"        <Sex>1</Sex>\n" +
//		"        <City/>\n" +
//		"        <Mail>dummy1@aktion-hip.ch</Mail>\n" +
//		"        <Double>13.11</Double>\n" +
//		"        <TestID>355</TestID>\n" +
//		"        <Mutation>2001-11-02 12:35:40.0</Mutation>\n" +
//		"    </propertySet>\n" +
//		"</TestDomainObject>\n" +
//		"<TestDomainObject>\n" +
//		"    <propertySet>\n" +
//		"        <PLZ/>\n" +
//		"        <Language/>\n" +
//		"        <Fax/>\n" +
//		"        <Tel/>\n" +
//		"        <Firstname>Adam</Firstname>\n" +
//		"        <Name>test 2</Name>\n" +
//		"        <Street/>\n" +
//		"        <Amount>12.45</Amount>\n" +
//		"        <Password/>\n" +
//		"        <Sex>1</Sex>\n" +
//		"        <City/>\n" +
//		"        <Mail>dummy1@aktion-hip.ch</Mail>\n" +
//		"        <Double>13.11</Double>\n" +
//		"        <TestID>342</TestID>\n" +
//		"        <Mutation>2001-11-02 12:35:40.0</Mutation>\n" +
//		"    </propertySet>\n" +
//		"</TestDomainObject></Root></transformed>";
	
	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllFromSimple();
		System.out.println("Deleted all entries in tblTest.");
	}
	
	@Test
	public void testDo() throws Exception {
		Context lContext = new TestContext();
//		TestOutputStream lStream = new TestOutputStream();
//		lStream.setFlushMode(false);
		TestPrintWriter lWriter = new TestPrintWriter();

		for (int i = 1; i < 16; i++) 
			data.createTestEntry("test " + i);		

		ResultSet lResult = data.executeQuery("SELECT TESTID, SNAME, SFIRSTNAME FROM tblTest ORDER BY sName");
		QueryResult lQueryResult = new DefaultQueryResult(data.getSimpleHome(), lResult, null);
		TestXSLQueryResultView lResultView = new TestXSLQueryResultView(lContext, lQueryResult, 15);
		assertEquals("page size", 4, lResultView.getPageSize());
		
		lResultView.renderToWriter(lWriter, "");
		String lStreamed = lWriter.getStreamedText();
		assertTrue("content page 1.1", lStreamed.indexOf("<Name>test 1</Name>") >= 0);
		assertTrue("content page 1.2", lStreamed.indexOf("<Name>test 10</Name>") >= 0);
		assertTrue("content page 1.3", lStreamed.indexOf("<Name>test 11</Name>") >= 0);
		assertTrue("content page 1.4", lStreamed.indexOf("<Name>test 12</Name>") >= 0);
		assertEquals("not content page 1.1", -1, lStreamed.indexOf("<Name>test 13</Name>"));
		assertEquals("not content page 1.2", -1, lStreamed.indexOf("<Name>test 2</Name>"));
		lWriter.close();
		lWriter = new TestPrintWriter();

		lResultView.nextPage();
		lResultView.renderToWriter(lWriter, "");
		lStreamed = lWriter.getStreamedText();
		assertTrue("content page 2.1", lStreamed.indexOf("<Name>test 13</Name>") >= 0);
		assertTrue("content page 2.2", lStreamed.indexOf("<Name>test 14</Name>") >= 0);
		assertTrue("content page 2.3", lStreamed.indexOf("<Name>test 15</Name>") >= 0);
		assertTrue("content page 2.4", lStreamed.indexOf("<Name>test 2</Name>") >= 0);
		assertEquals("not content page 2.1", -1, lStreamed.indexOf("<Name>test 1</Name>"));
		assertEquals("not content page 2.2", -1, lStreamed.indexOf("<Name>test 3</Name>"));
		lWriter.close();
		lWriter = new TestPrintWriter();
		
		lResultView.previousPage();
		lResultView.renderToWriter(lWriter, "");
		lStreamed = lWriter.getStreamedText();
		assertTrue("content page 3.1", lStreamed.indexOf("<Name>test 1</Name>") >= 0);
		assertTrue("content page 3.2", lStreamed.indexOf("<Name>test 10</Name>") >= 0);
		assertTrue("content page 3.3", lStreamed.indexOf("<Name>test 11</Name>") >= 0);
		assertTrue("content page 3.4", lStreamed.indexOf("<Name>test 12</Name>") >= 0);
		assertEquals("not content page 3.1", -1, lStreamed.indexOf("<Name>test 13</Name>"));
		assertEquals("not content page 3.2", -1, lStreamed.indexOf("<Name>test 2</Name>"));
		lWriter.close();
		lWriter = new TestPrintWriter();

		//jumps to last page
		lResultView.nextPage(3);
		lResultView.renderToWriter(lWriter, "");
		lStreamed = lWriter.getStreamedText();
		assertTrue("content page 4.1", lStreamed.indexOf("<Name>test 7</Name>") >= 0);
		assertTrue("content page 4.2", lStreamed.indexOf("<Name>test 8</Name>") >= 0);
		assertTrue("content page 4.3", lStreamed.indexOf("<Name>test 9</Name>") >= 0);
		assertEquals("not content page 4.1", -1, lStreamed.indexOf("<Name>test 10</Name>"));
		assertEquals("not content page 4.2", -1, lStreamed.indexOf("<Name>test 6</Name>"));
		lWriter.close();
		lWriter = new TestPrintWriter();
		
		//jumps back to second page
		lResultView.previousPage(2);
		lResultView.renderToWriter(lWriter, "");
		lStreamed = lWriter.getStreamedText();
		assertTrue("content page 5.1", lStreamed.indexOf("<Name>test 13</Name>") >= 0);
		assertTrue("content page 5.2", lStreamed.indexOf("<Name>test 14</Name>") >= 0);
		assertTrue("content page 5.3", lStreamed.indexOf("<Name>test 15</Name>") >= 0);
		assertTrue("content page 5.4", lStreamed.indexOf("<Name>test 2</Name>") >= 0);
		assertEquals("not content page 5.1", -1, lStreamed.indexOf("<Name>test 1</Name>"));
		assertEquals("not content page 5.2", -1, lStreamed.indexOf("<Name>test 3</Name>"));
		lWriter.close();
		lWriter = new TestPrintWriter();
		
		//jumps to last page again
		lResultView.nextPage(5);
		lResultView.renderToWriter(lWriter, "");
		lStreamed = lWriter.getStreamedText();
		assertTrue("content page 6.1", lStreamed.indexOf("<Name>test 7</Name>") >= 0);
		assertTrue("content page 6.2", lStreamed.indexOf("<Name>test 8</Name>") >= 0);
		assertTrue("content page 6.3", lStreamed.indexOf("<Name>test 9</Name>") >= 0);
		assertEquals("not content page 6.1", -1, lStreamed.indexOf("<Name>test 11</Name>"));
		assertEquals("not content page 6.2", -1, lStreamed.indexOf("<Name>test 2</Name>"));
		lWriter.close();
		lWriter = new TestPrintWriter();

		//sets to first page
		lResultView.setToFirstPage();
		lResultView.renderToWriter(lWriter, "");
		lStreamed = lWriter.getStreamedText();
		assertTrue("content page 7.1", lStreamed.indexOf("<Name>test 1</Name>") >= 0);
		assertTrue("content page 7.2", lStreamed.indexOf("<Name>test 10</Name>") >= 0);
		assertTrue("content page 7.3", lStreamed.indexOf("<Name>test 11</Name>") >= 0);
		assertTrue("content page 7.4", lStreamed.indexOf("<Name>test 12</Name>") >= 0);
		assertEquals("not content page 7.1", -1, lStreamed.indexOf("<Name>test 13</Name>"));
		assertEquals("not content page 7.2", -1, lStreamed.indexOf("<Name>test 2</Name>"));
		lWriter.close();
		lWriter = new TestPrintWriter();
		
		//sets to first page
		lResultView.setToLastPage();
		lResultView.renderToWriter(lWriter, "");
		lStreamed = lWriter.getStreamedText();
		assertTrue("content page 8.1", lStreamed.indexOf("<Name>test 7</Name>") >= 0);
		assertTrue("content page 8.2", lStreamed.indexOf("<Name>test 8</Name>") >= 0);
		assertTrue("content page 8.3", lStreamed.indexOf("<Name>test 9</Name>") >= 0);
		assertEquals("not content page 8.1", -1, lStreamed.indexOf("<Name>test 11</Name>"));
		assertEquals("not content page 8.2", -1, lStreamed.indexOf("<Name>test 2</Name>"));
		
		lResultView.finalize();
	}
}
