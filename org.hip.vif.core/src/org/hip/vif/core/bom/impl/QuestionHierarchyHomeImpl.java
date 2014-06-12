/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2003, Benno Luthiger

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
package org.hip.vif.core.bom.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Question;
import org.hip.vif.core.bom.QuestionHierarchy;
import org.hip.vif.core.bom.QuestionHierarchyHome;
import org.hip.vif.core.bom.QuestionHome;

/**
 * This domain object implements the QuestionHierarchyHome interface.
 * 
 * @author: Benno Luthiger
 * @see org.hip.vif.core.bom.QuestionHierarchyHome
 */
@SuppressWarnings("serial")
public class QuestionHierarchyHomeImpl extends DomainObjectHomeImpl implements QuestionHierarchyHome {
	/* Every home has to know the class it handles. They provide access to
		this name through the method <I>getObjectClassName</I>;
	*/
	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.QuestionHierarchyImpl";

	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	\n" +
		"<objectDef objectName='QuestionAuthorReviewer' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	\n" +
		"	<keyDefs>	\n" +
		"		<keyDef>	\n" +
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_PARENT_ID + "'/>	\n" +
		"			<keyItemDef seq='1' keyPropertyName='" + KEY_CHILD_ID + "'/>	\n" +
		"		</keyDef>	\n" +
		"	</keyDefs>	\n" +
		"	<propertyDefs>	\n" +
		"		<propertyDef propertyName='" + KEY_PARENT_ID + "' valueType='Long' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblQuestionHierarchy' columnName='ParentID'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_CHILD_ID + "' valueType='Long' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblQuestionHierarchy' columnName='ChildID'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='" + KEY_GROUP_ID + "' valueType='Long' propertyType='simple'>	\n" +
		"			<mappingDef tableName='tblQuestionHierarchy' columnName='GroupID'/>	\n" +
		"		</propertyDef>	\n" +
		"	</propertyDefs>	\n" +
		"</objectDef>";	

	/**
	 * Constructor for QuestionHierarchyHome.
	 */
	public QuestionHierarchyHomeImpl() {
		super();
	}

	/**
	 * @see org.hip.kernel.bom.GeneralDomainObjectHome#getObjectClassName()
	 */
	public String getObjectClassName() {
		return OBJECT_CLASS_NAME;
	}

	/**
	 * @see org.hip.kernel.bom.impl.AbstractDomainObjectHome#getObjectDefString()
	 */
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}

	/**
	 * Returns the id of the parent of the specified question.
	 * 
	 * @param inQuestionID Long
	 * @return org.hip.vif.bom.QuestionHierarchy
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	public QuestionHierarchy getParent(Long inQuestionID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHierarchyHome.KEY_CHILD_ID, inQuestionID);
		return (QuestionHierarchy)findByKey(lKey);
	}
	/**
	 * @deprecated
	 */
	public QuestionHierarchy getParent(String inQuestionID) throws VException, SQLException {
		return getParent(new Long(inQuestionID));
	}

	/**
	 * Returns the parent question of the specified question.
	 * 
	 * @param inQuestionID java.lang.String
	 * @return org.hip.vif.bom.QuestionHierarchy
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	public Question getParentQuestion(Long inQuestionID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHierarchyHome.KEY_CHILD_ID, inQuestionID);
		try {
			Long lID = new Long(findByKey(lKey).get(QuestionHierarchyHome.KEY_PARENT_ID).toString());
			return ((QuestionHome)BOMHelper.getQuestionHome()).getQuestion(lID);
		}
		catch (BOMNotFoundException exc) {
			return (Question)((QuestionHome)BOMHelper.getQuestionHome()).create();
		}
	}	
	/**
	 * @deprecated
	 */
	public Question getParentQuestion(String inQuestionID) throws VException, SQLException {
		return getParentQuestion(new Long(inQuestionID));
	}

	/**
	 * Returns the childs of the specified question.
	 * 
	 * @param inQuestionID java.lang.String
	 * @return org.hip.kernel.bom.QueryResult
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 * @deprecated
	 */
	public QueryResult getChilds(String inQuestionID) throws VException, SQLException {
		return getChilds(new Long(inQuestionID));
	}

	/**
	 * Returns the childs of the specified question.
	 * 
	 * @param inQuestionID java.lang.Long
	 * @return org.hip.kernel.bom.QueryResult of QuestionHierarchy objects.
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	public QueryResult getChilds(Long inQuestionID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHierarchyHome.KEY_PARENT_ID, inQuestionID);
		return select(lKey);
	}
	
	/**
	 * Returns the siblings of the specified question including the node itself.
	 * 
	 * @param inQuestionID java.lang.String
	 * @return org.hip.kernel.bom.QueryResult
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	public QueryResult getSiblings(Long inQuestionID) throws VException, SQLException {
		return getChilds((Long)getParent(inQuestionID).get(QuestionHierarchyHome.KEY_PARENT_ID));
	}
	public QueryResult getSiblings(String inQuestionID) throws VException, SQLException {
		return getChilds(getParent(inQuestionID).get(QuestionHierarchyHome.KEY_PARENT_ID).toString());
	}
	
	/**
	 * Counts the specified question's childs.
	 * 
	 * @param inQuestionID java.lang.String
	 * @return int
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	public int countChilds(Long inQuestionID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHierarchyHome.KEY_PARENT_ID, inQuestionID);
		return getCount(lKey);
	}
	/**
	 * @deprecated
	 */
	public int countChilds(String inQuestionID) throws VException, SQLException {
		return countChilds(new Long(inQuestionID));
	}

	/**
	 * Adds a new entry to the question hierarchy.
	 * 
	 * @param inParentID java.lang.Long
	 * @param inChildID java.lang.Long
	 * @throws org.hip.kernle.exc.VException
	 * @throws java.sql.SQLException
	 */
	public void ucNew(Long inParentID, Long inChildID, Long inGroupID) throws VException, SQLException {
		QuestionHierarchy lHierachy = (QuestionHierarchy)create();
		lHierachy.set(QuestionHierarchyHome.KEY_PARENT_ID, inParentID);
		lHierachy.set(QuestionHierarchyHome.KEY_CHILD_ID, inChildID);
		lHierachy.set(QuestionHierarchyHome.KEY_GROUP_ID, inGroupID);
		lHierachy.insert(true);
	}
	
	/**
	 * Returns true if the specified question has a parent.
	 * 
	 * @param inChildID
	 * @return boolean True, if the specified question has a parent.
	 * @throws VException
	 * @throws SQLException
	 */
	public boolean hasParent(Long inChildID) throws VException, SQLException {
		try {
			KeyObject lKey = new KeyObjectImpl();
			lKey.setValue(QuestionHierarchyHome.KEY_CHILD_ID, inChildID);
			findByKey(lKey);
			return true; 
		}
		catch (BOMNotFoundException exc) {
			return false;
		}
	}
	
	/**
	 * Checks the visibility for guests of the specified question
	 * at the specified depth.
	 * 
	 * @param inQuestionID String
	 * @param inDepth Long
	 * @return boolean true, is question is visible
	 */
	public boolean isVisibleForGuestDepth(Long inQuestionID, Long inDepth) {
		return isVisibleForGuestDepth(inQuestionID.toString(), inDepth);
	}
	public boolean isVisibleForGuestDepth(String inQuestionID, Long inDepth) {
		int lDepth = inDepth.intValue();
		//pre
		if (lDepth <= 0) return false;
		if (lDepth == 1) return checkForRoot(inQuestionID);
		
		ObjectDef lObjectDef = getObjectDef();
		String lTableName = lObjectDef.getTableNames2().toArray(new String[1])[0];
		String lFieldChild = lObjectDef.getPropertyDef(KEY_CHILD_ID).getMappingDef().getColumnName();
		String lFieldParent = lObjectDef.getPropertyDef(KEY_PARENT_ID).getMappingDef().getColumnName();
		
		String lSelect = new String(createSelectRecursive(lTableName, lFieldParent, lFieldChild, inQuestionID, lDepth-1));
		try {
			return !select(lSelect).hasMoreElements();
		}
		catch (SQLException exc) {
			return false;
		}
	}
	
	/**
	 * Creates recursively a SQL command in the form:
	 * SELECT parentid FROM tblquestionhierarchy WHERE childid IN (...
	 *   SELECT parentid FROM tblquestionhierarchy WHERE childid = inQuestionID
	 * )
	 * 
	 * @param inTableName String
	 * @param inFieldParent String
	 * @param inFieldChild String
	 * @param inQuestionID String
	 * @param inDepth int
	 * @return StringBuffer
	 */
	private StringBuffer createSelectRecursive(String inTableName, String inFieldParent, String inFieldChild, String inQuestionID, int inDepth) {
		StringBuffer outSelect = new StringBuffer("SELECT ");
		outSelect.append(inFieldParent).append(" FROM ").append(inTableName).append(" WHERE ").append(inFieldChild);
		if (inDepth <= 0) {
			outSelect.append(" = ").append(inQuestionID);
		}
		else {
			outSelect.append(" IN (").append(createSelectRecursive(inTableName, inFieldParent, inFieldChild, inQuestionID, inDepth-1)).append(")");
		}
		return outSelect;
	}
	
	private boolean checkForRoot(String inQuestionID) {
		try {
			Question lQuestion = ((QuestionHome)BOMHelper.getQuestionHome()).getQuestion(inQuestionID);
			Long lRootFlag = new Long(lQuestion.get(QuestionHome.KEY_ROOT_QUESTION).toString());
			return lRootFlag.intValue() > 0;
		}
		catch (VException exc) {
			return false;
		}
		catch (SQLException exc) {
			return false;
		}
	}
	
	private QueryResult getHierarchy(Long inGroupID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(QuestionHierarchyHome.KEY_GROUP_ID, inGroupID);
		return select(lKey);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.core.bom.QuestionHierarchyHome#getChildrenChecker(java.lang.Long)
	 */
	public ChildrenChecker getChildrenChecker(Long inGroupID) throws VException, SQLException {
		return new ChildrenChecker(getHierarchy(inGroupID));
	}
	/**
	 * @deprecated
	 */
	public ChildrenChecker getChildrenChecker(String inGroupID) throws VException, SQLException {
		return getChildrenChecker(new Long(inGroupID));
	}
	
// --- 
	
	public static class ChildrenChecker {
		private Set<Long> parents = new HashSet<Long>();
		private Map<Long, Long> childParentMap = new HashMap<Long, Long>();
		
		/**
		 * Private constructor
		 * 
		 * @param inGroupHierarchy QueryResult the query result of all questions in the hierarchy belonging to the same group
		 * @throws VException
		 * @throws SQLException
		 */
		private ChildrenChecker(QueryResult inGroupHierarchy) throws VException, SQLException {
			while (inGroupHierarchy.hasMoreElements()) {
				GeneralDomainObject lItem = inGroupHierarchy.nextAsDomainObject();
				Long lParentID = (Long) lItem.get(QuestionHierarchyHome.KEY_PARENT_ID);
				parents.add(lParentID);
				childParentMap.put((Long) lItem.get(QuestionHierarchyHome.KEY_CHILD_ID), lParentID);
			}
		};
		
		/**
		 * Checks whether the Question with the specified ID has children, thus is a parent.
		 * 
		 * @param inQuestionID Long
		 * @return boolean <code>true</code> if the question with the specified ID has children.
		 */
		public boolean hasChildren(Long inQuestionID) {
			return parents.contains(inQuestionID);
		}
		
		/**
		 * Returns the ID of the parent question of the child question with the specified ID.
		 * 
		 * @param inChildID Long
		 * @return Long the parent's ID
		 */
		public Long getParentID(Long inChildID) {
			return childParentMap.get(inChildID);
		}
	}
	
}
