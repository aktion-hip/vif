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

package org.hip.kernel.exc;

import java.util.Locale;

/**
 * Base class of exceptions of the VIF Framework and Application
 * 
 * @author Benno Luthiger
 */ 
public class VException extends Exception implements VThrowable {
	
	//instance attributes
	private 	Throwable		rootCause		= null;
	protected	ExceptionData	exceptionData	= null;

	/**
	 * Default VException constructor
	 */
	public VException() {
		this( "no message text" );
	}
	
	/**
	 * VException constructor with specified message
	 * 
	 * @param inSimpleMessage java.lang.String
	 */
	public VException(String inSimpleMessage) {
		exceptionData = new ExceptionData(this, inSimpleMessage);
	}
	
	/**
	 * VException constructor with message key and application context
	 * 
	 * @param inMsgKey String
	 * @param inLocale Locale
	 */
	public VException(String inMsgKey, Locale inLocale) {
		this(inMsgKey, null, inLocale);
	}
	
	/**
	 * VException constructor with message key and parameters
	 * 
	 * @param inMsgKey 		java.lang.String The key of the error message in the ressource bundle.
	 * @param inMsgParameters 	java.lang.Object[] 
	 */ 
	public VException(String inMsgKey, Object[] inMsgParameters) {
		this((String)null, inMsgKey, inMsgParameters);
	}
	
	/**
	 * VException constructor with message key, parameters and application context
	 * 
	 * @param inMsgKey String The key of the error message in the resource bundle.
	 * @param inMsgParameters java.lang.Object[] 
	 * @param inLocale Locale
	 */
	public VException(String inMsgKey, Object[] inMsgParameters, Locale inLocale) {
		this((String)null, inMsgKey, inMsgParameters);
		exceptionData.setLocale(inLocale);
	}

	/**
	 * VException constructor with message source, key and parameters
	 * 
	 * @param inMsgSource 		java.lang.String The source the error happened.
	 * @param inMsgKey 		java.lang.String The key of the error message in the ressource bundle.
	 * @param inMsgParameters 	java.lang.Object[] 
	 */
	public VException(String inMsgSource, String inMsgKey, Object[] inMsgParameters) {
		exceptionData = new ExceptionData(this, inMsgSource, inMsgKey, inMsgParameters);
	}
	
	/**
	 * 	A hook to allow subclasses to define their own ExceptionData
	 *  classes.
	 * 
	 * 	@return org.hip.kernel.exc.ExceptionData
	 */
	protected ExceptionData createExceptionData() {
		return new ExceptionData(this);
	}
	
	/**
	 * Creates the ExceptionData
	 * 
	 * @return org.hip.kernel.exc.ExceptionData
	 */
	private ExceptionData excData() {
		if (exceptionData == null) {
			exceptionData = createExceptionData();
		}
		return exceptionData;
	}
	
	/**
	 * Returns the message localized according to the actual language setting
	 * 
	 * @return java.lang.String
	 */ 
	public String getLocalizedMessage() {
		return excData().getLocalizedMessage();
	}
	
	/**
	 * Returns the message localized according to the specified language
	 * 
	 * @return java.lang.String
	 * @param inLanguage java.util.Locale
	 */
	public String getLocalizedMessage(Locale inLanguage) {
		return excData().getLocalizedMessage(inLanguage);
	}

	/**
	 * Returns the exception's message
	 * 
	 * @return java.lang.String
	 */
	public String getMessage() {
		return excData().getMessage();
	}

	/**
	 * Returns the original exception if any.
	 *
	 * @return java.lang.Throwable
	 */
	public final Throwable getRootCause() {
		return rootCause;
	}

	/**
	 * Returns true if this exception contains a root cause exception.
	 *
	 * @return boolean
	 */
	public boolean hasRootCause() {
		return rootCause != null;
	}

	/**
	 * Setter for the root cause exception.
	 *
	 * @param inRootCause java.lang.Throwable
	 */
	public synchronized void setRootCause(Throwable inRootCause) {
		rootCause = inRootCause;
	}
	
	/**
	 * 	We override this method to invoke the getMessage.
	 *
	 * 	@return java.lang.String
	 */
	public String toString() {
		String lMessage = (excData().get(ExceptionData.DATA_MSG_KEY) == null) ? excData().getMessage() : excData().get(ExceptionData.DATA_MSG_KEY).toString();
		
		return this.getClass().getName() + ": " + lMessage;
	}
}
