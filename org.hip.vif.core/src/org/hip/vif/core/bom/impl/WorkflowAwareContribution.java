/*
	This package is part of the application VIF.
	Copyright (C) 2003, Benno Luthiger

	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package org.hip.vif.core.bom.impl;

import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.GettingException;
import org.hip.kernel.exc.DefaultExceptionHandler;
import org.hip.kernel.workflow.State;
import org.hip.kernel.workflow.Workflow;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.kernel.workflow.WorkflowAwareImpl;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.bom.VIFWorkflowAware;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.interfaces.IReviewable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides the workflow functionality for a contribution.
 * 
 * Created on 08.07.2003
 * @author Luthiger
 */
@SuppressWarnings("serial")
public abstract class WorkflowAwareContribution extends HistorizableDomainObject implements WorkflowAware, VIFWorkflowAware, IReviewable {
	private static final Logger LOG = LoggerFactory.getLogger(WorkflowAwareContribution.class);
	
	//constants
	public final static int S_PRIVATE = 			1;
	public final static int S_WAITING_FOR_REVIEW = 	2;
	public final static int S_UNDER_REVISION = 		3;
	public final static int S_OPEN = 				4;
	public final static int S_ANSWERED_REQUESTED = 	5;
	public final static int S_ANSWERED = 			6;
	public final static int S_REOPEN_REQUESTED =	7;
	public final static int S_DELETED = 			8;

	public final static String STATE_PRIVATE 				= String.valueOf(S_PRIVATE);
	public final static String STATE_WAITING_FOR_REVIEW		= String.valueOf(S_WAITING_FOR_REVIEW);
	public final static String STATE_UNDER_REVISION 		= String.valueOf(S_UNDER_REVISION);
	public final static String STATE_OPEN 					= String.valueOf(S_OPEN);
	private final static String STATE_ANSWERED_REQUESTED	= String.valueOf(S_ANSWERED_REQUESTED);
	public final static String STATE_ANSWERED 				= String.valueOf(S_ANSWERED);
	private final static String STATE_REOPEN_REQUESTED 		= String.valueOf(S_REOPEN_REQUESTED);
	private final static String STATE_DELETED 				= String.valueOf(S_DELETED);

	public final static String TRANS_REQUEST = 				"RequestReview";
	public final static String TRANS_ACCEPT = 				"AcceptReview";
	public final static String TRANS_PUBLISH = 				"Publish";
	public final static String TRANS_ADMIN_PUBLISH = 		"AdminPublish";
	public final static String TRANS_REJECT_REVIEW = 		"RejectReview";
	public final static String TRANS_GIVE_BACK_REVIEW = 	"GiveBackReview";
	public final static String TRANS_ADMIN_RESET = 			"AdminReset";
	public final static String TRANS_REQUEST_ANSWERED = 	"RequestAnswered";
	public final static String TRANS_ADMIN_SET_ANSWERED1 = 	"AdminSetAnswered1";
	public final static String TRANS_ADMIN_SET_ANSWERED2 = 	"AdminSetAnswered2";
	public final static String TRANS_ADMIN_REJECT = 		"AdminRejectAnswered";
	public final static String TRANS_REQUEST_REOPEN = 		"RequestReopen";
	public final static String TRANS_ADMIN_REOPEN1 = 		"AdminReopen1";
	public final static String TRANS_ADMIN_REOPEN2 = 		"AdminReopen2";
	public final static String TRANS_ADMIN_REJECT_REOPEN = 	"AdminRejectReopen";	
	public final static String TRANS_DELETE = 				"Delete";
	public final static String TRANS_ADMIN_DELETE1 = 		"AdminDelete1";
	public final static String TRANS_ADMIN_DELETE2 = 		"AdminDelete2";
	
	public final static Integer[] STATES_PUBLISHED 	 = {S_OPEN, S_ANSWERED, S_ANSWERED_REQUESTED};
	public final static Integer[] STATES_UNPUBLISHED = {S_PRIVATE, S_WAITING_FOR_REVIEW, S_UNDER_REVISION};
	public final static Integer[] STATES_ANSWERED	 = {S_ANSWERED, S_DELETED, S_REOPEN_REQUESTED};
	
	//instance variables
	WorkflowAwareImpl workflowAware;

	/**
	 * WorkflowAwareContribution default constructor.
	 * 
	 */
	public WorkflowAwareContribution() throws WorkflowException {
		super();
		workflowAware = new WorkflowAwareImpl(createWorkflow(STATE_PRIVATE), new Object[] {}, this);
	}

	private Workflow createWorkflow(String inInitialStateName) throws WorkflowException {
		Workflow lWorkflow = new Workflow();
		lWorkflow.addState(STATE_PRIVATE);
		lWorkflow.addState(STATE_WAITING_FOR_REVIEW);
		lWorkflow.addState(STATE_UNDER_REVISION);
		lWorkflow.addState(STATE_OPEN);
		lWorkflow.addState(STATE_ANSWERED_REQUESTED);
		lWorkflow.addState(STATE_ANSWERED);
		lWorkflow.addState(STATE_REOPEN_REQUESTED);
		lWorkflow.addState(STATE_DELETED);

		lWorkflow.addTransition(TRANS_REQUEST, STATE_PRIVATE, STATE_WAITING_FOR_REVIEW);
		lWorkflow.addTransition(TRANS_ACCEPT, STATE_WAITING_FOR_REVIEW, STATE_UNDER_REVISION);
		lWorkflow.addTransition(TRANS_PUBLISH, STATE_UNDER_REVISION, STATE_OPEN);
		lWorkflow.addTransition(TRANS_ADMIN_PUBLISH, STATE_PRIVATE, STATE_OPEN);
		lWorkflow.addTransition(TRANS_REJECT_REVIEW, STATE_WAITING_FOR_REVIEW, STATE_PRIVATE);
		lWorkflow.addTransition(TRANS_GIVE_BACK_REVIEW, STATE_UNDER_REVISION, STATE_PRIVATE);
		lWorkflow.addTransition(TRANS_ADMIN_RESET, STATE_OPEN, STATE_PRIVATE);
		lWorkflow.addTransition(TRANS_REQUEST_ANSWERED, STATE_OPEN, STATE_ANSWERED_REQUESTED);
		lWorkflow.addTransition(TRANS_ADMIN_SET_ANSWERED1, STATE_ANSWERED_REQUESTED, STATE_ANSWERED);
		lWorkflow.addTransition(TRANS_ADMIN_SET_ANSWERED2, STATE_OPEN, STATE_ANSWERED);
		lWorkflow.addTransition(TRANS_ADMIN_REJECT, STATE_ANSWERED_REQUESTED, STATE_OPEN);
		lWorkflow.addTransition(TRANS_REQUEST_REOPEN, STATE_ANSWERED, STATE_REOPEN_REQUESTED);
		lWorkflow.addTransition(TRANS_ADMIN_REOPEN1, STATE_ANSWERED, STATE_OPEN);
		lWorkflow.addTransition(TRANS_ADMIN_REOPEN2, STATE_REOPEN_REQUESTED, STATE_OPEN);
		lWorkflow.addTransition(TRANS_ADMIN_REJECT_REOPEN, STATE_REOPEN_REQUESTED, STATE_ANSWERED);
		lWorkflow.addTransition(TRANS_DELETE, STATE_PRIVATE, STATE_DELETED);
		lWorkflow.addTransition(TRANS_ADMIN_DELETE1, STATE_ANSWERED, STATE_DELETED);
		lWorkflow.addTransition(TRANS_ADMIN_DELETE2, STATE_OPEN, STATE_DELETED);
		
		lWorkflow.setInitialStateName(inInitialStateName);
		return lWorkflow;
	}

	public void enterWorkflow(Workflow inWorkflow, Object[] inArgs) throws WorkflowException {
		workflowAware.enterWorkflow(inWorkflow, inArgs, this);
	}

	public void enterWorkflow(Workflow inWorkflow, String inInitialStateName, Object[] inArgs) throws WorkflowException {
		workflowAware.enterWorkflow(inWorkflow, inInitialStateName, inArgs, this);
	}

	public void doTransition(String inTransitionName, Object[] inArgs) throws WorkflowException {
		workflowAware.doTransition(inTransitionName, inArgs, this);
	}

	public String getStateName() {
		return workflowAware.getStateName();
	}

	public State getState() throws WorkflowException {
		return workflowAware.getState();
	}

	/**
	 * Use the afterLoad hook to rebind the reinitialized object to the
	 * Workflow, i.e. to set the stored state to the workflow.
	 */
	protected void afterLoad() {
		try {
			workflowAware.enterWorkflow(createWorkflow(getActualStateValue()), new Object[] {}, this);		
		}
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
		}	
	}
	
	//The following methods define the interface of the workflow.
	
	/**
	 * STATE_PRIVATE
	 */
	public void onEnter_1(Long inAuthorID) {
		LOG.trace("onEnter_1(): STATE_PRIVATE");
	}
	
	/**
	 * STATE_PRIVATE
	 */
	public void onLeave_1(Long inAuthorID) {
		LOG.trace("onLeave_1(): STATE_PRIVATE");
	}
	
	/**
	 * STATE_WAITING_FOR_REVIEW
	 */
	public void onEnter_2(Long inAuthorID) {
		LOG.trace("onEnter_2(): STATE_WAITING_FOR_REVIEW");
	}
	
	/**
	 * STATE_WAITING_FOR_REVIEW
	 */
	public void onLeave_2(Long inAuthorID) {
		LOG.trace("onLeave_2(): STATE_WAITING_FOR_REVIEW");
	}
	
	/**
	 * STATE_UNDER_REVISION
	 */
	public void onEnter_3(Long inAuthorID) {
		LOG.trace("onEnter_3(): STATE_UNDER_REVISION");
	}
	
	/**
	 * STATE_UNDER_REVISION
	 */
	public void onLeave_3(Long inAuthorID) {
		LOG.trace("onLeave_3(): STATE_UNDER_REVISION");
	}
	
	/**
	 * STATE_OPEN
	 * @throws WorkflowException 
	 */
	public void onEnter_4(Long inAuthorID) throws WorkflowException {
		LOG.trace("onEnter_4(): STATE_OPEN");
	}
	
	/**
	 * STATE_OPEN
	 */
	public void onLeave_4(Long inAuthorID) {
		LOG.trace("onLeave_4(): STATE_OPEN");
	}
	
	/**
	 * STATE_ANSWERED_REQUESTED
	 */
	public void onEnter_5(Long inAuthorID) {
		LOG.trace("onEnter_5(): STATE_ANSWERED_REQUESTED");
	}
	
	/**
	 * STATE_ANSWERED_REQUESTED
	 */
	public void onLeave_5(Long inAuthorID) {
		LOG.trace("onLeave_5(): STATE_ANSWERED_REQUESTED");
	}
	
	/**
	 * STATE_ANSWERED
	 */
	public void onEnter_6(Long inAuthorID) {
		LOG.trace("onEnter_6(): STATE_ANSWERED");
	}
	
	/**
	 * STATE_ANSWERED
	 */
	public void onLeave_6(Long inAuthorID) {
		LOG.trace("onLeave_6(): STATE_ANSWERED");
	}
	
	/**
	 * STATE_REOPEN_REQUESTED
	 */
	public void onEnter_7(Long inAuthorID) {
		LOG.trace("onEnter_7(): STATE_REOPEN_REQUESTED");
	}
	
	/**
	 * STATE_REOPEN_REQUESTED
	 */
	public void onLeave_7(Long inAuthorID) {
		LOG.trace("onLeave_7(): STATE_REOPEN_REQUESTED");
	}
	
	/**
	 * STATE_DELETED
	 * @throws WorkflowException 
	 */
	public void onEnter_8(Long inAuthorID) throws WorkflowException {
		LOG.trace("onEnter_8(): STATE_DELETED");
		removeFromIndex();
	}
	
	/**
	 * STATE_DELETED
	 */
	public void onLeave_8(Long inAuthorID) {
		LOG.trace("onLeave_8(): STATE_DELETED");
	}

	public void onTransition_RequestReview(Long inAuthorID) {
		LOG.trace("onTransition_RequestReview(): TRANS_REQUEST");
		updateState(S_WAITING_FOR_REVIEW, inAuthorID);
	}

	public void onTransition_AcceptReview(Long inReviewerID) {
		LOG.trace("onTransition_AcceptReview(): TRANS_ACCEPT");
		updateState(S_UNDER_REVISION, inReviewerID);
	}

	public void onTransition_Publish(Long inReviewerID) throws WorkflowException {
		LOG.trace("onTransition_Publish(): TRANS_PUBLISH");
		updateState(S_OPEN, inReviewerID);
		addToIndex();
	}

	public void onTransition_RejectReview(Long inReviewerID) {
		LOG.trace("onTransition_RejectReview(): TRANS_REJECT_REVIEW");
		updateState(S_PRIVATE, inReviewerID);
	}

	public void onTransition_GiveBackReview(Long inReviewerID) {
		LOG.trace("onTransition_GiveBackReview(): TRANS_GIVE_BACK_REVIEW");
		updateState(S_PRIVATE, inReviewerID);
	}

	public void onTransition_Delete(Long inAuthorID) {
		LOG.trace("onTransition_Delete(): TRANS_DELETE");
		updateState(S_DELETED, inAuthorID);
	}

	public void onTransition_AdminPublish(Long inAuthorID) throws WorkflowException {
		LOG.trace("onTransition_AdminPublish(): TRANS_ADMIN_PUBLISH");
		updateState(S_OPEN, inAuthorID);
		addToIndex();
	}

	public void onTransition_RequestAnswered(Long inAuthorID) {
		LOG.trace("onTransition_RequestAnswered(): TRANS_REQUEST_ANSWERED");
		updateState(S_ANSWERED_REQUESTED, inAuthorID);
	}

	/**
	 * Transition from AnsweredRequested to Answered.
	 * @param inAuthorID
	 */
	public void onTransition_AdminSetAnswered1(Long inAuthorID) {
		LOG.trace("onTransition_AdminSetAnswered1(): TRANS_ADMIN_SET_ANSWERED1");
		updateState(S_ANSWERED, inAuthorID);
	}

	/**
	 * Transition from Open to Answered (short cut for admins).
	 * @param inAuthorID
	 */
	public void onTransition_AdminSetAnswered2(Long inAuthorID) {
		LOG.trace("onTransition_AdminSetAnswered2(): TRANS_ADMIN_SET_ANSWERED2");
		updateState(S_ANSWERED, inAuthorID);
	}

	public void onTransition_AdminRejectAnswered(Long inAuthorID) {
		LOG.trace("onTransition_AdminRejectAnswered(): TRANS_ADMIN_REJECT");
		updateState(S_OPEN, inAuthorID);
	}
	
	public void onTransition_RequestReopen(Long inAuthorID) {
		LOG.trace("onTransition_RequestReopen(): TRANS_REQUEST_REOPEN");
		updateState(S_REOPEN_REQUESTED, inAuthorID);
	}
	
	public void onTransition_AdminRejectReopen(Long inAuthorID) {
		LOG.trace("onTransition_AdminRejectReopen(): TRANS_ADMIN_REJECT_REOPEN");
		updateState(S_ANSWERED, inAuthorID);
	}

	/**
	 * Transition from Answered to Open (short cut for admins).
	 * @param inAuthorID
	 */
	public void onTransition_AdminReopen1(Long inAuthorID) {
		LOG.trace("onTransition_AdminReopen1(): TRANS_ADMIN_REOPEN1");
		updateState(S_OPEN, inAuthorID);
	}

	/**
	 * Transition from ReopenRequested to Open.
	 * @param inAuthorID
	 */
	public void onTransition_AdminReopen2(Long inAuthorID) {
		LOG.trace("onTransition_AdminReopen2(): TRANS_ADMIN_REOPEN2");
		updateState(S_OPEN, inAuthorID);
	}

	/**
	 * Transition from Answered to Deleted.
	 * @param inAuthorID
	 */
	public void onTransition_AdminDelete1(Long inAuthorID) {
		LOG.trace("onTransition_AdminDelete1(): TRANS_ADMIN_DELETE1");
		updateState(S_DELETED, inAuthorID);
	}

	/**
	 * Transition from Open to Deleted.
	 * @param inAuthorID
	 */
	public void onTransition_AdminDelete2(Long inAuthorID) {
		LOG.trace("onTransition_AdminDelete2(): TRANS_ADMIN_DELETE2");
		updateState(S_DELETED, inAuthorID);
	}

	public void onTransition_AdminReset(Long inAuthorID) throws WorkflowException {
		LOG.trace("onTransition_AdminReset(): TRANS_ADMIN_RESET");
		updateState(S_PRIVATE, inAuthorID);
		removeFromIndex();
	}
	
	//Hooks to be implemented by subclasses
	
	/**
	 * Returns the actual state of this workflow aware domain object.
	 * 
	 * @return java.lang.String
	 */	
	abstract String getActualStateValue() throws GettingException;
	
	/**
	 * Changes the state of this workflow aware domain object to the specified value.
	 * 
	 * @param inNewState int
	 * @param inAuthorID java.lang.Long The user causing the change.
	 * @throws BOMChangeValueException
	 */
	abstract void setState(int inNewState, Long inAuthorID) throws BOMChangeValueException;
	
	/**
	 * Returns true if the state of this object is <code>private</code>
	 * 
	 * @param boolean
	 */
	public boolean isPrivate() {
		return STATE_PRIVATE.equals(getStateName());
	}
	
	/**
	 * Returns true if the state of this object is unpublished.
	 * 
	 * @return boolean
	 */
	public boolean isUnpublished() {
		int lState = Integer.parseInt(getStateName());
		return isUnpublished(lState);
	}
	
	private static boolean isUnpublished(int inState) {
		for (int i = 0; i < STATES_UNPUBLISHED.length; i++) {
			if (inState == STATES_UNPUBLISHED[i])
				return true;
		}
		return false;
	}
	
	/**
	 * Convenience method.
	 * 
	 * @param inState String
	 * @return boolean <code>true</code> if the passed state matches an unpublished state
	 */
	public static boolean isUnpublished(String inState) {
		int lState = Integer.parseInt(inState);
		return isUnpublished(lState);
	}
	
	/**
	 * Returns true if the state of this object is published.
	 * 
	 * @param boolean
	 */
	public boolean isPublished() {
		int lState = Integer.parseInt(getStateName());
		for (int i = 0; i < STATES_PUBLISHED.length; i++) {
			if (lState == STATES_PUBLISHED[i])
				return true;
		}
		return false;
	}
	
	/**
	 * Convenience method: checks whether the specified state corresponds to a published state.
	 * 
	 * @param inState int the state to check
	 * @return boolean <code>true</code> if the specified state corresponds to a published state
	 */
	public static boolean isPublished(int inState) {		
		for (int i = 0; i < STATES_PUBLISHED.length; i++) {
			if (inState == STATES_PUBLISHED[i])
				return true;
		}
		return false;
	}
	
	private void updateState(int inState, Long inAuthorID) {
		try {
			setState(inState, inAuthorID);
			update(true);
		}
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
		}	
	}
	
	/**
	 * Add this contribution to the full text search index.
	 * Hook for subclasses.
	 * 
	 * @throws WorkflowException
	 */
	abstract void addToIndex() throws WorkflowException;
	
	/**
	 * Removes this contribution from the full text search index.
	 * Hook for subclasses.
	 *  
	 * @throws WorkflowException
	 */
	abstract void removeFromIndex() throws WorkflowException;
	
	/**
	 * Convenience method: converts a collection of <code>VIFWorkflowAware</code> to <code>IReviewable</code>.
	 * 
	 * @param inContributions
	 * @return Collection<IReviewable>
	 */
	public static Collection<IReviewable> convertToReviewable(Collection<VIFWorkflowAware> inContributions) {
		Collection<IReviewable> outContributions = new Vector<IReviewable>(inContributions.size());
		for (VIFWorkflowAware lContribution : inContributions) {
			outContributions.add((IReviewable) lContribution);
		}
		return outContributions;
	}
}
