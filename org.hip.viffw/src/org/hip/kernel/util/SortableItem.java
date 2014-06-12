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

/**
 * This is a generic interface for sortable values. 
 * This interface may be used to create a sorted list of elements,
 * e.g. a list of columnNames used for the SQL ORDER BY clause.
 * 
 * Created on 13.09.2002
 * @author Benno Luthiger
 */
public interface SortableItem extends Serializable {

	/**
	 * @param inNameValueListVisitor org.hip.kernel.util.NameValueListVisitor
	 */
	void accept(NameValueListVisitor inNameValueListVisitor);
	
	/**
	 * Returns the list the instance is member from.
	 *
	 * @return org.hip.kernel.util.SortedList
	 */
	SortedList getOwingList();
	
	/**
	 * Returns the value of the sorted item.
	 * 
	 * @return java.lang.Object
	 */
	Object getValue();
	
	/**
	 * @param inOwingList org.hip.kernel.util.SortedList
	 */
	void setOwingList(SortedList inOwingList);
}
