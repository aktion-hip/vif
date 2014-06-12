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

import org.hip.kernel.exc.VException;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.exc.BOMChangeValueException;
import org.hip.vif.core.util.BeanWrapperHelper;
import org.hip.vif.forum.groups.Activator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Window.Notification;


/**
 * Task to display a question in editable form.
 * 
 * Created on 13.08.2003
 * @author Luthiger
 */
@Partlet
public class QuestionEditTask extends AbstractQuestionTask {
	private static final Logger LOG = LoggerFactory.getLogger(QuestionEditTask.class);
	
	private Question question = null;
	
	private Question getQuestion() throws VException, SQLException {
		if (question == null) {
			question = BOMHelper.getQuestionHome().getQuestion(getQuestionID().toString());
		}
		return question;
	}
	
	@Override
	protected Long getParentQuestionID() throws VException, SQLException {
		return BeanWrapperHelper.getLong(QuestionHierarchyHome.KEY_PARENT_ID, BOMHelper.getQuestionHierarchyHome().getParent(getQuestionID()));
	}

	@Override
	protected String getQuestionText() throws VException, SQLException {
		return BeanWrapperHelper.getString(QuestionHome.KEY_QUESTION, getQuestion());
	}

	@Override
	protected String getRemarkText() throws VException, SQLException {
		return BeanWrapperHelper.getString(QuestionHome.KEY_REMARK, getQuestion());
	}
	
	@Override
	public boolean saveQuestion(String inQuestion, String inRemark) {
		try {
			getQuestion().ucSave(cleanUp(inQuestion), cleanUp(inRemark), getActor().getActorID());
			showNotification(Activator.getMessages().getMessage("msg.task.data.changed"), Notification.TYPE_TRAY_NOTIFICATION); //$NON-NLS-1$
			sendEvent(ContributionsListTask.class);
			return true;
		} catch (BOMChangeValueException exc) {
			LOG.error("Error while saving the question.", exc); //$NON-NLS-1$
		} catch (VException exc) {
			LOG.error("Error while saving the question.", exc); //$NON-NLS-1$
		} catch (SQLException exc) {
			LOG.error("Error while saving the question.", exc); //$NON-NLS-1$
		}
		return false;
	}

}
