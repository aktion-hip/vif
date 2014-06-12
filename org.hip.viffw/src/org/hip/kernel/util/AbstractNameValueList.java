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

package org.hip.kernel.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.sys.VObject;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.sys.Assert;

/**
 * This is the base implementation for a name value list.
 */
abstract public class AbstractNameValueList extends VObject implements NameValueList, Serializable {

	// Instance variables
	private Hashtable<String, NameValue> nameValues = null;
	
	/**
	 * AbstractNameValueList default constructor.
	 */
	public AbstractNameValueList() {
		super();
	}
	
	/**
	 * @param inVisitor org.hip.kernel.util.NameValueListVisitor
	 */
	public void accept(NameValueListVisitor inVisitor) {
		inVisitor.visitNameValueList(this);
	}
	
	/**
	 * This method adds a new item to the set.
	 *
	 * @param inNameValue org.hip.kernel.util.inNameValue
	 */
	public void add(NameValue inNameValue) {
	
		// Pre: inNameValue not null 
		if (VSys.assertNotNull(this, "add", inNameValue) == Assert.FAILURE) {
			return;
		}
		// Post: Value set
		inNameValue.setOwingList(this);
		nameValues().put(inNameValue.getName(), inNameValue);
	}
	
	/**
	 * This method must be implemented by concrete subclasses.
	 * They are responsible to create instances of the right
	 * type.
	 *
	 * @return org.hip.kernel.util.NameValue
	 * @param inName java.lang.String
	 * @param inValue java.lang.Object
	 * @exception org.hip.kernel.util.VInvalidNameException
	 * @exception org.hip.kernel.util.VInvalidValueException
	 */
	protected abstract NameValue create(String inName, Object inValue) throws VInvalidNameException, VInvalidValueException;

	/**
	 * @return boolean
	 */
	protected abstract boolean dynamicAddAllowed();
	
	/**
	 * Returns the item with the specified name.
	 *
	 * @return org.hip.kernel.util.NameValue
	 * @param inName java.lang.String
	 */
	public NameValue get(String inName) {
	
		// Pre1: name not null
		if (VSys.assertNotNull(this, "get", inName) == Assert.FAILURE)
			return null;
	
		// Post: 
		return nameValues().get(inName);
	}
	
	/**
	 * Returns an Iterator over the names.
	 * 
	 * @return java.util.Iterator<String>
	 * @deprecated Use {@link NameValueList#getNames2()} instead.
	 */
	public Iterator<String> getNames() {
		return getNames2().iterator();
	}
	
	/**
	 * Returns a Collection view of the names contained in this 
	 * <code>NameValueList</code> list.
	 * 
	 * @return Collection<String>
	 */
	public Collection<String> getNames2() {
		Collection<String> outNames = new Vector<String>();
		for (NameValue lNameValue : nameValues().values()) {
			outNames.add(lNameValue.getName());
		}
		return outNames;
	}
	
	/**
	 * Returns an iterator over the name/value-pairs.
	 *
	 * @return java.util.Enumeration
	 * @deprecated Use {@link NameValueList#getNameValues2()} instead.
	 */
	public Enumeration<NameValue> getNameValues() {
		return nameValues().elements();
	}
	
	/**
	 * Returns a Collection view of the <code>NameValue</code>s contained in this 
	 * <code>NameValueList</code> list.
	 * 
	 * @return Collection<NameValue>
	 */
	public Collection<NameValue> getNameValues2() {
		return nameValues().values();
	}
	
	/**
	 * Checks whether the list contains a value for the specified name.
	 * 
	 * @param inName String
	 * @return boolean
	 */
	public boolean hasValue(String inName) {
		return nameValues().containsKey(inName);
	}
	
	/**
	 * Setter vor nameValues hashtable.
	 * 
	 * @param inNameValues java.util.Hashtable
	 */
	protected void setNameValues(Hashtable<String, NameValue> inNameValues) {
		nameValues = inNameValues;
	}

	/**
	 * Returns the value for the given inName.
	 *
	 * @return java.lang.Object
	 * @param inName java.lang.String
	 * @exception org.hip.kernel.util.VInvalidNameException
	 */
	public Object getValue(String inName) throws VInvalidNameException {
	
		// Pre: inName not null
		if (VSys.assertNotNull(this, "getValue", inName) == Assert.FAILURE)
			return null;
	
		// Pre: list must contain the element
		if (!nameValues().containsKey(inName)) {
			 throw new VInvalidNameException(inName);
	
	 	}
		// Post: return value
		return nameValues().get(inName).getValue();
	}

	/**
	 * @return java.util.Hashtable
	 */
	private synchronized Hashtable<String, NameValue> nameValues() {
		if (nameValues == null)
			nameValues = new Hashtable<String, NameValue>(11);
	
		return nameValues;
	}

	/**
	 * This method sets the value of the item with the specified name.
	 *
	 * @param inName java.lang.String
	 * @param inValue java.lang.Object
	 * @exception org.hip.kernel.util.VInvalidNameException
	 * @exception org.hip.kernel.util.VInvalidValueException
	 */
	public void setValue(String inName, Object inValue) throws VInvalidNameException, VInvalidValueException {
	
		// Pre: inName not null
		if (VSys.assertNotNull(this, "setValue", inName) == Assert.FAILURE)
			return;
	
		// Post: set
		try { 
			if (nameValues().containsKey(inName)) {
			 	nameValues().get(inName).setValue(inValue);
			} 
			else {
				if (!dynamicAddAllowed()) {
					 throw new VInvalidNameException(inName);
				}
				NameValue lNameValue = create(inName, inValue);
				nameValues().put(lNameValue.getName(), lNameValue);
			}
		} 
		catch (VInvalidNameException exc) {
			throw exc;
		} 
		catch (VInvalidValueException exc) {
			throw exc;
		}
	}

	/**
	 * Returns count of nameValues added to this list.
	 * 
	 * @return int
	 */
	public int size() {
		return nameValues().size();
	}
	
	/**
	 * 	@return boolean
	 * 	@param inObject java.lang.Object
	 */
	public boolean equals( Object inObject ) {
		if (inObject == null) return false;
		if ( !( inObject instanceof NameValueList)) return false;
		
		if (size() != ((NameValueList)inObject).size()) return false;
		
		//check each NameValue in the list whether they are equal
		boolean isEqual = true;
		for (NameValue lNameValue : getNameValues2()) {
			isEqual = lNameValue.equals(((NameValueList)inObject).get(lNameValue.getName()));
			if (!isEqual) break;			
		}
		return isEqual;
	}

	/**
	 * @return java.lang.String
	 */
	public String toString() {
		StringBuffer lMarkups = new StringBuffer();
		for (NameValue lNameValue : getNameValues2()) {
			lMarkups.append("\t" + lNameValue.toString() + "\n");
		}
			
		return Debug.classMarkupString(this, "", new String(lMarkups));
	}

	/**
	 * Returns the HashCode calculated from the name.
	 * 
	 * @return int
	 */
	public int hashCode() {
		int outHashCode = 1;
		for (NameValue lNameValue : getNameValues2()) {
			outHashCode ^= lNameValue.hashCode();			
		}
		return outHashCode;
	}
}