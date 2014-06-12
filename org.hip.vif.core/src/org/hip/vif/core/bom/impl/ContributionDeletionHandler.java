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
package org.hip.vif.core.bom.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.XMLSerializer;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VObject;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Completion;
import org.hip.vif.core.bom.CompletionHome;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.TextHome;
import org.hip.vif.core.bom.VIFWorkflowAware;
import org.hip.vif.markup.serializer.MarkupToHtmlSerializer;

/**
 * Visitor class to collect the nodes and dependents about to be deleted.
 * 
 * @author Benno Luthiger
 * Created on Mar 12, 2004
 */
public class ContributionDeletionHandler extends VObject implements QuestionHierarchyVisitor {
	private Collection<Object> questionIDs;
	private Collection<Object> completionIDs;
	protected Collection<Object> textIDs;
	protected XMLSerializer serializer;
	
	/**
	 * ContributionDeletionHandler
	 */
	public ContributionDeletionHandler() {
		super();
		questionIDs = new Vector<Object>();
		completionIDs = new Vector<Object>();
		textIDs = new Vector<Object>();
		serializer = new MarkupToHtmlSerializer(); //new MarkupToPlainSerializer()
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

	/**
	 * Execute the visit of a Completion entry in the QuestionHierarchy.
	 * 
	 * @param inCompletion JoinCompletionToQuestion
	 * @throws VException
	 * @throws SQLException
	 */
	public void visitCompletion(JoinCompletionToQuestion inCompletion) throws VException, SQLException {
		inCompletion.accept(serializer);
		completionIDs.add(inCompletion.get(CompletionHome.KEY_ID));
	}
	
	/**
	 * Execute the visit of a Question entry in the QuestionHierarchy.
	 * 
	 * @param inQuestion Question
	 * @throws VException
	 * @throws SQLException
	 */
	public void visitQuestion(Question inQuestion) throws VException, SQLException {
		inQuestion.accept(serializer);
		questionIDs.add(inQuestion.get(QuestionHome.KEY_ID));
	}

	/**
	 * We only delete private text entries
	 */
	public void visitText(Text inText) throws VException, SQLException {
		// we only delete private text entry versions
		if (!((VIFWorkflowAware)inText).isPrivate()) return;
		inText.accept(serializer);
		textIDs.add(inText.getIDVersion());
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
		inChild.accept(serializer);
		questionIDs.add(inChild.get(QuestionHome.KEY_ID));
	}
	
	/**
	 * Returns the visited domain objects as serialized XML.
	 * 
	 * @return String
	 */
	public String getConfirmationXML() {
		return serializer.toString();
//		return serializer.toString().replaceAll("&", "&amp;");
	}
	
	/**
	 * Returns the contributions selected for deletion.
	 * 
	 * @return Collection
	 * @throws VException
	 * @throws SQLException
	 */
	public Collection<VIFWorkflowAware> getContributions() throws VException, SQLException {
		Collection<VIFWorkflowAware> outContributions = new Vector<VIFWorkflowAware>();
		if (canSelect(questionIDs)) {
			QueryResult lQuestion = BOMHelper.getQuestionHome().select(getKey(questionIDs, QuestionHome.KEY_ID));
			while (lQuestion.hasMoreElements()) {
				outContributions.add((VIFWorkflowAware)lQuestion.next());
			}
		}

		if (canSelect(completionIDs)) {
			QueryResult lCompletions = BOMHelper.getCompletionHome().select(getKey(completionIDs, CompletionHome.KEY_ID));
			while (lCompletions.hasMoreElements()) {
				outContributions.add((VIFWorkflowAware)lCompletions.next());
			}
		}
		
		if (canSelect(textIDs)) {
			TextHome lTextHome = BOMHelper.getTextHome();
			for (Object lTextID : textIDs) {
				outContributions.add((VIFWorkflowAware)lTextHome.getText(lTextID.toString()));
			}
		}
		
		return outContributions;
	}
	
	private KeyObject getKey(Collection<Object> inIDs, String inPropertyName) throws VException {
		KeyObject outKey = new KeyObjectImpl();
		KeyObject lKey;
		for (Object lID : inIDs) {
			lKey = new KeyObjectImpl();
			lKey.setValue(inPropertyName, new BigDecimal(lID.toString()));
			outKey.setValue(lKey, BinaryBooleanOperator.OR);			
		}
		return outKey;
	}
	
	private boolean canSelect(Collection<Object> inIDs) {
		return inIDs.size() > 0;
	}
	
	/**
	 * Returns the number of contributions selected for deletion.
	 * 
	 * @return int
	 */
	public int getNumberOfContributions() {
		return questionIDs.size() + completionIDs.size() + textIDs.size();
	}
	
	/**
	 * @return String the name of the transition to delete the entries, e.g. <code>WorkflowAwareContribution.TRANS_DELETE</code>
	 */
	public String getTransitionName() {
		return WorkflowAwareContribution.TRANS_DELETE;
	}
	
}
