package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.Text.ITextValues;
import org.hip.vif.core.bom.TextAuthorReviewerHome;
import org.hip.vif.core.bom.TextHistoryHome;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.search.AbstractSearching;
import org.hip.vif.core.search.NoHitsException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Luthiger Created: 17.06.2010 */
public class TextImplTest {
    private static DataHouseKeeper data;
    private Analyzer analyzer;

    @BeforeClass
    public static void init() {
        data = DataHouseKeeper.getInstance();
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
        final TextHome lHome = BOMHelper.getTextHome();

        assertEquals("count 0", 0, lHome.getCount());

        final String lTitle = "My Bibliography";
        final String lAuthor = "Doe, Jane";

        final Text lText = (Text) lHome.create();
        Long lTextId = lText.ucNew(createTestValues(lTitle, lAuthor), new Long(77));
        assertEquals("count 1", 1, lHome.getCount());
        assertEquals("count authors", 1, BOMHelper.getTextAuthorReviewerHome().getCount());

        KeyObject lKey = new KeyObjectImpl();
        lKey.setValue(TextHome.KEY_ID, lTextId);
        Text lRetrieved = (Text) lHome.findByKey(lKey);

        assertEquals("title", lTitle, lRetrieved.get(TextHome.KEY_TITLE));
        assertEquals("author", lAuthor, lRetrieved.get(TextHome.KEY_AUTHOR));
        assertEquals("reference 1", "Doe 2010", lRetrieved.get(TextHome.KEY_REFERENCE));
        assertEquals("state 1", WorkflowAwareContribution.S_PRIVATE,
                Integer.parseInt(lRetrieved.get(TextHome.KEY_STATE).toString()));
        assertEquals("version 1", 0, Integer.parseInt(lRetrieved.get(TextHome.KEY_VERSION).toString()));
        assertEquals("count with ID 1", 1, lHome.getCount(lKey));

        // create version
        lRetrieved.ucNew(lTextId, 1, "Referenece", createTestValues(lTitle, lAuthor), new Long(88));

        assertEquals("count with ID 2", 2, lHome.getCount(lKey));
        lRetrieved = lHome.getText(lTextId, 1);
        assertEquals("state 2", WorkflowAwareContribution.S_PRIVATE,
                Integer.parseInt(lRetrieved.get(TextHome.KEY_STATE).toString()));
        assertEquals("version 2", 1, Integer.parseInt(lRetrieved.get(TextHome.KEY_VERSION).toString()));
        assertEquals("author", lAuthor, lRetrieved.get(TextHome.KEY_AUTHOR));

        // test reference functionality
        lText.setVirgin();
        lTextId = lText.ucNew(createTestValues("My Bibliography", "Adam Riese"), new Long(77));
        lKey = new KeyObjectImpl();
        lKey.setValue(TextHome.KEY_ID, lTextId);
        lRetrieved = (Text) lHome.findByKey(lKey);
        assertEquals("reference 2", "Riese 2010", lRetrieved.get(TextHome.KEY_REFERENCE));
    }

    @Test
    public void testNew() throws Exception {
        final TextHome lHome = BOMHelper.getTextHome();

        assertEquals("count 0", 0, lHome.getCount());

        final String lTitle = "My Bibliography";
        final String lAuthor = "Doe, Jane";
        final Long lActor = new Long(77);

        Text lText = (Text) lHome.create();
        lText.set(TextHome.KEY_AUTHOR, lAuthor);
        lText.set(TextHome.KEY_TITLE, lTitle);

        try {
            lText.ucNew(lActor);
            fail();
        } catch (final Exception lExc) {
            // intentionally left empty
        }

        lText.set(TextHome.KEY_YEAR, "2000");
        final Long lTextId = lText.ucNew(lActor);
        assertEquals("count 1", 1, lHome.getCount());
        assertEquals("count authors", 1, BOMHelper.getTextAuthorReviewerHome().getCount());

        lText = lHome.getText(lTextId, 0);
        assertEquals(lTitle, lText.get(TextHome.KEY_TITLE));
        assertEquals(lAuthor, lText.get(TextHome.KEY_AUTHOR));
        assertEquals("Doe 2000", lText.get(TextHome.KEY_REFERENCE));
    }

    @Test
    public void testUcSave() throws Exception {
        final String lTitle1 = "My Bibliography";
        final String lAuthor1 = "Doe, Jane";
        final String lTitle2 = "The Java Bible";
        final String lAuthor2 = "Mill, Francis";
        final Long lActor1 = new Long(77);
        final Long lActor2 = new Long(7);
        final Integer lVersion = new Integer(0);

        final TextHome lHome = BOMHelper.getTextHome();
        Text lText = (Text) lHome.create();
        final Long lTextId = lText.ucNew(createTestValues(lTitle1, lAuthor1), lActor1);
        assertEquals("count 1", 1, lHome.getCount());

        final KeyObject lKeyText = new KeyObjectImpl();
        lKeyText.setValue(TextHome.KEY_ID, lTextId);
        lKeyText.setValue(TextHome.KEY_VERSION, lVersion);
        lText = (Text) lHome.findByKey(lKeyText);

        assertEquals("title 1", lTitle1, lText.get(TextHome.KEY_TITLE));
        assertEquals("author 1", lAuthor1, lText.get(TextHome.KEY_AUTHOR));

        KeyObject lKeyTAR = new KeyObjectImpl();
        lKeyTAR.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        lKeyTAR.setValue(ResponsibleHome.KEY_MEMBER_ID, lActor1);
        lKeyTAR.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, lTextId);
        lKeyTAR.setValue(TextAuthorReviewerHome.KEY_VERSION, lVersion);

        final TextAuthorReviewerHome lTARHome = BOMHelper.getTextAuthorReviewerHome();
        assertNotNull("question-author-reviewer 1", lTARHome.findByKey(lKeyTAR));

        lText.ucSave(createTestValues(lTitle2, lAuthor2), lActor2);
        lText = (Text) lHome.findByKey(lKeyText);

        assertEquals("title 2", lTitle2, lText.get(TextHome.KEY_TITLE));
        assertEquals("author 2", lAuthor2, lText.get(TextHome.KEY_AUTHOR));

        assertEquals("count history 1", 1, lTARHome.getCount());
        try {
            lTARHome.findByKey(lKeyTAR);
            fail("should'nt get here");
        } catch (final BOMNotFoundException exc) {
            // left empty intentionally
        }

        lKeyTAR = new KeyObjectImpl();
        lKeyTAR.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        lKeyTAR.setValue(ResponsibleHome.KEY_MEMBER_ID, lActor2);
        lKeyTAR.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, lTextId);
        lKeyTAR.setValue(TextAuthorReviewerHome.KEY_VERSION, lVersion);

        assertNotNull("question-author-reviewer 2", lTARHome.findByKey(lKeyTAR));

        // save again with identical values, therefore, no update is made
        lText.ucSave(createTestValues(lTitle2, lAuthor2), lActor1);

        assertEquals("count history 2", 1, lTARHome.getCount());
        assertNotNull("question-author-reviewer 3", lTARHome.findByKey(lKeyTAR));
    }

    @Test
    public void testSave() throws Exception {
        final String lTitle1 = "My Bibliography";
        final String lAuthor1 = "Doe, Jane";
        final String lTitle2 = "The Java Bible";
        final String lAuthor2 = "Mill, Francis";
        final Long lActor1 = new Long(77);
        final Long lActor2 = new Long(7);
        final Integer lVersion = new Integer(0);

        final TextHome lHome = BOMHelper.getTextHome();
        Text lText = (Text) lHome.create();
        final Long lTextId = lText.ucNew(createTestValues(lTitle1, lAuthor1), lActor1);
        assertEquals("count 1", 1, lHome.getCount());

        final KeyObject lKeyText = new KeyObjectImpl();
        lKeyText.setValue(TextHome.KEY_ID, lTextId);
        lKeyText.setValue(TextHome.KEY_VERSION, lVersion);
        lText = (Text) lHome.findByKey(lKeyText);

        assertEquals("title 1", lTitle1, lText.get(TextHome.KEY_TITLE));
        assertEquals("author 1", lAuthor1, lText.get(TextHome.KEY_AUTHOR));

        KeyObject lKeyTAR = new KeyObjectImpl();
        lKeyTAR.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        lKeyTAR.setValue(ResponsibleHome.KEY_MEMBER_ID, lActor1);
        lKeyTAR.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, lTextId);
        lKeyTAR.setValue(TextAuthorReviewerHome.KEY_VERSION, lVersion);

        final TextAuthorReviewerHome lTARHome = BOMHelper.getTextAuthorReviewerHome();
        assertNotNull("question-author-reviewer 1", lTARHome.findByKey(lKeyTAR));

        lText.set(TextHome.KEY_TITLE, lTitle2);
        lText.set(TextHome.KEY_AUTHOR, lAuthor2);

        lText.ucSave(lActor2);
        lText = (Text) lHome.findByKey(lKeyText);

        assertEquals("title 2", lTitle2, lText.get(TextHome.KEY_TITLE));
        assertEquals("author 2", lAuthor2, lText.get(TextHome.KEY_AUTHOR));

        assertEquals("count history 1", 1, lTARHome.getCount());
        try {
            lTARHome.findByKey(lKeyTAR);
            fail("should'nt get here");
        } catch (final BOMNotFoundException exc) {
            // left empty intentionally
        }

        lKeyTAR = new KeyObjectImpl();
        lKeyTAR.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.AUTHOR.getValue());
        lKeyTAR.setValue(ResponsibleHome.KEY_MEMBER_ID, lActor2);
        lKeyTAR.setValue(TextAuthorReviewerHome.KEY_TEXT_ID, lTextId);
        lKeyTAR.setValue(TextAuthorReviewerHome.KEY_VERSION, lVersion);

        assertNotNull("question-author-reviewer 2", lTARHome.findByKey(lKeyTAR));

        // save again with identical values, therefore, no update is made
        lText.set(TextHome.KEY_TITLE, lTitle2);
        lText.set(TextHome.KEY_AUTHOR, lAuthor2);
        lText.ucSave(lActor1);

        assertEquals("count history 2", 1, lTARHome.getCount());
        assertNotNull("question-author-reviewer 3", lTARHome.findByKey(lKeyTAR));
    }

    @Test
    public void testStateChange() throws Exception {
        final TextHome lHome = data.getTextHome();
        final TextHistoryHome lHistoryHome = BOMHelper.getTextHistoryHome();
        final Long lTextID = data.createText("Title", "Author");
        Text lText = lHome.getText(lTextID, 0);
        assertEquals("state 1", WorkflowAwareContribution.S_PRIVATE,
                Integer.parseInt(lText.get(TextHome.KEY_STATE).toString()));
        assertEquals("count history 0", 0, lHistoryHome.getCount());

        ((WorkflowAwareContribution) lText).onTransition_RequestReview(new Long(6));
        lText = lHome.getText(lTextID, 0);
        assertEquals("state 2", WorkflowAwareContribution.S_WAITING_FOR_REVIEW,
                Integer.parseInt(lText.get(TextHome.KEY_STATE).toString()));
        assertEquals("count history 1", 1, lHistoryHome.getCount());
    }

    //
    // @Test
    // public void testGetOptions() throws Exception {
    // String lExpected = "<option value=\"0\">Buch</option>" + NL + "<option value=\"1\">Artikel</option>" + NL +
    // "<option value=\"2\">Beitrag</option>" + NL + "<option value=\"3\">Web-Seite</option>" + NL;
    // assertEquals("rendered options de", lExpected, TextImpl.getOptions(Locale.GERMAN));
    //
    // lExpected = "<option value=\"0\">Book</option>" + NL + "<option value=\"1\">Article</option>" + NL +
    // "<option value=\"2\">Contribution</option>" + NL + "<option value=\"3\">Web-Page</option>" + NL;
    // assertEquals("rendered options de", lExpected, TextImpl.getOptions(Locale.ENGLISH));
    //
    // TextHome lHome = BOMHelper.getTextHome();
    // String lTextID = data.createText("Title", "Author");
    // Text lText = lHome.getText(lTextID, 0);
    //
    // lExpected = "<option value=\"0\">Buch</option>" + NL +
    // "<option value=\"1\" selected=\"selected\">Artikel</option>" + NL + "<option value=\"2\">Beitrag</option>" + NL +
    // "<option value=\"3\">Web-Seite</option>" + NL;
    // assertEquals("rendered options selected de", lExpected, lText.getOptionsSelected());
    //
    // lExpected = "<option value=\"0\">Book</option>" + NL +
    // "<option value=\"1\" selected=\"selected\">Article</option>" + NL + "<option value=\"2\">Contribution</option>" +
    // NL + "<option value=\"3\">Web-Page</option>" + NL;
    // assertEquals("rendered options selected en", lExpected, lText.getOptionsSelected());
    // }

    @Test
    public void testIndexing() throws Exception {
        final TextHome lHome = BOMHelper.getTextHome();

        final Object[] lActorID = new Object[] { new Long(96) };
        assertEquals("number of indexed 1", 0, IndexHouseKeeper.countIndexedContents());

        final String lTitle1 = "Design patterns";
        final Long lTextID = data.createText(lTitle1, "Author");
        assertEquals("number of indexed 1", 0, IndexHouseKeeper.countIndexedContents());

        pause(500);
        Text lText = lHome.getText(lTextID, 0);
        ((WorkflowAware) lText).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, lActorID);

        final String lTitle2 = "Entwurfsmuster";
        clone(lText, lHome, 1, lTitle2, WorkflowAwareContribution.S_PRIVATE);
        assertEquals("count", 2, lHome.getCount());

        // we find the first title indexed
        assertEquals("number of indexed 2", 1, IndexHouseKeeper.countIndexedContents());
        assertIndexedTitle("indexed 'Design patterns'", lTitle1, 1);
        assertIndexedTitle("not indexed 'Entwurfsmuster'", lTitle2, 0);

        pause(600);
        lText = lHome.getText(lTextID, 1);
        ((WorkflowAware) lText).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, lActorID);

        // we expect the second title indexed now
        assertIndexedTitle("unindexed 'Design patterns'", lTitle1, 0);
        assertIndexedTitle("indexed 'Entwurfsmuster'", lTitle2, 1);

        // check states
        lText = lHome.getText(lTextID, 1);
        assertEquals("version 1: published", WorkflowAwareContribution.S_OPEN,
                Integer.parseInt(lText.get(TextHome.KEY_STATE).toString()));
        pause(600);
        lText = lHome.getText(lTextID, 0);
        assertEquals("version 0: deleted", WorkflowAwareContribution.S_DELETED,
                Integer.parseInt(lText.get(TextHome.KEY_STATE).toString()));
    }

    private void clone(final Text inText, final TextHome inTextHome, final int inVersion, final String inTitle,
            final int inState) throws VException, SQLException {
        final DomainObject lClone = inTextHome.create();
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

    private void assertIndexedTitle(final String inMessage, final String inSearch, final int inHits) throws Exception {
        final IndexReader lReader = DirectoryReader.open(IndexHouseKeeper.getContentsIndexDir());
        try {
            final String lFieldName = AbstractSearching.IndexField.BIBLIO_TITLE.fieldName;
            final Document[] lHits = IndexHouseKeeper.search(createQuery(inSearch, lFieldName), lReader);
            assertEquals(inMessage, inHits, lHits.length);
        } catch (final NoHitsException exc) {
            if (inHits > 0) {
                fail(inMessage);
            }
        } finally {
            lReader.close();
        }
    }

    private Query createQuery(final String inQuery, final String inField) throws ParseException {
        final QueryParser lParser = new QueryParser(inField, analyzer);
        return lParser.parse(inQuery);
    }

    private void pause(final long inMillis) throws InterruptedException {
        synchronized (this) {
            wait(inMillis);
        }
    }

    private ITextValues createTestValues(final String inTitle, final String inAuthor) {
        return new DataHouseKeeper.TextValues(inTitle, inAuthor, "Foo, James", "Everything you need to know", "2010",
                "The Publication", "20-77", "5", "76", "NZZ Press", "Zürich", "Very <strong>strange</strong> story", 1);
    }

    @Test
    public void testCreateHistory() throws Exception {
        // preparation
        final String lTitle1 = "My Bibliography";
        final String lAuthor1 = "Doe, Jane";
        final Long lActor1 = new Long(77);

        final TextHome lHome = BOMHelper.getTextHome();
        final TextHistoryHome lHistoryHome = BOMHelper.getTextHistoryHome();
        Text lText = (Text) lHome.create();
        final Long lTextId = lText.ucNew(createTestValues(lTitle1, lAuthor1), lActor1);
        assertEquals(1, lHome.getCount());
        assertEquals(0, lHistoryHome.getCount());

        lText = lHome.getText(lTextId, 0);

        // the historization sequence
        final Timestamp lMutationDate = new Timestamp(System.currentTimeMillis());
        final DomainObject lHistory = ((TextImpl) lText).createHistory();
        lHistory.set(TextHome.KEY_TO, lMutationDate);
        lHistory.set(TextHistoryHome.KEY_MEMBER_ID, lActor1);
        lHistory.insert(true);

        // test the outcome
        assertEquals(1, lHistoryHome.getCount());
        assertEquals(lTitle1, lHistory.get(TextHome.KEY_TITLE));
        assertEquals(lAuthor1, lHistory.get(TextHome.KEY_AUTHOR));
    }

    @Test
    public void testCreateVersion() throws Exception {
        // preparation
        final String lTitle1 = "My Bibliography";
        final String lTitle2 = "Your Bibliography";
        final String lAuthor1 = "Doe, Jane";
        final Long lActor1 = new Long(77);

        final TextHome lHome = BOMHelper.getTextHome();
        Text lText = (Text) lHome.create();
        final Long lTextId = lText.ucNew(createTestValues(lTitle1, lAuthor1), lActor1);
        assertEquals(1, lHome.getCount());

        // create version
        lText = lHome.getText(lTextId, 0); // version is 0
        lText.set(TextHome.KEY_TITLE, lTitle2);
        final Long lTextIdNew = lText.createNewVersion(new Long(76));

        assertTrue(lTextId.equals(lTextIdNew));
        assertEquals(2, lHome.getCount());

        lText = lHome.getText(lTextId, 1); // version is 1
        assertEquals(lTitle2, lText.get(TextHome.KEY_TITLE));
        assertEquals(lAuthor1, lText.get(TextHome.KEY_AUTHOR));
    }

    @Test
    public void testCreateNewVersion() throws Exception {
        final String lTitle = "My Bibliography";
        final String lAuthor = "Doe, Jane";
        final Long lActor1 = new Long(77);

        final TextHome lHome = BOMHelper.getTextHome();
        Text lText = (Text) lHome.create();
        lText.set(TextHome.KEY_AUTHOR, lAuthor);
        lText.set(TextHome.KEY_TITLE, lTitle);
        lText.set(TextHome.KEY_YEAR, "1999");
        assertEquals(0, lHome.getCount());

        final Long lTextId = lText.createNewVersion(lActor1);
        assertEquals(1, lHome.getCount());
        lText = lHome.getText(lTextId, 0);
        assertEquals(lTitle, lText.get(TextHome.KEY_TITLE));
        assertEquals(lAuthor, lText.get(TextHome.KEY_AUTHOR));
    }

    @Test
    public void testIsValid() throws Exception {
        final Text lText = (Text) BOMHelper.getTextHome().create();
        assertFalse(lText.isValid());

        lText.set(TextHome.KEY_AUTHOR, "author");
        assertFalse(lText.isValid());

        lText.set(TextHome.KEY_TITLE, "title");
        assertFalse(lText.isValid());

        lText.set(TextHome.KEY_YEAR, "2000");
        assertTrue(lText.isValid());

    }
}
