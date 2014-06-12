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

package org.hip.vif.forum.usersettings.ui;

import java.sql.SQLException;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.interfaces.IEventableTask;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.web.util.LinkButtonHelper;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

/**
 * Base class for rating views.
 * 
 * @author Luthiger
 * Created: 26.12.2011
 */
@SuppressWarnings("serial")
public abstract class AbstractRatingView extends CustomComponent {

	/**
	 * Displays the list of reviewed contributions.
	 * 
	 * @param inQuestions {@link QueryResult}
	 * @param inCompletions {@link QueryResult}
	 * @param inTexts {@link QueryResult}
	 * @param inTask {@link IEventableTask}
	 * @param inLayout {@link VerticalLayout}
	 * @param inMessages {@link IMessages}
	 * @throws SQLException
	 * @throws BOMException
	 * @throws VException
	 */
	protected void listContributions(QueryResult inQuestions, QueryResult inCompletions, QueryResult inTexts,
			final IEventableTask inTask, VerticalLayout inLayout, IMessages inMessages) throws SQLException, BOMException, VException {
		//questions
		while (inQuestions.hasMoreElements()) {
			inLayout.addComponent(createQuestion(inQuestions.nextAsDomainObject(), (IEventableTask)inTask));
		}
		//completions
		while (inCompletions.hasMoreElements()) {
			inLayout.addComponent(createCompletion(inCompletions.nextAsDomainObject(), (IEventableTask)inTask, inMessages));
		}
		//texts
		while (inTexts.hasMoreElements()) {
			inLayout.addComponent(createBibliography(inTexts.nextAsDomainObject(), (IEventableTask) inTask));
		}
	}

	private Component createQuestion(GeneralDomainObject inQuestion, IEventableTask inTask) {
		return LinkButtonHelper.createLinkButton(String.format("%s - %s",  //$NON-NLS-1$
						BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION_DECIMAL, inQuestion), 
						BeanWrapperHelper.getPlain(QuestionHome.KEY_QUESTION, inQuestion)), 
				LookupType.CONTENT, 
				BeanWrapperHelper.getLong(QuestionHome.KEY_ID, inQuestion), inTask);
	}

	private Component createCompletion(GeneralDomainObject inCompletion, IEventableTask inTask, IMessages inMessages) {
		return LinkButtonHelper.createLinkButton(inMessages.getFormattedMessage("ui.rating.completion", BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION_DECIMAL, inCompletion)),  //$NON-NLS-1$
				LookupType.CONTENT, 
				BeanWrapperHelper.getLong(CompletionHome.KEY_QUESTION_ID, inCompletion), inTask);
	}

	private Component createBibliography(GeneralDomainObject inBibliography, IEventableTask inTask) throws VException {
		return LinkButtonHelper.createLinkButton(BeanWrapperHelper.getString(TextHome.KEY_REFERENCE, inBibliography), 
				LookupType.BIBLIOGRAPHY, 
				BeanWrapperHelper.getLong(TextHome.KEY_ID, inBibliography), inTask);
	}

}