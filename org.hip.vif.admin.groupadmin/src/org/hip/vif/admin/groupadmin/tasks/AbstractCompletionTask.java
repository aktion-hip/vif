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

package org.hip.vif.admin.groupadmin.tasks;

import org.hip.vif.admin.groupadmin.Constants;
import org.hip.vif.web.tasks.AbstractWebController;

/**
 * Abstract base class for completion manipulation.
 * 
 * @author Luthiger Created: 24.11.2011
 */
public abstract class AbstractCompletionTask extends AbstractWebController {

	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_COMPLETION_NEW;
	}

	/**
	 * Callback method, saves the changes.
	 * 
	 * @param inQuestion
	 *            String
	 * @param inRemark
	 *            String
	 * @return boolean <code>true</code> if successful
	 */
	abstract public boolean saveCompletion(String inCompletionText);

}
