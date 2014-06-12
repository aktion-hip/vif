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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class implements generic functionality which can be use by
 * workflow aware objects, i.e. objects implementing the interface WorkflowAware.
 * These objects can delegate calls to WorkflowAware methods to instances
 * of this class. 
 *  
 * Created on 03.07.2003
 * @author Benno Luthiger
 * (inspired by itools by Juan David Ibáñez Palomar <jdavid@itaapy.com>)
 * @see org.hip.kernel.workflow.WorkflowAware
 */
public class WorkflowAwareImpl extends Object {
	//constants
	private final static String METHOD_ON_ENTER 		= "onEnter_";
	private final static String METHOD_ON_LEAVE 		= "onLeave_";
	private final static String METHOD_ON_TRANSITION 	= "onTransition_";
	
	//instance variables
	private Workflow workflow;
	private String actualStateName;

	/**
	 * WorkflowAwareImpl constructor.
	 * 
	 * @param inWorkflow Workflow The relevant Workflow.
	 * @param inArgs java.langObject[] Arguments passed down to all handlers called.
	 * @param inCaller WorkflowAware The object delegating the workflow aware behaviour.
	 * @throws WorkflowException
	 */
	public WorkflowAwareImpl(Workflow inWorkflow, Object[] inArgs, WorkflowAware inCaller) throws WorkflowException {
		super();
		enterWorkflow(inWorkflow, inArgs, inCaller);
	}	

	/**
	 * WorkflowAwareImpl constructor.
	 * 
	 * @param inWorkflow Workflow The relevant Workflow.
	 * @param inInitialStateName java.lang.String The name of the initial State.
	 * @param inArgs java.langObject[] Arguments passed down to all handlers called.
	 * @param inCaller WorkflowAware The object delegating the workflow aware behaviour.
	 * @throws WorkflowException
	 */
	public WorkflowAwareImpl(Workflow inWorkflow, String inInitialStateName, Object[] inArgs, WorkflowAware inCaller) throws WorkflowException {
		super();
		enterWorkflow(inWorkflow, inWorkflow.getInitialStateName(), inArgs, inCaller);
	}
	
	/**
	 * [Re-]Bind this object to the specified workflow.
	 * The Workflow must provide a default initial State.
	 * 
	 * @param inWorkflow Workflow The Workflow the workflow aware object has to be bound to.
	 * @param inArgs java.langObject[] Arguments passed down to all handlers called.
	 * @param inCaller WorkflowAware The object delegating the workflow aware behaviour.
	 * @throws WorkflowException
	 */
	public void enterWorkflow(Workflow inWorkflow, Object[] inArgs, WorkflowAware inCaller) throws WorkflowException {
		enterWorkflow(inWorkflow, inWorkflow.getInitialStateName(), inArgs, inCaller);
	}
	
	/**
	 * [Re-]Bind this object to the specified workflow.
	 * The <code>inInitialStateName</code> parameter is the workflow State that 
	 * should be taken on initially.
	 * 
	 * @param inWorkflow Workflow The Workflow the workflow aware object has to be bound to.
	 * @param inInitialStateName java.lang.String The name of the initial State.
	 * @param inArgs java.langObject[] Arguments passed down to all handlers called.
	 * @param inCaller WorkflowAware The object delegating the workflow aware behaviour.
	 * @throws WorkflowException
	 */
	public void enterWorkflow(Workflow inWorkflow, String inInitialStateName, Object[] inArgs, WorkflowAware inCaller) throws WorkflowException {
		workflow = inWorkflow;
		actualStateName = inInitialStateName;
		doOnEnter(inInitialStateName, inCaller, inArgs);
	}

	/**
	 * Performs the transition with the specified name, 
	 * changes the state of the object and runs any defined 
	 * state/transition handlers.
	 * Extra arguments are passed down to all handlers called.
	 * 
	 * @param inTransitionName java.lang.String
	 * @param inArgs java.lang.Object[] Arguments passed down to all handlers called.
	 * @param inCaller WorkflowAware The object delegating the workflow aware behaviour.
	 * @throws WorkflowException
	 */
	public void doTransition(String inTransitionName, Object[] inArgs, WorkflowAware inCaller) throws WorkflowException {
		//call app-specifig leave-state handler if any
		doOnLeave(actualStateName, inCaller, inArgs);

		State lState = workflow.getState(actualStateName);
		String lStateToName = lState.getTransition(inTransitionName).getStateTo();
		actualStateName = lStateToName;
		
		//call app-specific transition state handler if any
		doOnTransition(inTransitionName, inCaller, inArgs);
		//call app-specific enter-state handler if any
		doOnEnter(actualStateName, inCaller, inArgs);
	}

	/**
	 * Returns the name of the current State.
	 * 
	 * @return java.lang.String
	 */
	public String getStateName() {
		return actualStateName;
	}

	/**
	 * Returns the current State instance.
	 * 
	 * @return State
	 * @throws WorkflowException
	 */
	public State getState() throws WorkflowException {
		return workflow.getState(actualStateName);
	}
	
	/**
	 * @param inMethodType java.lang.String
	 * @param inStateName java.lang.String
	 * @return java.lang.String
	 */
	private String getMethodName(String inMethodType, String inStateName) {
		return inMethodType + inStateName;
	}
	
	/**
	 * Returns an array of parameter types.
	 * 
	 * @param inArgs java.lang.Object[]
	 * @return java.lang.Class[]
	 */
	private Class<?>[] prepareParameterTypes(Object[] inArgs) {
		if (inArgs.length == 0)
			return new Class[] {};
		
		Class<?>[] outParameterTypes = new Class[inArgs.length];
		for (int i = 0; i < inArgs.length; i++) {
			Object lArg = inArgs[i];
			outParameterTypes[i] = lArg.getClass();
		}
		return outParameterTypes;
	}
	
	/**
	 * Do the specified action (onEnter, onLeave, onTransition) on the specified
	 * caller object by invoking the appropriate method.
	 * If the caller object doesn't implement the specified method, nothing happens.
	 * 
	 * @param inMethodName java.lang.String
	 * @param inCaller WorkflowAware
	 * @param inArgs java.lang.Object[]
	 * @throws WorkflowException
	 */
	private void doAction(String inMethodName, WorkflowAware inCaller, Object[] inArgs) throws WorkflowException {
		try {
			Method lAction = inCaller.getClass().getMethod(inMethodName, prepareParameterTypes(inArgs));
			lAction.invoke(inCaller, inArgs);
		}
		catch (NoSuchMethodException exc) {
			//left blank intentionally
		}
		catch (InvocationTargetException exc) {
			Throwable lThrowable = exc.getTargetException();
			throw new WorkflowError(lThrowable.getMessage(), lThrowable);
		}
		catch (Exception exc) {
			throw new WorkflowException(exc.getMessage());
		}
	}
	
	private void doOnEnter(String inStateName, WorkflowAware inCaller, Object[] inArgs) throws WorkflowException {
		doAction(getMethodName(METHOD_ON_ENTER, inStateName), inCaller, inArgs);
	}
	
	private void doOnLeave(String inStateName, WorkflowAware inCaller, Object[] inArgs) throws WorkflowException {
		doAction(getMethodName(METHOD_ON_LEAVE, inStateName), inCaller, inArgs);
	}

	private void doOnTransition(String inStateName, WorkflowAware inCaller, Object[] inArgs) throws WorkflowException {
		doAction(getMethodName(METHOD_ON_TRANSITION, inStateName), inCaller, inArgs);
	}
}
