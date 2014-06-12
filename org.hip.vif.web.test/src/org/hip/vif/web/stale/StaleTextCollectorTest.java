package org.hip.vif.web.stale;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.CompletionAuthorReviewerHome;
import org.hip.vif.core.bom.QuestionAuthorReviewerHome;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.TextAuthorReviewerHome;
import org.hip.vif.web.stale.StaleRequestHelper.CompletionCollector;
import org.hip.vif.web.stale.StaleRequestHelper.QuestionCollector;
import org.hip.vif.web.stale.StaleRequestHelper.TextCollector;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 08.03.2012
 */
public class StaleTextCollectorTest {
	private static final String NL = System.getProperty("line.separator");
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
	public final void testGetNotificationText() throws Exception {
		String[] lMemberIDs = data.create2Members();
		Long lQuestionID = data.createQuestion("Test question", "1:1");
		Long lCompletionID = data.createCompletion("Test completion", lQuestionID);
		Long lTextID = data.createText("Text 1", "Foo, Jane");
		
		long lNow = System.currentTimeMillis();
		createAuthorReviewerEntry(data.getQuestionAuthorReviewerHome().create(), QuestionAuthorReviewerHome.KEY_QUESTION_ID, lQuestionID, lMemberIDs[0], ResponsibleHome.Type.AUTHOR.getValue(), lNow);
		createAuthorReviewerEntry(data.getQuestionAuthorReviewerHome().create(), QuestionAuthorReviewerHome.KEY_QUESTION_ID, lQuestionID, lMemberIDs[1], ResponsibleHome.Type.REVIEWER.getValue(), lNow);
		createAuthorReviewerEntry(data.getCompletionAuthorReviewerHome().create(), CompletionAuthorReviewerHome.KEY_COMPLETION_ID, lCompletionID, lMemberIDs[0], ResponsibleHome.Type.AUTHOR.getValue(), lNow);
		createAuthorReviewerEntry(data.getCompletionAuthorReviewerHome().create(), CompletionAuthorReviewerHome.KEY_COMPLETION_ID, lCompletionID, lMemberIDs[1], ResponsibleHome.Type.REVIEWER.getValue(), lNow);
		createTextAuthorReviewerEntry(lTextID, lMemberIDs[0], ResponsibleHome.Type.AUTHOR.getValue(), lNow);
		createTextAuthorReviewerEntry(lTextID, lMemberIDs[1], ResponsibleHome.Type.REVIEWER.getValue(), lNow);
		
		KeyObject lReviewerKey = new KeyObjectImpl();
		lReviewerKey.setValue(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
		
		QueryResult lResult = BOMHelper.getJoinQuestionToContributorsHome().select(lReviewerKey);
		QuestionCollector lQCollector = new QuestionCollector(lResult.next());
		lResult = BOMHelper.getJoinCompletionToMemberHome().select(lReviewerKey);
		CompletionCollector lCCollector = new CompletionCollector(lResult.next());
		lResult = BOMHelper.getJoinTextToMemberHome().select(lReviewerKey);
		TextCollector lTCollector = new TextCollector(lResult.next());
		
		String lExpected = "Question 1:1: Test question\nRemark: Remark\n\n";
		StaleTextCollector lCollector = new StaleTextCollector();
		lCollector.reset();
		lQCollector.accept(lCollector);
		assertEquals(lExpected, lCollector.getNotificationText().toString());
		
		lExpected = "Completion (1:1): Test completion\n\n";
		lCollector.reset();
		lCCollector.accept(lCollector);
		assertEquals(lExpected, lCollector.getNotificationText().toString());
		
		lExpected = "Bibliography\n\nType: Article" + NL + 
				"Title: Text 1" + NL + 
				"Subtitle: About the subtitle" + NL + 
				"Author: Foo, Jane" + NL + 
				"Year: 2010" + NL + 
				"Pages: 44-55" + NL + 
				"Volume: 12" + NL + 
				"Number: 8" + NL;
		lCollector.reset();
		lTCollector.accept(lCollector);
		assertEquals(lExpected, lCollector.getNotificationText().toString());
	}
	
	private void createAuthorReviewerEntry(DomainObject lEntry, String inFieldName, Long inContributionID, String inMemberID, Integer inType, long inTime) throws VException, SQLException {
		lEntry.set(inFieldName, inContributionID);
		lEntry.set(ResponsibleHome.KEY_MEMBER_ID, new Long(inMemberID));
		lEntry.set(ResponsibleHome.KEY_TYPE, inType);
		lEntry.set(ResponsibleHome.KEY_CREATED, new Timestamp(inTime));
		lEntry.insert(true);
	}

	private void createTextAuthorReviewerEntry(Long inTextID, String inMemberID, Integer inType, long inTime) throws VException, SQLException {
		DomainObject lEntry = data.getTextAuthorReviewerHome().create();
		lEntry.set(TextAuthorReviewerHome.KEY_TEXT_ID, inTextID);
		lEntry.set(TextAuthorReviewerHome.KEY_VERSION, new Long(0));
		lEntry.set(ResponsibleHome.KEY_MEMBER_ID, new Long(inMemberID));
		lEntry.set(ResponsibleHome.KEY_TYPE, inType);
		lEntry.set(ResponsibleHome.KEY_CREATED, new Timestamp(inTime));
		lEntry.insert(true);
	}


}
