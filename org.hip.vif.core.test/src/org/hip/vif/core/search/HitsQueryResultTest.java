package org.hip.vif.core.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.hip.kernel.bom.DomainObjectCollection;
import org.hip.kernel.bom.DomainObjectIterator;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on 28.09.2005
 */
public class HitsQueryResultTest {
	private final static String nl = System.getProperty("line.separator");

	private static DataHouseKeeper data;
	private Document[] hits;
	private Object[] ids;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@Before
	public void setUp() throws Exception {		
		//create test index with two indexed member entries
		IndexHouseKeeper.redirectDocRoot(true);
		ids = data.create2Members();
		
		IndexSearcher lSearcher = new IndexSearcher(VIFIndexing.INSTANCE.createMemberIndexReader());
		try {
			QueryParser lParser = new QueryParser(IndexHouseKeeper.LUCENE_VERSION, AbstractSearching.IndexField.MEMBER_NAME.fieldName, IndexHouseKeeper.getAnalyzer());
			hits = IndexHouseKeeper.processSearchResults(lSearcher.search(lParser.parse("NameT*"), IndexHouseKeeper.NUMBER_OF_HITS), lSearcher);
		}
		finally {
			lSearcher.close();
		}
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllFromMember();
		data.deleteAllFromLinkMemberRole();
		IndexHouseKeeper.deleteTestIndexDir();
	}

	@Test
	public void testNext() throws IOException, VException, SQLException {
		//pre
		assertEquals("number of docs", 2, hits.length);
		
		Collection<String> lExpected = new ArrayList<String>();
		Collections.addAll(lExpected, "NameT1", "NameT2");
		
		MemberHitsResult lResult = new MemberHitsResult(hits);
		
		assertTrue("has next 1", lResult.hasMoreElements());		
		GeneralDomainObject lObject = lResult.next();
		assertTrue(lExpected.contains(lObject.get(AbstractSearching.IndexField.MEMBER_NAME.fieldName)));

		assertTrue("has next 2", lResult.hasMoreElements());		
		lObject = lResult.next();
		assertTrue(lExpected.contains(lObject.get(AbstractSearching.IndexField.MEMBER_NAME.fieldName)));
		assertFalse("has next no", lResult.hasMoreElements());
	}

	/*
	 * Test method for 'org.hip.vif.search.HitsQueryResult.nextn(int)'
	 */
	@Test
	public void testNextn() throws VException, IOException, SQLException {
		//pre
		assertEquals("number of docs", 2, hits.length);

		MemberHitsResult lResult = new MemberHitsResult(hits);
		DomainObjectCollection lCollection = lResult.nextn(2);
		DomainObjectIterator lIter = lCollection.elements();
		
		Collection<String> lExpected = new ArrayList<String>();
		Collections.addAll(lExpected, "NameT1", "NameT2");

		int i = 0;
		while (lIter.hasMoreElements()) {
			assertTrue(lExpected.contains(lIter.nextElement().get(AbstractSearching.IndexField.MEMBER_NAME.fieldName)));
			i++;
		}
		assertEquals("number of returned", 2, i);
	}

	/*
	 * Test method for 'org.hip.vif.search.HitsQueryResult.nextAsXMLString()'
	 */
	@Test
	public void testNextAsXMLString() throws VException, SQLException, IOException {
		String lTemplate1 = "" + nl +
			"<MemberHitsObject>" + nl +
			"    <propertySet>" + nl +
			"        <memberCity>StadtT</memberCity>" + nl +
			"        <memberFirstname>VornameT1</memberFirstname>" + nl +
			"        <memberUserID>TestUsr-DHK1</memberUserID>" + nl +
			"        <memberMail>1.mail@test</memberMail>" + nl +
			"        <memberName>NameT1</memberName>" + nl +
			"        <memberPostal>PLZ-T</memberPostal>" + nl +
			"        <memberStreet>StrasseT</memberStreet>" + nl +
			"        <memberID>%s</memberID>" + nl +
			"    </propertySet>" + nl +
			"</MemberHitsObject>";
		String lTemplate2 = "" + nl +
				"<MemberHitsObject>" + nl +
				"    <propertySet>" + nl +
				"        <memberCity>StadtT</memberCity>" + nl +
				"        <memberFirstname>VornameT2</memberFirstname>" + nl +
				"        <memberUserID>TestUsr-DHK2</memberUserID>" + nl +
				"        <memberMail>2.mail@test</memberMail>" + nl +
				"        <memberName>NameT2</memberName>" + nl +
				"        <memberPostal>PLZ-T</memberPostal>" + nl +
				"        <memberStreet>StrasseT</memberStreet>" + nl +
				"        <memberID>%s</memberID>" + nl +
				"    </propertySet>" + nl +
				"</MemberHitsObject>";
		Collection<String> lExpected = new ArrayList<String>();
		Collections.addAll(lExpected, String.format(lTemplate1, ids[0]), String.format(lTemplate2, ids[1]));
		
		//pre
		assertEquals("number of docs", 2, hits.length);

		MemberHitsResult lResult = new MemberHitsResult(hits);
		assertTrue(lResult.hasMoreElements());		
		assertTrue(lExpected.contains(lResult.nextAsXMLString()));		
		assertTrue(lResult.hasMoreElements());		
		assertTrue(lExpected.contains(lResult.nextAsXMLString()));		
		assertFalse(lResult.hasMoreElements());		
	}
	
}
