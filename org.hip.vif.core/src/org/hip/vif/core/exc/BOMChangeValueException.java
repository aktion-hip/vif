package org.hip.vif.core.exc;

/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2001, Benno Luthiger

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

import org.hip.kernel.bom.BOMException;

/**
 * This exception is used whenever an exception caused by changing
 * a domain objects values occurs.
 * 
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class BOMChangeValueException extends BOMException {
	/**
	 * BOMChangeValueException constructor comment.
	 */
	public BOMChangeValueException() {
		super();
	}
	
	/**
	 * BOMChangeValueException constructor comment.
	 * @param inMessage java.lang.String
	 */
	public BOMChangeValueException(String inMessage) {
		super(inMessage);
	}
}
