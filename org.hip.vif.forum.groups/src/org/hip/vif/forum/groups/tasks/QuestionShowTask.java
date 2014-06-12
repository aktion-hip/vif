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

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.EmptyQueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.forum.groups.data.CompletionsHelper;
import org.hip.vif.forum.groups.data.QuestionContainer;
import org.hip.vif.forum.groups.data.QuestionWrapper;
import org.hip.vif.forum.groups.ui.QuestionView;
import org.hip.vif.forum.groups.util.SwitchHelper;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

/**
 * Task to display a question and its completions.
 * 
 * Created on 11.08.2003
 * @author Luthiger
 */
@SuppressWarnings("serial")
@Partlet
public class QuestionShowTask extends AbstractGroupsTask implements ValueChangeListener {
	private QuestionView questionView;

	/**
	 * @see org.hip.vif.servlets.AbstractVIFTask#needsPermission()
	 */
	protected String needsPermission() {
		return ""; //$NON-NLS-1$
	}

	public Component runChecked() throws VException {			
		try {
			Long lQuestionID = getQuestionID();
			String lQuestionIDs = lQuestionID.toString();
			//get question
			Question lQuestion = BOMHelper.getQuestionHome().getQuestion(lQuestionID);
			//get group
			Long lGroupID = getGroupID();
			Group lGroup = BOMHelper.getGroupHome().getGroup(lGroupID);
			
			loadContextMenu(lGroup.needsReview() ? Constants.MENU_SET_ID_CONTRIBUTE : Constants.MENU_SET_ID_PROCESS);
			
			//check guest depth
			boolean isGuest = getActor().isGuest();
			Long lGuestDepth = lGroup.getGuestDepth();
			if (isGuest) {
				checkGuestDepth(lQuestionID, lGuestDepth);
			}
			//get children
			QueryResult lChildren = new EmptyQueryResult(BOMHelper.getQuestionHome());
			if (isGuest) {
				if (isVisibleForGuestDepth(lQuestionID, lGuestDepth-1)) {
					lChildren = BOMHelper.getJoinQuestionToChildHome().getPublishedChilds(lQuestionID);
				}
			}
			else {
				lChildren = BOMHelper.getJoinQuestionToChildHome().getPublishedChilds(lQuestionID);
			}
			
			//buttons
			SwitchHelper lSwitchHelper = new SwitchHelper();
			Long lActorID = getActor().getActorID();
			if (lActorID.intValue() >= 0) {
				lSwitchHelper = new SwitchHelper(BOMHelper.getSubscriptionHome().hasSubscription(lQuestionID, lActorID), 
						BOMHelper.getBookmarkHome().hasBookmark(lQuestionIDs, lActorID), lQuestionID, lActorID);
			}
			
			CodeList lCodeList = CodeListHome.instance().getCodeList(QuestionState.class, getAppLocale().getLanguage());
			questionView = new QuestionView(lGroup, lQuestion, lCodeList,
					lQuestion.isRoot() ? QuestionContainer.createEmpty() : QuestionContainer.createData(BOMHelper.getQuestionHierarchyHome().getParentQuestion(lQuestionID)),
					QuestionContainer.createData(lChildren),
					getAuthors(lQuestionIDs),
					getReviewers(lQuestionIDs),
					CompletionsHelper.getNormalizedCompletions(getPublishedCompletions(lQuestionID)),
					getPublishedBibliography(lQuestionID),
					lSwitchHelper, isGuest, this);
			return questionView;
		} 
		catch (GuestDepthException exc) {
			throw exc;
		}
		catch (Exception exc) {
			throw createContactAdminException(exc);
		}
	}

	private void checkGuestDepth(Long inQuestionID, Long inDepth) throws GuestDepthException {
		if (!isVisibleForGuestDepth(inQuestionID, inDepth)) {
			throw new GuestDepthException(Activator.getMessages().getMessage("errmsg.guest.notVisible")); //$NON-NLS-1$
		}
	}
	
	private boolean isVisibleForGuestDepth(Long inQuestionID, Long inDepth) {
		return BOMHelper.getQuestionHierarchyHome().isVisibleForGuestDepth(inQuestionID, inDepth);
	}

	/**
	 * Handle click events on table of parent or child questions.
	 * 
	 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
	 */
	public void valueChange(ValueChangeEvent inEvent) {
		Property lProperty = inEvent.getProperty();
		if (questionView.checkSelectionSource(lProperty)) {			
			if (lProperty instanceof Table) {
				Object lValue = ((Table) lProperty).getValue();
				if (lValue instanceof QuestionWrapper) {
					setQuestionID(((QuestionWrapper) lValue).getQuestionID());
					sendEvent(QuestionShowTask.class);
				}
			}
		}
	}
	
}
