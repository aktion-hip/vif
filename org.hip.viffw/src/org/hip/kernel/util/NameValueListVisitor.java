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

/**
 * This is the interface for a visitor pattern to a name value list.
 *
 * @author Benno Luthiger
 */
public interface NameValueListVisitor extends Visitor {
	/**
	 * @param inNameValue org.hip.kernel.util.NameValue
	 */
	void visitNameValue(NameValue inNameValue);

	/**
	 * @param inNameValueList org.hip.kernel.util.NameValueList
	 */
	void visitNameValueList(NameValueList inNameValueList);
	
	/**
	 * @param inSortableItem org.hip.kernel.util.SortableItem
	 */
	void visitSortableItem(SortableItem inSortableItem);
	
	/**
	 * @param inSortedList org.hip.kernel.util.SortedList
	 */
	void visitSortedList(SortedList inSortedList);
	
	/**
	 * Clears the Visitor
	 */
	void clear();
}
