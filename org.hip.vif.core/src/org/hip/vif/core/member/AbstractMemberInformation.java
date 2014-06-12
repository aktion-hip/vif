/*
	This package is part of the application VIF.
	Copyright (C) 2007, Benno Luthiger

	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package org.hip.vif.core.member;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * Abstract base class for classes implementing the <code>MemberInformation</code> interface.
 *
 * @author Luthiger
 * 23.04.2007
 */
public abstract class AbstractMemberInformation {
	private Hashtable<String, Object> values = new Hashtable<String, Object>(17);
	
	/**
	 * Retrieves the set of stored entries.
	 * 
	 * @return Set<Map.Entry<String, Object>>
	 */
	protected Set<Map.Entry<String, Object>> entries() {
		return values.entrySet();
	}
	
	/**
	 * Sets the specified key - value pair to the store.
	 * 
	 * @param inKey String
	 * @param inValue Object
	 */
	protected void put(String inKey, Object inValue) {
		if (inValue == null) return;
		values.put(inKey, inValue);
	}
}
