/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2001, Benno Luthiger

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

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.ModifierStrategy;
import org.hip.kernel.bom.impl.PreparedUpdateStatement;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.workflow.WorkflowException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.ParticipantHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.exc.BOMChangeValueException;

/**
 * This domain object home implements the ParticipantHome interface.
 * 
 * Created on 01.11.2002
 * 
 * @author Benno Luthiger
 * @see org.hip.vif.core.bom.ParticipantHome
 */
@SuppressWarnings("serial")
public class ParticipantHomeImpl extends DomainObjectHomeImpl implements
		ParticipantHome {

	/*
	 * Every home has to know the class it handles. They provide access to this
	 * name through the method <I>getObjectClassName</I>;
	 */
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.ParticipantImpl";

	private final static String XML_OBJECT_DEF = "<?xml version='1.0' encoding='ISO-8859-1'?>	\n"
			+ "<objectDef objectName='GroupAdmin' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	\n"
			+ "	<keyDefs>	\n"
			+ "		<keyDef>	\n"
			+ "			<keyItemDef seq='0' keyPropertyName='"
			+ KEY_MEMBER_ID
			+ "'/>	\n"
			+ "			<keyItemDef seq='1' keyPropertyName='"
			+ KEY_GROUP_ID
			+ "'/>	\n"
			+ "		</keyDef>	\n"
			+ "	</keyDefs>	\n"
			+ "	<propertyDefs>	\n"
			+ "		<propertyDef propertyName='"
			+ KEY_MEMBER_ID
			+ "' valueType='Long' propertyType='simple'>	\n"
			+ "			<mappingDef tableName='tblParticipant' columnName='MemberID'/>	\n"
			+ "		</propertyDef>	\n"
			+ "		<propertyDef propertyName='"
			+ KEY_GROUP_ID
			+ "' valueType='Long' propertyType='simple'>	\n"
			+ "			<mappingDef tableName='tblParticipant' columnName='GroupID'/>	\n"
			+ "		</propertyDef>	\n"
			+ "		<propertyDef propertyName='"
			+ KEY_SUSPEND_FROM
			+ "' valueType='Timestamp' propertyType='simple'>	\n"
			+ "			<mappingDef tableName='tblParticipant' columnName='dtSuspendFrom'/>	\n"
			+ "		</propertyDef>	\n"
			+ "		<propertyDef propertyName='"
			+ KEY_SUSPEND_TO
			+ "' valueType='Timestamp' propertyType='simple'>	\n"
			+ "			<mappingDef tableName='tblParticipant' columnName='dtSuspendTo'/>	\n"
			+ "		</propertyDef>	\n" + "	</propertyDefs>	\n" + "</objectDef>";

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName()
	 */
	@Override
	public String getObjectClassName() {
		return OBJECT_CLASS_NAME;
	}

	/**
	 * @see org.hip.kernel.bom.impl.AbstractDomainObjectHome#getObjectDefString()
	 */
	@Override
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hip.vif.core.bom.ParticipantHome#saveRegisterings(java.lang.Long,
	 * java.util.Collection, java.lang.String)
	 */
	@Override
	public Collection<Long> saveRegisterings(final Long inActorID,
			final Collection<Long> inGroupIDs) throws BOMChangeValueException {
		final Collection<Long> outDeletedAdmins = new Vector<Long>();

		try {
			// Retrieve all public groups the actor is participating.
			KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(KEY_MEMBER_ID, inActorID);
			lKey.setValue(GroupHome.KEY_PRIVATE, GroupHome.IS_PUBLIC, "=",
					BinaryBooleanOperator.AND);
			lKey.setValue(createSubscribable(), BinaryBooleanOperator.AND);
			final QueryResult lExisting = BOMHelper
					.getJoinParticipantToGroupHome().select(lKey);
			while (lExisting.hasMoreElements()) {
				final Long lGroupID = new Long(lExisting.nextAsDomainObject()
						.get(KEY_GROUP_ID).toString());
				// Check whether the actor retains participation.
				if (inGroupIDs.contains(lGroupID)) {
					// If yes, remove groupID from collection.
					inGroupIDs.remove(lGroupID);
				} else {
					// If no, check whether the actor tried to unregister from a
					// group she's administering.
					if (BOMHelper.getGroupAdminHome().isGroupAdmin(inActorID,
							lGroupID)) {
						// If yes, skip.
						outDeletedAdmins.add(lGroupID);
					} else {
						// If no, remove entry and, thus, participation.
						lKey = new KeyObjectImpl();
						lKey.setValue(KEY_MEMBER_ID, inActorID);
						lKey.setValue(KEY_GROUP_ID, lGroupID);
						delete(lKey, true);
					}
				}
			}

			for (final Iterator<Long> lNewEntries = inGroupIDs.iterator(); lNewEntries
					.hasNext();) {
				create(inActorID, lNewEntries.next());
			}
			return outDeletedAdmins;
		}
		catch (final VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final WorkflowException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	private KeyObject createSubscribable() throws VException {
		final Integer[] lStates = VIFGroupWorkflow.ENLISTABLE_STATES;
		final KeyObject outKey = new KeyObjectImpl();
		for (int i = 0; i < lStates.length; i++) {
			outKey.setValue(GroupHome.KEY_STATE, lStates[i], "=",
					BinaryBooleanOperator.OR);
		}
		return outKey;
	}

	/**
	 * Returns an Collection of ids of discussion groups the specified user has
	 * registered for.
	 * 
	 * @param inActorID
	 *            java.lang.Long The unique identification of the member.
	 * @return Collection<String> of ids of discussion groups the specified user
	 *         has registered for
	 * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	@Override
	public Collection<Long> getRegisterings(final Long inActorID)
			throws BOMChangeValueException {
		final Collection<Long> outGroupIDs = new ArrayList<Long>();
		try {
			final QueryResult lEntries = getGroups(inActorID);
			while (lEntries.hasMoreElements()) {
				final GeneralDomainObject lEntry = lEntries
						.nextAsDomainObject();
				outGroupIDs.add((Long) lEntry.get(KEY_GROUP_ID));
			}
		}
		catch (final VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		return outGroupIDs;
	}

	private QueryResult getGroups(final Long inActorID)
			throws BOMChangeValueException {
		try {
			final KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(KEY_MEMBER_ID, inActorID);
			return select(lKey);
		}
		catch (final VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	/**
	 * Makes the specified member an active participant in the specified group.
	 * 
	 * @param inMemberID
	 *            String
	 * @param inGroupID
	 *            String
	 * @return DomainObject
	 * @throws VException
	 * @throws WorkflowException
	 * @throws SQLException
	 */
	@Override
	public DomainObject create(final String inMemberID, final String inGroupID)
			throws VException, WorkflowException, SQLException {
		final Long lMemberID = new Long(inMemberID);
		final Long lGroupID = new Long(inGroupID);
		create(lMemberID, lGroupID);
		return find(lMemberID, lGroupID);
	}

	/**
	 * Makes the specified member an active participant in the specified group
	 * only if she is not registered yet.
	 * 
	 * @param inMemberID
	 *            String
	 * @param inGroupID
	 *            String
	 * @return boolean
	 * @throws VException
	 * @throws WorkflowException
	 * @throws SQLException
	 */
	@Override
	public boolean createChecked(final String inMemberID, final String inGroupID)
			throws VException, WorkflowException, SQLException {
		return createChecked(new Long(inMemberID), new Long(inGroupID));
	}

	@Override
	public boolean createChecked(final Long inMemberID, final Long inGroupID)
			throws VException, WorkflowException, SQLException {
		try {
			find(inMemberID, inGroupID);
			return false;
		}
		catch (final BOMNotFoundException exc) {
			create(inMemberID, inGroupID);
			return true;
		}
	}

	private void create(final Long inMemberID, final Long inGroupID)
			throws VException, WorkflowException, SQLException {
		final DomainObject lParticipant = create();
		lParticipant.set(KEY_MEMBER_ID, inMemberID);
		lParticipant.set(KEY_GROUP_ID, inGroupID);

		// on some MySQL systems, it seems that we need to have at least 1
		// second
		final Timestamp lInit = new Timestamp(1000);
		lParticipant.set(KEY_SUSPEND_FROM, lInit);
		lParticipant.set(KEY_SUSPEND_TO, lInit);
		lParticipant.insert(true);
		final GroupHome lGroupHome = (GroupHome) VSys.homeManager
				.getHome(GroupImpl.HOME_CLASS_NAME);
		lGroupHome.getGroup(inGroupID).checkActivationState(
				getParticipantsOfGroup(inGroupID));
	}

	private DomainObject find(final Long inMemberID, final Long inGroupID)
			throws VException, SQLException {
		final KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(ParticipantHome.KEY_MEMBER_ID, inMemberID);
		lKey.setValue(ParticipantHome.KEY_GROUP_ID, inGroupID);
		return findByKey(lKey);
	}

	/**
	 * Checks whether the specified actor is participant, i.e. is registered in
	 * at least one group.
	 * 
	 * @param inActorID
	 *            Long
	 * @return boolean, true if participant.
	 * @throws BOMChangeValueException
	 */
	@Override
	public boolean isParticipant(final Long inActorID)
			throws BOMChangeValueException {
		try {
			final KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(KEY_MEMBER_ID, inActorID);
			return getCount(lKey) > 0;
		}
		catch (final VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	/**
	 * Checks whether the specified actor is participant of the specified group.
	 * 
	 * @param inGroupID
	 *            Long
	 * @param inActorID
	 *            Long
	 * @return boolean True, if the actor is not participant of the specified
	 *         group.
	 * @throws BOMChangeValueException
	 */
	@Override
	public boolean isParticipantOfGroup(final Long inGroupID,
			final Long inActorID) throws BOMChangeValueException {
		try {
			final KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(KEY_GROUP_ID, inGroupID);
			lKey.setValue(KEY_MEMBER_ID, inActorID);
			return getCount(lKey) > 0;
		}
		catch (final VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	/**
	 * Returns the number of participants of the specified group.
	 * 
	 * @param inGroupID
	 *            Long
	 * @return int
	 * @throws BOMChangeValueException
	 */
	@Override
	public int getParticipantsOfGroup(final Long inGroupID)
			throws BOMChangeValueException {
		try {
			final KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(KEY_GROUP_ID, inGroupID);
			return getCount(lKey);
		}
		catch (final VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	/**
	 * Removes the specified participant from the specified group.
	 * 
	 * @param inGroupID
	 *            Long
	 * @param inMemberID
	 *            Long
	 * @throws BOMChangeValueException
	 */
	@Override
	public void removeParticipant(final Long inGroupID, final Long inMemberID)
			throws BOMChangeValueException {
		try {
			final KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(KEY_GROUP_ID, inGroupID);
			lKey.setValue(KEY_MEMBER_ID, inMemberID);
			delete(lKey, true);
		}
		catch (final VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	/**
	 * Suspends the specified actor's participations in his or her discussion
	 * groups for the specified time period.
	 * 
	 * @param inActorID
	 *            Long
	 * @param inFrom
	 *            Timestamp
	 * @param inTo
	 *            Timestamp
	 * @throws BOMChangeValueException
	 */
	@Override
	public void suspendParticipation(final Long inActorID,
			final Timestamp inFrom, final Timestamp inTo)
			throws BOMChangeValueException {
		try {
			final KeyObject lChange = new KeyObjectImpl();
			lChange.setValue(ParticipantHome.KEY_SUSPEND_FROM, inFrom);
			lChange.setValue(ParticipantHome.KEY_SUSPEND_TO, inTo);

			final KeyObject lWhere = new KeyObjectImpl();
			lWhere.setValue(ParticipantHome.KEY_MEMBER_ID, inActorID);

			final PreparedUpdateStatement lStatement = new PreparedUpdateStatement(
					this, lChange, lWhere);
			lStatement.executeUpdate();
			lStatement.commit();
		}
		catch (final VException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
		catch (final SQLException exc) {
			throw new BOMChangeValueException(exc.getMessage());
		}
	}

	/**
	 * Get the specified actor's suspend from and suspend to dates. The method
	 * returns a Collection containing the maximum date for SuspendFrom and
	 * SuspendTo.
	 * 
	 * @param ininActorID
	 *            Long
	 * @return Collection
	 * @throws VException
	 * @throws SQLException
	 */
	@Override
	public Collection<Object> getActorSuspend(final Long inActorID)
			throws VException, SQLException {
		final KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(ParticipantHome.KEY_MEMBER_ID, inActorID);

		final ModifierStrategy lStrategy = new ModifierStrategy(new String[] {
				ParticipantHome.KEY_SUSPEND_FROM,
				ParticipantHome.KEY_SUSPEND_TO }, ModifierStrategy.MAX);
		return getModified(lStrategy, lKey);
	}

}
