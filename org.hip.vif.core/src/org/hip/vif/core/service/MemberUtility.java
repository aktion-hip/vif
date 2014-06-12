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
package org.hip.vif.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.hip.vif.core.ApplicationConstants;
import org.hip.vif.core.interfaces.IActorManager;
import org.hip.vif.core.interfaces.IAuthenticatorContribution;
import org.hip.vif.core.interfaces.IMemberSearcherContribution;
import org.hip.vif.core.member.IAuthenticator;
import org.hip.vif.core.member.IMemberSearcher;

/**
 * Singleton object to manage the <code>IMemberSearcherContribution</code> (i.e.
 * searcher) and <code>IAuthenticatorContribution</code> contributions.
 * 
 * @author Luthiger Created: 13.05.2011
 */
public enum MemberUtility {
	INSTANCE;

	private final Collection<IMemberSearcherContribution> availableSearchers = new Vector<IMemberSearcherContribution>();
	private final Collection<IAuthenticatorContribution> availableAuthenticators = new Vector<IAuthenticatorContribution>();
	private IActorManager actorManager;

	/**
	 * Register member search component.
	 * 
	 * @param inSearcherContribution
	 *            {@link IMemberSearcherContribution}
	 */
	public void add(final IMemberSearcherContribution inSearcherContribution) {
		availableSearchers.add(inSearcherContribution);
	}

	/**
	 * Unregister member search component.
	 * 
	 * @param inSearcherContribution
	 *            {@link IMemberSearcherContribution}
	 */
	public void remove(final IMemberSearcherContribution inSearcherContribution) {
		availableSearchers.remove(inSearcherContribution);
	}

	/**
	 * Register authentication component.
	 * 
	 * @param inAuthenticator
	 *            {@link IAuthenticatorContribution}
	 */
	public void add(final IAuthenticatorContribution inAuthenticator) {
		availableAuthenticators.add(inAuthenticator);
	}

	/**
	 * Unregister authentication component.
	 * 
	 * @param inAuthenticator
	 *            {@link IAuthenticatorContribution}
	 */
	public void remove(final IAuthenticatorContribution inAuthenticator) {
		availableAuthenticators.remove(inAuthenticator);
	}

	/**
	 * Registers IActorManager instance.
	 * 
	 * @param inActorManager
	 *            {@link IActorManager}
	 */
	public void setActorManager(final IActorManager inActorManager) {
		actorManager = inActorManager;
	}

	/**
	 * Unregisters IActorManager instance.
	 * 
	 * @param inActorManager
	 *            {@link IActorManager}
	 */
	public void removeActorManager(final IActorManager inActorManager) {
		actorManager = null;
	}

	/**
	 * Retrieves the registered <code>IActorManager</code> instance.
	 * 
	 * @return {@link IActorManager}, may be <code>null</code>
	 */
	public IActorManager getActorManager() {
		return actorManager;
	}

	/**
	 * Retrieve the actual member searcher.
	 * 
	 * @return {@link IMemberSearcher}
	 */
	public IMemberSearcher getActiveMemberSearcher() {
		if (availableSearchers.isEmpty())
			return null;

		final String lMemberSearcherName = getContributionName(PreferencesHandler.KEY_MEMBER_SEARCHER);
		for (final IMemberSearcherContribution lContribution : availableSearchers) {
			if (lMemberSearcherName.equals(lContribution.getBundleName())) {
				return lContribution.getMemberSearcher();
			}
		}

		// if we didn't find the appropriate one, take the first available
		return availableSearchers.iterator().next().getMemberSearcher();
	}

	/**
	 * Retrieve the actual authenticator.
	 * 
	 * @return {@link IAuthenticator}
	 * @throws VException
	 */
	public IAuthenticator getActiveAuthenticator() throws VException {
		if (availableAuthenticators.isEmpty())
			return null;

		final String lAuthenticatorName = getContributionName(PreferencesHandler.KEY_MEMBER_SEARCHER);
		for (final IAuthenticatorContribution lContribution : availableAuthenticators) {
			if (lAuthenticatorName.equals(lContribution.getBundleName())) {
				return lContribution.getAuthenticator();
			}
		}

		// if we didn't find the appropriate one, take the first available
		return availableAuthenticators.iterator().next().getAuthenticator();
	}

	private String getContributionName(final String inPropertyName) {
		// check the application's properties
		String lValue = "";
		try {
			lValue = PreferencesHandler.INSTANCE.get(inPropertyName);
		}
		catch (final IOException exc) {
			lValue = VSys.getBundleProperty(inPropertyName);
		}
		return lValue != null && lValue.length() != 0 ? lValue
				: ApplicationConstants.DFLT_SEARCHER;
	}

	/**
	 * Returns the list of available contributions.
	 * 
	 * @return String[] the (bundle) names of the registered searchers
	 */
	public String[] getContributionNames() {
		final String[] out = new String[availableSearchers.size()];

		int i = 0;
		for (final IMemberSearcherContribution lContribution : availableSearchers) {
			out[i++] = lContribution.getBundleName();
		}
		return out;
	}

}
