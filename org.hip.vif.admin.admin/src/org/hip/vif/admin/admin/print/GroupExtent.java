/**
 This package is part of the administration of the application VIF.
 Copyright (C) 2008-2014, Benno Luthiger

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
package org.hip.vif.admin.admin.print;

import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.kernel.sys.VSys;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.impl.JoinQuestionToCompletionAndContributors;
import org.hip.vif.core.bom.impl.JoinQuestionToCompletionAndContributorsHome;
import org.hip.vif.core.bom.impl.JoinQuestionToContributors;
import org.hip.vif.core.bom.impl.JoinQuestionToContributorsHome;
import org.hip.vif.core.util.IQueryStrategy;
import org.hip.vif.web.bom.Group;
import org.hip.vif.web.bom.VifBOMHelper;

/**
 * Helper class, provides all queries (<code>IQueryStrategy</code>) to retrieve
 * the content of a group. Useful as parameter object.
 * 
 * @author Luthiger Created: 20.09.2009
 */
public class GroupExtent {
	private Long groupID;
	// information contained in the group table entry
	private Group group;
	// all questions in the group and its authors/reviewers
	private IQueryStrategy questions;
	// all completions of the group's questions and its authors/reviewers
	private IQueryStrategy completions;

	private OrderObject order;

	/**
	 * GroupExtent constructor.
	 * 
	 * @param inGroupID
	 *            Long the group's ID.
	 * @throws VException
	 */
	public GroupExtent(final Long inGroupID) throws VException {
		groupID = inGroupID;
		order = createOrder();
		group = VifBOMHelper.getGroupHome().getGroup(inGroupID);
		questions = new IQueryStrategy() {
			@Override
			public QueryResult getQueryResult() throws Exception {
				return ((JoinQuestionToContributorsHome) VSys.homeManager
						.getHome(JoinQuestionToContributors.HOME_CLASS_NAME))
						.selectOfGroup(inGroupID, order);
			}
		};
		completions = new IQueryStrategy() {
			@Override
			public QueryResult getQueryResult() throws Exception {
				return ((JoinQuestionToCompletionAndContributorsHome) VSys.homeManager
						.getHome(JoinQuestionToCompletionAndContributors.HOME_CLASS_NAME))
						.selectOfGroup(inGroupID, order);
			}
		};
	}

	private OrderObject createOrder() throws VException {
		final OrderObject outOrder = new OrderObjectImpl();
		outOrder.setValue(QuestionHome.KEY_ID, 1);
		return outOrder;
	}

	/**
	 * @return Long the group's ID.
	 */
	public Long getGroupID() {
		return groupID;
	}

	/**
	 * @return Group the group model object.
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * @return QueryResult containing all published questions of the group.
	 * @throws Exception
	 */
	public QueryResult getQuestions() throws Exception {
		return questions.getQueryResult();
	}

	/**
	 * @return QueryResult containing all published completions of the group.
	 * @throws Exception
	 */
	public QueryResult getCompletions() throws Exception {
		return completions.getQueryResult();
	}

}
