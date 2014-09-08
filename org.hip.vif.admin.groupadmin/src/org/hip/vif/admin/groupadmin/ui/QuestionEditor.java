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
import java.util.List;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.data.CompletionsHelper.Completion;
import org.hip.vif.admin.groupadmin.tasks.AbstractQuestionTask;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.web.bom.GroupHome;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;
import org.ripla.web.util.RiplaViewHelper;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;

/** Question editor to create or edit question.
 *
 * @author Luthiger Created: 24.11.2011 */
@SuppressWarnings("serial")
public class QuestionEditor extends AbstractQuestionView {

    /** Constructor to edit an existing question (called from edit button on <code>QuestionView</code>).
     * 
     * @param inGroup {@link Group}
     * @param inQuestion {@link Question}
     * @param inCompletions {@link List} of <code>Completion</code>s
     * @param inBibliography {@link QueryResult}
     * @param inCodeList {@link CodeList}
     * @param inAdminQuestionEditTask {@link AbstractQuestionTask}
     * @throws SQLException
     * @throws VException */
    public QuestionEditor(final Group inGroup, final Question inQuestion,
            final List<Completion> inCompletions,
            final QueryResult inBibliography, final CodeList inCodeList,
            final AbstractQuestionTask inTask) throws VException, SQLException {

        final IMessages lMessages = Activator.getMessages();
        final VerticalLayout lLayout = createLayout(inGroup, lMessages);

        // editor
        final String lLabel = String
                .format(lMessages
                        .getMessage("ui.discussion.question.view.title.question"), BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION_DECIMAL, inQuestion)); //$NON-NLS-1$
        lLayout.addComponent(new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-title", lLabel), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$
        final RichTextArea lQuestionEditor = createEditField(
                BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION,
                        inQuestion), 150);
        lLayout.addComponent(lQuestionEditor);

        lLayout.addComponent(new Label(
                String.format(
                        VIFViewHelper.TMPL_TITLE,
                        "vif-subtitle", lMessages.getMessage("ui.discussion.question.view.label.remark")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$
        final RichTextArea lRemarkEditor = createEditField(
                BeanWrapperHelper
                        .getString(QuestionHome.KEY_REMARK, inQuestion),
                250);
        lLayout.addComponent(lRemarkEditor);

        lLayout.addComponent(createSaveButton(inTask, lQuestionEditor,
                lRemarkEditor, lMessages));

        lLayout.addComponent(RiplaViewHelper.createSpacer());
        // completions
        for (final Completion lCompletion : inCompletions) {
            lLayout.addComponent(createCompletion(lCompletion, inCodeList,
                    lMessages, inTask));
        }

        // texts
        createBibliography(lLayout, inBibliography, lMessages, inTask);
    }

    /** View to create a new question or to edit a pending question.<br />
     * Note: the question must not be a root question.
     * 
     * @param inQuestion String
     * @param inRemark String
     * @param inParentQuestion {@link Question} must not be <code>null</code>
     * @param inGroup {@link Group}
     * @param inCompletions {@link QueryResult} the parent question's completions
     * @param inBibliography {@link QueryResult} the parent question's texts
     * @param inAuthors {@link QueryResult} the parent question's authors
     * @param inReviewers {@link QueryResult} the parent question's reviewers
     * @param inCodeList {@link CodeList}
     * @param inTask
     * @throws VException
     * @throws SQLException */
    public QuestionEditor(final String inQuestion, final String inRemark,
            final Question inParentQuestion, final Group inGroup,
            final QueryResult inCompletions, final QueryResult inBibliography,
            final QueryResult inAuthors, final QueryResult inReviewers,
            final CodeList inCodeList, final AbstractQuestionTask inTask)
            throws VException, SQLException {

        final IMessages lMessages = Activator.getMessages();
        final VerticalLayout lLayout = createLayout(inGroup, lMessages);

        // parent question
        final String lLabel = String
                .format(lMessages
                        .getMessage("ui.discussion.question.view.title.question"), BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION_DECIMAL, inParentQuestion)); //$NON-NLS-1$
        lLayout.addComponent(createQuestion(inParentQuestion, lLabel,
                inCodeList, inAuthors, inReviewers, lMessages, inTask));

        // parent question: completions
        while (inCompletions.hasMoreElements()) {
            lLayout.addComponent(createCompletion(inCompletions.next(),
                    inCodeList, lMessages, inTask));
        }

        // parent question: texts
        createBibliography(lLayout, inBibliography, lMessages, inTask);

        lLayout.addComponent(new Label(
                String.format(
                        VIFViewHelper.TMPL_TITLE,
                        "vif-title", lMessages.getMessage("ui.discussion.question.view.question.follow.up")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$
        final RichTextArea lQuestionEditor = createEditField(inQuestion, 150);
        lLayout.addComponent(lQuestionEditor);

        lLayout.addComponent(new Label(
                String.format(
                        VIFViewHelper.TMPL_TITLE,
                        "vif-subtitle", lMessages.getMessage("ui.discussion.question.view.label.remark")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$
        final RichTextArea lRemarkEditor = createEditField(inRemark, 250);
        lLayout.addComponent(lRemarkEditor);

        lLayout.addComponent(createSaveButton(inTask, lQuestionEditor,
                lRemarkEditor, lMessages));
    }

    /** @param inTask
     * @param inMessages
     * @param inQuestionEditor
     * @param inRemarkEditor
     * @return Button */
    private Button createSaveButton(final AbstractQuestionTask inTask,
            final RichTextArea inQuestionEditor,
            final RichTextArea inRemarkEditor, final IMessages inMessages) {
        final Button lSave = new Button(inMessages.getMessage("ui.button.save")); //$NON-NLS-1$
        lSave.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                final String lQuestion = inQuestionEditor.getValue();
                final String lRemark = inRemarkEditor.getValue();
                if (checkEditorInput(lQuestion) || checkEditorInput(lRemark)) {
                    Notification.show(
                            inMessages.getMessage("errmsg.question.not.empty"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                    return;
                }
                if (!inTask.saveQuestion(lQuestion, lRemark)) {
                    Notification.show(
                            inMessages.getMessage("errmsg.save.general"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                }
            }
        });
        return lSave;
    }

    /** Editor to create a new root question.
     * 
     * @param inGroup {@link Group}
     * @param inTask {@link AbstractQuestionTask} */
    public QuestionEditor(final Group inGroup, final AbstractQuestionTask inTask) {
        final IMessages lMessages = Activator.getMessages();
        final VerticalLayout lLayout = createLayout(inGroup, lMessages);

        lLayout.addComponent(new Label(
                String.format(
                        VIFViewHelper.TMPL_TITLE,
                        "vif-title", lMessages.getMessage("ui.discussion.question.view.title.question.root")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$
        final RichTextArea lQuestionEditor = createEditField("", 150); //$NON-NLS-1$
        lLayout.addComponent(lQuestionEditor);

        lLayout.addComponent(new Label(
                String.format(
                        VIFViewHelper.TMPL_TITLE,
                        "vif-subtitle", lMessages.getMessage("ui.discussion.question.view.label.remark")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$
        final RichTextArea lRemarkEditor = createEditField("", 250); //$NON-NLS-1$
        lLayout.addComponent(lRemarkEditor);

        lLayout.addComponent(createSaveButton(inTask, lQuestionEditor,
                lRemarkEditor, lMessages));
    }

    /** @param inGroup
     * @param inMessages
     * @return */
    private VerticalLayout createLayout(final Group inGroup,
            final IMessages inMessages) {
        final VerticalLayout lLayout = new VerticalLayout();
        setCompositionRoot(lLayout);

        lLayout.setStyleName("vif-view"); //$NON-NLS-1$
        final String lTitle = inMessages.getFormattedMessage("ui.discussion.question.view.title.activ", //$NON-NLS-1$
                BeanWrapperHelper.getLong(GroupHome.KEY_ID, inGroup),
                BeanWrapperHelper.getString(GroupHome.KEY_NAME, inGroup));
        lLayout.addComponent(new Label(
                String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle", lTitle), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-2$
        return lLayout;
    }

    private RichTextArea createEditField(final String inContent,
            final int inHeight) {
        final RichTextArea outEditor = new RichTextArea();
        outEditor.setWidth("70%"); //$NON-NLS-1$
        outEditor.setHeight(inHeight, Unit.PIXELS);
        outEditor.setValue(inContent);
        outEditor.setStyleName("vif-editor"); //$NON-NLS-1$
        return outEditor;
    }

}
