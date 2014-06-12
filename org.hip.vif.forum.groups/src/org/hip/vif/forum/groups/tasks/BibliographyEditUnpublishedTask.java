/*
 This package is part of the application VIF.
 Copyright (C) 2008, Benno Luthiger

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
package org.hip.vif.forum.groups.tasks;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.VIFWorkflowAware;

import com.vaadin.ui.Component;

/**
 * For unpublished bibliography entries:
 * Task to display a bibliography entry in editable form.
 *
 * @author Luthiger
 * Created: 01.07.2010
 */
@Partlet
public class BibliographyEditUnpublishedTask extends AbstractBibliographyTask {

	@Override
	protected Component runChecked() throws VException {
		try {
			Long lTextID = getTextID();
			int lTextVersion = getTextVersion().intValue();

			//get text entry
			Text lText = BOMHelper.getTextHome().getText(lTextID, lTextVersion);
			
			//only edit the text entry if it is not yet published
			if (((VIFWorkflowAware)lText).isUnpublished()) {
				return editBibliography(lText, lTextID, lTextVersion, false);
			}
			else {
				sendEvent(BibliographyShowTask.class);
				return null;
			}
		} 
		catch (Exception exc) {
			throw createContactAdminException(exc);
		}
	}
	
}
