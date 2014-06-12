/*
	This package is part of the application VIF
	Copyright (C) 2004, Benno Luthiger

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public
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

package org.hip.vif.web.stale;

import java.io.IOException;
import java.sql.SQLException;

import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.impl.NotificationTextCollector;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.markup.TextParser;
import org.hip.vif.web.Activator;
import org.hip.vif.web.stale.StaleRequestHelper.CompletionCollector;
import org.hip.vif.web.stale.StaleRequestHelper.QuestionCollector;
import org.hip.vif.web.stale.StaleRequestHelper.TextCollector;

/**
 * Visitor class to collect the contributions whose request for review is pending since long time.
 * 
 * @author Benno Luthiger
 */
public class StaleTextCollector extends NotificationTextCollector {	
	private final static String KEY_QUESTION 		= "mail.stale.question"; //$NON-NLS-1$
	private final static String KEY_REMARK 			= "mail.stale.remark"; //$NON-NLS-1$
	private final static String KEY_COMPLETION 		= "mail.stale.completion"; //$NON-NLS-1$
	private final static String KEY_BIBLIOGRAPHY 	= "mail.stale.bibliography"; //$NON-NLS-1$

	private String questionMessage 		= ""; //$NON-NLS-1$
	private String remarkMessage 		= ""; //$NON-NLS-1$
	private String completionMessage 	= ""; //$NON-NLS-1$
	private String bibliographyMessage 	= ""; //$NON-NLS-1$
	
	private TextParser markupParser = new TextParser();
	private StringBuilder notificationText;
	private StringBuilder notificationTextHtml;
	private IMessages messages = Activator.getMessages();
	
	/**
	 * NotificationTextCollector constructor.
	 */
	public StaleTextCollector() {
		super();
		notificationText = new StringBuilder();
		notificationTextHtml = new StringBuilder();
	}
	
	/**
	 * Execute the visit of a Question entry in the QuestionHierarchy.
	 * 
	 * @param inQuestionCollector Question
	 * @throws VException
	 * @throws SQLException
	 */
	public void visitQuestion(QuestionCollector inQuestionCollector) {
		notificationText.append(questionMessage).append(" ").append(inQuestionCollector.getDecimal()).append(": ").append(inQuestionCollector.getContributionTitle()).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		notificationText.append(remarkMessage).append(": ").append(inQuestionCollector.getRemark()).append("\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
		notificationTextHtml.append("<p><b>").append(questionMessage).append(" <i>").append(inQuestionCollector.getDecimal()).append("</i>:</b></p>").append(markupParser.parseToHtml(inQuestionCollector.getContributionTitle())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		notificationTextHtml.append("<p><b>").append(remarkMessage).append(":</b></p>").append(markupParser.parseToHtml(inQuestionCollector.getRemark())); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Execute the visit of a Completion entry in the QuestionHierarchy.
	 * 
	 * @param inCompletion Completion
	 * @throws VException
	 * @throws SQLException
	 */
	public void visitCompletion(CompletionCollector inCompletion) {		
		notificationText.append(completionMessage).append(" (").append(inCompletion.getDecimalID()).append("): ").append(inCompletion.getContributionTitle()).append("\n\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		notificationTextHtml.append("<p><b>").append(completionMessage).append(" <i>(").append(inCompletion.getDecimalID()).append(")</i>:</b></p>").append(markupParser.parseToHtml(inCompletion.getContributionTitle())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public void visitText(TextCollector inText) {
		notificationText.append(bibliographyMessage).append("\n\n").append(inText.getContentPlain()); //$NON-NLS-1$
		notificationTextHtml.append("<p><b>").append(bibliographyMessage).append(":</b></p>").append(inText.getContentHtml()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	
	/**
	 * Returns the collected text to notify the users about the changes.
	 * 
	 * @return StringBuilder
	 */
	public StringBuilder getNotificationText() {
		return notificationText;
	}
	public StringBuilder getNotificationTextHtml() {
		return notificationTextHtml;
	}
	
	/**
	 * Same as <code>getNotificationText()</code>.
	 * 
	 * @return StringBuilder
	 */
	public StringBuilder getNotificationTextWithIntro() {
		return getNotificationText();
	}
	/**
	 * Same as <code>getNotificationTextHtml()</code>.
	 * 
	 * @return StringBuilder
	 */
	public StringBuilder getNotificationTextHtmlWithIntro() {
		return getNotificationTextHtml();
	}
		
	/**
	 * Resets the instance (i.e. the StringBuffer).
	 * 
	 * @throws IOException
	 */
	public void reset() throws IOException {
		notificationText = new StringBuilder();
		notificationTextHtml = new StringBuilder();
		initMessages();
	}
	
	/**
	 * Adds the author's name to the notification text.
	 * 
	 * @param inAuthorName String
	 */
	public void setMadeBy(String inAuthorName) {
		//intentionally left empty
	}
	
	private void initMessages() throws IOException {
		questionMessage = messages.getMessage(KEY_QUESTION);
		remarkMessage = messages.getMessage(KEY_REMARK);
		completionMessage = messages.getMessage(KEY_COMPLETION);
		bibliographyMessage = messages.getMessage(KEY_BIBLIOGRAPHY);
	}	
	
}
