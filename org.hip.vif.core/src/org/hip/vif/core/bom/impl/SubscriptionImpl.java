/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2004, Benno Luthiger

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
package org.hip.vif.core.bom.impl;

import org.hip.kernel.bom.impl.DomainObjectImpl;
import org.hip.vif.core.bom.Subscription;

/** Implementation of the Subscription model.
 * 
 * @see org.hip.vif.core.bom.Subscription
 *
 * @author Benno Luthiger Created on Feb 14, 2004 */
@SuppressWarnings("serial")
public class SubscriptionImpl extends DomainObjectImpl implements Subscription {
    public final static String HOME_CLASS_NAME = "org.hip.vif.core.bom.impl.SubscriptionHomeImpl";

    /** SubscriptionImpl constructor. */
    public SubscriptionImpl() {
        super();
    }

    /** This Method returns the class name of the home.
     *
     * @return java.lang.String */
    @Override
    public String getHomeClassName() {
        return HOME_CLASS_NAME;
    }
}
