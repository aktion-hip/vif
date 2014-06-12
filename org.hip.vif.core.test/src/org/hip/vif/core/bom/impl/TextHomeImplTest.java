package org.hip.vif.core.bom.impl;

import static org.junit.Assert.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.DataHouseKeeper.TextValues;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 02.08.2010
 */
public class TextHomeImplTest {
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		IndexHouseKeeper.redirectDocRoot(true);
	}
	
	@After
	public void tearDown() throws Exception {
		data.deleteAllInAll();
		IndexHouseKeeper.deleteTestIndexDir();
	}
	
	@Test
	public void testGetText() throws Exception {
		String lExpected = "The first Book";
		Long lTextID = data.createText(lExpected, "Riese, Adam");
		
		TextHome lHome = data.getTextHome();
		assertEquals("number", 1, lHome.getCount());
		Text lText = lHome.getText(lTextID, 0);
		assertEquals("retrieved 1", lExpected, lText.get(TextHome.KEY_TITLE));
		
		String lTextIDv = String.format("%s-%s", lTextID, 0);
		assertEquals("id-version combined", lTextID + "-0", lTextIDv);
		lText = lHome.getText(lTextIDv);
		assertEquals("retrieved 2", lExpected, lText.get(TextHome.KEY_TITLE));
	}
	
	@Test
	public void testCreateNewVersion() throws Exception {
		String lAuthor = "Riese, Adam";
		TextHome lHome = data.getTextHome();
		Long lTextID1 = data.createText("The first Book", lAuthor);
		
		assertEquals("count 1", 1, lHome.getCount());
		
		String lTitle = "The version book";
		TextValues lValues = new DataHouseKeeper.TextValues(lTitle, lAuthor, "Foo, James", "Everything you need to know", "2010", "The Publication", "20-77", "5", "76", "NZZ Press", "Zürich", "Very *strange* story", 1);
		Text lText = lHome.getText(lTextID1, 0);
		lHome.createNewVersion(lText, lValues, new Long(9));
		
		assertEquals("count 2", 2, lHome.getCount());
		
		lText = lHome.getText(lTextID1, 1);
		assertEquals("state", WorkflowAwareContribution.S_PRIVATE, Integer.parseInt(lText.get(TextHome.KEY_STATE).toString()));
		assertEquals("version", 1, Integer.parseInt(lText.get(TextHome.KEY_VERSION).toString()));
		assertEquals("title", lTitle, lText.get(TextHome.KEY_TITLE));
	}
	
	@Test
	public void testSelectAutoComplete() throws Exception {
		//create 6 text entries
		Long lTextID1 = data.createText("The first Book", "Riese, Adam");
		Long lTextID2 = data.createText("abc first Book", "author");
		Long lTextID3 = data.createText("abcd first Book", "author");
		Long lTextID4 = data.createText("ABC first Book", "author");
		Long lTextID5 = data.createText("aB12 first Book", "author");
		Long lTextID6 = data.createText("aab first Book", "author");

		TextHome lHome = data.getTextHome();
		List<String> lResult = lHome.getAutoCompleteSelection(TextHome.KEY_TITLE, "a");
		assertEquals("number 1", 6, lHome.getCount());
		assertEquals("nothing published", 0, lResult.size());
		
		//publish
		publish(lTextID1, lHome);
		publish(lTextID2, lHome);
		publish(lTextID3, lHome);
		publish(lTextID4, lHome);
		publish(lTextID5, lHome);
		publish(lTextID6, lHome);
		clone(lTextID2, lHome);

		//we have 7 entries published
		assertEquals("number 2", 7, lHome.getCount());

		//however, we retrieve only 5 entries because we've grouped on the entry's id to eliminate version duplicates
		String[] lExpected = {"ABC first Book", "aB12 first Book", "aab first Book", "abc first Book", "abcd first Book"};
		lResult = lHome.getAutoCompleteSelection(TextHome.KEY_TITLE, "a");
		assertEquals("retrieved", lExpected.length, lResult.size());

		int i = 0;
		for (String lTitle : lResult) {
//			System.out.println(lTitle);
			assertEquals("auto completion published", lExpected[i++], lTitle);
		}
	}	

	private void clone(Long inTextID, TextHome inTextHome) throws VException, SQLException {
		Text lText = inTextHome.getText(inTextID, 0);
		DomainObject lClone = inTextHome.create();
		lClone.set(TextHome.KEY_TYPE, lText.get(TextHome.KEY_TYPE));
		lClone.set(TextHome.KEY_TITLE, lText.get(TextHome.KEY_TITLE));
		lClone.set(TextHome.KEY_AUTHOR, lText.get(TextHome.KEY_AUTHOR));
		lClone.set(TextHome.KEY_COAUTHORS, lText.get(TextHome.KEY_COAUTHORS));
		lClone.set(TextHome.KEY_SUBTITLE, lText.get(TextHome.KEY_SUBTITLE));
		lClone.set(TextHome.KEY_YEAR, lText.get(TextHome.KEY_YEAR));
		lClone.set(TextHome.KEY_PUBLICATION, lText.get(TextHome.KEY_PUBLICATION));
		lClone.set(TextHome.KEY_PAGES, lText.get(TextHome.KEY_PAGES));
		lClone.set(TextHome.KEY_VOLUME, lText.get(TextHome.KEY_VOLUME));
		lClone.set(TextHome.KEY_NUMBER, lText.get(TextHome.KEY_NUMBER));
		lClone.set(TextHome.KEY_PUBLISHER, lText.get(TextHome.KEY_PUBLISHER));
		lClone.set(TextHome.KEY_PLACE, lText.get(TextHome.KEY_PLACE));
		lClone.set(TextHome.KEY_REMARK, lText.get(TextHome.KEY_REMARK));
		lClone.set(TextHome.KEY_STATE, lText.get(TextHome.KEY_STATE));
		lClone.set(TextHome.KEY_VERSION, new Long(1));
		lClone.set(TextHome.KEY_ID, lText.get(TextHome.KEY_ID));
		lClone.insert(true);
	}

	private void publish(Long inTextID, TextHome inTextHome) throws VException, SQLException {
		Text lText = inTextHome.getText(inTextID, 0);
		lText.set(TextHome.KEY_STATE, new Long(WorkflowAwareContribution.S_OPEN));
		lText.update(true);
	}
	
	@Test
	public void testSelectTitleOrAuthor() throws Exception {
		String lTitle = "1x1";
		String lAuthor = "Riese, Adam";
		
		//create 4 text entries
		Long lTextID1 = data.createText("The first Book", "Doe, Jane");
		Long lTextID2 = data.createText(lTitle, "author");
		Long lTextID3 = data.createText("title", lAuthor);
		Long lTextID4 = data.createText(lTitle, lAuthor);
		
		TextHome lHome = data.getTextHome();
		assertEquals("number 1", 4, lHome.getCount());
		QueryResult lResult = lHome.selectTitleOrAuthor(lTitle, lAuthor);
		assertQueryResult(lResult, new String[] {}, "nothing published");
		
		//publish
		publish(lTextID1, lHome);
		publish(lTextID2, lHome);
		publish(lTextID3, lHome);
		publish(lTextID4, lHome);
		
		lResult = lHome.selectTitleOrAuthor(lTitle, lAuthor);
		assertQueryResult(lResult, new String[] {"1x1/author", "title/Riese, Adam", "1x1/Riese, Adam"}, "selection");
	}
	
	private void assertQueryResult(QueryResult inResult, String[] inExpected, String inMessage) throws VException, SQLException {
		List<String> lExpected = Arrays.asList(inExpected);
		int i = 0;
		while (inResult.hasMoreElements()) {
			GeneralDomainObject lModel = inResult.nextAsDomainObject();
			i++;
			assertTrue(inMessage + ": " + i, lExpected.contains(lModel.get(TextHome.KEY_TITLE) + "/" + lModel.get(TextHome.KEY_AUTHOR)));
		}
		assertEquals(inMessage + ": size", inExpected.length, i);
	}
	
	@Test
	public void testIsPublished() throws Exception {
		TextHome lHome = data.getTextHome();
		Long lTextID = data.createText("The first Book", "Doe, Jane");
		assertEquals("count 0", 1, lHome.getCount());
		
		assertFalse("nothing published", lHome.hasPublishedVersion(lTextID));
		publish(lTextID, lHome);
		
		assertEquals("count 1", 1, lHome.getCount());
		assertTrue("entry published", lHome.hasPublishedVersion(lTextID));
	}
	
	@Test
	public void testCheckReference() throws Exception {
		TextHome lHome = data.getTextHome();
		Long lTextID = data.createText("The first Book", "Smith, Bob");
		
		Text lText = lHome.getText(lTextID, 0);
		String lReference = String.format("%s %s", "Smith", 2010);
		String lReferenceChecked = lHome.checkReference(lReference);
		assertEquals("reference 1", lReference, lReferenceChecked);
		
		lText.set(TextHome.KEY_REFERENCE, lReferenceChecked);
		lText.update(true);
		
		Long lTextID2 = data.createText("The second Book", "Smith, Bob");
		lText = lHome.getText(lTextID2, 0);
		lReferenceChecked = lHome.checkReference(lReference);
		assertEquals("reference 2", lReference+"a", lReferenceChecked);
		
		lText.set(TextHome.KEY_REFERENCE, lReferenceChecked);
		lText.update(true);
		
		Long lTextID3 = data.createText("The third Book", "Smith, Bob");
		lText = lHome.getText(lTextID3, 0);
		lReferenceChecked = lHome.checkReference(lReference);
		assertEquals("reference 3", lReference+"b", lReferenceChecked);
	}

}
