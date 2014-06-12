/*
	This package is part of the application VIF.
	Copyright (C) 2002, Benno Luthiger

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
package org.hip.vif.core.code;

import org.hip.kernel.code.AbstractCode;

/**
 * Represents code for the state of a discussion group
 * The possible states are:<br/>
 * <ul>
 * <li><code>created</code>: A group that is created but not yet open for registration (not yet visible for members).</li>
 * <li><code>open</code>: A that is open for registration but not yet active because of lack of participants (visible for members).</li>
 * <li><code>active</code>: An active group where participants can add contributions.</li>
 * <li><code>suspended</code>: An inactive group which can be activated again (publicly viewable).</li>
 * <li><code>settled</code>: A group which main question is ansered but which is publicly viewable.</li>
 * <li><code>closed</code>: A group that is closed by the group administrator, i.e. not more publicly viewable.</li>
 * </ul>
 * 
 * Created on 23.07.2002
 * @author Benno Luthiger
 */
public class GroupState extends AbstractCode {
	//constants
	public static final String CODEID = "GROUPSTATES";

	/**
	 * Constructor for GroupState.
	 */
	public GroupState() {
		this("");
	}

	/**
	 * Constructor for GroupState.
	 * @param inElementID java.lang.String
	 */
	public GroupState(String inElementID) {
		super(CODEID);
		setElementID(inElementID);
	}

}
