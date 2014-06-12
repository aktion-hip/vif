/**
 This package is part of the proper forum application VIF.
 Copyright (C) 2004-2014, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.core.bom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;

import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.authorization.AbstractVIFAuthorization;
import org.hip.vif.core.authorization.IAuthorization;
import org.hip.vif.core.authorization.VIFDefaultAuthorization;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.member.IActor;

/**
 * Class storing all data relevant to describe the actor.
 * 
 * @author Benno Luthiger Created on Jul 15, 2004
 */
@SuppressWarnings("serial")
public class Actor implements IActor, Serializable {
	// Keyname of the members identification
	public static final String ACTOR_ID_KEY = "memberId";
	// Keyname of the userId
	public static final String USER_ID_KEY = "userId";
	private static final String AUTHORIZATION_KEY = "authorization";

	// Instance variables
	private Hashtable<String, Object> properties = null;

	/**
	 * Actor constructor.
	 * 
	 * @param inActorID
	 *            Long
	 * @param inUserID
	 *            String
	 * @throws BOMChangeValueException
	 */
	public Actor(final Long inActorID, final String inUserID)
			throws BOMChangeValueException {
		super();
		setActorID(inActorID);
		setUserID(inUserID);
		properties().put(AUTHORIZATION_KEY, new VIFAuthorization(inActorID));
	}

	/**
	 * Returns properties.
	 * 
	 * @return java.util.Hashtable Table of properties
	 */
	private Hashtable<String, Object> properties() {
		if (properties == null) {
			properties = new Hashtable<String, Object>();
		}
		return properties;
	}

	/**
	 * Returns the value with key <code>inName</code> if set in the context.
	 * Returns null if not found.
	 * 
	 * @param String
	 *            inName - Key of a value
	 * @return value as Object
	 */
	private Object get(final String inName) {
		return properties().get(inName);
	}

	/**
	 * Sets property <code>inValue</code> in context with key or name
	 * <code>inName</code>.
	 * 
	 * @param inName
	 *            java.lang.String key or name of property
	 * @param inValue
	 *            java.lang.Object value of property
	 */
	private void set(final String inName, final Object inValue) {
		properties().put(inName, inValue);
	}

	/**
	 * Returns the MemberID, i.e. the unique key of the Member table.
	 * 
	 * @return java.lang.Long The unique identification of the member
	 */
	@Override
	public Long getActorID() {
		final Long outActorID = (Long) get(ACTOR_ID_KEY);
		return (outActorID != null) ? outActorID : new Long(0);
	}

	/**
	 * Sets the MemberID, i.e. the unique key identifying the actor in the
	 * Member table.
	 * 
	 * @param inActorID
	 *            Long
	 */
	public void setActorID(final Long inActorID) {
		set(ACTOR_ID_KEY, inActorID);
	}

	/**
	 * Sets the user's login id.
	 * 
	 * @param inUserID
	 *            java.lang.String
	 */
	public void setUserID(final String inUserID) {
		set(USER_ID_KEY, inUserID);
	}

	/**
	 * Gets the user's login id.
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String getUserID() {
		final Object outUserID = get(USER_ID_KEY);
		return outUserID == null || "".equals(outUserID) ? getDftId()
				: outUserID.toString();
	}

	/**
	 * The default id, locale insensitive. Subclasses may override (e.g. using
	 * message key "org.hip.vif.core.guest").
	 * 
	 * @return
	 */
	protected String getDftId() {
		return ApplicationConstants.DFLT_USER_ID;
	}

	/**
	 * Returns the actor's authorization.
	 * 
	 * @return Authorization
	 */
	@Override
	public IAuthorization getAuthorization() {
		final Object outAuthorization = get(AUTHORIZATION_KEY);
		return (outAuthorization != null ? (IAuthorization) outAuthorization
				: new VIFDefaultAuthorization());
	}

	/**
	 * Checks whether the actor is participant of the specified group.
	 * 
	 * @param inGroupID
	 *            Long
	 * @return boolean
	 * @throws BOMChangeValueException
	 */
	@Override
	public boolean isRegistered(final Long inGroupID)
			throws BOMChangeValueException {
		if (inGroupID.longValue() == -1)
			return false;
		return BOMHelper.getParticipantHome().isParticipantOfGroup(inGroupID,
				getActorID());
	}

	/**
	 * Checks whether the actor is administering the specified group.
	 * 
	 * @param inGroupID
	 *            Long
	 * @return boolean
	 * @throws BOMChangeValueException
	 */
	@Override
	public boolean isGroupAdmin(final Long inGroupID)
			throws BOMChangeValueException {
		if (inGroupID.longValue() == -1)
			return false;
		return BOMHelper.getGroupAdminHome().isGroupAdmin(getActorID(),
				inGroupID);
	}

	/**
	 * Refreshes the actor's authorization, i.g. after she has registered and,
	 * therefore, became participant.
	 * 
	 * @throws BOMChangeValueException
	 */
	@Override
	public void refreshAuthorization() throws BOMChangeValueException {
		final Hashtable<String, Object> lProperties = properties();
		lProperties.remove(AUTHORIZATION_KEY);
		lProperties.put(AUTHORIZATION_KEY, new VIFAuthorization(getActorID()));
	}

	/**
	 * Checks whether this actor is guest or not.
	 * 
	 * @return boolean, true, if this actor is logged in as guest.
	 */
	@Override
	public boolean isGuest() {
		if (getActorID().longValue() == AbstractVIFAuthorization.GUEST_ID
				.longValue()) {
			return true;
		}
		return false;
	}

	private void writeObject(final ObjectOutputStream out) throws IOException {
		out.writeObject(properties);
	}

	@SuppressWarnings("unchecked")
	private void readObject(final ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		properties = (Hashtable<String, Object>) in.readObject();
	}

}
