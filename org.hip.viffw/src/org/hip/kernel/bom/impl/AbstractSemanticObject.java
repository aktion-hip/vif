package org.hip.kernel.bom.impl;

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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.Property;
import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.SemanticObject;
import org.hip.kernel.bom.SettingException;
import org.hip.kernel.sys.VObject;
import org.hip.kernel.util.Debug;
import org.hip.kernel.util.VInvalidNameException;
import org.hip.kernel.util.VInvalidValueException;

/**
 * This class implements the interface SemanticObject.
 *
 *	@author Benno Luthiger
 *	@see org.hip.kernel.bom.SemanticObject
 */
abstract public class AbstractSemanticObject extends VObject implements SemanticObject, Serializable {
	// Instance variables
	private PropertySet propertySet = null;
	
	/**
	 * AbstractSemanticObject default constructor.
	 */
	public AbstractSemanticObject() {
		super();
	}
	
	/**
	 * SemanticObjects are equal if all of their
	 * name-values are equal.
	 *
	 * @return boolean
	 */
	public boolean equals(Object inObject) {
		if (inObject == null) return false;
		if (!(inObject instanceof SemanticObject)) return false;
	
		SemanticObject lObjectToCompare = (SemanticObject)inObject;
		try {
			for (String lName : getPropertyNames2()) {
				Object lValue = get(lName);
				if (lValue == null) {
					if (lObjectToCompare.get(lName) != null)
						return false;
				}
				else {
					return lValue.equals(lObjectToCompare.get(lName));
				}				
			}
		}
		catch (GettingException exc) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the Value to a given Name.
	 *
	 * @return java.lang.Object
	 * @param inName java.lang.String
	 * @exception org.hip.kernel.bom.GettingException
	 */
	public Object get(String inName) throws GettingException {
		try { 
			return propertySet().getValue(inName);			
		} 
		catch (VInvalidNameException exc) {
			throw new GettingException("InvalidName: " + inName);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.SemanticObject#getPropertyNames()
	 */
	public Iterator<String> getPropertyNames() {
		return propertySet().getNames2().iterator();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.SemanticObject#getPropertyNames2()
	 */
	public Collection<String> getPropertyNames2() {
		return propertySet().getNames2();
	}
	
	/**
	 * Returns a hash code value for the semantic object.
	 *
	 * @return int
	 */
	public int hashCode() {
		int outCode = 1;
		try {
			for (String lName : getPropertyNames2()) {
				outCode ^= lName.hashCode() ^ (get(lName)==null ? 0 : get(lName).hashCode());				
			}
		}
		catch (GettingException exc) {
			//left blank intentionally
		}
		return outCode;
	}
	
	/**
	 * 	A hook-method for subclasses to initialize the property
	 *	set.
	 * 
	 * 	@param inSet org.hip.kernel.bom.PropertySet
	 */
	protected abstract void initializePropertySet(PropertySet inSet);
	
	/**
	 * @return boolean
	 */
	protected abstract boolean isDynamicAddAllowed();
	
	/**
	 * Accessor to the property set.
	 * 
	 * @return org.hip.kernel.bom.PropertySet
	 */
	public synchronized PropertySet propertySet() {
	
		if (propertySet == null) {
			 propertySet = new PropertySetImpl(this);
			 initializePropertySet(propertySet);
		}
		return propertySet;
	}
	
	/**
	 * This method sets the value of the specified Property
	 *
	 * @param inName java.lang.String
	 * @param inValue java.lang.Object
	 * @exceptions org.hip.kernel.bom.SettingException
	 */
	public void set(String inName, Object inValue) throws SettingException {
		
		Property lProperty = (Property)propertySet().get(inName);
		if (lProperty == null) {
			if (isDynamicAddAllowed()) {
				propertySet().add(new PropertyImpl(propertySet(), inName, inValue));
			}
			else {
				throw new SettingException("Invalid name: " + inName);
			}
		} 
		else {
			try { 
				lProperty.setValue(inValue);
			} 
			catch (VInvalidValueException exc) {
				throw new SettingException(exc.getMessage());
			} // catch		
		} // if-else	 
	}
	
	/**
	 * Used to set an existing instance to an initial state for that
	 * it can be reused.
	 */
	public void setVirgin() {
		propertySet().setVirgin();
	}
	
	public String toString() {
		StringBuffer lMessage = new StringBuffer("");
		try {
			for (String lName : getPropertyNames2()) {
				lMessage.append("\t<name=\"" + lName + "\" ");
				lMessage.append("value=\"" + (get(lName)==null ? "null" : get(lName).toString()) + "\"/>\n");				
			}
		}
		catch (GettingException exc) {
			//left blank intentionally
		}
		return Debug.classMultilineMarkupString(this, new String(lMessage));
	}
}