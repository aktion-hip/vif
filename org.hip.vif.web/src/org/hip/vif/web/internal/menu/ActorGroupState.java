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
package org.hip.vif.web.internal.menu;

import java.util.Collection;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.member.IActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple parameter object to pass values down the menu hierarchy.<br/>
 * For group states see e.g. <code>VIFGroupWorkflow.STATE_ACTIVE</code>.
 *
 * @author Luthiger
 */
public class ActorGroupState {
	private static final Logger LOG = LoggerFactory.getLogger(ActorGroupState.class);
	
	//actor states
	public Collection<String> actorPermissions;
	public boolean isRegistered = false;
	public boolean isGroupAdmin = false;
	
	//group states
	public String groupState = ""; //$NON-NLS-1$
	public boolean isPrivateType = false;
	
	/**
	 * Factory method.
	 * 
	 * @param inGroupID Long
	 * @param inActor {@link IActor}
	 * @return {@link ActorGroupState} the initialized parameter object
	 */
	public static ActorGroupState getActorGroupState(Long inGroupID, IActor inActor) {
		ActorGroupState outState = new ActorGroupState();
		outState.actorPermissions = inActor.getAuthorization().getPermissions();
		try {
			outState.isRegistered = inActor.isRegistered(inGroupID);
			outState.isGroupAdmin = inActor.isGroupAdmin(inGroupID);
			
			if (inGroupID.intValue() <= 0) return outState;
			
			Group lGroup = ((GroupHome)BOMHelper.getGroupHome()).getGroup(inGroupID);
			outState.groupState = lGroup.get(GroupHome.KEY_STATE).toString();
			outState.isPrivateType = "1".equals(lGroup.get(GroupHome.KEY_PRIVATE).toString()); //$NON-NLS-1$
		} 
		catch (VException exc) {
			LOG.error("Error encountered while preparing the permissions of user with ID \"{}\" ({})!", new Object[] {inActor.getUserID(), inActor.getActorID()}, exc); //$NON-NLS-1$
		}
		return outState;
	}
	
}