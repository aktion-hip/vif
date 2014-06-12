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

package org.hip.vif.forum.groups.ui;

import java.sql.SQLException;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.interfaces.IPluggableTask;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.util.AuthorReviewerRenderHelper;
import org.hip.vif.forum.groups.util.AuthorReviewerRenderHelper.AuthorReviewerRenderer;
import org.hip.vif.web.interfaces.IBibliographyTask;
import org.hip.vif.web.util.BibliographyViewHelper;
import org.hip.vif.web.util.BibliographyViewHelper.FormCreator;
import org.hip.vif.web.util.UploadComponent;
import org.hip.vif.web.util.VIFViewHelper;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

/**
 * View to show the bibliographical entry.
 * 
 * @author Luthiger
 * Created: 12.06.2011
 */
@SuppressWarnings("serial")
public class BibliographyView extends AbstractContributionView {

	/**
	 * Constructor to show the values of a published bibliography entry.
	 * 
	 * @param inText GeneralDomainObject
	 * @param inDownloads {@link QueryResult}
	 * @param inAuthor Member
	 * @param inReviewer Member may be <code>null</code>
	 * @param inKeyType String the object's key for the text type
	 * @param inIsGuest boolean
	 * @param inTask {@link IPluggableTask}
	 * @throws SQLException 
	 * @throws VException 
	 */
	public BibliographyView(GeneralDomainObject inText, QueryResult inDownloads, Member inAuthor, Member inReviewer, 
			String inKeyType, boolean inIsGuest, IPluggableTask inTask) throws VException, SQLException {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);

		IMessages lMessages = Activator.getMessages();
		lLayout.addComponent(BibliographyViewHelper.createBiblioView(inText, inDownloads, inKeyType));
		
		//add author/reviewer
		AuthorReviewerRenderer lRenderer = AuthorReviewerRenderHelper.createRenderer(inAuthor, inReviewer, inTask, !inIsGuest);
		lLayout.addComponent(lRenderer.render(lMessages.getMessage("ui.question.view.label.author"),  //$NON-NLS-1$
				lMessages.getMessage("ui.question.view.label.reviewer"), //$NON-NLS-1$
				BeanWrapperHelper.getFormattedDate(TextHome.KEY_FROM, inText))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Constructor to edit the values of a bibliography entry.
	 * 
	 * @param inText 
	 * @param inDownloads {@link QueryResult}
	 * @param inAuthors {@link QueryResult} 
	 * @param inReviewers {@link QueryResult}
	 * @param inCodeList {@link CodeList}
	 * @param inCreateVersion boolean <code>true</code> if the form should create a new (private) version of the published entry, <code>false</code> in case of editing an unpublished version.
	 * @param inTask {@link IBibliographyTask}
	 * @throws SQLException 
	 * @throws VException 
	 */
	public BibliographyView(final Text inText, QueryResult inDownloads, QueryResult inAuthors, 
			QueryResult inReviewers, CodeList inCodeList, final boolean inCreateVersion, final IBibliographyTask inTask) throws SQLException, VException {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		final IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-view"); //$NON-NLS-1$
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle", lMessages.getMessage("ui.bibliography.editor.title.page")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$

		final FormCreator lForm = BibliographyViewHelper.createBiblioForm(inText, new UploadComponent(inDownloads, inTask), getState(BeanWrapperHelper.getString(TextHome.KEY_STATE, inText), inCodeList, lMessages));
		lLayout.addComponent(lForm.createForm());
		Button save = new Button(lMessages.getMessage("ui.button.save")); //$NON-NLS-1$
		
		//author/reviewer
		AuthorReviewerRenderer lRenderer = AuthorReviewerRenderHelper.createRenderer(inAuthors, inReviewers, inTask, true);
		lLayout.addComponent(lRenderer.render(lMessages.getMessage("ui.question.view.label.author"),  //$NON-NLS-1$
				lMessages.getMessage("ui.question.view.label.reviewer"))); //$NON-NLS-1$ //$NON-NLS-2$ 
		
		//add buttons
		lLayout.addComponent(VIFViewHelper.createSpacer());
		save.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				try {
					lForm.commit();
					acutalizeType(inText, lForm.getTypes());
					if (!inTask.saveText(inText, inCreateVersion)) {
						getWindow().showNotification(lMessages.getMessage("errmsg.save.general"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
					}
				}
				catch (InvalidValueException exc) {
					//intentionally left empty
				} 				
			}
		});
		lLayout.addComponent(save);
	}
	
	private void acutalizeType(Text inText, Select inTypes) {
		try {
			inText.set(TextHome.KEY_TYPE, new Long(inTypes.getValue().toString()));
		} catch (VException exc) {
			//intentionally left empty
		}
	}
	
}