/*
	This package is part of application VIF.
	Copyright (C) 2003, Benno Luthiger

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
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.JoinedDomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.impl.JoinParticipantToMember;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.exc.NoReviewerException;
import org.hip.vif.core.interfaces.IReviewable;

/**
 * Home of join from the Participant BOM to the Member BOM. This join can be
 * used to retrieve the member data of all participants for a specified
 * discussion group.
 * 
 * Created on 14.08.2003
 * 
 * @author Benno Luthiger
 */
@SuppressWarnings("serial")
public class JoinParticipantToMemberHome extends JoinedDomainObjectHomeImpl {
	private static final int THRESHOLD = 50;

	// Every home has to know the class it handles. They provide access to
	// this name through the method <I>getObjectClassName</I>;
	private final static String JOIN_OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.JoinParticipantToMember";

	private final static String XML_OBJECT_DEF = "<?xml version='1.0' encoding='ISO-8859-1'?>	\n"
			+ "<joinedObjectDef objectName='JoinParticipantToMember' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n"
			+ "	<columnDefs>	\n" + "		<columnDef columnName='"
			+ ParticipantHome.KEY_GROUP_ID
			+ "' domainObject='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n"
			+ "		<columnDef columnName='"
			+ ParticipantHome.KEY_SUSPEND_FROM
			+ "' domainObject='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n"
			+ "		<columnDef columnName='"
			+ ParticipantHome.KEY_SUSPEND_TO
			+ "' domainObject='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n"
			+ "		<columnDef columnName='"
			+ MemberHome.KEY_ID
			+ "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n"
			+ "		<columnDef columnName='"
			+ MemberHome.KEY_USER_ID
			+ "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n"
			+ "		<columnDef columnName='"
			+ MemberHome.KEY_NAME
			+ "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n"
			+ "		<columnDef columnName='"
			+ MemberHome.KEY_FIRSTNAME
			+ "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n"
			+ "		<columnDef columnName='"
			+ MemberHome.KEY_MAIL
			+ "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n"
			+ "		<columnDef columnName='"
			+ MemberHome.KEY_SEX
			+ "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n"
			+ "	</columnDefs>	\n"
			+ "	<joinDef joinType='EQUI_JOIN'>	\n"
			+ "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n"
			+ "		<objectDesc objectClassName='org.hip.vif.core.bom.impl.MemberImpl'/>	\n"
			+ "		<joinCondition>	\n"
			+ "			<columnDef columnName='"
			+ ParticipantHome.KEY_MEMBER_ID
			+ "' domainObject='org.hip.vif.core.bom.impl.ParticipantImpl'/>	\n"
			+ "			<columnDef columnName='"
			+ MemberHome.KEY_ID
			+ "' domainObject='org.hip.vif.core.bom.impl.MemberImpl'/>	\n"
			+ "		</joinCondition>	\n" + "	</joinDef>	\n" + "</joinedObjectDef>";

	/**
	 * Constructor for JoinParticipantToMemberHome.
	 */
	public JoinParticipantToMemberHome() {
		super();
	}

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName()
	 */
	@Override
	public String getObjectClassName() {
		return JOIN_OBJECT_CLASS_NAME;
	}

	/**
	 * @see org.hip.kernel.bom.impl.AbstractDomainObjectHome#getObjectDefString()
	 */
	@Override
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}

	/**
	 * Returns the participants for the specified group.
	 * 
	 * @param inGroupID
	 *            java.lang.Long
	 * @return org.hip.kernel.bom.QueryResult
	 * @throws org.hip.vif.core.exc.bom.impl.BOMChangeValueException
	 */
	public QueryResult select(final Long inGroupID)
			throws BOMChangeValueException {
		try {
			final KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(GroupAdminHome.KEY_GROUP_ID, inGroupID);
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
	 * Returns the active participants of the specified group.
	 * 
	 * @param inGroupID
	 *            BigDecimal
	 * @return QueryResult
	 * @throws VException
	 * @throws SQLException
	 */
	public QueryResult selectActive(final Long inGroupID) throws VException,
			SQLException {
		final KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(ParticipantHome.KEY_GROUP_ID, inGroupID);
		lKey.setValue(getKeyActive());
		return select(lKey);
	}

	/**
	 * Returns a collection filled with the active participants of the specified
	 * group.
	 * 
	 * @param inGroupID
	 * @return List of JoinParticipantToMember objects
	 * @throws VException
	 * @throws SQLException
	 * @see JoinParticipantToMember
	 */
	public List<VIFMember> getActive(final Long inGroupID) throws VException,
			SQLException {
		return fillCollection(selectActive(inGroupID));
	}

	private List<VIFMember> fillCollection(final QueryResult inResult)
			throws VException, SQLException {
		final List<VIFMember> outList = new Vector<VIFMember>();
		while (inResult.hasMoreElements()) {
			outList.add((VIFMember) inResult.nextAsDomainObject());
		}
		return outList;
	}

	private KeyObject getKeyActive() throws VException {
		final Timestamp lNow = new Timestamp(System.currentTimeMillis());
		final KeyObject outKey = new KeyObjectImpl();
		outKey.setValue(ParticipantHome.KEY_SUSPEND_FROM, lNow, ">",
				BinaryBooleanOperator.OR);
		outKey.setValue(ParticipantHome.KEY_SUSPEND_TO, lNow, "<",
				BinaryBooleanOperator.OR);
		return outKey;
	}

	/**
	 * Returns a Collection of the mail addresses of the specified group's
	 * participants.
	 * 
	 * @param inGroupID
	 *            Long
	 * @return Collection<String>
	 * @throws VException
	 * @throws SQLException
	 */
	public Collection<String> getParticipantsMail(final Long inGroupID)
			throws VException, SQLException {
		final QueryResult lParticipants = select(inGroupID);
		final Collection<String> outMails = new Vector<String>();
		while (lParticipants.hasMoreElements()) {
			outMails.add(lParticipants.nextAsDomainObject()
					.get(MemberHome.KEY_MAIL).toString());
		}
		return outMails;
	}

	/**
	 * Randomly chooses a participant that can be assigned the reviewer task for
	 * the specified contributions.<br/>
	 * This method checks the availability of the reviewer. The check consists
	 * of the following prerequisites:
	 * <ul>
	 * <li>the chosen participant is not the contributions' author</li>
	 * <li>the chosen participant did not formerly refuse to review one of the
	 * contributions</li>
	 * </ul>
	 * If the method is not able to assign a participant for the reviewer task,
	 * a <code>NoReviewerException</code> is thrown.
	 * 
	 * @param inGroupID
	 *            Long the group the prospective reviewer is participating in
	 * @param inAuthorID
	 *            Long the contributions' author
	 * @param inContributions
	 *            Collection<IReviewable> the contribution to review
	 * @return {@link VIFMember}
	 * @throws VException
	 * @throws SQLException
	 * @throws NoReviewerException
	 * @see NoReviewerException
	 */
	public VIFMember getRandomParticipant(final Long inGroupID,
			final Long inAuthorID, final Collection<IReviewable> inContributions)
			throws VException, SQLException, NoReviewerException {
		final KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(ParticipantHome.KEY_GROUP_ID, inGroupID);
		lKey.setValue(getKeyActive());

		final int lNumberOfParticipants = getCount(lKey);
		if (lNumberOfParticipants < THRESHOLD) {
			return randomIteration(fillCollection(select(lKey)), inAuthorID,
					inContributions, inGroupID);
		}
		return randomIteration(lKey, lNumberOfParticipants, inAuthorID,
				inContributions, inGroupID);
	}

	/**
	 * If the number of participants exceeds <code>THRESHOLD</code>, the cursor
	 * is run to a random position in a <code>QueryResult</code> of potential
	 * participants. If the participant at the cursor position is not available,
	 * the participant at the following position is checked. This process is
	 * repeated until the end of the result set is reached and then started at
	 * the beginning of the set until the original cursor position. If all tries
	 * didn't succeed, a <code>NoReviewerException</code> is thrown.
	 * 
	 * @param inKey
	 * @param inNumberOfParticipants
	 * @param inAuthorID
	 * @param inContributions
	 * @param inGroupID
	 * @return
	 * @throws VException
	 * @throws SQLException
	 * @throws NoReviewerException
	 */
	protected VIFMember randomIteration(final KeyObject inKey,
			final int inNumberOfParticipants, final Long inAuthorID,
			final Collection<IReviewable> inContributions, final Long inGroupID)
			throws VException, SQLException, NoReviewerException {
		final Random lRandom = new Random(System.currentTimeMillis());
		int lPosition = lRandom.nextInt(inNumberOfParticipants);
		final int lRange = lPosition;

		QueryResult lParticipants = select(inKey);
		int i = 0;
		while (lParticipants.hasMoreElements()) {
			final VIFMember outParticipant = (VIFMember) lParticipants.next();
			if (i++ == lPosition) {
				if (checkAvailability(outParticipant, inAuthorID,
						inContributions)) {
					lParticipants.close();
					return outParticipant;
				}
				// just take the next
				lPosition++;
			}
		}
		lParticipants.close();

		// if no suitable found, try again from the beginning
		lParticipants = select(inKey);
		for (int j = 0; j < lRange; j++) {
			final VIFMember outParticipant = (VIFMember) lParticipants.next();
			if (checkAvailability(outParticipant, inAuthorID, inContributions)) {
				lParticipants.close();
				return outParticipant;
			}
		}
		lParticipants.close();
		throw new NoReviewerException(inGroupID);
	}

	/**
	 * If the number of participants is less the <code>THRESHOLD</code>, this
	 * method picks randomly a participant and checks his availability. If the
	 * check fails, the process is repeated x times, where x is the size of the
	 * given list of participants. After this number of unsuccessful tries, a
	 * <code>NoReviewerException</code> is thrown.
	 * 
	 * @param inParticipants
	 * @param inAuthorID
	 * @param inContributions
	 * @param inGroupID
	 * @return
	 * @throws VException
	 * @throws SQLException
	 * @throws NoReviewerException
	 */
	private VIFMember randomIteration(final List<VIFMember> inParticipants,
			final Long inAuthorID,
			final Collection<IReviewable> inContributions, final Long inGroupID)
			throws VException, SQLException, NoReviewerException {
		final Random lRandom = new Random(System.currentTimeMillis());
		final int lCount = inParticipants.size();
		for (int i = 0; i < lCount; i++) {
			final int lPosition = lRandom.nextInt(lCount);
			final VIFMember outParticipant = inParticipants.get(lPosition);
			if (checkAvailability(outParticipant, inAuthorID, inContributions)) {
				return outParticipant;
			}
		}
		throw new NoReviewerException(inGroupID);
	}

	/**
	 * @param inParticipant
	 * @param inAuthorID
	 * @param inContributions
	 * @return boolean <code>true</code> if the participant is available
	 * @throws VException
	 * @throws SQLException
	 */
	private boolean checkAvailability(final VIFMember inParticipant,
			final Long inAuthorID, final Collection<IReviewable> inContributions)
			throws VException, SQLException {
		if (inParticipant.isSameMember(inAuthorID))
			return false;
		final Long lReviewerID = inParticipant.getMemberID();
		for (final IReviewable lContribution : inContributions) {
			if (lContribution.checkRefused(lReviewerID)) {
				return false;
			}
		}
		return true;
	}

}
