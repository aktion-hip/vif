package org.hip.kernel.util;

/*
	This package is part of the framework used for the application VIF.
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

import java.io.Serializable;

import org.hip.kernel.sys.VObject;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.sys.Assert;

/**
 * This is the base implementation for the name value support.
 *
 * @author Benno Luthiger
 */
abstract public class AbstractNameValue extends VObject	implements NameValue, Serializable {
	// Instance variables
	private NameValueList	owingList	= null;
	private String			name		= null;
	private Object			value		= null;
	
	/**
	 * AbstractNameValue constructor.
	 * Initializes name, value and the list the instance belongs to.
	 *
	 * @param inOwingList org.hip.kernel.util.NameValueList
	 * @param inName java.lang.String
	 * @param inValue java.lang.Object
	 */
	public AbstractNameValue(NameValueList inOwingList, String inName, Object inValue) {
		owingList 	= inOwingList;
		name		= inName;
		value		= inValue;
	}
	
	/**
	 * @param inVisitor org.hip.kernel.util.NameValueListVisitor
	 */
	public void accept(NameValueListVisitor inVisitor) {
		inVisitor.visitNameValue(this);
	}
	
	/**
	 * @param inName java.lang.String
	 * @exception org.hip.kernel.util.VInvalidNameException
	 */
	protected abstract void checkName(String inName) throws VInvalidNameException ;
	
	/**
	 * @param inNalue java.lang.Object
	 * @exception org.hip.kernel.util.VInvalidValueException
	 */
	protected abstract void checkValue(Object inValue) throws VInvalidValueException ;
	
	/**
	 * Returns the name.
	 *
	 * @return java.lang.String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the list the instance is member from.
	 *
	 * @return org.hip.kernel.util.NameValueList
	 */
	public NameValueList getOwingList() {
		return owingList;
	}
	
	/**
	 * @return java.lang.Object
	 */
	public Object getValue() {
		return value;
	}
	
	/**
	 * @return java.lang.String
	 */
	protected String name() {
		return name;
	}
	
	/**
	 * @param name java.lang.String
	 */
	protected synchronized void name(String inName) {
		name = inName;
	}
	
	/**
	 * Sets the value of the instance with the specified name.
	 *
	 * @param inName java.lang.String
	 * @param inValue java.lang.Object
	 * @exception org.hip.kernel.util.VInvalidNameException
	 * @exception org.hip.kernel.util.VInvalidValueException
	 */
	public void set(String inName, Object inValue)  throws VInvalidNameException, VInvalidValueException {
		try {	
			checkName(inName); 
			checkValue(inValue);
		} 
		catch (VInvalidValueException exc) {
			throw exc;
		} 
		catch (VInvalidNameException exc) {
			throw exc;
		}	
	}
	
	/**
	 * @param name java.lang.String
	 */
	public void setName(String inName) {
	}
	
	/**
	 * @param inOwingList org.hip.kernel.util.NameValueList
	 */
	public void setOwingList(NameValueList inOwingList) {
	
		// Pre: owingList not null
		if (VSys.assertNotNull(this, "setOwingList", inOwingList) == Assert.FAILURE) {
			return;
		}
	
		// Post: owing list set
		owingList = inOwingList;
	}
	
	/**
	 * Sets the value of the instance.
	 *
	 * @param inValue java.lang.Object
	 * @exception org.hip.kernel.util.VInvalidValueException
	 */
	public void setValue(Object inValue) throws VInvalidValueException {
		try { 
			// protected setter
			checkValue(inValue);
			value(inValue);
		}
		catch (VInvalidValueException exc) {
			throw exc ;
		}
	}
	
	/**
	 * Gets value.
	 *
	 * @param value java.lang.Object
	 */
	protected Object value() {
		return value;
	}
	
	/**
	 * Protected setter of value.
	 *
	 * @param inValue java.lang.Object
	 */
	protected synchronized void value(Object inValue) {
		value = inValue;
	}
	
	/**
	 * Returns the HashCode calculated from the name and value.
	 * 
	 * @return int
	 */
	public int hashCode() {
		int outHashCode = getValue() == null ? 0 : getValue().hashCode();
		return getName().hashCode() ^ outHashCode;
	}
	
	/**
	 * Returns true if inObject is a <code>NameValue</code> and its
	 * name and value are equal to this NameValue.
	 * 
	 * @return boolean
	 * @param inObject java.lang.Object
	 */
	public boolean equals(Object inObject) {
	
		if (inObject == null) return false;
		if (!(inObject instanceof NameValue)) return false;
		
		NameValue lInNameValue = (NameValue)inObject;
		if (lInNameValue.getName().equals(getName())) {
			// either both values have to be null
			if (lInNameValue.getValue() == null) {
				if (getValue() == null) return true;
				return false;
			}
			// or both values have to be equal
			else {
				if (lInNameValue.getValue().equals(getValue())) return true;
			}
		}
		return false;
	}
	
	/**
	 * @return java.lang.String
	 */
	public String toString() {
		String lMessage = "name=\""	+ getName() + "\" " 
		                + "value=\"" + ((getValue() != null) ? getValue().toString() : "null" ) + "\"";
		return Debug.classMarkupString(this, lMessage);
	}
}
