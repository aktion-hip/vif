/*
	This package is part of the servlet framework used for the application VIF.
	Copyright (C) 2001, Benno Luthiger

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

package org.hip.kernel.bom.model.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.model.JoinDef;
import org.hip.kernel.bom.model.JoinDefDef;
import org.hip.kernel.bom.model.JoinOperation;
import org.hip.kernel.bom.model.MetaModelHome;
import org.hip.kernel.bom.model.MetaModelObject;
import org.hip.kernel.bom.model.PlaceholderDef;
import org.hip.kernel.exc.DefaultExceptionWriter;
import org.hip.kernel.util.Debug;

/**
 * Implementation of the JoinDef interface
 * 
 * @author: Benno Luthiger
 */
public class JoinDefImpl extends AbstractModelObject implements JoinDef {
	// member variables
	private JoinOperation joinOperation;
	private JoinDef parentJoinDef 	= null;
	private JoinDef childJoinDef	= null;

	/**
	 * JoinDefImpl default constructor.
	 */
	public JoinDefImpl() {
		super();
		joinOperation = new JoinOperationImpl();
	}
	
	/**
	 * JoinDefImpl constructor with initial values.
	 * The array of the objects contains the names in the first column and the values in the second.
	 * 
	 * @param inInitialValues java.lang.Object[][]
	 */
	public JoinDefImpl(Object[][] inInitialValues) {
		super(inInitialValues);
		joinOperation = new JoinOperationImpl();
	}
	
	/**
	 * Sets the name of the tables this JoinDef is joining.
	 *
	 * @param inTableName java.lang.String
	 */
	public void setTableName(String inTableName) {
		joinOperation.setTableName(inTableName);
	}
	
	/**
	 * Sets the columns the join is operating upon.
	 *
	 * @param inColumnName java.lang.String
	 */
	public void addColumnDef(String inColumnName) {
		joinOperation.setJoinOperand(inColumnName);
	}
	
	/**
	 * Sets the nested query to the JoinOperation.
	 * 
	 * @param inSQLQuery String 
	 */
	public void setNestedQuery(String inSQLQuery) {
		joinOperation.setTableName(inSQLQuery);
	}
	
	/**
	 * Fills the specified placeholder with the specified value.
	 * 
	 * @param inAlias String The placeholder's id.
	 * @param inSQL String SQL command string.
	 */
	public void fillPlaceholder(String inAlias, String inSQL) {
		joinOperation.fillPlaceholder(inAlias, inSQL);
	}
	
	/**
	 * @return org.hip.kernel.bom.model.MetaModelObject
	 */
	public MetaModelObject getMetaModelObject() {
		return MetaModelHome.singleton.getJoinDefDef();
	}
	
	/**
	 * JoinDefs can be involved.
	 * This method sets a reference to the parent JoinDef.
	 *
	 * @param inJoinDef org.hip.kernel.bom.model.JoinDef
	 */
	public void setParentJoinDef(JoinDef inJoinDef) {
		parentJoinDef = inJoinDef;
	}
	
	/**
	 * Returns the parent JoinDef if this JoinDef is involved.
	 *
	 * @return org.hip.kernel.bom.model.JoinDef
	 */
	public JoinDef getParentJoinDef() {
		return parentJoinDef;
	}
	
	/**
	 * JoinDefs can be involved.
	 * This method sets a reference to the child JoinDef.
	 *
	 * @param inJoinDef org.hip.kernel.bom.model.JoinDef
	 */
	public void setChildJoinDef(JoinDef inJoinDef) {
		childJoinDef = inJoinDef;
	}
	
	/**
	 * Returns the child JoinDef if this JoinDef is involved.
	 *
	 * @return org.hip.kernel.bom.model.JoinDef
	 */
	public JoinDef getChildJoinDef() {
		return childJoinDef;
	}
	
	private boolean isInvolved() {
		return (childJoinDef != null);
	}
	
	/**
	 * JoinDefs are equal if their join operations and operatorTypes are equal.
	 *
	 * @return boolean
	 */
	public boolean equals(Object inObject) {
		if (inObject == null) return false;
		if (!(inObject instanceof JoinDef)) return false;
	
		JoinDef lJoinDef = (JoinDef)inObject;
		return getJoinOperations().equals(lJoinDef.getJoinOperations());
	}
	
	/**
	 * Returns a hash code value for the object def.
	 *
	 * @return int
	 */
	public int hashCode() {
		int outCode = joinOperation.hashCode();
		if (isInvolved()) {
			outCode ^= getChildJoinDef().hashCode();
		}
		return outCode;
	}
	
	public String toString() {
		String lMessage = "";
		try {
			lMessage = "joinType=\"" + (String)get(JoinDefDef.joinType) + "\"";
		}
		catch (GettingException exc) {
			//left blank intentionally
		}
		return Debug.classMarkupString(this, lMessage);
	}
	
	/**
	 * Returns a Collection containing all table names of this joined object def.
	 * 
	 * @return java.util.Collection<String>
	 */
	public Collection<String> getTableNames() {
		Set<String> outTables = new HashSet<String>();
		outTables.add(joinOperation.getLeftTableName());
		outTables.add(joinOperation.getRightTableName());
		if (isInvolved()) {
			outTables.addAll(childJoinDef.getTableNames());
		}
		return outTables;
	}
	
	/**
	 * Returns a Collection containing all join operations of this joined object def.
	 * 
	 * @return java.util.Collection
	 */
	public Collection<JoinOperation> getJoinOperations() {
		Vector<JoinOperation> outOperations = new Vector<JoinOperation>();
		checkJoinType();
		outOperations.add(joinOperation);
		if (isInvolved()) {
			outOperations.addAll(childJoinDef.getJoinOperations());
		}
		return outOperations;
	}
	
	private void checkJoinType() {
		if ("".equals(joinOperation.getJoinType())) {
			try {
				joinOperation.setJoinType((String)get(JoinDefDef.joinType));
			}
			catch (GettingException exc) {
				DefaultExceptionWriter.printOut(this, exc, true);
				joinOperation.setJoinType(JoinDef.DFT_JOIN_TYPE);
			}
		}
	}
	
	/**
	 * Adds the specified PlaceholderDef to the collection of PlaceholderDefs.
	 * 
	 * @param inPlaceholderDef PlaceholderDef
	 */
	public void addPlaceholderDef(PlaceholderDef inPlaceholderDef) {
		joinOperation.addPlaceholderDef(inPlaceholderDef);
	}
	
	/**
	 * Checks whether this join includes the specified placeholder.
	 * 
	 * @param inAlias String
	 * @return boolean
	 */
	public boolean hasPlaceholder(String inAlias) {
		return joinOperation.hasPlaceholder(inAlias);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.kernel.bom.model.JoinDef#addJoinCondition(java.lang.String)
	 */
	public void addJoinCondition(String inOperatorType) {
		joinOperation.addJoinCondition(inOperatorType);
	}
	
}