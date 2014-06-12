package org.hip.vif.core.bom.search;

import static org.junit.Assert.assertEquals;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.search.AbstractSearching;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 23.02.2012
 */
public class VIFContentIndexerTest {
	private static DataHouseKeeper data;
	private Analyzer analyzer;

	@BeforeClass
	public static void init() throws Exception {
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
	public void testAddQuestionToIndex() throws Exception {
		//we create the context: member as author of a question in a discussion group
		Long lGroupID = data.createGroup();
		Long lQuestionID = data.createQuestion("Index Test", "1.2.3", lGroupID, true);
		String lMemberID = data.createMember("1");
		data.createQuestionProducer(lQuestionID, new Long(lMemberID), true);
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHome.KEY_ID, lQuestionID);
		
		VIFContentIndexer lIndexer = new VIFContentIndexer();
		lIndexer.addQuestionToIndex(lKey);
		assertEquals("group and question still private", 0, IndexHouseKeeper.countIndexedContents());

		//make question public
		QuestionHome lHome = data.getQuestionHome();
		Question lQuestion = lHome.getQuestion(lQuestionID);
		lQuestion.set(QuestionHome.KEY_STATE, new Long(4));
		lQuestion.update(true);

		lIndexer.addQuestionToIndex(lKey);
		assertEquals("group still private", 0, IndexHouseKeeper.countIndexedContents());
		
		//make group active
		GroupHome lGroupHome = data.getGroupHome();
		Group lGroup = lGroupHome.getGroup(lGroupID);
		lGroup.set(GroupHome.KEY_STATE, new Long(3));
		lGroup.update(true);
		
		lIndexer.addQuestionToIndex(lKey);
		assertEquals("question indexed", 1, IndexHouseKeeper.countIndexedContents());
	}

	@Test
	public void testAddQuestionToIndex2() throws Exception {
		//we create the context: member as author of a question in a discussion group
		//this time, however, the group is of type 'private'
		GroupHome lGroupHome = data.getGroupHome();		
		String lGroupID = lGroupHome.createNew("TestGroup1", "Group Nr. 1", "1", "3", "10", true).toString();
		Long lQuestionID = data.createQuestion("Index Test", "1.2.3", new Long(lGroupID), true);
		String lMemberID = data.createMember("1");
		data.createQuestionProducer(lQuestionID, new Long(lMemberID), true);
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHome.KEY_ID, lQuestionID);
		
		VIFContentIndexer lIndexer = new VIFContentIndexer();
		lIndexer.addQuestionToIndex(lKey);
		assertEquals("group and question still private", 0, IndexHouseKeeper.countIndexedContents());

		//make question public
		QuestionHome lHome = data.getQuestionHome();
		Question lQuestion = lHome.getQuestion(lQuestionID);
		lQuestion.set(QuestionHome.KEY_STATE, new Long(4));
		lQuestion.update(true);

		lIndexer.addQuestionToIndex(lKey);
		assertEquals("group still private", 0, IndexHouseKeeper.countIndexedContents());
		
		//make group active
		Group lGroup = lGroupHome.getGroup(lGroupID);
		lGroup.set(GroupHome.KEY_STATE, new Long(3));
		lGroup.update(true);
		
		lIndexer.addQuestionToIndex(lKey);
		assertEquals("still nothing to be indexed", 0, IndexHouseKeeper.countIndexedContents());
	}
	
	@Test
	public void testAddCompletionToIndex() throws Exception {
		//we create the context: member as author of a question's completion in a discussion group
		Long lGroupID = data.createGroup();
		Long lQuestionID = data.createQuestion("Index Test", "1.2.3", lGroupID, true);
		Long lCompletionID = data.createCompletion("Completion for fulltext searching", lQuestionID);
		String lMemberID = data.createMember("1");
		data.createCompletionProducer(lCompletionID, new Long(lMemberID), true);
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(CompletionHome.KEY_ID, lCompletionID);
		
		VIFContentIndexer lIndexer = new VIFContentIndexer();
		lIndexer.addCompletionToIndex(lKey);
		assertEquals("group, question and completion still private", 0, IndexHouseKeeper.countIndexedContents());
		
		//make question and completion public
		Question lQuestion = data.getQuestionHome().getQuestion(lQuestionID);
		lQuestion.set(QuestionHome.KEY_STATE, new Long(4));
		lQuestion.update(true);
		Completion lCompletion = data.getCompletionHome().getCompletion(lCompletionID);
		lCompletion.set(CompletionHome.KEY_STATE, new Long(4));
		lCompletion.update(true);		

		lIndexer.addCompletionToIndex(lKey);
		assertEquals("group still private", 0, IndexHouseKeeper.countIndexedContents());		
		
		//make group active
		GroupHome lGroupHome = data.getGroupHome();
		Group lGroup = lGroupHome.getGroup(lGroupID);
		lGroup.set(GroupHome.KEY_STATE, new Long(3));
		lGroup.update(true);
		
		lIndexer.addCompletionToIndex(lKey);
		assertEquals("completion indexed", 1, IndexHouseKeeper.countIndexedContents());
	}
	
	@Test
	public void testAddCompletionToIndex2() throws Exception {
		//we create the context: member as author of a question's completion in a discussion group
		//this time, however, the group is of type 'private'
		GroupHome lGroupHome = data.getGroupHome();		
		String lGroupID = lGroupHome.createNew("TestGroup1", "Group Nr. 1", "1", "3", "10", true).toString();
		Long lQuestionID = data.createQuestion("Index Test", "1.2.3", new Long(lGroupID), true);
		Long lCompletionID = data.createCompletion("Completion for fulltext searching", lQuestionID);
		String lMemberID = data.createMember("1");
		data.createCompletionProducer(lCompletionID, new Long(lMemberID), true);
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(CompletionHome.KEY_ID, lCompletionID);
		
		VIFContentIndexer lIndexer = new VIFContentIndexer();
		lIndexer.addCompletionToIndex(lKey);
		assertEquals("group, question and completion still private", 0, IndexHouseKeeper.countIndexedContents());
		
		//make question and completion public
		Question lQuestion = data.getQuestionHome().getQuestion(lQuestionID);
		lQuestion.set(QuestionHome.KEY_STATE, new Long(4));
		lQuestion.update(true);
		Completion lCompletion = data.getCompletionHome().getCompletion(lCompletionID);
		lCompletion.set(CompletionHome.KEY_STATE, new Long(4));
		lCompletion.update(true);		

		lIndexer.addCompletionToIndex(lKey);
		assertEquals("group still private", 0, IndexHouseKeeper.countIndexedContents());		
		
		//make group active
		lGroupHome = data.getGroupHome();
		Group lGroup = lGroupHome.getGroup(lGroupID);
		lGroup.set(GroupHome.KEY_STATE, new Long(3));
		lGroup.update(true);
		
		lIndexer.addCompletionToIndex(lKey);
		assertEquals("still nothing to be indexed", 0, IndexHouseKeeper.countIndexedContents());
	}

	@Test
	public void testAddGroupContentToIndex() throws Exception {
		//we create the context: member as author of a question with completion in a discussion group
		//group is public
		GroupHome lGroupHome = data.getGroupHome();		
		String lGroupID = lGroupHome.createNew("TestGroup1", "Group Nr. 1", "1", "3", "10", false).toString();
		Long lQuestionID = data.createQuestion("Index Test", "1.2.3", new Long(lGroupID), true);
		Long lCompletionID = data.createCompletion("Completion for fulltext searching", lQuestionID);
		Long lMemberID = new Long(data.createMember("one"));
		Member lAuthor = data.getMemberHome().getMember(lMemberID);
		data.createQuestionProducer(lQuestionID, lMemberID, true);
		data.createCompletionProducer(lCompletionID, lMemberID, true);
		
		VIFContentIndexer lIndexer = new VIFContentIndexer();
		lIndexer.addGroupContentToIndex(lGroupID);
		assertEquals("group, question and completion still private", 0, IndexHouseKeeper.countIndexedContents());		
		
		//make question and completion public
		Question lQuestion = data.getQuestionHome().getQuestion(lQuestionID);
		lQuestion.set(QuestionHome.KEY_STATE, new Long(4));
		lQuestion.update(true);
		Completion lCompletion = data.getCompletionHome().getCompletion(lCompletionID);
		lCompletion.set(CompletionHome.KEY_STATE, new Long(4));
		lCompletion.update(true);
		
		lIndexer.addGroupContentToIndex(lGroupID);
		assertEquals("group still private", 0, IndexHouseKeeper.countIndexedContents());	
		
		//make group active
		lGroupHome = data.getGroupHome();
		Group lGroup = lGroupHome.getGroup(lGroupID);
		lGroup.set(GroupHome.KEY_STATE, new Long(3));
		lGroup.update(true);
		
		lIndexer.addGroupContentToIndex(lGroupID);
		assertEquals("indexed two more document", 2, IndexHouseKeeper.countIndexedContents());

		//search for different fields
		IndexReader lReader = IndexReader.open(IndexHouseKeeper.getContentsIndexDir());
		try {
			Document[] lHits = IndexHouseKeeper.search(createQuery(lAuthor.get(MemberHome.KEY_NAME).toString(), AbstractSearching.IndexField.AUTHOR_NAME.fieldName), lReader);
			assertEquals("found document with author name", 2, lHits.length);
			lHits = IndexHouseKeeper.search(createQuery(lAuthor.get(MemberHome.KEY_FIRSTNAME).toString(), AbstractSearching.IndexField.AUTHOR_NAME.fieldName), lReader);
			assertEquals("found document with author first name", 2, lHits.length);
			lHits = IndexHouseKeeper.search(createQuery("index", AbstractSearching.IndexField.QUESTION_TEXT.fieldName), lReader);
			assertEquals("found document with question text", 1, lHits.length);
			lHits = IndexHouseKeeper.search(createQuery("index", AbstractSearching.IndexField.CONTENT_FULL.fieldName), lReader);
			assertEquals("found document with question text in fulltext search", 1, lHits.length);
			lHits = IndexHouseKeeper.search(createQuery("fulltext", AbstractSearching.IndexField.CONTENT_FULL.fieldName), lReader);
			assertEquals("found document with contribution text", 1, lHits.length);
		}
		finally {
			lReader.close();
		}
		
		//now the reverse way: removing of content
		lIndexer.removeGroupContent(lGroupID);
		assertEquals("index empty again", 0, IndexHouseKeeper.countIndexedContents());
	}

	@Test
	public void testDeleteQuestionInIndex() throws Exception {
		//we create the context: member as author of a question in a discussion group
		Long lGroupID = data.createGroup();
		Long lQuestionID = data.createQuestion("Index Test", "1.2.3", lGroupID, true);
		String lMemberID = data.createMember("1");
		data.createQuestionProducer(lQuestionID, new Long(lMemberID), true);
		
		int lInitial = IndexHouseKeeper.countIndexedContents();
		
		//make group active and question public
		QuestionHome lHome = data.getQuestionHome();
		Question lQuestion = lHome.getQuestion(lQuestionID);
		lQuestion.set(QuestionHome.KEY_STATE, new Long(4));
		lQuestion.update(true);
		
		GroupHome lGroupHome = data.getGroupHome();
		Group lGroup = lGroupHome.getGroup(lGroupID);
		lGroup.set(GroupHome.KEY_STATE, new Long(3));
		lGroup.update(true);
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHome.KEY_ID, lQuestionID);
		VIFContentIndexer lIndexer = new VIFContentIndexer();
		lIndexer.addQuestionToIndex(lKey);
		assertEquals("question indexed", lInitial + 1, IndexHouseKeeper.countIndexedContents());
		
		lIndexer.deleteQuestionInIndex(lQuestionID.toString());
		assertEquals("question deleted from index", lInitial, IndexHouseKeeper.countIndexedContents());
	}
	
	@Test
	public void testAddText() throws Exception {
		//we create the context:
		Long lTextID = data.createText("The Text", "Foo, Jane");
		
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(TextHome.KEY_ID, new Long(lTextID));
		
		VIFContentIndexer lIndexer = new VIFContentIndexer();
		lIndexer.addTextToIndex(lKey);
		assertEquals("text still private", 0, IndexHouseKeeper.countIndexedContents());
		
		//make text public
		TextHome lHome = BOMHelper.getTextHome();
		Text lText = lHome.getText(lTextID, 0);
		lText.set(TextHome.KEY_STATE, new Long(4));
		lText.update(true);
		
		lIndexer.addTextToIndex(lKey);
		assertEquals("text published", 1, IndexHouseKeeper.countIndexedContents());
		
		//make a delete
		lIndexer.deleteTextInIndex(lTextID.toString());
		assertEquals("text deleted from index", 0, IndexHouseKeeper.countIndexedContents());
	}
	
	private Query createQuery(String inQuery, String inField) throws ParseException {
		QueryParser lParser = new QueryParser(IndexHouseKeeper.LUCENE_VERSION, inField, analyzer);
		return lParser.parse(inQuery);
	}

}