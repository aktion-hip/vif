/**
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2001-2014, Benno Luthiger

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

package org.hip.vif.core.bom;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.DomainObjectHome;
import org.hip.kernel.exc.VException;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.exc.BOMChangeValueException;

/** ParticipantHome is responsible to manage instances of class org.hip.vif.bom.Participant
 *
 * Created on 01.11.2002
 * 
 * @author Benno Luthiger
 * @see org.hip.vif.core.bom.Participant */
public interface ParticipantHome extends DomainObjectHome {
    public final static String KEY_MEMBER_ID = "MemberID";
    public final static String KEY_GROUP_ID = "GroupID";
    public final static String KEY_SUSPEND_FROM = "SuspendFrom";
    public final static String KEY_SUSPEND_TO = "SuspendTo";

    /** Modifies the participation settings of the specified user.
     * 
     * @param inActorID java.lang.Long The unique identification of the member.
     * @param inGroupIDs Collection<String> group IDs of discussion groups the member wants to participate.
     * @return Collection<Long> the groups the user tried to unregister but she's not allowed to do this because she's
     *         administering the group
     * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException */
    Collection<Long> saveRegisterings(Long inActorID, Collection<Long> inGroupIDs) throws BOMChangeValueException;

    /** Returns an array of ids of discussion groups the specified user has registered for.
     * 
     * @param inActorID java.lang.Long The unique identification of the member.
     * @return Collection<Long>
     * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException */
    Collection<Long> getRegisterings(Long inActorID) throws BOMChangeValueException;

    /** Makes the specified member an active participant in the specified group.
     * 
     * @param inMemberID String
     * @param inGroupID String
     * @return DomainObject
     * @throws VException
     * @throws WorkflowException
     * @throws SQLException */
    DomainObject create(String inMemberID, String inGroupID) throws VException, WorkflowException, SQLException;

    /** Makes the specified member an active participant in the specified group only if she is not registered yet.
     * 
     * @param inMemberID String
     * @param inGroupID String
     * @return boolean <code>true</code> if the participant is created new, <code>false</code> if she existed yet
     * @throws VException
     * @throws WorkflowException
     * @throws SQLException */
    boolean createChecked(String inMemberID, String inGroupID) throws VException, WorkflowException, SQLException;

    /** Makes the specified member an active participant in the specified group only if she is not registered yet.
     * 
     * @param inMemberID Long
     * @param inGroupID Long
     * @return boolean <code>true</code> if the participant is created new, <code>false</code> if she existed yet
     * @throws VException
     * @throws WorkflowException
     * @throws SQLException */
    boolean createChecked(Long inMemberID, Long inGroupID) throws VException, WorkflowException, SQLException;

    /** Checks whether the specified actor is participant, i.e. is registered in at least one group.
     * 
     * @param inActorID Long
     * @return boolean, true if participant.
     * @throws BOMChangeValueException */
    boolean isParticipant(Long inActorID) throws BOMChangeValueException;

    /** Checks whether the specified actor is participant of the specified group.
     * 
     * @param inGroupID Long
     * @param inActorID Long
     * @return boolean True, if the actor is not participant of the specified group.
     * @throws BOMChangeValueException */
    boolean isParticipantOfGroup(Long inGroupID, Long inActorID) throws BOMChangeValueException;

    /** Returns the number of participants of the specified group.
     * 
     * @param inGroupID Long
     * @return int
     * @throws BOMChangeValueException */
    int getParticipantsOfGroup(Long inGroupID) throws BOMChangeValueException;

    /** Removes the specified participant from the specified group.
     * 
     * @param inGroupID Long
     * @param inMemberID Long
     * @throws BOMChangeValueException */
    void removeParticipant(Long inGroupID, Long inMemberID) throws BOMChangeValueException;

    /** Supspends the specified actor's participations in his or her discussion groups for the specified time period.
     * 
     * @param inActorID Long
     * @param inFrom Timestamp
     * @param inTo Timestamp
     * @throws BOMChangeValueException */
    void suspendParticipation(Long inActorID, Timestamp inFrom, Timestamp inTo) throws BOMChangeValueException;

    /** Get the specified actor's suspend from and suspend to dates. The method returns a Collection containing the
     * maximum date for SuspendFrom and SuspendTo.
     * 
     * @param ininActorID Long
     * @return Collection
     * @throws VException
     * @throws SQLException */
    Collection<Object> getActorSuspend(Long inActorID) throws VException, SQLException;

}
