/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

package org.hip.vif.member.external;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.interfaces.IAuthenticatorContribution;
import org.hip.vif.core.interfaces.IMemberSearcherContribution;
import org.hip.vif.core.member.IAuthenticator;
import org.hip.vif.core.member.IMemberSearcher;
import org.osgi.framework.FrameworkUtil;

/**
 * This bundles service component providing the services for <code>IMemberSearcherContribution</code>
 * and <code>IAuthenticatorContribution</code>.
 * This class registers this bundle's functionality for member authentication and searching.
 * 
 * @author Luthiger
 * Created: 12.05.2011
 */
public class MemberComponent implements IMemberSearcherContribution, IAuthenticatorContribution {

	public String getBundleName() {
		return FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getSymbolicName();
	}

	public IAuthenticator getAuthenticator() throws VException {
		return new ExtDBAuthenticator();
	}

	public IMemberSearcher getMemberSearcher() {
		return new ExtDBMemberSearcher();
	}

}
