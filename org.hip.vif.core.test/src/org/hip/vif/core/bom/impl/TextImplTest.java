package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.TestAppContext;
import org.hip.vif.core.TestApplication;
import org.hip.vif.core.adapters.BibliographyAdapter;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.Text.ITextValues;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.TextAuthorReviewerHome;
import org.hip.vif.core.bom.TextHistoryHome;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.search.AbstractSearching;
import org.hip.vif.core.search.NoHitsException;
import org.hip.vif.core.service.ApplicationData;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 17.06.2010
 */
public class TextImplTest {
	private static final String NL = System.getProperty("line.separator");
	
	private static DataHouseKeeper data;
	private Analyzer analyzer;

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
		
		TestApplication lApp = new TestApplication();
		lApp.start(null, null, new TestAppContext());
		ApplicationData.create(lApp);
	}

	@Before
	public void setUp() throws Exception {
		analyzer = IndexHouseKeeper.getAnalyzer();
		IndexHouseKeeper.redirectDocRoot(true);
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllInAll();
		IndexHouseKeeper.deleteTestIndexDir();
	}

	@Test
	public void testUcNew() throws Exception {
		TextHome lHome = BOMHelper.getTextHome();
		
		assertEquals("count 0", 0, lHome.getCount());
		
		String lTitle = "My Bibliography";
		String lAuthor = "Doe, Jane";
		
		Text lText = (Text) lHome.create();
		Long lTextId = lText.ucNew(createTestValues(lTitle, lAuthor), new Long(77));
		assertEquals("count 1", 1, lHome.getCount());
		assertEquals("count authors", 1, BOMHelper.getTextAuthorReviewerHome().getCount());
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(TextHome.KEY_ID, lTextId);
		Text lRetrieved = (Text)lHome.findByKey(lKey);
		
		assertEquals("title", lTitle, lRetrieved.get(TextHome.KEY_TITLE));
		assertEquals("author", lAuthor, lRetrieved.get(TextHome.KEY_AUTHOR));
		assertEquals("reference 1", "Doe 2010", lRetrieved.get(TextHome.KEY_REFERENCE));
		assertEquals("state 1", WorkflowAwareContribution.S_PRIVATE, Integer.parseInt(lRetrieved.get(TextHome.KEY_STATE).toString()));
		assertEquals("version 1", 0, Integer.parseInt(lRetrieved.get(TextHome.KEY_VERSION).toString()));
		assertEquals("count with ID 1", 1, lHome.getCount(lKey));
		
		//create version
		lRetrieved.ucNew(lTextId, 1, "Referenece", createTestValues(lTitle, lAuthor), new Long(88));
		
		assertEquals("count with ID 2", 2, lHome.getCount(lKey));
		lRetrieved = lHome.getText(lTextId, 1);
		assertEquals("state 2", WorkflowAwareContribution.S_PRIVATE, Integer.parseInt(lRetrieved.get(TextHome.KEY_STATE).toString()));
		assertEquals("version 2", 1, Integer.parseInt(lRetrieved.get(TextHome.KEY_VERSION).toString()));
		assertEquals("author", lAuthor, lRetrieved.get(TextHome.KEY_AUTHOR));

		//test reference functionality
		lText.setVirgin();
		lTextId = lText.ucNew(createTestValues("My Bibliography", "Adam Riese"), new Long(77));
		lKey = new KeyObjectImpl();
		lKey.setValue(TextHome.KEY_ID, lTextId);
		lRetrieved = (Text)lHome.findByKey(lKey);
		assertEquals("reference 2", "Riese 2010", lRetrieved.get(TextHome.KEY_REFERENCE));
	}
	
	@Test
	public void testNew() throws Exception {
		TextHome lHome = BOMHelper.getTextHome();
		
		assertEquals("count 0", 0, lHome.getCount());
		
		String lTitle = "My Bibliography";
		String lAuthor = "Doe, Jane";
		Long lActor = new Long(77);
		
		Text lText = (Text) lHome.create();
		lText.set(TextHome.KEY_AUTHOR, lAuthor);
		lText.set(TextHome.KEY_TITLE, lTitle);
		
		try {
			lText.ucNew(lActor);
			fail();
		}
		catch (Exception lExc) {
			// intentionally left empty
		}
		
		lText.set(TextHome.KEY_YEAR, "2000");
		Long lTextId = lText.ucNew(lActor);
		assertEquals("count 1", 1, lHome.getCount());
		assertEquals("count authors", 1, BOMHelper.getTextAuthorReviewerHome().getCount());
		
		lText = lHome.getText(lTextId, 0);
		assertEquals(lTitle, lText.get(TextHome.KEY_TITLE));
		assertEquals(lAuthor, lText.get(TextHome.KEY_AUTHOR));
		assertEquals("Doe 2000", lText.get(TextHome.KEY_REFERENCE));
	}
	
	@Test
	public void testUcSave() throws Exception {
		String lTitle1 = "My Bibliography";
		String lAuthor1 = "Doe, Jane";
		String lTitle2 = "The Java Bible";
		String lAuthor2 = "Mill, Francis";
		Long lActor1 = new Long(77);
		Long lActor2 = new Long(7);
		Integer lVersion = new Integer(0);
		
		TextHome lHome = BOMHelper.getTextHome();
		Text lText = (Text) lHome.create();
		Long lTextId = lText.ucNew(createTestValues(lTitle1, lAuthor1), lActor1);
		assertEquals("count 1", 1, lHome.getCount());

		KeyObject lKeyText = new KeyObjectImpl();
		lKeyText.setValue(TextHome.KEY_ID, lTextId);
		lKeyText.setValue(TextHome.KEY_VERSION, lVersion);
		lText = (Text) lHome.findByKey(lKeyText);
		
		assertEquals("title 1", lTitle1, (String)lText.get(TextHome.KEY_TITLE));
		assertEquals("author 1", lAuthor1, (String)lText.get(TextHome.KEY_AUTHOR));
		
		KeyObject lKeyTAR = new KeyObjectImpl();
		lKeyTAR.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		lKeyTAR.setValue(ResponsibleHome.KEY_MEMBER_ID, lActor1);
		lKeyTAR.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, lTextId);			
		lKeyTAR.setValue(TextAuthorReviewerHome.KEY_VERSION, lVersion);
		
		TextAuthorReviewerHome lTARHome = BOMHelper.getTextAuthorReviewerHome();
		assertNotNull("question-author-reviewer 1", lTARHome.findByKey(lKeyTAR));
		
		lText.ucSave(createTestValues(lTitle2, lAuthor2), lActor2);
		lText = (Text) lHome.findByKey(lKeyText);
		
		assertEquals("title 2", lTitle2, (String)lText.get(TextHome.KEY_TITLE));
		assertEquals("author 2", lAuthor2, (String)lText.get(TextHome.KEY_AUTHOR));
		
		assertEquals("count history 1", 1, lTARHome.getCount());
		try {
			 lTARHome.findByKey(lKeyTAR);
			 fail("should'nt get here");
		}
		catch (BOMNotFoundException exc) {
			//left empty intentionally
		}
		
		lKeyTAR = new KeyObjectImpl();
		lKeyTAR.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		lKeyTAR.setValue(ResponsibleHome.KEY_MEMBER_ID, lActor2);
		lKeyTAR.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, lTextId);			
		lKeyTAR.setValue(TextAuthorReviewerHome.KEY_VERSION, lVersion);
		
		assertNotNull("question-author-reviewer 2", lTARHome.findByKey(lKeyTAR));
		
		//save again with identical values, therefore, no update is made
		lText.ucSave(createTestValues(lTitle2, lAuthor2), lActor1);
		
		assertEquals("count history 2", 1, lTARHome.getCount());
		assertNotNull("question-author-reviewer 3", lTARHome.findByKey(lKeyTAR));
	}
	
	@Test
	public void testSave() throws Exception {
		String lTitle1 = "My Bibliography";
		String lAuthor1 = "Doe, Jane";
		String lTitle2 = "The Java Bible";
		String lAuthor2 = "Mill, Francis";
		Long lActor1 = new Long(77);
		Long lActor2 = new Long(7);
		Integer lVersion = new Integer(0);
		
		TextHome lHome = BOMHelper.getTextHome();
		Text lText = (Text) lHome.create();
		Long lTextId = lText.ucNew(createTestValues(lTitle1, lAuthor1), lActor1);
		assertEquals("count 1", 1, lHome.getCount());
		
		KeyObject lKeyText = new KeyObjectImpl();
		lKeyText.setValue(TextHome.KEY_ID, lTextId);
		lKeyText.setValue(TextHome.KEY_VERSION, lVersion);
		lText = (Text) lHome.findByKey(lKeyText);
		
		assertEquals("title 1", lTitle1, (String)lText.get(TextHome.KEY_TITLE));
		assertEquals("author 1", lAuthor1, (String)lText.get(TextHome.KEY_AUTHOR));
		
		KeyObject lKeyTAR = new KeyObjectImpl();
		lKeyTAR.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		lKeyTAR.setValue(ResponsibleHome.KEY_MEMBER_ID, lActor1);
		lKeyTAR.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, lTextId);			
		lKeyTAR.setValue(TextAuthorReviewerHome.KEY_VERSION, lVersion);
		
		TextAuthorReviewerHome lTARHome = BOMHelper.getTextAuthorReviewerHome();
		assertNotNull("question-author-reviewer 1", lTARHome.findByKey(lKeyTAR));
		
		lText.set(TextHome.KEY_TITLE, lTitle2);
		lText.set(TextHome.KEY_AUTHOR, lAuthor2);
		
		lText.ucSave(lActor2);
		lText = (Text) lHome.findByKey(lKeyText);
		
		assertEquals("title 2", lTitle2, (String)lText.get(TextHome.KEY_TITLE));
		assertEquals("author 2", lAuthor2, (String)lText.get(TextHome.KEY_AUTHOR));
		
		assertEquals("count history 1", 1, lTARHome.getCount());
		try {
			lTARHome.findByKey(lKeyTAR);
			fail("should'nt get here");
		}
		catch (BOMNotFoundException exc) {
			//left empty intentionally
		}
		
		lKeyTAR = new KeyObjectImpl();
		lKeyTAR.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
		lKeyTAR.setValue(ResponsibleHome.KEY_MEMBER_ID, lActor2);
		lKeyTAR.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, lTextId);			
		lKeyTAR.setValue(TextAuthorReviewerHome.KEY_VERSION, lVersion);
		
		assertNotNull("question-author-reviewer 2", lTARHome.findByKey(lKeyTAR));
		
		//save again with identical values, therefore, no update is made
		lText.set(TextHome.KEY_TITLE, lTitle2);
		lText.set(TextHome.KEY_AUTHOR, lAuthor2);
		lText.ucSave(lActor1);
		
		assertEquals("count history 2", 1, lTARHome.getCount());
		assertNotNull("question-author-reviewer 3", lTARHome.findByKey(lKeyTAR));
	}
	
	@Test
	public void testStateChange() throws Exception {
		TextHome lHome = data.getTextHome();
		TextHistoryHome lHistoryHome = BOMHelper.getTextHistoryHome();
		Long lTextID = data.createText("Title", "Author");
		Text lText = lHome.getText(lTextID, 0);
		assertEquals("state 1", WorkflowAwareContribution.S_PRIVATE, Integer.parseInt(lText.get(TextHome.KEY_STATE).toString()));
		assertEquals("count history 0", 0, lHistoryHome.getCount());
		
		((WorkflowAwareContribution)lText).onTransition_RequestReview(new Long(6));
		lText = lHome.getText(lTextID, 0);
		assertEquals("state 2", WorkflowAwareContribution.S_WAITING_FOR_REVIEW, Integer.parseInt(lText.get(TextHome.KEY_STATE).toString()));
		assertEquals("count history 1", 1, lHistoryHome.getCount());
	}
//	
//	@Test
//	public void testGetOptions() throws Exception {
//		String lExpected = "<option value=\"0\">Buch</option>" + NL + "<option value=\"1\">Artikel</option>" + NL + "<option value=\"2\">Beitrag</option>" + NL + "<option value=\"3\">Web-Seite</option>" + NL;
//		assertEquals("rendered options de", lExpected, TextImpl.getOptions(Locale.GERMAN));
//
//		lExpected = "<option value=\"0\">Book</option>" + NL + "<option value=\"1\">Article</option>" + NL + "<option value=\"2\">Contribution</option>" + NL +	"<option value=\"3\">Web-Page</option>" + NL;
//		assertEquals("rendered options de", lExpected, TextImpl.getOptions(Locale.ENGLISH));
//
//		TextHome lHome = BOMHelper.getTextHome();
//		String lTextID = data.createText("Title", "Author");
//		Text lText = lHome.getText(lTextID, 0);
//
//		lExpected = "<option value=\"0\">Buch</option>" + NL + "<option value=\"1\" selected=\"selected\">Artikel</option>" + NL + "<option value=\"2\">Beitrag</option>" + NL + "<option value=\"3\">Web-Seite</option>" + NL;
//		assertEquals("rendered options selected de", lExpected, lText.getOptionsSelected());
//
//		lExpected = "<option value=\"0\">Book</option>" + NL + "<option value=\"1\" selected=\"selected\">Article</option>" + NL + "<option value=\"2\">Contribution</option>" + NL +	"<option value=\"3\">Web-Page</option>" + NL;
//		assertEquals("rendered options selected en", lExpected, lText.getOptionsSelected());
//	}
	
	@Test
	public void testIndexing() throws Exception {
		TextHome lHome = BOMHelper.getTextHome();

		Object[] lActorID = new Object[] {new Long(96)};
		assertEquals("number of indexed 1", 0, IndexHouseKeeper.countIndexedContents());
		
		String lTitle1 = "Design patterns";
		Long lTextID = data.createText(lTitle1, "Author");
		assertEquals("number of indexed 1", 0, IndexHouseKeeper.countIndexedContents());
		
		pause(500);
		Text lText = lHome.getText(lTextID, 0);
		((WorkflowAware)lText).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, lActorID);
		
		String lTitle2 = "Entwurfsmuster";
		clone(lText, lHome, 1, lTitle2, WorkflowAwareContribution.S_PRIVATE);
		assertEquals("count", 2, lHome.getCount());

		//we find the first title indexed
		assertEquals("number of indexed 2", 1, IndexHouseKeeper.countIndexedContents());
		assertIndexedTitle("indexed 'Design patterns'", lTitle1, 1);
		assertIndexedTitle("not indexed 'Entwurfsmuster'", lTitle2, 0);		
		
		pause(600);
		lText = lHome.getText(lTextID, 1);
		((WorkflowAware)lText).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, lActorID);

		//we expect the second title indexed now
		assertIndexedTitle("unindexed 'Design patterns'", lTitle1, 0);
		assertIndexedTitle("indexed 'Entwurfsmuster'", lTitle2, 1);
		
		//check states
		lText = lHome.getText(lTextID, 1);
		assertEquals("version 1: published", WorkflowAwareContribution.S_OPEN, Integer.parseInt(lText.get(TextHome.KEY_STATE).toString()));
		pause(600);
		lText = lHome.getText(lTextID, 0);
		assertEquals("version 0: deleted", WorkflowAwareContribution.S_DELETED, Integer.parseInt(lText.get(TextHome.KEY_STATE).toString()));
	}

	private void clone(Text inText, TextHome inTextHome, int inVersion, String inTitle, int inState) throws VException, SQLException {
		DomainObject lClone = inTextHome.create();
		lClone.set(TextHome.KEY_TYPE, inText.get(TextHome.KEY_TYPE));
		lClone.set(TextHome.KEY_AUTHOR, inText.get(TextHome.KEY_AUTHOR));
		lClone.set(TextHome.KEY_COAUTHORS, inText.get(TextHome.KEY_COAUTHORS));
		lClone.set(TextHome.KEY_SUBTITLE, inText.get(TextHome.KEY_SUBTITLE));
		lClone.set(TextHome.KEY_YEAR, inText.get(TextHome.KEY_YEAR));
		lClone.set(TextHome.KEY_PUBLICATION, inText.get(TextHome.KEY_PUBLICATION));
		lClone.set(TextHome.KEY_PAGES, inText.get(TextHome.KEY_PAGES));
		lClone.set(TextHome.KEY_VOLUME, inText.get(TextHome.KEY_VOLUME));
		lClone.set(TextHome.KEY_NUMBER, inText.get(TextHome.KEY_NUMBER));
		lClone.set(TextHome.KEY_PUBLISHER, inText.get(TextHome.KEY_PUBLISHER));
		lClone.set(TextHome.KEY_PLACE, inText.get(TextHome.KEY_PLACE));
		lClone.set(TextHome.KEY_REMARK, inText.get(TextHome.KEY_REMARK));
		lClone.set(TextHome.KEY_ID, inText.get(TextHome.KEY_ID));

		lClone.set(TextHome.KEY_STATE, new Long(inState));
		lClone.set(TextHome.KEY_VERSION, new Long(inVersion));
		lClone.set(TextHome.KEY_TITLE, inTitle);
		lClone.insert(true);
	}
	
	private void assertIndexedTitle(String inMessage, String inSearch, int inHits) throws Exception {
		IndexReader lReader = IndexReader.open(IndexHouseKeeper.getContentsIndexDir());
		try {
			String lFieldName = AbstractSearching.IndexField.BIBLIO_TITLE.fieldName;
			Document[] lHits = IndexHouseKeeper.search(createQuery(inSearch, lFieldName), lReader);
			assertEquals(inMessage, inHits, lHits.length);
		}
		catch (NoHitsException exc) {
			if (inHits > 0) {
				fail(inMessage);
			}
		}
		finally {
			lReader.close();
		}
	}
	
	private Query createQuery(String inQuery, String inField) throws ParseException {
		QueryParser lParser = new QueryParser(IndexHouseKeeper.LUCENE_VERSION, inField, analyzer);
		return lParser.parse(inQuery);
	}
	
	private void pause(long inMillis) throws InterruptedException {
		synchronized (this) {
			wait(inMillis);
		}
	}
	
	private ITextValues createTestValues(String inTitle, String inAuthor) {
		return new DataHouseKeeper.TextValues(inTitle, inAuthor, "Foo, James", "Everything you need to know", "2010", "The Publication", "20-77", "5", "76", "NZZ Press", "Zürich", "Very <strong>strange</strong> story", 1);
	}
	
	@Test
	public void testGetNotification() throws Exception {
		
		String lTitle = "My Bibliography";
		String lAuthor = "Doe, Jane";
		Long lActor = new Long(77);
		Integer lVersion = new Integer(0);
		
		TextHome lHome = BOMHelper.getTextHome();
		Text lText = (Text) lHome.create();
		Long lTextId = lText.ucNew(createTestValues(lTitle, lAuthor), lActor);

		KeyObject lKeyText = new KeyObjectImpl();
		lKeyText.setValue(TextHome.KEY_ID, lTextId);
		lKeyText.setValue(TextHome.KEY_VERSION, lVersion);
		lText = (Text) lHome.findByKey(lKeyText);
	
		String lExptected = "Typ: Artikel" + NL + "Titel: My Bibliography" + NL + "Untertitel: Everything you need to know" + NL + "Autor: Doe, Jane" + NL + "Co-Autoren: Foo, James" + NL + "Jahr: 2010" + NL + "Zeitschrift: The Publication" + NL + "Herausgeber: NZZ Press" + NL + "Ort: Zürich" + NL + "Seiten: 20-77" + NL + "Vol.: 5" + NL + "Nr.: 76" + NL + "Bemerkung: Very strange story" + NL;
		ApplicationData.initLocale(Locale.GERMAN);
		assertEquals("notification de", lExptected, lText.getNotification());

		ApplicationData.initLocale(Locale.ENGLISH);
		lExptected = "Type: Article" + NL + "Title: My Bibliography" + NL + "Subtitle: Everything you need to know" + NL + "Author: Doe, Jane" + NL + "Co-Authors: Foo, James" + NL + "Year: 2010" + NL + "Publication: The Publication" + NL + "Publisher: NZZ Press" + NL + "Place: Zürich" + NL + "Pages: 20-77" + NL + "Volume: 5" + NL + "Number: 76" + NL + "Remark: Very strange story" + NL;
		assertEquals("notification en", lExptected, lText.getNotification());
		
		ApplicationData.initLocale(Locale.GERMAN);
		lExptected = "<table border=\"0\" style=\"font-size:11pt;\"><tr><td><i>Typ</i>:</td><td>Artikel</td></tr><tr><td><i>Titel</i>:</td><td>My Bibliography</td></tr><tr><td><i>Untertitel</i>:</td><td>Everything you need to know</td></tr><tr><td><i>Autor</i>:</td><td>Doe, Jane</td></tr><tr><td><i>Co-Autoren</i>:</td><td>Foo, James</td></tr><tr><td><i>Jahr</i>:</td><td>2010</td></tr><tr><td><i>Zeitschrift</i>:</td><td>The Publication</td></tr><tr><td><i>Herausgeber</i>:</td><td>NZZ Press</td></tr><tr><td><i>Ort</i>:</td><td>Zürich</td></tr><tr><td><i>Seiten</i>:</td><td>20-77</td></tr><tr><td><i>Vol.</i>:</td><td>5</td></tr><tr><td><i>Nr.</i>:</td><td>76</td></tr><tr><td><i>Bemerkung</i>:</td><td>Very <strong>strange</strong> story</td></tr></table>";
		assertEquals("notification de html", lExptected, lText.getNotificationHtml());

		ApplicationData.initLocale(Locale.ENGLISH);
		lExptected = "<table border=\"0\" style=\"font-size:11pt;\"><tr><td><i>Type</i>:</td><td>Article</td></tr><tr><td><i>Title</i>:</td><td>My Bibliography</td></tr><tr><td><i>Subtitle</i>:</td><td>Everything you need to know</td></tr><tr><td><i>Author</i>:</td><td>Doe, Jane</td></tr><tr><td><i>Co-Authors</i>:</td><td>Foo, James</td></tr><tr><td><i>Year</i>:</td><td>2010</td></tr><tr><td><i>Publication</i>:</td><td>The Publication</td></tr><tr><td><i>Publisher</i>:</td><td>NZZ Press</td></tr><tr><td><i>Place</i>:</td><td>Zürich</td></tr><tr><td><i>Pages</i>:</td><td>20-77</td></tr><tr><td><i>Volume</i>:</td><td>5</td></tr><tr><td><i>Number</i>:</td><td>76</td></tr><tr><td><i>Remark</i>:</td><td>Very <strong>strange</strong> story</td></tr></table>";
		assertEquals("notification en html", lExptected, lText.getNotificationHtml());

		lText.ucSave(new DataHouseKeeper.TextValues(lTitle, lAuthor, "", "", "2010", "", "", "", "", "", "", "", 1), lActor);

		ApplicationData.initLocale(Locale.GERMAN);
		lExptected = "Typ: Artikel" + NL + "Titel: My Bibliography" + NL + "Autor: Doe, Jane" + NL + "Jahr: 2010" + NL;
		assertEquals("notification de minimal 1", lExptected, lText.getNotification());

		lText.setVirgin();
		lTextId = lText.ucNew(new DataHouseKeeper.TextValues(lTitle, lAuthor, "", "", "2010", "", "", "", "", "", "", "", 1), lActor);
		
		lKeyText = new KeyObjectImpl();
		lKeyText.setValue(TextHome.KEY_ID, lTextId);
		lKeyText.setValue(TextHome.KEY_VERSION, lVersion);
		lText = (Text) lHome.findByKey(lKeyText);
		
		assertEquals("notification de minimal 2", lExptected, lText.getNotification());
		
		lExptected = "<table border=\"0\" style=\"font-size:11pt;\"><tr><td><i>Typ</i>:</td><td>Artikel</td></tr><tr><td><i>Titel</i>:</td><td>My Bibliography</td></tr><tr><td><i>Autor</i>:</td><td>Doe, Jane</td></tr><tr><td><i>Jahr</i>:</td><td>2010</td></tr></table>";
		assertEquals("notification de html minimal", lExptected, lText.getNotificationHtml());
//		System.out.println(lText.getNotificationHtml(Locale.GERMAN.getLanguage()));
	}

	@Test
	public void testHasWebPageUrl() throws Exception {
		Text lText = (Text) data.getTextHome().create();
		lText.set(TextHome.KEY_TYPE, new Long(TextImpl.TextType.WEBPAGE.getTypeValue()));
		
		lText.set(TextHome.KEY_PUBLICATION, "http://aaa.bb.org/");
		assertTrue("url 1", new BibliographyAdapter(lText, TextHome.KEY_TYPE).hasWebPageUrl());
		
		lText.set(TextHome.KEY_PUBLICATION, "https://aaa.bb.org/");
		assertTrue("url 2", new BibliographyAdapter(lText, TextHome.KEY_TYPE).hasWebPageUrl());
		
		lText.set(TextHome.KEY_PUBLICATION, "ftp://aaa.bb.org/");
		assertTrue("url 3", new BibliographyAdapter(lText, TextHome.KEY_TYPE).hasWebPageUrl());
		
		lText.set(TextHome.KEY_PUBLICATION, "www.bb.org/path");
		assertTrue("url 4", new BibliographyAdapter(lText, TextHome.KEY_TYPE).hasWebPageUrl());
		
		lText.set(TextHome.KEY_PUBLICATION, "some/path");
		assertFalse("no url 1", new BibliographyAdapter(lText, TextHome.KEY_TYPE).hasWebPageUrl());
		
		lText.set(TextHome.KEY_PUBLICATION, "http://aaa.bb.org/");
		lText.set(TextHome.KEY_TYPE, new Long(TextImpl.TextType.ARTICLE.getTypeValue()));
		assertFalse("no url 2", new BibliographyAdapter(lText, TextHome.KEY_TYPE).hasWebPageUrl());
	}

	@Test
	public void testCreateHistory() throws Exception {
		//preparation
		String lTitle1 = "My Bibliography";
		String lAuthor1 = "Doe, Jane";
		Long lActor1 = new Long(77);
		
		TextHome lHome = BOMHelper.getTextHome();
		TextHistoryHome lHistoryHome = BOMHelper.getTextHistoryHome();
		Text lText = (Text) lHome.create();
		Long lTextId = lText.ucNew(createTestValues(lTitle1, lAuthor1), lActor1);
		assertEquals(1, lHome.getCount());
		assertEquals(0, lHistoryHome.getCount());
		
		lText = lHome.getText(lTextId, 0);
		
		//the historization sequence
		Timestamp lMutationDate = new Timestamp(System.currentTimeMillis());
		DomainObject lHistory = ((TextImpl)lText).createHistory();
		lHistory.set(TextHome.KEY_TO, lMutationDate);
		lHistory.set(TextHistoryHome.KEY_MEMBER_ID, lActor1);
		lHistory.insert(true);
		
		//test the outcome
		assertEquals(1, lHistoryHome.getCount());
		assertEquals(lTitle1, (String)lHistory.get(TextHome.KEY_TITLE));
		assertEquals(lAuthor1, (String)lHistory.get(TextHome.KEY_AUTHOR));		
	}
	
	@Test
	public void testCreateVersion() throws Exception {
		//preparation
		String lTitle1 = "My Bibliography";
		String lTitle2 = "Your Bibliography";
		String lAuthor1 = "Doe, Jane";
		Long lActor1 = new Long(77);
		
		TextHome lHome = BOMHelper.getTextHome();
		Text lText = (Text) lHome.create();
		Long lTextId = lText.ucNew(createTestValues(lTitle1, lAuthor1), lActor1);
		assertEquals(1, lHome.getCount());
		
		//create version
		lText = lHome.getText(lTextId, 0); //version is 0
		lText.set(TextHome.KEY_TITLE, lTitle2);
		Long lTextIdNew = lText.createNewVersion(new Long(76));
		
		assertTrue(lTextId.equals(lTextIdNew));
		assertEquals(2, lHome.getCount());
		
		lText = lHome.getText(lTextId, 1); //version is 1
		assertEquals(lTitle2, lText.get(TextHome.KEY_TITLE));
		assertEquals(lAuthor1, lText.get(TextHome.KEY_AUTHOR));
	}
	
	@Test
	public void testCreateNewVersion() throws Exception {
		String lTitle = "My Bibliography";
		String lAuthor = "Doe, Jane";
		Long lActor1 = new Long(77);
		
		TextHome lHome = BOMHelper.getTextHome();
		Text lText = (Text) lHome.create();
		lText.set(TextHome.KEY_AUTHOR, lAuthor);
		lText.set(TextHome.KEY_TITLE, lTitle);
		lText.set(TextHome.KEY_YEAR, "1999");
		assertEquals(0, lHome.getCount());
		
		Long lTextId = lText.createNewVersion(lActor1);
		assertEquals(1, lHome.getCount());
		lText = lHome.getText(lTextId, 0);
		assertEquals(lTitle, lText.get(TextHome.KEY_TITLE));
		assertEquals(lAuthor, lText.get(TextHome.KEY_AUTHOR));
	}

	@Test
	public void testIsValid() throws Exception {
		Text lText = (Text) BOMHelper.getTextHome().create();
		assertFalse(lText.isValid());
		
		lText.set(TextHome.KEY_AUTHOR, "author");
		assertFalse(lText.isValid());
		
		lText.set(TextHome.KEY_TITLE, "title");
		assertFalse(lText.isValid());
		
		lText.set(TextHome.KEY_YEAR, "2000");
		assertTrue(lText.isValid());
		
	}
}
