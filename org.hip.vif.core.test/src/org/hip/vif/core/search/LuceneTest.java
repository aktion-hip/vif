package org.hip.vif.core.search;

import java.io.IOException;

import static org.junit.Assert.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Benno Luthiger
 * Created on 06.10.2005
 */
public class LuceneTest {
	private final static String IDENTITY = "identity";
	private final static String SEARCH_TEXT = "searchText";
	
	private final static int ID1 = 33;
	private final static int ID2 = 55;
	private final static int ID3 = 66;
	
	private final static String TEXT1 = "Jane Doe";
	private final static String TEXT2 = "Adam Riese";
	private final static String TEXT3 = "Julius";
	
	private Analyzer analyzer;

	@Before
	public void setUp() throws Exception {
		DataHouseKeeper.getInstance();
		analyzer = IndexHouseKeeper.getAnalyzer();
		IndexHouseKeeper.redirectDocRoot(true);
	}

	@After
	public void tearDown() throws Exception {
		IndexHouseKeeper.deleteTestIndexDir();
	}

	@Test
	public void testManageIndex() throws Exception {
		//create
		assertEquals("index new", 3, createIndex());
		
		//read
		assertEquals("number of indexed 1", 3, IndexHouseKeeper.countIndexedMembers());
		
		//search for 'Riese'
		IndexReader lReader = VIFIndexing.INSTANCE.createMemberIndexReader();
		Document[] lHits = IndexHouseKeeper.search(createQuery("Riese"), lReader);
		assertEquals("number of found 1", 1, lHits.length);
		assertEquals("ID of found 1", String.valueOf(ID2), lHits[0].get(IDENTITY));
		
		//remove document with id 1
		IndexWriter lWriter = VIFIndexing.INSTANCE.getMemberIndexWriter(false);
		Term lTerm = new Term(IDENTITY, String.valueOf(ID1));
		lWriter.deleteDocuments(lTerm);
		assertTrue(lWriter.hasDeletions());
		lWriter.commit();
		assertTrue(lWriter.hasDeletions());
		assertEquals("number of indexed 2", 2, lWriter.numDocs());
		
		//reindex existing doc with id 2 and text 1
		lWriter = VIFIndexing.INSTANCE.getMemberIndexWriter(false);
		lWriter.addDocument(createDoc(ID2, TEXT1));
		lWriter.optimize();
		lWriter.commit();
		
		//read again: the reindexed document is doubled and not replaced!
		assertEquals("number of indexed 3", 3, IndexHouseKeeper.countIndexedMembers());
		lReader = VIFIndexing.INSTANCE.createMemberIndexReader();;
		lHits = IndexHouseKeeper.search(createQuery("Jane"), lReader);
		assertEquals("ID of found 2", String.valueOf(ID2), lHits[0].get(IDENTITY));
		lHits = IndexHouseKeeper.search(createQuery("Riese"), lReader);
		assertEquals("ID of found 3", String.valueOf(ID2), lHits[0].get(IDENTITY));
		
		//for a correct reindex, we have to delete the document first...
		lTerm = new Term(IDENTITY, String.valueOf(ID2));
		lWriter.deleteDocuments(lTerm);
		lWriter.commit();
		assertTrue("has deletions 2", lWriter.hasDeletions());
		assertEquals("number of indexed 4", 1, lWriter.numDocs());

		//then reindex the document...
		lWriter = VIFIndexing.INSTANCE.getMemberIndexWriter(false);
		lWriter.addDocument(createDoc(ID2, TEXT1));
		lWriter.optimize();
		lWriter.commit();

		//now test.
		lReader = VIFIndexing.INSTANCE.createMemberIndexReader();;
		lHits = IndexHouseKeeper.search(createQuery("Jane"), lReader);
		assertEquals("ID of found 4", String.valueOf(ID2), lHits[0].get(IDENTITY));
		try {
			lHits = IndexHouseKeeper.search(createQuery("Riese"), lReader);
			fail("shouldn't get here, because no hits found for 'Riese'");
		}
		catch (NoHitsException exc) {
			//intentionally left empty
		}
	}
	
	private int createIndex() throws IOException {
		IndexWriter lWriter = VIFIndexing.INSTANCE.getMemberIndexWriter(false);
		lWriter.addDocument(createDoc(ID1, TEXT1));
		lWriter.addDocument(createDoc(ID2, TEXT2));
		lWriter.addDocument(createDoc(ID3, TEXT3));
		lWriter.optimize();
		lWriter.commit();
		return lWriter.numDocs();
	}
	
	private Document createDoc(int inID, String inText) {
		Document outDocument = new Document();
		outDocument.add(new Field(IDENTITY, String.valueOf(inID), Field.Store.YES, Field.Index.NOT_ANALYZED));
		outDocument.add(new Field(SEARCH_TEXT, inText, Field.Store.YES, Field.Index.ANALYZED));
		return outDocument;
	}
	
	private Query createQuery(String inItem) throws ParseException {
		QueryParser lParser = new QueryParser(IndexHouseKeeper.LUCENE_VERSION, SEARCH_TEXT, analyzer);
		return lParser.parse(inItem);
	}
	
}
