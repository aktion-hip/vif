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

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.admin.groupadmin.Activator;
import org.hip.vif.admin.groupadmin.tasks.AbstractCompletionTask;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.web.bom.GroupHome;
import org.hip.vif.web.util.BeanWrapperHelper;
import org.hip.vif.web.util.VIFViewHelper;
import org.ripla.interfaces.IMessages;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;

/** View to display the data of a completion.
 *
 * @author Luthiger Created: 21.11.2011 */
@SuppressWarnings("serial")
public class CompletionView extends AbstractContributionView {

    /** View to edit a completion.
     *
     * @param inCompletion
     * @param inCompletions
     * @param inQuestion
     * @param inGroup
     * @param inCodeList
     * @param inTask
     * @throws VException
     * @throws SQLException */
    public CompletionView(final Completion inCompletion,
            final QueryResult inCompletions, final Question inQuestion,
            final Group inGroup, final CodeList inCodeList,
            final AbstractCompletionTask inTask) throws VException,
            SQLException {
        final IMessages lMessages = Activator.getMessages();
        final VerticalLayout lLayout = createLayout(inGroup, lMessages);

        final String lSubTitle = String.format(lMessages.getFormattedMessage("ui.discussion.completion.title", //$NON-NLS-1$
                BeanWrapperHelper.getPlain(QuestionHome.KEY_QUESTION_DECIMAL,
                        inQuestion)));
        lLayout.addComponent(new Label(
                String.format(VIFViewHelper.TMPL_TITLE, "vif-title", lSubTitle), ContentMode.HTML)); //$NON-NLS-1$

        addProperQuestion(inQuestion, lMessages, lLayout);

        // completions
        while (inCompletions.hasMoreElements()) {
            lLayout.addComponent(createCompletion(inCompletions.next(),
                    inCodeList, lMessages, inTask));
        }

        lLayout.addComponent(new Label(
                String.format(
                        VIFViewHelper.TMPL_TITLE,
                        "vif-subtitle", lMessages.getMessage("ui.discussion.completion.label")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-2$

        final String lValueBefore = BeanWrapperHelper.getString(
                CompletionHome.KEY_COMPLETION, inCompletion);
        final RichTextArea lEditor = createEditField(lValueBefore, 250);
        lLayout.addComponent(lEditor);

        lLayout.addComponent(createSaveButton(lEditor, lValueBefore, lMessages,
                inTask));
    }

    /** View to create a new completion.
     *
     * @param inCompletion
     * @param inCompletionID
     * @param inQuestion
     * @param inCompletions
     * @param inGroup
     * @param inCodeList
     * @param inTask
     * @throws VException
     * @throws SQLException */
    public CompletionView(final String inCompletion, final Long inCompletionID,
            final DomainObject inQuestion, final QueryResult inCompletions,
            final Group inGroup, final CodeList inCodeList,
            final AbstractCompletionTask inTask) throws VException,
            SQLException {

        final IMessages lMessages = Activator.getMessages();
        final VerticalLayout lLayout = createLayout(inGroup, lMessages);

        final String lSubTitle = String.format(lMessages.getFormattedMessage("ui.discussion.completion.title.create", //$NON-NLS-1$
                BeanWrapperHelper.getPlain(QuestionHome.KEY_QUESTION_DECIMAL,
                        inQuestion)));
        lLayout.addComponent(new Label(
                String.format(VIFViewHelper.TMPL_TITLE, "vif-title", lSubTitle), ContentMode.HTML)); //$NON-NLS-1$

        addProperQuestion(inQuestion, lMessages, lLayout);

        // completions
        while (inCompletions.hasMoreElements()) {
            final GeneralDomainObject lCompletionBO = inCompletions.next();
            final Long lCompletionID = BeanWrapperHelper.getLong(
                    CompletionHome.KEY_ID, lCompletionBO);
            // we have to filter out the actual completion in the edit case
            // because this completion is displayed in the editor
            if (!lCompletionID.equals(inCompletionID)) {
                lLayout.addComponent(createCompletion(lCompletionBO,
                        inCodeList, lMessages, inTask));
            }
        }

        lLayout.addComponent(new Label(
                String.format(
                        VIFViewHelper.TMPL_TITLE,
                        "vif-subtitle", lMessages.getMessage("ui.discussion.completion.label")), ContentMode.HTML)); //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-2$

        final RichTextArea lEditor = createEditField(inCompletion, 250);
        lLayout.addComponent(lEditor);

        lLayout.addComponent(createSaveButton(lEditor, inCompletion, lMessages,
                inTask));
    }

    /** @param inGroup
     * @param inMessages
     * @return VerticalLayout */
    private VerticalLayout createLayout(final Group inGroup,
            final IMessages inMessages) {
        final VerticalLayout lLayout = new VerticalLayout();
        setCompositionRoot(lLayout);

        lLayout.setStyleName("vif-view"); //$NON-NLS-1$
        final String lTitle = String.format(inMessages.getFormattedMessage("ui.discussion.question.view.title.page", //$NON-NLS-1$
                BeanWrapperHelper.getString(GroupHome.KEY_ID, inGroup),
                BeanWrapperHelper.getString(GroupHome.KEY_NAME, inGroup)));
        lLayout.addComponent(new Label(
                String.format(VIFViewHelper.TMPL_TITLE, "vif-pagetitle", lTitle), ContentMode.HTML)); //$NON-NLS-1$
        return lLayout;
    }

    /** @param inEditor
     * @param inValueBefore
     * @param inMessages
     * @param inTask
     * @return Button */
    private Button createSaveButton(final RichTextArea inEditor,
            final String inValueBefore, final IMessages inMessages,
            final AbstractCompletionTask inTask) {
        final Button lSave = new Button(inMessages.getMessage("ui.button.save")); //$NON-NLS-1$
        lSave.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent inEvent) {
                final String lValue = inEditor.getValue();
                if (checkEditorInput(lValue)) { //$NON-NLS-1$
                    inEditor.setValue(inValueBefore); //$NON-NLS-1$
                    Notification.show(
                            inMessages
                            .getMessage("errmsg.completion.not.empty"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                    return;
                }
                if (!inTask.saveCompletion(lValue)) {
                    Notification.show(
                            inMessages.getMessage("errmsg.save.general"), Type.WARNING_MESSAGE); //$NON-NLS-1$
                }
            }
        });
        return lSave;
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
