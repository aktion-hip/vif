package org.hip.kernel.exc.test;

/*
	This package is part of the servlet framework used for the application VIF.
	Copyright (C) 2001, Benno Luthiger

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

import org.hip.kernel.exc.VThrowable;
import org.hip.kernel.exc.VException;
import org.hip.kernel.exc.ExceptionHandler;
import org.hip.kernel.exc.AbstractExceptionHandler;

/**
 * 	A test implementation for an exception handler for testing purpose.
 *
 * 	@author	Benno Luthiger
 */
public class TestExceptionHandler extends AbstractExceptionHandler {
	
	//class attributes
	private static ExceptionHandler	cInstance = null;
	private int numberOfHandles = 0;
	
	/**
	 * Converts any Throwable to an instance of VThrowable
	 *
	 * @return java.lang.Throwable
	 * @param inThrowable 	java.lang.Throwable
	 * @param inId 			java.lang.String
	 */
	public Throwable convert(Throwable inThrowableToBeConverted, String inId) {
		
		VThrowable outException = new VException(inId);
		outException.setRootCause(inThrowableToBeConverted);
	
		//remove constructor statements from stackTrace
		((Throwable)outException).fillInStackTrace();  
		
		return (Throwable)outException;
	}
	
	/**
	 * @return java.lang.String
	 * @param inCatchingObject java.lang.Object
	 * @param inThrowable java.lang.Throwable
	 */
	private static String createMessage(Object inCatchingObject, Throwable inThrowable) {
	
		//add info about catchingObject
		StringBuffer outMessage = new StringBuffer();
		outMessage.append((inCatchingObject != null) ? "Caught in: " + inCatchingObject.getClass().getName() + " --> " : "");
	
		//add info of the nested exception
		if (AbstractExceptionHandler.isVException(inThrowable) && ((VThrowable) inThrowable).hasRootCause()) {
			  outMessage.append(inThrowable.toString());
			  Throwable lNestedThrowable = ((VThrowable)inThrowable).getRootCause();
			  outMessage.append(", Nested: " + lNestedThrowable.toString());
		} 
	
		return new String(outMessage);	
	}
	
	public int getNumberOfHandles() {
		return numberOfHandles;
	}
	
	/**
	 * 	Returns the single instance of this handler class.
	 * 
	 * 	@return org.hip.kernel.exc.ExceptionHandler
	 */
	static public ExceptionHandler instance() {
		
		if ( cInstance == null ) {
			 cInstance =  new TestExceptionHandler();
		}
		return cInstance;
	}
	
	/**
	 * 	@param inCatchingObject java.lang.Object
	 * 	@param inThrowable java.lang.Throwable
	 * 	@param inPrintStackTrace boolean
	 */
	protected void protectedHandle(Object inCatchingObject, Throwable inThrowable, boolean inPrintStackTrace) {
		System.out.println("Test of protectedHandle: " + ++numberOfHandles + "\n" +createMessage(inCatchingObject, inThrowable));
	}
}
