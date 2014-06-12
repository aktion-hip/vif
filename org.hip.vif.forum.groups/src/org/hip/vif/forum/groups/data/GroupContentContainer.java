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

package org.hip.vif.forum.groups.data;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHome;
import org.hip.vif.core.bom.impl.QuestionHierarchyHomeImpl.ChildrenChecker;

import com.vaadin.data.util.HierarchicalContainer;

/**
 * Container to manage the content (i.e. the contributions of a discussion group).
 * Usage: <pre>GroupContentContainer.createData(questions, childrenChecker, codeList, expandDepth)</pre>
 * 
 * @author Luthiger
 * Created: 22.05.2011
 */
@SuppressWarnings("serial")
public class GroupContentContainer extends HierarchicalContainer {
	
	private GroupContentWrapper root;
	private int depth = 0;

	/**
	 * Private constructor
	 * 
	 * @param inTreeExpandDepth int the depth up to which the tree of question nodes should be displayed expanded
	 */
	private GroupContentContainer(int inTreeExpandDepth) {
		depth = inTreeExpandDepth;
	}

	/**
	 * Factory method to create a <code>GroupContentContainer</code>.
	 * 
	 * @param inContent {@link QueryResult} the questions of the discussion groups
	 * @return {@link GroupContentContainer} helper class to build the question hierarchy
	 * @param inCodeList {@link CodeList} the code list for question states 
	 * @param int inTreeExpandDepth the depth of the tree to show the nodes expanded
	 * @throws SQLException 
	 * @throws VException 
	 */
	public static GroupContentContainer createData(QueryResult inContent, ChildrenChecker inChecker, CodeList inCodeList, int inTreeExpandDepth) throws VException, SQLException {
		GroupContentContainer out = new GroupContentContainer(inTreeExpandDepth);
		Map<Long, GroupContentWrapper> lIdWrapperMap = new HashMap<Long, GroupContentWrapper>();
		Set<Long> lChildren = new TreeSet<Long>();
		
		while (inContent.hasMoreElements()) {
			//preparation
			Question lQuestion = (Question) inContent.nextAsDomainObject();
			Long lQuestionID = (Long)lQuestion.get(QuestionHome.KEY_ID);

			GroupContentWrapper lQuestionWrapped = GroupContentWrapper.createItem(lQuestion, inCodeList);
			if (lQuestion.isRoot()) {
				out.root = lQuestionWrapped;
			}
			else {
				lChildren.add(lQuestionID);				
			}
			lIdWrapperMap.put(lQuestionID, lQuestionWrapped);

			//first set all tree items as leaf (i.e. without children)
			out.addItem(lQuestionWrapped);
			out.setChildrenAllowed(lQuestionWrapped, false);
		}
		//set parents
		for (Long lChildID : lChildren) {
			GroupContentWrapper lParent = lIdWrapperMap.get(inChecker.getParentID(lChildID));
			//then allow children for identified parents only
			out.setChildrenAllowed(lParent, true);
			out.setParent(lIdWrapperMap.get(lChildID), lParent);
		}
		return out;
	}

	/**
	 * @return {@link GroupContentWrapper} the root of the tree of this group's content elements
	 */
	public GroupContentWrapper getRoot() {
		return root;
	}

	/**
	 * @return Collection<GroupContentWrapper> the collection of nodes that should be displayed expanded according to the expand depth
	 */
	public Collection<GroupContentWrapper> getExpandedNodes() {
		Collection<GroupContentWrapper> out = new Vector<GroupContentWrapper>();
		//whole tree collapsed
		if (depth == 0) {
			return out;
		}
		//only root is expanded
		out.add(root);
		if (depth == 1) {
			return out;
		}
		//the rest
		out = getExpandedChildren(root, out, 2);
		return out;
	}

	@SuppressWarnings("unchecked")
	private Collection<GroupContentWrapper> getExpandedChildren(GroupContentWrapper inParent, Collection<GroupContentWrapper> inExpanded, int inCurrentDepth) {
		Collection<GroupContentWrapper> lChildren = (Collection<GroupContentWrapper>) getChildren(inParent);
		if (lChildren == null) return inExpanded;
		
		for (GroupContentWrapper lChild : lChildren) {
			inExpanded.add(lChild);
			if (inCurrentDepth < depth) {
				if (hasChildren(lChild)) {
					inExpanded = getExpandedChildren(lChild, inExpanded, inCurrentDepth+1);
				}
			}
		}
		return inExpanded;
	}

}
