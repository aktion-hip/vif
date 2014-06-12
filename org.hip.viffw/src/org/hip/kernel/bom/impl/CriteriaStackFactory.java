/*
	This package is part of the framework used for the application VIF.
	Copyright (C) 2006, Benno Luthiger

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

import org.hip.kernel.bom.ICriteriaStack;

/**
 * A simple factory to create instances of <code>ICriteriaStack</code>.
 *
 * @author Luthiger
 * Created on 13.07.2007
 */
public class CriteriaStackFactory {
	public enum StackType { SQL, LDAP, FLAT_JOIN };
	
	private final StackType stackType;
	private String join;
	
	public CriteriaStackFactory(StackType inStackType) {
		this(inStackType, null);
	}
	
	public CriteriaStackFactory(StackType inStackType, String inJoin) {		
		stackType = inStackType;
		join = inJoin;
	}
	
	public void setJoin(String inJoin) {
		join = inJoin;
	}
	
	public ICriteriaStack getCriteriaStack() {
		//default
		ICriteriaStack outStack = new SQLCriteriaStack();
		//else
		if (StackType.LDAP.equals(stackType)) {
			outStack = new LDAPCriteriaStack();
		}
		else if (StackType.FLAT_JOIN.equals(stackType)) {
			outStack = new FlatJoinCriteriaStack();
		}
		if (join != null && join.length() > 0) {
			outStack.setJoin(join);
		}
		return outStack;
	}

}
