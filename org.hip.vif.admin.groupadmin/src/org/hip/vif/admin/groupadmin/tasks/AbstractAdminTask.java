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

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.impl.TextQuestionHome;
import org.hip.vif.web.tasks.AbstractWebController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for this bundle's tasks.
 * 
 * @author Luthiger Created: 04.12.2011
 */
public abstract class AbstractAdminTask extends AbstractWebController {
	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractAdminTask.class);

	/**
	 * Unlinks (i.e. removes) the bibliography entry with the specified ID from
	 * the actual question.
	 * 
	 * @param inTextID
	 *            Long
	 * @return boolean <code>true</code> if successful
	 */
	public boolean unlinkBibliography(final Long inTextID) {
		try {
			final TextQuestionHome lHome = BOMHelper.getTextQuestionHome();
			final KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(TextQuestionHome.KEY_TEXTID, inTextID);
			lKey.setValue(TextQuestionHome.KEY_QUESTIONID, getQuestionID());
			lHome.delete(lKey, true);

			showNotification(Activator.getMessages().getMessage(
					"msg.bibliography.link.removed")); //$NON-NLS-1$
			sendEvent(AdminQuestionShowTask.class);
			return true;
		}
		catch (final VException exc) {
			LOG.error(
					"Error encountered while removing the bibliography entry!", exc); //$NON-NLS-1$
		}
		catch (final SQLException exc) {
			LOG.error(
					"Error encountered while removing the bibliography entry!", exc); //$NON-NLS-1$
		}
		return false;
	}

}
