package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.IndexHouseKeeper;
import org.hip.vif.core.bom.ResponsibleHome;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextAuthorReviewerHome;
import org.hip.vif.core.bom.TextHome;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Luthiger
 * Created: 09.10.2010
 */
public class JoinTextToMemberHomeTest {
	private static final long ONE_DAY = 1000*60*60*24;
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
		data.deleteAllFromMember();
		data.deleteAllFromText();
		data.deleteAllFromTextAuthorReviewer();
		data.deleteAllFromLinkMemberRole();
		IndexHouseKeeper.deleteTestIndexDir();
	}
	
	@Test
	public void testObjects() throws Exception {
		JoinTextToMemberHome lHome = new JoinTextToMemberHomeSub();
		Iterator<Object> lTest = lHome.getTestObjects();
		String lExpected = "SELECT tblTextVersion.TEXTID, tblTextVersion.NVERSION, tblTextVersion.STITLE, tblTextVersion.SREFERENCE, tblTextVersion.NSTATE, tblTextAuthorReviewer.MEMBERID, tblTextAuthorReviewer.NTYPE, tblTextAuthorReviewer.DTCREATION, tblMember.SNAME, tblMember.SFIRSTNAME, tblMember.SMAIL FROM tblTextVersion INNER JOIN tblTextAuthorReviewer ON tblTextVersion.TEXTID = tblTextAuthorReviewer.TEXTID AND tblTextVersion.NVERSION = tblTextAuthorReviewer.NVERSION INNER JOIN tblMember ON tblTextAuthorReviewer.MEMBERID = tblMember.MEMBERID WHERE tblTextVersion.TEXTID = 8";
		assertEquals(lExpected, (String)lTest.next());
	}
	
	@Test
	public void testSelectStaleWaitingForReview() throws Exception {
		String[] lMemberIDs = data.create2Members();
		Long lTextID = data.createText("Test text", "Doe, Jane");
		setTextState(lTextID);
		
		long lNow = System.currentTimeMillis();
		
		createTextAuthorReviewerEntry(lTextID, lMemberIDs[0], (lNow-ONE_DAY));
		createTextAuthorReviewerEntry(lTextID, lMemberIDs[1], (lNow-(3*ONE_DAY)));
		
		JoinTextToMemberHome lHome = data.getJoinTextToMemberHome();
		QueryResult lResult = lHome.selectStaleWaitingForReview(new Timestamp(lNow-(2*ONE_DAY)));
		while (lResult.hasMoreElements()) {
			assertEquals(lMemberIDs[1], lResult.next().get(ResponsibleHome.KEY_MEMBER_ID).toString());
		}
	}

	private void createTextAuthorReviewerEntry(Long inTextID, String inMemberID, long inTime) throws VException, SQLException {
		DomainObject lEntry = data.getTextAuthorReviewerHome().create();
		lEntry.set(TextAuthorReviewerHome.KEY_TEXT_ID, inTextID);
		lEntry.set(TextAuthorReviewerHome.KEY_VERSION, 0l);
		lEntry.set(ResponsibleHome.KEY_MEMBER_ID, new Long(inMemberID));
		lEntry.set(ResponsibleHome.KEY_TYPE, ResponsibleHome.Type.REVIEWER.getValue());
		lEntry.set(ResponsibleHome.KEY_CREATED, new Timestamp(inTime));
		lEntry.insert(true);
	}

	private void setTextState(Long inTextID) throws VException, SQLException {
		Text lText = data.getTextHome().getText(inTextID, 0);
		lText.set(TextHome.KEY_STATE, new Long(WorkflowAwareContribution.S_WAITING_FOR_REVIEW));
		lText.update(true);
	}

// ---
	
	@SuppressWarnings("serial")
	private class JoinTextToMemberHomeSub extends JoinTextToMemberHome {
		@Override
		protected Vector<Object> createTestObjects() {
			Vector<Object> out = new Vector<Object>();
			try {
				KeyObject lKey = new KeyObjectImpl();
				lKey.setValue(TextHome.KEY_ID, new Long(8));
				out.add(createSelectString(lKey));
			}
			catch (VException exc) {
				fail(exc.getMessage());
			}
			return out;
		}
	}
	
}
