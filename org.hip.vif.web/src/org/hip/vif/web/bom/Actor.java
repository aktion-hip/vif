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
package org.hip.vif.web.bom;

import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.web.Activator;

/**
 * Class storing all data relevant to describe the actor.<br />
 * This class provides i18n messages.
 * 
 * @author lbenno
 */
@SuppressWarnings("serial")
public class Actor extends org.hip.vif.core.bom.Actor {

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
		super(inActorID, inUserID);
	}

	@Override
	protected String getDftId() {
		return Activator.getMessages().getMessage("org.hip.vif.core.guest");
	}

}
