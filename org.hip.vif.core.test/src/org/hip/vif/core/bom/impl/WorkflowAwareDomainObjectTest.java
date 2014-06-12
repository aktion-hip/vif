package org.hip.vif.core.bom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.DataHouseKeeper;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created on 14.08.2003
 * @author Luthiger
 */
public class WorkflowAwareDomainObjectTest {
	private static DataHouseKeeper data;

	@SuppressWarnings("serial")
	private class CompletionSub extends CompletionImpl {
		public CompletionSub() throws WorkflowException {
			super();
		}
		public void setState(int inNewState, Long inAuthorID) throws BOMChangeValueException {
			super.setState(inNewState, inAuthorID);
		}
	}
	
	@SuppressWarnings("serial")
	private class CompletionHomeSub extends CompletionHomeImpl {
		public CompletionHomeSub() {
			super();
		}
		public DomainObject newInstance() throws BOMException {
			try {
				return (DomainObject)new CompletionSub();
			}
			catch (Exception exc) {
				throw new BOMException(exc.getMessage());
			}	
		}
	}

	@BeforeClass
	public static void init() {
		data = DataHouseKeeper.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		data.deleteAllInAll();
	}

	@Test
	public void testSetState() throws VException, SQLException {
		int lCountCompletions = data.getCompletionHome().getCount();
		int lCountCompletionHistory = data.getCompletionHistoryHome().getCount();
		Long lCompletionID = data.createCompletion("completion 1", 1l, WorkflowAwareContribution.S_PRIVATE);
		assertEquals("number of comletions 1", lCountCompletions+1, data.getCompletionHome().getCount());
		assertEquals("number of comletion history 1", lCountCompletionHistory, data.getCompletionHistoryHome().getCount());
		
		((CompletionSub)retrieveDomainObject(lCompletionID, CompletionHome.KEY_ID, new CompletionHomeSub())).setState(WorkflowAwareContribution.S_WAITING_FOR_REVIEW, new Long(2));
		assertEquals("number of comletions 2", lCountCompletions+1, data.getCompletionHome().getCount());
		assertEquals("number of comletion history 2", lCountCompletionHistory+1, data.getCompletionHistoryHome().getCount());			
	}

	@Test
	public void testIsUnpublished() throws VException, SQLException {
		DomainObjectHome lHome = data.getCompletionHome();
		int lCount = lHome.getCount();
		Long lCompletionID1 = data.createCompletion("completion 1", 1l, WorkflowAwareContribution.S_PRIVATE);
		Long lCompletionID2 = data.createCompletion("completion 2", 1l, WorkflowAwareContribution.S_WAITING_FOR_REVIEW);
		Long lCompletionID3 = data.createCompletion("completion 3", 1l, WorkflowAwareContribution.S_UNDER_REVISION);
		Long lCompletionID4 = data.createCompletion("completion 4", 1l, WorkflowAwareContribution.S_OPEN);
		Long lCompletionID5 = data.createCompletion("completion 5", 1l, WorkflowAwareContribution.S_ANSWERED);
		Long lCompletionID6 = data.createCompletion("completion 6", 1l, WorkflowAwareContribution.S_DELETED);
		assertEquals("number of comletions", lCount+6, lHome.getCount());
		
		assertTrue("unpublished 1", retrieveDomainObject(lCompletionID1, CompletionHome.KEY_ID, lHome).isUnpublished());
		assertTrue("unpublished 2", retrieveDomainObject(lCompletionID2, CompletionHome.KEY_ID, lHome).isUnpublished());
		assertTrue("unpublished 3", retrieveDomainObject(lCompletionID3, CompletionHome.KEY_ID, lHome).isUnpublished());
		assertFalse("unpublished 4", retrieveDomainObject(lCompletionID4, CompletionHome.KEY_ID, lHome).isUnpublished());
		assertFalse("unpublished 5", retrieveDomainObject(lCompletionID5, CompletionHome.KEY_ID, lHome).isUnpublished());
		assertFalse("unpublished 6", retrieveDomainObject(lCompletionID6, CompletionHome.KEY_ID, lHome).isUnpublished());
		
		assertFalse(WorkflowAwareContribution.isUnpublished(WorkflowAwareContribution.STATE_ANSWERED));
		assertFalse(WorkflowAwareContribution.isUnpublished(WorkflowAwareContribution.STATE_OPEN));
		assertTrue(WorkflowAwareContribution.isUnpublished(WorkflowAwareContribution.STATE_PRIVATE));
		assertTrue(WorkflowAwareContribution.isUnpublished(WorkflowAwareContribution.STATE_UNDER_REVISION));
		assertTrue(WorkflowAwareContribution.isUnpublished(WorkflowAwareContribution.STATE_WAITING_FOR_REVIEW));
	}

	@Test
	public void testIsPublished() throws VException, SQLException {
		DomainObjectHome lHome = data.getCompletionHome(); 
		int lCount = lHome.getCount();
		Long lCompletionID1 = data.createCompletion("completion 1", 1l, WorkflowAwareContribution.S_PRIVATE);
		Long lCompletionID2 = data.createCompletion("completion 2", 1l, WorkflowAwareContribution.S_WAITING_FOR_REVIEW);
		Long lCompletionID3 = data.createCompletion("completion 3", 1l, WorkflowAwareContribution.S_UNDER_REVISION);
		Long lCompletionID4 = data.createCompletion("completion 4", 1l, WorkflowAwareContribution.S_OPEN);
		Long lCompletionID5 = data.createCompletion("completion 5", 1l, WorkflowAwareContribution.S_ANSWERED);
		Long lCompletionID6 = data.createCompletion("completion 6", 1l, WorkflowAwareContribution.S_DELETED);
		assertEquals("number of comletions", lCount+6, lHome.getCount());
		
		assertFalse("published 1", retrieveDomainObject(lCompletionID1, CompletionHome.KEY_ID, lHome).isPublished());
		assertFalse("published 2", retrieveDomainObject(lCompletionID2, CompletionHome.KEY_ID, lHome).isPublished());
		assertFalse("published 3", retrieveDomainObject(lCompletionID3, CompletionHome.KEY_ID, lHome).isPublished());
		assertTrue("published 4", retrieveDomainObject(lCompletionID4, CompletionHome.KEY_ID, lHome).isPublished());
		assertTrue("published 5", retrieveDomainObject(lCompletionID5, CompletionHome.KEY_ID, lHome).isPublished());
		assertFalse("published 6", retrieveDomainObject(lCompletionID6, CompletionHome.KEY_ID, lHome).isPublished());
	}
	
	private WorkflowAwareContribution retrieveDomainObject(Long inID, String inColumnName, DomainObjectHome inHome) throws VException, SQLException {
		KeyObject lKeyObject = new KeyObjectImpl();
		lKeyObject.setValue(inColumnName, inID);
		return (WorkflowAwareContribution)inHome.findByKey(lKeyObject);
	}
	
}
