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

import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Text;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;

import com.vaadin.ui.Component;

/**
 * For published bibliography entries: Task to display a bibliography entry in
 * editable form.
 * 
 * @author Luthiger Created: 26.09.2011
 */
@UseCaseController
public class BibliographyEditPublishedTask extends AbstractBibliographyTask {

	@Override
	protected Component runChecked() throws RiplaException {
		try {
			final Long lTextID = getTextID();
			final int lTextVersion = getTextVersion().intValue();
			// get text entry
			final Text lText = BOMHelper.getTextHome().getText(lTextID,
					lTextVersion);

			return editBibliography(lText, lTextID, lTextVersion, true);
		}
		catch (final Exception exc) {
			throw createContactAdminException(exc);
		}
	}

}
