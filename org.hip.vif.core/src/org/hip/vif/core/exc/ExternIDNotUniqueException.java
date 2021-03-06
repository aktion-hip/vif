/**
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2001-2014, Benno Luthiger

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

package org.hip.vif.core.exc;


/**
 * Warning exception which is thrown when the specified UserID is not unique
 * 
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class ExternIDNotUniqueException extends VIFWarningException {
	/**
	 * ExternIDNotUniqueException constructor comment.
	 */
	public ExternIDNotUniqueException() {
		super();
	}

	/**
	 * ExternIDNotUniqueException constructor comment.
	 * 
	 * @param inSimpleMessage
	 *            java.lang.String
	 */
	public ExternIDNotUniqueException(final String inSimpleMessage) {
		super(inSimpleMessage);
	}

	/**
	 * ExternIDNotUniqueException constructor comment.
	 * 
	 * @param inMsgKey
	 *            java.lang.String
	 * @param inMsgParameters
	 *            java.lang.Object[]
	 */
	public ExternIDNotUniqueException(final String inMsgKey,
			final java.lang.Object[] inMsgParameters) {
		super(inMsgKey, inMsgParameters);
	}

	/**
	 * ExternIDNotUniqueException constructor comment.
	 * 
	 * @param inMsgSource
	 *            java.lang.String
	 * @param inMsgKey
	 *            java.lang.String
	 * @param inMsgParameters
	 *            java.lang.Object[]
	 */
	public ExternIDNotUniqueException(final String inMsgSource,
			final String inMsgKey, final java.lang.Object[] inMsgParameters) {
		super(inMsgSource, inMsgKey, inMsgParameters);
	}
}
