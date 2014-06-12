package org.hip.kernel.util;

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

import java.util.Vector;
import java.util.StringTokenizer;

import org.hip.kernel.sys.VSys;
import org.hip.kernel.sys.Assert;

/**
 * This is the default implementation for the name value support.
 */
public class DefaultNameValue extends AbstractNameValue {
	/**
	 * DefaultNameValue default constructor.
	 *
	 * @param inOwingList org.hip.kernel.util.NameValueList
	 * @param inName java.lang.String
	 * @param inValue java.lang.Object
	 */
	public DefaultNameValue(NameValueList inOwingList, String inName, Object inValue) {
		super(inOwingList, inName, inValue);
	}
	
	/**
	 * @param inName java.lang.String
	 * @exception org.hip.kernel.util.VInvalidNameException
	 */
	protected void checkName(String inName) throws VInvalidNameException {
	}
	
	/**
	 * @param inValue java.lang.Object
	 * @exception org.hip.kernel.util.VInvalidValueException
	 */
	protected void checkValue(Object inValue) throws VInvalidValueException {
	}
	
	/**
	 * The incoming string must have the form "<name>=<value>,".
	 *
	 * @return java.util.Vector Vector of NameValues extracted from the specified inSource
	 * @param inSource java.lang.String
	 */
	static public Vector<NameValue> extract(String inSource) {
	
		Vector<NameValue> outNameValues = new Vector<NameValue>();
	
		// Pre: inSource not null
		if (VSys.assertNotNull(DefaultNameValue.class, "extract", inSource) == Assert.FAILURE)
			return outNameValues;
	
		// We let the string tokenize
		StringTokenizer lTokenizer = new StringTokenizer(inSource, ",");
		while (lTokenizer.hasMoreElements()) {
			String lNext = lTokenizer.nextToken();
			int	lPosition = lNext.indexOf("=");
			String lName = lNext.substring(0, Math.min(lPosition, lNext.length()) ).trim();
			String lValue = lNext.substring(lPosition + 1).trim();
			NameValue lNameValue = new DefaultNameValue(null, lName, lValue);
			outNameValues.addElement(lNameValue);
		}
		return outNameValues;
	}
}
