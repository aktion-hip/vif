package org.hip.kernel.workflow;

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

import java.util.HashMap;

/**
 * This class is used to describe a workflow.
 * A workflow has states (one of them is the initial state), 
 * and states have transitions that go to another state.
 * 
 * Created on 03.07.2003
 * @author Benno Luthiger
 * (inspired by itools by Juan David Ibáñez Palomar <jdavid@itaapy.com>)
 */
public class Workflow extends Object {
	private HashMap<String, StateImpl> states;
	private String initialState;

	/**
	 * Inner class implementing the State interface.
	 * 
	 * @see org.hip.kernel.workflow.State
	 */
	private class StateImpl extends Object implements State {
		private HashMap<String, Transition> transitions;

		/**
		 * StateImpl default constructor.
		 */
		public StateImpl() {
			super();
			transitions = new HashMap<String, Transition>();
		}

		/**
		 * @see org.hip.vif.workflow.State#addTransition()
		 */
		public void addTransition(String inTransitionName, Transition inTransition) {
			transitions.put(inTransitionName, inTransition);
		}
	
		/**
		 * @see org.hip.vif.workflow.State#getTransition()
		 */
		public Transition getTransition(String inTransitionName) {
			return transitions.get(inTransitionName);
		}
	}

	/**
	 * Inner class implementing the Transition interface.
	 * 
	 * @see org.hip.kernel.workflow.Transition
	 */
	public class TransitionImpl extends Object implements Transition {
		private String stateFrom;
		private String stateTo;

		/**
		 * TransitionImpl constructor with specified states from and to.
		 * 
		 * @param inStateFrom java.lang.String
		 * @param inStateTo java.lang.String
		 */
		public TransitionImpl(String inStateFrom, String inStateTo) {
			super();
			stateFrom = inStateFrom;
			stateTo = inStateTo;
		}
	
		/**
		 * @see org.hip.vif.workflow.Transition#getStateFrom()
		 */
		public String getStateFrom() {
			return stateFrom;
		}
	
		/**
		 * @see org.hip.vif.workflow.Transition#getStateTo()
		 */
		public String getStateTo() {
			return stateTo;
		}
	}

	/**
	 * Workflow default constructor.
	 */
	public Workflow() {
		super();
		states = new HashMap<String, StateImpl>();
		initialState = null;
	}
	
	/**
	 * Sets the state with the specified name as default initial state.
	 * 
	 * @param inInitialStateName java.lang.String
	 * @throws WorkflowException
	 */
	public void setInitialStateName(String inInitialStateName) throws WorkflowException {
		getState(inInitialStateName);
		initialState = inInitialStateName;
	}

	/**
	 * Returns the name of the initial state.
	 * 
	 * @return java.lang.String
	 */
	public String getInitialStateName() {
		return initialState;
	}
	
	/**
	 * Returns the State with the specified name. 
	 * 
	 * @param inStateName java.lang.String
	 * @return State
	 * @throws WorkflowException
	 */
	public State getState(String inStateName) throws WorkflowException {
		State lState = states.get(inStateName);
		if (lState == null)
			throw new WorkflowException("Unregistered state: " + inStateName);

		return lState;
	}
	
	/**
	 * Adds a new State with the specified name.
	 * 
	 * @param inStateName java.lang.String
	 */
	public void addState(String inStateName) {
		states.put(inStateName, new StateImpl());
	}
	
	/**
	 * Adds a new Transition. 
	 * The specified state names from and to respectively are 
	 * the origin and destination states of the transition. 
	 * 
	 * @param inTransitionName java.lang.String
	 * @param inStateFrom java.lang.String
	 * @param inStateTo java.lang.String
	 * @throws WorkflowException
	 */
	public void addTransition(String inTransitionName, String inStateFrom, String inStateTo) throws WorkflowException {
		getState(inStateTo);
		State lState = getState(inStateFrom);
		lState.addTransition(inTransitionName, new TransitionImpl(inStateFrom, inStateTo));
	}
}
