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

package org.hip.vif.admin.groupadmin.ui;

import java.sql.SQLException;

import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.util.AuthorReviewerRenderHelper;
import org.hip.vif.admin.groupadmin.util.AuthorReviewerRenderHelper.AuthorReviewerRenderer;
import org.hip.vif.core.bom.Member;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.interfaces.IPluggableTask;
import org.hip.vif.web.interfaces.IBibliographyTask;
import org.hip.vif.web.interfaces.IPluggableWithLookup;
import org.hip.vif.web.util.BibliographyViewHelper;
import org.hip.vif.web.util.BibliographyViewHelper.FormCreator;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.UploadComponent;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

/**
 * View to show the bibliographical entry.
 * 
 * @author Luthiger Created: 01.12.2011
 */
@SuppressWarnings("serial")
public class BibliographyView extends AbstractContributionView {

	/**
	 * Constructor to show the values of a published bibliography entry.
	 * 
	 * @param inText
	 *            GeneralDomainObject
	 * @param inDownloads
	 *            {@link QueryResult}
	 * @param inAuthor
	 *            Member
	 * @param inReviewer
	 *            Member may be <code>null</code>
	 * @param inKeyType
	 *            String the object's key for the text type
	 * @param inIsGuest
	 *            boolean
	 * @param inTask
	 *            {@link IPluggableTask}
	 * @throws SQLException
	 * @throws VException
	 */
	public BibliographyView(final GeneralDomainObject inText,
			final QueryResult inDownloads, final Member inAuthor,
			final Member inReviewer, final String inKeyType,
			final boolean inIsGuest, final IPluggableWithLookup inTask)
			throws VException, SQLException {
		final VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);

		final IMessages lMessages = Activator.getMessages();
		lLayout.addComponent(BibliographyViewHelper.createBiblioView(inText,
				inDownloads, inKeyType));

		// add author/reviewer
		final AuthorReviewerRenderer lRenderer = AuthorReviewerRenderHelper
				.createRenderer(inAuthor, inReviewer, inTask);
		lLayout.addComponent(lRenderer.render(
				lMessages
						.getMessage("ui.discussion.question.view.label.author"), //$NON-NLS-1$
				lMessages
						.getMessage("ui.discussion.question.view.label.reviewer"), //$NON-NLS-1$
				BeanWrapperHelper.getFormattedDate(TextHome.KEY_FROM, inText))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Constructor to edit the values of a bibliography entry.
	 * 
	 * @param inText
	 *            {@link Text}
	 * @param inDownloads
	 *            {@link QueryResult}
	 * @param inAuthors
	 *            {@link QueryResult}
	 * @param inReviewers
	 *            {@link QueryResult}
	 * @param inCodeList
	 *            {@link CodeList}
	 * @param inCreateVersion
	 *            boolean <code>true</code> if the form should create a new
	 *            (private) version of the published entry, <code>false</code>
	 *            in case of editing an unpublished version.
	 * @param inTask
	 *            {@link IBibliographyTask}
	 * @throws SQLException
	 * @throws VException
	 */
	public BibliographyView(final Text inText, final QueryResult inDownloads,
			final QueryResult inAuthors, final QueryResult inReviewers,
			final CodeList inCodeList, final boolean inCreateVersion,
			final IBibliographyTask inTask) throws SQLException, VException {
		final VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		final IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-view"); //$NON-NLS-1$
		lLayout.addComponent(new Label(
				String.format(
						VIFViewHelper.TMPL_TITLE,
						"vif-pagetitle", lMessages.getMessage("ui.bibliography.editor.title.page")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$

		final FormCreator lForm = BibliographyViewHelper
				.createBiblioForm(
						inText,
						new UploadComponent(inDownloads, inTask),
						getState(BeanWrapperHelper.getString(
								TextHome.KEY_STATE, inText), inCodeList,
								lMessages));
		lLayout.addComponent(lForm.createForm());
		final Button lSave = new Button(lMessages.getMessage("ui.button.save")); //$NON-NLS-1$

		// author/reviewer
		final AuthorReviewerRenderer lRenderer = AuthorReviewerRenderHelper
				.createRenderer(inAuthors, inReviewers, inTask);
		lLayout.addComponent(lRenderer.render(
				lMessages
						.getMessage("ui.discussion.question.view.label.author"), //$NON-NLS-1$
				lMessages
						.getMessage("ui.discussion.question.view.label.reviewer"))); //$NON-NLS-1$ //$NON-NLS-2$ 

		// add buttons
		lLayout.addComponent(RiplaViewHelper.createSpacer());
		lSave.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(final ClickEvent inEvent) {
				try {
					lForm.commit();
					acutalizeType(inText, lForm.getTypes());
					if (!inTask.saveText(inText, inCreateVersion)) {
						Notification.show(
								lMessages.getMessage("errmsg.save.general"), Notification.Type.WARNING_MESSAGE); //$NON-NLS-1$
					}
				}
				catch (final CommitException exc) {
					// intentionally left empty
				}
			}
		});
		lLayout.addComponent(lSave);
	}

	private void acutalizeType(final Text inText, final ComboBox inTypes) {
		try {
			inText.set(TextHome.KEY_TYPE, new Long(inTypes.getValue()
					.toString()));
		}
		catch (final VException exc) {
			// intentionally left empty
		}
	}

}
