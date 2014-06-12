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

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Group;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.impl.TextQuestionHome;
import org.hip.vif.core.bom.impl.WorkflowAwareContribution;
import org.hip.vif.core.code.QuestionState;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.util.AutoCompleteHelper;
import org.hip.vif.core.util.ParameterObject;
import org.hip.vif.forum.groups.Activator;
import org.hip.vif.forum.groups.Constants;
import org.hip.vif.forum.groups.data.ContributionContainer;
import org.hip.vif.forum.groups.data.ContributionWrapper;
import org.hip.vif.forum.groups.ui.BibliographySearchView;
import org.hip.vif.web.util.LinkButtonHelper.LookupType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window.Notification;


/**
 * Task to search for a Bibliography entry for that it can be linked to a question.
 *
 * @author Luthiger
 * Created: 14.06.2010
 */
@SuppressWarnings("serial")
@Partlet
public class BibliographyHandleTask extends AbstractBibliographyTask implements ValueChangeListener {
	private static final Logger LOG = LoggerFactory.getLogger(BibliographyHandleTask.class);
	
	private ContributionContainer texts;
	
	@Override
	protected String needsPermission() {
		return Constants.PERMISSION_EDIT_BIBLIOGRAPHY;
	}

	@Override
	protected Component runChecked() throws VException {
		IMessages lMessages = Activator.getMessages();
		
		try {
			Group lGroup = BOMHelper.getGroupHome().getGroup(getGroupID());
			if (!lGroup.isActive()) {
				return reDisplay(lMessages.getMessage("errmsg.not.active"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
			}
			
			if (!lGroup.isParticipant(getActor().getActorID())) {
				return reDisplay(lMessages.getMessage("errmsg.not.participant"), Notification.TYPE_WARNING_MESSAGE); //$NON-NLS-1$
			}
			
			loadContextMenu(Constants.MENU_SET_ID_GROUP_CONTENT);
			
			String lQuestionID = getQuestionID().toString();
			Question lQuestion = BOMHelper.getQuestionHome().getQuestion(lQuestionID);
			
			KeyObject lKey = BOMHelper.getKeyStates(TextHome.KEY_STATE, WorkflowAwareContribution.STATES_PUBLISHED);
			AutoCompleteHelper lHelper = new AutoCompleteHelper(BOMHelper.getTextHome(), lKey);
			return new BibliographySearchView(lQuestion, lGroup, 
					 lHelper.getTitlesContainer(),
					 lHelper.getAuthorsContainer(),
					 this);
		}
		catch (Exception exc) {
			throw createContactAdminException(exc);
		}
	}

	/**
	 * Do search for titles or authors.
	 * 
	 * @param inTitle Object, input, may be <code>null</code>
	 * @param inAuthor Object, input, may be <code>null</code>
	 * @return boolean <code>true</code> if the input has been processed successfully, <code>false</code> else.
	 */
	public boolean searchFor(Object inTitle, Object inAuthor) {
		try {
			QueryResult lSearchResult = BOMHelper.getTextHome().selectTitleOrAuthor(inTitle == null ? "" : inTitle.toString(),  //$NON-NLS-1$
					inAuthor == null ? "" : inAuthor.toString()); //$NON-NLS-1$
			if (lSearchResult.hasMoreElements()) {
				//display list of search results
				texts = ContributionContainer.createData(lSearchResult, CodeListHome.instance().getCodeList(QuestionState.class, getAppLocale().getLanguage()));
				return true;
			}
			else {
				return createNew(inTitle, inAuthor);
			}
		} catch (VException exc) {
			LOG.error("Error while searching for bibliography items.", exc); //$NON-NLS-1$
		} catch (SQLException exc) {
			LOG.error("Error while searching for bibliography items.", exc); //$NON-NLS-1$
		}
		return false;
	}

	/**
	 * Display form to create bibliographical entry.
	 * 
	 * @param inTitle
	 * @param inAuthor
	 * @return boolean <code>true</code> if the input has been processed successfully, <code>false</code> else.
	 */
	public boolean createNew(Object inTitle, Object inAuthor) {
		try {
			ParameterObject lParameters = new ParameterObject();
			lParameters.set(KEY_PARAMETER_TITLE, inTitle == null ? "" : inTitle.toString()); //$NON-NLS-1$
			lParameters.set(KEY_PARAMETER_AUTHOR, inAuthor == null ? "" : inAuthor.toString()); //$NON-NLS-1$
			setParameters(lParameters);
			sendEvent(BibliographyNewTask.class);
			return true;
		} 
		catch (VException exc) {
			LOG.error("Error while displaying the view to create a new bibliographical entry.", exc); //$NON-NLS-1$
		}
		return false;
	}	

	/**
	 * Returns the data container containing the text entries according to the input in the combo boxes.
	 * 
	 * @return {@link ContributionContainer}
	 */
	public ContributionContainer getTexts() {
		return texts;
	}

	/**
	 * Adds the selected bibliography items.
	 */
	public void addBibliography() {
		TextQuestionHome lHome = BOMHelper.getTextQuestionHome();
		int lCount = 0;
		try {
			for (ContributionWrapper lText : texts.getItemIds()) {
				if (!lText.isChecked()) {
					continue;
				}
				lCount++;
				lHome.createEntry(splitTextID(lText.getID()), getQuestionID().toString());
			}
		} catch (VException exc) {
			LOG.error("Error while linking the bibliography item to the question.", exc); //$NON-NLS-1$
		} catch (SQLException exc) {
			LOG.error("Error while linking the bibliography item to the question.", exc); //$NON-NLS-1$
		}
		
		IMessages lMessages = Activator.getMessages();
		if (lCount == 0) {
			showNotification(lMessages.getMessage("errmsg.save.general"), Notification.TYPE_ERROR_MESSAGE); //$NON-NLS-1$
			return;
		}
		showNotification(lMessages.getMessage(lCount == 1 ? "msg.bibliography.addedS" : "msg.bibliography.addedM"), Notification.TYPE_TRAY_NOTIFICATION); //$NON-NLS-1$ //$NON-NLS-2$		
		sendEvent(QuestionShowTask.class);
	}
	
	private Long splitTextID(String inTextID) {
		return Long.parseLong(inTextID.split("-")[0]); //$NON-NLS-1$
	}

	/**
	 * Displays the selected bibliography item in edit view.
	 */
	public void editBibliography() {
		for (ContributionWrapper lText : texts.getItemIds()) {
			if (!lText.isChecked()) {
				continue;
			}
			setTextID(lText.getID());
			sendEvent(BibliographyEditPublishedTask.class);
			return;
		}
	}

	/**
	 * Method called when user clicks the table of the search results =>
	 * show bibliography entry in popup window.
	 * 
	 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
	 */
	public void valueChange(ValueChangeEvent inEvent) {
		Property lProperty = inEvent.getProperty();
		if (lProperty instanceof Table) {
			Object lValue = ((Table) lProperty).getValue();
			if (lValue instanceof ContributionWrapper) {
				Long lID = splitTextID(((ContributionWrapper) lValue).getID());
				requestLookup(LookupType.BIBLIOGRAPHY, lID);
			}
		}
	}

}
