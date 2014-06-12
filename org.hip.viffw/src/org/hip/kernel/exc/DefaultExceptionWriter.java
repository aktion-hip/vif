package org.hip.kernel.exc;

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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.hip.kernel.sys.VSys;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of error log stream.
 * 
 * @author Benno Luthiger
 */
public class DefaultExceptionWriter {
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DefaultExceptionWriter.class);
	
	//constants
	public static final String		LOG_SWITCH			= "org.hip.error.log";
	public static final String		LOG_FILENAME_ID		= "org.hip.error.log.file";	
	public static final String		EXCEPTION_LOG_NAME 	= "error%g.log";
		
	// Class attributes
	private static PrintWriter 	cDefaultWriter 	= new PrintWriter(VSys.err);
	private static boolean		doLogging		= true;
	
	/**
	 * Creates an error message with the specified information.
	 * 
	 * @return java.lang.String
	 * @param inCatchingObject java.lang.Object
	 * @param inThrowable java.lang.Throwable
	 */
	protected static String createMessage(Throwable inThrowable) {
		StringBuffer outMessage = new StringBuffer();

		//add info of the nested exception
		if(AbstractExceptionHandler.isVException(inThrowable) && ((VThrowable) inThrowable).hasRootCause()) {
			  outMessage.append(inThrowable);
			  Throwable lNestedThrowable = ((VThrowable)inThrowable).getRootCause();
			  outMessage.append(", Nested: ").append(lNestedThrowable).append("\n");
			  outMessage.append(getStackTraceAsString(lNestedThrowable));
		} 
		else {
			outMessage.append(getStackTraceAsString(inThrowable));
		}		
		return new String(outMessage);
	}
	
	/**
	 * Converts the stack trace to a string.
	 * 
	 * @return java.lang.String
	 * @param inThrowable java.lang.Throwable
	 */
	protected static String getStackTraceAsString(Throwable inThrowable) {
		StringWriter lStringWriter = new StringWriter();
		PrintWriter lWriter = new PrintWriter(lStringWriter);
		inThrowable.printStackTrace(lWriter);
		return lStringWriter.toString();
	}
	
	/**
	 * Prints an error message with the specified information to the configured error stream.
	 * 
	 * @param inCatchingObject java.lang.Object
	 * @param inThrowable java.lang.Throwable
	 * @param inPrintStackTrace boolean
	 */
	public static void printOut(Object inCatchingObject, Throwable inThrowable, boolean inPrintStackTrace) {
		if (doLogging) {	
			printOutToLog(inCatchingObject, inThrowable);			
		} 
		else {
			printOut(inCatchingObject, inThrowable, inPrintStackTrace, cDefaultWriter);
		}
	}
	
	/**
	 * Prints an error message with the specified information to the specified PrintWriter
	 * 
	 * @param inCatchingObject java.lang.Object
	 * @param inThrowable java.lang.Throwable
	 * @param inPrintStackTrace boolean
	 * @param inWriter java.io.PrintWriter
	 */
	public static void printOut(Object inCatchingObject, Throwable inThrowable, boolean inPrintStackTrace, PrintWriter inWriter) {
		if (inPrintStackTrace) {
			inThrowable.printStackTrace(inWriter);
		}
		else {
			String lMessage = (inCatchingObject != null) ? "Caught in: " + inCatchingObject.getClass().getName() + "." : "";
			inWriter.println(lMessage);
			inWriter.flush();		                        
		}
	}
	
	/**
	 * @param inCatchingObject java.lang.Object
	 * @param inException java.lang.Throwable
	 * @param inPrintStackTrace boolean
	 */
	private static void printOutToLog(Object inCatchingObject, Throwable inException) {
		String lMessage = (inCatchingObject != null) ? "Caught in: " + inCatchingObject.getClass().getName() + "." : "";
		LOG.error(lMessage, inException);
	}
	
	/**
	 * Method to call during the application's shut down.
	 * Closes all handlers attached to the application's error logger.
	 * @deprecated
	 */
	public static void closeLogger() {
		//not used anymore
	}	
	
}
