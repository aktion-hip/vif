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

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.tasks.AbstractQuestionTask;
import org.hip.vif.web.util.VIFViewHelper;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

/**
 * The view component to edit the content of a question.
 * 
 * @author Luthiger
 * Created: 22.07.2011
 */
@SuppressWarnings("serial")
public class QuestionEditor extends AbstractQuestionView {
	private RichTextArea questionEditor;
	private RichTextArea remarkEditor;

	/**
	 * Constructor
	 * 
	 * @param inQuestion String 
	 * @param inRemark String
	 * @param inParentQuestion {@link Question}
	 * @param inGroup {@link Group}
	 * @param inCompletions {@link QueryResult} the parent question's completions
	 * @param inBibliography {@link QueryResult} the parent question's texts
	 * @param inAuthors {@link QueryResult} the parent question's authors
	 * @param inReviewers {@link QueryResult} the parent question's reviewers
	 * @param inCodeList {@link CodeList}
	 * @param inTask {@link AbstractQuestionTask}
	 * @throws VException
	 * @throws SQLException
	 */
	public QuestionEditor(String inQuestion, String inRemark, Question inParentQuestion, Group inGroup, QueryResult inCompletions, 
			QueryResult inBibliography, QueryResult inAuthors, QueryResult inReviewers, 
			CodeList inCodeList, final AbstractQuestionTask inTask) throws VException, SQLException {
		VerticalLayout lLayout = new VerticalLayout();
		setCompositionRoot(lLayout);
		
		final IMessages lMessages = Activator.getMessages();
		lLayout.setStyleName("vif-view"); //$NON-NLS-1$
		String lTitle = String.format(lMessages.getMessage("ui.question.editor.title.page"), inGroup.get(GroupHome.KEY_NAME).toString()); //$NON-NLS-1$
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle", lTitle), Label.CONTENT_XHTML)); //$NON-NLS-1$

		//parent question
		String lLabel = String.format(lMessages.getMessage("ui.question.editor.title.question"), BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION_DECIMAL, inParentQuestion)); //$NON-NLS-1$
		lLayout.addComponent(createQuestion(inParentQuestion, lLabel, inCodeList, inAuthors, inReviewers, lMessages, false, inTask));
		
		//parent question: completions
		while (inCompletions.hasMoreElements()) {
			lLayout.addComponent(createCompletion(inCompletions.next(), inCodeList, lMessages, false, inTask));
		}
		
		//parent question: texts
		createBibliography(lLayout, inBibliography, lMessages, inTask);

		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-title", lMessages.getMessage("ui.question.editor.label.question")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$
		questionEditor = createEditField(inQuestion, 150);
		lLayout.addComponent(questionEditor);
		
		lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-subtitle", lMessages.getMessage("ui.question.editor.label.remark")), Label.CONTENT_XHTML)); //$NON-NLS-1$ //$NON-NLS-2$
		remarkEditor = createEditField(inRemark, 250);
		lLayout.addComponent(remarkEditor);
		
		Button lSave = new Button(lMessages.getMessage("ui.button.save")); //$NON-NLS-1$
		lLayout.addComponent(lSave);
		lSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				String lQuestion = (String) questionEditor.getValue();
				String lRemark = (String) remarkEditor.getValue();
				if (checkEditorInput(lQuestion) || checkEditorInput(lRemark)) {
					getWindow().showNotification(lMessages.getMessage("errmsg.question.not.empty"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
					return;
				}
				if (!inTask.saveQuestion(lQuestion, lRemark)) {
					getWindow().showNotification(lMessages.getMessage("errmsg.save.general"), Notification.TYPE_WARNING_MESSAGE);					 //$NON-NLS-1$
				}
			}
		});
	}

	private RichTextArea createEditField(String inContent, int inHeight) {
		RichTextArea outEditor = new RichTextArea();
		outEditor.setWidth("70%"); //$NON-NLS-1$
		outEditor.setHeight(inHeight, UNITS_PIXELS);
		outEditor.setValue(inContent);
		outEditor.setStyleName("vif-editor"); //$NON-NLS-1$
		return outEditor;
	}

}
