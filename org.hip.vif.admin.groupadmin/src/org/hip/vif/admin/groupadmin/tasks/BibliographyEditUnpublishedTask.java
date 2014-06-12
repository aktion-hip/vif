/**
 This package is part of the application VIF.
 Copyright (C) 2008-2014, Benno Luthiger

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
package org.hip.vif.admin.groupadmin.tasks;

import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.VIFWorkflowAware;
import org.ripla.annotations.UseCaseController;
import org.ripla.exceptions.RiplaException;

import com.vaadin.ui.Component;

/**
 * For unpublished bibliography entries: Task to display a bibliography entry in
 * editable form.
 * 
 * @author Luthiger Created: 01.07.2010
 */
@UseCaseController
public class BibliographyEditUnpublishedTask extends AbstractBibliographyTask {

	@Override
	protected Component runChecked() throws RiplaException {
		try {
			final Long lTextID = getTextID();
			final int lTextVersion = getTextVersion().intValue();
			// get text entry
			final Text lText = BOMHelper.getTextHome().getText(lTextID,
					lTextVersion);

			// only edit the text entry if it is not yet published
			if (((VIFWorkflowAware) lText).isUnpublished()) {
				return editBibliography(lText, lTextID, lTextVersion, false);
			} else {
				sendEvent(BibliographyShowTask.class);
				return null;
			}
		}
		catch (final Exception exc) {
			throw createContactAdminException(exc);
		}
	}

}
