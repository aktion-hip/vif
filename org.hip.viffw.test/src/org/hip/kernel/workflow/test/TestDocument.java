package org.hip.kernel.workflow.test;

/*
	This class is part of the workflow framework of the application VIF.
	Copyright (C) 2003, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

import org.hip.kernel.workflow.State;
import org.hip.kernel.workflow.Workflow;
import org.hip.kernel.workflow.WorkflowAware;
import org.hip.kernel.workflow.WorkflowAwareImpl;
import org.hip.kernel.workflow.WorkflowException;

/**
 * WorkflowAware document for testing purposes.
 * 
 * Created on 04.07.2003
 * @author Benno Luthiger
 * (inspired by itools by Juan David Ibáñez Palomar <jdavid@itaapy.com>)
 */
public class TestDocument implements WorkflowAware {
	//constants
	public final static String STATE_PRIVATE = 		"Private";
	public final static String STATE_PENDING = 		"Pending";
	public final static String STATE_PUBLIC = 		"Public";
	public final static String TRANS_REQUEST = 		"Request";
	public final static String TRANS_ACCEPT = 		"Accept";
	public final static String TRANS_REJECT = 		"Reject";
	
	//instance variables
	WorkflowAwareImpl workflowAware;
	private int counterEnters;
	private int counterLeaves;
	private int counterTransitions;	

	/**
	 * TestDocument default constructor.
	 * 
	 */
	public TestDocument() throws WorkflowException {
		super();
		workflowAware = new WorkflowAwareImpl(createWorkflow(), new Object[] {"Just created."}, this);
		counterEnters = 0;
		counterLeaves = 0;
		counterTransitions = 0;
	}

	public void doTransition(String inTransitionName, Object[] inArgs) throws WorkflowException {
		workflowAware.doTransition(inTransitionName, inArgs, this);
	}

	public void enterWorkflow(Workflow inWorkflow, Object[] inArgs) throws WorkflowException {
		workflowAware.enterWorkflow(inWorkflow, inArgs, this);
	}

	public void enterWorkflow(Workflow inWorkflow, String inInitialStateName, Object[] inArgs) throws WorkflowException {
		workflowAware.enterWorkflow(inWorkflow, inInitialStateName, inArgs, this);
	}

	public String getStateName() {
		return workflowAware.getStateName();
	}

	public State getState() throws WorkflowException {
		return workflowAware.getState();
	}

	private Workflow createWorkflow() throws WorkflowException {
		Workflow lWorkflow = new Workflow();
		lWorkflow.addState(STATE_PRIVATE);
		lWorkflow.addState(STATE_PENDING);
		lWorkflow.addState(STATE_PUBLIC);
		lWorkflow.addTransition(TRANS_REQUEST, STATE_PRIVATE, STATE_PENDING);
		lWorkflow.addTransition(TRANS_REJECT, STATE_PENDING, STATE_PRIVATE);
		lWorkflow.addTransition(TRANS_ACCEPT, STATE_PENDING, STATE_PUBLIC);
		lWorkflow.setInitialStateName(STATE_PRIVATE);
		return lWorkflow;
	}
	
	public void onEnter_Private(String inMessage) {
		System.out.println("onEnter_Private: " + inMessage);
		counterEnters++;
	}
	
	public void onLeave_Private(String inMessage) {
		System.out.println("onLeave_Private: " + inMessage);
		counterLeaves++;
	}
	
	public void onEnter_Public(String inMessage) {
		System.out.println("onEnter_Public: " + inMessage);
		counterEnters++;
	}
	
	public void onLeave_Public(String inMessage) {
		System.out.println("onLeave_Public: " + inMessage);
		counterLeaves++;
	}
	
	public void onEnter_Pending(String inMessage) {
		System.out.println("onEnter_Pending: " + inMessage);
		counterEnters++;
	}
	
	public void onLeave_Pending(String inMessage) {
		System.out.println("onLeave_Pending: " + inMessage);
		counterLeaves++;
	}
	
	public void onTransition_Request(String inMessage) {
		System.out.println("onTransition_Request: " + inMessage);
		counterTransitions++;
	}
	
	public void onTransition_Reject(String inMessage) {
		System.out.println("onTransition_Reject: " + inMessage);
		counterTransitions++;
	}
	
	public void onTransition_Accept(String inMessage) {
		System.out.println("onTransition_Accept: " + inMessage);
		counterTransitions++;
	}
	
	public int getNumberOfEnters() {
		return counterEnters;
	}
	
	public int getNumberOfLeaves() {
		return counterLeaves;
	}
	
	public int getNumberOfTransitions() {
		return counterTransitions;
	}
	
	/**
	 * Imagine this document stored, e.g. in a database or in the filesystem.
	 * If you want to go on working with this document, you have to create
	 * an instance with a workflow in the state stored.
	 * This method is to test such a case.
	 * 
	 * @param inInitialStateName
	 * @throws WorkflowException
	 */
	public void reBind(String inInitialStateName) throws WorkflowException {
		workflowAware.enterWorkflow(createWorkflow(), inInitialStateName, new Object[] {"Made rebind."}, this);		
	}
}
