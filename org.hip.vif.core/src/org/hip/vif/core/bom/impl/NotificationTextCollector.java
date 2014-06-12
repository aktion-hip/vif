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

package org.hip.vif.core.bom.impl;

import java.io.IOException;
import java.sql.SQLException;

import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VObject;
import org.hip.vif.core.Activator;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.service.PreferencesHandler;
import org.hip.vif.core.util.HtmlCleaner;

/**
 * Visitor class to collect the notification texts of contributions.
 * 
 * @author Benno Luthiger
 * Created on Mar 5, 2004
 */
public class NotificationTextCollector extends VObject implements QuestionHierarchyVisitor {
	//constants
	private final static String KEY_QUESTION 	= "org.hip.vif.msg.question.Question";
	private final static String KEY_REMARK 		= "org.hip.vif.msg.question.Remark";
	private final static String KEY_COMPLETION 	= "org.hip.vif.msg.question.Completion";
	private final static String KEY_BIBLIOGRAPHY 	= "org.hip.vif.msg.question.Bibliography";
	private final static String KEY_GREETINGS 	= "org.hip.vif.msg.mail.greetings";
	private final static String KEY_INTRO 		= "org.hip.vif.msg.mail.publish.intro";
	private final static String KEY_MADE_BY		= "org.hip.vif.msg.mail.publish.madeby";

	private StringBuilder greetingsMessage = new StringBuilder();
	private StringBuilder greetingsMessageHtml = new StringBuilder();
	private String introMessage			= "";
	private String questionMessage 		= "";
	private String remarkMessage 		= "";
	private String completionMessage 	= "";
	private String bibliographyMessage 	= "";
	
	private StringBuilder notificationText;
	private StringBuilder notificationTextHtml;
	private IMessages messages = Activator.getMessages();
	
	/**
	 * NotificationTextCollector constructor.
	 */
	public NotificationTextCollector() {
		super();
		notificationText = new StringBuilder();
		notificationTextHtml = new StringBuilder();
	}
	
	/**
	 * Execute the visit of a Question entry in the QuestionHierarchy.
	 * 
	 * @param inQuestion Question
	 * @throws VException
	 * @throws SQLException
	 */
	public void visitQuestion(Question inQuestion) throws VException {
		Object lDecimal = inQuestion.get(QuestionHome.KEY_QUESTION_DECIMAL);
		String lQuestion = inQuestion.get(QuestionHome.KEY_QUESTION).toString();
		String lRemark = inQuestion.get(QuestionHome.KEY_REMARK).toString();
		
		notificationText.append(questionMessage).append(" ").append(lDecimal).append(": ").append(HtmlCleaner.toPlain(lQuestion)).append("\n");
		notificationText.append(remarkMessage).append(": ").append(HtmlCleaner.toPlain(lRemark)).append("\n\n");
		notificationTextHtml.append("<p><b>").append(questionMessage).append(" <i>").append(lDecimal).append("</i>:</b></p>").append(lQuestion);
		notificationTextHtml.append("<p><b>").append(remarkMessage).append(":</b></p>").append(lRemark);
	}
	
	/**
	 * Execute the visit of a Completion entry in the QuestionHierarchy.
	 * 
	 * @param inCompletion Completion
	 * @throws VException
	 * @throws SQLException
	 */
	public void visitCompletion(Completion inCompletion) throws VException {
		String lCompletion = inCompletion.get(CompletionHome.KEY_COMPLETION).toString();
		notificationText.append(completionMessage).append(" (").append(inCompletion.getDecimalID()).append("): ").append(HtmlCleaner.toPlain(lCompletion)).append("\n\n");
		notificationTextHtml.append("<p><b>").append(completionMessage).append(" <i>(").append(inCompletion.getDecimalID()).append(")</i>:</b></p>").append(lCompletion);
	}

	/**
	 * Execute the visit of a Completion entry in the QuestionHierarchy.
	 * 
	 * @param inCompletion JoinCompletionToQuestion
	 * @throws VException
	 * @throws SQLException
	 */
	public void visitCompletion(JoinCompletionToQuestion inCompletion) throws VException, SQLException {
		String lCompletion = inCompletion.get(CompletionHome.KEY_COMPLETION).toString();
		notificationText.append(completionMessage).append(" (").append(inCompletion.get(QuestionHome.KEY_QUESTION_DECIMAL)).append("): ").append(HtmlCleaner.toPlain(lCompletion)).append("\n\n");
		notificationTextHtml.append("<p><b>").append(completionMessage).append(" <i>(").append(inCompletion.get(QuestionHome.KEY_QUESTION_DECIMAL)).append(")</i>:</b></p>").append(lCompletion);
	}

	public void visitText(Text inText) throws VException, SQLException {
		notificationText.append(bibliographyMessage).append("\n\n").append(inText.getNotification());
		notificationTextHtml.append("<p><b>").append(bibliographyMessage).append(":</b></p>").append(inText.getNotificationHtml());
	}
	
	/**
	 * Execute the visit of a JoinSubscriptionToMember entry in the QuestionHierarchy.
	 * 
	 * @param inSubscriber JoinSubscriptionToMember
	 * @throws VException
	 * @throws SQLException
	 */
	public void visitSubscriber(JoinSubscriptionToMember inSubscriber) throws VException, SQLException {
		//left empty intentionally
	}
	
	/**
	 * Execute the visit of a JoinQuestionToChild entry in the QuestionHierarchy.
	 * 
	 * @param inChild JoinQuestionToChild
	 * @throws VException
	 * @throws SQLException
	 */
	public void visitChild(JoinQuestionToChild inChild) throws VException, SQLException {
		//left empty intentionally
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
	 * Returns the collected text to notify the users about the changes.
	 * Version with intro message (messages published) and greetings.
	 * 
	 * @return StringBuilder
	 */
	public StringBuilder getNotificationTextWithIntro() {
		StringBuilder outNotification = new StringBuilder(introMessage).append("\n\n").append(notificationText);
		return outNotification.append(greetingsMessage);
	}
	public StringBuilder getNotificationTextHtmlWithIntro() {
		StringBuilder outNotification = new StringBuilder("<p>").append(introMessage).append("</p>").append(notificationTextHtml);
		return outNotification.append(greetingsMessageHtml);
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
		String lMessage = messages.getFormattedMessage(KEY_MADE_BY, inAuthorName);
		notificationText.append(lMessage).append("\n\n");
		notificationTextHtml.append("<p>").append(lMessage).append("</p>");
	}
	
	private void initMessages() throws IOException {
		introMessage = messages.getMessage(KEY_INTRO);
		questionMessage = messages.getMessage(KEY_QUESTION);
		remarkMessage = messages.getMessage(KEY_REMARK);
		completionMessage = messages.getMessage(KEY_COMPLETION);
		bibliographyMessage = messages.getMessage(KEY_BIBLIOGRAPHY);
		PreferencesHandler lPreferences = PreferencesHandler.INSTANCE;
		if (greetingsMessage.length() == 0) {
			greetingsMessage.append("\n").append(messages.getMessage(KEY_GREETINGS)).append("\n\n").append(lPreferences.get(PreferencesHandler.KEY_MAIL_NAMING));
		}
		if (greetingsMessageHtml.length() == 0) {
			greetingsMessageHtml.append("<p>").append(messages.getMessage(KEY_GREETINGS)).append("</p><p>").append(lPreferences.get(PreferencesHandler.KEY_MAIL_NAMING)).append("</p>");
		}
	}
	
}
