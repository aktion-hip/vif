package org.hip.vif.admin.groupadmin.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.bom.impl.TextQuestionHome;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.util.GroupStateChangeParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 07.12.2011
 */
public class BiblioDeleteHelperTest {
	private static DataHouseKeeper data;
	
	private static final Object[] ACTOR_ID = new Object[] {33l};
	
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
	public final void testDo() throws Exception {
		//we create two members to use them as group administrator
		String[] lMemberIDs = data.create2Members();
		//we create two discussion groups
		Long[] lGroupIDs = data.create2Groups();
		prepareGroup(lGroupIDs[0], lMemberIDs[0]);
		prepareGroup(lGroupIDs[1], lMemberIDs[1]);
		//we create three questions
		Long lQuestionID1 = data.createQuestion("question 1", "1:1");
		Long lQuestionID2 = data.createQuestion("question 2", "1:2");
		Long lQuestionID3 = data.createQuestion("question 3", "2:3");
		prepareQuestion(lQuestionID1, lGroupIDs[0]);
		prepareQuestion(lQuestionID2, lGroupIDs[1]);
		prepareQuestion(lQuestionID3, lGroupIDs[0]);
		//we create three text entries
		Long lTextID1 = data.createText("deletable", "one, joe");
		Long lTextID2 = data.createText("undeletable", "second, peter");
		Long lTextID3 = data.createText("referenced", "third, martha");
		createTextReference(lQuestionID1, lTextID2);
		createTextReference(lQuestionID2, lTextID2);
		createTextReference(lQuestionID3, lTextID3);
		
		Collection<Long> lTextIDs = new Vector<Long>();
		Collections.addAll(lTextIDs, new Long(lTextID1), new Long(lTextID2), new Long(lTextID3));
		BiblioDeleteHelper lHelper = new BiblioDeleteHelper(lTextIDs, new Long(lMemberIDs[0]));
		Collection<Long> lDeletable = lHelper.getDeletableTexts();
		Collection<Long> lReferenced = lHelper.getReferencedTexts();
		Collection<Long> lUndeletable = lHelper.getUndeletableTexts();
		assertEquals(1, lDeletable.size());
		assertEquals(1, lReferenced.size());
		assertEquals(1, lUndeletable.size());
		assertTrue(lDeletable.contains(new Long(lTextID1)));
		assertTrue(lUndeletable.contains(new Long(lTextID2)));
		assertTrue(lReferenced.contains(new Long(lTextID3)));
	}

	private void createTextReference(Long inQuestionID, Long inTextID) throws VException, SQLException {
		DomainObject lTextQuestion = BOMHelper.getTextQuestionHome().create();
		lTextQuestion.set(TextQuestionHome.KEY_TEXTID, inTextID);
		lTextQuestion.set(TextQuestionHome.KEY_QUESTIONID, inQuestionID);
		lTextQuestion.insert(true);		
	}

	/**
	 * Makes question belonging to specified group and make question published.
	 * 
	 * @param inQuestionID
	 * @param inGroupID
	 * @throws VException
	 * @throws SQLException
	 * @throws WorkflowException
	 */
	private void prepareQuestion(Long inQuestionID, Long inGroupID) throws VException, SQLException, WorkflowException {
		QuestionHome lHome = data.getQuestionHome();
		Question lQuestion = lHome.getQuestion(inQuestionID);
		lQuestion.set(QuestionHome.KEY_GROUP_ID, inGroupID);
		lQuestion.update(true);
		((WorkflowAware)lQuestion).doTransition(WorkflowAwareContribution.TRANS_ADMIN_PUBLISH, ACTOR_ID);
	}

	/**
	 * Sets group admin and makes group open.
	 * 
	 * @param inGroupID
	 * @param inMemberID
	 * @throws Exception
	 */
	private void prepareGroup(Long inGroupID, String inMemberID) throws Exception {
		data.createGroupAdmin(inGroupID, new Long(inMemberID));
		GroupHome lHome = data.getGroupHome();
		Group lGroup = lHome.getGroup(inGroupID);
		((WorkflowAware)lGroup).doTransition(VIFGroupWorkflow.TRANS_OPEN, new Object[] {new GroupStateChangeParameters()});
	}
	
	

}
