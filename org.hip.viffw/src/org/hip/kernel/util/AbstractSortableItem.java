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

import org.hip.kernel.sys.VObject;

/**
 * This is the base implementation of the sortabel item support.
 * 
 * Created on 13.09.2002
 * @author Benno Luthiger
 */
public abstract class AbstractSortableItem extends VObject implements SortableItem, Comparable<SortableItem> {
	// Instance variables
	private SortedList	owingList	= null;
	private Object		value		= null;

	/**
	 * Constructor for AbstractSortableItem.
	 */
	public AbstractSortableItem() {
		super();
	}

	/**
	 * @see org.hip.kernel.util.SortableItem#accept(NameValueListVisitor)
	 */
	public void accept(NameValueListVisitor inListVisitor) {
		inListVisitor.visitSortableItem(this);
	}

	/**
	 * @see org.hip.kernel.util.SortableItem#getOwingList()
	 */
	public SortedList getOwingList() {
		return owingList;
	}

	/**
	 * @see org.hip.kernel.util.SortableItem#getValue()
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @see org.hip.kernel.util.SortableItem#setOwingList(SortedList)
	 */
	public void setOwingList(SortedList inOwingList) {
		owingList = inOwingList;
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
	 * @param inValue java.lang.Object
	 * @exception org.hip.kernel.util.VInvalidValueException
	 */
	protected abstract void checkValue(Object inValue) throws VInvalidValueException ;
	
	/**
	 * Returns true if inObject is a <code>SortableItem</code> and its
	 * value is equal to this SortableItem's value.
	 * 
	 * @return boolean
	 * @param inObject java.lang.Object
	 */
	public boolean equals(Object inObject) {
	
		if (inObject == null) return false;
		if (!(inObject instanceof SortableItem)) return false;
		
		SortableItem lSortableItem = (SortableItem)inObject;
		return getValue().equals(lSortableItem.getValue());
	}	
	/**
	 * @return java.lang.String
	 */
	public String toString() {
		String lMessage = "value=\"" + ((getValue() != null) ? getValue().toString() : "null" ) + "\"";
		return Debug.classMarkupString(this, lMessage);
	}
	
	/**
	 * Returns the HashCode calculated from the value.
	 * 
	 * @return int
	 */
	public int hashCode() {
		return (getValue() == null ? 0 : getValue().hashCode());
	}
}
