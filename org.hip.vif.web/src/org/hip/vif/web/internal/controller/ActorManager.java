/**
	This package is part of the application VIF.
	Copyright (C) 2012-2014, Benno Luthiger

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
package org.hip.vif.web.internal.controller;

import java.sql.SQLException;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.interfaces.IActorManager;
import org.hip.vif.core.member.IActor;
import org.hip.vif.web.bom.Actor;
import org.hip.vif.web.util.RoleHelper;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.VaadinSession;

/**
 * The VIF implementation of the <code>IActorManager</code>. The actor object is
 * created and set into the Vaadin context.
 * 
 * @author lbenno
 */
public class ActorManager implements IActorManager {
	private static final Logger LOG = LoggerFactory
			.getLogger(ActorManager.class);

	private UserAdmin userAdmin;

	@Override
	public void setActorToContext(final Long inMemberID, final String inUserID)
			throws VException {
		final IActor lActor = new Actor(inMemberID, inUserID);
		try {
			VaadinSession.getCurrent().getLockInstance().lock();
			VaadinSession.getCurrent().setAttribute(IActor.class, lActor);
		} finally {
			VaadinSession.getCurrent().getLockInstance().unlock();
		}
		try {
			RoleHelper.mapUserToRole(lActor, userAdmin);
		}
		catch (final SQLException exc) {
			LOG.error(
					"An error encountered while creating the OSGi user object!",
					exc);
		}
	}

	/**
	 * Bind the OSGi user admin object.
	 * 
	 * @param inUserAdmin
	 *            {@link UserAdmin}
	 */
	public void setUserAdmin(final UserAdmin inUserAdmin) {
		userAdmin = inUserAdmin;
	}

	/**
	 * Unbind the OSGi user admin object.
	 * 
	 * @param inUserAdmin
	 *            {@link UserAdmin}
	 */
	public void unsetUserAdmin(final UserAdmin inUserAdmin) {
		userAdmin = null;
	}

}
