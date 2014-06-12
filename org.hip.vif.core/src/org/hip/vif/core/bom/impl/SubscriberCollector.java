/*
	This package is part of the application VIF.
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

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VObject;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.MemberHome;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.SubscriptionHome;
import org.hip.vif.core.bom.Text;

/**
 * Visitor class to collect the subscriber's mail address.
 * 
 * @author Benno Luthiger
 * Created on Feb 16, 2004
 */
public class SubscriberCollector extends VObject implements QuestionHierarchyVisitor {
	private final static String SEPARATOR = ", ";
	
	private boolean filteredSelect = false;
	private StringBuffer mailAddresses = new StringBuffer();
	
	/**
	 * SubscriberCollector constructor.
	 * 
	 * @param inFilteredSelect boolean If true, the visitor selects only subtree subscribers.
	 */
	public SubscriberCollector(boolean inFilteredSelect) {
		super();
		filteredSelect = inFilteredSelect;
	}

	/**
	 * Execute the visit of a Completion entry in the QuestionHierarchy.
	 * 
	 * @param inCompletion Completion
	 * @throws VException
	 * @throws SQLException
	 */
	public void visitCompletion(Completion inCompletion) throws VException {
		//left empty intentionally
	}

	public void visitText(Text inText) throws VException, SQLException {
		// intentionally left empty: we don't subscribe to text entries.
	}
	
	/**
	 * Execute the visit of a Completion entry in the QuestionHierarchy.
	 * 
	 * @param inCompletion JoinCompletionToQuestion
	 * @throws VException
	 * @throws SQLException
	 */
	public void visitCompletion(JoinCompletionToQuestion inCompletion) throws VException, SQLException {
		// intentionally left empty
	}
	
	/**
	 * Execute the visit of a Question entry in the QuestionHierarchy.
	 * 
	 * @param inQuestion Question
	 * @throws VException
	 * @throws SQLException
	 */
	public void visitQuestion(Question inQuestion) throws VException, SQLException {
		Long lQuestionID = new Long(inQuestion.get(QuestionHome.KEY_ID).toString());
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(SubscriptionHome.KEY_QUESTIONID, lQuestionID);
		if (filteredSelect) {
			lKey.setValue(SubscriptionHome.KEY_LOCAL, new Integer(0));
		}
		QueryResult lResult = BOMHelper.getJoinSubscriptionToMemberHome().select(lKey);
		while (lResult.hasMoreElements()) {
			((JoinSubscriptionToMember)lResult.next()).accept(this);
		}
	}
	
	/**
	 * Execute the visit of a JoinSubscriptionToMember entry in the QuestionHierarchy.
	 * 
	 * @param inSubscriber JoinSubscriptionToMember
	 * @throws VException
	 * @throws SQLException
	 */
	public void visitSubscriber(JoinSubscriptionToMember inSubscriber) throws VException, SQLException {
		String lMail = (String)inSubscriber.get(MemberHome.KEY_MAIL);
		if (mailAddresses.indexOf(lMail) < 0) {
			if (mailAddresses.length() > 0) {
				mailAddresses.append(SEPARATOR);
			}
			mailAddresses.append(lMail);
		}
	}
	
	/**
	 * Execute the visit of a JoinQuestionToChild entry in the QuestionHierarchy.
	 * 
	 * @param inChild JoinQuestionToChild
	 * @throws VException
	 * @throws SQLException
	 */
	public void visitChild(JoinQuestionToChild inChild) throws VException, SQLException {
		// intentionally left empty
	}
	
	public String getMailAddresses() {
		return new String(mailAddresses);
	}
	
	/**
	 * This method allows to update the selection behaviour of the collector.
	 * 
	 * @param inFilteredSelect
	 */
	public void updateSelectionFilter(boolean inFilteredSelect) {
		filteredSelect = inFilteredSelect;
	}
	
	/**
	 * Checks for the specified address and deletes it, if it exists.
	 * 
	 * @param inMailAddress String
	 */
	public void checkFor(String inMailAddress) {
		int lSeparatorLength = SEPARATOR.length();
		int lSequenceStart = mailAddresses.indexOf(inMailAddress);
		int lSequenceLength = inMailAddress.length();
		
		if (lSequenceStart < 0) return;
		if (lSequenceStart > 0) {
			if (!SEPARATOR.equals(mailAddresses.substring(lSequenceStart-lSeparatorLength, lSequenceStart))) return;
			mailAddresses.delete(lSequenceStart-lSeparatorLength, lSequenceStart+lSequenceLength);
			return;
		}
		if (lSequenceStart == 0) {
			mailAddresses.delete(lSequenceStart, lSequenceStart+lSequenceLength);
			if (mailAddresses.length() > 0) {
				mailAddresses.delete(0, lSeparatorLength);
			}
		}
	}
	
}
