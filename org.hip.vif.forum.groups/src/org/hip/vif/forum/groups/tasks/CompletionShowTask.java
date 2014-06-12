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

import java.sql.SQLException;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.forum.groups.ui.ContributionReView;

import com.vaadin.ui.Component;

/**
 * Task to display a completion.
 *
 * @author Luthiger
 * Created: 13.03.2008
 */
@Partlet
public class CompletionShowTask extends AbstractGroupsTask {

	/* (non-Javadoc)
	 * @see org.hip.vif.servlets.AbstractVIFTask#needsPermission()
	 */
	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_NEW_COMPLETION;
	}

	@Override
	protected Component runChecked() throws VException {		
		try {
			Long lCompletionID = getCompletionID();
			Long lQuestionID = getQuestionID();
			String lQuestionIDs = lQuestionID.toString();
			Long lGroupID = getGroupID();

			//get completion
			Completion lCompletion = BOMHelper.getCompletionHome().getCompletion(lCompletionID);
			QueryResult lSiblings = BOMHelper.getJoinCompletionToMemberHome().selectAuthorViewOfSiblings(lQuestionID, getActor().getActorID(), lCompletionID);
			
			//get question
			Question lQuestion = BOMHelper.getQuestionHome().getQuestion(lQuestionIDs);
			
			CodeList lCodeList = CodeListHome.instance().getCodeList(QuestionState.class, getAppLocale().getLanguage());
			return new ContributionReView(lCompletion, 
					BOMHelper.getGroupHome().getGroup(lGroupID), 
					lQuestion, lSiblings, getPublishedBibliography(lQuestionID),
					getAuthors(lQuestionIDs), getReviewers(lQuestionIDs),
					lCodeList, this);
		} 
		catch (SQLException exc) {
			throw createContactAdminException(exc);
		}		
	}
	
}
