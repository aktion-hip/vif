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


import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.impl.JoinAuthorReviewerToTextHome.PublishedText;
import org.hip.vif.forum.groups.ui.BibliographyView;

import com.vaadin.ui.Component;

/**
 * Task to show a bibliography entry.
 *
 * @author Luthiger
 * Created: 14.07.2010
 */
@Partlet
public class BibliographyShowTask extends AbstractGroupsTask {

	private BibliographyView textView;

	/**
	 * @see org.hip.vif.servlets.AbstractVIFTask#needsPermission()
	 */
	protected String needsPermission() {
		return ""; //$NON-NLS-1$
	}

	@Override
	protected Component runChecked() throws VException {
		try {
			Long lTextID = getTextID();
			Long lVersion = getTextVersion();
			
			if (lVersion < 0) {
				//display published version of bibliography entry
				
				//get text parameter object containing the published version of the requested text
				PublishedText lText = BOMHelper.getJoinAuthorReviewerToTextHome().getTextPublished(lTextID);
				MemberHome lMemberHome = BOMHelper.getMemberCacheHome();
				GeneralDomainObject lTextBOM = lText.getText();
				textView = new BibliographyView(lTextBOM, getDownloads(lTextID),
						lMemberHome.getMember(lText.getAuthorID()),
						lText.getReviewerID() == null ? null : lMemberHome.getMember(lText.getReviewerID()),
						TextHome.KEY_BIBLIO_TYPE, getActor().isGuest(), 
						this);
			}
			else {
				//display specified version of bibliography entry
				Text lText = BOMHelper.getTextHome().getText(lTextID, lVersion.intValue());
				textView = new BibliographyView(lText, getDownloads(lTextID), 
						BOMHelper.getTextAuthorReviewerHome().getAuthor(lTextID, lVersion.intValue()), null, 
						TextHome.KEY_TYPE, false, this);
			}
			return textView;
		} 
		catch (Exception exc) {
			throw createContactAdminException(exc);
		}
	}

}
