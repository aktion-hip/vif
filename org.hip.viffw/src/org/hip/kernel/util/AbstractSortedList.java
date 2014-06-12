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

import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeSet;

import org.hip.kernel.sys.Assert;
import org.hip.kernel.sys.VObject;
import org.hip.kernel.sys.VSys;

/**
 * AbstractSortedList.java
 * 
 * Created on 13.09.2002
 * @author Benno Luthiger
 */
public abstract class AbstractSortedList extends VObject implements SortedList, Serializable {
	//Instance variables
	TreeSet<SortableItem> sortedList = null;
	
	/**
	 * Constructor for AbstractSortedList.
	 */
	public AbstractSortedList() {
		super();
	}

	/**
	 * @see org.hip.kernel.util.SortedList#accept(NameValueListVisitor)
	 */
	public void accept(NameValueListVisitor inListVisitor) {
		inListVisitor.visitSortedList(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.util.SortedList#getItems()
	 */
	public Iterator<SortableItem> getItems() {
		return sortedList().iterator();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.util.SortedList#getItems2()
	 */
	public TreeSet<SortableItem> getItems2() {
		return sortedList();
	}

	/**
	 * This method sets the value of the item with the specified sort position.
	 *
	 * @param inValue java.lang.Object
	 * @param inPosition int
	 * @throws org.hip.kernel.util.VInvalidSortCriteriaException
	 * @throws org.hip.kernel.util.VInvalidValueException
	 */
	public void setValue(Object inValue, int inPosition) throws VInvalidSortCriteriaException, VInvalidValueException {
		try {
			add(create(inValue, inPosition));
		}
		catch (VInvalidValueException exc) {
			throw exc;
		}
	}
	
	/**
	 * This method sets the value of the item with the specified sort criteria.
	 *
	 * @param inValue java.lang.Object
	 * @param inSortCriteria java.lang.Object
	 * @throws org.hip.kernel.util.VInvalidSortCriteriaException
	 * @throws org.hip.kernel.util.VInvalidValueException
	 */
	public void setValue(Object inValue, Object inSortCriteria) throws VInvalidSortCriteriaException, VInvalidValueException {
		// Pre: inSortCriteria not null
		if (VSys.assertNotNull(this, "setValue(Object inValue, Object inSortCriteria)", inSortCriteria) == Assert.FAILURE) {
			 return;
	 	}
	
		// Post: set
		try {
			add(create(inValue, inSortCriteria));
		}
		catch (VInvalidValueException exc) {
			throw exc;
		}
	}

	/**
	 * This method adds a new item to the sorted list.
	 * 
	 * @see org.hip.kernel.util.SortedList#add(SortableItem)
	 */
	protected void add(SortableItem inSortableItem) {
		// Pre: inSortableItem not null 
		if (VSys.assertNotNull(this, "add", inSortableItem) == Assert.FAILURE) {
			return;
		}
		// Post: Value set
		inSortableItem.setOwingList(this);
		sortedList().add(inSortableItem);
	}

	/**
	 * @see org.hip.kernel.util.SortedList#size()
	 */
	public int size() {
		return sortedList().size();
	}
	
	/**
	 * This method must be implemented by concrete subclasses.
	 * They are responsible to create instances of the right
	 * type.
	 *
	 * @return org.hip.kernel.util.SortableItem
	 * @param inValue java.lang.Object
	 * @param inPosition int
	 * @throws org.hip.kernel.util.VInvalidSortCriteriaException
	 * @throws org.hip.kernel.util.VInvalidValueException
	 */
	protected abstract SortableItem create(Object inValue, int inPosition) throws VInvalidSortCriteriaException, VInvalidValueException;
	
	/**
	 * This method must be implemented by concrete subclasses.
	 * They are responsible to create instances of the right
	 * type.
	 *
	 * @return org.hip.kernel.util.SortableItem
	 * @param inValue java.lang.Object
	 * @param inSortCriteria java.lang.Object
	 * @throws org.hip.kernel.util.VInvalidSortCriteriaException
	 * @throws org.hip.kernel.util.VInvalidValueException
	 */
	protected abstract SortableItem create(Object inValue, Object inSortCriteria) throws VInvalidSortCriteriaException, VInvalidValueException;
	
	private synchronized TreeSet<SortableItem> sortedList() {
		if (sortedList == null) {
			sortedList = new TreeSet<SortableItem>();
		}
		return sortedList;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		DefaultNameValueListVisitor lVisitor = new DefaultNameValueListVisitor();
		accept(lVisitor);
		return lVisitor.toString();
	}
}
