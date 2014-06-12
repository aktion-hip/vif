/*
 This package is part of the application VIF.
 Copyright (C) 2010, Benno Luthiger

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
package org.hip.vif.core.exc;

import org.hip.kernel.exc.VRuntimeException;
import org.hip.kernel.sys.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Assert class provides methods to assert.
 * Variant of the framework's <code>Assert</code> class using the <code>IDelayedMessage</code> to display properly localized messages.
 *
 * @author Luthiger
 * Created: 09.07.2010
 * @see org.hip.kernel.sys.Assert
 */
public class Assert {
	private static final Logger LOG = LoggerFactory.getLogger(Assert.class);
	
	public enum AssertLevel {
		ERROR_OUT, EXCEPTION, ERROR;
	}

	/** Constants */
	public static final	boolean FAILURE		= true;
	public static final boolean NO_FAILURE  = false;
	
	/**
	 * This method checks the condition. If it evaluates to false, it will print out an assert failure message or throw a AssertionFailedError.
	 * 
	 * @param inAssertLevel {@link AssertLevel}
	 * @param inCaller Object
	 * @param inCallerMethod String
	 * @param inMessage {@link IDelayedMessage}
	 * @param inCondition boolean
	 * @return boolean
	 */
	public final static boolean assertTrue(AssertLevel inAssertLevel, Object inCaller, String inCallerMethod, boolean inCondition) {
//	public final static boolean assertTrue(AssertLevel inAssertLevel, Object inCaller, String inCallerMethod, IDelayedMessage inMessage, boolean inCondition) {
		if (!inCondition) {
			return fail(inAssertLevel, inCaller, inCallerMethod, "assertTrue");
		}
		return Assert.NO_FAILURE;
	}

	private static synchronized boolean fail(AssertLevel inAssertLevel, Object inCaller, String inCallerMethod, String inAssertName) {
		switch (inAssertLevel) {
		case ERROR_OUT:
			LOG.error(prepareAssertText(inCaller, inCallerMethod, inAssertName));
			break;
		case EXCEPTION:
			LOG.error(prepareAssertText(inCaller, inCallerMethod, inAssertName));
			throw new VRuntimeException();
		case ERROR:
			throw new AssertionFailedError(inCaller, inCallerMethod, inAssertName);
		default:
			LOG.error(prepareAssertText(inCaller, inCallerMethod, inAssertName));
		}
		return Assert.FAILURE;
	}
	
	/**
	 * This method prepares the assert text from the passed objects.
	 *
	 * @return java.lang.String
	 * @param inCaller java.lang.Object
	 * @param inCallerMethod java.lang.String
	 * @param inAssertName java.lang.String
	 */
	protected static String prepareAssertText(Object inCaller, String inCallerMethod, String inAssertName) {
		String lClassName = inCaller != null ? inCaller.getClass().getName() : "unknownClass";
		String lMethodName = inCallerMethod != null ? inCallerMethod : "unknownMethod";
		
		return String.format("Assertion failure (%s) in: %s: %s", inAssertName, lClassName, lMethodName);
	}

}
