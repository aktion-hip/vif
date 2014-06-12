/*
 This package is part of the application VIF.
 Copyright (C) 2005, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.forum.groups.tasks;

import org.hip.vif.core.exc.VIFWarningException;

/**
 * Warning that can be thrown if a guest tries to see a question that is
 * beyond the guest depth threshold.
 * 
 * @author Benno Luthiger
 * Created on Apr 14, 2005
 */
@SuppressWarnings("serial")
public class GuestDepthException extends VIFWarningException {

	/**
	 * @param inSimpleMessage
	 */
	public GuestDepthException(String inSimpleMessage) {
		super(inSimpleMessage);
	}

	/**
	 * @param inMsgKey
	 * @param inMsgParameters
	 */
	public GuestDepthException(String inMsgKey, Object[] inMsgParameters) {
		super(inMsgKey, inMsgParameters);
	}

	/**
	 * @param inMsgSource
	 * @param inMsgKey
	 * @param inMsgParameters
	 */
	public GuestDepthException(String inMsgSource, String inMsgKey, Object[] inMsgParameters) {
		super(inMsgSource, inMsgKey, inMsgParameters);
	}

}
