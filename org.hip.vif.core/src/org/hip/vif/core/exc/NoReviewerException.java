/*
 This package is part of the application VIF.
 Copyright (C) 2010, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
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
package org.hip.vif.core.exc;

import org.hip.kernel.exc.ExceptionData;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;

/**
 * Exception to signal that the system could not find an available participant
 * to review a contribution.
 * 
 * @author Luthiger Created: 19.10.2010
 */
@SuppressWarnings("serial")
public class NoReviewerException extends VIFWarningException {
	private static final String MESSAGE = "No participants available to review the contributions in group %s.";

	private final Long groupId;

	/**
	 * NoReviewerException constructor.
	 * 
	 * @param inGroupID
	 *            Long the group the exceptions refers to
	 */
	public NoReviewerException(final Long inGroupID) {
		groupId = inGroupID;
	}

	@Override
	protected ExceptionData createExceptionData() {
		return new ExceptionData(this, getMessage(groupId));
	}

	private static String getMessage(final Long inGroupID) {
		String outMessage = String.format(MESSAGE, inGroupID.toString());
		try {
			final Group lGroup = BOMHelper.getGroupHome().getGroup(inGroupID);
			outMessage = String.format(MESSAGE, lGroup.get(GroupHome.KEY_NAME));
		}
		catch (final VException exc) {
			// intentionally left empty
		}
		return outMessage;
	}

}
