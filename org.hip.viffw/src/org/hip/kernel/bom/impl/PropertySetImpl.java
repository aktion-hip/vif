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

package org.hip.kernel.bom.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.DomainObjectVisitor;
import org.hip.kernel.bom.Property;
import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.SemanticObject;
import org.hip.kernel.util.AbstractNameValueList;
import org.hip.kernel.util.NameValue;
import org.hip.kernel.util.VInvalidNameException;
import org.hip.kernel.util.VInvalidValueException;

/**
 * This class implements the PropertySet interface.
 *
 *	@author		Benno Luthiger
 *	@see		org.hip.kernel.bom.PropertySet
 */
public class PropertySetImpl extends AbstractNameValueList implements PropertySet {
	// Instance variables
	private	SemanticObject parent = null;

	/**
	 * PropertySetImpl constructor. Intializes the parent.
	 *
	 * @param inParent org.hip.kernel.bom.SemanticObject
	 */
	public PropertySetImpl(SemanticObject inParent) {
		parent = inParent;
	}

	/**
	 * @param inVisitor org.hip.kernel.bom.DomainObjectVisitor
	 */
	public void accept(DomainObjectVisitor inVisitor) {
		inVisitor.visitPropertySet(this);
	}

	/**
	 * This method creates a PropertyImpl (NameValue) 
	 * and initializes it with the inValue.
	 *
	 * @return org.hip.kernel.util.NameValue
	 * @param inName java.lang.String
	 * @param inValue java.lang.Object
	 * @exception org.hip.kernel.util.VInvalidNameException
	 * @exception org.hip.kernel.util.VInvalidValueException
	 */
	protected NameValue create(String inName, Object inValue) throws VInvalidValueException, VInvalidNameException {
		NameValue outValue = (NameValue) new PropertyImpl( this, inName ) ;
		outValue.setValue(inValue);
		return outValue;
	}

	/**
	 * @return boolean
	 */
	public boolean dynamicAddAllowed() {
		return true;
	}

	/**
	 * Sets the notification status of the Properties in this PropertySet.
	 * Notification set to true means that the Properties in this set are
	 * marked as changed when they are initialized.
	 * Default: false.
	 * 
	 * @param inNotification boolean
	 */
	public void notifyInit(boolean inNotification) {
		for (NameValue lNameValue : this.getNameValues2()) {
			((Property)lNameValue).notifyInit(inNotification);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.PropertySet#getChangedProperties()
	 */
	public Iterator<Property> getChangedProperties() {
		return getChangedProperties2().iterator();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.PropertySet#getChangedProperties2()
	 */
	public Collection<Property> getChangedProperties2() {
		Collection<Property> outChanged = new Vector<Property>();
		for (NameValue lNameValue : this.getNameValues2()) {
			if (((Property)lNameValue).isChanged()) {
				outChanged.add((Property)lNameValue);
			}
		}
		return outChanged;
	}
	
	
	/**
	 * @return org.hip.kernel.bom.SemanticObject
	 */
	public SemanticObject getParent() {
		return parent;
	}

	/**
	 * Used to set an existing instance to an initial state for that
	 * it can be reused.
	 */
	public void setVirgin() {
		for (NameValue lNameValue : getNameValues2()) {
			((Property)lNameValue).setVirgin();
		}
	}

	/**
	 * @return java.lang.String
	 */
	public String toString() {
		StringBuffer outString = new StringBuffer("<PropertySet> ");
		for (NameValue lNameValue : getNameValues2()) {
			outString.append("\n\t").append(lNameValue.toString());
		}
		outString.append("\n </PropertySet> \n");
		return new String(outString);
	}
}