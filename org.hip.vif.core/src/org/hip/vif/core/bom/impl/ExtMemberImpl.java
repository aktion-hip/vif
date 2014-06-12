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
package org.hip.vif.core.bom.impl;


/**
 * Version of <code>Member</code> object retrieved from an external member database.
 *
 * @author Luthiger
 * 21.04.2007
 */
@SuppressWarnings("serial")
public class ExtMemberImpl extends MemberImpl {
	public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.ExtMemberHomeImpl";
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.core.bom.impl.MemberImpl#getHomeClassName()
	 */
	public String getHomeClassName() {
		return HOME_CLASS_NAME;
	}
	
}
