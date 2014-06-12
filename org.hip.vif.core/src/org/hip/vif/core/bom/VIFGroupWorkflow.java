/**
	This package is part of the application VIF.
	Copyright (C) 2004-2014, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public
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
package org.hip.vif.core.bom;

/**
 * This interface describes VIF specific workflow constants for discussion
 * groups.
 * 
 * @author Benno Luthiger Created on Jan 3, 2004
 */
public interface VIFGroupWorkflow {
	// constants
	public final static int S_CREATED = 1;
	public final static int S_OPEN = 2;
	public final static int S_ACTIVE = 3;
	public final static int S_SUSPENDED = 4;
	public final static int S_SETTLED = 5;
	public final static int S_CLOSED = 6;

	public final static String STATE_CREATED = String.valueOf(S_CREATED);
	public final static String STATE_OPEN = String.valueOf(S_OPEN);
	public final static String STATE_ACTIVE = String.valueOf(S_ACTIVE);
	public final static String STATE_SUSPENDED = String.valueOf(S_SUSPENDED);
	public final static String STATE_SETTLED = String.valueOf(S_SETTLED);
	public final static String STATE_CLOSED = String.valueOf(S_CLOSED);

	public final static String TRANS_OPEN = "Open";
	public final static String TRANS_ACTIVATE = "Activate";
	public final static String TRANS_DEACTIVATE = "Deactivate";
	public final static String TRANS_SUSPEND = "Suspend";
	public final static String TRANS_SETTLE = "Settle";
	public final static String TRANS_CLOSE = "Close";
	public final static String TRANS_CLOSE1 = "Close1";
	public final static String TRANS_CLOSE2 = "Close2";
	public final static String TRANS_CLOSE3 = "Close3";
	public final static String TRANS_REACTIVATE = "Reactivate";
	public final static String TRANS_REACTIVATE1 = "Reactivate1";
	public final static String TRANS_REACTIVATE2 = "Reactivate2";
	public final static String TRANS_REACTIVATE3 = "Reactivate3";
	public final static String TRANS_REOPEN = "Reopen";

	public final static Integer[] VISIBLE_STATES = { S_OPEN, S_ACTIVE,
			S_SUSPENDED, S_SETTLED };
	public final static Integer[] ENLISTABLE_STATES = { S_OPEN, S_ACTIVE };
}
