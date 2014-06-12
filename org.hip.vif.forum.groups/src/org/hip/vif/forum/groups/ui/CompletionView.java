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

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.tasks.AbstractCompletionTask;
import org.hip.vif.web.util.VIFViewHelper;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

/**
 * View to create and edit a completion.
 * 
 * @author Luthiger
 * Created: 14.07.2011
 */
@SuppressWarnings("serial")
public class CompletionView extends AbstractContributionView {
	private RichTextArea completionEditor;

	/**
	 * Constructor
	 * 
	 * @param inCompletion String the completion text
	 * @param inCompletionID Long
	 * @param inQuestion {@link DomainObject} the question the completion is belonging to
	 * @param inCompletions {@link QueryResult} other completions belonging to the question
	 * @param inGroup {@link Group} the discussion group the question belongs to
	 * @param inCodeList {@link CodeList} the code list for the contributions' workflow states
	 * @param inTask {@link AbstractCompletionTask} 
	 * @throws VException
	 * @throws SQLException
	 */
	public CompletionView(String inCompletion, Long inCompletionID, DomainObject inQuestion, QueryResult inCompletions, Group inGroup, 
			CodeList inCodeList, final AbstractCompletionTask inTask) throws VException, SQLException {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		
		final IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-view"); //$NON-NLS-1$
		String lTitle = String.format(lMessages.getFormattedMessage("ui.completion.group.title",  //$NON-NLS-1$
				BeanWrapperHelper.getString(GroupHome.KEY_ID, inGroup), 
				BeanWrapperHelper.getString(GroupHome.KEY_NAME, inGroup)));
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-title", lTitle), Label.CONTENT_XHTML)); //$NON-NLS-1$

		String lSubTitle = String.format(lMessages.getFormattedMessage("ui.completion.title",  //$NON-NLS-1$
				BeanWrapperHelper.getPlain(QuestionHome.KEY_QUESTION_DECIMAL, inQuestion)));
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle", lSubTitle), Label.CONTENT_XHTML)); //$NON-NLS-1$
		
		addProperQuestion(inQuestion, lMessages, lLayout);

		//completions
		while (inCompletions.hasMoreElements()) {
			GeneralDomainObject lCompletionBO = inCompletions.next();
			Long lCompletionID = BeanWrapperHelper.getLong(CompletionHome.KEY_ID, lCompletionBO);
			//we have to filter out the actual completion in the edit case because this completion is displayed in the editor
			if (!lCompletionID.equals(inCompletionID)) {
				lLayout.addComponent(createCompletion(lCompletionBO, inCodeList, lMessages, false, inTask));
			}
		}
		
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-subtitle", lMessages.getMessage("ui.completion.label")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-2$
		
		completionEditor = new RichTextArea();
		completionEditor.setWidth("70%"); //$NON-NLS-1$
		completionEditor.setHeight(250, UNITS_PIXELS);
		completionEditor.setValue(inCompletion);
		completionEditor.setStyleName("vif-editor"); //$NON-NLS-1$
		lLayout.addComponent(completionEditor);
		
		Button lSave = new Button(lMessages.getMessage("ui.button.save")); //$NON-NLS-1$
		lLayout.addComponent(lSave);
		lSave.addListener(new Button.ClickListener() {			
			public void buttonClick(ClickEvent inEvent) {
				String lValue = (String) completionEditor.getValue();
				if (checkEditorInput(lValue)) { //$NON-NLS-1$
					completionEditor.setValue(""); //$NON-NLS-1$
					getWindow().showNotification(lMessages.getMessage("errmsg.completion.not.empty"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
					return;
				}
				if (!inTask.saveCompletion(lValue)) {
					getWindow().showNotification(lMessages.getMessage("errmsg.save.general"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
				}
			}
		});
	}

}
