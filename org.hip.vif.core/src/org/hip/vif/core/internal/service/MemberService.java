/**
	This package is part of the application VIF.
	Copyright (C) 2011-2014, Benno Luthiger

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
package org.hip.vif.core.internal.service;

import org.hip.vif.core.interfaces.IActorManager;
import org.hip.vif.core.interfaces.IAuthenticatorContribution;
import org.hip.vif.core.interfaces.IMemberSearcherContribution;
import org.hip.vif.core.service.MemberUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service component for the <code>IAuthenticatorContribution</code>,
 * <code>IMemberSearcherContribution</code> and <code>IActorManager</code>
 * service.
 * 
 * @see IMemberSearcherContribution
 * @see IAuthenticatorContribution
 * @see IActorManager
 * @author Luthiger Created: 13.05.2011
 */
public class MemberService {
	private static final Logger LOG = LoggerFactory
			.getLogger(MemberService.class);

	public void bindAuthenticator(
			final IAuthenticatorContribution inAuthenticator) {
		LOG.debug("Binding authenticator provided by '{}'.",
				inAuthenticator.getBundleName());
		MemberUtility.INSTANCE.add(inAuthenticator);
	}

	public void unbindAuthenticator(
			final IAuthenticatorContribution inAuthenticator) {
		LOG.debug("Unbinding authenticator from '{}'.",
				inAuthenticator.getBundleName());
		MemberUtility.INSTANCE.remove(inAuthenticator);
	}

	public void bindSearcher(final IMemberSearcherContribution inSearcher) {
		LOG.debug("Binding member searcher provided by '{}'.",
				inSearcher.getBundleName());
		MemberUtility.INSTANCE.add(inSearcher);
	}

	public void unbindSearcher(final IMemberSearcherContribution inSearcher) {
		LOG.debug("Unbinding member searcher from '{}'.",
				inSearcher.getBundleName());
		MemberUtility.INSTANCE.remove(inSearcher);
	}

	public void setActorManager(final IActorManager inActorManager) {
		LOG.debug("Registering actor manager!");
		MemberUtility.INSTANCE.setActorManager(inActorManager);
	}

	public void removeActorManager(final IActorManager inActorManager) {
		LOG.debug("Removing actor manager!");
		MemberUtility.INSTANCE.removeActorManager(inActorManager);
	}

}
