package org.hip.kernel.bom.impl;

import org.hip.kernel.bom.LimitObject;
import org.hip.kernel.sys.VObject;

/*
 This package is part of the framework used for the application VIF.
 Copyright (C) 2005, Benno Luthiger

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

/**
 * Implements the LIMIT interface to limit the number of rows returned.
 * 
 * @author Benno Luthiger
 */
public class LimitObjectImpl extends VObject implements LimitObject {
	private int number = 0;
	private int offset = 0;
	
	/**
	 * LimitObjectImpl constructor.
	 * 
	 * @param inNumber int Number of rows to be returned.
	 */
	public LimitObjectImpl(int inNumber) {
		super();
		number = inNumber;
	}
	
	/**
	 * LimitObjectImpl constructor.
	 * 
	 * @param inNumber int Number of rows to return.
	 * @param inOffset int Offset of the first row to return.
	 */
	public LimitObjectImpl(int inNumber, int inOffset) {
		super();
		number = inNumber;
		offset = inOffset;
	}

	/**
	 * Returns the limitation as array {Integer(LIMIT), Integer(OFFSET)}.
	 * 
	 * @return Object[]
	 */
	public Object[] getArguments() {
		Object[] outArguments = {new Integer(number), new Integer(offset)};
		return outArguments;
	}
}
