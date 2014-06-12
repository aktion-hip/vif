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

import org.hip.kernel.bom.DomainObjectVisitor;
import org.hip.kernel.bom.ObjectRefProperty;
import org.hip.kernel.bom.Property;
import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.SemanticObject;
import org.hip.kernel.bom.model.PropertyDef;
import org.hip.kernel.bom.model.TypeDef;
import org.hip.kernel.exc.DefaultExceptionHandler;
import org.hip.kernel.exc.DefaultExceptionWriter;
import org.hip.kernel.util.AbstractNameValue;
import org.hip.kernel.util.VInvalidNameException;
import org.hip.kernel.util.VInvalidValueException;

/**
 * 	This is the implementation of the Property interface.
 *
 *	@author		Benno Luthiger
 *	@see		org.hip.kernel.bom.Property
 */
public class PropertyImpl extends AbstractNameValue implements Property {
	// Instance variables
	private PropertyDef	propertyDef		= null;
	private Object		initial			= null;
	private Object		current			= null;
	private boolean		isChanged      	= false;
	private boolean		isInitialized  	= false;
	private boolean		notifyInit		= false;
	private Class<?>	typeInformation = null;

	/**
	 * PropertyImpl constructor without value.
	 *
	 * @param inSet org.hip.kernel.bom.PropertySet
	 * @param inName java.lang.String
	 */
	public PropertyImpl(PropertySet inSet, String inName) {
		this(inSet, inName, null);
		isInitialized = false;	
	}
	
	/**
	 * PropertyImpl constructor which initializes with name and value.
	 *
	 * @param inSet org.hip.kernel.bom.PropertySet
	 * @param inName java.lang.String
	 * @param inValue java.lang.Object
	 */
	public PropertyImpl( PropertySet inSet, String inName, Object inValue ) {
		super( inSet, inName, inValue ) ;
		this.value( inValue ) ;
	}
	
	/**
	 * PropertyImpl constructor which initializes with name and value.
	 * The Property also has typeInformation to check the value.
	 *
	 * @param inSet org.hip.kernel.bom.PropertySet
	 * @param inName java.lang.String
	 * @param inValue java.lang.Object
	 * @param inTypeInformation java.lang.String
	 * @throws org.hip.kernel.util.VInvalidValueException
	 */
	public PropertyImpl(PropertySet inSet, String inName, Object inValue, String inTypeInformation) throws VInvalidValueException {
		super(inSet, inName, inValue);
		if (inTypeInformation.length() > 0) {
			try {
				typeInformation = Class.forName(inTypeInformation);
			}
			catch (ClassNotFoundException exc) {
				if (!TypeDef.Binary.equals(inTypeInformation)) {
					//We trace nothing in the case of Binary.
					DefaultExceptionWriter.printOut(this, exc, true);
				}
			}
		}
		this.setValue(inValue);
	}
	
	/**
	 * Implements visitor pattern for this class.
	 * 
	 * @param inVisitor org.hip.kernel.bom.DomainObjectVisitor
	 */
	public void accept(DomainObjectVisitor inVisitor) {
		inVisitor.visitProperty(this);
	}
	
	/**
	 * @param inName java.lang.String
	 * @exception org.hip.kernel.util.VInvalidNameException
	 */
	protected void checkName(String inName) throws VInvalidNameException {
		if (inName == null)
			throw new VInvalidNameException("Name of Property must not be null");
		if (!inName.equals(name()))
			throw new VInvalidNameException("Invalid Name " + inName);
	}
	
	/**
	 * @param inValue java.lang.Object
	 * @exception org.hip.kernel.util.VInvalidValueException
	 */
	protected void checkValue(Object inValue) throws VInvalidValueException {
		if (typeInformation == null) return;
		if (inValue == null) return;
		if (!typeInformation.isInstance(inValue)) 
			throw new VInvalidValueException("Invalid Type " + inValue.getClass().getName());
	}
	
	/**
	 * 	@return boolean
	 * 	@param inObject java.lang.Object
	 */
	public boolean equals( Object inObject ) {
		if (inObject == null) return false;
		if (!(inObject instanceof Property)) return false;
		
		return this.getName().equals(((Property)inObject).getName());
	}
	
	/**
	 * @return java.lang.String
	 */
	public String getFormatPattern() {
		try {
			return (String) getPropertyDef().get( "formatPattern" );
		} 
		catch (Exception exc) {
			return null;
		}
	}
	
	/**
	 * Returns the SemanticObject in the OwingList the instance belongs to.
	 *
	 * @return org.hip.kernel.bom.SemanticObject
	 */
	public SemanticObject getOwingObject() {
		return ((PropertySet)getOwingList()).getParent();
	}
	
	/**
	 * @return org.hip.kernel.bom.PropertyDef
	 */
	public PropertyDef getPropertyDef() {
		if (propertyDef == null)
			propertyDef = ((DomainObjectImpl)getOwingObject()).getHome().getPropertyDef(this.getName()) ;
		return propertyDef;
	}
	
	/**
	 * Gets the current value.
	 *
	 * @param value java.lang.Object
	 */
	public Object getValue() {
		return current;
	}
	
	/**
	 * Returns the HashCode calculated from the name.
	 * 
	 * @return int
	 */
	public int hashCode() {
		return this.getName().hashCode();
	}
	
	/**
	 * Returns true if the value has been changed after 
	 * the initial setting.
	 * 
	 * @return boolean
	 */
	public boolean isChanged() {
		return isChanged;
	}
	
	/**
	 * @return boolean
	 */
	public boolean isObjectRef() {
		return (this instanceof ObjectRefProperty);
	}
	
	/**
	 * @return boolean
	 */
	public boolean isSimple() {
		return true;
	}
	
	/**
	 * Sets the notification status of this Property.
	 * Notification set to true means that the Property is marked as changed
	 * when the property is initialized.
	 * Default: false.
	 * 
	 * @param inNotification boolean
	 */
	public void notifyInit(boolean inNotification) {
		notifyInit = inNotification;
	}
	
	/**
	 * Used to set an existing instance to an initial state for that
	 * it can be reused.
	 */
	public void setVirgin() {
		initial = null;
		current = initial;
		isInitialized = false;
		isChanged = false;
		notifyInit = false;
	}
	
	/**
	 * @return java.lang.String
	 */
	public String toString() {
	
		String retVal = "<Property " 
		                + "name=\""		+ getName() + "\" " 
		                + "value=\"" 	+ ((getValue() != null)? getValue().toString() : "null" ) + "\""
		                + " /> ";
		return retVal;
	}
	
	/**
	 * Gets the current value.
	 *
	 * @return java.lang.Object
	 */
	protected Object value() {
		return current;
	}
	
	/**
	 * Sets the current value of the Property and handles initial state.
	 *
	 * @param inValue java.lang.Object
	 */
	protected void value(Object inValue) {
		
		try {		
			if (!isInitialized) {
				if (inValue != null) {
					//we only initialize when value not null
					initial = inValue;
					current = initial;
					isInitialized = true;
					if (notifyInit) isChanged = true;
				}
			} //if (not initialized)
			else {
				// initial value exists
				if (inValue != null) {
					// value can be compared
					if (inValue.equals(initial)) {
						// value is equal to initial, therefore, set current to initial
						current = initial;
				 		isChanged = false;
				 	} //if (value==initial)
					else if (inValue.equals(current)) {
						// value is equal to current, therefore, return without any change
						return ;
				 	} //else (value==current)
					else {
					 	// value is different, therefore, set current to new value, mark instance as changed
				 		current = inValue;
				 		isChanged = true;
				 	} //else (value different)
				} //if (value not null)
				else {
					// new value is null and, therefore, can't be compared
					if (current != null) {
						// set current to new value, mark instance as changed
						current = inValue;
					 	isChanged = true;
					} // if
				} //else (value null)
			} //else (initialized)
		} // try
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
		}	// try-catch
	}
}