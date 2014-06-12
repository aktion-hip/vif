package org.hip.vif.web.stale;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GeneralDomainObject;
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
 * Created: 21.10.2010
 */
public class StaleRequestHelperTest {
	private static final String NL = System.getProperty("line.separator");
	private static DataHouseKeeper data;

	@BeforeClass
	public static void init() throws Exception {
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
	public void testCollector() throws Exception {
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
		QuestionCollector lQCollector = new QuestionCollectorSub(lResult.next());
		assertEquals("23", lQCollector.getAuthorGroup().getGroupID().toString());
		assertEquals("1:1", lQCollector.getDecimal());
		assertEquals("Test question", lQCollector.getContributionTitle());
		assertEquals("Remark", lQCollector.getRemark());
		assertEquals("NameT2", lQCollector.getReviewerName());
		assertEquals("VornameT2", lQCollector.getReviewerFirstname());
		assertEquals("2.mail@test", lQCollector.getReviewerMail());
		
		lResult = BOMHelper.getJoinCompletionToMemberHome().select(lReviewerKey);
		CompletionCollector lCCollector = new CompletionCollectorSub(lResult.next());
		assertEquals("23", lCCollector.getAuthorGroup().getGroupID().toString());
		assertEquals("Test completion", lCCollector.getContributionTitle());
		assertEquals("1:1", lCCollector.getDecimalID());
		
		lResult = BOMHelper.getJoinTextToMemberHome().select(lReviewerKey);
		TextCollector lTCollector = new TextCollectorSub(lResult.next());
		assertNull(lTCollector.getAuthorGroup().getGroupID());
		String lExpected = "Type: Article"+NL+"Title: Text 1"+NL+"Subtitle: About the subtitle"+NL+"Author: Foo, Jane"+NL+"Year: 2010"+NL+"Pages: 44-55"+NL+"Volume: 12"+NL+"Number: 8"+NL;
		assertEquals(lExpected, lTCollector.getContentPlain());
		lExpected = "<table border=\"0\" style=\"font-size:11pt;\"><tr><td><i>Type</i>:</td><td>Article</td></tr><tr><td><i>Title</i>:</td><td>Text 1</td></tr><tr><td><i>Subtitle</i>:</td><td>About the subtitle</td></tr><tr><td><i>Author</i>:</td><td>Foo, Jane</td></tr><tr><td><i>Year</i>:</td><td>2010</td></tr><tr><td><i>Pages</i>:</td><td>44-55</td></tr><tr><td><i>Volume</i>:</td><td>12</td></tr><tr><td><i>Number</i>:</td><td>8</td></tr></table>";
		assertEquals(lExpected, lTCollector.getContentHtml());
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
	
// ---
	
	private class QuestionCollectorSub extends QuestionCollector {
		protected QuestionCollectorSub(GeneralDomainObject inModel) throws VException {
			super(inModel);
		}		
	}
	private class CompletionCollectorSub extends CompletionCollector {
		protected CompletionCollectorSub(GeneralDomainObject inModel) throws VException {
			super(inModel);
		}
	}
	private class TextCollectorSub extends TextCollector {
		protected TextCollectorSub(GeneralDomainObject inModel) throws VException, SQLException {
			super(inModel);
		}
	}

}