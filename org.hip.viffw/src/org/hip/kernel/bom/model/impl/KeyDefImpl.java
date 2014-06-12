package org.hip.kernel.bom.model.impl;

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

import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.model.KeyDef;
import org.hip.kernel.bom.model.KeyDefDef;
import org.hip.kernel.bom.model.MetaModelHome;
import org.hip.kernel.bom.model.MetaModelObject;
import org.hip.kernel.exc.DefaultExceptionHandler;
import org.hip.kernel.util.Debug;

/**
 * Implements the KeyDef interface
 * 
 * @author Benno Luthiger
 * @see org.hip.kernel.bom.model.KeyDef
 */
abstract public class KeyDefImpl extends AbstractModelObject implements KeyDef {
	//
	private static Vector<String> emptyVector = new Vector<String>();
	
	/**
	 * KeyDefImpl default constructor.
	 */
	public KeyDefImpl() {
		super();
	}
	
	/**
	 * MappingDefImpl constructor with initial values.
	 * The array of the objects contains the names in the first column and the values in the second.
	 *
	 * @param inInitialValues java.lang.Object[][]
	 */
	public KeyDefImpl(java.lang.Object[][] inInitialValues) {
		super(inInitialValues);
	}
	
	/**
	 * 	Inserts a Property named inKeyName at the specified position
	 *  to the PropertySet.
	 * 
	 * 	@param inKeyName java.lang.String
	 * 	@param inPosition int
	 */
	@SuppressWarnings("unchecked")
	public void addKeyNameAt(String inKeyName, int inPosition) {		
		try { 	
			Vector<String> lKeyItems = (Vector<String>)this.get(KeyDefDef.keyItems);
			if (lKeyItems == null) {
				lKeyItems = new Vector<String>();
				this.set(KeyDefDef.keyItems, lKeyItems);
			}
	   		lKeyItems.ensureCapacity(inPosition);
			lKeyItems.insertElementAt(inKeyName, inPosition);
		} 
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
		} // try/catch		
	}
	
	/**
	 * KeyDefs are equal if all of their keyPropertyNames are equal.
	 *
	 * @return boolean
	 */
	public boolean equals(Object inObject) {
		if (inObject == null) return false;
		if (!(inObject instanceof KeyDef)) return false;
	
		try {
			return ((Vector<?>)get(KeyDefDef.keyItems)).equals(((KeyDef)inObject).get(KeyDefDef.keyItems));
		}
		catch (GettingException exc) {
			return false;
		}
	}
	
	/**
	 * 	Returns the property name for the given sequence number.
	 *  Numbering starts with 0.
	 * 
	 * 	@return java.lang.String
	 * 	@param inSequenceNumber int
	 */
	public String getKeyName(int inSequenceNumber) {		
		String outKeyName = "";
		
		try { 	
			Vector<?> lKeyItems = (Vector<?>)this.get(KeyDefDef.keyItems);
			if (lKeyItems != null) {
		 		 outKeyName = (String) lKeyItems.elementAt(inSequenceNumber);
			}
		}
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);		
		} // try/catch
		
		return outKeyName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.model.KeyDef#getKeyNames()
	 */
	public Iterator<String> getKeyNames() {	
		return getKeyNames2().iterator();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.model.KeyDef#getKeyNames2()
	 */
	@SuppressWarnings("unchecked")
	public Vector<String> getKeyNames2() {
		Vector<String> outKeyNames = null;
		try { 	
			outKeyNames = (Vector<String>)this.get(KeyDefDef.keyItems);
			if (outKeyNames == null)
				outKeyNames = emptyVector;
		} 
		catch (Exception exc) {
			DefaultExceptionHandler.instance().handle(exc);
		}
		return outKeyNames;
	}
	
	/**
	 * @return org.hip.kernel.bom.model.MetaModelObject
	 */
	public MetaModelObject getMetaModelObject() {
		return MetaModelHome.singleton.getKeyDefDef();
	}
	
	/**
	 * Returns a hash code value for the key def.
	 *
	 * @return int
	 */
	public int hashCode() {
		int outCode = 1;
		try {
			outCode = ((Vector<?>)get(KeyDefDef.keyItems)).hashCode();
		}
		catch (GettingException exc) {
			//left blank intentionally
		}
		return outCode;
	}
	
	/**
	 * 	@return boolean
	 */
	public boolean isPrimaryKeyDef() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.impl.DomainObjectImpl#toString()
	 */
	public String toString() {
		StringBuffer lMessage = new StringBuffer("");
		for (Iterator<String> lKeyNames = getKeyNames(); lKeyNames.hasNext();) {
			lMessage.append("keyPropertyName=\"").append(lKeyNames.next()).append("\" ");
		}
		return Debug.classMarkupString(this, new String(lMessage));
	}
}
